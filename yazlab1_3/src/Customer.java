import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Customer implements Runnable {
    private final Lock customerLock;
    private int customerID;
    private Table table;
    private final int step;
    private final boolean isPriority;
    private static int customerCount = 0;
    private int normalCustomerCount;
    private int priorityCustomerCount;
    private long entryTime; // Müşterinin restorana giriş zamanı
    private static final long MAX_WAIT_TIME = 20000; // 20 saniye
    private boolean food = false;

    public Customer(int step, boolean isPriority) {

        this.step = step;
        this.isPriority = isPriority;
        this.customerLock = new ReentrantLock();
        synchronized (customerLock) {
            this.customerID = ++customerCount;
        }

    }

    public boolean isFood() {
        return food;
    }

    public void setFood(boolean food) {
        this.food = food;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setTable(Table table) {
        this.table = table;
    }


    @Override
    public void run() {

        synchronized (customerLock) {
            entryTime = System.currentTimeMillis(); // Müşteri restorana giriş zamanını kaydet
            Main.waitingCustomers.add(this); // Bekleme listesine ekle
            System.out.println("Müşteri restorana giriş yaptı. Müşteri ID: " + customerID);
        }

        // 20 saniye kontrolü
        while (Main.waitingCustomers.contains(this)) {
            long elapsedTime = System.currentTimeMillis() - entryTime;
            if (elapsedTime > MAX_WAIT_TIME) {
                synchronized (Main.waitingCustomers) {
                    synchronized (customerLock) {
                        Main.waitingCustomers.remove(this);
                        Main.customers.remove(this);
                        System.out.println("Uzun süreli bekleme sebebiyle müşteri " + customerID + " restorandan ayrıldı.");
                        Main.bilgiTextArea.append("Uzun süreli bekleme sebebiyle " + customerID + " nolu müşteri restorandan ayrıldı.\n");
                        Main.CustomersThread.get(customerID - 1).interrupt();
                        try {
                            Main.yazici.write("Uzun süreli bekleme sebebiyle " + customerID + " nolu müşteri restorandan ayrıldı.\n");
                        } catch (IOException e) {

                        }
                        break;
                    }
                }
            }

            try {
                Thread.sleep(1000); // Belirli aralıklarla kontrol et
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while (true) {
            synchronized (customerLock) {
                if (isFood()) {
                    try {
                        Thread.sleep(3000); // Belirli aralıklarla kontrol et
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Main.addPayList(this.customerID);
                    this.setFood(false);
                    break;
                }
            }
        }

        updateCustomerCounts();
    }


    private void updateCustomerCounts() {
        if (!isPriority) {
            normalCustomerCount++;
        } else {
            priorityCustomerCount++;
        }
    }
}

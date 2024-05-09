import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Chef implements Runnable {
    private final int chefID;
    private final int maxConcurrentOrders;
    private final Lock chefLock;

    public Chef(int chefID, int maxConcurrentOrders) {
        this.chefID = chefID;
        this.maxConcurrentOrders = maxConcurrentOrders;
        this.chefLock = new ReentrantLock();
    }

    @Override
    public void run() {
        while (true) {
            int orderId = getOrder();
            if (orderId != -1) {
                prepareOrder(orderId);
            } else {
                // Sipariş kalmadı, bekleme durumuna geç
                System.out.println("Aşçı " + chefID + " bekleme durumuna geçti.");
                Main.orderTextArea.append("Aşçı " + chefID + " bekleme durumuna geçti.\n");
                try {
                    Main.yazici.write("Aşçı " + chefID + " bekleme durumuna geçti.\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                waitForOrder();
            }
        }
    }

    private int getOrder() {
        int orderNumber = -1;

        synchronized (Main.orderList) {
            if (!Main.orderList.isEmpty()) {
                orderNumber = Main.orderList.remove(0);
            }
        }
        return orderNumber;
    }

    private void prepareOrder(int orderId) {
        chefLock.lock();
        try {
            System.out.println(chefID + " Nolu aşçı " + orderId + " Nolu müşterinin siparişini aldı ve hazırlamaya başladı.");
            Main.prepareOrder(chefID, orderId, false);

            // Sipariş hazırlama süresini simüle et
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Main.prepareOrder(chefID, orderId, true);

        } finally {
            chefLock.unlock();
        }
    }

    private void waitForOrder() {
        synchronized (Main.orderList) {
            try {
                Main.orderList.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}

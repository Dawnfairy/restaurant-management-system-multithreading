import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class Main {

    static int[] normalCustomerCounts;
    static int[] priorityCustomerCounts;
    static int maxTables = 6;
    static int remainingTables;
    static ArrayList<Customer> waitingCustomers = new ArrayList<>();
    static ArrayList<Customer> normalCustomersCustomer = new ArrayList<>();
    static ArrayList<Customer> priorityCustomersCustomer = new ArrayList<>();
    static ArrayList<Customer> customers = new ArrayList<>();
    static ArrayList<Waiter> waiters = new ArrayList<>();
    static ArrayList<Chef> chefs = new ArrayList<>();
    static ArrayList<Table> tables = new ArrayList<>();
    static JFrame simulationWaiter = new JFrame("Garson Arayüzü");
    static JFrame simulationChef = new JFrame("Aşçı Arayüzü");
    static JFrame simulationCashier = new JFrame("Kasiyer Arayüzü");
    static JPanel panel = new JPanel();
    static JPanel panel1 = new JPanel();
    static JPanel panel2 = new JPanel();
    static JPanel tablePanel;
    static Color priorityColor = Color.decode("#8E44AD");
    static Color normalColor = Color.decode("#BB8FCE");
    static Color takeOrderColor = Color.decode("#CD6155");
    static Color eatFood = Color.decode("#2ECC71");
    static Color payListColor = Color.decode("#34495E");
    static ArrayList<JPanel> tablePanelList = new ArrayList<>();
    static JTextArea bilgiTextArea = new JTextArea(15, 40);
    static JScrollPane scrollPane = new JScrollPane(bilgiTextArea);
    static JTextArea orderTextArea = new JTextArea(20, 40);
    static JScrollPane scrollPane1 = new JScrollPane(orderTextArea);
    static JTextArea cashierTextArea = new JTextArea(20, 22);
    static JScrollPane scrollPane2 = new JScrollPane(cashierTextArea);
    static JTextArea cashierTextArea1 = new JTextArea(20, 19);
    static JScrollPane scrollPane21 = new JScrollPane(cashierTextArea1);
    static final ArrayList<Integer> orderList = new ArrayList<>();
    static ArrayList<Integer> payList = new ArrayList<>();
    static ArrayList<Thread> CustomersThread = new ArrayList<>();
    static ArrayList<Thread> CashierThread = new ArrayList<>();
    static ArrayList<Thread> WaiterThread = new ArrayList<>();
    static ArrayList<Thread> ChefThread = new ArrayList<>();
    static int caseCustomer = 0;
    static int caseCustomerPay = 0;
    static JLabel caseCustomerLabel = new JLabel("0");
    static JLabel caseCustomerPayLabel = new JLabel("0");
    static BufferedWriter yazici;

    static int maxYield = 0;
    static int tableCount;
    static int waiterCount;
    static int chefCount;
    static int leaveCustomerCount = 0;


    public static void main(String[] args) {


        String dosyaYolu = "metinDosyasi.txt";

        try {
            // FileWriter ve BufferedWriter kullanarak dosya oluşturup veriyi yazma
            FileWriter dosyaYazici = new FileWriter(dosyaYolu);
            yazici = new BufferedWriter(dosyaYazici);
            System.out.println("Veri dosyaya yazıldı.");

        } catch (IOException e) {
            e.printStackTrace();
        }

        JFrame problems = new JFrame("Restoran Yönetim Sistemi");
        problems.setSize(350, 150);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JButton problem1 = new JButton("Problem 1");

        problem1.addActionListener(e -> {

            problems.setVisible(false);

            simulationWaiter.getContentPane().add(panel, BorderLayout.CENTER);//garson arayüzü
            simulationChef.getContentPane().add(panel1, BorderLayout.CENTER);//aşçı arayüzü
            simulationCashier.getContentPane().add(panel2, BorderLayout.CENTER);//kasiyer arayüzü

            int stepCount = getStepCount();//adım sayısını al
            remainingTables = maxTables;//masa sayısı
            if (stepCount > 0) {
                normalCustomerCounts = new int[stepCount];//normal müşteri sayısı
                priorityCustomerCounts = new int[stepCount];//öncelikli müşteri sayısı

                //garsonları oluştur ve garson arraylistine ekle
                waiters.add(new Waiter(1, tables));
                waiters.add(new Waiter(2, tables));
                waiters.add(new Waiter(3, tables));
                //aşçıları oluştur ve aşçı arraylistine ekle
                chefs.add(new Chef(1, 2));
                chefs.add(new Chef(2, 2));

                int totalCustomers;
                for (int i = 0; i < stepCount; i++) {

                    //müşteri sayılarını al ve ilgili arrayliste ekle
                    int normalCustomers = getCustomerCount("Normal", i + 1);
                    int priorityCustomers = getCustomerCount("Öncelikli", i + 1);

                    //her adım için max 10 müşteri kontrolü
                    totalCustomers = normalCustomers + priorityCustomers;
                    if (totalCustomers > 10) {
                        JOptionPane.showMessageDialog(null, "Toplam müşteri sayısı 10'u aşamaz!", "Hata", JOptionPane.ERROR_MESSAGE);
                        normalCustomers = getCustomerCount("Normal", i + 1);
                        priorityCustomers = getCustomerCount("Öncelikli", i + 1);
                    }
                    //müşterileri oluştur ve müşteri arraylistine ekle
                    createCustomer(normalCustomers, priorityCustomers, i + 1);
                }
                if (!customers.isEmpty()) {

                    StringBuilder scenarioMessage = new StringBuilder("Senaryo oluşturuldu. Adım Sayısı: " + stepCount + "\n");
                    for (int i = 0; i < stepCount; i++) {
                        scenarioMessage.append(String.format("(%d.adım) %d müşteri %d öncelikli\n", i + 1, normalCustomerCounts[i], priorityCustomerCounts[i]));
                        try {
                            yazici.write(String.format("(%d.adım) %d müşteri %d öncelikli\n", i + 1, normalCustomerCounts[i], priorityCustomerCounts[i]));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                    JOptionPane.showMessageDialog(null, scenarioMessage.toString());

                    //müşteri threadlerini oluştur ve başlat
                    startCustomerThreads(customers);

                    //masaları oluştur ve ilk 6 kişiyi masalara yerleştir
                    for (int i = 0; i < maxTables; i++) {
                        Table table = new Table(i + 1);
                        Customer customer = customers.get(i);
                        table.setCustomer(customer);
                        customer.setTable(table);
                        tables.add(table);
                        caseCustomer++;
                        String count = String.valueOf(caseCustomer);
                        caseCustomerLabel.setText(count);
                    }

                    simulateCustomersAtTables();

                    for (Waiter waiter : waiters) {
                        Thread thread = new Thread(waiter);
                        thread.start();
                        WaiterThread.add(thread);
                    }

                    for (Chef chef : chefs) {
                        Thread thread = new Thread(chef);
                        thread.start();
                        ChefThread.add(thread);
                    }


                    Thread thread = new Thread(new Cashier());
                    thread.start();
                    CashierThread.add(thread);

                }
            }

            simulationWaiter.setSize(500, 500);
            simulationWaiter.setLocation(20, 158);
            simulationWaiter.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            simulationWaiter.setVisible(true);

            simulationCashier.setSize(500, 500);
            simulationCashier.setLocation(1015, 158);
            simulationCashier.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            simulationCashier.setVisible(true);

            simulationChef.setSize(500, 500);
            simulationChef.setLocationRelativeTo(null);
            simulationChef.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            simulationChef.setVisible(true);

            System.out.println("Müşteri Listesi");
            for (Customer customer : customers) {
                System.out.println(customer + " No: " + customer.getCustomerID());
            }
            System.out.println("\nNormal Müşteri Listesi");
            for (Customer customer : normalCustomersCustomer) {
                System.out.println(customer + " No: " + customer.getCustomerID());
            }
            System.out.println("\nÖncelikli Müşteri Listesi");
            for (Customer customer : priorityCustomersCustomer) {
                System.out.println(customer + " No: " + customer.getCustomerID());
            }
            System.out.println("\nBekleme Listesi");
            for (Customer customer : waitingCustomers) {
                System.out.println(customer + " No: " + customer.getCustomerID());
            }
            System.out.println("\nGarson Listesi");
            for (Waiter customer : waiters) {
                System.out.println(customer + " No: " + customer.getWaiterID());
            }
            System.out.println("\nMasa Listesi");
            for (Table customer : tables) {
                System.out.println(customer + " No: " + customer.getTableNumber());
            }

        });

        buttonPanel.add(problem1);
        JButton problem2 = new JButton("Problem 2");

        problem2.addActionListener(e -> {

            problems.setVisible(false);

            JFrame Frame = new JFrame("Kazanç Hesapla");

            JButton button = new JButton("Sonuç");
            ArrayList<Problem2Customer> seatedCustomers = new ArrayList<>();
            ArrayList<Problem2Table> tables = new ArrayList<>();
            ArrayList<Problem2Customer> normalCustomers = new ArrayList<>();
            ArrayList<Problem2Customer> priorityCustomers = new ArrayList<>();
            ArrayList<Problem2Customer> totalCustomers = new ArrayList<>();
            ArrayList<Problem2Waiter> waiters = new ArrayList<>();
            ArrayList<Problem2Chef> chefs = new ArrayList<>();
            ArrayList<Problem2Table> fullTable = new ArrayList<>();
            ArrayList<Problem2Customer> ayrilanMusteriler20SaniyeSonra = new ArrayList<>();

            JLabel sonucLabel = new JLabel();
            JLabel sonucLabel2 = new JLabel();
            JLabel sonucLabel3 = new JLabel();
            JLabel sonucLabel4 = new JLabel();
            JLabel sonucLabel5 = new JLabel();
            JLabel sonucLabel6 = new JLabel();
            JLabel sonucLabel7 = new JLabel();

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    Frame.setVisible(false);

                    int totalTime = Integer.parseInt(JOptionPane.showInputDialog(null, "Toplam süreyi giriniz.(sn)"));
                    int period = Integer.parseInt(JOptionPane.showInputDialog(null, "Müşteri gelme aralığını giriniz.(sn)"));
                    int normalCustomer = Integer.parseInt(JOptionPane.showInputDialog(null, "Normal müşteri sayısını giriniz."));
                    int priorityCustomer = Integer.parseInt(JOptionPane.showInputDialog(null, "Öncelikli müşteri sayısını giriniz."));

                    int yield;
                    int time = 0;
                    int sayac = 0;

                    int totalCustomerCount = (normalCustomer + priorityCustomer) * ((totalTime / period)+1);
                    int tempTotalCustomerCount = totalCustomerCount;

                    yield = totalCustomerCount;

                    tables.clear();
                    waiters.clear();
                    chefs.clear();
                    int tableCounter = 1;
                    while (tableCounter < 15) {
                        int waiterCounter = 1;
                        while (waiterCounter < 10) {
                            int chefCounter = 1;
                            while (chefCounter < 5) {

                                tables.clear();
                                waiters.clear();
                                chefs.clear();

                                sayac = 0;

                                int count = 0;
                                while (count < chefCounter * 2) {//her bir aşçı 2 yemek hazırlayabilir yani 2 aşçı olarak varsayabiliriz
                                    Problem2Chef c = new Problem2Chef();
                                    chefs.add(c);
                                    count++;
                                }

                                count = 0;
                                while (count < waiterCounter) {//garsonları oluşturduk
                                    Problem2Waiter w = new Problem2Waiter();
                                    waiters.add(w);
                                    count++;
                                }

                                count = 0;
                                while (count < tableCounter) {//masaları oluşturduk
                                    Problem2Table t = new Problem2Table();
                                    tables.add(t);
                                    count++;
                                }

                                time = 0;
                                if (time < 1) {

                                    int say = 0;
                                    while (say < normalCustomer) {//normal müşteriler
                                        Problem2Customer m = new Problem2Customer();
                                        normalCustomers.add(m);
                                        say++;
                                    }

                                    say = 0;
                                    while (say < priorityCustomer) {//öncelikli müşteriler
                                        Problem2Customer m = new Problem2Customer();
                                        m.onceliksiz = 0;//önceliklilere 0 verdik.
                                        priorityCustomers.add(m);
                                        say++;
                                    }
                                    say = 0;

                                    totalCustomers.addAll(priorityCustomers);
                                    totalCustomers.addAll(normalCustomers);

                                }

                                int totalTableWaiterChefCount = tableCounter + waiterCounter + chefCounter;
                                yield = yield - totalTableWaiterChefCount;  //verim = toplamgelecekkişisayısı - masa - garson - aşçı
                                totalCustomerCount = tempTotalCustomerCount;

                                while (totalCustomerCount != 0)  //eğer bekleme süresi 20 sn geçerse ya da müşteri masadan kalkıp hesabı öderse bu sayı 1 azaltılır
                                {
                                    for (Problem2Table table : tables) {
                                        if (table.isFull == 0) {
                                            if (!priorityCustomers.isEmpty()) {
                                                table.isFull = 1;
                                                fullTable.add(table);
                                                seatedCustomers.add(priorityCustomers.get(0));
                                                priorityCustomers.get(0).tableNumber = tables.indexOf(table);
                                                priorityCustomers.remove(0);
                                            } else if (!normalCustomers.isEmpty()) {
                                                table.isFull = 1;
                                                fullTable.add(table);
                                                seatedCustomers.add(normalCustomers.get(0));
                                                normalCustomers.get(0).tableNumber = tables.indexOf(table);
                                                normalCustomers.remove(0);
                                            } else {
                                                break;
                                            }
                                        }
                                    }


                                    for (Problem2Customer customer : seatedCustomers) {
                                        if (customer.orderProcess == 0) {
                                            for (Problem2Waiter waiter : waiters) {
                                                if (waiter.isEmpty == 1) {
                                                    customer.orderProcess = 1;
                                                    waiter.isEmpty = 0;
                                                    waiter.siparisAldiMi = 1;
                                                    waiter.customerNumber = seatedCustomers.indexOf(customer);
                                                    break;
                                                } else {
                                                    //System.out.println("boşta garson yok");
                                                }
                                            }
                                        }
                                    }


                                    for (int counter = 0; counter < seatedCustomers.size(); counter++) {
                                        if (seatedCustomers.get(counter).isOrdered == 1) {
                                            for (Problem2Chef chef : chefs) {
                                                if (seatedCustomers.get(counter).asciYemegiHazirlamayaBasladiMi == 0 && chef.isEmpty1) {//aşçının sipariş hazırlamaya başlaması
                                                    chef.customerNumber1 = counter;
                                                    chef.isEmpty1 = false;
                                                    seatedCustomers.get(counter).asciYemegiHazirlamayaBasladiMi = 1;

                                                } else {
                                                    //System.out.println("boşta aşçı yok");
                                                }
                                            }
                                        }
                                    }

                                    time++;

                                    for (Problem2Customer seatedCustomer : seatedCustomers) {
                                        if (seatedCustomer.isEat == 1 && !seatedCustomer.isFinished && seatedCustomer.odemeSuresi == 0) {//ödeme sürecini bitir

                                            tables.get(seatedCustomer.tableNumber).isFull = 0;
                                            fullTable.remove(tables.get(seatedCustomer.tableNumber));
                                            seatedCustomer.odemeSuresi++;
                                            //System.out.println(seatedCustomers.get(l) + " normal ayrıldı");
                                            totalCustomerCount--;
                                            seatedCustomer.isFinished = true;

                                            break;
                                        }else{
                                            //System.out.println("ödeme devam");
                                        }
                                    }

                                    if (time % period == 0) {
                                        //System.out.println("yeni müşteriler");
                                    }

                                    for (Problem2Customer seatedCustomer : seatedCustomers) {

                                        if (seatedCustomer.isFoodReady == 1) {//müşteri 3 saniye yemek yeme süreci
                                            seatedCustomer.eatingTime++;

                                            if (seatedCustomer.eatingTime == 3) {
                                                seatedCustomer.isEat = 1;
                                            }
                                        }
                                    }

                                    for (Problem2Chef chef : chefs) {
                                        if (!chef.isEmpty1) {// aşçının 3 saniye yemek hazırlama süreci

                                            chef.yemekHazirlamaSuresi1++;
                                            if (chef.yemekHazirlamaSuresi1 == 3) {

                                                chef.yemekHazirlamaSuresi1 = 0;
                                                chef.isEmpty1 = true;
                                                seatedCustomers.get(chef.customerNumber1).isFoodReady = 1;
                                                chef.customerNumber1 = -1;
                                            }
                                        }
                                    }

                                    for (Problem2Waiter waiter : waiters) {//garson 2 saniye sipariş alma süreci
                                        if (waiter.isEmpty == 0) {
                                            waiter.garsonSiparisAlmaZaman++;

                                            if (waiter.garsonSiparisAlmaZaman == 2) {
                                                waiter.isEmpty = 1;
                                                waiter.siparisAldiMi = 0;
                                                seatedCustomers.get(waiter.customerNumber).isOrdered = 1;
                                                waiter.garsonSiparisAlmaZaman = 0;
                                                waiter.customerNumber = -1;

                                            }
                                        }
                                    }

                                    if (time % period == 0 && totalCustomerCount > 0 && totalTime >= time) {// müşteri gelme aralığında müşteri oluşturma

                                        {
                                            int f = 0;
                                            while (f < priorityCustomer) {
                                                Problem2Customer m = new Problem2Customer();
                                                m.onceliksiz = 0;
                                                priorityCustomers.add(m);

                                                f++;
                                            }
                                        }
                                        int f = 0;
                                        while (f < normalCustomer) {
                                            Problem2Customer m = new Problem2Customer();
                                            normalCustomers.add(m);

                                            f++;
                                        }

                                        totalCustomers.addAll(priorityCustomers);
                                        totalCustomers.addAll(normalCustomers);
                                    }

                                    {
                                        int f = 0;
                                        while (f < normalCustomers.size()) {
                                            //20 saniye bekleme süresi

                                            normalCustomers.get(f).waitingTime++;
                                            if (normalCustomers.get(f).waitingTime > 20) {
                                                yield--;
                                                sayac++;
                                                //System.out.println(normalCustomers.get(f));
                                                normalCustomers.get(f).beklemeSuresiDolduMu = true;
                                                ayrilanMusteriler20SaniyeSonra.add(normalCustomers.get(f));
                                                totalCustomerCount--;
                                                totalCustomers.remove(normalCustomers.get(f));
                                                normalCustomers.remove(f);

                                            }
                                            f++;
                                        }
                                    }

                                    int f = 0;
                                    while (f < priorityCustomers.size()) {
                                        //20 saniye bekleme süresi
                                        priorityCustomers.get(f).waitingTime++;
                                        if (priorityCustomers.get(f).waitingTime > 20) {
                                            yield--;
                                            //System.out.println(priorityCustomers.get(f) + " ");
                                            sayac++;
                                            priorityCustomers.get(f).beklemeSuresiDolduMu = true;
                                            ayrilanMusteriler20SaniyeSonra.add(priorityCustomers.get(f));
                                            totalCustomerCount--;
                                            totalCustomers.remove(priorityCustomers.get(f));
                                            priorityCustomers.remove(f);

                                        }
                                        f++;
                                    }
                                }

                                if (yield > maxYield) {//en büyük kazancı bulma
                                    maxYield = yield;
                                    leaveCustomerCount = sayac;
                                    waiterCount = waiterCounter;
                                    chefCount = chefCounter;
                                    tableCount = tableCounter;
                                    System.out.println("Kazanç : " + tempTotalCustomerCount + " - " + leaveCustomerCount + " - " + tableCount + " - " + waiterCount + " - " + chefCount + " = " + maxYield);

                                }

                                totalCustomers.clear();
                                ayrilanMusteriler20SaniyeSonra.clear();
                                fullTable.clear();
                                seatedCustomers.clear();
                                normalCustomers.clear();
                                priorityCustomers.clear();
                                yield = tempTotalCustomerCount;
                                chefCounter++;
                            }
                            waiterCounter++;
                        }
                        tableCounter++;
                    }

                    Frame.getContentPane().removeAll();
                    Frame.revalidate();
                    Frame.repaint();

                    Frame.setVisible(true);
                    Frame.setLocationRelativeTo(null);

                    String baslik = "Toplam Kazanç";
                    String toplamMusteriSayisi = "Toplam Gelen Müşteri Sayısı : " + tempTotalCustomerCount;
                    String ayrilanMusteriSayisi = "Ayrılan Müşteri Sayısı : " + leaveCustomerCount;
                    String masaSayisi = "Masa Sayısı : " + tableCount;
                    String garsonSayisi = "Garson Sayısı : " + waiterCount;
                    String asciSayisi = "Aşçı Sayısı : " + chefCount;
                    String kazanc = "Kazanç : " + tempTotalCustomerCount + " - " + leaveCustomerCount + " - " + tableCount + " - " + waiterCount + " - " + chefCount + " = " + maxYield;
                    sonucLabel.setText(toplamMusteriSayisi);
                    sonucLabel2.setText(ayrilanMusteriSayisi);
                    sonucLabel3.setText(masaSayisi);
                    sonucLabel4.setText(garsonSayisi);
                    sonucLabel5.setText(asciSayisi);
                    sonucLabel6.setText(kazanc);
                    sonucLabel7.setText(baslik);

                    Frame.add(sonucLabel7);
                    Frame.add(sonucLabel);
                    Frame.add(sonucLabel2);
                    Frame.add(sonucLabel3);
                    Frame.add(sonucLabel4);
                    Frame.add(sonucLabel5);
                    Frame.add(sonucLabel6);

                }
            });

            Font labelFont = new Font("Arial", Font.BOLD, 18);
            Color labelColor = priorityColor;

            Font labelFont2 = new Font("Arial", Font.ITALIC, 15);
            Color labelColor2 = Color.BLACK;

            sonucLabel7.setForeground(labelColor);
            sonucLabel7.setFont(labelFont);

            sonucLabel.setForeground(labelColor2);
            sonucLabel2.setForeground(labelColor2);
            sonucLabel3.setForeground(labelColor2);
            sonucLabel4.setForeground(labelColor2);
            sonucLabel5.setForeground(labelColor2);
            sonucLabel6.setForeground(labelColor2);

            sonucLabel.setFont(labelFont2);
            sonucLabel2.setFont(labelFont2);
            sonucLabel3.setFont(labelFont2);
            sonucLabel4.setFont(labelFont2);
            sonucLabel5.setFont(labelFont2);
            sonucLabel6.setFont(labelFont2);

            button.setPreferredSize(new Dimension(70, 40));

            Font buttonFont = button.getFont();
            button.setFont(new Font(buttonFont.getName(), buttonFont.getStyle(), 20));
            button.setBounds(20, 90, 70, 40);

            sonucLabel7.setBounds(75, -70, 250, 200);
            sonucLabel.setBounds(20, -30, 250, 200);
            sonucLabel2.setBounds(20, 0, 250, 200);
            sonucLabel3.setBounds(20, 30, 250, 200);
            sonucLabel4.setBounds(20, 60, 250, 200);
            sonucLabel5.setBounds(20, 90, 250, 200);
            sonucLabel6.setBounds(20, 120, 250, 200);
            Frame.setLayout(new BorderLayout());
            Frame.add(button, BorderLayout.CENTER);
            button.setPreferredSize(new Dimension(50, 25));
            Frame.setSize(300, 300);
            Frame.setLocationRelativeTo(null);
            Frame.setVisible(true);
        });

        buttonPanel.add(problem2);
        problems.getContentPane().add(buttonPanel, BorderLayout.CENTER);
        problems.setLocationRelativeTo(null);
        problems.setVisible(true);
        problems.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    private static int getStepCount() {
        String stepCountText = JOptionPane.showInputDialog(null, "Adım Sayısını Giriniz:");
        try {
            return Integer.parseInt(stepCountText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Geçersiz adım sayısı!", "Hata", JOptionPane.ERROR_MESSAGE);
            return -1;
        }
    }

    private static int getCustomerCount(String type, int step) {
        String customerCountText = JOptionPane.showInputDialog(null, String.format("(%d.adım) %s Müşteri Sayısını Giriniz:", step, type));

        try {
            if (type.equals("Öncelikli")) {
                priorityCustomerCounts[step - 1] = Integer.parseInt(customerCountText);
                return Integer.parseInt(customerCountText);
            } else if (type.equals("Normal")) {
                normalCustomerCounts[step - 1] = Integer.parseInt(customerCountText);
                return Integer.parseInt(customerCountText);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Geçersiz müşteri sayısı!", "Hata", JOptionPane.ERROR_MESSAGE);
            return -1;
        }

        return -2;
    }

    private static void createCustomer(int normalCustomers, int priorityCustomers, int step) {

        for (int i = 0; i < normalCustomers; i++) {
            customers.add(new Customer(step, false));
            //normalCustomersCustomer.add(new Customer(step, false));
        }
        for (int i = 0; i < priorityCustomers; i++) {
            customers.add(new Customer(step, true));
            //priorityCustomersCustomer.add(new Customer(step, true));
        }
    }

    private static void startCustomerThreads(ArrayList<Customer> customers) {

        //ArrayList<Thread> priorityCustomersThread = new ArrayList<>();
        for (Customer customer : customers) {
            Thread thread = new Thread(customer);
            thread.start();
            if (customer.isPriority()) {
                priorityCustomersCustomer.add(customer);
                System.out.println("thread öncelikli : " + thread.getName());
            } else {
                normalCustomersCustomer.add(customer);
                System.out.println("thread normal : " + thread.getName());
            }
            CustomersThread.add(thread);
        }
    }

    private static void simulateCustomersAtTables() {
        int customersAtTable = Math.min(remainingTables, customers.size());
        //bilgiTextArea.setEditable(false);
        panel1.add(createEmptyPanel());
        JLabel baslik1 = new JLabel("SİPARİŞLER");
        panel1.add(baslik1);
        panel1.add(createEmptyPanel());
        panel1.add(scrollPane1);
        panel2.add(createEmptyPanel());
        JLabel baslik2 = new JLabel("KASA");
        JLabel toplamMusteri = new JLabel("Ödeme Oranı: ");
        JLabel slash = new JLabel(" / ");
        panel2.add(baslik2);
        panel2.add(createEmptyPanel());
        panel2.add(toplamMusteri);
        panel2.add(caseCustomerPayLabel);
        panel2.add(slash);
        panel2.add(caseCustomerLabel);
        panel2.add(createEmptyPanel());
        panel2.add(scrollPane2);
        panel2.add(scrollPane21);
        panel.add(createEmptyPanel());
        JLabel baslik = new JLabel("MASALAR");
        panel.add(baslik);
        panel.add(createEmptyPanel());
        bilgiTextArea.append(customers.size() + " Müşteri geldi.\n");
        try {
            yazici.write(customers.size() + " Müşteri geldi.\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < customersAtTable; i++) {
            //ilk altı kişiyi bekleyenlerden sil
            Customer customer = waitingCustomers.remove(0);

            //boş masa sayısı
            remainingTables--;
            tablePanel = new JPanel();
            tablePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            tablePanel.setSize(70, 50);

            Dimension panelSize = new Dimension(70, 50);
            tablePanel.setPreferredSize(panelSize);
            // masaların arka plan rengini müşteri tipine göre belirle
            if (customer.isPriority()) {
                priorityCustomersCustomer.remove(0);
                tablePanel.setBackground(priorityColor);
            } else {
                normalCustomersCustomer.remove(0);
                tablePanel.setBackground(normalColor);
            }

            JLabel label = new JLabel("Müşteri " + customer.getCustomerID());
            tablePanel.add(label);
            tablePanelList.add(tablePanel);
            panel.add(tablePanel);

            bilgiTextArea.append(customer.getCustomerID() + " Nolu müşteri oturdu.\n");
            try {
                yazici.write(customer.getCustomerID() + " Nolu müşteri oturdu.\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(customer.getCustomerID() + " Nolu Müşteri Masaya Yerleştirildi.");
        }
        bilgiTextArea.append(waitingCustomers.size() + " Müşteri beklemede.\n");
        try {
            yazici.write(waitingCustomers.size() + " Müşteri beklemede.\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        panel.add(createEmptyPanel());
        JLabel oncelikliMusteri = new JLabel("Öncelikli Müşteri");
        JLabel normalMusteri = new JLabel("Normal Müşteri");
        JLabel yemekYeme = new JLabel("Yemek Yeniyor");
        JLabel siparisAlma = new JLabel("Sipariş Veriliyor");
        JLabel kasa = new JLabel("Kasa Sırasında");
        oncelikliMusteri.setForeground(priorityColor);
        normalMusteri.setForeground(normalColor);
        yemekYeme.setForeground(eatFood);
        siparisAlma.setForeground(takeOrderColor);
        kasa.setForeground(payListColor);
        panel.add(oncelikliMusteri);
        panel.add(normalMusteri);
        panel.add(siparisAlma);
        panel.add(yemekYeme);
        panel.add(kasa);
        panel.add(createEmptyPanel());
        panel.add(scrollPane);
        waitingCustomers.clear();
        waitingCustomers.addAll(priorityCustomersCustomer);
        waitingCustomers.addAll(normalCustomersCustomer);

    }

    protected static void takeOrder(int waiterID, int customerID, int tableNumber, boolean server) {

        if (server) {
            bilgiTextArea.append(waiterID + " Nolu garson " + customerID + " Nolu müşterinin siparişini aşçıya iletti: (Masa " + tableNumber + ")\n");
            orderTextArea.append(customerID + " Nolu müşteriden sipariş geldi! (Masa " + tableNumber + ")\n");
            try {
                yazici.write(waiterID + " Nolu garson " + customerID + " Nolu müşterinin siparişini aşçıya iletti: (Masa " + tableNumber + ")\n");
                yazici.write(customerID + " Nolu müşteriden sipariş geldi! (Masa " + tableNumber + ")\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        } else {
            tablePanelList.get(tableNumber - 1).setBackground(takeOrderColor);
            bilgiTextArea.append(waiterID + " Nolu garson " + customerID + " Nolu müşterinin siparişini aldı: (Masa " + tableNumber + ")\n");
            try {
                yazici.write(waiterID + " Nolu garson " + customerID + " Nolu müşterinin siparişini aldı: (Masa " + tableNumber + ")\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public static void prepareOrder(int chefID, int orderID, boolean ready) {
        int tableNumber = 0;
        Customer customerFood = null;

        for (Table table : tables) {
            if (table.getCustomer() != null && table.getCustomer().getCustomerID() == orderID) {
                tableNumber = table.getTableNumber();
            }
        }

        for (Customer customer : customers) {
            if (customer.getCustomerID() == orderID) {
                customerFood = customer;
            }
        }

        if (ready) {
            tablePanelList.get(tableNumber - 1).setBackground(eatFood);
            bilgiTextArea.append(chefID + " Nolu aşçı " + orderID + " Nolu müşterinin siparişini hazırladı. Sipariş teslim edildi.\n");
            orderTextArea.append(chefID + " Nolu aşçı " + orderID + " Nolu müşterinin siparişini hazırladı. Sipariş teslim edildi.\n");
            assert customerFood != null;
            customerFood.setFood(true);
            try {
                yazici.write(chefID + " Nolu aşçı " + orderID + " Nolu müşterinin siparişini hazırladı. Sipariş teslim edildi.\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        } else {
            //bilgiTextArea.append(chefID + " Nolu aşçı " + orderID + " Nolu müşterinin siparişini aldı ve hazırlamaya başladı.\n");
            orderTextArea.append(chefID + " Nolu aşçı " + orderID + " Nolu müşterinin siparişini hazırlamaya başladı.\n");
            try {
                yazici.write(chefID + " Nolu aşçı " + orderID + " Nolu müşterinin siparişini hazırlamaya başladı.\n");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void addPayList(int customerID) {

        int tableNumber = 0;

        for (Table table : tables) {
            if (table.getCustomer() != null && table.getCustomer().getCustomerID() == customerID) {
                tableNumber = table.getTableNumber();
            }
        }
        tablePanelList.get(tableNumber - 1).setBackground(payListColor);
        cashierTextArea.append(customerID + " Nolu müşteri ödeme için bekliyor.\n");
        payList.add(customerID);
        try {
            yazici.write(customerID + " Nolu müşteri ödeme için bekliyor.\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private static JPanel createEmptyPanel() {
        JPanel emptyPanel = new JPanel();
        emptyPanel.setBorder(new EmptyBorder(5, 5, 5, 500));
        //emptyPanel.setBackground(Color.ORANGE);
        return emptyPanel;
    }

}
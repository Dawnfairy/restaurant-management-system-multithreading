import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Cashier implements Runnable {
    public Cashier() {
    }

    @Override
    public void run() {
        while (true) {
            synchronized (Main.payList) {
                // ödeme listesindeki ödemeleri al ve işle
                for (Integer payList : new ArrayList<>(Main.payList)) {
                    Main.cashierTextArea.append(payList + " Nolu müşterinin ödemesi alınıyor.\n");
                    try {
                        Main.yazici.write(payList + " Nolu müşterinin ödemesi alınıyor.\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.println("Sipariş işlendi: " + payList);
                    Main.cashierTextArea.append(payList + " Nolu müşterinin ödemesi tamamlandı.\n");
                    Main.cashierTextArea1.append(payList + " Nolu müşteri restorandan ayrıldı.\n");
                    try {
                        Main.yazici.write(payList + " Nolu müşterinin ödemesi tamamlandı.\n");
                        Main.yazici.write(payList + " Nolu müşteri restorandan ayrıldı.\n");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Main.caseCustomerPay++;
                    String count = String.valueOf(Main.caseCustomerPay);
                    Main.caseCustomerPayLabel.setText(count);
                    Main.payList.remove(payList); // İşlenen siparişi listeden kaldır

                    int tableNumber = 0;
                    for (Table table : Main.tables) {
                        if (table.getCustomer() != null && table.getCustomer().getCustomerID() == payList) {
                            tableNumber = table.getTableNumber();
                            table.setCustomer(null);
                        }
                    }
                    Main.tablePanelList.get(tableNumber - 1).setBackground(Color.WHITE);
                    Component[] components = Main.tablePanelList.get(tableNumber - 1).getComponents();
                    for (Component component : components) {
                        if (component instanceof JLabel) {
                            Main.tablePanelList.get(tableNumber - 1).remove(component);
                            break; // Kaldırdıktan sonra döngüden çık
                        }
                    }
                    Main.CustomersThread.get(payList - 1).interrupt();// ödeme yapan müşterinin thread ını öldür
                    Main.remainingTables++;

                }

            }
            boolean finish = true;
            for (Table table : Main.tables) {
                if (table.getCustomer() != null) {
                    finish = false;
                    break;
                }
            }
            if (finish) {
                try {
                    Main.yazici.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                for (Thread waiter : Main.WaiterThread) {
                    waiter.interrupt();
                }
                JOptionPane.showMessageDialog(null, "SİMÜLASYON SONA ERDİ!", "Uyarı", JOptionPane.INFORMATION_MESSAGE);

                for (Thread cashier : Main.CashierThread) {
                    cashier.interrupt();
                }
                break;

            }
        }
    }


}

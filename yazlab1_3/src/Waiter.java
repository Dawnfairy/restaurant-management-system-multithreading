import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

class Waiter implements Runnable {
    private final int waiterID;
    private final ArrayList<Table> assignedTables;

    public Waiter(int waiterID, ArrayList<Table> assignedTables) {
        this.waiterID = waiterID;
        this.assignedTables = assignedTables;
    }

    public int getWaiterID() {
        return waiterID;
    }

    @Override
    public void run() {
        while (true) {
            for (Table table : assignedTables) {
                tryTakeOrder(table);
            }
        }
    }

    private void tryTakeOrder(Table table) {
        if (table.getLock().tryLock()) {
            try {
                if (table.getCustomer() != null && !table.isOrder()) {
                    Customer customer = table.getCustomer();

                    try {
                        Thread.sleep(2000); // Sipariş alma süresini simüle et
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Main.takeOrder(waiterID, customer.getCustomerID(), table.getTableNumber(), false);

                    try {
                        Thread.sleep(1000); // Siparişi servis etme süresini simüle et
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    Main.takeOrder(waiterID, customer.getCustomerID(), table.getTableNumber(), true);
                    int orderId = customer.getCustomerID();
                    synchronized (Main.orderList) {
                        Main.orderList.add(orderId);
                        Main.orderList.notifyAll();
                    }
                    table.setOrder(true);
                } else if (table.getCustomer() == null && !Main.waitingCustomers.isEmpty()) {
                    synchronized (Main.waitingCustomers) {
                        Customer customer = Main.waitingCustomers.remove(0);
                        table.setCustomer(customer);
                        table.setOrder(false);
                        Main.caseCustomer++;
                        String count = String.valueOf(Main.caseCustomer);
                        Main.caseCustomerLabel.setText(count);

                        if (customer.isPriority()) {
                            Main.priorityCustomersCustomer.remove(0);
                            Main.tablePanelList.get(table.getTableNumber() - 1).setBackground(Main.priorityColor);
                        } else {
                            Main.normalCustomersCustomer.remove(0);
                            Main.tablePanelList.get(table.getTableNumber() - 1).setBackground(Main.normalColor);
                        }

                        // İlgili tablePanel'dan JLabel'ı kaldır
                        Component[] components = Main.tablePanelList.get(table.getTableNumber() - 1).getComponents();
                        for (Component component : components) {
                            if (component instanceof JLabel) {
                                Main.tablePanelList.get(table.getTableNumber() - 1).remove(component);
                                break; // Kaldırdıktan sonra döngüden çık
                            }
                        }

                        JLabel label = new JLabel("Müşteri " + customer.getCustomerID());
                        Main.tablePanelList.get(table.getTableNumber() - 1).add(label);
                        Main.tablePanelList.get(table.getTableNumber() - 1).revalidate();
                        Main.tablePanelList.get(table.getTableNumber() - 1).repaint();

                        Main.bilgiTextArea.append(customer.getCustomerID() + " Nolu müşteri oturdu.\n");
                        Main.yazici.write(customer.getCustomerID() + " Nolu müşteri oturdu.\n");
                        System.out.println(customer.getCustomerID() + " Nolu Müşteri Masaya Yerleştirildi.");

                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                table.getLock().unlock();
            }
        }
    }

}
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Table {
    private int tableNumber;
    private Customer customer = null;
    private boolean order = false;
    private final Lock lock;

    public Table(int tableNumber) {
        this.tableNumber = tableNumber;
        this.lock = new ReentrantLock();
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public boolean isOrder() {
        return order;
    }

    public void setOrder(boolean order) {
        this.order = order;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Lock getLock() {
        return lock;
    }
}
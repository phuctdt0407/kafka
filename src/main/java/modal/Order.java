package modal;

public class Order {
    private int idOrder;
    private User user;

    private Driver driver;

    public Order(int idOrder, User user, Driver driver) {
        this.idOrder = idOrder;
        this.user = user;
        this.driver = driver;
    }

    public int getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(int idOrder) {
        this.idOrder = idOrder;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }
}

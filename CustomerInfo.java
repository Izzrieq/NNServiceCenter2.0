import java.util.ArrayList;
import java.util.List;

public class CustomerInfo {
    int customerId;
    String customerName;
    String vehiclePlateNumber;
    List<ServiceInfo> services;

    // Constructor
    public CustomerInfo(int customerId, String customerName, String vehiclePlateNumber) {
        this.customerId = customerId;
        this.customerName = customerName;
        this.vehiclePlateNumber = vehiclePlateNumber;
        this.services = new ArrayList<>();
    }

    // Add a service to the customer
    public void addService(ServiceInfo service) {
        services.add(service);
    }

    // Get the total cost of all services
    public double getTotalCost() {
        return services.stream().mapToDouble(ServiceInfo::getServiceCost).sum();
    }

    // Getters
    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getVehiclePlateNumber() {
        return vehiclePlateNumber;
    }

    public List<ServiceInfo> getServices() {
        return services;
    }
}

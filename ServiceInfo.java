public class ServiceInfo {
    int serviceId;
    String serviceType;
    double serviceCost;
    String serviceDate;
    String estimatedCompletionTime;

    // Constructor
    public ServiceInfo(int serviceId, String serviceType, double serviceCost, String serviceDate, String estimatedCompletionTime) {
        this.serviceId = serviceId;
        this.serviceType = serviceType;
        this.serviceCost = serviceCost;
        this.serviceDate = serviceDate;
        this.estimatedCompletionTime = estimatedCompletionTime;
    }
    
    // Getter
    public double getServiceCost() {
        return serviceCost;
    }
    public int getServiceId() {
        return serviceId;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getServiceDate() {
        return serviceDate;
    }

    public String getEstimatedCompletionTime() {
        return estimatedCompletionTime;
    }
}

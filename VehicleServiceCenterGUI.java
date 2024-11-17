import java.io.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Stack;

public class VehicleServiceCenterGUI {
    private static List<CustomerInfo> customerList = new ArrayList<>();
    private static DecimalFormat df = new DecimalFormat("#.00"); 
    private static int currentIndex = 0; 
    private static JLabel customerListLabel = new JLabel();
    private static Set<CustomerInfo> customersInLanes = new LinkedHashSet<>(); 
    private static Stack<CustomerInfo> completeStack = new Stack<>();
    private static Map<Integer, CustomerInfo> comboBoxIndexToCustomerMap = new HashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
                    JFrame frame = new JFrame("NN Vehicle Service Center");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setSize(1200, 800);
                    frame.setLayout(new BorderLayout());

                    JTextArea customerArea = new JTextArea(100, 60);
                    customerArea.setEditable(false);
                    JScrollPane customerScroll = new JScrollPane(customerArea);

                    JTextArea lane1Area = new JTextArea(5, 40);
                    lane1Area.setEditable(false);
                    JScrollPane lane1Scroll = new JScrollPane(lane1Area);

                    JTextArea lane2Area = new JTextArea(5, 40);
                    lane2Area.setEditable(false);
                    JScrollPane lane2Scroll = new JScrollPane(lane2Area);

                    JTextArea lane3Area = new JTextArea(5, 40);
                    lane3Area.setEditable(false);
                    JScrollPane lane3Scroll = new JScrollPane(lane3Area);

                    JPanel panel = new JPanel();
                    panel.setLayout(new BorderLayout());
                    customerListLabel.setText("Customer List: 0 in file - 0 remaining"); 
                    panel.add(customerListLabel, BorderLayout.NORTH); 
                    panel.add(customerScroll, BorderLayout.CENTER);

                    JPanel lanePanel = new JPanel();
                    lanePanel.setLayout(new GridLayout(3, 1));
                    lanePanel.add(new JLabel("Service Lane 1"));
                    lanePanel.add(lane1Scroll);
                    lanePanel.add(new JLabel("Service Lane 2"));
                    lanePanel.add(lane2Scroll);
                    lanePanel.add(new JLabel("Service Lane 3"));
                    lanePanel.add(lane3Scroll);

                    JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, lanePanel);
                    splitPane.setDividerLocation(300);

                    frame.add(splitPane, BorderLayout.CENTER);

                    JPanel bottomPanel = new JPanel(new BorderLayout());
                    JButton loadButton = new JButton("Load Data into Lanes");
                    JButton queueButton = new JButton("Queue Customers");

                    JPanel buttonPanel = new JPanel(new FlowLayout());
                    buttonPanel.add(loadButton);
                    buttonPanel.add(queueButton);

                    JButton addButton = new JButton("Add Customer");
                    JButton deleteButton = new JButton("Delete Selected Customer");
                    buttonPanel.add(addButton);
                    buttonPanel.add(deleteButton);

                    JButton addServiceButton = new JButton("Add Services");
                    buttonPanel.add(addServiceButton);

                    JButton exportButton = new JButton("Export Completed Transactions");
                    buttonPanel.add(exportButton);

                    bottomPanel.add(buttonPanel, BorderLayout.NORTH);

                    JComboBox<String> customerSelector = new JComboBox<>();
                    JTextArea customerDetailsArea = new JTextArea(10, 40);
                    customerDetailsArea.setEditable(false);

                    bottomPanel.add(new JLabel("Select Customer for Details:"), BorderLayout.WEST);
                    bottomPanel.add(customerSelector, BorderLayout.CENTER);
                    bottomPanel.add(new JScrollPane(customerDetailsArea), BorderLayout.SOUTH);

                    frame.add(bottomPanel, BorderLayout.SOUTH);

                    loadButton.addActionListener(e -> {
                                loadAndProcessData(customerArea, lane1Area, lane2Area, lane3Area, false);
                                populateCustomerSelector(customerSelector); 
                        });

                    queueButton.addActionListener(e -> {
                                loadAndProcessData(customerArea, lane1Area, lane2Area, lane3Area, true); 
                                populateCustomerSelector(customerSelector); 
                        });

                    customerSelector.addActionListener(e -> {
                                int selectedIndex = customerSelector.getSelectedIndex();
                                if (selectedIndex >= 0) {
                                    CustomerInfo selectedCustomer = comboBoxIndexToCustomerMap.get(selectedIndex);
                                    if (selectedCustomer != null) {
                                        displayCustomerDetails(selectedCustomer, customerDetailsArea);
                                    }
                                }
                        });

                    addButton.addActionListener(e -> addCustomer(customerSelector, customerArea));

                    deleteButton.addActionListener(e -> {
                                int selectedIndex = customerSelector.getSelectedIndex();
                                if (selectedIndex >= 0) {
                                    deleteCustomer(selectedIndex, customerSelector, customerArea);
                                } else {
                                    JOptionPane.showMessageDialog(null, "Please select a customer to delete.");
                                }
                        });

                    addServiceButton.addActionListener(e -> {
                                int selectedIndex = customerSelector.getSelectedIndex();
                                if (selectedIndex >= 0) {
                                    CustomerInfo selectedCustomer = comboBoxIndexToCustomerMap.get(selectedIndex);
                                    if (selectedCustomer != null) {
                                        addService(selectedCustomer, customerDetailsArea);
                                    }
                                } else {
                                    JOptionPane.showMessageDialog(null, "Please select a customer to add a service.");
                                }
                        });

                    exportButton.addActionListener(e -> {
                                exportCompletedTransactionsToFile();
                        }); 

                    frame.setVisible(true);
            });
    }

    private static void addService(CustomerInfo customer, JTextArea customerDetailsArea) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JCheckBox service1 = new JCheckBox("1. Oil Change, Cost: 89.99, Date: 2024-06-15, Duration: 2 Hour");
        JCheckBox service2 = new JCheckBox("2. Tire Rotation, Cost: 122.55, Date: 2024-06-20, Duration: 2 Hour");
        JCheckBox service3 = new JCheckBox("3. Brake Inspection, Cost: 61.12, Date: 2024-06-23, Duration: 1 Hour");
        JCheckBox service4 = new JCheckBox("4. Engine Tune-Up, Cost: 155.35, Date: 2024-06-30, Duration: 3 Hours");

        panel.add(service1);
        panel.add(service2);
        panel.add(service3);
        panel.add(service4);

        JButton addButton = new JButton("Add Selected Services");
        panel.add(addButton);

        addButton.addActionListener(e -> {if (service1.isSelected()) {
                        ServiceInfo service = new ServiceInfo(1, "Oil Change", 89.99, "2024-06-15", "2 Hour");
                        customer.addService(service);
                    }
                    if (service2.isSelected()) {
                        ServiceInfo service = new ServiceInfo(2, "Tire Rotation", 122.55, "2024-06-20", "2 Hour");
                        customer.addService(service);
                    }
                    if (service3.isSelected()) {
                        ServiceInfo service = new ServiceInfo(3, "Brake Inspection", 61.12, "2024-06-23", "1 Hour");
                        customer.addService(service);
                    }
                    if (service4.isSelected()) {
                        ServiceInfo service = new ServiceInfo(4, "Engine Tune-Up", 155.35, "2024-06-30", "3 Hours");
                        customer.addService(service);
                    }

                    JOptionPane.showMessageDialog(null, "Selected services added to customer!");

                    displayCustomerDetails(customer, customerDetailsArea);
            });

        int option = JOptionPane.showOptionDialog(null, panel, "Select Services to Add", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, 
                null, new Object[]{}, null);

        if (option == JOptionPane.OK_OPTION) {
        }
    }

    private static void loadAndProcessData(JTextArea customerArea, JTextArea lane1Area, JTextArea lane2Area, JTextArea lane3Area, boolean limitQueue) {
        StringBuilder lane1Info = new StringBuilder();
        StringBuilder lane2Info = new StringBuilder();
        StringBuilder lane3Info = new StringBuilder();
        StringBuilder displayedCustomerInfo = new StringBuilder(); 

        if (customerList.isEmpty()) {
            try (BufferedReader br = new BufferedReader(new FileReader("serviceRequests.txt"))) {
                String line;
                CustomerInfo currentCustomer = null;

                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        int customerId = Integer.parseInt(parts[0].trim());
                        String customerName = parts[1].trim();
                        String vehiclePlateNumber = parts[2].trim();
                        currentCustomer = new CustomerInfo(customerId, customerName, vehiclePlateNumber);
                        customerList.add(currentCustomer);
                    } else if (parts.length == 5) {
                        int serviceId = Integer.parseInt(parts[0].trim());
                        String serviceType = parts[1].trim();
                        double serviceCost = Double.parseDouble(parts[2].trim());
                        String serviceDate = parts[3].trim();
                        String estimatedCompletionTime = parts[4].trim();

                        if (currentCustomer != null) {
                            ServiceInfo service = new ServiceInfo(serviceId, serviceType, serviceCost, serviceDate, estimatedCompletionTime);
                            currentCustomer.addService(service);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        int lane1Count = 0, lane2Count = 0, lane3Count = 0;
        Queue<CustomerInfo> lane1Queue = new LinkedList<>();
        Queue<CustomerInfo> lane2Queue = new LinkedList<>();
        Queue<CustomerInfo> lane3Queue = new LinkedList<>();

        boolean assignToLane1 = true;

        for (int i = currentIndex; i < customerList.size() && (lane1Count < 5 || lane2Count < 5 || lane3Count < 5); i++) {
            CustomerInfo customer = customerList.get(i);
            int numServices = customer.getServices().size();

            if (numServices <= 3) {
                if (assignToLane1 && lane1Count < 5) {
                    lane1Queue.add(customer);
                    lane1Count++;
                    displayedCustomerInfo.append("Lane 1: ").append(customer.getCustomerName()).append("\n");
                    customersInLanes.add(customer); 
                    currentIndex++;  
                    assignToLane1 = false;  
                } else if (!assignToLane1 && lane2Count < 5) {
                    lane2Queue.add(customer);
                    lane2Count++;
                    displayedCustomerInfo.append("Lane 2: ").append(customer.getCustomerName()).append("\n");
                    customersInLanes.add(customer);
                    currentIndex++; 
                    assignToLane1 = true;  
                }
            } else if (lane3Count < 5) {
                lane3Queue.add(customer);
                lane3Count++;
                displayedCustomerInfo.append("Lane 3: ").append(customer.getCustomerName()).append("\n");
                customersInLanes.add(customer);
                currentIndex++; 
            }
        }

        lane1Info.append("\nProcessing Service Lane 1:\n");
        processLane(lane1Queue, lane1Info);
        lane2Info.append("\nProcessing Service Lane 2:\n");
        processLane(lane2Queue, lane2Info);
        lane3Info.append("\nProcessing Service Lane 3:\n");
        processLane(lane3Queue, lane3Info);

        customerArea.setText(displayedCustomerInfo.toString());
        lane1Area.setText(lane1Info.toString());
        lane2Area.setText(lane2Info.toString());
        lane3Area.setText(lane3Info.toString());

        int remainingCustomers = customerList.size() - currentIndex;
        customerListLabel.setText("Customer List: " + customerList.size() + " in file - " + remainingCustomers + " remaining");
    }

    private static void populateCustomerSelector(JComboBox<String> customerSelector) {
        customerSelector.removeAllItems();
        comboBoxIndexToCustomerMap.clear(); 
        int index = 0;
        for (CustomerInfo customer : customersInLanes) { 
            customerSelector.addItem(customer.getCustomerName());
            comboBoxIndexToCustomerMap.put(index, customer);
            index++;
        }
    }

    private static void displayCustomerDetails(CustomerInfo customer, JTextArea customerDetailsArea) {
        StringBuilder details = new StringBuilder();
        details.append("Customer ID: ").append(customer.getCustomerId()).append("\n");
        details.append("Name: ").append(customer.getCustomerName()).append("\n");
        details.append("Vehicle Plate Number: ").append(customer.getVehiclePlateNumber()).append("\n");
        details.append("Services:\n");
        for (ServiceInfo service : customer.getServices()) {
            details.append("- Service ID: ").append(service.getServiceId())
            .append(", Type: ").append(service.getServiceType())
            .append(", Cost: ").append(df.format(service.getServiceCost()))
            .append(", Date: ").append(service.getServiceDate())
            .append(", Estimated Completion: ").append(service.getEstimatedCompletionTime())
            .append("\n");
        }
        customerDetailsArea.setText(details.toString());
    }

    private static void processLane(Queue<CustomerInfo> laneQueue, StringBuilder laneInfo) {
        while (!laneQueue.isEmpty()) {
            CustomerInfo customer = laneQueue.poll();
            double totalCost = customer.getTotalCost();
            laneInfo.append("Completed: ").append(customer.getCustomerName())
            .append(" Total Cost: ").append(df.format(totalCost)).append("\n");
            completeStack.push(customer);
        }
    }

    private static void addCustomer(JComboBox<String> customerSelector, JTextArea customerArea) {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField vehicleField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(3, 2));
        panel.add(new JLabel("Customer ID:"));
        panel.add(idField);
        panel.add(new JLabel("Customer Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Vehicle Plate Number:"));
        panel.add(vehicleField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Add New Customer", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                int customerId = Integer.parseInt(idField.getText().trim());
                String customerName = nameField.getText().trim();
                String vehiclePlateNumber = vehicleField.getText().trim();

                if (customerName.isEmpty() || vehiclePlateNumber.isEmpty()) {
                    throw new IllegalArgumentException("All fields must be filled out.");
                }

                CustomerInfo newCustomer = new CustomerInfo(customerId, customerName, vehiclePlateNumber);
                customerList.add(newCustomer);
                JOptionPane.showMessageDialog(null, "Customer added successfully!");

                populateCustomerSelector(customerSelector);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Invalid customer ID. Please enter a valid number.");
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }
    }

    private static void deleteCustomer(int index, JComboBox<String> customerSelector, JTextArea customerArea) {
        if (index >= 0 && index < customerList.size()) {
            CustomerInfo removedCustomer = customerList.remove(index);
            customersInLanes.remove(removedCustomer); 
            JOptionPane.showMessageDialog(null, "Customer '" + removedCustomer.getCustomerName() + "' deleted successfully!");
        } else {
            JOptionPane.showMessageDialog(null, "Invalid selection.");
        }
    }

    private static void updateCustomerArea(JTextArea customerArea) {
        StringBuilder displayedCustomerInfo = new StringBuilder();
        for (CustomerInfo customer : customerList) {
            if (!customersInLanes.contains(customer)) {
                displayedCustomerInfo.append("Customer ID: ").append(customer.getCustomerId())
                .append(", Name: ").append(customer.getCustomerName())
                .append(", Vehicle Plate Number: ").append(customer.getVehiclePlateNumber())
                .append("\n");
            }
        }
        customerArea.setText(displayedCustomerInfo.toString());
    }

    private static void exportCompletedTransactionsToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("completedTransactions.txt"))) {
            while (!completeStack.isEmpty()) {
                CustomerInfo customer = completeStack.pop(); 
                writer.write("Customer ID: " + customer.getCustomerId() + "\n");
                writer.write("Name: " + customer.getCustomerName() + "\n");
                writer.write("Vehicle Plate Number: " + customer.getVehiclePlateNumber() + "\n");
                writer.write("Services:\n");
                for (ServiceInfo service : customer.getServices()) {
                    writer.write("- Service ID: " + service.getServiceId()
                        + ", Type: " + service.getServiceType()
                        + ", Cost: " + df.format(service.getServiceCost())
                        + ", Date: " + service.getServiceDate()
                        + ", Estimated Completion: " + service.getEstimatedCompletionTime()
                        + "\n");
                }
                writer.write("Total Service Cost Paid: " + df.format(customer.getTotalCost()) + "\n");
                writer.write("-------------------------------------------------\n");
            }
            JOptionPane.showMessageDialog(null, "Completed transactions exported to 'completedTransactions.txt'!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error exporting transactions: " + e.getMessage());
            e.printStackTrace();
        }
    }

}

//invoice generator using applet
import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.print.*;
import java.util.ArrayList;

public class InvoiceGeneratorApplet extends Applet implements Printable {

    private TextField itemNameField, itemPriceField, quantityField, customerNameField;
    private Button addButton, generateInvoiceButton;
    private TextArea invoiceArea;
    private ArrayList<Product> products;

    public void init() {
        setLayout(new BorderLayout());

        // Panel for input fields and buttons
        Panel inputPanel = new Panel();
        inputPanel.setLayout(new GridLayout(5, 2, 10, 10));

        // Initialize product list
        products = new ArrayList<>();

        // Input fields for Customer Name, Item Name, Item Price, and Quantity
        inputPanel.add(new Label("Customer Name:"));
        customerNameField = new TextField(20);
        inputPanel.add(customerNameField);

        inputPanel.add(new Label("Item Name:"));
        itemNameField = new TextField(20);
        inputPanel.add(itemNameField);

        inputPanel.add(new Label("Item Price (₹):"));
        itemPriceField = new TextField(10);
        inputPanel.add(itemPriceField);

        inputPanel.add(new Label("Quantity:"));
        quantityField = new TextField(5);
        inputPanel.add(quantityField);

        // Buttons for adding product and generating invoice
        addButton = new Button("Add Product");
        inputPanel.add(addButton);
        generateInvoiceButton = new Button("Generate Invoice");
        inputPanel.add(generateInvoiceButton);

        // Add input panel to the top of the layout
        add(inputPanel, BorderLayout.NORTH);

        // Panel to display the invoice area neatly in the center
        Panel centerPanel = new Panel();
        centerPanel.setLayout(new BorderLayout());

        // Text area to display invoice, added to the center panel
        invoiceArea = new TextArea(20, 50);
        invoiceArea.setEditable(false);
        invoiceArea.setFont(new Font("Monospaced", Font.PLAIN, 12)); // Use monospaced font for neat alignment
        centerPanel.add(invoiceArea, BorderLayout.CENTER);

        // Add center panel to the main layout
        add(centerPanel, BorderLayout.CENTER);

        // Action listener for adding products
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addProduct();
            }
        });

        // Action listener for generating the invoice
        generateInvoiceButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                generateInvoice();
            }
        });
    }

    // Method to add product to the list
    private void addProduct() {
        try {
            String itemName = itemNameField.getText();
            double itemPrice = Double.parseDouble(itemPriceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            // Add the product to the list
            products.add(new Product(itemName, itemPrice, quantity));

            // Update the invoice area with all products
            updateInvoiceArea();
        } catch (NumberFormatException e) {
            invoiceArea.setText("Error: Please enter valid numeric values for price and quantity.");
        }
    }

    // Method to update the invoice area with current product list
    private void updateInvoiceArea() {
        StringBuilder invoiceText = new StringBuilder();
        double subTotal = 0;

        // Add heading and store name
        invoiceText.append("******************************************\n");
        invoiceText.append("            KUMUDHAM STORE\n");
        invoiceText.append("       Thank You for Shopping With Us!\n");
        invoiceText.append("******************************************\n\n");

        // Add customer name
        String customerName = customerNameField.getText().trim();
        if (!customerName.isEmpty()) {
            invoiceText.append("Customer: ").append(customerName).append("\n");
        }

        // Generate the invoice details
        invoiceText.append("\nItems Purchased:\n");
        invoiceText.append("----------------------------------\n");
        for (Product product : products) {
            invoiceText.append(String.format("%-25s ₹%-10.2f x%-5d ₹%-10.2f\n", product.getItemName(),
                    product.getItemPrice(), product.getQuantity(), product.getItemPrice() * product.getQuantity()));
            subTotal += product.getItemPrice() * product.getQuantity();
        }

        // Calculate Tax and Total (GST at 18%)
        double gst = subTotal * 0.18; // GST is 18%
        double totalAmount = subTotal + gst;

        invoiceText.append("\n----------------------------------\n");
        invoiceText.append(String.format("%-25s ₹%-10.2f\n", "Subtotal:", subTotal));
        invoiceText.append(String.format("%-25s ₹%-10.2f\n", "GST (18%):", gst));
        invoiceText.append(String.format("%-25s ₹%-10.2f\n", "Total Amount:", totalAmount));
        invoiceText.append("\n******************************************\n");
        invoiceText.append("           Thank You for Your Purchase!\n");
        invoiceText.append("******************************************\n");

        // Set the invoice text to the text area
        invoiceArea.setText(invoiceText.toString());
    }

    // Method to generate the invoice (printing it)
    private void generateInvoice() {
        try {
            PrinterJob printerJob = PrinterJob.getPrinterJob();
            printerJob.setPrintable(this);
            if (printerJob.printDialog()) {
                printerJob.print();
            }
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    // Printable interface method
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        // Print the invoice content
        String invoiceText = invoiceArea.getText();
        String[] lines = invoiceText.split("\n");
        int y = 100;
        for (String line : lines) {
            g.drawString(line, 100, y);
            y += 15; 
        }

        return PAGE_EXISTS;
    }

    // Product class to hold item details
    class Product {
        private String itemName;
        private double itemPrice;
        private int quantity;

        public Product(String itemName, double itemPrice, int quantity) {
            this.itemName = itemName;
            this.itemPrice = itemPrice;
            this.quantity = quantity;
        }

        public String getItemName() {
            return itemName;
        }

        public double getItemPrice() {
            return itemPrice;
        }

        public int getQuantity() {
            return quantity;
        }
    }
}

package main.java;

import java.io.*;
import java.security.InvalidParameterException;
import java.util.*;

import main.DataStructures.SkipList;
import main.DataStructures.Trees.BinarySearchTree;
import main.java.ECommerceSystem.*;

/**
 * Class to represent Sellers in the system
 */
public class Seller extends User {
    /**
     * Inner class to control orders
     */
    protected class Order implements Comparable<Order>{
        private String customer, address, phoneNum;
        private Product product;
        private int ID;
        private int quantity;

        /**
         * Constructor for order
         * @param orderString String representation of the order
         */
        public Order(String orderString) {
            String[] temp = orderString.split(" ");
            ID = Integer.parseInt(temp[0]);
            product = getProduct(temp[1], username);
            quantity = Integer.parseInt(temp[2]);
            customer = temp[3];
            phoneNum = temp[4];
            address = temp[5];
        }

        /**
         * Constructor for order
         * @param product Product to be ordered
         * @param ID ID of the product
         * @param quantity Quantity of the product
         * @param customer Customer who have bought the product
         * @param phoneNum Phone number of the customer
         * @param address Address of the customer
         */
        public Order(Product product, int ID, int quantity, String customer, String phoneNum, String address) {
            if (product == null || customer == null || phoneNum == null || address  == null)
                throw new InvalidParameterException();

            this.ID = ID;
            this.quantity = quantity;
            this.product = product;
            this.customer = customer;
            this.address = address;
            this.phoneNum = phoneNum;
        }

        /**
         * Method to accept the order
         */
        public void accept() {
            updateOrders(ID, 1);
        }

        /**
         * Method to reject the order
         */
        public void reject() {
            updateOrders(ID, -1);
        }

        /**
         * Setter for quantity of the product
         * @param quantity Quantity of the product
         * @return boolean value if there is enough stock
         */
        public boolean setQuantity(int quantity) {
            if (quantity < 0 || quantity <= product.getStock() + this.quantity) {
                product.setStock(product.getStock() + this.quantity - quantity);
                this.quantity = quantity;
                return true;
            }

            return false;
        }

        /**
         * Overridden toString method
         * @return String representation of the Order
         */
        @Override
        public String toString() {
            StringBuilder strb = new StringBuilder();
            strb.append(ID).append(" ")
                    .append(product.getProductName())
                    .append(" ")
                    .append(quantity)
                    .append(" ")
                    .append(customer)
                    .append(" ")
                    .append(phoneNum)
                    .append(" ")
                    .append(address);

            return strb.toString();
        }

        /**
         * Method to compare orders by their IDs
         * @param order Other order that will be compared
         * @return Integer value to represent relation between other order and "this" order
         */
        @Override
        public int compareTo(Order order) {
            return ID - order.ID;
        }
    }

    private SkipList<Order> orderHistory;
    private Queue<Order> waitingOrders;
    private ArrayList<Product> productList;

    /**
     * Constructor for Seller
     * @param username Username of the seller
     * @param callerSystem System which the seller belongs to
     */
    public Seller(String username, ECommerceSystem callerSystem) {
        super(username, callerSystem);

        orderHistory = new SkipList<>();
        waitingOrders = new ArrayDeque<>();
        productList = new ArrayList<>();

        File file = new File(systemRef.resourcesDir + "Sellers/" + username + ".txt");
        if (file.exists()) {
            try {
                Scanner reader = new Scanner(file);
                String buffer = reader.nextLine();
                Product targetProduct;

                // Check that the file is not corrupted
                // Each file must start with the name of the seller
                if (!buffer.contains(username))
                    throw new StreamCorruptedException();

                // The list of products
                if (reader.hasNext()) {
                    buffer = reader.nextLine();
                    if (!buffer.isEmpty()) {
                        String[] products = buffer.split(" ");
                        for (String productName : products) {
                            Product target = getProduct(productName, username);
                            if (target != null)
                                productList.add(target);
                        }
                    }
                }

                // The list of waiting orders
                if (reader.hasNext()) {
                    buffer = reader.nextLine();
                    if (!buffer.isEmpty()) {
                        String[] orders = buffer.split("\\|");
                        for (String orderString : orders)
                            waitingOrders.add(new Order(orderString));
                    }
                }

                // The list of past orders
                if (reader.hasNext()) {
                    buffer = reader.nextLine();
                    if (!buffer.isEmpty()) {
                        String[] orders = buffer.split("\\|");
                        for (String orderString : orders) {
                            Order order = new Order(orderString);
                            orderHistory.insert(order);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.print("The file was not found, default values are used.\n");
            } catch (NoSuchElementException | StreamCorruptedException el) {
                System.out.print("The file was corrupted, default values are used.\n");
                file.renameTo(new File(systemRef.resourcesDir + "Sellers/" + username + "_corrupted.txt"));
            }
        }

        else
            new File(systemRef.resourcesDir + "Sellers").mkdir();
    }

    /**
     * User interface of the Seller, overridden from abstract User class
     */
    @Override
    public void UI(){
        int inputInt = 0;
        String inputStr = null;
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.print("\033[H\033[2JWelcome to the seller menu\nEnter the number of an action:\n1- Order management.\n2- Product Management.\n0- Log out.\n\n");
            inputInt = getInputInt(scan, "Choice: ");

            if (inputInt == 1){
                //Order Management
                while (true) {
                    System.out.print("\033[H\033[2JOrder Management:\n0- Go back.\n1- Waiting Orders.\n2- Order History.\n\n");
                    inputInt = getInputInt(scan, "Choice: ");
                    if (inputInt == 1) {
                        Object[] orders = waitingOrders.toArray();

                        int i = 0, pageStart;
                        boolean flag1 = true;
                        while (flag1) {
                            System.out.print("\033[H\033[2JWaiting Orders:\n");

                            if (waitingOrders.isEmpty()) {
                                System.out.print("There are no waiting orders.\n(Tap Enter to go back)");
                                scan.nextLine();
                                break;
                            }

                            pageStart = i;
                            System.out.printf("Page %d:\n", i / 8 + 1);
                            for (; i < orders.length && i < pageStart + 8; ++i) {
                                Order order = (Order) orders[i];
                                System.out.printf("%d- %06d: %s %d, %s\n", (i + 1),
                                        order.ID, order.product.getProductName(),
                                        order.quantity, order.customer);
                            }

                            System.out.print("\nChoose an action:\n0: Go back\nn: Next page\np: Previous page\ne: Examine the fist order\n\n");
                            boolean flag2 = true;
                            while (flag2) {
                                System.out.print("Choice: ");
                                inputStr = scan.nextLine();

                                if (inputStr.equals("p")) {
                                    if (pageStart > 7) {
                                        i = pageStart - 8;
                                        flag2 = false;
                                    }

                                    else
                                        System.out.print("\033[2A\r\033[JThere are no previous pages.\n");
                                }

                                else if (inputStr.equals("n")) {
                                    if (i >= waitingOrders.size() - 1) {
                                        System.out.print("\033[2A\r\033[JThere are no next pages.\n");
                                        i = pageStart;
                                    }

                                    flag2 = false;
                                }

                                else if (inputStr.equals("0")) {
                                    flag1 = false;
                                    flag2 = false;
                                }

                                else if (inputStr.equals("e")) {
                                    Order head = waitingOrders.peek();
                                    System.out.printf("\nOrder %d:\nProduct: %s\nQuantity: %d\nCustomer: %s\nAddress: %s\nPhone Number: %s",
                                            head.ID, head.product.getProductName(), head.quantity, head.customer, head.address, head.phoneNum);
                                    System.out.print("\nChoose an action:\n0- Go back\n1- Approve\n2- Reject\n\n");
                                    while (true) {
                                        inputInt = getInputInt(scan, "Choice: ");
                                        if (inputInt == 1) {
                                            waitingOrders.peek().accept();
                                            orderHistory.insert(waitingOrders.poll());
                                            orders = waitingOrders.toArray();
                                            flag2 = false;
                                            i = pageStart;
                                            break;
                                        }

                                        if (inputInt == 2) {
                                            Order target = waitingOrders.poll();
                                            target.reject();
                                            orderHistory.insert(target);
                                            orders = waitingOrders.toArray();
                                            target.product.setStock(target.product .getStock() + target.quantity);
                                            flag2 = false;
                                            i = pageStart;
                                            break;
                                        }

                                        else if (inputInt == 0) {
                                            i = pageStart;
                                            flag2 = false;
                                            break;
                                        }

                                        System.out.print("\033[2A\r\033[JInvalid Input\n");
                                    }
                                }

                                else {
                                    System.out.print("\033[2A\r\033[JInvalid Input\n");
                                }
                            }
                        }
                    }

                    else if (inputInt == 2) {
                        if (!orderHistory.isEmpty()) {
                            Object[] orders = orderHistory.toArray();

                            int i = 0, pageStart;
                            boolean flag1 = true;
                            while (flag1) {
                                System.out.print("\033[H\033[2JOrder History:\n");
                                pageStart = i;
                                System.out.printf("Page %d:\n", i / 8 + 1);
                                for (; i < orders.length && i < pageStart + 8; ++i) {
                                    Order order = (Order) orders[i];
                                    System.out.printf("%d- %06d: %s %d, %s\n", (i + 1),
                                            order.ID, order.product.getProductName(),
                                            order.quantity, order.customer);
                                }

                                System.out.print("\nChoose an action:\n0: Go back\nn: Next page\np: Previous page\nNumber of order to examine\n\n");
                                boolean flag2 = true;
                                while (flag2) {
                                    System.out.print("Choice: ");
                                    inputStr = scan.nextLine();

                                    if (inputStr.equals("p")) {
                                        if (i > 7) {
                                            i = (int) Math.pow((double) i, (double) (i / 8 - 1));
                                            flag2 = false;
                                        } else
                                            System.out.print("\033[2A\r\033[JThere are no previous pages.\n");
                                    }

                                    else if (inputStr.equals("n")) {
                                        if (i < orders.length - 1) {
                                            i = (int) Math.pow((double) i, (double) (i / 8 + 1));
                                            flag2 = false;
                                        } else
                                            System.out.print("\033[2A\r\033[JThere are no next pages.\n");
                                    }

                                    else if (inputStr.equals("0")) {
                                        flag1 = false;
                                        flag2 = false;
                                    }

                                    else if (isInteger(inputStr)) {
                                        int target = Integer.parseInt(inputStr) - 1;
                                        if (target > i || target < pageStart) {
                                            System.out.print("\033[2A\r\033[JInvalid Input\n");
                                        }

                                        else {
                                            Order head = (Order) orders[target];

                                            System.out.printf("\nOrder %d:\nProduct: %s\nQuantity: %d\nCustomer: %s\nAddress: %s\nPhone Number: %s\n",
                                                    head.ID, head.product.getProductName(), head.quantity, head.customer, head.address, head.phoneNum);
                                            System.out.print("\n0- Go Back.\n\n");
                                            while (true) {
                                                inputInt = getInputInt(scan, "Choice: ");
                                                if (inputInt == 0) {
                                                    i = pageStart;
                                                    flag2 = false;
                                                    break;
                                                }
                                                System.out.print("\033[2A\r\033[JInvalid Input\n");
                                            }
                                        }
                                    }

                                    else {
                                        System.out.print("\033[2A\r\033[JInvalid Input\n");
                                    }
                                }
                            }
                        }

                        else {
                            System.out.print("There are no previous orders.\n(Tap Enter to go back)");
                            scan.nextLine();
                        }
                    }

                    else if (inputInt == 0) {
                        break;
                    }

                    else {
                        System.out.print("\033[2A\r\033[JInvalid Input\n");
                    }
                }
            }

            else if (inputInt == 2){
                //Product management
                while (true) {
                    System.out.print("\033[H\033[2JProduct Management:");
                    System.out.print("\nChoose an action:\n0- Go back.\n1- View your products.\n2- Add a new product from the pool.\n3- Send a new product request to the system.\n\n");
                    inputInt = getInputInt(scan, "Choice: ");

                    if (inputInt == 0) {
                        break;
                    }

                    else if (inputInt == 1) {
                        if (!productList.isEmpty()) {
                            boolean flag1 = true;
                            int i = 0, pageStart;
                            while (flag1) {
                                System.out.print("\033[H\033[2JMy Products:\n");
                                pageStart = i;
                                System.out.printf("Page %d:\n", i / 8 + 1);
                                System.out.print("Name: Median price Stock\n");
                                for (; i < productList.size() && i < pageStart + 8; ++i) {
                                    Product product = productList.get(i);
                                    System.out.printf("%d- %s: %.3f %d\n", (i + 1),
                                            product.getProductName(), product.getPrice(), product.getStock());
                                }

                                System.out.print("\nChoose an action:\n0: Go back\nn: Next page\np: Previous page\na number: Examine the product with the number\n\n");
                                boolean flag2 = true;
                                while (flag2) {
                                    System.out.print("Choice: ");
                                    inputStr = scan.nextLine();

                                    if (inputStr.equals("p")) {
                                        if (pageStart > 7) {
                                            i = pageStart - 8;
                                            flag2 = false;
                                        }

                                        else
                                            System.out.print("\033[2A\r\033[JThere are no previous pages.\n");
                                    }

                                    else if (inputStr.equals("n")) {
                                        if (i > productList.size()) {
                                            System.out.print("\033[2A\r\033[JThere are no next pages.\n");
                                            i = pageStart;
                                        }

                                        flag2 = false;
                                    }

                                    else if (inputStr.equals("0")) {
                                        flag1 = false;
                                        flag2 = false;
                                    }

                                    else if (isInteger(inputStr)) {
                                        int target = Integer.parseInt(inputStr) - 1;
                                        if (target > i || target < pageStart) {
                                            System.out.print("\033[2A\r\033[JInvalid Input\n");
                                        }

                                        else {
                                            Product product = productList.get(target);
                                            System.out.printf("%d- %s: %.3f %d\n", (i + 1),
                                                    product.getProductName(), product.getPrice(), product.getStock());
                                            System.out.print("\n0- Go back.\n1- Remove the product from your list.\n\n");
                                            while (true) {
                                                inputInt = getInputInt(scan, "Choice: ");
                                                if (inputInt == 0) {
                                                    i = pageStart;
                                                    flag2 = false;
                                                    break;
                                                }

                                                else if (inputInt == 1) {
                                                    productList.remove(target);
                                                    getProduct(product.getProductName()).remove(product);

                                                    for (BinarySearchTree<Product> temp : getProducts())
                                                        if (temp.getData() != null && temp.getData().getProductName().equals(product.getProductName())) {
                                                            temp.remove(product);
                                                            break;
                                                        }

                                                    i = pageStart;
                                                    flag2 = false;
                                                    break;
                                                }

                                                System.out.print("\033[2A\r\033[JInvalid Input\n");
                                            }
                                        }
                                    }

                                    else {
                                        System.out.print("\033[2A\r\033[JInvalid Input\n");
                                    }
                                }
                            }
                        }

                        else {
                            System.out.print("You have no products.\n(Tap Enter to go back)");
                            scan.nextLine();
                        }
                    }

                    else if (inputInt == 2) {
                        String[] products = getProductsMap().keySet().toArray(new String[0]);

                        if (products.length != 0) {
                            boolean flag1 = true;
                            int i = 0, pageStart;
                            while (flag1) {
                                System.out.print("\033[H\033[2JProduct Pool:\n");
                                pageStart = i;
                                System.out.printf("Page %d:\n", i / 8 + 1);
                                for (; i < products.length && i - pageStart < 8; ++i)
                                    System.out.printf("%d- %s\n", (i + 1), products[i]);

                                System.out.print("\nChoose an action:\n0: Go back\nn: Next page\np: Previous page\na number: Examine the product with the number\n\n");
                                boolean flag2 = true;
                                while (flag2) {
                                    System.out.print("Choice: ");
                                    inputStr = scan.nextLine();

                                    if (inputStr.equals("p")) {
                                        if (pageStart > 7) {
                                            i = pageStart - 8;
                                            flag2 = false;
                                        }

                                        else
                                            System.out.print("\033[2A\r\033[JThere are no previous pages.\n");
                                    }

                                    else if (inputStr.equals("n")) {
                                        if (i >= products.length) {
                                            System.out.print("\033[2A\r\033[JThere are no next pages.\n");
                                            i = pageStart;
                                        }

                                        else
                                            flag2 = false;
                                    }

                                    else if (inputStr.equals("0")) {
                                        flag1 = false;
                                        flag2 = false;
                                    }

                                    else if (isInteger(inputStr)) {
                                        int target = Integer.parseInt(inputStr) - 1;
                                        if (target > i || target < pageStart) {
                                            System.out.print("\033[2A\r\033[JInvalid Input\n");
                                        }

                                        else {
                                            System.out.printf("%d- %s\n", target + 1, products[target]);
                                            if (productAvailable(products[target]) == null) {
                                                System.out.print("\nDo you want to add this product to your list?\n0- No.\n1- Yes\n\n");
                                                while (true) {
                                                    inputInt = getInputInt(scan, "Choice: ");
                                                    if (inputInt == 0) {
                                                        i = pageStart;
                                                        flag2 = false;
                                                        break;
                                                    }

                                                    else if (inputInt == 1) {
                                                        double price;
                                                        System.out.print("\n\n");
                                                        while (true) {
                                                            System.out.print("Enter the price: ");
                                                            try {
                                                                price = scan.nextDouble();
                                                                break;
                                                            } catch (Exception e) {
                                                                System.out.print("\033[2A\r\033[JInvalid Input\n");
                                                            }
                                                        }

                                                        System.out.print("\n\n");
                                                        int stock = getInputInt(scan, "Enter the stock amount: ");

                                                        Product newProduct = new Product(products[target], username, price, stock);
                                                        productList.add(newProduct);
                                                        getProduct(products[target]).add(newProduct);
/*
                                                        for (BinarySearchTree<Product> temp : getProducts())
                                                            if (temp.getData() != null && temp.getData().getProductName().equals(products[target])) {
                                                                temp.add(newProduct);
                                                                break;
                                                            }
  */                                                      updateBST();

                                                        i = pageStart;
                                                        flag2 = false;
                                                        break;
                                                    }
                                                    System.out.print("\033[2A\r\033[JInvalid Input\n");
                                                }
                                            }

                                            else {
                                                System.out.print("\nThis product is already added to your products.\n0- Go back.\n\n");
                                                while (true) {
                                                    inputInt = getInputInt(scan, "Choice: ");
                                                    if (inputInt == 0) {
                                                        i = pageStart;
                                                        flag2 = false;
                                                        break;
                                                    }
                                                    System.out.print("\033[2A\r\033[JInvalid Input\n");
                                                }
                                            }
                                        }
                                    }

                                    else {
                                        System.out.print("\033[2A\r\033[JInvalid Input\n");
                                    }
                                }
                            }
                        }

                        else {
                            System.out.print("You have no products.\n(Tap Enter to go back)");
                            scan.nextLine();
                        }
                    }

                    else if (inputInt == 3) {
                        System.out.print("\n\nEnter the name of the new product, or 0 to go back: ");
                        inputStr = scan.nextLine();
                        if (inputStr.equals("0"))
                            break;

                        else if (newProductRequest(inputStr))
                            System.out.print("The product will be added to the pool when it's approved by the admins.\n(Tap Enter to go back)");

                        else
                            System.out.print("This product already exists.\n(Tap Enter to go back)");

                        scan.nextLine();
                    }

                    else {
                        System.out.println("Invalid choice, please try again.");
                    }
                }
            }

            else if (inputInt == 0) {
                try {
                    saveToFile();
                } catch (IOException e) {
                    System.out.printf("Error: %s\n", e);
                    e.printStackTrace();
                }
                System.out.println("GOOD-BYE!");
                break;
            }

            else {
                System.out.println("Invalid choice, please try again.");
            }
        }
    }

    /**
     * Method to save informations to the file
     * @throws IOException to avoid any crash
     */
    public void saveToFile() throws IOException {
        new File(systemRef.resourcesDir + "Sellers").mkdir();
        FileWriter file = new FileWriter(systemRef.resourcesDir + "Sellers/" + username + ".txt");
        file.write(username + "\n");

        for (Product product : productList)
            file.write(product.getProductName() + " ");

        file.write("\n");

        for (Order order : waitingOrders)
            file.write(order + "|");

        file.write("\n");

        Order[] old = orderHistory.toArray();
        if (old != null)
            for (Order order : old)
                file.write(order + "|");

        file.close();
    }

    /**
     * Method to see if given product is in the productList or not
     * @param productName Name of the product
     * @return Returns product whose name is given in the parameters
     */
    public Product productAvailable(String productName) {
        for (Product product : productList)
            if (product.getProductName().equals(productName))
                return product;

        return null;
    }

    /**
     * Method to add order to the waiting orders
     * @param product Product to be ordered
     * @param ID ID of the product
     * @param quantity Quantity of the product
     * @param customer Customer who have bought the product
     * @param phoneNum Phone number of the customer
     * @param address Address of the customer
     */
    public boolean addOrder (Product product, int ID, int quantity, String customer, String phoneNum, String address) {
        if (product.getStock() - quantity < 0){
            System.out.println("There is no enough stock.");
            return false;
        }
        waitingOrders.add(new Order(product, ID, quantity, customer, phoneNum, address));
        product.setStock(product.getStock() - quantity);
        return true;
    }
}
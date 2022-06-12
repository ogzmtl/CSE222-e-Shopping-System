package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;

import main.java.ECommerceSystem.*;

public class Seller extends User {
    protected class Order {
        private String customer, address, phoneNum;
        private Product product;
        private final int ID;
        private int quantity;

        public Order(String orderString) {
            String[] temp = orderString.split(" ");
            ID = Integer.parseInt(temp[0]);
            product = getProduct(temp[1], username);
            quantity = Integer.parseInt(temp[2]);
            customer = temp[3];
            phoneNum = temp[4];
            address = temp[5];
        }

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

        public void Accept() {
            systemRef.UnproccessedOrders.put(ID, true);
        }

        public boolean setQuantity(int quantity) {
            if (quantity < 0 || quantity <= product.getStock() + this.quantity) {
                product.setStock(product.getStock() + this.quantity - quantity);
                this.quantity = quantity;
                return true;
            }

            return false;
        }

        @Override
        public String toString() {
            StringBuilder strb = new StringBuilder();
            strb.append(ID).append(":")
                .append(product.getProductName())
                .append(",")
                .append(quantity)
                .append(" ");

            return strb.toString();
        }
    }

    private LinkedList<Order> orderHistory;
    private Queue<Order> waitingOrders;
    private LinkedList<Product> productList;

    public Seller(String username, ECommerceSystem callerSystem)
            throws FileNotFoundException {
        super(username, callerSystem);

        orderHistory = new LinkedList<>();
        waitingOrders = new ArrayDeque<>();
        productList = new LinkedList<>();

        File file = new File(systemRef.resourcesDir + "Sellers/" + username + ".txt");
        if (file.exists()) {
            Scanner reader = new Scanner(file);
            String buffer = reader.nextLine();
            Product targetProduct;

            // Check that the file is not corrupted
            // Each file must start with the name of the seller
            if (!buffer.contains(username)) {
                file.renameTo(new File(username + "_damaged.txt"));
                throw new FileNotFoundException("The file found was damaged");
            }

            // The list of products
            if (reader.hasNext()){
                buffer = reader.nextLine();
                String[] products = buffer.split(" ");
                for (String productName : products)
                    productList.addFirst(getProduct(productName, username));
            }

            // The list of waiting orders
            if (reader.hasNext()) {
                buffer = reader.nextLine();
                String[] orders = buffer.split("\\|");
                for (String orderString : orders)
                    waitingOrders.add(new Order(orderString));
            }

            // The list of past orders
            if (reader.hasNext()) {
                buffer = reader.nextLine();
                String[] orders = buffer.split("\\|");
                for (String orderString : orders)
                    orderHistory.addFirst(new Order(orderString));
            }
        }
    }

    @Override
    public void UI(){
        int inputInt = 0;
        String inputStr = null;
        Scanner scan = new Scanner(System.in);
        System.out.print("Welcome to the seller menu\n");

        while (true) {
            inputInt = getInputInt(scan,"Enter the number of an action:\n1- Order management.\n2- Add a new product.\n3- Statistics.\n0- Log out.\n");
            System.out.print(inputInt);

            if (inputInt == 1){
                System.out.print("Waiting Orders:\n");
                if (!waitingOrders.isEmpty()) {
                    System.out.print("Oldest order:\n");
                    System.out.print(waitingOrders.peek());
                    System.out.print("\nDo you want to confirm the order?\n(Answer with yes or no)\n");
                    inputStr = scan.next();
                    if(inputStr.equals("yes")) {
                        waitingOrders.peek().Accept();
                        orderHistory.add(waitingOrders.peek());
                        waitingOrders.poll();
                    }
//                    List<Order> orders = waitingOrders.stream().toList();
//
//                    int i = 0, pageLength;
//                    while (true) {
//                        pageLength = i;
//                        System.out.printf("Page %d:\n", i/8 + 1);
//                        for (; i < orders.length && i < 8; ++i)
//                            System.out.print((i + 1) + "-" + orders[i].toString());
//                        pageLength = i - pageLength;
//
//                        System.out.print("\nChoose an action:\nn: Next page\np: Previous page\n b: Go back\nnumber of the order to process\nChoice: ");
//                        inputStr = scan.nextLine();
//
//                        if (inputStr.equals("p")) {
//                            if (i > 7)
//                                i = i/8 - 1;
//                        }
//
//                        else if (isInteger(inputStr)) {
//                            inputInt = Integer.parseInt(inputStr);
//                            if (inputInt > i || inputInt < i - pageLength)
//                                System.out.print("Invalid Input\n");
//
//                            else {
//                                waitingOrders.peek().process();
//                                orderHistory.add(waitingOrders.peek());
//                                waitingOrders.poll();
//                            }
//                        }
//                    }
//
//                    System.out.print("\nDo you want to confirm the order?\n(Answer with yes or no)\n");
//                    inputStr = scan.next();
//                    if(inputStr.equals("yes")) {
//                        waitingOrders.peek().process();
//                        orderHistory.add(waitingOrders.peek());
//                        waitingOrders.poll();
//                    }
                }

                else {
                    System.out.print("There are no waiting orders.\n");
                }
            }

            else if (inputInt == 2 || inputInt == 3) {
                System.out.print("To Be Implemented\n");
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

    public void saveToFile() throws IOException {
        FileWriter file = new FileWriter(systemRef.resourcesDir + "Sellers/" + username + ".txt");
        file.write(username + "\n");

        for (ECommerceSystem.Product product : productList)
            file.write(product.getProductName() + " ");

        file.write("\n");

        for (Order order : waitingOrders)
            file.write(order + "|");

        file.write("\n");

        for (Order order : orderHistory)
            file.write(order + "|");

        file.close();
    }

    public void addOrder (Product product, int ID, int quantity, String customer, String phoneNum, String address) {
        waitingOrders.add(new Order(product, ID, quantity, customer, phoneNum, address));
    }
}
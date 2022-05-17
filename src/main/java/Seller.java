package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

import main.java.ECommerceSystem.*;

public class Seller extends User {
    private LinkedList<Order> orderHistory;
    private Queue<Order> waitingOrders;
    private LinkedList<Product> productList;

    private class Order {
        private final int ID;
        private Map<ECommerceSystem.Product, Integer> orderedProducts;

        private static int lastID = 0;

        public Order() {
            orderedProducts = new HashMap<>();
            ID = ++lastID;
        }

        public Order(String orderString) {
            orderedProducts = new HashMap<>();

            String[] temp = orderString.split(":");
            ID = Integer.parseInt(temp[0]);
            temp = temp[1].split(" ");

            String[] product_stock;
            for (String product : temp) {
                product_stock = product.split(",");
                orderedProducts.put(getProduct(product_stock[0], username),
                        Integer.parseInt(product_stock[1]));
            }

            lastID = ID;
        }

        public Order(Map<ECommerceSystem.Product, Integer> orderedProducts) {
            this.orderedProducts = new HashMap<>();
            this.orderedProducts.putAll(orderedProducts);
            ID = ++lastID;
        }

        public void add(ECommerceSystem.Product product, int quantity) {
            orderedProducts.put(product, quantity);
            product.setStock(product.getStock() - quantity);
        }

        public Integer remove(ECommerceSystem.Product product) {
            return orderedProducts.remove(product);
        }

        public List<ECommerceSystem.Product> process() {
            LinkedList<ECommerceSystem.Product> outOfStock = new LinkedList<>();

            BiConsumer<ECommerceSystem.Product, Integer> processor = new BiConsumer<ECommerceSystem.Product, Integer>() {
                @Override
                public void accept(ECommerceSystem.Product product, Integer numOfUnits) {
                    if (product.getStock() < numOfUnits)
                        outOfStock.addFirst(product);

                    else
                        product.setStock(product.getStock() - numOfUnits);
                }
            };

            orderedProducts.forEach(processor);

            if (outOfStock.isEmpty())
                return null;
            else
                return outOfStock;
        }

        @Override
        public String toString() {
            StringBuilder strb = new StringBuilder();
            strb.append(ID).append(":");

            for (Map.Entry<ECommerceSystem.Product, Integer> entry : orderedProducts.entrySet())
                strb.append(entry.getKey().getProductName())
                        .append(",")
                        .append(entry.getValue())
                        .append(" ");

            return strb.toString();
        }
    }

    public Seller(String username, String password, ECommerceSystem callerSystem)
            throws FileNotFoundException {
        super(username, password, callerSystem);

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
    public void UI() {
        int inputInt = 0;
        String inputStr = null;
        Scanner scan = new Scanner(System.in);
        System.out.print("Welcome to the seller menu\n");

        while(true){
            System.out.print("Enter the number of an action:\n1- Order management.\n2- Add a new product.\n3- Statistics.\n0- Log out.\n");

            try{
                inputInt = scan.nextInt();
                scan.nextLine();
                if (inputInt == 1){
                    if (!waitingOrders.isEmpty()) {
                        System.out.print("Oldest order:\n");
                        System.out.print(waitingOrders.peek());
                        System.out.print("\nDo you want to confirm the order?\n(Answer with yes or no)\n");
                        inputStr = scan.next();
                        if(inputStr.equals("yes")) {
                            waitingOrders.peek().process();
                            orderHistory.add(waitingOrders.peek());
                            waitingOrders.poll();
                        }
                    }
                    else System.out.print("There are no waiting orders.\n");
                }
                else if (inputInt == 2 || inputInt == 3) System.out.print("To Be Implemented\n");
                else if (inputInt == 0) {
                    saveToFile();
                    System.out.println("GOOD-BYE!");
                    break;
                }
                else System.out.println("Invalid choice, please try again.");
            }
            catch (InputMismatchException e){
                scan.nextLine();
                System.out.printf("Error: %s\n", e);
                e.printStackTrace();
            } catch (Exception e) {
                System.out.printf("Error: %s\n", e);
                e.printStackTrace();
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

    public void addOrder (Map<ECommerceSystem.Product, Integer> orderProducts) {
        waitingOrders.add(new Order(orderProducts));
    }
}
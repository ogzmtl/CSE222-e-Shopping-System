package main.java;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.*;

import main.DataStructures.SkipList;
import main.DataStructures.Trees.BinarySearchTree;
import main.java.ECommerceSystem.*;

public class Seller extends User {
    protected class Order {
        private String customer, address, phoneNum;
        private Product product;
        private int ID;
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

        public void accept() {
            updateOrders(ID, 1);
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
    }

    private LinkedList<Order> orderHistory;
    private Queue<Order> waitingOrders;
    private ArrayList<Product> productList;

    public Seller(String username, ECommerceSystem callerSystem)
            throws FileNotFoundException {
        super(username, callerSystem);

        orderHistory = new LinkedList<>();
        waitingOrders = new ArrayDeque<>();
        productList = new ArrayList<>();

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
                    productList.add(getProduct(productName, username));
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
                        orderHistory.add(order);
                    }
                }
            }
        }
    }

//    private String[] MaptoArray(Map<String, LinkedList<Product>> map) {
//        ArrayList<String> products = new ArrayList<>(map.size());
//        for (Map.Entry<String, LinkedList<Product>> temp : map.entrySet())
//            if (productAvailable(temp.getKey()) == null)
//                products.add(temp.getKey());
//
//        return products.toArray(new String[0]);
//    }

    @Override
    public void UI(){
        int inputInt = 0;
        String inputStr = null;
        Scanner scan = new Scanner(System.in);

        while (true) {
            System.out.print("\033[H\033[2JWelcome to the seller menu\nEnter the number of an action:\n1- Order management.\n2- Product Management.\n3- Statistics.\n0- Log out.\n\n");
            inputInt = getInputInt(scan, "Choice: ");

            if (inputInt == 1){
                //Order Management
                while (true) {
                    System.out.print("\033[H\033[2JOrder Management:\n0- Go back.\n1- Waiting Orders.\n2- Order History.\n\n");
                    inputInt = getInputInt(scan, "Choice: ");
                    if (inputInt == 1) {
                        if (!waitingOrders.isEmpty()) {
                            Object[] orders = waitingOrders.toArray();

                            int i = 0, pageLength;
                            boolean flag1 = true;
                            while (flag1) {
                                System.out.print("\033[H\033[2JWaiting Orders:\n");
                                pageLength = i;
                                System.out.printf("Page %d:\n", i / 8 + 1);
                                for (; i < orders.length && i < pageLength + 8; ++i) {
                                    Order order = (Order) orders[i];
                                    System.out.printf("%d- %06d: %s %d, %s\n", (i + 1),
                                            order.ID, order.product.getProductName(),
                                            order.quantity, order.customer);
                                }
                                pageLength = i - pageLength;

                                System.out.print("\nChoose an action:\n0: Go back\nn: Next page\np: Previous page\ne: Examine the fist order\n\n");
                                boolean flag2 = true;
                                while (flag2) {
                                    System.out.print("Choice: ");
                                    inputStr = scan.nextLine();

                                    if (inputStr.equals("p")) {
                                        if (i > 7) {
                                            i = (int) Math.pow((double) i, (double) (i / 8 - 1));
                                            flag2 = false;
                                        }

                                        else
                                            System.out.print("\033[2A\r\033[JThere are no previous pages.\n");
                                    }

                                    else if (inputStr.equals("n")) {
                                        if (i < orders.length - 1) {
                                            i = (int) Math.pow((double) i, (double) (i / 8 + 1));
                                            flag2 = false;
                                        }

                                        else
                                            System.out.print("\033[2A\r\033[JThere are no next pages.\n");
                                    }

                                    else if (inputStr.equals("0")) {
                                        flag1 = false;
                                        flag2 = false;
                                    }

                                    else if (inputStr.equals("e")) {
                                        Order head = waitingOrders.peek();
                                        System.out.printf("\nOrder %d:\nProduct: %s\nQuantity: %d\nCustomer: %s\nAddress: %s\nPhone Number: %s",
                                                head.ID, head.product.getProductName(), head.quantity, head.customer, head.address, head.phoneNum);
                                        System.out.print("\nDo you want to confirm the order? (Answer with yes or no)\n\n");
                                        while (true) {
                                            System.out.print("Choice: ");
                                            inputStr = scan.nextLine();
                                            if (inputStr.equals("yes")) {
                                                waitingOrders.peek().accept();
                                                orderHistory.add(waitingOrders.peek().ID, waitingOrders.poll());
                                                orders = waitingOrders.stream().toArray();
                                                flag2 = false;
                                                break;
                                            }

                                            else if (inputStr.equals("no")) {
                                                i -= pageLength;
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

                        else {
                            System.out.print("There are no waiting orders.\n(Tap Enter to go back)");
                            scan.nextLine();
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
                    System.out.print("\033[H\033[2JProduct Management:\n");
                    System.out.print("\nChoose an action:\n0- Go back.\n1- View your products.\n2- Add a new product from the pool.\n3- Send a new product request to the system.\n\n");
                    inputInt = getInputInt(scan, "Choice: ");

                    if (inputInt == 0) {
                        break;
                    }

                    if (inputInt == 1){
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

                    if (inputInt == 2){
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

                                System.out.print("Choose an action:\n0: Go back\nn: Next page\np: Previous page\na number: Examine the product with the number\n\n");
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
                                                    } else if (inputInt == 1) {
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
                }
            }

            else if(inputInt == 3) {
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

        for (Product product : productList)
            file.write(product.getProductName() + " ");

        file.write("\n");

        for (Order order : waitingOrders)
            file.write(order + "|");

        file.write("\n");

        for (Order order : orderHistory)
            file.write(order + "|");

        file.close();
    }

    public Product productAvailable(String productName) {
        for (Product temp : productList)
            if (temp.getProductName().equals(productName))
                return temp;

        return null;
    }

    public void addOrder (Product product, int ID, int quantity, String customer, String phoneNum, String address) {
        waitingOrders.add(new Order(product, ID, quantity, customer, phoneNum, address));
    }
}
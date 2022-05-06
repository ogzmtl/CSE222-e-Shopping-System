package MainSystem;

import MainSystem.ECommerceSystem.Product;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;

public class Seller extends User {
    private class Order {
        private final int ID;
        private Map<Product, Integer> orderedProducts;

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
        }

        public void add(Product product, int quantity) {
            orderedProducts.put(product, quantity);
            product.setStock(product.getStock() - quantity);
        }

        public Integer remove(Product product){
            return orderedProducts.remove(product);
        }

        public List<Product> process() {
            List<Product> outOfStock = new LinkedList<>();

            BiConsumer<Product, Integer> processor = new BiConsumer<Product, Integer>() {
                @Override
                public void accept(Product product, Integer numOfUnits) {
                    if(product.getStock() < numOfUnits)
                        outOfStock.add(product);

                    else
                        product.setStock(product.getStock() - numOfUnits);
                }
            };

            orderedProducts.forEach(processor);

            if(outOfStock.isEmpty())
                return null;
            else
                return outOfStock;
        }

        @Override
        public String toString() {
            StringBuilder strb = new StringBuilder();
            strb.append(ID).append(":");

            for (Map.Entry<Product, Integer> entry : orderedProducts.entrySet())
                strb.append(entry.getKey().getProductName())
                    .append(",")
                    .append(entry.getValue())
                    .append(" ");

            return strb.toString();
        }
    }

    private LinkedList<Order> orderHistory;
    private Queue<Order> waitingOrders;
    private LinkedList<Product> productList;

	/*
		yeni ürün eklenmesi için talep oluştur
		sattığı ürünlerin listesi
		sipariş verilenler
		ürün istatistiği --> her üründen kaç tane sattığı --> inner class olabilir

		UI implementasyonu
	*/

    public Seller(String username, String password)
            throws FileNotFoundException {
        super(username, password);

        orderHistory = new LinkedList<>();
        waitingOrders = new LinkedList<>();
        productList = new LinkedList<>();

        File file = new File(username + ".txt");
        if(file.exists()){
            Scanner reader = new Scanner(file);
            String buffer = reader.nextLine();
            Product targetProduct;

            // Check that the file is not corrupted
            // Each file must start with the name of the seller
            if(!buffer.contains(username)){
                file.renameTo(new File(username + "_damaged.txt"));
                throw new FileNotFoundException("The file found was damaged");
            }

            // The list of products
            buffer = reader.nextLine();
            String[] products = buffer.split(" ");
            for(String productName : products)
                productList.add(getProduct(productName, username));

            // The list of waiting orders
            buffer = reader.nextLine();
            String[] orders = buffer.split("\\|");
            for (String orderString : orders)
                waitingOrders.add(new Order(orderString));

            // The list of past orders
            buffer = reader.nextLine();
            orders = buffer.split("\\|");
            for (String orderString : orders)
                waitingOrders.add(new Order(orderString));
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

            inputInt = scan.nextInt();
            switch (inputInt) {
                case 1: if (!waitingOrders.isEmpty()) {
                    System.out.print("Oldest order:\n");
                    System.out.print(waitingOrders.peek());
                    System.out.print("Do you want to confirm the order?\n(Answer with yes or no)\n");
                    inputStr = scan.next();
                    if(inputStr.equals("yes"))
                        waitingOrders.peek().process();
                }
                else
                    System.out.print("There are no waiting orders.\n");

                    break;

                case 2: case 3:
                    System.out.print("To Be Implemented\n");
                    break;

                case 0: break;
            }
        }
    }

    public void saveToFile() throws IOException {
        FileWriter file = new FileWriter(username + ".txt", false);
        file.write(username + "\n");

        for (Product product : productList)
            file.write(product.getProductName());

        for (Order order : waitingOrders)
            file.write(order + "|");

        file.write("\n");

        for (Order order : orderHistory)
            file.write(order + "|");

        file.write("\n");

        file.close();
    }
}
package MainSystem;

import MainSystem.ECommerceSystem.Product;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Seller extends User {
//    private static class Product {
//        private String name;
//        private float price;
//        private int stock;
//        private boolean published;
//
//        public Product(String name) {
//            this.name = name;
//            price = 0;
//            stock = 0;
//            published = false;
//        }
//
//        public Product(String name, int stock) {
//            this.name = name;
//            price = 0;
//            stock = stock;
//            published = false;
//        }
//
//        public Product(String name, int stock, float price, boolean published) {
//            this.name = name;
//            price = price;
//            stock = stock;
//            published = published;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public void setPrice(float price) {
//            this.price = price;
//        }
//
//        public void setPublished(boolean published) {
//            this.published = published;
//        }
//
//        public void setStock(int stock) {
//            this.stock = stock;
//        }
//
//        public float getPrice() {
//            return price;
//        }
//
//        public int getStock() {
//            return stock;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public boolean isPublished() {
//            return published;
//        }
//    }

    private static class Order {
        private Map<Product, Integer> productList;

        public Order() {

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

            if(!buffer.contains(username)){
                file.renameTo(new File(username + "_damaged.txt"));
                throw new FileNotFoundException("The file found was damaged");
            }

            buffer = reader.nextLine();
            for(String word : buffer.split(" ")) {
                targetProduct =
            }
        }
    }

    private int intInput(InputStream stream) throws Exception {
        Scanner scanner = new Scanner(stream);
        int buffer;

        while(scanner.hasNext()){
            try {
                buffer = scanner.nextInt();
            } catch (InputMismatchException e) {
                continue;
            }

            return buffer;
        }

        throw new Exception("No input was detected\n");
    }

    @Override
    public void UI() {
        int input = 0;
        System.out.printf("Welcome to the seller menu\n");

        while(true){
            System.out.printf("Enter the number of an action:\n1- Order management.\n2- Add a new product.\n3- Statistics.\n0- Log out.\n");
            try{
                input = intInput(System.in);
            } catch (Exception e) {
                System.out.println("Error during getting input.");
                e.printStackTrace();
            }

            switch (input) {
                case 1: System.out.print("Last 10 orders:\n");
                        Iterator<Order> iterator = waitingOrders.iterator();
                        for(int i = 0; i < 10; ++i)
                            System.out.print(iterator.next());

                case 0: break;
            }
        }
    }
}
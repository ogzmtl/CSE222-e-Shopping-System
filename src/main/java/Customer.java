package main.java;

import main.DataStructures.Graph.*;
import main.DataStructures.Trees.BinarySearchTree;
import main.java.ECommerceSystem.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.InputMismatchException;

@SuppressWarnings("unchecked")
public class Customer extends ECommerceSystem.User {
    private LinkedList<Pair<Product, Integer>> basket = new LinkedList();
    private LinkedList<Pair<Pair<Product, Integer>, Pair<Integer, Integer>>> formerOrders = new LinkedList();
    private LinkedList<Pair<Pair<Product, Integer>, Pair<Integer, Integer>>> orders = new LinkedList();
    private MatrixGraph graph= new MatrixGraph(getID(), true);
    private double wallet = 0.0;

    public Customer(String usernameValue, ECommerceSystem callerSystem) {
        super(usernameValue, callerSystem);
        new File(systemRef.resourcesDir + "Customers").mkdir();

        try{
            File file = new File(systemRef.resourcesDir + "Customers/" + username + ".txt");
            if (file.exists()) {
                Scanner scan = new Scanner(file);

                wallet = Double.parseDouble(scan.nextLine());

                int basketSize = Integer.parseInt(scan.nextLine());
                for (int i = 0; i < basketSize; ++i){
                    String product = scan.next();
                    String seller = scan.next();
                    Double price = Double.parseDouble(scan.next());
                    int stock = Integer.parseInt(scan.next());
                    int amount = Integer.parseInt(scan.next());
                    basket.add(new Pair<Product, Integer>(new Product(product, seller, price, stock), amount));
                }


                int formerOrdersSize = Integer.parseInt(scan.next());
                for (int i = 0; i < formerOrdersSize; ++i){
                    String product = scan.next();
                    String seller = scan.next();
                    Double price = Double.parseDouble(scan.next());
                    int stock = Integer.parseInt(scan.next());
                    int amount = Integer.parseInt(scan.next());
                    int id = Integer.parseInt(scan.next());

                    HashMap<Integer, Integer> orderSituations = getOrders();
                    int situation = orderSituations.get(id);

                    if (situation != 0){
                        formerOrders.add(new Pair<Pair<Product, Integer>, Pair<Integer, Integer>>(new Pair<Product, Integer>(new Product(product, seller, price, stock), amount), new Pair<Integer, Integer>(id, situation)));
                    }
                    else
                        orders.add(new Pair<Pair<Product, Integer>, Pair<Integer, Integer>>(new Pair<Product, Integer>(new Product(product, seller, price, stock), amount), new Pair<Integer, Integer>(id, situation)));
                }

                int ordersSize = Integer.parseInt(scan.next());
                for (int i = 0; i < ordersSize; ++i){
                    String product = scan.next();
                    String seller = scan.next();
                    Double price = Double.parseDouble(scan.next());
                    int stock = Integer.parseInt(scan.next());
                    int amount = Integer.parseInt(scan.next());
                    int id = Integer.parseInt(scan.next());

                    HashMap<Integer, Integer> orderSituations = getOrders();
                    int situation = orderSituations.get(id);

                    if (situation != 0){
                        formerOrders.add(new Pair<Pair<Product, Integer>, Pair<Integer, Integer>>(new Pair<Product, Integer>(new Product(product, seller, price, stock), amount), new Pair<Integer, Integer>(id, situation)));
                        if (situation == -1) wallet += amount * price;
                    }
                    else
                        orders.add(new Pair<Pair<Product, Integer>, Pair<Integer, Integer>>(new Pair<Product, Integer>(new Product(product, seller, price, stock), amount), new Pair<Integer, Integer>(id, situation)));

                }
            }
        } catch (Exception e) {
            System.out.println("Error during opening the file.");
            e.printStackTrace();
        }

        for (int i = 0; i < getID() && i < orders.size(); ++i){
            Pair<Pair<Product, Integer>, Pair<Integer, Integer>> newIndexElement = orders.get(i);
            if (newIndexElement != null && newIndexElement.getValue().getValue() != -1)
                graph.insert(new Edge(newIndexElement.getValue().getKey(), i));
        }
    }

    private void exit(){
        try{
            FileWriter writer = new FileWriter(systemRef.resourcesDir + "Customers/" + username + ".txt");

            writer.write(wallet + "\n");

            writer.write(basket.size() + "\n");
            for (Pair<Product, Integer> p : basket) 
                writer.write(p.getKey().getProductName() + " " + p.getKey() + " " + p.getValue() + "\n");

            writer.write(formerOrders.size() + "\n");
            for (Pair<Pair<Product, Integer>, Pair<Integer, Integer>> p : formerOrders)
                writer.write(p.getKey().getKey().getProductName() + " " + p.getKey().getKey() + " " + p.getKey().getValue() + " " + p.getValue().getKey() + "\n");
            
            writer.write(orders.size() + "\n");
            for (Pair<Pair<Product, Integer>, Pair<Integer, Integer>> p : orders)
                writer.write(p.getKey().getKey().getProductName() + " " + p.getKey().getKey() + " " + p.getKey().getValue() + " " + p.getValue().getKey() + "\n");

            writer.close();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private class Pair<K, V>{
        public K key;
        public V value;

        public Pair(){
            key = null;
            value = null;
        }

        public Pair(K key, V value){
            this.key = key;
            this.value = value;
        }

        public K getKey(){
            return key;
        }

        public V getValue(){
            return value;
        }
    }

    @Override
    public void UI() {
        Map<String, LinkedList<Product>> products = getProductsMap();
        ArrayList<BinarySearchTree<Product>> productsOrdered = getProducts();
        int choice;
        boolean flag = true;
        Scanner scan = new Scanner(System.in);

        System.out.printf("\nWelcome dear %s!", username);

        do {
            try {
                System.out.println("\nWhat do you want to do?");
                System.out.println("0 - Exit\n1 - Change your password\n2 - Display products\n3 - Choose a product and see sellers\n4 - Add product to basket\n5 - See the basket\n6 - Add money to your account\n7 - See your money\n8 - Order\n9 - See previous orders\n10 - Check your order status");
                System.out.printf("Choice: ");
                choice = scan.nextInt();
                scan.nextLine();
                if (choice == 0) {
                    System.out.printf("Goodbye %s!\n", username);
                    flag = false;
                    exit();
                } else if (choice == 1) {
                    System.out.printf("Enter your new password: ");
                    String newPassword = scan.nextLine();
                    newPassword.trim();
                    changePass(newPassword);
                    System.out.println("Your password has been changed successfully!");
                } else if (choice == 2) {
                    displayProduct();
                } else if (choice == 3) {
                    String choiceProduct;
                    System.out.printf("Please enter the product's name: ");
                    choiceProduct = scan.nextLine();
                    if (!products.containsKey(choiceProduct)){
                        System.out.println("This product does not exist.");    
                    }
                    else if (products.get(choiceProduct).size() == 0){
                        System.out.println("There is no seller for this product.");
                    }
                    else{
                        int index = 0;
                        for (int i = 0; i < productsOrdered.size(); ++i){
                            if (productsOrdered.get(i).getData() != null && productsOrdered.get(i).getData().getProductName().equals(choiceProduct)) index = i;
                        }
                        boolean inFlag = true;
                        while (inFlag){
                            inFlag = false;
                            System.out.printf("Which do you want -> Sort the prices in ascending order (1) | in descending order (2): ");
                            choiceProduct = scan.nextLine();

                            if (choiceProduct.equals("1")){
                                ascendingTraversal(productsOrdered.get(index));
                            }
                            else if (choiceProduct.equals("2")){
                                descendingTraversal(productsOrdered.get(index));
                            }
                            else{
                                inFlag = true;
                                System.out.println("Please choose 1 or 2. Try again.");
                            }
                        }
                    }
                } else if (choice == 4) {
                    Product toOrder = null;
                    String choiceProduct;
                    System.out.printf("Please enter the product's name: ");
                    choiceProduct = scan.nextLine();

                    if (!products.containsKey(choiceProduct)){
                        System.out.println("This product does not exist.");    
                        continue;
                    }
                    if (products.get(choiceProduct).size() == 0){
                        System.out.println("There is no seller for this product.");
                        continue;
                    }

                    String sellerName;
                    System.out.printf("Please enter the seller's name: ");
                    sellerName = scan.nextLine();
                    boolean sellerFlag = false;
                    for (Product p : products.get(choiceProduct)){
                        if (p.getSellerName().equals(sellerName)) {sellerFlag = true; toOrder = p; break;}
                    }
                    if (!sellerFlag){ System.out.println("This seller does not exist for this product."); continue;}
                    if (toOrder.getStock() < 1) {System.out.println("There is no stock for this product."); continue;}

                    boolean basketFlag = true;
                    do{
                        try{
                            int numOfProducts;
                            System.out.printf("How many products do you want to add to your basket?: ");
                            numOfProducts = scan.nextInt(); scan.nextLine();
                            if (numOfProducts < 1){
                                System.out.println("Invalid number, try again.");
                            }
                            else if (numOfProducts > toOrder.getStock()){
                                System.out.println("There is no enough stock, try again.");
                            }
                            else{
                                basket.add(new Pair(toOrder, numOfProducts));
                                basketFlag = false;
                            }
                        }
                        catch (InputMismatchException e){
                            scan.nextLine();
                            System.out.printf("Error: %s\n", e);
                            e.printStackTrace();
                        }
                        catch(Exception e){
                            System.out.printf("Error: %s\n", e);
                            e.printStackTrace();
                        }
                    }while(basketFlag);
                    
                    System.out.println("Product(s) added successfully to your basket");

                } else if (choice == 5){
                    if (basket.size() == 0) System.out.println("Basket is empty.");
                    else{
                        for (Pair<Product, Integer> p : basket){
                            System.out.println("Product: " + p.getKey().getProductName() + " | Seller: " + p.getKey().getSellerName() + " | Price: " + p.getKey().getPrice() + " | Number of products: " + p.getValue());
                        }
                    }
                    
                } else if (choice == 6){
                    boolean walletFlag = true;
                    while (walletFlag){
                        try{
                            System.out.printf("Enter amount (you can add $100.00 at most at a time): $");
                            double money = scan.nextDouble();
                            if (money > 100.0) System.out.println("You have exceeded the limit, try again.");
                            else if (money < 0.0) System.out.println("Invalid value, try again.");
                            else {
                                wallet += money;
                                System.out.println("Your wallet has been updated successfully."); 
                                walletFlag = false;
                            }
                        }
                        catch (InputMismatchException e) {
                            scan.nextLine();
                            System.out.printf("Error: %s\n", e);
                            e.printStackTrace();
                        } catch (Exception e) {
                            System.out.printf("Error: %s\n", e);
                            e.printStackTrace();
                        }
                    }
                } else if (choice == 7){
                    System.out.println("Wallet: $" + wallet);
                } else if (choice == 8){
                    if (basket.isEmpty()){System.out.println("Your basket is empty."); continue;}
                    System.out.printf("Please enter the address: ");
                    String addr = scan.nextLine();

                    Long phone = 0L;
                    boolean phoneFlag = true;
                    do{
                        try{
                            System.out.printf("Please enter the phone number: ");
                            phone = scan.nextLong(); scan.nextLine();
                            phoneFlag = false;
                        }
                        catch (InputMismatchException e) {
                            scan.nextLine();
                            System.out.printf("Error: %s\n", e);
                            e.printStackTrace();
                        } catch (Exception e) {
                            System.out.printf("Error: %s\n", e);
                            e.printStackTrace();
                        }
                    }while(phoneFlag);

                    double sum = 0;
                    for (Pair<Product, Integer> p : basket) sum += p.getKey().getPrice()*p.getValue();
                    if (sum > wallet){ System.out.println("There is no enough money at your wallet."); continue; }
                    wallet -= sum;
                    for (Pair<Product, Integer> p : basket){
                        orders.add(new Pair<Pair<Product, Integer>, Pair<Integer, Integer>>(p, new Pair<Integer, Integer>(getID(), 0)));
                        Seller seller = new Seller(p.getKey().getSellerName(), systemRef);
                        seller.addOrder(p.getKey(), getID(), p.getValue(), username, phone.toString(), addr);
                        seller.saveToFile();
                        updateOrders(getID(), 0);
                        incrementID();
                    }
                    basket.clear();
                } else if (choice == 9){
                    if (formerOrders.size() == 0) System.out.println("There is no former order.");
                    else{
                        for (Pair<Pair<Product, Integer>, Pair<Integer, Integer>> p : formerOrders) 
                            System.out.println("Product: " + p.getKey().getKey().getProductName() + " - Seller: " + p.getKey().getKey().getSellerName() + " - Price: " + p.getKey().getKey().getPrice() + " - Amount: " + p.getKey().getValue());
                    }
                } else if (choice == 10){
                    if (formerOrders.size() == 0 && orders.size() == 0) System.out.println("There is no orders yet.");
                    else{
                        for (Pair<Pair<Product, Integer>, Pair<Integer, Integer>> p : formerOrders){
                            System.out.printf("Product: " + p.getKey().getKey().getProductName() + " - Seller: " + p.getKey().getKey().getSellerName() + " - Price: " + p.getKey().getKey().getPrice() + " - Amount: " + p.getKey().getValue());
                            if (p.getValue().getValue() == -1) System.out.println(" - Status: CANCELLED");
                            else if (p.getValue().getValue() == 1) System.out.println(" - Status: FINISHED");
                        }
                        for (Pair<Pair<Product, Integer>, Pair<Integer, Integer>> p : orders){
                            System.out.println("Product: " + p.getKey().getKey().getProductName() + " - Seller: " + p.getKey().getKey().getSellerName() + " - Price: " + p.getKey().getKey().getPrice() + " - Amount: " + p.getKey().getValue() + " - Status: WAITING");
                        }
                    }
                } else System.out.println("Invalid choice, please try again.");
            } catch (InputMismatchException e) {
                scan.nextLine();
                System.out.printf("Error: %s\n", e);
                e.printStackTrace();
            } catch (Exception e) {
                System.out.printf("Error: %s\n", e);
                e.printStackTrace();
            }
        } while (flag);
    }

    private void ascendingTraversal(BinarySearchTree<Product> bst){
        if (bst == null) return;
        else{
            ascendingTraversal(bst.getLeftSubtree());
            if (bst.getData()!=null)
                System.out.println("Seller: " + bst.getData().getSellerName() + " - Price: " + bst.getData().getPrice() + " - Stock: " + bst.getData().getStock());
            ascendingTraversal(bst.getRightSubtree());
        }
    }

    private void descendingTraversal(BinarySearchTree<Product> bst){
        if (bst == null) return;
        else{
            descendingTraversal(bst.getRightSubtree());
            if (bst.getData()!=null)
                System.out.println("Seller: " + bst.getData().getSellerName() + " - Price: " + bst.getData().getPrice() + " - Stock: " + bst.getData().getStock());
            descendingTraversal(bst.getLeftSubtree());
        }
    }

    private void displayProduct() {
        Map<String, LinkedList<Product>> products = getProductsMap();
        ArrayList<String> array = new ArrayList(products.size());
        int i = 0;

        for (Map.Entry<String, LinkedList<Product>> entry : products.entrySet()) array.add(entry.getKey());
        quickSort(array);
        for (int j = 0; j < array.size(); ++j) System.out.println(array.get(j));
    }

    /**
     * This method sorts the array with quick sort algorithm.
     * @param array Array to be sorted.
     */
    public static void quickSort(ArrayList<String> array) {
        recursiveQuickSort(array, 0, array.size()-1);
    }

    /**
     * Helper recursive method for quick sort.
     * @param array Array to be sorted.
     * @param first Starting index.
     * @param last Ending index.
     */
    private static void recursiveQuickSort(ArrayList<String> array, int first, int last) {
        if (last <= first) return;
        int index = partition(array, first, last);
        recursiveQuickSort(array, first, index-1);
        recursiveQuickSort(array, index+1, last);
    }

    /**
     * Partition method for quick sort.
     * @param array Array to be sorted.
     * @param first Starting index.
     * @param last Ending index.
     * @return Returns index of the pivot value.
     */
    private static int partition(ArrayList<String> array, int first, int last) {
        String prior = array.get(first);
        int up = first, down = last;
        do {
            while (prior.compareTo(array.get(down)) < 0) --down;
            while ((up < last) && (prior.compareTo(array.get(down)) >= 0)) ++up;
            if (up < down) swap(array,up,down);
        }while (down > up); 
        swap(array,first,down);
        return down;
    }

    private static void swap(ArrayList<String> array, int a, int b){
        String temp = array.get(a);
        array.set(a, array.get(b));
        array.set(b, temp);
    }
}
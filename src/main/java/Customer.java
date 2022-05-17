package main.java;

import main.DataStructures.Trees.BinarySearchTree;
import main.java.ECommerceSystem.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

public class Customer extends ECommerceSystem.User {

    public Customer(String usernameValue, String passwordValue, ECommerceSystem callerSystem) {
        super(usernameValue, passwordValue, callerSystem);
    }

    protected class Wallet {
        private double balance;

        public Wallet(double balance) {
            this.balance = balance;
        }

        public void increase(double paymentAmount) {
            setBalance(this.balance + paymentAmount);
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public double getBalance() {
            return balance;
        }

        public void decrease(double paymentAmount) throws Exception {
            if (this.balance - paymentAmount < 0)
                throw new Exception();
            else
                setBalance(this.balance - paymentAmount);
        }
    }

    @Override
    public void UI() {
        ArrayList<BinarySearchTree<Product>> productsOrdered = getProducts();
        Scanner inp = new Scanner(System.in);
        int input = 1;
        String productName;
        while (input != 0) {
            System.out.printf("0.Exit\n1.Display Products\n");
            input = inp.nextInt();

            if (input == 1) {
                for (BinarySearchTree<Product> temp : productsOrdered)
                    if(temp.getData() != null)
                        System.out.println(temp.getData().getProductName());

                System.out.println("Enter product name :");
                Scanner input1 = new Scanner(System.in);
                productName = input1.nextLine();

                System.out.printf("1.Display by product name\n2.Display by price\n");
                input = inp.nextInt();

                if (input == 1 || input == 2)
                    displayProduct(productName, input);
                else
                    try {
                        throw new Exception();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            } else if (input == 2) {
                System.out.println("In Progress");
                //displayBalance()
            }
            else if(input == 0){
                System.out.println("GoodBye!!!");
            }
            else {
                try {
                    throw new Exception();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void displayProduct(String productName, int flag) {
        ArrayList<BinarySearchTree<Product>> productsOrdered = getProducts();
        LinkedList<ECommerceSystem.Product> temp = new LinkedList<>();
        Iterator<ECommerceSystem.Product> iterator = temp.iterator();

        temp = getProduct(productName);

        System.out.printf("Product : " + productName + "\n");
        System.out.println("~~~~~");

        if (flag == 1) { // display by product name
            for (ECommerceSystem.Product iter : temp) {
                System.out.printf("Seller :" + iter.getSellerName() + "\n");
                System.out.printf("Price :" + iter.getPrice() + "\n");
                System.out.println("~~~~~");
            }
        } else if (flag == 2) { // display product by price ascending
            System.out.println("In progress");
            for (BinarySearchTree<ECommerceSystem.Product> temp2 : productsOrdered)
                if (temp2.getData() != null &&
                        temp2.getData().getProductName().equals(productName))
                    System.out.println(temp2);
        }
    }
}

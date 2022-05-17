package main.java;

import java.net.URI;
import java.net.URL;
import java.util.*;
import java.io.*;
import java.lang.System;

import sourcepackage.*;

@SuppressWarnings("unchecked")
public class ECommerceSystem {
	protected final String resourcesDir = "./src/main/resources/";
	private Map<String, String> Sellers = new HashMap<>();
	private Map<String, String> Customers = new HashMap<>();
	private Map<String, String> Admins = new HashMap<>();
	private Map<String, LinkedList<Product>> products = new TreeMap();
	private PriorityQueue<Request> Requests = new PriorityQueue();
	private ArrayList<BinarySearchTree<Product>> productsOrdered = new ArrayList();

	public static abstract class User {
		//protected String userID;
		protected ECommerceSystem systemRef;
		protected String username;
		protected String password;

		public User(String usernameValue, String passwordValue, ECommerceSystem callerSystem){
			username = usernameValue;
			password = passwordValue;
			systemRef = callerSystem;
		}

		public abstract void UI();

		protected Product getProduct(String productName, String seller) {
			LinkedList<Product> targetList = systemRef.products.get(productName);
			if(targetList != null)
				for(Product temp : targetList)
					if(temp.sellerName.equals(seller)) return temp;

			return null;
		}

		protected LinkedList<Product> getProduct(String productName) {
			LinkedList<Product> targetList = systemRef.products.get(productName);
			return targetList;
		}

		protected ArrayList<BinarySearchTree<Product>> getProducts() {
			return systemRef.productsOrdered;
		}

		protected void changePass (String newPass) {
			password = newPass;
		}
	}

	private void createBST() {
		productsOrdered = new ArrayList();
		for (Map.Entry<String, LinkedList<Product>> entry : products.entrySet()) {
			productsOrdered.add(new BinarySearchTree());
			LinkedList<Product> temp = entry.getValue();
			Iterator<Product> iter = temp.iterator();
			while (iter.hasNext()) productsOrdered.get(productsOrdered.size() - 1).add(iter.next().clone());
		}
	}

	protected static class Product implements Comparable<Product>, Cloneable {
		private final String productName;
		private final String sellerName;
		private double price;
		private int stock;

		public Product(String productName, String sellerName, double price, int stock) {
			this.productName = productName;
			this.sellerName = sellerName;
			this.price = price;
			this.stock = stock;
		}

		public String toString(){
			StringBuilder strb = new StringBuilder();
			strb.append(sellerName).append(" ")
					.append(price).append(" ")
					.append(stock);

			return strb.toString();
		}

		@Override
		public int compareTo(Product o){
			if (o.price < price) return 1;
			if (o.price == price) return 0;
			else return -1;
		}

		public String getProductName() {
			return productName;
		}

		public int getStock() {
			return stock;
		}

		public void setStock(int stock) {
			this.stock = stock;
		}

		public String getSellerName() {
			return sellerName;
		}

		public double getPrice() {
			return price;
		}

		public Product clone() {
			try {
				Product copy = (Product) super.clone();
				//gives me the Product object which is shallow copied
				return copy;
			} catch (CloneNotSupportedException e) {
				//this will never happen
				return null;
			}
		}
	}

	private class Admin extends User {
		public Admin(String usernameValue, String passwordValue, ECommerceSystem callerSystem) {
			super(usernameValue, passwordValue, callerSystem);
		}

		public void UI() {
			int choice;
			boolean flag = true;
			Scanner scan = new Scanner(System.in);

			System.out.printf("\nWelcome dear %s!", username);

			do {
				try {
					System.out.println("\nWhat do you want to do?");
					System.out.println("0 - Exit\n1 - Check requests\n2 - Remove user\n3 - Add product\n4 - Remove product");
					System.out.printf("Choice: ");
					choice = scan.nextInt();
					scan.nextLine();

					if (choice == 0) {
						System.out.printf("Goodbye %s!\n", username);
						flag = false;
					} else if (choice == 1) {
						if (Requests.size() == 0) System.out.println("There is no pending request.");
						else {
							boolean acceptFlag = true;
							do {
								System.out.printf("Next pending request --> %s\n", Requests.peek());
								System.out.printf("Do you want to accept it? (y: yes, n: no) --> ");
								String acceptance = scan.nextLine();
								if (acceptance.equals("y")) {
									if (Requests.peek().priority == 0)
										Sellers.put(((SellerRequest) Requests.peek()).user.username, ((SellerRequest) Requests.poll()).user.password);
									else addProduct(((ProductRequest) Requests.poll()).productName);
									acceptFlag = false;
								} else if (acceptance.equals("n")) {
									Requests.poll();
									acceptFlag = false;
								} else System.out.println("Invalid choice, please try again.");
							} while (acceptFlag);
						}
					} else if (choice == 2) {
						System.out.printf("Enter the type of the user you want to remove (s: seller, c: customer): ");
						String remove = scan.nextLine();
						if (remove.equals("s")) {
							System.out.printf("Enter the username you want to remove: ");
							remove = scan.nextLine();
							remove.trim();
							if (Sellers.containsKey(remove)) Sellers.remove(remove);
							else System.out.println("There is no such user.");
						} else if (remove.equals("c")) {
							System.out.printf("Enter the username you want to remove: ");
							remove = scan.nextLine();
							remove.trim();
							if (Customers.containsKey(remove)) Customers.remove(remove);
							else System.out.println("There is no such user.");
						} else System.out.println("Invalid choice.");
					} else if (choice == 3) {
						System.out.printf("Enter the name of the product: ");
						String productName = scan.nextLine();
						productName.trim();
						addProduct(productName);
					} else if (choice == 4) {
						System.out.printf("Enter the name of the product: ");
						String productName = scan.nextLine();
						productName.trim();
						removeProduct(productName);
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
	}

	private abstract static class Request implements Comparable<Request> {
		protected int priority;

		public int compareTo(Request o) {
			return priority - o.priority;
		}
	}

	protected static class SellerRequest extends Request {
		private User user;

		public SellerRequest(User user) {
			this.user = user;
			priority = 0;
		}

		public String toString() {
			return "Seller request: " + user.username;
		}
	}

	protected static class ProductRequest extends Request {
		private String productName;

		public ProductRequest(String name) {
			productName = name;
			priority = 1;
		}

		public String toString() {
			return "Product request: " + productName;
		}
	}

	public ECommerceSystem() {
		//READING FROM FILES
		try {
			File file = new File(resourcesDir + "Products.txt");
			file.createNewFile();
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()) {
				String temp = reader.nextLine();
				String[] lineWords = temp.trim().split("\\s+");
				products.put(lineWords[0], new LinkedList());
				for (int i = 1; i < lineWords.length; i += 3)
					products.get(lineWords[0]).add(new Product(lineWords[0], lineWords[i], Double.parseDouble(lineWords[i + 1]), Integer.parseInt(lineWords[i + 2])));
			}
			reader.close();

			file = new File(resourcesDir + "Sellers.txt");
			file.createNewFile();
			reader = new Scanner(file);
			while (reader.hasNext()) Sellers.put(reader.next(), reader.next());

			file = new File( resourcesDir + "Customers.txt");
			file.createNewFile();
			reader = new Scanner(file);
			while (reader.hasNext()) Customers.put(reader.next(), reader.next());

			file = new File(resourcesDir + "Admins.txt");
			file.createNewFile();
			reader = new Scanner(file);
			while (reader.hasNext()) Admins.put(reader.next(), reader.next());

			file = new File(resourcesDir + "Requests.txt");
			file.createNewFile();
			reader = new Scanner(file);
			while (reader.hasNext()) {
				int priority = Integer.parseInt(reader.next());
				if (priority == 0) Requests.add(new SellerRequest(new Seller(reader.next(), reader.next(), this)));
				else Requests.add(new ProductRequest(reader.next()));
			}

			createBST();
		} catch (Exception e) {
			System.out.println("Error during opening the file.");
			e.printStackTrace();
		}
	}

	public void menu() {
		boolean flag = true;
		System.out.println("~~~~ Welcome to E-Commerce System ~~~~\n--------------------------------------");
		int choice;
		Scanner scan = new Scanner(System.in);
		do {
			try {
				System.out.println("\nMENU");
				System.out.println("0 - Exit\n1 - Log in\n2 - Sign up");
				System.out.printf("Choice : ");
				choice = scan.nextInt();
				scan.nextLine();

				if (choice == 0) {
					System.out.println("\nGOOD-BYE!");
					exit();
					flag = false;
				} else if (choice == 1) {
					boolean logInFlag = true;
					do {
						try {
							System.out.println("\n0 - Back\n1 - Log in as admin\n2 - Log in as customer\n3 - Log in as seller");
							System.out.printf("Choice : ");
							choice = scan.nextInt();
							scan.nextLine();

							if (choice == 0) logInFlag = false;
							else if (choice == 1) {
								System.out.printf("\nUsername: ");
								String usernameValue = scan.nextLine();
								System.out.printf("Password: ");
								String passwordValue = scan.nextLine();
								usernameValue.trim();
								passwordValue.trim();

								if (Admins.containsKey(usernameValue) && Admins.get(usernameValue).equals(passwordValue)) {
									Admin newAdmin = new Admin(usernameValue, passwordValue, this);
									newAdmin.UI();
									logInFlag = false;
								} else System.out.println("Invalid username or password!");
							} else if (choice == 2) {
								System.out.printf("\nUsername: ");
								String usernameValue = scan.nextLine();
								System.out.printf("Password: ");
								String passwordValue = scan.nextLine();
								usernameValue.trim();
								passwordValue.trim();

								if (Customers.containsKey(usernameValue) && Customers.get(usernameValue).equals(passwordValue)) {
									Customer newCustomer = new Customer(usernameValue, passwordValue, this);
									newCustomer.UI();
									//saveRequests();
									logInFlag = false;
								} else System.out.println("Invalid username or password!");
							} else if (choice == 3) {
								System.out.printf("\nUsername: ");
								String usernameValue = scan.nextLine();
								System.out.printf("Password: ");
								String passwordValue = scan.nextLine();
								usernameValue.trim();
								passwordValue.trim();

								if (Sellers.containsKey(usernameValue) && Sellers.get(usernameValue).equals(passwordValue)) {
									Seller newSeller = new Seller(usernameValue, passwordValue, this);
									newSeller.UI();
									saveRequests();
									logInFlag = false;
								} else System.out.println("Invalid username or password!");
							} else System.out.println("Invalid choice, please try again.");
						} catch (Exception e) {
							scan.nextLine();
							System.out.printf("Error: %s\n", e);
							e.printStackTrace();
						}
					} while (logInFlag);
				} else if (choice == 2) {
					boolean signUpFlag = true;
					do {
						try {
							System.out.println("\n0 - Back\n1 - Sign up as customer\n2 - Sign up as seller");
							System.out.printf("Choice : ");
							choice = scan.nextInt();
							scan.nextLine();

							if (choice == 0) signUpFlag = false;
							else if (choice == 1) {
								System.out.printf("\nUsername: ");
								String usernameValue = scan.nextLine();
								System.out.printf("Password: ");
								String passwordValue = scan.nextLine();

								if (!Customers.containsKey(usernameValue)) {
									Customers.put(usernameValue, passwordValue);
									System.out.println("You have signed up successfully.\n");
									saveCustomers();
									signUpFlag = false;
								} else System.out.println("This username has been taken.");
							} else if (choice == 2) {
								System.out.printf("\nUsername: ");
								String usernameValue = scan.nextLine();
								System.out.printf("Password: ");
								String passwordValue = scan.nextLine();
								usernameValue.trim();
								passwordValue.trim();

								if (!Sellers.containsKey(usernameValue)) {
									SellerRequest newRequest = new SellerRequest(new Seller(usernameValue, passwordValue, this));
									Requests.add(newRequest);
									System.out.println("Approval sent!\n");
									saveRequests();
									signUpFlag = false;
								} else System.out.println("This username has been taken.");
							} else System.out.println("Invalid choice, please try again.");
						} catch (Exception e) {
							scan.nextLine();
							System.out.printf("Error: %s\n", e);
							e.printStackTrace();
						}
					} while (signUpFlag);
				} else System.out.println("Invalid choice, please try again.");
			} catch (Exception e) {
				scan.nextLine();
				System.out.printf("Error: %s\n", e);
				e.printStackTrace();
			}
		} while (flag);
	}

	private void addProduct(String productName) {
		if (!products.containsKey(productName)) {
			products.put(productName, new LinkedList());
			createBST();
		} else System.out.println("This product already exists.");
	}

	private void removeProduct(String productName) {
		if (products.containsKey(productName)) {
			products.remove(productName);
			createBST();
		} else System.out.println("This product doesn't exist.");
	}

	private void saveProducts() {
		try {
			FileWriter writer = new FileWriter(resourcesDir + "Products.txt");

			for (Map.Entry<String, LinkedList<Product>> entry : products.entrySet()) {
				writer.write(entry.getKey() + " ");
				LinkedList<Product> temp = entry.getValue();
				Iterator<Product> iter = temp.iterator();
				while (iter.hasNext()) writer.write(iter.next() + " ");
				writer.write("\n");
			}

			writer.close();
		} catch (IOException e) {
			System.out.println(e);
			e.printStackTrace();
		}
	}

	private void saveCustomers() {
		try {
			File file = new File(resourcesDir + "Customers.txt");
			file.createNewFile();
			BufferedWriter bf = new BufferedWriter(new FileWriter(file));

			for (Map.Entry<String, String> entry : Customers.entrySet()) {
				bf.write(entry.getKey() + " " + entry.getValue());
				bf.newLine();
			}

			bf.flush();
			bf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveSellers() {
		try {
			File file = new File(resourcesDir + "Sellers.txt");
			file.createNewFile();
			BufferedWriter bf = new BufferedWriter(new FileWriter(file));

			for (Map.Entry<String, String> entry : Sellers.entrySet()) {
				bf.write(entry.getKey() + " " + entry.getValue());
				bf.newLine();
			}

			bf.flush();
			bf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveRequests() {
		try {
			FileWriter writer = new FileWriter(resourcesDir + "Requests.txt");
			Iterator<Request> iterator = Requests.iterator();

			while (iterator.hasNext()) {
				Request temp = iterator.next();
				if (temp.priority == 0)
					writer.write(temp.priority + " " + ((SellerRequest) temp).user.username + " " + ((SellerRequest) temp).user.password + "\n");
				else writer.write(temp.priority + " " + ((ProductRequest) temp).productName + "\n");
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void exit() {
		saveProducts();
		saveCustomers();
		saveSellers();
		saveRequests();
	}
}

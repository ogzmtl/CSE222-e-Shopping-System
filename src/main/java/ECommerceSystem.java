package main.java;

import java.util.*;
import java.io.*;
import java.lang.System;
import java.util.function.BiConsumer;

import main.DataStructures.Trees.BinarySearchTree;

/**
 * Class to control whole E-commerce system
 */
@SuppressWarnings("unchecked")
public class ECommerceSystem {
	/**
	 * Constant variable for file paths
	 */
	protected final String resourcesDir = System.getProperty("user.dir") + "/src/main/resources/";
	
	private Map<String, String> Sellers = new HashMap<>();
	private Map<String, String> Customers = new HashMap<>();
	private Map<String, String> Admins = new HashMap<>();
	private Map<String, LinkedList<Product>> products = new TreeMap();
	private PriorityQueue<Request> Requests = new PriorityQueue();
	private ArrayList<BinarySearchTree<Product>> productsOrdered = new ArrayList();
	private Map<Integer, Integer> UnproccessedOrders = new HashMap<>();
	private static int lastID = 0;

	/**
	 * Inner static abstract User class
	 */
	public static abstract class User {
		/** System that user belongs to */
		protected ECommerceSystem systemRef;
		/** Username of the user */
		protected String username;

		/**
		 * Constructor for class User
		 * @param userNameValue Username of the user
		 * @param callerSystem System that the user belongs to
		 */
		User(String userNameValue, ECommerceSystem callerSystem){
			username = userNameValue;
			systemRef = callerSystem;
		}

		/**
		 * Abstract user interface class
		 */
		protected abstract void UI();

		/**
		 * Getter for the product
		 * @param productName Name of the product
		 * @param seller Name of the seller of the product
		 * @return Product that identified with parameters
		 */
		protected Product getProduct(String productName, String seller) {
			LinkedList<Product> targetList = systemRef.products.get(productName);
			if(targetList != null)
				for(Product temp : targetList)
					if(temp.sellerName.equals(seller)) return temp;

			return null;
		}

		/**
		 * Getter for the products linked list
		 * @param productName Name of the product
		 * @return LinkedList that consists of Products
		 */
		protected LinkedList<Product> getProduct(String productName) {
			return systemRef.products.get(productName);
		}

		/**
		 * Method to control if given string is integer or not
		 * @param str String that will be checked whether it consists of number or not
		 * @return Boolean value to identify if string is integer or not
		 */
		public static boolean isInteger(String str) {
			if (str == null) {
				return false;
			}
			int length = str.length();
			if (length == 0) {
				return false;
			}
			for (int i = 0; i < length; i++) {
				char c = str.charAt(i);
				if (c < '0' || c > '9') {
					return false;
				}
			}
			return true;
		}

		/**
		 * Incrementer for the products' ID
		 */
		protected void incrementID(){
			systemRef.lastID++;
		}

		/**
		 * Getter for the products' map
		 * @return Map that consists of product names and their sellers
		 */
		protected Map<String, LinkedList<Product>> getProductsMap(){
			return systemRef.products;
		}

		/**
		 * Getter for ID of the products
		 * @return ID of the products
		 */
		protected int getID(){
			return systemRef.lastID;
		}

		/**
		 * Method to update waiting orders
		 * @param idValue ID of the product
		 * @param situation Integer to represent situation (-1 for cancelled, 0 for processing, 1 for finished)
		 */
		protected void updateOrders(int idValue, int situation){
			systemRef.UnproccessedOrders.put(idValue, situation);
		}

		/**
		 * Getter for the orders map
		 * @return Map that consists of ID and situation of the products
		 */
		protected HashMap<Integer, Integer> getOrders(){
			return (HashMap)systemRef.UnproccessedOrders;
		}

		/**
		 * Getter for the products ArrayList
		 * @return ArrayList that consists of bst for each product
		 */
		protected ArrayList<BinarySearchTree<Product>> getProducts() {
			return systemRef.productsOrdered;
		}

		protected void updateBST(){
			systemRef.createBST();
		}

		/**
		 * Password changer for users
		 * @param newPass New password
		 * @throws InvalidClassException for not allowed class
		 */
		protected void changePass (String newPass) throws InvalidClassException {
			if (getClass().equals(Seller.class))
				systemRef.Sellers.put(username, newPass);

			else if (getClass().equals(Customer.class))
				systemRef.Customers.put(username, newPass);

			else if (getClass().equals(Admin.class))
				systemRef.Admins.put(username, newPass);

			else throw new InvalidClassException("This user is not allowed in the system");
		}

		/**
		 * Method to get input as integer
		 * @param scan Scanner to get input
		 * @param loopMsg String value
		 * @return integer representation of the input
		 */
		protected int getInputInt(Scanner scan, String loopMsg) {
			while (true) {
				System.out.print(loopMsg);
				try {
					int in = scan.nextInt();
					scan.nextLine();
					return in;
				} catch (InputMismatchException e2) {
					scan.nextLine();
					System.out.print("\033[2A\r\033[JInvalid Input\n");
				}
			}
		}

		/**
		 * Method to create request to add new product to the product list
		 * @param product Name of the product that will be requested
		 * @return boolean value to indicate if request is sent or not
		 */
		protected boolean newProductRequest (String product) {
			if (systemRef.products.containsKey(product))
				return false;

			return systemRef.Requests.add(new ProductRequest(product));
		}
	}

	/**
	 * Method to create binary search tree for the products
	 */
	private void createBST() {
		productsOrdered = new ArrayList();
		for (Map.Entry<String, LinkedList<Product>> entry : products.entrySet()) {
			productsOrdered.add(new BinarySearchTree());
			LinkedList<Product> temp = entry.getValue();
			for (Product product : temp) productsOrdered.get(productsOrdered.size() - 1).add(product.clone());
		}
	}

	/**
	 * Inner class for the products
	 */
	protected static class Product implements Comparable<Product>, Cloneable {
		private final String productName;
		private final String sellerName;
		private double price;
		private int stock;

		/**
		 * Constructor for the Product
		 * @param productName Name of the product
		 * @param sellerName Name of the seller
		 * @param price Price of the product
		 * @param stock Stock amount of the product
		 */
		public Product(String productName, String sellerName, double price, int stock) {
			this.productName = productName;
			this.sellerName = sellerName;
			this.price = price;
			this.stock = stock;
		}

		/**
		 * Overridden toString method
		 * @return String representation of the product
		 */
		public String toString(){
			StringBuilder strb = new StringBuilder();
			strb.append(sellerName).append(" ")
					.append(price).append(" ")
					.append(stock);

			return strb.toString();
		}

		/**
		 * Overridden compareTo method to compare products by their prices
		 * @param o Product that will be compared
		 * @return Integer value to represent bigger, smaller, or equal situations
		 */
		@Override
		public int compareTo(Product o){
			return Double.compare(price, o.price);
		}

		/**
		 * Getter for the product name
		 * @return Name of the product
		 */
		public String getProductName() {
			return productName;
		}

		/**
		 * Getter for the product stock
		 * @return Stock of the product
		 */
		public int getStock() {
			return stock;
		}

		/**
		 * Setter for the stock of the product
		 * @param stock Stock of the product
		 */
		public void setStock(int stock) {
			this.stock = stock;
		}

		/**
		 * Getter for the name of the seller
		 * @return Name of the seller
		 */
		public String getSellerName() {
			return sellerName;
		}

		/**
		 * Getter for the product price
		 * @return Price of the product
		 */
		public double getPrice() {
			return price;
		}

		/**
		 * Clone method to copy product
		 * @return Product that will be cloned
		 */
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

	/**
	 * Inner Admin class
	 */
	private class Admin extends User {
		/**
		 * Constructor for the Admin class which calls User's constructor
		 * @param usernameValue username of the Adming
		 * @param callerSystem System that Admin belongs to
		 */
		public Admin(String usernameValue, ECommerceSystem callerSystem) {
			super(usernameValue, callerSystem);
		}

		/**
	     * User interface of the Admin, overridden from abstract User class
	     */
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
										Sellers.put(((SellerRequest) Requests.peek()).user.username, ((SellerRequest) Requests.poll()).password);
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
							if (Sellers.containsKey(remove)){
								Sellers.remove(remove);
								File file = new File(resourcesDir + "Sellers/" + remove + ".txt");
								file.delete();
							}
							else System.out.println("There is no such user.");
						} else if (remove.equals("c")) {
							System.out.printf("Enter the username you want to remove: ");
							remove = scan.nextLine();
							remove.trim();
							if (Customers.containsKey(remove)){
								Customers.remove(remove);
								File file = new File(resourcesDir + "Customers/" + remove + ".txt");
								file.delete();
							}
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

	/**
	 * Inner Request class
	 */
	private abstract static class Request implements Comparable<Request> {
		/**
		 * Priority value for the requests
		 */
		protected int priority;

		/**
		 * Compare method to compare requests by their priority value
		 * @param o Other request
		 * @return int to represent relation of bigger, smaller, or equal
		 */
		public int compareTo(Request o) {
			return priority - o.priority;
		}
	}

	/**
	 * Inner SellerRequest class extends Request
	 */
	protected static class SellerRequest extends Request {
		private User user;
		private String password;

		/**
		 * Constructor for SellerRequest class
		 * @param user User of the system
		 * @param password Password of the user
		 */
		public SellerRequest(User user, String password) {
			this.user = user;
			this.password = password;
			priority = 0;
		}

		/**
		 * Overridden toString method
		 * @return String representation of the class
		 */
		public String toString() {
			return "Seller request: " + user.username;
		}
	}

	/**
	 * Inner ProductRequest class extends Request
	 */
	protected static class ProductRequest extends Request {
		private String productName;

		/**
		 * Constructor for ProductRequest class
		 * @param name Name of the product
		 */
		public ProductRequest(String name) {
			productName = name;
			priority = 1;
		}

		/**
		 * Overridden toString method
		 * @return String representation of the class
		 */
		public String toString() {
			return "Product request: " + productName;
		}
	}

	/**
	 * No parameter constructor for the system
	 */
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
				if (priority == 0) Requests.add(new SellerRequest(new Seller(reader.next(), this), reader.next()));
				else Requests.add(new ProductRequest(reader.next()));
			}

			file = new File(resourcesDir + "Orders.txt");
			file.createNewFile();
			reader = new Scanner(file);
			if (reader.hasNext()) lastID = Integer.parseInt(reader.next());
			while (reader.hasNext()){
				int idValue = Integer.parseInt(reader.next());
				int bool = Integer.parseInt(reader.next());
				UnproccessedOrders.put(idValue, bool);
			}

			createBST();
		} catch (Exception e) {
			System.out.println("Error during opening the file.");
			e.printStackTrace();
		}
	}

	/**
	 * Starter menu of the E-Commerce System
	 */
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
									Admin newAdmin = new Admin(usernameValue, this);
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
									Customer newCustomer = new Customer(usernameValue, this);
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
									Seller newSeller = new Seller(usernameValue, this);
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
									SellerRequest newRequest = new SellerRequest(new Seller(usernameValue, this), passwordValue);
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

	/**
	 * Method to add product to list
	 * @param productName Name of the product that will be added to list
	 */
	private void addProduct(String productName) {
		if (!products.containsKey(productName)) {
			products.put(productName, new LinkedList());
			createBST();
		} else System.out.println("This product already exists.");
	}

	/**
	 * Method to remove product from the list
	 * @param productName Name of the product that will be removed from the list
	 */
	private void removeProduct(String productName) {
		if (products.containsKey(productName)) {
			if (products.get(productName).size() == 0){
				products.remove(productName);
				createBST();
			}
			else System.out.println("There is/are seller(s) for this product.");
		} else System.out.println("This product doesn't exist.");
	}

	/**
	 * Method to save products to the txt file
	 */
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

	/**
	 * Method to save customers to the txt file
	 */
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

	/**
	 * Method to save sellers to the txt file
	 */
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

	/**
	 * Method to save requests to the txt file
	 */
	private void saveRequests() {
		try {
			FileWriter writer = new FileWriter(resourcesDir + "Requests.txt");
			Iterator<Request> iterator = Requests.iterator();

			while (iterator.hasNext()) {
				Request temp = iterator.next();
				if (temp.priority == 0)
					writer.write(temp.priority + " " + ((SellerRequest) temp).user.username + " " + ((SellerRequest) temp).password + "\n");
				else writer.write(temp.priority + " " + ((ProductRequest) temp).productName + "\n");
			}

			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to save orders to the txt file
	 */
	private void saveOrders(){
		try{
			FileWriter writer = new FileWriter(resourcesDir + "Orders.txt");
			writer.write(lastID + "\n");

			for (Map.Entry<Integer, Integer> entry : UnproccessedOrders.entrySet()){
				writer.write(entry.getKey() + " " + entry.getValue() + "\n");
			}

			writer.close();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Exit method to save all informations necessary to txt files
	 */
	private void exit() {
		saveProducts();
		saveCustomers();
		saveSellers();
		saveRequests();
		saveOrders();
	}
}

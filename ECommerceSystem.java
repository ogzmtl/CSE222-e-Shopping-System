import java.util.*;
import java.io.*;
import java.lang.System;
import java.util.function.BiConsumer;

import sourcepackage.*;

@SuppressWarnings("unchecked")
public class ECommerceSystem {
	private Map<String, String> Sellers = new HashMap<>();
	private Map<String, String> Customers = new HashMap<>();
	private Map<String, String> Admins = new HashMap<>();
	private Map<String, LinkedList<Product>> products = new TreeMap();
	private PriorityQueue<Request> Requests = new PriorityQueue();
	private ArrayList<BinarySearchTree<Product>> productsOrdered = new ArrayList();

	private void createBST() {
		productsOrdered = new ArrayList();
		for (Map.Entry<String, LinkedList<Product>> entry : products.entrySet()) {
			productsOrdered.add(new BinarySearchTree());
			LinkedList<Product> temp = entry.getValue();
			Iterator<Product> iter = temp.iterator();
			while (iter.hasNext()) productsOrdered.get(productsOrdered.size() - 1).add(iter.next().clone());
		}
	}

	private Product getProduct(String productName, String seller) {
		LinkedList<Product> targetList = products.get(productName);
		if(targetList != null)
			for(Product temp : targetList)
				if(temp.sellerName.equals(seller)) return temp;

		return null;
	}

	private LinkedList<Product> getProduct(String productName) {
		LinkedList<Product> targetList = products.get(productName);
		return targetList;

	}

	private class Product implements Comparable<Product>, Cloneable {
		private String productName;
		private String sellerName;
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

	private class Customer extends User {

		public Customer(String usernameValue, String passwordValue) {
			super(usernameValue, passwordValue);
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
			Scanner inp = new Scanner(System.in);
			int input = 1;
			String productName;
			while (input != 0) {
				System.out.printf("0.Exit\n1.Display Products\n");
				input = inp.nextInt();

				if (input == 1) {
					for (BinarySearchTree<Product> temp : productsOrdered)
						if(temp.getData() != null)
							System.out.println(temp.getData().productName);

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
			LinkedList<Product> temp = new LinkedList<>();
			Iterator<Product> iterator = temp.iterator();

			temp = getProduct(productName);

			System.out.printf("Product : " + productName + "\n");
			System.out.println("~~~~~");

			if (flag == 1) { // display by product name
				for (Product iter : temp) {
					System.out.printf("Seller :" + iter.sellerName + "\n");
					System.out.printf("Price :" + iter.price + "\n");
					System.out.println("~~~~~");
				}
			} else if (flag == 2) { // display product by price ascending
				System.out.println("In progress");
				for (BinarySearchTree<Product> temp2 : productsOrdered)
					if (temp2.getData() != null &&
						temp2.getData().productName.equals(productName))
						System.out.println(temp2);
			}
		}
	}

	private class Seller extends User {
		private LinkedList<Order> orderHistory;
		private Queue<Order> waitingOrders;
		private LinkedList<Product> productList;

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

				lastID = ID;
			}

			public Order(Map<Product, Integer> orderedProducts) {
				this.orderedProducts = new HashMap<>();
				this.orderedProducts.putAll(orderedProducts);
				ID = ++lastID;
			}

			public void add(Product product, int quantity) {
				orderedProducts.put(product, quantity);
				product.setStock(product.getStock() - quantity);
			}

			public Integer remove(Product product) {
				return orderedProducts.remove(product);
			}

			public List<Product> process() {
				LinkedList<Product> outOfStock = new LinkedList<>();

				BiConsumer<Product, Integer> processor = new BiConsumer<Product, Integer>() {
					@Override
					public void accept(Product product, Integer numOfUnits) {
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

				for (Map.Entry<Product, Integer> entry : orderedProducts.entrySet())
					strb.append(entry.getKey().getProductName())
							.append(",")
							.append(entry.getValue())
							.append(" ");

				return strb.toString();
			}
		}

		public Seller(String username, String password)
				throws FileNotFoundException {
			super(username, password);

			orderHistory = new LinkedList<>();
			waitingOrders = new ArrayDeque<>();
			productList = new LinkedList<>();

			File file = new File(username + ".txt");
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
				buffer = reader.nextLine();
				String[] products = buffer.split(" ");
				for (String productName : products) {
					productList.addFirst(getProduct(productName, username));
				}

				// The list of waiting orders
				if (reader.hasNext()){
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
			FileWriter file = new FileWriter(username + ".txt", false);
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

		public void addOrder (Map<Product, Integer> orderProducts) {
			waitingOrders.add(new Order(orderProducts));
		}
	}

	private class Admin extends User {
		public Admin(String usernameValue, String passwordValue) {
			super(usernameValue, passwordValue);
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

	private abstract class Request implements Comparable<Request> {
		protected int priority;

		public int compareTo(Request o) {
			return priority - o.priority;
		}
	}

	private class SellerRequest extends Request {
		private User user;

		public SellerRequest(User user) {
			this.user = user;
			priority = 0;
		}

		public String toString() {
			return "Seller request: " + user.username;
		}
	}

	private class ProductRequest extends Request {
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
			File file = new File("Products.txt");
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

			file = new File("Sellers.txt");
			file.createNewFile();
			reader = new Scanner(file);
			while (reader.hasNext()) Sellers.put(reader.next(), reader.next());

			file = new File("Customers.txt");
			file.createNewFile();
			reader = new Scanner(file);
			while (reader.hasNext()) Customers.put(reader.next(), reader.next());

			file = new File("Admins.txt");
			file.createNewFile();
			reader = new Scanner(file);
			while (reader.hasNext()) Admins.put(reader.next(), reader.next());

			file = new File("Requests.txt");
			file.createNewFile();
			reader = new Scanner(file);
			while (reader.hasNext()) {
				int priority = Integer.parseInt(reader.next());
				if (priority == 0) Requests.add(new SellerRequest(new Seller(reader.next(), reader.next())));
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
									Admin newAdmin = new Admin(usernameValue, passwordValue);
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
									Customer newCustomer = new Customer(usernameValue, passwordValue);
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
									Seller newSeller = new Seller(usernameValue, passwordValue);
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
									SellerRequest newRequest = new SellerRequest(new Seller(usernameValue, passwordValue));
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
			File file = new File("Products.txt");
			file.createNewFile();

			FileWriter writer = new FileWriter("Products.txt");

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
			File file = new File("Customers.txt");
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
			File file = new File("Sellers.txt");
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
			File file = new File("Requests.txt");
			file.createNewFile();

			FileWriter writer = new FileWriter("Requests.txt");
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

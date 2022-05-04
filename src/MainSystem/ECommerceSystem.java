package MainSystem;

import java.util.*;
import java.io.*;
import java.lang.System;
@SuppressWarnings("unchecked")
public class ECommerceSystem{
	private Map<String, String> Sellers = new HashMap<>();
	private Map<String, String> Customers = new HashMap<>();
	private Map<String, String> Admins = new HashMap<>();
	private Map<String, LinkedList<Product>> products = new TreeMap();
	private PriorityQueue<Request> Requests = new PriorityQueue();

	protected class Product{
		private String productName;
		private String sellerName;
		private double price;
		private int stock;

		public Product(String productName, String sellerName, double price, int stock){
			this.productName = productName;
			this.sellerName = sellerName;
			this.price = price;
			this.stock = stock;
		}



		public String toString(){
			return sellerName + " " + price + " " + stock;
		}
	}
	
	private class Admin extends User{
		public Admin(String usernameValue, String passwordValue){
			super(usernameValue, passwordValue);
		}

		public void UI(){
			int choice;
			boolean flag = true;
			Scanner scan = new Scanner(System.in);

			System.out.printf("\nWelcome dear %s!", username);

			do{
				try{
					System.out.println("\nWhat do you want to do?");
					System.out.println("0 - Exit\n1 - Check requests\n2 - Remove user\n3 - Add product\n4 - Remove product");
					System.out.printf("Choice: ");
					choice = scan.nextInt();
					scan.nextLine();

					if (choice == 0){
						System.out.printf("Goodbye %s!\n", username);
						flag = false;
					}
					else if (choice == 1){
						if (Requests.size() == 0) System.out.println("There is no pending request.");
						else{
							boolean acceptFlag = true;
							do{
								System.out.printf("Next pending request --> %s\n", Requests.peek());
								System.out.printf("Do you want to accept it? (y: yes, n: no) --> ");
								String acceptance = scan.nextLine();
								if (acceptance.equals("y")){
									if (Requests.peek().priority == 0) Sellers.put(((SellerRequest)Requests.peek()).user.username, ((SellerRequest)Requests.poll()).user.password);
									else addProduct(((ProductRequest)Requests.poll()).productName);
									acceptFlag = false;
								}
								else if (acceptance.equals("n")){
									Requests.poll();
									acceptFlag = false;
								}
								else System.out.println("Invalid choice, please try again.");
							}while(acceptFlag);
						}
					}
					else if (choice == 2){
						System.out.printf("Enter the type of the user you want to remove (s: seller, c: customer): ");
						String remove = scan.nextLine();
						if (remove.equals("s")){
							System.out.printf("Enter the username you want to remove: ");
							remove = scan.nextLine();
							remove.trim();
							if (Sellers.containsKey(remove)) Sellers.remove(remove);
							else System.out.println("There is no such user.");
						}
						else if (remove.equals("c")){
							System.out.printf("Enter the username you want to remove: ");
							remove = scan.nextLine();
							remove.trim();
							if (Customers.containsKey(remove)) Customers.remove(remove);
							else System.out.println("There is no such user.");
						}
						else System.out.println("Invalid choice.");
					}
					else if (choice == 3){
						System.out.printf("Enter the name of the product: ");
						String productName = scan.nextLine();
						productName.trim();
						addProduct(productName);
					}
					else if (choice == 4){
						System.out.printf("Enter the name of the product: ");
						String productName = scan.nextLine();
						productName.trim();
						removeProduct(productName);
					}
					else System.out.println("Invalid choice, please try again.");
				}
				catch (InputMismatchException e){
					scan.nextLine();
					System.out.printf("Error: %s\n", e);
					e.printStackTrace();
				}
				catch (Exception e){
					System.out.printf("Error: %s\n", e);
					e.printStackTrace();
				}
			}while(flag);
		}
	}

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
					targetProduct = getProduct(word, username);
					if (targetProduct != null)
						productList.add(targetProduct);
				}

				buffer = reader.nextLine();
				String[] words;
				for(String word : buffer.split(" ")) {
					words = word.split(",");
					targetProduct = getProduct(word, username);
					if (targetProduct != null)
						productList.add(targetProduct);
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
	
	private abstract class Request implements Comparable<Request>{
		protected int priority;

		public int compareTo(Request o){
			return priority - o.priority;
		}
	}

	private class SellerRequest extends Request{
		private User user;

		public SellerRequest(User user){
			this.user = user;
			priority = 0;
		}

		public String toString(){
			return "Seller request: " + user.username;
		}
	}

	private class ProductRequest extends Request{
		private String productName;
		public ProductRequest(String name){
			productName = name;
			priority = 1;
		}
		public String toString(){
			return "Product request: " + productName;
		}
	}

	public ECommerceSystem(){
		//READING FROM FILES
		try{
			File file = new File("Products.txt");
			file.createNewFile();
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()){
				String temp = reader.nextLine();
				String[] lineWords = temp.trim().split("\\s+");
				products.put(lineWords[0], new LinkedList());
				for (int i = 1; i < lineWords.length; i+=3) products.get(lineWords[0]).add(new Product(lineWords[0], lineWords[i], Double.parseDouble(lineWords[i+1]), Integer.parseInt(lineWords[i+2])));	
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
			while (reader.hasNext()){
				int priority = Integer.parseInt(reader.next());
				if (priority == 0) Requests.add(new SellerRequest(new Seller(reader.next(), reader.next())));
				else Requests.add(new ProductRequest(reader.next()));
			}
		}
		catch (Exception e){
			System.out.println("Error during opening the file.");
			e.printStackTrace();
		}
	}

	public void menu(){
		boolean flag = true;
		System.out.println("~~~~ Welcome to E-Commerce System ~~~~\n--------------------------------------");
		int choice;
		Scanner scan = new Scanner(System.in);
		do{
			try{
				System.out.println("\nMENU");
				System.out.println("0 - Exit\n1 - Log in\n2 - Sign up");
				System.out.printf("Choice : ");
				choice = scan.nextInt();
				scan.nextLine();

				if (choice == 0){
					System.out.println("\nGOOD-BYE!");
					flag = false;
				}
				else if (choice == 1){
					boolean logInFlag = true;
					do{
						try{
							System.out.println("\n0 - Back\n1 - Log in as admin\n2 - Log in as customer\n3 - Log in as seller");
							System.out.printf("Choice : ");
							choice = scan.nextInt();
							scan.nextLine();

							if (choice == 0) logInFlag = false;
							else if (choice == 1){
								System.out.printf("\nUsername: ");
								String usernameValue = scan.nextLine();
								System.out.printf("Password: ");
								String passwordValue = scan.nextLine();
								usernameValue.trim(); passwordValue.trim();

								if (Admins.containsKey(usernameValue) && Admins.get(usernameValue).equals(passwordValue)){
									Admin newAdmin = new Admin(usernameValue, passwordValue);
									newAdmin.UI();
									exit();
									logInFlag = false;
								}
								else System.out.println("Invalid username or password!");
							}
							else if (choice == 2){
								System.out.printf("\nUsername: ");
								String usernameValue = scan.nextLine();
								System.out.printf("Password: ");
								String passwordValue = scan.nextLine();
								usernameValue.trim(); passwordValue.trim();

								if (Customers.containsKey(usernameValue) && Customers.get(usernameValue).equals(passwordValue)){
									Customer newCustomer = new Customer(usernameValue, passwordValue);
									newCustomer.UI();
									//saveRequests();
									logInFlag = false;
								}
								else System.out.println("Invalid username or password!");
							}
							else if (choice == 3){
								System.out.printf("\nUsername: ");
								String usernameValue = scan.nextLine();
								System.out.printf("Password: ");
								String passwordValue = scan.nextLine();
								usernameValue.trim(); passwordValue.trim();

								if (Sellers.containsKey(usernameValue) && Sellers.get(usernameValue).equals(passwordValue)){
									Seller newSeller = new Seller(usernameValue, passwordValue);
									newSeller.UI();
									saveRequests();
									logInFlag = false;
								}
								else System.out.println("Invalid username or password!");	
							}
							else System.out.println("Invalid choice, please try again.");
						}
						catch(Exception e){
							scan.nextLine();
							System.out.printf("Error: %s\n", e);
							e.printStackTrace();
						}
					}while(logInFlag);
				}
				else if (choice == 2){
					boolean signUpFlag = true;
					do{
						try{
							System.out.println("\n0 - Back\n1 - Sign up as customer\n2 - Sign up as seller");
							System.out.printf("Choice : ");
							choice = scan.nextInt();
							scan.nextLine();

							if (choice == 0) signUpFlag = false;
							else if (choice == 1){
								System.out.printf("\nUsername: ");
								String usernameValue = scan.nextLine();
								System.out.printf("Password: ");
								String passwordValue = scan.nextLine();

								if (!Customers.containsKey(usernameValue)){
									Customers.put(usernameValue, passwordValue);
									System.out.println("You have signed up successfully.\n");
									saveCustomers();
									signUpFlag = false;
								}
								else System.out.println("This username has been taken.");
							}
							else if (choice == 2) {
								System.out.printf("\nUsername: ");
								String usernameValue = scan.nextLine();
								System.out.printf("Password: ");
								String passwordValue = scan.nextLine();
								usernameValue.trim(); passwordValue.trim();

								if (!Sellers.containsKey(usernameValue)){
									SellerRequest newRequest = new SellerRequest(new Seller(usernameValue, passwordValue));
									Requests.add(newRequest);
									System.out.println("Approval sent!\n");
									saveRequests();
									signUpFlag = false;
								}
								else System.out.println("This username has been taken.");
							}
							else System.out.println("Invalid choice, please try again.");
						}
						catch(Exception e){
							scan.nextLine();
							System.out.printf("Error: %s\n", e);
							e.printStackTrace();
						}
					}while(signUpFlag);
				}
				else System.out.println("Invalid choice, please try again.");
			}
			catch(Exception e){
				scan.nextLine();
				System.out.printf("Error: %s\n", e);
				e.printStackTrace();
			} 
		}while(flag);
	}

	private void addProduct(String productName){
		if (!products.containsKey(productName)) products.put(productName, new LinkedList());
		else System.out.println("This product already exists.");
	}

	private void removeProduct(String productName){
		if (products.containsKey(productName)) products.remove(productName);
		else System.out.println("This product doesn't exist.");
	}

	private void saveProducts(){
		try{
			File file = new File("Products.txt");
			file.createNewFile();

			FileWriter writer = new FileWriter("Products.txt");

			for (Map.Entry<String, LinkedList<Product>> entry : products.entrySet()){
				writer.write(entry.getKey() + " ");
				LinkedList<Product> temp = entry.getValue();
				Iterator<Product> iter = temp.iterator();
				while (iter.hasNext()) writer.write(iter.next() + " ");
				writer.write("\n");
			}

			writer.close();
		}
		catch (IOException e){
			System.out.println(e);
			e.printStackTrace();
		}
	}

	private void saveCustomers(){
		try{
			File file = new File("Customers.txt");
			file.createNewFile();
			BufferedWriter bf = new BufferedWriter(new FileWriter(file));

			for (Map.Entry<String, String> entry : Customers.entrySet()){
				bf.write(entry.getKey() + " " + entry.getValue());
				bf.newLine();
			}

			bf.flush();
			bf.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private void saveSellers(){
		try{
			File file = new File("Sellers.txt");
			file.createNewFile();
			BufferedWriter bf = new BufferedWriter(new FileWriter(file));

			for (Map.Entry<String, String> entry : Sellers.entrySet()){
				bf.write(entry.getKey() + " " + entry.getValue());
				bf.newLine();
			}

			bf.flush();
			bf.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private void saveRequests(){
		try{
			File file = new File("Requests.txt");
			file.createNewFile();

			FileWriter writer = new FileWriter("Requests.txt");
			Iterator<Request> iterator = Requests.iterator();

			while (iterator.hasNext()){
				Request temp = iterator.next();
				if (temp.priority == 0) writer.write(temp.priority + " " + ((SellerRequest)temp).user.username + " " + ((SellerRequest)temp).user.password + "\n");
				else writer.write(temp.priority + " " + ((ProductRequest)temp).productName + "\n");
			}

			writer.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	private void exit(){
		saveProducts();
		saveCustomers();
		saveSellers();
		saveRequests();
	}

	public Product getProduct(String productName, String seller) {
		LinkedList<Product> targetList = products.get(productName);
		for(Product temp : targetList)
			if(temp.productName.equals(productName))
				return temp;

		return null;
	}
}
package System;

import Users.User;

import java.util.*;
import java.io.*;
import java.lang.System;

@SuppressWarnings("unchecked")
public class ECommerceSystem{
	private static Map<String, String> Sellers = new HashMap<>();
	private static Map<String, String> Customers = new HashMap<>();
	private static Map<String, String> Admins = new HashMap<>();
	private LinkedList<String> products = new LinkedList();
	//priority queue

	private class Admin extends User {
		//taleplere bak --> queuedaki ilk şey neyse onu sor
		//kullanıcı çıkarma
		//ürün ekleme

		public Admin(String usernameValue, String passwordValue) {
			super(usernameValue, passwordValue);
		}

		@Override
		public void UI() {

		}
	}

	private abstract static class Request implements Comparable<Request>{
		protected int priority;

		public int compareTo(Request o){
			if (o == null) throw new IllegalArgumentException();
			return o.priority - priority;
		}
	}

	private abstract class SellerRequest extends Request{
		private User user;
		public SellerRequest(User user){
			this.user = user;
			priority = 0;
		}
	}

	private abstract class ProductRequest extends Request{
		private String productName;
		public ProductRequest(String name){
			productName = name;
			priority = 1;
		}
	}

	public ECommerceSystem(String usernameValue, String passwordValue){
		//READING FROM FILES
		try{
			File file = new File("Products.txt");
			file.createNewFile();
			Scanner reader = new Scanner(file);
			while (reader.hasNextLine()) products.add(reader.nextLine());
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
		}
		catch (Exception e){
			System.out.println("Error during opening the file.");
			e.printStackTrace();
		}
	}

	public void menu(){
//		menu giris yap
//		1-admin
//		2-seller
//		3-customer
//		3
//		username: mert
//		password: password
//		check if user exists
//		customer hakan = new customer();
//		customer.UI();
//
//		3
//		username: enes
//		password: 123
//
//		----------------------------------------
//
//		2
//		username: ikea
//		password: sweden
//		check if user exists
//		--> approval sent
//		Request newRequest = new Request(new User(ikea, sweden));
		//priorityqueue.add(newRequest);


	}

	public static <T extends User> boolean approval(T user) throws Exception{
		if (user.userType.equals("src.Users.Seller")){
			if (!Sellers.containsKey(user.username)) Sellers.put(user.username, user.password);
			else throw new Exception("There is a seller with the same name.");
		}
		else{
			if (!Customers.containsKey(user.username)) Customers.put(user.username, user.password);
			else throw new Exception("There is a customer with the same name.");
		}
		return true;
	}

	public void addProduct(String productName) throws Exception{
		if (!products.contains(productName)) products.add(productName);
		else throw new Exception("This product already exists.");
	}

	public void removeProduct(String productName) throws Exception{
		if (products.contains(productName)) products.remove(productName);
		else throw new Exception("This product doesn't exist.");
	}

	private void saveProducts(){
		try{
			File file = new File("Products.txt");
			file.createNewFile();

			FileWriter writer = new FileWriter("Products.txt");
			Iterator<String> iterator = products.iterator();

			while (iterator.hasNext()) writer.write(iterator.next() + "\n");

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

	public void exit(){
		saveProducts();
		saveCustomers();
		saveSellers();
	}
}
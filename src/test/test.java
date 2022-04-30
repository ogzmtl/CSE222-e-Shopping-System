package src.test;

import java.lang.System;
public class test{
	public static void main(String[] args){
		try{
			Admin admin = new Admin("hakan", "123");
			admin.addProduct("mert");
			admin.removeProduct("Telsiz");
			admin.exit();
		}
		catch(Exception e){
			System.out.printf("%s\n", e);
		}
	}
}
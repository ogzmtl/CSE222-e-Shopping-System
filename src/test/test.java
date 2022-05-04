package test;

import MainSystem.ECommerceSystem;

import java.lang.System;
public class test{
	public static void main(String[] args){
		try{
			ECommerceSystem admin = new ECommerceSystem();
			admin.menu();
//			admin.addProduct("mert");
//			admin.removeProduct("Telsiz");
//			admin.exit();
		}
		catch(Exception e){
			System.out.printf("%s\n", e);
		}
	}
}
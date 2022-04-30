package src.Users;

public class Seller extends User{
    private class product {

    }
	/*
		kayıt olma bilgileri system'de alındı --> constructor bu bilgilerle çağrılacak
		yeni ürün eklenmesi için talep oluştur
		sattığı ürünlerin listesi
		sipariş verilenler
		ürün istatistiği --> her üründen kaç tane sattığı --> inner class olabilir
		
		UI implementasyonu
	*/

    public Seller(String usernameValue, String passwordValue) {
        super(usernameValue, passwordValue);
    }

    @Override
    public void UI() {

    }
}
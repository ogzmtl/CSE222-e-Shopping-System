public abstract class User{
	//protected String userID;
	protected String username;
	protected String password;

	public User(String usernameValue, String passwordValue){
		username = usernameValue;
		password = passwordValue;
	}

	public abstract void UI();


	//şifre yenileme
}
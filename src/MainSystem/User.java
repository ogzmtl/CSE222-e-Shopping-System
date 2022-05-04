package MainSystem;

public abstract class User{
	//protected String userID;
	protected String username;
	protected String password;

	public User(String usernameValue, String passwordValue){
		username = usernameValue;
		password = passwordValue;
	}

	public abstract void UI();
hhhh
	public boolean changePassword (String newP) {
		if (newP == null ||
			newP.length() < 8 ||
			newP.equals(password))
			return false;

		password = newP;
		return true;
	}
}
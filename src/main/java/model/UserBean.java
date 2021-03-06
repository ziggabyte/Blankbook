package model;

public class UserBean { //Bean med data som används vid inloggning 
	private String email;
	private String password;
	private String name;
	
	public UserBean(String email, String password) {
		this.email=email;
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public void resetUserBean() {
		this.email = null;
		this.password = null;
		this.name = null;
	}
	

}

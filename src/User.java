import java.io.Serializable;

import com.google.gson.Gson;


public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Gson uGson;
	
	protected String uEMail;
	protected int uID;
	protected String uPass;
	
	private int uAccountID; 
	
	
	protected Info uInfo;

	public User (String name, int ID, int accountID, String pass){
		// Initialisation du printer
		uGson = new Gson();
		
		// Initialisation des attributs
		uID = ID;
		uEMail = name;
		uPass = pass;
		
		uAccountID = accountID;
		
		// Initialisation de l'objet d'info
		uInfo = new Info(uEMail, pass);
	}
	
	// Accès aux informations dans le format Json
	public String printInfo(){
		return uGson.toJson(uInfo);
	}
	public String printUser(){
		return uGson.toJson(this);
	}
	
	// Main pour test
	public static void main(String[]args){
		
		
		
	}
	
	
	
	public String getName(){
		return uEMail;
	}
	public int getID(){
		return uID;
	}
	
	
	class Info{
		private String email;
		private String pass;
		
		public Info(String email, String pass){
			this.email = email;
			this.pass = pass;
		}
		
		public String getEMail(){
			return email;
		}
		public String getPass(){
			return pass;
		}
	}

}

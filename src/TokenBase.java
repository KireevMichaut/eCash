import java.sql.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class TokenBase {

	private Connection connection;
	private static final String SECRET_KEY = "secret";
	
	public TokenBase(String url, String user, String pass){
		// Connexion à la base de données
		try {
	        Class.forName("com.mysql.jdbc.Driver");
	    } catch (ClassNotFoundException e) {
	        System.err.println("Impossible de charger le driver " + e.getMessage());
	    } 
		try{
			connection = DriverManager.getConnection(url, user, pass);
			
		}catch (SQLException e) {
			System.err.println("[SQL ERROR] Couldn't connect to the database : " + e.getMessage());
			return;
		}
		
	}
	
	// Chiffrement d'une String en blowfish
	private String encrypt(String input){
		byte[] crypt = null;
		
		try {
			// Génération d'une spec de clé secrète à partir de la clé "secret"
			SecretKeySpec KS = new SecretKeySpec(SECRET_KEY.getBytes(), "Blowfish");
			
			// Initialisation d'un code basé sur blowfish
		    Cipher cipher = Cipher.getInstance("Blowfish");
		    cipher.init(Cipher.ENCRYPT_MODE, KS);

		    // chiffrement du message
		    crypt = cipher.doFinal(input.getBytes());
			
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Algorithme inconnu " + e.getMessage());
		} catch (NoSuchPaddingException e) {
			System.err.println("Offset inconnu " + e.getMessage());
		} catch (InvalidKeyException e) {
			System.err.println("Clé non valide " + e.getMessage());
		} catch (IllegalBlockSizeException e) {
			System.err.println("Bloc de taille invalide " + e.getMessage());
		} catch (BadPaddingException e) {
			System.err.println("Mauvais Padding : " + e.getMessage());
		}
		
		// (pour des raisons d'encodage des caractères et de sauvegarde des bytes[] en base, on gardera les HexValues)
		return byteArrayToHexString(crypt);
	}
	
	private static String byteArrayToHexString(byte[] b) {
		  String result = "";
		  for (int i=0; i < b.length; i++) {
		    result += Integer.toString( ( b[i] & 0xff ) + 0x100, 16).substring( 1 );
		  }
		  return result;
	}
	
	// Enregistrement d'un nouvel utilisateur
	public boolean registerNewUser(String email, String pass, int accountID){
		
		String encPass = encrypt(pass);
		
		PreparedStatement pst;
		
		try {
			
			// Préparation de l'insertion
			pst = connection.prepareStatement("INSERT INTO user (email, password) VALUES (?,?)");
			
			pst.setString(1, email);
			pst.setString(2, encPass);
			
			pst.executeUpdate();
			
			// Récupération de l'ID de l'utilisateur
			pst = connection.prepareStatement("SELECT id FROM user WHERE email = ?");
			
			pst.setString(1, email);
			
			if(!pst.execute()){
				return false;
			}
			ResultSet rs = pst.getResultSet();
			rs.next();
			
			int id = rs.getInt(1);
			
			//TODO récupération du mot de passe en provenance de la banque
			String accPass = "lalala";
			
			// Enregistrement du nouveau compte en banque
			pst = connection.prepareStatement("INSERT INTO account_access (id, account_id, password) VALUES (?,?,?)");
			
			pst.setInt(1, id);
			pst.setInt(2, accountID);
			pst.setString(3, accPass);
			
			if(!(pst.executeUpdate() == 1)){
				return false;
			}
			
			return true;
		} catch (SQLException e) {
			System.err.println("[ERREUR SQL] Impossible d'insérer le nouvel utilisateur: " + e.getMessage());
			return false;
		}
		
		
	}
	
	// Authentification de l'utilisateur
	public boolean authenticate(String email, String password){
		boolean ok = false;
		
		String encPass = encrypt(password);
		
		try {
			PreparedStatement pst = connection.prepareStatement("SELECT password FROM user WHERE email = ?");
			
			pst.setString(1, email);
			
			ResultSet rs = pst.executeQuery();
			
			rs.next();
			String basePass = rs.getString(1); 
			
			if(basePass.equals(encPass)){
				ok = true;
			}
			
		} catch (SQLException e) {
			System.err.println("[ERREUR SQL] : " + e.getMessage());
		}
		
		return ok;
	}
	
	// Authentification de l'utilisateur
		public void changePass(int ID, String newPass){
			
			String encPass = encrypt(newPass);
			
			try {
				PreparedStatement pst = connection.prepareStatement("UPDATE user SET password = ? WHERE id = ?");
				
				
				
				pst.setString(1, encPass);
				pst.setInt(2, ID);
				
				pst.executeUpdate();
				
				
			} catch (SQLException e) {
				System.err.println("[ERREUR SQL] : " + e.getMessage());
			}
			
		}
		
	
	
	
	// Main pour test
	public static void main(String[]args){
		// Création d'une connexion
		TokenBase tBase = new TokenBase("jdbc:mysql://localhost:3306/db_ecash", "TokenServer", "ecash");

			if(tBase.registerNewUser("a@b" , "lololo", 1234)){
				System.out.println("ok");
			}else{
				System.err.println("pas ok");
			}
			
			
	        try {
	            /* Fermeture de la connexion */
	        	tBase.connection.close();
	        } catch ( SQLException ignore ) {
	            /* Si une erreur survient lors de la fermeture, il suffit de l'ignorer. */
	        	System.err.println("[ERREUR SQL] IMPOSSIBLE DE FERMER LA CONNEXION!!");
	        }	

		    
		}
		
}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import java.net.Socket;
import java.net.UnknownHostException;


public class BankConnection {
	
	private Socket _s;
	private BufferedReader _in;
	private PrintWriter _out;
	
	public BankConnection(String url, int port){
		try {
			_s = new Socket(url,port);
			
			_out = new PrintWriter(_s.getOutputStream());
			_in = new BufferedReader(new InputStreamReader(_s.getInputStream()));
			
		} catch (UnknownHostException e) {
			System.err.println("[ERROR] Couldn't connect to " + url +" : " +e.getMessage());
		} catch (IOException e) {
			System.err.println("[ERROR] Couldn't create the socket : " + e.getMessage());
		}
	}
	
	public void creditAccount(int accountID, double amount){
		System.out.println("Lynx -> Asking Bank for credit on account " + accountID + " for € " + amount);
		
		_out.println("3"); // Demande de crédit sur un compte
		_out.println("{id:"+accountID+",amount:"+amount+"}");
		
		
	}
	
}

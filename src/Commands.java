import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

// Cette classe est une classe "boîte à outils"
		class Commands{
			
			protected TokenThread _t;
			protected TokenServer _s;
			protected BufferedReader _in;
			protected PrintWriter _out;

			protected Gson g;
			
			protected TokenBase _tBase;
			protected BankConnection _bc;
			
			// Données de l'utilisateur
			private String _email;
			private String _pass;
			
			public Commands (TokenThread t){
				this._t = t;
				this._s = t.get_serv();
				this._in = t.get_in();
				this._out = t.get_out();
				
				this.g = new Gson();
				
				this._tBase = new TokenBase("jdbc:mysql://localhost:3306/db_ecash", "TokenServer", "ecash");
			}
			
			
			
		
			public void handleNewConnection(){

				String message =""; // message du client
				
				
				try{
					// Variable pour savoir si on continue
					ArrayList<String> errorLog = new ArrayList<String>();
					
					_out.println("Server Lynx | CacheCash server");
					_out.println("                                                  |   `\\.      ,/'");
					_out.println("--------------------------------------------      |    |\\\\____//|");
					_out.println("                                                  |    )/_ `' _\\(");
					_out.println("Copyright : PSKL & SERGIO, 2014                   |   ,'/0`__'0\\`\\");
					_out.println("                                                  |   /. (_><_) ,\\");
					_out.println("--------------------------------------------      |   ` )/`--'\\(`'  atc");
					_out.println("                                                  |     `      '");
					_out.println();
					_out.println(" > Please identify yourself (Json format: email + pass)");
					_out.flush();
					
					// Attente du message au format Json contenant les informations de connexion
					message = _in.readLine();
					
					// Récupération des identifiants de connexion
					User.Info info = g.fromJson(message, User.Info.class);
					_email = info.getEMail();
					_pass = info.getPass();
					
					// Appel en base pour identifier l'utilisateur
					boolean isIdentified = false;
					if(_tBase.authenticate(_email, _pass)){
						isIdentified = true;
					}
					
					// Mauvais mot de passe
					if(!isIdentified){
						errorLog.add("User not identified");
						_out.println("[ERROR] -> We could not identify you.");
						_out.flush();
						return;
					}
					
					// Bon mot de passe
					// On souhaite la bienvenue à l'utilisateur
					_out.println("");
					_out.println("Welcome "+info.getEMail()+"!");
					_out.println("");
					_out.println(" > What would you want to do now?");
						
					do{
						_out.println("	- type 'quit' to quit");
						_out.println("	- type 'hamster' for the hamster");
						_out.println("	- type '1' to get money from your account");
						_out.println("	- type '2' to cash money to your acccount");
						_out.println("- - - - - - - - - - - - - - - - - - -");
						_out.flush();
						
						message = _in.readLine();
						
						
					}while(!(message.equals("hamster"))
							&& !(message.equals("quit"))
							&& !(message.equals("1"))
							&& !(message.equals("2")));
					
					
					if(message.equals("quit")){
						ejectUser(info);
					}else if(message.equals("hamster")){
						printHamster();
						ejectUser(info);
					}else if(message.equals("1")){
						// TODO méthode de 
						return;
					}else if(message.equals("2")){
						if(identifyUserAgain(info)){
							cashToken(info);
						}else{
							_out.println("We could not identify you!");
							ejectUser(info);
						}
					}
					
						
					
					
				// cas d'erreur	
				}catch(IOException e){
					System.err.println(e.getMessage());
				
				// Cas de déconnexion
				}finally{
					
					// Cas normal -> déconnexion du client
					try{
						// On indique la déconnexion du client
						System.out.println("Lynx --> Client n°"+_s.getCliCnt()+" just disconnected");
						_s.delClient(_s.getCliCnt()-1);
						_t.get_s().close();
						
					}catch(IOException e){
						System.out.println(e.getMessage());
					}
				}

				
				
			}
			
			private boolean identifyUserAgain(User.Info info2) throws JsonSyntaxException, IOException {
				System.out.println("Trying to identify " + info2.getEMail());
				
				_out.println(" > Please identify yourself again");
				_out.flush();
				
				User.Info info = g.fromJson(_in.readLine(), User.Info.class);
				
				return (info.getEMail().equals(_email) && info.getPass().equals(_pass));
			}
		
			private void printHamster(){
				System.out.println("HAMSTER!!!");
				
				_out.println("               .     .");
				_out.println("              (>\\---/<)");
				_out.println("              ,'     `.");
				_out.println("             /  q   p  \\");
				_out.println("            (  >(_Y_)<  )");
				_out.println("             >-' `-' `-<-.");
				_out.println("            /  _.== ,=.,- \\");
				_out.println("           /,    )`  '(    )");
				_out.println("          ; `._.'      `--<");
				_out.println("          :     \\        |  )");
				_out.println("          \\      )       ;_/  hjw");
				_out.println("           `\"._ _/_  ___.'-\\\\\\");
				_out.println("             `--\\\\\\");
				_out.flush();
				
			}
			
			private void ejectUser(User.Info info){
				System.out.println("Ejection of " + info.getEMail());
				
				_out.println("Bye "+info.getEMail()+"!");
				_out.flush();
				return;
			}
			
			private ArrayList<String> cashToken(User.Info info){
				
				ArrayList<String> errorLog = new ArrayList<String>();
				
				// On demande à l'utilisateur d'envoyer son token (haché)
				_out.println(" > Please provide your token id's hash and it's value {id:'???', value:'???'}");
				_out.flush();
				
				// On récupère les infos
				String message;
				try {
					message = _in.readLine();
					
					Token.TokenInfo tInfo = g.fromJson(message, Token.TokenInfo.class);  
					
					System.out.println(info.getEMail() + " asking to cash a token: [" + tInfo.getID() + " | " + tInfo.getValue() +"]");
					
					// TODO Vérification de solvabilité du client
					
					
					
					
				} catch (IOException e) {
					System.err.println("Couldn't receive the token informations required from the user : " +e.getMessage());
				}
				
				
				
				
				return errorLog;
			}
		}
	
		

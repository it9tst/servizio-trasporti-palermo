package com.stp.server;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.stp.server.util.json.JSONException;
import com.stp.server.util.json.JSONObject;


public class ThreadServer implements Runnable{
	
	private Socket clientSocket;
	private BufferedReader input;
	private PrintWriter output;
	private JSONObject json;
	private ConnectionDB db;
	private Integer codeResponse=null;
	private Integer[] freeAction= {0 , 9, 47, 34, 33, 45, 14};
			
			
			
	@Override
	public void run(){
		List <JSONObject> res = null;
		boolean flag = false;
		try{
			String request = this.input.readLine();
			
			this.json= new JSONObject(request);
			this.log(request);
			try{
				int action= this.json.getInt("action");
				if(!Arrays.asList(this.freeAction).contains(action)) {
					if(this.json.isNull("nick") || this.json.isNull("password")) {
						this.send(-6);
						this.close();
						return;
					}
					res = this.db.login(json.getString("nick"), json.getString("password"));
        			if(res!=null) {
        				if(res.get(0).getInt("PrimoAccesso")>0) {
        					this.send(-6);
    						this.close();
    						return;
        				}
        				if(!res.get(0).isNull("DataLicenziamento")) {
        					this.send(-6);
    						this.close();
    						return;
        				}
        				if(res.get(0).getInt("TentativiAccesso")>2) {
        					this.send(-6);
    						this.close();
    						return;
        				}
        			}else {
        				this.send(-6);
						this.close();
						return;
        			}
					
				}
				res=null;
				// se non sei loggato non fai niente; eheh disattivato in fase di test
				//if(this.json.getInt("action")!=0 && this.db.login(json.getString("nick"), json.getString("password")) == null) {
				//	return;					
				//}
				//System.out.println(this.json);
	        	switch(action){
	            	case 0: 
	            		// esegue il login
	            		
	            		if(json.getString("nick").length()>0 && json.getString("password").length()>0){
	            			res = this.db.login(json.getString("nick"), json.getString("password"));
	            			if(res!=null) {
	            				if(res.get(0).getInt("PrimoAccesso")>0) {
	            					res=null;
	            					this.codeResponse=-3;
	            					break;
	            				}
	            				if(!res.get(0).isNull("DataLicenziamento")) {
	            					res=null;
	            					this.codeResponse=-5;
	            					break;
	            				}
	            				if(res.get(0).getInt("TentativiAccesso")>2) {
	            					res=null;
	            					this.codeResponse=-4;
	            					break;
	            				}
	            				if(this.db.checkCartellino(res.get(0).getString("CodiceFiscale"))) {
	            					res.get(0).put("StatoCartellino", true);
	            				}else {
	            					res.get(0).put("StatoCartellino", false);
	            				}
	            				
	            			}else {
	            				this.codeResponse=-2;
	            			}
	            		}
	            	break;
	            	case 1:
	            		//ricerca l'elenco dei cedolini ok
	            		if(json.getString("CF").length()==16){
	            			res = this.db.visualizzaElencoCedolini(json.getString("CF"));
	            		}
	            	break;
	            	case 2:
	            		//ricerca il cedolino selezionato ok
	            		if(json.getString("CF").length()==16 && json.getInt("idCedolino")>0){
	            			res= this.db.visualizzaCedolino(json.getString("CF"),json.getInt("idCedolino"));
	            		}
	            	break;
	            	case 3:
	            		//timbra il cartellino(entrata e uscita) dovrebbe averlo fatto Luca
	            		if(json.getString("CF").length()>0){
	            			flag=this.db.timbraCartellino(json.getString("CF"));    			
	            		}
	            	break;	            		
	            	case 4:
	            		//ricerca i turni del dipendente ok
	            		if(json.getString("CodiceFiscale").length()>0){
	            			res = this.db.visualizzaTurni(json.getString("CodiceFiscale"), json.getString("start"), json.getString("end"));
	            		} 
	            	break;
	            	case 5:
	            		//ricerca elenco dei dipendenti ok
	            		if(!json.isNull("filtro")) {
	            			if(!json.isNull("disponibili")) {
	            				res = this.db.visualizzaElencoDipendenti(json.getInt("filtro"), json.getInt("disponibili"));
	            			}else {
	            				res = this.db.visualizzaElencoDipendenti(json.getInt("filtro"), 0);
	            			}
	            		}else {
	            			res = this.db.visualizzaElencoDipendenti(0, 0);
	            		}
	            	break;	
	            	case 6:
	            		//ricerca il dipendente selezionato ok
	            		if(json.getString("CF").length()>0){
	            			res = this.db.visualizzaDipendente(json.getString("CF"));
	            		} 
	            	break;
	            	case 7:
	            		//ricerca elenco fermate ok
	            		res = this.db.visualizzaElencoFermate();
	            	break;
	            	case 8:
	            		//ricerca fermata selezionata ok
	            		if(json.getInt("idFermata")>0){
	            			res = this.db.visualizzaFermata(json.getInt("idFermata"));
	            		} 
	            	break;
	            	case 9:
	            		//ricerca elenco linee ok
	            		res = this.db.visualizzaElencoLinee();
	            	break;
	            	case 10:
	            		//ricerca linea selezionata ok
	            		if(json.getInt("idLinea")>0){
	            			res = this.db.visualizzaLinea(json.getInt("idLinea"));
	            		} 
	            	break;
	            	case 11:
	            		//ricerca elenco autobus ok
	            		res = this.db.visualizzaElencoAutobus();
	            	break;
	            	case 12:
	            		//ricerca autobus selezionato ok
	            		if(json.getString("ricerca").length()>0){
	            			res = this.db.visualizzaAutobus(json.getString("ricerca"));
	            		} 
	            	break;
	            	case 13:
	            		//modifica orario linea
	            		if(json.getInt("idLinea")>0){
	            			if(json.isNull("OraInizioCorsaFestivo") ||
	            					json.isNull("OraFineCorsaFestivo") ||
	            					json.isNull("DurataFestivo") ||
	            					json.isNull("NumeroBusConsigliatoFestivo")) {
	            				flag=this.db.setOrarioLinea(json.getInt("idLinea"), json.getString("OraInizioCorsa"), json.getString("OraFineCorsa"), json.getString("Durata"), json.getInt("NumeroBusConsigliato"));
	            				
	            			}else {
	            				flag=this.db.setOrarioLinea(json.getInt("idLinea"), json.getString("OraInizioCorsa"), json.getString("OraFineCorsa"), json.getString("Durata"), json.getInt("NumeroBusConsigliato"), json.getString("OraInizioCorsaFestivo"), json.getString("OraFineCorsaFestivo"), json.getString("DurataFestivo"), json.getInt("NumeroBusConsigliatoFestivo"));
	            				
	            			}
	            			
	            		} 
	            	break;
	            	case 14:
	            		if(!json.isNull("Username") && !json.isNull("Vecchia") && !json.isNull("Nuova")){
	            			
	            			flag=this.db.cambiaPassword(json.getString("Username"), json.getString("Vecchia"), json.getString("Nuova"));			
	            		} 
	            		
	            	break;
	            	case 15:
	            		// modificaDipendente ok
	            		if(json.getString("CF").length()>0){
	            			flag = this.db.modificaDipendente(json); 
	            			
	            			
	            			
	            				this.send(1);
	            				String name = "default.png";
	            				if(!json.isNull("Foto") && !json.isNull("ext")) {
	            					name=receiveImage(json.getString("CF"), json.getString("ext"));
	            				}
	            				this.db.updateFoto(json.getString("CF"), name);
	            			
	            		} 
	            	break;
	            	case 16:
	            		// reimposta password ok
	            		if(json.getString("CodiceFiscale").length()>0){
	            			flag = this.db.reimpostaPassword(json.getString("CodiceFiscale"), json.getString("Email"), json.getString("Username")); 
	            		} 
	            	break;            	
	            	case 17:
	            		// Aggiungi turno non passo refIdLinea lo setto a 0, sarà AssociaAustistaALinea a settarlo
	            		if(json.getString("CF").length()>0){
	            			flag = this.db.aggiungiTurno(json.getString("CF"),json.getString("Giorno"), json.getString("OraInizioTurno"), json.getString("OraFineTurno")); 
	            		} 
	            	break;
	            	case 18:
	            		// Elimina Turno ok vedere PrimaryKey
	            		if(json.getString("CF").length()>0){
	            			flag = this.db.eliminaTurno(json.getString("CF"),json.getString("Giorno")); 
	            		} 
	            	break;
	            	case 19:
	            		// Gestisci Guasti
	            		if(json.getString("Targa").length()>0){
	            			String descrizione=null;
	            			if(!json.isNull("DescrizioneGuasto")) {
	            				descrizione=json.getString("DescrizioneGuasto");
	            			}
	            			flag = this.db.gestisciGuasti(json.getString("Targa"),json.getInt("StatoGuasto"), descrizione);
	            		} 
	            	break;
	            	case 20:
	            		// Elimina Linea ok
	            		if(json.getInt("idLinea")>0){
	            			flag = this.db.eliminaLinea(json.getInt("idLinea")); 
	            		} 
	            	break;
	            	case 21:
	            		// Elimina Fermata ok
	            		if(json.getInt("idFermata")>0){
	            			flag = this.db.eliminaFermata(json.getInt("idFermata")); 
	            		} 
	            	break;
	            	case 22:
	            		// Elimina Fermata da Linea ok
	            		if(json.getInt("idLinea")>0 && json.getInt("idFermata")>0 ){
	            			flag = this.db.eliminaFermataDaLinea(json.getInt("idLinea"), json.getInt("idFermata"));
	            		} 
	            	break;
	            	case 23:
	            		// Elimina Dipendente ok
	            		if(json.getString("CodiceFiscale").length()>0){
	            			flag = this.db.eliminaDipendente(json.getString("CodiceFiscale")); 
	            		} 
	            	break;
	            	case 24:
	            		// Elimina Autobus ok
	            		if(json.getString("Targa").length()>0){
	            			flag = this.db.eliminaAutobus(json.getString("Targa")); 
	            		} 
	            	break;
	            	case 25:
	            		// Crea Linea ok
	            		if(json.getInt("idLinea")>0){
	            			if(json.isNull("OraInizioCorsaFestivo") ||
	            					json.isNull("OraFineCorsaFestivo") ||
	            					json.isNull("DurataFestivo") ||
	            					json.isNull("NumeroBusConsigliatoFestivo")) {
	            				this.codeResponse=this.db.creaLinea(json.getInt("idLinea"), json.getString("OraInizioCorsa"), json.getString("OraFineCorsa"), json.getString("Durata"), json.getInt("NumeroBusConsigliato"));
	            				
	            			}else {
	            				this.codeResponse=this.db.creaLinea(json.getInt("idLinea"), json.getString("OraInizioCorsa"), json.getString("OraFineCorsa"), json.getString("Durata"), json.getInt("NumeroBusConsigliato"), json.getString("OraInizioCorsaFestivo"), json.getString("OraFineCorsaFestivo"), json.getString("DurataFestivo"), json.getInt("NumeroBusConsigliatoFestivo"));
	            				
	            			}
	            			
	            		} 
	            	break;
	            	case 26:
	            		// Crea Fermate ok warning numero civico stringa possono esserci le lettere, interi es: 32A
	            		//if(json.getInt("idFermata")>0){
	            			codeResponse = this.db.creaFermata(json.getString("Indirizzo"), json.getString("NumeroCivico"), json.getDouble("x"),json.getDouble("y")); 
	            		//}
	            	break;
	            		// Compila Cedolino (era stipendio lo cambiamo) ok
	            	case 27:
	            		if(!json.isNull("CodiceFiscale") && !json.isNull("OreMensili") && 
	            				!json.isNull("OreLavorative") && !json.isNull("OreMalattia") && 
	            				!json.isNull("OreFerie") && !json.isNull("Totale")) {
	            			
	            			String data=LocalDate.now().toString();
	            			String file=json.getString("CodiceFiscale")+"_"+data;
	            			if(this.receivePdf(file)) {
	            				
	            				if(this.db.compilaCedolino(json.getString("CodiceFiscale"),
	            						data,
	            						json.getInt("OreMensili"),
	            						json.getInt("OreLavorative"),
	            						json.getInt("OreMalattia"),
	            						json.getInt("OreFerie"),
	            						json.getDouble("Totale"),
	            						file)) {
	            					
	            					this.codeResponse=1;
	            				}
	            				
	            			}else {
	            				this.codeResponse=-1;
	            			}
	            			
	            			
	            		}	            		
	            	break;
	            		// Associa Autista a Linea ok
	            	case 28:
	            		if(json.getString("RefCodiceFiscale").length()>0){
	            			flag = this.db.associaAutistaALinea(json.getString("RefCodiceFiscale"), json.getString("Giorno"), json.getInt("RefIdLinea"), json.getString("OraInizioTurno"), json.getString("OraFineTurno"));
	            		} 	            		
	            	break;
	            	case 29:
	            		// Aggiungi Fermata a Linea ok
	            		if(json.getInt("idLinea")>0 && json.getInt("idFermata")>0 && json.getInt("posizione")>0){
	            			flag = this.db.aggiungiFermataALinea(json.getInt("idLinea"), json.getInt("idFermata"), json.getInt("posizione")); 
	            		} 
	            	break;
	            	case 30:
	            		// Ricerca fermate della Linea ok
	            		if(json.getInt("idLinea")>0){
	            			res=this.db.ricercaFermateLinea(json.getInt("idLinea"));
	            		} 
	            	break;
	            	case 31:
	            		// Aggiungi Dipendente ok
	            		if(json.getString("CodiceFiscale").length()>0){
	            			this.codeResponse = this.db.aggiungiDipendente(json.getString("CodiceFiscale"), json.getString("Nome"), json.getString("Cognome"), json.getString("DataDiNascita"), json.getString("Sesso"), json.getString("LuogoDiNascita"), json.getString("ProvinciaDiNascita"),
	            			json.getString("ComuneDiResidenza"), json.getString("ProvinciaDiResidenza"), json.getInt("CAP"), json.getString("Email"), json.getString("RecapitoTelefonico"),
	            			json.getString("ContoCorrente"), json.getString("IndirizzoDiResidenza"), json.getInt("RefIdRuolo"));  
	            			
	            			if(this.codeResponse==1) {
	            				this.send(1);
	            				String name = "default.png";
	            				if(!json.isNull("Foto") && !json.isNull("ext")) {
	            					name=receiveImage(json.getString("CodiceFiscale"), json.getString("ext"));
	            				}
	            				this.db.updateFoto(json.getString("CodiceFiscale"), name);
	            			}
	            			
	            		} 
	            	break;
	            	case 32:
	            		// Aggiungi Autobus ok
	            		if(json.getString("Targa").length()>0){
	            			codeResponse = this.db.aggiungiAutobus(json.getString("Targa"), json.getInt("PostiSeduti"), json.getInt("PostiInPiedi"), json.getInt("PostiPerDisabili"));
	            		} 	
	            	break;
	            	case 33:
	            		//Ricerca le fermate per il cittadino in base a latitudine, longitudine e raggio
	            		if(json.getDouble("radius")>0){
	            			res=this.db.cercaFermateCittadino(json.getDouble("latitude"),json.getDouble("longitude"),json.getDouble("radius"));
	            			
	            		}
	            	break;
	            	case 34:
	            		//Manda al cittadino la linea selezionata con le sue caratteristiche e le sue fermate;
	            		if(json.getInt("idLinea")>0) {
	            		res = this.db.ricercaLineaCittadino(json.getInt("idLinea"));
	            		
	            	}
	            	break;
	            	case 35:
	            		if(!json.isNull("ricerca")) {
	            			if(json.getString("ricerca").length()>0) {
	            				res = this.db.cercaFermataElenco(json.getString("ricerca"));
	            			}
	            		}
	            	break;
					case 36:
	            		//download foto
	            		if(!json.isNull("foto")) {
	            			this.sendImage(json.getString("foto"));
	            		}
	            	break;
	            	case 37:
	            		//ricerca dipendente
	            		if(!json.isNull("ricerca")) {
	            			if(json.getString("ricerca").length()>0) {
	            				if(!json.isNull("filtro")) {
	            					if(!json.isNull("disponibili")) {
	            						res = this.db.cercaDipendentiElenco(json.getString("ricerca"), json.getInt("filtro"), json.getInt("disponibili"));
	    	            			}else {
	    	            				res = this.db.cercaDipendentiElenco(json.getString("ricerca"), json.getInt("filtro"), 0);
	    	            			}
	    	            		}else {
	    	            			res = this.db.cercaDipendentiElenco(json.getString("ricerca"), 0, 0);
	    	            		}
	            				
	            			}
	            		}
	            	break;
	            	case 38:
	            		//recupera ruoli
	            		res = this.db.getRuoli();
	            	break;
	            	case 39:
	            		//compila cedolino
	            		
	            		
	            		
	            		

	            	break;
	            	case 40:
	            		if(!json.isNull("Targa") && !json.isNull("CF")) {
	            			codeResponse = this.db.registraUscitaAutobus(json.getString("Targa"), json.getString("CF"));
	            		}
	            	break;
	            	case 41:
	            		if(!json.isNull("Targa")) {
	            			codeResponse = this.db.registraEntrataAutobus(json.getString("Targa"));
	            		}
	            	break;
	            	case 42:
	            		if(!json.isNull("idLinea")) {
	            			flag = this.db.pulisciFermateLinea(json.getInt("idLinea"));
	            		}
	            	break;
	            	case 43:
	            		if(!json.isNull("CodiceFiscale")) {
	            			res=new ArrayList<JSONObject>();
	            			res.add(this.db.getInfoCedolino(json.getString("CodiceFiscale")));
	            			if(res.get(0).getInt("code")!=1) {
	            				this.codeResponse=res.get(0).getInt("code");
	            				res=null;
	            			}
	            		}
	            	break;
	            	case 44: // ritorna tutte le linee passanti per una fermata (le linee le torna con i dettagli e l'elenco di tutte le fermate per cui passa) serve per il cercafermata cittadino
	            		if(!json.isNull("codiceFermata")) {
	            			res=this.db.cercaLineeFermata(json.getInt("codiceFermata"));
	            		}
					break;
	            	case 45: //ritorna le linee che collegano(anche a 3 vie) la fermata vicina al punto di partenza e quella più vicina al punto di arrivo
	            		if( !(json.isNull("lat1") || json.isNull("lon1") || json.isNull("lat2") || json.isNull("lon2"))) {
	            			res=this.db.calcolaPercorso(json.getDouble("lat1"),json.getDouble("lon1"),json.getDouble("lat2"),json.getDouble("lon2"));
	            			if(res.size()>0 && !res.get(0).isNull("code") &&res.get(0).getInt("code")==-2) {
	            				res=null;
	            				this.codeResponse=-2;
	            			}
	            		}
	            	break;
	            	case 46: //ritorna le linee che collegano(anche a 3 vie) la fermata vicina al punto di partenza e quella più vicina al punto di arrivo
	            		if(!json.isNull("CodiceFiscale")) {
	            			flag=this.db.licenziaDipendente(json.getString("CodiceFiscale"));
	            		}
	            	break;
	            	case 47:
	            		if(!json.isNull("ricerca")) {
	            			if(json.getString("ricerca").length()>0) {
	            				res = this.db.cercaLineaElenco(json.getString("ricerca"));
	            			}
	            		}
	            	break;
	            	case 48:
	            		if(json.getString("RefCodiceFiscale").length()>0){
	            			flag = this.db.rimuoviAssociaAutistaALinea(json.getString("RefCodiceFiscale"), json.getString("Giorno"), json.getString("OraInizioTurno"), json.getString("OraFineTurno"));
	            		} 	            		
	            	break;
	            	case 49:
	            		// Aggiungi Capolinea a Linea ok
	            		if(json.getInt("idLinea")>0){
	            			int capA=-1;
	            			int capB=-1;
	            			if(!json.isNull("CapA")) {
	            				capA=json.getInt("CapA");
	            			}
	            			if(!json.isNull("CapB")) {
	            				capB=json.getInt("CapB");
	            			}
	            			flag = this.db.aggiungiCapolineaaALinea(json.getInt("idLinea"), capA, capB); 
	            		} 
	            	break;
	            	case 50:
	            		//ricerca l'elenco dei cedolini ok
	            		if(!json.isNull("Cedolino") && !json.isNull("Email")){
	            			flag= this.sendCedolinoEmail(json.getString("Cedolino"), json.getString("Email"));
	            		}
	            	break;
	            	case 51:
	            		//ricerca l'elenco dei cedolini ok
	            		if(!json.isNull("Cedolino")){
	            			flag= this.sendCedolino(json.getString("Cedolino"));
	            		}
	            	break;
	            	case 52:
	            		//ricerca l'elenco dei cedolini ok
	            		if(!json.isNull("CodiceFiscale")){
	            			flag= this.db.riassumiDipendente(json.getString("CodiceFiscale"));
	            		}
	            	break;
	            	default:
	            		this.send(-1);
	            	break;
	            }
	        	if(res==null) {
	        		if(flag == true) {
	        			this.send(1);
	        		}else {
	        			if(codeResponse!=null) {
	        				this.send(codeResponse);
	        			}
	        		}
	        	}
	        	else {
	        		this.send(1, res);
	        	}
	        	if(res==null && flag == false) {
	        		this.send(-1);
	        	}
	        }catch(JSONException e){
	        	this.sendFormatError();
	        	e.printStackTrace();
	        	System.out.println("Errore json");
	        }catch(Exception e) {
	        	e.printStackTrace();
	        	this.send(-1);
	        }
			this.close();
		}catch(IOException e){
			//errore lettura e scrittura da socket
			System.out.println("Errore lettura socket");
		}
	}
	
	
	public ThreadServer(Socket socket){
		this.clientSocket=socket;
		try{
			this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
			this.output = new PrintWriter(this.clientSocket.getOutputStream(), true);
			this.db = new ConnectionDB();
			this.execute();
		}catch(IOException e){
			
			System.out.println("Errore connessione client");
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("Errore inizializzazione connessione db");
		}catch(ClassNotFoundException e){
			System.out.println("Driver sql non trovato");
		}
		
	}

	private void execute() {
		Thread t= new Thread(this);
		t.start();
	}

	
	
	
	private void log(String log){
		try {
		PrintWriter logFile = new PrintWriter("log.txt");
		logFile.println(this.clientSocket.getInetAddress().getHostAddress() + " " + log);
		logFile.close();
		}catch(Exception e) {
			System.out.println("errore scrittura file di log");
		}
	}
	
	
	
	
	private void close() throws IOException{
		this.clientSocket.close();
		this.db.close();
	}
	
	private void send(String st){
		
		this.output.println(st);
	}
	private void send(int code, String message) {
		this.send("{code:"+code+",message:'"+message+"'}");
	}
	private void send(int code){
		this.send("{code:"+code+"}");
	}
	private void send(int code, List <JSONObject> res){
		for(int i=0;i<res.size();i++) {
			res.get(i).put("code", code);
			this.send(res.get(i).toString());
		}
		if(this.json.getInt("action")==0) {
			this.sendImage(res.get(0).getString("Foto"));
		}
		this.send("{code:0}"); //SEGNALO QUANDO HO FINITO DI MANDARE
	}
	private void sendFormatError(){
		this.send(-1, "Errore: formato messaggio non valido");
	}
	private void sendImage(String foto) {
		try {
			DataOutputStream dout=new DataOutputStream(this.clientSocket.getOutputStream());
			dout.flush();

			File f=null;
			try {
				f = new File(this.getClass().getResource("/img/"+foto).toURI());
				//this.send(1);
			} catch (URISyntaxException e) {
				return;
			}
			dout.flush();
			FileInputStream fin=new FileInputStream(f);

			byte b[]=new byte [1024];
			int read;
			while((read = fin.read(b)) != -1){
			    dout.write(b, 0, read); 
			    dout.flush();
			}
			fin.close();

			dout.flush();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	
	private String receiveImage(String cf, String ext) {
		try {
			DataInputStream din=new DataInputStream(this.clientSocket.getInputStream());
			byte b[]=new byte [1024];
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			long bytesRead;
			do{
				
				bytesRead = din.read(b, 0, b.length);
				
				baos.write(b,0,b.length);
			}while(!(bytesRead<1024));
			
	
			byte[] imageInByte = baos.toByteArray();

			
			try (FileOutputStream fos = new FileOutputStream("bin/img/"+cf.toLowerCase()+"."+ext)) {
			   fos.write(imageInByte);
			   fos.close();
			}
            return cf.toLowerCase()+"."+json.getString("ext");
        } catch (Exception ex) {
        	ex.printStackTrace();
            return "default.png";
        }
	}
	private boolean receivePdf(String file) {
		this.send(1);
		try {
			DataInputStream dis = new DataInputStream(this.clientSocket.getInputStream());
			File f=new File("bin/cedolini/"+file+".pdf");
			
	        DataOutputStream dos = new DataOutputStream(
	                new FileOutputStream(f));
	        
	        byte[] buffer = new byte[1024];
	        int len;
	        while ((len = dis.read(buffer)) != -1) {
	            dos.write(buffer, 0, len);
	        }
	        dos.flush();
	        dos.close();
	        dis.close();
	    } catch (Exception ex) {
	    	ex.printStackTrace();
        	return false;
        }
		return true;
	}
	
	 public static void sendFromGMail(String from, String pass, String[] to, String subject, String body) {
        new Thread() {
        	@Override
        	public void run() {
        		
        		Properties props = System.getProperties();
                String host = "smtp.gmail.com";
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", host);
                props.put("mail.smtp.user", from);
                props.put("mail.smtp.password", pass);
                props.put("mail.smtp.port", "587");
                props.put("mail.smtp.auth", "true");

                Session session = Session.getDefaultInstance(props);
                MimeMessage message = new MimeMessage(session);

                try {
                    message.setFrom(new InternetAddress(from));
                    InternetAddress[] toAddress = new InternetAddress[to.length];

                    // To get the array of addresses
                    for( int i = 0; i < to.length; i++ ) {
                        toAddress[i] = new InternetAddress(to[i]);
                    }

                    for( int i = 0; i < toAddress.length; i++) {
                        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
                    }

                    message.setSubject(subject);
                    message.setText(body);
                    Transport transport = session.getTransport("smtp");
                    transport.connect(host, from, pass);
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();
                }
                catch (AddressException ae) {
                    ae.printStackTrace();
                }
                catch (MessagingException me) {
                    me.printStackTrace();
                }
        	}
        }.start();
		
    }
	 
	 private boolean sendCedolinoEmail(String file, String email) {
		 new Thread() {
	        	@Override
	        	public void run() {
	        		String[] to = { email };
	        		Properties props = System.getProperties();
	                String host = "smtp.gmail.com";
	                props.put("mail.smtp.starttls.enable", "true");
	                props.put("mail.smtp.host", host);
	                props.put("mail.smtp.user", "servicetrasportipalermo");
	                props.put("mail.smtp.password", "");
	                props.put("mail.smtp.port", "587");
	                props.put("mail.smtp.auth", "true");

	                Session session = Session.getDefaultInstance(props);
	                MimeMessage message = new MimeMessage(session);

	                try {
	                	message.setSubject("Cedolino");
	                    message.setFrom(new InternetAddress("servicetrasportipalermo"));
	                    InternetAddress[] toAddress = new InternetAddress[to.length];

	                    // To get the array of addresses
	                    for( int i = 0; i < to.length; i++ ) {
	                        toAddress[i] = new InternetAddress(to[i]);
	                    }

	                    for( int i = 0; i < toAddress.length; i++) {
	                        message.addRecipient(Message.RecipientType.TO, toAddress[i]);
	                    }

	                    
	                   
	                    
	                    Multipart multipart = new MimeMultipart();

	                    MimeBodyPart textBodyPart = new MimeBodyPart();
	                    textBodyPart.setText("In allegato il cedolino richiesto");
	                    
	                    MimeBodyPart attachmentBodyPart= new MimeBodyPart();
	                    DataSource source = new FileDataSource("bin/cedolini/"+file+".pdf"); // ex : "C:\\test.pdf"
	                    attachmentBodyPart.setDataHandler(new DataHandler(source));
	                    attachmentBodyPart.setFileName("cedolino.pdf"); // ex : "test.pdf"

	                    multipart.addBodyPart(textBodyPart);  // add the text part
	                    multipart.addBodyPart(attachmentBodyPart); // add the attachement part

	                    message.setContent(multipart);
	                    
	                    
	                    Transport transport = session.getTransport("smtp");
	                    transport.connect(host, "servicetrasportipalermo", "");
	                    transport.sendMessage(message, message.getAllRecipients());
	                    transport.close();
	                }
	                catch (AddressException ae) {
	                    ae.printStackTrace();
	                }
	                catch (MessagingException me) {
	                    me.printStackTrace();
	                }
	        	}
	        }.start();
		 
		 return false;
	 }
	 public boolean sendCedolino(String file) {

        try {

			DataOutputStream out = new DataOutputStream(this.clientSocket.getOutputStream());
	        DataInputStream dis = new DataInputStream(new FileInputStream("bin/cedolini/"+file+".pdf"));
	        ByteArrayOutputStream ao = new ByteArrayOutputStream();
	        int read = 0;
	        byte[] buf = new byte[1024];
	        while ((read = dis.read(buf)) > -1) {
	            ao.write(buf, 0, read);
	        }
			out.write(ao.toByteArray());
	        out.flush();
	        out.close();
	        dis.close();
	        return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;

	}
}

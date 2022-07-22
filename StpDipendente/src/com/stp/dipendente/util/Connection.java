package com.stp.dipendente.util;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.stp.dipendente.model.Autobus;
import com.stp.dipendente.model.Cedolino;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.model.Fermata;
import com.stp.dipendente.model.Linea;
import com.stp.dipendente.model.Ruolo;
import com.stp.dipendente.model.Turno;
import com.stp.dipendente.util.json.JSONException;
import com.stp.dipendente.util.json.JSONObject;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;


public class Connection {
	private String serverAddress;
	private int port;
	private Socket commSocket;
	private BufferedReader input;
	private PrintWriter output;
	private JSONObject request;
	private Dipendente utente;
	private final String CONFFILE="connection.cnf";
	
	public Connection() throws UnknownHostException, IOException, ConnectionException {
		this(null);
	}
	public Connection(Dipendente utente) throws UnknownHostException, IOException, ConnectionException {
		try (InputStream in = this.getClass().getResourceAsStream("/"+CONFFILE);
			    BufferedReader reader =
			      new BufferedReader(new InputStreamReader(in))) {
			    String ip = reader.readLine();
			    if(ip==null || !ip.trim().substring(0, 2).toLowerCase().equals("ip")) {
			    	throw new ConnectionException("File di configurazione errato");
			    }
			    this.serverAddress= ip.substring(ip.indexOf("=")+1, ip.length()).trim();
			    
			    String port = reader.readLine();
			    if(port==null || !port.trim().substring(0, 4).toLowerCase().equals("port")) {
			    	throw new ConnectionException("File di configurazione errato");
			    }
			    this.port= Integer.parseInt(port.substring(port.indexOf("=")+1, port.length()).trim());
			} catch (IOException x) {
				throw new ConnectionException("File di configurazione mancante");
			} catch(NumberFormatException e) {
				throw new ConnectionException("File di configurazione errato");
			}
		
		this.request=new JSONObject();
		this.commSocket = new Socket(this.serverAddress, this.port);
		this.input = new BufferedReader(new InputStreamReader(commSocket.getInputStream()));
		this.output = new PrintWriter(commSocket.getOutputStream(), true);
		this.utente= utente;
	}
	private void send(String s) throws IOException{
		output.println(s);
	}
	private void send() throws IOException{
		this.send(this.request.toString());
	}
	private void sendLogged() throws IOException, ConnectionException {
		if(this.utente==null) {
			throw new ConnectionException("Devi effettuare il login per compiere questa azione");
		}
		this.request.put("nick", this.utente.getUsername());
		this.request.put("password", this.utente.getPassword());
		this.send();
	}

	public Dipendente login(String nick, String pass) throws IOException, JSONException, DipendenteException, ConnectionException{
		this.request.put("action", 0);
		this.request.put("nick", nick);
		this.request.put("password", pass);
		this.send();
		JSONObject result=new JSONObject(this.input.readLine());
		Dipendente utente=null;
		System.out.println(result);
		if(result.getInt("code")==-2) {
			throw new ConnectionException("Nome utente o password errati").setErrorCode(-2);
		}else if(result.getInt("code")==-3) {
			throw new ConnectionException("Stato di primo accesso").setErrorCode(-3);
		}else if(result.getInt("code")==-4) {
			throw new ConnectionException("Account bloccato contattare l'amministratore").setErrorCode(-4);
		}else if(result.getInt("code")==-5) {
			throw new ConnectionException("Non hai i permessi per accedere all'area riservata").setErrorCode(-5);
		}else if(result.getInt("code")==1){
			result.put("Password", pass);
			utente= new Dipendente(result);

			DataInputStream din=new DataInputStream(this.commSocket.getInputStream());
			byte b[]=new byte [1024];
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			long bytesRead;
			do{
				bytesRead = din.read(b, 0, b.length);
				baos.write(b,0,b.length);
			}while(!(bytesRead<1024));
			

			byte[] imageInByte = baos.toByteArray();
			WritableImage image = new WritableImage(510, 540);
	        try {
	        	
	            ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
	            
	            BufferedImage read = ImageIO.read(bis);
	            image = SwingFXUtils.toFXImage(read, null);
	            utente.setImage(image);
	        } catch (Exception ex) {
	           
	        }
			return utente;
		}
		return null;
	}
	
	public boolean cambiaPassword(String nick, String vecchia, String nuova) throws IOException {
		this.request.put("action", 14);
		this.request.put("Username", nick);
		this.request.put("Vecchia", vecchia);
		this.request.put("Nuova", nuova);
		this.send();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1) {
			return true;
		}
		return false;
	}
	
	
	public boolean timbroCartellino() throws IOException, ConnectionException {
		this.request.put("action", 3);
		this.request.put("CF", utente.getCodiceFiscale());
		this.sendLogged();
		
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			return true;
		}
		return false;
	}
	public ArrayList<Turno> getTurni(Dipendente dipendente, LocalDateTime start, LocalDateTime end)throws IOException, JSONException, TurnoException, ConnectionException {
		this.request.put("action", 4);
		this.request.put("CodiceFiscale", dipendente.getCodiceFiscale());
		this.request.put("start", start);
		this.request.put("end", end);
		this.sendLogged();
		ArrayList<Turno> res= new ArrayList<Turno>();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
				if(result.isNull("RefIdLinea")) {
					res.add(new Turno(result.getString("Giorno"), result.getString("OraInizioTurno"), result.getString("OraFineTurno")));
				}else {
					res.add(new Turno(result.getString("Giorno"), result.getString("OraInizioTurno"), result.getString("OraFineTurno"),  Integer.parseInt(result.getString("RefIdLinea"))));
				}
				
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	public boolean aggiungiFermata(Fermata fermata) throws IOException, ConnectionException {
		this.request.put("action", 26);
		this.request.put("Indirizzo", fermata.getIndirizzo());
		this.request.put("NumeroCivico", fermata.getNumeroCivico());
		this.request.put("x", fermata.getCoordinataX());
		this.request.put("y", fermata.getCoordinataY());
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1) {
			return true;
		}else if(result.getInt("code")==-2){
			throw new ConnectionException("Fermata già esistente").setErrorCode(-2);
		}
		return false;

	}
	public boolean rimuoviFermata(Fermata fermata) throws IOException, ConnectionException {
		this.request.put("action", 21);
		this.request.put("idFermata", fermata.getIdFermata());
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			return true;
		}
		
		return false;
	}
	public boolean rimuoviLinea(Linea linea) throws IOException, ConnectionException {
		this.request.put("action", 20);
		this.request.put("idLinea", linea.getIdLinea());
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			return true;
		}
		return false;
	}
	
	
	public ArrayList<Fermata> getFermate() throws IOException, JSONException, ConnectionException{
		this.request.put("action", 7);
		this.sendLogged();
		ArrayList<Fermata> res= new ArrayList<Fermata>();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
				res.add(new Fermata(result.getInt("idFermata"), result.getString("Indirizzo"), result.getString("NumeroCivico"), result.getDouble("CoordinataX"),result.getDouble("CoordinataY")));
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	public ArrayList<Fermata> cercaFermateElenco(String ricerca) throws IOException, JSONException, ConnectionException{
		this.request.put("action", 35);
		this.request.put("ricerca", ricerca);
		this.sendLogged();
		ArrayList<Fermata> res= new ArrayList<Fermata>();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
				res.add(new Fermata(result.getInt("idFermata"), result.getString("Indirizzo"), result.getString("NumeroCivico"), result.getDouble("CoordinataX"),result.getDouble("CoordinataY")));
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	
	public ArrayList<Dipendente> getDipendenti(int filtro, int disponibili) throws IOException, JSONException, DipendenteException, ConnectionException{
		this.request.put("action", 5);
		this.request.put("filtro", filtro);
		this.request.put("disponibili", disponibili);
		this.sendLogged();
		ArrayList<Dipendente> res= new ArrayList<Dipendente>();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
				result.put("StatoCartellino", false);
				res.add(new Dipendente(result));
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	public ArrayList<Dipendente> getDipendenti() throws IOException, JSONException, DipendenteException, ConnectionException{
		return this.getDipendenti(0, 0);
	}
	public WritableImage getFotoProfilo(Dipendente dipendente) throws IOException, JSONException, DipendenteException, URISyntaxException, ConnectionException{
		this.request.put("action", 36);
		this.request.put("foto", dipendente.getFoto());
		this.sendLogged();
		WritableImage image = new WritableImage(480, 510);
		try {
			DataInputStream din=new DataInputStream(this.commSocket.getInputStream());
			byte b[]=new byte [1024];
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			long bytesRead;
			do{
				bytesRead = din.read(b, 0, b.length);
				baos.write(b,0,b.length);
			}while(!(bytesRead<1024));
			
	
			byte[] imageInByte = baos.toByteArray();
			

            ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
            
            BufferedImage read = ImageIO.read(bis);
            image = SwingFXUtils.toFXImage(read, null);
            return image;
        } catch (Exception ex) {
        	
        	File f = new File(this.getClass().getResource("/img/default.png").toURI());
        	byte[] data = Files.readAllBytes(f.toPath());
        	ByteArrayInputStream bis = new ByteArrayInputStream(data);
            
            BufferedImage read = ImageIO.read(bis);
            image = SwingFXUtils.toFXImage(read, null);
            return image;
        }
	}
	public ArrayList<Dipendente> cercaDipendenti(String text, int filtro, int disponibili) throws IOException, JSONException, DipendenteException, ConnectionException{
		this.request.put("action", 37);
		this.request.put("ricerca", text);
		this.request.put("filtro", filtro);
		this.request.put("disponibili", disponibili);
		this.sendLogged();
		ArrayList<Dipendente> res= new ArrayList<Dipendente>();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
				result.put("StatoCartellino", false);
				res.add(new Dipendente(result));
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	public ArrayList<Dipendente> cercaDipendenti(String text) throws IOException, JSONException, DipendenteException, ConnectionException{
		return this.cercaDipendenti(text, 0, 0);
	}
	public ArrayList<Ruolo> getRuoli() throws JSONException, IOException, ConnectionException{
		this.request.put("action", 38);
		this.sendLogged();
		ArrayList<Ruolo> res= new ArrayList<Ruolo>();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
				res.add(new Ruolo(result.getInt("idRuolo"), result.getString("NomeRuolo"), result.getDouble("RetribuzioneOraria"), result.getInt("OreSettimanali")));
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	public JSONObject addDipendente(Dipendente dipendente, String extension) throws IOException, ConnectionException {
		this.request.put("action", 31);
		this.request.put("CodiceFiscale", dipendente.getCodiceFiscale());
		this.request.put("Nome", dipendente.getNome());
		this.request.put("Cognome", dipendente.getCognome());
		this.request.put("DataDiNascita", dipendente.getDataNascita());
		this.request.put("Sesso", dipendente.getSesso());
		this.request.put("LuogoDiNascita", dipendente.getLuogoDiNascita());
		this.request.put("ProvinciaDiNascita", dipendente.getProvinciaDiNascita());
		this.request.put("ComuneDiResidenza", dipendente.getComuneDiResidenza());
		this.request.put("ProvinciaDiResidenza", dipendente.getProvinciaDiResidenza());
		this.request.put("CAP", dipendente.getCAP());
		this.request.put("Email", dipendente.getEmail());
		this.request.put("RecapitoTelefonico", dipendente.getRecapitoTelefonico());
		this.request.put("IndirizzoDiResidenza", dipendente.getIndirizzoResidenza());
		this.request.put("RefIdRuolo", dipendente.getRuolo().getIdRuolo());
		this.request.put("ContoCorrente", dipendente.getContoCorrente());
		if(dipendente.getImage()!=null) {
			this.request.put("Foto", true);
			this.request.put("ext", extension);
		}
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		System.out.println("dsf");
		System.out.println(result);
		System.out.println("dsf");
		if(result.getInt("code")==1) {
			if(dipendente.getImage()!=null) {
				this.sendImage(dipendente.getImage(), extension);
			}
		}else {
			if(result.getInt("code")==-2) {
				throw new ConnectionException("Dipendente già presente.");
			}
		}
		
		return result;
	}
	private void sendImage(Image image, String extension) {
		File outputFile=null;
	    try {
	    	outputFile = new File("file."+extension);
		    BufferedImage bImage = SwingFXUtils.fromFXImage(image, null);
		    ImageIO.write(bImage, extension, outputFile); 
	    } catch (Exception e) {
	    	return;
	    }
	    try {
			DataOutputStream dout=new DataOutputStream(this.commSocket.getOutputStream());
			dout.flush();

			dout.flush();
			FileInputStream fin=new FileInputStream(outputFile);

			byte b[]=new byte [1024];
			int read;
			while((read = fin.read(b)) != -1){
			    dout.write(b, 0, read); 
			    dout.flush();
			}
			fin.close();

			dout.flush();
			Files.deleteIfExists(outputFile.toPath());
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public boolean updateTurni(Dipendente dip, Turno turno) throws IOException, ConnectionException {
		this.request.put("action", 17);
		LocalDateTime d1 = LocalDateTime.ofInstant(turno.getGiorno().toInstant(), ZoneId.systemDefault());
		LocalDateTime d2 = LocalDateTime.ofInstant(turno.getOraInizio().toInstant(), ZoneId.systemDefault());
		LocalDateTime d3 = LocalDateTime.ofInstant(turno.getOraFine().toInstant(), ZoneId.systemDefault());
		this.request.put("CF", dip.getCodiceFiscale());
		this.request.put("Giorno", d1.toString().substring(0, 10));
		this.request.put("OraInizioTurno", d2.toString().substring(11,16));
		this.request.put("OraFineTurno", d3.toString().substring(11, 16));
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			return true;
		}
		return false;
	}
	
	
	public boolean deleteTurni(Dipendente dip, Turno turno) throws IOException, ConnectionException {
		this.request.put("action", 18);
		LocalDateTime d1 = LocalDateTime.ofInstant(turno.getGiorno().toInstant(), ZoneId.systemDefault());
		LocalDateTime d2 = LocalDateTime.ofInstant(turno.getOraInizio().toInstant(), ZoneId.systemDefault());
		LocalDateTime d3 = LocalDateTime.ofInstant(turno.getOraFine().toInstant(), ZoneId.systemDefault());
		this.request.put("CF", dip.getCodiceFiscale());
		this.request.put("Giorno", d1.toString().substring(0, 10));
		this.request.put("OraInizioTurno", d2.toString().substring(11,16));
		this.request.put("OraFineTurno", d3.toString().substring(11, 16));
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			return true;
		}
		return false;
	}

	public boolean sendCedolino(String file, Dipendente dipendente, int oreLavorate, int oreMalattia, int oreFerie, double totale) throws IOException, ConnectionException {
		this.request.put("action", 27);
		this.request.put("CodiceFiscale", dipendente.getCodiceFiscale());
		this.request.put("OreMensili", dipendente.getRuolo().getOreSettimanli()*4);
		this.request.put("OreLavorative", oreLavorate);
		this.request.put("OreMalattia", oreMalattia);
		this.request.put("OreFerie", oreFerie);
		this.request.put("Totale", totale);
		this.sendLogged();
		JSONObject response=new JSONObject(this.input.readLine());
		if(response.getInt("code")==1) {
			DataOutputStream out = new DataOutputStream(this.commSocket.getOutputStream());
	        DataInputStream dis = new DataInputStream(new FileInputStream(file));
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
	        File fileStip= new File(file);
	        
	        Files.deleteIfExists(fileStip.toPath());
	        
		}
		return true;
	}
	
	public JSONObject editDipendente(JSONObject json, String extension, Image img) throws IOException, ConnectionException {
		this.request=json;
		this.request.put("action", 15);
		
		this.sendLogged();
		System.out.println(this.request);
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1) {
			if(img!=null) {
				this.sendImage(img, extension);
			}
		}
		return result;
	}
	public ArrayList<Autobus> getAutobus() throws IOException, JSONException, ConnectionException{
		this.request.put("action", 11);
		this.sendLogged();
		ArrayList<Autobus> res= new ArrayList<Autobus>();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
				result.put("StatoCartellino", false);
				res.add(new Autobus(result.getString("Targa"),
						result.getInt("PostiSeduti"),
						result.getInt("PostiInPiedi"),
						result.getInt("PostiPerDisabili"),
						Autobus.PresenzaAutobus.presenza(result.getInt("StatoPresenza")),
						Autobus.StatoAutobus.stato(result.getInt("StatoGuasto")),
						(result.isNull("DescrizioneGuasto"))?null:result.getString("DescrizioneGuasto")));
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	public ArrayList<Autobus> cercaAutobus(String text) throws IOException, JSONException, ConnectionException{
		this.request.put("action", 12);
		this.request.put("ricerca", text);
		this.sendLogged();
		ArrayList<Autobus> res= new ArrayList<Autobus>();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
				result.put("StatoCartellino", false);
				res.add(new Autobus(result.getString("Targa"),
						result.getInt("PostiSeduti"),
						result.getInt("PostiInPiedi"),
						result.getInt("PostiPerDisabili"),
						Autobus.PresenzaAutobus.presenza(result.getInt("StatoPresenza")),
						Autobus.StatoAutobus.stato(result.getInt("StatoGuasto")),
						(result.isNull("DescrizioneGuasto"))?null:result.getString("DescrizioneGuasto")));
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	public int registraUscita(Dipendente dipendente, Autobus autobus) throws IOException, ConnectionException {
		this.request.put("action", 40);
		this.request.put("Targa", autobus.getTarga());
		this.request.put("CF", dipendente.getCodiceFiscale());
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());

		return result.getInt("code");
	}
	public boolean registraEntrata(Autobus autobus) throws IOException, ConnectionException {
		this.request.put("action", 41);
		this.request.put("Targa", autobus.getTarga());
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			return true;
		}
		return false;
	}
	public int addAutobus(Autobus autobus) throws IOException, ConnectionException {
		this.request.put("action", 32);
		this.request.put("Targa", autobus.getTarga());
		this.request.put("PostiInPiedi", autobus.getPostiInPiedi());
		this.request.put("PostiSeduti", autobus.getPostiSeduti());
		this.request.put("PostiPerDisabili", autobus.getPostiPerDisabili());
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		
		return result.getInt("code");
	}
	public boolean eliminaAutobus(Autobus autobus) throws IOException, ConnectionException {
		this.request.put("action", 24);
		this.request.put("Targa", autobus.getTarga());
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());

		if(result.getInt("code")==1){
			return true;
		}
		return false;
	}
	public boolean setStatoGuasto(Autobus autobus) throws IOException, ConnectionException {
		this.request.put("action", 19);
		this.request.put("Targa", autobus.getTarga());
		this.request.put("StatoGuasto", autobus.getStatoGuasto().getCodice());
		if(autobus.getStatoGuasto().getCodice()==3) {
			this.request.put("DescrizioneGuasto", autobus.getDescrizioneGuasto());
		}
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());

		if(result.getInt("code")==1){
			return true;
		}
		return false;
	}
	public ArrayList<Linea> getLinee() throws IOException, JSONException, LineaException, ConnectionException{
		this.request.put("action", 9);
		this.sendLogged();
		ArrayList<Linea> res= new ArrayList<Linea>();
		JSONObject result=new JSONObject(this.input.readLine());
		Linea tmp;
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
				if(result.isNull("OraInizioCorsaFestivo") ||
					result.isNull("OraFineCorsaFestivo") ||
					result.isNull("DurataFestivo") ||
					result.isNull("NumeroBusConsigliatoFestivo")) {
					
					tmp=new Linea(result.getInt("idLinea"),
							result.getString("OraInizioCorsa"),
							result.getString("OraFineCorsa"),
							result.getString("Durata"),
							result.getInt("NumeroBusConsigliato"),
							null, null
						);
						
				}else {
					tmp=new Linea(result.getInt("idLinea"),
							result.getString("OraInizioCorsa"),
							result.getString("OraFineCorsa"),
							result.getString("Durata"),
							result.getInt("NumeroBusConsigliato"),
							result.getString("OraInizioCorsaFestivo"),
							result.getString("OraFineCorsaFestivo"),
							result.getString("DurataFestivo"),
							result.getInt("NumeroBusConsigliatoFestivo"),
							null, null
						);
					
				}
				if(!(result.isNull("idFermataA") || result.isNull("IndirizzoA") ||
						result.isNull("NumeroCivicoA") ||result.isNull("CoordinataXA") ||
						result.isNull("CoordinataYA"))) {
					tmp.setCapA(new Fermata(result.getInt("idFermataA"), result.getString("IndirizzoA"),
							result.getString("NumeroCivicoA"), result.getDouble("CoordinataXA"),
							result.getDouble("CoordinataYA")));
				}
				
				if(!(result.isNull("idFermataB") || result.isNull("IndirizzoB") ||
						result.isNull("NumeroCivicoB") ||result.isNull("CoordinataXB") ||
						result.isNull("CoordinataYB"))) {
					tmp.setCapB(new Fermata(result.getInt("idFermataB"), result.getString("IndirizzoB"),
							result.getString("NumeroCivicoB"), result.getDouble("CoordinataXB"),
							result.getDouble("CoordinataYB")));
				}
				

				
				res.add(tmp);
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	public int aggiungiLinea(Linea linea) throws IOException, ConnectionException {
		if(linea!=null) {
			this.request.put("action", 25);
			this.request.put("idLinea", linea.getIdLinea());
			this.request.put("OraInizioCorsa", linea.getOraInizioCorsa());
			this.request.put("OraFineCorsa", linea.getOraFineCorsa());
			this.request.put("Durata", linea.getDurata());
			this.request.put("NumeroBusConsigliato", linea.getnBusConsigliato());
			if(!(linea.getOraInizioCorsaFestivo()==null || linea.getOraFineCorsaFestivo()==null ||
					linea.getDurataFestivo()==null)) {
					this.request.put("OraInizioCorsaFestivo", linea.getOraInizioCorsaFestivo());
					this.request.put("OraFineCorsaFestivo", linea.getOraFineCorsaFestivo());
					this.request.put("DurataFestivo", linea.getDurataFestivo());
					this.request.put("NumeroBusConsigliatoFestivo", linea.getnBusConsigliatoFestivo());
				
			}
			this.sendLogged();
			
			return new JSONObject(this.input.readLine()).getInt("code");
		}
		return -1;
	}
	public int associaAutista(Dipendente dipendente, Turno turno,Linea linea) throws IOException, ConnectionException {
		if(linea!=null && dipendente!=null && turno!=null) {
			LocalDateTime d1 = LocalDateTime.ofInstant(turno.getGiorno().toInstant(), ZoneId.systemDefault());
			LocalDateTime d2 = LocalDateTime.ofInstant(turno.getOraInizio().toInstant(), ZoneId.systemDefault());
			LocalDateTime d3 = LocalDateTime.ofInstant(turno.getOraFine().toInstant(), ZoneId.systemDefault());
			this.request.put("action", 28);
			this.request.put("RefIdLinea", linea.getIdLinea());
			this.request.put("RefCodiceFiscale", dipendente.getCodiceFiscale());
			this.request.put("Giorno", d1.toString().substring(0, 10));
			this.request.put("OraInizioTurno", d2.toString().substring(11,16));
			this.request.put("OraFineTurno", d3.toString().substring(11, 16));
			this.sendLogged();
			
			return new JSONObject(this.input.readLine()).getInt("code");
		}
		return -1;
	}
	public int rimuoviAssociaAutista(Dipendente dipendente, Turno turno) throws IOException, ConnectionException {
		if(dipendente!=null && turno!=null) {
			LocalDateTime d1 = LocalDateTime.ofInstant(turno.getGiorno().toInstant(), ZoneId.systemDefault());
			LocalDateTime d2 = LocalDateTime.ofInstant(turno.getOraInizio().toInstant(), ZoneId.systemDefault());
			LocalDateTime d3 = LocalDateTime.ofInstant(turno.getOraFine().toInstant(), ZoneId.systemDefault());
			this.request=new JSONObject();
			this.request.put("action", 48);
			this.request.put("RefCodiceFiscale", dipendente.getCodiceFiscale());
			this.request.put("Giorno", d1.toString().substring(0, 10));
			this.request.put("OraInizioTurno", d2.toString().substring(11,16));
			this.request.put("OraFineTurno", d3.toString().substring(11, 16));
			this.sendLogged();
			
			return new JSONObject(this.input.readLine()).getInt("code");
		}
		return -1;
	}
	
	public ArrayList<Fermata> getFermateLinea(Linea linea) throws IOException, JSONException, ConnectionException{
		this.request.put("action", 30);
		this.request.put("idLinea", linea.getIdLinea());
		this.sendLogged();
		ArrayList<Fermata> res= new ArrayList<Fermata>();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
				res.add(new Fermata(result.getInt("idFermata"), result.getString("Indirizzo"), result.getString("NumeroCivico"), result.getDouble("CoordinataX"),result.getDouble("CoordinataY")));
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	
	public boolean pulisciFermateLinea(Linea linea) throws IOException, JSONException, ConnectionException{
		this.request.put("action", 42);
		this.request.put("idLinea", linea.getIdLinea());
		this.sendLogged();

		JSONObject result=new JSONObject(this.input.readLine());
		System.out.println(result);
		if(result.getInt("code")==1){
			return true;
		}
		return false;
	}
	public boolean aggiungiFermataLinea(Linea linea, Fermata fermata, int index) throws IOException, JSONException, ConnectionException{
		this.request.put("action", 29);
		this.request.put("idLinea", linea.getIdLinea());
		this.request.put("idFermata", fermata.getIdFermata());
		this.request.put("posizione", index);
		this.sendLogged();
		System.out.println(this.request);
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			return true;
		}
		return false;
	}
	public JSONObject getInfoCedolino(Dipendente dipendente) throws IOException, ConnectionException{
		this.request.put("action", 43);
		this.request.put("CodiceFiscale", dipendente.getCodiceFiscale());
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		
		return result;
	}
	
	public boolean licenziaDipendente(Dipendente dipendente) throws IOException, ConnectionException {

		this.request.put("action", 46);
		this.request.put("CodiceFiscale", dipendente.getCodiceFiscale());
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1) {
			return true;
		}
		return false;
	}
	public boolean riassumiDipendente(Dipendente dipendente) throws IOException, ConnectionException {
		this.request.put("action", 52);
		this.request.put("CodiceFiscale", dipendente.getCodiceFiscale());
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1) {
			return true;
		}
		return false;
	}
	
	public boolean reimpostaPassword(Dipendente dipendente) throws IOException, ConnectionException {
		this.request.put("action", 16);
		this.request.put("CodiceFiscale", dipendente.getCodiceFiscale());
		this.request.put("Email", dipendente.getEmail());
		this.request.put("Username", dipendente.getUsername());
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1) {
			return true;
		}
		return false;
	}
	public boolean eliminaDipendente(Dipendente dipendente) throws IOException, ConnectionException {
		this.request.put("action", 23);
		this.request.put("CodiceFiscale", dipendente.getCodiceFiscale());
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1) {
			return true;
		}
		return false;
	}
	
	public ArrayList<Cedolino> getCedolini() throws JSONException, IOException, ConnectionException{
		this.request=new JSONObject();
		this.request.put("action", 1);
		this.request.put("CF", utente.getCodiceFiscale());
		this.sendLogged();
		ArrayList<Cedolino> res= new ArrayList<Cedolino>();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
				res.add(new Cedolino(result.getString("Data"), result.getInt("OreMensili"), result.getInt("OreLavorative"), result.getInt("OreMalattia"),result.getInt("OreFerie"),result.getDouble("TotalePagamento"),result.getInt("idCedolino"), result.getString("FileCedolino")));
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	
	public void emailCedolino(String file) throws JSONException, IOException, ConnectionException{
		this.request.put("action", 50);
		this.request.put("Cedolino", file);
		this.request.put("Email", utente.getEmail());
		this.sendLogged();
		
	}
	
	
	public ArrayList<Linea> cercaLineaElenco(String ricerca) throws IOException, JSONException, LineaException, ConnectionException{
		this.request.put("action", 47);
		this.request.put("ricerca", ricerca);
		this.sendLogged();
		ArrayList<Linea> res= new ArrayList<Linea>();
		Linea tmp;
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
				if(result.isNull("OraInizioCorsaFestivo") ||
						result.isNull("OraFineCorsaFestivo") ||
						result.isNull("DurataFestivo") ||
						result.isNull("NumeroBusConsigliatoFestivo")) {
						
						tmp=new Linea(result.getInt("idLinea"),
								result.getString("OraInizioCorsa"),
								result.getString("OraFineCorsa"),
								result.getString("Durata"),
								result.getInt("NumeroBusConsigliato"),
								null, null
							);
							
					}else {
						tmp=new Linea(result.getInt("idLinea"),
								result.getString("OraInizioCorsa"),
								result.getString("OraFineCorsa"),
								result.getString("Durata"),
								result.getInt("NumeroBusConsigliato"),
								result.getString("OraInizioCorsaFestivo"),
								result.getString("OraFineCorsaFestivo"),
								result.getString("DurataFestivo"),
								result.getInt("NumeroBusConsigliatoFestivo"),
								null, null
							);
						
					}
					if(!(result.isNull("idFermataA") || result.isNull("IndirizzoA") ||
							result.isNull("NumeroCivicoA") ||result.isNull("CoordinataXA") ||
							result.isNull("CoordinataYA"))) {
						tmp.setCapA(new Fermata(result.getInt("idFermataA"), result.getString("IndirizzoA"),
								result.getString("NumeroCivicoA"), result.getDouble("CoordinataXA"),
								result.getDouble("CoordinataYA")));
					}
					
					if(!(result.isNull("idFermataB") || result.isNull("IndirizzoB") ||
							result.isNull("NumeroCivicoB") ||result.isNull("CoordinataXB") ||
							result.isNull("CoordinataYB"))) {
						tmp.setCapB(new Fermata(result.getInt("idFermataB"), result.getString("IndirizzoB"),
								result.getString("NumeroCivicoB"), result.getDouble("CoordinataXB"),
								result.getDouble("CoordinataYB")));
					}
					

					
					res.add(tmp);
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	
	public boolean modificaLinea(Linea linea) throws IOException, ConnectionException {
		if(linea!=null) {
			this.request.put("action", 13);
			this.request.put("idLinea", linea.getIdLinea());
			this.request.put("OraInizioCorsa", linea.getOraInizioCorsa());
			this.request.put("OraFineCorsa", linea.getOraFineCorsa());
			this.request.put("Durata", linea.getDurata());
			this.request.put("NumeroBusConsigliato", linea.getnBusConsigliato());
			if(!(linea.getOraInizioCorsaFestivo()==null || linea.getOraFineCorsaFestivo()==null ||
					linea.getDurataFestivo()==null)) {
					this.request.put("OraInizioCorsaFestivo", linea.getOraInizioCorsaFestivo());
					this.request.put("OraFineCorsaFestivo", linea.getOraFineCorsaFestivo());
					this.request.put("DurataFestivo", linea.getDurataFestivo());
					this.request.put("NumeroBusConsigliatoFestivo", linea.getnBusConsigliatoFestivo());
				
			}
			this.sendLogged();
			
			if(new JSONObject(this.input.readLine()).getInt("code")==1) {
				return true;
			};
		}
		return false;
	}
	
	public boolean aggiungiCapolinea(Linea linea, Fermata fermata1, Fermata fermata2) throws IOException, JSONException, ConnectionException{

		this.request.put("action", 49);
		this.request.put("idLinea", linea.getIdLinea());
		if(fermata1!=null) {
			this.request.put("CapA", fermata1.getIdFermata());
		}
		if(fermata2!=null) {
			this.request.put("CapB", fermata2.getIdFermata());
		}
		this.sendLogged();
		JSONObject result=new JSONObject(this.input.readLine());
		if(result.getInt("code")==1){
			return true;
		}
		return false;
	}
	
	public ByteArrayOutputStream receivePdf(String file) throws JSONException, IOException, ConnectionException{
		this.request=new JSONObject();
		this.request.put("action", 51);
		this.request.put("Cedolino", file);
		this.sendLogged();
		
		try {
			DataInputStream dis = new DataInputStream(this.commSocket.getInputStream());

	        
	        byte[] buffer = new byte[1024];
	        int len;
			ByteArrayOutputStream baos=new ByteArrayOutputStream();

	        
	        while ((len = dis.read(buffer)) != -1) {
	        	baos.write(buffer, 0, len);
	        }
	        baos.flush();
	        dis.close();
	        return baos;
	    } catch (Exception ex) {
	    	ex.printStackTrace();

        }
		return null;
	}
	
	public void close(){
		try {
			this.input.close();
		}catch(IOException e) {
			
		}
		this.output.close();
		try {
			this.commSocket.close();
		}catch(IOException e) {
			
		}		
	}
}

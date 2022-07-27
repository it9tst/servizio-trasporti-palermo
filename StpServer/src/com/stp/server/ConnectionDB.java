package com.stp.server;

import java.security.SecureRandom;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;
import com.stp.server.util.RandomString;
import com.stp.server.util.json.JSONObject;


public class ConnectionDB {
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://localhost/stp?autoReconnect=true&useSSL=false";
	
	private static final String USER = "root";
	private static final String PASS = "";
	
	private Connection connection = null;
	private Statement statement = null;
	
	public ConnectionDB() throws ClassNotFoundException, SQLException{

		Class.forName(JDBC_DRIVER);
		this.connection = DriverManager.getConnection(DB_URL,USER,PASS);
		this.statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

	}

	
	public List <JSONObject> login(String nick, String pass){
		try{
			/*List <JSONObject> comuni = selectQuery("Select Comune, Provincia, CodFisco From comuni");
			try {
		        File file = new File("text.txt");
		        FileWriter fw = new FileWriter(file);
		        for(JSONObject t: comuni) {
			        fw.write("{nome:\""+t.getString("Comune")+"\", provincia: \""+t.getString("Provincia")+"\", codFisc: \""+t.getString("CodFisco")+"\"}\r\n");
					fw.flush();
		        }
		        fw.close();
		     
		    } catch (IOException e) {
		        e.printStackTrace();
		    }*/
			return selectQuery("SELECT * , rg.DataAssunzione, rg.DataLicenziamento FROM dipendente d, Ruolo r, registroassunzioni rg WHERE Username='"+nick+"' AND Password='"+pass+"' AND d.RefIdRuolo=r.idRuolo AND rg.RefCodiceFiscale=d.CodiceFiscale");
		}catch(SQLException e){
			// ricordati di aggiungere un sistema di log
			System.out.println("Errore esecuzione query login");
		}
		return null;
	}
	
	
	public List <JSONObject> visualizzaElencoCedolini(String CF){
		try{
			String sql="SELECT * FROM cedolino WHERE RefCodiceFiscale='"+CF+"'";
			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione elenco cedolini");
		}
		return null;
	}
	public List <JSONObject> visualizzaCedolino(String CF, int idCedolino){
		try{
			String sql="SELECT * FROM cedolino WHERE RefCodiceFiscale='"+CF+"'AND idCedolino='"+idCedolino+"'";
			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione visualizza cedolino");
		}
		return null;
	}
	public List <JSONObject> visualizzaElencoLinee(){
		try{
			String sql="SELECT idLinea, OraInizioCorsa, OraFineCorsa, Durata, NumeroBusConsigliato, OraInizioCorsaFestivo, OraFineCorsaFestivo, DurataFestivo, NumeroBusConsigliatoFestivo, f1.Indirizzo as IndirizzoA, f1.NumeroCivico as NumeroCivicoA, f1.idFermata as idFermataA, f1.CoordinataX as CoordinataXA, f1.CoordinataY as CoordinataYA, f2.Indirizzo as IndirizzoB, f2.NumeroCivico as NumeroCivicoB, f2.idFermata as idFermataB, f2.CoordinataX as CoordinataXB, f2.CoordinataY as CoordinataYB FROM linea l left join fermata f1 on l.CapA=f1.idFermata left join fermata f2 on l.CapB=f2.idFermata";
			return selectQuery(sql);
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("Errore esecuzione elenco linee");
		}
		return null;
	}
	public List <JSONObject> visualizzaLinea(int idLinea){
		try{
			String sql="SELECT * FROM linea WHERE idLinea='"+idLinea+"'";
			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione visualizza linea");
		}
		return null;
	}
	public List <JSONObject> visualizzaElencoFermate(){
		try{
			String sql="SELECT * FROM fermata";
			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione elenco fermate");
		}
		return null;
	}
	public List <JSONObject> cercaFermataElenco(String ricerca){
		try{
			String sql="SELECT * FROM fermata WHERE Indirizzo LIKE '%"+ricerca+"%'";
			
			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione elenco fermate");
		}
		return null;
	}
	public List <JSONObject> cercaLineaElenco(String ricerca){
		try{
			String sql="SELECT * FROM linea WHERE CONVERT(idLinea, CHAR(16)) LIKE '%"+ricerca+"%'";

			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione elenco fermate");
		}
		return null;
	}
	public List <JSONObject> visualizzaFermata(int idFermata){
		try{
			String sql="SELECT * FROM fermata WHERE idFermata='"+idFermata+"'";
			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione visualizza fermata");
		}
		return null;
	}
	public List <JSONObject> visualizzaElencoDipendenti(int filtro, int disponibili){
		try{
			String sql;
			if(filtro!=0) {
				if(disponibili!=0) {
					sql="SELECT * , rg.DataAssunzione, rg.DataLicenziamento  FROM dipendente d, Ruolo r, registroassunzioni rg WHERE r.idRuolo=d.RefIdRuolo AND rg.RefCodiceFiscale=d.CodiceFiscale AND r.idRuolo="+filtro+" AND d.CodiceFiscale IN (SELECT RefCodiceFiscale FROM turno WHERE Giorno='"+LocalDate.now()+"')";
					
				}else {
					sql="SELECT * , rg.DataAssunzione, rg.DataLicenziamento  FROM dipendente d, Ruolo r, registroassunzioni rg WHERE r.idRuolo=d.RefIdRuolo AND rg.RefCodiceFiscale=d.CodiceFiscale AND r.idRuolo="+filtro;
				}
			}else {
				sql="SELECT * , rg.DataAssunzione, rg.DataLicenziamento  FROM dipendente d, Ruolo r, registroassunzioni rg WHERE r.idRuolo=d.RefIdRuolo AND rg.RefCodiceFiscale=d.CodiceFiscale";
			}

			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione elenco dipendenti");
		}
		return null;
	}
	public List <JSONObject> visualizzaDipendente(String CF){
		try{
			String sql="SELECT * FROM dipendente WHERE CodiceFiscale='"+CF+"'";
			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione visualizza dipendente");
		}
		return null;
	}
	public List <JSONObject> visualizzaElencoAutobus(){
		try{
			String sql="SELECT * FROM autobus";
			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione elenco autobus");
		}
		return null;
	}
	public List <JSONObject> visualizzaAutobus(String Targa){
		try{
			String sql="SELECT * FROM autobus WHERE Targa LIKE '%"+Targa+"%'";

			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione visualizza autobus");
		}
		return null;
	}
	public List <JSONObject> visualizzaTurni(String CF, String start, String end){
		try{
			String sql="SELECT * FROM turno WHERE RefCodiceFiscale='"+CF+"' AND Giorno>='"+start+"' AND Giorno<='"+end+"'";

			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione visualizza turni");
		}
		return null;
	}	
	
	
	
	public boolean timbraCartellino(String CF){
		try {
			Time orario = new Time(System.currentTimeMillis());
			ResultSet result = this.executeSql("SELECT * FROM cartellino WHERE RefCodiceFiscale='"+CF+"' AND OrarioUscita IS NULL");
			if(result.next()) {
				result.updateTime("OrarioUscita", orario);
				result.updateRow();
				return true;
			}else {
				Date data = new Date(System.currentTimeMillis());
				HashMap<String, String> datasql = new HashMap<String, String>();
				datasql.put("RefCodiceFiscale",CF);
				datasql.put("Data",data.toString());
				datasql.put("OrarioEntrata", orario.toString());
				if(this.insertQuery("cartellino", datasql , null)) {
					return true;
				}

			}
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query timbro cartellino");
		}
		return false;
		
	}
	
	public boolean checkCartellino(String CF) {
		try {
			ResultSet result = this.executeSql("SELECT * FROM cartellino WHERE RefCodiceFiscale='"+CF+"' AND OrarioUscita IS NULL");
			if(result.next()) {
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	public boolean setOrarioLinea(int idLinea,String OraInizio, String OraFine, String Durata, int NumConsigliato, String OraInizioFestivo, String OraFineFestivo, String DurataFestivo,int NumConsigliatoFestivo){
		try {
			HashMap<String,String> datasql=new HashMap<>();
			HashMap<String,Integer> datasql1=new HashMap<>();
			datasql.put("OraInizioCorsa",OraInizio);
			datasql.put("OraFineCorsa",OraFine);
			datasql.put("Durata",Durata);
			datasql1.put("NumeroBusConsigliato",NumConsigliato);
			datasql.put("OraInizioCorsaFestivo",OraInizioFestivo);
			datasql.put("OraFineCorsaFestivo",OraFineFestivo);
			datasql.put("DurataFestivo",DurataFestivo);
			datasql1.put("NumeroBusConsigliatoFestivo",NumConsigliatoFestivo);
			if(this.updateQuery("linea",datasql,datasql1,"idLinea='"+idLinea+"'")) {
					return true;
			}
		}catch(SQLException e) {
			System.out.println("Errore esecuzione modifica orario linea");
		}
		return false;
	}
	
	
	public boolean setOrarioLinea(int idLinea,String OraInizio, String OraFine, String Durata, int NumConsigliato){
		try {
			HashMap<String,String> datasql=new HashMap<>();
			HashMap<String,Integer> datasql1=new HashMap<>();
			datasql.put("OraInizioCorsa",OraInizio);
			datasql.put("OraFineCorsa",OraFine);
			datasql.put("Durata",Durata);
			datasql1.put("NumeroBusConsigliato",NumConsigliato);
			if(this.updateQuery("linea",datasql,datasql1,"idLinea='"+idLinea+"'")) {
					if(this.updateSql("UPDATE linea SET OraInizioCorsaFestivo=NULL, OraFineCorsaFestivo=NULL, DurataFestivo=NULL, NumeroBusConsigliatoFestivo=NULL WHERE idLinea='"+idLinea+"'")) {
						return true;
					}
			}
		}catch(SQLException e) {
			System.out.println("Errore esecuzione modifica orario linea");
		}
		return false;
	}
	
	
	public boolean modificaDipendente(JSONObject json) {
		Iterator <String> cursore = json.keys();
		HashMap<String,String> data1 = new HashMap<>();
		HashMap<String,Integer> data2 = new HashMap<>();
		String corrente;
		try {
			while(cursore.hasNext()) {
				corrente = cursore.next();

				if(corrente.equals("nick") || corrente.equals("password") || corrente.equals("action") || corrente.equals("CF") || 
						corrente.equals("Foto") || corrente.equals("ext")) continue; //così non inserisce in mappa il mio nick e pass cosi non li modifica
				if(corrente.equals("CAP") || corrente.equals("PrimoAccesso") || corrente.equals("RefIdRuolo")) {
					data2.put(corrente, json.getInt(corrente));
				}
				else {
					data1.put(corrente, json.getString(corrente));
				}
			}
			return updateQuery("dipendente", data1, data2,"CodiceFiscale='"+json.getString("CF")+"'"); // attento se nel json lo chiama CF e anche gli altri parametri
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query modifica dipendente");
		}
		return false;
	}
	public boolean cambiaPassword(String username, String vecchia, String nuova) {
		
		HashMap<String,String> datasql = new HashMap<>();
		HashMap<String,Integer> datasql2 = new HashMap<>();
		try {
			datasql.put("Password", nuova);
			datasql2.put("PrimoAccesso", 0);
			return updateQuery("dipendente", datasql, datasql2," Username='"+username+"' AND Password='"+vecchia+"'");

		
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query cambia password");
		}
		return false;
	}
	public boolean reimpostaPassword(String CF, String email, String username) {
		HashMap<String,String> datasql = new HashMap<>();
		HashMap<String,Integer> datasql2 = new HashMap<>();
		try {
			datasql2.put("PrimoAccesso", 1);
			RandomString tickets = new RandomString(8, new SecureRandom(), CF);
			String pass=tickets.nextString();
			
				datasql.put("Password", ServerStp.cryptaPass(username,pass));
		boolean flag=updateQuery("dipendente", datasql, datasql2,"CodiceFiscale='"+CF+"'");
		if(flag) {

			String[] to = { email };
    		ThreadServer.sendFromGMail("servicetrasportipalermo", "", to, "Informazioni account", "Nuova password: "+pass);
		}
		
		return flag;
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query reimposta password");
		}
		return false;
	}
	
	public boolean aggiungiTurno(String CF, String Giorno, String OraInizioTurno, String OraFineTurno){
		try {
			HashMap<String,String> datasql=new HashMap<>();
			datasql.put("RefCodiceFiscale", CF);
			datasql.put("Giorno", Giorno);
			datasql.put("OraInizioTurno", OraInizioTurno);
			datasql.put("OraFineTurno", OraFineTurno);
			// RefIdLinea sarà associaAutistaALinea a settarlo di default è null
			return this.insertQuery("turno", datasql, null); 
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query aggiungi turno");
		}
		return false;
	}
	
	
	
	public boolean eliminaTurno(String CF, String Giorno){
		try {
			return this.deleteQuery("turno", "RefCodiceFiscale='"+CF+"' " + "AND " + "Giorno='"+Giorno+"'"); 
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query eliminazione turno");
		}
		return false;
	}
	
	
	public boolean gestisciGuasti(String Targa, int StatoGuasto, String DescrizioneGuasto ) {
		try {
			if(DescrizioneGuasto==null) {
				return this.updateSql("UPDATE autobus SET StatoGuasto="+StatoGuasto+", DescrizioneGuasto=NULL WHERE Targa='"+Targa+"'");
			}
			return this.updateSql("UPDATE autobus SET StatoGuasto="+StatoGuasto+", DescrizioneGuasto='"+DescrizioneGuasto+"' WHERE Targa='"+Targa+"'");
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query gestisci guasti");
		}
		return false;
	}
	
	
	public boolean eliminaLinea(int idLinea){
		Integer id = idLinea;
		try {
			return this.deleteQuery("linea", "idLinea='"+id.toString()+"'"); 
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query eliminazione linea");
		}
		return false;
	}
	
	public boolean eliminaFermata(int idFermata){
		Integer id = idFermata;
		try {
			return this.deleteQuery("fermata", "idFermata='"+id.toString()+"'"); 
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query eliminazione fermata");
		}
		return false;
	}
	
	
	public boolean eliminaFermataDaLinea(int idLinea, int idFermata){
		Integer id1 = idLinea;
		Integer id2 = idFermata;
		try {
			return this.deleteQuery("lineaassociafermata", "RefIdLinea="+id1.toString()+" " + "AND " + "RefIdFermata="+id2.toString()+""); 
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query eliminazione fermata da linea");
		}
		return false;
	}
	
	
	public boolean eliminaDipendente(String CF){
		try {
			return this.deleteQuery("dipendente", "CodiceFiscale='"+CF+"'"); 
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query eliminazione dipendente");
		}
		return false;
	}
	
	public boolean eliminaAutobus(String Targa){
		try {
			return this.deleteQuery("autobus", "Targa='"+Targa+"'"); 
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query eliminazione autobus");
		}
		return false;
	}
	
	
	public int creaLinea(int idLinea, String OraInizioCorsa, String OraFineCorsa, String Intervallo, int NumeroBusConsigliato, String OraInizioCorsaFestivo, String OraFineCorsaFestivo, String IntervalloFestivo, int NumeroBusConsigliatoFestivo){
		try{
			HashMap<String,String> data1=new HashMap<>();
			HashMap<String,Integer> data2=new HashMap<>();			
			data1.put("OraInizioCorsa", OraInizioCorsa);
			data1.put("OraFineCorsa", OraFineCorsa);
			data1.put("Durata", Intervallo);
			data1.put("OraInizioCorsaFestivo", OraInizioCorsaFestivo);
			data1.put("OraFineCorsaFestivo", OraFineCorsaFestivo);
			data1.put("DurataFestivo", IntervalloFestivo);	
			data2.put("idLinea", idLinea);
			data2.put("NumeroBusConsigliato", NumeroBusConsigliato);
			data2.put("NumeroBusConsigliatoFestivo", NumeroBusConsigliatoFestivo);
			if(insertQuery("linea",data1, data2)) {
				return 1;
			}
		}catch(SQLException e) {
			if(e.getErrorCode()==1062) {
				return -2;
			}
			System.out.println("Errore esecuzione query aggiungi linea");
		}
		return -1;
	}
	public int creaLinea(int idLinea, String OraInizioCorsa, String OraFineCorsa, String Intervallo, int NumeroBusConsigliato){
		try{
			HashMap<String,String> data1=new HashMap<>();
			HashMap<String,Integer> data2=new HashMap<>();			
			data1.put("OraInizioCorsa", OraInizioCorsa);
			data1.put("OraFineCorsa", OraFineCorsa);
			data1.put("Durata", Intervallo);
			data2.put("idLinea", idLinea);
			data2.put("NumeroBusConsigliato", NumeroBusConsigliato);
			if(insertQuery("linea",data1, data2)) {
				return 1;
			}
		}catch(SQLException e) {
			if(e.getErrorCode()==1062) {
				return -2;
			}
			System.out.println("Errore esecuzione query aggiungi linea");
		}
		return -1;
	}
	
	
	public int creaFermata(String Indirizzo, String NumeroCivico, double x, double y){
		try{
			HashMap<String,String> data1=new HashMap<>();
			HashMap<String,Double> data2=new HashMap<>();
			data1.put("Indirizzo", Indirizzo);
			data1.put("NumeroCivico", NumeroCivico);
			data2.put("CoordinataX", x);
			data2.put("CoordinataY", y);
			if(insertQuery("fermata",data1, null, data2)) {
				return 1;
			}
		}catch(SQLException e) {
			if(e.getErrorCode()==1062) {
				return -2;
			}
			System.out.println("Errore esecuzione query aggiungi fermata");
		}
		return -1;
	}
	
	
	public boolean compilaCedolino(String CF, String Data, int OreMensili, int OreLavorative, int OreMalattia, int OreFerie, double TotalePagamento, String FileCedolino){
		try{
			HashMap<String,String> data1=new HashMap<>();
			HashMap<String,Integer> data2=new HashMap<>();
			HashMap<String,Double> data3=new HashMap<>();
			data1.put("RefCodiceFiscale", CF);
			data1.put("Data", Data);
			data2.put("OreMensili", OreMensili);
			data2.put("OreLavorative", OreLavorative);
			data2.put("OreMalattia", OreMalattia);
			data2.put("OreFerie", OreFerie);
			data1.put("FileCedolino", FileCedolino);
			data3.put("TotalePagamento", TotalePagamento);
			 // lo setto intanto a 0, poi faccio una query specifica per trattare il valore double		
			return insertQuery("cedolino", data1, data2, data3);
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query compila cedolino");
		}
		return false;
	}
	
	
	public boolean associaAutistaALinea(String CF, String Giorno, int RefIdLinea, String OraInizioTurno, String OraFineTurno) {

		HashMap<String,Integer> data2 = new HashMap<>();
		try {
				data2.put("RefIdLinea", RefIdLinea);
		return updateQuery("turno", null, data2, "RefCodiceFiscale='"+CF+"' AND Giorno='"+Giorno+"' AND OraInizioTurno='"+OraInizioTurno+"' AND OraFineTurno='"+OraFineTurno+"'");
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query associa autista a linea");
		}
		return false;
	}
	public boolean rimuoviAssociaAutistaALinea(String CF, String Giorno, String OraInizioTurno, String OraFineTurno) {
		try {
				
				return updateSql("UPDATE turno SET RefIdLinea=NULL WHERE RefCodiceFiscale='"+CF+"' AND Giorno='"+Giorno+"' AND OraInizioTurno='"+OraInizioTurno+"' AND OraFineTurno='"+OraFineTurno+"'");
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query associa autista a linea");
		}
		return false;
	}
	
	public boolean aggiungiFermataALinea(int idLinea, int idFermata, int posizione){
		HashMap<String,Integer> datasql = new HashMap<>();
		datasql.put("RefIdLinea", idLinea);
		datasql.put("RefIdFermata", idFermata);
		datasql.put("Posizione", posizione);
		try {
			return insertQuery("lineaassociafermata", null , datasql); 
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query aggiungi fermata a linea");
		}
		return false;
	}
	public boolean aggiungiCapolineaaALinea(int idLinea, int idFermata1, int idFermata2){
		HashMap<String,Integer> datasql = new HashMap<>();
		if(idFermata1>-1) {
			datasql.put("CapA", idFermata1);
		}
		if(idFermata2>-1) {
			datasql.put("CapB", idFermata2);
		}
		try {
			return updateSql("UPDATE linea SET CapA="+ ((idFermata1>-1)?idFermata1:"NULL")+", CapB="+ ((idFermata2>-1)?idFermata2:"NULL")+" WHERE idLinea="+idLinea); 
		}catch(SQLException e) {
			e.printStackTrace();
			System.out.println("Errore esecuzione query aggiungi capolinea a linea");
		}
		return false;
	}
	
	public int aggiungiDipendente(String CF, String Nome, String Cognome, String DataDiNascita, String Sesso, String LuogoDiNascita, String ProvinciaDiNascita,
			String ComuneDiResidenza, String ProvinciaDiResidenza, int CAP, String Email, String RecapitoTelefonico, String ContoCorrente
			, String IndirizzoDiResidenza, int RefIdRuolo){	
		ResultSet res;
		Integer count = 0;
		try{
			HashMap<String,String> data1=new HashMap<>();
			HashMap<String,Integer> data2=new HashMap<>();
			res = executeSql("SELECT * FROM dipendente WHERE " + "Nome='"+Nome+"'" + "AND " + "Cognome='"+Cognome+"'" );			
			while(res.next()) count++;
			
			
			RandomString tickets = new RandomString(8, new SecureRandom(), CF);
			String pass=tickets.nextString();
			data1.put("CodiceFiscale", CF);
			data1.put("Nome", Nome);
			data1.put("Cognome", Cognome);
			data1.put("DataDiNascita", DataDiNascita);
			data1.put("Sesso", Sesso);
			data1.put("LuogoDiNascita", LuogoDiNascita);
			data1.put("ProvinciaDiNascita", ProvinciaDiNascita);
			data1.put("ComuneDiResidenza", ComuneDiResidenza);
			data1.put("ProvinciaDiResidenza", ProvinciaDiResidenza);
			data1.put("Email", Email);
			data1.put("RecapitoTelefonico", RecapitoTelefonico);
			data1.put("ContoCorrente", ContoCorrente);
			data1.put("Password", ServerStp.cryptaPass((Nome + "." + Cognome + (count!=0 ? count:"")).toLowerCase().replace(" ", ""), pass)); //genera una passoword random e la crypta
			data1.put("IndirizzoDiResidenza", IndirizzoDiResidenza);
			data1.put("Username", (Nome + "." + Cognome + (count!=0 ? count:"")).toLowerCase().replace(" ", "") ); // setta come username nome.cognome, se ci sono omonimi mette un numero 
			data2.put("CAP", CAP);
			data2.put("RefIdRuolo", RefIdRuolo);
			boolean flag=insertQuery("dipendente", data1, data2);
			if(flag) {
				data1=new HashMap<>();
				data1.put("RefCodiceFiscale", CF);
				data1.put("DataAssunzione", LocalDate.now().toString());
				insertQuery("registroassunzioni", data1, null);
				String[] to = { Email };
        		ThreadServer.sendFromGMail("servicetrasportipalermo", "", to, "Informazioni account", "Nick: "+(Nome + "." + Cognome + (count!=0 ? count:"")).toLowerCase().replace(" ", "")+"\r\nPassword: "+pass);
			}
			return 1;
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query aggiungi dipendente");
			if(e.getErrorCode()==1062) {
				return -2;
			}
				
			
		}
		return -1;
	}
	
	

	
	
	
	public int aggiungiAutobus(String Targa, int PostiSeduti, int PostiInPiedi, int PostiPerDisabili){
		try{
			HashMap<String,String> data1=new HashMap<>();
			HashMap<String,Integer> data2=new HashMap<>();
			data1.put("Targa", Targa);
			data2.put("PostiSeduti", PostiSeduti);
			data2.put("PostiInPiedi", PostiInPiedi);
			data2.put("PostiPerDisabili", PostiPerDisabili);
			data2.put("StatoPresenza", 1); // all'atto dell'inserimento un autobus è funzionante e presente
			data2.put("StatoGuasto", 1);
			if(insertQuery("autobus", data1, data2)) {
				return 1;
			}
		}catch(SQLException e) {
			if(e.getErrorCode()==1062){
				return -2;
			}
			System.out.println("Errore esecuzione aggiungi autobus");
		}
		return -1;
	}
	public List <JSONObject> ricercaFermateLinea(int idLinea){
		try{
			String sql="SELECT f.* FROM lineaassociafermata laf,fermata f WHERE laf.refIdLinea='"+idLinea+"'AND laf.refIdFermata=f.idFermata ORDER BY Posizione ASC";
			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione ricerca Fermate linea");
		}
		return null;
	}
	public List <JSONObject> ricercaLineaCittadino(int idLinea) {
		List <JSONObject> fermate = this.ricercaFermateLinea(idLinea);
		
		
		return fermate;
		}
		
	public List <JSONObject> cercaFermateCittadino(double lat, double lon, double radius){
		try{
			
			String sql="SELECT * FROM fermata WHERE lat_lng_distance(CoordinataX, CoordinataY , "+lat+", "+lon+")<"+radius;
			//"SELECT *, min(latlong_distance(CoordinataX, CoordinataY ,  "+lat+", "+lon+"))  FROM fermata";
			
			
			
			List <JSONObject> fermate=selectQuery(sql);
			List <JSONObject> linee;
			int index = 0;
			for(JSONObject j: fermate) {
				linee=selectQuery("SELECT RefIdLinea FROM lineaassociafermata WHERE RefIdFermata="+j.getInt("idFermata"));
				String s="";
				if(linee!=null) {
					for(JSONObject l: linee) {
						s=s+l.getInt("RefIdLinea")+"-";
					}
					s=s.substring(0, s.length()-1);
				}
				
				
				fermate.get(index++).put("Linee", s);
			}
			return fermate;
		}catch(SQLException e){
			System.out.println("Errore esecuzione ricerca fermate cittadino");
		}
		return null;
	}
	
	
	
	public List <JSONObject> cercaFermataPiuVicina(double lat, double lon){
		try{
			String sql="SELECT * FROM fermata where lat_lng_distance(CoordinataX, CoordinataY ,  "+lat+", "+lon+") <= all (SELECT lat_lng_distance(CoordinataX, CoordinataY ,  "+lat+", "+lon+") FROM fermata)";
			return selectQuery(sql);
			}catch(SQLException e){
			System.out.println("Errore esecuzione ricerca fermata più vicina");
		}
		return null;
	}
	public List <JSONObject> cercaLineeFermata(int codiceFermata){
			try{
				String sql="SELECT RefIdLinea as idLinea FROM lineaassociafermata WHERE RefIdFermata='"+codiceFermata+"'";
				List<JSONObject> lineeFermata=selectQuery(sql);
				List<JSONObject> res=new ArrayList<>();
				for(JSONObject linea :lineeFermata) {
					res.add(this.ricercaLineaCittadino(linea.getInt("idLinea")).get(0));
				}
				return res;
				}catch(SQLException e){
				System.out.println("Errore esecuzione query cercaLineFermata");
			}
			return null;
	}
	public List<JSONObject> calcolaPercorso(double lat1, double lon1, double lat2,double lon2){
		
		try {
			JSONObject fermataPartenza=this.cercaFermataPiuVicina(lat1,lon1).get(0);
			JSONObject fermataArrivo=this.cercaFermataPiuVicina(lat2,lon2).get(0);
			if(fermataPartenza.getInt("idFermata")==fermataArrivo.getInt("idFermata")) {
				List<JSONObject> l= new ArrayList<JSONObject>();
				l.add(new JSONObject("{code:-2}"));
				return l;
			}
			return selectQuery("call dijkastra("+fermataPartenza.getInt("idFermata")+", "+fermataArrivo.getInt("idFermata")+")");
			
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query calcolaPercorso");
		}
		return null;
		
	}

	public List<JSONObject> calcolaPercorsoADueVie(int idFermataPartenza, int idFermataArrivo){
		try {
			 String where="laf1.RefIdFermata='"+idFermataPartenza+"' AND laf1.RefIdLinea=laf2.RefIdLinea AND laf2.RefIdFermata=laf3.RefIdFermata AND laf3.RefIdLinea=laf4.RefIdLinea AND laf4.RefIdFermata='"+idFermataArrivo+"' AND laf1.posizion<laf2.posizione AND laf3.posizione<laf4.posizione";
			 String sql="SELECT laf1.idLinea as idLinea1,laf1.posizione as pos1, laf2.RefIdFermata as idFermataIntermedia,laf2.posizione as pos2,laf4.RefIdLinea as idLinea2 laf3.posizione as pos3, laf4.posizione as pos4 FROM lineassociafermata laf1, lineaassociafermata laf2, lineaassociafermata laf3, lineaassociafermata laf4 WHERE"+where;
			 List<JSONObject> queryRes=selectQuery(sql);
			 List<JSONObject> res=new ArrayList<>();
			 if(queryRes==null) {
				 res.add(new JSONObject("{numLinee:3}"));
				 return res;
			 }
			 else {
				 for(JSONObject percorso :queryRes) {
						String sql1="SELECT f.*,laf.posizione as pos FROM lineaassociafermata laf,fermata f WHERE laf.RefIdFermata=f.idFermata AND laf.RefIdLinea='"+percorso.getInt("idLinea1")+"' AND laf.posizione>='"+percorso.getInt("pos1")+"' AND laf.posizione<='"+percorso.getInt("pos2")+"'";
						res.add(new JSONObject("{numLinee:2,idLinea1:"+percorso.getInt("idLinea1")+",idLinea2:"+percorso.getInt("idLinea2")+"}"));
						String sql2="SELECT f.*,laf.posizione as pos FROM lineaassociafermata laf,fermata f WHERE laf.RefIdFermata=f.idFermata AND laf.RefIdLinea='"+percorso.getInt("idLinea2")+"' AND laf.posizione>='"+percorso.getInt("pos3")+"' AND laf.posizione<='"+percorso.getInt("pos4")+"'";
						List<JSONObject> fermateLinea1=selectQuery(sql1);
						List<JSONObject> fermateLinea2=selectQuery(sql2);
						for(JSONObject fermata : fermateLinea1) {
							res.get(queryRes.size()-1).put( "1."+Integer.toString(fermata.getInt("pos"))  ,fermata.toString());
						}
						for(JSONObject fermata : fermateLinea2) {
							res.get(queryRes.size()-1).put("2."+Integer.toString(fermata.getInt("pos"))  ,fermata.toString());
						}
				}
				return res;// ritorna il percorso per ogni coppia di linee trovate ogni JSON è del tipo:
							// {numLinee:2,idLinea1:101,idLinea2:102, 1.0:{primafermatalinea1}, 1.1:{secondafermatalinea1},...,2.7:{primafermatalinea2},2.8:{secondafermatalinea2}...}
							//NOTA Ultimafermatalinea1 e primafermatalinea2 coincidono e sono la fermata di congiunzione
			 }
		}catch(SQLException e){
				System.out.println("Errore esecuzione query calcolaPercorsoADueVie");
		}
		return null;
	}
	
	
	// esegue l'update generico
	public boolean updateQuery(String table, HashMap<String, String> data1, HashMap<String, Integer> data2, String where) throws SQLException{
		String sql="UPDATE "+table+" SET ";
		if(data1!=null) {
			for (HashMap.Entry<String, String> entry : data1.entrySet()) {
				sql=sql+entry.getKey() + "='" + entry.getValue().replace("'", "\\'")+"' , ";
			}
		}
		if(data2!=null) {
			for (HashMap.Entry<String, Integer> entry : data2.entrySet()) {
				sql=sql+entry.getKey() + "='" + entry.getValue()+"' , ";
			}
		}
		
		sql=sql.substring(0, sql.length() - 2) + " WHERE "+ where;
		return this.updateSql(sql);
	}

	public boolean updateSql(String sql) throws SQLException {
		if(this.statement.executeUpdate(sql)>0){
			return true;
		}
		return false;
	}
	// esegue l'update generico
	
	
	
	//esegue l'inserimento generico
	public boolean insertQuery(String table, HashMap<String, String> data1, HashMap<String, Integer> data2) throws SQLException{
		return this.insertQuery(table, data1, data2, null);
	}
	
	public boolean insertQuery(String table, HashMap<String, String> data1, HashMap<String, Integer> data2, HashMap<String, Double> data3) throws SQLException{
		String sql="INSERT INTO "+table+"(";
		String sql1="";
		String sql2="";
		if(data1!=null) {
			for (HashMap.Entry<String, String> entry : data1.entrySet()) {
				sql1=sql1+entry.getKey()+" , ";
				sql2=sql2+"'"+entry.getValue().replace("'", "\\'")+"' , ";
			}
		}
		if(data2!=null) {
			for (HashMap.Entry<String, Integer> entry : data2.entrySet()) {
				sql1=sql1+entry.getKey()+" , ";
				sql2=sql2+""+entry.getValue()+" , ";
			}
		}
		if(data3!=null) {
			for (HashMap.Entry<String, Double> entry : data3.entrySet()) {
				sql1=sql1+entry.getKey()+" , ";
				sql2=sql2+"'"+entry.getValue()+"' , ";
			}
		}
		sql1=sql1.substring(0, sql1.length() - 2);
		sql2=sql2.substring(0, sql2.length() - 2);
		sql=sql + sql1 +") VALUES(" +sql2+")"; 
		return this.updateSql(sql);
	}
	//esegue l'inserimento generico
	
	
	//esegue query generica con ritorno resultset
	private ResultSet executeSql(String sql) throws SQLException{
	    return this.statement.executeQuery(sql);
	}//esegue query generica con ritorno resultset
	
	
	
	//esegue selezione generica e resituisce JSONObject con varie righe
	public List <JSONObject> selectQuery(String sql) throws SQLException{

		ResultSet result = this.executeSql(sql);
		ResultSetMetaData rsmd = result.getMetaData();
		int n=0,nrows=0,j=0;
		n=rsmd.getColumnCount();
		if (result.last()) {
		    nrows = result.getRow();
		    result.beforeFirst();
		}
		if(nrows == 0) return null;
		List <JSONObject>  res = new ArrayList<>();
		for(int i=0; i<nrows; i++){
				res.add(new JSONObject());
		}
		while(result.next()){	
			for(int i=1;i<=n;i++) {
				res.get(j).put(rsmd.getColumnLabel(i),result.getString(rsmd.getColumnLabel(i)));
				}
			j++;
		}
		return res;
	}
	
	// esegue la cancellazione generica
	public boolean deleteQuery  (String table, String condition) throws SQLException{

		if(this.statement.executeUpdate("delete from " + table + " where " + condition)>0) {
			return true;
		}
		return false;
	}
	public List<JSONObject> cercaDipendentiElenco(String ricerca, int filtro, int disponibili) {
		try{
			String sql;
			if(filtro!=0) {
				if(disponibili!=0) {
					sql="SELECT *, rg.DataAssunzione, rg.DataLicenziamento FROM dipendente d, Ruolo r, registroassunzioni rg WHERE (CodiceFiscale LIKE '%"+ricerca+"%' OR Nome LIKE '%"+ricerca+"%' OR Cognome LIKE '%"+ricerca+"%' OR NomeRuolo LIKE '%"+ricerca+"%') AND r.idRuolo=d.RefIdRuolo AND rg.RefCodiceFiscale=d.CodiceFiscale AND r.idRuolo="+filtro+" AND d.CodiceFiscale IN (SELECT RefCodiceFiscale FROM turno WHERE Giorno='"+LocalDate.now()+"')";
					
				}else {
					sql="SELECT *, rg.DataAssunzione, rg.DataLicenziamento FROM dipendente d, Ruolo r, registroassunzioni rg WHERE (CodiceFiscale LIKE '%"+ricerca+"%' OR Nome LIKE '%"+ricerca+"%' OR Cognome LIKE '%"+ricerca+"%' OR NomeRuolo LIKE '%"+ricerca+"%') AND r.idRuolo=d.RefIdRuolo AND rg.RefCodiceFiscale=d.CodiceFiscale AND r.idRuolo="+filtro;
				}
				
			}else {
				sql="SELECT *, rg.DataAssunzione, rg.DataLicenziamento FROM dipendente d, Ruolo r, registroassunzioni rg WHERE (CodiceFiscale LIKE '%"+ricerca+"%' OR Nome LIKE '%"+ricerca+"%' OR Cognome LIKE '%"+ricerca+"%' OR NomeRuolo LIKE '%"+ricerca+"%') AND r.idRuolo=d.RefIdRuolo AND rg.RefCodiceFiscale=d.CodiceFiscale";
			}
			

			return selectQuery(sql);
		}catch(SQLException e){
			System.out.println("Errore esecuzione ricerca dipendente");
		}
		return null;
	}
	public List <JSONObject> getRuoli(){
		try{
			String sql="SELECT * FROM ruolo";
			return selectQuery(sql);
		}catch(SQLException e){
			System.out.println("Errore esecuzione recupero ruoli");
		}
		return null;
	}
	public boolean updateFoto(String cf, String name) throws SQLException {
		try{
			HashMap<String,String> data1=new HashMap<>();
			data1.put("Foto", name);
			return updateQuery("dipendente", data1, null,"CodiceFiscale='"+cf+"'");

		}catch(SQLException e){
			System.out.println("Errore esecuzione query update foto");
		}
		
		return false;
	}
	public int registraUscitaAutobus(String Targa, String CodiceFiscale){

		try{
			if(selectQuery("SELECT * FROM registrodeposito WHERE RefCodiceFiscale='"+CodiceFiscale+"' AND OraEntrata IS NULL")!=null) {
				
				return -2;
			}
			
			
			String now=LocalDateTime.now().toString();
			HashMap<String,String> data1=new HashMap<>();
			data1.put("RefTarga", Targa);
			data1.put("RefCodiceFiscale", CodiceFiscale);
			data1.put("Giorno", now.substring(0,10));
			data1.put("OraUscita", now.substring(11, now.length()));
			boolean flag=insertQuery("registrodeposito", data1, null);
			if(!flag) {

				return -1;
			}
			HashMap<String,Integer> data2=new HashMap<>();
			data2.put("StatoPresenza", 0); // eventualmente nulla

			if(updateQuery("autobus", null, data2, "Targa='"+Targa+"'")) {
				return 1;
			}
		}catch(SQLException e) {
			System.out.println("Errore esecuzione aggiungi autobus");
		}
		return -1;
	}
	
	public int registraEntrataAutobus(String Targa){

		try{
			if(selectQuery("SELECT * FROM registrodeposito WHERE RefTarga='"+Targa+"' AND OraEntrata IS NULL")==null) {
				return -2;
			}
			
			
			String now=LocalDateTime.now().toString();
			HashMap<String,String> data1=new HashMap<>();
			data1.put("OraEntrata", now.substring(11, now.length()));

			boolean flag=updateQuery("registrodeposito", data1, null, "RefTarga='"+Targa+"'");
			if(flag) {
				HashMap<String,Integer> data2=new HashMap<>();
				data2.put("StatoPresenza", 1);
				if(updateQuery("autobus", null, data2, "Targa='"+Targa+"'")) {
					return 1;
				}
				
			}
		}catch(SQLException e) {
			System.out.println("Errore esecuzione aggiungi autobus");
		}
		return -1;
	}
	
	public boolean pulisciFermateLinea(int idLinea){
		try {
			return this.deleteQuery("lineaassociafermata", "RefIdLinea="+idLinea+""); 
		}catch(SQLException e) {
			System.out.println("Errore esecuzione query eliminazione turno");
		}
		return false;
	}
	
	public JSONObject getInfoCedolino(String CF) {
		JSONObject res=new JSONObject();
		try{
			String dataAssunzione = null;
			ResultSet r=this.statement.executeQuery("select max(DataAssunzione) as DataAssunzione from registroassunzioni where RefCodiceFiscale='"+CF+"'");
			if(r.next() && r.getString("DataAssunzione")!=null) {
				dataAssunzione=r.getString("DataAssunzione");
				
				LocalDate fromDateTime = LocalDate.parse("2017-11-02").withDayOfMonth(1);
				LocalDate toDateTime = LocalDate.now().withDayOfMonth(1);

				
				LocalDate tempDateTime = LocalDate.from( fromDateTime );


				long months = tempDateTime.until( toDateTime, ChronoUnit.MONTHS);

				if(months<1L) {
					res.put("code", -2);
					return res;
				}
				
				
			}else {
				res.put("code", -1);
				return res;
			}
			
			
			
			r=this.statement.executeQuery("select substr(max(Data), 1, 7) as DataLastCedolino from cedolino where RefCodiceFiscale='"+CF+"'");
			String dataLastCedolino=null;
			if(r.next() && r.getString("DataLastCedolino")!=null) {
				dataLastCedolino=r.getString("DataLastCedolino");
				
				LocalDate fromDateTime = LocalDate.parse(dataLastCedolino+"-01");
				LocalDate toDateTime = LocalDate.now().withDayOfMonth(1);

				LocalDate tempDateTime = LocalDate.from( fromDateTime );



				long months = tempDateTime.until( toDateTime, ChronoUnit.MONTHS);
				
				if(months<1L) {
					res.put("code", -3);
					return res;
				}
			}
			r=this.statement.executeQuery("select timediff(OrarioUscita, OrarioEntrata) as ore from cartellino where RefCodiceFiscale='"+CF+"' AND Data LIKE '"+LocalDate.now().minusMonths(1).toString().substring(0, 8)+"%'");
			long second=0;
			while(r.next()) {
				second=second+r.getTime("ore").getTime()/1000L+3600L;
			}
			r=this.statement.executeQuery("select count(distinct Data) as giorniLavorativi from cartellino where RefCodiceFiscale='"+CF+"' AND Data LIKE '"+LocalDate.now().minusMonths(1).toString().substring(0, 8)+"%'");
			int giorniLavorativi = 0;
			if(r.next()) {
				giorniLavorativi=r.getInt("giorniLavorativi");
			}
			res.put("giorniLavorativi", giorniLavorativi);

			res.put("oreLavorate", Math.rint(((double)second)/60/60));
			res.put("dataAssunzione", dataAssunzione);
			
			//return selectQuery(sql);
		}catch(SQLException e){
			e.printStackTrace();
			System.out.println("Errore esecuzione info cedolino");
			res.put("code", -3);
			
		}

		res.put("code",1);
		return res;
	}
	
	public boolean licenziaDipendente(String CF) {
		try {
			HashMap<String,String> datasql=new HashMap<>();
			datasql.put("DataLicenziamento", LocalDate.now().toString());
			if(this.updateQuery("registroassunzioni",datasql,null,"RefCodiceFiscale='"+CF+"'")) {
					return true;
			}
		}catch(SQLException e) {
			System.out.println("Errore esecuzione licenzia dipendente");
		}
		
		return false;
	}
	
	public boolean riassumiDipendente(String CF) {
		try {
			HashMap<String,String> datasql=new HashMap<>();
			datasql.put("DataAssunzione", LocalDate.now().toString());
			datasql.put("RefCodiceFiscale", CF);
			if(this.insertQuery("registroassunzioni", datasql, null)) {
					return true;
			}
		}catch(SQLException e) {
			System.out.println("Errore esecuzione riassunzione dipendente");
		}
		
		return false;
	}
	
	
	public void close(){
		// ricordati di aggiungere un sistema di log
		try{
			this.connection.close();
		}catch(SQLException e){
			//connection già chiusa la cosa mi puzza
		}
		
	}
}

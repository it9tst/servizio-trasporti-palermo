package com.stp.dipendente.model;

import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.stp.dipendente.util.json.JSONException;
import com.stp.dipendente.util.json.JSONObject;

import javafx.scene.image.Image;

import com.stp.dipendente.util.Comune;
import com.stp.dipendente.util.DipendenteException;
//aggiungere controlli sui dati
public class Dipendente {
	private int id;
	private String codiceFiscale;
	private String nome;
	private String cognome;
	private String dataNascita;
	private String sesso;
	private String luogoDiNascita;
	private String provinciaDiNascita;
	private String comuneDiResidenza;
	private String provinciaDiResidenza;
	private int cap;
	private String email;
	private String recapitoTelefonico;
	private String contoCorrente;
	private String password;
	private String indirizzoResidenza;

	private String username;
	private Ruolo ruolo;
	private Image image;
	private boolean statoCartellino;
	private String foto;
	private String dataAssunzione;
	private String dataLicenziamento;
	private int primoAccesso;
	
	public String getDataAssunzione() {
		return dataAssunzione;
	}
	public void setDataAssunzione(String dataAssunzione) {
		this.dataAssunzione = dataAssunzione;
	}
	public String getDataLicenziamento() {
		return dataLicenziamento;
	}
	public void setDataLicenziamento(String dataLicenziamento) {
		this.dataLicenziamento = dataLicenziamento;
	}
	public String getFoto() {
		return foto;
	}
	public void setFoto(String foto) {
		this.foto = foto;
	}
	public Image getImage() {
		return image;
	}
	public void setImage(Image image) {
		this.image = image;
	}
	public Dipendente(JSONObject result) throws DipendenteException, JSONException{
		this( result.getString("CodiceFiscale"), result.getString("Nome"),result.getString("Cognome"),
				result.getString("DataDiNascita"),result.getString("Sesso"),result.getString("LuogoDiNascita"),
				result.getString("ProvinciaDiNascita"),result.getString("ComuneDiResidenza"),result.getString("ProvinciaDiResidenza"),
				result.getInt("CAP"),result.getString("Email"),result.getString("RecapitoTelefonico"),
				result.getString("ContoCorrente"),result.getString("IndirizzoDiResidenza"),
				result.getString("Username"),result.getString("Password"), new Ruolo(result.getInt("idRuolo"), result.getString("NomeRuolo"),
						result.getDouble("RetribuzioneOraria"), result.getInt("OreSettimanali")), result.getBoolean("StatoCartellino"), result.getString("DataAssunzione"), null, result.getString("Foto"));
		if(!result.isNull("DataLicenziamento")) {
			this.setDataLicenziamento(result.getString("DataLicenziamento"));
		}
		if(!result.isNull("PrimoAccesso")) {
			this.setPrimoAccesso(result.getInt("PrimoAccesso"));
		}
		if(!result.isNull("idDipendente")) {
			this.setId(result.getInt("idDipendente"));
		}
		
		
	}

	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int isPrimoAccesso() {
		return primoAccesso;
	}
	public void setPrimoAccesso(int primoAccesso) {
		this.primoAccesso = primoAccesso;
	}
	public Dipendente(String codiceFiscale, String nome, String cognome, String dataNascita, String sesso,
			String luogoDiNascita, String provinciaDiNascita, String comuneDiResidenza, String provinciaDiResidenza,
			int cap, String email, String recapitoTelefonico, String contoCorrente,
			String indirizzoResidenza, String username, String password, Ruolo ruolo, boolean statoCartellino, String dataAssunzione, String foto) throws DipendenteException {
		this( codiceFiscale, nome, cognome, dataNascita, sesso,
				luogoDiNascita, provinciaDiNascita, comuneDiResidenza, provinciaDiResidenza,
				cap, email, recapitoTelefonico, contoCorrente,
				indirizzoResidenza, username, password, ruolo, statoCartellino, dataAssunzione, null, foto);
		
	}
	
	
	public Dipendente(String codiceFiscale, String nome, String cognome, String dataNascita, String sesso,
			String luogoDiNascita, String provinciaDiNascita, String comuneDiResidenza, String provinciaDiResidenza,
			int cap, String email, String recapitoTelefonico, String contoCorrente,
			String indirizzoResidenza, String username, String password, Ruolo ruolo, boolean statoCartellino, String dataAssunzione, String dataLicenziamento, String foto) throws DipendenteException {

		this.foto=foto;
		this.statoCartellino=statoCartellino;
		

		this.setNome(nome);
		this.setCognome(cognome);
		this.setSesso(sesso);
		this.setLuogoDiNascita(luogoDiNascita);
		this.setProvinciaDiNascita(provinciaDiNascita);
		this.setDataNascita(dataNascita);
    	
		
		//controllo paesi
		
		
		this.setComuneDiResidenza(comuneDiResidenza);
		this.setProvinciaDiResidenza(provinciaDiResidenza);
		this.setIndirizzoResidenza(indirizzoResidenza);
		this.setCAP(cap);
		
    	this.setEmail(email);
		
    	this.setRecapitoTelefonico(recapitoTelefonico);
    	this.setContoCorrente(contoCorrente);
		this.setPassword(password);
    	this.setUsername(username);
    	this.setRuolo(ruolo);
    	this.setCodiceFiscale(codiceFiscale);
    	this.setDataAssunzione(dataAssunzione);
    	this.setDataLicenziamento(dataLicenziamento);

	}

	public boolean isStatoCartellino() {
		return statoCartellino;
	}
	public void toggleStatoCartellino() {
		if(this.statoCartellino) {
			this.statoCartellino=false;
		}else {
			this.statoCartellino=true;
		}
	}
	public String getCodiceFiscale() {
		return codiceFiscale;
	}
	public void setCodiceFiscale(String codiceFiscale) throws DipendenteException{
		if(codiceFiscale.length()!=16){
			throw new DipendenteException("Codice Fiscale inserito errato");
		}
		this.codiceFiscale = codiceFiscale;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) throws DipendenteException {
		if(nome==null) {
			throw new DipendenteException("Inserisci un nome");
		}
		if(!nome.matches("[a-zA-Z' ]+")){
			throw new DipendenteException("Nome inserito errato");
		}
		this.nome = nome;
	}
	public String getCognome() {
		return cognome;
	}
	public void setCognome(String cognome) throws DipendenteException {
		if(cognome==null) {
			throw new DipendenteException("Inserisci un cognome");
		}
		if(!cognome.matches("[a-zA-Z' ]+")){
			throw new DipendenteException("Cognome inserito errato");
		}
		this.cognome = cognome;
	}
	public String getDataNascita() {
		return dataNascita;
	}
	public void setDataNascita(String dataNascita) throws DipendenteException {
		if(dataNascita==null) {
			throw new DipendenteException("Inserisci una data di nascita");
		}
		DateFormat format= new SimpleDateFormat("yyyy-MM-dd");
    	Date d1;
		try {
			d1 = format.parse(dataNascita);
		} catch (ParseException e) {
			throw new DipendenteException("Data nel formato non valido");
		}
    	Calendar cal = Calendar.getInstance();
    	cal.add(Calendar.YEAR, -18);
    	Date d2=cal.getTime();
    	if(d2.compareTo(d1)!=1){
    		throw new DipendenteException("Il dipendente deve aver compiuto la maggiore età");
    	}
		this.dataNascita = dataNascita;
	}
	public String getSesso() {
		return sesso;
	}
	public void setSesso(String sesso) throws DipendenteException {
		if(sesso==null) {
			throw new DipendenteException("Inserisci il sesso");
		}
		if(!sesso.equals("M") && !sesso.equals("F")){
			throw new DipendenteException("Sesso non valido "+ sesso);
		}
		this.sesso = sesso;
	}
	public String getLuogoDiNascita() {
		return luogoDiNascita;
	}
	public void setLuogoDiNascita(String luogoDiNascita) throws DipendenteException {
		if(luogoDiNascita==null) {
			throw new DipendenteException("Inserisci un luogo di nascita");
		}
		this.luogoDiNascita = luogoDiNascita;
	}
	public String getProvinciaDiNascita() {
		return provinciaDiNascita;
	}
	public void setProvinciaDiNascita(String provinciaDiNascita) throws DipendenteException {
		if(provinciaDiNascita==null) {
			throw new DipendenteException("Inserisci una provincia di nascita");
		}
		this.provinciaDiNascita = provinciaDiNascita;
	}
	public String getComuneDiResidenza() {
		return comuneDiResidenza;
	}
	public void setComuneDiResidenza(String comuneDiResidenza) throws DipendenteException {
		if(comuneDiResidenza==null) {
			throw new DipendenteException("Inserisci un comune di residenza");
		}
		this.comuneDiResidenza = comuneDiResidenza;
	}
	public String getProvinciaDiResidenza() {
		return provinciaDiResidenza;
	}
	public void setProvinciaDiResidenza(String provinciaDiResidenza) throws DipendenteException {
		if(provinciaDiResidenza==null) {
			throw new DipendenteException("Inserisci una provincia di residenza");
		}
		this.provinciaDiResidenza = provinciaDiResidenza;
	}
	public int getCAP() {
		return cap;
	}
	public void setCAP(int cap) throws DipendenteException {
		if(cap<1) {
			throw new DipendenteException("Inserisci un cap valido");
		}
		this.cap = cap;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) throws DipendenteException {
		if(email==null) {
			throw new DipendenteException("Inserisci una email");
		}
		String regex = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$";
    	Pattern pattern = Pattern.compile(regex);
    	Matcher matcher = pattern.matcher(email);
    	if(!matcher.matches()){
    		throw new DipendenteException("Email non valida");
    	}
		this.email = email;
	}
	public String getRecapitoTelefonico() {
		return recapitoTelefonico;
	}
	public void setRecapitoTelefonico(String recapitoTelefonico) throws DipendenteException {
		if(recapitoTelefonico==null || recapitoTelefonico.length()<1) {
			throw new DipendenteException("Inserisci un recapito telefonico");
		}
		this.recapitoTelefonico = recapitoTelefonico;
	}
	public String getContoCorrente() {
		return contoCorrente;
	}
	public void setContoCorrente(String contoCorrente) throws DipendenteException {
		if(contoCorrente==null || contoCorrente.length()<1) {
			throw new DipendenteException("Inserisci un conto corrente");
		}
		this.contoCorrente = contoCorrente;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getIndirizzoResidenza() {
		return indirizzoResidenza;
	}
	public void setIndirizzoResidenza(String indirizzoResidenza) {
		this.indirizzoResidenza = indirizzoResidenza;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Ruolo getRuolo() {
		return this.ruolo;
	}
	public void setRuolo(Ruolo ruolo) throws DipendenteException {
		if(ruolo==null) {
			throw new DipendenteException("Inserisci un ruolo per il dipendente");
		}
		this.ruolo = ruolo;
	}
	
	public static String calcolaCF(String nome, String cognome, String dataNascita, String luogoNascita, String sesso, HashMap<String,Comune> comuni, HashMap<String,Comune> comuni2) throws DipendenteException {
		String codFis = "";
		
		if(nome==null || nome.length()<1) {
			throw new DipendenteException("Inserisci un nome valido");
		}
		if(cognome==null || cognome.length()<1) {
			throw new DipendenteException("Inserisci un cognome valido");
		}
		if(sesso==null || !(sesso.equals("M") || sesso.equals("F"))) {
			throw new DipendenteException("Inserisci un sesso valido");
		}
		if(luogoNascita==null || cognome.length()<1) {
			throw new DipendenteException("Inserisci un luogo di nascita valido");
		}
		if(!comuni.containsKey(luogoNascita.toLowerCase().trim()) && !comuni2.containsKey(luogoNascita.toLowerCase().trim())) {
			throw new DipendenteException("Luogo di nascita non valido");
		}
		if(dataNascita==null) {
			throw new DipendenteException("Inserisci una data valida");
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			@SuppressWarnings("unused")
			Date giorno= sdf.parse(dataNascita);
		}catch(ParseException e) {
			throw new DipendenteException("Data non valida");
		}
		String cognomecf = cognome.replaceAll("\\s+","").toUpperCase();
		String nomecf = nome.replaceAll("\\s+","").toUpperCase();
		String dataNascitacf = dataNascita;
		String comuneNascitacf = luogoNascita;
		
		
		
		/*calcolo prime 3 lettere */
		int cont = 0;
		/*caso cognome minore di 3 lettere*/
		if (cognomecf.length()<3){
			codFis+= cognomecf;
			while (codFis.length()<3) codFis+= "X";
			cont=3;
		}
		/*caso normale*/
		for (int i=0;i<cognomecf.length();i++) {
			if (cont==3) break;
			if (cognomecf.charAt(i)!='A' && cognomecf.charAt(i)!='E' &&
				cognomecf.charAt(i)!='I' && cognomecf.charAt(i)!='O' &&
				cognomecf.charAt(i)!='U') {
				codFis+= Character.toString(cognomecf.charAt(i));
				cont++;
			}
		}
		/* nel casoci siano meno di 3 consonanti*/
		while (cont<3) {
			for (int i=0;i<cognomecf.length();i++) {
				if (cont==3) break;
				if (cognomecf.charAt(i)=='A' || cognomecf.charAt(i)=='E' ||
					cognomecf.charAt(i)=='I' || cognomecf.charAt(i)=='O' ||
					cognomecf.charAt(i)=='U') {
					codFis+= Character.toString(cognomecf.charAt(i));
					cont++;
				}
			}
		}
		/*lettere nome*/
		cont = 0;
		/*caso nome minore di 3 lettere*/
		if (nomecf.length()<3){
			codFis+= nomecf;
			while (codFis.length()<6) codFis+= "X";
			cont=3;
		}else {
			/*caso normale*/
			int numConsNome=0;
			for (int i=0;i<nomecf.length();i++) {
				if (nomecf.charAt(i)!='A' && nomecf.charAt(i)!='E' &&
					nomecf.charAt(i)!='I' && nomecf.charAt(i)!='O' &&
					nomecf.charAt(i)!='U') {
					numConsNome++;
				}
			}
			if(numConsNome>3) {
				cont=0;
				for (int i=0;i<nomecf.length();i++) {
					
					if (cont==4) break;
					if (nomecf.charAt(i)!='A' && nomecf.charAt(i)!='E' &&
						nomecf.charAt(i)!='I' && nomecf.charAt(i)!='O' &&
						nomecf.charAt(i)!='U') {
						if (cont==1) {
							cont++;
							continue;
						}
						codFis+= Character.toString(nomecf.charAt(i));
						cont++;
						
						
					}
				}
			}else {
				for (int i=0;i<nomecf.length();i++) {
					if (cont==3) break;
					if (nomecf.charAt(i)!='A' && nomecf.charAt(i)!='E' &&
						nomecf.charAt(i)!='I' && nomecf.charAt(i)!='O' &&
						nomecf.charAt(i)!='U') {
						codFis+= Character.toString(nomecf.charAt(i));
						cont++;
					}
				}
				/* nel casoci siano meno di 3 consonanti*/
				while (cont<3) {
					for (int i=0;i<nomecf.length();i++) {
						if (cont==3) break;
						if (nomecf.charAt(i)=='A' || nomecf.charAt(i)=='E' ||
							nomecf.charAt(i)=='I' || nomecf.charAt(i)=='O' ||
							nomecf.charAt(i)=='U') {
							codFis+= Character.toString(nomecf.charAt(i));
							cont++;
						}
					}
				}
			}
		}
		
		
		/* anno */
		codFis+=dataNascitacf.substring(2,4);
		/*Mese*/
		int mese=0;
		if (dataNascitacf.charAt(5)== '0') mese = Integer.parseInt(dataNascitacf.substring(6,7));
		else mese = Integer.parseInt(dataNascitacf.substring(5,7));
		switch (mese) {
			case 1: {codFis+="A";break;}
			case 2: {codFis+="B";break;}
			case 3: {codFis+="C";break;}
			case 4: {codFis+="D";break;}
			case 5: {codFis+="E";break;}
			case 6: {codFis+="H";break;}
			case 7: {codFis+="L";break;}
			case 8: {codFis+="M";break;}
			case 9: {codFis+="P";break;}
			case 10: {codFis+="R";break;}
			case 11: {codFis+="S";break;}
			case 12: {codFis+="T";break;}
		}
		/*giorno*/
		int giorno=0;
		if (dataNascitacf.charAt(8)== '0') giorno = Integer.parseInt(dataNascitacf.substring(9,10));
			else giorno = Integer.parseInt(dataNascitacf.substring(8,10));
		
		if (sesso.equals("F")){
			giorno+=40;	
		}
		if(giorno<10) {
			codFis+="0"+giorno;
		}else {
			codFis+=giorno;
		}
		
		
		/*comune nascita*/
		Comune comune;
		if(!comuni.containsKey(luogoNascita.toLowerCase().trim()) ) {
			comune=comuni2.get(comuneNascitacf.toLowerCase().trim());
		}else {
			comune=comuni.get(comuneNascitacf.toLowerCase().trim());
		}
		
		

		
		String codReg = comune.getCodiceFisc();
		codFis+=codReg;
		/*Carattere di controllo*/
		int sommaPari=0;

		for (int i=1;i<=13;i+=2) {
			switch (codFis.charAt(i)) {
				case '0': {sommaPari+=0;break;}
				case '1': {sommaPari+=1;break;}
				case '2': {sommaPari+=2;break;}
				case '3': {sommaPari+=3;break;}
				case '4': {sommaPari+=4;break;}
				case '5': {sommaPari+=5;break;}
				case '6': {sommaPari+=6;break;}
				case '7': {sommaPari+=7;break;}
				case '8': {sommaPari+=8;break;}
				case '9': {sommaPari+=9;break;}
				case 'A': {sommaPari+=0;break;}
				case 'B': {sommaPari+=1;break;}
				case 'C': {sommaPari+=2;break;}
				case 'D': {sommaPari+=3;break;}
				case 'E': {sommaPari+=4;break;}
				case 'F': {sommaPari+=5;break;}
				case 'G': {sommaPari+=6;break;}
				case 'H': {sommaPari+=7;break;}
				case 'I': {sommaPari+=8;break;}
				case 'J': {sommaPari+=9;break;}
				case 'K': {sommaPari+=10;break;}
				case 'L': {sommaPari+=11;break;}
				case 'M': {sommaPari+=12;break;}
				case 'N': {sommaPari+=13;break;}
				case 'O': {sommaPari+=14;break;}
				case 'P': {sommaPari+=15;break;}
				case 'Q': {sommaPari+=16;break;}
				case 'R': {sommaPari+=17;break;}
				case 'S': {sommaPari+=18;break;}
				case 'T': {sommaPari+=19;break;}
				case 'U': {sommaPari+=20;break;}
				case 'V': {sommaPari+=21;break;}
				case 'W': {sommaPari+=22;break;}
				case 'X': {sommaPari+=23;break;}
				case 'Y': {sommaPari+=24;break;}
				case 'Z': {sommaPari+=25;break;}
			}
		}
		int sommaDispari=0;
		for (int i=0;i<=14;i+=2) {
			switch (codFis.charAt(i)) {
				case '0': {sommaDispari+=1;break;}
				case '1': {sommaDispari+=0;break;}
				case '2': {sommaDispari+=5;break;}
				case '3': {sommaDispari+=7;break;}
				case '4': {sommaDispari+=9;break;}
				case '5': {sommaDispari+=13;break;}
				case '6': {sommaDispari+=15;break;}
				case '7': {sommaDispari+=17;break;}
				case '8': {sommaDispari+=19;break;}
				case '9': {sommaDispari+=21;break;}
				case 'A': {sommaDispari+=1;break;}
				case 'B': {sommaDispari+=0;break;}
				case 'C': {sommaDispari+=5;break;}
				case 'D': {sommaDispari+=7;break;}
				case 'E': {sommaDispari+=9;break;}
				case 'F': {sommaDispari+=13;break;}
				case 'G': {sommaDispari+=15;break;}
				case 'H': {sommaDispari+=17;break;}
				case 'I': {sommaDispari+=19;break;}
				case 'J': {sommaDispari+=21;break;}
				case 'K': {sommaDispari+=2;break;}
				case 'L': {sommaDispari+=4;break;}
				case 'M': {sommaDispari+=18;break;}
				case 'N': {sommaDispari+=20;break;}
				case 'O': {sommaDispari+=11;break;}
				case 'P': {sommaDispari+=3;break;}
				case 'Q': {sommaDispari+=6;break;}
				case 'R': {sommaDispari+=8;break;}
				case 'S': {sommaDispari+=12;break;}
				case 'T': {sommaDispari+=14;break;}
				case 'U': {sommaDispari+=16;break;}
				case 'V': {sommaDispari+=10;break;}
				case 'W': {sommaDispari+=22;break;}
				case 'X': {sommaDispari+=25;break;}
				case 'Y': {sommaDispari+=24;break;}
				case 'Z': {sommaDispari+=23;break;}
			}
		}
		int interoControllo = (sommaPari+sommaDispari)%26;
		String carattereControllo="";
		switch (interoControllo) {
			case 0:{carattereControllo="A";break;}
			case 1:{carattereControllo="B";break;}
			case 2:{carattereControllo="C";break;}
			case 3:{carattereControllo="D";break;}
			case 4:{carattereControllo="E";break;}
			case 5:{carattereControllo="F";break;}
			case 6:{carattereControllo="G";break;}
			case 7:{carattereControllo="H";break;}
			case 8:{carattereControllo="I";break;}
			case 9:{carattereControllo="J";break;}
			case 10:{carattereControllo="K";break;}
			case 11:{carattereControllo="L";break;}
			case 12:{carattereControllo="M";break;}
			case 13:{carattereControllo="N";break;}
			case 14:{carattereControllo="O";break;}
			case 15:{carattereControllo="P";break;}
			case 16:{carattereControllo="Q";break;}
			case 17:{carattereControllo="R";break;}
			case 18:{carattereControllo="S";break;}
			case 19:{carattereControllo="T";break;}
			case 20:{carattereControllo="U";break;}
			case 21:{carattereControllo="V";break;}
			case 22:{carattereControllo="W";break;}
			case 23:{carattereControllo="X";break;}
			case 24:{carattereControllo="Y";break;}
			case 25:{carattereControllo="Z";break;}
		}
		codFis+=carattereControllo;
		return codFis;
	}
	
}
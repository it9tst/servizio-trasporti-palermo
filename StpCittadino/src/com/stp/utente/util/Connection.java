package com.stp.utente.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import com.stp.utente.model.Fermata;
import com.stp.utente.model.Linea;
import com.stp.utente.util.json.JSONException;
import com.stp.utente.util.json.JSONObject;

public class Connection {
	private final String serverAddress="127.0.0.1";
	private Socket commSocket;
	private BufferedReader input;
	private PrintWriter output;
	private JSONObject request;
	
	public Connection() throws UnknownHostException, IOException {
		commSocket = new Socket(serverAddress, 1234);
		input = new BufferedReader(new InputStreamReader(commSocket.getInputStream()));
		output = new PrintWriter(commSocket.getOutputStream(), true);
	}
	private void send(String s) throws IOException{
		output.println(s);
	}
	
	public void sendHello() throws IOException{
		this.send("Hello");
	}
	
	
	public ArrayList<Linea> getLinee() throws IOException, JSONException, LineaException{
		this.request=new JSONObject();
		this.request.put("action", 9);
		this.send(this.request.toString());
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
	
	public ArrayList<Linea> cercaLineaElenco(String ricerca) throws IOException, JSONException, LineaException{
		this.request=new JSONObject();
		this.request.put("action", 47);
		this.request.put("ricerca", ricerca);
		this.send(this.request.toString());
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
						tmp.setCapA(new Fermata(result.getInt("idFermataB"), result.getString("IndirizzoB"),
								result.getString("NumeroCivicoB"), result.getDouble("CoordinataXB"),
								result.getDouble("CoordinataYB")));
					}
					

					
					res.add(tmp);
				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	
	public void getLineeFermate(Linea linea) throws IOException, JSONException, LineaException{
		this.request=new JSONObject();
		this.request.put("action", 34);
		this.request.put("idLinea", linea.getIdLinea());
		this.send(this.request.toString());
		JSONObject result=new JSONObject(this.input.readLine());


		Fermata fermata;

		int i=0;

		if(result.getInt("code")==1){
			linea.getFermate().clear();
			while(result.getInt("code")==1) {
			
				fermata=new Fermata(result.getInt("idFermata"), 
						result.getString("Indirizzo"), 
						result.getString("NumeroCivico"), 
						result.getDouble("CoordinataX"),
						result.getDouble("CoordinataY"));
				
				linea.addFermata(i, fermata);
				i++;
				result=new JSONObject(this.input.readLine());
			}
		}
	}
	
	public ArrayList<Fermata> getFermateRaggio(double lat, double lon, double meters) throws IOException, JSONException, LineaException{
		this.request=new JSONObject();
		this.request.put("action", 33);
		this.request.put("latitude", lat);
		this.request.put("longitude", lon);
		this.request.put("radius", meters);
		this.send(this.request.toString());
		JSONObject result=new JSONObject(this.input.readLine());

		Fermata fermata;
		ArrayList<Fermata> res=new ArrayList<Fermata>();
		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
			
				fermata=new Fermata(result.getInt("idFermata"), 
						result.getString("Indirizzo"), 
						result.getString("NumeroCivico"), 
						result.getDouble("CoordinataX"),
						result.getDouble("CoordinataY"),
						result.getString("Linee"));
				
				res.add(fermata);

				result=new JSONObject(this.input.readLine());
			}
		}
		return res;
	}
	public ArrayList<JSONObject> calcolaPercorso(double lat1, double lon1, double lat2, double lon2) throws IOException, JSONException, LineaException{
		this.request=new JSONObject();
		this.request.put("action", 45);
		this.request.put("lat1", lat1);
		this.request.put("lon1", lon1);
		this.request.put("lat2", lat2);
		this.request.put("lon2", lon2);
		this.send(this.request.toString());
		
		ArrayList<JSONObject> results= new ArrayList<JSONObject>();
		
		JSONObject result=new JSONObject(this.input.readLine());

		if(result.getInt("code")==1){
			while(result.getInt("code")==1) {
			
				results.add(result);

				result=new JSONObject(this.input.readLine());
			}
		}else {
			results.add(result);
		}
		return results;
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
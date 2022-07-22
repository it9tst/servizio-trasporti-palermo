package com.stp.utente.model;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import com.stp.utente.util.LineaException;

public class Linea {
	private int idLinea;
	private LocalTime oraInizioCorsa;
	private LocalTime oraFineCorsa;
	private LocalTime durata;
	private int nBusConsigliato;
	private LocalTime oraInizioCorsaFestivo;
	private LocalTime oraFineCorsaFestivo;
	private LocalTime durataFestivo;
	private int nBusConsigliatoFestivo;
	private ArrayList<Fermata> fermate;
	private Fermata capA;
	private Fermata capB;
	
	public Fermata getCapA() {
		return capA;
	}

	public void setCapA(Fermata capA) {
		this.capA = capA;
	}

	public Fermata getCapB() {
		return capB;
	}

	public void setCapB(Fermata capB) {
		this.capB = capB;
	}

	public Linea(int idLinea, String oraInizioCorsa, String oraFineCorsa, String durata, int nBusConsigliato,
			String oraInizioCorsaFestivo, String oraFineCorsaFestivo, String durataFestivo, int nBusConsigliatoFestivo, ArrayList<Fermata> fermate, Fermata capA, Fermata capB) throws LineaException {
		this.setIdLinea(idLinea);
		this.setnBusConsigliato(nBusConsigliato);
		this.setnBusConsigliatoFestivo(nBusConsigliatoFestivo);
		
		try {
			this.setOraInizioCorsa(LocalTime.parse(oraInizioCorsa));
			this.setOraFineCorsa(LocalTime.parse(oraFineCorsa));
			this.setDurata(LocalTime.parse(durata));
			if(oraInizioCorsaFestivo!=null && oraFineCorsaFestivo!=null && durataFestivo!=null) {
				this.setOraInizioCorsaFestivo(LocalTime.parse(oraInizioCorsaFestivo));
				this.setOraFineCorsaFestivo(LocalTime.parse(oraFineCorsaFestivo));
				this.setDurataFestivo(LocalTime.parse(durataFestivo));
			}else {
				this.setOraInizioCorsaFestivo(null);
				this.setOraFineCorsaFestivo(null);
				this.setDurataFestivo(null);
			}
		}catch(DateTimeParseException e) {
			throw new LineaException("Formato orario non valido");
		}
		
		this.setCapA(capA);
		this.setCapB(capB);
		
		this.setFermate(fermate);
	}
	
	public Linea(int idLinea, String oraInizioCorsa, String oraFineCorsa, String durata, int nBusConsigliato,
			String oraInizioCorsaFestivo, String oraFineCorsaFestivo, String durataFestivo, int nBusConsigliatoFestivo, Fermata capA, Fermata capB) throws LineaException {
		this(idLinea, oraInizioCorsa, oraFineCorsa, durata, nBusConsigliato, oraInizioCorsaFestivo, oraFineCorsaFestivo, durataFestivo, nBusConsigliatoFestivo, new ArrayList<Fermata>(), capA , capB);
	}
	
	public Linea(int idLinea, String oraInizioCorsa, String oraFineCorsa, String durata, int nBusConsigliato, Fermata capA, Fermata capB) throws LineaException {
		this(idLinea, oraInizioCorsa, oraFineCorsa, durata, nBusConsigliato, null, null, null, 0, new ArrayList<Fermata>(), capA , capB);
	}
	public Linea(int idLinea, String oraInizioCorsa, String oraFineCorsa, String durata, int nBusConsigliato, ArrayList<Fermata> fermate, Fermata capA, Fermata capB) throws LineaException {
		this(idLinea, oraInizioCorsa, oraFineCorsa, durata, nBusConsigliato, null, null, null, 0, fermate, capA , capB);
	}
	
	public Linea(int idLinea, LocalTime oraInizioCorsa, LocalTime oraFineCorsa, LocalTime durata, int nBusConsigliato,
			LocalTime oraInizioCorsaFestivo, LocalTime oraFineCorsaFestivo, LocalTime durataFestivo, int nBusConsigliatoFestivo, ArrayList<Fermata> fermate, Fermata capA, Fermata capB) throws LineaException {
		this.setIdLinea(idLinea);
		this.setnBusConsigliato(nBusConsigliato);
		this.setnBusConsigliatoFestivo(nBusConsigliatoFestivo);
		

		this.setOraInizioCorsa(oraInizioCorsa);
		this.setOraFineCorsa(oraFineCorsa);
		this.setDurata(durata);
		if(oraInizioCorsaFestivo!=null && oraFineCorsaFestivo!=null && durataFestivo!=null) {
			this.setOraInizioCorsaFestivo(oraInizioCorsaFestivo);
			this.setOraFineCorsaFestivo(oraFineCorsaFestivo);
			this.setDurataFestivo(durataFestivo);
		}else {
			this.setOraInizioCorsaFestivo(null);
			this.setOraFineCorsaFestivo(null);
			this.setDurataFestivo(null);
		}
		
		
		this.setCapA(capA);
		this.setCapB(capB);
		
		this.setFermate(fermate);
	}
	public Linea(int idLinea, LocalTime oraInizioCorsa, LocalTime oraFineCorsa, LocalTime durata, int nBusConsigliato,
			LocalTime oraInizioCorsaFestivo, LocalTime oraFineCorsaFestivo, LocalTime durataFestivo, int nBusConsigliatoFestivo) throws LineaException {
		this(idLinea, oraInizioCorsa, oraFineCorsa, durata, nBusConsigliato, oraInizioCorsaFestivo, oraFineCorsaFestivo, durataFestivo, nBusConsigliatoFestivo, new ArrayList<Fermata>(), null , null);
	}
	public Linea(int idLinea, LocalTime oraInizioCorsa, LocalTime oraFineCorsa, LocalTime durata, int nBusConsigliato) throws LineaException {
		this(idLinea, oraInizioCorsa, oraFineCorsa, durata, nBusConsigliato, null, null, null, 0, new ArrayList<Fermata>(), null , null);
	}
	public void addFermata(int index, Fermata fermata) {
		fermate.add(index, fermata);
	}
	
	public int getIdLinea() {
		return idLinea;
	}

	public void setIdLinea(int idLinea) {
		this.idLinea = idLinea;
	}

	public LocalTime getOraInizioCorsa() {
		return oraInizioCorsa;
	}

	public void setOraInizioCorsa(LocalTime oraInizioCorsa) {
		this.oraInizioCorsa = oraInizioCorsa;
	}

	public LocalTime getOraFineCorsa() {
		return oraFineCorsa;
	}

	public void setOraFineCorsa(LocalTime oraFineCorsa) {
		this.oraFineCorsa = oraFineCorsa;
	}

	public LocalTime getDurata() {
		return durata;
	}

	public void setDurata(LocalTime durata) {
		this.durata = durata;
	}

	public int getnBusConsigliato() {
		return nBusConsigliato;
	}

	public void setnBusConsigliato(int nBusConsigliato) {
		this.nBusConsigliato = nBusConsigliato;
	}

	public LocalTime getOraInizioCorsaFestivo() {
		return oraInizioCorsaFestivo;
	}

	public void setOraInizioCorsaFestivo(LocalTime oraInizioCorsaFestivo) {
		this.oraInizioCorsaFestivo = oraInizioCorsaFestivo;
	}

	public LocalTime getOraFineCorsaFestivo() {
		return oraFineCorsaFestivo;
	}

	public void setOraFineCorsaFestivo(LocalTime oraFineCorsaFestivo) {
		this.oraFineCorsaFestivo = oraFineCorsaFestivo;
	}

	public LocalTime getDurataFestivo() {
		return durataFestivo;
	}

	public void setDurataFestivo(LocalTime durataFestivo) {
		this.durataFestivo = durataFestivo;
	}

	public int getnBusConsigliatoFestivo() {
		return nBusConsigliatoFestivo;
	}

	public void setnBusConsigliatoFestivo(int nBusConsigliatoFestivo) {
		this.nBusConsigliatoFestivo = nBusConsigliatoFestivo;
	}

	public ArrayList<Fermata> getFermate() {
		return fermate;
	}

	public void setFermate(ArrayList<Fermata> fermate) {
		this.fermate = fermate;
	}

	public void removeFermata(int index) {
		fermate.remove(index);
	}
}
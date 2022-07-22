package com.stp.utente.model;

import java.util.Comparator;

public class Fermata implements Comparator<Fermata>{
	private int idFermata;
	private String indirizzo;
	private String numeroCivico;
	private double coordinataX;
	private double coordinataY;
	private String linee;
	
	public Fermata(int idFermata, String indirizzo, String numeroCivico, double coordinataX, double coordinataY){
		this(idFermata, indirizzo, numeroCivico, coordinataX, coordinataY, "");
	}
	
	public String getLinee() {
		return linee;
	}

	public void setLinee(String linee) {
		this.linee = linee;
	}

	public Fermata(int idFermata, String indirizzo, String numeroCivico, double coordinataX, double coordinataY, String linee){
		this.idFermata=idFermata;
		this.indirizzo = indirizzo;
		this.numeroCivico = numeroCivico;
		this.coordinataX = coordinataX;
		this.coordinataY = coordinataY;
		this.linee=linee;
	}
	
	
	public int getIdFermata() {
		return idFermata;
	}

	public void setIdFermata(int idFermata) {
		this.idFermata = idFermata;
	}

	public String getIndirizzo() {
		return indirizzo;
	}
	
	public void setIndirizzo(String indirizzo) {
		this.indirizzo = indirizzo;
	}
	
	public String getNumeroCivico() {
		return numeroCivico;
	}

	public void setNumeroCivico(String numeroCivico) {
		this.numeroCivico = numeroCivico;
	}

	public double getCoordinataX() {
		return coordinataX;
	}

	public void setCoordinataX(double coordinataX) {
		this.coordinataX = coordinataX;
	}

	public double getCoordinataY() {
		return coordinataY;
	}

	public void setCoordinataY(double coordinataY) {
		this.coordinataY = coordinataY;
	}

	@Override
	public int compare(Fermata o1, Fermata o2) {
		if(o1.getIdFermata()>o2.getIdFermata()) {
			return -1;
		}
		if(o1.getIdFermata()<o2.getIdFermata()){
			return 1;
		}
		return 0;
	}
	
	public boolean equals(Fermata f) {
		if(this.compare(this, f)==0) {
			return true;
		}
		return false;
	}
}
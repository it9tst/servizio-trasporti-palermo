package com.stp.dipendente.model;

import java.util.Comparator;

public class Fermata implements Comparator<Fermata>{
	private int idFermata;
	private String indirizzo;
	private String numeroCivico;
	private double coordinataX;
	private double coordinataY;
	
	public Fermata(int idFermata, String indirizzo, String numeroCivico, double coordinataX, double coordinataY){
		this.idFermata=idFermata;
		this.indirizzo = indirizzo;
		this.numeroCivico = numeroCivico;
		this.coordinataX = coordinataX;
		this.coordinataY = coordinataY;
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
	@Override
	public String toString() {
		return this.indirizzo+" "+ this.numeroCivico;
	}
}
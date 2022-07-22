package com.stp.dipendente.model;

public class Ruolo {
	private int idRuolo;
	private String nomeRuolo;
	private double retribuzioneOraria;
	private int oreSettimanli;
	
	public Ruolo(int idRuolo, String nomeRuolo, double retribuzioneOraria, int oreSettimanli) {
		this.idRuolo = idRuolo;
		this.nomeRuolo = nomeRuolo;
		this.retribuzioneOraria = retribuzioneOraria;
		this.oreSettimanli = oreSettimanli;
	}

	public int getIdRuolo() {
		return idRuolo;
	}

	public String getNomeRuolo() {
		return nomeRuolo;
	}

	public double getRetribuzioneOraria() {
		return retribuzioneOraria;
	}

	public int getOreSettimanli() {
		return oreSettimanli;
	}
	

}

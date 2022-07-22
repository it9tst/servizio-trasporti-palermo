package com.stp.dipendente.util;

public class Comune {
	private String nome;
	private String provincia;
	private String CodiceFisc;
	public String getNome() {
		return nome;
	}
	public String getProvincia() {
		return provincia;
	}
	public String getCodiceFisc() {
		return CodiceFisc;
	}
	public Comune(String nome, String provincia, String codiceFisc) {
		super();
		this.nome = nome;
		this.provincia = provincia;
		CodiceFisc = codiceFisc;
	}
}

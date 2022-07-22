package com.stp.dipendente.model;

import java.time.LocalDate;

public class Cedolino {
	private LocalDate data;
	private int oreMensili;
	private int oreLavorative;
	private int oreMalattia;
	private int oreFerie;
	private double totalePagamento;
	private int idCedolino;
	private String fileCedolino;
	
	
	public Cedolino(LocalDate data, int oreMensili, int oreLavorative, int oreMalattia, int oreFerie,
			double totalePagamento, int idCedolino, String fileCedolino) {

		this.data = data;
		this.oreMensili = oreMensili;
		this.oreLavorative = oreLavorative;
		this.oreMalattia = oreMalattia;
		this.oreFerie = oreFerie;
		this.totalePagamento = totalePagamento;
		this.idCedolino = idCedolino;
		this.fileCedolino = fileCedolino;
	}
	
	public Cedolino(String data, int oreMensili, int oreLavorative, int oreMalattia, int oreFerie,
			double totalePagamento, int idCedolino, String fileCedolino) {
		this(LocalDate.parse(data), oreMensili, oreLavorative, oreMalattia, oreFerie, totalePagamento, idCedolino, fileCedolino);
	}
	public LocalDate getData() {
		return data;
	}
	public void setData(LocalDate data) {
		this.data = data;
	}
	public int getOreMensili() {
		return oreMensili;
	}
	public void setOreMensili(int oreMensili) {
		this.oreMensili = oreMensili;
	}
	public int getOreLavorative() {
		return oreLavorative;
	}
	public void setOreLavorative(int oreLavorative) {
		this.oreLavorative = oreLavorative;
	}
	public int getOreMalattia() {
		return oreMalattia;
	}
	public void setOreMalattia(int oreMalattia) {
		this.oreMalattia = oreMalattia;
	}
	public int getOreFerie() {
		return oreFerie;
	}
	public void setOreFerie(int oreFerie) {
		this.oreFerie = oreFerie;
	}
	public double getTotalePagamento() {
		return totalePagamento;
	}
	public void setTotalePagamento(double totalePagamento) {
		this.totalePagamento = totalePagamento;
	}
	public int getIdCedolino() {
		return idCedolino;
	}
	public void setIdCedolino(int idCedolino) {
		this.idCedolino = idCedolino;
	}
	public String getFileCedolino() {
		return fileCedolino;
	}
	public void setFileCedolino(String fileCedolino) {
		this.fileCedolino = fileCedolino;
	}

	
}

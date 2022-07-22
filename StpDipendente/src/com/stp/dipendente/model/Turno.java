package com.stp.dipendente.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import com.stp.dipendente.util.TurnoException;

public class Turno implements Comparator<Turno>{
	private Date giorno;
	private Date oraInizio;
	private Date oraFine;
	private int idLinea=-1;
	
	public int getIdLinea() {
		return idLinea;
	}
	public void setIdLinea(int idLinea) {
		this.idLinea = idLinea;
	}
	public Turno(String giorno, String oraInizio, String oraFine) throws TurnoException {
		this(giorno, oraInizio, oraFine, -1);
	}
	public Turno(String giorno, String oraInizio, String oraFine, int idLinea) throws TurnoException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			this.giorno= sdf.parse(giorno);
		}catch(ParseException e) {
			e.printStackTrace();
			throw new TurnoException("Errore nella data");
		}
		sdf.applyPattern("HH:mm");
		try {
			this.oraInizio= sdf.parse(oraInizio);
			this.oraFine= sdf.parse(oraFine);
		}catch(ParseException e) {
			e.printStackTrace();
			throw new TurnoException("Errore nell'orario");
		}
		this.setIdLinea(idLinea);
	}
	public Date getGiorno() {
		return this.giorno;
	}
	public Date getOraInizio() {
		return this.oraInizio;
	}
	public Date getOraFine() {
		return this.oraFine;
	}
	@SuppressWarnings("deprecation")
	public Date getDataInizio() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.giorno);
		calendar.add(Calendar.HOUR_OF_DAY, this.oraInizio.getHours());
		return calendar.getTime();
	}
	@SuppressWarnings("deprecation")
	public Date getDataFine() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.giorno);
		calendar.add(Calendar.HOUR_OF_DAY,this.oraFine.getHours());
		return calendar.getTime();
	}
	@Override
	public int compare(Turno t1, Turno t2) {
		if(t1.getGiorno().equals(t2.getGiorno()) && t1.getOraInizio().equals(t2.getOraInizio()) && t1.getOraFine().equals(t2.getOraFine())) {
			return 0;
		}
		return 1;
	}
	
	public boolean equals(Turno t) {
		if(this.compare(this, t)==0) return true;
		return false;
	}
}

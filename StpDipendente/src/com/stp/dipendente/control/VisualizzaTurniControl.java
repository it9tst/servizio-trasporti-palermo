package com.stp.dipendente.control;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import com.jfoenix.controls.JFXButton;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.model.Turno;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.TurnoException;
import com.stp.dipendente.util.json.JSONException;

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;

public class VisualizzaTurniControl extends MainControlLogged{
	
	private LocalDateTime weekStart;
	private LocalDateTime weekEnd;
	private ArrayList<Turno> turniWeek;
	@SuppressWarnings("unused")
	private int oreSettimanali;
	private long hourSumWeek;
	
    public VisualizzaTurniControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}
	@FXML
    private Agenda agendaid;
    
    @FXML
    private BorderPane pane;
    
    @FXML
    private Text usernameDipendente;

    @FXML
    private Circle timbro;
    
    @FXML
    private JFXButton indietroButton;
    
    @FXML
    private AnchorPane parentStackPane;
    
    @FXML
    void menuDipendente() {
		this.setCenter(800, 600);
		new MenuDipendenteControl("MenuDipendente", this.getStage(), this.getUtente()).start();
    }
    
    @FXML
    private void avantiWeek() {
    	agendaid.setDisplayedLocalDateTime(agendaid.getDisplayedLocalDateTime().plus(Period.ofWeeks(1)));
    	 aggiornaTurni();
    };
    
    @FXML
    private void indietroWeek() {
    	agendaid.setDisplayedLocalDateTime(agendaid.getDisplayedLocalDateTime().plus(Period.ofWeeks(-1)));
    	 aggiornaTurni();
    };
    
    public void init() {
    	super.init();
    	this.aggiornaTurni();
    	this.agendaid.setEditAppointmentCallback(new Callback<Appointment, Void>(){

			@Override
			public Void call(Appointment param) {
				
				return null;
			}
    		
    	});
    }
    private ArrayList<Turno> getTurniDipendente(Dipendente dipendente, LocalDateTime start, LocalDateTime end) { 
    	ArrayList<Turno> turni=null;
    	Connection conn=null;
		try {
			conn= new Connection(this.getUtente());
			turni = conn.getTurni(dipendente, start, end);
		} catch (IOException | TurnoException | JSONException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		}catch (ConnectionException e) {
    		this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    	return turni;
    }
    
    
    
    
    private void aggiornaTurni() {
    	
    	this.weekStart=this.agendaid.displayedLocalDateTime().getValue().with(DayOfWeek.MONDAY)
				.withHour(0)
				.withMinute(0)
				.withSecond(0)
				.withNano(0);
		this.weekEnd=this.agendaid.displayedLocalDateTime().getValue().with(DayOfWeek.SUNDAY)
				.withHour(0)
				.withMinute(0)
				.withSecond(0)
				.withNano(0);
		System.out.println(this.turniWeek);
		this.turniWeek= this.getTurniDipendente(this.getUtente(), this.weekStart, this.weekEnd);
		this.oreSettimanali=this.getUtente().getRuolo().getOreSettimanli();
		Instant instant;
		LocalDateTime res1, res2;
		agendaid.appointments().clear();
		this.hourSumWeek=0;
		for(Turno t: this.turniWeek) {
			instant = Instant.ofEpochMilli(t.getDataInizio().getTime());
			res1 = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
			instant = Instant.ofEpochMilli(t.getDataFine().getTime());
			res2 = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
			if(res2.getHour()==0 && res2.getMinute()==0) {
				res2=res2.plusDays(1);
			}
			
	    	LocalDateTime tempDateTime = LocalDateTime.from(res1);
	    	long hours = tempDateTime.until(res2, ChronoUnit.HOURS);
	    	tempDateTime = tempDateTime.plusHours(hours);
	    	
	    	this.hourSumWeek=this.hourSumWeek+hours;
	    	
	    	Appointment app=new Agenda.AppointmentImplLocal()
	            .withStartLocalDateTime(res1)
	            .withEndLocalDateTime(res2)
	            .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group11"));
	    	if(this.getUtente().getRuolo().getIdRuolo()==4) {
	    		if(t.getIdLinea()>0) {
	    			app.setSummary(Integer.toString(t.getIdLinea()));
	    		}
	    		
	    	}
			agendaid.appointments().add(app);
		}
    }
}

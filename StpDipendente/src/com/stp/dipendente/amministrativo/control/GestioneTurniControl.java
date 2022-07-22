package com.stp.dipendente.amministrativo.control;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.Instant;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Map;


import com.jfoenix.controls.JFXButton;
import com.stp.dipendente.control.ElencoDipendentiControl;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.model.Turno;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.TurnoException;
import com.stp.dipendente.util.json.JSONException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javafx.util.Callback;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;
import jfxtras.scene.control.agenda.Agenda.LocalDateTimeRange;


public class GestioneTurniControl extends ElencoDipendentiControl {
	public GestioneTurniControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}
	private Dipendente dipendente;
	private LocalDateTime weekStart;
	private LocalDateTime weekEnd;
	private List<Turno> turniWeekList;
	private Map<Appointment, Turno> turniWeek;
	private Map<Appointment, Turno> turniAdd;
	private ArrayList<Turno> turniDelete;
	@SuppressWarnings("unused")
	private int oreSettimanali;
	private long hourSumWeek;
	
	@FXML
    private Agenda agendaid;
	
	@FXML
	private AnchorPane parentStackPane;
    
    @FXML
    private BorderPane pane;
    
    @FXML
    private Text usernameDipendente;

    @FXML
    private Circle timbro;
    
    @FXML
    private JFXButton indietroButton;
    
    @FXML
    private Text oreSettimanaliText;
    
    @FXML
    void elencoDipendenti() {
    	this.gestionePersonale();
    }
    
    @FXML
    private void avantiWeek() {
    	agendaid.setDisplayedLocalDateTime(agendaid.getDisplayedLocalDateTime().plus(Period.ofWeeks(1)));
		this.aggiornaTurni();
    };
    
    @FXML
    private void indietroWeek() {
    	agendaid.setDisplayedLocalDateTime(agendaid.getDisplayedLocalDateTime().plus(Period.ofWeeks(-1)));
    	this.aggiornaTurni();
    };
    
    @FXML
    private void salvaTurni(){
    	for(Turno t: this.turniDelete) {
       		Connection conn=null;
			try {
				conn = new Connection(this.getUtente());
				conn.deleteTurni(this.dipendente, t);
	        	conn.close();
			} catch (IOException e) {
				this.openDialog(this.parentStackPane, "Errore caricamento turni");
				return;
			} catch (ConnectionException e) {
	    		this.openDialog(this.parentStackPane, e.getMessage());
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}
        	
    	}
    	for(Turno t: this.turniAdd.values()) {
    		Connection conn=null;
			try {
				conn = new Connection(this.getUtente());
				conn.updateTurni(this.dipendente, t);
	        	conn.close();
			} catch (IOException e) {
				this.openDialog(this.parentStackPane, "Errore caricamento turni");
				return;
			}catch (ConnectionException e) {
	    		this.openDialog(this.parentStackPane, e.getMessage());
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}
        	
    	}
    	JFXButton btn= new JFXButton("OK");
		btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent arg0) {
				GestioneTurniControl.this.gestionePersonale();
			}
			
		});
    	this.openDialog(this.parentStackPane, "Turni salvati correttamente", btn);
    	
    }
    
    
    void gestionePersonale() {
    	this.setCenter(1200, 800);
    	this.setPkg("");
    	this.setXmlFileName("ElencoDipendenti");
    	this.start();
    }
    @Override
	public void init() {
    	super.init();
    	setPkg("amministrativo/");
    	if(this.getXmlFileName().equals("GestisciTurni")) {
    		this.turniAdd =new  HashMap<Appointment, Turno>();
    		this.turniWeek = new  HashMap<Appointment, Turno>();
    		this.turniDelete = new ArrayList<Turno>();
    		this.aggiornaTurni();
    		this.agendaid.newAppointmentCallbackProperty().set(new Callback<Agenda.LocalDateTimeRange,Agenda.Appointment>(){
				@Override
				public Appointment call(LocalDateTimeRange localDateTimeRange) {
					int startHour=(localDateTimeRange.getStartLocalDateTime().getMinute()>30?
							localDateTimeRange.getStartLocalDateTime().getHour()+1:
							localDateTimeRange.getStartLocalDateTime().getHour());
					
					LocalDateTime start=LocalDateTime.of(localDateTimeRange.getStartLocalDateTime().getYear(),
							localDateTimeRange.getStartLocalDateTime().getMonth(),
							localDateTimeRange.getStartLocalDateTime().getDayOfMonth(),
							(startHour==24?23:startHour),
							(startHour==24?59:0));
					
					
					
					int endHour=(localDateTimeRange.getEndLocalDateTime().getMinute()>30?
							localDateTimeRange.getEndLocalDateTime().getHour()+1:
							localDateTimeRange.getEndLocalDateTime().getHour());
					LocalDateTime end;
					if(endHour==24) {
						end=LocalDateTime.of(localDateTimeRange.getEndLocalDateTime().getYear(),
								localDateTimeRange.getEndLocalDateTime().getMonth(),
								localDateTimeRange.getEndLocalDateTime().getDayOfMonth()+1,
								0,
								0);
					}else {
						end=LocalDateTime.of(localDateTimeRange.getEndLocalDateTime().getYear(),
								localDateTimeRange.getEndLocalDateTime().getMonth(),
								localDateTimeRange.getEndLocalDateTime().getDayOfMonth(),
								endHour,
								0);
					}
					
					
					
					/*if(LocalDateTime.now().compareTo(start)>=0) {
						return null;
					}*/
							
							
					
					if(start.toLocalTime().compareTo(end.toLocalTime())>=0) {
						if(!(end.getHour()==0 && end.getMinute()==0)) {
							return null;
						}
						
					}
					
					
					
					
					for(Appointment a: GestioneTurniControl.this.agendaid.appointments()) {
						if(a.getStartLocalDateTime().compareTo(start)<=0 && a.getEndLocalDateTime().compareTo(end)>=0) {
							System.out.println("12");
							return null;
						}
						if(a.getStartLocalDateTime().compareTo(start)>=0 && a.getStartLocalDateTime().compareTo(end)<0) {
							System.out.println("13");
							return null;
						}
						if(a.getEndLocalDateTime().compareTo(start)>0 && a.getEndLocalDateTime().compareTo(end)<=0) {
							System.out.println("14");
							return null;
						}
					}
					
					
					
					LocalDateTime tempDateTime = LocalDateTime.from(start);
			    	long hours = tempDateTime.until(end, ChronoUnit.HOURS);
			    	if(hours==0) {
			    		System.out.println("15");
			    		return null;
			    	}
			    	tempDateTime = tempDateTime.plusHours(hours);
			    	if((GestioneTurniControl.this.hourSumWeek+hours)>GestioneTurniControl.this.dipendente.getRuolo().getOreSettimanli()) {
			    		GestioneTurniControl.this.openDialog(GestioneTurniControl.this.parentStackPane, "Ore settimanali massime superate");
			    		return null;
			    	}
			    	
			    	GestioneTurniControl.this.hourSumWeek=GestioneTurniControl.this.hourSumWeek+hours;
			    	GestioneTurniControl.this.oreSettimanaliText.setText(Long.toString(GestioneTurniControl.this.dipendente.getRuolo().getOreSettimanli()-GestioneTurniControl.this.hourSumWeek));
			    	
			    	Appointment app = new Agenda.AppointmentImplLocal()
	                  .withStartLocalDateTime(start)
	                  .withEndLocalDateTime(end)
	                  .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group11"));
			    	try {
						GestioneTurniControl.this.turniAdd.put(app, new Turno(
								start.toString().substring(0, 10),
								start.toString().substring(11,16),
								end.toString().substring(11, 16)));
						
						return app;
					} catch (TurnoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	
			    	return null;
					
				}
    			
    		});
    		this.agendaid.setDeleteCallback(new Callback<Appointment,Void>(){

				@Override
				public Void call(Appointment param) {
					
					/*if(LocalDateTime.now().compareTo(param.getStartLocalDateTime())>=0) {
						agendaid.appointments().add(param);
						return null;
					}*/
					LocalDateTime tempDateTime = LocalDateTime.from(param.getStartLocalDateTime());
					
			    	long hours = tempDateTime.until(param.getEndLocalDateTime(), ChronoUnit.HOURS);
			    	GestioneTurniControl.this.hourSumWeek=GestioneTurniControl.this.hourSumWeek-hours;
			    	GestioneTurniControl.this.oreSettimanaliText.setText(Long.toString(GestioneTurniControl.this.dipendente.getRuolo().getOreSettimanli()-GestioneTurniControl.this.hourSumWeek));
			    	if(GestioneTurniControl.this.turniAdd.containsKey(param)) {
						GestioneTurniControl.this.turniAdd.remove(param);
						return null;
					}
					if(GestioneTurniControl.this.turniWeek.containsKey(param)) {
						GestioneTurniControl.this.turniDelete.add(GestioneTurniControl.this.turniWeek.get(param));
					}
					return null;
				}
    			
    		});
    		this.agendaid.setActionCallback(new Callback<Appointment,Void>(){

				@Override
				public Void call(Appointment param) {
					
					return null;
				}
    			
    		});
    		

    	}
    	
    }
    private ArrayList<Turno> getTurniDipendente(Dipendente dipendente, LocalDateTime start, LocalDateTime end) { 
    	ArrayList<Turno> turni=null;
    	Connection conn=null;
		try {
			conn= new Connection(this.getUtente());
			turni = conn.getTurni(dipendente, start, end);
		} catch (JSONException | IOException | TurnoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}catch (ConnectionException e) {
    		this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    	return turni;
    }
    public void action(Dipendente dipendente) {
    	this.dipendente=dipendente;
		this.setCenter(1200, 800);
		this.setXmlFileName("GestisciTurni");
		this.start();
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
		this.turniWeekList= this.getTurniDipendente(this.dipendente, this.weekStart, this.weekEnd);
		this.oreSettimanali=this.dipendente.getRuolo().getOreSettimanli();
		Instant instant;
		LocalDateTime res1, res2;
		agendaid.appointments().clear();
		this.hourSumWeek=0;
		Agenda.AppointmentImplLocal app;

		
		outer:
		for(Turno t: this.turniWeekList) {
			for(Turno t2: this.turniDelete) {
				if(t2.equals(t)) {
					continue outer;
				}
			}
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
	    	app =new Agenda.AppointmentImplLocal()
    	            .withStartLocalDateTime(res1)
    	            .withEndLocalDateTime(res2)
    	            .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group11"));
	    	this.turniWeek.put(app, t);
	    	
			agendaid.appointments().add(app);
		}
		for (Appointment a : this.turniAdd.keySet()) {
			agendaid.appointments().add(a);
			
			if(a.getStartLocalDateTime().compareTo(this.weekStart)>=0 && a.getStartLocalDateTime().compareTo(this.weekEnd)<0) {
				LocalDateTime tempDateTime = LocalDateTime.from(a.getStartLocalDateTime());
		    	long hours = tempDateTime.until(a.getEndLocalDateTime(), ChronoUnit.HOURS);
		    	tempDateTime = tempDateTime.plusHours(hours);
		    	
		    	this.hourSumWeek=this.hourSumWeek+hours;
			}
			
			
		}
		
		this.oreSettimanaliText.setText(Long.toString(this.dipendente.getRuolo().getOreSettimanli()-this.hourSumWeek));
    }
}

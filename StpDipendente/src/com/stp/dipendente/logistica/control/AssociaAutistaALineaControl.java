package com.stp.dipendente.logistica.control;


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

import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.scene.control.agenda.Agenda.Appointment;

import com.stp.dipendente.control.ElencoDipendentiControl;
import com.stp.dipendente.control.ElencoLineeControl;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.model.Linea;
import com.stp.dipendente.model.Turno;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.TurnoException;
import com.stp.dipendente.util.json.JSONException;

public class AssociaAutistaALineaControl extends ElencoDipendentiControl{
	public AssociaAutistaALineaControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}

	private LocalDateTime weekStart;
	private LocalDateTime weekEnd;
	private Dipendente dipendenteAssocia;
	private List<Turno> turniWeekList;
	private Map<Appointment,Turno> turniWeekMap;
	private Turno turnoAssocia;
	
	@FXML
	private AnchorPane footerContent;
	

	
    @FXML
    private BorderPane root;

    @FXML
    private Text usernameDipendente;

    @FXML
    private Circle timbro;

    @FXML
    private JFXButton salvaButton;

    @FXML
    private JFXButton annullaButton;

    @FXML
    private AnchorPane parentStackPane;
    
    @FXML
    private Agenda agendaid;
	
    
    @FXML
    private BorderPane pane;
    
    @FXML
    private JFXButton indietroButton;
    
    @FXML
    private Text oreSettimanaliText;
    
    @FXML
    private Text oreSettText;
    

    
    @FXML
    void tornaIndietro() {
    	/*if(this.dipendenteModifica==null){
    		this.gestionePersonale();
    	}else {
    		if(this.disabilitati) {
        		this.gestionePersonale();
        	}else {
        		this.toggleEditBox();
        	}
    	}*/
    	
    }
    void gestionePersonale() {
    	this.setCenter(1200, 800);
    	this.setPkg("");
    	this.setXmlFileName("ElencoDipendenti");
    	this.start();
    }
    @Override
	public void init(){
    	super.init();
    	this.setPkg("logistica/");
    	if(this.getXmlFileName().equals("ElencoDipendenti")) {
    		
    	}else if(this.getXmlFileName().equals("GestisciTurni")) {
    		this.oreSettText.setText("");
    		this.footerContent.getChildren().remove(this.salvaButton);
    		this.turniWeekMap= new HashMap<Appointment, Turno>();
    		this.aggiornaTurni();
    		/*Connection conn;
			try {
				conn = new Connection();
				conn.getTurni(this.getUtente(), dipendente, start, end)
			} catch (IOException | JSONException | DipendenteException | URISyntaxException e) {
				this.openDialog(this.parentStackPane, "Errore caricamento foto");
			}*/
    		this.agendaid.setDeleteCallback(new Callback<Appointment, Void>(){

				@Override
				public Void call(Appointment param) {
					rimuoviAssociaAutista(param);
					return null;
				}
    			
    		});
    		this.agendaid.setActionCallback(new Callback<Appointment,Void>(){

				@Override
				public Void call(Appointment param) {
					AssociaAutistaALineaControl.this.turnoAssocia=AssociaAutistaALineaControl.this.turniWeekMap.get(param);
					new ElencoLineeControl("ElencoLinee", new Stage(), getUtente()){
						@Override
						public void action(LineaRecursive item) {
							AssociaAutistaALineaControl.this.associaAutista(item.getLinea(), param);
							this.getStage().close();
						}
						
						@Override
						public void menuDipendente() {
							this.getStage().close();
						}
						
						@Override
						public void init() {
							super.init();
							this.getAggiungiLineaButton().setVisible(false);
						}
					}.start();
					return null;
				}
    		
    		});
    	}
    	
    }
    
    @Override
    public void action(Dipendente dipendente) {
    	if(this.getXmlFileName().equals("ElencoDipendenti")) {
    		this.dipendenteAssocia=dipendente;
    		this.setCenter(1200, 800);
    		this.setPkg("amministrativo/");
       		this.setXmlFileName("GestisciTurni");
    		this.start();
    	}
    	
    }
    
    public void associaAutista(Linea linea, Appointment app) {
    	
    	if(this.dipendenteAssocia!=null && this.turnoAssocia!=null && linea!=null) {
    		Connection conn=null;
    		try {
    			conn= new Connection(this.getUtente()); 
    			if(conn.associaAutista(this.dipendenteAssocia, this.turnoAssocia, linea)==1) {
    				this.openDialog(this.parentStackPane, "Linea associata correttamente");
    				
    				this.agendaid.appointments().remove(app);
    				app.setSummary(Integer.toString(linea.getIdLinea()));
    				this.agendaid.appointments().add(app);
    			}else {
    				this.openDialog(this.parentStackPane, "Errore, riprovare più tardi");
    			}
    		}catch (IOException | JSONException e) {
    			this.openDialog(this.parentStackPane, "Errore connessione");
    		} catch (ConnectionException e) {
    			this.openDialog(this.parentStackPane, e.getMessage());
    		}finally {
    			if(conn!=null) {
    				conn.close();
    			}
    		}
    	}
    }
    
    public void rimuoviAssociaAutista(Appointment app) {
    	
    	if(this.dipendenteAssocia!=null && app!=null) {
    		Turno turno=AssociaAutistaALineaControl.this.turniWeekMap.get(app);
    		Connection conn= null;
    		try {
    			conn= new Connection(this.getUtente()); 
    			if(conn.rimuoviAssociaAutista(this.dipendenteAssocia, turno)==1) {
    				
    				
    				app.setSummary("");
					this.agendaid.appointments().add(app);
    			}else {
    				this.openDialog(this.parentStackPane, "Errore, riprovare più tardi");
    			}
    		}catch (IOException | JSONException e) {
    			this.openDialog(this.parentStackPane, "Errore connessione");
    		} catch (ConnectionException e) {
    			this.openDialog(this.parentStackPane, e.getMessage());
    		}finally {
    			if(conn!=null) {
    				conn.close();
    			}
    		}
    	}
    }
    
    @FXML
    public void elencoDipendenti() {
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
    	;   	
    }
    
    private ArrayList<Turno> getTurniDipendente(Dipendente dipendente, LocalDateTime start, LocalDateTime end) { 
    	ArrayList<Turno> turni=null;
    	Connection conn=null;
		try {
			conn= new Connection(this.getUtente());
			turni = conn.getTurni(dipendente, start, end);
		}catch (IOException | JSONException | TurnoException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		} catch (ConnectionException e) {
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
		this.turniWeekList= this.getTurniDipendente(this.dipendenteAssocia, this.weekStart, this.weekEnd);
		Instant instant;
		LocalDateTime res1, res2;
		agendaid.appointments().clear();
		Agenda.AppointmentImplLocal app;
		this.turniWeekMap.clear();
		
		for(Turno t: this.turniWeekList) {
			instant = Instant.ofEpochMilli(t.getDataInizio().getTime());
			res1 = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
			instant = Instant.ofEpochMilli(t.getDataFine().getTime());
			res2 = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
			
			
	    	LocalDateTime tempDateTime = LocalDateTime.from(res1);
	    	long hours = tempDateTime.until(res2, ChronoUnit.HOURS);
	    	tempDateTime = tempDateTime.plusHours(hours);
	    	
	    	
	    	app =new Agenda.AppointmentImplLocal()
    	            .withStartLocalDateTime(res1)
    	            .withEndLocalDateTime(res2)
    	            .withAppointmentGroup(new Agenda.AppointmentGroupImpl().withStyleClass("group11"));
	    	if(t.getIdLinea()!=-1) {
	    		app.setSummary(Integer.toString(t.getIdLinea()));
	    	}
	    	this.turniWeekMap.put(app, t);
			this.agendaid.appointments().add(app);
		}

		
    }
}

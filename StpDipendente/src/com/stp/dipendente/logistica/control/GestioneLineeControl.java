package com.stp.dipendente.logistica.control;

import java.io.IOException;
import java.time.LocalTime;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableColumn;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.stp.dipendente.control.ElencoLineeControl;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.model.Linea;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.LineaException;
import com.stp.dipendente.util.json.JSONException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class GestioneLineeControl extends ElencoLineeControl {
	private boolean initialCheck=false;
	
	public GestioneLineeControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}
	
	private Linea lineaModifica;
	
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
    private TextField idLinea;

    @FXML
    private JFXCheckBox checkFestivoCheckBox;

    @FXML
    private TextField nBusConsigliato;

    @FXML
    private TextField nBusConsigliatoFestivo;

    @FXML
	private JFXComboBox<LocalTime> oraInizioCorsa;

    @FXML
    private JFXComboBox<LocalTime> oraFineCorsa;

    @FXML
    private JFXComboBox<LocalTime> durata;

    @FXML
    private JFXComboBox<LocalTime> oraInizioCorsaFestivo;

    @FXML
    private JFXComboBox<LocalTime> oraFineCorsaFestivo;

    @FXML
    private JFXComboBox<LocalTime> durataFestivo;
    
    @FXML
    private AnchorPane footerPane;


    @FXML
    void checkFestivo() {
		if(checkFestivoCheckBox.isSelected()) {
    		this.oraInizioCorsaFestivo.setDisable(false);
    		this.oraFineCorsaFestivo.setDisable(false);
    		this.durataFestivo.setDisable(false);
    		this.nBusConsigliatoFestivo.setDisable(false);
		} else {
    		this.oraInizioCorsaFestivo.setDisable(true);
    		this.oraFineCorsaFestivo.setDisable(true);
    		this.durataFestivo.setDisable(true);
    		this.nBusConsigliatoFestivo.setDisable(true);
		}
    }
    
	@FXML
	public void aggiungiLinea() {
		this.lineaModifica=null;
		this.setCenter(800, 600);
		this.setXmlFileName("SchedaLinea");
		this.start();
	}

	public void cercaLinea() {
		super.cercaLinea();
    	this.getTreeTable().getColumns().remove(3);
    	this.addEliminaColumn();
	}
	
    @FXML
    void salvaLinea() {
    	Linea linea=null;
    	int id;
    	int nConsigliati=0;
    	int nConsigliatiFestivo = 0;
    	try {
    		id=Integer.parseInt(this.idLinea.getText());
        	if(id<1) {
        		throw new NumberFormatException();
        	}
    	}catch(NumberFormatException e) {
    		this.openDialog(this.parentStackPane, "Inserisci un numero di linea valido");
    		return;
    	}
    	if(this.oraInizioCorsa.getValue()==null) {
    		this.openDialog(this.parentStackPane, "Inserisci un orario di inizio corsa valido");
    		return;
    	}
    	if(this.oraFineCorsa.getValue()==null) {
    		this.openDialog(this.parentStackPane, "Inserisci un orario di fine corsa valido");
    		return;
    	}
    	if(this.durata.getValue()==null) {
    		this.openDialog(this.parentStackPane, "Inserisci una durata valida");
    		return;
    	}
    	if(this.nBusConsigliato.getText()==null){
    		this.openDialog(this.parentStackPane, "Inserisci un numero di autobus valido");
    		return;
    	}
    	try {
    		nConsigliati=Integer.parseInt(this.nBusConsigliato.getText());
        	if(id<1) {
        		throw new NumberFormatException();
        	}
    	}catch(NumberFormatException e) {
    		this.openDialog(this.parentStackPane, "Inserisci un numero di autobus valido");
    		return;
    	}
    	
    	if(this.oraInizioCorsa.getValue().compareTo(this.oraFineCorsa.getValue())>-1) {
    		this.openDialog(this.parentStackPane, "La data di fine corsa deve essere maggiore di quella di inizio");
    		return;
    	}
    	
    	if(this.checkFestivoCheckBox.isSelected()) {
        	if(this.oraInizioCorsaFestivo.getValue()==null) {
        		this.openDialog(this.parentStackPane, "Inserisci un orario di inizio corsa valido");
        		return;
        	}
        	if(this.oraFineCorsaFestivo.getValue()==null) {
        		this.openDialog(this.parentStackPane, "Inserisci un orario di fine corsa valido");
        		return;
        	}
        	if(this.durataFestivo.getValue()==null) {
        		this.openDialog(this.parentStackPane, "Inserisci una durata valida");
        		return;
        	}
        	if(this.nBusConsigliatoFestivo.getText()==null){
        		this.openDialog(this.parentStackPane, "Inserisci un numero di autobus valido");
        		return;
        	}
        	try {
        		nConsigliatiFestivo=Integer.parseInt(this.nBusConsigliatoFestivo.getText());
            	if(id<1) {
            		throw new NumberFormatException();
            	}
        	}catch(NumberFormatException e) {
        		this.openDialog(this.parentStackPane, "Inserisci un numero di autobus valido");
        		return;
        	}
        	if(this.oraInizioCorsaFestivo.getValue().compareTo(this.oraFineCorsaFestivo.getValue())>-1) {
        		this.openDialog(this.parentStackPane, "La data di fine corsa deve essere maggiore di quella di inizio");
        		return;
        	}
    	}
    	try {
	    	if(this.checkFestivoCheckBox.isSelected()) {
	    		linea= new Linea(id, this.oraInizioCorsa.getValue(), this.oraFineCorsa.getValue(), this.durata.getValue(), nConsigliati,
						this.oraInizioCorsaFestivo.getValue(), this.oraFineCorsaFestivo.getValue(), this.durataFestivo.getValue(), nConsigliatiFestivo);
	    		
	    	}else {
	    		linea= new Linea(id, this.oraInizioCorsa.getValue(), this.oraFineCorsa.getValue(), this.durata.getValue(), nConsigliati);
				
    		}
	    	
    	} catch (LineaException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
    	}
    	JFXButton btn = new JFXButton("OK");
		btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				GestioneLineeControl.this.gestioneLinee();
				
			}
		});
		Connection conn=null;
    	try {
    		
    		if(this.lineaModifica!=null) {
    			if(this.lineaModifica.getDurata().equals(linea.getDurata()) &&
    					this.lineaModifica.getnBusConsigliato()==linea.getnBusConsigliato() &&
    					this.lineaModifica.getOraInizioCorsa().equals(linea.getOraInizioCorsa()) &&
    					this.lineaModifica.getOraFineCorsa().equals(linea.getOraFineCorsa())) {
    				if(!(this.initialCheck^this.checkFestivoCheckBox.isSelected())) {
    					if(!this.initialCheck) {
    						return;
    					}else {
    						if(this.lineaModifica.getDurataFestivo().equals(linea.getDurataFestivo()) &&
    		    					this.lineaModifica.getnBusConsigliatoFestivo()==linea.getnBusConsigliatoFestivo() &&
    		    					this.lineaModifica.getOraInizioCorsaFestivo().equals(linea.getOraInizioCorsaFestivo()) &&
    		    					this.lineaModifica.getOraFineCorsaFestivo().equals(linea.getOraFineCorsaFestivo())) {
    							return;
    						}
    					}
    				}
    			}
    			conn=new Connection(this.getUtente());
    			if(conn.modificaLinea(linea)) {
    				this.openDialog(this.parentStackPane, "Linea aggiornata correttamente", btn);
    			}
    			
    		}else {
    			conn=new Connection(this.getUtente());
        		switch(conn.aggiungiLinea(linea)) {
        			case 1:
        				this.openDialog(this.parentStackPane, "Linea aggiunta correttamente", btn);
        			break;
        			case -2:
        				this.openDialog(this.parentStackPane, "La linea inserita esiste già");
        			break;
        			default:
        				this.openDialog(this.parentStackPane, "Errore, riprovare più tardi");
        			break;
        		}
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
    	
    	
    	
    	/*if(this.idLinea.getText().length()>0 && this.oraInizioCorsa.getValue() && this.oraInizioCorsa.getValue()!=null && this.oraFineCorsa.getValue()!=null && nBusConsigliato) {
			try {
				GeocodingResult[] results =  GeocodingApi.geocode(this.geocodingService, this.via.getText()+" "+this.civico.getText()+" , Palermo").await();
				if(results.length>0) {
					Connection conn = new Connection();
					if(conn.aggiungiFermata(new Fermata(1 ,this.via.getText(),this.civico.getText(), results[0].geometry.location.lat, results[0].geometry.location.lng), this.getUtente())) {
						JFXButton btn = new JFXButton("OK");
						btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
							@Override
							public void handle(MouseEvent event) {
								//GestioneLineeControl.this.gestioneFermate();
								
							}
						});
						this.openDialog(this.parentStackPane, "Fermata aggiunta correttamente", btn);
					}else {
						this.openDialog(this.parentStackPane, "Errore, riprovare più tardi");
					}
				}else{
					this.openDialog(this.parentStackPane, "Nessun indirizzo trovato");
				}
				
				
				
			} catch (ApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else {
			this.openDialog(this.parentStackPane, "Inserire indirizzo e numero civico corretti");
		}*/
    }
    
	@FXML
	public void annulla() {
		this.setCenter(1200, 800);
		this.setPkg("");
		this.setXmlFileName("ElencoLinee");
		this.start();
	}
    
    void gestioneLinee() {
    	this.setCenter(1200, 800);
		this.setPkg("");
    	this.setXmlFileName("ElencoLinee");
    	this.start();
    }
    
	@FXML
	public void indietro() {
		this.setPkg("");
		this.setCenter(1200, 800);
		this.setXmlFileName("ElencoLinee");
		this.start();
	}
    
    @Override
	public void init() {
		super.init();
		this.setPkg("logistica/");
		if(this.getXmlFileName().equals("SchedaLinea")) {
			Callback<ListView<LocalTime>, ListCell<LocalTime>> cellFactory =new Callback<ListView<LocalTime>, ListCell<LocalTime>>(){

				@Override
				public ListCell<LocalTime> call(ListView<LocalTime> param) {
					return new ListCell<LocalTime>(){
	                    @Override
	                    protected void updateItem(LocalTime item, boolean empty) {
	                        super.updateItem(item, empty);
	                        if (item == null || empty) {
	                            setGraphic(null);
	                        } else {
	                            setText(item.toString());
	                        }
	                    }
	                } ;
				}
				
			};
			this.oraInizioCorsa.setCellFactory(cellFactory);
			this.oraFineCorsa.setCellFactory(cellFactory);
			this.oraInizioCorsaFestivo.setCellFactory(cellFactory);
			this.oraFineCorsaFestivo.setCellFactory(cellFactory);
			this.durata.setCellFactory(cellFactory);
			
			StringConverter<LocalTime> converter=new StringConverter<LocalTime>() {
  	              @Override
  	              public String toString(LocalTime user) {
  	                if (user == null){
  	                  return null;
  	                } else {
  	                  return user.toString();
  	                }
  	              }

  	            @Override
  	            public LocalTime fromString(String userId) {
  	                return null;
  	            }
  	        };
			
  	        this.oraInizioCorsa.setConverter(converter);
  			this.oraFineCorsa.setConverter(converter);
  			this.oraInizioCorsaFestivo.setConverter(converter);
  			this.oraFineCorsaFestivo.setConverter(converter);
  			this.durataFestivo.setConverter(converter);
  			
  			ObservableList<LocalTime> timeInterval =FXCollections.observableArrayList();
  			
  			for(int i=0;i<48; i++) {
  				timeInterval.add(LocalTime.of(0,0).plusMinutes(30*i));
			}
  			this.oraInizioCorsa.setItems(timeInterval);
  			this.oraFineCorsa.setItems(timeInterval);
  			this.oraInizioCorsaFestivo.setItems(timeInterval);
  			this.oraFineCorsaFestivo.setItems(timeInterval);
  			
  			ObservableList<LocalTime> durataInterval =FXCollections.observableArrayList();
  			
  			for(int i=1;i<=12; i++) {
  				durataInterval.add(LocalTime.of(0,0).plusMinutes(10*i));
			}
  			this.durata.setItems(durataInterval);
  			this.durataFestivo.setItems(durataInterval);
  			
  			
  			if(this.lineaModifica==null) {
  				
  			}else {
  				this.idLinea.setText(Integer.toString(this.lineaModifica.getIdLinea()));
  				this.idLinea.setDisable(true);
  				this.oraInizioCorsa.setValue(this.lineaModifica.getOraInizioCorsa());
  				this.oraFineCorsa.setValue(this.lineaModifica.getOraFineCorsa());
  				this.durata.setValue(this.lineaModifica.getDurata());
  				this.nBusConsigliato.setText(Integer.toString(this.lineaModifica.getnBusConsigliato()));
  				if(this.lineaModifica.getOraInizioCorsaFestivo()!=null &&
  						this.lineaModifica.getOraFineCorsaFestivo()!=null &&
  						this.lineaModifica.getDurataFestivo()!=null &&
  						this.lineaModifica.getnBusConsigliatoFestivo()!=0
  						) {
  					this.checkFestivoCheckBox.setSelected(true);
  					this.initialCheck=true;
  					
  					this.checkFestivo();
  					this.oraInizioCorsaFestivo.setValue(this.lineaModifica.getOraInizioCorsaFestivo());
  	  				this.oraFineCorsaFestivo.setValue(this.lineaModifica.getOraFineCorsaFestivo());
  	  				this.durataFestivo.setValue(this.lineaModifica.getDurataFestivo());
  	  				this.nBusConsigliatoFestivo.setText(Integer.toString(this.lineaModifica.getnBusConsigliatoFestivo()));
  				}
  				
  				
  				JFXButton aggiungiFermate = new JFXButton("Aggiungi Fermate");
  				AnchorPane.setLeftAnchor(aggiungiFermate, 60.0);
  				AnchorPane.setTopAnchor(aggiungiFermate, 20.0);
  				aggiungiFermate.setButtonType(ButtonType.RAISED);
  				aggiungiFermate.setFont(Font.font(15.0));
  				aggiungiFermate.setStyle("-fx-background-color:  #f0f0f0");
  				this.footerPane.getChildren().add(aggiungiFermate);
  				aggiungiFermate.setOnMouseClicked(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						GestioneLineeControl.this.getStage().hide();
						Stage window2=new Stage();
						window2.setTitle("Servizio Trasporti Palermo");
						window2.getIcons().add(GestioneLineeControl.this.getStage().getIcons().get(0));
						GestioneLineeControl.this.setCenter(1200, 800);
						new AssociaFermataALineaControl("AssociaFermataALinea", GestioneLineeControl.this.getStage(), GestioneLineeControl.this.getUtente()).setLinea(GestioneLineeControl.this.lineaModifica).setPkg("logistica/").start();
						
					}
  					
  				});
  				
  			}
  			
		}else if(this.getXmlFileName().equals("ElencoLinee")){
			this.addEliminaColumn();
		}
    }
    
    public void action(LineaRecursive linea) {
    		this.lineaModifica=linea.getLinea();
    		this.setCenter(800, 600);
    		this.setXmlFileName("SchedaLinea");
    		this.start();
    		
    }
    
    public GestioneLineeControl setLinea(Linea linea) {
    	this.lineaModifica=linea;
    	return this;
    }
    
    
    public void addEliminaColumn() {
		JFXTreeTableColumn<LineaRecursive,String> eliminaButton = new JFXTreeTableColumn<LineaRecursive,String>("Elimina");
		eliminaButton.setSortable(false);
		
		eliminaButton.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineaRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<LineaRecursive, String> param) {
				return new SimpleStringProperty("Elimina");
            }
        });
		eliminaButton.setCellFactory(new Callback<TreeTableColumn<LineaRecursive,String>, TreeTableCell<LineaRecursive,String>>(){

			@Override
			public TreeTableCell<LineaRecursive, String> call(TreeTableColumn<LineaRecursive, String> param) {
				return new ButtonCell();
			}
			
		});
		this.getTreeTable().getColumns().add(eliminaButton);
	}
    
    private class ButtonCell extends TreeTableCell<LineaRecursive, String> {
        final JFXButton buttonElimina = new JFXButton("Elimina");

        public ButtonCell(){
        	this.setAlignment(Pos.CENTER);
        	buttonElimina.setButtonType(ButtonType.FLAT);
        	buttonElimina.textProperty().bind(itemProperty());
        	buttonElimina.setMaxHeight(30.0);
        	buttonElimina.setMinHeight(30.0);
        	buttonElimina.setPrefHeight(30.0);
        	buttonElimina.setStyle("-fx-background-color: #f0f0f0 ;");
        	buttonElimina.setFont(Font.font ("System", 12));
        	this.setStyle("-fx-padding: 0 0 0 0");
        	
        	buttonElimina.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                	GestioneLineeControl.this.eliminaLinea(((LineaRecursive) getTreeTableRow().getItem()));
                	//ButtonCell.this.getChildren().remove(ButtonCell.this.buttonElimina);
                }
            });

        }

        @Override
        protected void updateItem(String t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(buttonElimina);
                
            }
        }
    }
    
    public void eliminaLinea(LineaRecursive linea) {
    	Connection conn=null;
    	try {
    		conn = new Connection(this.getUtente());
        	if(conn.rimuoviLinea(linea.getLinea())) {
        		this.getElencoLinee().remove(linea.getLinea());
            	this.getLineeObservable().remove(linea);
            	this.getTreeTable().getColumns().remove(3);
            	this.addEliminaColumn();
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
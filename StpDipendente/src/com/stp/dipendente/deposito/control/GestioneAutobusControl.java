package com.stp.dipendente.deposito.control;

import java.io.IOException;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.stp.dipendente.control.ElencoAutobusControl;
import com.stp.dipendente.model.Autobus;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.json.JSONException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

public class GestioneAutobusControl extends ElencoAutobusControl{
	private JFXButton addAutobus;
	private Autobus autobusModifica;
	
	
    @FXML
    private JFXButton salvaButton;

    @FXML
    private JFXButton annullaButton;

    @FXML
    private TextField targa;

    @FXML
    private TextField postiSeduti;

    @FXML
    private TextField postiInPiedi;

    @FXML
    private TextField postiPerDisabili;
    
    @FXML
    private AnchorPane parentStackPane;
    
    @FXML
    private AnchorPane footerPane;
    
    
    
    @FXML
    private void salvaAutobus(ActionEvent event) {
    	Autobus aut=null;
		try {
			if(this.targa.getText()==null || this.targa.getText().length()!=7) {
				this.openDialog(this.parentStackPane, "Inserire un numero di targa valido");
				return;
			}
			String targa=this.targa.getText();
			
			if(this.postiInPiedi.getText()==null) {
				this.openDialog(this.parentStackPane, "Inserire numero valido per i posti in piedi");
				return;
			}
			int postiInPiediInt;
			try {
				postiInPiediInt=Integer.parseInt(this.postiInPiedi.getText());
			}catch(NumberFormatException e) {
				this.openDialog(this.parentStackPane, "Inserire numero valido per i posti in piedi");
				return;
			}
			if(this.postiSeduti.getText()==null) {
				this.openDialog(this.parentStackPane, "Inserire numero valido per i posti seduti");
				return;
			}
			int postiSedutiInt;
			try {
				postiSedutiInt=Integer.parseInt(this.postiSeduti.getText());
			}catch(NumberFormatException e) {
				this.openDialog(this.parentStackPane, "Inserire numero valido per i posti seduti");
				return;
			}
			if(this.postiPerDisabili.getText()==null) {
				this.openDialog(this.parentStackPane, "Inserire numero valido per i posti per disabili");
				return;
			}
			int postiPerDisabiliInt;
			try {
				postiPerDisabiliInt=Integer.parseInt(this.postiPerDisabili.getText());
			}catch(NumberFormatException e) {
				this.openDialog(this.parentStackPane, "Inserire numero valido per i posti per disabili");
				return;
			}
			
			aut = new Autobus(targa,
					postiInPiediInt,
					postiSedutiInt,
					postiPerDisabiliInt,
					Autobus.PresenzaAutobus.PRESENTE,
					Autobus.StatoAutobus.UTILIZZABILE,
					null);
			
			JFXButton btn= new JFXButton("OK");
			btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					GestioneAutobusControl.this.elencoAutobus();
				}
				
			});
			
    		Connection conn=null;
			try {
				conn = new Connection(this.getUtente());
				switch(conn.addAutobus(aut)){
					case 1:
						this.openDialog(this.parentStackPane, "Autobus inserito correttamente.", btn);
					break;
					case -2:
						this.openDialog(this.parentStackPane, "Autobus già presente.");
					break;
					default:
						this.openDialog(this.parentStackPane, "Errore, riprovare più tardi.");
					break;
				}
			} catch (IOException | JSONException e) {
				this.openDialog(this.parentStackPane, "Errore: verificare la connessione o riprovare più tardi");
			}catch (ConnectionException e) {
	    		this.openDialog(this.parentStackPane, e.getMessage());
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}
    		
		}catch(NullPointerException e) {
			this.openDialog(this.parentStackPane, "Compila i campi");
		}
    }

    @FXML
    void annullaModifica() {
    	this.elencoAutobus();
    }
    
    void elencoAutobus() {
    	this.autobusModifica=null;
    	this.setCenter(1200, 800);
    	this.setPkg("");
		this.setXmlFileName("ElencoAutobus");
		this.start();
    }
    
    
    @Override
    public void cercaAutobus() {
    	super.cercaAutobus();
    	this.getTreeTable().getColumns().remove(3);
    	this.addEliminaColumn();
    }
    
    
    @Override
	public void init() {
    	super.init();
    	
    	if(this.getXmlFileName().equals("ElencoAutobus")) {
    		this.addAutobus = new JFXButton("Aggiungi Autobus");
        	AnchorPane.setTopAnchor(addAutobus, 15.0);
        	AnchorPane.setRightAnchor(addAutobus, 10.0);
        	this.addAutobus.setButtonType(JFXButton.ButtonType.RAISED);
        	this.addAutobus.setFont(new Font(15.0));
        	this.addAutobus.setStyle("-fx-background-color: #f0f0f0");
        	this.getContentHeader().getChildren().add(this.addAutobus);
        	
        	this.addAutobus.setOnMouseClicked(new EventHandler<MouseEvent>() {
    			@Override
    			public void handle(MouseEvent event) {
    				GestioneAutobusControl.this.autobusModifica=null;
    				GestioneAutobusControl.this.setCenter(800, 600);
    				GestioneAutobusControl.this.setPkg("deposito/");
					GestioneAutobusControl.this.setXmlFileName("SchedaAutobus");
					GestioneAutobusControl.this.start();
    			}
        		
        	});
        	this.addEliminaColumn();
        	
    	}else if(this.getXmlFileName().equals("SchedaAutobus")) {
    		if(this.autobusModifica==null) {
    			this.targa.setDisable(false);
    			this.postiSeduti.setDisable(false);
    			this.postiInPiedi.setDisable(false);
    			this.postiPerDisabili.setDisable(false);
    		}else {
    			this.targa.setText(this.autobusModifica.getTarga());
    			this.postiSeduti.setText(Integer.toString(this.autobusModifica.getPostiSeduti()));
    			this.postiInPiedi.setText(Integer.toString(this.autobusModifica.getPostiInPiedi()));
    			this.postiPerDisabili.setText(Integer.toString(this.autobusModifica.getPostiPerDisabili()));
    			this.targa.setDisable(true);
    			this.postiSeduti.setDisable(true);
    			this.postiInPiedi.setDisable(true);
    			this.postiPerDisabili.setDisable(true);
    			this.footerPane.getChildren().remove(this.salvaButton);
    			AnchorPane.setLeftAnchor(this.annullaButton, 40.0);
    			AnchorPane.setRightAnchor(this.annullaButton, null);
    			this.annullaButton.setText("Indietro");

    		}
    	}
    	
    }
    
    
    
	public GestioneAutobusControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
	}
	
	
	@Override
    public void action(AutobusRecursive autobus) {
		this.autobusModifica=autobus.getAutobus();
		this.setCenter(800, 600);
		this.setPkg("deposito/");
		this.setXmlFileName("SchedaAutobus");
		this.start();
    }
	
	public void eliminaAutobus(AutobusRecursive autobus) {
		Connection conn = null;
		try {
			conn= new Connection(this.getUtente());
			JFXButton btn= new JFXButton("OK");
			btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					GestioneAutobusControl.this.elencoAutobus();
				}
				
			});
			if(conn.eliminaAutobus(autobus.getAutobus())) {
				this.getElencoAutobus().remove(autobus.getAutobus());
				this.getAutobusObservable().remove(autobus);
            	this.getTreeTable().getColumns().remove(3);
            	this.addEliminaColumn();
				this.openDialog(this.parentStackPane, "Autobus eliminato correttamente.");
				return;
			}
			this.openDialog(this.parentStackPane, "Errore: verificare la connessione o riprovare più tardi");
		} catch (IOException | JSONException e) {
			this.openDialog(this.parentStackPane, "Errore: verificare la connessione o riprovare più tardi");
		}catch (ConnectionException e) {
    		this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
	}
	
	private class ButtonCell extends TreeTableCell<AutobusRecursive, String> {
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
                	GestioneAutobusControl.this.eliminaAutobus(((AutobusRecursive) getTreeTableRow().getItem()));
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
	
	public void addEliminaColumn() {
		JFXTreeTableColumn<AutobusRecursive,String> eliminaButton = new JFXTreeTableColumn<AutobusRecursive,String>("Elimina");
		eliminaButton.setSortable(false);
		
		eliminaButton.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<AutobusRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<AutobusRecursive, String> param) {
				return new SimpleStringProperty("Elimina");
            }
        });
		eliminaButton.setCellFactory(new Callback<TreeTableColumn<AutobusRecursive,String>, TreeTableCell<AutobusRecursive,String>>(){

			@Override
			public TreeTableCell<AutobusRecursive, String> call(TreeTableColumn<AutobusRecursive, String> param) {
				return new ButtonCell();
			}
			
		});
		this.getTreeTable().getColumns().add(eliminaButton);
	}
}

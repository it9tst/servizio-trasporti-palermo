package com.stp.dipendente.deposito.control;

import java.io.IOException;

import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.stp.dipendente.control.ElencoAutobusControl;
import com.stp.dipendente.control.ElencoDipendentiControl;
import com.stp.dipendente.model.Autobus;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

public class EntrataUscitaAutobusControl extends ElencoAutobusControl{
	private AutobusRecursive autobusSelezione;
	private ToggleButton toggleSelezione;
	
    public EntrataUscitaAutobusControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}

    
    @Override
    public void cercaAutobus() {
    	super.cercaAutobus();
    	this.getTreeTable().getColumns().remove(2);
    	this.addActionButton();
    }

	@Override
	public void init() {
    	super.init();
    	this.getTreeTable().getColumns().remove(2);
    	this.addActionButton();
		
		
    }
    
    private class ButtonCell extends TreeTableCell<AutobusRecursive, String> {
        final JFXToggleButton toogleButton = new JFXToggleButton();

        public ButtonCell(){
        	toogleButton.textProperty().bind(itemProperty());
        	toogleButton.setMaxHeight(10.0);
        	toogleButton.setMinHeight(10.0);
        	toogleButton.setPrefHeight(10.0);
        	toogleButton.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                	action(((AutobusRecursive) getTreeTableRow().getItem()));
                }
            });
        	
        	/**/
        }

        public void action(AutobusRecursive aut) {
        	EntrataUscitaAutobusControl.this.toggleSelezione=this.toogleButton;
        	if(aut.getAutobus().getStatoPresenza()==Autobus.PresenzaAutobus.PRESENTE) {
        		EntrataUscitaAutobusControl.this.autobusSelezione=aut;
            	EntrataUscitaAutobusControl.this.openElencoDipendenti();
        	}else {
        		EntrataUscitaAutobusControl.this.registraEntrata(aut.getAutobus());
        	}
        	
        }
        //Display button if the row is not empty
        @Override
        protected void updateItem(String t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(toogleButton);
                
                if((((AutobusRecursive) getTreeTableRow().getItem()).getPresenza()).getValue().equals("Presente")){
            		this.toogleButton.setSelected(true);
            	}else {
            		this.toogleButton.setSelected(false);
            	}
            }
        }
    }
    
    private void openElencoDipendenti() {
    	Stage s= new Stage();
    	s.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
            	EntrataUscitaAutobusControl.this.toggleSelezione.setSelected(true);
            }
        });  
    	new ElencoDipendentiControl("ElencoDipendenti", s, this.getUtente()) {
    		@Override
			public void action(Dipendente dipendente) {
    			EntrataUscitaAutobusControl.this.registraUscita(dipendente);
    			this.getStage().close();
    		}
    		@Override
			public void menuDipendente() {
    			
    			this.getStage().close();
    		}
    	}.setFiltro(4).start();
    }
    public void registraUscita(Dipendente dipendente) {
    	if(this.autobusSelezione!= null && dipendente!= null) {
    		Connection conn=null;
    		try {
				conn= new Connection(this.getUtente());
				switch(conn.registraUscita(dipendente, this.autobusSelezione.getAutobus())) {
					case 1:
						this.autobusSelezione.tooglePresenza();
						this.autobusSelezione.getAutobus().setStatoPresenza(Autobus.PresenzaAutobus.ASSENTE);
					break;
					case -2:
						this.openDialog(this.parentStackPane, "Autista già uscito");
						this.toggleSelezione.setSelected(true);
					break;
					default:
						this.toggleSelezione.setSelected(true);
					break;
					
				}
			} catch (IOException e) {
    			this.openDialog(this.parentStackPane, "Errore connessione");
    		}catch (ConnectionException e) {
	    		this.openDialog(this.parentStackPane, e.getMessage());
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}
    	}else {
    		this.openDialog(this.parentStackPane, "Selezione errata riprova");
    	}
    	this.getScene().getRoot().requestFocus();
    }
    public void registraEntrata(Autobus autobus) {
    	Connection conn=null;
    	try {
			conn= new Connection(this.getUtente());
			if(conn.registraEntrata(autobus)) {
				this.autobusSelezione.tooglePresenza();
				this.autobusSelezione.getAutobus().setStatoPresenza(Autobus.PresenzaAutobus.PRESENTE);
			}else {
				this.toggleSelezione.setSelected(false);
			}
			this.getScene().getRoot().requestFocus();
		} catch (IOException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		}catch (ConnectionException e) {
    		this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    }
    
    public void addActionButton() {
    	JFXTreeTableColumn<AutobusRecursive,String> actionButton = new JFXTreeTableColumn<AutobusRecursive,String>("Action");
		actionButton.setSortable(false);
		/*actionButton.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<Autobus, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<Autobus, String> param) {
				return param.getValue().getValue().getAction();
            }
        });*/
		
		actionButton.setCellFactory(new Callback<TreeTableColumn<AutobusRecursive,String>, TreeTableCell<AutobusRecursive,String>>(){

			@Override
			public TreeTableCell<AutobusRecursive, String> call(TreeTableColumn<AutobusRecursive, String> param) {
				return new ButtonCell();
			}
			
		});
		this.getTreeTable().getColumns().add(actionButton);
    }
}

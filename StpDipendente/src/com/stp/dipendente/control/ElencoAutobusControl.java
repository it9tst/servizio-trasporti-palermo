package com.stp.dipendente.control;


import java.io.IOException;
import java.util.List;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;

import com.jfoenix.controls.JFXTreeTableColumn;

import javafx.scene.control.TreeItem;

import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;

import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import com.stp.dipendente.model.Autobus;

import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.json.JSONException;
import com.stp.dipendente.model.Dipendente;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.util.Callback;

public class ElencoAutobusControl extends MainControlLogged {
	private ObservableList<AutobusRecursive> autobusObservable;
	private List<Autobus> elencoAutobus;
	
	public List<Autobus> getElencoAutobus(){
		return this.elencoAutobus;
	}
	public ObservableList<AutobusRecursive> getAutobusObservable(){
		return this.autobusObservable;
	}
	
	@FXML
	private JFXTreeTableView<AutobusRecursive> table;
	 
    @FXML
    private JFXButton indietroButton;

    @FXML
    private Text usernameDipendente;
    
    @FXML
    private JFXTextField cercaAutobus;

    @FXML
    private JFXButton cercaAutobusButton;
    
    @FXML
    private AnchorPane contentHeader;
    
    @FXML
    protected AnchorPane parentStackPane;
    
	public ElencoAutobusControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
	}
    
    @FXML
    public void cercaAutobus() {
    	if(this.cercaAutobus.getText().length()>0) {
    		Connection conn=null;
    		try {
    			conn = new Connection(this.getUtente());

				this.elencoAutobus= conn.cercaAutobus(this.cercaAutobus.getText());
				int index=0;
				this.autobusObservable.clear();
				for(Autobus t: this.elencoAutobus) {
					this.autobusObservable.add(new AutobusRecursive(index++,t));
				}


    		} catch (IOException | JSONException e) {
    			this.openDialog(this.parentStackPane, "Errore connessione");
    		}catch (ConnectionException e) {
	    		this.openDialog(this.parentStackPane, e.getMessage());
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}
    	}else {
    		this.autobusObservable.clear();
    		this.getAutobus();
    	}
    	
    }
    
    @FXML
    public AnchorPane getContentHeader() {
    	return this.contentHeader;
    }
	
    public JFXTreeTableView<AutobusRecursive> getTreeTable() {
    	return table;
    }

	public class AutobusRecursive extends RecursiveTreeObject<AutobusRecursive>{
		private StringProperty targa;
		private StringProperty statoPresenza;
		private StringProperty statoGuasto;
		private Autobus autobus;
		private int index;
		
		public AutobusRecursive(int index, Autobus autobus){
			this.setIndex(index);
			this.targa = new SimpleStringProperty(autobus.getTarga());
			this.statoPresenza  = new SimpleStringProperty(autobus.getStatoPresenza().getNome());
			this.statoGuasto  = new SimpleStringProperty(autobus.getStatoGuasto().getNome());
			this.autobus=autobus;
			
		}

		public StringProperty getTarga() {
			return this.targa;
		}
		public StringProperty getPresenza() {
			return this.statoPresenza;
		}
		public StringProperty getStatoGuasto() {
			return this.statoGuasto;
		}
		public void tooglePresenza() {
			if(statoPresenza.getValue().equals("Presente")) {
				this.statoPresenza.set("Assente");
			}else {
				this.statoPresenza.set("Presente");
			}
		}
		public String getMotivazioneGuasto() {
			return this.autobus.getDescrizioneGuasto();
		}
		public Autobus getAutobus() {
			return this.autobus;
		}
		public void setStatoGuasto(Autobus.StatoAutobus stato) {
			this.autobus.setStatoGuasto(stato);
			this.statoGuasto.set(autobus.getStatoGuasto().getNome());
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}
	
	

    @FXML
    void menuDipendente() {
		this.setCenter(800, 600);
		new MenuDipendenteControl("MenuDipendente", this.getStage(), this.getUtente()).start();
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public void init() {
    	super.init();
    	if(this.getXmlFileName().equals("ElencoAutobus")) {
    		JFXTreeTableColumn<AutobusRecursive,String> targa = new JFXTreeTableColumn<AutobusRecursive,String>("Targa");
        	
    		targa.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<AutobusRecursive, String>, ObservableValue<String>>() {
    	          
    			@Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<AutobusRecursive, String> param) {
                    return param.getValue().getValue().targa ;
                }
            });
    		
    		JFXTreeTableColumn<AutobusRecursive,String> statoPresenza = new JFXTreeTableColumn<AutobusRecursive,String>("Stato Presenza");
    		
    		statoPresenza.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<AutobusRecursive, String>, ObservableValue<String>>() {
    	          
    			@Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<AutobusRecursive, String> param) {
                    return param.getValue().getValue().statoPresenza;
                }
    			
            });
    		
    		JFXTreeTableColumn<AutobusRecursive,String> statoGuasto = new JFXTreeTableColumn<AutobusRecursive,String>("Stato Guasto");
    		
    		statoGuasto.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<AutobusRecursive, String>, ObservableValue<String>>() {
    	          
    			@Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<AutobusRecursive, String> param) {
                    return param.getValue().getValue().statoGuasto;
                }
    			
            });
    		this.autobusObservable = FXCollections.observableArrayList();

    		this.getAutobus();
    		    		
    		final TreeItem<AutobusRecursive> root = new RecursiveTreeItem<AutobusRecursive>(this.autobusObservable, RecursiveTreeObject::getChildren);
    		table.getColumns().setAll(targa, statoPresenza, statoGuasto);
    		table.setRoot(root);
    		table.setShowRoot(false);
    	
    		this.getTreeTable().setRowFactory(new Callback<TreeTableView<AutobusRecursive>, TreeTableRow<AutobusRecursive>>(){
    			
    			@Override
    			public TreeTableRow<AutobusRecursive> call(TreeTableView<AutobusRecursive> view) {
    				TreeTableRow<AutobusRecursive> row = new TreeTableRow<AutobusRecursive>();
    				row.setOnMouseClicked(new EventHandler<MouseEvent> () {
    					@Override
    					public void handle(MouseEvent event) {
    						if(event.getClickCount()==2 && !row.isEmpty()) {
    							ElencoAutobusControl.this.action(row.getItem());
    							
    						}
    						
    					}
    					
    				});
    				return row;
    			}
        		
        	});
    		
    		
    	}		
    }
    
    private void getAutobus() {
    	Connection conn=null;
		try {
			conn = new Connection(this.getUtente());

			this.elencoAutobus= conn.getAutobus();
			int index=0;
			this.autobusObservable.clear();
			for(Autobus t: this.elencoAutobus) {
				this.autobusObservable.add(new AutobusRecursive(index++,t));
			}


		} catch (IOException | JSONException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		}catch (ConnectionException e) {
    		this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    }
    
    public void action(AutobusRecursive autobus) {
    	System.out.print(autobus);
    }
    
}
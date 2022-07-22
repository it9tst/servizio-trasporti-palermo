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

import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import com.stp.dipendente.model.Dipendente;

import com.stp.dipendente.model.Linea;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.LineaException;
import com.stp.dipendente.util.json.JSONException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.util.Callback;

public class ElencoLineeControl extends MainControlLogged {
	private List<Linea> elencoLinee;
	private ObservableList<LineaRecursive> lineeObservable;

	public ElencoLineeControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}

	public List<Linea> getElencoLinee(){
		return this.elencoLinee;
	}
	public ObservableList<LineaRecursive> getLineeObservable(){
		return this.lineeObservable;
	}
	
	@FXML
	private JFXTreeTableView<LineaRecursive> table;
	 
    @FXML
    private JFXButton indietroButton;

    @FXML
    private Text usernameDipendente;

    @FXML
    private Circle timbro;
    
    @FXML
    private JFXButton aggiungiLineaButton;
    
    @FXML
    private JFXTextField cercaLinea;

    @FXML
    private JFXButton cercaLineaButton;
    
    @FXML
    private AnchorPane parentStackPane;
    
    @FXML
    private AnchorPane headerPane;
    
    @FXML
    public void cercaLinea() {
    	if(this.cercaLinea.getText().length()<1) {
    		this.getLinee();
    		return;
    				
    	}
    	Connection conn=null;
		try {
			conn = new Connection(this.getUtente());
			this.elencoLinee= conn.cercaLineaElenco(this.cercaLinea.getText());
			int index=0;
			this.lineeObservable.clear();
			for(Linea t: this.elencoLinee) {
				this.lineeObservable.add(new LineaRecursive(index,t));
			}
		}catch (IOException | JSONException | LineaException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		} catch (ConnectionException e) {
			this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    }
    
    @FXML
	public void aggiungiLinea() {
    		
    }
	
    public JFXTreeTableView<LineaRecursive> getTreeTable() {
    	return this.table;
    }
    
    public JFXButton getAggiungiLineaButton() {
    	return this.aggiungiLineaButton;
    }
    
	public class LineaRecursive extends RecursiveTreeObject<LineaRecursive>{
		StringProperty idLinea;
		StringProperty capPartenza;
		StringProperty capArrivo;
		private Linea linea;
		int index;
		
		public LineaRecursive(int index, Linea linea){
			this.index=index;
			this.linea = linea;
			this.idLinea = new SimpleStringProperty(Integer.toString(linea.getIdLinea()));
			if(linea.getCapA()!=null) {
				this.capPartenza  = new SimpleStringProperty(linea.getCapA().toString());
			}else {
				this.capPartenza  = new SimpleStringProperty("--");
			}
			if(linea.getCapB()!=null) {
				this.capArrivo  = new SimpleStringProperty(linea.getCapB().toString());
			}else {
				this.capArrivo  = new SimpleStringProperty("--");
			}
			
		}

		public Linea getLinea() {
			return linea;
		}
	}

    @FXML
    public void menuDipendente() {
		this.setCenter(800, 600);
		new MenuDipendenteControl("MenuDipendente", this.getStage(), this.getUtente()).start();
    }
    

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
    	super.init();
    	
    	if(this.getXmlFileName().equals("ElencoLinee")) {
    		JFXTreeTableColumn<LineaRecursive,String> idLinea = new JFXTreeTableColumn<LineaRecursive,String>("ID Linea");
        	
    		idLinea.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineaRecursive, String>, ObservableValue<String>>() {
    	          
    			@Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<LineaRecursive, String> param) {
                    return param.getValue().getValue().idLinea ;
                }
            });
    		
    		JFXTreeTableColumn<LineaRecursive,String> capPartenza = new JFXTreeTableColumn<LineaRecursive,String>("Cap. Partenza");
    		
    		capPartenza.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineaRecursive, String>, ObservableValue<String>>() {
    	          
    			@Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<LineaRecursive, String> param) {
                    return param.getValue().getValue().capPartenza ;
                }
            });
    		
    		JFXTreeTableColumn<LineaRecursive,String> capArrivo = new JFXTreeTableColumn<LineaRecursive,String>("Cap. Arrivo");
    		
    		capArrivo.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineaRecursive, String>, ObservableValue<String>>() {
    	          
    			@Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<LineaRecursive, String> param) {
                    return param.getValue().getValue().capArrivo ;
                }
            });
    		
    		this.lineeObservable = FXCollections.observableArrayList();
    		this.getLinee();
    		/*try {
    			//linee.add(new LineaRecursive(101, "Stadio", "Stazione Centrale", new Linea(101, "07:00:00", "21:00:00", "00:10:00", 20, "07:00:00", "21:00:00", "00:10:00", 20)));
    			//linee.add(new LineaRecursive(101, "Stadio", "Stazione Centrale", new Linea(109, "08:00:00", "22:00:00", "00:20:00", 10, "07:00:00", "21:00:00", "00:10:00", 20)));
    			//linee.add(new LineaRecursive(101, "Stadio", "Stazione Centrale", new Linea(234, "09:00:00", "21:00:00", "00:30:00", 20, "07:00:00", "21:00:00", "00:10:00", 20)));
    		} catch (LineaException e) {
    			System.out.println("Ehi");
    		}*/
    		
    		final TreeItem<LineaRecursive> root = new RecursiveTreeItem<LineaRecursive>(this.lineeObservable, RecursiveTreeObject::getChildren);
    		table.getColumns().setAll(idLinea, capPartenza ,capArrivo);
    		table.setRoot(root);
    		table.setShowRoot(false);
    		
    		table.setRowFactory(new Callback<TreeTableView<LineaRecursive>, TreeTableRow<LineaRecursive>>(){
    			
    			@Override
    			public TreeTableRow<LineaRecursive> call(TreeTableView<LineaRecursive> view) {
    				TreeTableRow<LineaRecursive> row = new TreeTableRow<LineaRecursive>();
    				row.setOnMouseClicked(new EventHandler<MouseEvent> () {
    					@Override
    					public void handle(MouseEvent event) {
    						if(event.getClickCount()==2 && !row.isEmpty()) {
    							ElencoLineeControl.this.action(row.getItem());
    						}
    					}
    				});
    				return row;
    			}
        	});
    	}
    }

	public void action(LineaRecursive item) {
		
		
	}
	
	
	public void getLinee() {
    	Connection conn=null;
		try {
			conn = new Connection(this.getUtente());
			this.elencoLinee= conn.getLinee();
			int index=0;
			for(Linea t: this.elencoLinee) {
				this.lineeObservable.add(new LineaRecursive(index,t));
			}
		}catch (IOException | JSONException | LineaException e) {
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

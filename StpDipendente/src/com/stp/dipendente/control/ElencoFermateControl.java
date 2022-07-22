package com.stp.dipendente.control;


import java.io.IOException;
import java.util.ArrayList;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.model.Fermata;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.json.JSONException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.util.Callback;

public class ElencoFermateControl extends MainControlLogged {
	private ObservableList<FermataRecursive> fermateObsevable;
	private ArrayList<Fermata> elencoFermate;
	
	public ElencoFermateControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}

	@FXML
	private JFXTreeTableView<FermataRecursive> table;
	 
    @FXML
    private JFXButton indietroButton;

    @FXML
    private Text usernameDipendente;

    @FXML
    private Circle timbro;
    
    @FXML
    private JFXButton aggiungiFermataButton;
    
    @FXML
    private JFXTextField cercaFermata;

    @FXML
    private JFXButton cercaFermataButton;
    
    @FXML
    private AnchorPane parentStackPane;
    
    @FXML
    void cercaFermata() {
      	if(this.cercaFermata.getText().length()<1) {
    		this.getFermate();
    		return;
    				
    	}
    	Connection conn=null;
		try {
			conn = new Connection(this.getUtente());
			this.elencoFermate= conn.cercaFermateElenco(this.cercaFermata.getText());
			int index=0;
			this.fermateObsevable.clear();
			for(Fermata t: this.elencoFermate) {
				this.fermateObsevable.add(new FermataRecursive(index,t));
			}
			this.aggiornaLista();
		} catch (IOException | JSONException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		} catch (ConnectionException e) {
			this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    }
	
    public void aggiornaLista() {
    	
    }
    
    
    public JFXTreeTableView<FermataRecursive> getTreeTable() {
    	return this.table;
    }

	public class FermataRecursive extends RecursiveTreeObject<FermataRecursive>{
		StringProperty id;
		StringProperty via;
		StringProperty civico;
		StringProperty eliminaAction;
		private Fermata fermata;
		public FermataRecursive(int index, Fermata fermata){
			this.fermata=fermata;
			this.id= new SimpleStringProperty(Integer.toString(fermata.getIdFermata()));
			this.via = new SimpleStringProperty(fermata.getIndirizzo());
			this.civico  = new SimpleStringProperty(fermata.getNumeroCivico());
			this.eliminaAction = new SimpleStringProperty("Elimina");
		}
		public Fermata getFermata() {
			return fermata;
		}
		public int getId() {
			return Integer.parseInt(id.getValue());
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
		
		
		this.cercaFermata.setOnKeyPressed(new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				if(event.getCode()==KeyCode.ENTER) {
					ElencoFermateControl.this.cercaFermata();
				}
				
			}
		});
		
		JFXTreeTableColumn<FermataRecursive,String> via = new JFXTreeTableColumn<FermataRecursive,String>("Indirizzo");

		via.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FermataRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FermataRecursive, String> param) {
                return param.getValue().getValue().via ;
            }
        });
		
		JFXTreeTableColumn<FermataRecursive,String> civico = new JFXTreeTableColumn<FermataRecursive,String>("Numero Civico");

		civico.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FermataRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FermataRecursive, String> param) {
                return param.getValue().getValue().civico ;
            }
        });
		
		this.fermateObsevable = FXCollections.observableArrayList();
		this.getFermate();
		
		final TreeItem<FermataRecursive> root = new RecursiveTreeItem<FermataRecursive>(fermateObsevable, RecursiveTreeObject::getChildren);
		table.getColumns().setAll(via, civico);
		table.setRoot(root);
		table.setShowRoot(false);
    }
    public void getFermate() {
    	Connection conn=null;
    	try {
			conn = new Connection(this.getUtente());
			this.elencoFermate= conn.getFermate();
			int index=0;
			this.fermateObsevable.clear();
			for(Fermata t: this.elencoFermate) {
				this.fermateObsevable.add(new FermataRecursive(index,t));
			}
			
		} catch (IOException | JSONException e) {
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

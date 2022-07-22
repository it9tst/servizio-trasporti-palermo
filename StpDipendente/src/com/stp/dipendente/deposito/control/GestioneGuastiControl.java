package com.stp.dipendente.deposito.control;

import java.io.IOException;
import java.util.function.Predicate;

import com.jfoenix.controls.JFXComboBox;
import com.stp.dipendente.control.ElencoAutobusControl;
import com.stp.dipendente.model.Autobus;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.json.JSONException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class GestioneGuastiControl extends ElencoAutobusControl{
	private AutobusRecursive autobusModifica;
	
	@FXML
	private HBox elencoAutobusContent;
	
	@FXML
	private Button salva;
	
	@FXML
	private RadioButton utilizzabileToggle;
	
	@FXML
	private RadioButton visionareToggle;
	
	@FXML
	private RadioButton gustoToggle;
	
	@FXML
	private TextField targa;
	
	@FXML
	private TextArea nota;
	
	private JFXComboBox<String> filtroGuasto;
	private ObservableList<String> filtroObs = FXCollections.observableArrayList();
	
    public GestioneGuastiControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}
    @Override
    public void cercaAutobus() {
    	super.cercaAutobus();
    	this.filtroGuasto.getSelectionModel().select(0);
    }
    
	@Override
	public void init() {
    	super.init();
    	
    	
    	
    	this.getTreeTable().getColumns().remove(1);
    	this.getTreeTable().setPrefWidth(500.0);
    	this.getTreeTable().setMaxWidth(500.0);
    	this.getTreeTable().setMinWidth(500.0);
    	
    	FXMLLoader loader = new FXMLLoader();
    	loader.setController(this);
        loader.setLocation(this.getClass().getResource("/com/stp/dipendente/deposito/view/GestisciGuasti.fxml"));
        try {
			this.elencoAutobusContent.getChildren().add(loader.load());
		} catch (IOException e) {
			this.openDialog(this.parentStackPane, "Errore caricamento risorse");
			return;
			
		}
        ToggleGroup group = new ToggleGroup();
        this.gustoToggle.setToggleGroup(group);
        this.visionareToggle.setToggleGroup(group);
        this.utilizzabileToggle.setToggleGroup(group);
        this.gustoToggle.setDisable(true);
        this.utilizzabileToggle.setDisable(true);
        this.visionareToggle.setDisable(true);
        
        this.gustoToggle.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				GestioneGuastiControl.this.nota.setDisable(false);
			}
        	
        });
        this.utilizzabileToggle.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				GestioneGuastiControl.this.nota.setDisable(true);
			}
        	
        });
        this.visionareToggle.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				GestioneGuastiControl.this.nota.setDisable(true);
			}
        	
        });
        this.filtroGuasto= new JFXComboBox<String>();
        
        this.filtroGuasto.setCellFactory(new Callback<ListView<String>, ListCell<String>>(){

			@Override
			public ListCell<String> call(ListView<String> param) {
				return new ListCell<String>(){
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null || empty) {
                            return;
                        } else {
                            setText(item);
                        }
                    }
                } ;
			}
			
		});
		this.filtroGuasto.setConverter(new StringConverter<String>() {
              @Override
              public String toString(String stato) {
                if (stato == null){
                  return null;
                } else {
                  return stato;
                }
              }

            @Override
            public String fromString(String statoId) {
                return null;
            }
        });
		this.filtroObs.clear();
		this.filtroObs.add("Tutti");
		this.filtroObs.add(Autobus.StatoAutobus.GUASTO.getNome());
		this.filtroObs.add(Autobus.StatoAutobus.UTILIZZABILE.getNome());
		this.filtroObs.add(Autobus.StatoAutobus.VISIONARE.getNome());
		this.filtroGuasto.setItems(filtroObs);
		AnchorPane.setLeftAnchor(this.filtroGuasto, 370.0);
		AnchorPane.setTopAnchor(this.filtroGuasto, 16.0);
		this.getContentHeader().getChildren().add(this.filtroGuasto);
		this.filtroGuasto.getSelectionModel().select(0);
		this.filtroGuasto.valueProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				GestioneGuastiControl.this.getTreeTable().setPredicate(new Predicate<TreeItem<AutobusRecursive>>() {
					@Override
					public boolean test(TreeItem<AutobusRecursive> t) {
						if(newValue.equals("Tutti")) {
							return true;
						}
						return t.getValue().getStatoGuasto().getValue().equals(newValue);
					}
					
				});
			}
				
		});

        
    }
    @FXML
    public void salva() {
    	if(this.autobusModifica!=null) {
    		Connection conn=null;
    		try {
    			conn = new Connection(this.getUtente());
    			if(this.gustoToggle.isSelected()) {
    				this.autobusModifica.setStatoGuasto(Autobus.StatoAutobus.GUASTO);
    				this.autobusModifica.getAutobus().setDescrizioneGuasto(this.nota.getText());
    			}else if(this.visionareToggle.isSelected()) {
    				this.autobusModifica.setStatoGuasto(Autobus.StatoAutobus.VISIONARE);
    				this.autobusModifica.getAutobus().setDescrizioneGuasto(null);
    			}else if(this.utilizzabileToggle.isSelected()) {
    				this.autobusModifica.setStatoGuasto(Autobus.StatoAutobus.UTILIZZABILE);
    				this.autobusModifica.getAutobus().setDescrizioneGuasto(null);
    			}
    			
    			if(conn.setStatoGuasto(this.autobusModifica.getAutobus())) {

    				this.openDialog(this.parentStackPane, "Stato autobus aggiornato");

    			}else {
    				this.openDialog(this.parentStackPane, "Errore, riprovare più tardi");
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
    }
    
    @Override
    public void action(AutobusRecursive autobus) {
    	this.gustoToggle.setDisable(false);
        this.utilizzabileToggle.setDisable(false);
        this.visionareToggle.setDisable(false);
    	this.autobusModifica=autobus;
    	this.targa.setText(this.autobusModifica.getAutobus().getTarga());
    	switch(this.autobusModifica.getAutobus().getStatoGuasto()) {
    		case GUASTO:
    			this.gustoToggle.setSelected(true);
    			this.nota.setDisable(false);
    			this.nota.setText(autobus.getAutobus().getDescrizioneGuasto());
    		break; 
    		case VISIONARE:
    			this.visionareToggle.setSelected(true);
    			this.nota.setDisable(true);
    			this.nota.setText(null);
    		break; 
    		case UTILIZZABILE:
    			this.utilizzabileToggle.setSelected(true);
    			this.nota.setDisable(true);
    			this.nota.setText(null);
    		break; 
    	}
    }
}

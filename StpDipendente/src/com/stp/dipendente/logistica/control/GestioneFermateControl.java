package com.stp.dipendente.logistica.control;

import java.io.IOException;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.GMapMouseEvent;
import com.lynden.gmapsfx.javascript.event.MouseEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.stp.dipendente.control.ElencoFermateControl;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.model.Fermata;
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

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import javafx.scene.shape.Circle;

import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class GestioneFermateControl extends ElencoFermateControl{
	@FXML
    private JFXButton salvaButton;

    @FXML
    private JFXButton annullaButton;
    
    @FXML
    private AnchorPane parentStackPane;
    
    @FXML
    private TextField civico;
    
    @FXML
    private AnchorPane mapsConteiner;
    
    @FXML
    private TextField via;
    
    @FXML
    private BorderPane root;

    @FXML
    private Text usernameDipendente;

    @FXML
    private Circle timbro;


    @FXML
    private GoogleMapView mapView;
    
    
    private GoogleMap map;
    
    private GeoApiContext geocodingService;
    private Marker markerCenter;
    
	public GestioneFermateControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}

	@FXML
	public void aggiungiFermata() {
		this.setCenter(800, 600);
		this.setPkg("logistica/");
		this.setXmlFileName("SchedaFermata");
		this.start();
	}
	
	public void action(Fermata fermata) {
		
	}
	
	@FXML
	public void salvaFermata(ActionEvent event) {
		if(this.via.getText().length()>0 && this.civico.getText().length()>0) {
			Connection conn=null;
			try {
				GeocodingResult[] results =  GeocodingApi.geocode(this.geocodingService, this.via.getText()+" "+this.civico.getText()+" , Palermo").await();
				if(results.length>0){
					conn = new Connection(this.getUtente());
					conn.aggiungiFermata(new Fermata(1 ,this.via.getText(),this.civico.getText(), results[0].geometry.location.lat, results[0].geometry.location.lng));
					this.openDialog(this.parentStackPane, "Fermata aggiunta correttamente");
				}else{
					this.openDialog(this.parentStackPane, "Nessun indirizzo trovato");
				}
				
				
				
			} catch (ApiException e) {
				this.openDialog(this.parentStackPane, "Errore caricamento fermata");
			} catch (InterruptedException e) {
				this.openDialog(this.parentStackPane, "Errore caricamento fermata");
			} catch (IOException | JSONException e) {
				this.openDialog(this.parentStackPane, "Errore connessione");
			} catch (ConnectionException e) {

				this.openDialog(this.parentStackPane, e.getMessage());
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}
		}else {
			this.openDialog(this.parentStackPane, "Inserire indirizzo e numero civico corretti");
		}

	}
	
    void gestioneFermate() {
    	this.setCenter(1200, 800);
    	this.setPkg("");
    	this.setXmlFileName("ElencoFermate");
    	this.start();
    }
	
	@FXML
	public void annulla() {
		this.gestioneFermate();
	}
	
	public void init() {
		super.init();
		if(this.getXmlFileName().equals("SchedaFermata")) {
			this.geocodingService = new GeoApiContext.Builder()
				    .apiKey("AIzaSyDd5ldzc7SXPMtE4fqg_8JkxHHb_hkxPr4")
				    .build();
			
			this.mapView.addMapInializedListener(new MapComponentInitializedListener () {

				@Override
				public void mapInitialized() {
					MapOptions mapOptions = new MapOptions();

					
					mapOptions.center(new LatLong(38.103611, 13.369445))
	                .overviewMapControl(false)
	                .panControl(false)
	                .rotateControl(false)
	                .scaleControl(false)
	                .streetViewControl(false)
	                .zoomControl(false)
	                .zoom(12)
	                .mapType(MapTypeIdEnum.ROADMAP);
					
					mapOptions.getJSObject().setMember("disableDoubleClickZoom", true);
					
					
					map = mapView.createMap(mapOptions);
					map.addMouseEventHandler(UIEventType.dblclick , new MouseEventHandler() {

						@Override
						public void handle(GMapMouseEvent event) {
							LatLong latLong = event.getLatLong();
							map.clearMarkers();
							
							
							MarkerOptions markerOptions = new MarkerOptions();
							markerOptions.position(latLong)
							.visible(true);
							
							
							GestioneFermateControl.this.markerCenter=new Marker(markerOptions);
							map.addMarker( GestioneFermateControl.this.markerCenter);
							int zoom = map.getZoom();
						    map.setZoom(3);
						    map.setZoom(zoom);
						    try {
								GeocodingResult[] results =  GeocodingApi.geocode(GestioneFermateControl.this.geocodingService, latLong.getLatitude()+", "+latLong.getLongitude()).await();
								if(results.length>0) {
									GestioneFermateControl.this.via.setText(results[0].addressComponents[1].longName);
									GestioneFermateControl.this.civico.setText(results[0].addressComponents[0].longName);
								}
						    } catch (ApiException | InterruptedException | IOException e) {
						    	GestioneFermateControl.this.openDialog(GestioneFermateControl.this.parentStackPane, "Errore caricamento fermata");
							}
						}
						
					});
					
				}
				
			});
			
			
			
		}else if(this.getXmlFileName().equals("ElencoFermate")){
			this.addEliminaColumn();
		}
		
		
	}
	
	public void addEliminaColumn() {
		JFXTreeTableColumn<FermataRecursive,String> eliminaButton = new JFXTreeTableColumn<FermataRecursive,String>("Elimina");
		eliminaButton.setSortable(false);
		
		eliminaButton.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FermataRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FermataRecursive, String> param) {
				return new SimpleStringProperty("Elimina");
            }
        });
		eliminaButton.setCellFactory(new Callback<TreeTableColumn<FermataRecursive,String>, TreeTableCell<FermataRecursive,String>>(){

			@Override
			public TreeTableCell<FermataRecursive, String> call(TreeTableColumn<FermataRecursive, String> param) {
				return new ButtonCell();
			}
			
		});
		this.getTreeTable().getColumns().add(eliminaButton);
	}
	
	@FXML
	public void centerMap() {
		if(this.via.getText().length()>0 && this.civico.getText().length()>0) {
			try {
				GeocodingResult[] results =  GeocodingApi.geocode(this.geocodingService, this.via.getText()+" "+this.civico.getText()+" , Palermo").await();
				if(results.length>0) {
					map.clearMarkers();
					map.setCenter(new LatLong(results[0].geometry.location.lat, results[0].geometry.location.lng));
					map.setZoom(18);
					MarkerOptions markerOptions = new MarkerOptions();
					markerOptions.position(new LatLong(results[0].geometry.location.lat, results[0].geometry.location.lng))
					
					.visible(true)
			        .title(this.via.getText()+" "+this.civico.getText());
					this.markerCenter=new Marker(markerOptions);
					map.addMarker(this.markerCenter);
					int zoom = map.getZoom();
				    map.setZoom(zoom+1);
				    map.setZoom(zoom);
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
		}
	}
	
    private class ButtonCell extends TreeTableCell<FermataRecursive, String> {
        final JFXButton buttonElimina = new JFXButton("Elimina");

        public ButtonCell(){
        	this.setAlignment(Pos.CENTER);
        	buttonElimina.setButtonType(ButtonType.FLAT);
        	buttonElimina.textProperty().bind(itemProperty());
        	buttonElimina.setMaxHeight(25.0);
        	buttonElimina.setMinHeight(25.0);
        	buttonElimina.setPrefHeight(25.0);
        	buttonElimina.setStyle("-fx-background-color: #f0f0f0 ;");
        	//buttonElimina.setFont(Font.font ("System", 12));
        	this.setStyle("-fx-padding: 0 0 0 0");
        	
        	buttonElimina.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                	GestioneFermateControl.this.eliminaFermata(getTreeTableRow().getIndex(), ((FermataRecursive) getTreeTableRow().getItem()));
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
    
    private void eliminaFermata(int index, FermataRecursive fermata){
    	Connection conn=null;
    	try {
    		conn = new Connection(this.getUtente());
        	if(conn.rimuoviFermata(fermata.getFermata())) {
        		this.getTreeTable().getRoot().getChildren().remove(index);
            	this.getTreeTable().getColumns().remove(2);
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
    public void aggiornaLista() {
    	this.getTreeTable().getColumns().remove(2);
    	this.addEliminaColumn();
    }
}

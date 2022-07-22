package com.stp.utente.control;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXSlider;
import com.jfoenix.controls.JFXTextField;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.event.UIEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.shapes.Circle;
import com.lynden.gmapsfx.shapes.CircleOptions;
import com.stp.utente.model.Fermata;
import com.stp.utente.util.Connection;
import com.stp.utente.util.LineaException;
import com.stp.utente.util.json.JSONException;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import jfxtras.scene.control.ImageViewButton;
import netscape.javascript.JSObject;

public class CercaFermateControl extends MainControl{

    

    
    
    private GeoApiContext geocodingService;
	
	public CercaFermateControl(String xmlFileName, Stage window) {
		super(xmlFileName, window);
		// TODO Auto-generated constructor stub
	}

	@FXML
    private ImageViewButton indietroButton;

    @FXML
    private Text usernameDipendente;

    @FXML
    private AnchorPane parentStackPane;

    @FXML
    private AnchorPane headerPane;

    @FXML
    private JFXTextField cercaLinea;

    @FXML
    private JFXButton centraMappaButton;
    
    @FXML
    private GoogleMapView mapView;
    
    @FXML
    private JFXTextField centraText;

    @FXML
    private JFXSlider raggioSlider;

    @FXML
    private Text raggio;
    
    
    
    
    private double raggioD=0.3;
    private Circle circle;
    private InfoWindow infoWindow;
    
    @FXML
    void centraMappa() {
    	if(this.centraText.getText().length()>0) {
			try {
				GeocodingResult[] results =  GeocodingApi.geocode(geocodingService, this.centraText.getText()+" , Palermo").await();
				if(results.length>0) {
					map.clearMarkers();
					map.setCenter(new LatLong(results[0].geometry.location.lat, results[0].geometry.location.lng));
					map.setZoom(16);
					MarkerOptions markerOptions = new MarkerOptions();
					
					 if(circle!=null) {
					    	map.removeMapShape(circle);
					    }
					    
					    CircleOptions circleOpts = new CircleOptions()
								.center(new LatLong(results[0].geometry.location.lat, results[0].geometry.location.lng))
								.radius(raggioD*1000)
								.strokeColor("#8EBDFF")
								.strokeOpacity(0.5)
								.fillColor("#8EBDFF")
								.fillOpacity(0.5);
					    circle = new Circle(circleOpts);
						map.addMapShape(circle);
						map.addUIEventHandler(circle, UIEventType.click, new UIEventHandler() {

							@Override
							public void handle(JSObject arg0) {
								if(infoWindow!=null) {
									infoWindow.close();
								}
							}
							
						});
				    Connection conn= new Connection();
				    try {
						ArrayList<Fermata> fermate= conn.getFermateRaggio(results[0].geometry.location.lat, results[0].geometry.location.lng, raggioD);
						for(Fermata f: fermate) {
							markerOptions.position(new LatLong(f.getCoordinataX(), f.getCoordinataY()))
							
							.visible(true)
					        .title(f.getIndirizzo()+" "+f.getNumeroCivico());
							
							Marker mark =new Marker(markerOptions);
		
							map.addMarker(mark);
							
							
							String[] linee= f.getLinee().split("-");
							String content="";
							
							if(!f.getLinee().equals("")) {
								content=content+"<h2>Linee per questa Fermata:</h2>";
								for(String s: linee) {
									content=content+"<h4>"+s+"</h4>";
								}
							}else {
								content=content+"<h2>Nessuna linea per questa fermata</h2>";
							}
							final String cont=content;
							map.addUIEventHandler(mark, UIEventType.click, new UIEventHandler() {
								@Override
								public void handle(JSObject arg0) {
									InfoWindowOptions infoOptions = new InfoWindowOptions();
									if(infoWindow!=null) {
										infoWindow.close();
									}
							        infoOptions.content(cont);
									infoWindow= new InfoWindow(infoOptions);
							        infoWindow.open(map, mark);
									
								}
								
							});
							
						}
						map.setZoom(new Double(Math.round(14-Math.log(0.621*raggioD)/Math.log(2))).intValue());
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (LineaException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
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
		}
    }

    
    private GoogleMap map;
    
    
    public void init() {
    	this.geocodingService = new GeoApiContext.Builder()
			    .apiKey("AIzaSyDd5ldzc7SXPMtE4fqg_8JkxHHb_hkxPr4")
			    .build();
		
    	System.out.println(this.mapView);
    	
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
				mapOptions.getJSObject().setMember("clickableIcons", false);
				
				
				
				map = mapView.createMap(mapOptions);

				map.addUIEventHandler(map, UIEventType.click, new UIEventHandler() {

					@Override
					public void handle(JSObject arg0) {
						if(infoWindow!=null) {
							infoWindow.close();
						}
					}
					
				});
				
				
			}
			
		});
		
		this.raggioSlider.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> ov,
                    Number old_val, Number new_val) {
            		DecimalFormat df = new DecimalFormat("0.00");
                        raggio.setText(df.format(new_val.doubleValue())+" KM");
                        raggioD=new_val.doubleValue();
                        
                }
            });
		
		this.centraText.setOnKeyPressed(new EventHandler<KeyEvent>() 
		{
		    @Override
		    public void handle(KeyEvent keyEvent) 
		    {
		        if(keyEvent.getCode() == KeyCode.ENTER)
		        {
		        	centraMappa();
		        }
		    }
		});
		
    }
    
    @FXML
    public void indietro() {
		new MenuUtenteControl("MenuUtente", this.getStage()).start();
    }
}
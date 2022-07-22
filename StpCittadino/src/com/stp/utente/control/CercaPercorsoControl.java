package com.stp.utente.control;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;
import com.google.maps.model.LatLng;
import com.google.maps.model.TravelMode;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.JavascriptArray;
import com.lynden.gmapsfx.javascript.JavascriptObject;
import com.lynden.gmapsfx.javascript.event.UIEventHandler;
import com.lynden.gmapsfx.javascript.event.UIEventType;
import com.lynden.gmapsfx.javascript.object.GMapObjectType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.InfoWindow;
import com.lynden.gmapsfx.javascript.object.InfoWindowOptions;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.javascript.object.Size;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import com.stp.utente.control.CercaLineeControl.IconOptions;
import com.stp.utente.control.CercaLineeControl.IconsOptions;
import com.stp.utente.util.Connection;
import com.stp.utente.util.LineaException;
import com.stp.utente.util.json.JSONException;
import com.stp.utente.util.json.JSONObject;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import jfxtras.scene.control.ImageViewButton;
import netscape.javascript.JSObject;

public class CercaPercorsoControl extends MainControl {
	private ObservableList<StringRecursive> stringObservable;
	
	
	public CercaPercorsoControl(String xmlFileName, Stage window) {
		super(xmlFileName, window);
		// TODO Auto-generated constructor stub
	}
	
    @FXML
    private BorderPane backgroundPane2;

    @FXML
    private ImageViewButton indietroButton;

    @FXML
    private AnchorPane parentStackPane;

    @FXML
    private AnchorPane headerPane;

    @FXML
    private JFXTextField centraTextPartenza;

    @FXML
    private JFXTextField centraTextArrivo;
    
    @FXML
    private ImageViewButton invertiButton;
    
    @FXML
    private JFXTreeTableView<StringRecursive> table;
    
    //@FXML
	//private JFXTreeTableView<> table;

    @FXML
    private GoogleMapView mapView;
    
    private GoogleMap map;
    private GeoApiContext geocodingService;
    private InfoWindow infoWindow;
    
    private Polyline polyPercorso=null;
    private Polyline polyPiediPartenza=null;
    private Polyline polyPiediArrivo=null;
    private List<LatLng> pointList;
    private List<LatLong> pointListgm;
    
    @FXML
    void inverti() {
    		String a = this.centraTextPartenza.getText();
    		String b = this.centraTextArrivo.getText();
    		this.centraTextPartenza.setText(b);
    		this.centraTextArrivo.setText(a);
    }
    
    @SuppressWarnings("unchecked")
	public void init() {
    	super.init();
	    	this.centraTextPartenza.setOnKeyPressed(new EventHandler<KeyEvent>() {
			    @Override
			    public void handle(KeyEvent keyEvent) 
			    {
			        if(keyEvent.getCode() == KeyCode.ENTER)
			        {
			           	calcolaPercorso();
			        }
			    }
			});
	    	
	    	this.centraTextArrivo.setOnKeyPressed(new EventHandler<KeyEvent>() {
			    @Override
			    public void handle(KeyEvent keyEvent) 
			    {
			        if(keyEvent.getCode() == KeyCode.ENTER)
			        {
			        	calcolaPercorso();
			        }
			    }
			});
	    	
	    	
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
					//mapOptions.getJSObject().setMember("gestureHandling", "none");
					//mapOptions.getJSObject().setMember("keyboardShortcuts", false);
					
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
	    	
	    	JFXTreeTableColumn<StringRecursive,String> percorso = new JFXTreeTableColumn<StringRecursive,String>("Percorso suggerito");
	    	percorso.setSortable(false);
	    	percorso.setResizable(false);
	    	percorso.setPrefWidth(246.0);
	    	percorso.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<StringRecursive, String>, ObservableValue<String>>() {
    	          
    			@Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<StringRecursive, String> param) {
                    return param.getValue().getValue().s ;
                }
            });
	    	
	    	this.stringObservable = FXCollections.observableArrayList();
	    	
	    	final TreeItem<StringRecursive> root = new RecursiveTreeItem<StringRecursive>(this.stringObservable, RecursiveTreeObject::getChildren);
    		table.getColumns().setAll(percorso);
    		table.setRoot(root);
    		table.setShowRoot(false);
    }

    @FXML
    public void indietro() {
		new MenuUtenteControl("MenuUtente", this.getStage()).start();
    }

    public void calcolaPercorso() {
    	
    	LatLng partenza = null;
    	LatLng arrivo = null;
			
		try {
			GeocodingResult[] results =  GeocodingApi.geocode(this.geocodingService, this.centraTextPartenza.getText()+" , Palermo").await();
			if(results.length>0) {
				partenza=results[0].geometry.location;
				
			}
			results =  GeocodingApi.geocode(this.geocodingService, this.centraTextArrivo.getText()+" , Palermo").await();
			if(results.length>0) {
				arrivo=results[0].geometry.location;
				
			}
		} catch (ApiException e) {
			this.openDialog(this.parentStackPane, "Errore codice api");
			return;
		} catch (InterruptedException e) {
			this.openDialog(this.parentStackPane, "Errore, riprovare più tardi");
			return;
		} catch (IOException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
			return;
		}
		
	Connection conn=null;
    	try {
    		conn= new Connection();
    		ArrayList<JSONObject> res;
    		res=conn.calcolaPercorso(partenza.lat, partenza.lng, arrivo.lat, arrivo.lng);
    		if(res.get(0).getInt("code")==-2) {
    			this.openDialog(this.parentStackPane, "Partenza e arrivo uguali");
    			return;
    		}
    		if(res.get(0).getInt("code")==-3) {
    			this.openDialog(this.parentStackPane, "Non è disponibile nessun percorso");
    			return;
    		}
    		this.creaPercorso(res, partenza, arrivo);
    	}catch (IOException | JSONException | LineaException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    }
    private void creaPercorso(ArrayList<JSONObject> percorso, LatLng partenza, LatLng arrivo) {
    	if(percorso!=null && percorso.size()>0) {
    		MarkerOptions markerOptions = new MarkerOptions();
    		ArrayList<LatLng> fermate= new ArrayList<LatLng>();
    		try {
    			map.clearMarkers();
    			markerOptions.position(new LatLong(percorso.get(0).getDouble("CoordinataXA"), percorso.get(0).getDouble("CoordinataYA")))
				.visible(true);
				
				Marker markp =new Marker(markerOptions);

				map.addMarker(markp);
				
				this.stringObservable.clear();
				
				String info="Partenza: "+percorso.get(0).getInt("idLinea");
				this.stringObservable.add(new StringRecursive(info));
				
				final String contp=info;
				map.addUIEventHandler(markp, UIEventType.click, new UIEventHandler() {
					@Override
					public void handle(JSObject arg0) {
						InfoWindowOptions infoOptions = new InfoWindowOptions();
						if(infoWindow!=null) {
							infoWindow.close();
						}
				        infoOptions.content(contp);
						infoWindow= new InfoWindow(infoOptions);
				        infoWindow.open(map, markp);
						
					}
					
				});

				

				
				int idFermata=percorso.get(0).getInt("idLinea");
	    			for(JSONObject j: percorso) {
	    				if(idFermata!=j.getInt("idLinea")) {
	    					markerOptions.position(new LatLong(j.getDouble("CoordinataXA"), j.getDouble("CoordinataYA")))
	    					.visible(true);
	    					
	    					System.out.println(new LatLong(j.getDouble("CoordinataXA"), j.getDouble("CoordinataYA")));
	    					Marker mark =new Marker(markerOptions);
	
	    					map.addMarker(mark);
	    					
	    					
	    					
	    					info=idFermata+" > "+j.getInt("idLinea");
	    					this.stringObservable.add(new StringRecursive(info));
	    					
	    					final String conts=info;
	    					map.addUIEventHandler(mark, UIEventType.click, new UIEventHandler() {
	    						@Override
	    						public void handle(JSObject arg0) {
	    							InfoWindowOptions infoOptions = new InfoWindowOptions();
	    							if(infoWindow!=null) {
	    								infoWindow.close();
	    							}
	    					        infoOptions.content(conts);
	    							infoWindow= new InfoWindow(infoOptions);
	    					        infoWindow.open(map, mark);
	    							
	    						}
	    						
	    					});
	    					idFermata=j.getInt("idLinea");
	    				}
	    				fermate.add(new LatLng(j.getDouble("CoordinataXA"), j.getDouble("CoordinataYA")));
	    			}
	    			fermate.add(new LatLng(percorso.get(percorso.size()-1).getDouble("CoordinataXB"), percorso.get(percorso.size()-1).getDouble("CoordinataYB")));
	    			
	    			
	    			markerOptions.position(new LatLong(percorso.get(percorso.size()-1).getDouble("CoordinataXB"), percorso.get(percorso.size()-1).getDouble("CoordinataYB")))
					.visible(true);
					
	    			Marker marka =new Marker(markerOptions);
	
					map.addMarker(marka);
					
					
					
					info="Arrivo: "+percorso.get(percorso.size()-1).getInt("idLinea");
					this.stringObservable.add(new StringRecursive(info));
					
					final String conta=info;
					map.addUIEventHandler(marka, UIEventType.click, new UIEventHandler() {
						@Override
						public void handle(JSObject arg0) {
							InfoWindowOptions infoOptions = new InfoWindowOptions();
							if(infoWindow!=null) {
								infoWindow.close();
							}
					        infoOptions.content(conta);
							infoWindow= new InfoWindow(infoOptions);
					        infoWindow.open(map, marka);
							
						}
						
					});
					if(this.polyPiediArrivo!=null) {
						map.removeMapShape(this.polyPiediArrivo);
					}
					if(this.polyPiediPartenza!=null) {
						map.removeMapShape(this.polyPiediPartenza);
					}
					
					DirectionsResult req=null;
					try {
						req= DirectionsApi.newRequest(this.geocodingService)
								  .origin(partenza)
								  .destination(fermate.get(0))
								  .mode(TravelMode.WALKING)
								  .await();
					} catch (ApiException | InterruptedException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					IconOptions symbol = new IconOptions()
							.path("M 0,-1 0,1")
							.strokeOpacity(1.0)
							.scale(4.0)
							.fillColor("#0000ff");
					
					
					IconsOptions icons= new IconsOptions()
							.icon(symbol)
							.offset("0")
							.repeat("20px");
					
					JavascriptArray ja= new JavascriptArray();
					ja.push(icons);
					
					
					
					LatLong[] aryArr= new LatLong[req.routes[0].overviewPolyline.decodePath().size()];
					
					
					int i=0;
					for(LatLng r: req.routes[0].overviewPolyline.decodePath()) {
						aryArr[i] = new LatLong(r.lat, r.lng);
						i++;
					}
					
					MVCArray mvc = new MVCArray(aryArr);
					
					PolylineOptions polyOpts = new PolylineOptions()
					.path(mvc)
					.strokeOpacity(0.0)
					.strokeWeight(4)
					.icons(ja);
					this.polyPiediPartenza= new Polyline(polyOpts);
					map.addMapShape(this.polyPiediPartenza);
					
					
					
					
					
					
					
					try {
						req= DirectionsApi.newRequest(this.geocodingService)
								  .origin(fermate.get(fermate.size()-1))
								  .destination(arrivo)
								  .mode(TravelMode.WALKING)
								  .await();
					} catch (ApiException | InterruptedException | IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
					aryArr= new LatLong[req.routes[0].overviewPolyline.decodePath().size()];
					
					
					i=0;
					for(LatLng r: req.routes[0].overviewPolyline.decodePath()) {
						aryArr[i] = new LatLong(r.lat, r.lng);
						i++;
					}
					
					mvc = new MVCArray(aryArr);
					polyOpts = new PolylineOptions()
							.path(mvc)
							.strokeOpacity(0.0)
							.strokeWeight(4)
							.icons(ja);
					this.polyPiediArrivo= new Polyline(polyOpts);
					map.addMapShape(this.polyPiediArrivo);
					
					
					
	    			this.costruisciPercorso(fermate);
		    	} catch (JSONException e) {
		    		
		    		this.openDialog(this.parentStackPane, "Errore, riprovare più tardi");
					return;
			}
	    	}
    }
    
    private void costruisciPercorso(ArrayList<LatLng> fermate) {
    	int len=fermate.size();
		int tmp=len;
		ArrayList<LatLng> tmplist;
		Double d=Math.ceil(((double)len/24.0));
		ArrayList<ArrayList<LatLng>> ary = new ArrayList<ArrayList<LatLng>>();
		
		//int index=0;
		for(int i=0;i<d.intValue();i++) {
			tmplist=new ArrayList<>();
			for(int j=0;j<((tmp>24)?24:tmp);j++) {
				
				tmplist.add(new LatLng(fermate.get(j+ 24*i).lat, fermate.get(j+ 24*i).lng));
				/*Text txt =new Text(fermate.get(j+ 24*i).getIndirizzo()+" "+fermate.get(j+ 24*i).getNumeroCivico());
				txt.setWrappingWidth(190.0);
				AnchorPane.setTopAnchor(txt, index*25.0);
				index++;
				txt.setTextAlignment(TextAlignment.CENTER);
				txt.setFill(Paint.valueOf("#ffffff"));
				this.infoFermateScroll.getChildren().add(txt);*/
			}
			ary.add(tmplist);
			tmp=tmp-24;
		}

		DirectionsResult req;
		this.pointList = new ArrayList<LatLng>();
		
		try {
			for(int i=0;i<d.intValue();i++) {
				try {
					if(ary.get(i).size()<24) {
						if(ary.get(i).size()==2) {
							req= DirectionsApi.newRequest(this.geocodingService)
									  .origin(ary.get(i).get(0))
									  .destination(ary.get(i).get(ary.get(i).size()-1))
									  .mode(TravelMode.DRIVING)
									  .await();
						}else if(ary.get(i).size()==1) {
							if(ary.size()==1) {
								this.openDialog(this.parentStackPane, "La linea possiede un unica fermata");
								return;
							}else {
								req= null;
							}
						}else {
							req= DirectionsApi.newRequest(this.geocodingService)
									  .origin(ary.get(i).get(0))
									  .destination(ary.get(i).get(ary.get(i).size()-1))
									  .mode(TravelMode.DRIVING).waypoints(ary.get(i).subList(1, ary.get(i).size()-1).toArray(new LatLng[0]))
									  .await();
						}
					}else {
						if(i<(ary.size()-1)) {
							req= DirectionsApi.newRequest(this.geocodingService)
									  .origin(ary.get(i).get(0))
									  .destination(ary.get(i+1).get(0))
									  .mode(TravelMode.DRIVING).waypoints(ary.get(i).subList(1, ary.get(i).size()).toArray(new LatLng[0]))
									  .await();
						}else {
							req= DirectionsApi.newRequest(this.geocodingService)
									  .origin(ary.get(i).get(0))
									  .destination(ary.get(i).get(ary.get(i).size()-1))
									  .mode(TravelMode.DRIVING).waypoints(ary.get(i).subList(1, ary.get(i).size()-1).toArray(new LatLng[0]))
									  .await();
						}
					}
					
					if(req!=null) {
						pointList.addAll(req.routes[0].overviewPolyline.decodePath());
					}
					
	
				} catch (ApiException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException | JSONException  e) {
			e.printStackTrace();
			this.openDialog(this.parentStackPane, "Errore caricamento linee");
		}

		
		if(this.polyPercorso!=null) {
			map.removeMapShape(this.polyPercorso);
		}

		this.pointListgm=new ArrayList<LatLong>();
		
		LatLong[] aryArr= new LatLong[pointList.size()];
		int i=0;
		for(LatLng r: pointList) {
			aryArr[i] = new LatLong(r.lat, r.lng);
			this.pointListgm.add(aryArr[i++]);
		}
		//aryArr[res.size()]=aryArr[0];
		MVCArray mvc = new MVCArray(aryArr);
		
	
		
		
		
		PolylineOptions polyOpts = new PolylineOptions()
		.path(mvc)
		.strokeOpacity(1.0)
		.strokeColor("#ff0000")
		.strokeWeight(4);
		this.polyPercorso = new Polyline(polyOpts);
		map.addMapShape(this.polyPercorso);

		
		map.setCenter(aryArr[aryArr.length/2]);
		map.setZoom(15);
    }
    
    
    
    public class StringRecursive extends RecursiveTreeObject<StringRecursive>{
		private StringProperty s;

		public StringRecursive(String s){
			this.s = new SimpleStringProperty(s);
		}
		
	}
    
public class IconOptions extends JavascriptObject {
	    
	    protected String path;
	    protected String fillColor;
	    protected double fillOpacity;
	    protected double scale;
	    protected String strokeColor;
	    protected int strokeWeight;
	    protected double strokeOpacity;
	    protected String url;
	    protected Size size;
	    protected double rotation;
	    

	    public IconOptions() {
	        super(GMapObjectType.OBJECT);
	    }
	    	    
	    public IconOptions path(String path) {
	    	setProperty("path", path);
	    	this.path = path;
	        return this;
			
		}
	    public IconOptions rotation(double rotation) {
	    	setProperty("rotation", rotation);
	    	this.rotation = rotation;
	        return this;
			
		}
	    public IconOptions size(int w, int h) {
	    	this.size=new Size(w,h);
	    	setProperty("size", this.size);
	        return this;
			
		}
	    public IconOptions fillColor(String fillColor) {
	    	setProperty("fillColor", fillColor);
	    	this.fillColor = fillColor;
	        return this;
			
		}
	    public IconOptions url(String url) {
	    	setProperty("url", url);
	    	this.url = url;
	        return this;
			
		}
	    public IconOptions fillOpacity(double fillOpacity) {
	    	setProperty("fillOpacity", fillOpacity);
	    	this.fillOpacity = fillOpacity;
	        return this;
			
		}

	    public IconOptions scale(double scale) {
	    	setProperty("scale", scale);
	    	this.scale = scale;
	        return this;
			
		}

	    public IconOptions strokeColor(String strokeColor) {
	    	setProperty("strokeColor", strokeColor);
	    	this.strokeColor = strokeColor;
	        return this;
			
		}

	    public IconOptions strokeWeight(int strokeWeight) {
	    	setProperty("strokeWeight", strokeWeight);
	    	this.strokeWeight = strokeWeight;
	        return this;
			
		}
	    public IconOptions strokeOpacity(double strokeOpacity) {
	    	setProperty("strokeOpacity", strokeOpacity);
	    	this.strokeOpacity = strokeOpacity;
	        return this;
			
		}
	}

	public class IconsOptions extends JavascriptObject {
    
	    protected String offset;
	    protected String repeat;
	    protected JavascriptObject icon;
	
	
	    //protected String label;
	    
	    public IconsOptions() {
	        super(GMapObjectType.OBJECT);
	    }
	    	    
	    public IconsOptions offset(String offset) {
	    	setProperty("offset", offset);
	    	this.offset = offset;
	        return this;
			
		}
	    public IconsOptions repeat(String repeat) {
	    	setProperty("repeat", repeat);
	    	this.repeat = repeat;
	        return this;
			
		}
	    public IconsOptions icon(JavascriptObject icon) {
	    	setProperty("icon", icon);
	    	this.icon = icon;
	        return this;
			
		}
	}
}
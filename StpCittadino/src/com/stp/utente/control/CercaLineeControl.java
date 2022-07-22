package com.stp.utente.control;


import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.LatLng;

import com.google.maps.model.TravelMode;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.JavascriptObject;
import com.lynden.gmapsfx.javascript.object.GMapObjectType;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MVCArray;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import com.lynden.gmapsfx.javascript.object.Marker;
import com.lynden.gmapsfx.javascript.object.MarkerOptions;
import com.lynden.gmapsfx.javascript.object.Size;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import com.stp.utente.model.Fermata;
import com.stp.utente.model.Linea;
import com.stp.utente.util.Connection;
import com.stp.utente.util.LineaException;
import com.stp.utente.util.json.JSONException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.util.Callback;
import jfxtras.scene.control.ImageViewButton;

public class CercaLineeControl extends MainControl {
	private List<Linea> elencoLinee;
	private ObservableList<LineaRecursive> lineeObservable;

	public CercaLineeControl(String xmlFileName, Stage window) {
		super(xmlFileName, window);
		// TODO Auto-generated constructor stub
	}

	@FXML
	private JFXTreeTableView<LineaRecursive> table;
	 
    @FXML
    private ImageViewButton indietroButton;

    @FXML
    private Text usernameDipendente;

    @FXML
    private Circle timbro;
    
    @FXML
    private JFXButton aggiungiLineaButton;

    @FXML
    private JFXButton cercaLineaButton;
    
    @FXML
    private AnchorPane parentStackPane;
    
    @FXML
    private AnchorPane headerPane;

    @FXML
    private GoogleMapView mapView;
    
    @FXML
    private VBox infoFermate;
    
    @FXML
    private AnchorPane infoFermateScroll;
    
    @FXML
    private AnchorPane footerPane;
    
    @FXML
    private AnchorPane infoOrariScroll;
    
    
    private GoogleMap map;
    private GeoApiContext geocodingService;
    

    private Polyline polyPercorsoAndata=null;
    private Polyline polyPercorsoRitorno=null;
    private List<LatLng> pointList;
    private List<LatLong> pointListgm;
	
    public JFXTreeTableView<LineaRecursive> getTreeTable() {
    	return this.table;
    }

	private class LineaRecursive extends RecursiveTreeObject<LineaRecursive>{
		StringProperty idLinea;

		private Linea linea;
		int index;
		
		@SuppressWarnings("unused")
		public int getIndex() {
			return index;
		}

		public LineaRecursive(int index, Linea linea){
			this.index=index;
			this.linea = linea;
			this.idLinea = new SimpleStringProperty(Integer.toString(linea.getIdLinea()));
		}

		public Linea getLinea() {
			return linea;
		}
	}

    @FXML
    private void menuUtente() {
		new MenuUtenteControl("MenuUtente", this.getStage()).start();
    }
    
    
    public void setRouteMarker(Marker mark) {
    	map.addMarker(mark);
    }
    

	@SuppressWarnings("unchecked")
	@Override
	public void init() {
    	super.init();
    	
		JFXTreeTableColumn<LineaRecursive,String> idLinea = new JFXTreeTableColumn<LineaRecursive,String>("ID Linea");
    	
		idLinea.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<LineaRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<LineaRecursive, String> param) {
                return param.getValue().getValue().idLinea ;
            }
        });
		
		this.lineeObservable = FXCollections.observableArrayList();
		this.getLinee();
		
		final TreeItem<LineaRecursive> root = new RecursiveTreeItem<LineaRecursive>(this.lineeObservable, RecursiveTreeObject::getChildren);
		table.getColumns().setAll(idLinea);
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
							CercaLineeControl.this.action(row.getItem());
						}
					}
				});
				return row;
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
		        
				
				
			}
			
		});
		this.table.setRowFactory(new Callback<TreeTableView<LineaRecursive>, TreeTableRow<LineaRecursive>>(){
			
			@Override
			public TreeTableRow<LineaRecursive> call(TreeTableView<LineaRecursive> view) {
				TreeTableRow<LineaRecursive> row = new TreeTableRow<LineaRecursive>();
				row.setOnMouseClicked(new EventHandler<MouseEvent> () {
					@Override
					public void handle(MouseEvent event) {
						if(event.getClickCount()==2 && !row.isEmpty()) {
							CercaLineeControl.this.action(row.getItem());
						}
					}
				});
				return row;
			}
    	});
    	
    }

	private void action(LineaRecursive item) {
		Connection conn=null;
		try {
			conn = new Connection();
			conn.getLineeFermate(item.getLinea());
			map.clearMarkers();

			ArrayList<Fermata> fermate=item.getLinea().getFermate();

			
			
			int index=0;
			for(Fermata f: fermate) {
				if(f.equals(item.getLinea().getCapB())) {
					
					break;
				}
				index++;
			}
			fermate.add(fermate.get(0));
			this.creaPolyline(fermate.subList(0, index+1), 0, "#ff0000");
			this.creaPolyline(fermate.subList(index, fermate.size()), 1, "#0000ff");

			
			if(item.getLinea().getCapA()!=null) {
				MarkerOptions markerOptions = new MarkerOptions();
		        markerOptions.position(new LatLong(item.getLinea().getCapA().getCoordinataX(),item.getLinea().getCapA().getCoordinataY()))
		        .visible(true)
		        .title("Capolinea A")
		        .icon("http://maps.google.com/mapfiles/markerA.png");

		        Marker marker = new Marker( markerOptions );
		        map.addMarker(marker);
			}
			if(item.getLinea().getCapB()!=null) {
				MarkerOptions markerOptions = new MarkerOptions();
		        markerOptions.position(new LatLong(item.getLinea().getCapB().getCoordinataX(),item.getLinea().getCapB().getCoordinataY()))
		        .visible(true)
		        .title("Capolinea B")
		        .icon("http://maps.google.com/mapfiles/markerB.png");

		        Marker marker = new Marker( markerOptions );
		        map.addMarker(marker);
			}
			
			
			
			int zoom=this.map.getZoom();
			this.map.setZoom(3);
			this.map.setZoom(zoom);
			
			this.createOrario(item);

		}catch (IOException | JSONException | LineaException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
		
	}
	
	private void creaPolyline(List<Fermata> fermate, int sel, String colore) throws IOException {
		int len=fermate.size();
		int tmp=len;
		ArrayList<LatLng> tmplist;
		Double d=Math.ceil(((double)len/24.0));
		ArrayList<ArrayList<LatLng>> ary = new ArrayList<ArrayList<LatLng>>();
		this.infoFermate.setVisible(true);
		this.infoFermateScroll.getChildren().clear();
		int index=0;
		
		for(int i=0;i<d.intValue();i++) {
			tmplist=new ArrayList<>();
			for(int j=0;j<((tmp>24)?24:tmp);j++) {
				tmplist.add(new LatLng(fermate.get(j+ 24*i).getCoordinataX(), fermate.get(j+ 24*i).getCoordinataY()));
				Text txt =new Text(fermate.get(j+ 24*i).getIndirizzo()+" "+fermate.get(j+ 24*i).getNumeroCivico());
				txt.setWrappingWidth(190.0);
				AnchorPane.setTopAnchor(txt, index*25.0);
				index++;
				txt.setTextAlignment(TextAlignment.CENTER);
				txt.setFill(Paint.valueOf("#ffffff"));
				this.infoFermateScroll.getChildren().add(txt);
			}
			ary.add(tmplist);
			tmp=tmp-24;
		}
		((ScrollPane)this.infoFermateScroll.getParent().getParent().getParent()).setVvalue(0.0);
		
		DirectionsResult req;
		this.pointList = new ArrayList<LatLng>();
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
		

		if(sel==0) {
			if(this.polyPercorsoAndata!=null) {
				map.removeMapShape(this.polyPercorsoAndata);
			}
		}
		if(sel==1) {
			if(this.polyPercorsoRitorno!=null) {
				map.removeMapShape(this.polyPercorsoRitorno);
			}
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
		.strokeColor(colore)
		.strokeWeight(4);

		if(sel==0) {
			this.polyPercorsoAndata = new Polyline(polyOpts);
			map.addMapShape(this.polyPercorsoAndata);

		}
		if(sel==1) {
			this.polyPercorsoRitorno = new Polyline(polyOpts);
			map.addMapShape(this.polyPercorsoRitorno);
		}
		

		
		map.setCenter(aryArr[aryArr.length/2]);
		map.setZoom(15);
	}
	
	
	private void createOrario(LineaRecursive item) {
		
		if(item.getLinea().getOraInizioCorsaFestivo() == null) {
			LocalTime start= item.getLinea().getOraInizioCorsa();
			LocalTime end= item.getLinea().getOraFineCorsa();
			LocalTime tmp=start;
			LocalTime durata= item.getLinea().getDurata();

			int minuti= durata.getHour()*60 + durata.getMinute();
			
			long intervallo= (minuti*60*1000)/(item.getLinea().getnBusConsigliato());
			
			LocalTime intervall=Instant.ofEpochMilli(intervallo).atZone(ZoneId.of("Greenwich")).toLocalTime();

			String orario="";
			int l=0;
			while(tmp.compareTo(end)<=0) {
				
				tmp=tmp.plus(Duration.ofNanos(intervall.toNanoOfDay()));
				orario=orario+tmp.toString()+" > ";
				l=l+(tmp.toString()+" > ").length();
				
				if(l>160) {
					orario=orario+"\r\n";
					l=0;
				}
			}
			orario=orario.substring(0, orario.length()-2);
			
			Text t= new Text("SERVIZIO: FERIALE\n" + orario);
			t.setFont(Font.font("Times New Roman", 16));
			
			this.infoOrariScroll.getChildren().clear();
			t.setTextAlignment(TextAlignment.CENTER);
			this.infoOrariScroll.getChildren().add(t);

			AnchorPane.setTopAnchor(t, 10.0);
			AnchorPane.setLeftAnchor(t, 10.0);
		} else {
			LocalTime start= item.getLinea().getOraInizioCorsa();
			LocalTime end= item.getLinea().getOraFineCorsa();
			LocalTime tmp=start;
			LocalTime durata= item.getLinea().getDurata();

			int minuti= durata.getHour()*60 + durata.getMinute();
			
			long intervallo= (minuti*60*1000)/(item.getLinea().getnBusConsigliato());
			
			LocalTime intervall=Instant.ofEpochMilli(intervallo).atZone(ZoneId.of("Greenwich")).toLocalTime();
			
			String orario="";
			int l=0;
			while(tmp.compareTo(end)<=0) {
				
				tmp=tmp.plus(Duration.ofNanos(intervall.toNanoOfDay()));
				orario=orario+tmp.toString()+" > ";
				l=l+(tmp.toString()+" > ").length();
				
				if(l>160) {
					orario=orario+"\r\n";
					l=0;
				}
			}
			orario=orario.substring(0, orario.length()-2);
			
			LocalTime startF= item.getLinea().getOraInizioCorsaFestivo();
			LocalTime endF= item.getLinea().getOraFineCorsaFestivo();
			LocalTime tmpF=startF;
			LocalTime durataF= item.getLinea().getDurataFestivo();
			int minutiF= durataF.getHour()*60 + durataF.getMinute();
			long intervalloF= (minutiF*60*1000)/(item.getLinea().getnBusConsigliatoFestivo());
			LocalTime intervallF=Instant.ofEpochMilli(intervalloF).atZone(ZoneId.of("Greenwich")).toLocalTime();
			
			String orarioF="";
			int lF=0;
			while(tmpF.compareTo(endF)<=0) {
				
				tmpF=tmpF.plus(Duration.ofNanos(intervallF.toNanoOfDay()));
				orarioF=orarioF+tmpF.toString()+" > ";
				lF=lF+(tmpF.toString()+" > ").length();
				
				if(lF>160) {
					orarioF=orarioF+"\r\n";
					lF=0;
				}
			}
			orarioF=orarioF.substring(0, orarioF.length()-2);
			
			Text t= new Text("SERVIZIO: FERIALE\n" + orario + "\n\nSERVIZIO: FESTIVO\n" + orarioF);
			t.setFont(Font.font("Times New Roman", 16));
			
			this.infoOrariScroll.getChildren().clear();
			t.setTextAlignment(TextAlignment.CENTER);
			this.infoOrariScroll.getChildren().add(t);

			AnchorPane.setTopAnchor(t, 10.0);
			AnchorPane.setLeftAnchor(t, 10.0);
		}
	}
	
	
	private void getLinee() {
    	Connection conn=null;
		try {
			conn = new Connection();
			this.elencoLinee= conn.getLinee();
			int index=0;
			for(Linea t: this.elencoLinee) {
				this.lineeObservable.add(new LineaRecursive(index,t));
			}
			
		}catch (IOException | JSONException | LineaException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		}finally {
			if(conn!=null) {
				conn.close();
			}
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
	
	@FXML
    public void indietro() {
    	this.setCenter(1200, 800);
		new MenuUtenteControl("MenuUtente", this.getStage()).start();
    }
}

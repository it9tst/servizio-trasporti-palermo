package com.stp.utente;

import java.net.URL;

import com.stp.utente.control.MenuUtenteControl;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;


public class MainAppUtente extends Application{
	private Image faviconImage;
	
	private Stage window;
	
	private final String VIEWDIR="view/";
	private final String FAVICONURL="/img/favicon.png";

	
	@Override 
	public void start(Stage stage){
		this.window = stage;
		this.faviconImage = new Image(MainAppUtente.class.getResourceAsStream(FAVICONURL));
		this.window.getIcons().add(this.faviconImage); 
		
        this.initMenuWindow();
	}
	public void initMenuWindow() {
		this.setCenter(1200, 800);
        this.window.setTitle("Servizio Trasporti Palermo");
		new MenuUtenteControl("MenuUtente", this.window).start();
	}
	public void alert(String titolo, String header, String content){
		this.alert(titolo, header, content, 0);
	}
	public void alert(String titolo, String header, String content, int tipo){
		AlertType type=AlertType.INFORMATION;
		switch(tipo){
			case 1:
				type=AlertType.CONFIRMATION;
			break;
			case 2:
				type=AlertType.WARNING;
			break;
			case 3:
				type=AlertType.ERROR;
			break;
			case 4:
				type=AlertType.NONE;
			break;
				
		}
		Alert alert = new Alert(type);
		alert.setTitle(titolo);
		alert.setHeaderText(header);
		alert.setContentText(content);
		
		Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
		alertStage.getIcons().add(faviconImage);
		
		alert.showAndWait();
		
	}
	
	public void setCenter(int x, int y) {
		 Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		 this.window.setX((primaryScreenBounds.getWidth() - x)/2);
		 this.window.setY((primaryScreenBounds.getHeight() - y)/2);
	}
	
	public static void main(String[] args){
		launch(args);
	}

	
	public URL getViewResource(String file) {
		return this.getClass().getResource(VIEWDIR+file+".fxml");
	}
	public URL getStyleResource(String file) {
		return this.getClass().getResource(file+".css");
	}
}
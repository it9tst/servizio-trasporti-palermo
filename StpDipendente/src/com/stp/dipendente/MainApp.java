package com.stp.dipendente;


import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import com.stp.dipendente.control.LoginControl;

public class MainApp extends Application{
	private Image faviconImage;
	
	private Stage window;
	
	private final String FAVICONURL="/img/favicon.png";

	
	@Override 
	public void start(Stage stage){
		this.window = stage;
		this.faviconImage = new Image(MainApp.class.getResourceAsStream(FAVICONURL));
		this.window.getIcons().add(this.faviconImage); 
		
        this.initLoginWindow();
	}
	public void initLoginWindow() {
		this.setCenter(800, 600);
        this.window.setTitle("Servizio Trasporti Palermo");
		new LoginControl("Login", this.window).start();
	}
	public void setCenter(int x, int y) {
		 Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		 this.window.setX((primaryScreenBounds.getWidth() - x)/2);
		 this.window.setY((primaryScreenBounds.getHeight() - y)/2);
	}
	
	public static void main(String[] args){
		launch(args);
	}

}

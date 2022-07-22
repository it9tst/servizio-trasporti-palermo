package com.stp.utente.control;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import jfxtras.scene.control.ImageViewButton;

public class MenuUtenteControl extends MainControl{
	private Image linea;
	private Image lineaPressed;
	private Image fermata;
	private Image fermataPressed;
	private Image percorso;
	private Image percorsoPressed;
	
    public MenuUtenteControl(String xmlFileName, Stage window) {
		super(xmlFileName, window);
		// TODO Auto-generated constructor stub
	}

	@FXML
    private AnchorPane backgroundPane;

    @FXML
    private ImageViewButton cercaPercorsoButton;

    @FXML
    private ImageViewButton cercaLineaButton;

    @FXML
    private ImageViewButton cercaFermataButton;

    @FXML
    void cercaFermata(MouseEvent event) {
    	this.setCenter(1200, 800);
		new CercaFermateControl("CercaFermate", this.getStage()).start();
    }

    @FXML
    void cercaLinea(MouseEvent event) {
    	this.setCenter(1200, 800);
		new CercaLineeControl("CercaLinee", this.getStage()).start();
    }

    @FXML
    void cercaPercorso(MouseEvent event) {
    	this.setCenter(1200, 800);
		new CercaPercorsoControl("CercaPercorso", this.getStage()).start();
    }
    
    @Override
	public void init() {
    	super.init();
    	System.out.println(MenuUtenteControl.this.getClass().getResource("/img/cercaLinea.png").toString());
    	System.out.println(MenuUtenteControl.this.getClass().getResourceAsStream("/img/cercaLinea.png"));

			this.linea = new Image(MenuUtenteControl.this.getClass().getResourceAsStream("/img/cercaLinea.png"));
			this.lineaPressed = new Image(this.getClass().getResourceAsStream("/img/cercaLineaPressed.png"));
			this.fermata = new Image(this.getClass().getResourceAsStream("/img/cercaFermata.png"));
			this.fermataPressed = new Image(this.getClass().getResourceAsStream("/img/cercaFermataPressed.png"));
			this.percorso = new Image(this.getClass().getResourceAsStream("/img/cercaPercorso.png"));
			this.percorsoPressed = new Image(this.getClass().getResourceAsStream("/img/cercaPersorsoPressed.png"));
    	

    	this.cercaLineaButton.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				MenuUtenteControl.this.cercaLineaButton.setImage(MenuUtenteControl.this.lineaPressed);
				
			}
    	});
        
    	this.cercaLineaButton.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				MenuUtenteControl.this.cercaLineaButton.setImage(MenuUtenteControl.this.linea);
				
			}
    	});
    	this.cercaFermataButton.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				MenuUtenteControl.this.cercaFermataButton.setImage(MenuUtenteControl.this.fermataPressed);
				
			}
    	});
        
    	this.cercaFermataButton.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				MenuUtenteControl.this.cercaFermataButton.setImage(MenuUtenteControl.this.fermata);
				
			}
    	});
    	this.cercaPercorsoButton.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				MenuUtenteControl.this.cercaPercorsoButton.setImage(MenuUtenteControl.this.percorsoPressed);
				
			}
    	});
        
    	this.cercaPercorsoButton.setOnMouseReleased(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				MenuUtenteControl.this.cercaPercorsoButton.setImage(MenuUtenteControl.this.percorso);
				
			}
    	});
        
        //((MainControl)loader.getController()).setInitElement(this.getStage(), this.getScene(), this.getMainApp());
    }

}

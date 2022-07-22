package com.stp.dipendente.amministrativo.control;

import com.jfoenix.controls.JFXButton;
import com.stp.dipendente.control.MainControlLogged;
import com.stp.dipendente.model.Dipendente;

import javafx.fxml.FXML;
import javafx.stage.Stage;


public class MenuAmministrativoControl extends MainControlLogged{

    public MenuAmministrativoControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}

	@FXML
    private JFXButton gestionePersonaleButton;

    @FXML
    private JFXButton gestioneTurniButton;

    @FXML
    private JFXButton compilaStipendioButton;

    @FXML
    void gestionePersonale() {
    	this.setCenter(1200, 800);
    	new GestionePersonaleControl("ElencoDipendenti", this.getStage(), this.getUtente()).start();
    } 
    
    @FXML
    void gestioneTurni() {
    	this.setCenter(1200, 800);
    	new GestioneTurniControl("ElencoDipendenti", this.getStage(), this.getUtente()).start();
	   
    } 
    
    @FXML
    void compilaStipendio() {
    	this.setCenter(1200, 800);
    	new CompilaStipendioControl("ElencoDipendenti", this.getStage(), this.getUtente()).start();
    }
    public void init() {
    	setPkg("amministrativo/");
    }
}
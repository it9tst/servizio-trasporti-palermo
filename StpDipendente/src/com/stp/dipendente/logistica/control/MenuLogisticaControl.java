package com.stp.dipendente.logistica.control;

import com.jfoenix.controls.*;
import com.stp.dipendente.control.MainControlLogged;
import com.stp.dipendente.model.Dipendente;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class MenuLogisticaControl extends MainControlLogged{

    public MenuLogisticaControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}

	@FXML
    private JFXButton gestioneLineeButton;
    
    @FXML
    private JFXButton gestioneFermateButton;
    
    @FXML
    private JFXButton associaLineaButton;

    @FXML
    void gestioneLinee() {
    	this.setCenter(1200, 800);
    	new GestioneLineeControl("ElencoLinee", this.getStage(), this.getUtente()).start();
    } 
    
    @FXML
    void gestioneFermate() {
    	this.setCenter(1200, 800);
    	new GestioneFermateControl("ElencoFermate", this.getStage(), this.getUtente()).start();
    }
    @FXML
    void associaLinea() {
    	this.setCenter(1200, 800);
    	new AssociaAutistaALineaControl("ElencoDipendenti", this.getStage(), this.getUtente()).setFiltro(4).setDisponibili(1).start();
    } 
}
package com.stp.dipendente.deposito.control;

import com.jfoenix.controls.*;
import com.stp.dipendente.control.MainControlLogged;
import com.stp.dipendente.model.Dipendente;

import javafx.fxml.FXML;
import javafx.stage.Stage;

public class MenuDepositoControl extends MainControlLogged{

    public MenuDepositoControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}

	@FXML
    private JFXButton entrataUscitaAutobusButton;

    @FXML
    private JFXButton gestioneGuastiButton;

    @FXML
    private JFXButton gestioneAutobusButton;

    @FXML
    void entrataUscitaAutobus() {
    	this.setCenter(1200, 800);
    	new EntrataUscitaAutobusControl("ElencoAutobus", this.getStage(), this.getUtente()).start();
    } 
    
    @FXML
    void gestioneGuasti() {
    	this.setCenter(1200, 800);
    	new GestioneGuastiControl("ElencoAutobus", this.getStage(), this.getUtente()).start();

    } 
    
    @FXML
    void gestioneAutobus() {
    	this.setCenter(1200, 800);
    	new GestioneAutobusControl("ElencoAutobus", this.getStage(), this.getUtente()).start();
    } 
}
package com.stp.dipendente.control;

import com.stp.dipendente.model.Dipendente;

import javafx.fxml.FXML;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainControlLogged extends MainControl{
	private Dipendente utente;
	
	@FXML
	private Text usernameDipendente;
	
    @FXML
    private Circle timbro;
	

	public MainControlLogged(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window);
		this.utente=utente;
	}
	public Dipendente getUtente() {
		return this.utente;
	}
	
	public void init() {
		super.init();
		System.out.println(this.getUtente());
		this.usernameDipendente.setText(this.getUtente().getUsername());
		this.toogleCartellino();
	}
	public void toogleCartellino() {
		if(!this.getUtente().isStatoCartellino()) {
			timbro.setFill(Paint.valueOf("#ae0000"));
		}else {
			timbro.setFill(Paint.valueOf("#01ab00"));
		}
	}
	
}

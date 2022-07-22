package com.stp.dipendente.control;

import com.stp.dipendente.model.Dipendente;


import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class VisualizzaDatiControl extends MainControlLogged{

    public VisualizzaDatiControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}

	@FXML
    private TextField nomeDipendente;

    @FXML
    private TextField cognomeDipendente;

    @FXML
    private TextField sessoDipendente;

    @FXML
    private TextField luogoNascitaDipendente;

    @FXML
    private TextField dataNascitaDipendente;

    @FXML
    private TextField codiceFiscaleDipendente;

    @FXML
    private TextField comuneResidenzaDipendente;

    @FXML
    private TextField provinciaResidenzaDipendente;

    @FXML
    private TextField indirizzoResidenzaDipendente;

    @FXML
    private TextField capDipendente;

    @FXML
    private TextField emailDipendente;

    @FXML
    private TextField recapitoTelefonicoDipendente;

    @FXML
    private TextField contoCorrenteDipendente;

    @FXML
    private TextField ruoloDipendente;

    @FXML
    private BorderPane root;

    @FXML
    private Text usernameDipendente;
    
    @FXML
    private ImageView photo;
    

    @FXML
    void menuDipendente() {
    	new MenuDipendenteControl("MenuDipendente", this.getStage(), this.getUtente()).start();
    }
    
    @FXML
    private Circle timbro;
    
    @Override
	public void init() {
    	super.init();
		this.usernameDipendente.setText(this.getUtente().getUsername());
		this.nomeDipendente.setText(this.getUtente().getNome());
		this.cognomeDipendente.setText(this.getUtente().getCognome());
		this.sessoDipendente.setText(this.getUtente().getSesso());
		this.dataNascitaDipendente.setText(this.getUtente().getDataNascita());
		this.luogoNascitaDipendente.setText(this.getUtente().getLuogoDiNascita());
		this.codiceFiscaleDipendente.setText(this.getUtente().getCodiceFiscale());
		this.comuneResidenzaDipendente.setText(this.getUtente().getComuneDiResidenza());
		this.provinciaResidenzaDipendente.setText(this.getUtente().getProvinciaDiResidenza());
		this.capDipendente.setText(Integer.toString(this.getUtente().getCAP()));
		this.indirizzoResidenzaDipendente.setText(this.getUtente().getIndirizzoResidenza());
		this.recapitoTelefonicoDipendente.setText(this.getUtente().getRecapitoTelefonico());
		this.emailDipendente.setText(this.getUtente().getEmail());
		this.contoCorrenteDipendente.setText(this.getUtente().getContoCorrente());
		this.ruoloDipendente.setText(this.getUtente().getRuolo().getNomeRuolo());
		this.photo.setImage(this.getUtente().getImage());
    }
}

package com.stp.dipendente.control;

import com.jfoenix.controls.JFXButton;
import com.stp.dipendente.MainApp;
import com.stp.dipendente.amministrativo.control.MenuAmministrativoControl;
import com.stp.dipendente.deposito.control.MenuDepositoControl;
import com.stp.dipendente.logistica.control.MenuLogisticaControl;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;

import java.io.IOException;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MenuDipendenteControl extends MainControlLogged{
	@FXML
    private Text usernameDipendente;
    
    @FXML
    private BorderPane root;
    
    @FXML
    private JFXButton visualizzaCedolinoButton;
    
    @FXML
    private JFXButton timbroCartellinoButton;

    @FXML
    private JFXButton visualizzaTurniButton;

    @FXML
    private JFXButton visualizzaDatiButton;
    
    @FXML
    private Circle timbro;
    
    @FXML
    private AnchorPane parentStackPane;
    
    public MenuDipendenteControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
	}
    
    @FXML
    private void timbroCartellino() {
    	Connection conn=null;
		try {
			conn=new Connection(this.getUtente());
			if(conn.timbroCartellino()) {
				this.getUtente().toggleStatoCartellino();
				toogleCartellino();
			}
		} catch (IOException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		}catch (ConnectionException e) {
    		this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    }

    @FXML
    private void visualizzaTurni() {
		this.setCenter(1200, 800);
		new VisualizzaTurniControl("VisualizzaTurni", this.getStage(), this.getUtente()).start();
    }

    @FXML
    private void visualizzaDati() {
    	new VisualizzaDatiControl("VisualizzaDati", this.getStage(), this.getUtente()).start();
    }

    @FXML
    private void visualizzaCedolino() {
    	new VisualizzaCedolinoControl("VisualizzaCedolino", this.getStage(), this.getUtente()).start();
    }

    @FXML
    private void logoutDipendente() {
    	new LoginControl("Login", this.getStage()).start();
    }
    
    @Override
	public void init() {
    	super.init();
    	this.getScene().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				System.out.println(event.getTarget());
				
			}
    		
    	});
    	HBox anc;
    	FXMLLoader loader = new FXMLLoader();
    	switch(this.getUtente().getRuolo().getIdRuolo()) {
    		case 1:
    			loader.setController(new MenuAmministrativoControl("MenuAmministrativo", this.getStage(), this.getUtente()));
    			loader.setLocation(MainApp.class.getResource("amministrativo/view/MenuAmministrativo.fxml"));
    		break;
    		case 2:
    			loader.setController(new MenuLogisticaControl("MenuLogistica", this.getStage(), this.getUtente()));
    			loader.setLocation(MainApp.class.getResource("logistica/view/MenuLogistica.fxml"));
    		break;
    		case 3:
    			loader.setController(new MenuDepositoControl("MenuDeposito", this.getStage(), this.getUtente()));
    			loader.setLocation(MainApp.class.getResource("deposito/view/MenuDeposito.fxml"));
    		break;
    	}
	    if(this.getUtente().getRuolo().getIdRuolo()!=4) {
	    	try {
				anc= (HBox) loader.load();
				root.setBottom(anc);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
        
        
        //((MainControl)loader.getController()).setInitElement(this.getStage(), this.getScene(), this.getMainApp());
    }

}

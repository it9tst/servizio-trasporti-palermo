package com.stp.dipendente.control;



import java.io.IOException;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.DipendenteException;
import com.stp.dipendente.util.json.JSONException;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import com.stp.dipendente.model.Dipendente;

public class LoginControl extends MainControl{
	private Dipendente utente;
	private boolean primoAccesso=false;
	private String vecchiaPassword;
	private JFXPasswordField password2;
	
	
	@FXML
    private JFXButton loginButton;
	
    @FXML
    private JFXPasswordField password;
    
    @FXML
    private JFXTextField username; 
    
    @FXML
    private AnchorPane parentStackPane;
    
    @FXML
    private ImageView userIcon;

    @FXML
    private ImageView passIcon;
    private String usernameP;
    
	public LoginControl(String xmlFileName, Stage window) {
		super(xmlFileName, window);
	}
    
    @FXML
    private void loginButtonClick(){
    	if(!primoAccesso) {
    		Connection conn = null;
    		try {
    			
        		conn= new Connection();
        		utente=null;
				utente = conn.login(username.getText(), cryptaPass(username.getText(), password.getText()));
				new MenuDipendenteControl("MenuDipendente", this.getStage(), utente).start();

            } catch (JSONException | DipendenteException | IOException e) {
				this.openDialog(this.parentStackPane, "Errore durante la connessione, riprovare più tardi o contattare l'amministratore", -160.0, -82.0, 0.0, 0.0);
			}catch(ConnectionException e) {
				if(e.getErrorCode()==-3) {
					this.vecchiaPassword=password.getText();
					this.usernameP=this.username.getText();
					this.parentStackPane.getChildren().remove(this.username);
					this.password2 = new JFXPasswordField();
					AnchorPane.setLeftAnchor(this.password2, 273.0);
					AnchorPane.setTopAnchor(this.password2, 133.0);
					this.password2.setPrefHeight(39.0);
					this.password2.setPrefWidth(292.0);
					this.parentStackPane.getChildren().add(this.password2);
					this.primoAccesso=true;
					this.password2.setPromptText("Inserire nuova password");
					this.password2.setFont(new Font(15.0));
					this.password.setPromptText("Reinserire nuova password");
					this.password.setText("");
					this.userIcon.setImage(this.passIcon.getImage());
					this.loginButton.setText("Reimposta");
					return;
				}
				this.openDialog(this.parentStackPane, e.getMessage(), -160.0, -82.0, 0.0, 0.0);
				
			}finally{
				if(conn!=null) {
					conn.close();
				}
			}
    	}else {
    		if(this.password2.getText().length()<6 || !this.password2.getText().matches(".*\\d+.*") || this.password2.getText().equals( this.password2.getText().toUpperCase()) || this.password2.getText().equals( this.password2.getText().toLowerCase())) {
    			this.openDialog(this.parentStackPane, "Inserire una password valida:\n- Minimo 6 caratteri\n- Almeno una lettera minuscola, maiuscola e un mumero", -160.0, -82.0, 0.0, 0.0);
    			return;
    		}
    		if(!this.password2.getText().equals(this.password.getText())) {
    			this.openDialog(this.parentStackPane, "Le due password devono coincidere", -160.0, -82.0, 0.0, 0.0);
    			return;
    		}
    		Connection conn;
    		try {
        		conn= new Connection();

				if(conn.cambiaPassword(this.usernameP, cryptaPass(this.usernameP,this.vecchiaPassword), cryptaPass(this.usernameP,this.password2.getText()))) {
					JFXButton btn= new JFXButton("OK");
					btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent arg0) {
							LoginControl.this.start();
						}
						
					});
					this.primoAccesso=false;
					this.openDialog(this.parentStackPane, "Password reimpostata correttamente", btn, -160.0, -82.0, 0.0, 0.0);
				}
    				
    				
    			
        		
            }catch (JSONException | IOException e) { 
            	this.openDialog(this.parentStackPane, "Errore durante la connessione, riprovare più tardi o contattare l'amministratore", -160.0, -82.0, 0.0, 0.0);
            } catch (ConnectionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	
    	
    }
    
    private String cryptaPass(String nick, String pass){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	    md.update((pass+nick).getBytes());
	    byte byteData[] = md.digest();

	    StringBuffer hashCodeBuffer = new StringBuffer();
	    for (int i = 0; i < byteData.length; i++) {
	        hashCodeBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	    }
	    return hashCodeBuffer.toString();
	}
    public void init() {
    	this.username.setText("luca.aldaire");
    	this.password.setText("Banana12");
    }
}

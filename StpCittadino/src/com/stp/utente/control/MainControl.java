package com.stp.utente.control;


import java.io.IOException;
import java.util.ArrayList;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainControl {
	private Scene rootScene;
	private Stage window;
	private String xmlFileName;
	private BorderPane rootLayout;

	public Stage getStage(){
		return this.window;
	}
	public Scene getScene(){
		return this.rootScene;
	}	
	
	public MainControl(String xmlFileName, Stage window) {
		this.xmlFileName=xmlFileName;
		this.window=window;
	}
	public void start() {
		try {
            FXMLLoader loader = new FXMLLoader();
            loader.setController(this);
            loader.setLocation(this.getClass().getResource("/com/stp/utente/view/"+this.xmlFileName+".fxml"));
            this.rootLayout = (BorderPane) loader.load();
            
	        this.rootLayout.getStylesheets().addAll(this.getClass().getResource("/css/style.css").toExternalForm());

            this.rootScene = new Scene(rootLayout);
            this.window.setScene(rootScene);
            this.window.setResizable(false);
              
            this.window.sizeToScene();
            this.window.show();
            this.rootLayout.requestFocus();
            
        }catch (IOException e){
            e.printStackTrace();
        }
		this.init();
	}
	public void init() {
		//System.out.println("Padre");
	}

	public void setCenter(int x, int y) {
		 Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		 this.window.setX((primaryScreenBounds.getWidth() - x)/2);
		 this.window.setY((primaryScreenBounds.getHeight() - y)/2);
	}
	public void setXmlFileName(String xmlFileName) {
		this.xmlFileName=xmlFileName;
	}
	public String getXmlFileName() {
		return this.xmlFileName;
	}
	public void openDialog(Pane parentDialog, String message, ArrayList<Button> btns) {
    	this.openDialog(parentDialog, message, btns, -50.0, -82.0, 0.0, 0.0);
    }
    public void openDialog(Pane parentDialog, String message, Button btn) {
    	ArrayList<Button> list = new ArrayList<Button>();
    	list.add(btn);
    	this.openDialog(parentDialog, message, list);
    }
    public void openDialog(Pane parentDialog, String message) {
    	this.openDialog(parentDialog, message, new JFXButton("OK"));
    }
    public void openDialog(Pane parentDialog, String message, ArrayList<Button> btns, double top, double bottom, double left, double right) {
    	StackPane sPane = new StackPane();
		AnchorPane.setTopAnchor(sPane, top);
		AnchorPane.setBottomAnchor(sPane, bottom);
		AnchorPane.setLeftAnchor(sPane, left);
		AnchorPane.setRightAnchor(sPane, right);
		parentDialog.getChildren().add(sPane);
		
		JFXDialogLayout content = new JFXDialogLayout();
		content.setBody(new Text(message));
		JFXDialog dialog = new JFXDialog(sPane, content, JFXDialog.DialogTransition.CENTER);
		content.setActions(btns);
		dialog.setOverlayClose(false);
		dialog.show();
		
		
		for(Button  btn: btns) {
			btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent event) {
					dialog.close();
					parentDialog.getChildren().remove(sPane);
				}
		    	 
		     });
		}
    }
    public void openDialog(Pane parentDialog, String message, double top, double bottom, double left, double right) {
    	ArrayList<Button> list = new ArrayList<Button>();
    	list.add(new JFXButton("OK"));
    	this.openDialog(parentDialog, message, list, top, bottom, left, right);
    }   
}
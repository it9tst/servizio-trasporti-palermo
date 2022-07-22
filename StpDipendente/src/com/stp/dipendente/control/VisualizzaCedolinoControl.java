package com.stp.dipendente.control;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableColumn;

import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import com.stp.dipendente.model.Cedolino;
import com.stp.dipendente.model.Dipendente;

import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.geometry.Pos;
import javafx.util.Callback;

public class VisualizzaCedolinoControl extends MainControlLogged  {
	private ObservableList<CedolinoRecursive> cedoliniObservable = FXCollections.observableArrayList();
	private ArrayList<Cedolino> elencoCedolini;
	private Image imageSalva;
	private Image imageEmail;
	
	
	public VisualizzaCedolinoControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}

	@FXML
	private JFXTreeTableView<CedolinoRecursive> table;
	 
    @FXML
    private JFXButton indietroButton;

    @FXML
    private Text usernameDipendente;

    @FXML
    private Circle timbro;
    
    @FXML
    private AnchorPane parentStackPane;

	class CedolinoRecursive extends RecursiveTreeObject<CedolinoRecursive>{
		private int index;
		StringProperty data;
		StringProperty totale;
		private Cedolino cedolino;
		
		public CedolinoRecursive(int index, Cedolino cedolino)
		{
			this.setCedolino(cedolino);
			this.setIndex(index);
			this.data  = new SimpleStringProperty(cedolino.getData().toString());
			this.totale  = new SimpleStringProperty(Double.toString(cedolino.getTotalePagamento()));			
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}

		public Cedolino getCedolino() {
			return cedolino;
		}

		public void setCedolino(Cedolino cedolino) {
			this.cedolino = cedolino;
		}
	}
	
    @FXML
    void menuDipendente() {
    	new MenuDipendenteControl("MenuDipendente", this.getStage(), this.getUtente()).start();
    }
    
    
    @SuppressWarnings("unchecked")
	public void init() {
    	super.init();
    	
    	JFXTreeTableColumn<CedolinoRecursive,String> data = new JFXTreeTableColumn<CedolinoRecursive,String>("Data");
    	
		data.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CedolinoRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CedolinoRecursive, String> param) {
                return param.getValue().getValue().data;
            }
        });
		//data.setPrefWidth(400.0);
		//data.setMaxWidth(400.0);
		//data.setMinWidth(400.0);
		
		JFXTreeTableColumn<CedolinoRecursive,String> totale = new JFXTreeTableColumn<CedolinoRecursive,String>("Totale");
		
		totale.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CedolinoRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CedolinoRecursive, String> param) {
                return param.getValue().getValue().totale;
            }
			
        });
		//totale.setPrefWidth(400.0);
		//totale.setMaxWidth(400.0);
		//totale.setMinWidth(400.0);
		
		
		try {
			this.imageSalva= new Image(new FileInputStream(new File(this.getClass().getResource("../../../../img/download.png").toURI())), 25.0, 25.0, true,  false);
			this.imageEmail= new Image(new FileInputStream(new File(this.getClass().getResource("../../../../img/email.png").toURI())), 25.0, 25.0, true,  false);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//private Image imageEmail;
		final TreeItem<CedolinoRecursive> root = new RecursiveTreeItem<CedolinoRecursive>(cedoliniObservable, RecursiveTreeObject::getChildren);
		table.getColumns().setAll(data, totale);
		table.setRoot(root);
		table.setShowRoot(false);
		this.addSalvaColumn();
		this.getCedolini();
		this.addMailColumn();
    }
    
    public void addSalvaColumn() {
		JFXTreeTableColumn<CedolinoRecursive,String> salvaButton = new JFXTreeTableColumn<CedolinoRecursive,String>("Download");
		salvaButton.setSortable(false);
		
		salvaButton.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CedolinoRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CedolinoRecursive, String> param) {
				return null;
            }
        });
		salvaButton.setCellFactory(new Callback<TreeTableColumn<CedolinoRecursive,String>, TreeTableCell<CedolinoRecursive,String>>(){

			@Override
			public TreeTableCell<CedolinoRecursive, String> call(TreeTableColumn<CedolinoRecursive, String> param) {
				return new ButtonCellSalva();
			}
			
		});
		this.table.getColumns().add(salvaButton);
	}
    
    public void addMailColumn() {
		JFXTreeTableColumn<CedolinoRecursive,String> mailButton = new JFXTreeTableColumn<CedolinoRecursive,String>("E-Mail");
		mailButton.setSortable(false);
		
		mailButton.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<CedolinoRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<CedolinoRecursive, String> param) {
				return null;
            }
        });
		mailButton.setCellFactory(new Callback<TreeTableColumn<CedolinoRecursive,String>, TreeTableCell<CedolinoRecursive,String>>(){

			@Override
			public TreeTableCell<CedolinoRecursive, String> call(TreeTableColumn<CedolinoRecursive, String> param) {
				return new ButtonEmailSalva();
			}
			
		});
		this.table.getColumns().add(mailButton);
	}
    
    private class ButtonCellSalva extends TreeTableCell<CedolinoRecursive, String> {
        final Button buttonSalva = new Button();

        public ButtonCellSalva(){
        	this.setAlignment(Pos.CENTER);
        	buttonSalva.setGraphic(new ImageView(VisualizzaCedolinoControl.this.imageSalva));
        	buttonSalva.textProperty().bind(itemProperty());
        	buttonSalva.setMaxHeight(25.0);
        	buttonSalva.setMinHeight(25.0);
        	buttonSalva.setPrefHeight(25.0);
        	buttonSalva.setStyle("-fx-background-color: #ffffff00 ;");
        	//buttonElimina.setFont(Font.font ("System", 12));
        	this.setStyle("-fx-padding: 0 0 0 0");
        	
        	buttonSalva.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                	VisualizzaCedolinoControl.this.salvaCedolino(((CedolinoRecursive) getTreeTableRow().getItem()));
                	//ButtonCell.this.getChildren().remove(ButtonCell.this.buttonElimina);
                }
            });

        }

        @Override
        protected void updateItem(String t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(buttonSalva);
                
            }
        }
    }
    
    private class ButtonEmailSalva extends TreeTableCell<CedolinoRecursive, String> {
        final Button buttonMail = new Button();

        public ButtonEmailSalva(){
        	this.setAlignment(Pos.CENTER);
        	buttonMail.textProperty().bind(itemProperty());
        	buttonMail.setGraphic(new ImageView(VisualizzaCedolinoControl.this.imageEmail));
        	buttonMail.setMaxHeight(25.0);
        	buttonMail.setMinHeight(25.0);
        	buttonMail.setPrefHeight(25.0);
        	buttonMail.setStyle("-fx-background-color: #ffffff00 ;");
        	//buttonElimina.setFont(Font.font ("System", 12));
        	this.setStyle("-fx-padding: 0 0 0 0");
        	
        	buttonMail.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                	VisualizzaCedolinoControl.this.emailCedolino(((CedolinoRecursive) getTreeTableRow().getItem()));
                	//ButtonCell.this.getChildren().remove(ButtonCell.this.buttonElimina);
                }
            });

        }

        @Override
        protected void updateItem(String t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
                setGraphic(buttonMail);
                
            }
        }
    }
    
    public void getCedolini() {
    	Connection conn=null;
		try {
			conn = new Connection(this.getUtente());
			this.elencoCedolini= conn.getCedolini();
			int index=0;
			for(Cedolino t: this.elencoCedolini) {
				this.cedoliniObservable.add(new CedolinoRecursive(index++,t));
			}
		}  catch (IOException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		}catch (ConnectionException e) {
    		this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    }
    
    public void salvaCedolino(CedolinoRecursive cedolino) {
    	Connection conn=null;
		try {
			conn = new Connection(this.getUtente());
			ByteArrayOutputStream bos= conn.receivePdf( cedolino.getCedolino().getFileCedolino());
			
	    	
	    	FileChooser fileChooser = new FileChooser();
	    	  
	        //Set extension filter
	        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf");
	        fileChooser.getExtensionFilters().add(extFilter);
	        
	        //Show save file dialog
	        File file = fileChooser.showSaveDialog(this.getStage());
	        
	        if(file != null){
	        	try(OutputStream outputStream = new FileOutputStream(file)) {
	        		bos.writeTo(outputStream);
	        	} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        }
		} catch (ConnectionException | IOException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    	
    	
    }
    public void emailCedolino(CedolinoRecursive cedolino) {
    	Connection conn=null;
		try {
			conn = new Connection(this.getUtente());
			conn.emailCedolino(cedolino.getCedolino().getFileCedolino());
			this.openDialog(this.parentStackPane, "Email inviata correttamente");

		} catch (ConnectionException | IOException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    }
}

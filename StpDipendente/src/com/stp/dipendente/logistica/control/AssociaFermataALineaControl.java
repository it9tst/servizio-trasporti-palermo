package com.stp.dipendente.logistica.control;


import java.io.IOException;
import java.util.ArrayList;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.stp.dipendente.control.MainControlLogged;
import com.stp.dipendente.control.MenuDipendenteControl;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.model.Fermata;
import com.stp.dipendente.model.Linea;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.json.JSONException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;

public class AssociaFermataALineaControl extends MainControlLogged{
	private Linea linea;
	private ObservableList<FermataRecursive> fermateObsevable;
	private ObservableList<FermataRecursive> fermateObsevableView;
	private ArrayList<Fermata> elencoFermate;
	private ArrayList<Fermata> elencoFermateView;
	private int numFermateInit;
	private boolean edited=false;

	private FermataRecursive capA=null;
	private FermataRecursive capB=null;
	private int indexA;
	private int indexB;
	
    public AssociaFermataALineaControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}
    

    @FXML
    private JFXTextField cercaFermata;

    @FXML
    private JFXButton cercaFermataButton;

    @FXML
    private TextField idLinea;

    @FXML
    private JFXTreeTableView<FermataRecursive> table;

    @FXML
    private JFXTreeTableView<FermataRecursive> view;

    @FXML
    private Text usernameDipendente;

    @FXML
    private Circle timbro;
    
    @FXML
    private AnchorPane parentStackPane;
    
    @FXML
    void cercaFermata() {
      	if(this.cercaFermata.getText().length()<1) {
    		this.getFermate();
    		return;
    				
    	}
    	Connection conn= null;
		try {
			conn = new Connection(this.getUtente());
			this.elencoFermate= conn.cercaFermateElenco(this.cercaFermata.getText());
			int index=0;
			this.fermateObsevable.clear();
			for(Fermata t: this.elencoFermate) {
				this.fermateObsevable.add(new FermataRecursive(index,t));
			}
			this.aggiornaLista();
		}catch (IOException | JSONException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		} catch (ConnectionException e) {
			this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    }
    
    public void aggiornaLista() {
    	
    }
    
    class FermataRecursive extends RecursiveTreeObject<FermataRecursive>{
		StringProperty id;
		StringProperty via;
		StringProperty civico;
		StringProperty eliminaAction;
		private Fermata fermata;
		public FermataRecursive(int index, Fermata fermata){
			this.fermata=fermata;
			this.id= new SimpleStringProperty(Integer.toString(fermata.getIdFermata()));
			this.via = new SimpleStringProperty(fermata.getIndirizzo());
			this.civico  = new SimpleStringProperty(fermata.getNumeroCivico());
			this.eliminaAction = new SimpleStringProperty("Elimina");
		}
		public Fermata getFermata() {
			return fermata;
		}
		public int getId() {
			return Integer.parseInt(id.getValue());
		}
	}

    @FXML
    void menuDipendente() {
		this.setCenter(800, 600);
		this.setPkg("");
		new MenuDipendenteControl("MenuDipendente", this.getStage(), this.getUtente()).start();
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public void init() {
		super.init();
		this.idLinea.setText(Integer.toString(this.linea.getIdLinea()));
		this.table.setRowFactory(new Callback<TreeTableView<FermataRecursive>, TreeTableRow<FermataRecursive>>(){

			@Override
			public TreeTableRow<FermataRecursive> call(TreeTableView<FermataRecursive> param) {
				TreeTableRow<FermataRecursive> row = new TreeTableRow<FermataRecursive>();

				row.setOnDragDetected(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {
						Dragboard db = row.startDragAndDrop(TransferMode.ANY);
				        ClipboardContent content = new ClipboardContent();
				        content.putString(row.getItem().getFermata().getIndirizzo()+" "+row.getItem().getFermata().getNumeroCivico());
				        db.setContent(content);
					    event.consume();
					}
					
				});
				return row;
			}
			
		});
		
		JFXTreeTableColumn<FermataRecursive,String> via = new JFXTreeTableColumn<FermataRecursive,String>("Indirizzo");
		via.setContextMenu(null);
		via.setPrefWidth(321.0);
		via.setMaxWidth(321.0);
		via.setMinWidth(321.0);
		via.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FermataRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FermataRecursive, String> param) {
                return param.getValue().getValue().via ;
            }
        });
		
		JFXTreeTableColumn<FermataRecursive,String> civico = new JFXTreeTableColumn<FermataRecursive,String>("Numero Civico");
		civico.setContextMenu(null);
		civico.setPrefWidth(201.0);
		civico.setMaxWidth(201.0);
		civico.setMinWidth(201.0);
		civico.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FermataRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FermataRecursive, String> param) {
                return param.getValue().getValue().civico ;
            }
        });
		
		this.fermateObsevable = FXCollections.observableArrayList();
		this.getFermate();
		
		final TreeItem<FermataRecursive> root = new RecursiveTreeItem<FermataRecursive>(fermateObsevable, RecursiveTreeObject::getChildren);
		this.table.getColumns().setAll(via, civico);
		this.table.setRoot(root);
		this.table.setShowRoot(false);
		
		
		
		

		
		
		//view
		this.view.setOnDragOver(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				if(fermateObsevableView.size()==0) {
					event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			        event.consume();
				}
		       
			}
			
		});
		this.view.setOnDragDropped(new EventHandler<DragEvent>() {

			@Override
			public void handle(DragEvent event) {
				if(fermateObsevableView.size()==0) {
					AssociaFermataALineaControl.this.edited=true;
					AssociaFermataALineaControl.this.fermateObsevableView.add(((TreeTableRow<FermataRecursive>)event.getGestureSource()).getItem());
				}
			}
			
		});
		this.view.setRowFactory(new Callback<TreeTableView<FermataRecursive>, TreeTableRow<FermataRecursive>>(){

			@Override
			public TreeTableRow<FermataRecursive> call(TreeTableView<FermataRecursive> param) {
				TreeTableRow<FermataRecursive> row = new TreeTableRow<FermataRecursive>();
				row.setOnDragDropped(new EventHandler<DragEvent>() {

					@Override
					public void handle(DragEvent event) {
						Fermata daInserire=((TreeTableRow<FermataRecursive>)event.getGestureSource()).getItem().getFermata();
						FermataRecursive daInserireRecursive=  ((TreeTableRow<FermataRecursive>)event.getGestureSource()).getItem();
						for(FermataRecursive t: AssociaFermataALineaControl.this.fermateObsevableView) {
							if(t.getFermata().equals(daInserire)){
								return;
							}
						}
						
						AssociaFermataALineaControl.this.edited=true;
						System.out.println(event.getY());
						if(((TreeTableRow<FermataRecursive>)event.getGestureTarget()).isEmpty()) {
							AssociaFermataALineaControl.this.fermateObsevableView.add(daInserireRecursive);
						}else {
							ObservableList<FermataRecursive> elenco = FXCollections.observableArrayList();
							elenco.addAll(AssociaFermataALineaControl.this.fermateObsevableView);
							AssociaFermataALineaControl.this.fermateObsevableView.clear();
							int i=0;
							if(event.getY()<24.0) {
								for(FermataRecursive t: elenco) {
									
									if(((TreeTableRow<FermataRecursive>)event.getGestureTarget()).getIndex()==i++) {
										AssociaFermataALineaControl.this.fermateObsevableView.add(daInserireRecursive);
									}
									AssociaFermataALineaControl.this.fermateObsevableView.add(t);
								}
							}else {
								for(FermataRecursive t: elenco) {
									AssociaFermataALineaControl.this.fermateObsevableView.add(t);
									if(((TreeTableRow<FermataRecursive>)event.getGestureTarget()).getIndex()==i++) {
										AssociaFermataALineaControl.this.fermateObsevableView.add(daInserireRecursive);
									}
								}
							}
							
						}
						AssociaFermataALineaControl.this.view.getColumns().remove(2);
						AssociaFermataALineaControl.this.view.getColumns().remove(2);
						AssociaFermataALineaControl.this.addEliminaColumn();
						AssociaFermataALineaControl.this.addCheckColumn();
						
					}
					
				});
				row.setOnDragOver(new EventHandler<DragEvent>() {

					@Override
					public void handle(DragEvent event) {
						event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
				        event.consume();
				       
					}
					
				});
				return row;
			}
			
		});
		
		
		
		JFXTreeTableColumn<FermataRecursive,String> viaview = new JFXTreeTableColumn<FermataRecursive,String>("Indirizzo");
		viaview.setSortable(false);
		viaview.setContextMenu(null);
		viaview.setPrefWidth(213.0);
		viaview.setMaxWidth(213.0);
		viaview.setMinWidth(213.0);
		viaview.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FermataRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FermataRecursive, String> param) {
                return param.getValue().getValue().via ;
            }
        });
		
		JFXTreeTableColumn<FermataRecursive,String> civicoview = new JFXTreeTableColumn<FermataRecursive,String>("Numero Civico");
		civicoview.setSortable(false);
		civicoview.setContextMenu(null);
		civicoview.setPrefWidth(120.0);
		civicoview.setMaxWidth(120.0);
		civicoview.setMinWidth(120.0);
		civicoview.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FermataRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FermataRecursive, String> param) {
                return param.getValue().getValue().civico ;
            }
        });


		this.fermateObsevableView = FXCollections.observableArrayList();
		this.getFermateLinea();
		
		final TreeItem<FermataRecursive> rootview = new RecursiveTreeItem<FermataRecursive>(fermateObsevableView, RecursiveTreeObject::getChildren);
		this.view.getColumns().setAll(viaview, civicoview);
		this.view.setRoot(rootview);
		this.view.setShowRoot(false);

		this.addEliminaColumn();
		this.addCheckColumn();



		
    }
    public void getFermate() {
    	Connection conn= null;
		try {
			conn = new Connection(this.getUtente());
			this.elencoFermate= conn.getFermate();
			int index=0;
			for(Fermata t: this.elencoFermate) {
				this.fermateObsevable.add(new FermataRecursive(index,t));
			}
			
		}catch (IOException | JSONException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		} catch (ConnectionException e) {
			this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    }
    public AssociaFermataALineaControl setLinea(Linea linea) {
    	this.linea=linea;
    	return this;
    }
    
    
    @FXML
    public void salva() {
    	if(!edited) {
    		this.setCenter(800, 600);
        	new GestioneLineeControl("SchedaLinea", this.getStage(), this.getUtente()).setLinea(this.linea).setPkg("logistica/").start();
        	return;
    	}
    	Connection conn=null;
    	try {
			
    		if(this.numFermateInit!=0) {
    			conn = new Connection(this.getUtente());
				if(conn.pulisciFermateLinea(this.linea)) {
				   	JFXButton btn = new JFXButton("OK");
					btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent event) {
							AssociaFermataALineaControl.this.annulla();
							
						}
					});
					int index=1;
					for(FermataRecursive f: fermateObsevableView) {
						System.out.println(f.getFermata().getIndirizzo());
						conn =  new Connection(this.getUtente());
			    		conn.aggiungiFermataLinea(this.linea, f.getFermata(), index++);
			    	}
					numFermateInit=fermateObsevableView.size();
					this.openDialog(this.parentStackPane, "Fermate salvate correttamente", btn, -150.0, -182.0, 0.0, 0.0);
					
				}	
    		}else {
    			
    			JFXButton btn = new JFXButton("OK");
				btn.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent event) {
						AssociaFermataALineaControl.this.annulla();
						
					}
				});
    			int index=1;
				for(FermataRecursive f: fermateObsevableView) {
					System.out.println(f.getFermata().getIndirizzo());
					conn =  new Connection(this.getUtente());
		    		conn.aggiungiFermataLinea(this.linea, f.getFermata(), index++);
		    	}
				numFermateInit=fermateObsevableView.size();
				this.openDialog(this.parentStackPane, "Fermate salvate correttamente", btn, -150.0, -182.0, 0.0, 0.0);
    		}
    		if(capA!= null || capB!=null) {
    			conn =new Connection(this.getUtente());
    			if(capA==null) {
    				conn.aggiungiCapolinea(this.linea, null, capB.getFermata());
    			}else if(capB==null) {
    				conn.aggiungiCapolinea(this.linea, capA.getFermata(), null);
    			}else {
    				conn.aggiungiCapolinea(this.linea, capA.getFermata(), capB.getFermata());
    			}
    			
    				
    			
    		}
						
			
    	}catch (IOException | JSONException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		} catch (ConnectionException e) {
			this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}

    }
    @FXML
    public void annulla() {
    	this.setCenter(800, 600);
    	new GestioneLineeControl("SchedaLinea", this.getStage(), this.getUtente()).setLinea(this.linea).setPkg("logistica/").start();
    }
    
    private void getFermateLinea() {
    	Connection conn= null;
		try {
			conn = new Connection(this.getUtente());
			this.elencoFermateView= conn.getFermateLinea(this.linea);
			this.numFermateInit=this.elencoFermateView.size();
			int index=0;
			for(Fermata t: this.elencoFermateView) {
				FermataRecursive tmp=new FermataRecursive(index,t);
				if(this.linea.getCapA()!=null && t.equals(this.linea.getCapA())) {
					this.capA=tmp;
					this.indexA=index;
				}
				if(this.linea.getCapB()!=null && t.equals(this.linea.getCapB())) {
					this.capB=tmp;
					this.indexB=index;
				}
				this.fermateObsevableView.add(tmp);
				index++;
			}
			

		}catch (IOException | JSONException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		} catch (ConnectionException e) {
			this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    }
    
    private class ButtonCell extends TreeTableCell<FermataRecursive, String> {
        final JFXButton buttonElimina = new JFXButton("Elimina");

        public ButtonCell(){
        	this.setAlignment(Pos.CENTER);
        	buttonElimina.setButtonType(ButtonType.FLAT);
        	buttonElimina.textProperty().bind(itemProperty());
        	buttonElimina.setMaxHeight(30.0);
        	buttonElimina.setMinHeight(30.0);
        	buttonElimina.setPrefHeight(30.0);
        	buttonElimina.setStyle("-fx-background-color: #f0f0f0 ;");
        	buttonElimina.setFont(Font.font ("System", 12));
        	this.setStyle("-fx-padding: 0 0 0 0");
        	
        	buttonElimina.setOnAction(new EventHandler<ActionEvent>(){
                @Override
                public void handle(ActionEvent t) {
                	AssociaFermataALineaControl.this.eliminaFermata(getTreeTableRow().getIndex(), ((FermataRecursive) getTreeTableRow().getItem()));
                	//ButtonCell.this.getChildren().remove(ButtonCell.this.buttonElimina);
                }
            });

        }

        @Override
        protected void updateItem(String t, boolean empty) {
            super.updateItem(t, empty);
            if (!empty) {
            	
                setGraphic(buttonElimina);
                
            }
        }
    }
    
    public void eliminaFermata(int index, FermataRecursive fermata) {
    	this.edited=true;
    	if(capA!=null && capA.getFermata().equals(fermata.getFermata())){
    		capA=null;
    	}
    	if(capB!=null && capB.getFermata().equals(fermata.getFermata())){
    		capB=null;
    	}
    	AssociaFermataALineaControl.this.fermateObsevableView.remove(fermata);
    	this.view.getColumns().remove(2);
    	this.view.getColumns().remove(2);
		this.addEliminaColumn();
		this.addCheckColumn();
    }
    
    public void addEliminaColumn() {
		JFXTreeTableColumn<FermataRecursive,String> eliminaButton = new JFXTreeTableColumn<FermataRecursive,String>("Elimina");
		eliminaButton.setSortable(false);
		eliminaButton.setPrefWidth(82.0);
		eliminaButton.setMaxWidth(82.0);
		eliminaButton.setMinWidth(82.0);
		eliminaButton.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FermataRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FermataRecursive, String> param) {
				return new SimpleStringProperty("Elimina");
            }
        });
		eliminaButton.setCellFactory(new Callback<TreeTableColumn<FermataRecursive,String>, TreeTableCell<FermataRecursive,String>>(){

			@Override
			public TreeTableCell<FermataRecursive, String> call(TreeTableColumn<FermataRecursive, String> param) {
				return new ButtonCell();
			}
			
		});
		this.view.getColumns().add(eliminaButton);
	}
    
    private class CheckCell extends TreeTableCell<FermataRecursive, String> {
        final JFXCheckBox checkCapolinea = new JFXCheckBox();

        public CheckCell(){
        	this.setAlignment(Pos.CENTER);
        	checkCapolinea.textProperty().bind(itemProperty());
        	checkCapolinea.setOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent event) {
					edited=true;
					// TODO Auto-generated method stub
					if(capA!=null && capB!=null) {
						checkCapolinea.setSelected(false);
                		if(getTreeTableRow().getIndex()==indexA) {
                			capA=null;
                		}else if(getTreeTableRow().getIndex()==indexB) {
                			capB=null;
                		}
                	}else {
                		if(!checkCapolinea.isSelected()) {
                			System.out.println("adsf");
                			if(getTreeTableRow().getIndex()==indexA) {
                    			capA=null;
                    		}else if(getTreeTableRow().getIndex()==indexB) {
                    			capB=null;
                    		}
                		}else {
                			if(capA==null && capB==null) {
                        		capA=((FermataRecursive) getTreeTableRow().getItem());
                        		indexA=getTreeTableRow().getIndex();
                        	}else if(capA!=null && capB==null) {
                        		if(getTreeTableRow().getIndex()>indexA) {
                        			capB=((FermataRecursive) getTreeTableRow().getItem());
                        			indexB=getTreeTableRow().getIndex();
                        		}else {
                        			capB=capA;
                        			indexB=indexA;
                        			capA=((FermataRecursive) getTreeTableRow().getItem());
                        			indexA=getTreeTableRow().getIndex();
                        		}
                        	}else {
                        		if(getTreeTableRow().getIndex()<indexB) {
                        			capA=((FermataRecursive) getTreeTableRow().getItem());
                        			indexA=getTreeTableRow().getIndex();
                        		}else {
                        			capA=capB;
                        			indexA=indexB;
                        			capB=((FermataRecursive) getTreeTableRow().getItem());
                        			indexB=getTreeTableRow().getIndex();
                        		}
                        	}
                		}
                	}
					if(capA!=null){
						System.out.println(capA.getFermata());
					}
					if(capB!=null){
						System.out.println(capB.getFermata());
					}
						
                }
				
        		
        	});
        	
        	
        }

        @Override
        protected void updateItem(final String t, final boolean empty) {
            super.updateItem(t, empty);
            if(capA!=null) {
            	if(((FermataRecursive) getTreeTableRow().getItem())!=null && ((FermataRecursive) getTreeTableRow().getItem()).getFermata().equals(capA.getFermata())) {
            		checkCapolinea.setSelected(true);
            		if (!empty) {
                        setGraphic(checkCapolinea);
                        return;
                    }
            	}
            }
            if(capB!=null) {
            	if(((FermataRecursive) getTreeTableRow().getItem())!=null && ((FermataRecursive) getTreeTableRow().getItem()).getFermata().equals(capB.getFermata())) {
            		checkCapolinea.setSelected(true);
            		if (!empty) {
                        setGraphic(checkCapolinea);
                        return;
                    }
            	}
            }
            checkCapolinea.setSelected(false);
            if (!empty) {
                setGraphic(checkCapolinea);
            }
           
        }
    }
    
    public void addCheckColumn() {
		JFXTreeTableColumn<FermataRecursive,String> capolineaCheck = new JFXTreeTableColumn<FermataRecursive,String>("Capolinea");
		capolineaCheck.setSortable(false);
		capolineaCheck.setPrefWidth(100.0);
		capolineaCheck.setMaxWidth(100.0);
		capolineaCheck.setMinWidth(100.0);
		capolineaCheck.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<FermataRecursive, String>, ObservableValue<String>>() {
	          
			@Override
            public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<FermataRecursive, String> param) {
				return new SimpleStringProperty("");
            }
        });
		capolineaCheck.setCellFactory(new Callback<TreeTableColumn<FermataRecursive,String>, TreeTableCell<FermataRecursive,String>>(){

			@Override
			public TreeTableCell<FermataRecursive, String> call(TreeTableColumn<FermataRecursive, String> param) {
				return new CheckCell();
			}
			
		});
		this.view.getColumns().add(capolineaCheck);
	}

}

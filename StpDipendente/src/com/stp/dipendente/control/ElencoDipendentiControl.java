package com.stp.dipendente.control;


import java.io.IOException;

import java.util.ArrayList;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableColumn;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;

import com.stp.dipendente.model.Dipendente;

import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.DipendenteException;
import com.stp.dipendente.util.json.JSONException;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.util.Callback;

public class ElencoDipendentiControl extends MainControlLogged {
	private ObservableList<DipendenteRecursive> dipendentiObservable;
	public ObservableList<DipendenteRecursive> getDipendentiObservable() {
		return dipendentiObservable;
	}

	public ArrayList<Dipendente> getElencoDipendenti() {
		return elencoDipendenti;
	}

	private ArrayList<Dipendente> elencoDipendenti;
	private int filtro=-1;
	private int disponibili=0;
	
    public ElencoDipendentiControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}

	public int getFiltro() {
		return filtro;
	}

	public ElencoDipendentiControl setFiltro(int filtro) {
		this.filtro = filtro;
		return this;
	}
	public ElencoDipendentiControl setDisponibili(int disponibili) {
		this.disponibili = disponibili;
		return this;
	}
	@FXML
    private BorderPane root;
    
    @FXML
    private AnchorPane contentPane;
    
	@FXML
	private JFXTreeTableView<DipendenteRecursive> table;
	 
    @FXML
    private JFXButton indietroButton;

    @FXML
    private Text usernameDipendente;

    @FXML
    private Circle timbro;

    
    @FXML
    private JFXTextField cercaPersonale; 
    
    @FXML
    private AnchorPane parentStackPane;
    
    @FXML
    public void cercaPersonale() {
    	if(this.cercaPersonale.getText().length()>0) {
	    	Connection conn=null;
			try {
				conn = new Connection(this.getUtente());

				if(this.filtro!=-1) {
					this.elencoDipendenti= conn.cercaDipendenti(this.cercaPersonale.getText(), this.filtro, this.disponibili);
				}else {
					this.elencoDipendenti= conn.cercaDipendenti(this.cercaPersonale.getText());
				}
				
				int index=0;
				this.dipendentiObservable.clear();
				for(Dipendente t: this.elencoDipendenti) {
					this.dipendentiObservable.add(new DipendenteRecursive(index,t));
				}

			} catch (IOException | JSONException | DipendenteException e) {
				this.openDialog(this.parentStackPane, "Errore connessione");
			} catch (ConnectionException e) {
				this.openDialog(this.parentStackPane, e.getMessage());
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}
    	}else {
    		this.dipendentiObservable.clear();
    		this.getDipendenti();
    	}
    }

    public BorderPane getRoot() {
    	return this.root;
    }
	
    public AnchorPane getContentPane() {
    	return this.contentPane;
    }
    public JFXTreeTableView<DipendenteRecursive> getTreeTable(){
    	return this.table;
    }



	public class DipendenteRecursive extends RecursiveTreeObject<DipendenteRecursive>{
		private StringProperty nome;
		private StringProperty cognome;
		private StringProperty codiceFiscale;
		private StringProperty ruolo;
		private Dipendente dipendente;
		private int index;
		
		public DipendenteRecursive(int index, Dipendente dipendente)
		{
			this.setIndex(index);
			this.nome = new SimpleStringProperty(dipendente.getNome());
			this.cognome  = new SimpleStringProperty(dipendente.getCognome());
			this.codiceFiscale  = new SimpleStringProperty(dipendente.getCodiceFiscale());
			this.ruolo = new SimpleStringProperty(dipendente.getRuolo().getNomeRuolo());
			this.dipendente = dipendente;
		}
		
		public StringProperty getCodiceFiscale() {
	        return this.codiceFiscale;
	    }
		public StringProperty getNome() {
	        return this.nome;
	    }
		public StringProperty getCognome() {
	        return this.cognome;
	    }
		public StringProperty getRuolo() {
	        return this.ruolo;
	    }
		public Dipendente getDipendente() {
			return this.dipendente;
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

    @FXML
    public void menuDipendente() {
		this.setCenter(800, 600);
		new MenuDipendenteControl("MenuDipendente", this.getStage(), this.getUtente()).start();
    }
    
    @SuppressWarnings("unchecked")
	@Override
	public void init() {
    	super.init();
    	if(this.getXmlFileName().equals("ElencoDipendenti")) {
    		JFXTreeTableColumn<DipendenteRecursive,String> nome = new JFXTreeTableColumn<DipendenteRecursive,String>("Nome");
        	
    		nome.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<DipendenteRecursive, String>, ObservableValue<String>>() {
    	          
    			@Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<DipendenteRecursive, String> param) {
                    return param.getValue().getValue().getNome() ;
                }
            });
    		
    		JFXTreeTableColumn<DipendenteRecursive,String> cognome = new JFXTreeTableColumn<DipendenteRecursive,String>("Cognome");
    		
    		cognome.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<DipendenteRecursive, String>, ObservableValue<String>>() {
    	          
    			@Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<DipendenteRecursive, String> param) {
                    return param.getValue().getValue().getCognome();
                }
    			
            });
    		
    		JFXTreeTableColumn<DipendenteRecursive,String> codiceFiscale = new JFXTreeTableColumn<DipendenteRecursive,String>("Codice Fiscale");
    		
    		codiceFiscale.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<DipendenteRecursive, String>, ObservableValue<String>>() {
    	          
    			@Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<DipendenteRecursive, String> param) {
                    return param.getValue().getValue().getCodiceFiscale();
                }
            });
    		
    		JFXTreeTableColumn<DipendenteRecursive,String> ruolo = new JFXTreeTableColumn<DipendenteRecursive,String>("Ruolo");
    		
    		ruolo.setCellValueFactory(new Callback<TreeTableColumn.CellDataFeatures<DipendenteRecursive, String>, ObservableValue<String>>() {
    	          
    			@Override
                public ObservableValue<String> call(TreeTableColumn.CellDataFeatures<DipendenteRecursive, String> param) {
                    return param.getValue().getValue().getRuolo();
                }
            });
    		
    		
    		this.dipendentiObservable = FXCollections.observableArrayList();
    		this.getDipendenti();
    		    		
    		
    		final TreeItem<DipendenteRecursive> root = new RecursiveTreeItem<DipendenteRecursive>(this.dipendentiObservable, RecursiveTreeObject::getChildren);
    		table.getColumns().setAll(nome, cognome, codiceFiscale, ruolo);
    		table.setRoot(root);
    		table.setShowRoot(false);
    	
    		this.getTreeTable().setRowFactory(new Callback<TreeTableView<DipendenteRecursive>, TreeTableRow<DipendenteRecursive>>(){
    			
    			@Override
    			public TreeTableRow<DipendenteRecursive> call(TreeTableView<DipendenteRecursive> view) {
    				TreeTableRow<DipendenteRecursive> row = new TreeTableRow<DipendenteRecursive>();
    				row.setOnMouseClicked(new EventHandler<MouseEvent> () {
    					@Override
    					public void handle(MouseEvent event) {
    						if(event.getClickCount()==2 && !row.isEmpty()) {
    							ElencoDipendentiControl.this.action(row.getItem().getDipendente());
    							
    						}
    						
    					}
    					
    				});
    				return row;
    			}
        		
        	});
    	}
    	
    }
    
    public void getDipendenti() {
    	Connection conn=null;
		try {
			conn = new Connection(this.getUtente());

			if(this.filtro!=-1) {
				this.elencoDipendenti= conn.getDipendenti(this.filtro, this.disponibili);
			}else {
				this.elencoDipendenti= conn.getDipendenti();
			}
			
			int index=0;
			this.dipendentiObservable.clear();
			for(Dipendente t: this.elencoDipendenti) {
				this.dipendentiObservable.add(new DipendenteRecursive(index,t));
			}

		} catch (IOException | JSONException | DipendenteException e) {
			this.openDialog(this.parentStackPane, "Errore connessione");
		} catch (ConnectionException e) {
			this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
		
	}
    
	public void action(Dipendente dipendente) {
    	;
    }

    
}

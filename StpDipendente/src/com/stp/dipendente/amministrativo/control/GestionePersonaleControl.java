package com.stp.dipendente.amministrativo.control;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

import com.stp.dipendente.control.ElencoDipendentiControl;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.model.Ruolo;
import com.stp.dipendente.util.Comune;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.DipendenteException;
import com.stp.dipendente.util.json.JSONException;
import com.stp.dipendente.util.json.JSONObject;

public class GestionePersonaleControl extends ElencoDipendentiControl{
	public GestionePersonaleControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}

	private boolean fotoEdited=false;
	private JFXButton addDipendente;
	private Dipendente dipendenteModifica;
	private boolean disabilitati=false;
	private ArrayList<Ruolo> ruoli;
	ObservableList<String> sessoObs = 
		    FXCollections.observableArrayList(
		        "M",
		        "F"
		    );

	ObservableList<Ruolo> ruoloObs = FXCollections.observableArrayList();
	private HashMap<String, Comune> comuni;
	private HashMap<String, Comune> comuni2;
	private String imgExtension=null;
	
	
    @FXML
    private BorderPane root;

    @FXML
    private Text usernameDipendente;

    @FXML
    private Circle timbro;

    @FXML
    private JFXButton salvaButton;

    @FXML
    private JFXButton annullaButton;

    @FXML
    private AnchorPane parentStackPane;

    @FXML
    private TextField nomeDipendente;

    @FXML
    private TextField cognomeDipendente;

    @FXML
    private JFXComboBox<String> sessoDipendente;

    @FXML
    private TextField luogoNascitaDipendente;

    @FXML
    private TextField provinciaNascitaDipendente;

    @FXML
    private DatePicker dataNascitaDipendente;

    @FXML
    private Button caricaImgButton;

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
    private JFXComboBox<Ruolo> ruoloDipendente;

    @FXML
    private TextField codiceFiscaleDipendente;

    @FXML
    private Button calcolaCFButton;

    @FXML
    private JFXButton modificaDatiButton;
    
    @FXML
    private Button reimpostaPasswordButton;
    
    @FXML
    private Button eliminaDipendenteButton;
    
    @FXML
    private Button licenziaDipendenteButton;
    
    @FXML
    private JFXButton indietroButton;
    

    @FXML
    private Button reimpostaButton;

    @FXML
    private Button eliminaButton;

    @FXML
    private Button licenziaButton;
    
    @FXML
    private Text cfText;
    
    @FXML
    private ImageView photo;
    
    @FXML
    void calcolaCF() {
    	try {
    		String dataNascita=null;
    		if(this.dataNascitaDipendente.getValue()!=null) {
    			dataNascita=this.dataNascitaDipendente.getValue().toString();
    		}
    		String cf=Dipendente.calcolaCF(this.nomeDipendente.getText(), this.cognomeDipendente.getText(), 
    				dataNascita , this.luogoNascitaDipendente.getText(), 
        			this.sessoDipendente.getValue(), this.comuni, this.comuni2);
    		this.codiceFiscaleDipendente.setText(cf);
    	}catch(NullPointerException e) {
    		this.openDialog(this.parentStackPane, "Compila i campi");
    	} catch (DipendenteException e) {
    		this.openDialog(this.parentStackPane, e.getMessage());
		}
    	
    	
    }

    
    @FXML
    void caricaImg() {
    	FileChooser fileChooser = new FileChooser();
    	FileChooser.ExtensionFilter png = new FileChooser.ExtensionFilter("PNG", "*.png");
    	FileChooser.ExtensionFilter jpg = new FileChooser.ExtensionFilter("JPEG", "*.jpg","*.jpeg");
    	fileChooser.getExtensionFilters().add(png);
    	fileChooser.getExtensionFilters().add(jpg);
    	fileChooser.setTitle("Seleziona immagine");
    	File img = fileChooser.showOpenDialog(this.getStage());
    	if(img!=null) {
    		
            try {
            	this.imgExtension = "";
            	String fileName=img.getAbsolutePath();
            	int i = fileName.lastIndexOf('.');
            	int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

            	if (i > p) {
            		this.imgExtension  = fileName.substring(i+1);
            	}
            	System.out.println(this.imgExtension );
            	byte[] imageInByte = Files.readAllBytes(img.toPath());
        		WritableImage image = new WritableImage(510, 540);
                ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
                
                BufferedImage read = ImageIO.read(bis);
                image = SwingFXUtils.toFXImage(read, null);
                this.photo.setImage(image);
                this.fotoEdited=true;
            } catch (Exception ex) {
            	this.openDialog(this.parentStackPane, "Immagine caricata non valida");
            }
    	}
    }
    
    @FXML
    private void salvaPersonale(ActionEvent event){
    	if(dipendenteModifica==null) {
    		Dipendente dip=null;
    		Connection conn=null;
    		try {
    			String username=this.nomeDipendente.getText().replaceAll("\\s+","")+"."+this.cognomeDipendente.getText().replaceAll("\\s+","");
				dip = new Dipendente(this.codiceFiscaleDipendente.getText(), this.nomeDipendente.getText(), 
						this.cognomeDipendente.getText(), this.dataNascitaDipendente.getValue().toString(), this.sessoDipendente.getValue(),
						this.luogoNascitaDipendente.getText(), this.provinciaNascitaDipendente.getText(), 
						this.comuneResidenzaDipendente.getText(), this.provinciaResidenzaDipendente.getText(),
						Integer.parseInt(this.capDipendente.getText()), this.emailDipendente.getText(), this.recapitoTelefonicoDipendente.getText(), 
						this.contoCorrenteDipendente.getText(), this.indirizzoResidenzaDipendente.getText(), 
						username, null, 
						this.ruoloDipendente.getValue(), false, LocalDate.now().toString(), null, null);
				
				JFXButton btn= new JFXButton("OK");
				btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
					@Override
					public void handle(MouseEvent arg0) {
						GestionePersonaleControl.this.gestionePersonale();
					}
					
				});
				
				if(this.photo.getImage()!=null) {
					dip.setImage(this.photo.getImage());
				}
	    		

				conn = new Connection(this.getUtente());
				if(conn.addDipendente(dip, this.imgExtension).getInt("code")==1){
					this.openDialog(this.parentStackPane, "Dipendente inserito correttamente.\nUsername e Password inviata correttamente all'E-mail del dipendente.", btn);
				}

	    		
			} catch (DipendenteException e) {
				this.openDialog(this.parentStackPane, e.getMessage());
				return;
			}catch(NumberFormatException e) {
				this.openDialog(this.parentStackPane, "Inserisci un cap numerico");
				return;
			}catch(NullPointerException e) {
				this.openDialog(this.parentStackPane, "Compila i campi");
			}catch (IOException e) {
				this.openDialog(this.parentStackPane, "Errore: verificare la connessione o riprovare più tardi");
			}catch(JSONException e) {
				this.openDialog(this.parentStackPane, "Errore: verificare la connessione o riprovare più tardi");
			} catch (ConnectionException e) {
				this.openDialog(this.parentStackPane, e.getMessage());
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}    		
    	}else {
    		if(disabilitati) {
    			this.toggleEditBox();
    		}else {
    			Dipendente dip=null;
    			Connection conn=null;
        		try {
        			String username=this.nomeDipendente.getText().replaceAll("\\s+","")+"."+this.cognomeDipendente.getText().replaceAll("\\s+","");
    				dip = new Dipendente(this.codiceFiscaleDipendente.getText(), this.nomeDipendente.getText(), 
    						this.cognomeDipendente.getText(), this.dataNascitaDipendente.getValue().toString(), this.sessoDipendente.getValue(),
    						this.luogoNascitaDipendente.getText(), this.provinciaNascitaDipendente.getText(), 
    						this.comuneResidenzaDipendente.getText(), this.provinciaResidenzaDipendente.getText(),
    						Integer.parseInt(this.capDipendente.getText()), this.emailDipendente.getText(), this.recapitoTelefonicoDipendente.getText(), 
    						this.contoCorrenteDipendente.getText(), this.indirizzoResidenzaDipendente.getText(), 
    						username, null, 
    						this.ruoloDipendente.getValue(), false, this.dipendenteModifica.getDataAssunzione(), null, null);
    				
    				JFXButton btn= new JFXButton("OK");
    				btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
    					@Override
    					public void handle(MouseEvent arg0) {
    						GestionePersonaleControl.this.gestionePersonale();
    					}
    					
    				});
    				JSONObject json= new JSONObject();
    				if(this.photo.getImage()!=null && this.fotoEdited) {
    					dip.setImage(this.photo.getImage());
    				}
    				if(!this.dipendenteModifica.getCodiceFiscale().equals(dip.getCodiceFiscale())) {
    					json.put("CodiceFiscale", dip.getCodiceFiscale());
    				}
    				if(!this.dipendenteModifica.getNome().equals(dip.getNome())) {
    					json.put("Nome", dip.getNome());
    				}
    				if(!this.dipendenteModifica.getCognome().equals(dip.getCognome())) {
    					json.put("Cognome", dip.getCognome());
    				}
    				if(!this.dipendenteModifica.getDataNascita().equals(dip.getDataNascita())) {
    					json.put("DataDiNascita", dip.getDataNascita());
    				}
    				if(!this.dipendenteModifica.getSesso().equals(dip.getSesso())) {
    					json.put("Sesso", dip.getSesso());
    				}
    				if(!this.dipendenteModifica.getLuogoDiNascita().equals(dip.getLuogoDiNascita())) {
    					json.put("LuogoDiNascita", dip.getLuogoDiNascita());
    				}
    				if(!this.dipendenteModifica.getProvinciaDiNascita().equals(dip.getProvinciaDiNascita())) {
    					json.put("ProvinciaDiNascita", dip.getProvinciaDiNascita());
    				}
    				if(!this.dipendenteModifica.getComuneDiResidenza().equals(dip.getComuneDiResidenza())) {
    					json.put("ComuneDiResidenza", dip.getComuneDiResidenza());
    				}
    				if(this.dipendenteModifica.getCAP()!=dip.getCAP()) {
    					json.put("CAP", dip.getCAP());
    				}
    				if(!this.dipendenteModifica.getEmail().equals(dip.getEmail())) {
    					json.put("Email", dip.getEmail());
    				}
    				if(!this.dipendenteModifica.getRecapitoTelefonico().equals(dip.getRecapitoTelefonico())) {
    					json.put("RecapitoTelefonico", dip.getRecapitoTelefonico());
    				}
    				if(!this.dipendenteModifica.getIndirizzoResidenza().equals(dip.getIndirizzoResidenza())) {
    					json.put("IndirizzoDiResidenza", dip.getIndirizzoResidenza());
    				}
    				if(this.dipendenteModifica.getRuolo().getIdRuolo()!=dip.getRuolo().getIdRuolo()) {
    					json.put("RefIdRuolo", dip.getRuolo().getIdRuolo());
    				}
    				if(!this.dipendenteModifica.getContoCorrente().equals(dip.getContoCorrente())) {
    					json.put("ContoCorrente", dip.getContoCorrente());
    				}
    				if(dip.getImage()!=null) {
    					json.put("Foto", true);
    					json.put("ext", this.imgExtension);
    				}
    				if(json.length()>0) {
    					json.put("CF", this.dipendenteModifica.getCodiceFiscale());
    					

    					conn = new Connection(this.getUtente());
    					
    					if(conn.editDipendente(json, this.imgExtension, dip.getImage()).getInt("code")==1){
    						this.openDialog(this.parentStackPane, "Dipendente aggiornato correttamente.", btn);
    					}

    				}
    	    		
    			} catch (DipendenteException e) {
    				this.openDialog(this.parentStackPane, e.getMessage());
    				return;
    			}catch(NumberFormatException e) {
    				this.openDialog(this.parentStackPane, "Inserisci un cap numerico");
    				return;
    			}catch(NullPointerException e) {
    				e.printStackTrace();
    				this.openDialog(this.parentStackPane, "Compila i campi");
    			} catch (IOException e) {
					this.openDialog(this.parentStackPane, "Errore: verificare la connessione o riprovare più tardi");
				}catch(JSONException e) {
					this.openDialog(this.parentStackPane, "Errore: verificare la connessione o riprovare più tardi");
				} catch (ConnectionException e) {
					this.openDialog(this.parentStackPane, e.getMessage());
				}finally {
					if(conn!=null) {
						conn.close();
					}
				}
    			
    			
    			this.aggiornaDatiDipendente();
    		}
    	}
    }

    @FXML
    void tornaIndietro() {
    	if(this.dipendenteModifica==null){
    		this.gestionePersonale();
    	}else {
    		if(this.disabilitati) {
        		this.gestionePersonale();
        	}else {
        		this.toggleEditBox();
        	}
    	}
    	
    }
    void gestionePersonale() {
    	this.setPkg("");
    	this.disabilitati=false;
    	this.setCenter(1200, 800);
    	this.setXmlFileName("ElencoDipendenti");
    	this.start();
    }
    @Override
	public void init(){
    	setPkg("amministrativo/");
    	if(this.getXmlFileName().equals("ElencoDipendenti")) {
    		super.init();
    		this.addDipendente = new JFXButton("Aggiungi Personale");
        	AnchorPane.setTopAnchor(addDipendente, 15.0);
        	AnchorPane.setRightAnchor(addDipendente, 10.0);
        	this.addDipendente.setButtonType(JFXButton.ButtonType.RAISED);
        	this.addDipendente.setFont(new Font(15.0));
        	this.addDipendente.setStyle("-fx-background-color: #f0f0f0");
        	this.getContentPane().getChildren().add(this.addDipendente);
        	
        	this.addDipendente.setOnMouseClicked(new EventHandler<MouseEvent>() {
    			@Override
    			public void handle(MouseEvent event) {
    				GestionePersonaleControl.this.setCenter(800, 600);
    				GestionePersonaleControl.this.dipendenteModifica=null;
    				GestionePersonaleControl.this.setXmlFileName("SchedaDipendente");
    				GestionePersonaleControl.this.start();
    			}
        		
        	});       	
        	
    	}else if(this.getXmlFileName().equals("SchedaDipendente")) {
    		this.toogleCartellino();
    		this.loadComuni();
    		this.usernameDipendente.setText(this.getUtente().getUsername());
    		Connection conn=null;
			try {
				conn=new Connection(this.getUtente());
				this.ruoli=conn.getRuoli();
			} catch (JSONException | IOException e) {
				this.openDialog(this.parentStackPane, "Errore connessione");
				return;
			} catch (ConnectionException e) {
				this.openDialog(this.parentStackPane, e.getMessage());
				return;
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}
			//System.out.print(this.ruoli);
			this.sessoDipendente.setItems(sessoObs);
			this.ruoloObs.clear();
			for(Ruolo r: this.ruoli) {
				this.ruoloObs.add(r);
			}
			this.ruoloDipendente.setItems(ruoloObs);
			this.ruoloDipendente.setCellFactory(new Callback<ListView<Ruolo>, ListCell<Ruolo>>(){

				@Override
				public ListCell<Ruolo> call(ListView<Ruolo> param) {
					return new ListCell<Ruolo>(){
	                    @Override
	                    protected void updateItem(Ruolo item, boolean empty) {
	                        super.updateItem(item, empty);
	                        if (item == null || empty) {
	                            setGraphic(null);
	                        } else {
	                            setText(item.getNomeRuolo());
	                        }
	                    }
	                } ;
				}
				
			});
			this.ruoloDipendente.setConverter(new StringConverter<Ruolo>() {
	              @Override
	              public String toString(Ruolo user) {
	                if (user == null){
	                  return null;
	                } else {
	                  return user.getNomeRuolo();
	                }
	              }

	            @Override
	            public Ruolo fromString(String userId) {
	                return null;
	            }
	        });
    			
    		if(this.dipendenteModifica!=null) {
    			this.toggleEditBox();
        		this.nomeDipendente.setText(this.dipendenteModifica.getNome());
        		this.cognomeDipendente.setText(this.dipendenteModifica.getCognome());
        		this.sessoDipendente.setValue(this.dipendenteModifica.getSesso());

        		
        		this.dataNascitaDipendente.setValue(LocalDate.parse(this.dipendenteModifica.getDataNascita()));;
        		this.luogoNascitaDipendente.setText(this.dipendenteModifica.getLuogoDiNascita());
        		this.provinciaNascitaDipendente.setText(this.dipendenteModifica.getProvinciaDiNascita());
        		
        		this.codiceFiscaleDipendente.setText(this.dipendenteModifica.getCodiceFiscale());
        		
        		this.comuneResidenzaDipendente.setText(this.dipendenteModifica.getComuneDiResidenza());
        		this.provinciaResidenzaDipendente.setText(this.dipendenteModifica.getProvinciaDiResidenza());
        		this.capDipendente.setText(Integer.toString(this.dipendenteModifica.getCAP()));
        		this.indirizzoResidenzaDipendente.setText(this.dipendenteModifica.getIndirizzoResidenza());
        		this.recapitoTelefonicoDipendente.setText(this.dipendenteModifica.getRecapitoTelefonico());
        		
        		
        		this.emailDipendente.setText(this.dipendenteModifica.getEmail());
        		this.contoCorrenteDipendente.setText(this.dipendenteModifica.getContoCorrente());
        		for(Ruolo r : this.ruoloDipendente.getItems()) {
        			if(r.getIdRuolo()==this.dipendenteModifica.getRuolo().getIdRuolo()) {
        				this.ruoloDipendente.setValue(r);
        			}
        		}
        		this.ruoloDipendente.setValue(this.dipendenteModifica.getRuolo());

        		this.photo.setImage(this.dipendenteModifica.getImage());
        		if(this.dipendenteModifica.getDataLicenziamento()!=null) {
        			this.licenziaButton.setText("Riassumi Dipendente");
        		}
        		
        		
    		}else {
    			this.disabilitati=false;
    			this.modificaDatiButton.setText("Salva");
        		this.reimpostaButton.setVisible(false);
        		this.eliminaButton.setVisible(false);
        		this.licenziaButton.setVisible(false);
        		this.calcolaCFButton.setVisible(true);
        		this.cfText.setVisible(false);
        		this.annullaButton.setText("Annulla");
    		}
    	}
    	
    }
    private void toggleEditBox() {
    	if(disabilitati) {
			this.modificaDatiButton.setText("Salva");
			this.annullaButton.setText("Annulla");
			this.nomeDipendente.setDisable(false);
    		this.cognomeDipendente.setDisable(false);
    		this.sessoDipendente.setDisable(false);
    		this.dataNascitaDipendente.setDisable(false);
    		this.luogoNascitaDipendente.setDisable(false);
    		this.provinciaNascitaDipendente.setDisable(false);
    		this.codiceFiscaleDipendente.setDisable(false);
    		this.comuneResidenzaDipendente.setDisable(false);
    		this.provinciaResidenzaDipendente.setDisable(false);
    		this.capDipendente.setDisable(false);
    		this.indirizzoResidenzaDipendente.setDisable(false);
    		this.recapitoTelefonicoDipendente.setDisable(false);
    		this.emailDipendente.setDisable(false);
    		this.contoCorrenteDipendente.setDisable(false);
    		this.ruoloDipendente.setDisable(false);
    		this.reimpostaButton.setVisible(true);
    		this.eliminaButton.setVisible(true);
    		this.licenziaButton.setVisible(true);
    		this.calcolaCFButton.setVisible(true);
    		this.cfText.setVisible(false);
    		this.caricaImgButton.setVisible(true);
    		this.disabilitati=false;
    	}else {
    		this.modificaDatiButton.setText("Modifica");
    		this.annullaButton.setText("Indietro");
			this.nomeDipendente.setDisable(true);
    		this.cognomeDipendente.setDisable(true);
    		this.sessoDipendente.setDisable(true);
    		this.dataNascitaDipendente.setDisable(true);
    		this.luogoNascitaDipendente.setDisable(true);
    		this.provinciaNascitaDipendente.setDisable(true);
    		this.codiceFiscaleDipendente.setDisable(true);
    		this.comuneResidenzaDipendente.setDisable(true);
    		this.provinciaResidenzaDipendente.setDisable(true);
    		this.capDipendente.setDisable(true);
    		this.indirizzoResidenzaDipendente.setDisable(true);
    		this.recapitoTelefonicoDipendente.setDisable(true);
    		this.emailDipendente.setDisable(true);
    		this.contoCorrenteDipendente.setDisable(true);
    		this.ruoloDipendente.setDisable(true);
    		this.reimpostaButton.setVisible(false);
    		this.eliminaButton.setVisible(false);
    		this.licenziaButton.setVisible(false);
    		this.calcolaCFButton.setVisible(false);
    		this.cfText.setVisible(true);
    		this.caricaImgButton.setVisible(false);
    		this.disabilitati=true;
    	}
    }
    public void aggiornaDatiDipendente() {
    	
    }
    
    public void aggiungiDipendente() {
    	
    }
    
    
    @FXML
    void licenziaDipendente(ActionEvent event) {
    	if(this.licenziaButton.getText().equals("Riassumi Dipendente")) {
    		Connection conn= null;
    		try {
        		conn=new Connection(this.getUtente());
        		conn.riassumiDipendente(this.dipendenteModifica);
        		JFXButton btn= new JFXButton("OK");
        		btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
        			@Override
        			public void handle(MouseEvent event) {
        				GestionePersonaleControl.this.toggleEditBox();
        				GestionePersonaleControl.this.licenziaButton.setText("Riassumi Dipendente");
        			}
        			
        		});
        		
        		this.openDialog(this.parentStackPane, "Dipendente riassunto correttamente.", btn);
        	}catch(IOException e) {
        		this.openDialog(this.parentStackPane, "Errore, riprovare più tardi");
        	} catch (ConnectionException e) {
        		this.openDialog(this.parentStackPane, e.getMessage());
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}
    	}else {
    		Connection conn=null;
    		try {
        		conn=new Connection(this.getUtente());
        		conn.licenziaDipendente(this.dipendenteModifica);
        		JFXButton btn= new JFXButton("OK");
        		btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
        			@Override
        			public void handle(MouseEvent event) {
        				GestionePersonaleControl.this.toggleEditBox();
        				GestionePersonaleControl.this.licenziaButton.setText("Riassumi Dipendente");
        			}
        			
        		});
        		
        		this.openDialog(this.parentStackPane, "Dipendente licenziato correttamente.", btn);
        	}catch(IOException e) {
        		this.openDialog(this.parentStackPane, "Errore, riprovare più tardi");
        	} catch (ConnectionException e) {
        		this.openDialog(this.parentStackPane, e.getMessage());
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}
    	}
    }

    @FXML
    void reimpostaPassword(ActionEvent event) {
    	Connection conn=null;
    	try {
    		conn=new Connection(this.getUtente());
    		conn.reimpostaPassword(this.dipendenteModifica);
    		JFXButton btn= new JFXButton("OK");
    		btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
    			@Override
    			public void handle(MouseEvent event) {
    				GestionePersonaleControl.this.toggleEditBox();
    			}
    			
    		});
    		this.openDialog(this.parentStackPane, "Password reimpostata correttamente.\nPassword inviata correttamente all'E-mail del dipendente.", btn);
    	}catch(IOException e) {
    		this.openDialog(this.parentStackPane, "Errore, riprovare più tardi");
    	} catch (ConnectionException e) {
    		this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}

    }

    @FXML
    void eliminaDipendente() {
    	Connection conn=null;
    	try {
    		conn=new Connection(this.getUtente());
    		conn.eliminaDipendente(this.dipendenteModifica);
    		JFXButton btn= new JFXButton("OK");
    		btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
    			@Override
    			public void handle(MouseEvent event) {
    				GestionePersonaleControl.this.gestionePersonale();
    			}
    			
    		});
    		this.openDialog(this.parentStackPane, "Dipendente eliminato correttamente.", btn);
    	}catch(IOException e) {
    		this.openDialog(this.parentStackPane, "Errore, riprovare più tardi");
    	} catch (ConnectionException e) {
    		this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
    }
    
    @Override
    public void action(Dipendente dipendente) {

    		Connection conn=null;
			try {
				conn = new Connection(this.getUtente());
				//System.out.println(conn.getFotoProfilo(this.getUtente(), dipendente));
	    		dipendente.setImage(conn.getFotoProfilo(dipendente));
			} catch (IOException | JSONException | DipendenteException | URISyntaxException e) {
				this.openDialog(this.parentStackPane, "Errore caricamento foto");
			} catch (ConnectionException e) {
	    		this.openDialog(this.parentStackPane, e.getMessage());
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}
    		
    		

    	this.dipendenteModifica=dipendente;
		this.setCenter(800, 600);
		this.setXmlFileName("SchedaDipendente");
		this.start();
    }
    private void loadComuni() {
    	this.comuni=new HashMap<String, Comune>();
    	this.comuni2=new HashMap<String, Comune>();
    	try {
			File comuniFile = new File(this.getClass().getResource("/comuni.txt").toURI());
			FileReader fr = new FileReader(comuniFile);
			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(fr);
			String st;
			JSONObject json;
			int i=0;
			while((st=br.readLine())!= null) {
				
				json=new JSONObject(st);
				if(i>4046) {
					this.comuni.put(json.getString("nome").toLowerCase(), new Comune(json.getString("nome"), json.getString("provincia"),json.getString("codFisc")));
					
				}else{
					this.comuni2.put(json.getString("nome").toLowerCase(), new Comune(json.getString("nome"), json.getString("provincia"),json.getString("codFisc")));
				}
				
				i++;
			}
			/*for(String s:this.comuni.keySet()) {
				System.out.println(s.replaceAll("\\s+",""));
			}*/
			
			//System.out.println(this.comuni);
			//System.out.println(this.comuni2);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

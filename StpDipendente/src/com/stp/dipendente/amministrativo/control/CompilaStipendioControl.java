package com.stp.dipendente.amministrativo.control;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Math;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;

import com.stp.dipendente.control.ElencoDipendentiControl;
import com.stp.dipendente.model.Dipendente;
import com.stp.dipendente.util.Connection;
import com.stp.dipendente.util.ConnectionException;
import com.stp.dipendente.util.json.JSONException;
import com.stp.dipendente.util.json.JSONObject;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class CompilaStipendioControl extends ElencoDipendentiControl{
	private Dipendente dipendenteCompila;
	
	private JSONObject infoCedolino;
	
	
    public CompilaStipendioControl(String xmlFileName, Stage window, Dipendente utente) {
		super(xmlFileName, window, utente);
		// TODO Auto-generated constructor stub
	}
    
    @FXML
    private JFXButton salvaButton;

    @FXML
    private JFXButton annullaButton;

    @FXML
    private TextField idDipendente;

    @FXML
    private TextField nomeDipendente;

    @FXML
    private TextField cognomeDipendente;

    @FXML
    private TextField codiceFiscaleDipendente;

    @FXML
    private TextField luogoNascitaDipendente;

    @FXML
    private TextField dataNascitaDipendente;

    @FXML
    private TextField codiceDitta;

    @FXML
    private TextField posInail;

    @FXML
    private TextField meseRetribuzione;

    @FXML
    private TextField posInps;

    @FXML
    private TextField indirizzoDipendente;

    @FXML
    private TextField capDipendente;

    @FXML
    private TextField luogoResidenzaDipendente;

    @FXML
    private TextField provinciaResidenzaDipendente;

    @FXML
    private TextField sedeLavoro;

    @FXML
    private TextField dataAssunzioneDipendente;

    @FXML
    private TextField anzianitaServAnni;

    @FXML
    private TextField anzianitaServMesi;

    @FXML
    private TextField scadenzaContratto;

    @FXML
    private TextField fineRapporto;

    @FXML
    private TextField contrattoDiLavoro;

    @FXML
    private TextField ruoloDipendente;

    @FXML
    private TextField percentualePartTime;

    @FXML
    private TextField retribuzioneBase;

    @FXML
    private TextField indConting;

    @FXML
    private TextField terzoElemento;

    @FXML
    private TextField ferieOre;

    @FXML
    private TextField malattiaOre;

    @FXML
    private TextField modalitaPagamento;

    @FXML
    private TextField ibanDipendente;

    @FXML
    private TextField settRetr;

    @FXML
    private TextField ggRetribuiti;

    @FXML
    private TextField giorniLavorati;

    @FXML
    private TextField oreLavorate;

    @FXML
    private TextField retribuzioneOraria;

    @FXML
    private TextField retribuzioneGiornaliera;

    @FXML
    private TextField retribuzioneMensile;

    @FXML
    private TextField voce1;

    @FXML
    private TextField descrizione1;

    @FXML
    private TextField unitaMisura1;

    @FXML
    private TextField quantita1;

    @FXML
    private TextField base1;

    @FXML
    private TextField trattenute1;

    @FXML
    private TextField competenze1;

    @FXML
    private TextField voce2;

    @FXML
    private TextField descrizione2;

    @FXML
    private TextField unitaMisura2;

    @FXML
    private TextField quantita2;

    @FXML
    private TextField base2;

    @FXML
    private TextField trattenute2;

    @FXML
    private TextField competenze2;

    @FXML
    private TextField voce3;

    @FXML
    private TextField descrizione3;

    @FXML
    private TextField unitaMisura3;

    @FXML
    private TextField quantita3;

    @FXML
    private TextField base3;

    @FXML
    private TextField trattenute3;

    @FXML
    private TextField competenze3;

    @FXML
    private TextField voce4;

    @FXML
    private TextField descrizione4;

    @FXML
    private TextField unitaMisura4;

    @FXML
    private TextField quantita4;

    @FXML
    private TextField base4;

    @FXML
    private TextField trattenute4;

    @FXML
    private TextField competenze4;

    @FXML
    private TextField voce5;

    @FXML
    private TextField descrizione5;

    @FXML
    private TextField unitaMisura5;

    @FXML
    private TextField quantita5;

    @FXML
    private TextField base5;

    @FXML
    private TextField trattenute5;

    @FXML
    private TextField competenze5;

    @FXML
    private TextField voce6;

    @FXML
    private TextField descrizione6;

    @FXML
    private TextField unitaMisura6;

    @FXML
    private TextField quantita6;

    @FXML
    private TextField base6;

    @FXML
    private TextField trattenute6;

    @FXML
    private TextField competenze6;

    @FXML
    private TextField voce7;

    @FXML
    private TextField descrizione7;

    @FXML
    private TextField unitaMisura7;

    @FXML
    private TextField quantita7;

    @FXML
    private TextField base7;

    @FXML
    private TextField trattenute7;

    @FXML
    private TextField competenze7;

    @FXML
    private TextField voce8;

    @FXML
    private TextField descrizione8;

    @FXML
    private TextField unitaMisura8;

    @FXML
    private TextField quantita8;

    @FXML
    private TextField base8;

    @FXML
    private TextField trattenute8;

    @FXML
    private TextField competenze8;

    @FXML
    private TextField imponibileContributivo;

    @FXML
    private TextField imponibileContrArrot;

    @FXML
    private TextField descrizioneContributo1;

    @FXML
    private TextField aliq1;

    @FXML
    private TextField imponibile1;

    @FXML
    private TextField importo1;

    @FXML
    private TextField descrizioneContributo2;

    @FXML
    private TextField aliq2;

    @FXML
    private TextField imponibile2;

    @FXML
    private TextField importo2;

    @FXML
    private TextField descrizioneContributo3;

    @FXML
    private TextField aliq3;

    @FXML
    private TextField imponibile3;

    @FXML
    private TextField importo3;

    @FXML
    private TextField descrizioneContributo4;

    @FXML
    private TextField aliq4;

    @FXML
    private TextField imponibile4;

    @FXML
    private TextField importo4;

    @FXML
    private TextField descrizioneContributo5;

    @FXML
    private TextField aliq5;

    @FXML
    private TextField imponibile5;

    @FXML
    private TextField importo5;

    @FXML
    private TextField descrizioneContributo6;

    @FXML
    private TextField aliq6;

    @FXML
    private TextField imponibile6;

    @FXML
    private TextField importo6;

    @FXML
    private TextField inailVociTariffa;

    @FXML
    private TextField totaleContributi;

    @FXML
    private TextField imponLordoTFR;

    @FXML
    private TextField riduzioneTFR;

    @FXML
    private TextField imponNettoTFR;

    @FXML
    private TextField percentualeTFR;

    @FXML
    private TextField totaleCompetenze;

    @FXML
    private TextField totaleTrattenute;

    @FXML
    private TextField arrPrecedente;

    @FXML
    private TextField arrAttuale;

    @FXML
    private TextField nettoInBusta;

    @FXML
    private TextField nomeAzienda;

    @FXML
    private TextField indirizzoDitta;

    @FXML
    private TextField comuneDitta;

    @FXML
    private TextField capDitta;

    @FXML
    private TextField provinciaDitta;

    @FXML
    private TextArea comunicazioni;

   
    @FXML
    private TextField totaleSpettante;

    @FXML
    private Text usernameDipendente;

    @FXML
    private Circle timbro;
    
    @FXML
    private JFXCheckBox c1;
    
    @FXML
    private JFXCheckBox c2;
    
    @FXML
    private JFXCheckBox c3;
    
    @FXML
    private JFXCheckBox c4;
    
    @FXML
    private JFXCheckBox c5;

    @FXML
    private JFXCheckBox c6;
    
    @FXML
    private JFXCheckBox c7;
    
    @FXML
    private JFXCheckBox c8;
    
    @FXML
    private JFXCheckBox n1;
    
    @FXML
    private JFXCheckBox n2;
    
    @FXML
    private JFXCheckBox n3;
    
    @FXML
    private JFXCheckBox n4;
    
    @FXML
    private JFXCheckBox n5;
    
    @FXML
    private JFXCheckBox n6;
    
    @FXML
    private JFXCheckBox n7;
    
    @FXML
    private JFXCheckBox n8;
    
    @FXML
    private ArrayList<JFXCheckBox> c;
    
    
    @FXML
    private ArrayList<JFXCheckBox> n;
    
    @FXML
    private ArrayList<TextField> trattenute;
    
    @FXML
    private ArrayList<TextField> competenze;
    
    @FXML
    private ArrayList<TextField> aliq;
    
    @FXML
    private ArrayList<TextField> importo;
    
    @FXML
    private ArrayList<TextField> imponibile;
    
    @FXML
    private AnchorPane parentStackPane;

    @FXML
    void annulla() {
    	this.setPkg("");
    	this.setXmlFileName("ElencoDipendenti");
    	this.start();
    }

    @Override
    public void cercaPersonale() {
    	super.cercaPersonale();
    	DipendenteRecursive remove=null;
    	for(DipendenteRecursive d: this.getDipendentiObservable()) {
    		if(this.getUtente().getId()== d.getDipendente().getId()) {
    			remove= d;
    			break;
    		}
    	}
    	if(remove!=null) {
    		this.getDipendentiObservable().remove(remove);
    		this.getElencoDipendenti().remove(remove.getDipendente());
    	}
    	
    }
    
    @Override
    public void getDipendenti() {
    	super.getDipendenti();
    	DipendenteRecursive remove=null;
    	for(DipendenteRecursive d: this.getDipendentiObservable()) {
    		if(this.getUtente().getId()== d.getDipendente().getId()) {
    			remove= d;
    			break;
    		}
    	}
    	if(remove!=null) {
    		this.getDipendentiObservable().remove(remove);
    		this.getElencoDipendenti().remove(remove.getDipendente());
    	}
    }
    
    
    
    @FXML
    void salva() {
    		FileOutputStream fs;
		try {
		
			fs = new FileOutputStream(new File("stipendio.pdf"));

	    	 
	        PdfReader reader = new PdfReader("res/FacSimileBustaPaga.pdf");
	 
	        PdfStamper stamper = new PdfStamper(reader, fs);
	        AcroFields form = stamper.getAcroFields();
	        form.setField("codiceDitta", codiceDitta.getText());
	        form.setField("nomeAzienda", nomeAzienda.getText());
	        form.setField("indirizzoDitta", indirizzoDitta.getText());
	        form.setField("capDitta", capDitta.getText());
	        form.setField("comuneDitta", comuneDitta.getText());
	        form.setField("provinciaDitta", "("+provinciaDitta.getText()+")");
	        form.setField("posInail", posInail.getText());
	        form.setField("posInps", posInps.getText());
	        form.setField("meseRetribuzione", meseRetribuzione.getText());
	        form.setField("idDipendente", idDipendente.getText());
	        form.setField("cognomeDipendente", cognomeDipendente.getText().toUpperCase());
	        form.setField("nomeDipendente", nomeDipendente.getText().toUpperCase());
	        form.setField("codiceFiscaleDipendente", codiceFiscaleDipendente.getText());
	        form.setField("luogoNascitaDipendente", luogoNascitaDipendente.getText().toUpperCase());
	        form.setField("dataNascitaDipendente", dataNascitaDipendente.getText());
	        form.setField("indirizzoDipendente", this.indirizzoDipendente.getText().toUpperCase());
	        form.setField("capDipendente", this.capDipendente.getText());
	        form.setField("comuneResidenzaDipendente", this.luogoResidenzaDipendente.getText().toUpperCase());
	        form.setField("provinciaResidenzaDipendente", "("+this.provinciaResidenzaDipendente.getText().toUpperCase()+")");
	        form.setField("sedeLavoro", sedeLavoro.getText());
	        form.setField("dataAssunzioneDipendente", dataAssunzioneDipendente.getText());
	        form.setField("anzianitaServAnni", this.anzianitaServAnni.getText()); //ANNI-mesi da assunzione
	        form.setField("anzianitaServMesi", this.anzianitaServMesi.getText()); //anni-MESI da assunzione
	        form.setField("scadenzaContr", scadenzaContratto.getText());
	        form.setField("fineRapporto", fineRapporto.getText());
	        form.setField("contrattoLavoro", contrattoDiLavoro.getText());
	        form.setField("ruoloDipendente", ruoloDipendente.getText().toUpperCase());
	        form.setField("percentualePartTime", percentualePartTime.getText());
	        form.setField("retribuzioneBase", retribuzioneBase.getText());
	        form.setField("indConting", indConting.getText());
	        form.setField("terzoElemento", terzoElemento.getText());
	        form.setField("ferieOre", "");
	        form.setField("malattiaOre", "");
	        form.setField("modalitaPagamento", modalitaPagamento.getText());
	        form.setField("ibanDipendente", this.ibanDipendente.getText());
	        form.setField("settRetr", this.settRetr.getText());
	        form.setField("ggRetribuiti", ggRetribuiti.getText());
	        form.setField("ggLavorati", giorniLavorati.getText());
	        form.setField("oreLavorate", oreLavorate.getText());
	        form.setField("retribuzioneOraria", retribuzioneOraria.getText());
	        form.setField("retribuzioneGiornaliera", retribuzioneGiornaliera.getText());
	        form.setField("retribuzioneMensile", retribuzioneMensile.getText());
	        form.setField("voce1", voce1.getText());
	        form.setField("descrizione1", descrizione1.getText());
	        form.setField("unitaMisura1", unitaMisura1.getText());
	        form.setField("quantita1", quantita1.getText());
	        form.setField("base1", base1.getText());
	        form.setField("trattenute1", trattenute1.getText());
	        form.setField("competenze1", competenze1.getText());
	        
	        
	        
	        if(c1.isSelected()) {
	        		form.setField("c1", "*");
	        } else {
	        		form.setField("c1", "");
	        }
	        if(n1.isSelected()) {
	        		form.setField("n1", "*");
	        } else {
	        		form.setField("n1", "");
	        }
	        
	        form.setField("voce2", voce2.getText());
	        form.setField("descrizione2", descrizione2.getText());
	        form.setField("unitaMisura2", unitaMisura2.getText());
	        form.setField("quantita2", quantita2.getText());
	        form.setField("base2", base2.getText());
	        form.setField("trattenute2", trattenute2.getText());
	        form.setField("competenze2", competenze2.getText());
	        
	        if(c2.isSelected()) {
	    			form.setField("c2", "*");
	        } else {
	        		form.setField("c2", "");
	        }
	        if(n2.isSelected()) {
	        		form.setField("n2", "*");
	        } else {
	        		form.setField("n2", "");
	        }
        
	        form.setField("voce3", voce3.getText());
	        form.setField("descrizione3", descrizione3.getText());
	        form.setField("unitaMisura3", unitaMisura3.getText());
	        form.setField("quantita3", quantita3.getText());
	        form.setField("base3", base3.getText());
	        form.setField("trattenute3", trattenute3.getText());
	        form.setField("competenze3", competenze3.getText());
	        
	        if(c3.isSelected()) {
	    			form.setField("c3", "*");
	        } else {
	        		form.setField("c3", "");
	        }
	        if(n3.isSelected()) {
	        		form.setField("n3", "*");
	        } else {
	        		form.setField("n3", "");
	        }
	        
	        form.setField("voce4", voce4.getText());
	        form.setField("descrizione4", descrizione4.getText());
	        form.setField("unitaMisura4", unitaMisura4.getText());
	        form.setField("quantita4", quantita4.getText());
	        form.setField("base4", base4.getText());
	        form.setField("trattenute4", trattenute4.getText());
	        form.setField("competenze4", competenze4.getText());
	        
	        if(c4.isSelected()) {
        			form.setField("c4", "*");
	        } else {
	        		form.setField("c4", "");
	        }
	        if(n4.isSelected()) {
	        		form.setField("n4", "*");
	        } else {
	        		form.setField("n4", "");
	        }
	        
	        form.setField("voce5", voce5.getText());
	        form.setField("descrizione5", descrizione5.getText());
	        form.setField("unitaMisura5", unitaMisura5.getText());
	        form.setField("quantita5", quantita5.getText());
	        form.setField("base5", base5.getText());
	        form.setField("trattenute5", trattenute5.getText());
	        form.setField("competenze5", competenze5.getText());
	        
	        if(c5.isSelected()) {
        			form.setField("c5", "*");
	        } else {
	        		form.setField("c5", "");
	        }
	        if(n5.isSelected()) {
	        		form.setField("n5", "*");
	        } else {
	        		form.setField("n5", "");
	        }
	        
	        form.setField("voce6", voce6.getText());
	        form.setField("descrizione6", descrizione6.getText());
	        form.setField("unitaMisura6", unitaMisura6.getText());
	        form.setField("quantita6", quantita6.getText());
	        form.setField("base6", base6.getText());
	        form.setField("trattenute6", trattenute6.getText());
	        form.setField("competenze6", competenze6.getText());
	        
	        if(c6.isSelected()) {
	        		form.setField("c6", "*");
	        } else {
	        		form.setField("c6", "");
	        }
	        if(n6.isSelected()) {
	        		form.setField("n6", "*");
	        } else {
	        		form.setField("n6", "");
	        }
        
	        form.setField("voce7", voce7.getText());
	        form.setField("descrizione7", descrizione7.getText());
	        form.setField("unitaMisura7", unitaMisura7.getText());
	        form.setField("quantita7", quantita7.getText());
	        form.setField("base7", base7.getText());
	        form.setField("trattenute7", trattenute7.getText());
	        form.setField("competenze7", competenze7.getText());
	        
	        if(c7.isSelected()) {
	        		form.setField("c7", "*");
	        } else {
	        		form.setField("c7", "");
	        }
	        if(n7.isSelected()) {
	        		form.setField("n7", "*");
	        } else {
	        		form.setField("n7", "");
	        }
        
	        form.setField("voce8", voce8.getText());
	        form.setField("descrizione8", descrizione8.getText());
	        form.setField("unitaMisura8", unitaMisura8.getText());
	        form.setField("quantita8", quantita8.getText());
	        form.setField("base8", base8.getText());
	        form.setField("trattenute8", trattenute8.getText());
	        form.setField("competenze8", competenze8.getText());
	        
	        if(c8.isSelected()) {
	        		form.setField("c8", "*");
	        } else {
	        		form.setField("c8", "");
	        }
	        if(n8.isSelected()) {
	        		form.setField("n8", "*");
	        } else {
	        		form.setField("n8", "");
	        }
        
	        form.setField("imponibileContributivo", imponibileContributivo.getText());
	        form.setField("imponibileContrArrot", imponibileContrArrot.getText());
	        form.setField("totaleSpettante", totaleSpettante.getText());
	        form.setField("descrizioneContributo1", descrizioneContributo1.getText());
	        form.setField("aliq1", aliq1.getText());
	        form.setField("imponibile1", imponibile1.getText());
	        form.setField("importo1", importo1.getText());
	        form.setField("descrizioneContributo2", descrizioneContributo2.getText());
	        form.setField("aliq2", aliq2.getText());
	        form.setField("imponibile2", imponibile2.getText());
	        form.setField("importo2", importo2.getText());
	        form.setField("descrizioneContributo3", descrizioneContributo3.getText());
	        form.setField("aliq3", aliq3.getText());
	        form.setField("imponibile3", imponibile3.getText());
	        form.setField("importo3", importo3.getText());
	        form.setField("descrizioneContributo4", descrizioneContributo4.getText());
	        form.setField("aliq4", aliq4.getText());
	        form.setField("imponibile4", imponibile4.getText());
	        form.setField("importo4", importo4.getText());
	        form.setField("descrizioneContributo5", descrizioneContributo5.getText());
	        form.setField("aliq5", aliq5.getText());
	        form.setField("imponibile5", imponibile5.getText());
	        form.setField("importo5", importo5.getText());
	        form.setField("descrizioneContributo6", descrizioneContributo6.getText());
	        form.setField("aliq6", aliq6.getText());
	        form.setField("imponibile6", imponibile6.getText());
	        form.setField("importo6", importo6.getText());
	        form.setField("totaleSpettante", totaleSpettante.getText());
	        form.setField("inailVociTariffa", inailVociTariffa.getText());
	        form.setField("totaleContributi", totaleContributi.getText());
	        form.setField("comunicazioni", comunicazioni.getText());
	        form.setField("imponLordoTFR", imponLordoTFR.getText());
	        form.setField("riduzioneTFR", riduzioneTFR.getText());
	        form.setField("imponNettoTFR", imponNettoTFR.getText());
	        form.setField("percentualeTFR", percentualeTFR.getText());
	        form.setField("totaleCompetenze", totaleCompetenze.getText());
	        form.setField("totaleTrattenute", totaleTrattenute.getText());
	        form.setField("arrPrecedente", arrPrecedente.getText());
	        form.setField("arrAttuale", arrAttuale.getText());
	        form.setField("nettoInBusta", nettoInBusta.getText());

	        stamper.setFormFlattening(true);
	        stamper.close();
	        fs.close();
	        Connection conn=null;
	        try {
	        	conn=new Connection(this.getUtente());
		        if(conn.sendCedolino("stipendio.pdf", this.dipendenteCompila, 
		        		Integer.parseInt(this.oreLavorate.getText()),
		        		Integer.parseInt(this.malattiaOre.getText()),
		        		Integer.parseInt(this.ferieOre.getText()),
		        		Double.parseDouble(this.nettoInBusta.getText()))) {
		        	JFXButton btn= new JFXButton("OK");
					btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent arg0) {
							CompilaStipendioControl.this.annulla();
						}
						
					});
		        	this.openDialog(this.parentStackPane, "Cedolino compilato correttamente", btn,  -130.0 ,-130.0, 0.0, 0.0);
		        }else {
		        	this.openDialog(this.parentStackPane, "Errore compilazione cedolino", -130.0 ,-130.0, 0.0, 0.0);
		        }
	        }catch(NumberFormatException e) {
	        	e.printStackTrace();
	        	this.openDialog(this.parentStackPane, "Errore compilazione campi", -130.0 ,-130.0, 0.0, 0.0);
	        }catch (ConnectionException e) {
	    		this.openDialog(this.parentStackPane, e.getMessage());
			}finally {
				if(conn!=null) {
					conn.close();
				}
			}
	        
	        
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

	@Override
	public void init() {
	    	super.init();
	    	this.setPkg("amministrativo/");
	    	if(this.getXmlFileName().equals("CompilaCedolino")) {
	    		
	    		DecimalFormat numberFormat = new DecimalFormat("0.00");
	    		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
	    		symbols.setDecimalSeparator('.');
	    		symbols.setGroupingSeparator(' ');
	    		numberFormat.setDecimalFormatSymbols(symbols);
	    		this.infoCedolino=getInfoCedolino();
	    		// dati aziendali
		    	this.codiceDitta.setText("9500");
		    	this.posInps.setText("0006698768/55");
		    	this.posInail.setText("2582699");
		    	LocalDate mese=LocalDate.now().minusMonths(1);
		    	String meseNome = null;
		    	switch(mese.getMonthValue()) {
		    		case 1:
		    			meseNome="GENNAIO";
		    		break;
		    		case 2:
		    			meseNome="FEBBRAIO";
		    		break;
		    		case 3:
		    			meseNome="MARZO";
		    		break;
		    		case 4:
		    			meseNome="APRILE";
		    		break;
		    		case 5:
		    			meseNome="MAGGIO";
		    		break;
		    		case 6:
		    			meseNome="GIUGNO";
		    		break;
		    		case 7:
		    			meseNome="LUGLIO";
		    		break;
		    		case 8:
		    			meseNome="AGOSTO";
		    		break;
		    		case 9:
		    			meseNome="SETTEMBRE";
		    		break;
		    		case 10:
		    			meseNome="OTTOBRE";
		    		break;
		    		case 11:
		    			meseNome="NOVEMBRE";
		    		break;
		    		case 12:
		    			meseNome="DICEMBRE";
		    		break;
		    	}
		    	this.meseRetribuzione.setText(meseNome+" "+mese.getYear());
		    	this.nomeAzienda.setText("SERVICE TRASPORTI PALERMO");
		    	this.indirizzoDitta.setText("CORSO DEI MILLE, 1068");
		    	this.capDitta.setText("90123");
		    	this.comuneDitta.setText("PALERMO");
		    	this.provinciaDitta.setText("PA");
		    	// dati aziendali
		    	
		    	
		    	// dati dipendente
		    	//this.idDipendente.setText(Integer.toString(this.dipendenteCompila.getIdDipendente()));
		    	this.cognomeDipendente.setText(this.dipendenteCompila.getCognome());
		    	this.nomeDipendente.setText(this.dipendenteCompila.getNome());
		    	this.codiceFiscaleDipendente.setText(this.dipendenteCompila.getCodiceFiscale());
		    	this.luogoNascitaDipendente.setText(this.dipendenteCompila.getLuogoDiNascita());
		    	this.dataNascitaDipendente.setText(this.dipendenteCompila.getDataNascita());
		    	this.indirizzoDipendente.setText(this.dipendenteCompila.getIndirizzoResidenza());
		    	this.capDipendente.setText(Integer.toString(this.dipendenteCompila.getCAP()));
		    	this.idDipendente.setText(Integer.toString(this.dipendenteCompila.getId()));
		    	this.luogoResidenzaDipendente.setText(this.dipendenteCompila.getComuneDiResidenza());
		    	this.provinciaResidenzaDipendente.setText(this.dipendenteCompila.getProvinciaDiResidenza());
		    	this.sedeLavoro.setText("SEDE LEGALE");

		    	
		    	try {
		    		this.dataAssunzioneDipendente.setText(this.infoCedolino.getString("dataAssunzione"));
		    		LocalDate fromDateTime = LocalDate.parse(this.infoCedolino.getString("dataAssunzione"));
			    	LocalDate toDateTime = LocalDate.now().withDayOfMonth(1);

			    	LocalDate tempDateTime = LocalDate.from( fromDateTime );

			    	long years = tempDateTime.until( toDateTime, ChronoUnit.YEARS);
			    	tempDateTime = tempDateTime.plusYears( years );

			    	long months = tempDateTime.until( toDateTime, ChronoUnit.MONTHS);
			    	tempDateTime = tempDateTime.plusMonths( months );

			    	this.anzianitaServAnni.setText(Long.toString(years));
			    	this.anzianitaServMesi.setText(Long.toString(months));
		    	}catch(JSONException | NullPointerException e){
		    		
		    	}
		    	
		    	
		    	
		    	
		    	
		    	this.malattiaOre.setText("0");
		    	this.ferieOre.setText("0");
		    //this.fineRapporto.setText();	DA VEDERE dataLicenziamento
		    	this.contrattoDiLavoro.setText("TRASPORTI");
		    	this.ruoloDipendente.setText(this.dipendenteCompila.getRuolo().getNomeRuolo());
		    	this.percentualePartTime.setText(numberFormat.format((this.dipendenteCompila.getRuolo().getOreSettimanli()*100)/40));
		    	
		    	double retrBase = this.dipendenteCompila.getRuolo().getRetribuzioneOraria()*this.dipendenteCompila.getRuolo().getOreSettimanli()*4;
		    	this.retribuzioneBase.setText(numberFormat.format(retrBase));
		    	
		    	double indCont = this.dipendenteCompila.getCAP()/150;
		    	this.indConting.setText(numberFormat.format(indCont));
		    	
		    	double terzoElem = this.dipendenteCompila.getCAP()/10000;
		    	this.terzoElemento.setText(numberFormat.format(terzoElem));
		    	// dati dipendente
		    	
		    	
		    	
		    	// dati pagamento
		    //this.ferieOre.setText(value);
		    	//this.malattiaOre.setText(value);
		    	this.modalitaPagamento.setText("CONTO CORRENTE");
		    	this.ibanDipendente.setText(this.dipendenteCompila.getContoCorrente());
		    	this.settRetr.setText("4");
		    	this.ggRetribuiti.setText("26");
		    	try {
		    		this.giorniLavorati.setText(Integer.toString(this.infoCedolino.getInt("giorniLavorativi")));
		    		this.oreLavorate.setText(Integer.toString(this.infoCedolino.getInt("oreLavorate")));
		    	}catch(JSONException | NullPointerException e) {
		    		
		    	}
		    	
		    	
		    	this.retribuzioneOraria.setText(numberFormat.format(this.dipendenteCompila.getRuolo().getRetribuzioneOraria()));
		    	this.retribuzioneGiornaliera.setText(numberFormat.format((retrBase+indCont+terzoElem)/26));
		    	this.retribuzioneMensile.setText(numberFormat.format(retrBase+indCont+terzoElem));
		    	
		    	
		    	
		    	//init voce base
		    	ChangeListener<String> change=new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						CompilaStipendioControl.this.aggiornaCompetenzeTrattenute();
						
					}
		    		
		    	};
		    	ChangeListener<Boolean> changeSel=new ChangeListener<Boolean>() {
		    	    @Override
		    	    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
		    	    	CompilaStipendioControl.this.aggiornaCompetenzeTrattenute();
		    	    }
		    	};
		    	
		    	ChangeListener<String> changetrf=new ChangeListener<String>() {
					@Override
					public void changed(ObservableValue<? extends String> observable, String oldValue,
							String newValue) {
						CompilaStipendioControl.this.aggiornaInpoLordo();
						
					}
		    		
		    	};
		    	
		    	
		    	for(int i=0;i<8;i++) {
		    		this.competenze.get(i).textProperty().addListener(change);
		    		this.trattenute.get(i).textProperty().addListener(change);
		    		this.c.get(i).selectedProperty().addListener(changeSel);
		    		this.n.get(i).selectedProperty().addListener(changeSel);
		    	}
		    	this.voce1.setText("1");
		    	this.descrizione1.setText("Retribuzione ordinaria");
		    	this.unitaMisura1.setText("ORE");
		    	try {
		    		this.quantita1.setText(Integer.toString(this.infoCedolino.getInt("oreLavorate")));
		    		this.competenze1.setText(Double.toString(this.dipendenteCompila.getRuolo().getRetribuzioneOraria()*this.infoCedolino.getInt("oreLavorate")));
		    	}catch(JSONException | NullPointerException e){
		    		
		    	}
		    	
		    	this.base1.setText(Double.toString(this.dipendenteCompila.getRuolo().getRetribuzioneOraria()));
		    	this.trattenute1.setText("");
		    	
		    	this.c.get(0).setSelected(true);
		    	this.n.get(0).setSelected(true);
		    	
		    	
		    	for(int i=0;i<6;i++) {
		    		this.aliq.get(i).textProperty().addListener(change);
		    	}
		    	
		    	/*
		    	DoubleProperty a=new SimpleDoubleProperty();
		    	DoubleProperty b=new SimpleDoubleProperty();
		    	this.quantita1.textProperty().bindBidirectional(a, new NumberStringConverter());
		    	this.base1.textProperty().bindBidirectional(b, new NumberStringConverter());
		    	NumberBinding c=a.multiply(b);
		    	
		    	
		    	
		    	this.competenze1.textProperty().bind(c.asString());
		    	this.trattenute1.textProperty().bindBidirectional(a, new NumberStringConverter());*/
		    	

		    	
		    	//this.competenze1.textProperty().addListener(change);
		    	
		    	this.descrizioneContributo1.setText("INPS");
		    	this.aliq1.setText("9.190");
		    	
		    	this.descrizioneContributo2.setText("INPS RIDUZIONE PREVIDENZA");
		    	this.aliq2.setText("0.350");
		    	
		    	this.aggiornaCompetenzeTrattenute();
		    	this.inailVociTariffa.setText("9002");
		    	
		    	
		    	this.imponLordoTFR.textProperty().addListener(changetrf);
		    	this.percentualeTFR.textProperty().addListener(changetrf);
		    	
		    	
		    	/*DoubleProperty[] a= new SimpleDoubleProperty[6];
		    	DoubleProperty[] b= new SimpleDoubleProperty[6];
		    	DoubleBinding[] c = new DoubleBinding[6];

		        
		        
		        
		        
		        for(int i=0;i<6;i++) {
		        	a[i]=new SimpleDoubleProperty();
		        	b[i]=new SimpleDoubleProperty();
		        	c[i]=a[i].multiply(b[i]).divide(100);
		        	this.aliq.get(i).textProperty().bindBidirectional(a[i], new NumberStringConverter());
		        	this.aliq.get(i).textProperty().bindBidirectional(a[i], new NumberStringConverter());
		        	this.importo.get(i).textProperty().bind(c[i].asString());
		        }
		    	*/
		    	
		    	/*this.imponibile1.setText(Double.toString(impContrArr));
		    	this.importo1.setText("");
		    	
		    	this.imponibile2.setText(Double.toString(impContrArr));
		    	this.importo2.setText("");
		    	this.inailVociTariffa.setText("9002");
		    	this.totaleContributi.setText("");
		    	this.totaleCompetenze.setText(Double.toString(totSpett));
		    	this.totaleTrattenute.setText("");
		    	this.arrAttuale.setText("");
		    	this.nettoInBusta.setText("");*/
		    	
	    	}
	    	
	    	
	    	
    }	
	
	@Override
	public void action(Dipendente dipendente) {
		this.dipendenteCompila=dipendente;
		this.setXmlFileName("CompilaCedolino");
		this.start();
	}
	
	private JSONObject getInfoCedolino() {
		Connection conn=null;
		try {
			conn=new Connection(this.getUtente());
			JSONObject res= conn.getInfoCedolino(this.dipendenteCompila);
			JFXButton btn= new JFXButton("OK");
			btn.setOnMouseClicked(new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent arg0) {
					CompilaStipendioControl.this.annulla();
				}
				
			});
			System.out.println(res);
			if(res.getInt("code")==-2) {
	        	this.openDialog(this.parentStackPane, "Dipendente assunto da meno di un mese", btn, -130.0 ,-130.0, 0.0, 0.0);
	        	return null;
			}
			if(res.getInt("code")==-3) {
	        	this.openDialog(this.parentStackPane, "Stipendio già compilato per questo dipendente", btn, -130.0 ,-130.0, 0.0, 0.0);
	        	return null;
			}
			return res;
		}catch(IOException e) {
			//this.openDialog(this., message);
		}catch (ConnectionException e) {
    		this.openDialog(this.parentStackPane, e.getMessage());
		}finally {
			if(conn!=null) {
				conn.close();
			}
		}
		return null;
	}
	
	public void aggiornaCompetenzeTrattenute() {
		System.out.println("df");
		double sumContributivo=0.0;
		double sumSpettante=0.0;
		DecimalFormat numberFormat = new DecimalFormat("0.00");
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(' ');
		numberFormat.setDecimalFormatSymbols(symbols);
		for(int i=0;i<8;i++) {
			if(c.get(i).isSelected()) {
				if(isNumeric(this.competenze.get(i).getText())) {
					sumContributivo=sumContributivo+Double.parseDouble(this.competenze.get(i).getText());
				}
				if(isNumeric(this.trattenute.get(i).getText())) {
					sumContributivo=sumContributivo-Double.parseDouble(this.trattenute.get(i).getText());
				}
			}
			if(n.get(i).isSelected()) {
				if(isNumeric(this.competenze.get(i).getText())) {
					sumSpettante=sumSpettante+Double.parseDouble(this.competenze.get(i).getText());
				}
				if(isNumeric(this.trattenute.get(i).getText())) {
					sumSpettante=sumSpettante-Double.parseDouble(this.trattenute.get(i).getText());
				}
			}
		}
		this.imponibileContributivo.setText(numberFormat.format(sumContributivo));
		this.imponibileContrArrot.setText(numberFormat.format(Math.rint(sumContributivo)));
		this.totaleSpettante.setText(numberFormat.format(sumSpettante));
		this.totaleCompetenze.setText(numberFormat.format(sumSpettante));
		Double sumImporto=0.0;
		for(int i=0;i<6;i++) {
			this.imponibile.get(i).setText(numberFormat.format(Math.rint(sumContributivo)));
			if(isNumeric(this.aliq.get(i).getText())) {
				this.importo.get(i).setText(numberFormat.format(Math.rint(sumContributivo)*Double.parseDouble(this.aliq.get(i).getText())/100.0));
				sumImporto=sumImporto+Math.rint(sumContributivo)*Double.parseDouble(this.aliq.get(i).getText())/100.0;
			}
			
		}
		this.totaleContributi.setText(numberFormat.format(sumImporto));
		this.totaleTrattenute.setText(numberFormat.format(sumImporto));
		this.nettoInBusta.setText(numberFormat.format(Math.rint(sumSpettante-sumImporto)));
		this.arrAttuale.setText(numberFormat.format(Math.abs(sumSpettante-sumImporto - Math.rint(sumSpettante-sumImporto))));
	}
	
	public void aggiornaInpoLordo() {
		DecimalFormat numberFormat = new DecimalFormat("0.00");
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		symbols.setDecimalSeparator('.');
		symbols.setGroupingSeparator(' ');
		numberFormat.setDecimalFormatSymbols(symbols);
		if(isNumeric(this.imponLordoTFR.getText()) && isNumeric(this.percentualeTFR.getText())) {
			this.imponNettoTFR.setText(numberFormat.format(Double.parseDouble(this.imponLordoTFR.getText())*Double.parseDouble(this.percentualeTFR.getText())/100.0));
			this.riduzioneTFR.setText(numberFormat.format(Double.parseDouble(this.imponLordoTFR.getText())-Double.parseDouble(this.imponNettoTFR.getText())));
		}
	}
	
	
	
	public static boolean isNumeric(String str){  
		try{  
			@SuppressWarnings("unused")
			double d = Double.parseDouble(str);  
		}catch(NumberFormatException e) { 
			return false;  
		}
		return true;
	}
	
}

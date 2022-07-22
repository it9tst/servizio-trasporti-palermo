package com.stp.dipendente.model;

public class Autobus {
	private String targa;
	private int postiSeduti;
	private int postiInPiedi;
	private int postiPerDisabili;
	private PresenzaAutobus statoPresenza;
	private StatoAutobus statoGuasto;
	private String descrizioneGuasto;
	
	
	public Autobus(String targa, int postiSeduti, int postiInPiedi, int postiPerDisabili, PresenzaAutobus statoPresenza,
			StatoAutobus statoGuasto, String descrizioneGuasto) {
		super();
		this.targa = targa;
		this.postiSeduti = postiSeduti;
		this.postiInPiedi = postiInPiedi;
		this.postiPerDisabili = postiPerDisabili;
		this.statoPresenza = statoPresenza;
		this.statoGuasto = statoGuasto;
		this.descrizioneGuasto = descrizioneGuasto;
	}

	public int getPostiSeduti() {
		return postiSeduti;
	}

	public void setPostiSeduti(int postiSeduti) {
		this.postiSeduti = postiSeduti;
	}

	public int getPostiInPiedi() {
		return postiInPiedi;
	}

	public void setPostiInPiedi(int postiInPiedi) {
		this.postiInPiedi = postiInPiedi;
	}


	public int getPostiPerDisabili() {
		return postiPerDisabili;
	}

	public void setPostiPerDisabili(int postiPerDisabili) {
		this.postiPerDisabili = postiPerDisabili;
	}

	public PresenzaAutobus getStatoPresenza() {
		return statoPresenza;
	}

	public void setStatoPresenza(PresenzaAutobus statoPresenza) {
		this.statoPresenza = statoPresenza;
	}

	public StatoAutobus getStatoGuasto() {
		return statoGuasto;
	}

	public void setStatoGuasto(StatoAutobus statoGuasto) {
		this.statoGuasto = statoGuasto;
	}
	public String getDescrizioneGuasto() {
		return descrizioneGuasto;
	}


	public void setDescrizioneGuasto(String descrizioneGuasto) {
		this.descrizioneGuasto = descrizioneGuasto;
	}


	public String getTarga() {
		return targa;
	}


	public enum StatoAutobus {
		
		UTILIZZABILE(1, "Utilizzabile"),
        VISIONARE(2, "Da Visionare"),
        GUASTO(3, "Guasto");
         
        private int codice;
        private String nome;
 
        public int getCodice() {
            return codice;
        }
         
        public String getNome() {
            return nome;
        }
         
        private StatoAutobus(int codice, String nome) {
 
            this.codice = codice;
            this.nome = nome;
        }
        
        public static StatoAutobus stato(int i) {
        	if(i==1) return UTILIZZABILE;
        	if(i==2) return VISIONARE;
        	return GUASTO;
        }
	}
	public enum PresenzaAutobus {
        
        PRESENTE(true, "Presente"),
        ASSENTE(false, "Assente");
         
        private boolean presenza;
        private String nome;
 
        public boolean getPresenza() {
            return presenza;
        }
         
        public String getNome() {
            return nome;
        }
         
        private PresenzaAutobus(boolean presenza, String nome) {
 
            this.presenza = presenza;
            this.nome = nome;
        }
        public static PresenzaAutobus presenza(int i) {
        	if(i==0) return ASSENTE;
        	return PRESENTE;
        }
	}
	
}

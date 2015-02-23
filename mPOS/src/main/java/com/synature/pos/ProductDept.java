package com.synature.pos;

public class ProductDept {
	private int PDID;
	private int PGID;
	private String PDCOD;	//product dept code
	private String PDNAM;	//product dept name
	private int ACTIVATE;
	private int DEL;
	private int PDORD; 		//ordering
	private String UDATE;
	public int getPDID() {
		return PDID;
	}
	public void setPDID(int pDID) {
		PDID = pDID;
	}
	public int getPGID() {
		return PGID;
	}
	public void setPGID(int pGID) {
		PGID = pGID;
	}
	public String getPDCOD() {
		return PDCOD;
	}
	public void setPDCOD(String pDCOD) {
		PDCOD = pDCOD;
	}
	public String getPDNAM() {
		return PDNAM;
	}
	public void setPDNAM(String pDNAM) {
		PDNAM = pDNAM;
	}
	public int getACTIVATE() {
		return ACTIVATE;
	}
	public void setACTIVATE(int aCTIVATE) {
		ACTIVATE = aCTIVATE;
	}
	public int getDEL() {
		return DEL;
	}
	public void setDEL(int dEL) {
		DEL = dEL;
	}
	public int getPDORD() {
		return PDORD;
	}
	public void setPDORD(int pDORD) {
		PDORD = pDORD;
	}
	public String getUDATE() {
		return UDATE;
	}
	public void setUDATE(String uDATE) {
		UDATE = uDATE;
	}
}

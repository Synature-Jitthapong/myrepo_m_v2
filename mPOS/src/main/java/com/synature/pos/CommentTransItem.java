package com.synature.pos;

public class CommentTransItem {
	private int CDID;
	private int CID;
	private String CCOD;
	private String CNAM;
	private int DEL;
	private int ACTIVATE;
	public int getCDID() {
		return CDID;
	}
	public void setCDID(int cDID) {
		CDID = cDID;
	}
	public int getCID() {
		return CID;
	}
	public void setCID(int cID) {
		CID = cID;
	}
	public String getCCOD() {
		return CCOD;
	}
	public void setCCOD(String cCOD) {
		CCOD = cCOD;
	}
	public String getCNAM() {
		return CNAM;
	}
	public void setCNAM(String cNAM) {
		CNAM = cNAM;
	}
	public int getDEL() {
		return DEL;
	}
	public void setDEL(int dEL) {
		DEL = dEL;
	}
	public int getACTIVATE() {
		return ACTIVATE;
	}
	public void setACTIVATE(int aCTIVATE) {
		ACTIVATE = aCTIVATE;
	}
}

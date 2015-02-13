package com.synature.pos;

public class PComponentGroup{
	private int PGRID;
	private int PID;
	private int SMODE;
	private int SGRPNO;
	private String SGRPNAM;
	private double RQAMT;
	private double RQMINAMT;	
	public int getPGRID() {
		return PGRID;
	}
	public void setPGRID(int pGRID) {
		PGRID = pGRID;
	}
	public int getPID() {
		return PID;
	}
	public void setPID(int pID) {
		PID = pID;
	}
	public int getSMODE() {
		return SMODE;
	}
	public void setSMODE(int sMODE) {
		SMODE = sMODE;
	}
	public int getSGRPNO() {
		return SGRPNO;
	}
	public void setSGRPNO(int sGRPNO) {
		SGRPNO = sGRPNO;
	}
	public String getSGRPNAM() {
		return SGRPNAM;
	}
	public void setSGRPNAM(String sGRPNAM) {
		SGRPNAM = sGRPNAM;
	}
	public double getRQAMT() {
		return RQAMT;
	}
	public void setRQAMT(double rQAMT) {
		RQAMT = rQAMT;
	}
	public double getRQMINAMT() {
		return RQMINAMT;
	}
	public void setRQMINAMT(double rQMINAMT) {
		RQMINAMT = rQMINAMT;
	}
}

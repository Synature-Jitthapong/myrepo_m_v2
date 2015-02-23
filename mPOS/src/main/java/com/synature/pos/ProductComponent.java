package com.synature.pos;

public class ProductComponent{
	private int PGRID;
	private int PID;
	private int SMODE;
	private int CHDPID;
	private double CHDAMT;
	private double FPRICE;
	private double FINCPRICE;
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
	public int getCHDPID() {
		return CHDPID;
	}
	public void setCHDPID(int cHDPID) {
		CHDPID = cHDPID;
	}
	public double getCHDAMT() {
		return CHDAMT;
	}
	public void setCHDAMT(double cHDAMT) {
		CHDAMT = cHDAMT;
	}
	public double getFPRICE() {
		return FPRICE;
	}
	public void setFPRICE(double fPRICE) {
		FPRICE = fPRICE;
	}
	public double getFINCPRICE() {
		return FINCPRICE;
	}
	public void setFINCPRICE(double fINCPRICE) {
		FINCPRICE = fINCPRICE;
	}
}

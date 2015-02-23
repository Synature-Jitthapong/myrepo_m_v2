package com.synature.pos;

public class ProductPrice {
	private int PPID;
	private int PID;
	private double PRICE;
	private int SMODE;
	private String FDATE;
	private String TDATE;
	public int getPPID() {
		return PPID;
	}
	public void setPPID(int pPID) {
		PPID = pPID;
	}
	public int getPID() {
		return PID;
	}
	public void setPID(int pID) {
		PID = pID;
	}
	public double getPRICE() {
		return PRICE;
	}
	public void setPRICE(double pRICE) {
		PRICE = pRICE;
	}
	public int getSMODE() {
		return SMODE;
	}
	public void setSMODE(int sMODE) {
		SMODE = sMODE;
	}
	public String getFDATE() {
		return FDATE;
	}
	public void setFDATE(String fDATE) {
		FDATE = fDATE;
	}
	public String getTDATE() {
		return TDATE;
	}
	public void setTDATE(String tDATE) {
		TDATE = tDATE;
	}
}

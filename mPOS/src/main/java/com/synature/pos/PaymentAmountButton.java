package com.synature.pos;

public class PaymentAmountButton{
	private int PaymentAmountID;
	private double PaymentAmount;
	private int Ordering;
	
	public int getOrdering() {
		return Ordering;
	}
	public void setOrdering(int ordering) {
		Ordering = ordering;
	}
	public int getPaymentAmountID() {
		return PaymentAmountID;
	}
	public void setPaymentAmountID(int paymentAmountID) {
		PaymentAmountID = paymentAmountID;
	}
	public double getPaymentAmount() {
		return PaymentAmount;
	}
	public void setPaymentAmount(double paymentAmount) {
		PaymentAmount = paymentAmount;
	}
}

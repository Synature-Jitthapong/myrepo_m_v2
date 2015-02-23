package com.synature.mpos.database.model;

import java.util.ArrayList;
import java.util.List;

import android.widget.EditText;

public class OrderDetail extends Product{
		private int orderDetailId;
		private int transactionId;
		private int computerId;
		private int orderSetId;
		private int promotionPriceGroupId;
		private int promotionTypeId;
		private double orderQty;
		private double deductAmount;
		private double totalRetailPrice;
		private double totalSalePrice;
		private double priceDiscount;
		private double memberDiscount;
		private double vatExclude;
		private int priceOrPercent;
		private String promotionName;
		private String orderComment;
		private int vatType;
		private double vat;
		private int discountType;	// 1 price, 2 percent
		private boolean isChecked;
		public EditText mTxtFocus;
		private List<OrderSet.OrderSetDetail> ordSetDetailLst = new ArrayList<OrderSet.OrderSetDetail>();
		private List<OrderComment> orderCommentLst = new ArrayList<OrderComment>();
		public boolean isChecked() {
			return isChecked;
		}
		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}
		public int getDiscountType() {
			return discountType;
		}
		public void setDiscountType(int discountType) {
			this.discountType = discountType;
		}
		public double getVat() {
			return vat;
		}
		public void setVat(double vat) {
			this.vat = vat;
		}
		public double getTotalRetailPrice() {
			return totalRetailPrice;
		}
		public void setTotalRetailPrice(double totalRetailPrice) {
			this.totalRetailPrice = totalRetailPrice;
		}
		public double getTotalSalePrice() {
			return totalSalePrice;
		}
		public void setTotalSalePrice(double totalSalePrice) {
			this.totalSalePrice = totalSalePrice;
		}
		public double getPriceDiscount() {
			return priceDiscount;
		}
		public void setPriceDiscount(double priceDiscount) {
			this.priceDiscount = priceDiscount;
		}
		public double getMemberDiscount() {
			return memberDiscount;
		}
		public void setMemberDiscount(double memberDiscount) {
			this.memberDiscount = memberDiscount;
		}
		public int getVatType() {
			return vatType;
		}
		public void setVatType(int vatType) {
			this.vatType = vatType;
		}
		public int getOrderDetailId() {
			return orderDetailId;
		}
		public void setOrderDetailId(int orderDetailId) {
			this.orderDetailId = orderDetailId;
		}
		public int getTransactionId() {
			return transactionId;
		}
		public void setTransactionId(int transactionId) {
			this.transactionId = transactionId;
		}
		public int getComputerId() {
			return computerId;
		}
		public void setComputerId(int computerId) {
			this.computerId = computerId;
		}
		public int getOrderSetId() {
			return orderSetId;
		}
		public void setOrderSetId(int orderSetId) {
			this.orderSetId = orderSetId;
		}
		public int getPromotionPriceGroupId() {
			return promotionPriceGroupId;
		}
		public void setPromotionPriceGroupId(int promotionPriceGroupId) {
			this.promotionPriceGroupId = promotionPriceGroupId;
		}
		public int getPromotionTypeId() {
			return promotionTypeId;
		}
		public void setPromotionTypeId(int promotionTypeId) {
			this.promotionTypeId = promotionTypeId;
		}
		public double getOrderQty() {
			return orderQty;
		}
		public void setOrderQty(double orderQty) {
			this.orderQty = orderQty;
		}
		public double getDeductAmount() {
			return deductAmount;
		}
		public void setDeductAmount(double deductAmount) {
			this.deductAmount = deductAmount;
		}
		public double getVatExclude() {
			return vatExclude;
		}
		public void setVatExclude(double vatExclude) {
			this.vatExclude = vatExclude;
		}
		public int getPriceOrPercent() {
			return priceOrPercent;
		}
		public void setPriceOrPercent(int priceOrPercent) {
			this.priceOrPercent = priceOrPercent;
		}
		public String getPromotionName() {
			return promotionName;
		}
		public void setPromotionName(String promotionName) {
			this.promotionName = promotionName;
		}
		public String getOrderComment() {
			return orderComment;
		}
		public void setOrderComment(String orderComment) {
			this.orderComment = orderComment;
		}
		public List<OrderSet.OrderSetDetail> getOrdSetDetailLst() {
			return ordSetDetailLst;
		}
		public void setOrdSetDetailLst(List<OrderSet.OrderSetDetail> ordSetDetailLst) {
			this.ordSetDetailLst = ordSetDetailLst;
		}
		public void setOrderCommentLst(List<OrderComment> orderCommentLst) {
			this.orderCommentLst = orderCommentLst;
		}
		public List<OrderComment> getOrderCommentLst() {
			return orderCommentLst;
		}
	}
package com.synature.pos;

import java.util.List;

public class MasterData {
	private List<ShopProperty> ShopProperty;
	private List<ComputerProperty> ComputerProperty;
	private List<Staff> Staffs;
	private List<StaffPermission> StaffPermission;
	private List<GlobalProperty> GlobalProperty;
	private List<Language> Language;
	private List<ProgramFeature> ProgramFeature;
	private List<HeaderFooterReceipt> HeaderFooterReceipt;
	private List<BankName> BankName;
	private List<CreditCardType> CreditCardType;
	private List<PayType> PayType;
	private List<PayType> PayTypeFinishWaste;
	private List<PaymentAmountButton> PaymentAmountButton;

	private List<ProductGroup> ProductGroup;
	private List<ProductDept> ProductDept;
	private List<Product> Products;
	private List<ProductPrice> ProductPrice;
	private List<ProductComponent> ProductComponent;
	private List<PComponentGroup> PComponentGroup;
	private List<CommentTransDept> CommentTransDept;
	private List<CommentTransItem> COmmentTransItem;
	private List<CommentProduct> CommentProduct;
	private List<SaleMode> SaleMode;
	private List<MenuCommentGroup> MenuCommentGroup;
	private List<MenuComment> MenuComment;
	private List<MenuFixComment> MenuFixComment;
	
	private List<PromotionPriceGroup> PromotionPriceGroup;
	private List<PromotionProductDiscount> PromotionProductDiscount;
	
	public List<PayType> getPayTypeFinishWaste() {
		return PayTypeFinishWaste;
	}
	public List<ShopProperty> getShopProperty() {
		return ShopProperty;
	}
	public List<ComputerProperty> getComputerProperty() {
		return ComputerProperty;
	}
	public List<Staff> getStaffs() {
		return Staffs;
	}
	public List<StaffPermission> getStaffPermission() {
		return StaffPermission;
	}
	public List<GlobalProperty> getGlobalProperty() {
		return GlobalProperty;
	}
	public List<Language> getLanguage() {
		return Language;
	}
	public List<ProgramFeature> getProgramFeature() {
		return ProgramFeature;
	}
	public List<HeaderFooterReceipt> getHeaderFooterReceipt() {
		return HeaderFooterReceipt;
	}
	public List<BankName> getBankName() {
		return BankName;
	}
	public List<CreditCardType> getCreditCardType() {
		return CreditCardType;
	}
	public List<PayType> getPayType() {
		return PayType;
	}
	public List<PaymentAmountButton> getPaymentAmountButton() {
		return PaymentAmountButton;
	}
	public List<ProductGroup> getProductGroup() {
		return ProductGroup;
	}
	public List<ProductDept> getProductDept() {
		return ProductDept;
	}
	public List<Product> getProducts() {
		return Products;
	}
	public List<ProductPrice> getProductPrice() {
		return ProductPrice;
	}
	public List<ProductComponent> getProductComponent() {
		return ProductComponent;
	}
	public List<PComponentGroup> getPComponentGroup() {
		return PComponentGroup;
	}
	public List<CommentProduct> getCommentProduct() {
		return CommentProduct;
	}
	public List<SaleMode> getSaleMode() {
		return SaleMode;
	}
	public List<MenuCommentGroup> getMenuCommentGroup() {
		return MenuCommentGroup;
	}
	public List<MenuComment> getMenuComment() {
		return MenuComment;
	}
	public List<MenuFixComment> getMenuFixComment() {
		return MenuFixComment;
	}
	public List<CommentTransDept> getCommentTransDept() {
		return CommentTransDept;
	}
	public List<CommentTransItem> getCOmmentTransItem() {
		return COmmentTransItem;
	}
	public List<PromotionPriceGroup> getPromotionPriceGroup() {
		return PromotionPriceGroup;
	}
	public List<PromotionProductDiscount> getPromotionProductDiscount() {
		return PromotionProductDiscount;
	}
}

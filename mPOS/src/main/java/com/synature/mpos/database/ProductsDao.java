package com.synature.mpos.database;

import java.util.ArrayList;
import java.util.List;

import com.synature.mpos.database.model.Product;
import com.synature.mpos.database.model.ProductComponent;
import com.synature.mpos.database.model.ProductComponentGroup;
import com.synature.mpos.database.model.ProductDept;
import com.synature.mpos.database.model.ProductGroup;
import com.synature.mpos.database.table.BaseColumn;
import com.synature.mpos.database.table.ProductComponentGroupTable;
import com.synature.mpos.database.table.ProductComponentTable;
import com.synature.mpos.database.table.ProductDeptTable;
import com.synature.mpos.database.table.ProductGroupTable;
import com.synature.mpos.database.table.ProductTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.text.TextUtils;

public class ProductsDao extends MPOSDatabase {
	
	/**
	 * Product normal type
	 */
	public static final int NORMAL_TYPE = 0;
	
	/**
	 * Product set 
	 */
	public static final int SET = 1;
	
	/**
	 * Product size
	 */
	public static final int SIZE = 2;
	
	/**
	 * Product open price
	 */
	public static final int OPEN_QTY = 5;
	
	/**
	 * Product set can select
	 */
	public static final int SET_CAN_SELECT = 7;
	
	/**
	 * MenuComment not have price
	 */
	public static final int COMMENT_NOT_HAVE_PRICE = 14;
	
	/**
	 * MenuComment have price
	 */
	public static final int COMMENT_HAVE_PRICE = 15;
	
	/**
	 * Child of product type 7 have price
	 */
	public static final int CHILD_OF_SET_HAVE_PRICE = -6;
	
	/**
	 * Product include vat
	 */
	public static final int VAT_TYPE_INCLUDED = 1;
	
	/**
	 * Product exclude vat
	 */
	public static final int VAT_TYPE_EXCLUDE = 2;
	
	/**
	 * Product no vat
	 */
	public static final int NO_VAT = 0;
	
	/**
	 * Product that no activated
	 */
	public static final int NO_ACTIVATED = 0;
	
	/**
	 * Product that activated
	 */
	public static final int ACTIVATED = 1;
	
	public static final String[] ALL_PRODUCT_COLS = {
		ProductTable.COLUMN_PRODUCT_ID, 
		ProductDeptTable.COLUMN_PRODUCT_DEPT_ID,
		ProductTable.COLUMN_PRODUCT_CODE,
		ProductTable.COLUMN_PRODUCT_BAR_CODE,
		ProductTable.COLUMN_PRODUCT_NAME,
		ProductTable.COLUMN_PRODUCT_NAME1,
		ProductTable.COLUMN_PRODUCT_NAME2,
		ProductTable.COLUMN_PRODUCT_DESC,
		ProductTable.COLUMN_PRODUCT_TYPE_ID,
		ProductTable.COLUMN_PRODUCT_PRICE,
		ProductTable.COLUMN_PRODUCT_UNIT_NAME,
		ProductTable.COLUMN_DISCOUNT_ALLOW,
		ProductTable.COLUMN_VAT_TYPE,
		ProductTable.COLUMN_VAT_RATE,
		ProductTable.COLUMN_IS_OUTOF_STOCK,
		ProductTable.COLUMN_IMG_FILE_NAME
	};
	
	public static final String[] ALL_PRODUCT_GROUP_COLS = {
		ProductGroupTable.COLUMN_PRODUCT_GROUP_ID,
		ProductGroupTable.COLUMN_PRODUCT_GROUP_CODE,
		ProductGroupTable.COLUMN_PRODUCT_GROUP_NAME,
		ProductGroupTable.COLUMN_PRODUCT_GROUP_NAME1,
		ProductGroupTable.COLUMN_PRODUCT_GROUP_NAME2
	};
	
	public static final String[] ALL_PRODUCT_DEPT_COLS = {
		ProductGroupTable.COLUMN_PRODUCT_GROUP_ID,
		ProductDeptTable.COLUMN_PRODUCT_DEPT_ID,
		ProductDeptTable.COLUMN_PRODUCT_DEPT_CODE,
		ProductDeptTable.COLUMN_PRODUCT_DEPT_NAME,
		ProductDeptTable.COLUMN_PRODUCT_DEPT_NAME1
	};

	public ProductsDao(Context context){
		super(context);
	}
	
	/**
	 * List PComponetGroup for create SetGroup
	 * @param productId
	 * @return List<ProductComponentGroup>
	 */
	public List<ProductComponentGroup> listProductComponentGroup(int productId){
		List<ProductComponentGroup> pCompGroupLst = new ArrayList<ProductComponentGroup>();
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT a." + ProductTable.COLUMN_PRODUCT_ID + ","
				+ " a." + ProductComponentTable.COLUMN_PGROUP_ID + ","
				+ " a." + ProductComponentGroupTable.COLUMN_SET_GROUP_NO + ","
				+ " a." + ProductComponentGroupTable.COLUMN_SET_GROUP_NAME + ","
				+ " a." + ProductComponentGroupTable.COLUMN_REQ_AMOUNT + ","
				+ " a." + ProductComponentGroupTable.COLUMN_REQ_MIN_AMOUNT + ", "
				+ " a." + ProductTable.COLUMN_SALE_MODE + ","
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME + ","
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME1 + ","
				+ " b." + ProductTable.COLUMN_IMG_FILE_NAME 
				+ " FROM " + ProductComponentGroupTable.TABLE_PCOMPONENT_GROUP + " a "
				+ " LEFT JOIN " + ProductTable.TABLE_PRODUCT + " b "
				+ " ON a." + ProductTable.COLUMN_PRODUCT_ID + "=b."
				+ ProductTable.COLUMN_PRODUCT_ID 
				+ " WHERE a." + ProductTable.COLUMN_PRODUCT_ID + "=?"
				+ " AND b." + ProductTable.COLUMN_ACTIVATE + "=?"
				+ " AND b." + COLUMN_DELETED + "=?"
				+ " ORDER BY a." + ProductComponentGroupTable.COLUMN_SET_GROUP_NO,
				new String[]{
					String.valueOf(productId),
					String.valueOf(ACTIVATED),
					String.valueOf(NOT_DELETE)
				}
		);
		if(cursor.moveToFirst()){
			do{
				ProductComponentGroup pCompGroup = new ProductComponentGroup();
				String pgName = cursor.getString(cursor.getColumnIndex(ProductComponentGroupTable.COLUMN_SET_GROUP_NAME));
				pCompGroup.setProductId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID)));
				pCompGroup.setProductGroupId(cursor.getInt(cursor.getColumnIndex(ProductComponentTable.COLUMN_PGROUP_ID)));
				pCompGroup.setGroupNo(cursor.getInt(cursor.getColumnIndex(ProductComponentGroupTable.COLUMN_SET_GROUP_NO)));
				pCompGroup.setGroupName(TextUtils.isEmpty(pgName) ? cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME)) : pgName);
				pCompGroup.setRequireAmount(cursor.getDouble(cursor.getColumnIndex(ProductComponentGroupTable.COLUMN_REQ_AMOUNT)));
				pCompGroup.setRequireMinAmount(cursor.getDouble(cursor.getColumnIndex(ProductComponentGroupTable.COLUMN_REQ_MIN_AMOUNT)));
				pCompGroup.setSaleMode(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_SALE_MODE)));
				pCompGroup.setProductName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME)));
				pCompGroup.setProductName1(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME1)));
				pCompGroup.setImgName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_IMG_FILE_NAME)));
				pCompGroupLst.add(pCompGroup);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return pCompGroupLst;
	}
	
	/**
	 * @param groupId
	 * @return List<ProductComponent>
	 */
	public List<ProductComponent> listProductComponent(int groupId){
		List<ProductComponent> pCompLst = new ArrayList<ProductComponent>();
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT a." + ProductComponentTable.COLUMN_PGROUP_ID + ","
				+ " a." + ProductComponentTable.COLUMN_CHILD_PRODUCT_AMOUNT + ","
				+ " a." + ProductComponentTable.COLUMN_FLEXIBLE_INCLUDE_PRICE + ","
				+ " a." + ProductComponentTable.COLUMN_FLEXIBLE_PRODUCT_PRICE + ","
				+ " a." + ProductTable.COLUMN_SALE_MODE + ","
				+ " b." + ProductTable.COLUMN_PRODUCT_ID + ","
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME + ","
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME1 + ","
				+ " b." + ProductTable.COLUMN_PRODUCT_PRICE + ","
				+ " b." + ProductTable.COLUMN_IMG_FILE_NAME 
				+ " FROM " + ProductComponentTable.TABLE_PCOMPONENT + " a "
				+ " LEFT JOIN " + ProductTable.TABLE_PRODUCT + " b "
				+ " ON a." + ProductComponentTable.COLUMN_CHILD_PRODUCT_ID + "=b." + ProductTable.COLUMN_PRODUCT_ID 
				+ " WHERE a." + ProductComponentTable.COLUMN_PGROUP_ID + "=?"
				+ " AND b." + COLUMN_DELETED + "=?"
				+ " ORDER BY b." + ProductTable.COLUMN_ORDERING + ", b." + ProductTable.COLUMN_PRODUCT_NAME,
				new String[]{
					String.valueOf(groupId),
					String.valueOf(NOT_DELETE)
				}
		);
		if(cursor.moveToFirst()){
			do{
				ProductComponent pComp = new ProductComponent();
				pComp.setProductGroupId(cursor.getInt(cursor.getColumnIndex(ProductComponentTable.COLUMN_PGROUP_ID)));
				pComp.setProductId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID)));
				pComp.setSaleMode(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_SALE_MODE)));
				pComp.setChildProductAmount(cursor.getDouble(cursor.getColumnIndex(ProductComponentTable.COLUMN_CHILD_PRODUCT_AMOUNT)));
				pComp.setFlexibleIncludePrice(cursor.getInt(cursor.getColumnIndex(ProductComponentTable.COLUMN_FLEXIBLE_INCLUDE_PRICE)));
				pComp.setFlexibleProductPrice(cursor.getDouble(cursor.getColumnIndex(ProductComponentTable.COLUMN_FLEXIBLE_PRODUCT_PRICE)));
				pComp.setProductName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME)));
				pComp.setProductName1(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME1)));
				pComp.setImgName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_IMG_FILE_NAME)));
				pComp.setProductPrice(cursor.getDouble(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_PRICE)));
				pCompLst.add(pComp);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return pCompLst;
	}
	
	/**
	 * @return List<ProductGroup>
	 */
	public List<ProductGroup> listProductGroup(){
		List<ProductGroup> pgLst = new ArrayList<ProductGroup>();
		Cursor cursor = getReadableDatabase().query(
				ProductGroupTable.TABLE_PRODUCT_GROUP, 
				ALL_PRODUCT_GROUP_COLS, 
				ProductTable.COLUMN_ACTIVATE + "=?"
				+ " AND " + COLUMN_DELETED + "=?", 
				new String[]{
					String.valueOf(ACTIVATED),
					String.valueOf(NOT_DELETE)
				}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				ProductGroup pg = toProductGroup(cursor);
				pgLst.add(pg);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return pgLst;
	}

	/**
	 * @return List<ProductDept>
	 */
	public List<ProductDept> listProductDept(){
		List<ProductDept> pdLst = new ArrayList<ProductDept>();
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT a." + ProductGroupTable.COLUMN_PRODUCT_GROUP_ID + ", "
				+ " a." + ProductDeptTable.COLUMN_PRODUCT_DEPT_ID + ","
				+ " a." + ProductDeptTable.COLUMN_PRODUCT_DEPT_CODE + ","
				+ " a." + ProductDeptTable.COLUMN_PRODUCT_DEPT_NAME + ","
				+ " a." + ProductDeptTable.COLUMN_PRODUCT_DEPT_NAME1
				+ " FROM " + ProductDeptTable.TABLE_PRODUCT_DEPT + " a "
				+ " LEFT JOIN " + ProductGroupTable.TABLE_PRODUCT_GROUP + " b "
				+ " ON a." + ProductGroupTable.COLUMN_PRODUCT_GROUP_ID + "=b." + ProductGroupTable.COLUMN_PRODUCT_GROUP_ID
				+ " WHERE b." + ProductGroupTable.COLUMN_IS_COMMENT + "=?"
				+ " AND a." + ProductTable.COLUMN_ACTIVATE + "=?"
				+ " AND a." + COLUMN_DELETED + "=?"
				+ " ORDER BY a." + COLUMN_ORDERING + ", a." + ProductDeptTable.COLUMN_PRODUCT_DEPT_NAME,
				new String[]{
					String.valueOf(0),
					String.valueOf(ACTIVATED),
					String.valueOf(NOT_DELETE)
				});
		if(cursor.moveToFirst()){
			do{
				ProductDept pd = toProductDept(cursor);
				pdLst.add(pd);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return pdLst;
	}
	
	/**
	 * @param proId
	 * @return List<Product>
	 */
	public List<Product> listProductSize(int proId){
		List<Product> pLst = new ArrayList<Product>();
		Cursor cursor = getReadableDatabase().rawQuery(
				"SELECT b." + ProductTable.COLUMN_PRODUCT_ID + ", " 
				+ " b." + ProductTable.COLUMN_PRODUCT_TYPE_ID + ", " 
				+ " b." + ProductTable.COLUMN_PRODUCT_CODE + ", " 
				+ " b." + ProductTable.COLUMN_PRODUCT_BAR_CODE + ", " 
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME + ", "  
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME1 + ", " 
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME2 + ", "  
				+ " b." + ProductTable.COLUMN_PRODUCT_PRICE + ", " 
				+ " b." + ProductTable.COLUMN_VAT_TYPE + ", " 
				+ " b." + ProductTable.COLUMN_VAT_RATE + ", "
				+ " b." + ProductTable.COLUMN_IMG_FILE_NAME
				+ " FROM " + ProductComponentTable.TABLE_PCOMPONENT + " a "
				+ " INNER JOIN " + ProductTable.TABLE_PRODUCT + " b "
				+ " ON a." + ProductComponentTable.COLUMN_CHILD_PRODUCT_ID + "=b."
				+ ProductTable.COLUMN_PRODUCT_ID 
				+ " WHERE a."+ ProductTable.COLUMN_PRODUCT_ID + "=?"
				+ " AND b." + COLUMN_DELETED + "=?"
				+ " ORDER BY b." + COLUMN_ORDERING + ", b." + ProductTable.COLUMN_PRODUCT_NAME,
				new String[] { 
					String.valueOf(proId),
					String.valueOf(NOT_DELETE)
				}
		);
		if(cursor.moveToFirst()){
			do{
				Product p = new Product();
				p.setProductId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID)));
				p.setProductTypeId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_TYPE_ID)));
				p.setProductCode(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_CODE)));
				p.setProductBarCode(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_BAR_CODE)));
				p.setProductName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME)));
				p.setProductName1(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME1)));
				p.setProductPrice(cursor.getDouble(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_PRICE)));
				p.setVatType(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_VAT_TYPE)));
				p.setVatRate(cursor.getDouble(cursor.getColumnIndex(ProductTable.COLUMN_VAT_RATE)));
				p.setImgName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_IMG_FILE_NAME)));
				pLst.add(p);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return pLst;
	}
	
	/**
	 * @param query
	 * @return List<Product>
	 */
	public List<Product> listProduct(String query){
		List<Product> pLst = new ArrayList<Product>();
		Cursor cursor = getReadableDatabase().query(
				ProductTable.TABLE_PRODUCT,
				ALL_PRODUCT_COLS,
				"(" + ProductTable.COLUMN_PRODUCT_CODE + " LIKE '%?" 
				+ "%?' " + " OR " + ProductTable.COLUMN_PRODUCT_NAME
				+ " LIKE '%" + query + "%')"
				+ " AND " + COLUMN_DELETED + "=?", 
				new String[]{
					query,
					query,
					String.valueOf(NOT_DELETE)
				}, null, null, COLUMN_ORDERING + ", " + ProductTable.COLUMN_PRODUCT_NAME);
		if(cursor.moveToFirst()){
			do{
				Product p = toProduct(cursor);
				pLst.add(p);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return pLst;
	}
	
	/**
	 * @param deptId
	 * @return List<Product>
	 */
	public List<Product> listProduct(int deptId){
		List<Product> pLst = new ArrayList<Product>();
		Cursor cursor = getReadableDatabase().query(
				ProductTable.TABLE_PRODUCT,
				ALL_PRODUCT_COLS,
				ProductDeptTable.COLUMN_PRODUCT_DEPT_ID + "=? " 
				+ " AND " + ProductTable.COLUMN_ACTIVATE + "=?"
				+ " AND " + COLUMN_DELETED + "=?",
				new String[] { 
					String.valueOf(deptId), 
					String.valueOf(ACTIVATED),
					String.valueOf(NOT_DELETE)
				}, null, null, COLUMN_ORDERING + ", " + ProductTable.COLUMN_PRODUCT_NAME);
		if(cursor.moveToFirst()){
			do{
				Product p = toProduct(cursor);
				pLst.add(p);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return pLst;
	}

	/**
	 * Check allow discount
	 * @param productId
	 * @return true if allow discount
	 */
	public boolean isAllowDiscount(int productId){
		boolean isAllowDiscount = false;
		Cursor cursor = getReadableDatabase().query(ProductTable.TABLE_PRODUCT, 
				new String[]{
					ProductTable.COLUMN_DISCOUNT_ALLOW,
				}, ProductTable.COLUMN_PRODUCT_ID + "=?", 
				new String[]{
					String.valueOf(productId)
				}, null, null, null);
		if(cursor.moveToFirst()){
			if(cursor.getInt(0) == 1)
				isAllowDiscount = true;
		}
		cursor.close();
		return isAllowDiscount;
	}
	
	/**
	 * @param productId
	 * @return vatType include or exclude
 	 */
	public int getVatType(int productId){
		int vatType = VAT_TYPE_INCLUDED;
		Cursor cursor = queryProduct(
				new String[] { 
					ProductTable.COLUMN_VAT_TYPE 
				},
				ProductTable.COLUMN_PRODUCT_ID + "=?",
				new String[] { 
					String.valueOf(productId) 
				}
		);
		if(cursor.moveToFirst()){
			vatType = cursor.getInt(0);
		}
		cursor.close();
		return vatType;
	}
	
	/**
	 * @param productId
	 * @return product vat rate
	 */
	public double getVatRate(int productId){
		double vatRate = 0.0f;
		Cursor cursor = queryProduct(
				new String[] { ProductTable.COLUMN_VAT_RATE },
				ProductTable.COLUMN_PRODUCT_ID + "=?",
				new String[] { 
					String.valueOf(productId) 
				});
		if(cursor.moveToFirst()){
			vatRate = cursor.getDouble(0);
		}
		cursor.close();
		return vatRate;
	}
	
	/**
	 * Get product by bar code
	 * @param barCode
	 * @return Product
	 */
	public Product getProduct(String barCode){
		Product p = null;
		Cursor cursor = getReadableDatabase().query(
				ProductTable.TABLE_PRODUCT,
				ALL_PRODUCT_COLS, 
				ProductTable.COLUMN_PRODUCT_BAR_CODE + "=?"
				+ " AND " + COLUMN_DELETED + "=?",
				new String[] { 
					barCode,
					String.valueOf(NOT_DELETE)
				}, null, null, null);
		if(cursor.moveToFirst()){
			p = toProduct(cursor);
		}
		cursor.close();
		return p;
	}
	
	/**
	 * @param proId
	 * @return Product
	 */
	public Product getProduct(int proId){
		Product p = new Product();
		Cursor cursor = getReadableDatabase().query(
				ProductTable.TABLE_PRODUCT,
				ALL_PRODUCT_COLS, 
				ProductTable.COLUMN_PRODUCT_ID + "=?",
				new String[] { 
					String.valueOf(proId) 
				}, null, null, null);
		if(cursor.moveToFirst()){
			p = toProduct(cursor);
		}
		cursor.close();
		return p;
	}
	
	/**
	 * @param deptId
	 * @return ProductDept
	 */
	public ProductDept getProductDept(int deptId){
		ProductDept pd = new ProductDept();
		Cursor cursor = getReadableDatabase().query(
				ProductDeptTable.TABLE_PRODUCT_DEPT, 
				ALL_PRODUCT_DEPT_COLS,
				ProductDeptTable.COLUMN_PRODUCT_DEPT_ID + "=?",
				new String[] { 
					String.valueOf(deptId) 
				}, null, null, null);
		if(cursor.moveToFirst()){
			pd = toProductDept(cursor);
		}
		cursor.close();
		return pd;
	}
	
	/**
	 * @param columns
	 * @param selection
	 * @param selectionArgs
	 * @return Cursor
	 */
	public Cursor queryProduct(String[] columns, String selection, String[] selectionArgs){
		return getReadableDatabase().query(ProductTable.TABLE_PRODUCT, columns,
				selection, selectionArgs, null, null, null);
	}
	
	public Product toProduct(Cursor cursor){
		Product p = new Product();
		p.setProductId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID)));
		p.setProductDeptId(cursor.getInt(cursor.getColumnIndex(ProductDeptTable.COLUMN_PRODUCT_DEPT_ID)));
		p.setProductTypeId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_TYPE_ID)));
		p.setProductCode(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_CODE)));
		p.setProductBarCode(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_BAR_CODE)));
		p.setProductName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME)));
		p.setProductName1(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME1)));
		p.setProductName2(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME2)));
		p.setProductDesc(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_DESC)));
		p.setProductUnitName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_UNIT_NAME)));
		p.setProductPrice(cursor.getDouble(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_PRICE)));
		p.setVatType(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_VAT_TYPE)));
		p.setVatRate(cursor.getDouble(cursor.getColumnIndex(ProductTable.COLUMN_VAT_RATE)));
		p.setDiscountAllow(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_DISCOUNT_ALLOW)));
		p.setImgName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_IMG_FILE_NAME)));
		return p;
	}

	public ProductDept toProductDept(Cursor cursor){
		ProductDept pd = new ProductDept();
		pd.setProductDeptId(cursor.getInt(cursor.getColumnIndex(ProductDeptTable.COLUMN_PRODUCT_DEPT_ID)));
		pd.setProductGroupId(cursor.getInt(cursor.getColumnIndex(ProductGroupTable.COLUMN_PRODUCT_GROUP_ID)));
		pd.setProductDeptCode(cursor.getString(cursor.getColumnIndex(ProductDeptTable.COLUMN_PRODUCT_DEPT_CODE)));
		pd.setProductDeptName(cursor.getString(cursor.getColumnIndex(ProductDeptTable.COLUMN_PRODUCT_DEPT_NAME)));
		pd.setProductDeptName1(cursor.getString(cursor.getColumnIndex(ProductDeptTable.COLUMN_PRODUCT_DEPT_NAME1)));
		return pd;
	}

	public ProductGroup toProductGroup(Cursor cursor){
		ProductGroup pg = new ProductGroup();
		pg.setProductGroupId(cursor.getInt(cursor.getColumnIndex(ProductGroupTable.COLUMN_PRODUCT_GROUP_ID)));
		pg.setProductGroupCode(cursor.getString(cursor.getColumnIndex(ProductGroupTable.COLUMN_PRODUCT_GROUP_CODE)));
		pg.setProductGroupName(cursor.getString(cursor.getColumnIndex(ProductGroupTable.COLUMN_PRODUCT_GROUP_NAME)));
		pg.setProductGroupName1(cursor.getString(cursor.getColumnIndex(ProductGroupTable.COLUMN_PRODUCT_GROUP_NAME1)));
		pg.setProductGroupName2(cursor.getString(cursor.getColumnIndex(ProductGroupTable.COLUMN_PRODUCT_GROUP_NAME2)));
		return pg;
	}
	
	/**
	 * @param pCompGroupLst
	 * @throws SQLException
	 */
	public void insertPComponentGroup(List<com.synature.pos.PComponentGroup> pCompGroupLst) throws SQLException{
		getWritableDatabase().delete(ProductComponentGroupTable.TABLE_PCOMPONENT_GROUP, null, null);
		for(com.synature.pos.PComponentGroup pCompGroup : pCompGroupLst){
			ContentValues cv = new ContentValues();
			cv.put(ProductComponentTable.COLUMN_PGROUP_ID, pCompGroup.getPGRID());
			cv.put(ProductTable.COLUMN_PRODUCT_ID, pCompGroup.getPID());
			cv.put(ProductTable.COLUMN_SALE_MODE, pCompGroup.getSMODE());
			cv.put(ProductComponentGroupTable.COLUMN_SET_GROUP_NO, pCompGroup.getSGRPNO());
			cv.put(ProductComponentGroupTable.COLUMN_SET_GROUP_NAME, pCompGroup.getSGRPNAM());
			cv.put(ProductComponentGroupTable.COLUMN_REQ_AMOUNT, pCompGroup.getRQAMT());
			cv.put(ProductComponentGroupTable.COLUMN_REQ_MIN_AMOUNT, pCompGroup.getRQMINAMT());
			getWritableDatabase().insertOrThrow(ProductComponentGroupTable.TABLE_PCOMPONENT_GROUP, null, cv);
		}
	}
	
	/**
	 * @param pCompLst
	 * @throws SQLException
	 */
	public void insertProductComponent(List<com.synature.pos.ProductComponent> pCompLst) throws SQLException{
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(ProductComponentTable.TABLE_PCOMPONENT, null, null);
			for(com.synature.pos.ProductComponent pComp : pCompLst){
				ContentValues cv = new ContentValues();
				cv.put(ProductComponentTable.COLUMN_PGROUP_ID, pComp.getPGRID());
				cv.put(ProductTable.COLUMN_PRODUCT_ID, pComp.getPID());
				cv.put(ProductTable.COLUMN_SALE_MODE, pComp.getSMODE());
				cv.put(ProductComponentTable.COLUMN_CHILD_PRODUCT_ID, pComp.getCHDPID());
				cv.put(ProductComponentTable.COLUMN_CHILD_PRODUCT_AMOUNT, pComp.getCHDAMT());
				cv.put(ProductComponentTable.COLUMN_FLEXIBLE_PRODUCT_PRICE, pComp.getFPRICE());
				cv.put(ProductComponentTable.COLUMN_FLEXIBLE_INCLUDE_PRICE, pComp.getFINCPRICE());
				getWritableDatabase().insertOrThrow(ProductComponentTable.TABLE_PCOMPONENT, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
	
	/**
	 * @param pgLst
	 * @param mgLst
	 * @throws SQLException
	 */
	public void insertProductGroup(List<com.synature.pos.ProductGroup> pgLst) throws SQLException{
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(ProductGroupTable.TABLE_PRODUCT_GROUP, null, null);
			for(com.synature.pos.ProductGroup pg : pgLst){
				ContentValues cv = new ContentValues();
				cv.put(ProductGroupTable.COLUMN_PRODUCT_GROUP_ID, pg.getPGID());
				cv.put(ProductGroupTable.COLUMN_PRODUCT_GROUP_CODE, pg.getPGCOD());
				cv.put(ProductGroupTable.COLUMN_PRODUCT_GROUP_NAME, pg.getPGNAM());
				cv.put(ProductGroupTable.COLUMN_PRODUCT_GROUP_TYPE, pg.getPGTY());
				cv.put(ProductGroupTable.COLUMN_IS_COMMENT, pg.getISCOMM());
				cv.put(COLUMN_ORDERING, pg.getPGORD());
				cv.put(ProductTable.COLUMN_ACTIVATE, pg.getACTIVATE());
				cv.put(COLUMN_DELETED, pg.getDEL());
				getWritableDatabase().insertOrThrow(ProductGroupTable.TABLE_PRODUCT_GROUP, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
	
	/**
	 * @param pdLst
	 * @param mdLst
	 * @throws SQLException
	 */
	public void insertProductDept(List<com.synature.pos.ProductDept> pdLst) throws SQLException{
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(ProductDeptTable.TABLE_PRODUCT_DEPT, null, null);
			for(com.synature.pos.ProductDept pd : pdLst){
				ContentValues cv = new ContentValues();
				cv.put(ProductDeptTable.COLUMN_PRODUCT_DEPT_ID, pd.getPDID());
				cv.put(ProductGroupTable.COLUMN_PRODUCT_GROUP_ID, pd.getPGID());
				cv.put(ProductDeptTable.COLUMN_PRODUCT_DEPT_CODE, pd.getPDCOD());
				cv.put(ProductDeptTable.COLUMN_PRODUCT_DEPT_NAME, pd.getPDNAM());
				cv.put(ProductTable.COLUMN_ACTIVATE, pd.getACTIVATE());
				cv.put(COLUMN_DELETED, pd.getDEL());
				cv.put(COLUMN_ORDERING, pd.getPDORD());
				getWritableDatabase().insertOrThrow(ProductDeptTable.TABLE_PRODUCT_DEPT, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
	
	/**
	 * @param pLst
	 * @param mLst
	 * @throws SQLException
	 */
	public void insertProducts(List<com.synature.pos.Product> pLst) throws SQLException{
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(ProductTable.TABLE_PRODUCT, null, null);
			for(com.synature.pos.Product p : pLst){
				ContentValues cv = new ContentValues();
				cv.put(ProductTable.COLUMN_PRODUCT_ID, p.getPID());
				cv.put(ProductDeptTable.COLUMN_PRODUCT_DEPT_ID, p.getPDID());
				cv.put(ProductGroupTable.COLUMN_PRODUCT_GROUP_ID, p.getPGID());
				cv.put(ProductTable.COLUMN_PRODUCT_CODE, p.getPCODE());
				cv.put(ProductTable.COLUMN_PRODUCT_BAR_CODE, p.getPBAR());
				cv.put(ProductTable.COLUMN_PRODUCT_NAME, p.getPNAME0());
				cv.put(ProductTable.COLUMN_PRODUCT_NAME1, p.getPNAME1());
				cv.put(ProductTable.COLUMN_PRODUCT_NAME2, p.getPNAME2());
				cv.put(ProductTable.COLUMN_PRODUCT_TYPE_ID, p.getPTYPE());
				cv.put(ProductTable.COLUMN_PRODUCT_PRICE, p.getPRICE());
				cv.put(ProductTable.COLUMN_PRODUCT_UNIT_NAME, p.getUNITNAME());
				cv.put(ProductTable.COLUMN_PRODUCT_DESC, p.getPDESC());
				cv.put(ProductTable.COLUMN_DISCOUNT_ALLOW, p.getDISALLOW());
				cv.put(ProductTable.COLUMN_VAT_TYPE, p.getVATTYP());
				cv.put(ProductTable.COLUMN_VAT_RATE, p.getVATRATE());
				cv.put(ProductTable.COLUMN_IS_OUTOF_STOCK, p.getOOSTOC());
				cv.put(ProductTable.COLUMN_ACTIVATE, p.getACTIVATE());
				cv.put(COLUMN_DELETED, p.getDEL());
				cv.put(ProductTable.COLUMN_IMG_FILE_NAME, p.getIMGLINK());
				cv.put(BaseColumn.COLUMN_ORDERING, p.getPORD());
				getWritableDatabase().insertOrThrow(ProductTable.TABLE_PRODUCT, null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
}

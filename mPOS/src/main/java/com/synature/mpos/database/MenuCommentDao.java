package com.synature.mpos.database;

import java.util.ArrayList;
import java.util.List;

import com.synature.mpos.database.model.Comment;
import com.synature.mpos.database.model.CommentGroup;
import com.synature.mpos.database.table.MenuFixCommentTable;
import com.synature.mpos.database.table.ProductGroupTable;
import com.synature.mpos.database.table.ProductTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class MenuCommentDao extends MPOSDatabase{

	public MenuCommentDao(Context context) {
		super(context);
	}

	/**
	 * @return List<Comment> 
	 */
	public List<Comment> listMenuComment(){
		List<Comment> mcl = new ArrayList<Comment>();
		String selection = "a." + ProductGroupTable.COLUMN_IS_COMMENT + "=?"
				+ " AND a." + COLUMN_DELETED + "=?"
				+ " AND b." + COLUMN_DELETED + "=?";
		String[] selectionArgs = {
				String.valueOf(1),
				String.valueOf(NOT_DELETE),
				String.valueOf(NOT_DELETE)
		};
		Cursor cursor = queryMenuComment(selection, selectionArgs);
		if(cursor.moveToFirst()){
			do{
				Comment cm = new Comment();
				cm.setCommentId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID)));
				cm.setCommentName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME)));
				cm.setCommentPrice(cursor.getDouble(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_PRICE)));
				mcl.add(cm);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return mcl;
	}
	
	/**
	 * @param groupId
	 * @return List<Comment>
	 */
	public List<Comment> listMenuComment(int groupId){
		List<Comment> mcl = new ArrayList<Comment>();
		String selection = "a." + ProductGroupTable.COLUMN_PRODUCT_GROUP_ID + "=?" 
				+ " AND a." + ProductGroupTable.COLUMN_IS_COMMENT + "=?"
				+ " AND a." + COLUMN_DELETED + "=?"
				+ " AND b." + COLUMN_DELETED + "=?";
		String[] selectionArgs = {
				String.valueOf(groupId),
				String.valueOf(1),
				String.valueOf(NOT_DELETE),
				String.valueOf(NOT_DELETE)
		};
		Cursor cursor = queryMenuComment(selection, selectionArgs);
		if(cursor.moveToFirst()){
			do{
				Comment cm = new Comment();
				cm.setCommentId(cursor.getInt(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_ID)));
				cm.setCommentName(cursor.getString(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_NAME)));
				cm.setCommentPrice(cursor.getDouble(cursor.getColumnIndex(ProductTable.COLUMN_PRODUCT_PRICE)));
				mcl.add(cm);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return mcl;
	}
	
	private Cursor queryMenuComment(String selection, String[] selectionArgs){
		String sql = "SELECT b." + ProductTable.COLUMN_PRODUCT_ID + ", "
				+ " b." + ProductTable.COLUMN_PRODUCT_NAME + ", "
				+ " b." + ProductTable.COLUMN_PRODUCT_PRICE
				+ " FROM " + ProductGroupTable.TABLE_PRODUCT_GROUP + " a "
				+ " LEFT JOIN " + ProductTable.TABLE_PRODUCT + " b "
				+ " ON a." + ProductGroupTable.COLUMN_PRODUCT_GROUP_ID + "=b." + ProductGroupTable.COLUMN_PRODUCT_GROUP_ID
				+ " WHERE " + selection
				+ " ORDER BY b." + COLUMN_ORDERING + ", b." + ProductTable.COLUMN_PRODUCT_NAME;
		return getReadableDatabase().rawQuery(sql, selectionArgs);
	}
	
	/**
	 * @return List<CommentGroup> 
	 */
	public List<CommentGroup> listMenuCommentGroup(){
		List<CommentGroup> cgl = new ArrayList<CommentGroup>();
		Cursor cursor = getReadableDatabase().query(
				ProductGroupTable.TABLE_PRODUCT_GROUP, 
				new String[]{
					ProductGroupTable.COLUMN_PRODUCT_GROUP_ID,
					ProductGroupTable.COLUMN_PRODUCT_GROUP_NAME
				}, 
				ProductGroupTable.COLUMN_IS_COMMENT + "=?"
				+ " AND " + COLUMN_DELETED + "=?", 
				new String[]{
						String.valueOf(1),
						String.valueOf(ProductsDao.NOT_DELETE)
				}, null, null, null);
		if(cursor.moveToFirst()){
			do{
				CommentGroup cg = new CommentGroup();
				cg.setCommentGroupId(cursor.getInt(cursor.getColumnIndex(ProductGroupTable.COLUMN_PRODUCT_GROUP_ID)));
				cg.setCommentGroupName(cursor.getString(cursor.getColumnIndex(ProductGroupTable.COLUMN_PRODUCT_GROUP_NAME)));
				cgl.add(cg);
			}while(cursor.moveToNext());
		}
		cursor.close();
		return cgl;
	}
	
	/**
	 * @param cfl
	 */
	public void insertMenuFixComment(List<com.synature.pos.MenuFixComment> cfl){
		getWritableDatabase().beginTransaction();
		try {
			getWritableDatabase().delete(MenuFixCommentTable.TABLE_MENU_FIX_COMMENT, null, null);
			for(com.synature.pos.MenuFixComment cf : cfl){
				ContentValues cv = new ContentValues();
				cv.put(ProductTable.COLUMN_PRODUCT_ID, cf.getMID());
				cv.put(MenuFixCommentTable.COLUMN_COMMENT_ID, cf.getMCOMID());
				getWritableDatabase().insertOrThrow(MenuFixCommentTable.TABLE_MENU_FIX_COMMENT, 
						null, cv);
			}
			getWritableDatabase().setTransactionSuccessful();
		} finally {
			getWritableDatabase().endTransaction();
		}
	}
}

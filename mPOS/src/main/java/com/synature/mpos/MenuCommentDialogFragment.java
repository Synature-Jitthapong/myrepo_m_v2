package com.synature.mpos;

import java.util.List;

import com.synature.mpos.database.GlobalPropertyDao;
import com.synature.mpos.database.MenuCommentDao;
import com.synature.mpos.database.ProductsDao;
import com.synature.mpos.database.TransactionDao;
import com.synature.mpos.database.model.Comment;
import com.synature.mpos.database.model.CommentGroup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class MenuCommentDialogFragment extends DialogFragment{
	
	/**
	 * selected order position
	 */
	private int mPosition;
	
	private int mTransactionId;
	private int mOrderDetailId;
	private int mComputerId;
	private int mVatType;
	private double mVatRate;
	private String mMenuName;
	private String mOrderComment;
	
	private GlobalPropertyDao mFormat;
	private TransactionDao mTrans;
	private MenuCommentDao mComment;
	private List<CommentGroup> mCommentGroupLst;
	private List<Comment> mCommentLst;
	private MenuCommentAdapter mCommentAdapter;
	private ArrayAdapter<CommentGroup> mCommentGroupAdapter;

	private OnCommentDismissListener mListener;
	
	private Spinner mSpCommentGroup;
	private ListView mLvComment;
	private EditText mTxtComment;
	private ImageButton mBtnHideKeyboard;
	
	public static MenuCommentDialogFragment newInstance(int position, int transactionId, 
			int computerId, int orderDetailId, int vatType, double vatRate, 
			String menuName, String orderComment){
		MenuCommentDialogFragment f = new MenuCommentDialogFragment();
		Bundle b = new Bundle();
		b.putInt("position", position);
		b.putInt("transactionId", transactionId);
		b.putInt("computerId", computerId);
		b.putInt("orderDetailId", orderDetailId);
		b.putInt("vatType", vatType);
		b.putDouble("vatRate", vatRate);
		b.putString("menuName", menuName);
		b.putString("orderComment", orderComment);
		f.setArguments(b);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setCancelable(false);
		mPosition = getArguments().getInt("position");
		mTransactionId = getArguments().getInt("transactionId");
		mComputerId = getArguments().getInt("computerId");
		mOrderDetailId = getArguments().getInt("orderDetailId");
		mVatType = getArguments().getInt("vatType");
		mVatRate = getArguments().getDouble("vatRate");
		mMenuName = getArguments().getString("menuName");
		mOrderComment = getArguments().getString("orderComment");
		
		mFormat = new GlobalPropertyDao(getActivity());
		mTrans = new TransactionDao(getActivity());
		mComment = new MenuCommentDao(getActivity());
		mCommentLst = mComment.listMenuComment();
		mCommentGroupLst = mComment.listMenuCommentGroup();
		CommentGroup commentGroup = new CommentGroup();
		commentGroup.setCommentGroupId(0);
		commentGroup.setCommentGroupName("-- ALL --");
		mCommentGroupLst.add(0, commentGroup);
		
		mTrans.getWritableDatabase().beginTransaction();
	}

	@Override
	public void onAttach(Activity activity) {
        super.onAttach(activity);
		if(activity instanceof OnCommentDismissListener){
			mListener = (OnCommentDismissListener) activity;
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater)
				getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.menu_comment, null, false);
		mSpCommentGroup = (Spinner) view.findViewById(R.id.spCommentGroup);
		mLvComment = (ListView) view.findViewById(R.id.lvComment);
		mTxtComment = (EditText) view.findViewById(R.id.txtComment);
		mBtnHideKeyboard = (ImageButton) view.findViewById(R.id.btnHideKeyboard); 
		mTxtComment.setText(mOrderComment);
		setupCommentGroupAdapter();
		setupCommentAdapter();
		mSpCommentGroup.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				CommentGroup commentGroup = (CommentGroup) parent.getItemAtPosition(position);
				if(commentGroup.getCommentGroupId() == 0)
					mCommentLst = mComment.listMenuComment();
				else
					mCommentLst = mComment.listMenuComment(commentGroup.getCommentGroupId());
				mCommentAdapter.notifyDataSetChanged();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			}
			
		});
		mBtnHideKeyboard.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(
					      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mTxtComment.getWindowToken(), 0);
			}
			
		});
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getActivity().getString(R.string.menu_comment) + ": " + mMenuName);
		builder.setView(view);
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				mTrans.getWritableDatabase().endTransaction();
			}
		});
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(!TextUtils.isEmpty(mTxtComment.getText())){
					mTrans.updateOrderComment(mTransactionId, mOrderDetailId, 
							mTxtComment.getText().toString());
				}
				mTrans.getWritableDatabase().setTransactionSuccessful();
				mTrans.getWritableDatabase().endTransaction();
				mListener.onDismiss(mPosition, mOrderDetailId);
			}
		});
		final AlertDialog d = builder.create();
		d.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		d.show();
		return d;
	}
	
	private void setupCommentGroupAdapter(){
		if(mCommentGroupAdapter == null){
			mCommentGroupAdapter = 
					new ArrayAdapter<CommentGroup>(getActivity(), 
							android.R.layout.simple_spinner_dropdown_item, 
							mCommentGroupLst);
			mSpCommentGroup.setAdapter(mCommentGroupAdapter);
		}
		mCommentGroupAdapter.notifyDataSetChanged();
	}
	
	private void setupCommentAdapter(){
		if(mCommentAdapter == null){
			mCommentAdapter = new MenuCommentAdapter();
			mLvComment.setAdapter(mCommentAdapter);
		}
		mCommentAdapter.notifyDataSetChanged();
	}
	
	private class MenuCommentAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mCommentLst.size();
		}

		@Override
		public Comment getItem(int position) {
			return mCommentLst.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				LayoutInflater inflater = (LayoutInflater)
						getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.comment_list_item, parent, false);
				holder.tvCommentName = (CheckedTextView) convertView.findViewById(R.id.chkCommentName);
				holder.tvCommentPrice = (TextView) convertView.findViewById(R.id.tvCommentPrice);
				holder.tvCommentQty = (TextView) convertView.findViewById(R.id.tvCommentQty);
				holder.btnCommentMinus = (Button) convertView.findViewById(R.id.btnCommentMinus);
				holder.btnCommentPlus = (Button) convertView.findViewById(R.id.btnCommentPlus);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			final Comment comment = mCommentLst.get(position);
			// get order comment if this comment already add to db
			// and update to view
			Comment orderComment = mTrans.getOrderComment(
					mTransactionId, mOrderDetailId, comment.getCommentId());
			// check added this comment
			if(orderComment.getCommentId() != 0){
				comment.setCommentQty(orderComment.getCommentQty());
				comment.setSelected(true);
				holder.btnCommentMinus.setEnabled(true);
				holder.btnCommentPlus.setEnabled(true);
			}else{
				holder.btnCommentMinus.setEnabled(false);
				holder.btnCommentPlus.setEnabled(false);
			}
			holder.tvCommentName.setChecked(comment.isSelected());
			holder.tvCommentName.setText(String.valueOf((position + 1)) + ". ");
			holder.tvCommentName.setText(comment.getCommentName());
			holder.tvCommentPrice.setText(mFormat.currencyFormat(comment.getCommentPrice()));
			holder.tvCommentQty.setText(mFormat.qtyFormat(comment.getCommentQty() == 0 ? 1 : comment.getCommentQty()));
			if(comment.getCommentPrice() > 0){
				holder.btnCommentMinus.setVisibility(View.VISIBLE);
				holder.btnCommentPlus.setVisibility(View.VISIBLE);
				holder.tvCommentQty.setVisibility(View.VISIBLE);
				holder.tvCommentPrice.setVisibility(View.VISIBLE);
			}else{
				holder.btnCommentMinus.setVisibility(View.GONE);
				holder.btnCommentPlus.setVisibility(View.GONE);
				holder.tvCommentQty.setVisibility(View.GONE);
				holder.tvCommentPrice.setVisibility(View.GONE);
			}
			holder.btnCommentMinus.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					double qty = comment.getCommentQty() == 0 ? 1 : comment.getCommentQty();
					if(--qty > 0){
						comment.setCommentQty(qty);
						mTrans.updateOrderComment(mTransactionId, mOrderDetailId, 
								comment.getCommentId(), qty, comment.getCommentPrice());
						mCommentAdapter.notifyDataSetChanged();
					}
				}
				
			});
			holder.btnCommentPlus.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					double qty = comment.getCommentQty() == 0 ? 1 : comment.getCommentQty();
					comment.setCommentQty(++qty);
					mTrans.updateOrderComment(mTransactionId, mOrderDetailId, 
							comment.getCommentId(), qty, comment.getCommentPrice());
					mCommentAdapter.notifyDataSetChanged();
				}
				
			});
			convertView.setOnClickListener(new OnCommentClickListener(comment));
			return convertView;
		}
		
		/**
		 * @author j1tth4
		 * listener for add delete comment
		 */
		private class OnCommentClickListener implements OnClickListener{

			private Comment mComment;
			
			public OnCommentClickListener(Comment comment){
				mComment = comment;
			}
			
			@Override
			public void onClick(View v) {
				if(mComment.isSelected()){
					mComment.setSelected(false);
					mTrans.deleteOrderComment(mTransactionId, mOrderDetailId, mComment.getCommentId());
				}else{
					double price = mComment.getCommentPrice() < 0 ? 0 : mComment.getCommentPrice();
					int proTypeId = price > 0 ? ProductsDao.COMMENT_HAVE_PRICE : ProductsDao.COMMENT_NOT_HAVE_PRICE;
					mComment.setSelected(true);
					mTrans.addOrderComment(mTransactionId, mComputerId, mOrderDetailId, mComment.getCommentId(), 
							proTypeId, 1, price);
				}
				mCommentAdapter.notifyDataSetChanged();	
			}
			
		}
		
		private class ViewHolder{
			CheckedTextView tvCommentName;
			TextView tvCommentPrice;
			TextView tvCommentQty;
			Button btnCommentMinus;
			Button btnCommentPlus;
		}	
	}
	
	public static interface OnCommentDismissListener{
		void onDismiss(int position, int orderDetailId);
	}
}

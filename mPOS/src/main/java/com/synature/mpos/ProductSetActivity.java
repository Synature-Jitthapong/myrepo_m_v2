package com.synature.mpos;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.synature.mpos.database.GlobalPropertyDao;
import com.synature.mpos.database.ProductsDao;
import com.synature.mpos.database.TransactionDao;
import com.synature.mpos.database.model.OrderSet;
import com.synature.mpos.database.model.OrderSet.OrderSetDetail;
import com.synature.mpos.database.model.Product;
import com.synature.mpos.database.model.ProductComponent;
import com.synature.mpos.database.model.ProductComponentGroup;
import com.synature.util.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class ProductSetActivity extends Activity{

	public static final String EDIT_MODE = "edit";
	
	public static final String ADD_MODE = "add";
	
	private ProductsDao mProduct;
	private GlobalPropertyDao mFormat;
	
	private TransactionDao mTrans;
	
	private int mTransactionId;
	private int mComputerId;
	private int mProductId;
	private int mOrderDetailId;
	private String mSetGroupName;
	
	private List<ProductComponent> mProductCompLst;
	private List<OrderSet> mOrderSetLst;
	private OrderSetAdapter mOrderSetAdapter;
	
	private ExpandableListView mLvOrderSet;
	private GridView mGvSetItem;
	private HorizontalScrollView mScroll;
	
	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_product_set);

		mLvOrderSet = (ExpandableListView) findViewById(R.id.lvOrderSet);
		mGvSetItem = (GridView) findViewById(R.id.gvSetItem);
		mScroll = (HorizontalScrollView) findViewById(R.id.horizontalScrollView1);
		
		mProduct = new ProductsDao(ProductSetActivity.this);
		mFormat = new GlobalPropertyDao(ProductSetActivity.this);
		mTrans = new TransactionDao(ProductSetActivity.this);
		mTrans.getWritableDatabase().beginTransaction();
		
		Intent intent = getIntent();
		mTransactionId = intent.getIntExtra("transactionId", 0);
		mComputerId = intent.getIntExtra("computerId", 0);
		mProductId = intent.getIntExtra("productId", 0);
		mSetGroupName = intent.getStringExtra("setGroupName");
		if(mSetGroupName != null)
			setTitle(mSetGroupName);
		
		if(intent.getStringExtra("mode").equals(ADD_MODE)){
			final Product p = mProduct.getProduct(mProductId);
			if(p.getProductPrice() > -1){
				mOrderDetailId = mTrans.addOrderDetail(mTransactionId, 
						mComputerId, mProductId, p.getProductTypeId(), 
						p.getVatType(), p.getVatRate(), 1, p.getProductPrice());
			}else{
				final EditText txtProductPrice = new EditText(this);
				txtProductPrice.setInputType(InputType.TYPE_CLASS_NUMBER);
				txtProductPrice.setOnEditorActionListener(new OnEditorActionListener(){
			
					@Override
					public boolean onEditorAction(TextView v, int actionId,
							KeyEvent event) {
						// TODO Auto-generated method stub
						return false;
					}
					
				});
				new AlertDialog.Builder(this)
				.setTitle(R.string.enter_price)
				.setView(txtProductPrice)
				.setCancelable(false)
				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener(){
			
					@Override
					public void onClick(DialogInterface dialog, int which) {
						cancelOrderSet();
					}
					
				})
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						double openPrice = 0;
						try {
							openPrice = Utils.stringToDouble(txtProductPrice.getText().toString());
							mOrderDetailId = mTrans.addOrderDetail(mTransactionId, 
									mComputerId, p.getProductId(), p.getProductTypeId(), 
									p.getVatType(), p.getVatRate(), 1, openPrice);
						} catch (ParseException e) {
							new AlertDialog.Builder(ProductSetActivity.this)
							.setTitle(R.string.enter_price)
							.setMessage(R.string.enter_valid_numeric)
							.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {	
								}
							})
							.show();
							e.printStackTrace();
						}
					}
				})
				.show();
			}	
		}else if(intent.getStringExtra("mode").equals(EDIT_MODE)){
			mOrderDetailId = intent.getIntExtra("orderDetailId", 0);
		}

		setupSetGroupButton();
		loadOrderSet();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.product_set, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			cancelOrderSet();
			return true;
		case R.id.itemConfirm:
			confirmOrderSet();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private double updateBadge(int groupId, double requireAmount, double requireMinAmount){
		double totalQty = mTrans.getOrderSetTotalQty(mTransactionId, mOrderDetailId, groupId);
		try {
			LinearLayout scrollContent = getScrollContainer();
			View groupBtn = scrollContent.findViewById(groupId);
			TextView tvBadge = (TextView) groupBtn.findViewById(R.id.tvReqAmount);
			double reductQty = requireAmount - totalQty;
			tvBadge.setText(mFormat.qtyFormat(reductQty));
			if(requireMinAmount > 0){
				tvBadge.setText(mFormat.qtyFormat(requireMinAmount) + "-" + 
						mFormat.qtyFormat(reductQty));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return totalQty;
	}
	
	@SuppressLint("NewApi")
	private void setupSetGroupButton(){
		boolean isHasGroup0 = false;
		List<ProductComponentGroup> productCompGroupLst;
		productCompGroupLst = mProduct.listProductComponentGroup(mProductId);
		if(productCompGroupLst != null){
			final LinearLayout scrollContent = getScrollContainer();
			for(int i = 0; i < productCompGroupLst.size(); i++){
				final ProductComponentGroup pCompGroup = productCompGroupLst.get(i);
				LayoutInflater inflater = (LayoutInflater)
						getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View setGroupView = inflater.inflate(R.layout.set_group_button_layout, null, false);
				setGroupView.setId(pCompGroup.getProductGroupId());
				TextView tvGroupName = (TextView) setGroupView.findViewById(R.id.tvSetGName);
				TextView tvBadge = (TextView) setGroupView.findViewById(R.id.tvReqAmount);
				tvGroupName.setText(pCompGroup.getGroupName());
				
				if(pCompGroup.getGroupNo() == 0){
					isHasGroup0 = true;
					setGroupView.setVisibility(View.GONE);
					if(mTrans.checkAddedOrderSet(mTransactionId, 
							mOrderDetailId, pCompGroup.getProductGroupId()) == 0){
						List<ProductComponent> pCompLst = 
								mProduct.listProductComponent(pCompGroup.getProductGroupId());
						if(pCompLst != null){
							for(ProductComponent pComp : pCompLst){
								double qty = pComp.getChildProductAmount() > 0 ? pComp.getChildProductAmount() : 1;
								double price = pComp.getFlexibleProductPrice() > 0 ? pComp.getFlexibleProductPrice() : 0.0d;
								addOrderSet(pCompGroup.getProductGroupId(), pComp.getProductId(), 
										qty, price, pCompGroup.getRequireAmount(), 
										pCompGroup.getRequireMinAmount(), pCompGroup.getGroupName(), pComp.getProductName());
							}
						}
					}
				}else{
					setGroupView.setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							mProductCompLst = mProduct.listProductComponent(pCompGroup.getProductGroupId());
							
							// I use Products.ProductGroupId instead ProductComponent.PGroupId
							SetItemAdapter adapter = new SetItemAdapter(
									pCompGroup.getProductGroupId(), 
									pCompGroup.getGroupName(),
									pCompGroup.getRequireAmount(), 
									pCompGroup.getRequireMinAmount());
							mGvSetItem.setAdapter(adapter);

							v.setSelected(true);
							for(int j = 0; j < scrollContent.getChildCount(); j++){
								View child = scrollContent.getChildAt(j);
								if(child.getId() != pCompGroup.getProductGroupId()){
									child.setSelected(false);
								}
							}
						}
						
					});	
					if(i == 0 || isHasGroup0){
						isHasGroup0 = false;
						try {
							setGroupView.callOnClick();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if(pCompGroup.getRequireAmount() > 0){
					tvBadge.setVisibility(View.VISIBLE);
				}else{
					tvBadge.setVisibility(View.GONE);
				}
				scrollContent.addView(setGroupView, 
						new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
				
				if(pCompGroup.getRequireAmount() > 0){
					updateBadge(pCompGroup.getProductGroupId(), pCompGroup.getRequireAmount(), pCompGroup.getRequireMinAmount());
				}
			}
		}
	}
	
	private LinearLayout getScrollContainer(){
		return (LinearLayout) mScroll.findViewById(R.id.LinearLayout1);
	}
	
	/**
	 * load order set
	 */
	private void loadOrderSet(){
		mOrderSetLst = mTrans.listOrderSet(mTransactionId, mOrderDetailId); 
		if(mOrderSetLst == null){
			mOrderSetLst = new ArrayList<OrderSet>();
		}
		if(mOrderSetAdapter == null){
			mOrderSetAdapter = new OrderSetAdapter();
			mLvOrderSet.setAdapter(mOrderSetAdapter);
			mLvOrderSet.setGroupIndicator(null);
		}
		mOrderSetAdapter.notifyDataSetChanged();
		expandOrderSetLv(0);
		scrollOrderSetLv(mOrderSetAdapter.getGroupCount() - 1);
	}
	
	private void scrollOrderSetLv(final int position){
		mLvOrderSet.post(new Runnable(){

			@Override
			public void run() {
				mLvOrderSet.setSelectedGroup(position);
			}
			
		});
	}
	
	private void expandOrderSetLv(int position){
		if(position == 0){
			for(int i = 0; i < mOrderSetAdapter.getGroupCount(); i++){
				if(!mLvOrderSet.isGroupExpanded(i))
					mLvOrderSet.expandGroup(i);
			}
		}else{
			if(!mLvOrderSet.isGroupExpanded(position))
				mLvOrderSet.expandGroup(position);
		}
	}
	
	/**
	 * 
	 */
	private void confirmOrderSet(){
		List<ProductComponentGroup> productCompGroupLst 
			= mProduct.listProductComponentGroup(mProductId);
		boolean canDone = true;
		String selectGroup = "";
		if(productCompGroupLst != null){
			Iterator<ProductComponentGroup> it = productCompGroupLst.iterator();
			while(it.hasNext()){
				ProductComponentGroup pCompGroup = it.next();
				if(pCompGroup.getRequireAmount() > 0){
					double totalSetQty = mTrans.getOrderSetTotalQty(mTransactionId, mOrderDetailId, 
							pCompGroup.getProductGroupId());
					if(pCompGroup.getRequireMinAmount() == 0){
						if(pCompGroup.getRequireAmount() - totalSetQty > 0){
							canDone = false;
							selectGroup = pCompGroup.getGroupName();
							break;
						}
					}else{
						if(totalSetQty < pCompGroup.getRequireMinAmount()){
							canDone = false;
							selectGroup = pCompGroup.getGroupName();
							break;
						}
					}
				}
			}
		}
		
		if(canDone){
			mTrans.getWritableDatabase().setTransactionSuccessful();
			mTrans.getWritableDatabase().endTransaction();
			// set result for show on display
			Product p = mProduct.getProduct(mProductId);
			Intent intent = new Intent();
			intent.putExtra("setName", p.getProductName());
			intent.putExtra("setPrice", mFormat.currencyFormat(p.getProductPrice()));
			setResult(RESULT_OK, intent);
			finish();
		}else{
			new AlertDialog.Builder(ProductSetActivity.this)
			.setTitle(R.string.set_menu)
			.setMessage(ProductSetActivity.this.getString(R.string.please_select) + " " + selectGroup)
			.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
		}
	}
	
	private void cancelOrderSet(){
		if(getIntent().getStringExtra("mode").equals(ADD_MODE)){
			mTrans.deleteOrder(mTransactionId, mOrderDetailId);
			mTrans.getWritableDatabase().setTransactionSuccessful();
		}
		mTrans.getWritableDatabase().endTransaction();
		finish();
	}
	
	/**
	 * @author j1tth4
	 * Order Set Adapter
	 */
	private class OrderSetAdapter extends BaseExpandableListAdapter{

		@Override
		public int getGroupCount() {
			return mOrderSetLst != null ? mOrderSetLst.size() : 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			int count = mOrderSetLst.get(groupPosition).getOrderSetDetail() != null ? mOrderSetLst.get(groupPosition).getOrderSetDetail().size() : 0;
			return count; 
		}

		@Override
		public Object getGroup(int groupPosition) {
			return mOrderSetLst.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			return mOrderSetLst.get(groupPosition).getOrderSetDetail().get(childPosition);
		}

		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return 0;
		}

		@Override
		public boolean hasStableIds() {
			return false;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ViewSetGroupHolder holder;
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater)
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.order_set_template, parent, false);
				holder = new ViewSetGroupHolder();
				holder.tvSetGroupName = (TextView) convertView.findViewById(R.id.tvSetGroupName);
				holder.btnDel = (ImageButton) convertView.findViewById(R.id.btnSetGroupDel);
				convertView.setTag(holder);
			}else{
				holder = (ViewSetGroupHolder) convertView.getTag();
			}
			final OrderSet set = mOrderSetLst.get(groupPosition);
			holder.tvSetGroupName.setText(set.getSetGroupName());
			if(set.getSetGroupNo() == 0){
				holder.btnDel.setVisibility(View.INVISIBLE);
			}else{
				holder.btnDel.setVisibility(View.VISIBLE);
				holder.btnDel.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View v) {
						new AlertDialog.Builder(ProductSetActivity.this)
						.setTitle(R.string.delete)
						.setMessage(R.string.confirm_delete_item)
						.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						}).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
	
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								mTrans.deleteOrderSetByGroup(mTransactionId, 
										mOrderDetailId, set.getSetGroupId());
								updateBadge(set.getSetGroupId(), set.getReqAmount(), set.getReqMinAmount());
								loadOrderSet();
							}
							
						}).show();
					}
					
				});
			}
			return convertView;
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ViewSetDetailHolder holder;
			if(convertView == null){
				LayoutInflater inflater = (LayoutInflater)
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.order_set_detail_item, parent, false);
				holder = new ViewSetDetailHolder();
				holder.tvSetNo = (TextView) convertView.findViewById(R.id.tvSetNo);
				holder.tvSetName = (TextView) convertView.findViewById(R.id.tvSetName);
				holder.tvSetPrice = (TextView) convertView.findViewById(R.id.tvSetPrice);
				holder.tvSetQty = (TextView) convertView.findViewById(R.id.tvSetQty);
				holder.btnSetMinus = (Button) convertView.findViewById(R.id.btnSetMinus);
				holder.btnSetPlus = (Button) convertView.findViewById(R.id.btnSetPlus);
				convertView.setTag(holder);
			}else{
				holder = (ViewSetDetailHolder) convertView.getTag();
			}
			final OrderSet setGroup = mOrderSetLst.get(groupPosition);
			final OrderSetDetail detail = mOrderSetLst.get(groupPosition).getOrderSetDetail().get(childPosition);
			holder.tvSetNo.setText(String.valueOf(childPosition + 1) + ".");
			holder.tvSetName.setText(detail.getProductName());
			holder.tvSetQty.setText(mFormat.qtyFormat(detail.getOrderSetQty()));
			holder.tvSetPrice.setText(detail.getProductPrice() > 0 ? 
					mFormat.currencyFormat(detail.getProductPrice()) : null);
			if(setGroup.getSetGroupNo() == 0){
				holder.btnSetMinus.setVisibility(View.INVISIBLE);
				holder.btnSetPlus.setVisibility(View.INVISIBLE);
			}else{
				holder.btnSetMinus.setVisibility(View.VISIBLE);
				holder.btnSetPlus.setVisibility(View.VISIBLE);
				holder.btnSetMinus.setOnClickListener(new OnClickListener(){
	
					@Override
					public void onClick(View v) {
						double qty = detail.getOrderSetQty();
						double deductAmount = detail.getDeductAmount();
						if(qty > 0){
							qty -= deductAmount;
							if(qty == 0){
								new AlertDialog.Builder(ProductSetActivity.this)
								.setTitle(R.string.delete)
								.setMessage(R.string.confirm_delete_item)
								.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								}).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										mTrans.deleteOrderSet(mTransactionId, mOrderDetailId, detail.getOrderSetId());
										updateBadge(setGroup.getSetGroupId(), setGroup.getReqAmount(), setGroup.getReqMinAmount());
										loadOrderSet();
									}
								}).show();
							}else{
								detail.setOrderSetQty(qty);
								mTrans.updateOrderSet(mTransactionId,mOrderDetailId, detail.getOrderSetId(), detail.getProductId(), qty);
								updateBadge(setGroup.getSetGroupId(), setGroup.getReqAmount(), setGroup.getReqMinAmount());
								mOrderSetAdapter.notifyDataSetChanged();
							}
						}
					}
					
				});
				holder.btnSetPlus.setOnClickListener(new OnClickListener(){
	
					@Override
					public void onClick(View v) {
						double qty = detail.getOrderSetQty();
						double deductAmount = detail.getDeductAmount();
						if(setGroup.getReqAmount() > 0){
							// count total group qty from db
							double totalQty = mTrans.getOrderSetTotalQty(
									mTransactionId, mOrderDetailId, setGroup.getSetGroupId());
							if(totalQty < setGroup.getReqAmount()){
								qty += deductAmount;
								if(deductAmount <= setGroup.getReqAmount() - totalQty){
									detail.setOrderSetQty(qty);
									mTrans.updateOrderSet(mTransactionId, mOrderDetailId, detail.getOrderSetId(), detail.getProductId(), qty);
									updateBadge(setGroup.getSetGroupId(), setGroup.getReqAmount(), setGroup.getReqMinAmount());
									mOrderSetAdapter.notifyDataSetChanged();
								}else{
									new AlertDialog.Builder(ProductSetActivity.this)
									.setTitle(setGroup.getSetGroupName())
									.setMessage(getString(R.string.cannot_update) + " " 
											+ detail.getProductName() + " " + getString(R.string.because_deduct_at) + " " 
											+ mFormat.qtyFormat(detail.getDeductAmount()))
									.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
										
										@Override
										public void onClick(DialogInterface arg0, int arg1) {
										}
									}).show();
								}
							}
						}else{
							qty += deductAmount;
							detail.setOrderSetQty(qty);
							mTrans.updateOrderSet(mTransactionId, mOrderDetailId, detail.getOrderSetId(), detail.getProductId(), qty);
							updateBadge(setGroup.getSetGroupId(), setGroup.getReqAmount(), setGroup.getReqMinAmount());
							mOrderSetAdapter.notifyDataSetChanged();
						}
					}
				});
			}
			return convertView;
		}

		@Override
		public boolean isChildSelectable(int groupPosition,
				int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

		private class ViewSetGroupHolder{
			TextView tvSetGroupName;
			ImageButton btnDel;
		}
		
		private class ViewSetDetailHolder{
			TextView tvSetNo;
			TextView tvSetName;
			TextView tvSetPrice;
			TextView tvSetQty;
			Button btnSetMinus;
			Button btnSetPlus;
		}
	}
	
	/**
	 * @author j1tth4
	 * set menu item adapter
	 */
	public class SetItemAdapter extends BaseAdapter{
		
		private int mPcompGroupId;
		private String mGroupName;
		private double mRequireAmount;
		private double mRequireMinAmount;
		
		private LayoutInflater mInflater;
		
		private ImageLoader mImgLoader;
		
		/**
		 * @param pcompGroupId
		 * @param requireAmount
		 */
		public SetItemAdapter(int pcompGroupId, String groupName, double requireAmount, double requireMinAmount){
			mPcompGroupId = pcompGroupId;
			mGroupName = groupName;
			mRequireAmount = requireAmount;
			mRequireMinAmount = requireMinAmount;
			
			mImgLoader = new ImageLoader(ProductSetActivity.this, 0,
					MPOSApplication.IMG_DIR, ImageLoader.IMAGE_SIZE.MEDIUM);

			mInflater = (LayoutInflater)
					getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mProductCompLst != null ? mProductCompLst.size() : 0;
		}

		@Override
		public ProductComponent getItem(int position) {
			return mProductCompLst.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final SetItemViewHolder holder;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.set_item_template, parent, false);
				holder = new SetItemViewHolder();
				holder.tvMenu = (TextView) convertView.findViewById(R.id.textViewMenuName);
				holder.tvDeductAmount = (TextView) convertView.findViewById(R.id.tvDeAmount);
				holder.tvPrice = (TextView) convertView.findViewById(R.id.textViewMenuPrice);
				holder.imgMenu = (ImageView) convertView.findViewById(R.id.imageViewMenu);
				convertView.setTag(holder);
			}else{
				holder = (SetItemViewHolder) convertView.getTag();
			}
			
			final ProductComponent pComp = mProductCompLst.get(position);
			holder.tvMenu.setText(pComp.getProductName());
			holder.tvPrice.setText(mFormat.currencyFormat(pComp.getFlexibleProductPrice()));
			if(pComp.getFlexibleProductPrice() > 0){
				holder.tvPrice.setVisibility(View.VISIBLE);
			}else{
				holder.tvPrice.setVisibility(View.INVISIBLE);
			}
			if(pComp.getChildProductAmount() > 0 && pComp.getChildProductAmount() != 1){
				holder.tvDeductAmount.setText(mFormat.qtyFormat(pComp.getChildProductAmount()));
				holder.tvDeductAmount.setVisibility(View.VISIBLE);
			}else{
				holder.tvDeductAmount.setVisibility(View.INVISIBLE);
			}
			if(Utils.isShowMenuImage(ProductSetActivity.this)){
				holder.tvMenu.setLines(2);
				holder.tvMenu.setTextSize(14);
				holder.imgMenu.setVisibility(View.VISIBLE);
				holder.imgMenu.setImageBitmap(null);
				mImgLoader.displayImage(Utils.getImageUrl(ProductSetActivity.this) + pComp.getImgName(), holder.imgMenu);
			}else{
				holder.tvMenu.setLines(4);
				holder.tvMenu.setTextSize(getResources().getDimension(R.dimen.menu_text_large));
				holder.imgMenu.setVisibility(View.GONE);
			}
			
			convertView.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View v) {
					// childProductAmount is weight amount of set
					double qty = pComp.getChildProductAmount() > 0 ? pComp.getChildProductAmount() : 1;
					double price = pComp.getFlexibleIncludePrice() == 1 ? pComp.getFlexibleProductPrice() : 0;
					addOrderSet(mPcompGroupId, pComp.getProductId(), qty, price, mRequireAmount, mRequireMinAmount,
							mGroupName, pComp.getProductName());
					loadOrderSet();
				}
				
			});
			return convertView;
		}
		
		private class SetItemViewHolder extends MenuItemViewHolder{
			TextView tvDeductAmount;
		}
	}
	
	/**
	 * @param pCompGroupId
	 * @param productId
	 * @param deductQty
	 * @param price
	 * @param requireAmount
	 * @param requireMinAmount
	 * @param groupName
	 * @param productName
	 * @return orderSetId
	 */
	private int addOrderSet(int pCompGroupId, int productId, double deductQty, double price, double requireAmount,
			 double requireMinAmount, String groupName, String productName){
		int orderSetId = 0;
		if(requireAmount > 0){
			// count total group qty from db
			double totalQty = mTrans.getOrderSetTotalQty(mTransactionId, mOrderDetailId, pCompGroupId);
			
			if(totalQty < requireAmount){
				if(deductQty <= requireAmount - totalQty){
					orderSetId = mTrans.addOrderSet(mTransactionId, mComputerId, mOrderDetailId, productId, 
							ProductsDao.CHILD_OF_SET_HAVE_PRICE, deductQty, price, pCompGroupId, requireAmount, requireMinAmount);
				}else{
					new AlertDialog.Builder(ProductSetActivity.this)
					.setTitle(groupName)
					.setMessage(getString(R.string.cannot_add) + " " 
							+ productName + " " + getString(R.string.because_deduct_at) + " " 
							+ mFormat.qtyFormat(deductQty))
					.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
						}
					}).show();
					
				}
				totalQty = updateBadge(pCompGroupId, requireAmount, requireMinAmount);
				if(totalQty >= requireAmount)
					// select next group
					selectNextGroup();
			}
		}else{
			orderSetId = mTrans.addOrderSet(mTransactionId, mComputerId, mOrderDetailId, productId, 
					ProductsDao.CHILD_OF_SET_HAVE_PRICE, deductQty, price, pCompGroupId, requireAmount, requireMinAmount);
		}
		return orderSetId;
	}
	
	@SuppressLint("NewApi")
	private void selectNextGroup(){
		LinearLayout scroll = getScrollContainer();
		for(int i = 0; i < scroll.getChildCount(); i++){
			View child = scroll.getChildAt(i);
			if(child.isSelected()){
				if(i < scroll.getChildCount() - 1){
					child = scroll.getChildAt(i + 1);
					child.callOnClick();
				}
				return;
			}
		}
	}
}

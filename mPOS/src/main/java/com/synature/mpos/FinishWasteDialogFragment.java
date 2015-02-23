package com.synature.mpos;

import com.synature.mpos.database.GlobalPropertyDao;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class FinishWasteDialogFragment extends DialogFragment implements OnClickListener{

	public static final String TAG = "FinishWasteDialogFragment";
	
	private GlobalPropertyDao mGlobal;
	
	private OnFinishWasteListener mListener;
	
	private int mPayTypeId;
	private int mDocTypeId;
	private String mDocTypeHeader;
	private String mPayTypeName;
	private double mTotalPrice;
	
	private TextView mTvTitle;
	private TextView mTvTotalPrice;
	private EditText mTxtRemark;
	private Button mBtnConfirm;
	private Button mBtnCancel;
	
	public static FinishWasteDialogFragment newInstance(int payTypeId, String payTypeName,
			int docTypeId, String docTypeHeader, double totalPrice){
		FinishWasteDialogFragment f = new FinishWasteDialogFragment();
		Bundle b = new Bundle();
		b.putInt("payTypeId", payTypeId);
		b.putInt("docTypeId", docTypeId);
		b.putString("docTypeHeader", docTypeHeader);
		b.putString("payTypeName", payTypeName);
		b.putDouble("totalPrice", totalPrice);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mGlobal = new GlobalPropertyDao(getActivity());
		
		mPayTypeId = getArguments().getInt("payTypeId");
		mDocTypeId = getArguments().getInt("docTypeId");
		mDocTypeHeader = getArguments().getString("docTypeHeader");
		mPayTypeName = getArguments().getString("payTypeName");
		mTotalPrice = getArguments().getDouble("totalPrice");
		
		setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnFinishWasteListener){
			mListener = (OnFinishWasteListener) activity;
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.finish_waste_layout, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		mTvTitle = (TextView) view.findViewById(R.id.tvTitle);
		mTvTotalPrice = (TextView) view.findViewById(R.id.tvTotalPrice);
		mTxtRemark = (EditText) view.findViewById(R.id.txtRemark);
		mBtnConfirm = (Button) view.findViewById(R.id.btnConfirm);
		mBtnCancel = (Button) view.findViewById(R.id.btnCancel);
		mBtnConfirm.setOnClickListener(this);
		mBtnCancel.setOnClickListener(this);
		
		mTvTitle.setText(mPayTypeName);
		mTvTotalPrice.setText(mGlobal.currencyFormat(mTotalPrice));
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btnConfirm:
			String remark = TextUtils.isEmpty(mTxtRemark.getText().toString()) ? "" : mTxtRemark.getText().toString();
			mListener.onWasteConfirm(mPayTypeId, mDocTypeId, mDocTypeHeader, mTotalPrice, remark);
			dismiss();
			break;
		case R.id.btnCancel:
			mListener.onWasteCancel();
			dismiss();
			break;
		}
	}

	public static interface OnFinishWasteListener{
		void onWasteCancel();
		void onWasteConfirm(int payTypeId, int docTypeId, String docTypeHeader, double totalPrice, String remark);
	}
}

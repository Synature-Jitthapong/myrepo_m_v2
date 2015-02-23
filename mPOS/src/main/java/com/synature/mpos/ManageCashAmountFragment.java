package com.synature.mpos;

import java.text.ParseException;

import com.synature.mpos.database.GlobalPropertyDao;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ManageCashAmountFragment extends DialogFragment implements OnClickListener{

	public static final String TAG = ManageCashAmountFragment.class.getSimpleName();
	public static final int OPEN_SHIFT_MODE = 0;
	public static final int CLOSE_SHIFT_MODE = 1;
	public static final int END_DAY_MODE = 2;
	public static final int EDIT_CASH_MODE = 3;
	
	private GlobalPropertyDao mFormat;
	private int mMode;
	private double mTotalCash;
	private String mTitle;
	private StringBuilder mStrCashAmount;
	
	private EditText mTxtCash;
	private OnManageCashAmountDismissListener mDismissListener;
	
	public static ManageCashAmountFragment newInstance(String title, double totalCash, int mode){
		ManageCashAmountFragment f = new ManageCashAmountFragment();
		Bundle b = new Bundle();
		b.putString("title", title);
		b.putDouble("totalCash", totalCash);
		b.putInt("mode", mode);
		f.setArguments(b);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setCancelable(false);
		
		mTitle = getArguments().getString("title");
		mMode = getArguments().getInt("mode");
		mTotalCash = getArguments().getDouble("totalCash", 0);
		mFormat = new GlobalPropertyDao(getActivity());
		mStrCashAmount = new StringBuilder();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnManageCashAmountDismissListener){
			mDismissListener = (OnManageCashAmountDismissListener) activity;
		}
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		mTxtCash.setText(mFormat.currencyFormat(mTotalCash));
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View contentView = inflater.inflate(R.layout.fragment_cash_amount, null);
		contentView.setPadding(16, 16, 16, 16);
		Button btn0 = (Button) contentView.findViewById(R.id.btn0);
		Button btn1 = (Button) contentView.findViewById(R.id.btn1);
		Button btn2 = (Button) contentView.findViewById(R.id.btn2);
		Button btn3 = (Button) contentView.findViewById(R.id.btn3);
		Button btn4 = (Button) contentView.findViewById(R.id.btn4);
		Button btn5 = (Button) contentView.findViewById(R.id.btn5);
		Button btn6 = (Button) contentView.findViewById(R.id.btn6);
		Button btn7 = (Button) contentView.findViewById(R.id.btn7);
		Button btn8 = (Button) contentView.findViewById(R.id.btn8);
		Button btn9 = (Button) contentView.findViewById(R.id.btn9);
		Button btnClear = (Button) contentView.findViewById(R.id.btnClear);
		Button btnDel = (Button) contentView.findViewById(R.id.btnDel);
		Button btnDot = (Button) contentView.findViewById(R.id.btnDot);
		Button btnEnter = (Button) contentView.findViewById(R.id.btnEnter);
		btnEnter.setVisibility(View.INVISIBLE);
		btn0.setOnClickListener(this);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		btn5.setOnClickListener(this);
		btn6.setOnClickListener(this);
		btn7.setOnClickListener(this);
		btn8.setOnClickListener(this);
		btn9.setOnClickListener(this);
		btnClear.setOnClickListener(this);
		btnDot.setOnClickListener(this);
		btnDel.setOnClickListener(this);
		
		mTxtCash = (EditText) contentView.findViewById(R.id.txtDisplay);
		mTxtCash.setTextSize(getActivity().getResources().getInteger(R.integer.larger_text_size));
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(mTitle);
		builder.setView(contentView);
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(mMode){
				case OPEN_SHIFT_MODE:
					mDismissListener.onOpenShift(mTotalCash);
					break;
				case CLOSE_SHIFT_MODE:
					mDismissListener.onCloseShift(mTotalCash);
					break;
				case END_DAY_MODE:
					mDismissListener.onEndday(mTotalCash);
					break;
				case EDIT_CASH_MODE:
					mDismissListener.onEditCashAmount(mTotalCash);
					break;
				}
			}
		});
		return builder.create();
	}

	private void display(){
		try {
			mTotalCash = Utils.stringToDouble(mStrCashAmount.toString());
		} catch (ParseException e) {
			mTotalCash = 0;
			Log.d(TAG, e.getMessage());
		}
		mTxtCash.setText(mFormat.currencyFormat(mTotalCash));
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn0:
			mStrCashAmount.append("0");
			display();
			break;
		case R.id.btn1:
			mStrCashAmount.append("1");
			display();
			break;
		case R.id.btn2:
			mStrCashAmount.append("2");
			display();
			break;
		case R.id.btn3:
			mStrCashAmount.append("3");
			display();
			break;
		case R.id.btn4:
			mStrCashAmount.append("4");
			display();
			break;
		case R.id.btn5:
			mStrCashAmount.append("5");
			display();
			break;
		case R.id.btn6:
			mStrCashAmount.append("6");
			display();
			break;
		case R.id.btn7:
			mStrCashAmount.append("7");
			display();
			break;
		case R.id.btn8:
			mStrCashAmount.append("8");
			display();
			break;
		case R.id.btn9:
			mStrCashAmount.append("9");
			display();
			break;
		case R.id.btnClear:
			mStrCashAmount = new StringBuilder();
			display();
			break;
		case R.id.btnDel:
			try {
				mStrCashAmount.deleteCharAt(mStrCashAmount.length() - 1);
			} catch (Exception e) {
				mStrCashAmount = new StringBuilder();
			}
			display();
			break;
		case R.id.btnDot:
			mStrCashAmount.append(".");
			display();
			break;
		}
	}

	public static interface OnManageCashAmountDismissListener{
		void onOpenShift(double cashAmount);
		void onCloseShift(double cashAmount);
		void onEndday(double cashAmount);
		void onEditCashAmount(double cashAmount);
	}
}

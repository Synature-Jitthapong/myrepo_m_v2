package com.synature.mpos;

import com.synature.mpos.database.GlobalPropertyDao;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class OrderingKeypadFragment extends Fragment implements OnClickListener{

	public static final String TAG = OrderingKeypadFragment.class.getSimpleName();
	
	private GlobalPropertyDao mGlobal;
	private StringBuilder mStrVal;
	private int mTotalQty;
	
	private EditText mTxtDsp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mGlobal = new GlobalPropertyDao(getActivity());
		mStrVal = new StringBuilder();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.keypad_ordering_utils_fragment, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mTxtDsp = (EditText) getActivity().findViewById(R.id.editText1);
		
		Button b0 = (Button) view.findViewById(R.id.b0);
		Button b1 = (Button) view.findViewById(R.id.b1);
		Button b2 = (Button) view.findViewById(R.id.b2);
		Button b3 = (Button) view.findViewById(R.id.b3);
		Button b4 = (Button) view.findViewById(R.id.b4);
		Button b5 = (Button) view.findViewById(R.id.b5);
		Button b6 = (Button) view.findViewById(R.id.b6);
		Button b7 = (Button) view.findViewById(R.id.b7);
		Button b8 = (Button) view.findViewById(R.id.b8);
		Button b9 = (Button) view.findViewById(R.id.b9);
		Button bC = (Button) view.findViewById(R.id.bC);
		
		b0.setOnClickListener(this);
		b1.setOnClickListener(this);
		b2.setOnClickListener(this);
		b3.setOnClickListener(this);
		b4.setOnClickListener(this);
		b5.setOnClickListener(this);
		b6.setOnClickListener(this);
		b7.setOnClickListener(this);
		b8.setOnClickListener(this);
		b9.setOnClickListener(this);
		bC.setOnClickListener(this);
	}
	
	public void resetTotalQty(){
		mTotalQty = 0;
		resetButton();
	}
	
	public int getTotalQty(){
		return mTotalQty;
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if(id != R.id.bC){
			Button btn = (Button) v;
			if(mStrVal.length() < 6){
				mStrVal.append(btn.getText().toString());
				setValue();
			}
		}else if(id == R.id.bC){
			resetTotalQty();
		}
	}
	
	private void display(){
		if(mTotalQty > 0)
			mTxtDsp.setText(mGlobal.qtyFormat(mTotalQty));
		else
			mTxtDsp.setText(null);
	}
	
	private void setValue(){
		try {
			mTotalQty = Integer.parseInt(mStrVal.toString());
		} catch (NumberFormatException e) {}	
		display();
	}
	
	private void resetButton(){
		mStrVal.setLength(0);
		mStrVal.trimToSize();
		display();
	}
}

package com.synature.mpos;

import java.util.Calendar;

import com.synature.mpos.datasource.GlobalPropertyDataSource;
import com.synature.mpos.datasource.SessionDataSource;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class ResetEnddayStateDialogFragment extends DialogFragment{
	
	public static final String TAG = ResetEnddayStateDialogFragment.class.getSimpleName();
	
	public static final String RESET_PASS = "mposreset";
	
	private boolean mIsEndday = false;
	
	public static ResetEnddayStateDialogFragment newInstance(){
		ResetEnddayStateDialogFragment f = new ResetEnddayStateDialogFragment();
		return f;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		GlobalPropertyDataSource global = new GlobalPropertyDataSource(getActivity());
		final SessionDataSource sess = new SessionDataSource(getActivity());
		final String currSaleDate = global.dateFormat(Calendar.getInstance().getTime());
		final String now = String.valueOf(Utils.getDate().getTimeInMillis());
		mIsEndday = sess.checkEndday(now);
		String cfMsg = getString(R.string.confirm_reset_endday);
		if(mIsEndday){
			cfMsg += " " + currSaleDate;
		}else{
			cfMsg = currSaleDate + " " + getString(R.string.have_not_endday);
		}
		View content = getActivity().getLayoutInflater().inflate(R.layout.edittext_password, null);
		final EditText txtPass = (EditText) content.findViewById(R.id.txtPassword);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.reset_endday);
		builder.setMessage(cfMsg);
		if(!mIsEndday){
			builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
		}else{
			builder.setView(content);
			builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.setPositiveButton(R.string.yes, null);
		}
		final AlertDialog dialog = builder.create();
		dialog.show();
		if(mIsEndday){
			dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener(){
	
				@Override
				public void onClick(View v) {
					String pass = txtPass.getText().toString();
					if(!TextUtils.isEmpty(pass)){
						if(pass.equals(RESET_PASS)){
							sess.resetEndday(now);
							new AlertDialog.Builder(getActivity())
							.setTitle(R.string.reset_endday)
							.setMessage(R.string.reset_endday_success)
							.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
							dialog.dismiss();
						}else{
							txtPass.setError(getString(R.string.incorrect_password));
						}
					}else{
						txtPass.setError(getString(R.string.enter_password));
					}
				}
				
			});
		}
		return dialog;
	}

}

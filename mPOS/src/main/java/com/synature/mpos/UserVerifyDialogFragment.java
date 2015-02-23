package com.synature.mpos;

import com.synature.mpos.datasource.StaffsDataSource;
import com.synature.mpos.datasource.UserVerification;
import com.synature.pos.Staff;

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
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

public class UserVerifyDialogFragment extends DialogFragment{
	
	public static final String TAG = UserVerifyDialogFragment.class.getSimpleName();
	
	public static final int GRANT_PERMISSION = 0;
	public static final String ROOT_PASS = "promisesystem";
	
	private int mPermissionId;
	private OnCheckPermissionListener mListener;
	
	private TextView mTvMsg;
	private EditText mTxtStaffCode;
	private EditText mTxtPass;
	
	public static UserVerifyDialogFragment newInstance(int permissId){
		UserVerifyDialogFragment f = new UserVerifyDialogFragment();
		Bundle b = new Bundle();
		b.putInt("permissId", permissId);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnCheckPermissionListener){
			mListener = (OnCheckPermissionListener) activity;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mPermissionId = getArguments().getInt("permissId");
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View content = inflater.inflate(R.layout.user_verify_layout, null);
		mTvMsg = (TextView) content.findViewById(R.id.textView1);
		mTxtStaffCode = (EditText) content.findViewById(R.id.txtStaffCode);
		mTxtPass = (EditText) content.findViewById(R.id.txtStaffPass);
		boolean grantPermission = mPermissionId == GRANT_PERMISSION;
		if(grantPermission){
			mTxtStaffCode.setVisibility(View.GONE);
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setCancelable(false);
		builder.setTitle(grantPermission ? R.string.login : R.string.permission_required);
		builder.setView(content);
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setPositiveButton(android.R.string.ok, null);
		final AlertDialog d = builder.show();
		d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String code = mTxtStaffCode.getText().toString();
				String pass = mTxtPass.getText().toString();
				if(mPermissionId == GRANT_PERMISSION){
					if(!TextUtils.isEmpty(pass)){
						if(pass.equals(ROOT_PASS)){
							d.dismiss();
							mListener.onAllow(1, GRANT_PERMISSION);
						}else{
							mTxtPass.setError(getString(R.string.incorrect_password));
						}
					}else{
						mTxtPass.setError(getString(R.string.enter_password));
					}
				}else{
					if(!TextUtils.isEmpty(code)){
						if(!TextUtils.isEmpty(pass)){
							UserVerification verify = new UserVerification(getActivity(), code, pass);
							if(verify.checkUser()){
								Staff s = verify.checkLogin();
								if(s != null){
									mTvMsg.setVisibility(View.GONE);
									StaffsDataSource st = new StaffsDataSource(getActivity());
									switch(mPermissionId){
									case StaffsDataSource.VOID_PERMISSION:
										if(st.checkVoidPermission(s.getStaffRoleID())){
											d.dismiss();
											mListener.onAllow(s.getStaffID(), StaffsDataSource.VOID_PERMISSION);
										}else{
											mTvMsg.setVisibility(View.VISIBLE);
											mTvMsg.setText(R.string.not_have_permission_to_void);
										}
										break;
									case StaffsDataSource.OTHER_DISCOUNT_PERMISSION:
										if(st.checkOtherDiscountPermission(s.getStaffRoleID())){
											d.dismiss();
											mListener.onAllow(s.getStaffID(), StaffsDataSource.OTHER_DISCOUNT_PERMISSION);
										}else{
											mTvMsg.setVisibility(View.VISIBLE);
											mTvMsg.setText(R.string.not_have_permission_to_other_discount);
										}
										break;
									case StaffsDataSource.VIEW_REPORT_PERMISSION:
										d.dismiss();
										mListener.onAllow(s.getStaffID(), StaffsDataSource.VIEW_REPORT_PERMISSION);
										break;
									}
								}else{
									mTxtStaffCode.setError(null);
									mTxtPass.setError(getString(R.string.incorrect_password));
								}
							}else{
								mTxtStaffCode.setError(getString(R.string.incorrect_staff_code));
								mTxtPass.setError(null);
							}
						}else{
							mTxtStaffCode.setError(null);
							mTxtPass.setError(getString(R.string.enter_password));
						}
					}else{
						mTxtStaffCode.setError(getString(R.string.enter_staff_code));
						mTxtPass.setError(null);
					}
				}
			}
		});
		return d;
	}
	
	public static interface OnCheckPermissionListener{
		void onAllow(int staffId, int permissionId);
	}
}

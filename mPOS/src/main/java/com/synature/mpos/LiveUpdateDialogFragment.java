package com.synature.mpos;

import java.io.File;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ResultReceiver;

public class LiveUpdateDialogFragment extends DialogFragment 
	implements DialogInterface.OnClickListener{

	public static final String TAG = "LiveUpdateDialogFragment";
	
	private ProgressDialog mProgressDialog;
	
	private class DownloadReceiver extends ResultReceiver{

		public DownloadReceiver(Handler handler) {
			super(handler);
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			if(resultCode == DownloadService.UPDATE_PROGRESS){
				int progress = resultData.getInt("progress");
				mProgressDialog.setProgress(progress);
				if(progress == 100){
					mProgressDialog.dismiss();
					String fileName = resultData.getString("fileName");
					startInstallationActivity(fileName);
				}
			}
		}
		
	}
	
	public static LiveUpdateDialogFragment newInstance(String fileUrl){
		LiveUpdateDialogFragment f = new LiveUpdateDialogFragment();
		Bundle b = new Bundle();
		b.putString("fileUrl", fileUrl);
		f.setArguments(b);
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mProgressDialog = new ProgressDialog(getActivity());
		mProgressDialog.setTitle(R.string.downloading);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, 
				getString(android.R.string.cancel), this);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		String fileUrl = getArguments().getString("fileUrl");
		mProgressDialog.show();
		Intent intent = new Intent(getActivity(), DownloadService.class);
		intent.putExtra("fileUrl", fileUrl);
		intent.putExtra("receiver", new DownloadReceiver(new Handler()));
		getActivity().startService(intent);
		return mProgressDialog;
	}

	private void startInstallationActivity(String fileName){
		File download = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File apkFile = new File(download + File.separator + fileName);
	    Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
	    startActivity(intent);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dismiss();
	}
}

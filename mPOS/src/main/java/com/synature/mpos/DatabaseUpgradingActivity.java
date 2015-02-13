package com.synature.mpos;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.view.Menu;
import android.view.MenuItem;

public class DatabaseUpgradingActivity extends Activity {

	public static final int RESULT_SUCCESS = 1;
	public static final int RESULT_ERROR = 0;
	
	private class UpgradingReceiver extends ResultReceiver{

		private ProgressDialog progress;
		
		public UpgradingReceiver(Handler handler) {
			super(handler);
			progress.setMessage(getString(R.string.system_upgrade));
			progress.setCancelable(false);
			progress.setCanceledOnTouchOutside(false);
			progress.show();
		}

		@Override
		protected void onReceiveResult(int resultCode, Bundle resultData) {
			super.onReceiveResult(resultCode, resultData);
			if(resultCode == RESULT_SUCCESS){
				progress.dismiss();
				finish();
			}else if(resultCode == RESULT_ERROR){
				new AlertDialog.Builder(DatabaseUpgradingActivity.this)
				.setMessage(resultData.getString("msg"))
				.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}
				})
				.show();
			}
		}
		
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_database_upgrading);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.database_upgrading, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}

package com.synature.mpos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class RestoreDatabaseFragment extends DialogFragment{
	
	public static final String TAG = RestoreDatabaseFragment.class.getSimpleName();
	
	public static final String RESTORE_PASS = "mposrestore";
	
	private int mLastPosition = -1;
	private DatabaseInfo mDbInfo;
	private List<DatabaseInfo> mDbInfoLst;
	private DatabaseListAdapter mAdapter;
	
	private ListView mLvDatabase;
	
	public static RestoreDatabaseFragment newInstance(){
		RestoreDatabaseFragment f = new RestoreDatabaseFragment();
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final LayoutInflater inflater = getActivity().getLayoutInflater();
		View content = inflater.inflate(R.layout.database_listview, null);
		mLvDatabase = (ListView) content;
		setupDatabaseListViewAdapter();
		mLvDatabase.setOnItemClickListener(mOnItemClickListener);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.restore_db);
		builder.setView(content);
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.setPositiveButton(android.R.string.ok, null);
		final AlertDialog dMain = builder.create();
		dMain.show();
		dMain.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				if(mDbInfo != null && mDbInfo.isChecked()){
					View passView = inflater.inflate(R.layout.edittext_password, null);
					final EditText txtPass = (EditText) passView.findViewById(R.id.txtPassword);
					AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
					builder.setTitle(R.string.restore_db);
					builder.setMessage(mDbInfo.getFile().getName());
					builder.setView(passView);
					builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					});
					builder.setPositiveButton(android.R.string.ok, null);
					final AlertDialog dPass = builder.create();
					dPass.show();
					dPass.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener(){

						@Override
						public void onClick(View v) {
							String password = txtPass.getText().toString();
							if(!TextUtils.isEmpty(password)){
								if(password.equals(RESTORE_PASS)){
									try {
										restoreDatabase(mDbInfo.getFile());
										Toast.makeText(getActivity(), getActivity().getString(R.string.restore_db_success), Toast.LENGTH_SHORT).show();
									} catch (IOException e) {
										Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
									}
									dPass.dismiss();
									dMain.dismiss();
								}
							}
						}
						
					});
				}else{
					new AlertDialog.Builder(getActivity())
					.setTitle(R.string.restore_db)
					.setMessage(R.string.please_select_db)
					.setNeutralButton(R.string.close, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
				}
			}
			
		});
		return dMain;
	}

	private OnItemClickListener mOnItemClickListener = new OnItemClickListener(){

		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			mDbInfo = (DatabaseInfo) parent.getItemAtPosition(position);
			if(mDbInfo.isChecked())
				mDbInfo.setChecked(false);
			else
				mDbInfo.setChecked(true);
			if(mLastPosition > -1 && mLastPosition != position){
				DatabaseInfo dbInfo = mDbInfoLst.get(mLastPosition);
				dbInfo.setChecked(false);
			}
			mDbInfoLst.set(position, mDbInfo);
			mAdapter.notifyDataSetChanged();
			mLastPosition = position;
		}

	};
	
	@SuppressWarnings("resource")
	private void restoreDatabase(File file) throws IOException{
		File sd = Environment.getExternalStorageDirectory();
		FileChannel source = null;
		FileChannel destination = null;
		File dbPath = getActivity().getDatabasePath(MPOSApplication.DB_NAME);
		source = new FileInputStream(file).getChannel();
		destination = new FileOutputStream(dbPath).getChannel();
		destination.transferFrom(source, 0, source.size());
		source.close();
		destination.close();
	}
	
	private void setupDatabaseListViewAdapter(){
		if(mAdapter == null){
			listDatabaseInfo();
			mAdapter = new DatabaseListAdapter();
			mLvDatabase.setAdapter(mAdapter);
		}
		mAdapter.notifyDataSetChanged();
	}
	
	private void listDatabaseInfo(){
		File sd = Environment.getExternalStorageDirectory();
		File backupPath = new File(sd, MPOSApplication.BACKUP_DB_PATH);
		List<File> files = Utils.listFiles(backupPath);
		mDbInfoLst = new ArrayList<DatabaseInfo>();
		if(files == null){
			files = Utils.listFiles(sd);
		}
		for(File file : files){
			mDbInfoLst.add(toDatabaseInfo(file));
		}
	}
	
	private DatabaseInfo toDatabaseInfo(File file){
		DatabaseInfo dbInfo = new DatabaseInfo();
		dbInfo.setFile(file);
		return dbInfo;
	}
	
	private class DatabaseListAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater = getActivity().getLayoutInflater();
		
		@Override
		public int getCount() {
			return mDbInfoLst != null ? mDbInfoLst.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return mDbInfoLst.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.database_list_item, parent, false);
				holder.tvDbName = (CheckedTextView) convertView.findViewById(R.id.tvDbName);
				holder.tvModifyDate = (TextView) convertView.findViewById(R.id.tvModifyDate);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			DatabaseInfo dbInfo = mDbInfoLst.get(position);
			Calendar c = Calendar.getInstance();
			File file = dbInfo.getFile();
			c.setTimeInMillis(file.lastModified());
			String fileName = file.getName();
			holder.tvDbName.setText(fileName);
			holder.tvDbName.setChecked(dbInfo.isChecked());
			holder.tvModifyDate.setText(DateFormat.getInstance().format(c.getTime()));
			return convertView;
		}
		
		private class ViewHolder{
			CheckedTextView tvDbName;
			TextView tvModifyDate;
		}
	}
	
	private class DatabaseInfo{
		private File file;
		private boolean isChecked;
		
		public File getFile() {
			return file;
		}
		public void setFile(File file) {
			this.file = file;
		}
		public boolean isChecked() {
			return isChecked;
		}
		public void setChecked(boolean isChecked) {
			this.isChecked = isChecked;
		}
	}
}

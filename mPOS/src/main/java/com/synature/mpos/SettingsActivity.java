package com.synature.mpos;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

	public static final String KEY_PREF_SERVER_URL = "server_url";
	public static final String KEY_PREF_CONN_TIME_OUT_LIST = "connection_time_out";
	
	public static final String KEY_PREF_PRINTER_IP = "printer_ip";
	public static final String KEY_PREF_PRINTER_LIST = "printer_list";
	public static final String KEY_PREF_PRINTER_INTERNAL = "printer_internal";
	public static final String KEY_PREF_PRINTER_DEV_PATH = "printer_wintec_dev_path";
	public static final String KEY_PREF_PRINTER_BAUD_RATE = "printer_wintec_baud_rate";
	public static final String KEY_PREF_MSR_DEV_PATH = "msr_wintec_dev_path";
	public static final String KEY_PREF_MSR_BAUD_RATE = "msr_wintec_baud_rate";
	public static final String KEY_PREF_DSP_DEV_PATH = "dsp_wintec_dev_path";
	public static final String KEY_PREF_DSP_BAUD_RATE = "dsp_wintec_baud_rate";
	public static final String KEY_PREF_DRW_DEV_PATH = "drw_wintec_dev_path";
	public static final String KEY_PREF_DRW_BAUD_RATE = "drw_wintec_baud_rate";
	public static final String KEY_PREF_SHOW_MENU_IMG = "show_menu_image";
	public static final String KEY_PREF_SECOND_DISPLAY_IP = "second_display_ip";
	public static final String KEY_PREF_SECOND_DISPLAY_PORT = "second_display_port";
	public static final String KEY_PREF_ENABLE_DSP = "enable_dsp";
	public static final String KEY_PREF_DSP_TEXT_LINE1 = "dsp_wintec_line1";
	public static final String KEY_PREF_DSP_TEXT_LINE2 = "dsp_wintec_line2";
	public static final String KEY_PREF_ENABLE_SECOND_DISPLAY = "enable_second_display";
	public static final String KEY_PREF_LANGUAGE_LIST = "language_list";
	public static final String KEY_PREF_ENABLE_BACKUP_DB = "enable_backup_db";
	public static final String KEY_PREF_MONTHS_TO_KEEP_SALE = "months_keep_sale";
	
	// store update information
	public static final String KEY_PREF_NEED_TO_UPDATE = "need_to_update";
	public static final String KEY_PREF_NEW_VERSION = "new_version";
	public static final String KEY_PREF_FILE_URL = "file_url";
	public static final String KEY_PREF_APK_DOWNLOAD_STATUS = "apk_download_status"; 	// 0 fail, 1 success
	public static final String KEY_PREF_APK_DOWNLOAD_FILE_NAME = "apk_download_file_name";
	public static final String KEY_PREF_APK_MD5 = "apk_md5";
	public static final String KEY_PREF_LAST_UPDATE = "last_update";
	public static final String KEY_PREF_EXP_DATE = "software_exp_date";
	public static final String KEY_PREF_LOCK_DATE = "software_lock_date";

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
		case android.R.id.home:
			finish();
			return true;
		default :
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.pref_headers, target);
	}

	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = 
			new Preference.OnPreferenceChangeListener() {
		
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			String stringValue = value.toString();

			if (preference instanceof ListPreference) {
				ListPreference listPreference = (ListPreference) preference;
				int index = listPreference.findIndexOfValue(stringValue);
				preference
						.setSummary(index >= 0 ? listPreference.getEntries()[index]
								: null);

			} else {
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	private static void bindPreferenceSummaryToValue(Preference preference) {
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
						""));
	}
	
	public static class PrinterPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_printer);
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_PRINTER_IP));
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_PRINTER_LIST));
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_PRINTER_DEV_PATH));
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_PRINTER_BAUD_RATE));
		}
	}
	
	public static class WintecSettingFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_wintec);
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_DRW_DEV_PATH));
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_DRW_BAUD_RATE));
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_MSR_DEV_PATH));
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_MSR_BAUD_RATE));
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_DSP_DEV_PATH));
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_DSP_BAUD_RATE));
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_DSP_TEXT_LINE1));
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_DSP_TEXT_LINE2));
		}
	}
	
	public static class ConnectionPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_connection);
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_SERVER_URL));
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_CONN_TIME_OUT_LIST));
		}
	}
	
	public static class GeneralPreferenceFragment extends PreferenceFragment{

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_MONTHS_TO_KEEP_SALE));
		}
		
	}
	
	public static class SecondDisplayPreferenceFragment extends PreferenceFragment{

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_second_display);
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_SECOND_DISPLAY_IP));
			bindPreferenceSummaryToValue(findPreference(KEY_PREF_SECOND_DISPLAY_PORT));
		}
		
	}
	
	public void dspTestClick(final View v){
		WintecCustomerDisplay dsp = new WintecCustomerDisplay(getApplicationContext());
		dsp.displayWelcome();
	}
	
	public void drwTestClick(final View v){
		WintecCashDrawer drw = new WintecCashDrawer(getApplicationContext());
		drw.openCashDrawer();
	}
	
	public void printTestClick(final View v){
		if(Utils.isInternalPrinterSetting(getApplicationContext())){
			WinTecTestPrint wt = new WinTecTestPrint(getApplicationContext());
			wt.print();
		}else{
			EPSONTestPrint ep = new EPSONTestPrint(getApplicationContext());
			ep.print();
		}
	}
	
	public static class WinTecTestPrint extends WintecPrinter{
		
		public WinTecTestPrint(Context context){
			super(context);
			mTextToPrint.append(mContext.getString(R.string.print_test_text).replaceAll("\\*", " "));
		}
	}
	
	public static class EPSONTestPrint extends EPSONPrinter{

		public EPSONTestPrint(Context context) {
			super(context);
			mTextToPrint.append(mContext.getString(R.string.print_test_text).replaceAll("\\*", " "));
		}
	}
}

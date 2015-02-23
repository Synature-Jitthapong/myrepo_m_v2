package com.synature.mpos;

import java.util.List;

import com.synature.mpos.database.LanguageDao;
import com.synature.mpos.database.model.Language;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.Spinner;

public class SwitchLangFragment extends DialogFragment{

	private LanguageDao mLangDao;
	private List<Language> mLangLst;
	private LanguageAdapter mLangAdapter;
	
	private Spinner mSpLang;
	
	private OnChangeLanguageListener mListener;
	
	public static SwitchLangFragment newInstance(){
		SwitchLangFragment f = new SwitchLangFragment();
		return f;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLangDao = new LanguageDao(getActivity());
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnChangeLanguageListener){
			mListener = (OnChangeLanguageListener) activity;
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		View content = inflater.inflate(R.layout.lang_sw, null);
		mSpLang = (Spinner) content.findViewById(R.id.spLang);
		mSpLang.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				Language lang = (Language) arg0.getItemAtPosition(arg2);
				String langCode = "en_US";
				switch(lang.getLangId()){
				case 1:
					langCode = "en_US";
					break;
				case 2:
					langCode = "th_TH";
					break;
				}
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
				SharedPreferences.Editor editor = sharedPref.edit();
				editor.putString(SettingsActivity.KEY_PREF_LANGUAGE_LIST, langCode);
				editor.commit();
				Utils.switchLanguage(getActivity().getBaseContext(), langCode);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
		setupLangAdapter();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.language);
		builder.setView(content);
		builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
			}
		});
		builder.setPositiveButton(android.R.string.ok, null);
		final AlertDialog d = builder.create();
		d.show();
		d.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mListener.onChangeLanguage();
				d.dismiss();
			}
		});
		return d;
	}
	
	private void setupLangAdapter(){
		if(mLangAdapter == null){
			mLangLst = mLangDao.listLanguage();
			mLangAdapter = new LanguageAdapter();
			mSpLang.setAdapter(mLangAdapter);
		}
		mLangAdapter.notifyDataSetChanged();
		String langCode = Utils.getLangCode(getActivity());
		if(langCode.equals("en_US")){
			mSpLang.setSelection(0);
		}else if(langCode.equals("th_TH")){
			mSpLang.setSelection(1);
		}
	}
	
	private class LanguageAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater = getActivity().getLayoutInflater();
		
		@Override
		public int getCount() {
			return mLangLst != null ? mLangLst.size() : 0;
		}

		@Override
		public Object getItem(int position) {
			return mLangLst.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = mInflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
			}
			Language lang = mLangLst.get(position);
			((CheckedTextView) convertView).setText(lang.getLangName());
			return convertView;
		}
		
	}
	
	public static interface OnChangeLanguageListener{
		void onChangeLanguage();
	}
}

package com.synature.pos;

public class Language{
	private int LangID;
	private String LangName;
	private String LangCode;
	private int IsDefault;
	
	public int getIsDefault() {
		return IsDefault;
	}
	public void setIsDefault(int isDefault) {
		IsDefault = isDefault;
	}
	public int getLangID() {
		return LangID;
	}
	public void setLangID(int langID) {
		LangID = langID;
	}
	public String getLangName() {
		return LangName;
	}
	public void setLangName(String langName) {
		LangName = langName;
	}
	public String getLangCode() {
		return LangCode;
	}
	public void setLangCode(String langCode) {
		LangCode = langCode;
	}
	
	@Override
	public String toString() {
		return LangName;
	}
	
}
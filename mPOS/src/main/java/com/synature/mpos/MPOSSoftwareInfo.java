package com.synature.mpos;

import android.os.Parcel;
import android.os.Parcelable;

public class MPOSSoftwareInfo implements Parcelable {
	private String szRegisterServiceUrl;
	private String szSoftwareExpireDate;
	private String szLockExpireDate;
	private String szRegisterMessage;
	private String szSoftwareVersion;
	private String szSoftwareDownloadUrl;
	public String getSzRegisterServiceUrl() {
		return szRegisterServiceUrl;
	}
	public void setSzRegisterServiceUrl(String szRegisterServiceUrl) {
		this.szRegisterServiceUrl = szRegisterServiceUrl;
	}
	public String getSzRegisterMessage() {
		return szRegisterMessage;
	}
	public void setSzRegisterMessage(String szRegisterMessage) {
		this.szRegisterMessage = szRegisterMessage;
	}
	public String getSzSoftwareVersion() {
		return szSoftwareVersion;
	}
	public void setSzSoftwareVersion(String szSoftwareVersion) {
		this.szSoftwareVersion = szSoftwareVersion;
	}
	public String getSzSoftwareDownloadUrl() {
		return szSoftwareDownloadUrl;
	}
	public void setSzSoftwareDownloadUrl(String szSoftwareDownloadUrl) {
		this.szSoftwareDownloadUrl = szSoftwareDownloadUrl;
	}
	public String getSzLockExpireDate() {
		return szLockExpireDate;
	}
	public void setSzLockExpireDate(String szLockExpireDate) {
		this.szLockExpireDate = szLockExpireDate;
	}
	public String getSzSoftwareExpireDate() {
		return szSoftwareExpireDate;
	}
	public void setSzSoftwareExpireDate(String szSoftwareExpireDate) {
		this.szSoftwareExpireDate = szSoftwareExpireDate;
	}
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	}
}

package com.t3hh4xx0r.nfcvaultfree;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.t3hh4xx0r.nfcvaultfree.LaunchPopupManager.OnManagerCancelCallback;

public class LaunchPopup implements Parcelable {
	String title = "Launch Popup";

	String message = "This is a demo launch popup";

	String posButton;

	String neutralButton;

	public boolean isNeedsLocale() {
		return needsLocale;
	}

	public void setNeedsLocale(boolean needsLocale) {
		this.needsLocale = needsLocale;
	}

	String negButton;

	int icon = R.drawable.ic_launcher;

	int[] launchesTimes = new int[] { 1 };

	boolean needsPlayStore = false;
	boolean needsLocale = false;

	OnManagerCancelCallback managerCallback;

	OnClickListener posListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.d("CLICKED", "CLICKED POSITIVE");
		}
	};

	OnClickListener nuetralListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.d("CLICKED", "CLICKED NEUTRAL");
		}
	};

	OnClickListener negListener = new OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.d("CLICKED", "CLICKED NEGATIVE");
		}
	};

	public int getIcon() {
		return icon;
	}

	public int[] getLaunchesTimes() {
		return launchesTimes;
	}

	public OnManagerCancelCallback getManagerCallback() {
		return managerCallback;
	}

	public String getMessage() {
		return message;
	}

	public String getNegButton() {
		return negButton;
	}

	public OnClickListener getNegListener() {
		return negListener;
	}

	public String getNeutralButton() {
		return neutralButton;
	}

	public OnClickListener getNuetralListener() {
		return nuetralListener;
	}

	public String getPosButton() {
		return posButton;
	}

	public OnClickListener getPosListener() {
		return posListener;
	}

	public String getTitle() {
		return title;
	}

	public boolean isNeedsPlayStore() {
		return needsPlayStore;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public void setLaunchesTimes(int[] launchesTimes) {
		this.launchesTimes = launchesTimes;
	}

	public void setManagerCallback(OnManagerCancelCallback managerCallback) {
		this.managerCallback = managerCallback;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setNeedsPlayStore(boolean needsPlayStore) {
		this.needsPlayStore = needsPlayStore;
	}

	public void setNegButton(String negButton, OnClickListener negListener) {
		this.negButton = negButton;
		this.negListener = negListener;
	}

	public void setNeutralButton(String neutralButton,
			OnClickListener nuetralListener) {
		this.neutralButton = neutralButton;
		this.nuetralListener = nuetralListener;
	}

	public void setPosButton(String posButton, OnClickListener posListener) {
		this.posButton = posButton;
		this.posListener = posListener;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub

	}

}

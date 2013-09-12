package com.t3hh4xx0r.nfcvaultfree;

import java.util.ArrayList;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.common.primitives.Ints;

public class LaunchPopupManager implements Parcelable {

	ArrayList<LaunchPopup> popupList;
	int currentPos = 0;

	public interface OnManagerCancelCallback {
		public void onPopupFinished(LaunchPopup popup);
	}

	public OnManagerCancelCallback managerCallback = new OnManagerCancelCallback() {
		@Override
		public void onPopupFinished(LaunchPopup popup) {
			currentPos = currentPos + 1;
			Log.d("READY TO SHOW THE NEXT ONE", "SHOW IT NOW");
			if (popupList.size() > currentPos) {
				LaunchPopup nextPopup = popupList.get(currentPos);
				if (contains(nextPopup.getLaunchesTimes(),
						mPreferences.getInt(PREF.TOTAL_LAUNCH_COUNT, 0))) {
					showFragment(nextPopup);
				}
			}
		}
	};

	public OnManagerCancelCallback getManagerCallback() {
		return managerCallback;
	}

	public LaunchPopupManager(ArrayList<LaunchPopup> popupList,
			FragmentActivity activity) {
		this.popupList = popupList;
		mActivity = activity;
		mPreferences = mActivity.getSharedPreferences(PREF.NAME, 0);
		int version = getPackageInfo(mActivity).versionCode;
		int lastVersion = mPreferences.getInt(PREF.LAST_VERSION, 0);
		if (version != lastVersion) {
			resetData(mActivity);
			mPreferences.edit().putInt(PREF.LAST_VERSION, version).commit();
		}

		for (LaunchPopup popup : popupList) {
			popup.setManagerCallback(getManagerCallback());
		}
	}

	public void start() {
		if (mPreferences.getBoolean(PREF.DONT_SHOW_AGAIN, false)) {
			return;
		}

		Editor editor = mPreferences.edit();

		int totalLaunchCount = mPreferences.getInt(PREF.TOTAL_LAUNCH_COUNT, 0) + 1;
		editor.putInt(PREF.TOTAL_LAUNCH_COUNT, totalLaunchCount);

		int launchesSinceLastPrompt = mPreferences.getInt(
				PREF.LAUNCHES_SINCE_LAST_PROMPT, 0) + 1;
		editor.putInt(PREF.LAUNCHES_SINCE_LAST_PROMPT, launchesSinceLastPrompt);
		editor.commit();

		for (LaunchPopup popup : popupList) {
			if (contains(popup.getLaunchesTimes(), totalLaunchCount)) {
				editor.putInt(PREF.LAUNCHES_SINCE_LAST_PROMPT, 0);
				showFragment(popup);
				editor.commit();
				break;
			}
		}
	}

	public boolean contains(final int[] array, final int key) {
		return Ints.contains(array, key);
	}

	private void showFragment(LaunchPopup popup) {
		Log.d("SHOWING POPUP", popup.getTitle());
		if (!isPlayStoreInstalled()) {
			Log.d(TAG, "No Play Store installed on device.");
			if (popup.isNeedsPlayStore()) {
				return;
			}
		}

		if (!isLocaleInstalled()) {
			Log.d(TAG, "No Locale installed on device.");
			if (popup.isNeedsLocale()) {
				return;
			}
		}

		LaunchPopupFragment frag = new LaunchPopupFragment();
		Bundle b = new Bundle();
		b.putParcelable("popup", popup);
		frag.setArguments(b);
		if (mActivity.getSupportFragmentManager().findFragmentByTag(
				popup.getTitle()) != null) {
			Log.d("NOT SHOWING", "NOT DOING IT");
			return;
		}
		frag.show(mActivity.getSupportFragmentManager(), popup.getTitle());
	}

	private static final String TAG = "LaunchPopup";

	private FragmentActivity mActivity;
	private SharedPreferences mPreferences;

	/**
	 * Reset the launch logs
	 */
	public static void resetData(FragmentActivity activity) {
		activity.getSharedPreferences(PREF.NAME, 0).edit().clear().commit();
		Log.d(TAG, "Cleared SeeUpgrade shared preferences.");
	}

	/**
	 * @return Whether Google Play Store is installed on device
	 */
	private Boolean isPlayStoreInstalled() {
		PackageManager pacman = mActivity.getPackageManager();
		try {
			pacman.getApplicationInfo("com.android.vending", 0);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	private Boolean isLocaleInstalled() {
		PackageManager pacman = mActivity.getPackageManager();
		try {
			pacman.getApplicationInfo("com.twofortyfouram.locale", 0);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	public static Boolean isPluginInstalled(Activity a) {
		PackageManager pacman = a.getPackageManager();
		try {
			pacman.getApplicationInfo("com.t3hh4xx0r.nfcsecure_plugin", 0);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	static class PREF {
		public static final String NAME = "dialogs";
		private static final String LAST_VERSION = "PREF_LAST_VERSION";
		private static final String DONT_SHOW_AGAIN = "PREF_DONT_SHOW_AGAIN";
		public static final String TOTAL_LAUNCH_COUNT = "PREF_TOTAL_LAUNCH_COUNT";
		public static final String LAUNCHES_SINCE_LAST_PROMPT = "PREF_LAUNCHES_SINCE_LAST_PROMPT";
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

	public static PackageInfo getPackageInfo(Activity a) {
		PackageInfo pi = null;
		try {
			pi = a.getPackageManager().getPackageInfo(a.getPackageName(),
					PackageManager.GET_ACTIVITIES);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return pi;
	}

}
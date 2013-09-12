package com.t3hh4xx0r.nfcvaultfree;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import com.t3hh4xx0r.nfcvaultfree.activities.MainActivity;

public class SettingsProvider {
	private static final String HASHED_KEY = "key_hash";
	private static final String REMEMBER_ME = "remember_me";
	private static final String REMEMBER_ME_VALUE = "remember_me_value";
	public static final String SALT = "PASSWORDMANAGER";
	Context c;

	public SettingsProvider(Context c) {
		this.c = c;
	}

	public String getHashedKey() {
		return PreferenceManager.getDefaultSharedPreferences(c).getString(
				HASHED_KEY, "");
	}

	public void setHashedKey(String hashedKey)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] bytesOfMessage = hashedKey.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(bytesOfMessage);
		PreferenceManager
				.getDefaultSharedPreferences(c)
				.edit()
				.putString(HASHED_KEY,
						MainActivity.ByteArrayToHexString(digest)).apply();
	}

	public boolean isCorrectKey(String key)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] bytesOfMessage = key.getBytes("UTF-8");
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] digest = md.digest(bytesOfMessage);
		String keyHash = MainActivity.ByteArrayToHexString(digest);
		String storedHash = getHashedKey();
		Log.d("THE VALUES", keyHash + " : " + storedHash);
		return keyHash.equals(storedHash);
	}

	public String getRememberMeEmail() {
		return PreferenceManager.getDefaultSharedPreferences(c).getString(
				REMEMBER_ME_VALUE, "");
	}

	public void setRememberMeEmail(String email) {
		PreferenceManager.getDefaultSharedPreferences(c).edit()
				.putString(REMEMBER_ME_VALUE, email).apply();
	}

	public void setRememberMe(boolean rememeber) {
		PreferenceManager.getDefaultSharedPreferences(c).edit()
				.putBoolean(REMEMBER_ME, rememeber).apply();
	}

	public boolean getRememberMe() {
		return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(
				REMEMBER_ME, false);
	}
}

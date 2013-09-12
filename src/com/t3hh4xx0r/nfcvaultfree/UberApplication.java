/**
 * 
 */
package com.t3hh4xx0r.nfcvaultfree;

import android.app.Application;

import com.parse.Parse;

public class UberApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Parse.initialize(this, "CcZT91HAEuLssldRiQRMIPmBQfziW1yIHqBrmE6Q",
				"80FEqpDX4ord5LuTaTHJJm85tVXtXn2ZtrWXykLu");
	}
}

package com.t3hh4xx0r.nfcvaultfree;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class LaunchPopupFragment extends DialogFragment {
	LaunchPopup popup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		popup = (LaunchPopup) getArguments().get("popup");
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		if (popup.getIcon() != 0) {
			builder.setIcon(popup.getIcon());
		}
		builder.setTitle(popup.getTitle());
		builder.setCancelable(false);
		builder.setMessage(popup.getMessage());
		if (popup.getNegButton() != null) {
			builder.setNegativeButton(popup.getNegButton(),
					popup.getNegListener());
		}
		if (popup.getPosButton() != null) {
			builder.setPositiveButton(popup.getPosButton(),
					popup.getPosListener());
		}
		if (popup.getNeutralButton() != null) {
			builder.setNeutralButton(popup.getNeutralButton(),
					popup.getNuetralListener());
		}

		AlertDialog alert = builder.create();
		return alert;
	}

	@Override
	public void onCancel(DialogInterface dialog) {
		popup.getManagerCallback().onPopupFinished(popup);
		super.onCancel(dialog);
	}

}
package com.t3hh4xx0r.nfcvaultfree.activities;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;
import com.fima.cardsui.objects.Card.OnCardSwiped;
import com.fima.cardsui.objects.CardStack;
import com.fima.cardsui.views.CardUI;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.t3hh4xx0r.nfcvaultfree.LaunchPopup;
import com.t3hh4xx0r.nfcvaultfree.LaunchPopupManager;
import com.t3hh4xx0r.nfcvaultfree.Password;
import com.t3hh4xx0r.nfcvaultfree.PasswordCard;
import com.t3hh4xx0r.nfcvaultfree.PasswordCard.OnViewButtonLister;
import com.t3hh4xx0r.nfcvaultfree.R;
import com.t3hh4xx0r.nfcvaultfree.SettingsProvider;
import com.t3hh4xx0r.nfcvaultfree.encryption.Encryption;

import de.cketti.library.changelog.ChangeLog;

public class MainActivity extends FragmentActivity {
	CardUI mCardView;
	ArrayList<CardStack> mCardStacks;
	ArrayList<PasswordCard> mCards;
	ArrayList<String> stackTitles;

	private NfcAdapter mAdapter;
	private PendingIntent mPendingIntent;

	boolean isListeningforInitialTagScan = false;
	boolean isListeningforDecryptTagScan = false;
	PasswordCard cardFromDecrypt;

	String COLOR = "#33b6ea";

	ProgressBar pBar;

	AlertDialog setupDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.password_list);
		mAdapter = NfcAdapter.getDefaultAdapter(this);
		resolveIntent(getIntent());
		mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
				getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
		pBar = (ProgressBar) findViewById(R.id.progressBar1);
		mCardView = (CardUI) findViewById(R.id.cardsview);
		mCardView.setSwipeable(true);
		stackTitles = new ArrayList<String>();
		mCardStacks = new ArrayList<CardStack>();
		mCards = new ArrayList<PasswordCard>();

		setupPasswordCards();

		managePopups();
	}

	public CardStack getStackForTitle(String title) {
		for (CardStack stack : mCardStacks) {
			if (stack.getTitle().equals(title)) {
				return stack;
			}
		}
		CardStack stack = new CardStack();
		stack.setColor(COLOR);
		stack.setTitle(title);
		mCardStacks.add(stack);

		return stack;
	}

	private void setupPasswordStacks() {
		for (PasswordCard card : mCards) {
			getStackForTitle(card.getPassword().getDataStack()).add(card);
		}

		for (CardStack stack : mCardStacks) {
			mCardView.addStack(stack);
		}
		mCardView.refresh();
		hideProgress(this);
	}

	private void setupPasswordCards() {
		mCardView.clearCards();
		mCardStacks.clear();
		mCards.clear();
		mCardView.refresh();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Password");
		query.whereEqualTo("key_owner", ParseUser.getCurrentUser().getEmail());
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> results, ParseException e) {
				if (e == null) {
					for (final ParseObject o : results) {
						Password p = new Password(o.getString("data_stack"), o
								.getString("data_value"), o
								.getString("data_title"));
						p.setParseId(o.getObjectId());
						final PasswordCard card = new PasswordCard(p,
								viewButtonListener);
						card.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent i = new Intent(v.getContext(),
										AddPasswordActivity.class);
								Bundle b = new Bundle();
								b.putSerializable("password",
										card.getPassword());
								b.putStringArrayList("stacks", getStackNames());
								i.putExtras(b);
								startActivityForResult(i, 1);
							}
						});

						card.setOnCardSwipedListener(new OnCardSwiped() {
							@Override
							public void onCardSwiped(Card cardRes,
									final View layout) {
								AlertDialog.Builder b = new Builder(layout
										.getContext());
								b.setTitle("Delete This Data?");
								b.setMessage("This will delete the data from you local device and from the cloud.\n\nAre you sure you want to continue?");
								b.setPositiveButton("Yes",
										new OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												o.deleteInBackground(new DeleteCallback() {
													@Override
													public void done(
															ParseException arg0) {
														CardStack stack = getStackForTitle(card
																.getPassword()
																.getDataStack());
														if (stack.getCards()
																.size() == 0) {
															setupPasswordCards();
														}
													}
												});
											}

										});
								b.setNegativeButton("No",
										new OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												getStackForTitle(
														card.getPassword()
																.getDataStack())
														.add(card);
												mCardView.refresh();
											}
										});
								b.create().show();
							}
						});
						mCards.add(card);
					}
					setupPasswordStacks();
				} else {
					e.printStackTrace();
				}
			}
		});
	}

	private void hideProgress(Context c) {
		mCardView.setAnimation(AnimationUtils.loadAnimation(c,
				android.R.anim.fade_in));
		mCardView.setVisibility(View.VISIBLE);

		pBar.setAnimation(AnimationUtils.loadAnimation(c,
				android.R.anim.fade_out));
		pBar.setVisibility(View.GONE);
	}

	private void showProgress(Context c) {
		mCardView.setAnimation(AnimationUtils.loadAnimation(c,
				android.R.anim.fade_out));
		mCardView.setVisibility(View.GONE);

		pBar.setAnimation(AnimationUtils.loadAnimation(c,
				android.R.anim.fade_in));
		pBar.setVisibility(View.VISIBLE);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if (item.getItemId() == R.id.action_add_key) {
			if (new SettingsProvider(this).getHashedKey().isEmpty()) {
				requestKeyValidation(false, null);
			} else {
				notifyKeySet();
			}
		} else if (item.getItemId() == R.id.action_help) {
			requestHelp();
		} else if (item.getItemId() == R.id.action_add_password) {
			Intent i = new Intent(this, AddPasswordActivity.class);
			Bundle b = new Bundle();
			b.putStringArrayList("stacks", getStackNames());
			i.putExtras(b);
			startActivityForResult(i, 0);
		} else if (item.getItemId() == R.id.action_contact) {
			final Intent emailIntent = new Intent(
					android.content.Intent.ACTION_SEND);
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
					new String[] { "r2doesinc@gmail.com" });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
					"NFCVault Feedback");
			startActivity(Intent.createChooser(emailIntent, "Send via..."));
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private ArrayList<String> getStackNames() {
		ArrayList<String> results = new ArrayList<String>();
		for (int i = 0; i < mCardStacks.size(); i++) {
			results.add(mCardStacks.get(i).getTitle());
		}
		return results;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			if (data != null && data.hasExtra("password")) {
				Password result = (Password) data
						.getSerializableExtra("password");
				PasswordCard pCard = new PasswordCard(result,
						viewButtonListener);
				getStackForTitle(pCard.getPassword().getDataStack()).add(pCard);
				mCardView.refresh();
				showProgress(this);
				result.toParsePassword().saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException arg0) {
						setupPasswordCards();
						hideProgress(MainActivity.this);
					}
				});
			}
		} else if (requestCode == 1) {
			if (data != null && data.hasExtra("password")) {
				final Password result = (Password) data
						.getSerializableExtra("password");
				final Password old = (Password) data
						.getSerializableExtra("oldPassword");
				final PasswordCard pCard = new PasswordCard(result,
						viewButtonListener);
				ParseQuery<ParseObject> query = ParseQuery.getQuery("Password");
				query.getInBackground(pCard.getPassword().getParseId(),
						new GetCallback<ParseObject>() {
							@Override
							public void done(ParseObject foundPassObject,
									ParseException arg1) {
								if (foundPassObject != null) {
									Password foundPass = new Password(
											foundPassObject
													.getString("data_stack"),
											foundPassObject
													.getString("data_value"),
											foundPassObject
													.getString("data_title"));
									foundPass.setParseId(foundPassObject
											.getObjectId());
									foundPass.update(result);
									foundPass.toParsePassword()
											.saveInBackground(
													new SaveCallback() {
														@Override
														public void done(
																ParseException arg0) {
															for (PasswordCard card : mCards) {
																if (card.getPassword()
																		.getParseId()
																		.equals(old
																				.getParseId())) {
																	mCards.remove(card);
																	mCards.add(pCard);
																	break;
																}
															}
															setupPasswordCards();
															hideProgress(MainActivity.this);
														}
													});
								} else {
									hideProgress(MainActivity.this);
								}
							}
						});
			}
		}
	}

	private void notifyKeySet() {
		AlertDialog.Builder b = new Builder(this);
		b.setTitle("Master Password Already Set");
		b.setMessage("You have already set a master password. If you overwrite this, any password you have encrypted with your current master password will be reset. Are you sure you want to continue?");
		b.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				requestConfirmation();
			}
		});
		b.create().show();
	}

	protected void requestConfirmation() {
		AlertDialog.Builder b = new Builder(this);
		b.setTitle("Master Password Already Set");
		b.setMessage("Again, if you overwrite this, any password you have encrypted with your current master password will be reset. Are you sure you want to continue?");
		b.setPositiveButton("Yes", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				requestKeyValidation(false, null);
			}
		});
		b.create().show();
	}

	private void requestHelp() {
		AlertDialog.Builder b = new Builder(this);
		b.setTitle("How Does It Work?");
		b.setMessage("NFCVault uses the UID of the tag you scan as the encryption key. This app and the cloud storage never store your key, and all of the encryption and decryption is done locally. No unencrypted data is ever transmitted off of the device. This means if you have lost it and did not write down the value, there is no way to retrieve your passwords. We will provide you with the string value of your key. You MUST write this down in a safe place!");
		b.setPositiveButton("View Changelog", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				ChangeLog cl = new ChangeLog(MainActivity.this);
				cl.getFullLogDialog().show();
			}
		});
		b.create().show();
	}

	private void requestKeyValidation(boolean forDecryptView, PasswordCard card) {
		isListeningforInitialTagScan = !forDecryptView;
		isListeningforDecryptTagScan = forDecryptView;
		if (card != null) {
			cardFromDecrypt = card;
		}
		AlertDialog.Builder b = new Builder(this);
		b.setTitle("Master Password");
		if (forDecryptView) {
			final EditText keyView = new EditText(this);
			keyView.setHint("Or enter you master password here");
			b.setMessage("Please scan the tag you setup as your master password. We will then decrypt and show you your password.");
			b.setView(keyView);
			b.setPositiveButton("Decrypt", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					isListeningforInitialTagScan = false;
					isListeningforDecryptTagScan = false;
					try {
						handleDecryptTagInput(keyView.getText().toString());
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					} catch (NoSuchAlgorithmException e) {
						e.printStackTrace();
					}
					dialog.dismiss();
				}
			});
		} else {
			b.setMessage("Please scan the tag you plan to use as your master key. We will store the md5 of this value to ensure you are using the correct key when encrypting/decrypting your passwords.");
		}
		setupDialog = b.create();
		setupDialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
				isListeningforInitialTagScan = false;
				isListeningforDecryptTagScan = false;
			}
		});
		setupDialog.show();
	}

	private void warnWrongKey() {
		AlertDialog.Builder b = new Builder(this);
		b.setTitle("Master Password Mismatch");
		b.setMessage("When you originally setup your master password, the app hashed and stored that value. The hash of the key you just scanned does not match the stored hash. Confirm you are using the correct key and try again.");
		b.create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onNewIntent(Intent intent) {
		setIntent(intent);
		resolveIntent(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mAdapter != null) {
			mAdapter.disableForegroundDispatch(this);
		}
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mAdapter != null) {
			mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
		}
	}

	private void resolveIntent(Intent intent) {
		if (!isListeningforInitialTagScan && !isListeningforDecryptTagScan) {
			return;
		} else {
			if (isListeningforInitialTagScan) {
				handleInitialTagScan(intent);
			} else {
				try {
					handleDecryptTagScan(intent);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void handleDecryptTagScan(Intent intent)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
		String key = ByteArrayToHexString(tagId);
		handleDecryptTagInput(key);
	}

	private void handleDecryptTagInput(String key)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		if (new SettingsProvider(this).isCorrectKey(key)) {
			if (cardFromDecrypt != null) {
				String decrypted = Encryption.decryptString(cardFromDecrypt
						.getPassword().getDataValue(), key);
				cardFromDecrypt.desc.setText(decrypted);
				cardFromDecrypt.setShowingTrueData(!cardFromDecrypt
						.isShowingTrueData());
			}
		} else {
			warnWrongKey();
		}
		setupDialog.dismiss();
		cardFromDecrypt = null;
	}

	private void handleInitialTagScan(Intent intent) {
		try {
			TextView keyView = new TextView(this);
			LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
			keyView.setGravity(Gravity.CENTER);
			keyView.setLayoutParams(lp);
			keyView.setTextSize(35);
			keyView.setTypeface(null, Typeface.BOLD_ITALIC);
			keyView.setTextColor(getResources().getColor(
					android.R.color.holo_red_light));
			setupDialog.dismiss();
			byte[] tagId = intent.getByteArrayExtra(NfcAdapter.EXTRA_ID);
			String key = ByteArrayToHexString(tagId);
			keyView.setText(key);
			AlertDialog.Builder b = new Builder(this);
			b.setCancelable(false);
			b.setTitle("Master Password");
			b.setMessage("This is your master password. This is the only time it will ever be shown. Be SURE you write this down in case your tag is lost.");
			b.setView(keyView);
			b.setPositiveButton("Done", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			new SettingsProvider(this).setHashedKey(key);
			b.create().show();
		} catch (Exception e) {
			e.printStackTrace();
			AlertDialog.Builder b2 = new Builder(this);
			b2.setTitle("Error");
			b2.setMessage("A problem occured hashing your key. We were unable to continue, please try again.");
			b2.create().show();
		}
	}

	public static String ByteArrayToHexString(byte[] inarray) {
		int i, j, in;
		String[] hex = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A",
				"B", "C", "D", "E", "F" };
		String out = "";

		for (j = 0; j < inarray.length; ++j) {
			in = (int) inarray[j] & 0xff;
			i = (in >> 4) & 0x0f;
			out += hex[i];
			i = in & 0x0f;
			out += hex[i];
		}
		return out;
	}

	OnViewButtonLister viewButtonListener = new OnViewButtonLister() {
		@Override
		public void onViewButtonClicked(PasswordCard card) {
			if (!card.isShowingTrueData()) {
				requestKeyValidation(true, card);
			} else {
				card.setShowingTrueData(!card.isShowingTrueData());
			}
		}
	};

	private void managePopups() {
		LaunchPopup rateMe = setupRateMePopup();

		ArrayList<LaunchPopup> popups = new ArrayList<LaunchPopup>();
		popups.add(rateMe);
		LaunchPopupManager popupManager = new LaunchPopupManager(popups, this);
		// LaunchPopupManager.resetData(this);
		popupManager.start();
	}

	private int[] getRatePopupTimes() {
		ArrayList<Integer> res = new ArrayList<Integer>();
		for (int i = 1; i < 50; i++) {
			if ((i % 2) == 0) {
				res.add(i);
			}
		}
		return convertIntegers(res);
	}

	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}

	private LaunchPopup setupRateMePopup() {
		final LaunchPopup rate = new LaunchPopup();
		rate.setNeedsPlayStore(true);
		rate.setLaunchesTimes(getRatePopupTimes());
		rate.setTitle("Rate NFCSecure?");
		rate.setMessage("If you like this app, please take a moment to comment and rate it on Google Play. Thank you!");
		rate.setPosButton("Rate it", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse("market://details?id=" + getPackageName())));
				rate.getManagerCallback().onPopupFinished(rate);
			}
		});
		return rate;
	}

}

package com.ii.mobile.instantMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ii.mobile.beacon.R;
import com.ii.mobile.flow.Flow;
import com.ii.mobile.flow.FlowBinder;
import com.ii.mobile.flow.FlowRestService;
import com.ii.mobile.flow.SyncCallback;
import com.ii.mobile.flow.UpdateController;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.SendMessage;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;
import com.ii.mobile.util.PrettyPrint;

/**

 */
public class InstantMessageFragment extends Fragment implements NamedFragment, SyncCallback {
	private EditText chatInputWindow = null;
	protected TextView chatOutputWindow = null;
	private String currentText = "";
	private View view;
	private String title = "Instant Message";
	protected static InstantMessageFragment instantMessageFragment = null;
	private final boolean wantSampleText = false;

	// private boolean running = false;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		L.out("InstantMessageFragment: " + bundle);

	}

	@Override
	public void onPause() {
		super.onPause();
		// running = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		// running = true;
		if (getActivity() == null) {
			L.out("Unable to get activity for actionPager!");
			return;
		}
		View view = getActivity().findViewById(R.id.actionPager);
		if (view == null) {
			L.out("Unable to get view for actionPager!");
			return;
		}
		// view.setVisibility(View.GONE);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (instantMessageFragment != null)
			UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTOR_STATUS);
	}

	public void addDebugLongClick() {

		chatOutputWindow.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {

				Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(400);

				GetActorStatus getActorStatus = Flow.getFlow().getActorStatus("foo");
				String tmp = "No Actor Status";
				if (getActorStatus != null) {
					// Flow.getFlow().getActionHistory(getActorStatus);
					getActorStatus.json = null;
					addMessage(PrettyPrint.formatPrint(getActorStatus.getNewJson()), "#00ff00");
					tmp = "Actor Status: "
							+ StaticFlow.INSTANCE.findActorStatusName(getActorStatus.getActorStatusId());
					String actionId = getActorStatus.getActionId();
					if (actionId != null) {
						tmp += "\nAction Status: "
								+ StaticFlow.INSTANCE.findActionStatusName(getActorStatus.getActionStatusId());
						GetActionStatus getActionStatus = Flow.getFlow().getActionStatus(actionId);
						if (getActionStatus != null) {
							tmp += "\nAction actionNumber: " + getActionStatus.getActionNumber();
							// getActionStatus.json = null;
							addMessage(PrettyPrint.formatPrint(getActionStatus.getJson()), "#0000ff");
						} else {
							addMessage("failed to get ActionStatus", "#ff0000");
						}
					}
					MyToast.show(tmp);
				} else {
					addMessage("failed to get ActorStatus", "#ff0000");
				}
				if (getActorStatus != null)
					addMessage(PrettyPrint.formatPrintNormal(getActorStatus.toString()), "#000000");
				return true;
			}
		});
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			// We have different layouts, and in one of them this
			// fragment's containing frame doesn't exist. The fragment
			// may still be created from its saved state, but there is
			// no reason to try to create its view hierarchy because it
			// won't be displayed. Note this is not needed -- we could
			// just run the code below, where we would create and return
			// the view hierarchy; it would just never be used.
			return null;
		}

		// if (view != null)
		// return view;
		view = inflater.inflate(R.layout.frag_instant_message, container, false);
		chatInputWindow = (EditText) view.findViewById(R.id.chatInputWindow);

		chatInputWindow.addTextChangedListener(new TextValidator() {
		});
		// View foo = view.findViewById(R.id.chatOutputWindow);
		// // L.out("foo: " + foo);
		chatOutputWindow = (TextView) view.findViewById(R.id.chatOutputWindow);
		final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView);

		// scrollView.setFadingEdgeLength(150);
		TextView.OnEditorActionListener inputListener = new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView editView, int actionId, KeyEvent event) {
				L.out("rehello: " + editView.getText());
				String temp = editView.getText().toString();
				temp = temp.replace("\"", "");
				sendMessage(temp);
				editView.setText("");
				InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(chatInputWindow.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				scrollView.post(new Runnable() {

					@Override
					public void run() {
						L.out("running");
						scrollView.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});
				return true;
			}
		};

		addDebugLongClick();

		chatInputWindow.setOnEditorActionListener(inputListener);
		if (currentText.length() == 0) {
			if (wantSampleText)
				testDisplay();
		}
		else
			chatOutputWindow.setText(Html.fromHtml(currentText));
		scrollView.post(new Runnable() {

			@Override
			public void run() {
				// L.out("running");
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
		if (instantMessageFragment != null)
			UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);
		return view;
	}

	public void setPrimary() {
		L.out("set primary");
		instantMessageFragment = this;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private void testDisplay() {
		String[] dialog = getDialog();
		for (int i = 0; i < dialog.length; i++) {
			String temp = dialog[i];
			receiveMessage("henry", temp);
		}
		// sendMessage("I NEED A BREAK");
		// receiveMessage("ivan", "I see by your IPS you are on break!");
	}

	public String[] getDialog() {
		return SHORT_DIALOGUE;
	}

	private void sendMessage(String message) {
		User user = User.getUser();
		String sentDate = L.toDateAMPM(new GregorianCalendar().getTimeInMillis());
		addMessage(user.getUsername(), sentDate, message, true);
		String to = "kim.fairchild@iicorporate.com";
		SendMessage sendMessage = new SendMessage(user.getUsername(), sentDate, message, to);
		FlowBinder.updateLocalDatabase(FlowRestService.SEND_MESSAGE, sendMessage);
		// Flow.getFlow().sendMessage(message, to);
	}

	public static String convertDate(String input) {
		if (input == null)
			input = "1999-03-11T07:01:00.000Z";
		TimeZone utc = TimeZone.getTimeZone("UTC");
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		f.setTimeZone(utc);
		GregorianCalendar gregorianCalendar = new GregorianCalendar(utc);
		try {
			gregorianCalendar.setTime(f.parse(input));
		} catch (ParseException e) {

			e.printStackTrace();
		}
		String tmp = "Ivan's Special toast! :)\nFlow Time: " + input
				+ "\nMobile Time: " + gregorianCalendar.getTime()
				+ "\nOutput Time: " + L.toDateAMPM(gregorianCalendar.getTimeInMillis());
		L.out(tmp);
		// MyToast.show(tmp);
		// MyToast.show(tmp);
		return L.toDateAMPM(gregorianCalendar.getTimeInMillis());
	}

	private void receiveMessage(String fromUserName, String message, String receivedDate) {
		addMessage(fromUserName, receivedDate, message, false);
	}

	private void receiveMessage(String fromUserName, String message) {
		String receivedDate = L.toDateAMPM(new GregorianCalendar().getTimeInMillis());
		addMessage(fromUserName, receivedDate, message, false);
	}

	public static void receivedMessage(Bundle data) {
		L.out("received message");
		if (instantMessageFragment == null) {
			L.out("*** ERROR No instantMessageFragment to receive message");
			return;
		}
		instantMessageFragment.addMessage(data);
	}

	// String string = "2013-03-05T18:05:05.000Z";
	// String defaultTimezone = TimeZone.getDefault().getID();
	// Date date = (new
	// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(string.replaceAll("Z$",
	// "+0000"));
	//
	// L.out("string: " + string);
	// L.out("defaultTimezone: " + defaultTimezone);
	// L.out("date: " + (new
	// SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).format(date));
	// receivedDate: 2014-02-24T13:36:00.000Z
	private String toDatePretty(String receivedDate) {
		test(receivedDate);
		L.out("toDatePretty: " + receivedDate);
		// receivedDate = clean(receivedDate);
		L.out("clean: " + receivedDate);
		String defaultTimezone = TimeZone.getDefault().getID();
		L.out("defaultTimezone: " + defaultTimezone);
		// receivedDate = receivedDate.replaceAll("Z$", "+0000");
		SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		Calendar cal = Calendar.getInstance();
		// originalFormat.setTimeZone(cal.getTimeZone());
		SimpleDateFormat targetFormat = new SimpleDateFormat("hh:mm aa");
		targetFormat.setTimeZone(cal.getTimeZone());
		Date date;

		String temp = "";
		try {
			date = originalFormat.parse(receivedDate);
			L.out("Old Format :   " + originalFormat.format(date));
			L.out("New Format :   " + targetFormat.format(date));
			temp = targetFormat.format(date);

		} catch (Exception ex) {
			L.out("ERROR: date parsing error for: " + receivedDate);
		}
		return temp;
	}

	private void test(String string) {
		// String string = "2013-03-05T18:05:05.000Z";
		try {
			String defaultTimezone = TimeZone.getDefault().getID();
			Date date = (new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")).parse(string.replaceAll("Z$", "+0000"));

			L.out("string: " + string);
			L.out("defaultTimezone: " + defaultTimezone);
			L.out("date: " + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).format(date));
		} catch (Exception ex) {
			L.out("ERROR: date parsing error for: " + string);
		}
	}

	private String clean(String receivedDate) {
		int index = receivedDate.indexOf("T");
		if (index == -1)
			return receivedDate;
		int jindex = receivedDate.indexOf(".", index);
		if (jindex == -1)
			return receivedDate;
		return receivedDate.substring(index + 1, jindex);
	}

	private String parseDate(String receivedDate) {
		L.out("receivedDate: " + receivedDate);
		if (receivedDate == null) {
			String sentDate = L.toDateSecondAMPM(new GregorianCalendar().getTimeInMillis());
			return sentDate;
		}
		Long temp = L.getLong(receivedDate);
		if (temp != 0)
			return L.toDateAMPM(temp);

		return toDatePretty(receivedDate);
		// int index = receivedDate.indexOf("T");
		// if (index == -1)
		// return receivedDate;
		// int jindex = receivedDate.indexOf(".", index);
		// if (jindex == -1)
		// return receivedDate;
		// return receivedDate.substring(index + 1, jindex);
	}

	protected void addMessage(Bundle data) {
		String message = data.getString(Tickler.TEXT_MESSAGE);
		String receivedDate = data.getString(Tickler.RECEIVED_DATE);
		String fromUserName = data.getString(Tickler.FROM_USER_NAME);
		receivedDate = convertDate(receivedDate);
		receiveMessage(fromUserName, message, receivedDate);
		if (view == null)
			return;
		final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView);
		scrollView.post(new Runnable() {

			@Override
			public void run() {
				L.out("running");
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
		// AudioPlayer.INSTANCE.playSound(AudioPlayer.NEW_MESSAGE);
	}

	private void addMessage(String fromUserName, String receivedDate, String message, boolean me) {
		String color = "#2c96f7";
		// String color = "#0000FF";
		if (me)
			color = "#000000";
		currentText += "<br>" + "<font color=" + color + ">" + receivedDate
				+ " " + fromUserName.replace("@iicorporate.com", "")
				+ ": "
				+ message + "</font>";
		if (chatOutputWindow != null)
			chatOutputWindow.setText(Html.fromHtml(currentText));

	}

	private void addMessage(String message, String color) {

		currentText += "<br>" + "<font color=" + color + ">"
				+ message + "</font>";
		if (chatOutputWindow != null)
			chatOutputWindow.setText(Html.fromHtml(currentText));

	}

	@Override
	public void update() {
		L.out("InstantMessageFragment update");
	}

	public static final String[] SHORT_DIALOGUE =
	{
			"Now is the winter of our discontent",
			"Made glorious summer by this sun of York;",
			"And all the clouds that lour'd upon our house",
			"In the deep bosom of the ocean buried.",
			"Now are our brows bound with victorious wreaths;",
			"Our bruised arms hung up for monuments;",
			"Our stern alarums changed to merry meetings,",
			"Dive, thoughts, down to my soul: here",
			"Clarence comes."
	};

	public static final String[] DIALOGUE =
	{
			"Now is the winter of our discontent",
			"Made glorious summer by this sun of York;",
			"And all the clouds that lour'd upon our house",
			"In the deep bosom of the ocean buried.",
			"Now are our brows bound with victorious wreaths;",
			"Our bruised arms hung up for monuments;",
			"Our stern alarums changed to merry meetings,",
			"Our dreadful marches to delightful measures.",
			"Grim-visaged war hath smooth'd his wrinkled front;",
			"And now, instead of mounting barded steeds",
			"To fright the souls of fearful adversaries,",
			"He capers nimbly in a lady's chamber",
			"To the lascivious pleasing of a lute.",
			"But I, that am not shaped for sportive tricks,",
			"Nor made to court an amorous looking-glass;",
			"I, that am rudely stamp'd, and want love's majesty",
			"To strut before a wanton ambling nymph;",
			"I, that am curtail'd of this fair proportion,",
			"Cheated of feature by dissembling nature,",
			"Deformed, unfinish'd, sent before my time",
			"Into this breathing world, scarce half made up,",
			"And that so lamely and unfashionable",
			"That dogs bark at me as I halt by them;",
			"Why, I, in this weak piping time of peace,",
			"Have no delight to pass away the time,",
			"Unless to spy my shadow in the sun",
			"And descant on mine own deformity:",
			"And therefore, since I cannot prove a lover,",
			"To entertain these fair well-spoken days,",
			"I am determined to prove a villain",
			"And hate the idle pleasures of these days.",
			"Plots have I laid, inductions dangerous,",
			"By drunken prophecies, libels and dreams,",
			"To set my brother Clarence and the king",
			"In deadly hate the one against the other:",
			"And if King Edward be as true and just",
			"As I am subtle, false and treacherous,",
			"This day should Clarence closely be mew'd up,",
			"About a prophecy, which says that 'G'",
			"Of Edward's heirs the murderer shall be.",
			"Dive, thoughts, down to my soul: here",
			"Clarence comes."
	};

	@Override
	public void callback(GJon gJon, String payloadName) {
		L.out("callback: " + payloadName);
		// receiveMessage("I/O", gJon.getNewJson());
	}

	@Override
	public boolean wantActions() {
		return true;
	}

}

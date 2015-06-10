package com.ii.mobile.beacon.fragments;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ii.mobile.beacon.R;
import com.ii.mobile.beacon.StateChange;
import com.ii.mobile.flow.SyncCallback;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.util.L;
import com.ii.mobile.util.PrettyPrint;

/**

 */
public class EventOutputFragment extends Fragment implements NamedFragment, SyncCallback {
	// private final EditText chatInputWindow = null;
	protected TextView chatOutputWindow = null;
	private static String currentText = "";
	private View view;
	private String title = "Event Log";
	public static EventOutputFragment eventOutputFragment = null;

	private static boolean init = false;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		L.out("EventOutputFragment: " + bundle);
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
			L.out("Unable to get activity for EventOutputFragment!");
			return;
		}

		// View view = getActivity().findViewById(R.id.actionPager);
		// if (view == null) {
		// L.out("Unable to get view for actionPager!");
		// return;
		// }
		// view.setVisibility(View.GONE);
		chatOutputWindow.setText(Html.fromHtml(currentText));
	}

	private void displayHelp() {
		String breakLine = "<br>";
		String help[] = getDialog();
		String temp = "<html><font color=" + "#A9A9A9" + ">StateChanges (" + help.length + "): "
				+ breakLine;
		for (int i = 0; i < help.length; i++) {
			temp += "&#160;&#160;&#160;&#160;&#160" + help[i] + breakLine;
		}
		temp += "</font>";
		if (chatOutputWindow != null) {
			init = true;
			chatOutputWindow.setText(Html.fromHtml(temp));
			currentText = temp;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// if (eventOutputFragment != null)
		// UpdateController.INSTANCE.unRegisterCallback(this,
		// FlowRestService.GET_ACTOR_STATUS);
	}

	public void addDebugLongClick() {

		chatOutputWindow.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {

				Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(400);

				addMessage(PrettyPrint.formatPrintNormal("Test for log event"), "#000000");
				return true;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		eventOutputFragment = this;
		view = inflater.inflate(R.layout.beacon_message, container, false);
		chatOutputWindow = (TextView) view.findViewById(R.id.chatOutputWindow);
		// addDebugLongClick();
		if (!init) {
			displayHelp();
		}
		return view;
	}

	public void setPrimary() {
		L.out("set primary");
		eventOutputFragment = this;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String[] getDialog() {
		return StateChange.getDialog();

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

	public static void receivedMessage(Bundle data) {
		L.out("received message");
		if (eventOutputFragment == null) {
			L.out("*** ERROR No instantMessageFragment to receive message");
			return;
		}
		eventOutputFragment.addMessage(data);
	}

	protected void addMessage(Bundle data) {
		String message = data.getString(Tickler.TEXT_MESSAGE);
		String receivedDate = data.getString(Tickler.RECEIVED_DATE);
		String fromUserName = data.getString(Tickler.FROM_USER_NAME);
		receivedDate = convertDate(receivedDate);
		receiveMessage(fromUserName, message, receivedDate);

		final ScrollView scrollView = (ScrollView) view.findViewById(R.id.chatScrollView);
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

	private static String lastMessage = "";

	public static void receiveMessage(String fromUserName, String message) {
		if (eventOutputFragment == null)
			return;
		String receivedDate = L.toDateAMPM(new GregorianCalendar().getTimeInMillis());
		if (!lastMessage.equals(receivedDate + message)) {
			eventOutputFragment.addMessage(fromUserName, receivedDate, message, false);
			// SampleFragment.log(fromUserName + ": " + message);
		} else
			lastMessage = receivedDate + message;
	}

	public void addMessage(String message, String color) {

		currentText += "<br>" + "<font color=" + color + ">"
				+ message + "</font>";
		if (chatOutputWindow != null)
			chatOutputWindow.setText(Html.fromHtml(currentText));

	}

	@Override
	public void update() {
		L.out("InstantMessageFragment update");
	}

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

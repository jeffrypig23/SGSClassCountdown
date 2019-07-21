package com.spud.ClassCountdown.Activities;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.spud.ClassCountdown.R;
import com.spud.ClassCountdown.Regime.Class;
import com.spud.ClassCountdown.Regime.Regime;
import com.spud.ClassCountdown.Timer;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Stephen Ogden on 2/6/19.
 */
public class EditClasses extends android.support.v7.app.AppCompatActivity {

	public static String name;
	public static int[] dates;

	private LinearLayout classList;

	private View classNameView, startTimeView, endTimeView;

	private ArrayList<Class> classes = new ArrayList<>();

	@android.annotation.SuppressLint({"SetTextI18n", "InflateParams"})
	protected void onCreate(android.os.Bundle bundle) {
		super.onCreate(bundle);

		this.setContentView(R.layout.editclasses);

		// Setup the cancel button to just finish the activity
		this.findViewById(R.id.cancel).setOnClickListener((event) -> this.finish());

		// Update the header to also display the name of the regime
		((TextView) this.findViewById(R.id.classHeaderText)).setText("Adding classes for " + EditClasses.name);

		// Find the class view layout
		this.classList = this.findViewById(R.id.classList);

		// Get the layout inflater
		android.view.LayoutInflater inflater = this.getLayoutInflater();

		// Setup all the popup views
		this.classNameView = inflater.inflate(R.layout.classname, null);
		this.startTimeView = inflater.inflate(R.layout.classstarttime, null);
		this.endTimeView = inflater.inflate(R.layout.classendtime, null);

		android.database.sqlite.SQLiteDatabase database = Regime.getDatabase();

		// Get all the classes from the database
		android.database.Cursor result = database.rawQuery("SELECT classes FROM regimes WHERE name = \"" + EditClasses.name + "\";", null);

		if (result.moveToFirst()) {
			// Add the classes to the database
			for (int i = 0; i < result.getCount(); i++) {
				this.classes.addAll(java.util.Arrays.asList(java.util.Objects.requireNonNull(Regime.parseClasses(result.getString(result.getColumnIndex("classes"))))));
				// Move to the next row (break if it cant)
				if (!result.moveToNext()) {
					break;
				}
			}
		}

		result.close();
		database.close();

		// Setup the save button
		this.findViewById(R.id.save).setOnClickListener((event -> {
			try {
				new Regime(EditClasses.name, EditClasses.dates, classes.toArray(new Class[0])).saveRegime();
			} catch (org.json.JSONException e) {
				e.printStackTrace();
			}
			// Save the classes array to the database
			this.finish();
		}));

	}

	protected void onResume() {
		super.onResume();
		this.generateClasses();
	}

	// https://developer.android.com/guide/topics/ui/dialogs#java
	private AlertDialog setClassNames(String name, long startTime, long endTime, String customName) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		// Prepopulate any variables
		EditText officialName = this.classNameView.findViewById(R.id.className);
		if (!name.equals("")) {
			officialName.setText(name);
		}

		EditText customNameText = this.classNameView.findViewById(R.id.customClassName);
		if (!customName.equals("")) {
			customNameText.setText(customName);
		}

		dialog.setPositiveButton(R.string.next, (event, id) -> {
			// The specified child already has a parent. You must call removeView() on the child's parent first.
			ActivityHelper.killView(this.classNameView);
			AlertDialog startTimeDialog = this.setStartTime(officialName.getText().toString(), startTime, endTime, customNameText.getText().toString());
			startTimeDialog.setView(this.startTimeView);
			startTimeDialog.show();
		}).setNegativeButton(R.string.cancel, (event, id) -> ActivityHelper.killView((this.classNameView)));

		return dialog.create();
	}

	private AlertDialog setStartTime(String name, long startTime, long endTime, String customName) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		// Start time
		TimePicker start = this.startTimeView.findViewById(R.id.startTime);
		if (Build.VERSION.SDK_INT < 23) {
			start.setCurrentHour(Timer.getHour(startTime));
			start.setCurrentMinute(Timer.getMinute(startTime));
		} else {
			start.setHour(Timer.getHour(startTime));
			start.setMinute(Timer.getMinute(startTime));
		}

		dialog.setPositiveButton(R.string.next, (event, id) -> {
			long s;

			if (Build.VERSION.SDK_INT < 23) {
				s = start.getCurrentHour() * 3600 + start.getCurrentMinute() * 60;
			} else {
				s = start.getHour() * 3600 + start.getMinute() * 60;
			}

			ActivityHelper.killView(this.startTimeView);
			AlertDialog endTimeDialog = this.setEndTime(name, s, endTime, customName);
			endTimeDialog.setView(this.endTimeView);
			endTimeDialog.show();
		}).setNegativeButton(R.string.back, (event, id) -> {
			ActivityHelper.killView(this.startTimeView);
			AlertDialog classNameDialog = this.setClassNames(name, startTime, endTime, customName);
			classNameDialog.setView(this.classNameView);
			classNameDialog.show();
		});

		return dialog.create();
	}

	private AlertDialog setEndTime(String name, long startTime, long endTime, String customName) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		// End time
		TimePicker end = this.endTimeView.findViewById(R.id.endTime);
		if (Build.VERSION.SDK_INT < 23) {
			end.setCurrentHour(Timer.getHour(endTime));
			end.setCurrentMinute(Timer.getMinute(endTime));
		} else {
			end.setHour(Timer.getHour(endTime));
			end.setMinute(Timer.getMinute(endTime));
		}

		dialog.setPositiveButton(R.string.save, (event, id) -> {
			long e;

			if (Build.VERSION.SDK_INT < 23) {
				e = end.getCurrentHour() * 3600 + end.getCurrentMinute() * 60;
			} else {
				e = end.getHour() * 3600 + end.getMinute() * 60;
			}

			Class newClass = new Class(name, startTime, e, customName);

			this.classes.add(newClass);

			ActivityHelper.killView(this.endTimeView);
			this.generateClasses();
		}).setNegativeButton(R.string.back, (event, id) -> {
			ActivityHelper.killView(this.endTimeView);
			AlertDialog startTimeDialog = this.setStartTime(name, startTime, endTime, customName);
			startTimeDialog.setView(this.startTimeView);
			startTimeDialog.show();
		});

		return dialog.create();
	}

	private void generateClasses() {

		this.classList.removeAllViews();

		for (Class c : this.classes) {

			// Generate the title of the class
			TextView title = this.generateTextView();
			title.setText(c.hasCustomName() ? String.format(Locale.ENGLISH, "%s (%s)", c.getName(false), c.getName(true)) : c.getName(false));

			// Generate the time field of the class
			TextView time = this.generateTextView();

			// Get the start and end times
			long startTime = c.getStartTime(), endTime = c.getEndTime();

			String timeString;

			// Check if user is using 24 hour time
			if (android.text.format.DateFormat.is24HourFormat(this)) {
				timeString = String.format(Locale.ENGLISH, "%02d:%02d - %02d:%02d", Timer.getHour(startTime),
						Timer.getMinute(startTime), Timer.getHour(endTime), Timer.getMinute(endTime));
			} else {
				// If they are not, add in AM and PM symbols
				String start = this.get12Time(startTime), end = this.get12Time(endTime);
				timeString = String.format(Locale.ENGLISH, "%s - %s", start, end);
			}

			time.setText(timeString);

			Button edit = this.generateButton(R.string.edit);
			edit.setOnClickListener((e) -> {
				this.classes.remove(c);
				AlertDialog dialog = this.setClassNames(c.getName(false), c.getStartTime(), c.getEndTime(), c.hasCustomName() ? c.getName(true) : "");
				dialog.setView(this.classNameView);
				dialog.show();
			});

			Button delete = this.generateButton(R.string.delete);
			delete.setOnClickListener((e) -> {
				this.classes.remove(c);
				this.generateClasses();
			});

			LinearLayout classDetails = new LinearLayout(this);
			classDetails.setOrientation(LinearLayout.HORIZONTAL);

			classDetails.addView(title, 0);
			classDetails.addView(time, 1);
			classDetails.addView(edit, 2);
			classDetails.addView(delete, 3);

			this.classList.addView(classDetails);

		}

		// Add a button to add a new class
		Button add = this.generateButton("Add new class");
		LinearLayout.LayoutParams p = (LinearLayout.LayoutParams) add.getLayoutParams();
		p.setMargins(0, 0, 10, 0);
		add.setLayoutParams(p);
		add.setOnClickListener((event) -> {
			AlertDialog dialog = this.setClassNames("", Timer.getCurrentTime(), Timer.getCurrentTime(), "");
			dialog.setView(this.classNameView);
			dialog.show();
		});
		this.classList.addView(add);

	}

	private TextView generateTextView() {
		TextView t = new TextView(this);
		t.setTextColor(Color.WHITE);
		t.setTextSize(12);
		t.setGravity(android.view.Gravity.CENTER_VERTICAL);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		p.setMargins(0, 0, 20, 0);
		t.setLayoutParams(p);
		return t;
	}

	private Button generateButton(String text) {
		Button b = new Button(this);
		b.setText(text);
		b.setTextSize(12);
		b.setTextColor(Color.BLACK);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		b.setLayoutParams(p);
		return b;
	}

	private Button generateButton(int text) {
		String string = this.getResources().getText(text).toString();
		return this.generateButton(string);
	}

	private String get12Time(long seconds) {

		// Determine the hour and minute of the class's time
		int hour = Timer.getHour(seconds), minute = Timer.getMinute(seconds);

		// Determine whether or not it is during the AM hour or the PM hour
		boolean PM = hour >= 12;

		// If the hour is 0, switch it to display 12 AM
		hour = hour == 0 ? 12 : hour;

		// If the hour is greater than 12 (will be in the PM range) subtract 12
		hour = hour > 12 ? hour - 12 : hour;

		// Format the string and return it
		return String.format("%d:%02d %s", hour, minute, PM ? "PM" : "AM");

	}

}

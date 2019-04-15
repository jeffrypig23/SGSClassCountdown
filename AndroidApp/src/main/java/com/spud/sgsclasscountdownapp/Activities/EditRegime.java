package com.spud.sgsclasscountdownapp.Activities;

import android.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.spud.sgsclasscountdownapp.R;
import com.spud.sgsclasscountdownapp.Regime.Regime;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Stephen Ogden on 2/6/19.
 */
public class EditRegime extends android.support.v7.app.AppCompatActivity {

	private ArrayList<Regime> regimes = new ArrayList<>();

	private LinearLayout regimeList;

	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.editregime);

		// Linear layout to house all the regimes
		regimeList = this.findViewById(R.id.regimesList);

		// Setup the create new schedule button
		this.findViewById(R.id.newSchedule).setOnClickListener((event) -> this.createNewRegime().show());

		// Setup the save button
		this.findViewById(R.id.back).setOnClickListener((event) -> finish());

	}

	private void generateRegimeView() {

		regimeList.removeAllViews();

		for (Regime r : regimes) {

			android.util.Log.d("generateRegimeView", String.format("Generating view for regime %s", r.getName()));

			TextView title = this.createTextView();
			title.setText(r.getName());

			TextView classCount = this.createTextView();
			classCount.setText(r.getClassCount() == 1 ? "1 class" : r.getClassCount() + " classes");

			Button edit = new Button(this);
			edit.setText(R.string.edit);
			edit.setOnClickListener((e) -> {
				boolean su = false, m = false, tu = false, w = false, th = false, f = false, sa = false;
				for (int i : r.getDateOccurrence()) {
					if (i == Calendar.SUNDAY) {
						su = true;
					} else if (i == Calendar.MONDAY) {
						m = true;
					} else if (i == Calendar.TUESDAY) {
						tu = true;
					} else if (i == Calendar.WEDNESDAY) {
						w = true;
					} else if (i == Calendar.THURSDAY) {
						th = true;
					} else if (i == Calendar.FRIDAY) {
						f = true;
					} else if (i == Calendar.SATURDAY) {
						sa = true;
					}
				}
				this.createNewRegime(r.getName(), su, m, tu, w, th, f, sa).show();
			});
			edit.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

			Button delete = new Button(this);
			delete.setText(R.string.delete);
			delete.setOnClickListener((e) -> {
				regimes.remove(r);
				r.removeRegime();
				this.generateRegimeView();
			});
			delete.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));

			LinearLayout l = new LinearLayout(this);
			l.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			l.setOrientation(LinearLayout.HORIZONTAL);
			l.addView(title, 0);
			l.addView(classCount, 1);
			l.addView(edit, 2);
			l.addView(delete, 3);

			regimeList.addView(l);
		}

	}

	protected void onResume() {
		super.onResume();

		this.regimes.clear();

		android.database.sqlite.SQLiteDatabase database = Regime.getDatabase();

		// Get all the regimes from the database
		android.database.Cursor result = database.rawQuery("SELECT * FROM regimes;", null);

		// Move the cursor to the first row
		if (result.moveToFirst()) {
			for (int i = 0; i < result.getCount(); i++) {
				regimes.add(new Regime(result.getString(result.getColumnIndex("name")),
						Regime.parseDates(result.getString(result.getColumnIndex("occurrence"))),
						Regime.parseClasses(result.getString(result.getColumnIndex("classes")))));
				// Move to the next row (break if it cant)
				if (!result.moveToNext()) {
					break;
				}
			}
		}
		result.close();
		database.close();

		this.generateRegimeView();
	}

	// https://developer.android.com/guide/topics/ui/dialogs#java
	// TODO Use setMultiChoiceItems option in builder
	private AlertDialog createNewRegime(String name, boolean su, boolean m, boolean tu, boolean w, boolean th, boolean f, boolean sa) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		// Get the layout inflater
		android.view.LayoutInflater inflater = this.getLayoutInflater();

		android.view.View creationDialog = inflater.inflate(R.layout.createnewregime, null);

		// TODO
		dialog.setMultiChoiceItems(new String[] {"Sunday", "Monday", "Tuesday"}, null, null);

		final android.widget.EditText regimeName = creationDialog.findViewById(R.id.name);
		final android.widget.CheckBox sunday = creationDialog.findViewById(R.id.sunday),
				monday = creationDialog.findViewById(R.id.monday),
				tuesday = creationDialog.findViewById(R.id.tuesday),
				wednesday = creationDialog.findViewById(R.id.wednesday),
				thursday = creationDialog.findViewById(R.id.thursday),
				friday = creationDialog.findViewById(R.id.friday),
				saturday = creationDialog.findViewById(R.id.saturday);

		// Prepopulate any variables
		if (!name.equals("")) {
			regimeName.setText(name);
		}
		sunday.setChecked(su);
		monday.setChecked(m);
		tuesday.setChecked(tu);
		wednesday.setChecked(w);
		thursday.setChecked(th);
		friday.setChecked(f);
		saturday.setChecked(sa);

		dialog.setView(creationDialog);
		dialog.setPositiveButton("Add classes", (event, id) -> {
			EditClasses.name = regimeName.getText().toString();
			ArrayList<Integer> dates = new ArrayList<>();
			// Get which dates are checked
			if (sunday.isChecked()) {
				dates.add(Calendar.SUNDAY);
			}
			if (monday.isChecked()) {
				dates.add(Calendar.MONDAY);
			}
			if (tuesday.isChecked()) {
				dates.add(Calendar.TUESDAY);
			}
			if (wednesday.isChecked()) {
				dates.add(Calendar.WEDNESDAY);
			}
			if (thursday.isChecked()) {
				dates.add(Calendar.THURSDAY);
			}
			if (friday.isChecked()) {
				dates.add(Calendar.FRIDAY);
			}
			if (saturday.isChecked()) {
				dates.add(Calendar.SATURDAY);
			}

			if (dates.size() != 0) {
				EditClasses.dates = new int[dates.size()];
				for (int i = 0; i < dates.size(); i++) {
					EditClasses.dates[i] = dates.get(i);
				}
				this.startActivity(new android.content.Intent(EditRegime.this, EditClasses.class));
			}
		}).setNegativeButton(R.string.cancel, null);

		return dialog.create();

	}

	private AlertDialog createNewRegime() {
		return this.createNewRegime("", false, false, false, false, false, false, false);
	}

	private TextView createTextView() {
		TextView t = new TextView(this);
		t.setGravity(android.view.Gravity.CENTER_VERTICAL);
		t.setTextColor(android.graphics.Color.WHITE);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
		p.setMargins(0, 0, 20, 0);
		t.setLayoutParams(p);
		return t;
	}
}

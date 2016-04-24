package com.mayaswell.patientstatus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 */
public class MainActivity extends AppCompatActivity {
	PatientQuery dbQuery;
	private ListView patientView;
	private PatientAdapter patientAdapter;
	Patient selectedPatient;
	ArrayList<SearchParameters> searches;
	int currentSearchParametersIndex = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		selectedPatient = null;
		searches = new ArrayList<SearchParameters>();

		setContentView(R.layout.activity_main);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		String patientURLBase = getResources().getString(R.string.patient_url_base);
		String statusURLBase = getResources().getString(R.string.status_url_base);

		patientAdapter = new PatientAdapter(this, R.layout.patient_list_item);
		patientView = (ListView) findViewById(R.id.patientView);
		patientView.setAdapter(patientAdapter);
		patientView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		patientView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				selectedPatient = patientAdapter.getItem(position);
			}
		});

		dbQuery = new PatientQuery(patientURLBase, statusURLBase);
		dbQuery.setListener(new PatientQuery.Listener() {

			@Override
			public void ready() {
				setupInterface();
			}

			@Override
			public void results(Collection<Patient> patients) {
				displayPatients(patients);
			}

			@Override
			public void error(String msg) {
				errorDialog(msg);
			}
		});
	}

	private void displayPatients(Collection<Patient> patients) {
		patientAdapter.clear();
		if (patients != null) {
			Log.d("Main", "got " + patients.size() + "results");
		}
		patientAdapter.addAll(patients);
	}

	@Override
	protected void onStart()
	{
		super.onStart();
		dbQuery.setup();
	}

	private void setupInterface() {
		FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				searchDialog();
			}
		});
	}

	protected void errorDialog(String msg) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Ooops! Error ... ");
		dialog.setMessage(Html.fromHtml(msg));
		dialog.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

	protected void searchDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle("Search for Patient");
		dialog.setIcon(android.R.drawable.ic_menu_search);
		final View editParams = getLayoutInflater().inflate(R.layout.search_dialog_params, null);
		dialog.setView(editParams);
		dialog.setCancelable(false);
		final EditText fnameEdit = (EditText) editParams.findViewById(R.id.editText1);
		final EditText snameEdit = (EditText) editParams.findViewById(R.id.editText2);
		final EditText statusEdit = (EditText) editParams.findViewById(R.id.editText3);
		dialog.setPositiveButton("Do it!", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				final String fname = (fnameEdit != null) ? fnameEdit.getText().toString() : "";
				final String sname = (snameEdit != null) ? snameEdit.getText().toString() : "";
				final String status = (statusEdit != null) ? statusEdit.getText().toString() : "";
				searchPatient(fname, sname, status);
				SearchParameters sp = new SearchParameters(fname, sname, status);
				if (currentSearchParametersIndex < 0) {
					currentSearchParametersIndex = 0;
				}
				if (currentSearchParametersIndex >= searches.size()) {
					searches.add(sp);
					currentSearchParametersIndex = searches.size();
				} else {
					searches.set(currentSearchParametersIndex, sp);
				}
				currentSearchParametersIndex++;
				dialog.dismiss();
			}
		});
		dialog.setNeutralButton("Reset", null); // set listener after 'show' so it won't dismiss
		dialog.setNegativeButton("Cancel!", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		});
		dialog.show().getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				fnameEdit.setText("");
				snameEdit.setText("");
				statusEdit.setText("");
			}
		});
	}

	private void searchPatient(String fname, String sname, String status) {
		dbQuery.seatchPatientMatching(fname, sname, status);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		currentSearchParametersIndex--;
		if (currentSearchParametersIndex >= 0 && searches.size() > 1) {
			if (currentSearchParametersIndex >= searches.size()) {
				currentSearchParametersIndex = searches.size() - 2;
			}
			final SearchParameters sp = searches.get(currentSearchParametersIndex);
			if (sp != null) {
				Log.d("Main", "Redo previous " + sp.firstname+", "+sp.surname+", "+sp.status);
				searchPatient(sp.firstname, sp.surname, sp.status);
				return;
			}
		}
		super.onBackPressed();
	}
}

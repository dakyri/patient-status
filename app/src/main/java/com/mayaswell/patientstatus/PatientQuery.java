package com.mayaswell.patientstatus;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * I've wrapped what I hope are the most dirty assumptions into this wrapper class. As a first draft I've
 * downloaded all the preset data into a HashMap cache.
 *
 * Initial assumptions
 *  - patient data can reasonably fit in memory (realistically it's probably in a searchable database)
 *  - not likely to change over the lifespan of the app (so only needs to be grabbed once)
 */
public class PatientQuery {
	private final String patientURLBase;
	private final String statusURLBase;
	private Listener listener;

	HashMap<Integer, Patient> cacheDB;

	public interface Listener {
		void ready();
		void results(Collection<Patient> patients);
		void error(String msg);
	}

	public PatientQuery(String patientURLBase, String statusURLBase) {
		this.patientURLBase = patientURLBase;
		this.statusURLBase = statusURLBase;
		cacheDB = new HashMap<Integer, Patient>();
		listener = null;
	}

	/**
	 * requests the given page of patient data and loads into internal cache
	 * @param page
	 */
	protected void loadPatientData(int page) {
		JSONSlurper loader = new JSONSlurper() {
			@Override
			protected void onPostExecute(JSONObject result)
			{
				if (result == null) {
					notifyError(lastErrorMsg);
					return;
				}
				JSONArray resa = null;
				try {
					resa = result.getJSONArray("results");
					if (resa == null) {
						notifyError("No results in response for patient data");
						return;
					}
					for (int i=0; i<resa.length(); i++) {
						JSONObject resp = resa.getJSONObject(i);
						if (!cachePatientData(resp)) {
							return;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					notifyError("JSON error processing results for patient data");
				}
				int nextPage = 0;
				// we'll grab the next page if there is one else, we'll move on and grab statuses
				// getInt will throw an exception if 'next_page' is null, which is our endpoint
				try {
					nextPage = result.getInt("next_page");
				} catch (JSONException e) {
					nextPage = 0;
				}
				if (nextPage > 0) {
					loadPatientData(nextPage);
				} else {
					loadStatusData(1);
				}
			}

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
			}

			@Override
			protected void onProgressUpdate(Integer ... progressValues)
			{
				super.onProgressUpdate(progressValues);
			}
		};
		String targetURL = patientURLBase;
		if (page > 0) {
			targetURL += "?"+"page="+page;
		}
		loader.execute(targetURL);
	}

	/**
	 * requests the given page of status data and loads into cache
 	 * @param page
	 */
	protected void loadStatusData(int page) {
		JSONSlurper loader = new JSONSlurper() {
			@Override
			protected void onPostExecute(JSONObject result)
			{
				if (result == null) {
					notifyError(lastErrorMsg);
					return;
				}
				JSONArray resa = null;
				try {
					resa = result.getJSONArray("results");
					if (resa == null) {
						notifyError("No results in response for patient data");
						return;
					}
					for (int i=0; i<resa.length(); i++) {
						JSONObject resp = resa.getJSONObject(i);
						if (!cacheStatusData(resp)) {
							return;
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					notifyError("JSON error processing results for patient data");
				}
				int nextPage = 0;
				// we'll grab the next page if there is one else, we'll move on and grab statuses
				// getInt will throw an exception if 'next_page' is null, which is our endpoint
				try {
					nextPage = result.getInt("next_page");
				} catch (JSONException e) {
					nextPage = 0;
				}
				if (nextPage > 0) {
					loadStatusData(nextPage);
				} else {
					if (listener != null) {
						listener.ready();
					}
				}
			}

			@Override
			protected void onPreExecute()
			{
				super.onPreExecute();
			}

			@Override
			protected void onProgressUpdate(Integer ... progressValues)
			{
				super.onProgressUpdate(progressValues);
			}
		};
		String targetURL = statusURLBase;
		if (page > 0) {
			targetURL += "?"+"page="+page;
		}
		loader.execute(targetURL);
	}

	/**
	 * loads data for a particular patient record into the cache
	 * @param resp
	 * @return true if successful
	 */
	private boolean cachePatientData(JSONObject resp) {
		if (resp == null) {
			notifyError("Expected object in response for patient data");
			return false;
		}
		try {
			String firstname = resp.getString("name");
			String surname = resp.getString("surname");
			int id = resp.getInt("id");
//			Log.d("PatientQuery", "Caching, found " + firstname + ", " + surname + ", " + id);
			cacheDB.put(id, new Patient(id, firstname, surname));
		} catch (JSONException e) {
			notifyError("Expected field in patient object in response for patient data");
			return false;
		}
		return true;
	}

	private boolean cacheStatusData(JSONObject resp) {
		if (resp == null) {
			notifyError("Expected object in response for status data");
			return false;
		}
		try {
			int patientId = resp.getInt("patient");
			String status = resp.getString("status");
			int id = resp.getInt("id");
			Patient p = cacheDB.get(patientId);
//			Log.d("PatientQuery", "Caching, found status " + patientId + ", " + status + ", " + id);

			if (p != null) {
				p.status = status;
				Log.d("PatientQuery", "Caching, found status and patient " + p.firstName + ", "+p.surName + ", " + status + ", " + id);
			}
		} catch (JSONException e) {
			notifyError("Expected field in patient object in response for patient data");
			return false;
		}
		return true;
	}

	/**
	 * @param id
	 * @return the cached patient record for this id
	 */
	public Patient getPatient(int id) {
		return cacheDB.get(id);
	}

	/**
	 *
	 * @param fnmMatch
	 * @param snmMatch
	 * @param status
	 * @return
	 */
	private boolean getPatientsMatching(Collection<Patient> pList, String fnmMatch, String snmMatch, String status) {
		Iterator it = cacheDB.entrySet().iterator();
		boolean found = false;
		while (it.hasNext()) {
			HashMap.Entry kv = (HashMap.Entry) it.next();
			Patient p = (Patient)kv.getValue();
			if (isMatching(p, fnmMatch, snmMatch, status)) {
				Log.d("PatientQuery", "match "+p.toString());
				pList.add(p);
				found = true;
			}
		}
		if (listener != null) {
			listener.results(pList);
		}
		return found;
	}

	/**
	 * initiates a database search for the given patient
	 * @param fnmMatch
	 * @param snmMatch
	 * @param status
	 */
	public void seatchPatientMatching(String fnmMatch, String snmMatch, String status) {
		Log.d("PatientQuery ", "searching for " + (fnmMatch != null ? fnmMatch : "") + ", " + (snmMatch != null ? snmMatch : "") + ", "
				+ (status != null ? status : ""));
		getPatientsMatching(new ArrayList<Patient>(), fnmMatch, snmMatch, status);
	}


	/**
	 * matches if no restrictions are given, or if any of the given restrictions match
	 * @param p
	 * @param fnmMatch
	 * @param snmMatch
	 * @param status
	 * @return true it patient p matches the given parameters
	 */
	private boolean isMatching(Patient p, String fnmMatch, String snmMatch, String status) {
		if (p == null) {
			return false;
		}
		if ((fnmMatch == null || fnmMatch.equals(""))
				&& (snmMatch == null || snmMatch.equals(""))
				&& (status == null || status.equals(""))) {
			return true;
		}
//		Log.d("PatientQuery ", p.toString()+" -- "+(status!=null?status:"null"));
		if (p.firstName != null && fnmMatch != null && !fnmMatch.equals("")) {
			return p.firstName.matches(fnmMatch);
		}
		if (p.surName != null && snmMatch != null && !snmMatch.equals("")) {
			return p.surName.matches(snmMatch);
		}
		if (p.status != null && status != null && !status.equals("")) {
			return p.status.matches(status);
		}
		return false;
	}


	/**
	 * calls our listener if there's an error
	 * @param msg
	 */
	protected void notifyError(String msg) {
		if (listener != null) {
			listener.error(msg);
		}
	}

	/**
	 * does post construction initiatlizations that would be inappropriate fo the constructor.
	 * in particular, this kicks off a spate of requests to populate our cache.
	 */
	public void setup() {
		cacheDB = new HashMap<Integer, Patient>();
		loadPatientData(1);
	}

	/**
	 * sets up our listener
	 * @param l
	 */
	public void setListener(Listener l )
	{
		listener  = l;
	}

}

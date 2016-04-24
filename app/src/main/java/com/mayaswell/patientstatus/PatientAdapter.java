package com.mayaswell.patientstatus;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by dak on 4/24/2016.
 */
public class PatientAdapter extends ArrayAdapter<Patient> {
	public PatientAdapter(Context context, int resource) {
		super(context, resource);
	}

	public PatientAdapter(Context context, int resource, int textViewResourceId) {
		super(context, resource, textViewResourceId);
	}

	public PatientAdapter(Context context, int resource, Patient[] objects) {
		super(context, resource, objects);
	}

	public PatientAdapter(Context context, int textViewResourceId, List<Patient> objects) {
		super(context, textViewResourceId, objects);
	}

	public PatientAdapter(Context context, int resource, int textViewResourceId, Patient[] objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public PatientAdapter(Context context, int resource, int textViewResourceId, List<Patient> objects) {
		super(context, resource, textViewResourceId, objects);
	}

	public View getView(int position, View convertView, ViewGroup parent) {
//		if(convertView==null){
		final LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.patient_list_item, parent, false);
//		}
		final Patient p = getItem(position);
		final TextView nameView = (TextView) convertView.findViewById(R.id.itemNameView);
		final TextView statusView = (TextView) convertView.findViewById(R.id.itemStatusView);
		statusView.setText(p.status);
		String n="";
		final String sn = p.surName;
		final String fn = p.firstName;
		if (sn == null || sn.equals("")) {
			if (fn == null || fn.equals("")) {
				n = "Nobody";
			} else {
				n = fn;
			}
		} else if (fn == null || fn.equals("")) {
			n = sn;
		} else {
			n = fn + " " + sn;
		}
		Log.d("Adapter", "Got " + n + " id " + p.id);
		nameView.setText(n);
		return convertView;
	}

}

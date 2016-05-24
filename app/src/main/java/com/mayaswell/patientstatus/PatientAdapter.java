package com.mayaswell.patientstatus;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by dak on 4/24/2016.
 */
public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.ViewHolder>/*ArrayAdapter<Patient>*/ {
	ArrayList<Patient> dataSet = new ArrayList<Patient>();

	/**
	 * 	Provide a reference to the views for each data item
	 *  Complex data items may need more than one view per item, and
	 *  you provide access to all the views for a data item in a view holder
	 */
	public static class ViewHolder extends RecyclerView.ViewHolder {
		public RelativeLayout parent;
		protected TextView nameView;
		protected TextView statusView;
		public ViewHolder(RelativeLayout v) {
			super(v);
			parent = v;
			nameView = (TextView) v.findViewById(R.id.itemNameView);
			statusView = (TextView) v.findViewById(R.id.itemStatusView);
		}

		public void setToPatient(Patient p) {
			if (parent != null) {
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

			}
		}
	}

	public void clear() {
		dataSet.clear();
	}

	public void addAll(Collection<Patient> patients) {
		dataSet.addAll(patients);
		notifyDataSetChanged();
	}

	/**
	 *   create a new view
	 * @param parent
	 * @param viewType
	 * @return
	 */
	@Override
	public PatientAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		RelativeLayout v = (RelativeLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_list_item, parent, false);
		ViewHolder vh = new ViewHolder(v);
		return vh;
	}

	@Override
	public int getItemViewType(int pos) {
		return 0;
	}

	/**
	 * 	- get element from your dataset at this position
     *  - replace the contents of the view with that element
	 * @param holder
	 * @param position
	 */
	@Override
	public void onBindViewHolder(PatientAdapter.ViewHolder holder, int position) {
		holder.setToPatient(dataSet.get(position));
	}

	@Override
	public int getItemCount() {
		return dataSet.size();
	}
}

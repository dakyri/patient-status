package com.mayaswell.patientstatus;

/**
 * Created by dak on 4/24/2016.
 */
public class Patient {
	public String firstName;
	public String surName;
	public String status;
	public int id;

	public Patient(int id, String firstName, String surName) {
		this.id = id;
		this.firstName = firstName;
		this.surName = surName;
		status = "";
	}

	public Patient() {
		this(0, "", "");
	}

	public String toString() {
		return
			(firstName!=null?firstName:"") +" "+
			(surName!=null?surName:"") +", " +
			(status!=null?status:"");
	}
}

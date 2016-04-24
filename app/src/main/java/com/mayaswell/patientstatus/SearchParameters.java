package com.mayaswell.patientstatus;

/**
 * Created by dak on 4/24/2016.
 */
public class SearchParameters {
	String firstname;
	String surname;
	String status;

	SearchParameters(String firstname, String surname, String  status) {
		this.firstname = firstname;
		this.surname = surname;
		this.status = status;
	}
}

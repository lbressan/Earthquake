/**
 * 
 */
package com.lbb.earthquake;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * @author Leonardo Bressan
 *
 */
public class Preferences extends PreferenceActivity {

	public static final String PREF_AUTO_UPDATE = "PREF_AUTO_UPDATE";
	public static final String PREF_MIN_MAG = "PREF_MIN_MAG";
	public static final String PREF_UPDATE_FREQ = "PREF_UPDATE_FREQ";
	public static final String PREF_FEED_SOURCE_URL = "PREF_FEED_SOURCE_URL";
	
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.userpreferences);
	}

}

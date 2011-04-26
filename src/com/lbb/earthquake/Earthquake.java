package com.lbb.earthquake;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author Leonardo Bressan
 *
 */
public class Earthquake extends Activity implements OnSharedPreferenceChangeListener{
	
	/**
	 * Menu update entry
	 */
	private static final int MENU_UPDATE = Menu.FIRST;
	
	/**
	 * Menu clear entry
	 */
	private static final int MENU_CLEAR = Menu.FIRST + 1;
	
	/**
	 * Menu preferences 
	 */
	private static final int MENU_PREFERENCES = Menu.FIRST +2;
	
	/**
     * Preferences activity request code
     */
    private static final int SHOW_PREFERENCES = 1;
	
	/**
	 * Earthquake details dialog
	 */
	private static final int QUAKE_DIALOG = 1;
	
	Quake selectedQuake;
	
	/**
	 * xml constants 
	 */
	private static final String FEED_XML_ENTRY_TAG = "entry";
	private static final String FEED_XML_TITLE_TAG = "title";
	private static final String FEED_XML_LOCATION_TAG = "georss:point";
	private static final String FEED_XML_ELEVATION_TAG = "georss:elev";
	private static final String FEED_XML_LINK_TAG = "link";
	private static final String FEED_XML_UPDATED_TAG = "updated";
	private static final String FEED_XML_LINK_HREF_TAG = "href";
	private static final String FEED_XML_DATE_FORMAT = "yyyy-MM-dd'T'hh:mm:ss'Z'";
	
	/**
	 * Preference constants
	 */
	private int minimumMagnitude = 0;
	private boolean autoUpdate = false;
	private int updateFrequecy = 0;
	private String feedUrl = "";
	
	
	/**
	 * Reference to the list view
	 */
	ListView earthquakeListView;
	
	/**
	 * Reference to the array adapter 
	 */
	ArrayAdapter<Quake> aa;
	
	/**
	 * List of earthquakes 
	 */
	ArrayList<Quake> earthquakes = new ArrayList<Quake>();
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        earthquakeListView = (ListView) this.findViewById(R.id.earthquakeListView);
        
        earthquakeListView.setOnItemClickListener(
        		new OnItemClickListener() {
        			@Override
        			public void onItemClick(AdapterView<?> av, View v, int index,
        					long longArg) {
        					selectedQuake = earthquakes.get(index);
        					showDialog(QUAKE_DIALOG);
        			}
        		}
        	);
        
        int layoutID = android.R.layout.simple_list_item_1;
        aa = new ArrayAdapter<Quake>(this, layoutID, earthquakes);
        earthquakeListView.setAdapter(aa);
        
        // Update variables with preferences
        updateFromPreferences();
        
        // Call refresh earthquake method
        refreshEarthquakes();
    }
    
    
    /**
     * Update global class variable when the user close the preferences activity
     */
	private void updateFromPreferences() {
			Context context = getApplicationContext();
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			minimumMagnitude = Integer.parseInt(prefs.getString(Preferences.PREF_MIN_MAG, "0"));
			updateFrequecy = Integer.parseInt(prefs.getString(Preferences.PREF_UPDATE_FREQ, "0"));
			autoUpdate = prefs.getBoolean(Preferences.PREF_AUTO_UPDATE, false);
			feedUrl = prefs.getString(Preferences.PREF_FEED_SOURCE_URL,getString(R.string.quake_feed_m2_5));
			prefs.registerOnSharedPreferenceChangeListener(this);
	}
    
    @Override
	protected Dialog onCreateDialog(int id) {
		super.onCreateDialog(id);
		switch (id) {
		case QUAKE_DIALOG:
			LayoutInflater li = LayoutInflater.from(this);
			View quakeDetailsView = li.inflate(R.layout.quake_details, null);
			
			AlertDialog.Builder quakeDialog = new AlertDialog.Builder(this);
			quakeDialog.setTitle("Quake time");
			quakeDialog.setView(quakeDetailsView);
			return quakeDialog.create();
		}
		return null;
	}

	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		switch (id) {
		case QUAKE_DIALOG:
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
			String dateString = sdf.format(selectedQuake.getDate());
			String quakeText = selectedQuake.getQuakeTextForQuakeDialog();
			AlertDialog quakeDialog = (AlertDialog) dialog;
			quakeDialog.setTitle(dateString);
			TextView tv = (TextView) quakeDialog.findViewById(R.id.quakeDetailsTextView);
			tv.setText(quakeText);
			break;
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_UPDATE, Menu.NONE, R.string.menu_update);
		menu.add(0,MENU_CLEAR,Menu.NONE,R.string.menu_clear);
		menu.add(0,MENU_PREFERENCES,Menu.NONE,R.string.menu_preferences);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
		case MENU_UPDATE:
			refreshEarthquakes();
			return true;
		case MENU_CLEAR:
			clearEarthquakes();
			return true;
		case MENU_PREFERENCES:
			showPreferences();
			return true;
		}
		return false;
	}
	
	/**
	 * Show preferences activity
	 */
	private void showPreferences() {
		Intent i = new Intent(this, Preferences.class);
		startActivityForResult(i, SHOW_PREFERENCES);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case SHOW_PREFERENCES:
			refreshEarthquakes();
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}


	private void clearEarthquakes() {
		earthquakes.clear();
		aa.notifyDataSetChanged();
	}

	/**
     * Refresh Earthquakes getting and parsing the xml feed
     */
    private void refreshEarthquakes(){
    	URL url;
    	try {
    		// Get the quake feed url
        	String quakeFeed = feedUrl;
        	url = new URL(quakeFeed);
        	URLConnection connection;
        	connection = url.openConnection();
        	
        	HttpURLConnection httpConnection = (HttpURLConnection) connection;
        	int responseCode = httpConnection.getResponseCode();
        	
        	if (responseCode == HttpURLConnection.HTTP_OK) {
				InputStream in = httpConnection.getInputStream();
				DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				
				// Parse the feed
				Document dom = db.parse(in);
				Element docEle = dom.getDocumentElement();
				
				earthquakes.clear();
				
				// Get the node list
				NodeList nl = docEle.getElementsByTagName(FEED_XML_ENTRY_TAG);
				if (nl != null && nl.getLength() > 0) {
					for (int i = 0; i < nl.getLength(); i++) {
						Element entry = (Element) nl.item(i);
						Element title = (Element) entry.getElementsByTagName(FEED_XML_TITLE_TAG).item(0);
						Element g = (Element) entry.getElementsByTagName(FEED_XML_LOCATION_TAG).item(0);
						Element when = (Element) entry.getElementsByTagName(FEED_XML_UPDATED_TAG).item(0);
						Element link = (Element) entry.getElementsByTagName(FEED_XML_LINK_TAG).item(0);
						Element el = (Element) entry.getElementsByTagName(FEED_XML_ELEVATION_TAG).item(0);
						
						// Link
						String linkString = link.getAttribute(FEED_XML_LINK_HREF_TAG);
						
						// Date
						String dt = when.getFirstChild().getNodeValue();
						SimpleDateFormat sdf = new SimpleDateFormat(FEED_XML_DATE_FORMAT); // 2011-03-14T21:44:10Z
						Date quakeDate = new GregorianCalendar(0,0,0).getTime();
						try {
							quakeDate = sdf.parse(dt);
						} catch (ParseException e) {
							e.printStackTrace();
						}
						
						// Location
						double elevation = Double.parseDouble(el.getFirstChild().getNodeValue()); 
						String point = g.getFirstChild().getNodeValue();
						String[] location = point.split(" ");
						Location l = new Location("dummyGps");
						l.setLatitude(Double.parseDouble(location[0]));
						l.setLongitude(Double.parseDouble(location[1]));
						l.setAltitude(elevation);
						
						// Magnitude
						String details = title.getFirstChild().getNodeValue();
						String magnitudeString = details.split(" ")[1]; // "2.9,"
						double magnitude = Double.parseDouble(magnitudeString.substring(0, magnitudeString.length() -1 ));
						
						// Details
						details = details.split(",")[1].trim();
						
						// Create the new quake object
						Quake quake = new Quake(quakeDate, magnitude, details, linkString, l);
						addNewQuake(quake);
					}
				}
			}
        	
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} finally {}
    }
    
    /**
     * Add a new quake to the list
     * @param Quake quake - the quake object to add
     */
    private void addNewQuake(Quake quake) {
    	// check the minimum magnitude preference
    	if (quake.getMagnitude() >= minimumMagnitude) {
        	// add the new quake to the array list
        	earthquakes.add(quake);
        	// notify the array adapter of the changes
        	aa.notifyDataSetChanged();	
		}
    }

    /**
     * Called when Preferences changes
     */
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs,
			String key) {
		if (key.equals(Preferences.PREF_AUTO_UPDATE)) {
			autoUpdate = prefs.getBoolean(Preferences.PREF_AUTO_UPDATE, false);
		} else if (key.equals(Preferences.PREF_FEED_SOURCE_URL)) {
			feedUrl = prefs.getString(Preferences.PREF_FEED_SOURCE_URL,"");
		} else if (key.equals(Preferences.PREF_MIN_MAG)) {
			minimumMagnitude = Integer.parseInt(prefs.getString(Preferences.PREF_MIN_MAG, "0"));
		} else if (key.equals(Preferences.PREF_UPDATE_FREQ)) {
			updateFrequecy = Integer.parseInt(prefs.getString(Preferences.PREF_UPDATE_FREQ, "0"));
		}
	}
}
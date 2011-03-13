package com.lbb.earthquake;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author Leonardo Bressan
 *
 */
public class Earthquake extends Activity {
	
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
        int layoutID = android.R.layout.simple_list_item_1;
        aa = new ArrayAdapter<Quake>(this, layoutID, earthquakes);
        earthquakeListView.setAdapter(aa);
        
        refreshEarthquakes();
    }
    
    /**
     * Refresh Earthquakes getting and parsing the xml feed
     */
    private void refreshEarthquakes(){
    	
    }
    
    /**
     * Add a new quake to the list
     * @param quake - the quake object to add
     */
    private void addNewQuake(Quake quake) {
    	
    }
}
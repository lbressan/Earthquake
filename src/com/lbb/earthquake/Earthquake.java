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
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author Leonardo Bressan
 *
 */
public class Earthquake extends Activity {
	
	
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
        
        // Call refresh earthquake method
        refreshEarthquakes();
    }
    
    /**
     * Refresh Earthquakes getting and parsing the xml feed
     */
    private void refreshEarthquakes(){
    	URL url;
    	try {
    		// Get the quake feed url
        	String quakeFeed = getString(R.string.quake_feed);
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
						double elevation = Double.parseDouble(el.getNodeValue()); 
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
     * @param quake - the quake object to add
     */
    private void addNewQuake(Quake quake) {
    	
    }
}
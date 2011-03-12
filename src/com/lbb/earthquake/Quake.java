/**
 * Quake data structure
 */
package com.lbb.earthquake;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.location.Location;

/**
 * @author Leonardo Bressan
 *
 */
public class Quake {
	/**
	 * Earthquake date 
	 */
	private Date date;
	/**
	 * Earthquake magnitude
	 */
	private double magnitude;
	
	/**
	 * Earthquake details
	 */
	private String details;
	/**
	 * Earthquake link
	 */
	private String link;
	/**
	 * Earthquake location
	 */
	private Location location;
	
	/**
	 * Constructor of a Quake object
	 * @param date - the Earthquake date
	 * @param magnitude - the Earthquake magnitude
	 * @param details - the Earthquake details
	 * @param link - the Earthquake link
	 * @param location - the Earthquake location
	 */
	public Quake(Date date, double magnitude, 
			String details, String link, Location location) {
		this.date = date;
		this.magnitude = magnitude;
		this.details = details;
		this.link = link;
		this.location = location;
	}

	/**
	 * @return the Earthquake date
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * @return the Earthquake magnitude
	 */
	public double getMagnitude() {
		return magnitude;
	}

	/**
	 * @return the Earthquake details
	 */
	public String getDetails() {
		return details;
	}

	/**
	 * @return the Earthquake link
	 */
	public String getLink() {
		return link;
	}

	/**
	 * @return the Earthquake location
	 */
	public Location getLocation() {
		return location;
	}

	@Override
	public String toString() {
		SimpleDateFormat sdf = new SimpleDateFormat();
		String dateString = sdf.format(date);
		return dateString
			+ ": " + magnitude
			+ ": " + details;
	}
	
	
}

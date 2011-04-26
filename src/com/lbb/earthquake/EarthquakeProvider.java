/**
 * 
 */
package com.lbb.earthquake;

import java.sql.SQLException;
import java.util.regex.Matcher;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * @author Leonardo Bressan
 *
 */
public class EarthquakeProvider extends ContentProvider {

	/**
	 * URI for this provider
	 */
	public static final Uri CONTENT_URI = Uri.parse("Content://com.lbb.earthquake/earthquakes");
	
	/**
	 * Reference to the earthquakes database
	 */
	private SQLiteDatabase earthquakeDB;
	
	/**
	 * Provider string tag
	 */
	private static final String TAG = "EarthquakeProvider";
	
	/**
	 * Database name
	 */
	private static final String DATABASE_NAME = "earthquake.db";
	
	/**
	 * Database version
	 */
	private static final int DATABASE_VERSION = 1;
	
	/**
	 * Database earthquakes table
	 */
	private static final String EARTHQUAKE_TABLE = "earthquakes";
	
	/*
	 * Column names 
	 */
	/**
	 * ID column
	 */
	private static final String KEY_ID = "_id";
	/**
	 * Date column
	 */
	private static final String KEY_DATE = "date";
	/**
	 * Details column
	 */
	private static final String KEY_DETAILS = "details";
	/**
	 * Latitude column
	 */
	private static final String KEY_LOCATION_LAT = "latitude";
	/**
	 * Longitude column
	 */
	private static final String KEY_LOCATION_LNG = "longitude";
	/**
	 * Magnitude column
	 */
	private static final String KEY_MAGNITUDE = "magnitude";
	/**
	 *Link column 
	 */
	private static final String KEY_LINK = "link";
	
	/**
	 * URI for all quakes
	 */
	private static final int QUAKES = 1;
	/**
	 * URI for single quake
	 */
	private static final int QUAKE_ID = QUAKES + 1;
	
	private static final UriMatcher uriMatcher;
	
	static{
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("com.lbb.provider.Earthquake", "earthquekes", QUAKES);
		uriMatcher.addURI("com.lbb.provider.Earthquake", "earthquekes/#", QUAKE_ID);
	}
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String where, String[] whereArgs) {
		int count;
		switch (uriMatcher.match(uri)) {
		case QUAKES:
			count = earthquakeDB.delete(EARTHQUAKE_TABLE, where, whereArgs);
			break;
		case QUAKE_ID:
			String whereClause = KEY_ID  + " = "  
					+ uri.getPathSegments().get(1) 
					+ (!TextUtils.isEmpty(where)? " AND (" + where + ")" : "");
			count = earthquakeDB.delete(EARTHQUAKE_TABLE, whereClause, whereArgs);
			break;
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case QUAKES:
			return "vnd.android.cursor.dir/vnd.lbb.earthquake";
		case QUAKE_ID:
			return "vnd.android.cursor.item/vnd.lbb.earthquake";
		default:
			throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		long rowId = earthquakeDB.insert(EARTHQUAKE_TABLE, "quake", values);
		if (rowId > 0) {
			Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowId);
			getContext().getContentResolver().notifyChange(_uri, null);
			return _uri;
		}
		throw new SQLiteException("Failed to insert row into " + uri);
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		Context context = getContext();
		earthquakeDatabaseHelper dbHelper = new earthquakeDatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
		earthquakeDB = dbHelper.getWritableDatabase();
		return (earthquakeDB == null) ? false : true;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private static class earthquakeDatabaseHelper extends SQLiteOpenHelper {
		/**
		 * Database creation query
		 */
		private static final String DATABASE_CREATE = 
					"CREATE TABLE " + EARTHQUAKE_TABLE 
					+ " ("
					+ KEY_ID + " INTEGER "
					+ KEY_DATE + " FLOAT "
					+ KEY_DETAILS + " STRING "
					+ KEY_LINK + " STRING "
					+ KEY_LOCATION_LAT + " FLOAT "
					+ KEY_LOCATION_LNG + " FLOAT "
					+ KEY_MAGNITUDE + " FLOAT "
					+ ")";
		private static final String DATABASE_EARTHQUAKE_TABLE_DROP =
					"DROP TABLE IF EXIST "
					+ EARTHQUAKE_TABLE;
		
		public earthquakeDatabaseHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(DATABASE_CREATE);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to version " + newVersion + ", wich will destroy all old data");
			db.execSQL(DATABASE_EARTHQUAKE_TABLE_DROP);
			onCreate(db);
		}
		
	}

}

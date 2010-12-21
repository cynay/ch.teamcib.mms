package ch.teamcib.mms;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 
 * Class DataHelper
 * 
 * @author CiB
 *
 */
public class DataHelper {

	// ===========================================================
    // Finals 
    // ===========================================================
	private static final String DATABASE_NAME = "db_mms.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "tbl_mms";
	private final String MAX_VALUES = "20";

	// ===========================================================
    // Members
    // ===========================================================
	private Context context;
	private SQLiteDatabase db;

	
	/**
	 * Constructor creates a instance of the OpenHelper
	 * 
	 * @param the context from the calling activity
	 */
	public DataHelper(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
	}

	/**
	 * 	This method creates 
	 *  Insert Value into Table tbl_mms
	 * 
	 * @param hostname IP or hostname of the server in String
	 * @param key identifies the following value in String
	 * @param value associated with the key in String
	 */
	public void InsertIntoTable(String hostname, String key, String value) {
		Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String date = sdf.format(cal.getTime());
		
		this.db.execSQL("INSERT INTO " + TABLE_NAME
				+ " (date,hostname, key, value)" + " VALUES ('" + date
				+ "' , '" + hostname + "' , '" + key + "' , '" + value + "');");
		
		Log.i("-> DATAHELPER", "InsertIntoTable (done): " + hostname + key + value);
	}


	/**
	 * Delete content of table
	 */
	public void deleteAll() {
		this.db.delete(TABLE_NAME, null, null);
	}

	/**
	 * Delete a certain Row 
	 * rowId must known from the table
	 * 
	 * @param rowId in Integer  
	 */
	public void deleteRow(Integer rowId) {
		db.delete(TABLE_NAME, "_id=" + rowId, null);
		Log.i("-> DATAHELPER", "deleteRow (done): " + rowId);
	}

	
	/**
	 * Select each columns of the table tbl_mms.
	 * Limited rows output
	 * 
	 * @return cursor which returns the result of the query
	 */
	public Cursor selectTable() {
		Cursor cursor = this.db
				.rawQuery(
						"SELECT date, hostname,key, value FROM tbl_mms " +
						"ORDER BY date desc LIMIT " + MAX_VALUES,
						null);
		
		Log.i("-> DATAHELPER", "selectTable()");

		return cursor;
	}

	/**
	 * Select each columns of the table tbl_mms.
	 * Limited rows output
	 * Filtered by hostname
	 * 
	 * @param hostname IP or hostname of the server in String
	 * @return cursor which returns the result of the query 
	 */
	public Cursor selectCol(String hostname) {
		Log.i("-> DATAHELPER", "ENTERCOL");
		Cursor cursor = this.db.rawQuery(
			"SELECT date, hostname,key, value FROM tbl_mms WHERE hostname = '"
			+ hostname + "' ORDER BY date desc LIMIT " + MAX_VALUES, null);

		Log.i("-> DATAHELPER", "selectCol: " + hostname);

		return cursor;
	}
	
	
	/**
	 * Select each columns of the table tbl_mms.
	 * Limited rows output
	 * Filtered by hostname and key
	 * 
	 * @param hostname IP or hostname of the server in String
	 * @param key identifies the following value in String
	 * @return cursor which returns the result of the query
	 */
	public Cursor selectHostKey(String hostname, String key) {
		Cursor cursor = this.db.rawQuery(
			"SELECT date, hostname,key, value FROM tbl_mms WHERE hostname = '"
			+ hostname + "' AND key = '" + key + "' ORDER BY date desc LIMIT " 
			+ MAX_VALUES, null);

		return cursor;
	}
	
	/**
	 * Close database
	 */
	public void closeDB (){
		db.close();
		Log.i("-> DATAHELPER", "! closeDB()" );
	}

	/**
	 *  Class OpenHelper
	 *  This class creates the database
	 *  
	 * @author CiB
	 *
	 */
	private static class OpenHelper extends SQLiteOpenHelper {

		/**
		 * Constructor 
		 * Creates the database
		 * 
		 * @param context the context from the calling activity or class
		 */
		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "
					+ TABLE_NAME
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, date INTEGER, "
					+ "hostname TEXT, key TEXT, value TEXT)");

		}

		/**
		 * If data structure is changed this method have to run
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("-> DATAHELPER",
					"Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			onCreate(db);
		}
	}
}

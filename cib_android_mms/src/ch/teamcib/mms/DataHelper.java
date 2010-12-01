package ch.teamcib.mms;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper {

	// ===========================================================
    // Finals 
    // ===========================================================
	private static final String DATABASE_NAME = "db_mms.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "tbl_mms";

	private Context context;
	private SQLiteDatabase db;

	public DataHelper(Context context) {
		this.context = context;
		OpenHelper openHelper = new OpenHelper(this.context);
		this.db = openHelper.getWritableDatabase();
	}

	/**
	 * --USE Insert Value into Table tbl_mms
	 * 
	 * @param hostname
	 * @param key
	 * @param value
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
	 * --USE This method will insert the String which was send by the Server
	 * 
	 * @param hostinfo
	 */
	public void insertStringRow(String hostinfo) {

		String seperator = "[,]";
		String[] hostinfos = hostinfo.split(seperator);
		String hostname, key, value;
		hostname = hostinfos[0];
		key = hostinfos[1];
		value = hostinfos[2];

		InsertIntoTable(hostname, key, value);

	}

	/**
	 * Delete content of table
	 */
	public void deleteAll() {
		this.db.delete(TABLE_NAME, null, null);
	}

	/**
	 * Delete certain Row
	 * 
	 * @param rowId
	 */
	public void deleteRow(Integer rowId) {
		db.delete(TABLE_NAME, "_id=" + rowId, null);
		Log.i("-> DATAHELPER", "deleteRow (done): " + rowId);
	}

	/**
	 * 
	 * --USE
	 * 
	 * @return Cursor
	 */
	public Cursor selectTable() {
		Cursor cursor = this.db
				.rawQuery(
						"SELECT date, hostname,key, value FROM tbl_mms ORDER BY date desc LIMIT 20",
						null);
		
		Log.i("-> DATAHELPER", "selectTable()");

		return cursor;
	}

	/**
	 * --USE
	 * 
	 * @param para
	 * @return Cursor
	 */
	public Cursor selectCol(String hostname) {
		Log.i("-> DATAHELPER", "ENTERCOL");
		Cursor cursor = this.db.rawQuery(
				"SELECT date, hostname,key, value FROM tbl_mms WHERE hostname = '"
						+ hostname + "' ORDER BY date desc LIMIT 10 ", null);
		
		Log.i("-> DATAHELPER", "selectCol: " + hostname);

		return cursor;
	}
	
	/**
	 * --USE
	 * 
	 * @param para
	 * @return Cursor
	 */
	public Cursor selectHostKey(String hostname, String key) {
		Cursor cursor = this.db.rawQuery(
				"SELECT date, hostname,key, value FROM tbl_mms WHERE hostname = '"
						+ hostname + "' AND key = '" + key + "' ORDER BY date desc LIMIT 10 ", null);
		
		Log.i("-> DATAHELPER", "selectHostKey: " + hostname + key);

		return cursor;
	}
	
	public void closeDB (){
		db.close();
		Log.i("-> DATAHELPER", "! closeDB()" );
	}

	private static class OpenHelper extends SQLiteOpenHelper {

		OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);

		}

		/**
		 * Create table tbl_mms
		 */
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("CREATE TABLE "
					+ TABLE_NAME
					+ "(_id INTEGER PRIMARY KEY AUTOINCREMENT, date INTEGER, "
					+ "hostname TEXT, key TEXT, value TEXT)");

		}

		/**
		 * If data structure is changed this method have to run
		 * 
		 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w("-> DATAHELPER",
					"Upgrading database, this will drop tables and recreate.");
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			// db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME2);
			onCreate(db);
		}
	}
}

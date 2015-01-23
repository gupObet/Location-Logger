package com.example.networkscanproj;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class NetworkScanDB {
	
	
	public static String Key_RowID="_id";
	public static String Key_Lat="latitude";
	public static String Key_Lon="longitude";
	public static String Key_Accur="accuracy";
	public static String Key_Time="time";
	public static String Key_Prov = "provider";

	
	private static final String Database_Name="NetworkScanDB";
	private static final String Table_NetWS="Table_NetWS";
	
	private SQLiteDatabase myDatabase;
	private final Context myContext;
	private DbHelper myHelper;
	
	private static class DbHelper extends SQLiteOpenHelper {
		
		

		//changing the version number, makes the call onUpgrade to create the table again
		private static final int DATABASE_VERSION = 6;
		
		
		private static final String GeoLocInfoQuery = "Create Table " + Table_NetWS + " (" + Key_RowID + 
				" integer primary key autoincrement, " + Key_Prov + " text not null, " + Key_Lat + " real, " + Key_Lon + " real, " + 
				Key_Accur + " real, " + Key_Time + " real);";

		//creates a file in the external storage, the SD Card
		public DbHelper(Context context) {
			super(context, context.getExternalFilesDir(null).getAbsoluteFile() + "/"+ Database_Name, null, DATABASE_VERSION);
			// TODO Auto-generated constructor stub
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			Log.w("oncreate", "Creating, query statement is ");
			Log.w("oncreate", GeoLocInfoQuery);
			db.execSQL(GeoLocInfoQuery);
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			Log.w("onupgrade", String.format("CALLED oldVersion == %d, newVersion == %d", oldVersion, newVersion));
			if (oldVersion == newVersion) {
				Log.w("onupgrade", "CALLED oldVersion == newVersion");
				return;
			}
			//simplest way is to drop and recreate
			db.execSQL("DROP TABLE IF EXISTS " + Table_NetWS);
			Log.w("onupgrade", "CALLED oldVersion != newVersion, dropping table");			
			onCreate(db);
			
		}
		
	}
	
	public NetworkScanDB(Context c) {
		myContext=c;
	}
	
	public NetworkScanDB open() throws SQLException {
		
		myHelper = new DbHelper(myContext);
		myDatabase = myHelper.getWritableDatabase();  //allows for reading and writing to this databse
		return this;
	}
	
	public void close() {
	
		myHelper.close();
		
	}
	
	public long insertGeoLocInfo(String provider, double lat, double lon, float accur, double time) {
		
		ContentValues cv = new ContentValues();
		cv.put(Key_Prov, provider);
		cv.put(Key_Lat, lat);
		cv.put(Key_Lon, lon);
		cv.put(Key_Accur, accur);
		cv.put(Key_Time, time);

		return myDatabase.insert(Table_NetWS, null, cv);	
		
	}
	
	public Cursor getGeoLocInfoCursor() {
		
		String [] columns = new String [] {Key_RowID, Key_Prov, Key_Lat, Key_Lon, Key_Accur, Key_Time};
		//Double [] columns = new Double [] {Key_RowID, Key_Lat, Key_Lon, Key_Accur, Key_Time};
		Cursor c = myDatabase.query(Table_NetWS, columns, null, null, null, null, null);
		return c;
		
	}

}//endclass NetworkScanDB

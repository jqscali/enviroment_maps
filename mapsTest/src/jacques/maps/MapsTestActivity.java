package jacques.maps;

import java.io.IOException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;


public class MapsTestActivity extends MapActivity
{
    private static String DB_PATH_L = "/data/data/jacques.maps/databases/";
    private static String DB_NAME_L = "co2_emissions.sqlite3";
    private static String DB_NAME_C = "map_coords.sqlite3";
    
	private static final String DATABASE_CREATE = "create table MapCoords (RegionName text not null, "
			+ "SecondTierAuthority text not null, "
			+ "LARegionName text not null, "
			+ "MCX integer not null, "
			+ "MCY integer not null);";
	
	//Declaring variable
	DatabaseHelper myDbHelper;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Log.v("xxxxx","hello world");
        
        //Setting up the view
        setContentView(R.layout.main);
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        //Setting up the buttons
        final Button button = (Button) findViewById(R.id.buttonFilter);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
            }
        });
        
        //Loading the database
        loadDatabase();
       // SQLiteQueryBuilder myQuery = new SQLiteQueryBuilder();
       // myQuery.buildQuery("RegionName", null, null, null, null, null, null);
        //myQuery.setTables("co2_emissions");
        Cursor cur = myDbHelper.getDatabase().query("co2_emissions", null, null, null, null, null, null);
        cur.moveToFirst();
        
        boolean test = this.deleteDatabase(DB_NAME_C);
        DatabaseHelper lNewDBHelper = CreateNewDataBase();
        
        HttpClient httpclient = new DefaultHttpClient();
        
        while (cur.isAfterLast() == false)
        {
        	String result = "";
        	ResponseHandler<String> handler = new BasicResponseHandler();
	        String place = cur.getString(2).replace(" ", "+");

        	Log.v("xxlat",place+"+UK");
	        HttpGet request = new HttpGet("http://maps.google.com/maps/geo?q="+place+"+UK"+"&output=xml&oe=utf8");

    	    try {
				result = httpclient.execute(request, handler);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	    String coordonees = result.substring(result.indexOf("<coordinates>"), result.indexOf("</coordinates>"));
        	
    	    coordonees = coordonees.replaceAll("<coordinates>", "");
    	    coordonees = coordonees.replaceAll("</coordinates>", "");
    	    int index = coordonees.indexOf(",");
    	    String temp = coordonees.substring(0, index);
    	    coordonees = coordonees.replaceAll(temp+",", "");
    	    float lon = Float.parseFloat(temp); //Log.v("xxlon",temp);
    	    index = coordonees.indexOf(",");
    	    temp = coordonees.substring(0, index);
    	    float lat = Float.parseFloat(temp); //Log.v("xxlat",temp);
    	    
    	    GeoPoint gp = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
    	    
        	lNewDBHelper.CreateNewEntry(
        	createContentValues(cur.getString(0), cur.getString(1), cur.getString(2), gp.getLatitudeE6(), gp.getLongitudeE6()));
       	    
        	cur.moveToNext();
        }
        Log.v("xxxxx", "Finished!");
        
        // NOTE: Could use total for visualisation, but when user clicks on the icon
        // the rest of the details are shown.
        cur.close();

        
        //Adding overlay items
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        MyOverlays testOverlays = new MyOverlays(drawable);
        
        Cursor cur2 = lNewDBHelper.getDatabase().query("MapCoords", null, null, null, null, null, null);
        cur2.moveToFirst();
        
        GeoPoint point = new GeoPoint(cur2.getInt(3),cur2.getInt(4));
        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
        
        testOverlays.addOverlay(overlayitem);
        mapOverlays.add(testOverlays);
        
        lNewDBHelper.close();
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
		

	private void loadDatabase()
	{
		myDbHelper = new DatabaseHelper(this, DB_PATH_L, DB_NAME_L);
		 
		try
		{
			myDbHelper.createDataBase();
		}
		catch (IOException ioe)
		{
			throw new Error("Unable to create database");
	 	}
		 
		try
		{
			myDbHelper.openDataBase();
		}
		catch(SQLException sqle)
		{
			throw sqle;
	 	}
	}
	
	private DatabaseHelper CreateNewDataBase()
	{
		DatabaseHelper lNewDB = new DatabaseHelper(this, DB_PATH_L, DB_NAME_C);
		
		// Create a table in the database.
		try
		{
			lNewDB.CreateTableInDB(this, DATABASE_CREATE);
		}
		catch(SQLException sqle)
		{
			throw sqle;
	 	}
		
		return lNewDB;
	}
	
	private GetGeoPoint()
	{
		
	}
	
	private ContentValues createContentValues(String RN, String STA, String LARN,
			int X, int Y)
	{
		ContentValues values = new ContentValues();
		values.put("RegionName", RN);
		values.put("SecondTierAuthority", STA);
		values.put("LARegionName", LARN);
		values.put("MCX", X);
		values.put("MCY", Y);
		return values;
	}
}
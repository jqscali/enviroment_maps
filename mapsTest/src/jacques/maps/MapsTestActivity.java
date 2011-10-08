package jacques.maps;

import java.io.IOException;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import jacques.maps.MyOverlays;
import jacques.maps.DatabaseHelper;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

public class MapsTestActivity extends MapActivity {
	
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
        
        //Loading the database
        loadDatabase();
        SQLiteQueryBuilder myQuery = new SQLiteQueryBuilder();
       // myQuery.buildQuery("RegionName", null, null, null, null, null, null);
        //myQuery.setTables("co2_emissions");
        Cursor cur = myDbHelper.getDatabase().query("co2_emissions", null, null, null, null, null, null);
        cur.moveToFirst();
        while (cur.isAfterLast() == false) {
          //  view.append("n" + cur.getString(1));
        	Log.v("xxxxx",cur.getString(1));
       	    cur.moveToNext();
        }
        cur.close();
        
        //Adding overlay items
        List<Overlay> mapOverlays = mapView.getOverlays();
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        MyOverlays testOverlays = new MyOverlays(drawable);
        
        GeoPoint point = new GeoPoint(19240000,-99120000);
        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
        
        testOverlays.addOverlay(overlayitem);
        mapOverlays.add(testOverlays);
        
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
		

private void loadDatabase()
{
	myDbHelper = new DatabaseHelper(this);
	 
	try {
	 
	        	myDbHelper.createDataBase();
	 
	 	} catch (IOException ioe) {
	 
	 		throw new Error("Unable to create database");
	 
	 	}
	 
	 	try {
	 
	 		myDbHelper.openDataBase();
	 
	 	}catch(SQLException sqle){
	 
	 		throw sqle;
 
 	}
	}
}
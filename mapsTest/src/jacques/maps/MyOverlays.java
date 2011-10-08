package jacques.maps;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

@SuppressWarnings("rawtypes")
public class MyOverlays extends ItemizedOverlay {
	
	private ArrayList<OverlayItem> overlayItems = new ArrayList<OverlayItem>();
	private Context mContext;

	public MyOverlays(Drawable defaultMarker) {
		  super(boundCenterBottom(defaultMarker));
		}
	
	public MyOverlays(Drawable defaultMarker, Context context) {
		super(defaultMarker);
		// TODO Auto-generated constructor stub
		mContext = context;
	}
	
	public void addOverlay(OverlayItem overlayItem)
	{
		overlayItems.add(overlayItem);
		populate();
	}

	@Override
	protected OverlayItem createItem(int arg0) {
		// TODO Auto-generated method stub
		return overlayItems.get(arg0);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return overlayItems.size();
	}
	
	@Override
	protected boolean onTap(int index) {
	  OverlayItem item = overlayItems.get(index);
	  AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	  dialog.setTitle(item.getTitle());
	  dialog.setMessage(item.getSnippet());
	  dialog.show();
	  return true;
	}

}

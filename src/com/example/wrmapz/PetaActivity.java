package com.example.wrmapz;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.mapsforge.android.AndroidUtils;
import org.mapsforge.android.maps.DebugSettings;
import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.MapScaleBar;
import org.mapsforge.android.maps.MapScaleBar.TextField;
import org.mapsforge.android.maps.MapViewPosition;
import org.mapsforge.android.maps.Projection;
import org.mapsforge.android.maps.mapgenerator.TileCache;
import org.mapsforge.android.maps.overlay.Circle;
import org.mapsforge.android.maps.overlay.ListOverlay;
import org.mapsforge.android.maps.overlay.Marker;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.android.maps.overlay.PolygonalChain;
import org.mapsforge.android.maps.overlay.Polyline;
import org.mapsforge.core.model.BoundingBox;
import org.mapsforge.core.model.GeoPoint;
import org.mapsforge.core.model.MapPosition;
import org.mapsforge.map.reader.header.MapFileInfo;
import org.mapsforge.map.rendertheme.InternalRenderTheme;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wrmapz.controller.FileUtils;
import com.example.wrmapz.controller.Haversine;
import com.example.wrmapz.controller.InfoView;
import com.example.wrmapz.controller.KonversiKoordinat;
import com.example.wrmapz.controller.ScreenshotCapturer;
import com.example.wrmapz.controller.SeekBarChangeListener;
import com.example.wrmapz.controller.filefilter.FilterByFileExtension;
import com.example.wrmapz.controller.filefilter.ValidMapFile;
import com.example.wrmapz.controller.filefilter.ValidRenderTheme;
import com.example.wrmapz.controller.filepicker.FilePicker;
import com.example.wrmapz.controller.preferences.EditPreferences;
import com.example.wrmapz.model.Koordinat;
import com.example.wrmapz.view.PetaForm;

public class PetaActivity extends MapActivity 
implements android.view.View.OnClickListener, android.view.View.OnTouchListener {	
	
//	 The default number of tiles in the file system cache.	 
	public static final int FILE_SYSTEM_CACHE_SIZE_DEFAULT = 250;
//	 The maximum number of tiles in the file system cache.	 
	public static final int FILE_SYSTEM_CACHE_SIZE_MAX = 500;	
//	  The default move speed factor of the map.	 
	public static final int MOVE_SPEED_DEFAULT = 10;	
//	 The maximum move speed factor of the map.	 
	public static final int MOVE_SPEED_MAX = 30;	
	private PetaForm petaForm;	
	private double longitudeKitaBerdiri=0;
	private double latitudeKitaBerdiri=0;
	private ListOverlay listOverlay = new ListOverlay();
	private List<OverlayItem> overlayItems = listOverlay.getOverlayItems();
	ListOverlay listOverlay1 = new ListOverlay();
	List<OverlayItem> overlayItems1 = listOverlay1.getOverlayItems();
	ListOverlay listOverlay2 = new ListOverlay();
	List<OverlayItem> overlayItems2 = listOverlay2.getOverlayItems();	
	ListOverlay listOverlay3 = new ListOverlay();
	List<OverlayItem> overlayItems3 = listOverlay3.getOverlayItems();	
	ListOverlay listOverlay4 = new ListOverlay();
	List<OverlayItem> overlayItems4 = listOverlay4.getOverlayItems();	
	private static final int DIALOG_ENTER_COORDINATES = 0;
	private static final int DIALOG_INFO_MAP_FILE = 1;
	private static final int DIALOG_LOCATION_PROVIDER_DISABLED = 2;
	private static final int MENAMPILKAN_HASIL_HAVERSINE = 3;
	private static final int INFORMASI_LOKASIKU = 4;
	private  ArrayList<Koordinat> koordinat = new ArrayList<Koordinat>();
	private Koordinat koordinat1;	
	private static final int SELECT_MAP_FILE = 0;	
	private static final int SELECT_RENDER_THEME_FILE = 1;
//	private File MAP_FILE = new File(Environment.getExternalStorageDirectory().getPath(), "surabaya.map");
	private LocationManager locationManager;
	private int klik =1;
	private static final FileFilter FILE_FILTER_EXTENSION_MAP = new FilterByFileExtension(".map");
	private static final FileFilter FILE_FILTER_EXTENSION_XML = new FilterByFileExtension(".xml");
	private ValidMapFile validMapFile = new ValidMapFile();
	private ScreenshotCapturer screenshotCapturer;
	private KonversiKoordinat konversiKoordinat = new KonversiKoordinat();
	private WakeLock wakeLock;
//	private static final String BUNDLE_CENTER_AT_FIRST_FIX = "centerAtFirstFix";
	private static final String BUNDLE_SHOW_MY_LOCATION = "showMyLocation";
	private static final String BUNDLE_SNAP_TO_LOCATION = "snapToLocation";
	private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1/1000; // in Meters
	private static final long MINIMUM_TIME_BETWEEN_UPDATES = 1000; // in Milliseconds
	private double jarak3;
	private Location location ;
	GPSLocationListener locationListener = new GPSLocationListener();
	
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.peta_form);
		setPetaForm(new PetaForm(this));		
		
//		FileOpenResult fileOpenResult = petaForm.getMapView().setMapFile(MAP_FILE);
//		if (!fileOpenResult.isSuccess()) {
//			Toast.makeText(this, fileOpenResult.getErrorMessage(),Toast.LENGTH_LONG).show();
//			finish();
//		}
//		if (this.petaForm.getMapView().getMapFile() == null) {
//			startMapFilePicker();
//		}
		petaForm.getMapView().setClickable(true);
		petaForm.getMapView().setLongClickable(true);
		petaForm.getMapView().setBuiltInZoomControls(true);
		petaForm.getMapView().setFocusable(true);
		petaForm.getMapView().setFocusableInTouchMode(true);
		petaForm.getMapView().isInEditMode();
		petaForm.getToggleButtonsnapToLocationView().setOnClickListener(this);
		petaForm.getButtonUndo().setOnClickListener(this);
		petaForm.getMapView().setOnTouchListener((OnTouchListener) this);		
		this.screenshotCapturer = new ScreenshotCapturer(this);
		this.screenshotCapturer.start();
		
		// ---use the LocationManager class to obtain GPS locations---
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// cek apakah setting Use GPS Satellites sudah aktif apa belum
		if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {			
//			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
			 locationManager.requestLocationUpdates(
		                LocationManager.GPS_PROVIDER, 
		                MINIMUM_TIME_BETWEEN_UPDATES, 
		                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
		                new GPSLocationListener()
		        );
		} else {
			tampilkanAlertGPS();	
			
		}
		
		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		this.wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "AMV");
		
		MapScaleBar mapScaleBar = this.petaForm.getMapView().getMapScaleBar();
		mapScaleBar.setText(TextField.KILOMETER, getString(R.string.unit_symbol_kilometer));
		mapScaleBar.setText(TextField.METER, getString(R.string.unit_symbol_meter));
		
		if (savedInstanceState != null && savedInstanceState.getBoolean(BUNDLE_SHOW_MY_LOCATION)) {
//			enableShowMyLocation(savedInstanceState.getBoolean(BUNDLE_CENTER_AT_FIRST_FIX));
			if (savedInstanceState.getBoolean(BUNDLE_SNAP_TO_LOCATION)) {
//				enableSnapToLocation(false);
			}
		}
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if( v== petaForm.getToggleButtonsnapToLocationView()){
//			showToastOnUiThread("Anda klik toggle button");
			if(petaForm.getMapView().isClickable()==true){
				showToastOnUiThread("Peta tidak dapat di geser");
				petaForm.getMapView().setClickable(false);
				getPetaForm().getToggleButtonsnapToLocationView().setBackgroundResource(R.drawable.btn_snap_pressed);
				
			}
			else if(petaForm.getMapView().isClickable()==false){
				showToastOnUiThread("Peta dapat di geser");
				petaForm.getMapView().setClickable(true);
				getPetaForm().getToggleButtonsnapToLocationView().setBackgroundResource(R.drawable.btn_snap_normal);

			}
			}
		
		 if( v== petaForm.getButtonUndo()){
			 if(koordinat.size()==1){				 
				 koordinat.remove(0);
				 overlayItems3.remove(0); 
				 System.out.println("Ukuran Koordinat yg dihapus "+koordinat.size());
				 getPetaForm().getMapView().getOverlayController().redrawOverlays();
				 getPetaForm().getLocationManager2().setText("");				 
					 klik= koordinat.size();				
				 }		
			 
			 if(koordinat.size()>1){
			 koordinat.remove(koordinat.size()-1);
			 overlayItems3.remove(overlayItems3.size()-1);
			 System.out.println("Ukuran Koordinat yg dihapus "+koordinat.size());
			 getPetaForm().getMapView().getOverlayController().redrawOverlays();
			 getPetaForm().getLocationManager2().setText("");
			 klik= koordinat.size();
			 }
		 }

		
	}
	
	@Override
	public boolean onTouch(View arg0, MotionEvent event) {
		// TODO Auto-generated method stub
		boolean sentuh=true;	
		
		if(petaForm.getMapView().isClickable()==false){				
//			showToastOnUiThread("Peta dapat di touch titik");			
			Projection projection = getPetaForm().getMapView().getProjection();
		    GeoPoint geoPoint = projection.fromPixels((int) event.getX(), 
		    		(int) event.getY());
		    
		    koordinat1 = new Koordinat();
		    koordinat1.setLatitude(geoPoint.latitude);
		    koordinat1.setLongitude(geoPoint.longitude);
		    
			int sudah_ada=0;
			for(int a=0;a<koordinat.size();a++){
				if(koordinat.get(a).getLatitude()==koordinat1.getLatitude()
						&&(koordinat.get(a).getLongitude()==koordinat1.getLongitude())){
					sudah_ada=1;
				}
			}
			if(sudah_ada==0){
				koordinat.add(koordinat1);
				Marker marker1 = createMarker(R.drawable.marker_blue1, geoPoint);
				overlayItems3.add(marker1);
				getPetaForm().getMapView().getOverlays().add(listOverlay3);
				getPetaForm().getLocationManager2().setText(
						" Koordinat yang di klik ke " + (koordinat.size()) + " latitude  "
								+ geoPoint.latitude + " longitude  "
								+ geoPoint.longitude);
			}	
		    getPetaForm().getMapView().getOverlayController().redrawOverlays();//methode untuk mempercepat menampilkan hasil
		    klik=klik+1;
			sentuh=true;	
			System.out.println("Ukuran koordinat "+koordinat.size());
		}
		else if(petaForm.getMapView().isClickable()==true){				
//			showToastOnUiThread("Peta tidak dapat di touch titik");
			sentuh=false;			
		}
		return sentuh;
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		menu.findItem(R.id.info).setVisible(false);
		menu.findItem(R.id.screenshot).setVisible(true);
		menu.findItem(R.id.menu_position).setVisible(true);
		menu.findItem(R.id.menu_mapfile).setVisible(true);
		menu.findItem(R.id.action_settings).setVisible(true);
		menu.findItem(R.id.menu_render_theme).setVisible(false);
		menu.findItem(R.id.menu_preferences).setVisible(true);
		
		return true;
	}
	
	/**
	 * Uses the UI thread to display the given text message as toast
	 * notification.
	 * 
	 * @param text
	 *            the text message to display
	 */
	public void showToastOnUiThread(final String text) {
		if (AndroidUtils.currentThreadIsUiThread()) {
			Toast toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
			toast.show();
		} else {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast toast = Toast.makeText(PetaActivity.this, text,Toast.LENGTH_LONG);
					toast.show();
				}
			});
		}
	}
	
	public PetaForm getPetaForm() {
		return petaForm;
	}
	public void setPetaForm(PetaForm petaForm) {
		this.petaForm = petaForm;
	}

	
	private Circle createCircle1(GeoPoint geoPoint) {
		Paint paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintFill.setStyle(Paint.Style.FILL);
		paintFill.setColor(Color.BLUE);
		paintFill.setAlpha(16);
		Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setColor(Color.BLUE);
		paintStroke.setAlpha(128);
		paintStroke.setStrokeWidth(3);
		return new Circle(geoPoint, 200, paintFill, paintStroke);
	}


	private Polyline createPolyline1(GeoPoint geoPoint, GeoPoint geoPoint1) {
		List<GeoPoint> geoPoints = Arrays.asList(geoPoint, geoPoint1);
		PolygonalChain polygonalChain = new PolygonalChain(geoPoints);
		Paint paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
		paintStroke.setStyle(Paint.Style.STROKE);
		paintStroke.setColor(Color.MAGENTA);
		paintStroke.setAlpha(128);
		paintStroke.setStrokeWidth(5);
		paintStroke.setPathEffect(new DashPathEffect(new float[] { 25, 15 }, 0));
		return new Polyline(polygonalChain, paintStroke);
	}

	private Marker createMarker(int resourceIdentifier, GeoPoint geoPoint) {
		Drawable drawable = getResources().getDrawable(resourceIdentifier);		
		return new Marker(geoPoint, Marker.boundCenterBottom(drawable));
	}


	private class GPSLocationListener implements LocationListener {

		// Menampilkan Update Lokasi GPS terkini
		@Override
		public void onLocationChanged(Location loc) {

			if (loc != null) {		
			
				getPetaForm().getLocationManager1().setText(
			    "Lokasiku Lat: " + loc.getLatitude()+ konversiKoordinat.konversiKoordinatLatitude(loc)+
			    " Long: " +loc.getLongitude()+ konversiKoordinat.konversiKoordinatLongitude(loc)+")"
						);
//				getPetaForm().getLocationManager3().setText("Ketinggian diatas permukaan laut M:"
//						+String.valueOf(new BigDecimal(loc.getAltitude()).setScale(5,RoundingMode.HALF_EVEN))
//						+" Keakuratan Meter:"+loc.getAccuracy()
//						+" Privider GPS:"+loc.getProvider()
//						);
				
				setLatitudeKitaBerdiri(loc.getLatitude());
				setLongitudeKitaBerdiri(loc.getLongitude());
				setLocation(loc);
				GeoPoint mapCenter = new GeoPoint(loc.getLatitude(),loc.getLongitude());	
				Marker marker1 = createMarker(R.drawable.marker_green,mapCenter);
				Marker marker2 = createMarker(R.drawable.ic_maps_indicator_current_position_anim1, mapCenter);
							
				overlayItems.removeAll(overlayItems);
				overlayItems.add(marker1);
				overlayItems.add(marker2);

				getPetaForm().getMapView().getOverlays().add(listOverlay);
				getPetaForm().getMapView().getOverlayController().redrawOverlays();//methode untuk mempercepat menampilkan hasil
				
			}
		}

		@Override
		public void onProviderDisabled(String arg0) {
			// TODO Auto-generated method stub
			showToastOnUiThread("GPS Provider status Disable");
		}

		@Override
		public void onProviderEnabled(String arg0) {
			// TODO Auto-generated method stub
			showToastOnUiThread("GPS Provider status Enable");
			
		}

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// TODO Auto-generated method stub
//			showToastOnUiThread("Location status changed");
//			getPetaForm().getLocationManager3().append(" Status changed");
		}

}
	private void tampilkanAlertGPS() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("GPS belum diaktifkan. Apakah anda akan mengakstifkan konfigurasi GPS?").setCancelable(false)
				.setPositiveButton("Pilih Settings untuk mengaktifkan GPS",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}
	
	private void konfirmasiMenghitung() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setMessage("Apakah anda yakin akan menghitung jarak "+(koordinat.size())+" titik tersebut").setCancelable(false)
				.setPositiveButton("Ok",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								menampilkanJarak();
							}
						});
		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	public double getLongitudeKitaBerdiri() {
		return longitudeKitaBerdiri;
	}

	public void setLongitudeKitaBerdiri(double longitudeKitaBerdiri) {
		this.longitudeKitaBerdiri = longitudeKitaBerdiri;
	}

	public double getLatitudeKitaBerdiri() {
		return latitudeKitaBerdiri;
	}

	public void setLatitudeKitaBerdiri(double latitudeKitaBerdiri) {
		this.latitudeKitaBerdiri = latitudeKitaBerdiri;
	}
	@SuppressWarnings("deprecation")
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:			
			Intent setting = new Intent(android.provider.Settings.ACTION_SETTINGS);
			startActivity(setting);
			return true;
			
		case R.id.tarik_garis:	
//			double jarak3=0;
//			Haversine haversine = new Haversine();
			if(koordinat.size()>1){
				konfirmasiMenghitung();				
			}	
			else{
				onClick(petaForm.getMapView());
				showToastOnUiThread("Touch atau klik dahulu minimal 2 Titik di peta");
				}			
			return true;
			
		case R.id.map_file_centre:
			MapFileInfo mapFileInfo = petaForm.getMapView().getMapDatabase().getMapFileInfo();
			petaForm.getMapView().getMapViewPosition().setCenter(mapFileInfo.boundingBox.getCenterPoint());
			return true;
			
		case R.id.bersihkan_penanda:
			overlayItems1.removeAll(overlayItems1);
			overlayItems2.removeAll(overlayItems2);
			overlayItems3.removeAll(overlayItems3);
			overlayItems4.removeAll(overlayItems4);
			overlayItems.removeAll(overlayItems);			
			petaForm.reset();
			petaForm.getMapView().getOverlays().remove(listOverlay);
			getPetaForm().getMapView().getOverlays().remove(listOverlay2);
			getPetaForm().getMapView().getOverlays().remove(listOverlay3);
			getPetaForm().getMapView().getOverlays().remove(listOverlay4);
			koordinat.removeAll(koordinat);
			petaForm.getMapView().getOverlayController().redrawOverlays();//methode untuk mempercepat menampilkan hasil
			klik=1;
			return true;
		
		case R.id.show_mylocation:			
			if((getLatitudeKitaBerdiri()!=0 )&&(getLongitudeKitaBerdiri())!=0){
//				Toast.makeText(this, "Anda memilih Show My Location",Toast.LENGTH_LONG).show();
				GeoPoint geoPoint = new  GeoPoint(getLatitudeKitaBerdiri(), getLongitudeKitaBerdiri());
				 Marker marker1 = createMarker(R.drawable.marker_orange, geoPoint);
				 overlayItems.removeAll(overlayItems);
				 overlayItems.add(marker1);
//				 double latitude = Double.parseDouble(String.valueOf(new BigDecimal(getLatitudeKitaBerdiri()).setScale(5,RoundingMode.HALF_EVEN)));
//				 double longitude = Double.parseDouble(String.valueOf(new BigDecimal(getLongitudeKitaBerdiri()).setScale(5,RoundingMode.HALF_EVEN)));
//				 overlayItems.add(createCircle1( new  GeoPoint(latitude,longitude)));
				 overlayItems.add(createCircle1( new  GeoPoint(getLatitudeKitaBerdiri(),getLongitudeKitaBerdiri())));
				 petaForm.getMapView().getOverlays().add(listOverlay);							
				MapPosition newMapPosition = new MapPosition(geoPoint, (byte) 16);
				PetaActivity.this.petaForm.getMapView().getMapViewPosition().setMapPosition(newMapPosition);
			}
			else{
				showToastOnUiThread("Menunggu Posisi Dari GPS");
			}
			
//		case R.id.last_known_location:
//			if((getLatitudeKitaBerdiri()!=0 )&&(getLongitudeKitaBerdiri())!=0){				
//				GeoPoint geoPoint = new  GeoPoint(getLatitudeKitaBerdiri(), getLongitudeKitaBerdiri());
//				 Marker marker1 = createMarker(R.drawable.marker_orange, geoPoint);
//				 overlayItems.removeAll(overlayItems);
//				 overlayItems.add(marker1);
////				 double latitude = Double.parseDouble(String.valueOf(new BigDecimal(getLatitudeKitaBerdiri()).setScale(5,RoundingMode.HALF_EVEN)));
////				 double longitude = Double.parseDouble(String.valueOf(new BigDecimal(getLongitudeKitaBerdiri()).setScale(5,RoundingMode.HALF_EVEN)));
////				 overlayItems.add(createCircle1( new  GeoPoint(latitude,longitude)));
//				 overlayItems.add(createCircle1( new  GeoPoint(getLatitudeKitaBerdiri(),getLongitudeKitaBerdiri())));
//				 petaForm.getMapView().getOverlays().add(listOverlay);							
//				MapPosition newMapPosition = new MapPosition(geoPoint, (byte) 16);
//				PetaActivity.this.petaForm.getMapView().getMapViewPosition().setMapPosition(newMapPosition);
//			}
			return true;
			
		case R.id.map_file_properties:
			extracted();
			return true;
			
		case R.id.menu_mapfile:
			startMapFilePicker();			
			return true;
			
		case R.id.menu_screenshot_jpeg:
			this.screenshotCapturer.captureScreenshot(CompressFormat.JPEG);
			return true;

		case R.id.menu_screenshot_png:
			this.screenshotCapturer.captureScreenshot(CompressFormat.PNG);
			return true;

		case R.id.enter_coordinate:
			showDialog(DIALOG_ENTER_COORDINATES);
			return true;
//		case R.id.informasi_lokasi:
//			showDialog(INFORMASI_LOKASIKU);
//			return true;
		case R.id.about:
			startActivity(new Intent(this, InfoView.class));
			return true;
			
		case R.id.menu_preferences:
			startActivity(new Intent(this, EditPreferences.class));
			return true;
			
		case R.id.menu_render_theme:
			return true;

		case R.id.menu_render_theme_osmarender:
			this.petaForm.getMapView().setRenderTheme(InternalRenderTheme.OSMARENDER);
			return true;

		case R.id.menu_render_theme_select_file:
			startRenderThemePicker();
			return true;
			
	default:
		return false;
	}
	}
	
	public void menampilkanJarak(){
		jarak3=0;
		Haversine haversine = new Haversine();
		if(koordinat.size()>1){
			for (int a=1;a<koordinat.size();a++){
				overlayItems4.add(createPolyline1(
						new GeoPoint(koordinat.get((a-1)).getLatitude(), 
								koordinat.get((a-1)).getLongitude()), 
								new GeoPoint(koordinat.get(a).getLatitude(),
										koordinat.get(a).getLongitude())));
				petaForm.getMapView().getOverlays().add(listOverlay4);		
				jarak3=jarak3+haversine.haversine1(koordinat.get((a-1)).getLatitude(),koordinat.get((a-1)).getLongitude(), koordinat.get(a).getLatitude(), koordinat.get(a).getLongitude() );
			}
//			if(koordinat.size()>4){
//				jarak3=jarak3+haversine.haversine1(koordinat.get((koordinat.size()-1)).getLatitude(),koordinat.get((koordinat.size()-1)).getLongitude(), koordinat.get(0).getLatitude(), koordinat.get(0).getLongitude() );
//				overlayItems4.add(createPolyline1(new GeoPoint(koordinat.get(koordinat.size()-1).getLatitude(), koordinat.get(koordinat.size()-1).getLongitude()), new GeoPoint(koordinat.get(0).getLatitude(), koordinat.get(0).getLongitude())));
//			}
			
			
			petaForm.getLocationManager2().setText("jarak Haversine "+koordinat.size()+" Titik = " 
					+String.valueOf(new BigDecimal((jarak3*1000)).setScale(3,RoundingMode.HALF_EVEN))+ " Meter. atau "				
					+String.valueOf(new BigDecimal(jarak3).setScale(3,RoundingMode.HALF_EVEN))+ " KiloMeter "					
				);
			petaForm.getMapView().getOverlayController().redrawOverlays();//methode untuk mempercepat menampilkan hasil
		}
		
//		showDialog(MENAMPILKAN_HASIL_HAVERSINE);		
		
	}
	
	@SuppressWarnings("deprecation")
	private void extracted() {
		showDialog(DIALOG_INFO_MAP_FILE);
	}
	
	private void startMapFilePicker() {		
		FilePicker.setFileDisplayFilter(FILE_FILTER_EXTENSION_MAP);		
		FilePicker.setFileSelectFilter(validMapFile);
		startActivityForResult(new Intent(this, FilePicker.class), SELECT_MAP_FILE);
	}
	
	/**
	 * Sets all file filters and starts the FilePicker to select an XML file.
	 */
	private void startRenderThemePicker() {
		FilePicker.setFileDisplayFilter(FILE_FILTER_EXTENSION_XML);
		FilePicker.setFileSelectFilter(new ValidRenderTheme());
		startActivityForResult(new Intent(this, FilePicker.class), SELECT_RENDER_THEME_FILE);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		if (id == DIALOG_ENTER_COORDINATES) {
			builder.setIcon(android.R.drawable.ic_menu_mylocation);
			builder.setTitle(R.string.menu_position_enter_coordinates);
			LayoutInflater factory = LayoutInflater.from(this);
			final View view = factory.inflate(
					R.layout.dialog_enter_coordinates, null);
			builder.setView(view);
			builder.setPositiveButton(R.string.go_to_position,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// disable GPS follow mode if it is enabled
//							disableSnapToLocation(true);
							// set the map center and zoom level
							EditText latitudeView = (EditText) view	.findViewById(R.id.latitude);
							EditText longitudeView = (EditText) view.findViewById(R.id.longitude);
							double latitude = Double.parseDouble(latitudeView.getText().toString());
							double longitude = Double.parseDouble(longitudeView.getText().toString());
							GeoPoint geoPoint = new GeoPoint(latitude,longitude);
							SeekBar zoomLevelView = (SeekBar) view.findViewById(R.id.zoomLevel);
							MapPosition newMapPosition = new MapPosition(geoPoint, (byte) zoomLevelView.getProgress());
							petaForm.getMapView().getMapViewPosition().setMapPosition(newMapPosition);
							Marker marker1 = createMarker(R.drawable.marker_blue, geoPoint);
//							overlayItems2.removeAll(overlayItems2);
							overlayItems2.add(marker1);
							overlayItems2.add(createCircle1(new GeoPoint(latitude, longitude)));
							petaForm.getMapView().getOverlays().add(listOverlay2);
							getPetaForm().getMapView().getOverlayController().redrawOverlays();//methode untuk mempercepat menampilkan hasil
							
							koordinat1 = new Koordinat();
							koordinat1.setLatitude(latitude);
							koordinat1.setLongitude(longitude);
							koordinat.add(koordinat1);
							
						}
					});
			builder.setNegativeButton(R.string.cancel, null);
			return builder.create();
		}
		else if (id == MENAMPILKAN_HASIL_HAVERSINE) {
//			builder.setIcon(android.R.drawable.ic_menu_mylocation);
			builder.setTitle(R.string.hasil_perhitungan_haversine);
			LayoutInflater factory = LayoutInflater.from(this);
			final View view = factory.inflate(
					R.layout.menampilkan_hasil_haversine, null);
			builder.setView(view);
			
			TextView hasil1 = (TextView) view.findViewById(R.id.textView_hasil1);
			TextView hasil2 = (TextView) view.findViewById(R.id.TextView_hasil2);
			
			hasil1.setText("jumlah titik = "+(koordinat.size()));
					
			hasil2.setText("jarak = " 
					+String.valueOf(new BigDecimal((jarak3*1000)).setScale(3,RoundingMode.HALF_EVEN))+ " Meter. atau "				
					+String.valueOf(new BigDecimal(jarak3).setScale(3,RoundingMode.HALF_EVEN))+ " KiloMeter "					
				);
 
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
			builder.setNegativeButton(R.string.cancel, null);
			return builder.create();
			
		}
		else if (id == INFORMASI_LOKASIKU) {
			if (getLocation() != null){
			builder.setIcon(android.R.drawable.ic_menu_mylocation);
			builder.setTitle(R.string.informasi_lokasi);
			LayoutInflater factory = LayoutInflater.from(this);
			final View view = factory.inflate(
					R.layout.informasi_lokasiku, null);
			builder.setView(view);
			
			TextView latitude = (TextView) view.findViewById(R.id.latitude);
			TextView longitude = (TextView) view.findViewById(R.id.longitude);
			TextView ketinggian = (TextView) view.findViewById(R.id.ketinggian);
			TextView keakuratan = (TextView) view.findViewById(R.id.keakuratan);
			TextView providerGps = (TextView) view.findViewById(R.id.provider_gps);
			
			latitude.setText("Latitude: " + getLocation().getLatitude()+" "+ konversiKoordinat.konversiKoordinatLatitude(getLocation()));
			
			longitude.setText(
				    "Longitude: " +getLocation().getLongitude()+" "+ konversiKoordinat.konversiKoordinatLongitude(getLocation())+")"
							);			
			ketinggian.setText("Ketinggian diatas permukaan laut Meter : "
					+String.valueOf(new BigDecimal(getLocation().getAltitude()).setScale(5,RoundingMode.HALF_EVEN))
					
					);
			keakuratan.setText(
					"Keakuratan Meter :"+getLocation().getAccuracy()				
					);
			providerGps.setText("Provider GPS :"+getLocation().getProvider()
					);
								
			builder.setPositiveButton(R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
						}
					});
			builder.setNegativeButton(R.string.cancel, null);
		}
			else{
				showToastOnUiThread("Menunggu Posisi Dari GPS");
			}
			
			return builder.create();
		
		}
		else if (id == DIALOG_LOCATION_PROVIDER_DISABLED) {
			builder.setIcon(android.R.drawable.ic_menu_info_details);
			builder.setTitle(R.string.error);
			builder.setMessage(R.string.no_location_provider_available);
			builder.setPositiveButton(R.string.ok, null);
			return builder.create();
		} else if (id == DIALOG_INFO_MAP_FILE) {
			builder.setIcon(android.R.drawable.ic_menu_info_details);
			builder.setTitle(R.string.menu_info_map_file);
			LayoutInflater factory = LayoutInflater.from(this);
			builder.setView(factory.inflate(R.layout.dialog_info_map_file, null));
			
			builder.setPositiveButton(R.string.ok, null);
			builder.setInverseBackgroundForced(true);
			
			return builder.create();
		} else {
			// do dialog will be created
			return null;
		}
	}
	
	@SuppressWarnings("deprecation")
	protected void onPrepareDialog(int id, final Dialog dialog) {
		if (id == DIALOG_ENTER_COORDINATES) {
			// latitude
			EditText editText = (EditText) dialog.findViewById(R.id.latitude);
			MapViewPosition mapViewPosition = this.petaForm.getMapView().getMapViewPosition();
			GeoPoint mapCenter = mapViewPosition.getCenter();
			editText.setText(Double.toString(mapCenter.latitude));

			// longitude
			editText = (EditText) dialog.findViewById(R.id.longitude);
			editText.setText(Double.toString(mapCenter.longitude));

			// zoom level
			SeekBar zoomlevel = (SeekBar) dialog.findViewById(R.id.zoomLevel);
			zoomlevel.setMax(this.petaForm.getMapView().getDatabaseRenderer().getZoomLevelMax());
			zoomlevel.setProgress(mapViewPosition.getZoomLevel());

			// zoom level value
			final TextView textView = (TextView) dialog.findViewById(R.id.zoomlevelValue);
			textView.setText(String.valueOf(zoomlevel.getProgress()));
			zoomlevel.setOnSeekBarChangeListener(new SeekBarChangeListener(textView));
		} else if (id == DIALOG_INFO_MAP_FILE) {
			MapFileInfo mapFileInfo = this.petaForm.getMapView().getMapDatabase().getMapFileInfo();

			// map file name
			TextView textView = (TextView) dialog.findViewById(R.id.infoMapFileViewName);
			textView.setText(this.petaForm.getMapView().getMapFile().getAbsolutePath());

			// map file size
			textView = (TextView) dialog.findViewById(R.id.infoMapFileViewSize);
			textView.setText(FileUtils.formatFileSize(mapFileInfo.fileSize, getResources()));

			// map file version
			textView = (TextView) dialog.findViewById(R.id.infoMapFileViewVersion);
			textView.setText(String.valueOf(mapFileInfo.fileVersion));

			// map file debug
			textView = (TextView) dialog.findViewById(R.id.infoMapFileViewDebug);
			if (mapFileInfo.debugFile) {
				textView.setText(R.string.info_map_file_debug_yes);
			} else {
				textView.setText(R.string.info_map_file_debug_no);
			}

			// map file date
			textView = (TextView) dialog.findViewById(R.id.infoMapFileViewDate);
			Date date = new Date(mapFileInfo.mapDate);
			textView.setText(DateFormat.getDateTimeInstance().format(date));

			// map file area
			textView = (TextView) dialog.findViewById(R.id.infoMapFileViewArea);
			BoundingBox boundingBox = mapFileInfo.boundingBox;
			textView.setText(boundingBox.minLatitude + ", " + boundingBox.minLongitude + " � \n"
					+ boundingBox.maxLatitude + ", " + boundingBox.maxLongitude);

			// map file start position
			textView = (TextView) dialog.findViewById(R.id.infoMapFileViewStartPosition);
			GeoPoint startPosition = mapFileInfo.startPosition;
			if (startPosition == null) {
				textView.setText(null);
			} else {
				textView.setText(startPosition.latitude + ", " + startPosition.longitude);
			}

			// map file start zoom level
			textView = (TextView) dialog.findViewById(R.id.infoMapFileViewStartZoomLevel);
			Byte startZoomLevel = mapFileInfo.startZoomLevel;
			if (startZoomLevel == null) {
				textView.setText(null);
			} else {
				textView.setText(startZoomLevel.toString());
			}

			// map file language preference
			textView = (TextView) dialog.findViewById(R.id.infoMapFileViewLanguagePreference);
			textView.setText(mapFileInfo.languagePreference);

			// map file comment text
			textView = (TextView) dialog.findViewById(R.id.infoMapFileViewComment);
			textView.setText(mapFileInfo.comment);

			// map file created by text
			textView = (TextView) dialog.findViewById(R.id.infoMapFileViewCreatedBy);
			textView.setText(mapFileInfo.createdBy);
		} else {
			super.onPrepareDialog(id, dialog);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (requestCode == SELECT_MAP_FILE) {
			if (resultCode == RESULT_OK) {
				if (intent != null && intent.getStringExtra(FilePicker.SELECTED_FILE) != null) {
					this.petaForm.getMapView().setMapFile(new File(intent.getStringExtra(FilePicker.SELECTED_FILE)));
				}
			} else if (resultCode == RESULT_CANCELED && this.petaForm.getMapView().getMapFile() == null) {
				finish();
			}
		} else if (requestCode == SELECT_RENDER_THEME_FILE && resultCode == RESULT_OK && intent != null
				&& intent.getStringExtra(FilePicker.SELECTED_FILE) != null) {
			try {
				this.petaForm.getMapView().setRenderTheme(new File(intent.getStringExtra(FilePicker.SELECTED_FILE)));
			} catch (FileNotFoundException e) {
				showToastOnUiThread(e.getLocalizedMessage());
			}
		}
	}
	
	@SuppressLint("Wakelock")
	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

		MapScaleBar mapScaleBar = this.petaForm.getMapView().getMapScaleBar();
		mapScaleBar.setShowMapScaleBar(sharedPreferences.getBoolean("showScaleBar", false));
		String scaleBarUnitDefault = getString(R.string.preferences_scale_bar_unit_default);
		String scaleBarUnit = sharedPreferences.getString("scaleBarUnit", scaleBarUnitDefault);
		mapScaleBar.setImperialUnits(scaleBarUnit.equals("imperial"));

		try {
			String textScaleDefault = getString(R.string.preferences_text_scale_default);
			this.petaForm.getMapView().setTextScale(Float.parseFloat(sharedPreferences.getString("textScale", textScaleDefault)));
		} catch (NumberFormatException e) {
			this.petaForm.getMapView().setTextScale(1);
		}

		if (sharedPreferences.getBoolean("fullscreen", false)) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		} else {
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		}
		if (sharedPreferences.getBoolean("wakeLock", false) && !this.wakeLock.isHeld()) {
			this.wakeLock.acquire();
		}

		boolean persistent = sharedPreferences.getBoolean("cachePersistence", false);
		int capacity = Math.min(sharedPreferences.getInt("cacheSize", FILE_SYSTEM_CACHE_SIZE_DEFAULT),FILE_SYSTEM_CACHE_SIZE_MAX);
		TileCache fileSystemTileCache = this.petaForm.getMapView().getFileSystemTileCache();
		fileSystemTileCache.setPersistent(persistent);
		fileSystemTileCache.setCapacity(capacity);
		float moveSpeedFactor = Math.min(sharedPreferences.getInt("moveSpeed", MOVE_SPEED_DEFAULT), MOVE_SPEED_MAX) / 10f;
		this.petaForm.getMapView().getMapMover().setMoveSpeedFactor(moveSpeedFactor);
		this.petaForm.getMapView().getFpsCounter().setFpsCounter(sharedPreferences.getBoolean("showFpsCounter", false));
		boolean drawTileFrames = sharedPreferences.getBoolean("drawTileFrames", false);
		boolean drawTileCoordinates = sharedPreferences.getBoolean("drawTileCoordinates", false);
		boolean highlightWaterTiles = sharedPreferences.getBoolean("highlightWaterTiles", false);		
		DebugSettings debugSettings = new DebugSettings(drawTileCoordinates, drawTileFrames, highlightWaterTiles);
		this.petaForm.getMapView().setDebugSettings(debugSettings);

		if (this.petaForm.getMapView().getMapFile() == null) {
			startMapFilePicker();
		}
	}
	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}

package ca.nehil.rter.streamingapp2;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import ca.nehil.rter.streamingapp2.overlay.IndicatorFrame;
import ca.nehil.rter.streamingapp2.overlay.Triangle;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.util.Log;

public class POI {

	SensorSource sensorSource;
	protected ArrayList<POI> poiList;
	double camAngleHorizontal;
	double camAngleVertical;
	IndicatorFrame squareFrame;
	Triangle triangleFrame;
	boolean showLog;
	int fooCount, debugCount;
	private SharedPreferences storedValues;
	private float screenHeight;
	private float screenWidth;
	
	public POI(Context context, int _poiId, Double _remoteBearing, double _lat, double _lng, String _color, String _curThumbnailURL, String _type) {
		poiId = _poiId;
		remoteBearing = _remoteBearing; //orientation of device relative to N
		loc = new Location("poi");
		loc.setLatitude(_lat);
		loc.setLongitude(_lng);
		color = _color;
		curThumbnailURL = _curThumbnailURL;
		type = _type;
		sensorSource = SensorSource.getInstance(context);
		showLog = true;
		fooCount= 0;
		debugCount = 0;
		
		storedValues = context.getSharedPreferences(context.getString(R.string.sharedPreferences_filename), Context.MODE_PRIVATE);
		camAngleVertical = storedValues.getFloat("CamVerticalViewAngle", 46);
		camAngleHorizontal = storedValues.getFloat("CamHorizontalViewAngle", 60);
		screenWidth = storedValues.getFloat("screenSize.x", 0);
		screenHeight = storedValues.getFloat("screenSize.y", 0);
		Log.d("CameraDebug", "scrn ht poi: " + screenHeight);
	}

	public void updatePOIList(ArrayList<POI> newPoi){
		poiList = new ArrayList<POI>(newPoi);
	}

	public int poiId;
	Location loc;
	public Double remoteBearing; //angle of device relative to N
	public String curThumbnailURL;
	public String color;
	public String type;
	public float bearingTo(Location fromLoc) {
		return fromLoc.bearingTo(loc);
	}
	public float relativeBearingTo(Location fromLoc) { //bearing relative to user position
//		Log.d("SensorDebug", "curr orien: " + sensorSource.getCurrentOrientation());
		return minDegreeDelta(fromLoc.bearingTo(loc), sensorSource.getCurrentOrientation());
		//		return minDegreeDelta(fromLoc.bearingTo(loc), (float)sensorSource.getHeading());
	}
	public float distanceTo(Location fromLoc) {
		return fromLoc.distanceTo(loc);
	}

	private float minDegreeDelta(float deg1, float deg2) {
		float delta = deg1-deg2;
		if(delta > 180) delta -= 360;
		if(delta < -180) delta += 360;
		Log.d("DegreeDelta", "Delta: "+ delta + " deg1: " + deg1 + " deg2: " + deg2);

		return delta;
	}

	public int getId(){
		return poiId;
	}

	public Location getLocation(){
		return loc;
	}

	public Double getRemoteBearing(){
		return remoteBearing;
	}

	public String getThumbnailUrl(){
		return curThumbnailURL;
	}

	public String getColor(){
		return color;
	}

	public String getType(){
		return type;
	}

	/*
	 * Use this method to render each POI, called from the frame render in CameraGLRenderer.java
	 */
	public void render(GL10 gl, Location userLocation, Point screenSize){
		gl.glLoadIdentity();
		
		if( triangleFrame == null ) {
			triangleFrame = new Triangle();
		}
		
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 0.0f, -6.0f);
		gl.glMultMatrixf(sensorSource.getLandscapeRotationMatrix(), 0);

		gl.glPushMatrix();
		gl.glRotatef(90, 0, 0, 1);
		gl.glTranslatef(0.0f, 1.0f, 0.0f);
		triangleFrame.colour(Triangle.Colour.RED);
		for( int i = 0; i < 8; i++ ) {
			gl.glRotatef(360.0f/8.0f, 0, 1, 0);
			triangleFrame.draw(gl);
		}
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glTranslatef(0.0f, 1.0f, 0.0f);
		triangleFrame.colour(Triangle.Colour.GREEN);
		for( int i = 0; i < 8; i++ ) {
			gl.glRotatef(360.0f/8.0f, 0, 1, 0);
			triangleFrame.draw(gl);
		}
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		gl.glRotatef(90, -1, 0, 0);
		gl.glTranslatef(0.0f, 1.0f, 0.0f);
		triangleFrame.colour(Triangle.Colour.BLUE);
		for( int i = 0; i < 8; i++ ) {
			gl.glRotatef(360.0f/8.0f, 0, 1, 0);
			triangleFrame.draw(gl);
		}
		gl.glPopMatrix();
		
		gl.glPopMatrix();

		gl.glMultMatrixf(sensorSource.getLandscapeRotationMatrix(), 0);
		if(userLocation != null){
			//gl.glTranslatef(0, 10, 10);
			float scale = 100000.0f;
			gl.glTranslatef((float)(loc.getLongitude() - userLocation.getLongitude()) * scale, (float)(loc.getLatitude() - userLocation.getLatitude()) * scale, 0.0f);
		}
		
		if(this.type.equals("streaming-video-v1") || this.type.equals("type1")){
			squareFrame = new IndicatorFrame();
			squareFrame.draw(gl);
		}else if (this.type.equals("beacon") || this.type.equals("type2")){
			gl.glPushMatrix();
			//gl.glScalef(100, 100, 100);
			triangleFrame.colour(Triangle.Colour.GREEN);
			gl.glRotatef(90, 1, 0, 0);
			gl.glScalef(5, 5, 5);
			for( int i = 0; i < 8; i++ ) {
				gl.glRotatef(360.0f/8.0f, 0, 1, 0);
				triangleFrame.draw(gl);
			}
			gl.glPopMatrix();
		}
		
	}

}

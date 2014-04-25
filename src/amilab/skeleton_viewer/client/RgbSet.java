package amilab.skeleton_viewer.client;

import amilab.skeleton_viewer.shared.RgbMeasurements;

public class RgbSet {
	private final TimestampsList timestamps;
	private final String sensorId;
	
	public RgbSet(RgbMeasurements rm) {
		long[] ts = new long[rm.getImages().size()];
		for (int i = 0; i < ts.length; i++) {
			ts[i] = rm.getImages().get(i).created_at;
		}
		
		timestamps = new TimestampsList(ts);
		sensorId = rm.getSensorId();
	}
	
	public String getImageUrlAt(long time) {
		int index = timestamps.getIndexFor(time);
		if (index < 0) 
			return null;		
		else 
			return "/image-rgb/" + sensorId + "/" + timestamps.getTimestamp(index) + ".jpg";
	}
	
	public TimestampsList getTimestamps() {
		return timestamps;
	}
}

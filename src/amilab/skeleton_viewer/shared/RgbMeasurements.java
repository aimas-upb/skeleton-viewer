package amilab.skeleton_viewer.shared;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RgbMeasurements implements Serializable, MeasurmentsSet {
	String sensorId;
	List<ImageRgb> images;
	
	public RgbMeasurements(String sensorId, List<ImageRgb> images) {
		this.sensorId = sensorId;
		this.images = images;
	}
	
	public RgbMeasurements() {
		this("NA", new ArrayList<ImageRgb>());
	}
	
	public String getSensorId() {
		return sensorId;
	}

	public List<ImageRgb> getImages() {
		return images;
	}


}

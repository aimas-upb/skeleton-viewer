package amilab.skeleton_viewer.client;

import java.util.HashMap;
import java.util.Map;

import javax.vecmath.Color4f;

public class Colors {
	private static Map<String, Color4f> colorsBySensor = new HashMap<String, Color4f>();
	
	static {
		colorsBySensor.put("daq-01", new Color4f(1, 0, 0, 1));
		colorsBySensor.put("daq-02", new Color4f(0.6f, 0,0.6f, 1));
		colorsBySensor.put("daq-03", new Color4f(0, 1, 0, 1));
		colorsBySensor.put("daq-04", new Color4f(1, 0.64f, 0, 1));
		colorsBySensor.put("daq-05", new Color4f(1, 0, 1, 1));
		colorsBySensor.put("daq-06", new Color4f(1, 0, 1, 1));		
	}
	
	private static Color4f defaultColor = new Color4f(1, 1, 1, 1);
	
	public static Color4f getColor(String sensorId) {
		Color4f c = colorsBySensor.get(sensorId);
		if (c == null) {
			return defaultColor;
		} else {
			return c;
		}
	}
}

package amilab.skeleton_viewer.shared;

import java.io.Serializable;
import java.util.Comparator;

public class ImageRgb  implements Serializable {
	
	public String sensor_id;
	public long created_at;
	
	public static Comparator<ImageRgb> COMPARATOR_BY_TIME = new Comparator<ImageRgb>() {
		@Override
		public int compare(ImageRgb o1, ImageRgb o2) {
			return (int)(o1.created_at - o2.created_at);
		}
	};
	

}

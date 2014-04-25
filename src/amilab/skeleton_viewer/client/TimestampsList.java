package amilab.skeleton_viewer.client;

import java.util.Arrays;

public class TimestampsList {
	
	enum MatchPolicy {
		CLOSEST_BEFORE,
		CLOSEST_AFTER
	}
	
	private final long[] timestamps;
	
	public TimestampsList(long[] timestamps) {
		this.timestamps = timestamps;
	}

	public long[] getTimestamps() {
		return timestamps;
	}

	public int getIndexFor(long time) {
		return getIndexFor(time, MatchPolicy.CLOSEST_BEFORE);
	}
	
	public int getIndexFor(long time, MatchPolicy policy) {
		int pos = Arrays.binarySearch(timestamps, time);
		if (pos >= 0) 
			return pos;
		else {
			// the exact timestamp was not found
			int ip = - 1 - pos;//insertion point
			pos = policy == MatchPolicy.CLOSEST_BEFORE ? ip - 1 : ip;
			
			if (pos >= timestamps.length)
				return -1;
			else 
				return pos;
		}
	}
	
	public long getTimestamp(int index) {
		return timestamps[index];
	}
	
	public long getMinTime() {
		return timestamps[0];
	}
	
	public long getMaxTime() {
		return timestamps[timestamps.length - 1];
	}

}

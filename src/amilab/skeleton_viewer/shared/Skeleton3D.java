package amilab.skeleton_viewer.shared;

import java.io.Serializable;
import java.util.Comparator;

import javax.vecmath.Vector3f;

public class Skeleton3D implements Serializable {
	
	public static final int NB_POS = 15;
	
	public static Comparator<Skeleton3D> COMPARATOR_BY_TIME = new Comparator<Skeleton3D>() {
		@Override
		public int compare(Skeleton3D o1, Skeleton3D o2) {
			return (int)(o1.time - o2.time);
		}
	};
	
	public String sensorId;
	public long time; 
	public int lineNumber;
	
	Vector3f head ;
	Vector3f neck ;
	Vector3f left_shoulder ;
	Vector3f torso ;
	Vector3f left_knee ;
	Vector3f right_elbow ;
	Vector3f right_shoulder ;
	Vector3f right_hand ;
	Vector3f left_hip ;
	Vector3f left_foot ;
	Vector3f left_elbow ;
	Vector3f right_foot ;
	Vector3f left_hand ;
	Vector3f right_knee ;
	Vector3f right_hip ;
	
	public float[] getPositions() {
		int index = 0;
		float[] positions = new float[3 * NB_POS];
		
		index = addPos(head,            positions, index);
		index = addPos(neck,            positions, index);
		index = addPos(left_shoulder,   positions, index);
		index = addPos(torso,           positions, index);
		index = addPos(left_knee,       positions, index);
		index = addPos(right_elbow,     positions, index);
		index = addPos(right_shoulder,  positions, index);
		index = addPos(right_hand,      positions, index);
		index = addPos(left_hip,        positions, index);
		index = addPos(left_foot,       positions, index);
		index = addPos(left_elbow,      positions, index);
		index = addPos(right_foot,      positions, index);
		index = addPos(left_hand,       positions, index);
		index = addPos(right_knee,      positions, index);
		index = addPos(right_hip,       positions, index);		
		return positions;
	}
	
	/**
	 * Following segments need to be draw:
	
	('head', 'neck'),
	('neck', 'left_shoulder'),
	('left_shoulder', 'left_elbow'),
	('left_elbow', 'left_hand'),
	
	('neck', 'right_shoulder'),
	('right_shoulder', 'right_elbow'),
	('right_elbow', 'right_hand'),
	
	('left_shoulder', 'torso'),
	('right_shoulder', 'torso'),
	
	('torso','left_hip'),
	('left_hip','left_knee'),
	('left_knee','left_foot'),

	('torso','right_hip'),
	('right_hip','right_knee'),
	('right_knee','right_foot'),

	('left_hip','right_hip'),
	 */
	
	public static final int[] lines = {
		 0, 1,
		 1, 2,
		 2, 10,
		 10, 12,
		 1, 6,
		 6, 5,
		 5, 7,
		 2, 3,
		 6, 3,
		 3, 8,
		 8, 4,
		 4, 9,
		 3, 14,
		14, 13,
		13, 11,
		 8, 14,			
	};
	
	
	private int addPos(Vector3f src, float[] dest, int pos) {
		dest[pos] 		= src.x;
		dest[pos + 1] 	= src.y;
		dest[pos + 2] 	= src.z;
		return pos + 3;
	}
}

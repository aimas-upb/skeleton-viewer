package amilab.skeleton_viewer.shared;

import java.io.Serializable;
import java.util.List;

import javax.vecmath.Matrix4f;

public class SkeletonMeasurements implements Serializable, MeasurmentsSet {
	
	String sensorId;
	List<Skeleton3D> skeletons;
	Matrix4f sensorTransform;
	
	public SkeletonMeasurements() {
	}

	public SkeletonMeasurements(
			String sensorId, 
			List<Skeleton3D> skeletons,
			Matrix4f sensorTransform) {
		this.sensorId = sensorId;
		this.skeletons = skeletons;
		this.sensorTransform = sensorTransform;
	}

	public String getSensorId() {
		return sensorId;
	}

	public List<Skeleton3D> getSkeletons() {
		return skeletons;
	}

	public Matrix4f getSensorTransform() {
		return sensorTransform;
	}	

}

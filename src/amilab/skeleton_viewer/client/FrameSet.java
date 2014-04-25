package amilab.skeleton_viewer.client;

import gwt.g3d.client.gl2.GL2;
import gwt.g3d.client.gl2.enums.BeginMode;
import gwt.g3d.client.mesh.StaticMesh;
import gwt.g3d.client.shader.FlatShader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.vecmath.Color4f;
import javax.vecmath.Matrix4f;

import amilab.skeleton_viewer.shared.Skeleton3D;
import amilab.skeleton_viewer.shared.SkeletonMeasurements;

public class FrameSet {

	private Iterator<Skeleton3D> data;
	private final List<StaticMesh> meshes;
	private final TimestampsList timestamps;
	private final Skeleton3D[] skeletons;
	private final GL2 gl;
	private final FlatShader shader;
	private final Matrix4f sensorTransform;
	private final Color4f color;
	/** ms */
	private static final int TIME_TRESHOLD = 1000;
	
	public FrameSet(GL2 gl, FlatShader shader, Color4f color, SkeletonMeasurements measurements) {
		this.gl = gl;
		this.shader = shader;
		this.color = color;
		this.data = measurements.getSkeletons().iterator();
		long[] tsArray = new long[measurements.getSkeletons().size()];
		skeletons = new Skeleton3D[measurements.getSkeletons().size()];
		
		for (int i = 0; i < tsArray.length; i++) {
			tsArray[i] = measurements.getSkeletons().get(i).time;
			skeletons[i] = measurements.getSkeletons().get(i);
		}
		
		timestamps = new TimestampsList(tsArray);
		
		this.sensorTransform = measurements.getSensorTransform();
		meshes = new ArrayList<StaticMesh>(measurements.getSkeletons().size());
	}

	public Matrix4f getSensorTransform() {
		return sensorTransform;
	}

	public boolean isLoaded() {
		return data == null;
	}
	
	public int getNbLoadedFrames() {
		return meshes.size();
	}
	
	public void loadNextMesh() {
		if (!isLoaded()) {
			meshes.add(makeSkeleton(gl, data.next()));
			if (!data.hasNext()) {
				data = null;
			}
		}
	}
	
	public boolean isLoadingAt(long time) {
		int index = timestamps.getIndexFor(time);
		if (index < 0) 
			return false;
		else if (time - timestamps.getTimestamp(index) < TIME_TRESHOLD)
			return isLoading(index);
		else 
			return false;
	}
	
	public boolean isLoading(int index) {
		if (index >= 0) 
			return index >= meshes.size();
		else 
			return false;
	}
	
	public StaticMesh getMeshAt(long time) {
		int index = timestamps.getIndexFor(time);
		if (index < 0) 
			return null;
		else if (time - timestamps.getTimestamp(index) < TIME_TRESHOLD && index < meshes.size())
			return meshes.get(index);
		else 
			return null;
	}
	
	public Skeleton3D getSkeletonAt(long time) {
		int index = timestamps.getIndexFor(time);
		if (index < 0) 
			return null;
		else if (time - timestamps.getTimestamp(index) < TIME_TRESHOLD && index < meshes.size())
			return skeletons[index];
		else 
			return null;
	}
	
	public StaticMesh getMesh(int index) {
		return meshes.get(index);
	}

	private StaticMesh makeSkeleton(GL2 gl, Skeleton3D s) {
		float[] normals = null;
		float[] texCoords = null;
		
		StaticMesh mesh = new StaticMesh(gl, s.getPositions(), normals, texCoords, Skeleton3D.lines);
//		StaticMesh mesh = new StaticMesh(gl, PrimitiveFactory.makeBox());
		mesh.setBeginMode(BeginMode.LINES);
		mesh.setPositionIndex(shader.getAttributePosition());
		return mesh;
	}
	
	public void dispose() {
		System.out.println("FrameSet.cleanup()");
		for (StaticMesh m: meshes) {
			m.dispose();
		}
	}

	public Color4f getColor() {
		return color;
	}

	public TimestampsList getTimestamps() {
		return timestamps;
	}
	
}

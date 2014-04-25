package amilab.skeleton_viewer.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;

import org.apache.commons.io.IOUtils;

import amilab.skeleton_viewer.shared.Skeleton3D;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonLoader {
	private Gson gson = new Gson();
	
	public Map<String, Matrix4f> ktMatrixes = new HashMap<String, Matrix4f>();
	
	{
		init();
	}
	
	private void init() {
		//daq-03
		//{"beta": 0.4189, "X": 4490.0, "Y": 2290.0, "alpha": 0.0, "Z": 70.0, "gamma": 0.0}}
		
		//sensor position
		Map<String, float[]> kinects = new HashMap<String, float[]>();
		
		kinects.put("daq-01", new float[] {4000,	2348,	4785,	-3.1415f,	0.5149f, 0});
		kinects.put("daq-02", new float[] {0, 2340, 2612, 1.907f, 0.542f, 0});
		kinects.put("daq-03", new float[] {4490.0f, 2290.0f, 70.0f, 0.0f, 0.4189f, 0.0f});
		kinects.put("daq-04", new float[] {9340, 2299, 1009, -1.5708f, 0.3665f, 0});
		kinects.put("daq-05", new float[] {9340, 2298, 3150, -1.5708f, 0.4363f, 0});
		
		
		for (Entry<String, float[]> e: kinects.entrySet()) {
			float[] sp = e.getValue();
			Matrix4f rotx = new Matrix4f();
			rotx.rotX(sp[4]);
			
			Matrix4f roty = new Matrix4f();
			roty.rotY(sp[3]);
			
			Matrix4f rotz = new Matrix4f();
			rotz.rotZ(sp[5]);
	
			Matrix4f trans = new Matrix4f();
			trans.setIdentity();
	//		trans.setTranslation(new Vector3f(50, 0, 0));
			trans.setTranslation(new Vector3f(sp[0], sp[1], sp[2]));
	
			Matrix4f m = new Matrix4f();
			m.setIdentity();
			
			m.mul(roty);
			m.mul(rotx);
			m.mul(rotz);
			trans.mul(m);
			m = trans;
			ktMatrixes.put(e.getKey(), m);
			
		}
	}
	
	public Matrix4f getKinectMatrix(String id) {
		return new Matrix4f(ktMatrixes.get(id));
	}
	
	public Set<String> getKinectIds() {
		return Collections.unmodifiableSet(ktMatrixes.keySet());
	}
	

	
	public List<Skeleton3D> loadData(String baseFolder, String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(new File(baseFolder, fileName)));
		try {
			List<Skeleton3D> ret = new ArrayList<Skeleton3D>();
	
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				ret.add(loadSkeleton3D(line));
			}
			return ret;
		} finally {
			IOUtils.closeQuietly(reader);
		}
	}
	
	private Skeleton3D loadSkeleton3D(String line) throws IOException {
		JsonParser parser = new JsonParser();
		
		line = line.replaceAll("\"X\"", "\"x\"");
		line = line.replaceAll("\"Y\"", "\"y\"");
		line = line.replaceAll("\"Z\"", "\"z\"");
		
		JsonObject object = (JsonObject) parser.parse(line);
		
		JsonObject o3D = object.getAsJsonObject("skeleton_3D"); 
		Skeleton3D s = gson.fromJson(o3D, Skeleton3D.class);
		
		s.time = object.get("created_at").getAsLong();
		s.sensorId = object.get("sensor_id").getAsString();
		
		return s;
	}
	
}

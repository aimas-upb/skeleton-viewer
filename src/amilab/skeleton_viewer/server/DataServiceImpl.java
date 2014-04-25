package amilab.skeleton_viewer.server;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Matrix4f;
import javax.vecmath.Tuple4f;
import javax.vecmath.Vector3f;
import javax.vecmath.Vector4f;

import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.io.filefilter.FileFilterUtils;

import amilab.skeleton_viewer.client.DataService;
import amilab.skeleton_viewer.shared.Camera;
import amilab.skeleton_viewer.shared.ImageRgb;
import amilab.skeleton_viewer.shared.MeasurmentsSet;
import amilab.skeleton_viewer.shared.RgbMeasurements;
import amilab.skeleton_viewer.shared.Skeleton3D;
import amilab.skeleton_viewer.shared.SkeletonMeasurements;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class DataServiceImpl 
		extends RemoteServiceServlet 
		implements DataService {

	private static final String SOURCE_FOLDER = "source-folder";
	private static JsonLoader loader = new JsonLoader();

	@Override
	public Map<String, Vector3f> kinectPositions() {
		Tuple4f eye = new Vector4f(0, 0, 0, 1);
		List<String> ids = new ArrayList<String>(loader.getKinectIds());
		Map<String, Vector3f> ret = new HashMap<String, Vector3f>();
		for (String kid: ids) {
			Matrix4f m = loader.getKinectMatrix(kid);
			Tuple4f e = new Vector4f();
			m.transform(eye, e);
			ret.put(kid, new Vector3f(e.x, e.y, e.z));
		}
		
		return ret;
	}
	
	@Override
	public List<Camera> getCameras() {
		List<Camera> ret = new ArrayList<Camera>();

		Camera top = new Camera(
				"Top",
				new Vector3f(9340f / 2, 11000, 4785f / 2),
				new Vector3f(9340f / 2, 100, 4785f / 2),
				new Vector3f(0, 0, -1));
		
		ret.add(top);
		
		Tuple4f eye = new Vector4f(0, 0, 0, 1);
		Tuple4f center = new Vector4f(0, 0, 1, 1);
		Tuple4f up = new Vector4f(0, 1, 0, 1); 
		
		List<String> ids = new ArrayList<String>(loader.getKinectIds());
//		Collections.sort(ids);
		ids.remove("daq-03");
		ids.add(0, "daq-03");
		for (String kid: ids) {
			Matrix4f m = loader.getKinectMatrix(kid);
			Tuple4f e = new Vector4f();
			Tuple4f c = new Vector4f();
			Tuple4f u = new Vector4f();
			
			m.transform(eye, e);
			m.transform(center, c);
			m.transform(up, u);
			//TODO: it might be the case that the up vector just needs to be rotated 
			// around Z with gamma 
			u.sub(e);
			
			Camera cam = new Camera(
					kid,
					new Vector3f(e.x, e.y, e.z),
					new Vector3f(c.x, c.y, c.z),
					new Vector3f(u.x, u.y, u.z));
					
			ret.add(cam);
		}
		
		
		Camera door = new Camera(
				"Door",
				new Vector3f(100, 1600, 0),
				new Vector3f(9340f / 2, 1600, 4785f / 2),
				new Vector3f(0, 1, 0));
		
		
		ret.add(door);
		
		return ret;
	}

	@Override
	public List<String> getSourceFiles() {
		File folder = new File(getInitParameter(SOURCE_FOLDER));
		return new ArrayList<String>(Arrays.asList(folder.list(FileFilterUtils.suffixFileFilter("txt"))));
	}

	@Override
	public List<MeasurmentsSet> getRecord(String fileName) {
		try {
			FileLoader fLoader = new FileLoader();
			fLoader.loadFromFile(getInitParameter(SOURCE_FOLDER), fileName);
			fLoader.awaitLoading();
			
			MultiValueMap<String, Skeleton3D> mmap = new MultiValueMap<String, Skeleton3D>();
			for (Skeleton3D s: fLoader.skeletons) {
				mmap.put(s.sensorId, s);
			}

			List<MeasurmentsSet> measurements = new ArrayList<MeasurmentsSet>();
			for (String sensorId: mmap.keySet()) {
				List<Skeleton3D> skeletons = new ArrayList<Skeleton3D>(mmap.getCollection(sensorId));
				Collections.sort(skeletons, Skeleton3D.COMPARATOR_BY_TIME);
				measurements.add(new SkeletonMeasurements(
						sensorId, 
						skeletons, 
						loader.getKinectMatrix(sensorId)));
			}
			
			MultiValueMap<String, ImageRgb> imagesMmap = new MultiValueMap<String, ImageRgb>();
			for (ImageRgb s: fLoader.images) {
				imagesMmap.put(s.sensor_id, s);
			}
			
			for (String sensorId: imagesMmap.keySet()) {
				List<ImageRgb> images = new ArrayList<ImageRgb>(imagesMmap.getCollection(sensorId));
				Collections.sort(images, ImageRgb.COMPARATOR_BY_TIME);
				measurements.add(new RgbMeasurements(
						sensorId, 
						images));
			}
			
			return measurements;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}

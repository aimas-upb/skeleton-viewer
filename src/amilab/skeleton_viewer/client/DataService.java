package amilab.skeleton_viewer.client;

import java.util.List;
import java.util.Map;

import javax.vecmath.Vector3f;

import amilab.skeleton_viewer.shared.Camera;
import amilab.skeleton_viewer.shared.MeasurmentsSet;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("data")
public interface DataService extends RemoteService {
	
	List<MeasurmentsSet> getRecord(String fileName);
	
	List<Camera> getCameras();
	List<String> getSourceFiles();

	Map<String, Vector3f> kinectPositions();
	
}

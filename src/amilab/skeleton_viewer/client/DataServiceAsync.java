package amilab.skeleton_viewer.client;

import java.util.List;
import java.util.Map;

import javax.vecmath.Vector3f;

import amilab.skeleton_viewer.shared.Camera;
import amilab.skeleton_viewer.shared.MeasurmentsSet;

import com.google.gwt.user.client.rpc.AsyncCallback;


public interface DataServiceAsync {

	void getCameras(AsyncCallback<List<Camera>> callback);
	void getSourceFiles(AsyncCallback<List<String>> callback);
	void getRecord(String fileName,
			AsyncCallback<List<MeasurmentsSet>> callback);
	void kinectPositions(AsyncCallback<Map<String, Vector3f>> callback);


}

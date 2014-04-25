package amilab.skeleton_viewer.client;

import gwt.g3d.client.gl2.GL2;
import gwt.g3d.client.gl2.enums.BeginMode;
import gwt.g3d.client.math.MatrixStack;
import gwt.g3d.client.mesh.StaticMesh;
import gwt.g3d.client.primitive.MeshData;
import gwt.g3d.client.primitive.PrimitiveFactory;
import gwt.g3d.client.shader.FlatShader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.vecmath.Vector3f;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class Room {

	private final List<StaticMesh> meshes = new ArrayList<StaticMesh>();
	private final Map<String, Vector3f> kinects = new HashMap<String, Vector3f>();
 	private final StaticMesh kinect;
	
	public Room(GL2 gl, FlatShader shader) {
		loadKinectPositions();
		kinect = makeKinectBox(gl, shader);
//		kinect = makeFloor(gl, shader);
		meshes.add(makeFloor(gl, shader));
		meshes.add(makeLongWall(0.0f, gl, shader));
		meshes.add(makeLongWall(4785, gl, shader));
		
	}
	
	private void loadKinectPositions() {
		Services.dataService.kinectPositions(new AsyncCallback<Map<String,Vector3f>>() {
			@Override
			public void onSuccess(Map<String, Vector3f> result) {
				kinects.clear();
				kinects.putAll(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed loading kinect positions !");
			}
		});
		
	}
	
	private StaticMesh makeKinectBox(GL2 gl, FlatShader shader) {
		MeshData box = PrimitiveFactory.makeBox();
		StaticMesh mesh = new StaticMesh(gl, box);
		mesh.setBeginMode(BeginMode.TRIANGLES);
		mesh.setPositionIndex(shader.getAttributePosition());
		return mesh;
	}

	private StaticMesh makeFloor(GL2 gl, FlatShader shader) {
		MeshData box = PrimitiveFactory.makePlane();
		float[] planeVerts = { 
				    0, 0, 0, 
					0, 0, 4785, 
				 9340, 0, 4785, 
			     9340, 0, 0 };		
		
		box.setVertices(planeVerts);
		StaticMesh mesh = new StaticMesh(gl, box);
		mesh.setPositionIndex(shader.getAttributePosition());
		return mesh;
	}
	
	private StaticMesh makeLongWall(float z, GL2 gl, FlatShader shader) {
		MeshData box = PrimitiveFactory.makePlane();
		float[] planeVerts = { 
				   0,    0, z, 
				   0, 1000, z,
				9340, 1000, z,
				9340,    0, z, 
			      };		
		
		box.setVertices(planeVerts);
		StaticMesh mesh = new StaticMesh(gl, box);
		mesh.setPositionIndex(shader.getAttributePosition());
		return mesh;
	}
	
	public void draw(FlatShader shader) {
		shader.setColor(0.5f, 0.5f, 0.5f, 1.0f);
		meshes.get(0).draw();
		
		shader.setColor(0.35f, 0.35f, 0.35f, 1.0f);		
		meshes.get(1).draw();
		shader.setColor(0.65f, 0.65f, 0.65f, 1.0f);
		meshes.get(2).draw();
		
		for (Entry<String, Vector3f> e: kinects.entrySet()) {
			MatrixStack.MODELVIEW.push();
			try {
				
				shader.setColor(Colors.getColor(e.getKey()));
				MatrixStack.MODELVIEW.translate(e.getValue());
				
//				shader.setColor(1f, 0.65f, 0.65f, 1.0f);
//				MatrixStack.MODELVIEW.translate(1000, 30, 1000);
				MatrixStack.MODELVIEW.scale(100, 100, 100);
				
				shader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
				kinect.draw();
			
			} finally {
				MatrixStack.MODELVIEW.pop();
			}
		}
		
//		MatrixStack.MODELVIEW.push();		
//		try {
//			shader.setColor(1f, 0.65f, 0.65f, 1.0f);
//			
//			MatrixStack.MODELVIEW.translate(1000, 30, 1000);
//			MatrixStack.MODELVIEW.scale(100, 100, 100);
//			shader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
//			kinect.draw();
//	
//			MatrixStack.MODELVIEW.translate(1000, 30, 1000);
//			shader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
//			kinect.draw();
//			
//			MatrixStack.MODELVIEW.translate(1000, 30, 1000);
//			shader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
//			kinect.draw();
//			
//			MatrixStack.MODELVIEW.translate(1000, 30, 1000);
//			shader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
//			kinect.draw();
////			meshes.get(0).draw();			
////			shader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
////			
////			for (Entry<String, Vector3f> e: kinects.entrySet()) {
////				MatrixStack.MODELVIEW.push();
////				try {
////					shader.setColor(Colors.getColor(e.getKey()));
//////					MatrixStack.MODELVIEW.translate(e.getValue());
////					box.draw();
////				} finally {
////					MatrixStack.MODELVIEW.pop();
////				}
////			}
//		} finally {
//			MatrixStack.MODELVIEW.pop();
//		}
	}
	
	public void dispose() {
		for (StaticMesh mesh: meshes) {
			mesh.dispose();
		}
	}
	
	
}

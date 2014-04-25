package amilab.skeleton_viewer.shared;

import java.io.Serializable;

import javax.vecmath.Tuple3f;

public class Camera implements Serializable {
	
	public String description;
	public Tuple3f eye;
	public Tuple3f center;
	public Tuple3f up;
	
	public Camera(String description, Tuple3f eye, Tuple3f center, Tuple3f up) {
		this.description = description;
		this.eye = eye;
		this.center = center;
		this.up = up;
	}

	public Camera() {
	}
	
}

//package amilab.skeleton_viewer.client;
//
//import gwt.g3d.client.gl2.GL2;
//import gwt.g3d.client.gl2.GL2ContextHelper;
//import gwt.g3d.client.gl2.enums.ClearBufferMask;
//import gwt.g3d.client.gl2.enums.DepthFunction;
//import gwt.g3d.client.gl2.enums.EnableCap;
//import gwt.g3d.client.math.MatrixStack;
//import gwt.g3d.client.mesh.StaticMesh;
//import gwt.g3d.client.shader.FlatShader;
//import gwt.g3d.client.shader.ShaderException;
//
//import java.util.List;
//
//import javax.vecmath.Tuple3f;
//import javax.vecmath.Vector3f;
//
//import amilab.skeleton_viewer.shared.Skeleton3D;
//
//import com.google.gwt.canvas.client.Canvas;
//import com.google.gwt.core.client.EntryPoint;
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.core.client.Scheduler;
//import com.google.gwt.user.client.Timer;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.RootPanel;
//
//public class SceneCanvas implements EntryPoint {
//
//	private GL2 gl;
//	private FlatShader shader;
//	private Canvas surface;
//	private FrameSet frameSet;
//	
//	private final DataServiceAsync dataService = GWT.create(DataService.class);
//	private Timer timer;
//	
//	public SceneCanvas() {
//	}
//	
//	public void onModuleLoad() {
//		surface = Canvas.createIfSupported();
//		RootPanel.get("main_canvas").add(surface);
//		surface.setWidth("100%");
//		surface.setHeight("100%");
//		gl = GL2ContextHelper.getGL2(surface);
//		if (gl == null) {
//			Window.alert("No WebGL context found. Exiting.");
//			return;
//		}
//		surface.setCoordinateSpaceWidth(640);
//		surface.setCoordinateSpaceHeight(480);
//		// Sets up the GL context.
//		gl.clearColor(0.0f, 0f, 0.0f, 1f);
//		gl.clearDepth(1);
//		gl.clear(ClearBufferMask.COLOR_BUFFER_BIT, ClearBufferMask.DEPTH_BUFFER_BIT);
//		
//		System.out.println(surface.getCoordinateSpaceWidth() 
//				+ " "
//				+ surface.getCoordinateSpaceHeight()
//		);
//
//		gl.viewport(0, 0, surface.getCoordinateSpaceWidth(), surface.getCoordinateSpaceHeight());
//
//		gl.enable(EnableCap.DEPTH_TEST);
//		gl.depthFunc(DepthFunction.LEQUAL);
//
//		// Creates a lambertian shader.
//		//		  LambertianShader shader = new LambertianShader();
//		shader = new FlatShader();
//		try {
//			shader.init(gl);
//		} catch (ShaderException e) {
//			Window.alert("Error loading the shader.");
//			return;
//		}
//
//		// Binds the shader.
//		shader.bind();
//		shader.setColor(0.5f, 0.6f, 0.0f, 0.0f);
//		onAttach();
//	}
//
////	@Override
//	protected void onAttach() {
////		super.onAttach();
//		dataService.getRecord(new AsyncCallback<List<Skeleton3D>>() {
//			@Override
//			public void onSuccess(List<Skeleton3D> result) {
//				frameSet = new FrameSet(getGl(), shader, result);
//				Scheduler.get().scheduleIncremental(new FrameSetLoader(frameSet));
//				timer = new Timer() {
//					int frame = 0;
//					@Override
//					public void run() {
//						draw(frameSet, frame);
//						frame += 1;
//						if (frame >= frameSet.getNbLoadedFrames()) {
//							if (frameSet.isLoaded()) {
//								frame = frame % frameSet.getNbLoadedFrames();
//							} else {
//								frame = frameSet.getNbLoadedFrames() - 1;
//							}
//						}
//					}
//				};
//				timer.scheduleRepeating(100);
//			}
//			@Override
//			public void onFailure(Throwable caught) {
//				Window.alert("Could not load data from server: " + caught.getMessage());
//			}
//		});
//	}
//	
//	float rotate = 0.0f;
//	public void draw(FrameSet frameSet, int frame) {
////		System.out.println("Tick: " + rotation);
////		 Sets up the model view matrix.
//		StaticMesh mesh = frameSet.getMesh(frame);
//		
//		mesh.setPositionIndex(shader.getAttributePosition());
////		mesh.setNormalIndex(shader.getAttributeNormal());
//		
//		MatrixStack.MODELVIEW.push();
//		MatrixStack.MODELVIEW.translate(0, 0, 50);
//		MatrixStack.MODELVIEW.rotateY(rotate);
//		rotate += 0.3;
//		shader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
//		MatrixStack.MODELVIEW.pop();
//
//		// Sets up a basic camera for projection.
//		MatrixStack.PROJECTION.pushIdentity();
//		Tuple3f eye = new Vector3f(0, 0, 0);
//		Tuple3f center = new Vector3f(0, 0, 1000);
//		Tuple3f up = new Vector3f(0, 1, 0);
//		
//		MatrixStack.PROJECTION.perspective(90, 1, .1f, 10000);
////		MatrixStack.PROJECTION.lookAt(eye, center, up);
//		
//		shader.setProjectionMatrix(MatrixStack.PROJECTION.get());
//		MatrixStack.PROJECTION.pop();
//
//		// Draws the mesh.
//		mesh.draw();
//	}
//	
//	public GL2 getGl() {
//		return gl;
//	}
//
////	@Override
////	protected void onDetach() {
////		System.out.println("Canvas on detach");
////		timer.cancel();
////		frameSet.cleanup();
////		shader.dispose();
////		super.onDetach();
////	}
////	
////	
//
//}

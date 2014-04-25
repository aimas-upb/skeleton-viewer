package amilab.skeleton_viewer.client;

import gwt.g3d.client.gl2.GL2;
import gwt.g3d.client.gl2.GL2ContextHelper;
import gwt.g3d.client.gl2.enums.ClearBufferMask;
import gwt.g3d.client.gl2.enums.DepthFunction;
import gwt.g3d.client.gl2.enums.EnableCap;
import gwt.g3d.client.math.MatrixStack;
import gwt.g3d.client.mesh.StaticMesh;
import gwt.g3d.client.shader.FlatShader;
import gwt.g3d.client.shader.ShaderException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.vecmath.Tuple3f;

import amilab.skeleton_viewer.client.TimestampsList.MatchPolicy;
import amilab.skeleton_viewer.shared.Camera;
import amilab.skeleton_viewer.shared.MeasurmentsSet;
import amilab.skeleton_viewer.shared.RgbMeasurements;
import amilab.skeleton_viewer.shared.SkeletonMeasurements;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.CssColor;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.widgetideas.client.SliderBar;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
@SuppressWarnings("deprecation")
public class SkeletonViewer implements EntryPoint {


	private FlatShader shader;
	private Timer timer;
	private GL2 gl;
	private Camera camera;
	private Room room;

	private List<FrameSet> frameSets = new ArrayList<FrameSet>();
	private RgbSet rgbSet;
	private Image rgbImage = new Image();
	
	private final DataServiceAsync dataService = Services.dataService;
	protected List<Camera> cameras;
	private SliderBar slider;
	private SliderBar rgbSlider;
	protected FrameSetLoader frameSetLoader;
	private DateTimeFormat dtf = DateTimeFormat.getFormat("HH:mm:ss.SSS");
	private ChangeListener rgbSliderChangeListener;
	private MainToolbar mainToolbar;
	private SnapshotBar snapshotBar;
	private long[] skTimestamps;
	Canvas cmCanvas;
	
	public void onModuleLoad() {
		// Adds a canvas to the document.
		Canvas surface = Canvas.createIfSupported();
		RootPanel.get("main_canvas").add(surface);
		slider = new SliderBar(0.0, 100.0);
		slider.setStepSize(1);
		slider.setCurrentValue(0);
		slider.setNumTicks(10);
		slider.setNumLabels(5);
		slider.setSize("100%", "100%");
		
		KeyPressHandler handler = new KeyPressHandler() {
			@Override
			public void onKeyPress(KeyPressEvent event) {
				switch (event.getCharCode()) {
				case 'n':
					snapshotBar.forward();
					event.preventDefault();
					break;
				case 's':
					snapshotBar.snapAndForward();
					event.preventDefault();
					break;
				}
			}
		};
		
		RootPanel.get().addDomHandler(handler, KeyPressEvent.getType());
		
		rgbSlider = new SliderBar(0.0, 100.0);
		rgbSlider.setStepSize(1);
		rgbSlider.setCurrentValue(0);
		rgbSlider.setNumTicks(10);
		rgbSlider.setNumLabels(5);
		rgbSlider.setSize("100%", "100%");

		
		RootPanel.get("slider").add(slider);
		RootPanel.get("rgb_slider").add(rgbSlider);
		
		cmCanvas = Canvas.createIfSupported();
		RootPanel.get("color-map").add(cmCanvas);
		cmCanvas.setWidth("100%");
		cmCanvas.setHeight("18px");
		cmCanvas.setCoordinateSpaceHeight(18);
		
		Context2d c2d = cmCanvas.getContext2d();
		c2d.setFillStyle(CssColor.make(255, 0, 0));
		c2d.fillRect(0, 0, cmCanvas.getCoordinateSpaceWidth() / 2, cmCanvas.getCoordinateSpaceHeight());
		c2d.setFillStyle(CssColor.make(0, 0, 255));
		c2d.fillRect(cmCanvas.getCoordinateSpaceWidth() / 2, 0, cmCanvas.getCoordinateSpaceWidth() / 2, cmCanvas.getCoordinateSpaceHeight());
		mainToolbar = new MainToolbar();
		RootPanel.get("mainToolbar").add(mainToolbar);
		
		snapshotBar = new SnapshotBar(this);
		RootPanel.get("snapshotBar").add(snapshotBar);
		
		rgbImage.addStyleName("rgbImage");
		RootPanel.get("rgb_canvas").add(rgbImage);
		
		surface.setCoordinateSpaceWidth(1024);
		surface.setCoordinateSpaceHeight(768);
		
		gl = GL2ContextHelper.getGL2(surface);
		if (gl == null) {
			Window.alert("No WebGL context found. Exiting.");
			return;
		}

		// Sets up the GL context.
		gl.clearColor(0.0f, 0f, 0f, 1f);
		gl.clearDepth(1);
		gl.viewport(0, 0, surface.getCoordinateSpaceWidth(), surface.getCoordinateSpaceHeight());

		gl.enable(EnableCap.DEPTH_TEST);
		gl.depthFunc(DepthFunction.LEQUAL);

		shader = new FlatShader();
		try {
			shader.init(gl);
		} catch (ShaderException e) {
			Window.alert("Error loading the shader.");
			return;
		}

		// Binds the shader.
		shader.bind();
		
		gl.clear(ClearBufferMask.COLOR_BUFFER_BIT, ClearBufferMask.DEPTH_BUFFER_BIT);
		

		room = new Room(gl, shader);
		loadCameras();
		
		surface.addAttachHandler(new AttachEvent.Handler() {
			@Override
			public void onAttachOrDetach(AttachEvent event) {
				if (!event.isAttached()) {
					cleanup();
				}
			}
		});
	}
	
	private void loadCameras() {
		dataService.getCameras(new AsyncCallback<List<Camera>>() {
			@Override
			public void onSuccess(List<Camera> result) {
				cameras = result;
				camera = cameras.get(0);
				mainToolbar.camerasBox.clear();
				mainToolbar.camerasBox.setMultipleSelect(false);
				for (Camera c: cameras) {
					mainToolbar.camerasBox.addItem(c.description);
				}
				mainToolbar.camerasBox.addChangeHandler(new ChangeHandler() {
					@Override
					public void onChange(ChangeEvent event) {
						System.out.println("camera changed");
						camera = cameras.get(mainToolbar.camerasBox.getSelectedIndex());
					}
				});
				RootPanel.get("source_list").add(new SourceList(SkeletonViewer.this));
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Failed loading cameras !");
			}
		});
	}
	
	private void disposeData() {
		if (frameSetLoader != null) {			
			frameSetLoader.cancelled();
		}
		
		for (FrameSet fs: frameSets) {
			fs.dispose();
		}
		frameSets.clear();
		rgbSet = null;
		
		if (rgbSliderChangeListener != null) {
			rgbSlider.removeChangeListener(rgbSliderChangeListener);
		}
		
		if (timer != null) {
			timer.cancel();
		}
		
	}
	
	public void loadData(String fileName) {
		if (timer != null) {
			timer.cancel();
		}
		mainToolbar.loadingLabel.setText("Loading...");
		
		dataService.getRecord(fileName, new AsyncCallback<List<MeasurmentsSet>>() {
			@Override
			public void onSuccess(List<MeasurmentsSet> result) {
				disposeData();
				long max = Long.MIN_VALUE;
				long min = Long.MAX_VALUE;
				int totalTimestamps = 0;
				for (MeasurmentsSet m: result) {
					if (m instanceof SkeletonMeasurements) {
						SkeletonMeasurements sm = (SkeletonMeasurements) m;
						FrameSet fs = new FrameSet(
								gl, shader, Colors.getColor(sm.getSensorId()), sm);
						frameSets.add(fs);
						if (fs.getTimestamps().getMaxTime() > max) max = fs.getTimestamps().getMaxTime();
						if (fs.getTimestamps().getMinTime() < min) min = fs.getTimestamps().getMinTime();
						totalTimestamps += fs.getTimestamps().getTimestamps().length;
					} else if (m instanceof RgbMeasurements) {
						if (rgbSet == null) {
							//TODO: handle more rgb set
							RgbMeasurements rm = (RgbMeasurements) m;
							rgbSet = new RgbSet(rm);
							totalTimestamps += rgbSet.getTimestamps().getTimestamps().length;
						}
					}
				}
				
				if (rgbSet == null) {
					rgbSet = new RgbSet(new RgbMeasurements());
				}
				// need to copy all the timestamps 
				long[] allTimestamps = new long[totalTimestamps];
				int last = 0;
				for (FrameSet fs: frameSets) {
					System.arraycopy(fs.getTimestamps().getTimestamps(), 0, allTimestamps, last, fs.getTimestamps().getTimestamps().length);
					last += fs.getTimestamps().getTimestamps().length;
				}
				
				if (rgbSet != null) {
					System.arraycopy(rgbSet.getTimestamps().getTimestamps(), 0, allTimestamps, last, rgbSet.getTimestamps().getTimestamps().length);
					last += rgbSet.getTimestamps().getTimestamps().length;
				}
				
				Arrays.sort(allTimestamps);
				//removing duplicates
				long[] tempTimestamps = new long[totalTimestamps];
				tempTimestamps[0] = allTimestamps[0];
				last = 0;
				for (int i = 1; i < allTimestamps.length; i++) {
					//don't copy duplicates
					if (tempTimestamps[last] != allTimestamps[i]) {
						tempTimestamps[++last] = allTimestamps[i];
					}
				}
				skTimestamps = new long[last + 1];
				System.arraycopy(tempTimestamps, 0, skTimestamps, 0, skTimestamps.length);				
				
				slider.setMinValue(0);
				slider.setMaxValue(skTimestamps.length - 1);
				slider.setCurrentValue(0, false);
				
				if (rgbSet != null) {
					rgbSlider.setMinValue(0);
					rgbSlider.setMaxValue(rgbSet.getTimestamps().getTimestamps().length - 1);
					rgbSlider.setCurrentValue(0, false);
				}
				
				frameSetLoader = new FrameSetLoader(frameSets);
				Scheduler.get().scheduleIncremental(frameSetLoader);
				
				final Label rgbTime = new Label();
				mainToolbar.skeletonTime.setText("");
				
				RootPanel.get("rgb_time").clear();
				RootPanel.get("rgb_time").add(rgbTime);
				
				rgbSliderChangeListener = new ChangeListener() {
					@Override
					public void onChange(Widget sender) {
						int frame = (int) rgbSlider.getCurrentValue();
						long time = rgbSet.getTimestamps().getTimestamp(frame);
						TimestampsList tl = new TimestampsList(skTimestamps);
						int index = tl.getIndexFor(time, MatchPolicy.CLOSEST_AFTER);
						if (index == -1) {
							if (time < tl.getMinTime()) {
								index = (int) slider.getMinValue();
							} else {
								index = (int) slider.getMaxValue();
							}
						}
						slider.setCurrentValue(index, false);
					}
				};
				rgbSlider.addChangeListener(rgbSliderChangeListener);
				
				
				timer = new Timer() {
					@Override
					public void run() {
						int frame = (int)slider.getCurrentValue();
						long time = skTimestamps[frame];
//						mainToolbar.skeletonTime.setText(dtf.format(new Date(time)));
						mainToolbar.skeletonTime.setText(String.valueOf(frame));
						
						if (rgbSet != null) {
							int rgbIndex = rgbSet.getTimestamps().getIndexFor(time);
							if (rgbIndex == -1) {	
								//TODO: show "not available"
								rgbTime.setText("");
							}						
							else {
								long rgbTimestamp = rgbSet.getTimestamps().getTimestamp(rgbIndex);
								rgbSlider.setCurrentValue(rgbIndex, false);
								rgbTime.setText(dtf.format(new Date(rgbTimestamp)));
							}
						}
						
						boolean isLoading = false;
						for (FrameSet fs: frameSets) {
							isLoading = isLoading || fs.isLoadingAt(time);
						}
						
						if (isLoading) {
							mainToolbar.loadingLabel.setText("Loading...");
						} else {
							mainToolbar.loadingLabel.setText("");
							draw(shader, time, camera);
							updateRgb(time, rgbSet);
						}

						if (!mainToolbar.playButton.isDown() && !isLoading) {
							frame = frame + 1;
							if (frame > slider.getMaxValue()) {
								slider.setCurrentValue(slider.getMinValue());
							} else {
								slider.setCurrentValue(frame, false);
							}
						}
					}
				};
				timer.scheduleRepeating(50);
			}
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Could not load data from server: " + caught.getMessage());
			}
		});
	}
	
	public boolean isInteractive() {
		return mainToolbar.playButton.isDown();
	}
	
	public void forward() {
		int frame = (int)slider.getCurrentValue();
		frame = frame + 1;
		if (frame > slider.getMaxValue()) {
			slider.setCurrentValue(slider.getMinValue());
		} else {
			slider.setCurrentValue(frame, false);
		}
	}
	
	public long getPlaybackTime() {
		int frame = (int)slider.getCurrentValue();
		return skTimestamps[frame];
	}
	
	public List<FrameSet> getPlaybackFrameSets() {
		return frameSets;
	}
	
	private String lastRgbUrl = null;
	private void updateRgb(long time, RgbSet rgbSet) {
		if (rgbSet != null) {
			String imageUrl = rgbSet.getImageUrlAt(time);
			if (imageUrl == null) {
				//TODO: load "not available" image
			} else if (!imageUrl.equals(lastRgbUrl)) {
				rgbImage.setUrl(imageUrl);
				lastRgbUrl = imageUrl;
			}
		}
	}

	private void cleanup() {
		System.out.println("SkeletonViewer.cleanup()");
		disposeData();		
		room.dispose();
		shader.dispose();
	}
	
	
	private void draw(
			FlatShader shader, 
			long time,
			Camera camera) {
		
		gl.clear(ClearBufferMask.COLOR_BUFFER_BIT, ClearBufferMask.DEPTH_BUFFER_BIT);
		
		// Sets up a basic camera for projection.
		Tuple3f eye = camera.eye;
		Tuple3f center = camera.center;
		Tuple3f up = camera.up;

		MatrixStack.PROJECTION.pushIdentity();
		
		MatrixStack.PROJECTION.perspective(45, 58.0f/45, 201.1f, 20000);		
		MatrixStack.PROJECTION.lookAt(eye, center, up);
		shader.setProjectionMatrix(MatrixStack.PROJECTION.get());
		MatrixStack.PROJECTION.pop();

		//draw the room
		shader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
		room.draw(shader);
		
		for (FrameSet fs: frameSets) {
			// Draws the meshes.		
			StaticMesh mesh = fs.getMeshAt(time);
			if (mesh != null) {
				// Sets up the model view matrix.
				MatrixStack.MODELVIEW.push();
				MatrixStack.MODELVIEW.mul(fs.getSensorTransform());
				shader.setModelViewMatrix(MatrixStack.MODELVIEW.get());
				MatrixStack.MODELVIEW.pop();
				shader.setColor(fs.getColor());
				mesh.draw();
			}
		}
	}
}


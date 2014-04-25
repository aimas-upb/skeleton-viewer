package amilab.skeleton_viewer.client;

import java.util.HashSet;
import java.util.Set;

import amilab.skeleton_viewer.shared.Skeleton3D;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.Widget;

public class SnapshotBar extends Composite {

	private static SnapshotBarUiBinder uiBinder = GWT
			.create(SnapshotBarUiBinder.class);

	interface SnapshotBarUiBinder extends UiBinder<Widget, SnapshotBar> {
	}

	private Set<String> selections = new HashSet<String>();
	
	@UiField(provided=true)
	PushButton snapButton = new PushButton(new Image("res/snapshot-forward-32px.jpg"));
	@UiField(provided=true)
	PushButton fwdButton = new PushButton(new Image("res/forward-32px.png"));
	@UiField(provided=true)
	PushButton clearButton = new PushButton(new Image("res/delete-32px.png"));
	@UiField(provided=true)
	PushButton downloadButton = new PushButton(new Image("res/download-32px.png"));
	@UiField
	Label selected;
	
	private final SkeletonViewer viewer;
	
	public SnapshotBar(SkeletonViewer viewer) {
		this.viewer = viewer;
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	@UiHandler("snapButton")
	void onClickSnap(ClickEvent e) {
		snapAndForward();
	}

	public void snapAndForward() {
		long time = viewer.getPlaybackTime();
		StringBuilder sb = new StringBuilder();
		for (FrameSet fs: viewer.getPlaybackFrameSets()) {
			Skeleton3D s = fs.getSkeletonAt(time);
			sb.append(s == null ? "" : String.valueOf(s.lineNumber)).append(" ");
		}
		selections.add(sb.toString());
		forward();
		update();
	}
	
	@UiHandler("fwdButton")
	void onClickFwd(ClickEvent e) {
		forward();
	}

	public void forward() {
		if (viewer.isInteractive()) {
			viewer.forward();
		}
	}
	
	@UiHandler("clearButton")
	void onClickClear(ClickEvent e) {
		selections.clear();
		update();
	}
	
	@UiHandler("downloadButton")
	void onClickDonwload(ClickEvent e) {
		StringBuilder sb = new StringBuilder("data:text/html,");
		for (String s: selections) {
			sb.append(s).append("<br/>");
		}
//		Window.open(b64encode(sb.toString()), "Master file", "");
		Window.open(sb.toString(), "Master file", "");
	}
	
	private void update() {
		selected.setText("" + selections.size());
	}
	
	private static native String b64encode(String a) /*-{
	  return window.btoa(a);
	}-*/;
	
	
}

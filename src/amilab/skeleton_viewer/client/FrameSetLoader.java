package amilab.skeleton_viewer.client;

import java.util.List;

import com.google.gwt.core.client.Scheduler.RepeatingCommand;

public class FrameSetLoader implements RepeatingCommand {
	private final List<FrameSet> frameSets;
	private boolean cancelled = false;
	
	public FrameSetLoader(List<FrameSet> frameSets) {
		this.frameSets = frameSets;
	}

	@Override
	public boolean execute() {
		if (cancelled)
			return true;
		
		boolean hasMoreWork = false;
		for (FrameSet fs: frameSets) {
			if (!fs.isLoaded()) {
				fs.loadNextMesh();
				hasMoreWork = true;
			}
		}
		return hasMoreWork;
	}
	
	public void cancelled() {
		cancelled = true;
	}
}

package amilab.skeleton_viewer.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;

public class MainToolbar extends Composite {

	private static MainToolbarUiBinder uiBinder = GWT
			.create(MainToolbarUiBinder.class);

	interface MainToolbarUiBinder extends UiBinder<Widget, MainToolbar> {
	}

	public MainToolbar() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField(provided=true)
	ToggleButton playButton = new ToggleButton("Play", "Pause");
	@UiField
	ListBox camerasBox;
	@UiField
	Label loadingLabel;
	@UiField
	Label skeletonTime;
	
	@UiHandler("playButton")
	void onClick(ClickEvent e) {
	}


}

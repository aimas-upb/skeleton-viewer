package amilab.skeleton_viewer.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SourceList extends Composite {
	
	private VerticalPanel panel = new VerticalPanel();
	private final DataServiceAsync dataService = GWT.create(DataService.class);
	private final List<HandlerRegistration> handlerRegistration = new ArrayList<HandlerRegistration>();
	private static final String GROUP_NAME = "source_list_radio_group"; 
	private final SkeletonViewer viewer;
	
	public SourceList(SkeletonViewer viewer) {
		this.viewer = viewer;
		initWidget(panel);
	}

	@Override
	protected void onAttach() {
		super.onAttach();
		
		dataService.getSourceFiles(new AsyncCallback<List<String>>() {
			@Override
			public void onSuccess(List<String> result) {
				ValueChangeHandler<Boolean> handler = new ValueChangeHandler<Boolean>() {
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						if (event.getValue()) {
							loadData(((RadioButton)event.getSource()).getText());
						}
					}
				};
				
				for (String s: result) {
					RadioButton rb = new RadioButton(GROUP_NAME, s);
					panel.add(rb);
					handlerRegistration.add(rb.addValueChangeHandler(handler));
				}
			}
			
			@Override
			public void onFailure(Throwable caught) {
			}
		});
	}
	

	private void loadData(String fileName) {
		viewer.loadData(fileName);
	}

	@Override
	protected void onDetach() {
		super.onDetach();
		panel.clear();
		for (HandlerRegistration reg: handlerRegistration) {
			reg.removeHandler();
		}
		
	}
	
	
	
	
	
	
}

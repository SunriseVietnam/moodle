package com.moodle.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class WaterTalksFileUploadingComponent extends Composite{

    final FormPanel form = new FormPanel();
    VerticalPanel vPanel = new VerticalPanel();
    FileUpload fileUpload = new FileUpload();
    Label maxUpload =new Label();

    public WaterTalksFileUploadingComponent(){

        form.setMethod(FormPanel.METHOD_POST);
        form.setEncoding(FormPanel.ENCODING_MULTIPART); //  multipart MIME encoding
        form.setAction("/FileUploadByWaterTalks"); // The servlet FileUploadGreeting
        form.setWidget(vPanel);
        fileUpload.setName("uploader"); // Very important
        vPanel.add(fileUpload);
        maxUpload.setText("Maximum upload file size: 1MB");
        vPanel.add(maxUpload);
        vPanel.add(new Button("Submit", new ClickHandler() {
            public void onClick(ClickEvent event) {
                form.submit();
            }
        }));
        initWidget(form);

    }
}
package com.moodle.client;
//
//import com.google.gwt.core.client.EntryPoint;
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.dom.client.Document;
//import com.google.gwt.event.dom.client.ChangeEvent;
//import com.google.gwt.event.dom.client.ClickEvent;
//import com.google.gwt.resources.client.ClientBundle;
//import com.google.gwt.resources.client.CssResource;
//import com.google.gwt.uibinder.client.UiBinder;
//import com.google.gwt.uibinder.client.UiField;
//import com.google.gwt.uibinder.client.UiHandler;
//import com.google.gwt.user.client.Window;
//import com.google.gwt.user.client.ui.Button;
//import com.google.gwt.user.client.ui.FlowPanel;
//import com.google.gwt.user.client.ui.RootLayoutPanel;
//import com.moodle.parser.XMLConvertor;
//import org.vectomatic.file.*;
//import org.vectomatic.file.events.ErrorEvent;
//import org.vectomatic.file.events.ErrorHandler;
//import org.vectomatic.file.events.LoadEndEvent;
//import org.vectomatic.file.events.LoadEndHandler;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Entry point classes define <code>onModuleLoad()</code>.
// */
//
//
//public class app implements EntryPoint {
//  /**
//   * This is the entry point method.
//   */
//
//  public static final StringBuilder moodleText = new StringBuilder();
//  @UiField
//  FileUploadExt fileUpload;
//  @UiField
//  Button convertBtn;
//  @UiField(provided=true)
//  static AppBundle bundle = GWT.create(AppBundle.class);
//  protected boolean useTypedArrays;
//  protected FileReader reader;
//  protected List<File> readQueue;
//  interface AppBinder extends UiBinder<FlowPanel, app> {
//  }
//  private static AppBinder binder = GWT.create(AppBinder.class);
//
//  interface AppCss extends CssResource {
//    public String imagePanel();
//    public String fileUpload();
//    @ClassName("txt")
//    public String text();
//
//    String convert();
//
//    @ClassName("input-file")
//    String inputFile();
//  }
//  interface AppBundle extends ClientBundle {
//    @Source("app.css")
//    public AppCss css();
//  }
//
//  @Override
//  public void onModuleLoad() {
//
//    // Use typed arrays by default
//    useTypedArrays = !"false".equals(Window.Location.getParameter("typedArrays"));
//
//    // Create UI main elements
//    bundle.css().ensureInjected();
//    FlowPanel flowPanel = binder.createAndBindUi(this);
//    Document document = Document.get();
//    RootLayoutPanel.get().add(flowPanel);
//
//    reader = new FileReader();
//    reader.addLoadEndHandler(new LoadEndHandler() {
//      /**
//       * This handler is invoked when FileReader.readAsText(),
//       * FileReader.readAsBinaryString() or FileReader.readAsArrayBuffer()
//       * successfully completes
//       */
//      @Override
//      public void onLoadEnd(LoadEndEvent event) {
//        if (reader.getError() == null) {
//          if (readQueue.size() > 0) {
//            File file = readQueue.get(0);
//            try {
//              //imagePanel.add(createThumbnail(file));
//              GWT.log("onLoadEnd");
//              //Вывод текста файла
//              moodleText.append(reader.getStringResult());
//              //GWT.log(reader.getStringResult());
//            } finally {
//              XMLConvertor.collectXMLData(String.valueOf(moodleText));
//           //   GWT.log(moodleText.toString());
//              readQueue.remove(0);
//              readNextFile();
//            }
//          }
//        }
//      }
//    });
//    reader.addErrorHandler(new ErrorHandler() {
//      /**
//       * This handler is invoked when FileReader.readAsText(),
//       * FileReader.readAsBinaryString() or FileReader.readAsArrayBuffer()
//       * fails
//       */
//      @Override
//      public void onError(ErrorEvent event) {
//        if (readQueue.size() > 0) {
//          File file = readQueue.get(0);
//          handleError(file);
//          readQueue.remove(0);
//          readNextFile();
//        }
//      }
//    });
//    readQueue = new ArrayList<File>();
//  }
//  private void handleError(File file) {
//    FileError error = reader.getError();
//    String errorDesc = "";
//    if (error != null) {
//      ErrorCode errorCode = error.getCode();
//      if (errorCode != null) {
//        errorDesc = ": " + errorCode.name();
//      }
//    }
//    Window.alert("File loading error for file: " + file.getName() + "\n" + errorDesc);
//  }
//  /**
//   * Adds a collection of file the queue and begin processing them
//   * @param files
//   * The file to process
//   */
//  private void processFiles(FileList files) {
//    GWT.log("length=" + files.getLength());
//    for (File file : files) {
//      readQueue.add(file);
//    }
//    // Start processing the queue
//    readNextFile();
//  }/**
//   * Processes the next file in the queue. Depending on the MIME type of the
//   * file, a different way of loading the image is used to demonstrate all
//   * parts of the API
//   */
//  private void readNextFile() {
//    if (readQueue.size() > 0) {
//      File file = readQueue.get(0);
//      String type = file.getType();
//      try {
//        if ("image/svg+xml".equals(type)) {
//          reader.readAsText(file);
//        } else if (type.startsWith("image/png")) {
//          // Do not use the FileReader for PNG.
//          // Take advantage of the fact the browser can
//          // provide a directly usable blob:// URL
//          GWT.log("readNextFile");
//          //imagePanel.add(createThumbnail(file));
//          readQueue.remove(0);
//          readNextFile();
//        } else if (type.startsWith("image/")) {
//          // For other image types (GIF, JPEG), load them
//          // as typed arrays
//          if (useTypedArrays) {
//            reader.readAsArrayBuffer(file);
//          } else {
//            reader.readAsBinaryString(file);
//          }
//        } else if (type.startsWith("text/")) {
//          reader.readAsText(file);
//        }
//      } catch(Throwable t) {
//        // Necessary for FF (see bug https://bugzilla.mozilla.org/show_bug.cgi?id=701154)
//        // Standard-complying browsers will not go in this branch
//        handleError(file);
//        readQueue.remove(0);
//        readNextFile();
//      }
//    }
//  }
//
//  @UiHandler("convertBtn")
//  public void convert(ClickEvent event) {
//    //customUpload.click();
//    GWT.log("convert");
//  }
//  @UiHandler("fileUpload")
//  public void uploadFile(ChangeEvent event) {
//    GWT.log("uploadFile");
//    processFiles(fileUpload.getFiles());
//  }
//
//
//}

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

public class app implements EntryPoint {
  WaterTalksFileUploadingComponent fu = new WaterTalksFileUploadingComponent();
  @Override
  public void onModuleLoad() {
    RootPanel.get().add(fu);


    //Downloading
    final String url = "/FileUploadByWaterTalks";
    String name = "output.txt";

    Anchor link1 = new Anchor(name);
    RootPanel.get().add(link1);
    link1.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        Window.open(url, "_blank", "");
      }
    });

    Anchor link2 = new Anchor(name);
    RootPanel.get().add(link2);
    link2.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        Frame f = new Frame(url);
        f.setSize("600px", "400px");
        f.getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
        RootPanel.get().add(f);
      }
    });
  }
}
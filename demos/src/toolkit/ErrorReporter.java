/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.6.
 **
 ** Copyright (c) 2000-2023 by yWorks GmbH, Vor dem Kreuzberg 28,
 ** 72070 Tuebingen, Germany. All rights reserved.
 **
 ** yFiles demo files exhibit yFiles for JavaFX functionalities. Any redistribution
 ** of demo files in source code or binary form, with or without
 ** modification, is not permitted.
 **
 ** Owners of a valid software license for a yFiles for JavaFX version that this
 ** demo is shipped with are allowed to use the demo source code as basis
 ** for their own yFiles for JavaFX powered applications. Use of such programs is
 ** governed by the rights and conditions as set out in the yFiles for JavaFX
 ** license agreement.
 **
 ** THIS SOFTWARE IS PROVIDED ''AS IS'' AND ANY EXPRESS OR IMPLIED
 ** WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 ** MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 ** NO EVENT SHALL yWorks BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 ** SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 ** TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 ** PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 ** LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 ** NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 ** SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **
 ***************************************************************************/
package toolkit;

import com.yworks.yfiles.view.CanvasControl;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Displays an error dialog with the option to send an error report to yWorks GmbH detailing
 * the error with stacktrace as well as java and os specifics and optionally a user comment
 * and email address for inquiry.
 */
public class ErrorReporter implements Runnable {

  // Some constants to use for the UI
  private static final String REQUESTING_EMAIL_ADDRESS = "Please consider adding your email address in your report.\n" +
      "Your email address gives us the possibility to get in contact with you regarding your report. \n" +
      "This can help us in fixing reported issues. \n" +
      "We will never use your email address for any other purpose.";
  private static final String SENDING_REPORT_FAILED = "Unfortunately there was a problem sending your report.\n" +
      "Please try again later or contact us via yfilesjavafx@yworks.com";
  private static final String SENDING_ERROR_REPORT_SUCCESS = "Thank you for helping us improve yFiles for JavaFX.";

  private final Scene scene;
  private final String boundary;
  private Throwable exception;
  private String header;
  private boolean reportSent;

  /**
   * Constructs a new error reporter for the given exception. The scene is the parent window for
   * the dialogs and the header provides information about the demo.
   */
  public ErrorReporter(final Scene scene, final Throwable exception, final String header) {
    this.scene = scene;
    this.exception = exception;
    this.header = header;
    this.boundary = (int) (Math.random() * 100000.0) + "boundary" + (int) (Math.random() * 100000.0);
  }

  @Override
  public void run() {
    reportSent = false;
    GridPane contentPane = new GridPane();
    contentPane.setAlignment(Pos.CENTER);
    contentPane.setHgap(10);
    contentPane.setVgap(5);
    contentPane.setPadding(new Insets(10, 5, 10, 5));

    Text invalidLabel = new Text("We apologize for the error, this shouldn't happen.\n" +
        "Please help us fixing this issue by sending this error report.");
    contentPane.add(invalidLabel, 0, 0, 2, 1);

    TextField emailTextField = new TextField();
    Label emailLabel = new Label("E-Mail:");
    emailTextField.setPromptText("Please enter your email address for possible inquiries");
    Tooltip emailToolTip = new Tooltip(REQUESTING_EMAIL_ADDRESS);
    emailTextField.setTooltip(emailToolTip);
    emailLabel.setTooltip(emailToolTip);
    contentPane.add(emailLabel, 0, 1);
    contentPane.add(emailTextField, 1, 1);

    TextArea commentTextArea = new TextArea();
    commentTextArea.setPromptText("Please enter here the steps to reproduce the error, i.e. what were you doing when this error happened?");
    contentPane.add(new Text("Comment:"), 0, 2);
    contentPane.add(commentTextArea, 1, 2);

    ButtonType sendButtonType = new ButtonType("Send Report");
    ButtonType ignoreButtonType = new ButtonType("Ignore", ButtonBar.ButtonData.CANCEL_CLOSE);

    Alert dialog = new Alert(Alert.AlertType.ERROR);
    dialog.setTitle("Report Error");
    dialog.setHeaderText("The following exception occurred:\n" + getExceptionMessage());

    dialog.initOwner(scene != null ? scene.getWindow() : null);
    dialog.initModality(Modality.WINDOW_MODAL);
    dialog.getDialogPane().setExpandableContent(createStackTrace());
    dialog.getDialogPane().setContent(contentPane);
    dialog.getButtonTypes().setAll(sendButtonType, ignoreButtonType);

    Optional<ButtonType> result = dialog.showAndWait();

    ButtonType resultButtonType = result.orElse(ButtonType.OK);

    while (resultButtonType == sendButtonType){

      String email = emailTextField.getText();
      // request an email address for inquiries
      if (email.isEmpty() || !email.contains("@")) {
        Alert pleaseEnterEmailAlert = new Alert(Alert.AlertType.WARNING);
        pleaseEnterEmailAlert.setHeaderText("Missing or invalid email address");
        pleaseEnterEmailAlert.setContentText(REQUESTING_EMAIL_ADDRESS);
        pleaseEnterEmailAlert.showAndWait();
        resultButtonType = dialog.showAndWait().orElse(ButtonType.OK);
      } else {

        // we cannot realistically cancel the sending process once it is started, but we can at least display a hideable progress indicator.
        Alert progressIndicator = new Alert(Alert.AlertType.INFORMATION);
        progressIndicator.setGraphic(new ProgressIndicator());
        progressIndicator.setHeaderText("Sending error report...");
        progressIndicator.getButtonTypes().setAll(new ButtonType("Hide", ButtonBar.ButtonData.CANCEL_CLOSE));
        progressIndicator.show();
        Thread sendThread = new Thread(() -> {
          try {
            send(email, commentTextArea.getText());
            reportSent = true;
          } catch (IOException e) {
            // something bad happened... mark the report as not sent (which it is by default)
          }
          Platform.runLater(() -> {
            if (progressIndicator.isShowing()) {
              progressIndicator.close();
            }
            showReportSentDialog();
          });
        });
        sendThread.start();
        break;
      }
    }
  }

  /**
   * Chops up the possibly long error message (inserts newline escape characters after every 140 characters)
   */
  private String getExceptionMessage() {
    int wrappingWidth = 140;
    String message = exception.getMessage();
    if (message == null) {
      return "null";
    } else {
      StringBuilder result = new StringBuilder();
      for (int i = 0, n = message.length(); i < n; i += wrappingWidth) {
        result.append(message.substring(i, i + Math.min(wrappingWidth, n - i)));
        result.append("\n");
      }
      return result.toString();
    }
  }

  /**
   * Creates a pane to display the stacktrace.
   */
  private VBox createStackTrace() {
    VBox stacktracePane = new VBox(5);
    stacktracePane.setAlignment(Pos.TOP_LEFT);
    stacktracePane.setPadding(new Insets(10, 5, 10, 5));

    // exception details
    StringWriter sw = new StringWriter();
    PrintWriter pw = new PrintWriter(sw);
    exception.printStackTrace(pw);
    TextArea stacktraceTextArea = new TextArea(sw.toString());
    stacktraceTextArea.setEditable(false);
    stacktraceTextArea.setWrapText(true);

    stacktraceTextArea.setMaxWidth(Double.MAX_VALUE);
    stacktraceTextArea.setMaxHeight(Double.MAX_VALUE);
    VBox.setVgrow(stacktraceTextArea, Priority.ALWAYS);

    stacktracePane.getChildren().addAll(new Label("The exception stacktrace was:"), stacktraceTextArea);
    return stacktracePane;
  }

  /**
   * Dependent on the {@link #reportSent} property,
   * this either shows an information dialog which informs the user that the report
   * was successfully sent or an error dialog that something went wrong in the process.
   */
  private void showReportSentDialog() {
    Alert responseAlert = new Alert(reportSent ? Alert.AlertType.INFORMATION : Alert.AlertType.ERROR);
    responseAlert.setHeaderText(reportSent ? "Error report sent" : "Sending error report failed");
    responseAlert.setContentText(reportSent ? SENDING_ERROR_REPORT_SUCCESS : SENDING_REPORT_FAILED);
    responseAlert.showAndWait();
  }

  /**
   * Sends the error report with the given email and comment of the user.
   * Opens a {@link java.net.HttpURLConnection} and writes the error report
   * to its output stream. Retrieves the response and returns it.
   * @return the response of the server
   * @throws IOException
   */
  private String send(String email, String comment) throws IOException {
    HashMap<String, String> properties = new HashMap<>();

    // data of the user
    properties.put("user_email", email);
    properties.put("user_comment", comment);

    // data of the yFiles version and demo
    properties.put("url", this.header + " / " + exception.toString());
    properties.put("exact_product", "yFiles for JavaFX");
    properties.put("product_distribution", CanvasControl.class.getPackage().getImplementationTitle());
    properties.put("product_version", CanvasControl.class.getPackage().getImplementationVersion());

    // data of the used java version and the os
    addSystemInfo(properties, "java.version");
    addSystemInfo(properties, "java.vendor");
    addSystemInfo(properties, "java.vm.name");
    addSystemInfo(properties, "os.name");
    addSystemInfo(properties, "os.arch");
    addSystemInfo(properties, "os.version");


    final URL phpMailer = new URL("http://kb.yworks.com/errorFeedback.html");

    /* connect to given URL */
    final HttpURLConnection con = (HttpURLConnection) phpMailer.openConnection();

    /* initialize post */
    con.setRequestProperty( "Content-Type", "multipart/form-data; boundary=" + boundary );
    con.setDoOutput( true );
    con.setDoInput( true );
    con.setUseCaches( false );
    con.setRequestMethod( "POST" );
    con.connect();

    try {
      return sendImpl(con, properties);
    } finally {
      con.disconnect();
    }
  }

  /**
   * Writes the data to the outputstream of the connection and returns the response.
   * @return the response of the server for the request.
   * @throws IOException
   */
  private String sendImpl(final HttpURLConnection con, final HashMap<String, String> properties) throws IOException {

    /* write post data to output */
    try (OutputStream out = con.getOutputStream()) {
      writeContent(out, properties);
      out.flush();
    }

    /* get response */
    final StringBuilder response = new StringBuilder();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
      for (String line = br.readLine(); line != null; line = br.readLine()) {
        response.append( line );
        response.append( '\n' );
      }
    }
    return response.toString();
  }

  /**
   * Writes all properties in the appropriate format to the output stream.
   * @throws UnsupportedEncodingException
   */
  private void writeContent(final OutputStream os, final HashMap<String, String> properties) throws UnsupportedEncodingException {
    final PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
    out.write( "\n\n" );
    for ( Map.Entry<String, String> entry : properties.entrySet() ) {
      out.println("--" + boundary);
      final String name = entry.getKey();
      final String value = entry.getValue();
      String message = "Content-Disposition: form-data; name=\"error_dialog_" + name + '\"';
      out.println(message);
      out.println();
      out.println(value);
    }
    // add stacktrace
    out.println("--" + boundary);
    out.println("Content-Disposition: form-data; name=\"error_dialog_stacktrace\"");
    out.println();
    exception.printStackTrace(out);
    out.println("--" + boundary + "--" );

    out.flush();
  }

  /**
   * Small convenience method to add the result of {@link java.lang.System#getProperty(String)}
   * with the given key to the map.
   */
  private void addSystemInfo(Map<String, String> map, String key) {
    try {
      map.put(key, System.getProperty(key));
    } catch (SecurityException ignored) {
    }
  }
}

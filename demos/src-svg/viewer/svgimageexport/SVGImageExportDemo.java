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
package viewer.svgimageexport;

import viewer.imageexport.AbstractImageExportDemo;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.ContextConfigurator;

import org.apache.batik.anim.dom.SVGDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.jfxconverter.JFXConverter;
import org.jfxconverter.drivers.svg.ConvertorSVGGraphics2D;
import org.w3c.dom.svg.SVGDocument;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;

/**
 * Use the export capabilities of the yFiles components in combination with the JFXConverter framework to export to SVG files.
 * Draw arbitrary objects onto the <code>GraphControl</code> and interact with them via <code>InputModes</code>.
 * <br>
 * Known shortcomings:
 * <ul>
 *   <li>When mapping javafx.scene.text.Font to java.awt.Font it can be the case that no suitable match is found so the result looks quite different.</li>
 *   <li>As all JavaFX Shapes are mapped 'manually' by the converter to draw/fill calls, the result may differ a bit.</li>
 *   <li>CSS properties are also mapped 'manually' so there might be some left that are not handled.</li>
 * </ul>
 * <p>
 * The demo uses patched versions of the Batik SVG, JFXConverter and MDIUtilities libraries available on the yFiles website:
 * <ul>
 *   <li><a href="https://www.yworks.com/resources/yfilesjava/demos-support/3.6/batik.jar">Batik</a></li>
 *   <li><a href="https://www.yworks.com/resources/yfilesjavafx/demos-support/3.0/JFXConverter.jar">JFXConverter</a></li>
 *   <li><a href="https://www.yworks.com/resources/yfilesjavafx/demos-support/3.0/MDIUtilities-core-LGPL.jar">MDIUtilities-core-LGPL</a></li>
 *   <li><a href="https://www.yworks.com/resources/yfilesjavafx/demos-support/3.0/MDIUtilities-ui-LGPL.jar">MDIUtilities-ui-LGPL</a></li>
 * </ul>
 * </p>
 */
public class SVGImageExportDemo extends AbstractImageExportDemo {
  private static final java.awt.Color TRANSPARENT = new java.awt.Color(255, 255, 255, 0);

  public WebView preview;

  @Override
  public void initialize() {
    super.initialize();
    setBackgroundColor(preview, Color.LIGHTGREY);
  }

  /**
   * Update the export preview it the preview tab is selected.
   */
  @FXML
  protected void updatePreview() {
    if (tabPane.getSelectionModel().getSelectedIndex() == 0) {
      // if the current tab isn't the export preview, there is no need to update the preview
      // because when we change to the export preview later this will trigger an update anyway
      return;
    }

    // export the canvas content to an SVG Element and update the previewCanvas
    Element svgRoot = exportToSVGElement();
    DocumentFragment svgDocumentFragment = svgRoot.getOwnerDocument().createDocumentFragment();
    svgDocumentFragment.appendChild(svgRoot);

    try (StringWriter writer = new StringWriter()) {
      writeDocument(svgDocumentFragment, writer);
      preview.getEngine().loadContent(writer.toString(), "image/svg+xml");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected void saveToFile(String filename) {
    // append the correct file extension if it is missing
    if (!filename.endsWith(".svg")) {
      filename += ".svg";
    }

    // export to an SVG element
    Element svgRoot = exportToSVGElement();
    DocumentFragment svgDocumentFragment = svgRoot.getOwnerDocument().createDocumentFragment();
    svgDocumentFragment.appendChild(svgRoot);

    // write the SVG Document into the specified file
    try (FileOutputStream stream = new FileOutputStream(filename)) {
      OutputStreamWriter writer = new OutputStreamWriter(stream, "UTF-8");
      writeDocument(svgDocumentFragment, writer);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Writes the given SVG document to an output stream.
   * @param svgDocument the SVG document to write
   * @param writer the writer to write with
   */
  private void writeDocument(DocumentFragment svgDocument, Writer writer) throws IOException {
    try {
      // Prepare the DOM document for writing
      Source source = new DOMSource(svgDocument);
      Result result = new StreamResult(writer);

      // Write the DOM document to the file
      TransformerFactory tf = TransformerFactory.newInstance();
      try {
        tf.setAttribute("indent-number", 2);
      } catch (IllegalArgumentException iaex) {
        iaex.printStackTrace();
      }
      Transformer xformer = tf.newTransformer();
      xformer.setOutputProperty(OutputKeys.INDENT, "yes");
      xformer.transform(source, result);
    } catch (TransformerException e) {
      throw new IOException(e.getMessage());
    }
  }

  /**
   * Exports the content to a SVG element.
   */
  private Element exportToSVGElement() {
    // Create a SVG document.
    DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
    String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;
    SVGDocument doc = (SVGDocument) impl.createDocument(svgNS, "svg", null);

    // Create a converter for this document.
    SVGGraphics2D svgGraphics2D = new ConvertorSVGGraphics2D(doc);

    // paint the content of the exporting graph control to the Graphics object
    svgGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    ContextConfigurator cnfg = createContextConfigurator();

    // fill background
    Paint fill = transparent.isSelected() ? TRANSPARENT : toAwtColor(backgroundColor.getValue());
    final Paint oldPaint = svgGraphics2D.getPaint();
    svgGraphics2D.setPaint(fill);
    svgGraphics2D.fill(new Rectangle2D.Double(0, 0, cnfg.getViewWidth(), cnfg.getViewHeight()));
    svgGraphics2D.setPaint(oldPaint);

    // configure the Graphics transform
    final InsetsD margins = cnfg.getMargins();
    svgGraphics2D.translate(margins.getLeft(), margins.getTop());
    IRenderContext paintContext = cnfg.createRenderContext(getExportingGraphControl());
    try {
      svgGraphics2D.transform(toAffineTransform(paintContext.getViewTransform().createInverse()));
    } catch (NonInvertibleTransformException e) {
      e.printStackTrace();
    }

    // set the graphics clip
    final RectD clip = paintContext.getClip();
    if (clip != null) {
      svgGraphics2D.clip(new Rectangle2D.Double(clip.getX(), clip.getY(), clip.getWidth(), clip.getHeight()));
    }

    // export the canvas content
    Node node = getExportContent(cnfg);
    // use the JFXConverter to draw the content on the SVGGraphics2D
    JFXConverter converter = new JFXConverter();
    converter.convert(svgGraphics2D, node);

    svgGraphics2D.dispose();

    Element svgRoot = svgGraphics2D.getRoot(doc.getDocumentElement());
    svgRoot.setAttributeNS(
      "http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink");
    svgRoot.setAttribute("width", "" + cnfg.getViewWidth());
    svgRoot.setAttribute("height", "" + cnfg.getViewHeight());
    return svgRoot;
  }

  /**
   * Creates a Node with the content to export.
   * @param cnfg The ContextConfigurator providing the RenderContext for exporting the content.
   * @return a Node with the content to export.
   */
  private Node getExportContent(ContextConfigurator cnfg) {
    // export content of GraphControl
    GraphControl gc = this.getExportingGraphControl();
    Node content = gc.exportContent(cnfg.createRenderContext(gc));

    // add the content to a Scene so the css stylesheets can be applied
    Group root = new Group();
    Scene scene = new Scene(root);
    root.getChildren().add(content);
    content.applyCss();
    return content;
  }

  /**
   * Converts a JavaFX color to an AWT color.
   * @param fxColor the JavaFX color.
   * @return the AWT color
   */
  private static java.awt.Color toAwtColor(Color fxColor) {
    return new java.awt.Color(
        (float) fxColor.getRed(),
        (float) fxColor.getGreen(),
        (float) fxColor.getBlue(),
        (float) fxColor.getOpacity());
  }

  /**
   * Converts a JavaFX transform to an AWT transform.
   * @param transform the JavaFX transform.
   * @return the AWT transform
   */
  private AffineTransform toAffineTransform(Transform transform) {
    return new AffineTransform(transform.getMxx(), transform.getMyx(), transform.getMxy(),
                               transform.getMyy(), transform.getTx(), transform.getTy());
  }

  protected FileChooser.ExtensionFilter[] getExtensionFilters() {
    return new FileChooser.ExtensionFilter[] {new FileChooser.ExtensionFilter("SVG Files", "*.svg")};
  }

  /**
   * Sets the background color for the given web view.
   * <p>
   * This implementation uses reflection for accessing web view implementation
   * details and will most likely cease to work in a future Java 9 release.
   * </p><p>
   * However, setting the background color for the preview control is no
   * critical part of the demo but simply serves to as simple way to visualize
   * the actual bounds of the created SVG document in the preview control.
   * </p>
   */
  private static void setBackgroundColor( WebView view, Color background ) {
    WebEngine webEngine = view.getEngine();
    webEngine.documentProperty().addListener(
      ( ObservableValue<? extends Document> observable, Document oldValue, Document newValue) -> {
        try {
          // use reflection to retrieve the WebEngine's private 'page' field
          Field f = webEngine.getClass().getDeclaredField("page");
          f.setAccessible(true);
          Object page = (Object) f.get(webEngine);

          // set the background color of the page using reflection again
          Class pageType = page.getClass();
          Method setBackground = pageType.getDeclaredMethod("setBackgroundColor", Integer.TYPE);
          setBackground.invoke(page, Integer.valueOf(toAwtColor(background).getRGB()));
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
  }

  @Override
  public String getTitle() {
    return "SVG Image Export Demo - yFiles for JavaFX";
  }

  public static void main(String[] args) {
    launch(args);
  }
}

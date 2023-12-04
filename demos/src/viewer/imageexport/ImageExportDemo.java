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
package viewer.imageexport;

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.view.ContextConfigurator;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.PixelImageExporter;
import com.yworks.yfiles.view.input.GraphViewerInputMode;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Use the export capabilities of the yFiles components, namely export to the bitmap formats PNG, JPEG, GIF
 * and BMP as well as drawing arbitrary objects onto the <code>GraphControl</code> and interact with them via<code>
 * InputModes</code>.
 * <p>
 * The demo consists of an editable graph and a rectangle that defines a region on the graph
 * designated for export. The demo features a preview section where the image can be viewed prior to exporting it
 * with different settings that update the preview image on demand.
 * </p>
 * <p>
 *   The main logic for exporting images can be found in {@link #exportToImage()} and {@link #saveToFile()}.
 * </p>
 */
public class ImageExportDemo extends AbstractImageExportDemo {
  public ComboBox<ImageFormat> formatBox;
  // displays the preview image for the export
  public GraphControl previewControl;

  /**
   * Initializes the controller. This is called when the FXMLLoader instantiates the scene graph.
   * At the time this method is called, all nodes in the scene graph is available. Most importantly,
   * the GraphControl instance is initialized.
   * <p>
   *   In this demo however, the main initialization step is done in {@link #onLoaded()}.
   * </p>
   */
  public void initialize(){
    super.initialize();

    formatBox.getItems().addAll(ImageFormat.values());
    formatBox.getSelectionModel().selectFirst();
    formatBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updatePreview());
  }

  @Override
  protected void initializeInputModes() {
    super.initializeInputModes();

    previewControl.setInputMode(new GraphViewerInputMode());
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
    // clear the preview
    ICanvasObjectGroup rootGroup = previewControl.getRootGroup();
    while (rootGroup.getFirst() != null) {
      rootGroup.getFirst().remove();
    }

    // export the graph to an image and show it in the preview panel
    Image image = exportToImage();
    ImageView view = new ImageView(image);
    rootGroup.addChild(view, ICanvasObjectDescriptor.VISUAL);

    previewControl.setZoom(1);
    previewControl.setViewPoint(PointD.ORIGIN);
    previewControl.updateImmediately();
  }

  /**
   * Uses the current settings to construct a {@link SnapshotParameters} object
   * that, when given to the {@link PixelImageExporter}, converts the current
   * graph into an image as demanded.
   */
  private Image exportToImage() {
    // the control to export from
    GraphControl control = getExportingGraphControl();

    // create an exporter that exports the given region
    PixelImageExporter exporter = getPixelImageExporter();

    // get the image from the exporter
    return exporter.exportToBitmap(control);
  }

  /**
   * Creates an exporter that exports the given region.
   */
  private PixelImageExporter getPixelImageExporter() {
    // create an exporter with the settings of the option panel
    ContextConfigurator configurator = createContextConfigurator();

    PixelImageExporter exporter = new PixelImageExporter(configurator);
    // set the color for the background of the image
    boolean transparentBackground = transparent.isSelected();
    // check if the format is transparent PNG
    if (formatBox.getSelectionModel().getSelectedItem().supportsTransparency() && transparentBackground) {
      exporter.setBackgroundFill(Color.TRANSPARENT);
    } else {
      // if not, set the background color
      exporter.setBackgroundFill(backgroundColor.getValue());
      // it is also possible to parse the control's css settings:
      // either parse the style or the style sheets and look for the correct style class.
      // this is a bit of work so it is left out here for demonstration
      // purposes and we simple use the value of the color picker
    }
    return exporter;
  }


  protected void saveToFile(String filename) {
    // get the image to export
    Image image = exportToImage();

    // convert it to a AWT image to use ImageIO
    BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

    // jpeg and bmp do not support alpha channel, thus we have to make the image opaque
    ImageFormat format = formatBox.getSelectionModel().getSelectedItem();
    if (!format.supportsTransparency()){
      BufferedImage bufferedImageRGB = new BufferedImage(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferedImage.OPAQUE);
      Graphics2D graphics = bufferedImageRGB.createGraphics();
      graphics.drawImage(bufferedImage, 0, 0, null);
      bufferedImage = bufferedImageRGB;
    }

    // append the correct file extension if it is missing
    boolean hasExtension = false;
    for (int i = 0; i < format.extensions.length; i++) {
      String extension = format.extensions[i];
      if (filename.endsWith(extension)) {
        hasExtension = true;
        break;
      }
    }
    if (!hasExtension) {
      filename += "." + format.canonicalExtension();
    }

    try {
      FileOutputStream stream = new FileOutputStream(filename);
      ImageIO.write(bufferedImage, format.canonicalExtension(), stream);
      stream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  protected FileChooser.ExtensionFilter[] getExtensionFilters() {
    return new FileChooser.ExtensionFilter[] {formatBox.getSelectionModel().getSelectedItem().filter()};
  }

  /**
   * An enum for well-known image formats.
   */
  private enum ImageFormat {
    JPG("JPEG Files", false, true, "jpg", "jpeg", "jpe"),
    GIF("GIF Files", false, false, "gif"),
    PNG("PNG Files", true, false, "png"),
    BMP("Bitmap Files", false, false, "bmp");

    private String description;
    private boolean transparency;
    private boolean qualityAdjustable;
    private String[] extensions;
    private FileChooser.ExtensionFilter fileFilter;

    /**
     * Initializes a new <code>ImageFormat</code> instance for a single image format.
     * @param description       a human-readable description of the image format.
     * @param transparency      whether or not the file format supports transparency
     * @param qualityAdjustable whether or not the quality of the encoding is adjustable
     * @param extensions        the file name extensions used by the image format. The first given extension should be
     *                          the canonical file name extension
     */
    ImageFormat(String description, boolean transparency, boolean qualityAdjustable, String... extensions) {
      this.description = description;
      this.transparency = transparency;
      this.qualityAdjustable = qualityAdjustable;
      this.extensions = extensions;
      this.fileFilter = createFileFilter();
    }

    /**
     * Creates a {@link javax.swing.filechooser.FileFilter} for the image format.
     */
    private FileChooser.ExtensionFilter createFileFilter() {
      List<String> extensionList = new ArrayList<>(extensions.length);
      for (String extension : extensions) {
        extensionList.add("*." + extension);
      }
      return new FileChooser.ExtensionFilter(description, extensionList);
    }

    /**
     * Returns the canonical file name extension for the image format represented by this filter.
     */
    String canonicalExtension() {
      return extensions[0];
    }

    /**
     * Returns a {@link javax.swing.filechooser.FileFilter} for the image format.
     */
    FileChooser.ExtensionFilter filter() {
      return fileFilter;
    }

    /**
     * Determines whether or not the file format supports transparency.
     */
    boolean supportsTransparency() {
      return transparency;
    }

    /**
     * Determines whether or not the quality of the encoding is adjustable.
     */
    boolean isQualityAdjustable() {
      return qualityAdjustable;
    }

    @Override
    public String toString() {
      return canonicalExtension().toUpperCase();
    }
  }

  public static void main(String[] args) {
    launch(args);
  }
}

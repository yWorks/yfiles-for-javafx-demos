/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.ContextConfigurator;
import com.yworks.yfiles.view.PixelImageExporter;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.WritableImage;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * Helper class to provide simple bitmap export.
 */
public class BitmapExportHelper {
  PixelImageExporter exporter;

  boolean transparencyEnabled;

  /**
   * Initializes a new <code>BitmapExportHelper</code> instance with the
   * given configuration.
   */
  public BitmapExportHelper(ContextConfigurator configuration) {
    exporter = new PixelImageExporter(configuration);
  }

  /**
   * Determines if the given image format is supported for method
   * {@link #export(CanvasControl, OutputStream, String)}.
   * @param format the name of the image format to check.
   * @return <code>true</code> if
   * {@link #export(CanvasControl, OutputStream, String)} supports the
   * given image format and <code>false</code> otherwise.
   */
  public boolean isFormatSupported( String format ) {
    return findImageWriter(format) != null;
  }

  /**
   * Exports a region of the given {@link CanvasControl} to the given
   * {@link java.io.OutputStream}.
   * The region to export is determined by the exporter's {@link #getConfiguration() ContextConfigurator}.
   * The format for the generated image is defined by the given string parameter.
   * <p>
   * Calls {@link #exportToBitmap(CanvasControl)} for
   * creating the in-memory image that is written in the specified image format.
   * </p>
   * <p>
   * Use {@link #isFormatSupported(String)} to determine whether or not this
   * method supports a given image format.
   * </p>
   * @param canvas the {@link CanvasControl} to export to an image.
   * @param stream the stream to which the generated image is exported to.
   * @param format the informal name of the image format to export.
   * @throws IOException if an error occurred during image export.
   * @throws IllegalArgumentException if the specified format is not supportd.
   */
  public void export(CanvasControl canvas, OutputStream stream, String format) throws IOException {
    ImageWriter imageWriter = findImageWriter(format);
    if (imageWriter == null) {
      throw new IllegalArgumentException("Unsupported format: " + format);
    } else {
      export(canvas, stream, imageWriter);
    }
  }

  /**
   * Exports a region of the given {@link CanvasControl} to the given
   * {@link java.io.OutputStream}.
   * The region to export is determined by the exporter's {@link #getConfiguration() ContextConfigurator}.
   * <p>
   *   Calls {@link #exportToBitmap(CanvasControl)} for
   *   creating the in-memory image that is written by the given image writer.
   * </p>
   * <p>
   *   The given {@link ImageWriter writer} is used for encoding the exported
   *   region to an image format.
   *   <br>
   *   Note, this method does not {@link javax.imageio.ImageWriter#dispose() dispose}
   *   the given writer.
   * </p>
   * @param canvas the {@link CanvasControl} to export to an image.
   * @param stream the stream to which the generated image is exported to.
   * @param writer the {@link javax.imageio.ImageWriter} that encodes the exported region to an image format.
   * @throws IOException if an error occurred during image export.
   */
  public void export(CanvasControl canvas, OutputStream stream, ImageWriter writer) throws IOException {
    try (ImageOutputStream ios = ImageIO.createImageOutputStream(stream)) {
      writer.setOutput(ios);
      writer.write(null, new IIOImage(exportToBitmap(canvas), null, null), writer.getDefaultWriteParam());
    }
  }

  /**
   * Retrieves an image writer for the specifed raster image format.
   */
  private static ImageWriter findImageWriter(String format) {
    Iterator it = ImageIO.getImageWritersByFormatName(format);
    if (it.hasNext()) {
      return (ImageWriter) it.next();
    } else {
      return null;
    }
  }

  /**
   * Returns the {@link ContextConfigurator} instance that is used to
   * configure the view port that is exported to a bitmap image.
   */
  public ContextConfigurator getConfiguration() {
    return exporter.getConfiguration();
  }

  /**
   * Determines if this image exporter uses an intermediate
   * image that can handle non-opaque pixels and <code>false</code> otherwise.
   */
  public boolean isTransparencyEnabled() {
    return transparencyEnabled;
  }

  /**
   * Specifies if this image exporter uses an intermediate
   * image that can handle non-opaque pixels and <code>false</code> otherwise.
   */
  public void setTransparencyEnabled(boolean enabled) {
    transparencyEnabled = enabled;
  }

  /**
   * Convenience method that exports the {@link CanvasControl#contentRect} of a
   * control to an image in a stream.
   * For full control over the output use class {@link PixelImageExporter}.
   *
   * @param canvas The control whose content shall be written.
   * @param file   The name of the file to write the image to.
   */
  public static void exportToBitmap(CanvasControl canvas, String file) throws IOException {
    try (FileOutputStream stream = new FileOutputStream(file)) {
      int idx = file.lastIndexOf('.');
      String format = idx > -1 ? file.substring(idx + 1) : "png";
      exportToBitmap(canvas, stream, format, canvas.getContentRect());
    }
  }

  /**
   * Convenience method that exports the {@link CanvasControl#contentRect} of a
   * control to an image in a stream.
   * For full control over the output use class {@link PixelImageExporter}.
   *
   * @param canvas    The control whose content shall be written.
   * @param stream    The stream to write the image to.
   * @param format    The output format to use.
   * @param worldRect The rectangle in the world coordinate system to export.
   */
  public static void exportToBitmap(CanvasControl canvas, OutputStream stream, String format, RectD worldRect)
      throws IOException {

    // jpg and bmp do not support alpha
    boolean opaque =
            "jpeg".equals(format) || "jpe".equals(format) ||
            "jpg".equals(format) || "bmp".equals(format);

    ContextConfigurator configuration = new ContextConfigurator(worldRect);
    BitmapExportHelper exporter = new BitmapExportHelper(configuration);
    exporter.setTransparencyEnabled(!opaque);

    ImageIO.write(exporter.exportToBitmap(canvas), format, stream);
  }

  /**
   * Exports a region of the given {@link CanvasControl} instance to a
   * {@link java.awt.image.BufferedImage} instance.
   */
  private BufferedImage exportToBitmap(CanvasControl canvas) {
    return exportToBitmap(exporter, canvas, isTransparencyEnabled());
  }

  /**
   * Exports a region of the given {@link CanvasControl} instance to a
   * {@link java.awt.image.BufferedImage} instance.
   */
  public static BufferedImage exportToBitmap(
          PixelImageExporter exporter,
          CanvasControl canvas,
          boolean allowTransparency
  ) {
    WritableImage image = exporter.exportToBitmap(canvas);

    BufferedImage bi = SwingFXUtils.fromFXImage(image, null);

    return allowTransparency ? bi : removeAlpha(bi);
  }

  /**
   * Creates a copy of the given image that has no transparent color values.
   */
  private static BufferedImage removeAlpha(BufferedImage image) {
    int width = image.getWidth();
    int height = image.getHeight();

    BufferedImage copy = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    ColorModel colorModel = copy.getColorModel();
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        int rgb = image.getRGB(x, y);
        int newRgb = ((colorModel.getRed(rgb) & 0xFF) << 16) |
                     ((colorModel.getGreen(rgb) & 0xFF) << 8) |
                     ((colorModel.getBlue(rgb) & 0xFF));
        copy.setRGB(x, y, newRgb);
      }
    }

    return copy;
  }
}

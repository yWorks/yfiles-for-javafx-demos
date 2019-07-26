/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package deploy.obfuscation;

import com.yworks.yfiles.utils.Obfuscation;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * A JavaFX rectangle that is bound to a {@link com.yworks.yfiles.view.GraphControl}'s content rectangle,
 * zoom and viewport offset. The rectangle is visualizing the content rect at any zoom level at the correct
 * place. The offsets and values are calculated using JavaFX data binding mechanism.
 * <p>
 *   The purpose of this class is to show how fxml-relevant data structures have to be
 *   obfuscated.
 * </p>
 * <p>
 *   The class name itself and most of its members that are used in fxml to bind data
 *   need to be excluded from obfuscation: Mark class to be excluded from obfuscation
 *   and apply this setting to members. Override this on members that can be obfuscated.
 * </p>
 */
@Obfuscation( exclude = true, applyToMembers = true)
public class BackgroundRectangle extends Rectangle {

  /**
   * The alignment of the rectangle in the StackPane.
   * Although this property is public, it is only used
   * in code and not in reflection or fxml, thus obfuscate
   * it by overriding the inherited obfuscation configuration.
   */
  @Obfuscation ( exclude = false )
  public static final Pos alignment = Pos.TOP_LEFT;

  // Properties that are used to calculate the position and bounds of the rectangle.
  // Those are accessed and set in fxml and thus need to be excluded from obfuscation.
  // No need to annotate something here because they inherit their obfuscation configuration
  // from the class definition
  private DoubleProperty viewportXOffset = new SimpleDoubleProperty(this, "viewportXOffset", 0);
  private DoubleProperty viewportYOffset = new SimpleDoubleProperty(this, "viewportYOffset", 0);
  private DoubleProperty zoom = new SimpleDoubleProperty(this, "zoom", 0);
  private DoubleProperty contentRectXOffset = new SimpleDoubleProperty(this, "contentRectXOffset", 0);
  private DoubleProperty contentRectYOffset = new SimpleDoubleProperty(this, "contentRectYOffset", 0);
  private DoubleProperty contentRectWidth = new SimpleDoubleProperty(this, "contentRectWidth", 0);
  private DoubleProperty contentRectHeight = new SimpleDoubleProperty(this, "contentRectHeight", 0);

  // JavaFX getter / setter for the properties
  public double getZoom() {
    return zoom.get();
  }

  public DoubleProperty zoomProperty() {
    return zoom;
  }

  public void setZoom(double zoom) {
    this.zoom.set(zoom);
  }

  public double getViewportXOffset() {
    return viewportXOffset.get();
  }

  public DoubleProperty viewportXOffsetProperty() {
    return viewportXOffset;
  }

  public void setViewportXOffset(double viewportXOffset) {
    this.viewportXOffset.set(viewportXOffset);
  }

  public double getViewportYOffset() {
    return viewportYOffset.get();
  }

  public DoubleProperty viewportYOffsetProperty() {
    return viewportYOffset;
  }

  public void setViewportYOffset(double viewportYOffset) {
    this.viewportYOffset.set(viewportYOffset);
  }

  public double getContentRectXOffset() {
    return contentRectXOffset.get();
  }

  public DoubleProperty contentRectXOffsetProperty() {
    return contentRectXOffset;
  }

  public void setContentRectXOffset(double contentRectXOffset) {
    this.contentRectXOffset.set(contentRectXOffset);
  }

  public double getContentRectYOffset() {
    return contentRectYOffset.get();
  }

  public DoubleProperty contentRectYOffsetProperty() {
    return contentRectYOffset;
  }

  public void setContentRectYOffset(double contentRectYOffset) {
    this.contentRectYOffset.set(contentRectYOffset);
  }

  public double getContentRectWidth() {
    return contentRectWidth.get();
  }

  public DoubleProperty contentRectWidthProperty() {
    return contentRectWidth;
  }

  public void setContentRectWidth(double contentRectWidth) {
    this.contentRectWidth.set(contentRectWidth);
  }

  public double getContentRectHeight() {
    return contentRectHeight.get();
  }

  public DoubleProperty contentRectHeightProperty() {
    return contentRectHeight;
  }

  public void setContentRectHeight(double contentRectHeight) {
    this.contentRectHeight.set(contentRectHeight);
  }
  // end getter / setter

  /**
   * Creates a new rectangle and binds the values to compute its bounds.
   */
  public BackgroundRectangle() {
    StackPane.setAlignment(this, alignment);

    // bind the offset of the rectangle to the location of the content rect of the graph control
    translateXProperty().bind(computeViewOffset(contentRectXOffsetProperty(), viewportXOffsetProperty()));
    translateYProperty().bind(computeViewOffset(contentRectYOffsetProperty(), viewportYOffsetProperty()));

    // bind the width and height of the rectangle to the zoom-level adjusted width and height of the content rect
    widthProperty().bind(Bindings.multiply(zoomProperty(), contentRectWidthProperty()));
    heightProperty().bind(Bindings.multiply(zoomProperty(), contentRectHeightProperty()));
  }

  /**
   * Creates a {@link javafx.beans.binding.NumberBinding} that converts an offset relative to the (0,0) coordinate of the screen (the upper left corner)
   * given the offsets of the point in the actual coordinate system the viewport of the coordinate system and the zoom factor.
   * <p>
   *   The formula is
   *   <pre>
   *     viewOffset = (zoom * actualOffset) - (zoom * viewportOffset)
   *   </pre>
   * </p>
   * <p>
   *   This method is only used in non-public code and can thus be obfuscated.
   * </p>
   * @param actualOffset the offset in the originating coordinate system
   * @param viewportOffset the offset of the viewport on the originating coordinate system
   * @return A binding to the offset in the screen coordinate system that is updated when either the two input values change or when the zoom changes.
   */
  @Obfuscation ( exclude = false )
  private NumberBinding computeViewOffset(DoubleProperty actualOffset, DoubleProperty viewportOffset) {
    return Bindings.subtract(Bindings.multiply(zoomProperty(), actualOffset), Bindings.multiply(zoomProperty(), viewportOffset));
  }
}

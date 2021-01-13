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
package viewer.backgroundimage;

import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Creates JavaFX nodes that display an image.
 */
public class ImageVisualCreator implements IVisualCreator {
  private final Image image;
  private final double x;
  private final double y;

  /**
   * Initializes a new {@code ImageVisualCreator} instance
   * @param image The image to display by the creator's visuals. 
   */
  public ImageVisualCreator( Image image ) {
    this.image = image;
    this.x = -image.getWidth() * 0.5;
    this.y = -image.getHeight() * 0.5;
  }

  /**
   * Creates the JavaFX node for the background.
   * @param context The context which describes where the visual will be used.
   */
  @Override
  public Node createVisual( IRenderContext context ) {
    ImageView imgView = new ImageView(image);
    imgView.setX(x);
    imgView.setY(y);
    return imgView;
  }

  /**
   * Updates the JavaFX node for the background.
   * @param context The context which describes where the visual will be used.
   * @param oldNode The old JavaFX node
   */
  @Override
  public Node updateVisual( IRenderContext context, Node oldNode ) {
    if (oldNode instanceof ImageView &&
        ((ImageView) oldNode).getImage() == image) {
      return oldNode;
    }
    return createVisual(context);
  }
}

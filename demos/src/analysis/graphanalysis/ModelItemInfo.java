/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2020 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package analysis.graphanalysis;

import com.yworks.yfiles.graph.IModelItem;

import javafx.scene.paint.Color;

/**
 * Stores information for rendering an {@link com.yworks.yfiles.graph.IModelItem}.
 */
public abstract class ModelItemInfo {
  public static final Color[] DEFAULT_COLORS = new Color[]{
          Color.ROYALBLUE,
          Color.GOLD,
          Color.CRIMSON,
          Color.DARKTURQUOISE,
          Color.CORNFLOWERBLUE,
          Color.DARKSLATEBLUE,
          Color.ORANGERED,
          Color.MEDIUMSLATEBLUE,
          Color.FORESTGREEN,
          Color.MEDIUMVIOLETRED,
          Color.DARKCYAN,
          Color.CHOCOLATE,
          Color.ORANGE,
          Color.LIMEGREEN,
          Color.MEDIUMORCHID
  };

  private Color color;

  /**
   * Initializes a new {@code ModelItemInfo} instance with the given color
   * and components.
   * @param color The color to use by the style.
   * 
   */
  protected ModelItemInfo(Color color) {
    this.color = color;
  }

  /**
   * Returns the color to use by the style.
   * @return the color to use by the style.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Returns the color from the given item's tag.
   * @return the color from the given item's tag.
   */
  public static Color getColor(IModelItem element) {
    Object tag = element == null ? null : element.getTag();
    return tag instanceof ModelItemInfo
           ? ((ModelItemInfo) tag).getColor() : null;
  }

  /**
   * Determines the color of model items that belongs to the component with
   * the given index.
   * @param componentIdx the index of the component.
   * @return the color of model items that belongs to the component with
   * the given index.
   */
  public static Color getComponentColor(int componentIdx) {
    return DEFAULT_COLORS[componentIdx % DEFAULT_COLORS.length];
  }
}

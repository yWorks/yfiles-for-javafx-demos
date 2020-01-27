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
package layout.treemap;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILabelOwner;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.styles.AbstractLabelStyle;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.VisualGroup;

import java.util.Objects;
import javafx.geometry.Bounds;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * Visualizes text for interior node labels.
 * <p>
 * Automatically determines a suitable font size to ensure that label text fits
 * into the bounds of the label's owner node.
 * </p><p>
 * Only meant to be used for labels whose layout parameter is
 * {@link com.yworks.yfiles.graph.labelmodels.InteriorStretchLabelModel#CENTER}.
 * </p>
 */
public class DemoLabelStyle extends AbstractLabelStyle {
  static final Font DEFAULT_FONT = new Font("System", 4);
  static final InsetsD INSETS = new InsetsD(4);

  /**
   * Creates text visualizations for the given (node) label.
   */
  @Override
  protected Node createVisual( IRenderContext context, ILabel label ) {
    DemoLabelVisual visual = new DemoLabelVisual();
    visual.updateContent(label.getText(), getPreferredSize(label));
    visual.updateLocation(getBounds(context, label));
    return visual;
  }

  /**
   * Updates text visualizations for the given (node) label.
   */
  @Override
  protected Node updateVisual( IRenderContext context, Node oldVisual, ILabel label ) {
    if (oldVisual instanceof DemoLabelVisual) {
      DemoLabelVisual visual = (DemoLabelVisual) oldVisual;
      visual.updateContent(label.getText(), getPreferredSize(label));
      visual.updateLocation(getBounds(context, label));
      return visual;
    }
    return createVisual(context, label);
  }

  /**
   * Determines the preferred size for the given (node) label.
   * @return the width and height of the given label's owner node.
   */
  @Override
  protected SizeD getPreferredSize( ILabel label ) {
    ILabelOwner owner = label.getOwner();
    if (owner instanceof INode) {
      return ((INode) owner).getLayout().toSizeD();
    } else {
      return SizeD.ZERO;
    }
  }


  /**
   * Handles the automatic font size calculation.
   */
  private static final class DemoLabelVisual extends VisualGroup {
    final Text visual;
    SizeD size;
    String text;

    DemoLabelVisual() {
      visual = new Text(0, 0, "");
      visual.setFill(Color.WHITE);
      visual.setTextAlignment(TextAlignment.CENTER);
      visual.setTextOrigin(VPos.TOP);
      visual.setWrappingWidth(0);
      size = SizeD.ZERO;
      text = visual.getText();
    }

    void updateLocation( RectD lblBnds ) {
      if (getNullableChildren().size() > 0) {
        Bounds visBnds = visual.getLayoutBounds();
        setLayoutX(lblBnds.getX() + (lblBnds.getWidth() - visBnds.getWidth()) * 0.5);
        setLayoutY(lblBnds.getY() + (lblBnds.getHeight() - visBnds.getHeight()) * 0.5);
      } else {
        setLayoutX(lblBnds.getX());
        setLayoutY(lblBnds.getY());
      }
    }

    void updateContent( String text, SizeD size ) {
      if (Objects.equals(this.text, text) && Objects.equals(this.size, size)) {
        return;
      }

      this.text = text;
      this.size = size;

      configure(text, size);
    }

    /**
     * Calculates a font size which ensures that the display size of the given
     * text does not exceed the given size.
     */
    private void configure( String text, SizeD size ) {
      visual.setText(text);
      visual.setFont(DEFAULT_FONT);


      double prefW = size.width - INSETS.left - INSETS.right;
      double prefH = size.height - INSETS.top - INSETS.bottom;

      Bounds minBnds = visual.getLayoutBounds();
      if (prefW < minBnds.getWidth() || prefH < minBnds.getHeight()) {
        // the text does not fit into the given size
        // do not display any text in this case
        getNullableChildren().clear();
      } else {
        if (getNullableChildren().size() < 1) {
          add(visual);
        }


        int ub = 128;
        int lb =   0;

        for (int diff = 2 * (ub - lb); diff > 1; diff = ub - lb) {
          int tmp = diff / 2 + lb;
          visual.setFont(deriveFont(visual.getFont(), tmp));
          Bounds tmpBnds = visual.getLayoutBounds();
          if (prefW < tmpBnds.getWidth() || prefH < tmpBnds.getHeight()) {
            ub = tmp;
          } else {
            lb = tmp;
          }
        }
      }
    }

    private static Font deriveFont( Font font, int size ) {
      return new Font(font.getName(), size);
    }
  }
}

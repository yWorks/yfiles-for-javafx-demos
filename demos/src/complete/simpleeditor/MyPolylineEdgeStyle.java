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
package complete.simpleeditor;

import com.yworks.yfiles.geometry.GeneralPath;
import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.styles.IArrow;
import com.yworks.yfiles.graph.styles.IEdgePathCropper;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyle;
import com.yworks.yfiles.graph.styles.PolylineEdgeStyleRenderer;

import javax.annotation.Nonnull;

/**
 * TODO: apidoc
 *
 * @author Brunnermeier
 */
public class MyPolylineEdgeStyle extends PolylineEdgeStyle {
  public MyPolylineEdgeStyle() {
    super(new MyPolylineEdgeStyleRenderer());
  }

  private static class MyPolylineEdgeStyleRenderer extends PolylineEdgeStyleRenderer {
    protected GeneralPath cropPath(GeneralPath path ) {
      // clone the original path as we may need it later as backup
      GeneralPath backup = path.clone();

      GeneralPath croppedPath = super.cropPath(path);
      if (croppedPath.isEmpty()) {
        // crop again but ignore arrows
        IEdge edge = getEdge();
        croppedPath = backup;
        IEdgePathCropper sourceCalculator = edge.getSourcePort().lookup(IEdgePathCropper.class);
        if (sourceCalculator != null) {
          croppedPath = sourceCalculator.cropEdgePath(edge, true, IArrow.NONE, croppedPath);
        }
        IEdgePathCropper targetCalculator = edge.getTargetPort().lookup(IEdgePathCropper.class);
        if (targetCalculator != null) {
          croppedPath = targetCalculator.cropEdgePath(edge, false, IArrow.NONE, croppedPath);
        }
      }

      return croppedPath;
    }
  }
}

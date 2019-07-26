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
package viewer.graphmlcompatibility.extensions.for2x;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.labelmodels.EdgeSegmentLabelModel;
import com.yworks.yfiles.graph.labelmodels.EdgeSides;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.utils.Obfuscation;

/**
 * Handles the yFiles for JavaFX 2.0.x version of GraphML element
 * <code>&lt;RotatedSliderLabelModelParameter&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class RotatedSliderLabelModelParameterExtension extends AbstractSliderLabelModelParameterExtension {
  public RotatedSliderLabelModelParameterExtension() {
    super(FROM_SOURCE);
  }

  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    ILabelModel model = getModel();
    if (model instanceof RotatedSliderEdgeLabelModel) {
      RotatedSliderEdgeLabelModel rselm = (RotatedSliderEdgeLabelModel) model;
      double mDistance = rselm.getDistance();
      boolean mDrte = rselm.isDistanceRelativeToEdge();

      EdgeSides pSide = EdgeSides.ON_EDGE;
      if (mDistance > 0) {
        pSide = mDrte ? EdgeSides.RIGHT_OF_EDGE : EdgeSides.ABOVE_EDGE;
      } else if (mDistance < 0) {
        pSide = mDrte ? EdgeSides.LEFT_OF_EDGE : EdgeSides.BELOW_EDGE;
        mDistance *= -1;
      }

      EdgeSegmentLabelModel factory = new EdgeSegmentLabelModel();
      factory.setAngle(rselm.getAngle());
      factory.setAutoRotationEnabled(rselm.isAutoRotationEnabled());
      factory.setDistance(mDistance);

      int pIdx = getSegmentIndex();
      double pRatio = getSegmentRatio();
      int pLocation = getLocationAsFlag();
      if (is(pLocation, FROM_SOURCE)) {
        return factory.createParameterFromSource(pIdx, pRatio, pSide);
      } else {
        return factory.createParameterFromTarget(pIdx, pRatio, pSide);
      }
    } else {
      return new EdgeSegmentLabelModel().createDefaultParameter();
    }
  }
}

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
package viewer.graphmlcompatibility.extensions.for2x;

import com.yworks.yfiles.geometry.IOrientedRectangle;
import com.yworks.yfiles.graph.ILabel;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;

/**
 * Abstract base class for parsing discontinued legacy label models.
 * @author Thomas Behr
 */
abstract class AbstractEdgeLabelModel implements ILabelModel {
  private double angle;
  private double distance;

  AbstractEdgeLabelModel() {
    angle = 0;
    distance = 0;
  }

  public double getAngle() {
    return angle;
  }

  public void setAngle( double value ) {
    this.angle = value;
  }

  public double getDistance() {
    return distance;
  }

  public void setDistance( double value ) {
    this.distance = value;
  }



  /*
   * #####################################################################
   * ILabelModel dummy implementation
   * #####################################################################
   */

  public IOrientedRectangle getGeometry( ILabel label, ILabelModelParameter layoutParameter ) {
    throw new UnsupportedOperationException();
  }

  public ILabelModelParameter createDefaultParameter() {
    throw new UnsupportedOperationException();
  }

  public ILookup getContext( ILabel label, ILabelModelParameter layoutParameter ) {
    throw new UnsupportedOperationException();
  }

  @Override
  public <T> T lookup( final Class<T> type ) {
    throw new UnsupportedOperationException();
  }
}

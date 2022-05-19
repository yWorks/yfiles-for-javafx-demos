/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package complete.bpmn.view;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.SimpleNode;
import com.yworks.yfiles.view.IRenderContext;
import javafx.scene.Node;

class PlacedIcon implements IIcon {
  private final SimpleNode dummyNode;

  private final SimpleLabel dummyLabel;

  private final ILabelModelParameter placementParameter;

  private final IIcon innerIcon;

  public PlacedIcon( IIcon innerIcon, ILabelModelParameter placementParameter, SizeD minimumSize ) {
    this.innerIcon = innerIcon;
    this.placementParameter = placementParameter;
    dummyNode = new SimpleNode();
    dummyLabel = new SimpleLabel(dummyNode, "", placementParameter);
    dummyLabel.setPreferredSize(minimumSize);
  }

  public final Node createVisual( IRenderContext context ) {
    return innerIcon.createVisual(context);
  }

  public final Node updateVisual( IRenderContext context, Node oldVisual ) {
    return innerIcon.updateVisual(context, oldVisual);
  }

  public void setBounds( IRectangle bounds ) {
    dummyNode.setLayout(bounds);
    innerIcon.setBounds(placementParameter.getModel().getGeometry(dummyLabel, placementParameter).getBounds());
  }

  public final <TLookup> TLookup lookup( Class<TLookup> type ) {
    return (TLookup)innerIcon.lookup(type);
  }

}

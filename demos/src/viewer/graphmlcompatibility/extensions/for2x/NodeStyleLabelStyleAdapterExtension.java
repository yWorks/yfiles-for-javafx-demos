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
package viewer.graphmlcompatibility.extensions.for2x;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.NodeStyleLabelStyleAdapter;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;

/**
 * Handles the yFiles for JavaFX 2.0.x version of GraphML element
 * <code>&lt;NodeStyleLabelStyleAdapter&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class NodeStyleLabelStyleAdapterExtension extends MarkupExtension {
  private boolean autoFlipping;
  private ILabelStyle labelStyle;
  private InsetsD labelStyleInsets;
  private INodeStyle nodeStyle;

  public NodeStyleLabelStyleAdapterExtension() {
    NodeStyleLabelStyleAdapter prototype = new NodeStyleLabelStyleAdapter();
    autoFlipping = prototype.isAutoFlippingEnabled();
    labelStyle = prototype.getLabelStyle();
    labelStyleInsets = prototype.getLabelStyleInsets();
    nodeStyle = prototype.getNodeStyle();
  }

  public boolean isAutoFlipping() {
    return autoFlipping;
  }

  public void setAutoFlipping( boolean value ) {
    this.autoFlipping = value;
  }

  public ILabelStyle getLabelStyle() {
    return labelStyle;
  }

  public void setLabelStyle( ILabelStyle value ) {
    this.labelStyle = value;
  }

  public InsetsD getLabelStyleInsets() {
    return labelStyleInsets;
  }

  public void setLabelStyleInsets( InsetsD value ) {
    this.labelStyleInsets = value;
  }

  public INodeStyle getNodeStyle() {
    return nodeStyle;
  }

  public void setNodeStyle( INodeStyle value ) {
    this.nodeStyle = value;
  }

  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    NodeStyleLabelStyleAdapter style = new NodeStyleLabelStyleAdapter();
    style.setAutoFlippingEnabled(isAutoFlipping());
    ILabelStyle labelStyle = getLabelStyle();
    if (labelStyle != null) {
      style.setLabelStyle(labelStyle);
    }
    style.setLabelStyleInsets(getLabelStyleInsets());
    INodeStyle nodeStyle = getNodeStyle();
    if (nodeStyle != null) {
      style.setNodeStyle(nodeStyle);
    }
    return style;
  }
}

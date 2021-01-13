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
package com.yworks.yedlite.parts;

import org.eclipse.swt.widgets.Composite;

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.styles.CollapsibleNodeStyleDecorator;
import com.yworks.yfiles.graph.styles.INodeStyle;
import com.yworks.yfiles.graph.styles.PanelNodeStyle;

import javafx.scene.paint.Color;

/**
 * A palette view part that populates its palette with nodes with group node style {@link PanelNodeStyle}.
 */
public class GroupPalettePart extends PaletteViewPart {
  
  @SuppressWarnings("unchecked")
  @Override
  protected void populatePalette(Composite composite) {
    populateNodePalette(composite, GROUP_NODES_SELECTION_TYPE,
        new NamedStyle<INodeStyle>("GroupOrange", "Group Node", newGroupNodeStyle(Color.DARKORANGE)),
        new NamedStyle<INodeStyle>("GroupBlue", "Group Node", newGroupNodeStyle(Color.LIGHTBLUE)),
        new NamedStyle<INodeStyle>("GroupGreen", "Group Node", newGroupNodeStyle(Color.LIGHTGREEN))
    );
  }

  private static INodeStyle newGroupNodeStyle(Color color) {
    PanelNodeStyle style = new PanelNodeStyle();
    style.setColor(color);
    style.setInsets(new InsetsD(25, 5, 5, 5));
    
    //use decorator to get collapse/expand button for group nodes 
    return new CollapsibleNodeStyleDecorator(style);
  }
}

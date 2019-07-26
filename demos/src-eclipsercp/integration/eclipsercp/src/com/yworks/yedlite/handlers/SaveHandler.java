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
package com.yworks.yedlite.handlers;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.widgets.Shell;

import com.yworks.yedlite.ContextUtils;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;

/**
 * A handler for saving the current graph to a GraphML file.
 * <p>
 * {@link com.yworks.yedlite.parts.GraphEditorPart} and {@link com.yworks.yedlite.parts.GraphViewerPart} have annotated
 * <code>save</code> methods that are triggered by this handler.
 * </p>
 * @seealso com.yworks.yedlite.parts.GraphEditorPart#save(MPart, Shell)
 * @seealso com.yworks.yedlite.parts.GraphViewerPart#save(MPart, Shell)
 */
public class SaveHandler {

  @CanExecute
  public boolean canExecute(IEclipseContext ctx, MDirtyable dirty) {
    final GraphControl gc = ContextUtils.getGraphControl(ctx);
    IGraph graph = gc.getGraph();
    GraphEditorInputMode geim = (GraphEditorInputMode) gc.getInputMode();
    return gc != null && !geim.getWaitInputMode().isWaiting() && graph != null && graph.getNodes().size() > 0 && dirty.isDirty();
  }

  @Execute
  public void execute(IEclipseContext ctx, EPartService partService, Shell shell) {
    partService.savePart(partService.getActivePart(), false);
  }
}

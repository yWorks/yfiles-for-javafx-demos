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
package com.yworks.yedlite.handlers;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.MDirtyable;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.yworks.yedlite.ContextUtils;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;

/**
 * A handler that {@link GraphControl#importFromGraphML(URL) opens} a diagram saved in GraphML format.
 */
public class OpenHandler {

  @CanExecute
  public boolean canExecute(IEclipseContext ctx, MDirtyable dirty) {
    final GraphControl gc = ContextUtils.getGraphControl(ctx);
    IGraph graph = gc.getGraph();
    GraphEditorInputMode geim = (GraphEditorInputMode) gc.getInputMode();
    return gc != null && !geim.getWaitInputMode().isWaiting() && graph != null;
  }

  @Execute
  public void execute(IEclipseContext ctx, EPartService partService, Shell shell) {
    final FileDialog dialog = new FileDialog(shell, SWT.OPEN);
    dialog.setFilterExtensions(new String[] { "*.graphml" });
    String path = dialog.open();
    if (path != null) {
      File currentFile = new File(path);

      MPart part = partService.getActivePart();

      try {
        final GraphControl gc = ContextUtils.getGraphControl(ctx);

        gc.importFromGraphML(currentFile.getAbsolutePath());
        part.setLabel(currentFile.getName());
        part.setTooltip(path);
      } catch (IOException e) {
        e.printStackTrace();
      }

      ctx.set(ContextUtils.CURRENT_FILE, currentFile);

      partService.savePart(part, false);
    }
  }
}

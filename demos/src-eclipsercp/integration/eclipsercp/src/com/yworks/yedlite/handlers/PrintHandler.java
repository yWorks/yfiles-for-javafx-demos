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

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;

import com.yworks.yedlite.ContextUtils;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.view.CanvasPrinter;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;

import javafx.print.PrinterJob;

/**
 * A handler that prints the {@link GraphControl} content using a {@link PrinterJob}.
 */
public class PrintHandler {

  @CanExecute
  public boolean canExecute(IEclipseContext ctx) {
    final GraphControl gc = ContextUtils.getGraphControl(ctx);
    IGraph graph = gc.getGraph();
    GraphEditorInputMode geim = (GraphEditorInputMode) gc.getInputMode();
    return gc != null && !geim.getWaitInputMode().isWaiting() && graph != null && graph.getNodes().size() > 0;
  }

  @Execute
  public void execute(IEclipseContext ctx) {
    final GraphControl gc = ContextUtils.getGraphControl(ctx);

    final CanvasPrinter canvasPrinter = new CanvasPrinter(gc);
    final PrinterJob job = PrinterJob.createPrinterJob();

    final boolean success = canvasPrinter.print(job, true);
    if (success) {
      job.endJob();
    }
  }
}
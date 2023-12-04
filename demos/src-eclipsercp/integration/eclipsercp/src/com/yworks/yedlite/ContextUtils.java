/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.6.
 **
 ** Copyright (c) 2000-2023 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package com.yworks.yedlite;

import org.eclipse.e4.core.contexts.IEclipseContext;

import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.view.GraphControl;

/**
 * Helper class that contains methods to access the {@link GraphControl} the is
 * currently active and the {@link IGraph} that is shared between those
 * {@link GraphControl}s.
 */
public class ContextUtils {

  private ContextUtils() {}

  /**
   * The lookup key for the {@link com.yworks.yfiles.view.GraphControl}.
   */
  private static final String GRAPH_CONTROL = "com.yworks.yedlite.GRAPH_CONTROL";

  /**
   * The lookup key for the {@link com.yworks.yfiles.graph.IGraph}.
   */
  private static final String GRAPH = "com.yworks.yedlite.GRAPH";

  /**
   * The lookup key for the Graph reference counter.
   */
  private static final String GRAPH_COUNTER = "com.yworks.yedlite.GRAPH_COUNTER";

  /**
   * The lookup key for the current {@link java.io.File}.
   */
  public static final String CURRENT_FILE = "com.yworks.yedlite.CURRENT_PATH";

  /**
   * Adds the given {@link com.yworks.yfiles.view.GraphControl} to the global context.
   * If there is no {@link #getGraph(IEclipseContext) global graph}, we use
   * {@link GraphControl#getGraph()} as global graph.
   * If the given graph control is null, and no other graph control uses the
   * {@link #getGraph(IEclipseContext) global graph}, the graph will be disposed.
   */
  public static void setGraphControl(IEclipseContext ctx, GraphControl graphControl) {
    if (graphControl != null) {
      ctx.getParent().set(ContextUtils.GRAPH_CONTROL, graphControl);
      if (getGraph(ctx) == null) {
        setGraph(ctx, graphControl.getGraph());
      }
      incGraphCounter(ctx);
    } else {
      ctx.getParent().set(ContextUtils.GRAPH_CONTROL, null);
      decGraphCounter(ctx);
    }
  }

  /**
   * Returns the global {@link com.yworks.yfiles.view.GraphControl graph control}.
   */
  public static GraphControl getGraphControl(IEclipseContext ctx) {
    return (GraphControl) ctx.get(ContextUtils.GRAPH_CONTROL);
  }

  /**
   * Sets the global {@link com.yworks.yfiles.graph.IGraph graph}.
   */
  private static void setGraph(IEclipseContext ctx, IGraph graph) {
    ctx.getParent().getParent().set(ContextUtils.GRAPH, graph);
  }

  /**
   * Returns the global {@link com.yworks.yfiles.graph.IGraph graph}.
   */
  public static IGraph getGraph(IEclipseContext ctx) {
    return (IGraph) ctx.get(ContextUtils.GRAPH);
  }

  /**
   * Increments the counter indicating how often the global graph is used by
   * different graph controls.
   */
  private static void incGraphCounter(IEclipseContext ctx) {
    final Object value = ctx.get(ContextUtils.GRAPH_COUNTER);
    int count = value instanceof Integer ? ((Integer) value) + 1 : 1;
    ctx.getParent().getParent().set(ContextUtils.GRAPH_COUNTER, count);
  }

  /**
   * Decrements the counter indicating how often the global graph is used by
   * different graph controls. If the counter is zero, the global graph's undo
   * engine will be cleared and and the graph is set to null.
   */
  private static void decGraphCounter(IEclipseContext ctx) {
    final Object value = ctx.get(ContextUtils.GRAPH_COUNTER);
    if (value instanceof Integer) {
      int count = Math.max(0, ((Integer) value) - 1);
      ctx.getParent().getParent().set(ContextUtils.GRAPH_COUNTER, count);

      if (count == 0) {
        IGraph graph = getGraph(ctx);
        graph.getUndoEngine().clear();
        setGraph(ctx, null);
      }
    }
  }
}

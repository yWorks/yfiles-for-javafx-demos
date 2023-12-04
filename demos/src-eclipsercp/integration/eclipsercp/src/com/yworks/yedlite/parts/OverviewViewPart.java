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
package com.yworks.yedlite.parts;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.contexts.RunAndTrack;
import org.eclipse.e4.core.di.annotations.Optional;
import org.eclipse.e4.ui.di.UIEventTopic;
import org.eclipse.swt.widgets.Composite;

import com.yworks.yedlite.ContextUtils;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.GraphOverviewControl;

import javafx.embed.swt.FXCanvas;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;

/**
 * A view that displays the {@link GraphOverviewControl} of yFiles.
 * <p>
 * The GraphControl which is displayed is provided by the EditorViewPart and injected using eclipse event framework.
 * </p>
 */
public class OverviewViewPart {
  @Inject
  IEclipseContext ctx;

  FXCanvas canvas;

  /**
   * The control to display.
   */
  private GraphOverviewControl overview;

  /**
   * Backup field for the parent of this ViewPart to use in the event handling process.
   */
  private Composite parent;

  @PostConstruct
  public void initPart(Composite parent) {
    canvas = new FXCanvas(parent, 0);
    canvas.setScene(createFxScene());
  }

  private Scene createFxScene() {
    overview = new GraphOverviewControl();

    ctx.getParent().runAndTrack(new RunAndTrack() {
      @Override
      public boolean changed(IEclipseContext context) {
        GraphControl graphControl = ContextUtils.getGraphControl(context);

        overview.setGraphControl(graphControl);
        return true;
      }
    });

    return new Scene(new BorderPane(overview));
  }

  /**
   * Listen to the graphcontrol event
   * @param data currently null
   */
  @Inject
  @Optional
  private void listenForGraphViewerEvents(@UIEventTopic(GraphViewerPart.GRAPHCONTROL_EVENT) Object data) {
    overview.updateContentRect();
    overview.fitContent();
  }
}

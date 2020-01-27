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
package com.yworks.yedlite.handlers;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import com.yworks.yedlite.ContextUtils;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.PixelImageExporter;
import com.yworks.yfiles.view.input.GraphEditorInputMode;

import javafx.embed.swt.SWTFXUtils;
import javafx.scene.image.WritableImage;

/**
 * A handler for exporting the diagram in the {@link CanvasControl#getContentRect() content rectangle}
 * to a bitmap image.
 */
public class ExportHandler {
  private static final String[] IMAGE_FILTER_EXTENSIONS = {"*.jpg", "*.jpeg", "*.jpe", "*.png", "*.bmp"};

  @CanExecute
  public boolean canExecute(IEclipseContext ctx) {
    // check if no input mode is currently busy and if the graph has at least one node
    final GraphControl gc = ContextUtils.getGraphControl(ctx);
    IGraph graph = gc.getGraph();
    GraphEditorInputMode geim = (GraphEditorInputMode) gc.getInputMode();
    return gc != null && !geim.getWaitInputMode().isWaiting() && graph != null && graph.getNodes().size() > 0;
  }

  @Execute
  public void execute(IEclipseContext ctx, Shell shell) {
    final GraphControl gc = ContextUtils.getGraphControl(ctx);

    gc.updateContentRect(new InsetsD(5, 5, 5, 5));
    final FileDialog dialog = new FileDialog(shell, SWT.SAVE);

    dialog.setFilterExtensions(IMAGE_FILTER_EXTENSIONS);

    String path = dialog.open();

    if (path != null) {
      exportToBitmap(gc, path);
    }
  }

  private static void exportToBitmap(CanvasControl canvas, String file) {
    final String extension = file.substring(file.lastIndexOf("."));
    final int format = getFormat(extension);

    final PixelImageExporter exporter = new PixelImageExporter(canvas.getContentRect());
    final WritableImage image = exporter.exportToBitmap(canvas);
    final ImageData imageData = SWTFXUtils.fromFXImage(image, null);

    final ImageLoader loader = new ImageLoader();
    loader.data = new ImageData[] { imageData };
    loader.save(file, format);
  }

  private static int getFormat(String extension) {
    switch (extension) {
    default:
    case "jpg":
    case "jpeg":
    case "jpe":
      return SWT.IMAGE_JPEG;
    case "png":
      return SWT.IMAGE_PNG;
    case "bmp":
      return SWT.IMAGE_BMP;
    }
  }
}

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
package toolkit;

import com.yworks.yfiles.view.input.ICommand;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides some often used icons
 */
public class IconProvider {

  private static IconProvider INSTANCE = new IconProvider();

  public static Image NEW = new Image(getResource("new-document-16.png"));
  public static Image COPY = new Image(getResource("copy-16.png"));
  public static Image CUT = new Image(getResource("cut-16.png"));
  public static Image PASTE = new Image(getResource("paste-16.png"));
  public static Image DELETE = new Image(getResource("delete3-16.png"));
  public static Image EXPORT_IMAGE = new Image(getResource("export-image-16.png"));
  public static Image RELOAD = new Image(getResource("reload-16.png"));
  public static Image GRID = new Image(getResource("grid-16.png"));
  public static Image GROUP = new Image(getResource("group-16.png"));
  public static Image OPEN = new Image(getResource("open-16.png"));
  public static Image ORTHOGONAL_EDITING = new Image(getResource("orthogonal-editing-16.png"));
  public static Image PRINT = new Image(getResource("print-16.png"));
  public static Image REDO = new Image(getResource("redo-16.png"));
  public static Image UNDO = new Image(getResource("undo-16.png"));
  public static Image SAVE = new Image(getResource("save-16.png"));
  public static Image SNAPPING = new Image(getResource("snap-16.png"));
  public static Image UNGROUP = new Image(getResource("ungroup-16.png"));
  public static Image ZOOM_FIT = new Image(getResource("fit2-16.png"));
  public static Image ZOOM_IN = new Image(getResource("plus2-16.png"));
  public static Image ZOOM_OUT = new Image(getResource("minus2-16.png"));
  public static Image ZOOM_RESET = new Image(getResource("zoom-original2-16.png"));
  public static Image LAYOUT_HIERARCHIC = new Image(getResource("layout-hierarchic.png"));
  public static Image LAYOUT_ORGANIC = new Image(getResource("layout-organic-16.png"));
  public static Image LAYOUT_ORTHOGONAL = new Image(getResource("layout-orthogonal-16.png"));
  public static Image LAYOUT_TREE = new Image(getResource("layout-tree-16.png"));
  public static Image NEXT = new Image(getResource("rightarrow.png"));
  public static Image PREVIOUS = new Image(getResource("leftarrow.png"));
  public static Image UP = new Image(getResource("uparrow.png"));
  public static Image DOWN = new Image(getResource("downarrow.png"));
  public static Image PLUS = new Image(getResource("plus-16.png"));
  public static Image MINUS = new Image(getResource("minus-16.png"));
  public static Image LASSO = new Image(getResource("lasso.png"));
  public static Image RAISE = new Image(getResource("z-order-up-16.png"));
  public static Image LOWER = new Image(getResource("z-order-down-16.png"));
  public static Image TO_FRONT = new Image(getResource("z-order-top-16.png"));
  public static Image TO_BACK = new Image(getResource("z-order-bottom-16.png"));
  public static Image EDGE_LABEL = new Image(getResource("edgelabel-16.png"));

  private static Map<String, Image> names = new HashMap<>();
  static {
    names.put(ICommand.NEW.getName(), NEW);
    names.put(ICommand.COPY.getName(), COPY);
    names.put(ICommand.CUT.getName(), CUT);
    names.put(ICommand.PASTE.getName(), PASTE);
    names.put(ICommand.DELETE.getName(), DELETE);
    names.put("RELOAD", RELOAD);
    names.put("EXPORT_IMAGE", EXPORT_IMAGE);
    names.put("GRID", GRID);
    names.put(ICommand.GROUP_SELECTION.getName(), GROUP);
    names.put(ICommand.OPEN.getName(), OPEN);
    names.put("ORTHOGONAL_EDITING", ORTHOGONAL_EDITING);
    names.put(ICommand.PRINT.getName(), PRINT);
    names.put(ICommand.REDO.getName(), REDO);
    names.put(ICommand.UNDO.getName(), UNDO);
    names.put(ICommand.SAVE.getName(), SAVE);
    names.put("SNAPPING", SNAPPING);
    names.put(ICommand.UNGROUP_SELECTION.getName(), UNGROUP);
    names.put("ZOOM_FIT", ZOOM_FIT);
    names.put("ZOOM_RESET", ZOOM_RESET);
    names.put(ICommand.INCREASE_ZOOM.getName(), ZOOM_IN);
    names.put(ICommand.DECREASE_ZOOM.getName(), ZOOM_OUT);
    names.put("LAYOUT_HIERARCHIC", LAYOUT_HIERARCHIC);
    names.put("LAYOUT_ORGANIC", LAYOUT_ORGANIC);
    names.put("LAYOUT_ORTHOGONAL", LAYOUT_ORTHOGONAL);
    names.put("LAYOUT_TREE", LAYOUT_TREE);
    names.put("NEXT", NEXT);
    names.put("PREVIOUS", PREVIOUS);
    names.put("UP", UP);
    names.put("DOWN", DOWN);
    names.put("PLUS", PLUS);
    names.put("MINUS", MINUS);
    names.put(ICommand.FIT_GRAPH_BOUNDS.getName(), ZOOM_FIT);
    names.put("LASSO", LASSO);
    names.put(ICommand.RAISE.getName(), RAISE);
    names.put(ICommand.LOWER.getName(), LOWER);
    names.put(ICommand.TO_FRONT.getName(), TO_FRONT);
    names.put(ICommand.TO_BACK.getName(), TO_BACK);
    names.put("EDGE_LABEL", EDGE_LABEL);
  }
  
  private static InputStream getResource(String name) {
    return INSTANCE.getClass().getResourceAsStream("/resources/" + name);
  }

  public static ImageView valueOf(String name){
    Image img = names.get(name);
    if (img == null){
      throw new IllegalArgumentException("Icon not found: "+name);
    }
    return new ImageView(img);
  }
}

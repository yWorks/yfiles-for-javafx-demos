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
package toolkit;

import javafx.scene.control.Tooltip;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides some often used tooltips.
 */
public class TooltipProvider {
  private static Map<String, String> names = new HashMap<>();

  static {
    names.put("EXPORT_IMAGE", "Export to image");
    names.put("GRID", "Grid");
    names.put("ORTHOGONAL_EDITING", "Orthogonal edges");
    names.put("SNAPPING", "Snapping");
    names.put("ZOOM_FIT", "Fit content");
    names.put("LAYOUT_HIERARCHIC", "Apply hierarchic layout");
    names.put("LAYOUT_ORGANIC", "Apply organic layout");
    names.put("LAYOUT_ORTHOGONAL", "Apply orthogonal layout");
    names.put("LAYOUT", "Apply layout");
    names.put("NEXT", "Next");
    names.put("PREVIOUS", "Previous");
    names.put("ZOOM_RESET", "Zoom 1:1");
    names.put("LASSO", "Enable Lasso Selection");
  }

  public static Tooltip valueOf(String name){
    String text = names.get(name);
    if (text == null){
      throw new IllegalArgumentException("Tooltip text not found: "+ name);
    }
    return new Tooltip(text);
  }
}

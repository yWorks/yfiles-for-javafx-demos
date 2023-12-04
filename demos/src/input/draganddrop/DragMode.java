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
package input.draganddrop;

import com.yworks.yfiles.view.input.LabelDropInputMode;
import com.yworks.yfiles.view.input.NodeDropInputMode;
import com.yworks.yfiles.view.input.PortDropInputMode;

/**
 * An enum for snapping and preview configurations for drag operations.
 */
public enum DragMode {
  SNAPPING_AND_PREVIEW("Snapping & Preview", true, true),
  PREVIEW("Preview", false, true),
  NONE("None", false, false);

  private final String description;
  private final boolean snapping;
  private final boolean preview;

  DragMode( String description, boolean snapping, boolean preview ) {
    this.description = description;
    this.snapping = snapping;
    this.preview = preview;
  }

  public void apply( NodeDropInputMode ndim, LabelDropInputMode ldim, PortDropInputMode pdim ) {
    ndim.setSnappingEnabled(snapping);
    ndim.setPreviewEnabled(preview);
    ldim.setPreviewEnabled(preview);
    pdim.setSnappingEnabled(snapping);
    pdim.setPreviewEnabled(preview);
  }

  @Override
  public String toString() {
    return description;
  }
}

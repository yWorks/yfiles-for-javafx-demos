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
package com.yworks.yedlite.dragdrop;

import java.util.HashMap;
import java.util.Map;

import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.INode;

/**
 * Stores node templates to be dropped into a graph control.
 * Additionally, this class stores the current transfer data for SWT-to-JavaFX
 * drag and drop operations. This is done because the transfer data in the JavaFX
 * native drag events is not available before drop but
 * {@link com.yworks.yedlite.dragdrop.SwtNodeDropInputMode} need to be able to access
 * the transfer data on drag enter for determining which mode needs to handle
 * (and consume) the drag events.
 * The drag data is provided in text format: we convert an
 * enumeration constant to text when dragging starts and convert the text back
 * to the enumeration constant when the node is dropped.
 */
public final class DragAndDropDataManager {
  /**
   * Singleton instance for easy access to current transfer data as well as
   * the available edge and node templates for drop operations.
   */
  public static final DragAndDropDataManager INSTANCE = new DragAndDropDataManager();

  private final Map<String, Object> dropDataMap;
  private String currentId;

  /**
   * Initializes a new <code>DragAndDropDataManager</code> instance.
   */
  DragAndDropDataManager() {
    dropDataMap = new HashMap<String, Object>();
  }

  /**
   * Returns the edge or node template that is associated to the given
   * identifier.
   */
  public Object get(String id) {
    return dropDataMap.get(id);
  }

  /**
   * Associates node template to a unique identifier.
   * @param id the unique identifier for the node template.
   * @param template the node template.
   */
  public void put(String id, INode template) {
    dropDataMap.put(id, template);
  }
  
  /**
   * Associates edge template to a unique identifier.
   * @param id the unique identifier for the edge template.
   * @param template the edge template.
   */
  public void put(String id, IEdge template) {
    dropDataMap.put(id, template);
  }

  /**
   * Returns the current transfer data for SWT-to-JavaFX drag and drop
   * operations. This data is used as an identifier for an edge or node
   * template.
   */
  String getCurrentId() {
    return currentId;
  }

  /**
   * Sets the current transfer data for SWT-to-JavaFX drag and drop
   * operations. This data will be used as an identifier for an edge or node
   * template.
   */
  public void setCurrentId(String id) {
    this.currentId = id;
  }
}

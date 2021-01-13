/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package com.yworks.yedlite.popup;

import com.yworks.yfiles.graph.IEdge;
import com.yworks.yfiles.graph.IModelItem;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.view.input.PopulateItemContextMenuEventArgs;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuSeparator;
import org.eclipse.e4.ui.model.application.ui.menu.MPopupMenu;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

import java.util.List;

/**
 * Factory class that populates a context menu for nodes and edges.
 */
public class ContextMenuFactory {

  private final MPart part;
  private final String id;

  /**
   * Creates a new Factory for context menus.
   * @param part The model object of the Editor.
   * @param id The id of the registered context menu.
   */
  public ContextMenuFactory(MPart part, String id) {
    this.part = part;
    this.id = id;
  }
  
  /**
    * Populates the context menu.
    */
   public void populateContextMenu(PopulateItemContextMenuEventArgs<IModelItem> args) {
     String menuType = null;
     if (args.getItem() instanceof INode) {
       menuType = "Node";
     } else if (args.getItem() instanceof IEdge) {
       menuType = "Edge";
     } else {
       menuType = "Paper";
     }
 
     Menu contextMenu = (Menu) args.getMenu();

     // find the predefined popup menu with the correct id and populate the context menu with it
     for (MMenu mMenu : part.getMenus()) {
       if (id.equals(mMenu.getElementId()) && mMenu instanceof MPopupMenu) {
         populateContextMenuImpl(contextMenu, mMenu, menuType);
       }
     }
 
     args.setHandled(true);
   }
 
   private static void populateContextMenuImpl(Menu contextMenu, MMenu mMenu, String menuType) {
    boolean separator = false;
     for (MMenuElement mChild : mMenu.getChildren()) {
       final Object widget = mChild.getWidget();
       if (widget instanceof MenuItem) {
         if (!isValid(mChild, menuType)) {
           continue;
         }

        if (separator) {
          separator = false;
          new MenuItem(contextMenu, SWT.SEPARATOR);
        }

        final MenuItem prototype = (MenuItem) widget;
         MenuItem item = new MenuItem(contextMenu, prototype.getStyle());
 
         item.setText(prototype.getText());
         item.setImage(prototype.getImage());
 
         for (Listener listener : prototype.getListeners(SWT.Selection)) {
           item.addListener(SWT.Selection, listener);
         }
       } else if (mChild instanceof MMenuSeparator) {
        separator = true;
       }
     }
   }
 
   private static boolean isValid(MMenuElement element, String menuType) {
     final List<String> tags = element.getTags();
     return tags == null || tags.isEmpty() || tags.contains(menuType);
   }
  
}

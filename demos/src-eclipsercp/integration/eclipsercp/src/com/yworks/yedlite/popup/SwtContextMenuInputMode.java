/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.3.
 **
 ** Copyright (c) 2000-2019 by yWorks GmbH, Vor dem Kreuzberg 28,
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

import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.AbstractContextMenuInputMode;
import javafx.geometry.Point2D;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuAdapter;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * An implementation of {@link AbstractContextMenuInputMode} interface that will
 * display a {@link Menu SWT context menu} when the user right clicks on the {@link
 * com.yworks.yfiles.view.CanvasControl} or presses the menu key.
 * <p>
 * Note: the SWT context menu must be accessed and changed in the SWT event thread.
 */
public class SwtContextMenuInputMode extends AbstractContextMenuInputMode<Menu> {
  
  // the control in whose space the context menu is to appear
  private final Control parent;
  
  private final GraphControl gc;
  
  // the handler that invokes a callback as soon as the menu is closed
  private MenuAdapter closedHandler;

  /**
   * Initializes a new <code>SwtContextMenuInputMode</code> instance with a control in that the context menu is to
   * appear.
   * @param parent the control in whose space the context menu is to appear
   */
  public SwtContextMenuInputMode(Control parent, GraphControl gc) {
    this.parent = parent;
    this.gc = gc;
  }

  @Override
  protected Menu createMenu() {
    return new Menu(parent.getShell(), SWT.POP_UP);
  }

  @Override
  protected void showMenu(Menu menu, PointD viewLocation) {
    Point2D point = gc.localToScreen(viewLocation.x, viewLocation.y);
    menu.setLocation((int) point.getX(), (int) point.getY());
    menu.setVisible(true);
  }

  @Override
  protected void hideMenu(Menu menu) {
    if (menu != null && !menu.isDisposed()) {
      menu.setVisible(false);
    }
  }

  @Override
  protected void clearMenu(Menu menu) {
    for (MenuItem item : menu.getItems()) {
      item.dispose();
    }
  }

  @Override
  protected boolean isMenuEmpty(Menu menu) {
    return menu.getItemCount() == 0;
  }

  @Override
  protected void setMenuClosedCallback(Menu menu, Runnable menuClosedCallback) {
    // remove the old listener
    if (closedHandler != null) {
      menu.removeMenuListener(closedHandler);
      closedHandler = null;
    }

    // add a new one
    if (menuClosedCallback != null) {
      closedHandler = new MenuAdapter() {
        @Override
        public void menuHidden(MenuEvent e) {
          menuClosedCallback.run();
        }
      };
      menu.addMenuListener(closedHandler);
    }
  }
}

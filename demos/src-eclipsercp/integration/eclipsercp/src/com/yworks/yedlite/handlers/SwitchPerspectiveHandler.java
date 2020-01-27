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
import java.util.List;

import javax.inject.Named;

import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.CanExecute;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.advanced.MPerspective;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimBar;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimElement;
import org.eclipse.e4.ui.model.application.ui.basic.MTrimmedWindow;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBar;
import org.eclipse.e4.ui.model.application.ui.menu.MToolBarElement;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;

/**
 * A handler for switching between the editor and the viewer perspective based on a command parameter.
 */
public class SwitchPerspectiveHandler {

  @Execute
  public void switchPerspective(MWindow window, EPartService partService, EModelService modelService, @Named("perspective_parameter") String perspectiveId) {
    // use parameter to find perspectives
    List<MPerspective> perspectives = modelService.findElements(window, perspectiveId, MPerspective.class, null);

    // change menu for perspective
    MMenu mainMenu = window.getMainMenu();
    for (MMenuElement item : mainMenu.getChildren()) {
      setMaybeInvisible(item, perspectiveId);
    }

    // change toolbar for perspective
    if (window instanceof MTrimmedWindow) {
      for (MTrimBar mTrimBar : ((MTrimmedWindow) window).getTrimBars()) {
        for (MTrimElement mTrimElement : mTrimBar.getChildren()) {
          if (mTrimElement instanceof MToolBar) {
            for (MToolBarElement tElement : ((MToolBar) mTrimElement).getChildren()) {
              setMaybeInvisible(tElement, perspectiveId);
            }
          }
        }
      }
    }

    // switch to perspective with the ID if found
    if (!perspectives.isEmpty()) {
      partService.switchPerspective(perspectives.get(0));
    }
  }

  private void setMaybeInvisible(MMenuElement item, String perspectiveId) {
    if (item.getTags().size() > 0) {
      String tag = item.getTags().get(0);
      if (tag.equals("shared")) {
        item.setVisible(true);
        if (item instanceof MMenu) {
          MMenu menu = (MMenu) item;
          for (MMenuElement subitem : menu.getChildren()) {
            setMaybeInvisible(subitem, perspectiveId);
          }
        }
      } else {
        item.setVisible(tag.equals(perspectiveId));
      }
    } else {
      item.setVisible(false);
    }
  }

  private void setMaybeInvisible(MToolBarElement item, String perspectiveId) {
    if (item.getTags().size() > 0) {
      String tag = item.getTags().get(0);
      if (tag.equals("shared")) {
        item.setVisible(true);
      } else {
        item.setVisible(tag.equals(perspectiveId));
      }
    } else {
      item.setVisible(false);
    }
  }

  @CanExecute
  public boolean canExecute(IEclipseContext ctx) {
    return true;
  }

}

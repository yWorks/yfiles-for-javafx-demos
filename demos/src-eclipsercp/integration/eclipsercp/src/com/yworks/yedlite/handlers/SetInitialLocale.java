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
package com.yworks.yedlite.handlers;

import org.eclipse.e4.core.services.translation.TranslationService;
import org.eclipse.e4.ui.di.AboutToShow;
import org.eclipse.e4.ui.model.application.ui.MContext;
import org.eclipse.e4.ui.model.application.ui.basic.MWindow;
import org.eclipse.e4.ui.model.application.ui.menu.MMenu;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuElement;
import org.eclipse.e4.ui.model.application.ui.menu.MMenuItem;
import org.eclipse.e4.ui.workbench.modeling.EModelService;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/**
 * Initializes the <em>selected</em> state of the <em>language</em> menu item
 * corresponding to the initially set translation service locale.
 */
public class SetInitialLocale {
  boolean done;

  @AboutToShow
  public void setInitialLocale(EModelService modelService, MContext mContext) {
    if (done) {
      return;
    }

    done = true;

    if (mContext instanceof MWindow) {
      // there does not seem to be a way to inject the menu for which
      // this contribution has been registered nor a way to inject specific
      // parameter, thus the retrieving the required menu has to be done
      // "manually" with a hard-coded menu ID

      // find the menu that contains the menu items for changing application
      // language/locale
      List<MMenu> menus = modelService.findElements(
              (MWindow) mContext,
              "yed-lite-eclipse-rcp.menu.changelocale",
              MMenu.class,
              Collections.EMPTY_LIST,
              EModelService.IN_MAIN_MENU);
      if (!menus.isEmpty()) {
        Object candidate = mContext.getContext().get(TranslationService.LOCALE);

        MMenu menu = menus.get(0);
        if (candidate instanceof Locale) {
          Locale locale = (Locale) candidate;
          if (is(locale, Locale.ENGLISH)) {
            setSelected(menu, ".englishlocale", true);
          } else if (is(locale, Locale.GERMAN)) {
            setSelected(menu, ".germanlocale", true);
          }
        }
      }
    }
  }

  /**
   * Determines if the given candidate's language matches the given language.
   */
  private static boolean is( Locale candidate, Locale language ) {
    return language.getLanguage().equals(candidate.getLanguage());
  }

  /**
   * Sets the selected state of the item identified by the given item ID.
   */
  private static void setSelected(
          MMenu menu, String itemIdSuffix, boolean selected
  ) {
    for (Iterator<MMenuElement> it = menu.getChildren().iterator(); it.hasNext();) {
      MMenuElement item = it.next();
      if (item instanceof MMenuItem &&
          item.getElementId().endsWith(itemIdSuffix)) {
        ((MMenuItem) item).setSelected(true);
        return;
      }
    }
  }
}

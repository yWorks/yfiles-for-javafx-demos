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
package toolkit;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.dom.html.HTMLAnchorElement;

import java.io.IOException;
import java.net.URL;

/**
 * Utility methods for working with {@link WebView} controls.
 */
public class WebViewUtils {
  private WebViewUtils() {
  }


  /**
   * Displays the application's help text in the given web view.
   * Additionally, redirects the given web view's hyperlink handling to the
   * application host services'
   * {@link HostServices#showDocument(String) showDocument} feature.
   */
  public static void initHelp( WebView view, Application application ) {
    WebEngine engine = view.getEngine();
    URL helpFile = application.getClass().getResource("help.html");
    if (helpFile == null) {
      helpFile = application.getClass().getResource("resources/help.html");
    }
    if (helpFile != null) {
      engine.load(helpFile.toExternalForm());
    } else {
      engine.loadContent("Could not resolve help text. Please ensure that your build process or IDE adds the " +
          "help.html file to the class path.", "text/plain");
    }
    engine.getLoadWorker().stateProperty().addListener(
            new RegisterHyperlinkHandler(engine, application.getHostServices()));
  }


  private static final class RegisterHyperlinkHandler
          implements ChangeListener<Worker.State> {
    final WebEngine engine;
    final HostServices services;

    RegisterHyperlinkHandler( WebEngine engine, HostServices services ) {
      this.engine = engine;
      this.services = services;
    }

    @Override
    public void changed(
            ObservableValue<? extends Worker.State> observable,
            Worker.State oldValue,
            Worker.State newValue
    ) {
      if (Worker.State.SUCCEEDED == newValue) {
        Document document = engine.getDocument();
        NodeList anchors = document.getElementsByTagName("a");
        HyperlinkHandler handler = new HyperlinkHandler(services);
        for (int i = 0, n = anchors.getLength(); i < n; ++i) {
          ((EventTarget) anchors.item(i)).addEventListener("click", handler, false);
        }
      }
    }
  }

  private static final class HyperlinkHandler implements EventListener {
    final HostServices services;

    HyperlinkHandler( HostServices services ) {
      this.services = services;
    }

    @Override
    public void handleEvent( Event e ) {
      String href = ((HTMLAnchorElement) e.getCurrentTarget()).getHref();
      services.showDocument(href);

      e.preventDefault();
    }
  }
}

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
package viewer.graphviewer;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

/**
 * Helper to start the default browser and open a {@link URI}.
 */
abstract class BrowserLauncher {

  private BrowserLauncher() {
  }

  abstract boolean canLaunch();

  abstract void openLink(URI uri);

  static BrowserLauncher create() {
    String os = System.getProperty("os.name").toLowerCase();
    if (os.contains("win")) {
      return new WindowsBrowserLauncher();
    } else if (os.contains("mac")) {
      return new MacBrowserLauncher();
    } else if (os.contains("nix") || os.contains("nux")) {
      return new LinuxBrowserLauncher();
    } else {
      return null;
    }
  }

  private static class WindowsBrowserLauncher extends BrowserLauncher {
    @Override
    boolean canLaunch() {
      return true;
    }

    @Override
    void openLink(URI uri) {
      try {
        Desktop.getDesktop().browse(uri);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static class MacBrowserLauncher extends BrowserLauncher {
    @Override
    boolean canLaunch() {
      return true;
    }

    @Override
    void openLink(URI uri) {
      Runtime rt = Runtime.getRuntime();
      try {
        rt.exec("open " + uri.toASCIIString());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private static class LinuxBrowserLauncher extends BrowserLauncher {
    Runtime runtime;
    String browser;

    LinuxBrowserLauncher() {
      runtime = Runtime.getRuntime();
      browser = findBrowser(runtime);
    }

    @Override
    boolean canLaunch() {
      return browser != null;
    }

    @Override
    void openLink(URI uri) {
      try {
        runtime.exec(new String[]{browser, uri.toASCIIString()});
      } catch (IOException ioe) {
        ioe.printStackTrace();
      }
    }
  }

  private static String findBrowser(final Runtime runtime) {
    final String[] browsers = {
        "xdg-open",
        "gnome-open",
        "kde-open",
        "gnome-www-browser",
        "x-www-browser",
        "firefox",
        "google-chrome",
        "opera",
        "konqueror",
        "epiphany",
        "mozilla",
        "netscape",
    };
    try {
      for (String actualBrowser : browsers) {
        if (runtime.exec(new String[]{"which", actualBrowser}).waitFor() == 0) {
          return actualBrowser;
        }
      }
    } catch (InterruptedException | IOException e) {
      // handle as "no browser found" - i.e. ignore and return null below
    }
    return null;
  }
}

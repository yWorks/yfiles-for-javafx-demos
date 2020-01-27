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
package integration.swing;

import java.awt.EventQueue;
import java.lang.reflect.InvocationTargetException;

/**
 * On MacOSX events of JavaFX application thread and EDT are dispatched and 
 * processed on the AppKit thread. Thereby a {@link java.awt.EventQueue#invokeAndWait} 
 * can lead to a deadlock (see <a href="https://javafx-jira.kenai.com/browse/RT-31124">RT-31124</a>). 
 * To avoid this, we invoke all methods that access the context menu on the 
 * JavaFX application thread on MaxOSX. This issue might be fixed in JDK 9.
 */
class ThreadUtils {
  static final boolean macOSX = isMacOSX();
  static final boolean windows = isWindows();

  private static boolean isMacOSX() {
    String os = System.getProperty("os.name");
    return os.toLowerCase().startsWith("mac");
  }

  private static boolean isWindows() {
    String os = System.getProperty("os.name");
    return os.toLowerCase().startsWith("windows");
  }

  private ThreadUtils() {
  }

  static void invokeAndWait(Runnable r) {
    if (macOSX || EventQueue.isDispatchThread()) {
      r.run();
    } else {
      try {
        EventQueue.invokeAndWait(r);
      } catch (InterruptedException | InvocationTargetException e) {
        throw new RuntimeException(e);
      }
    }
  }

  static void invokeLater(Runnable r) {
    if (macOSX) {
      r.run();
    } else {
      EventQueue.invokeLater(r);
    }    
  }
}

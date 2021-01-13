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
package integration.swing;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * Paints {@link NodeTemplate} instances in a {@link javax.swing.JList}.
 */
class NodeTemplateCellRenderer implements javax.swing.ListCellRenderer<NodeTemplate> {
  // renders the list cell
  private DefaultListCellRenderer renderer;
  // holds an icon for each node
  private WeakHashMap<NodeTemplate, Icon> template2icon;

  NodeTemplateCellRenderer() {
    renderer = new DefaultListCellRenderer();
    template2icon = new WeakHashMap<>();
  }

  @Override
  public Component getListCellRendererComponent(JList<? extends NodeTemplate> list, NodeTemplate value, int index,
                                                boolean isSelected, boolean cellHasFocus) {
    // we use a label as component that renders the list cell and sets the icon that paints the given NodeTemplate
    JLabel label = (JLabel) renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
    label.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
    label.setHorizontalAlignment(SwingConstants.LEFT);
    label.setIcon(getIcon(value));
    label.setText(value.description());
    return label;
  }

  /**
   * Returns an {@link javax.swing.Icon} painting the given NodeTemplate.
   */
  private Icon getIcon(NodeTemplate nodeTemplate) {
    Icon icon = template2icon.get(nodeTemplate);
    if (icon == null) {
      icon = new NodeTemplateIcon(nodeTemplate);
      template2icon.put(nodeTemplate, icon);
    }
    return icon;
  }

  /**
   * An {@link javax.swing.Icon} that paints a {@link integration.swing.NodeTemplate}.
   */
  private static class NodeTemplateIcon implements Icon {
    private static final BufferedImage EMPTY_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);

    private BufferedImage image;

    NodeTemplateIcon(NodeTemplate nodeTemplate) {
      // create the JavaFX image on the JavaFX application thread
      FutureTask<Image> fxImageQuery = new FutureTask<>(nodeTemplate::createImage);
      Platform.runLater(fxImageQuery);
      try {
        // convert the the JavaFX image in a Swing image
        image = SwingFXUtils.fromFXImage(fxImageQuery.get(), null);
      } catch (InterruptedException | ExecutionException e) {
        image = EMPTY_IMAGE;
      }
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.drawImage(image, x, y, null);
    }

    @Override
    public int getIconWidth() {
      return image.getWidth();
    }

    @Override
    public int getIconHeight() {
      return image.getHeight();
    }
  }
}

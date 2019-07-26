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
package complete.orgchart;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Cache for the images of the persons so they get loaded only once.
 * Provides a valueOf(String) method that makes the provider accessible
 * in FXML via
 * <p>
 *   <code><xmp><ImageProvider fx:value="man1"/></xmp></code>
 * </p>
 */
public class ImageProvider {

  private static ImageProvider INSTANCE = new ImageProvider();

  private static Map<String, Image> images = new HashMap<>();

  private static Image WOMAN1 = new Image(INSTANCE.getClass().getResourceAsStream("resources/woman1.png"));
  private static Image WOMAN1_16 = new Image(INSTANCE.getClass().getResourceAsStream("resources/usericon_female1_16.png"));
  private static Image WOMAN2 = new Image(INSTANCE.getClass().getResourceAsStream("resources/woman2.png"));
  private static Image WOMAN3 = new Image(INSTANCE.getClass().getResourceAsStream("resources/woman3.png"));
  private static Image MAN1 = new Image(INSTANCE.getClass().getResourceAsStream("resources/man1.png"));
  private static Image MAN2 = new Image(INSTANCE.getClass().getResourceAsStream("resources/man2.png"));
  private static Image MAN3 = new Image(INSTANCE.getClass().getResourceAsStream("resources/man3.png"));

  static {
    images.put("woman1", WOMAN1);
    images.put("woman2", WOMAN2);
    images.put("woman3", WOMAN3);
    images.put("woman1_16", WOMAN1_16);
    images.put("man1", MAN1);
    images.put("man2", MAN2);
    images.put("man3", MAN3);
  }

  public static Image valueOf(String name){
    Image img = images.get(name);
    if (img == null){
      throw new IllegalArgumentException("Image not found: "+name);
    }
    return img;
  }
}

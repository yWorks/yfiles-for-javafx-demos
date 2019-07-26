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
package viewer.graphmlcompatibility.extensions.for2x;

import com.yworks.yfiles.graph.labelmodels.ILabelModel;
import com.yworks.yfiles.graphml.MarkupExtension;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * Abstract base class for parsing parameters of discontinued legacy label
 * models. 
 * @author Thomas Behr
 */
abstract class AbstractSliderLabelModelParameterExtension extends MarkupExtension {
  static final int LEFT = 1;
  static final int RIGHT = 2;
  static final int FROM_SOURCE = 4;
  static final int FROM_TARGET = 8;
  static final int CENTER = 16;

  private String location;
  private ILabelModel model;
  private int segmentIndex;
  private double segmentRatio;

  AbstractSliderLabelModelParameterExtension( int location ) {
    this.location = asString(location);
    this.segmentIndex = 0;
    this.segmentRatio = 0.5;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation( String value ) {
    this.location = value;
  }

  public ILabelModel getModel() {
    return model;
  }

  public void setModel( ILabelModel value ) {
    this.model = value;
  }

  public int getSegmentIndex() {
    return segmentIndex;
  }

  public void setSegmentIndex( int value ) {
    this.segmentIndex = value;
  }

  public double getSegmentRatio() {
    return segmentRatio;
  }

  public void setSegmentRatio( double value ) {
    this.segmentRatio = value;
  }


  int getLocationAsFlag() {
    return asFlag(getLocation());
  }


  private static String asString( int location ) {
    String del = "";
    StringBuilder sb = new StringBuilder();
    final int value = LEFT;
    if (is(location, LEFT)) {
      sb.append(del).append("Left");
      del = ", ";
    }
    if (is(location, RIGHT)) {
      sb.append(del).append("Right");
      del = ", ";
    }
    if (is(location, FROM_SOURCE)) {
      sb.append(del).append("FromSource");
      del = ", ";
    }
    if (is(location, FROM_TARGET)) {
      sb.append(del).append("FromTarget");
      del = ", ";
    }
    if (is(location, CENTER)) {
      sb.append(del).append("FromTarget");
      del = ", ";
    }
    return sb.toString();
  }

  static boolean is( final int location, final int value ) {
    return (location & value) == value;
  }

  private static int asFlag( final String location ) {
    HashMap<String, Integer> map = new HashMap<>();
    map.put(normalize(asString(LEFT)), Integer.valueOf(LEFT));
    map.put(normalize(asString(RIGHT)), Integer.valueOf(RIGHT));
    map.put(normalize(asString(FROM_SOURCE)), Integer.valueOf(FROM_SOURCE));
    map.put(normalize(asString(FROM_TARGET)), Integer.valueOf(FROM_TARGET));
    map.put(normalize(asString(CENTER)), Integer.valueOf(CENTER));

    int flag = 0;
    for (StringTokenizer st = new StringTokenizer(location, ","); st.hasMoreTokens();) {
      Integer value = map.get(normalize(st.nextToken()));
      if (value != null) {
        flag |= value.intValue();
      }
    }
    return flag;
  }

  private static String normalize( String location ) {
    if (location == null) {
      return "";
    } else {
      String s = location.trim().toLowerCase();
      if (s.indexOf('_') > -1) {
        return s.replaceAll("_", "");
      } else {
        return s;
      }
    }
  }
}

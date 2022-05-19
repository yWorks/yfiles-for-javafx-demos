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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.IRectangle;
import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.PointD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.INode;
import com.yworks.yfiles.graph.SimpleLabel;
import com.yworks.yfiles.graph.labelmodels.FreeLabelModel;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.ICanvasObject;
import com.yworks.yfiles.view.ICanvasObjectDescriptor;
import com.yworks.yfiles.view.ICanvasObjectGroup;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.TextWrapping;
import javafx.geometry.VPos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Provides tool tips for detail information on activities.
 */
class ToolTipHelper {
  /**
   * Shared singleton instance.
   */
  private static ToolTipHelper INSTANCE;


  /**
   * The canvas object group that holds the tool tip visualizations.
   */
  private ICanvasObjectGroup infoGroup;

  /**
   * The label used to create the activity info tool tip visualization.
   */
  private SimpleLabel activityInfoLabel;

  /**
   * The canvas object that holds the activity info tool tip visualization.
   */
  private ICanvasObject activityInfo;

  /**
   * The label used to create the time info tool tip visualization.
   */
  private SimpleLabel timeInfoLabel;

  /**
   * The canvas object that holds the time info tool tip visualization.
   */
  private ICanvasObject timeInfo;


  private ToolTipHelper() {
  }


  /**
   * Creates the activity info tool tip visualization.
   * @param activity for which the infos should be shown
   * @param center of the clicked node
   * @param viewPoint the viewPoint coordinates
   * @param viewPortWidth the width of the view port
   */
  private void createActivityInfoCanvasObjects(Activity activity, PointD center, PointD viewPoint, double viewPortWidth) {
    // create the string with all needed information about the given activity
    StringBuilder sb = new StringBuilder(activity.getName()).append("\n");
    sb.append("Start Time: ").append(format(activity.getStartDate())).append("\n");
    sb.append("End Time: ").append(format(activity.getStartDate())).append("\n");
    sb.append("Lead Time: ").append(Math.floor(activity.getLeadTime())).append("\n");
    sb.append("Follow-up Time: ").append(Math.floor(activity.getFollowUpTime())).append("\n");
    sb.append("Total Duration: ").append(Math.floor(GanttDataUtil.getTotalActivityDuration(activity))).append("\n");
    sb.append("Task: ").append(activity.getTask().getName());

    FreeLabelModel model = new FreeLabelModel();
    activityInfoLabel = new SimpleLabel(null, sb.toString(), model.createAbsolute(PointD.ORIGIN));
    DefaultLabelStyle style = new DefaultLabelStyle();
    Font oldFont = style.getFont();
    style.setFont(new Font(oldFont.getName(), oldFont.getSize() + 2));
    style.setInsets(new InsetsD(20));
    style.setBackgroundPaint(activity.getTask().getColor());
    style.setTextPaint(Color.WHITE);


    // apply styles and create the activity info group
    activityInfoLabel.setStyle(style);
    activityInfo = infoGroup.addChild(activityInfoLabel, ICanvasObjectDescriptor.ALWAYS_DIRTY_LOOKUP);

    // set size for info
    SizeD size = style.getRenderer().getPreferredSize(activityInfoLabel, style);
    activityInfoLabel.setPreferredSize(size);

    // check if there is enough space to display the tool tip above the activity node
    PointD location;
    if (center.getY() - GanttDataUtil.ACTIVITY_HEIGHT - size.getHeight() - 10 < viewPoint.getY()) {
      // not enough space above - draw below node
      location = new PointD(
        center.getX() - size.getWidth() / 2,
        center.getY() + GanttDataUtil.ACTIVITY_HEIGHT + size.getHeight());
    } else {
      // enough space above - draw above node
      location = new PointD(
        center.getX() - size.getWidth() / 2,
        center.getY() - GanttDataUtil.ACTIVITY_HEIGHT);
    }

    // fit label into viewport
    if (location.getX() < viewPoint.getX()) {
      location = new PointD(viewPoint.getX(), location.getY());
    }
    else if (location.getX() + size.width > viewPoint.getX() + viewPortWidth) {
      location = new PointD(viewPoint.getX() + (viewPortWidth - size.width), location.getY());
    }

    activityInfoLabel.setLayoutParameter(model.createAbsolute(location));

    infoGroup.toFront();
  }

  /**
   * Removes the activity info group and label.
   */
  private void removeActivityInfoImpl() {
    ICanvasObject group = activityInfo;
    if (group != null) {
      group.remove();
      activityInfo = null;
    }
    activityInfoLabel = null;
  }

  /**
   * Returns a human-readable representation of the given date-time.
   */
  private String format( LocalDateTime date ) {
    return date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, uuuu, HH:mm:ss"));
  }

  /**
   * Creates the time info tool tip visualization.
   */
  private void createTimeInfoCanvasObjects() {
    // configure label style for displaying plain text
    DefaultLabelStyle style = new DefaultLabelStyle();
    style.setTextAlignment(TextAlignment.CENTER);
    style.setTextWrapping(TextWrapping.NO_WRAP);
    style.setTextClippingEnabled(false);
    style.setVerticalTextAlignment(VPos.CENTER);
    style.setInsets(new InsetsD(2));
    style.setTextPaint(Color.BLACK);
    style.setBackgroundPaint(Color.rgb(255, 255, 255, 0.5));
    style.setBackgroundPen(Pen.getBlack());

    timeInfoLabel = new SimpleLabel(null, "", new FreeLabelModel().createAbsolute(PointD.ORIGIN));
    timeInfoLabel.setStyle(style);

    timeInfo = infoGroup.addChild(timeInfoLabel, ICanvasObjectDescriptor.ALWAYS_DIRTY_LOOKUP);
  }

  /**
   * Updates the time info.
   */
  private void updateTimeInfoImpl( String text, PointD location, boolean isFollowUp ) {
    timeInfoLabel.setText(text);

    ILabelStyle style = timeInfoLabel.getStyle();
    SizeD size = style.getRenderer().getPreferredSize(timeInfoLabel, style);
    timeInfoLabel.setPreferredSize(size);

    FreeLabelModel model = (FreeLabelModel) timeInfoLabel.getLayoutParameter().getModel();
    double timeInfoOffset = 4;
    if (isFollowUp) {
      // draw on the right side
      PointD anchor = new PointD(location.x + timeInfoOffset, location.y);
      timeInfoLabel.setLayoutParameter(model.createAbsolute(anchor));
    } else {
      // draw on the left side
      PointD anchor = new PointD(location.x - size.width - timeInfoOffset, location.y);
      timeInfoLabel.setLayoutParameter(model.createAbsolute(anchor));
    }

    infoGroup.toFront();
  }

  /**
   * Removes the time info.
   */
  private void removeTimeInfoImpl() {
    ICanvasObject group = timeInfo;
    if (group != null) {
      group.remove();
      timeInfo = null;
    }
    timeInfoLabel = null;
  }

  /**
   * Removes the canvas group used for displaying activity information.
   */
  private void dispose() {
    ICanvasObjectGroup group = infoGroup;
    if (group != null) {
      group.remove();
      infoGroup = null;
      activityInfo = null;
      timeInfo = null;
    }
    activityInfoLabel = null;
    timeInfoLabel = null;
  }


  /**
   * Creates a new singleton {@code ToolTipHelper} instance for the given
   * graph control.
   */
  static ToolTipHelper newInstance( GraphControl graphControl ) {
    ToolTipHelper oldHelper = INSTANCE;
    if (oldHelper != null) {
      oldHelper.dispose();
    }

    ToolTipHelper newHelper = new ToolTipHelper();
    newHelper.infoGroup = graphControl.getRootGroup().addGroup();
    INSTANCE = newHelper;
    return newHelper;
  }


  /**
   * Shows information about the activity associated to the given activity node.
   * @param viewPoint the viewpoint coordinates
   * @param viewPortWidth the width of the viewport
   */
  static void showActivityInfo(INode node, PointD viewPoint, double viewPortWidth) {
    Activity activity = (Activity) node.getTag();
    PointD center = node.getLayout().getCenter();
    ToolTipHelper helper = getInstance();
    if (helper.activityInfo != null) {
      helper.removeActivityInfoImpl();
    }
    helper.createActivityInfoCanvasObjects(activity, center, viewPoint, viewPortWidth);
  }

  /**
   * Removes the activity info group and label.
   */
  static void removeActivityInfo() {
    getInstance().removeActivityInfoImpl();
  }

  /**
   * Shows updated time info.
   */
  static void showTimeInfo( INode node, String text, boolean isFollowUp ) {
    ToolTipHelper helper = getInstance();
    if (helper.timeInfoLabel == null) {
      helper.createTimeInfoCanvasObjects();
    }
    IRectangle nl = node.getLayout();
    PointD p = isFollowUp ? nl.getTopRight() : nl.getTopLeft();
    helper.updateTimeInfoImpl(text, p, isFollowUp);
  }

  /**
   * Removes the time info.
   */
  static void removeTimeInfo() {
    getInstance().removeTimeInfoImpl();
  }

  /**
   * Returns the shared singleton instance.
   * @throws IllegalStateException if the singleton instance has not yet been
   * created.
   * @see #newInstance(GraphControl)
   */
  private static ToolTipHelper getInstance() {
    ToolTipHelper helper = INSTANCE;
    if (helper == null) {
      throw new IllegalStateException("ToolTipHelper not initialized.");
    } else {
      return helper;
    }
  }
}

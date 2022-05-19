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

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.VisualGroup;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * A custom {@link IVisualCreator} implementation to draw the timeline
 * at the top of the demo.
 */
public class TimelineVisualCreator implements IVisualCreator {
  private final GanttDataUtil data;

  /**
   * Initializes a new {@code TimelineVisualCreator} instance for the given
   * project schedule.
   */
  public TimelineVisualCreator( GanttDataUtil data ) {
    this.data = data;
  }

  /**
   * Creates the time line visualization.
   */
  @Override
  public Node createVisual( IRenderContext context ) {
    CanvasControl component = context.getCanvasControl();
    RectD viewport = component.getViewport();

    return new TimeLineVisual(data, viewport);
  }

  /**
   * Updates the time line visualization.
   */
  @Override
  public Node updateVisual( IRenderContext context, Node oldVisual ) {
    if (oldVisual instanceof TimeLineVisual) {
      TimeLineVisual timeLineVisual = (TimeLineVisual) oldVisual;
      timeLineVisual.update(context.getCanvasControl().getViewport());
      return timeLineVisual;
    } else {
      return createVisual(context);
    }
  }


  /**
   * Renders a time line of months and days.
   */
  private static class TimeLineVisual extends VisualGroup {
    private static final Color EVEN_COLOR = Color.rgb(155, 195, 255);
    private static final Color ODD_COLOR = Color.rgb(105, 145, 255);

    private final GanttDataUtil data;
    private List<TextVisual> months;
    private List<TextVisual> days;

    TimeLineVisual( GanttDataUtil data, RectD initialViewport ) {
      this.data = data;
      createInitialTimeline(initialViewport);
    }

    /**
     * Creates the timeline for the initially visible viewport.
     */
    private void createInitialTimeline( RectD initialViewport ) {
      double minX = initialViewport.getMinX();
      double maxX = initialViewport.getMaxX();

      LocalDateTime beginDate =
        data.getDate(minX).toLocalDate().minusDays(2).atStartOfDay();
      LocalDateTime endDate =
        data.getDate(maxX).toLocalDate().plusDays(2).atStartOfDay();

      createInitialMonthRectangles(beginDate);
      createInitialDayRectangles(beginDate, endDate);
    }

    /**
     * Creates and caches the visualizations for the initially visible day
     * headers in the the timeline.
     */
    private void createInitialDayRectangles(
            LocalDateTime beginDate, LocalDateTime endDate
    ) {
      int beginX = data.getX(beginDate);
      int endX = data.getX(endDate);

      double dx = endX - beginX;
      int initialCapacity =
        (int) Math.ceil(dx / GanttDataUtil.DAY_WIDTH);
      days = new ArrayList<>(initialCapacity + 1);
      LocalDateTime currentDate = beginDate;

      int y = 35;
      int width = GanttDataUtil.DAY_WIDTH;
      int height = 30;

      for (int x = beginX; x < endX + GanttDataUtil.DAY_WIDTH; x += width) {
        // create the background for the current day and add text for the
        // corresponding date
        TextVisual visual = new TextVisual();
        visual.setBounds(x, y, width, height);
        visual.setText(formatDay(currentDate));

        visual.setFill(getFill(currentDate, false));
        visual.setDate(currentDate);

        this.add(visual);
        days.add(visual);

        currentDate = currentDate.plusDays(1);
      }
    }

    /**
     * Creates and caches the visualizations for the initially visible month
     * headers in the the timeline.
     */
    private void createInitialMonthRectangles( LocalDateTime beginDate ) {
      months = new ArrayList<>(3);

      LocalDateTime currentDate = firstDayOfMonth(beginDate);

      int x = data.getX(currentDate);
      int y = 5;
      int height = 30;

      for (int i = 0; i < 3; i++) {
        // create the background for the current month and add the correct text
        int monthWidth = calculateMonthWidth(currentDate);
        TextVisual visual = new TextVisual();
        visual.setBounds(x, y, monthWidth, height);
        visual.setText(formatMonth(currentDate));

        visual.setFill(getFill(currentDate, true));
        visual.setDate(currentDate);

        this.add(visual);
        months.add(visual);

        x += monthWidth;
        currentDate = nextMonth(currentDate);
      }
    }

    /**
     * Returns the first day of the month of the given date.
     */
    private LocalDateTime firstDayOfMonth( LocalDateTime date ) {
      return date.with(TemporalAdjusters.firstDayOfMonth())
        .toLocalDate().atStartOfDay();
    }

    /**
     * Returns the month after the month of the given date.
     */
    private LocalDateTime nextMonth( LocalDateTime date ) {
      return date.plusMonths(1).toLocalDate()
        .with(TemporalAdjusters.firstDayOfMonth()).atStartOfDay();
    }

    /**
     * Calculates the width of the visual representation of the month of
     * the given date.
     */
    private int calculateMonthWidth( LocalDateTime date ) {
      return date.toLocalDate().lengthOfMonth() * GanttDataUtil.DAY_WIDTH;
    }

    /**
     * Updates the timeline for the given viewport.
     */
    public void update( RectD viewport ) {
      double minX = viewport.getMinX();
      double maxX = viewport.getMaxX();
      double dx = maxX - minX;

      int newDayQuantity = (int) Math.ceil(dx / GanttDataUtil.DAY_WIDTH) + 2;
      int newMonthQuantity = (int) Math.ceil(dx / (GanttDataUtil.DAY_WIDTH * 30)) + 1;
      if (newDayQuantity != days.size()) {
        onViewportWidthChanged(newDayQuantity, newMonthQuantity);
      }

      updateDayRectangles((int) minX);
      updateMonthRectangles((int) minX);
    }

    /**
     * Updates all month rectangles.
     */
    private void updateMonthRectangles( int x ) {
      for (TextVisual visual : months) {
        // get the start of the month
        LocalDateTime newDate = firstDayOfMonth(data.getDate(x));
        LocalDateTime oldDate = visual.getDate();

        int newX = data.getX(newDate);

        // update if the rectangle represents a different month
        if (!newDate.getMonth().equals(oldDate.getMonth())) {
          updateText(visual, newX, newDate, true);
        }

        x = newX + calculateMonthWidth(newDate);
      }
    }

    /**
     * Updates all day rectangles.
     */
    private void updateDayRectangles( int x ) {
      for (TextVisual visual : days) {
        LocalDateTime newDate = data.getDate(x);
        LocalDateTime oldDate = visual.getDate();

        // update if the rectangle represents a new date (new x value)
        if (!newDate.equals(oldDate)) {
          int newX = data.getX(newDate);
          updateText(visual, newX, newDate, false);
        }

        x += GanttDataUtil.DAY_WIDTH;
      }
    }

    /**
     * Updates the given text visual.
     * Applies new x and y coordinates and configures the appearance depending
     * on whether the date represents a day or a month.
     */
    private void updateText(
      TextVisual rect, int startX, LocalDateTime date, boolean isMonth
    ) {
      int width = isMonth ? calculateMonthWidth(date) : GanttDataUtil.DAY_WIDTH;
      int y = isMonth ? 5 : 35;
      rect.setDate(date);
      rect.setBounds(startX, y, width, 30);
      rect.setFill(getFill(date, isMonth));
      rect.setText(isMonth ? formatMonth(date) : formatDay(date));
    }

    /**
     * Handles viewport width changes and determines if timeline rectangles
     * need to be added or removed.
     */
    private void onViewportWidthChanged( int dayQuantity, int monthQuantity ) {
      int dayDiff = dayQuantity - days.size();
      if (dayDiff < 0) {
        removeDays(-dayDiff);
      } else {
        addDays(dayDiff);
      } 

      int monthDiff = monthQuantity - months.size();
      if (monthDiff < 0) {
        removeMonths(-monthDiff);
      } else {
        addMonths(monthDiff);
      }
    }

    /**
     * Adds new day rectangles in response to viewport width changes.
     */
    private void addDays( int count ) {
      addTexts(days, count);
    }

    /**
     * Removes day rectangles in response to viewport width changes.
     */
    private void removeDays( int count ) {
      removeTexts(this.days, count);
    }

    /**
     * Adds new month rectangles in response to viewport width changes.
     */
    private void addMonths( int count ) {
      addTexts(months, count);
    }

    /**
     * Removes month rectangles in response to viewport width changes.
     */
    private void removeMonths( int count ) {
      removeTexts(this.months, count);
    }

    private void addTexts( List<TextVisual> list, int count ) {
      for (int i = 0; i < count; ++i) {
        TextVisual visual = new TextVisual();
        list.add(visual);
        this.add(visual);
      }
    }

    private void removeTexts( List<TextVisual> list, int count ) {
      for (int i = 0; i < count; ++i) {
        this.remove(list.remove(list.size() - 1));
      }
    }

    /**
     * Formats and returns a month date string according to the given date.
     */
    private static String formatMonth( LocalDateTime date ) {
      return date.getMonth().toString() + " " + date.getYear();
    }

    /**
     * Formats and returns a day date string according to the given date.
     */
    private static String formatDay( LocalDateTime date ) {
      return String.valueOf(date.getDayOfMonth());
    }

    /**
     * Determines and returns fill color.
     */
    private Color getFill( LocalDateTime date, boolean isMonth ) {
      long l;
      if (isMonth) {
        l = data.untilOriginDate(date, ChronoUnit.MONTHS);
      } else {
        l = data.untilOriginDate(date, ChronoUnit.DAYS);
      }
      return l % 2 == 0 ? EVEN_COLOR : ODD_COLOR;
    }
  }


  private static final class TextVisual extends VisualGroup {
    private Rectangle background;
    private Text foreground;
    private LocalDateTime date;

    TextVisual() {
      add(background = newRectangle());
      add(foreground = newText());
      date = LocalDateTime.MIN;
    }

    LocalDateTime getDate() {
      return date;
    }

    void setDate( LocalDateTime date ) {
      this.date = date;
    }

    void setBounds( double x, double y, double width, double height ) {
      background.setWidth(width);
      background.setHeight(height);
      background.setX(x);
      background.setY(y);
    }

    void setFill( Color fill ) {
      background.setFill(fill);
    }
    
    void setText( String text ) {
      foreground.setText(text);

      // center the new text n the background
      double textWidth = foreground.getLayoutBounds().getWidth();
      double textHeight = foreground.getLayoutBounds().getHeight();
      foreground.setLayoutX(
        background.getX() + (background.getWidth() - textWidth) * 0.5);
      foreground.setLayoutY(
        background.getY() + (background.getHeight() - textHeight) * 0.5);
    }

    private static Rectangle newRectangle() {
      Rectangle rect = new Rectangle();
      rect.setStroke(Color.WHITE);
      return rect;
    }

    private static Text newText() {
      Text text = new Text(0, 0, "");
      text.setFill(Color.WHITE);
      text.setTextOrigin(VPos.TOP);
      return text;
    }
  }
}

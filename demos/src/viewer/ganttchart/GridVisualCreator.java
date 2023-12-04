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
package viewer.ganttchart;

import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.VisualGroup;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages and renders the background of the {@link GanttChartDemo#graphControl}.
 * Creates a grid whose horizontal lines correspond to task boundaries and whose
 * vertical lines correspond to day an month boundaries.
 */
public class GridVisualCreator implements IVisualCreator {
  private final GanttDataUtil data;

  /**
   * Initializes a new {@code GridVisualCreator} instance for the given project
   * schedule.
   */
  public GridVisualCreator( GanttDataUtil data ) {
    this.data = data;
  }

  /**
   * Creates the grid visualization.
   * @return
   */
  @Override
  public Node createVisual( IRenderContext context ) {
    CanvasControl component = context.getCanvasControl();
    RectD viewport = component.getViewport();

    return new GridVisual(data, viewport);
  }

  /**
   * Updates the grid visualization.
   */
  @Override
  public Node updateVisual( IRenderContext context, Node oldVisual ) {
    if (oldVisual instanceof GridVisual) {
      GridVisual gridVisual = (GridVisual) oldVisual;
      gridVisual.updateSeparators(context.getCanvasControl().getViewport());
      return gridVisual;
    } else {
      return createVisual(context);
    }
  }

  /**
   * Visualizes the grid in the background separating tasks and dates.
   */
  private static class GridVisual extends VisualGroup {
    private static final String TASK = "TASK";
    private static final String DATE = "DATE";
    private static final Color LINE_COLOR = Color.rgb(204, 204, 204);

    private final GanttDataUtil data;

    private List<Line> taskSeparators;
    private List<Line> dateSeparators;


    GridVisual( GanttDataUtil data, RectD initialViewport ) {
      this.data = data;

      createInitialTaskSeparators(
        initialViewport.getX(), initialViewport.getMaxX());
      createInitialDateSeparators(
        initialViewport.getMinX(), initialViewport.getMaxX(),
        initialViewport.getMinY(), initialViewport.getMaxY());
    }


    /**
     * Creates and caches the initial horizontal task separators.
     */
    protected void createInitialTaskSeparators( double beginX, double endX ) {
      taskSeparators = new ArrayList<>(data.getTasks().size());

      for (Task task : data.getTasks()) {
        double y = data.getTaskY(task)
          + data.getCompleteTaskHeight(task)
          + GanttDataUtil.TASK_SPACING;

        Line line = new Line(
          beginX - GanttDataUtil.DAY_WIDTH, y,
          endX + GanttDataUtil.DAY_WIDTH, y);
        line.getProperties().put(TASK, task);

        line.setFill(LINE_COLOR);
        line.setStrokeWidth(1);
        line.getStrokeDashArray().addAll(4d, 4d);
        line.setStrokeDashOffset(2);
        this.add(line);
        taskSeparators.add(line);
      }
    }

    /**
     * Creates and caches the inital vertical date separators.
     */
    private void createInitialDateSeparators(
      double minX, double maxX, double minY, double maxY
    ) {
      double viewportWidth = maxX - minX;
      int nLines = calculateVerticalQuantity(viewportWidth);
      dateSeparators = new ArrayList<>(nLines + 1);

      int x = ((int) minX);
      LocalDateTime currentDate = data.getDate(x);
      LocalDateTime lastDate = currentDate.minusDays(1);

      // end one day width to the right of the initially rightmost line in the
      // viewport
      while (x < maxX + GanttDataUtil.DAY_WIDTH) {
        Line line = new Line(x, minY, x, maxY);
        line.getProperties().put(DATE, currentDate);
        configureSeparator(line, isMonthSeparator(lastDate, currentDate));

        this.add(line);
        dateSeparators.add(line);

        x += GanttDataUtil.DAY_WIDTH;
        lastDate = currentDate;
        currentDate = currentDate.plusDays(1);
      }
    }

    /**
     * Updates all date and task separators.
     * If the viewport size has changed, new lines will be added for a bigger
     * viewport or removed for a smaller viewport.
     */
    private void updateSeparators( RectD newViewPort ) {
      double newMinX = newViewPort.getMinX();
      double newMaxX = newViewPort.getMaxX();
      double newMaxY = newViewPort.getMaxY();
      double newMinY = newViewPort.getMinY();
      double newWidth = newViewPort.getWidth();

      // viewportWidth changed, therefore the total number of vertical
      // separators needs to change
      int newVerticalQuantity = calculateVerticalQuantity(newWidth);
      if (newVerticalQuantity != dateSeparators.size()) {
        onViewportWidthChanged(newVerticalQuantity);
      }

      updateDateSeparators(((int) newMinX), newMinY, newMaxY);

      updateTaskSeparators(newMinX, newMaxX);
    }

    /**
     * Updates all horizontal task separators.
     */
    private void updateTaskSeparators( double newMinX, double newMaxX ) {
      for (Line line : taskSeparators) {
        // update task height
        Task task = (Task) line.getProperties().get(TASK);
        double y = data.getTaskY(task)
          + data.getCompleteTaskHeight(task)
          + GanttDataUtil.TASK_SPACING;

        if (y != line.getStartY()) {
          line.setStartY(y);
          line.setEndY(y);
        }

        // horizontal scroll and viewportWidth change
        if (newMinX != line.getStartX() || newMaxX != line.getEndX()) {
          line.setStartX(newMinX);
          line.setEndX(newMaxX);
        }
      }
    }

    /**
     * Updates all vertical date separators.
     */
    private void updateDateSeparators( int minX, double minY, double maxY ) {
      for (Line line : dateSeparators) {
        LocalDateTime currentDate = data.getDate(minX);
        int newX = data.getX(currentDate);
        // update if the line represents a new date (new x value) or if new y values should be assigned
        boolean update =
          line.getStartY() != minY ||
          line.getEndY() != maxY ||
          !currentDate.equals(line.getProperties().get(DATE));
        if (update) {
          updateDateLine(line, newX, newX, minY, maxY);
        }
        minX += GanttDataUtil.DAY_WIDTH;
      }
    }

    /**
     * Adds or removes new lines to/from {@link #dateSeparators} depending on
     * the required number of new date separators.
     * @param newVerticalQuantity the required number of date separators.
     */
    private void onViewportWidthChanged( int newVerticalQuantity ) {
      int nDiff = newVerticalQuantity - dateSeparators.size();
      if (nDiff < 0) {
        removeDateSeparators(-nDiff);
      } else {
        addDateSeparators(nDiff);
      }
    }

    /**
     * Adds the given number of new date separator lines.
     */
    private void addDateSeparators( int count ) {
      for (int i = 0; i < count; ++i) {
        Line line = new Line();
        dateSeparators.add(line);
        this.add(line);
      }
    }

    /**
     * Removes the given number of date separator lines.
     */
    private void removeDateSeparators( int count ) {
      for (int i = 0; i < count; ++i) {
        this.remove(dateSeparators.remove(dateSeparators.size() - 1));
      }
    }

    /**
     * Updates a date line.
     * Applies new start and end coordinates, updates the corresponding date,
     * and configures the visual.
     */
    private void updateDateLine(
      Line line, double minX, double maxX, double minY, double maxY
    ) {
      line.setStartX(minX);
      line.setEndX(maxX);
      line.setStartY(minY);
      line.setEndY(maxY);

      if (minX == maxX) {
        // vertical lines are day separators,
        // therefore store the corresponding date
        LocalDateTime newDate = data.getDate(minX);
        LocalDateTime dayBefore = newDate.minusDays(1);
        line.getProperties().put(DATE, newDate);
        configureSeparator(line, isMonthSeparator(dayBefore, newDate));
      }
    }

    /**
     * Configures the given line depending on whether or not it is a month or
     * day separator.
     */
    private void configureSeparator( Line line, boolean isMonth ) {
      line.setStrokeWidth(isMonth ? 3 : 1);
      line.setFill(LINE_COLOR);
    }

    /**
     * Determines if the separator between the given dates is a month or a day
     * separator.
     */
    private boolean isMonthSeparator(
      LocalDateTime previousDay, LocalDateTime currentDay
    ) {
      return previousDay.getMonth() != currentDay.getMonth();
    }

    /**
     * Calculates the required number of date separators.
     */
    private int calculateVerticalQuantity( double newWidth ) {
      return 1 + (int) Math.ceil(newWidth / GanttDataUtil.DAY_WIDTH);
    }
  }
}

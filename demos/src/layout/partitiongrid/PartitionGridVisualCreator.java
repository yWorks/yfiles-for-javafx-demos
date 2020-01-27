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
package layout.partitiongrid;

import com.yworks.yfiles.algorithms.YList;
import com.yworks.yfiles.geometry.RectD;
import com.yworks.yfiles.layout.ColumnDescriptor;
import com.yworks.yfiles.layout.PartitionGrid;
import com.yworks.yfiles.layout.RowDescriptor;
import com.yworks.yfiles.utils.IEnumerable;
import com.yworks.yfiles.view.IRenderContext;
import com.yworks.yfiles.view.IVisualCreator;
import com.yworks.yfiles.view.Pen;
import com.yworks.yfiles.view.VisualGroup;
import com.yworks.yfiles.view.IAnimation;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.Duration;
import java.util.List;

/**
 * Visualizes the partition grid that has been used in the last layout.
 * <p>
 * Each column and row is visualized by a {@link Rectangle}.
 * </p>
 * <p>
 * This class implements {@link IAnimation} and allows to animate the partition grid changes between two layout calculations.
 * </p>
 */
class PartitionGridVisualCreator implements IVisualCreator, IAnimation {

  // the columns and rows to draw
  private final Rectangle[] columns;
  private final Rectangle[] rows;

  // start and end positions of the columns and rows to animate between
  private RectD[] columnStarts;
  private RectD[] columnEnds;
  private RectD[] rowStarts;
  private RectD[] rowEnds;
  private PartitionGrid grid;

  /**
   * Creates a new instance with one column per color and one row per pen.
   * @param colors The colors used for the grid column fills.
   * @param pens The pens used for the grid row borders.
   */
  PartitionGridVisualCreator(List<Color> colors, List<Pen> pens) {
    //to colorize the background we use rectangles
    columns = new Rectangle[colors.size()];

    //iterate over every color(therefore any column) and create the color according rectangle
    for (int i = 0; i < colors.size(); i++) {
      columns[i] = new Rectangle(10, 10, colors.get(i));
      columns[i].setOpacity(0.3);
    }

    rows = new Rectangle[pens.size()];
    for (int i = 0; i < pens.size(); i++) {
      rows[i] = new Rectangle(10, 10, Color.TRANSPARENT);
      rows[i].setStroke(pens.get(i).getPaint());
      rows[i].setStrokeWidth(pens.get(i).getThickness());
    }
  }

  // region IVisualCreator implementation

  @Override
  public Node createVisual(IRenderContext renderContext) {
    // create a new VisualGroup and update it with the current state
    VisualGroup container = new VisualGroup();
    for (Rectangle column: columns) {
      container.add(column);
    }
    for (Rectangle row: rows) {
      container.add(row);
    }
    return container;
  }

  @Override
  public Node updateVisual(IRenderContext renderContext, Node oldVisual) {
    return oldVisual;
  }

  // endregion

  // region IAnimation implementation

  public void initialize() {
    // calculate min and max values of the partition grid bounds for the start and the end of the animation
    double minStartX = Double.MAX_VALUE;
    double maxStartX = Double.MIN_VALUE;
    double minStartY = Double.MAX_VALUE;
    double maxStartY = Double.MIN_VALUE;
    double minEndX = Double.MAX_VALUE;
    double maxEndX = Double.MIN_VALUE;
    double minEndY = Double.MAX_VALUE;
    double maxEndY = Double.MIN_VALUE;

    // looking at the y-coordinate and height of each row before and after the layout we can define min/maxStart/EndY
    List<RowDescriptor> gridRows = grid.getRows().toList();
    for (int i = 0; i < gridRows.size(); i++) {
      RowDescriptor rowDescriptor = (RowDescriptor) gridRows.get(i);
      Rectangle rowRect = rows[i];
      minStartY = Math.min(minStartY, rowRect.getLayoutY());
      maxStartY = Math.max(maxStartY, rowRect.getLayoutY() + rowRect.getHeight());
      minEndY = Math.min(minEndY, rowDescriptor.getComputedPosition());
      maxEndY = Math.max(maxEndY, rowDescriptor.getComputedPosition() + rowDescriptor.getComputedHeight());
    }

    // looking at the x-coordinate and width of each column before and after the layout we can define min/maxStart/EndX
    List<ColumnDescriptor> columns = grid.getColumns().toList();
    columnStarts = new RectD[columns.size()];
    columnEnds = new RectD[columns.size()];
    for (int i = 0; i < columns.size(); i++) {
      ColumnDescriptor columnDescriptor = (ColumnDescriptor) columns.get(i);
      Rectangle columnRect = this.columns[i];

      double startX = columnRect.getLayoutX();
      double startWidth = columnRect.getWidth();
      minStartX = Math.min(minStartX, startX);
      maxStartX = Math.max(maxStartX, startX + startWidth);

      double endX = columnDescriptor.getComputedPosition();
      double endWidth = columnDescriptor.getComputedWidth();
      minEndX = Math.min(minEndX, endX);
      maxEndX = Math.max(maxEndX, endX + endWidth);

      // for each column we store its layout before and after the layout
      columnStarts[i] = new RectD(startX, minStartY, startWidth, maxStartY - minStartY);
      columnEnds[i] = new RectD(endX, minEndY, endWidth, maxEndY - minEndY);
    }

    rowStarts = new RectD[gridRows.size()];
    rowEnds = new RectD[gridRows.size()];

    for (int i = 0; i < gridRows.size(); i++) {
      RowDescriptor rowDescriptor = (RowDescriptor) gridRows.get(i);
      Rectangle rowRect = this.rows[i];

      double startY = rowRect.getLayoutY();
      double startHeight = rowRect.getHeight();
      double endY = rowDescriptor.getComputedPosition();
      double endHeight = rowDescriptor.getComputedHeight();

      // for each row we store its layout before and after the layout
      rowStarts[i] = new RectD(minStartX, startY, maxStartX - minStartX, startHeight);
      rowEnds[i] = new RectD(minEndX, endY, maxEndX - minEndX, endHeight);
    }
  }

  public void animate(double time) {
    // for each row and column we calculate and set an intermediate layout corresponding to the time ratio
    for (int i = 0; i < this.rows.length; i++) {
      Rectangle row = this.rows[i];
      RectD rowStart = rowStarts[i];
      RectD rowEnd = rowEnds[i];

      row.setLayoutX(rowStart.getX() + time * (rowEnd.getX() - rowStart.getX()));
      row.setLayoutY(rowStart.getY() + time * (rowEnd.getY() - rowStart.getY()));
      row.setWidth(rowStart.getWidth() + time * (rowEnd.getWidth() - rowStart.getWidth()));
      row.setHeight(rowStart.getHeight() + time * (rowEnd.getHeight() - rowStart.getHeight()));
    }
    for (int i = 0; i < this.columns.length; i++) {
      Rectangle column = this.columns[i];
      RectD columnStart = columnStarts[i];
      RectD columnEnd = columnEnds[i];

      column.setLayoutX(columnStart.getX() + time * (columnEnd.getX() - columnStart.getX()));
      column.setLayoutY(columnStart.getY() + time * (columnEnd.getY() - columnStart.getY()));
      column.setWidth(columnStart.getWidth() + time * (columnEnd.getWidth() - columnStart.getWidth()));
      column.setHeight(columnStart.getHeight() + time * (columnEnd.getHeight() - columnStart.getHeight()));
    }
  }

  @Override
  public void cleanUp() {
    grid = null;
    rowStarts = null;
    rowEnds = null;
    columnStarts = null;
    columnEnds = null;
  }

  @Override
  public Duration getPreferredDuration() {
    return Duration.ofMillis(500);
  }

  // endregion

  /**
   * Sets the grid for the next animation.
   */
  public void setGrid(PartitionGrid grid) {
    this.grid = grid;
  }
}

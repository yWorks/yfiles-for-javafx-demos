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
package viewer.largegraphs;

import com.yworks.yfiles.graph.labelmodels.FreeLabelModel;
import com.yworks.yfiles.graph.styles.PathBasedEdgeStyleRenderer;
import com.yworks.yfiles.view.CanvasControl;
import com.yworks.yfiles.view.EdgeDecorationInstaller;
import com.yworks.yfiles.view.GraphOverviewControl;
import com.yworks.yfiles.view.PathRenderPolicy;
import com.yworks.yfiles.view.Pen;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.shape.Path;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.StrokeLineCap;
import viewer.largegraphs.styles.fast.FastEdgeStyle;
import viewer.largegraphs.styles.fast.FastLabelStyle;
import viewer.largegraphs.styles.levelofdetail.LevelOfDetailEdgeStyle;
import viewer.largegraphs.styles.levelofdetail.LevelOfDetailLabelStyle;
import viewer.largegraphs.styles.levelofdetail.LevelOfDetailNodeStyle;
import viewer.largegraphs.styles.selection.FastEdgeSelectionStyle;
import viewer.largegraphs.styles.selection.FastLabelSelectionStyle;
import viewer.largegraphs.styles.selection.FastNodeSelectionStyle;
import viewer.largegraphs.virtualization.VirtualizationEdgeStyleDecorator;
import viewer.largegraphs.virtualization.VirtualizationLabelStyleDecorator;
import viewer.largegraphs.virtualization.VirtualizationNodeStyleDecorator;

import java.util.function.Consumer;

/**
 * Collection of settings regarding performance optimizations in this demo.
 */
public class PerformanceSettings implements Cloneable{

  // region Backing fields

  private boolean overviewDisabled;
  private boolean fastStylesEnabled;
  private double minimumEdgeLength;
  private double edgeBendThreshold;
  private double edgeLabelVisibilityThreshold;
  private double nodeLabelVisibilityThreshold;
  private double edgeLabelTextThreshold;
  private double nodeLabelTextThreshold;
  private double complexNodeStyleThreshold;
  private boolean selectionHandlesDisabled;
  private boolean customSelectionDecoratorEnabled;
  private boolean labelModelBakingEnabled;
  private boolean pathRenderOptimizationEnabled;
  private boolean virtualizationDisabled;
  private double nodeVirtualizationThreshold;
  private double edgeVirtualizationThreshold;

  // endregion

  // region Overview

  /**
   * Gets a value indicating whether the graph overview should be disabled.
   * <p>
   * The {@link GraphOverviewControl} is almost the same as the normal graph control, just drawn with less fidelity,
   * not as often and usually smaller. With very large graphs, however, updating the overview can incur a significant
   * performance hit, so it's sometimes better to just turn it off.
   * </p>
   */
  boolean isOverviewDisabled() {
    return overviewDisabled;
  }

  /**
   * Sets a value indicating whether the graph overview should be disabled.
   * <p>
   * The {@link GraphOverviewControl} is almost the same as the normal graph control, just drawn with less fidelity,
   * not as often and usually smaller. With very large graphs, however, updating the overview can incur a significant
   * performance hit, so it's sometimes better to just turn it off.
   * </p>
   */
  void setOverviewDisabled(boolean overviewDisabled) {
    this.overviewDisabled = overviewDisabled;
    settingChanged("OverviewDisabled");
  }

  // endregion

  // region Fast styles

  /**
   * Gets a value indicating whether fast styles are enabled.
   * <p>
   * A large portion of rendering time is spent with creating and updating visuals of graph items via their styles. Most
   * default styles are intended for larger zoom levels and graphs of about a few hundred elements. With very large
   * graphs simplifying the appearance (and even drawing nothing at times) at lower zoom levels is a viable strategy to
   * improve performance (and also readability of the graph). In this regard, features with a large performance impact
   * are:
   * <ul>
   *   <li>Edges are clipped at their end nodes, which is impossible to see at low zoom levels, if at all.</li>
   *   <li>Edges are drawn with bends, which are also hard to see at low zoom levels.</li>
   *   <li>Labels are drawn even at low zoom levels where they aren't readable, let alone visible.</li>
   * </ul>
   * </p>
   * <p>
   *  Enabling fast styles then makes the following changes that improve performance considerably:
   *  <ul>
   *    <li>Level-of-detail style for nodes that adapts the graphical fidelity to the zoom level.</li>
   *    <li>Level-of-detail style for labels that hides labels below a certain zoom level.</li>
   *    <li>A simpler label style in between hiding labels and showing them fully, that just renders a rough shape of the text.</li>
   *    <li>A simpler edge style that hides very short edges and doesn't render bends below a configurable zoom level.</li>
   *  </ul>
   * </p>
   * @see LevelOfDetailNodeStyle
   * @see LevelOfDetailEdgeStyle
   * @see LevelOfDetailLabelStyle
   * @see FastEdgeStyle
   * @see FastLabelStyle
   * @see LargeGraphsDemo#updateStyles
   */
  boolean isFastStylesEnabled() {
    return fastStylesEnabled;
  }

  /**
   * Sets a value indicating whether fast styles are enabled.
   * <p>
   * A large portion of rendering time is spent with creating and updating visuals of graph items via their styles. Most
   * default styles are intended for larger zoom levels and graphs of about a few hundred elements. With very large
   * graphs simplifying the appearance (and even drawing nothing at times) at lower zoom levels is a viable strategy to
   * improve performance (and also readability of the graph). In this regard, features with a large performance impact
   * are:
   * <ul>
   *   <li>Edges are clipped at their end nodes, which is impossible to see at low zoom levels, if at all.</li>
   *   <li>Edges are drawn with bends, which are also hard to see at low zoom levels.</li>
   *   <li>Labels are drawn even at low zoom levels where they aren't readable, let alone visible.</li>
   * </ul>
   * </p>
   * <p>
   *  Enabling fast styles then makes the following changes that improve performance considerably:
   *  <ul>
   *    <li>Level-of-detail style for nodes that adapts the graphical fidelity to the zoom level.</li>
   *    <li>Level-of-detail style for labels that hides labels below a certain zoom level.</li>
   *    <li>A simpler label style in between hiding labels and showing them fully, that just renders a rough shape of the text.</li>
   *    <li>A simpler edge style that hides very short edges and doesn't render bends below a configurable zoom level.</li>
   *  </ul>
   * </p>
   * @see LevelOfDetailNodeStyle
   * @see LevelOfDetailEdgeStyle
   * @see LevelOfDetailLabelStyle
   * @see FastEdgeStyle
   * @see FastLabelStyle
   * @see LargeGraphsDemo#updateStyles
   */
  void setFastStylesEnabled(boolean fastStylesEnabled) {
    this.fastStylesEnabled = fastStylesEnabled;
    settingChanged("FastStylesEnabled");
  }

  /**
   * Gets the minimum length at which edges are still drawn.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * Edges shorter than this length in pixels are hidden.
   * </p>
   * @see FastEdgeStyle#getMinimumEdgeLength()
   * @see LargeGraphsDemo#updateStyles
   */
  double getMinimumEdgeLength() {
    return minimumEdgeLength;
  }

  /**
   * Sets the minimum length at which edges are still drawn.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * Edges shorter than this length in pixels are hidden.
   * </p>
   * @see FastEdgeStyle#getMinimumEdgeLength()
   * @see LargeGraphsDemo#updateStyles
   */
  void setMinimumEdgeLength(double minimumEdgeLength) {
    this.minimumEdgeLength = minimumEdgeLength;
    settingChanged("MinimumEdgeLength");
  }

  /**
   * Gets the zoom level below which bends are not rendered on edges.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * Below this zoom level edges won't show bends, simplifying their appearance.
   * </p>
   * @see FastEdgeStyle#getDrawBendsThreshold()
   * @see LargeGraphsDemo#updateStyles
   */
  double getEdgeBendThreshold() {
    return edgeBendThreshold;
  }

  /**
   * Sets the zoom level below which bends are not rendered on edges.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * Below this zoom level edges won't show bends, simplifying their appearance.
   * </p>
   * @see FastEdgeStyle#getDrawBendsThreshold()
   * @see LargeGraphsDemo#updateStyles
   */
  void setEdgeBendThreshold(double edgeBendThreshold) {
    this.edgeBendThreshold = edgeBendThreshold;
    settingChanged("EdgeBendThreshold");
  }

  /**
   * Gets the zoom level below which to hide edge labels.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * Below this zoom level edge labels are hidden. Above it, edge labels will be rendered in a simplified appearance
   * (see {@link FastLabelStyle}).
   * </p>
   * @see LargeGraphsDemo#updateStyles
   */
  double getEdgeLabelVisibilityThreshold() {
    return edgeLabelVisibilityThreshold;
  }

  /**
   * Sets the zoom level below which to hide edge labels.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * Below this zoom level edge labels are hidden. Above it, edge labels will be rendered in a simplified appearance
   * (see {@link FastLabelStyle}).
   * </p>
   * @see LargeGraphsDemo#updateStyles
   */
  void setEdgeLabelVisibilityThreshold(double edgeLabelVisibilityThreshold) {
    this.edgeLabelVisibilityThreshold = edgeLabelVisibilityThreshold;
    settingChanged("EdgeLabelVisibilityThreshold");
  }

  /**
   * Gets the zoom level below which to hide node labels.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * Below this zoom level node labels are hidden. Above it, node labels will be rendered in a simplified appearance
   * (see {@link FastLabelStyle}).
   * </p>
   * @see LargeGraphsDemo#updateStyles
   */
  double getNodeLabelVisibilityThreshold() {
    return nodeLabelVisibilityThreshold;
  }

  /**
   * Sets the zoom level below which to hide node labels.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * Below this zoom level node labels are hidden. Above it, node labels will be rendered in a simplified appearance
   * (see {@link FastLabelStyle}).
   * </p>
   * @see LargeGraphsDemo#updateStyles
   */
  void setNodeLabelVisibilityThreshold(double nodeLabelVisibilityThreshold) {
    this.nodeLabelVisibilityThreshold = nodeLabelVisibilityThreshold;
    settingChanged("NodeLabelVisibilityThreshold");
  }

  /**
   * Gets the zoom level above which to show edge labels with text.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * Below this zoom level edge labels are rendered in a simplified appearance (see {@link FastLabelStyle}).
   * Above it, edge labels will be rendered in full fidelity and readable.
   * </p>
   * @see LargeGraphsDemo#updateStyles
   */
  double getEdgeLabelTextThreshold() {
    return edgeLabelTextThreshold;
  }

  /**
   * Sets the zoom level above which to show edge labels with text.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * Below this zoom level edge labels are rendered in a simplified appearance (see {@link FastLabelStyle}).
   * Above it, edge labels will be rendered in full fidelity and readable.
   * </p>
   * @see LargeGraphsDemo#updateStyles
   */
  void setEdgeLabelTextThreshold(double edgeLabelTextThreshold) {
    this.edgeLabelTextThreshold = edgeLabelTextThreshold;
    settingChanged("EdgeLabelTextThreshold");
  }

  /**
   * Gets the zoom level above which to show node labels with text.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * Below this zoom level node labels are rendered in a simplified appearance (see {@link FastLabelStyle}).
   * Above it, node labels will be rendered in full fidelity and readable.
   * </p>
   * @see LargeGraphsDemo#updateStyles
   */
  double getNodeLabelTextThreshold() {
    return nodeLabelTextThreshold;
  }

  /**
   * Sets the zoom level above which to show node labels with text.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * Below this zoom level node labels are rendered in a simplified appearance (see {@link FastLabelStyle}).
   * Above it, node labels will be rendered in full fidelity and readable.
   * </p>
   * @see LargeGraphsDemo#updateStyles
   */
  void setNodeLabelTextThreshold(double nodeLabelTextThreshold) {
    this.nodeLabelTextThreshold = nodeLabelTextThreshold;
    settingChanged("NodeLabelTextThreshold");
  }

  /**
   * Gets the zoom level above which to display nodes in a visually more complex style.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * This setting controls actually two zoom level thresholds between three different node styles:
   * <ol>
   *   <li>A very simple visualization as a rectangle with no outline.</li>
   *   <li>A nicer, but still simple visualization as a rounded rectangle with an outline.</li>
   *   <li>A pretty visualization with highlights and gradients.</li>
   * </ol>
   * </p>
   * @see LargeGraphsDemo#updateStyles
   */
  double getComplexNodeStyleThreshold() {
    return complexNodeStyleThreshold;
  }

  /**
   * Sets the zoom level above which to display nodes in a visually more complex style.
   * <p>
   * This setting has no effect unless {@link #isFastStylesEnabled()} is <code>true</code>.
   * </p>
   * <p>
   * This setting controls actually two zoom level thresholds between three different node styles:
   * <ol>
   *   <li>A very simple visualization as a rectangle with no outline.</li>
   *   <li>A nicer, but still simple visualization as a rounded rectangle with an outline.</li>
   *   <li>A pretty visualization with highlights and gradients.</li>
   * </ol>
   * </p>
   * @see LargeGraphsDemo#updateStyles
   */
  void setComplexNodeStyleThreshold(double complexNodeStyleThreshold) {
    this.complexNodeStyleThreshold = complexNodeStyleThreshold;
    settingChanged("ComplexNodeStyleThreshold");
  }

  // endregion

  // region Virtualization

  /**
   * Gets a value indicating whether virtualization should be disabled at low zoom levels.
   * <p>
   * Virtualization in {@link CanvasControl} trims the JavaFX visual tree to the elements that are actually visible.
   * This works well when only a few hundred visuals need to be removed or added at a time but results in significant
   * stutter if thousands of elements are affected. The later happens typically while panning or zooming in large graph
   * at low zoom level.
   * </p>
   * <p>
   * To avoid this, virtualization should be disabled at low zoom levels but kept enabled at higher zoom levels, as it
   * will lower memory usage (and improve other performance figures).
   * </p>
   * <p>
   * Note that these values are very sensitive to the displayed graph and its layout as the values depend mostly on how
   * many items are visible at once and thus how many items are likely to be virtualized when zooming or panning. In
   * this demo labels are always virtualized because we assume that labels are not visible at zoom levels so low that
   * virtualization would provide.
   * </p>
   * <p>
   * Disabling virtualization can be implemented by unconditionally returning {@code true} from a style's visibility
   * test. This causes the {@link CanvasControl} to always assume that the item is visible in the current viewport and
   * thus never remove it from the visual tree.
   * </p>
   * @see VirtualizationNodeStyleDecorator
   * @see VirtualizationEdgeStyleDecorator
   * @see VirtualizationLabelStyleDecorator
   * @see LargeGraphsDemo#updateStyles()
   */
  boolean isVirtualizationDisabled() {
    return virtualizationDisabled;
  }

  /**
   * Sets a value indicating whether virtualization should be disabled at low zoom levels.
   * <p>
   * Virtualization in {@link CanvasControl} trims the JavaFX visual tree to the elements that are actually visible.
   * This works well when only a few hundred visuals need to be removed or added at a time but results in significant
   * stutter if thousands of elements are affected. The later happens typically while panning or zooming in large graph
   * at low zoom level.
   * </p>
   * <p>
   * To avoid this, virtualization should be disabled at low zoom levels but kept enabled at higher zoom levels, as it
   * will lower memory usage (and improve other performance figures).
   * </p>
   * <p>
   * Note that these values are very sensitive to the displayed graph and its layout as the values depend mostly on how
   * many items are visible at once and thus how many items are likely to be virtualized when zooming or panning. In
   * this demo labels are always virtualized because we assume that labels are not visible at zoom levels so low that
   * virtualization would provide.
   * </p>
   * <p>
   * Disabling virtualization can be implemented by unconditionally returning {@code true} from a style's visibility
   * test. This causes the {@link CanvasControl} to always assume that the item is visible in the current viewport and
   * thus never remove it from the visual tree.
   * </p>
   * @see VirtualizationNodeStyleDecorator
   * @see VirtualizationEdgeStyleDecorator
   * @see VirtualizationLabelStyleDecorator
   * @see LargeGraphsDemo#updateStyles()
   */
  void setVirtualizationDisabled(boolean virtualizationDisabled) {
    this.virtualizationDisabled = virtualizationDisabled;
    settingChanged("VirtualizationDisabled");
  }

  /**
   * Gets the zoom level below which virtualization is disabled for nodes.
   * <p>
   * This setting has no effect unless {@link #isVirtualizationDisabled()} is {@code true}.
   * </p>
   */
  double getNodeVirtualizationThreshold() {
    return nodeVirtualizationThreshold;
  }

  /**
   * Sets the zoom level below which virtualization is disabled for nodes.
   * <p>
   * This setting has no effect unless {@link #isVirtualizationDisabled()} is {@code true}.
   * </p>
   */
  void setNodeVirtualizationThreshold(double nodeVirtualizationThreshold) {
    this.nodeVirtualizationThreshold = nodeVirtualizationThreshold;
    settingChanged("NodeVirtualizationThreshold");
  }

  /**
   * Gets the zoom level below which virtualization is disabled for edges.
   * <p>
   * This setting has no effect unless {@link #isVirtualizationDisabled()} is {@code true}.
   * </p>
   */
  double getEdgeVirtualizationThreshold() {
    return edgeVirtualizationThreshold;
  }

  /**
   * Sets the zoom level below which virtualization is disabled for edges.
   * <p>
   * This setting has no effect unless {@link #isVirtualizationDisabled()} is {@code true}.
   * </p>
   */
  void setEdgeVirtualizationThreshold(double edgeVirtualizationThreshold) {
    this.edgeVirtualizationThreshold = edgeVirtualizationThreshold;
    settingChanged("EdgeVirtualizationThreshold");
  }

  // endregion

  // region Selection optimizations

  /**
   * Gets a value indicating whether selection handles should be hidden.
   * <p>
   * Selection handles come in a set of eight per selected node (and two per selected edge). Since performance is very
   * dependent on the number of items in the visual tree that can have a large impact if many items are selected.
   * Disabling selection handles in large graphs is often a fairly safe choice, especially since showing the handles on
   * thousands of selected nodes doesn't gain the user much (apart from confusion ... and wait time).
   * </p>
   * @see LargeGraphsDemo#updateSelectionHandlesSetting
   */
  boolean isSelectionHandlesDisabled() {
    return selectionHandlesDisabled;
  }

  /**
   * Sets a value indicating whether selection handles should be hidden.
   * <p>
   * Selection handles come in a set of eight per selected node (and two per selected edge). Since performance is very
   * dependent on the number of items in the visual tree that can have a large impact if many items are selected.
   * Disabling selection handles in large graphs is often a fairly safe choice, especially since showing the handles on
   * thousands of selected nodes doesn't gain the user much (apart from confusion ... and wait time).
   * </p>
   * @see LargeGraphsDemo#updateSelectionHandlesSetting
   */
  void setSelectionHandlesDisabled(boolean selectionHandlesDisabled) {
    this.selectionHandlesDisabled = selectionHandlesDisabled;
    settingChanged("SelectionHandlesDisabled");
  }

  /**
   * Gets a value indicating whether to use custom selection decorators.
   * <p>
   * The default selection decorators are somewhat slow to draw. This doesn't matter with selections of a few elements,
   * but when you select thousands of items there is a noticeable impact. This setting enables the use of custom-written
   * selection decorators (implemented as node, edge and label styles) instead of the default ones.
   * </p>
   * @see FastNodeSelectionStyle
   * @see FastEdgeSelectionStyle
   * @see FastLabelSelectionStyle
   * @see LargeGraphsDemo#setSelectionDecorators
   */
  boolean isCustomSelectionDecoratorEnabled() {
    return customSelectionDecoratorEnabled;
  }

  /**
   * Sets a value indicating whether to use custom selection decorators.
   * <p>
   * The default selection decorators are somewhat slow to draw. This doesn't matter with selections of a few elements,
   * but when you select thousands of items there is a noticeable impact. This setting enables the use of custom-written
   * selection decorators (implemented as node, edge and label styles) instead of the default ones.
   * </p>
   * @see FastNodeSelectionStyle
   * @see FastEdgeSelectionStyle
   * @see FastLabelSelectionStyle
   * @see LargeGraphsDemo#setSelectionDecorators
   */
  void setCustomSelectionDecoratorEnabled(boolean customSelectionDecoratorEnabled) {
    this.customSelectionDecoratorEnabled = customSelectionDecoratorEnabled;
    settingChanged("CustomSelectionDecoratorEnabled");
  }

  // endregion

  // region Label Model Baking

  /**
   * Gets a value indicating whether label model baking should be enabled.
   * <p>
   * Positioning labels can be expensive, since they are usually anchored to their owner. Thus, to determine a label's
   * position (which is needed for hit-testing and visibility checks) the owner's position needs to be known, too. If the
   * graph is known to be static (or changes to the graph are tightly controlled) we can simply replace all label's models
   * with instances of {@link FreeLabelModel} which records an absolute position and thus is much cheaper to calculate.
   * </p>
   * @see LargeGraphsDemo#updateLabelModelBakingSetting
   */
  boolean isLabelModelBakingEnabled() {
    return labelModelBakingEnabled;
  }

  /**
   * Sets a value indicating whether label model baking should be enabled.
   * <p>
   * Positioning labels can be expensive, since they are usually anchored to their owner. Thus, to determine a label's
   * position (which is needed for hit-testing and visibility checks) the owner's position needs to be known, too. If the
   * graph is known to be static (or changes to the graph are tightly controlled) we can simply replace all label's models
   * with instances of {@link FreeLabelModel} which records an absolute position and thus is much cheaper to calculate.
   * </p>
   * @see LargeGraphsDemo#updateLabelModelBakingSetting
   */
  void setLabelModelBakingEnabled(boolean labelModelBakingEnabled) {
    this.labelModelBakingEnabled = labelModelBakingEnabled;
    settingChanged("LabelModelBakingEnabled");
  }

  // endregion

  // region PathRenderOptimization

  /**
   * Gets a value indicating whether {@link PathBasedEdgeStyleRenderer} and {@link EdgeDecorationInstaller} uses
   * {@link Line}s, {@link QuadCurve}s and {@link CubicCurve}s instead of a {@link Path} to render the edge path.
   * <p>
   * This results in overlapping shapes at the bends of the path but performs better then using {@link PathRenderPolicy#PATH}.
   * The visualization at bends can be improved by using {@link StrokeLineCap#ROUND} as {@link Pen#getLineCap()}.
   */
  boolean isPathRenderOptimizationEnabled() {
    return pathRenderOptimizationEnabled;
  }

  /**
   * Sets a value indicating whether {@link PathBasedEdgeStyleRenderer} and {@link EdgeDecorationInstaller} uses
   * {@link Line}s, {@link QuadCurve}s and {@link CubicCurve}s instead of a {@link Path} to render the edge path.
   * <p>
   * This results in overlapping shapes at the bends of the path but performs better then using {@link PathRenderPolicy#PATH}.
   * The visualization at bends can be improved by using {@link StrokeLineCap#ROUND} as {@link Pen#getLineCap()}.
   */
  void setPathRenderOptimizationEnabled(boolean pathRenderOptimizationEnabled) {
    this.pathRenderOptimizationEnabled = pathRenderOptimizationEnabled;
    settingChanged("PathRenderOptimizationEnabled");
  }

  // endregion

  // region Property Changed Callback

  private Consumer<String> changedCallback;

  /**
   * Gets the Consumer that is called with the property name if any property has changed.
   */
  private Consumer<String> getChangedCallback() {
    return changedCallback;
  }

  /**
   * Sets the Consumer that is called with the property name if any property has changed.
   */
  void setChangedCallback(Consumer<String> changedCallback) {
    this.changedCallback = changedCallback;
  }

  /**
   * Triggers the {@link #getChangedCallback()} when any property has changed.
   * @param propertyName The name of the property to trigger.
   */
  private void settingChanged(String propertyName){
    if (changedCallback != null) {
      changedCallback.accept(propertyName);
    }
  }

  // endregion

  /**
   * Returns a copy of the given {@link PerformanceSettings} instance.
   * @param p The {@link PerformanceSettings} to copy.
   * @return The newly created copy of <code>p</code>
   */
  public static PerformanceSettings getCopy(PerformanceSettings p) {
    try {
      return (PerformanceSettings) p.clone();
    } catch (CloneNotSupportedException e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}

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
package com.yworks.yedlite.parts;

import org.eclipse.e4.core.services.nls.Message;

/**
 * Helper class containing the messages in the property and layout panels.
 */
@Message(contributionURI = "platform:/plugin/com.yworks.yEdLite/")
public class Messages {
  public String PropertiesPart_NoItemSelected;

  public String PropertiesPart_NodeProperties;
  public String PropertiesPart_GroupNode;
  public String PropertiesPart_FillColor;
  public String PropertiesPart_DropShadow;
  public String PropertiesPart_ShapeNode;
  public String PropertiesPart_Shape;
  public String PropertiesPart_Inset;
  public String PropertiesPart_Radius;
  public String PropertiesPart_LabelInsetsColor;
  public String PropertiesPart_EdgeProperties;
  public String PropertiesPart_LabelText;
  public String PropertiesPart_Smoothing;
  public String PropertiesPart_SourceArrow;
  public String PropertiesPart_TargetArrow;
  public String PropertiesPart_ArrowType;
  public String PropertiesPart_ArrowGeometry;
  public String PropertiesPart_ArrowCropLength;
  public String PropertiesPart_ArrowScaling;
  public String PropertiesPart_Pen;
  public String PropertiesPart_PenColor;
  public String PropertiesPart_PenThickness;
  public String PropertiesPart_PenDashStyle;

  public String LayoutPart_Apply;
  public String LayoutPart_LayoutHierarchic;
  public String LayoutPart_LayoutOrganic;
  public String LayoutPart_LayoutOrthogonal;
  public String LayoutPart_EdgeRouter;


  public String HierarchicLayoutConfig;
  public String HierarchicLayoutConfig_GeneralGroup;
  public String HierarchicLayoutConfig_GeneralGroup_InteractionGroup;
  public String HierarchicLayoutConfig_GeneralGroup_InteractionGroup_SelectedElementsIncrementallyItem;
  public String HierarchicLayoutConfig_GeneralGroup_InteractionGroup_UseDrawingAsSketchItem;
  public String HierarchicLayoutConfig_GeneralGroup_OrientationItem;
  public String HierarchicLayoutConfig_GeneralGroup_OrientationItem_value_TopToBottom;
  public String HierarchicLayoutConfig_GeneralGroup_OrientationItem_value_LeftToRight;
  public String HierarchicLayoutConfig_GeneralGroup_OrientationItem_value_BottomToTop;
  public String HierarchicLayoutConfig_GeneralGroup_OrientationItem_value_RightToLeft;
  public String HierarchicLayoutConfig_GeneralGroup_LayoutComponentsSeparatelyItem;
  public String HierarchicLayoutConfig_GeneralGroup_SymmetricPlacementItem;
  public String HierarchicLayoutConfig_GeneralGroup_MaximumDurationItem;
  public String HierarchicLayoutConfig_GeneralGroup_DistanceGroup;
  public String HierarchicLayoutConfig_GeneralGroup_DistanceGroup_NodeToNodeDistanceItem;
  public String HierarchicLayoutConfig_GeneralGroup_DistanceGroup_NodeToEdgeDistanceItem;
  public String HierarchicLayoutConfig_GeneralGroup_DistanceGroup_EdgeToEdgeDistanceItem;
  public String HierarchicLayoutConfig_GeneralGroup_DistanceGroup_MinimumLayerDistanceItem;
  public String HierarchicLayoutConfig_EdgeSettingsGroup;
  public String HierarchicLayoutConfig_EdgeSettingsGroup_EdgeRoutingItem;
  public String HierarchicLayoutConfig_EdgeSettingsGroup_EdgeRoutingItem_value_Octilinear;
  public String HierarchicLayoutConfig_EdgeSettingsGroup_EdgeRoutingItem_value_Orthogonal;
  public String HierarchicLayoutConfig_EdgeSettingsGroup_EdgeRoutingItem_value_Polyline;
  public String HierarchicLayoutConfig_EdgeSettingsGroup_BackloopRoutingItem;
  public String HierarchicLayoutConfig_EdgeSettingsGroup_AutomaticEdgeGroupingEnabledItem;
  public String HierarchicLayoutConfig_EdgeSettingsGroup_DirectGroupContentRoutingItem;
  public String HierarchicLayoutConfig_EdgeSettingsGroup_MinimumFirstSegmentLengthItem;
  public String HierarchicLayoutConfig_EdgeSettingsGroup_MinimumLastSegmentLengthItem;
  public String HierarchicLayoutConfig_EdgeSettingsGroup_MinimumEdgeLengthItem;
  public String HierarchicLayoutConfig_EdgeSettingsGroup_MinimumEdgeDistanceItem;
  public String HierarchicLayoutConfig_EdgeSettingsGroup_MinimumSlopeItem;
  public String HierarchicLayoutConfig_RankGroup;
  public String HierarchicLayoutConfig_RankGroup_RankingPolicyItem;
  public String HierarchicLayoutConfig_RankGroup_RankingPolicyItem_value_HierarchicalOptimal;
  public String HierarchicLayoutConfig_RankGroup_RankingPolicyItem_value_HierarchicalTightTreeHeuristic;
  public String HierarchicLayoutConfig_RankGroup_RankingPolicyItem_value_BfsLayering;
  public String HierarchicLayoutConfig_RankGroup_RankingPolicyItem_value_FromSketch;
  public String HierarchicLayoutConfig_RankGroup_RankingPolicyItem_value_HierarchicalTopmost;
  public String HierarchicLayoutConfig_RankGroup_LayerAlignmentItem;
  public String HierarchicLayoutConfig_RankGroup_LayerAlignmentItem_value_TopBorderOfNodes;
  public String HierarchicLayoutConfig_RankGroup_LayerAlignmentItem_value_CenterOfNodes;
  public String HierarchicLayoutConfig_RankGroup_LayerAlignmentItem_value_BottomBorderOfNodes;
  public String HierarchicLayoutConfig_RankGroup_ComponentArrangementPolicyItem;
  public String HierarchicLayoutConfig_RankGroup_ComponentArrangementPolicyItem_value_Topmost;
  public String HierarchicLayoutConfig_RankGroup_ComponentArrangementPolicyItem_value_Compact;
  public String HierarchicLayoutConfig_RankGroup_NodeCompactionItem;
  public String HierarchicLayoutConfig_RankGroup_SketchGroup;
  public String HierarchicLayoutConfig_RankGroup_SketchGroup_ScaleItem;
  public String HierarchicLayoutConfig_RankGroup_SketchGroup_HaloItem;
  public String HierarchicLayoutConfig_RankGroup_SketchGroup_MinimumSizeItem;
  public String HierarchicLayoutConfig_RankGroup_SketchGroup_MaximumSizeItem;
  public String HierarchicLayoutConfig_LabelingGroup;
  public String HierarchicLayoutConfig_LabelingGroup_NodePropertiesGroup;
  public String HierarchicLayoutConfig_LabelingGroup_NodePropertiesGroup_ConsiderNodeLabelsItem;
  public String HierarchicLayoutConfig_LabelingGroup_EdgePropertiesGroup;
  public String HierarchicLayoutConfig_LabelingGroup_EdgePropertiesGroup_EdgeLabelingItem;
  public String HierarchicLayoutConfig_LabelingGroup_EdgePropertiesGroup_EdgeLabelingItem_value_None;
  public String HierarchicLayoutConfig_LabelingGroup_EdgePropertiesGroup_EdgeLabelingItem_value_Integrated;
  public String HierarchicLayoutConfig_LabelingGroup_EdgePropertiesGroup_EdgeLabelingItem_value_Generic;
  public String HierarchicLayoutConfig_LabelingGroup_EdgePropertiesGroup_CompactEdgeLabelPlacementItem;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Parallel;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Orthogonal;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Horizontal;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Vertical;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_Anywhere;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_AtSource;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_AtTarget;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_Centered;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_AtSourcePort;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_AtTargetPort;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_Anywhere;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_OnEdge;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_Left;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_Right;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_LeftOrRight;
  public String HierarchicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementDistanceItem;
  public String HierarchicLayoutConfig_GroupingGroup;
  public String HierarchicLayoutConfig_GroupingGroup_GroupLayeringStrategyItem;
  public String HierarchicLayoutConfig_GroupingGroup_GroupLayeringStrategyItem_value_LayoutGroups;
  public String HierarchicLayoutConfig_GroupingGroup_GroupLayeringStrategyItem_value_IgnoreGroups;
  public String HierarchicLayoutConfig_GroupingGroup_GroupAlignmentItem;
  public String HierarchicLayoutConfig_GroupingGroup_GroupAlignmentItem_value_Top;
  public String HierarchicLayoutConfig_GroupingGroup_GroupAlignmentItem_value_Center;
  public String HierarchicLayoutConfig_GroupingGroup_GroupAlignmentItem_value_Bottom;
  public String HierarchicLayoutConfig_GroupingGroup_GroupEnableCompactionItem;
  public String HierarchicLayoutConfig_GroupingGroup_GroupHorizontalCompactionItem;
  public String HierarchicLayoutConfig_GroupingGroup_GroupHorizontalCompactionItem_value_Weak;
  public String HierarchicLayoutConfig_GroupingGroup_GroupHorizontalCompactionItem_value_Strong;
  public String HierarchicLayoutConfig_SwimlanesGroup;
  public String HierarchicLayoutConfig_SwimlanesGroup_TreatRootGroupAsSwimlanesItem;
  public String HierarchicLayoutConfig_SwimlanesGroup_UseOrderFromSketchItem;
  public String HierarchicLayoutConfig_SwimlanesGroup_SwimlineSpacingItem;
  public String HierarchicLayoutConfig_GridGroup;
  public String HierarchicLayoutConfig_GridGroup_GridEnabledItem;
  public String HierarchicLayoutConfig_GridGroup_GridSpacingItem;
  public String HierarchicLayoutConfig_GridGroup_GridPortAssignmentItem;
  public String HierarchicLayoutConfig_GridGroup_GridPortAssignmentItem_value_Default;
  public String HierarchicLayoutConfig_GridGroup_GridPortAssignmentItem_value_OnGrid;
  public String HierarchicLayoutConfig_GridGroup_GridPortAssignmentItem_value_OnSubgrid;

  public String OrganicLayoutConfig;
  public String OrganicLayoutConfig_VisualGroup;
  public String OrganicLayoutConfig_VisualGroup_ScopeItem;
  public String OrganicLayoutConfig_VisualGroup_ScopeItem_value_All;
  public String OrganicLayoutConfig_VisualGroup_ScopeItem_value_MainlySelection;
  public String OrganicLayoutConfig_VisualGroup_ScopeItem_value_Selection;
  public String OrganicLayoutConfig_VisualGroup_PreferredEdgeLengthItem;
  public String OrganicLayoutConfig_VisualGroup_MinimumNodeDistanceItem;
  public String OrganicLayoutConfig_VisualGroup_NodeOverlapsAllowedItem;
  public String OrganicLayoutConfig_VisualGroup_NodeEdgeOverlapsAvoidedItem;
  public String OrganicLayoutConfig_VisualGroup_CompactnessFactorItem;
  public String OrganicLayoutConfig_VisualGroup_UseAutoClusteringItem;
  public String OrganicLayoutConfig_VisualGroup_AutoClusteringQualityItem;
  public String OrganicLayoutConfig_RestrictionsGroup;
  public String OrganicLayoutConfig_RestrictionsGroup_RestrictOutputItem;
  public String OrganicLayoutConfig_RestrictionsGroup_RestrictOutputItem_value_Unrestricted;
  public String OrganicLayoutConfig_RestrictionsGroup_RestrictOutputItem_value_Rectangular;
  public String OrganicLayoutConfig_RestrictionsGroup_RestrictOutputItem_value_AspectRatio;
  public String OrganicLayoutConfig_RestrictionsGroup_RestrictOutputItem_value_Elliptical;
  public String OrganicLayoutConfig_RestrictionsGroup_CageGroup;
  public String OrganicLayoutConfig_RestrictionsGroup_CageGroup_RectCageUseViewItem;
  public String OrganicLayoutConfig_RestrictionsGroup_CageGroup_CageXItem;
  public String OrganicLayoutConfig_RestrictionsGroup_CageGroup_CageYItem;
  public String OrganicLayoutConfig_RestrictionsGroup_CageGroup_CageWidthItem;
  public String OrganicLayoutConfig_RestrictionsGroup_CageGroup_CageHeightItem;
  public String OrganicLayoutConfig_RestrictionsGroup_ARGroup;
  public String OrganicLayoutConfig_RestrictionsGroup_ARGroup_ArCageUseViewItem;
  public String OrganicLayoutConfig_RestrictionsGroup_ARGroup_CageRatioItem;
  public String OrganicLayoutConfig_GroupingGroup;
  public String OrganicLayoutConfig_GroupingGroup_GroupLayoutPolicyItem;
  public String OrganicLayoutConfig_GroupingGroup_GroupLayoutPolicyItem_value_LayoutGroups;
  public String OrganicLayoutConfig_GroupingGroup_GroupLayoutPolicyItem_value_FixBoundsOfGroups;
  public String OrganicLayoutConfig_GroupingGroup_GroupLayoutPolicyItem_value_FixContentsOfGroups;
  public String OrganicLayoutConfig_GroupingGroup_GroupLayoutPolicyItem_value_IgnoreGroups;
  public String OrganicLayoutConfig_AlgorithmGroup;
  public String OrganicLayoutConfig_AlgorithmGroup_QualityTimeRatioItem;
  public String OrganicLayoutConfig_AlgorithmGroup_MaximumDurationItem;
  public String OrganicLayoutConfig_AlgorithmGroup_ActivateDeterministicModeItem;
  public String OrganicLayoutConfig_LabelingGroup;
  public String OrganicLayoutConfig_LabelingGroup_NodePropertiesGroup;
  public String OrganicLayoutConfig_LabelingGroup_NodePropertiesGroup_ConsiderNodeLabelsItem;
  public String OrganicLayoutConfig_LabelingGroup_EdgePropertiesGroup;
  public String OrganicLayoutConfig_LabelingGroup_EdgePropertiesGroup_EdgeLabelingItem;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Parallel;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Orthogonal;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Horizontal;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Vertical;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_Anywhere;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_AtSource;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_AtTarget;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_Centered;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_Anywhere;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_OnEdge;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_Left;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_Right;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_LeftOrRight;
  public String OrganicLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementDistanceItem;
  public String OrganicLayoutConfig_SubstructureLayoutGroup;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_CycleSubstructureStyleItem;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_CycleSubstructureStyleItem_value_Ignore;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_CycleSubstructureStyleItem_value_Circular;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_ChainSubstructureStyleItem;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_ChainSubstructureStyleItem_value_Ignore;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_ChainSubstructureStyleItem_value_StraightLine;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_ChainSubstructureStyleItem_value_Rectangular;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_StarSubstructureStyleItem;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_StarSubstructureStyleItem_value_Ignore;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_StarSubstructureStyleItem_value_Circular;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_StarSubstructureStyleItem_value_Radial;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_StarSubstructureStyleItem_value_SeparatedRadial;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_ParallelSubstructureStyleItem;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_ParallelSubstructureStyleItem_value_Ignore;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_ParallelSubstructureStyleItem_value_Rectangular;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_ParallelSubstructureStyleItem_value_Radial;
  public String OrganicLayoutConfig_SubstructureLayoutGroup_ParallelSubstructureStyleItem_value_StraightLine;

  public String OrthogonalLayoutConfig;
  public String OrthogonalLayoutConfig_LayoutGroup;
  public String OrthogonalLayoutConfig_LayoutGroup_StyleItem;
  public String OrthogonalLayoutConfig_LayoutGroup_StyleItem_value_Normal;
  public String OrthogonalLayoutConfig_LayoutGroup_StyleItem_value_NormalTrees;
  public String OrthogonalLayoutConfig_LayoutGroup_StyleItem_value_UniformNodeSizes;
  public String OrthogonalLayoutConfig_LayoutGroup_StyleItem_value_NodeBoxes;
  public String OrthogonalLayoutConfig_LayoutGroup_StyleItem_value_Mixed;
  public String OrthogonalLayoutConfig_LayoutGroup_StyleItem_value_NodeBoxesFixedNodeSize;
  public String OrthogonalLayoutConfig_LayoutGroup_StyleItem_value_MixedFixedNodeSize;
  public String OrthogonalLayoutConfig_LayoutGroup_GridSpacingItem;
  public String OrthogonalLayoutConfig_LayoutGroup_EdgeLengthReductionItem;
  public String OrthogonalLayoutConfig_LayoutGroup_FromSketchModeItem;
  public String OrthogonalLayoutConfig_LayoutGroup_CrossingReductionItem;
  public String OrthogonalLayoutConfig_LayoutGroup_PerceivedBendsPostprocessingItem;
  public String OrthogonalLayoutConfig_LayoutGroup_RandomizationItem;
  public String OrthogonalLayoutConfig_LayoutGroup_FaceMaximizationItem;
  public String OrthogonalLayoutConfig_LabelingGroup;
  public String OrthogonalLayoutConfig_LabelingGroup_NodePropertiesGroup;
  public String OrthogonalLayoutConfig_LabelingGroup_NodePropertiesGroup_ConsiderNodeLabelsItem;
  public String OrthogonalLayoutConfig_LabelingGroup_EdgePropertiesGroup;
  public String OrthogonalLayoutConfig_LabelingGroup_EdgePropertiesGroup_EdgeLabelingItem;
  public String OrthogonalLayoutConfig_LabelingGroup_EdgePropertiesGroup_EdgeLabelingItem_value_None;
  public String OrthogonalLayoutConfig_LabelingGroup_EdgePropertiesGroup_EdgeLabelingItem_value_Integrated;
  public String OrthogonalLayoutConfig_LabelingGroup_EdgePropertiesGroup_EdgeLabelingItem_value_Generic;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Parallel;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Orthogonal;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Horizontal;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Vertical;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_Anywhere;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_AtSource;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_AtTarget;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_Centered;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_Anywhere;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_OnEdge;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_Left;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_Right;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_LeftOrRight;
  public String OrthogonalLayoutConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementDistanceItem;
  public String OrthogonalLayoutConfig_EdgesGroup;
  public String OrthogonalLayoutConfig_EdgesGroup_MinimumFirstSegmentLengthItem;
  public String OrthogonalLayoutConfig_EdgesGroup_MinimumSegmentLengthItem;
  public String OrthogonalLayoutConfig_EdgesGroup_MinimumLastSegmentLengthItem;
  public String OrthogonalLayoutConfig_EdgesGroup_ConsiderDirectionOfSelectedEdges;
  public String OrthogonalLayoutConfig_GroupingGroup;
  public String OrthogonalLayoutConfig_GroupingGroup_GroupLayoutPolicyItem;
  public String OrthogonalLayoutConfig_GroupingGroup_GroupLayoutPolicyItem_value_LayoutGroups;
  public String OrthogonalLayoutConfig_GroupingGroup_GroupLayoutPolicyItem_value_FixContentsOfGroups;
  public String OrthogonalLayoutConfig_GroupingGroup_GroupLayoutPolicyItem_value_IgnoreGroups;

  public String PolylineEdgeRouterConfig;
  public String PolylineEdgeRouterConfig_LayoutGroup;
  public String PolylineEdgeRouterConfig_LayoutGroup_ScopeItem;
  public String PolylineEdgeRouterConfig_LayoutGroup_ScopeItem_value_AllEdges;
  public String PolylineEdgeRouterConfig_LayoutGroup_ScopeItem_value_AffectedEdges;
  public String PolylineEdgeRouterConfig_LayoutGroup_ScopeItem_value_EdgesAtAffectedNodes;
  public String PolylineEdgeRouterConfig_LayoutGroup_OptimizationStrategyItem;
  public String PolylineEdgeRouterConfig_LayoutGroup_OptimizationStrategyItem_value_Balanced;
  public String PolylineEdgeRouterConfig_LayoutGroup_OptimizationStrategyItem_value_LessBends;
  public String PolylineEdgeRouterConfig_LayoutGroup_OptimizationStrategyItem_value_LessCrossings;
  public String PolylineEdgeRouterConfig_LayoutGroup_OptimizationStrategyItem_value_ShorterEdges;
  public String PolylineEdgeRouterConfig_LayoutGroup_MonotonicRestrictionItem;
  public String PolylineEdgeRouterConfig_LayoutGroup_MonotonicRestrictionItem_value_None;
  public String PolylineEdgeRouterConfig_LayoutGroup_MonotonicRestrictionItem_value_Horizontal;
  public String PolylineEdgeRouterConfig_LayoutGroup_MonotonicRestrictionItem_value_Vertical;
  public String PolylineEdgeRouterConfig_LayoutGroup_MonotonicRestrictionItem_value_Both;
  public String PolylineEdgeRouterConfig_LayoutGroup_EnableReroutingItem;
  public String PolylineEdgeRouterConfig_LayoutGroup_MaximumDurationItem;
  public String PolylineEdgeRouterConfig_DistancesGroup;
  public String PolylineEdgeRouterConfig_DistancesGroup_MinimumEdgeToEdgeDistanceItem;
  public String PolylineEdgeRouterConfig_DistancesGroup_MinimumNodeToEdgeDistanceItem;
  public String PolylineEdgeRouterConfig_DistancesGroup_MinimumNodeCornerDistanceItem;
  public String PolylineEdgeRouterConfig_DistancesGroup_MinimumFirstSegmentLengthItem;
  public String PolylineEdgeRouterConfig_DistancesGroup_MinimumLastSegmentLengthItem;
  public String PolylineEdgeRouterConfig_GridGroup;
  public String PolylineEdgeRouterConfig_GridGroup_GridEnabledItem;
  public String PolylineEdgeRouterConfig_GridGroup_GridSpacingItem;
  public String PolylineEdgeRouterConfig_PolylineGroup;
  public String PolylineEdgeRouterConfig_PolylineGroup_EnablePolylineRoutingItem;
  public String PolylineEdgeRouterConfig_PolylineGroup_PreferredPolylineSegmentLengthItem;
  public String PolylineEdgeRouterConfig_LabelingGroup;
  public String PolylineEdgeRouterConfig_LabelingGroup_NodePropertiesGroup;
  public String PolylineEdgeRouterConfig_LabelingGroup_NodePropertiesGroup_ConsiderNodeLabelsItem;
  public String PolylineEdgeRouterConfig_LabelingGroup_EdgePropertiesGroup;
  public String PolylineEdgeRouterConfig_LabelingGroup_EdgePropertiesGroup_ConsiderEdgeLabelsItem;
  public String PolylineEdgeRouterConfig_LabelingGroup_EdgePropertiesGroup_EdgeLabelingItem;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Parallel;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Orthogonal;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Horizontal;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementOrientationItem_value_Vertical;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_Anywhere;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_AtSource;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_AtTarget;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementAlongEdgeItem_value_Centered;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_Anywhere;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_OnEdge;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_Left;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_Right;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementSideOfEdgeItem_value_LeftOrRight;
  public String PolylineEdgeRouterConfig_LabelingGroup_PreferredPlacementGroup_LabelPlacementDistanceItem;

  public String About_Dialog_Title;
  public String About_Dialog_Content;
  public String Exit_Dialog_Title;
  public String Exit_Dialog_Content;
}

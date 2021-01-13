
# yFiles for JavaFX Programming Samples

This repository contains source code demos and tutorials that use the commercial
[yFiles for JavaFX](https://www.yworks.com/yfilesjavafx) software programming
library for the visualization of graphs, diagrams, and networks.
The library itself is __*not*__ part of this repository.

[![yFiles for HTML Demos](./demo-grid.png)](https://live.yworks.com/yfiles-for-html)

# Running the Demos

For most of these demos equivalent ones based on
[yFiles for HTML](https://www.yworks.com/yfileshtml) are hosted
[online here](https://live.yworks.com/yfiles-for-html) for everyone to play with.
Developers should
[evaluate the library](https://www.yworks.com/products/yfiles-for-javafx/evaluate),
instead. The evaluation version also contains these demos and the necessary
library to execute the code.
  
## [Complete Demos](demos/src/complete/)

  

 This folder and its subfolders contain demo applications which make use of the different features of yFiles for JavaFX.   

| Demo | Description |
|------|-------------|
|[SimpleEditor](demos/src/complete/simpleeditor/)| Shows a graph editor which demonstrates the editing features of yFiles for JavaFX. |
|[OrgChart](demos/src/complete/orgchart/)| View and manipulate an organization chart. |
|[RotatableNodes](demos/src/complete/rotatablenodes/)| Shows how support for rotated node visualizations can be implemented on top of the yFiles library. |
|[AggregateGraphWrapper](demos/src/complete/aggregategraphwrapper/)| Shows how to analyze a graph by __aggregating groups of nodes__ . |
|[CollapsibleTree](demos/src/complete/collapse/)| Interactively collapse and expand subgraphs. |
|[IsometricDrawing](demos/src/complete/isometric/)| Displays graphs in an isometric fashion to create an impression of a 3-dimensional view. |
|[LogicGate](demos/src/complete/logicgate/)| An editor for networks of logic gates with dedicated ports for incoming and outgoing connections. |
|[BpmnEditor](demos/src/complete/bpmn/)| Create and edit Business Process Diagrams. |
|[HierarchicGrouping](demos/src/complete/hierarchicgrouping/)| Organize subgraphs in groups and folders and interactively expand and collapse them. |
|[TableEditor](demos/src/complete/tableeditor/)| Interactive editing of tables using TableEditorInputMode. |
  
## [Layout Demos](demos/src/layout/)

  

 This folder and its subfolders contain demo applications which make use of the different layout algorithms of the layout component of yFiles for JavaFX.   

| Demo | Description |
|------|-------------|
|[LayoutStyles](demos/src/layout/layoutstyles/)| Play with the most used layout algorithms of yFiles. |
|[HierarchicLayout](demos/src/layout/hierarchiclayout/)| Showcase of one of our central layout algorithms, the HierarchicLayout. |
|[InteractiveOrganicLayout](demos/src/layout/interactiveorganic/)| Use InteractiveOrganicLayout for organic layout in interactive environments. |
|[FamilyTree](demos/src/layout/familytree/)| This demo shows how genealogical graphs (family trees) can be visualized. |
|[MarqueeClearAreaLayout](demos/src/layout/cleararea/)| Shows how to interactively move graph elements around a marquee rectangle in a given graph layout so that the modifications in the graph are minimal. |
|[CriticalPaths](demos/src/layout/criticalpaths/)| This demo shows how to emphazise important paths with hierarchic and tree layout algorithms. |
|[EdgeBundling](demos/src/layout/edgebundling/)| Demonstrates edge bundling for different layout styles. |
|[EdgeGrouping](demos/src/layout/edgegrouping/)| Shows the effects of edge and port grouping when arranging graphs with HierarchicLayout. |
|[FillAreaAfterDeletion](demos/src/layout/fillarea/)| Shows howto fill free space in the graph after deleting nodes. |
|[PartialLayout](demos/src/layout/partiallayout/)| Shows how to arrange some elements in a graph while keeping other elements fixed. |
|[PartitionGrid](demos/src/layout/partitiongrid/)| Demonstrates the usage of a `PartitionGrid` for hierarchic and organic layout calculations. |
|[Sankey](demos/src/layout/sankey/)| Uses Hierarchic Layout to arrange Sankey diagrams. |
|[SplitEdges](demos/src/layout/splitedges/)| Shows how to align edges at group nodes using RecursiveGroupLayout together with HierarchicLayout. |
|[TreeLayout](demos/src/layout/treelayout/)| Demonstrates the tree layout style and the different ways in which this layout can arrange a node and its children. |
|[TreeMap](demos/src/layout/treemap/)| Shows disk usage of a directory tree with the Tree Map layout. |
  
## [Input Demos](demos/src/input/)

  

 This folder and its subfolders contain demo applications which demonstrate how to use and customize the graph editing features provided by yFiles for JavaFX.   

| Demo | Description |
|------|-------------|
|[CustomLabelModel](demos/src/input/customlabelmodel/)| Customize label placement model. |
|[CustomPortModel](demos/src/input/customportmodel/)| Customize port location model. |
|[ContextMenu](demos/src/input/contextmenu/)| Create a context menu for graph items and manage it. |
|[CustomSnapping](demos/src/input/customsnapping/)| This demo shows how to customize `SnapLine` behaviour. |
|[DragAndDrop](demos/src/input/draganddrop/)| Shows the drag and drop support provided by yFiles for JavaFX. |
|[EdgeReconnectionPortCandidateProvider](demos/src/input/edgereconnection/)| Demo code that shows how to customize the reconnection behavior for existing edges. |
|[LabelHandleProvider](demos/src/input/labelhandleprovider/)| Enable interactive rotating and resizing labels. |
|[OrthogonalEdges](demos/src/input/orthogonaledges/)| Customize orthogonal edge editing. |
|[PortCandidateProvider](demos/src/input/portcandidateprovider/)| Customize the ports at which edges connect to nodes. |
|[PositionHandler](demos/src/input/positionhandler/)| Demo code that shows how to customize the movement behavior of `INode` s. |
|[ReparentHandler](demos/src/input/reparenthandler/)| Demo code that shows how to customize the reparent gesture in a grouped graph. |
|[ReshapeHandleProvider](demos/src/input/reshapehandleprovider/)| Customize the reshape behavior of nodes. |
|[SingleSelection](demos/src/input/singleselection/)| Configure the `GraphEditorInputMode` for single selection mode. |
|[SizeConstraintProvider](demos/src/input/sizeconstraintprovider/)| Demo code that shows how to customize the resizing behavior of `INode` s. |
  
## [Integration Demos](demos/src/integration/)

  

 This folder and its subfolders contain demo applications which illustrate the integration of yFiles for JavaFX with different GUI frameworks as well as integration of external libraries into yFiles for JavaFX.   

| Demo | Description |
|------|-------------|
|[EclipseRCP](demos/src-eclipsercp/integration/eclipsercp/)| Demo application that shows how to integrate yFiles for JavaFX in an Eclipse E4 RCP. |
|[Neo4j](demos/src-neo4j/integration/neo4j/)| Demo application that shows how to integrate Neo4j into yFiles for JavaFX. |
|[SwtDemo](demos/src-swt/integration/swt/)| Demo application that shows how to integrate yFiles for JavaFX in a Standard Widget Toolkit (SWT) application. |
|[SwingDemo](demos/src/integration/swing/)| Demo application that shows how to integrate yFiles for JavaFX in a Swing application. |
  
## [Viewer Demos](demos/src/viewer/)

  

 This folder and its subfolders contain demo applications which make use of the different features of the viewer component of yFiles for JavaFX.   

| Demo | Description |
|------|-------------|
|[Css](demos/src/viewer/css/)| Shows the possibilities to customize the visualizations in yFiles for JavaFX with the help of CSS. |
|[SVGImageExport](demos/src-svg/viewer/svgimageexport/)| Export a graph as a SVG image. |
|[BackgroundImage](demos/src/viewer/backgroundimage/)| Shows how to add background visualizations to a graph control. |
|[ClickableStyleDecorator](demos/src/viewer/clickablestyledecorator/)| Shows how to handle mouse clicks in specific areas of a node's visualization. |
|[EdgeToEdge](demos/src/viewer/edgetoedge/)| Shows edge-to-edge connections. |
|[GraphEvents](demos/src/viewer/events/)| Explore the different kinds of events dispatched by yFiles for JavaFX. |
|[Filtering](demos/src/viewer/filtering/)| Shows how to temporarily remove nodes or edges from the graph. |
|[FilteringWithFolding](demos/src/viewer/filteringandfolding/)| Shows how to combine yFiles [filtering](https://docs.yworks.com/yfilesjavafx/doc/api/#/dguide/filtering) and [folding](https://docs.yworks.com/yfilesjavafx/doc/api/#/dguide/folding) features. |
|[Folding](demos/src/viewer/folding/)| Shows how to use yFiles [folding](https://docs.yworks.com/yfilesjavafx/doc/api/#/dguide/folding) feature. |
|[GanttChartDemo](demos/src/viewer/ganttchart/)| Shows how to create a "Gantt chart" with yFiles for JavaFX. |
|[GraphCopy](demos/src/viewer/graphcopy/)| Shows how to copy a graph or sub graph. |
|[GraphMLCompatibility](demos/src/viewer/graphmlcompatibility/)| Shows how to enable read compatibility for GraphML files from older versions. |
|[GraphViewer](demos/src/viewer/graphviewer/)| A viewer which demonstrates different kinds of graphs created with yFiles for JavaFX. |
|[GridSnapping](demos/src/viewer/gridsnapping/)| Demonstrates how to enable grid snapping functionality for graph elements. |
|[ImageExport](demos/src/viewer/imageexport/)| Export a graph as a bitmap image. |
|[LargeGraphs](demos/src/viewer/largegraphs/)| Improve the rendering performance for very large graphs in yFiles for JavaFX. |
|[LevelOfDetail](demos/src/viewer/levelofdetail/)| Demonstrates how to change the level of detail when zooming in and out. |
|[Printing](demos/src/viewer/printing/)| Print the contents of a yFiles GraphControl. |
|[RenderingOrder](demos/src/viewer/renderingorder/)| Shows the effect of different rendering policies to the model items. |
|[SmartClickNavigation](demos/src/viewer/smartclicknavigation/)| Demonstrates how to navigate in a large graph. |
|[Snapping](demos/src/viewer/snapping/)| Demonstrates how to enable snapping functionality for graph elements. |
|[Tooltips](demos/src/viewer/tooltips/)| Demonstrates how to add tooltips to graph items. |
  
## [Style Demos](demos/src/style/)

  

 This folder and its subfolders contain demo applications which make use of the different features of the styles component of yFiles for JavaFX.   

| Demo | Description |
|------|-------------|
|[RichTextLabelStyle](demos/src/style/richtextlabelstyle/)| Using the JavaFX 8 Rich Text API in yFiles. |
|[SimpleCustomStyle](demos/src/style/simplecustomstyle/)| Shows how to implement sophisticated styles for graph objects in yFiles for JavaFX. |
|[TemplateStyle](demos/src/style/templatestyle/)| Shows how to use `TemplateNodeStyle` and `TemplateLabelStyle` to create complex node and label visualizations using FXML components. |
|[ZoomInvariantLabelStyleDemo](demos/src/style/zoominvariantlabelstyle/)| Demonstrates zoom-invariant label rendering. |
  
## [Graph Builder Demos](demos/src/builder/)

  

 This folder and its subfolders contain demo applications which demonstrate how to use the `GraphBuilder` classes for binding graph elements to business data in yFiles for JavaFX.   

| Demo | Description |
|------|-------------|
|[GraphBuilder](demos/src/builder/graphbuilder/)| Demonstrates creating a graph using the `GraphBuilder` class. |
|[InteractiveNodesGraphBuilder](demos/src/builder/interactivenodesgraphbuilder/)| Demonstrates creating a graph using class `AdjacentNodesGraphBuilder` . |
  
## [Analysis Demos](demos/src/analysis/)

  

 This folder and its subfolders contain demo applications which demonstrate some of the graph analysis algorithms available in yFiles for JavaFX.   

| Demo | Description |
|------|-------------|
|[GraphAnalysis](demos/src/analysis/graphanalysis/)| Algorithms to analyse the structure of a graph in yFiles for JavaFX. |
|[ShortestPath](demos/src/analysis/shortestpath/)| Usage and visualization of shortest path algorithms in yFiles for JavaFX. |
  
## [Deployment Demos](demos/src/deploy/)

  

 This folder and its subfolders contain demo applications which illustrate tasks for deployment of yFiles for JavaFX applications, e.g. obfuscation.   

| Demo | Description |
|------|-------------|
|[ObfuscationDemo](demos/src/deploy/obfuscation/)| Demonstrates the obfuscation of an yFiles for JavaFX application via yGuard. |
  
# Tutorials

The yFiles for JavaFX tutorials are extensive source code samples that present
the functionality of the yFiles for JavaFX library.

To navigate to a specific tutorial, just follow the corresponding link from the
table below.

## Available Tutorials

| Category | Description |
|----------|-------------|
|[Getting Started](./demos/src/tutorial01_GettingStarted/)| Introduces basic concepts as well as main features like custom styles, full user interaction, Undo/Redo, clipboard, I/O, grouping and folding.|
|[Custom Styles](./demos/src/tutorial02_CustomStyles/)| A step-by-step guide to customizing the visual representation of graph elements. This tutorial is intended for users who want to learn how to create custom styles from scratch.|


# License

Use of the software hosted in this repository is subject to the license terms of the corresponding yFiles for JavaFX license.
Owners of a valid software license for a yFiles for JavaFX version that these
demos are shipped with are allowed to use the demo source code as basis
for their own yFiles for JavaFX powered applications. Use of such programs is
governed by the rights and conditions as set out in the yFiles for JavaFX
license agreement. More details [here](./LICENSE). If in doubt, feel free to [contact](https://www.yworks.com/contact) the yFiles for JavaFX support team.

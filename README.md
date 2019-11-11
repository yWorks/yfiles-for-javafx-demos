
# yFiles for JavaFX Demo Sources

This repository contains source code demos that use the commercial [yFiles for JavaFX](https://www.yworks.com/yfilesjavafx) software programming library for the visualization of graphs, diagrams, and networks. The library itself is __*not*__ part of this repository.

[![yFiles for JavaFX Demos](./demo-grid.png)](https://live.yworks.com/yfiles-for-html)

# Running the Demos

For most of these demos equivalent ones based on [yFiles for HTML](https://www.yworks.com/yfileshtml)
are hosted [online here](https://live.yworks.com/yfiles-for-html) for everyone to play with. Developers should [evaluate the library](https://www.yworks.com/products/yfiles-for-javafx/evaluate), instead.
The evaluation version also contains these demos and the necessary library to execute the code.
  
## [Demo](demos/src/complete/README.md)

  

 This folder and its subfolders contain demo applications which make use of the different features of yFiles for JavaFX.   

| Demo | Description |
|------|:-----------:|
|[SimpleEditor](demos/src/complete/simpleeditor/README.md)| Shows a graph editor which demonstrates the editing features of yFiles for JavaFX. |
|[OrgChart](demos/src/complete/orgchart/README.md)| View and manipulate an organization chart. |
|[RotatableNodes](demos/src/complete/rotatablenodes/README.md)| Shows how support for rotated node visualizations can be implemented on top of the yFiles library. |
|[CollapsibleTree](demos/src/complete/collapse/README.md)| Interactively collapse and expand subgraphs. |
|[LogicGate](demos/src/complete/logicgate/README.md)| An editor for networks of logic gates with dedicated ports for incoming and outgoing connections. |
|[BpmnEditor](demos/src/complete/bpmn/README.md)| Create and edit Business Process Diagrams. |
|[HierarchicGrouping](demos/src/complete/hierarchicgrouping/README.md)| Organize subgraphs in groups and folders and interactively expand and collapse them. |
|[TableEditor](demos/src/complete/tableeditor/README.md)| Interactive editing of tables using TableEditorInputMode. |
  
## [Layout Demos](demos/src/layout/README.md)

  

 This folder and its subfolders contain demo applications which make use of the different layout algorithms of the layout component of yFiles for JavaFX.   

| Demo | Description |
|------|:-----------:|
|[LayoutStyles](demos/src/layout/layoutstyles/README.md)| Play with the most used layout algorithms of yFiles. |
|[HierarchicLayout](demos/src/layout/hierarchiclayout/README.md)| Showcase of one of our central layout algorithms, the HierarchicLayout. |
|[InteractiveOrganicLayout](demos/src/layout/interactiveorganic/README.md)| Use InteractiveOrganicLayout for organic layout in interactive environments. |
|[PartitionGrid](demos/src/layout/partitiongrid/README.md)| Demonstrates the usage of a *PartitionGrid* for hierarchic and organic layout calculations. |
  
## [Input Demos](demos/src/input/README.md)

  

 This folder and its subfolders contain demo applications which demonstrate how to use and customize the graph editing features provided by yFiles for JavaFX.   

| Demo | Description |
|------|:-----------:|
|[CustomPortModel](demos/src/input/customportmodel/README.md)| Customize port location model. |
|[ContextMenu](demos/src/input/contextmenu/README.md)| Create a context menu for graph items and manage it. |
|[CustomSnapping](demos/src/input/customsnapping/README.md)| This demo shows how to customize `SnapLine` behaviour. |
|[DragAndDrop](demos/src/input/draganddrop/README.md)| Shows the drag and drop support provided by yFiles for JavaFX. |
|[EdgeReconnectionPortCandidateProvider](demos/src/input/edgereconnection/README.md)| Demo code that shows how to customize the reconnection behavior for existing edges. |
|[OrthogonalEdges](demos/src/input/orthogonaledges/README.md)| Customize orthogonal edge editing. |
|[PortCandidateProvider](demos/src/input/portcandidateprovider/README.md)| Customize the ports at which edges connect to nodes. |
|[PositionHandler](demos/src/input/positionhandler/README.md)| Demo code that shows how to customize the movement behavior of INodes. |
|[ReparentHandler](demos/src/input/reparenthandler/README.md)| Demo code that shows how to customize the reparent gesture in a grouped graph. |
|[ReshapeHandleProvider](demos/src/input/reshapehandleprovider/README.md)| Demo code that shows how to customize the resize behavior of INodes. |
|[SingleSelection](demos/src/input/singleselection/README.md)| Configure the `GraphEditorInputMode` for single selection mode. |
|[SizeConstraintProvider](demos/src/input/sizeconstraintprovider/README.md)| Demo code that shows how to customize the resizing behavior of INodes. |
  
## [Integration Demos](demos/src/integration/README.md)

  

 This folder and its subfolders contain demo applications which illustrate the integration of yFiles for JavaFX with different GUI frameworks as well as integration of external libraries into yFiles for JavaFX.   

| Demo | Description |
|------|:-----------:|
|[EclipseRCP](demos/src-eclipsercp/integration/eclipsercp/README.md)| Demo application that shows how to integrate yFiles for JavaFX in an Eclipse E4 RCP. |
|[Neo4j](demos/src-neo4j/integration/neo4j/README.md)| Demo application that shows how to integrate Neo4j into yFiles for JavaFX. |
|[SwtDemo](demos/src-swt/integration/swt/README.md)| Demo application that shows how to integrate yFiles for JavaFX in a Standard Widget Toolkit (SWT) application. |
|[SwingDemo](demos/src/integration/swing/README.md)| Demo application that shows how to integrate yFiles for JavaFX in a Swing application. |
  
## [Viewer Demos](demos/src/viewer/README.md)

  

 This folder and its subfolders contain demo applications which make use of the different features of the viewer component of yFiles for JavaFX.   

| Demo | Description |
|------|:-----------:|
|[Css](demos/src/viewer/css/README.md)| Shows the possibilities to customize the visualizations in yFiles for JavaFX with the help of CSS. |
|[SVGImageExport](demos/src-svg/viewer/svgimageexport/README.md)| Export a graph as a SVG image. |
|[EdgeToEdge](demos/src/viewer/edgetoedge/README.md)| Shows edge-to-edge connections. |
|[GraphEvents](demos/src/viewer/events/README.md)| Explore the different kinds of events dispatched by yFiles for JavaFX. |
|[GraphMLCompatibility](demos/src/viewer/graphmlcompatibility/README.md)| Shows how to enable read compatibility for GraphML files from older versions. |
|[GraphViewer](demos/src/viewer/graphviewer/README.md)| A viewer which demonstrates different kinds of graphs created with yFiles for JavaFX. |
|[ImageExport](demos/src/viewer/imageexport/README.md)| Export a graph as a bitmap image. |
|[LargeGraphs](demos/src/viewer/largegraphs/README.md)| Improve the rendering performance for very large graphs in yFiles for JavaFX. |
|[Printing](demos/src/viewer/printing/README.md)| Print the contents of a yFiles GraphControl. |
|[RenderingOrder](demos/src/viewer/renderingorder/README.md)| Shows the effect of different rendering policies to the model items. |
  
## [Style Demos](demos/src/style/README.md)

  

 This folder and its subfolders contain demo applications which make use of the different features of the styles component of yFiles for JavaFX.   

| Demo | Description |
|------|:-----------:|
|[RichTextLabelStyle](demos/src/style/richtextlabelstyle/README.md)| Using the JavaFX 8 Rich Text API in yFiles. |
|[SimpleCustomStyle](demos/src/style/simplecustomstyle/README.md)| Shows how to implement sophisticated styles for graph objects in yFiles for JavaFX. |
|[TemplateStyle](demos/src/style/templatestyle/README.md)| Shows how to use `TemplateNodeStyle` and `TemplateLabelStyle` to create complex node and label visualizations using FXML components. |
  
## [Data Binding Demos](demos/src/databinding/README.md)

  

 This folder and its subfolders contain demo applications which demonstrate how to use the `GraphBuilder` classes for binding graph elements to business data in yFiles for JavaFX.   

| Demo | Description |
|------|:-----------:|
|[GraphBuilder](demos/src/databinding/graphbuilder/README.md)| Demonstrates data binding using the `GraphBuilder` class. |
|[InteractiveNodesGraphBuilder](demos/src/databinding/interactivenodesgraphbuilder/README.md)| Demonstrates data binding using class `AdjacentNodesGraphBuilder` . |
  
## [Analysis Demos](demos/src/analysis/README.md)

  

 This folder and its subfolders contain demo applications which demonstrate some of the graph analysis algorithms available in yFiles for JavaFX.   

| Demo | Description |
|------|:-----------:|
|[GraphAnalysis](demos/src/analysis/graphanalysis/README.md)| Algorithms to analyse the structure of a graph in yFiles for Java (Swing). |
|[ShortestPath](demos/src/analysis/shortestpath/README.md)| Usage and visualization of shortest path algorithms in yFiles for JavaFX. |
  
## [Deployment Demos](demos/src/deploy/README.md)

  

 This folder and its subfolders contain demo applications which illustrate tasks for deployment of yFiles for JavaFX applications, e.g. obfuscation.   

| Demo | Description |
|------|:-----------:|
|[ObfuscationDemo](demos/src/deploy/obfuscation/README.md)| Demonstrates the obfuscation of an yFiles for JavaFX application via yGuard. |
  
# Tutorials

The yFiles for JavaFX tutorials are extensive source code samples that present the functionality of the yFiles for JavaFX library. All tutorials can be found in subdirectories of the current directory.

To navigate to a specific tutorial, just follow the corresponding link from the table below.

## Available Tutorials

| Category | Description |
|------|:-----------:|
|[Getting Started](./tutorials/tutorial01_GettingStarted/README.md)| 	Introduces basic concepts as well as main features like custom styles, full user interaction, Undo/Redo, clipboard, I/O, grouping and folding.|
|[Custom Styles](./tutorials/tutorial02_CustomStyles/README.md)| A step-by-step guide to customizing the visual representation of graph elements. This tutorial is intended for users who want to learn how to create custom styles from scratch.|


# License

Use of the software hosted in this repository is subject to the license terms of the corresponding yFiles for JavaFX license.
Owners of a valid software license for a yFiles for JavaFX version that these
demos are shipped with are allowed to use the demo source code as basis
for their own yFiles for JavaFX powered applications. Use of such programs is
governed by the rights and conditions as set out in the yFiles for JavaFX
license agreement. More details [here](./LICENSE). If in doubt, feel free to [contact](https://www.yworks.com/contact) the yFiles for JavaFX support team.

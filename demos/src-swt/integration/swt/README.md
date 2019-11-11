# SWT Demo
 This demo shows how to integrate yFiles for JavaFX in a Standard Widget Toolkit (SWT) application.   
*  A toolbar that provides SWT buttons enables the user to change the zoom level of the GraphControl that is a JavaFX control as well as SWT buttons for undo/redo functionality.   
*  A right click on a node shown in the GraphControl opens a SWT context menu and allows the user to delete the clicked node from the GraphControl.   
*  On the left side a SWT palette offers nodes with different styles that can be dragged into the GraphControl.     

 Since this demo requires the `swt.jar` it is in a separate source folder.   

## Run the demo using an IDE
  

 To run this demo add the folder `src-swt` to your demo project's sources and add the `swt.jar` to the classpath/libraries/dependencies following your IDE's instructions. The `swt.jar` can be downloaded from the [SWT homepage](http://www.eclipse.org/swt/) . Alternatively you can use the Ant build script located in the current folder that downloads the `swt.jar` on demand and run this demo.   

## Run the demo using ANT
  

 To run the demo using ANT run the target `run-SwtApplication` of the demo build file ( `demos/build.xml` ). An `swt.jar` will automatically be downloaded.   

 Alternatively, an `swt.jar` can be placed in the `<yfiles-for-javafx>/lib` folder.   
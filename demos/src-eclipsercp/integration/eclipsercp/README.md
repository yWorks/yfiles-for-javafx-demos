# Eclipse RCP
 The following instructions describe how to set up the Eclipse E4 RCP Integration Demo. 

## Set up Eclipse to develop JavaFX
    
*  We recommend to install an [all-in-one download](http://efxclipse.bestsolution.at/install.html#all-in-one) , that provides preconfigured packages for developing Eclipse E4 RCP applications with JavaFX. It may be necessary to additionally install the latest updates: Click on __Help__ and select __Check for Updates__ .   
*  Alternatively you can assemble your own e(fx)clipse installation based on Eclipse SDK by following the instructions on [e(fx)clipse](http://www.eclipse.org/efxclipse/install.html#for-the-ambitious) .     

## Import the Eclipse RCP Integration Demo in your workspace
    
*  Start your Eclipse IDE.   
*  Click on __File__ and select __Import__ .   
*  Choose __Existing Projects into Workspace__ which is located under __General__ and click __Next__ .   
*  In __Select root directory__ , browse to your yFiles installation folder and select the folder `demos/src-eclipsercp/integration/eclipsercp` .     

## Enable development of yFiles for JavaFX in Eclipse
    
*  Copy your license file into the `src` folder of the demo project.   
*  Copy the `yfiles-for-javafx.jar` that is located in the `lib` directory of your yFiles for JavaFX installation into the `lib` folder of the demo project.   
*  Open the product file `com.yworks.yEdLite.product` within Eclipse.   
*  Select the __Contents__ tab and click on __Add Required Plug-ins__ .     

## Starting the demo
    
*  Open the product file `com.yworks.yEdLite.product` within Eclipse again.   
*  Select the __Overview__ tab and click on __Launch an Eclipse application__ or __Launch an Eclipse application in Debug mode__ .     

## How to run the demo on Linux
 To run the demo on Linux you should   
*  either add an environment variable `SWT_GTK3` with value `0` : open the __Run Configurations__ dialog, select the __Environment__ tab and add the new variable. See [Eclipse Forum](https://www.eclipse.org/forums/index.php/t/1072253/) .   
*  Or use at least Java 9 as execution environment: open the `com.yworks.yEdLite.product` file, select the __Lauching__ tab and choose at least Java 9 as __Execution Environment__ .     
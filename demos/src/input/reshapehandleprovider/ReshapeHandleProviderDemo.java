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
package input.reshapehandleprovider;

import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.GraphItemTypes;
import com.yworks.yfiles.graph.IGraph;
import com.yworks.yfiles.graph.IPort;
import com.yworks.yfiles.graph.PortDecorator;
import com.yworks.yfiles.graph.styles.DefaultLabelStyle;
import com.yworks.yfiles.graph.styles.NodeStylePortStyleAdapter;
import com.yworks.yfiles.graph.styles.ShapeNodeShape;
import com.yworks.yfiles.graph.styles.ShapeNodeStyle;
import com.yworks.yfiles.view.GraphControl;
import com.yworks.yfiles.view.input.GraphEditorInputMode;
import com.yworks.yfiles.view.input.IReshapeHandleProvider;
import com.yworks.yfiles.view.input.OrthogonalEdgeEditingContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebView;
import toolkit.DemoApplication;
import toolkit.DemoStyles;
import toolkit.Themes;
import toolkit.WebViewUtils;

import java.io.IOException;

/**
 * Shows how to implement a custom {@link IReshapeHandleProvider} for {@link IPort IPorts} using a
 * {@link NodeStylePortStyleAdapter}.
 */
public class ReshapeHandleProviderDemo extends DemoApplication {
    public GraphControl graphControl;
    public WebView help;

    /**
     * Registers a callback function as decorator that provides a customized {@link IReshapeHandleProvider} for
     * each port with a {@link NodeStylePortStyleAdapter}.
     * This callback function is called whenever a port in the graph is queried for its <code>IReshapeHandleProvider</code>.
     */
    public void registerReshapeHandleProvider() {
        PortDecorator portDecorator = graphControl.getGraph().getDecorator().getPortDecorator();
        portDecorator.getDecoratorFor(IReshapeHandleProvider.class).setFactory(
                port -> port.getStyle() instanceof NodeStylePortStyleAdapter,
                port -> {
                    NodeStylePortStyleAdapter style = (NodeStylePortStyleAdapter) port.getStyle();
                    PortReshapeHandleProvider provider = new PortReshapeHandleProvider(port, style, ctrlDown);
                    provider.setMinimumSize(new SizeD(5, 5));
                    return provider;
                }
        );
    }


    /**
     * Initializes this demo by configuring the default styles, input mode, and the model item lookup and creating an example graph
     * together with an enclosing rectangle some nodes may not stretch over.
     */
    public void initialize() {
        // setup the help text on the right side.
        WebViewUtils.initHelp(help, this);

        // initialize the default of the graph
        initializeGraphDefaults();

        // initialize the GraphEditorInputMode
        initializeInputMode();

        // register the reshape handle provider for ports
        registerReshapeHandleProvider();

        // read initial graph from sample file
        loadGraph();
    }

    /**
     * Initializes the graph defaults.
     */
    private void initializeGraphDefaults() {
        IGraph graph = graphControl.getGraph();
        DemoStyles.initDemoStyles(graph, Themes.PALETTE58);

        ShapeNodeStyle adaptedStyle = DemoStyles.createDemoShapeNodeStyle(ShapeNodeShape.RECTANGLE, Themes.PALETTE_GREEN);
        adaptedStyle.setPen(null);

        NodeStylePortStyleAdapter portStyleAdapter = new NodeStylePortStyleAdapter(adaptedStyle);
        portStyleAdapter.setRenderSize(new SizeD(7, 7));
        graph.getNodeDefaults().getPortDefaults().setStyle(portStyleAdapter);
        // each port needs its own style instance to have its own render size
        graph.getNodeDefaults().getPortDefaults().setStyleInstanceSharingEnabled(false);
        // disable removing ports when all attached edges have been removed
        graph.getNodeDefaults().getPortDefaults().setAutoCleanUpEnabled(false);

        DefaultLabelStyle nodeLabelStyle = DemoStyles.createDemoNodeLabelStyle(Themes.PALETTE55);
        nodeLabelStyle.setBackgroundPaint(null);
        graph.getNodeDefaults().getLabelDefaults().setStyle(nodeLabelStyle);
        DefaultLabelStyle portLabelStyle = DemoStyles.createDemoNodeLabelStyle(Themes.PALETTE58);
        portLabelStyle.setBackgroundPaint(null);
        graph.getNodeDefaults().getPortDefaults().getLabelDefaults().setStyle(portLabelStyle);
    }


    private void initializeInputMode() {
        // create a default editor input mode
        GraphEditorInputMode geim = new GraphEditorInputMode();

        // ports are preferred for clicks
        geim.setClickHitTestOrder(new GraphItemTypes[]{
                GraphItemTypes.PORT,
                GraphItemTypes.PORT_LABEL,
                GraphItemTypes.BEND,
                GraphItemTypes.EDGE_LABEL,
                GraphItemTypes.EDGE,
                GraphItemTypes.NODE,
                GraphItemTypes.NODE_LABEL,
        });
        // enable orthogonal edge editing
        geim.setOrthogonalEdgeEditingContext(new OrthogonalEdgeEditingContext());

        // PortReshapeHandlerProvider considers pressed Ctrl keys. Whenever Ctrl is pressed or released,
        // we force GraphEditorInputMode to requery the handles of selected items
        graphControl.addEventHandler(KeyEvent.KEY_PRESSED, this::updateHandles);
        graphControl.addEventHandler(KeyEvent.KEY_RELEASED, this::updateHandles);

        // finally, set the input mode to the graph control.
        graphControl.setInputMode(geim);
    }

    boolean ctrlDown = false;

    private void updateHandles(KeyEvent e) {
        boolean ctrlWasDown = this.ctrlDown;
        ctrlDown = e.isControlDown();
        if (ctrlDown != ctrlWasDown && e.getCode() == KeyCode.CONTROL) {
            // only update handles if a modifier state changed - not on redispatched pressed events
            ((GraphEditorInputMode) graphControl.getInputMode()).requeryHandles();
        }
    }


    /**
     * Loads a sample graph.
     */
    private void loadGraph() {
        try {
            graphControl.importFromGraphML(getClass().getResource("resources/defaultGraph.graphml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Centers the displayed content in the graph component.
     */
    public void onLoaded() {
        graphControl.fitGraphBounds();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

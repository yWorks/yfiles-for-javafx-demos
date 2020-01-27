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
package viewer.graphmlcompatibility;

import com.yworks.yfiles.graph.FoldingManager;
import com.yworks.yfiles.graph.labelmodels.FreeNodeLabelModel;
import com.yworks.yfiles.graph.portlocationmodels.FreeNodePortLocationModel;
import com.yworks.yfiles.graph.styles.Arrow;
import com.yworks.yfiles.graph.styles.VoidEdgeStyle;
import com.yworks.yfiles.graph.styles.VoidLabelStyle;
import com.yworks.yfiles.graph.styles.VoidNodeStyle;
import com.yworks.yfiles.graph.styles.VoidPathGeometry;
import com.yworks.yfiles.graph.styles.VoidPortStyle;
import com.yworks.yfiles.graph.styles.VoidShapeGeometry;
import com.yworks.yfiles.graph.styles.VoidStripeStyle;
import com.yworks.yfiles.graphml.GraphMLIOHandler;
import com.yworks.yfiles.graphml.SerializationProperties;
import com.yworks.yfiles.view.GraphControl;
import viewer.graphmlcompatibility.extensions.for2x.ArrowExtension;
import viewer.graphmlcompatibility.extensions.for2x.NodeStyleLabelStyleAdapterExtension;
import viewer.graphmlcompatibility.extensions.for2x.RotatedSideSliderEdgeLabelModel;
import viewer.graphmlcompatibility.extensions.for2x.RotatedSideSliderLabelModelParameterExtension;
import viewer.graphmlcompatibility.extensions.for2x.RotatedSliderEdgeLabelModel;
import viewer.graphmlcompatibility.extensions.for2x.RotatedSliderLabelModelParameterExtension;
import viewer.graphmlcompatibility.extensions.for2x.ShapeNodeStyleExtension;
import viewer.graphmlcompatibility.extensions.for2x.CollapsibleNodeStyleDecoratorExtension;
import viewer.graphmlcompatibility.extensions.for2x.SideSliderEdgeLabelModel;
import viewer.graphmlcompatibility.extensions.for2x.SideSliderLabelModelParameterExtension;
import viewer.graphmlcompatibility.extensions.for2x.SliderEdgeLabelModel;
import viewer.graphmlcompatibility.extensions.for2x.SliderLabelModelParameterExtension;
import viewer.graphmlcompatibility.extensions.for2x.EdgeViewStateExtension;
import viewer.graphmlcompatibility.extensions.for2x.GeneralPathNodeStyleExtension;
import viewer.graphmlcompatibility.extensions.for2x.LabelExtension;
import viewer.graphmlcompatibility.extensions.for2x.NodeScaledParameterExtension;
import viewer.graphmlcompatibility.extensions.for2x.NodeViewStateExtension;
import viewer.graphmlcompatibility.extensions.for2x.PolylineEdgeStyleExtension;
import viewer.graphmlcompatibility.extensions.for2x.PortDefaultsExtension;
import viewer.graphmlcompatibility.extensions.for2x.PortExtension;
import viewer.graphmlcompatibility.extensions.for2x.RotatingEdgeLabelModel;
import viewer.graphmlcompatibility.extensions.for2x.RotatingEdgeLabelModelParameterExtension;
import viewer.graphmlcompatibility.extensions.for2x.ShinyPlateNodeStyleExtension;
import viewer.graphmlcompatibility.extensions.for2x.SimpleLabelStyleExtension;
import viewer.graphmlcompatibility.extensions.for2x.TextFormat;
import viewer.graphmlcompatibility.extensions.for3x.DefaultLabelStyleExtension;
import viewer.graphmlcompatibility.extensions.for3x.LabelDefaultsExtension;
import viewer.graphmlcompatibility.extensions.shared.ArcEdgeStyleExtension;
import viewer.graphmlcompatibility.extensions.shared.ImageNodeStyleExtension;
import viewer.graphmlcompatibility.extensions.shared.MemoryIconLabelStyleExtension;

/**
 * Configures {@link GraphMLIOHandler} instances for reading yFiles for JavaFX
 * 3.0.x GraphML files.
 * <p>
 * The basic approach for programmatically converting yFiles for JavaFX 3.0.x
 * GraphML files into yFiles for JavaFX 3.1.x GraphML files is as follows:
 * </p>
 * <pre>
 * public static void convert(File src, File tgt) throws IOException {
 *   DefaultGraph graph = new DefaultGraph();
 *
 *   GraphMLIOHandler reader = new GraphMLIOHandler();
 *   CompatibilitySupport.configureIOHandler(reader);
 *   reader.read(graph, src.getAbsolutePath());
 *
 *   GraphMLIOHandler writer = new GraphMLIOHandler();
 *   writer.write(graph, tgt.getAbsolutePath());
 * }
 * </pre>
 * <p>
 * This approach assumes a source document representing a flat graph (i.e. a
 * graph without group or folder nodes).  
 * </p>
 */
public class CompatibilitySupport {
  private static final String COMMON_2_NS =
          "http://www.yworks.com/xml/yfiles-common/2.0";
  private static final String MARKUP_2_NS =
          "http://www.yworks.com/xml/yfiles-common/markup/2.0";
  private static final String YFILES_JAVAFX_2_NS =
          "http://www.yworks.com/xml/yfiles-java-fx/1.0";
  private static final String YFILES_JAVAFX_3_NS =
          "http://www.yworks.com/xml/yfiles-java-fx/3.0/xaml";

  private CompatibilitySupport() {
  }

  /**
   * Configures the given {@link GraphMLIOHandler} instance for reading
   * yFiles for JavaFX 3.0.x GraphML files.
   * <p>
   * <b>Note:</b> Do not use {@link GraphMLIOHandler} instances configured with
   * this method for writing GraphML files.  
   * </p>
   * @param reader the {@link GraphMLIOHandler} instance that will be
   * configured for reading yFiles for JavaFX 3.0.x GraphML files.
   */
  public static void configureIOHandler( GraphMLIOHandler reader ) {
    // enable CSharp style enumeration value name parsing, e.g.
    // CharacterEllipsis instead of CHARACTER_ELLIPSIS
    reader.getDeserializationPropertyOverrides().set(
            SerializationProperties.IGNORE_PROPERTY_CASE,
            Boolean.TRUE);
    // optionally:
    // prevent unknown GraphML elements and attributes from aborting the
    // the parse process
//    reader.getDeserializationPropertyOverrides().set(
//            SerializationProperties.IGNORE_XAML_DESERIALIZATION_ERRORS,
//            Boolean.TRUE);

    Class c = GraphMLIOHandler.class;


    //
    // mappings for yFiles for JavaFX 3.0.x compatibility
    //
    // xmlns:yfx="http://www.yworks.com/xml/yfiles-java-fx/3.0/xaml"
    addType(reader, YFILES_JAVAFX_3_NS, "ArcEdgeStyle", ArcEdgeStyleExtension.class);
    addType(reader, YFILES_JAVAFX_3_NS, "DefaultLabelStyle", DefaultLabelStyleExtension.class);
    addType(reader, YFILES_JAVAFX_3_NS, "IconLabelStyle", viewer.graphmlcompatibility.extensions.for3x.IconLabelStyleExtension.class);
    addType(reader, YFILES_JAVAFX_3_NS, "ImageNodeStyle", ImageNodeStyleExtension.class);
    addType(reader, YFILES_JAVAFX_3_NS, "MemoryIconLabelStyle", MemoryIconLabelStyleExtension.class);
    addPackage(reader, YFILES_JAVAFX_3_NS, "com.yworks.yfiles.graph.styles", Arrow.class);
    addPackage(reader, YFILES_JAVAFX_3_NS, "com.yworks.yfiles.markup.common", c);
    addPackage(reader, YFILES_JAVAFX_3_NS, "com.yworks.yfiles.markup.platform.java", c);
    addPackage(reader, YFILES_JAVAFX_3_NS, "com.yworks.yfiles.view", GraphControl.class);


    //
    // mappings for yFiles for JavaFX 2.0.x compatibility
    //
    // xmlns:x="http://www.yworks.com/xml/yfiles-common/markup/2.0"
    addPackage(reader, MARKUP_2_NS, "com.yworks.yfiles.markup.primitives", c);

    // xmlns:y="http://www.yworks.com/xml/yfiles-common/2.0"
    addPackage(reader, COMMON_2_NS, "com.yworks.yfiles.graph", FoldingManager.class);
    addPackage(reader, COMMON_2_NS, "com.yworks.yfiles.graphml", c);
    addPackage(reader, COMMON_2_NS, "com.yworks.yfiles.markup.common", c);
    addType(reader, COMMON_2_NS, "EdgeViewState", EdgeViewStateExtension.class);
    addType(reader, COMMON_2_NS, "Label", LabelExtension.class);
    addType(reader, COMMON_2_NS, "LabelDefaults", LabelDefaultsExtension.class);
    // the custom label extension needs to be registered for "Label" and
    // "LabelExtension" both to prevent the GraphML framework from using
    // the internal class com.yworks.yfiles.markup.common.LabelExtension
    // instead of the custom implementation
    addType(reader, COMMON_2_NS, "LabelExtension", LabelExtension.class);
    addType(reader, COMMON_2_NS, "NodeViewState", NodeViewStateExtension.class);
    addType(reader, COMMON_2_NS, "Port", PortExtension.class);
    addType(reader, COMMON_2_NS, "PortDefaults", PortDefaultsExtension.class);
    // the custom port extension needs to be registered for "Port" and
    // "PortExtension" both to prevent the GraphML framework from using
    // the internal class com.yworks.yfiles.markup.common.PortExtension
    // instead of the custom implementation
    addType(reader, COMMON_2_NS, "PortExtension", PortExtension.class);
    addType(reader, COMMON_2_NS, "VoidEdgeStyle", VoidEdgeStyle.class);
    addType(reader, COMMON_2_NS, "VoidLabelStyle", VoidLabelStyle.class);
    addType(reader, COMMON_2_NS, "VoidNodeStyle", VoidNodeStyle.class);
    addType(reader, COMMON_2_NS, "VoidPortStyle", VoidPortStyle.class);
    addType(reader, COMMON_2_NS, "VoidStripeStyle", VoidStripeStyle.class);
    addType(reader, COMMON_2_NS, "VoidPathGeometry", VoidPathGeometry.class);
    addType(reader, COMMON_2_NS, "VoidShapeGeometry", VoidShapeGeometry.class);

    addType(reader, YFILES_JAVAFX_2_NS, "ArcEdgeStyle", ArcEdgeStyleExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "Arrow", ArrowExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "CollapsibleNodeStyleDecorator", CollapsibleNodeStyleDecoratorExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "GeneralPathMarkup", com.yworks.yfiles.markup.common.GeneralPathExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "GeneralPathNodeStyle", GeneralPathNodeStyleExtension.class);
    // GenericModel has been renamed to GenericLabelModel
    addType(reader, YFILES_JAVAFX_2_NS, "GenericModel", com.yworks.yfiles.markup.common.GenericLabelModelExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "IconLabelStyle", viewer.graphmlcompatibility.extensions.for2x.IconLabelStyleExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "ImageNodeStyle", ImageNodeStyleExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "MemoryIconLabelStyle", MemoryIconLabelStyleExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "NinePositionsEdgeLabelParameter", com.yworks.yfiles.markup.common.NinePositionsEdgeLabelModelParameterExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "NodeScaledParameter", NodeScaledParameterExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "NodeScaledPortLocationModel", FreeNodePortLocationModel.class);
    addType(reader, YFILES_JAVAFX_2_NS, "NodeStyleLabelStyleAdapter", NodeStyleLabelStyleAdapterExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "PolylineEdgeStyle", PolylineEdgeStyleExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "RotatedSideSliderEdgeLabelModel", RotatedSideSliderEdgeLabelModel.class);
    addType(reader, YFILES_JAVAFX_2_NS, "RotatedSideSliderLabelModelParameter", RotatedSideSliderLabelModelParameterExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "RotatedSliderEdgeLabelModel", RotatedSliderEdgeLabelModel.class);
    addType(reader, YFILES_JAVAFX_2_NS, "RotatedSliderLabelModelParameter", RotatedSliderLabelModelParameterExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "RotatingEdgeLabelModel", RotatingEdgeLabelModel.class);
    addType(reader, YFILES_JAVAFX_2_NS, "RotatingEdgeLabelModelParameter", RotatingEdgeLabelModelParameterExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "ShapeNodeStyle", ShapeNodeStyleExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "SideSliderEdgeLabelModel", SideSliderEdgeLabelModel.class);
    addType(reader, YFILES_JAVAFX_2_NS, "SideSliderLabelModelParameter", SideSliderLabelModelParameterExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "SliderEdgeLabelModel", SliderEdgeLabelModel.class);
    addType(reader, YFILES_JAVAFX_2_NS, "SliderLabelModelParameter", SliderLabelModelParameterExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "SimpleLabelStyle", SimpleLabelStyleExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "ShinyPlateNodeStyle", ShinyPlateNodeStyleExtension.class);
    addType(reader, YFILES_JAVAFX_2_NS, "TextFormat", TextFormat.class);

    // xmlns:yfx="http://www.yworks.com/xml/yfiles-java-fx/1.0"
    addPackage(reader, YFILES_JAVAFX_2_NS, "com.yworks.yfiles.graph.styles", Arrow.class);
    addPackage(reader, YFILES_JAVAFX_2_NS, "com.yworks.yfiles.markup.common", c);
    addPackage(reader, YFILES_JAVAFX_2_NS, "com.yworks.yfiles.markup.platform.java", c);
    addPackage(reader, YFILES_JAVAFX_2_NS, "com.yworks.yfiles.view", GraphControl.class);

    // label models used to be in the platfrom specific namespace but
    // have been "moved" to the yFiles common namespace
    addPackage(reader, YFILES_JAVAFX_2_NS, "com.yworks.yfiles.graph.labelmodels", FreeNodeLabelModel.class);
    // port location models used to be in the platfrom specific namespace but
    // have been "moved" to the yFiles common namespace
    addPackage(reader, YFILES_JAVAFX_2_NS, "com.yworks.yfiles.graph.portlocationmodels", FreeNodePortLocationModel.class);
  }

  /**
   * Registers a Java type for parsing the given XML tag in the given namespace.
   */
  private static void addType(
          GraphMLIOHandler reader, String namespace, String tagName, Class type
  ) {
    reader.addXamlNamespaceMapping(namespace, tagName, type);
  }

  /**
   * Registers a Java package for parsing XML tags from the given namespace.
   * @param c a sample {@link Class} instance whose class loader is able to
   * instantiate types for the given package.
   */
  private static void addPackage(
          GraphMLIOHandler reader, String namespace, String packageName, Class c
  ) {
    reader.addXamlNamespaceMapping(namespace, packageName, c.getClassLoader());
  }
}

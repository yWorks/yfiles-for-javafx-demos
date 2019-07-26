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
package viewer.graphmlcompatibility.extensions.for3x;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.LabelDefaults;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;

/**
 * Handles the yFiles for Java 3.0.x version of GraphML element
 * <code>&lt;LabelDefaults&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class LabelDefaultsExtension extends MarkupExtension {
  private boolean autoAdjustingPreferredSizeEnabled;
  private ILabelModelParameter labelModelParameter;
  private boolean labelModelParameterInstanceSharingEnabled;
  private ILabelStyle style;
  private boolean styleInstanceSharingEnabled;

  public LabelDefaultsExtension() {
    LabelDefaults prototype = new LabelDefaults();
    autoAdjustingPreferredSizeEnabled = prototype.isAutoAdjustingPreferredSizeEnabled();
    labelModelParameter = prototype.getLayoutParameter();
    labelModelParameterInstanceSharingEnabled = prototype.isLayoutParameterInstanceSharingEnabled();
    style = prototype.getStyle();
    styleInstanceSharingEnabled = prototype.isStyleInstanceSharingEnabled();
  }

  /**
   * Handles the GraphML alias <code>AutoAdjustPreferredSize</code> used in
   * yFiles for JavaFX 2.0.x for property
   * <code>AutoAdjustingPreferredSizeEnabled</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isAutoAdjustingPreferredSizeEnabled() 
   */
  public boolean isAutoAdjustPreferredSize() {
    return isAutoAdjustingPreferredSizeEnabled();
  }

  /**
   * Handles the GraphML alias <code>AutoAdjustPreferredSize</code> used in
   * yFiles for JavaFX 2.0.x for property
   * <code>AutoAdjustingPreferredSizeEnabled</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setAutoAdjustingPreferredSizeEnabled(boolean)
   */
  public void setAutoAdjustPreferredSize( boolean value ) {
    setAutoAdjustingPreferredSizeEnabled(value);
  }

  public boolean isAutoAdjustingPreferredSizeEnabled() {
    return autoAdjustingPreferredSizeEnabled;
  }

  public void setAutoAdjustingPreferredSizeEnabled( boolean value ) {
    this.autoAdjustingPreferredSizeEnabled = value;
  }

  public ILabelModelParameter getLabelModelParameter() {
    return labelModelParameter;
  }

  public void setLabelModelParameter( ILabelModelParameter value ) {
    this.labelModelParameter = value;
  }

  public boolean isLabelModelParameterInstanceSharingEnabled() {
    return labelModelParameterInstanceSharingEnabled;
  }

  public void setLabelModelParameterInstanceSharingEnabled( boolean value ) {
    this.labelModelParameterInstanceSharingEnabled = value;
  }

  /**
   * Handles the GraphML alias <code>ShareLabelModelParameterInstance</code>
   * used in yFiles for JavaFX 2.0.x for property
   * <code>LabelModelParameterInstanceSharingEnabled</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isLabelModelParameterInstanceSharingEnabled() 
   */
  public boolean isShareLabelModelParameterInstance() {
    return isLabelModelParameterInstanceSharingEnabled();
  }

  /**
   * Handles the GraphML alias <code>ShareLabelModelParameterInstance</code>
   * used in yFiles for JavaFX 2.0.x for property
   * <code>LabelModelParameterInstanceSharingEnabled</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setLabelModelParameterInstanceSharingEnabled(boolean)
   */
  public void setShareLabelModelParameterInstance( boolean value ) {
    setLabelModelParameterInstanceSharingEnabled(value);
  }

  /**
   * Handles the GraphML alias <code>ShareStyleInstance</code> used in yFiles
   * for JavaFX 2.0.x for property <code>StyleInstanceSharingEnabled</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isStyleInstanceSharingEnabled()
   */
  public boolean isShareStyleInstance() {
    return isStyleInstanceSharingEnabled();
  }

  /**
   * Handles the GraphML alias <code>ShareStyleInstance</code> used in yFiles
   * for JavaFX 2.0.x for property <code>StyleInstanceSharingEnabled</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setStyleInstanceSharingEnabled(boolean)
   */
  public void setShareStyleInstance( boolean value ) {
    setStyleInstanceSharingEnabled(value);
  }

  public ILabelStyle getStyle() {
    return style;
  }

  public void setStyle( ILabelStyle value ) {
    this.style = value;
  }

  public boolean isStyleInstanceSharingEnabled() {
    return styleInstanceSharingEnabled;
  }

  public void setStyleInstanceSharingEnabled( boolean value ) {
    this.styleInstanceSharingEnabled = value;
  }

  @Override
  public Object provideValue( ILookup serviceProvider ) {
    LabelDefaults defaults = new LabelDefaults();
    defaults.setAutoAdjustingPreferredSizeEnabled(isAutoAdjustingPreferredSizeEnabled());
    ILabelModelParameter labelModelParameter = getLabelModelParameter();
    if (labelModelParameter != null) {
      defaults.setLayoutParameter(labelModelParameter);
    }
    defaults.setLayoutParameterInstanceSharingEnabled(isLabelModelParameterInstanceSharingEnabled());
    ILabelStyle style = getStyle();
    if (style != null) {
      defaults.setStyle(style);
    }
    defaults.setStyleInstanceSharingEnabled(isStyleInstanceSharingEnabled());
    return defaults;
  }
}

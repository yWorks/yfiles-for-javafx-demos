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
package viewer.graphmlcompatibility.extensions.for2x;

import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.PortDefaults;
import com.yworks.yfiles.graph.portlocationmodels.IPortLocationModelParameter;
import com.yworks.yfiles.graph.styles.IPortStyle;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;

/**
 * Handles the yFiles for JavaFX 2.0.x version of GraphML element
 * <code>&lt;PortDefaults&gt;</code>.
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class PortDefaultsExtension extends MarkupExtension {
  private boolean autoCleanupEnabled;
  private IPortLocationModelParameter locationModelParameter;
  private boolean locationModelParameterInstanceSharingEnabled;
  private IPortStyle style;
  private boolean styleInstanceSharingEnabled;

  public PortDefaultsExtension() {
    PortDefaults prototype = new PortDefaults();
    autoCleanupEnabled = prototype.isAutoCleanUpEnabled();
    locationModelParameter = prototype.getLocationParameter();
    locationModelParameterInstanceSharingEnabled = prototype.isLocationParameterInstanceSharingEnabled();
    style = prototype.getStyle();
    styleInstanceSharingEnabled = prototype.isStyleInstanceSharingEnabled();
  }

  public boolean isAutoCleanupEnabled() {
    return autoCleanupEnabled;
  }

  public void setAutoCleanupEnabled( boolean value ) {
    this.autoCleanupEnabled = value;
  }

  public IPortLocationModelParameter getLocationModelParameter() {
    return locationModelParameter;
  }

  public void setLocationModelParameter( IPortLocationModelParameter value ) {
    this.locationModelParameter = value;
  }

  public boolean isLocationModelParameterInstanceSharingEnabled() {
    return locationModelParameterInstanceSharingEnabled;
  }

  public void setLocationModelParameterInstanceSharingEnabled( boolean value ) {
    this.locationModelParameterInstanceSharingEnabled = value;
  }

  public IPortStyle getStyle() {
    return style;
  }

  public void setStyle( IPortStyle value ) {
    this.style = value;
  }

  /**
   * Handles the GraphML alias <code>ShareLocationModelParameterInstance</code>
   * used in yFiles for JavaFX 2.0.x for property
   * <code>LocationModelParameterInstanceSharingEnabled</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #isLocationModelParameterInstanceSharingEnabled()
   */
  public boolean isShareLocationModelParameterInstance() {
    return isLocationModelParameterInstanceSharingEnabled();
  }

  /**
   * Handles the GraphML alias <code>ShareLocationModelParameterInstance</code>
   * used in yFiles for JavaFX 2.0.x for property
   * <code>LocationModelParameterInstanceSharingEnabled</code>.
   * yFiles for JavaFX 2.0.x accepts both, the alias as well as the
   * actual property name when parsing GraphML. yFiles for JavaFX 3.1.x requires
   * two distinct properties for parsing both alternatives.
   * @see #setLocationModelParameterInstanceSharingEnabled(boolean)
   */
  public void setShareLocationModelParameterInstance( boolean value ) {
    setLocationModelParameterInstanceSharingEnabled(value);
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

  public boolean isStyleInstanceSharingEnabled() {
    return styleInstanceSharingEnabled;
  }

  public void setStyleInstanceSharingEnabled( boolean value ) {
    this.styleInstanceSharingEnabled = value;
  }

  @Override
  public Object provideValue( ILookup serviceProvider ) {
    PortDefaults defaults = new PortDefaults();
    defaults.setAutoCleanUpEnabled(isAutoCleanupEnabled());
    IPortLocationModelParameter param = getLocationModelParameter();
    if (param != null) {
      defaults.setLocationParameter(param);
    }
    defaults.setLocationParameterInstanceSharingEnabled(isLocationModelParameterInstanceSharingEnabled());
    IPortStyle style = getStyle();
    if (style != null) {
      defaults.setStyle(style);
    }
    defaults.setStyleInstanceSharingEnabled(isStyleInstanceSharingEnabled());
    return defaults;
  }
}

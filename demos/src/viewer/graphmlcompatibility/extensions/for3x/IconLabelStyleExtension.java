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

import com.yworks.yfiles.geometry.InsetsD;
import com.yworks.yfiles.geometry.SizeD;
import com.yworks.yfiles.graph.ILookup;
import com.yworks.yfiles.graph.labelmodels.ILabelModelParameter;
import com.yworks.yfiles.graph.styles.ILabelStyle;
import com.yworks.yfiles.graph.styles.IconLabelStyle;
import com.yworks.yfiles.graphml.GraphML;
import com.yworks.yfiles.graphml.MarkupExtension;
import com.yworks.yfiles.utils.Obfuscation;

import java.net.URL;

/**
 * Handles the yFiles for JavaFX 3.0.x version of GraphML element
 * <code>&lt;IconLabelStyle&gt;</code>.
 */
@GraphML(contentProperty = "Wrapped")
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public class IconLabelStyleExtension extends MarkupExtension {
  private boolean autoFlippingEnabled;
  private ILabelModelParameter iconPlacement;
  private SizeD iconSize;
  private ILabelStyle wrapped;
  private InsetsD wrappedInsets;
  private String icon;

  public IconLabelStyleExtension() {
    IconLabelStyle prototype = new IconLabelStyle();
    autoFlippingEnabled = prototype.isAutoFlippingEnabled();
    iconSize = prototype.getIconSize();
    iconPlacement = prototype.getIconPlacement();
    wrapped = prototype.getWrapped();
    wrappedInsets = prototype.getWrappedInsets();
    icon = fromUrl(prototype.getUrl());
  }
  public boolean isAutoFlippingEnabled() {
    return autoFlippingEnabled;
  }

  public void setAutoFlippingEnabled( boolean value ) {
    this.autoFlippingEnabled = value;
  }

  public ILabelModelParameter getIconPlacement() {
    return iconPlacement;
  }

  public void setIconPlacement( ILabelModelParameter value ) {
    this.iconPlacement = value;
  }

  public SizeD getIconSize() {
    return iconSize;
  }

  public void setIconSize( SizeD value ) {
    this.iconSize = value;
  }

  public ILabelStyle getWrapped() {
    return wrapped;
  }

  public void setWrapped( ILabelStyle value ) {
    this.wrapped = value;
  }

  public InsetsD getWrappedInsets() {
    return wrappedInsets;
  }

  public void setWrappedInsets( InsetsD value ) {
    this.wrappedInsets = value;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon( String value ) {
    this.icon = value;
  }

  @Override
  public Object provideValue( final ILookup serviceProvider ) {
    final IconLabelStyle style = new IconLabelStyle();
    style.setAutoFlippingEnabled(isAutoFlippingEnabled());
    style.setIconPlacement(getIconPlacement());
    style.setIconSize(getIconSize());
    style.setWrapped(getWrapped());
    style.setWrappedInsets(getWrappedInsets());
    style.setUrl(toUrl(getIcon()));
    return style;
  }

  private static String fromUrl( URL url ) {
    return url == null ? null : url.toExternalForm();
  }

  private static URL toUrl( String spec ) {
    if (spec == null) {
      return null;
    } else {
      try {
        return new URL(spec);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    }
  }
}

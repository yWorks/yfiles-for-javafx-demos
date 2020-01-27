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
package viewer.largegraphs;

import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * {@link TextField} that only accepts positive double values.
 */
public class DoubleTextField extends TextField {
  private static final Pattern VALID_PATTERN = Pattern.compile("(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

  final TextFormatter<Double> formatter;

  public DoubleTextField() {
    super();

    // fire onAction event when focus is lost
    focusedProperty().addListener((observable, oldValue, newValue) -> {
      if (!newValue) {
        fireEvent(new ActionEvent(this, null));
      }
    });

    UnaryOperator<TextFormatter.Change> filter = c -> {
      String text = c.getControlNewText();
      if (VALID_PATTERN.matcher(text).matches()) {
        return c ;
      } else {
        return null ;
      }
    };

    StringConverter<Double> converter = new StringConverter<Double>() {

      @Override
      public Double fromString(String s) {
        if (s.isEmpty()) {
          return 0.0 ;
        } else {
          return Double.valueOf(s);
        }
      }


      @Override
      public String toString(Double d) {
        return d.toString();
      }
    };

    formatter = new TextFormatter<>(converter, 0.0, filter);
    setTextFormatter(formatter);
  }

  public void setValue(double value) {
    formatter.setValue(value);
  }
}

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
package complete.bpmn.view;

import com.yworks.yfiles.utils.Obfuscation;

/**
 * Specifies the type of an activity according to BPMN.
 * @see ActivityNodeStyle
 */
@Obfuscation(stripAfterObfuscation = false, exclude = true, applyToMembers = true)
public enum ActivityType {
  /**
   * Specifies the type of an activity to be a Task according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  TASK(0),

  /**
   * Specifies the type of an activity to be a Sub-Process according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  SUB_PROCESS(1),

  /**
   * Specifies the type of an activity to be a Transaction Sub-Process according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  TRANSACTION(2),

  /**
   * Specifies the type of an activity to be an Event Sub-Process according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  EVENT_SUB_PROCESS(3),

  /**
   * Specifies the type of an activity to be a Call Activity according to BPMN.
   * @see complete.bpmn.view.ActivityNodeStyle
   */
  CALL_ACTIVITY(4);

  private final int value;

  private ActivityType( final int value ) {
    this.value = value;
  }

  public int value() {
    return this.value;
  }

  public static final ActivityType fromOrdinal( int ordinal ) {
    for (ActivityType current : values()) {
      if (ordinal == current.value) {
        return current;
      }
    }
    throw new IllegalArgumentException("Enum has no value " + ordinal);
  }

}

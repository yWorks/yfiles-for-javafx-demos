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
package viewer.printing;

import javafx.print.PageLayout;
import javafx.print.PrinterJob;
import javafx.scene.text.Text;

/**
 * Controller class for the input fields to configure the PageLayout of the PrinterJob.
 */
public class PageLayoutInputFieldController {

  // labels that display the current settings for the PageLayout
  public Text paperSizeLabel;
  public Text paperOrientationLabel;
  public Text paperMarginsTopLabel;
  public Text paperMarginsRightLabel;
  public Text paperMarginsBottomLabel;
  public Text paperMarginsLeftLabel;

  private PrinterJob job;

  /**
   * A simple mutex to prohibit multiple page setup dialogs from showing simultaneously
   */
  private boolean isPageSetupDialogShowing = false;

  /**
   * Brings up the page setup dialog in which the user can choose the paper settings for the printing.
   * The values are displayed on the left side of the application.
   */
  public void showPageSetupDialog() {
    if (job != null) {
      if (!isPageSetupDialogShowing) {
        isPageSetupDialogShowing = true;
        if (job.showPageSetupDialog(paperSizeLabel.getScene().getWindow())) {
          updateTextFields();
        }
        isPageSetupDialogShowing = false;
      }
    }
  }

  /**
   * Updates the values in the view that displays the values of the current set PageLayout.
   */
  public void updateTextFields() {
    if (job != null) {
      PageLayout newPageLayout = job.getJobSettings().getPageLayout();
      paperSizeLabel.setText(newPageLayout.getPaper().getName());
      paperOrientationLabel.setText(newPageLayout.getPageOrientation().name());
      paperMarginsTopLabel.setText((int) newPageLayout.getTopMargin() + "");
      paperMarginsRightLabel.setText((int) newPageLayout.getRightMargin() + "");
      paperMarginsBottomLabel.setText((int) newPageLayout.getBottomMargin() + "");
      paperMarginsLeftLabel.setText((int) newPageLayout.getLeftMargin() + "");
    }
  }

  /**
   * Returns the PrinterJob that is being configured by this instance.
   */
  public PrinterJob getJob() {
    return job;
  }

  /**
   * Sets the PrinterJob that is being configured by this instance.
   */
  public void setJob(final PrinterJob job) {
    this.job = job;
  }
}

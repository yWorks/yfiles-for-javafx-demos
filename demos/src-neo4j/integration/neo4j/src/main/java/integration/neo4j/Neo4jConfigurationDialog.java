/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.5.
 **
 ** Copyright (c) 2000-2022 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package integration.neo4j;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Configuration Dialog for the Neo4j database.
 */
public class Neo4jConfigurationDialog extends Dialog<DBInformation> {

  public Neo4jConfigurationDialog() {
    try {
      DialogPane dialog = FXMLLoader.load(getClass().getResource("Neo4jConfigurationDialog.fxml"));
      dialog.getButtonTypes().addAll(ButtonType.OK);
      setDialogPane(dialog);
      setTitle("Neo4j configuration");

      TextField dbUrlFld = (TextField) dialog.lookup("#dbUrlField");
      TextField dbNameFld = (TextField) dialog.lookup("#dbNameField");
      TextField usernameFld = (TextField) dialog.lookup("#usernameField");
      TextField passwordFld = (TextField) dialog.lookup("#passwordField");

      setResultConverter((buttonType) -> {
        if (buttonType != ButtonType.OK) {
          return null;
        }
        String dbUrl = dbUrlFld.getText();
        String dbName = dbNameFld.getText();
        String userName = usernameFld.getText();
        String password = passwordFld.getText();
        if (dbUrl.length() > 0 && userName.length() > 0 && password.length() > 0) {
          return new DBInformation(dbUrl, dbName, userName, password);
        } else {
          return null;
        }
      });
      Platform.runLater(passwordFld::requestFocus);
    } catch (IOException e) {
      throw new IllegalStateException("Could not initialize Neo4jConfigurationDialog.", e);
    }
  }
}

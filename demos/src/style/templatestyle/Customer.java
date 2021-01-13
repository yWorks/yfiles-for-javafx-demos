/****************************************************************************
 **
 ** This demo file is part of yFiles for JavaFX 3.4.
 **
 ** Copyright (c) 2000-2021 by yWorks GmbH, Vor dem Kreuzberg 28,
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
package style.templatestyle;

/**
 * A business object that describes a customer in our simple model.
 */
public class Customer {
  private String name;
  private String location;
  private int id;

  /**
   * Creates a new instance - for deserialization only.
   */
  public Customer() {
    this("", "", -1);
  }

  /**
   * Creates a new instance containing the given name, location and id.
   */
  public Customer(String name, String location, int id) {
    this.name = name;
    this.location = location;
    this.id = id;
  }

  /**
   * Returns the customer id.
   */
  public int getId() {
    return id;
  }

  /**
   * Specifies the customer id.
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Returns the customer location.
   */
  public String getLocation() {
    return location;
  }

  /**
   * Specifies the customer location.
   */
  public void setLocation(String location) {
    this.location = location;
  }

  /**
   * Returns the customer name.
   */
  public String getName() {
    return name;
  }

  /**
   * Specifies the customer name.
   */
  public void setName(String name) {
    this.name = name;
  }
}

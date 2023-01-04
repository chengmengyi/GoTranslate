/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package net.openvpn.ovpn3;

public class ClientAPI_ExternalPKISignRequest extends ClientAPI_ExternalPKIRequestBase {
  private transient long swigCPtr;

  protected ClientAPI_ExternalPKISignRequest(long cPtr, boolean cMemoryOwn) {
    super(ovpncliJNI.ClientAPI_ExternalPKISignRequest_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ClientAPI_ExternalPKISignRequest obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(ClientAPI_ExternalPKISignRequest obj) {
    long ptr = 0;
    if (obj != null) {
      if (!obj.swigCMemOwn)
        throw new RuntimeException("Cannot release ownership as memory is not owned");
      ptr = obj.swigCPtr;
      obj.swigCMemOwn = false;
      obj.delete();
    }
    return ptr;
  }

  @SuppressWarnings("deprecation")
  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        ovpncliJNI.delete_ClientAPI_ExternalPKISignRequest(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  public void setData(String value) {
    ovpncliJNI.ClientAPI_ExternalPKISignRequest_data_set(swigCPtr, this, value);
  }

  public String getData() {
    return ovpncliJNI.ClientAPI_ExternalPKISignRequest_data_get(swigCPtr, this);
  }

  public void setSig(String value) {
    ovpncliJNI.ClientAPI_ExternalPKISignRequest_sig_set(swigCPtr, this, value);
  }

  public String getSig() {
    return ovpncliJNI.ClientAPI_ExternalPKISignRequest_sig_get(swigCPtr, this);
  }

  public void setAlgorithm(String value) {
    ovpncliJNI.ClientAPI_ExternalPKISignRequest_algorithm_set(swigCPtr, this, value);
  }

  public String getAlgorithm() {
    return ovpncliJNI.ClientAPI_ExternalPKISignRequest_algorithm_get(swigCPtr, this);
  }

  public void setHashalg(String value) {
    ovpncliJNI.ClientAPI_ExternalPKISignRequest_hashalg_set(swigCPtr, this, value);
  }

  public String getHashalg() {
    return ovpncliJNI.ClientAPI_ExternalPKISignRequest_hashalg_get(swigCPtr, this);
  }

  public void setSaltlen(String value) {
    ovpncliJNI.ClientAPI_ExternalPKISignRequest_saltlen_set(swigCPtr, this, value);
  }

  public String getSaltlen() {
    return ovpncliJNI.ClientAPI_ExternalPKISignRequest_saltlen_get(swigCPtr, this);
  }

  public ClientAPI_ExternalPKISignRequest() {
    this(ovpncliJNI.new_ClientAPI_ExternalPKISignRequest(), true);
  }

}

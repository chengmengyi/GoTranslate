/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (https://www.swig.org).
 * Version 4.1.1
 *
 * Do not make changes to this file unless you know what you are doing - modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package net.openvpn.ovpn3;

public class ClientAPI_Event {
  private transient long swigCPtr;
  protected transient boolean swigCMemOwn;

  protected ClientAPI_Event(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(ClientAPI_Event obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected static long swigRelease(ClientAPI_Event obj) {
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
        ovpncliJNI.delete_ClientAPI_Event(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setError(boolean value) {
    ovpncliJNI.ClientAPI_Event_error_set(swigCPtr, this, value);
  }

  public boolean getError() {
    return ovpncliJNI.ClientAPI_Event_error_get(swigCPtr, this);
  }

  public void setFatal(boolean value) {
    ovpncliJNI.ClientAPI_Event_fatal_set(swigCPtr, this, value);
  }

  public boolean getFatal() {
    return ovpncliJNI.ClientAPI_Event_fatal_get(swigCPtr, this);
  }

  public void setName(String value) {
    ovpncliJNI.ClientAPI_Event_name_set(swigCPtr, this, value);
  }

  public String getName() {
    return ovpncliJNI.ClientAPI_Event_name_get(swigCPtr, this);
  }

  public void setInfo(String value) {
    ovpncliJNI.ClientAPI_Event_info_set(swigCPtr, this, value);
  }

  public String getInfo() {
    return ovpncliJNI.ClientAPI_Event_info_get(swigCPtr, this);
  }

  public ClientAPI_Event() {
    this(ovpncliJNI.new_ClientAPI_Event(), true);
  }

}

/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 3.0.12
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package com.chilkatsoft;

public class CkFtp2Progress extends CkBaseProgress {
  private transient long swigCPtr;

  protected CkFtp2Progress(long cPtr, boolean cMemoryOwn) {
    super(chilkatJNI.CkFtp2Progress_SWIGUpcast(cPtr), cMemoryOwn);
    swigCPtr = cPtr;
  }

  protected static long getCPtr(CkFtp2Progress obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        chilkatJNI.delete_CkFtp2Progress(swigCPtr);
      }
      swigCPtr = 0;
    }
    super.delete();
  }

  protected void swigDirectorDisconnect() {
    swigCMemOwn = false;
    delete();
  }

  public void swigReleaseOwnership() {
    swigCMemOwn = false;
    chilkatJNI.CkFtp2Progress_change_ownership(this, swigCPtr, false);
  }

  public void swigTakeOwnership() {
    swigCMemOwn = true;
    chilkatJNI.CkFtp2Progress_change_ownership(this, swigCPtr, true);
  }

  public CkFtp2Progress() {
    this(chilkatJNI.new_CkFtp2Progress(), true);
    chilkatJNI.CkFtp2Progress_director_connect(this, swigCPtr, swigCMemOwn, true);
  }

  public boolean BeginDownloadFile(String pathUtf8) {
    return (getClass() == CkFtp2Progress.class) ? chilkatJNI.CkFtp2Progress_BeginDownloadFile(swigCPtr, this, pathUtf8) : chilkatJNI.CkFtp2Progress_BeginDownloadFileSwigExplicitCkFtp2Progress(swigCPtr, this, pathUtf8);
  }

  public boolean VerifyDownloadDir(String pathUtf8) {
    return (getClass() == CkFtp2Progress.class) ? chilkatJNI.CkFtp2Progress_VerifyDownloadDir(swigCPtr, this, pathUtf8) : chilkatJNI.CkFtp2Progress_VerifyDownloadDirSwigExplicitCkFtp2Progress(swigCPtr, this, pathUtf8);
  }

  public boolean BeginUploadFile(String pathUtf8) {
    return (getClass() == CkFtp2Progress.class) ? chilkatJNI.CkFtp2Progress_BeginUploadFile(swigCPtr, this, pathUtf8) : chilkatJNI.CkFtp2Progress_BeginUploadFileSwigExplicitCkFtp2Progress(swigCPtr, this, pathUtf8);
  }

  public boolean VerifyUploadDir(String pathUtf8) {
    return (getClass() == CkFtp2Progress.class) ? chilkatJNI.CkFtp2Progress_VerifyUploadDir(swigCPtr, this, pathUtf8) : chilkatJNI.CkFtp2Progress_VerifyUploadDirSwigExplicitCkFtp2Progress(swigCPtr, this, pathUtf8);
  }

  public boolean VerifyDeleteDir(String pathUtf8) {
    return (getClass() == CkFtp2Progress.class) ? chilkatJNI.CkFtp2Progress_VerifyDeleteDir(swigCPtr, this, pathUtf8) : chilkatJNI.CkFtp2Progress_VerifyDeleteDirSwigExplicitCkFtp2Progress(swigCPtr, this, pathUtf8);
  }

  public boolean VerifyDeleteFile(String pathUtf8) {
    return (getClass() == CkFtp2Progress.class) ? chilkatJNI.CkFtp2Progress_VerifyDeleteFile(swigCPtr, this, pathUtf8) : chilkatJNI.CkFtp2Progress_VerifyDeleteFileSwigExplicitCkFtp2Progress(swigCPtr, this, pathUtf8);
  }

  public void EndUploadFile(String pathUtf8, long numBytes) {
    if (getClass() == CkFtp2Progress.class) chilkatJNI.CkFtp2Progress_EndUploadFile(swigCPtr, this, pathUtf8, numBytes); else chilkatJNI.CkFtp2Progress_EndUploadFileSwigExplicitCkFtp2Progress(swigCPtr, this, pathUtf8, numBytes);
  }

  public void EndDownloadFile(String pathUtf8, long numBytes) {
    if (getClass() == CkFtp2Progress.class) chilkatJNI.CkFtp2Progress_EndDownloadFile(swigCPtr, this, pathUtf8, numBytes); else chilkatJNI.CkFtp2Progress_EndDownloadFileSwigExplicitCkFtp2Progress(swigCPtr, this, pathUtf8, numBytes);
  }

  public void UploadRate(long byteCount, long bytesPerSec) {
    if (getClass() == CkFtp2Progress.class) chilkatJNI.CkFtp2Progress_UploadRate(swigCPtr, this, byteCount, bytesPerSec); else chilkatJNI.CkFtp2Progress_UploadRateSwigExplicitCkFtp2Progress(swigCPtr, this, byteCount, bytesPerSec);
  }

  public void DownloadRate(long byteCount, long bytesPerSec) {
    if (getClass() == CkFtp2Progress.class) chilkatJNI.CkFtp2Progress_DownloadRate(swigCPtr, this, byteCount, bytesPerSec); else chilkatJNI.CkFtp2Progress_DownloadRateSwigExplicitCkFtp2Progress(swigCPtr, this, byteCount, bytesPerSec);
  }

}

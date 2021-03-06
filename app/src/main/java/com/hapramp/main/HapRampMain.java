package com.hapramp.main;

import android.content.Context;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.hapramp.utils.FingerprintManager;

/**
 * Created by Ankit on 5/16/2017.
 */

public class HapRampMain extends MultiDexApplication {

  private static Context context;
  private static String fp;
  private static String sessionId;

  public static String getFp() {
    return fp;
  }

  public void onCreate() {
    super.onCreate();
    HapRampMain.context = getApplicationContext();
    FirebaseApp.initializeApp(context);
    fp = FingerprintManager.getCertificateSHA1Fingerprint(context);
    sessionId = String.valueOf(System.currentTimeMillis());
  }

  public static String getSessionId(){
    return sessionId;
  }

  public static Context getContext() {
    return HapRampMain.context;
  }
}

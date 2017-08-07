package com.type.sdk.android.pipa;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.type.utils.GetResId;

public class TypeSDKSplash extends Activity {

    private long m_dwSplashTime=3000;
    private boolean m_bPaused=false;
    private boolean m_bSplashActive=true;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(GetResId.getId(this, "layout", "splash"));

        Thread splashTimer=new Thread()
        {
            public void run(){
                try{
                    //wait loop
                    long ms=0;
                    while(m_bSplashActive && ms<m_dwSplashTime){
                        sleep(100);

                        if(!m_bPaused)
                            ms+=100;
                    }

                    startActivity(new Intent("com.google.app.splashy.CLEARSPLASH"));
                }
                catch(Exception ex){
                    Log.e("Splash",ex.getMessage());
                }
                finally{
                    finish();
                }
            }
        };
        splashTimer.start();
    }
}
package com.example.andrea22.gamehunt;

import android.app.Activity;
import android.support.multidex.MultiDexApplication;

/**
 * Created by andre on 13/11/2016.
 */

public class GameHunt extends MultiDexApplication {
    public void onCreate() {
        super.onCreate();
    }

    private Activity mCurrentActivity = null;
    public Activity getCurrentActivity(){
        return mCurrentActivity;
    }
    public void setCurrentActivity(Activity mCurrentActivity){
        this.mCurrentActivity = mCurrentActivity;
    }
}

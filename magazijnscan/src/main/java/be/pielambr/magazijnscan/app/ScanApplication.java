package be.pielambr.magazijnscan.app;

import android.app.Application;

import com.google.zxing.integration.android.IntentResult;

/**
 * Created by Pieterjan Lambrecht on 8/09/2014.
 */
public class ScanApplication extends Application {

    private IntentResult result;

    public ScanApplication() {

    }

    public IntentResult getResult() {
        return result;
    }

    public void setResult(IntentResult result) {
        this.result = result;
    }
}

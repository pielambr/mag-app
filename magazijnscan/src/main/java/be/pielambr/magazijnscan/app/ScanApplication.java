package be.pielambr.magazijnscan.app;

import android.app.Application;
import android.location.Location;

import com.google.zxing.integration.android.IntentResult;

/**
 * Created by Pieterjan Lambrecht on 8/09/2014.
 */
public class ScanApplication extends Application {

    private IntentResult result;
    private Location location;

    public ScanApplication() {

    }

    public IntentResult getResult() {
        return result;
    }

    public void setResult(IntentResult result) {
        this.result = result;
    }

    public void setLastLocation(Location location) {
        this.location = location;
    }

    public Location getLastLocation() {
        return this.location;
    }
}

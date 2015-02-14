package be.pielambr.magazijnscan.app;

import android.app.Application;
import android.location.Location;

import com.google.zxing.integration.android.IntentResult;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by Pieterjan Lambrecht on 8/09/2014.
 */
@ReportsCrashes(
        formKey = "", // This is required for backward compatibility but not used
        formUri = "http://www.pielambr.be/acra/report/report.php",
        httpMethod = org.acra.sender.HttpSender.Method.PUT,
        reportType = org.acra.sender.HttpSender.Type.JSON
)
public class ScanApplication extends Application {

    private IntentResult result;
    private Location location;

    public ScanApplication() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
        // The following line triggers the initialization of ACRA
        ACRA.init(this);
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

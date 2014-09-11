package be.pielambr.magazijnscan.magazijnscan;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;

import be.pielambr.magazijnscan.api.MagazijnAPI;
import be.pielambr.magazijnscan.app.ScanApplication;
import be.pielambr.util.Services;

/**
 * Created by Pieterjan Lambrecht on 8/09/2014.
 */
public class ScanResult extends Activity {
    private String barcode;
    private Location location;
    private MagazijnAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        this.api = new MagazijnAPI(this);
        this.barcode = ((ScanApplication) getApplication()).getResult().getContents();
        this.location = ((ScanApplication) getApplication()).getLastLocation();
    }
}

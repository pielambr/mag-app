package be.pielambr.magazijnscan.magazijnscan;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.zxing.integration.android.IntentResult;

import be.pielambr.magazijnscan.app.ScanApplication;

/**
 * Created by Pieterjan Lambrecht on 8/09/2014.
 */
public class ScanResult extends Activity {
    private IntentResult result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        this.result = ((ScanApplication) getApplication()).getResult();
        ((TextView) findViewById(R.id.textResults)).setText(result.getContents());
    }
}

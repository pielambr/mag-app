package be.pielambr.magazijnscan.magazijnscan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import be.pielambr.magazijnscan.app.ScanApplication;
import be.pielambr.util.Services;


public class Scan extends Activity implements LocationListener {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        MapsInitializer.initialize(this);
        TextView gps = (TextView) findViewById(R.id.textGPS);
        TextView internet = (TextView) findViewById(R.id.textInternet);
        if(Services.internetAvailable(this)){
            internet.setText(getString(R.string.available));
            internet.setTextColor(getResources().getColor(R.color.green));
        } else {
            internet.setText(getString(R.string.not_available));
            internet.setTextColor(getResources().getColor(R.color.red));
        }
        if(Services.locationAvailable(this)){
            gps.setText(getString(R.string.available));
            gps.setTextColor(getResources().getColor(R.color.green));
        } else {
            gps.setText(getString(R.string.not_available));
            gps.setTextColor(getResources().getColor(R.color.red));
        }
        if(mMap == null) {
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        }
        // Enable current location
        mMap.setMyLocationEnabled(true);
        // Listen for location changes
        LocationManager loc = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        loc.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
        if(Services.getPassword(this) != null && Services.getServerURL(this) != null) {
            hideSettings();
        } else {
            showSettings();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            // Show settings
            showSettings();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void scan(View v){
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if(scanningResult != null){
            ((ScanApplication) getApplication()).setResult(scanningResult);
            Intent i = new Intent(this, ScanResult.class);
            startActivity(i);
        } else {
            Toast toast = Toast.makeText(this, getString(R.string.no_scan_result), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng latlng = new LatLng(location.getLatitude(),location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17.0f));
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void showSettings() {
        findViewById(R.id.scanLayer).setVisibility(View.GONE);
        findViewById(R.id.settingsLayer).setVisibility(View.VISIBLE);
        EditText pw = (EditText) findViewById(R.id.editPassword);
        pw.setText(Services.getPassword(this));
        EditText server = (EditText) findViewById(R.id.editServer);
        server.setText(Services.getServerURL(this));
    }

    public void hideSettings() {
        findViewById(R.id.settingsLayer).setVisibility(View.GONE);
        findViewById(R.id.scanLayer).setVisibility(View.VISIBLE);
    }

    public void saveSettings(View v) {
        EditText pw = (EditText) findViewById(R.id.editPassword);
        EditText server = (EditText) findViewById(R.id.editServer);
        Services.savePassword(pw.getText().toString(), this);
        Services.saveServerURL(server.getText().toString(), this);
        hideSettings();
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(pw.getWindowToken(), 0);
    }
}

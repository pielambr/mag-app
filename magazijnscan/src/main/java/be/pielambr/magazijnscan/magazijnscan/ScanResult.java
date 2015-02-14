package be.pielambr.magazijnscan.magazijnscan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import be.pielambr.magazijnscan.api.Barcode;
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
    private Barcode code;

    private EditText codeField;
    private EditText descriptionField;
    private AutoCompleteTextView supplierField;

    private Button insertBtn;
    private Button checkoutBtn;
    private Button updateBtn;

    private String[] dealers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        this.api = new MagazijnAPI(this);
        this.barcode = ((ScanApplication) getApplication()).getResult().getContents();
        this.location = ((ScanApplication) getApplication()).getLastLocation();
        // Fields
        this.codeField = (EditText) findViewById(R.id.editBarcode);
        this.descriptionField = (EditText) findViewById(R.id.editDescription);
        this.supplierField = (AutoCompleteTextView) findViewById(R.id.editSupplier);
        // Buttons
        this.insertBtn = (Button) findViewById(R.id.buttonInsert);
        this.checkoutBtn = (Button) findViewById(R.id.buttonCheckout);
        this.updateBtn = (Button) findViewById(R.id.buttonUpdate);
        // Check code
        checkBarcode();
    }

    private void checkBarcode() {
        final Handler handler = new Handler(getMainLooper());
        code = new Barcode();
        code.code = barcode;
        // Check results of barcode
        new Thread(new Runnable() {
            public void run() {
                try {
                    api.makeRequest(MagazijnAPI.Method.GET, code);
                } catch (Exception ex){
                    showError();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(api.resultIsSuccess()) {
                            // Check the barcode object in the api (empty or not)
                            Barcode b = api.getBarcodeResult();
                            if(b == null) {
                                loadNewCode();
                                loadDealers();
                            } else {
                                code = b;
                                loadExistingCode();
                            }
                        } else {
                            showError();
                            popActivityStack();
                        }
                    }
                });
            }
        }).start();
    }

    private void loadNewCode() {
        insertBtn.setVisibility(View.VISIBLE);
        updateBtn.setVisibility(View.GONE);
        checkoutBtn.setVisibility(View.GONE);
        codeField.setText(code.code);
        disableField(codeField);
        stopLoading();
    }

    private void loadExistingCode() {
        insertBtn.setVisibility(View.GONE);
        updateBtn.setVisibility(View.VISIBLE);
        checkoutBtn.setVisibility(View.VISIBLE);
        codeField.setText(code.code);
        disableField(codeField);
        descriptionField.setText(code.description);
        disableField(descriptionField);
        supplierField.setText(code.leverancier);
        disableField(supplierField);
        stopLoading();
    }

    private void disableField(View v) {
        v.setFocusable(false);
        v.setFocusableInTouchMode(false);
        v.setClickable(false);
        v.setEnabled(false);
    }

    private void showError() {
        Toast error = Toast.makeText(getApplicationContext(), "Error happened connecting to API", Toast.LENGTH_LONG);
        error.show();
    }

    private void popActivityStack() {
        Intent intent = new Intent(this, Scan.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void showSuccess() {
        Toast success = Toast.makeText(getApplicationContext(), "API action successful!", Toast.LENGTH_LONG);
        success.show();
    }

    private void stopLoading() {
        findViewById(R.id.progressBar).setVisibility(View.GONE);
        findViewById(R.id.layerForm).setVisibility(View.VISIBLE);
    }

    private void startLoading() {
        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        findViewById(R.id.layerForm).setVisibility(View.GONE);
    }

    public void updateBarcode(View v) {
        startLoading();
        // Update barcode values
        code.latitude = String.valueOf(location.getLatitude());
        code.longitude = String.valueOf(location.getLongitude());
        // Submit to API
        final Handler handler = new Handler(getMainLooper());
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        new Thread(new Runnable() {
            public void run() {
                try {
                    api.makeRequest(MagazijnAPI.Method.UPDATE, code);
                } catch (Exception ex){
                    showError();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(api.resultIsSuccess()) {
                            showSuccess();
                            popActivityStack();
                        } else {
                            showError();
                            stopLoading();
                        }
                    }
                });
            }
        }).start();
    }

    public void insertBarcode(View v) {
        startLoading();
        // Update barcode values
        code.description = descriptionField.getText().toString();
        code.leverancier = supplierField.getText().toString();
        code.latitude = String.valueOf(location.getLatitude());
        code.longitude = String.valueOf(location.getLongitude());
        // Submit to API
        final Handler handler = new Handler(getMainLooper());
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        new Thread(new Runnable() {
            public void run() {
                try {
                    api.makeRequest(MagazijnAPI.Method.INSERT, code);
                } catch (Exception ex){
                    showError();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(api.resultIsSuccess()) {
                            showSuccess();
                            popActivityStack();
                        } else {
                            showError();
                            stopLoading();
                        }
                    }
                });
            }
        }).start();
    }

    public void checkoutBarcode(View v){
        startLoading();
        // Submit to API
        final Handler handler = new Handler(getMainLooper());
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        new Thread(new Runnable() {
            public void run() {
                try {
                    api.makeRequest(MagazijnAPI.Method.CHECKOUT, code);
                } catch (Exception ex){
                    showError();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(api.resultIsSuccess()) {
                            showSuccess();
                            popActivityStack();
                        } else {
                            showError();
                            stopLoading();
                        }
                    }
                });
            }
        }).start();
    }

    private void loadDealers() {
        final Handler handler = new Handler(getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    api.makeRequest(MagazijnAPI.Method.DEALERS, null);
                } catch (Exception ex) {
                    showError();
                }
                final JSONArray obj = api.getJSONArray(api.getResponse().getEntity());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if(api.resultIsSuccess()) {
                            dealers = new String[obj.length()];
                            for(int i = 0; i < obj.length(); i++) {
                                try {
                                    JSONObject object = obj.getJSONObject(i);
                                    dealers[i] = object.getString("naam");
                                } catch (JSONException ex) {
                                    // Do nothing
                                }
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, dealers);
                            supplierField.setAdapter(adapter);
                        } else {
                            showError();
                        }
                    }
                });
            }
        }).start();
    }
}

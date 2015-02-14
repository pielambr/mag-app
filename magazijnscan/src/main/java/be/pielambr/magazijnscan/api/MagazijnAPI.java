package be.pielambr.magazijnscan.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import be.pielambr.util.Services;

/**
 * Created by Pieterjan Lambrecht on 11/09/2014.
 */
public class MagazijnAPI {
    private final String API_URL;
    private final String API_PASSWORD;
    private HttpClient client;
    private boolean success;
    private Barcode barcode;
    private HttpResponse response;

    public MagazijnAPI(Context c) {
        this.API_URL = Services.getAPIURL(c) + "?action=";
        this.API_PASSWORD = Services.getPassword(c);
        this.client = new DefaultHttpClient();
    }

    public void makeRequest(Method m, Barcode b){
        HttpPost request = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("password", API_PASSWORD));
        switch(m) {
            case PRINT:
                request = new HttpPost(API_URL + "print");
                executeRequest(request, nameValuePairs);
                break;
            case DEALERS:
                request = new HttpPost(API_URL + "dealers");
                executeRequest(request, nameValuePairs);
                break;
            case UPDATE:
                request = new HttpPost(API_URL + "update");
                nameValuePairs.add(new BasicNameValuePair("barcode", b.code));
                nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(b.longitude)));
                nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(b.latitude)));
                executeRequest(request, nameValuePairs);
                break;
            case INSERT:
                request = new HttpPost(API_URL + "insert");
                nameValuePairs.add(new BasicNameValuePair("barcode", b.code));
                nameValuePairs.add(new BasicNameValuePair("description", b.description));
                nameValuePairs.add(new BasicNameValuePair("leverancier", b.leverancier));
                nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(b.longitude)));
                nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(b.latitude)));
                executeRequest(request, nameValuePairs);
                break;
            case CHECKOUT:
                request = new HttpPost(API_URL + "checkout");
                nameValuePairs.add(new BasicNameValuePair("barcode", b.code));
                executeRequest(request, nameValuePairs);
                break;
            case GET:
                request = new HttpPost(API_URL + "get");
                nameValuePairs.add(new BasicNameValuePair("barcode", b.code));
                HttpResponse res = executeRequest(request, nameValuePairs);
                if(success) {
                    JSONObject json = getJSON(res.getEntity());
                    if(json.length() < 1) {
                        this.barcode = null;
                    } else {
                        try {
                            this.barcode = new Barcode();
                            barcode.code = json.getString("code");
                            barcode.description = json.getString("description");
                            barcode.leverancier = json.getString("leverancier");
                            barcode.latitude = json.getString("latitude");
                            barcode.longitude = json.getString("longitude");
                        } catch (JSONException ex) {
                            this.barcode = null;
                        }
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("This API method is not available");
        }
    }

    private HttpResponse executeRequest(HttpPost req, List<NameValuePair> values){
        try {
            req.setEntity(new UrlEncodedFormEntity(values));
            HttpResponse response = client.execute(req);
            if(response.getStatusLine().getStatusCode() != 200) {
                success = false;
            } else {
                success = true;
                this.response = response;
                return response;
            }
        } catch (IOException e) {
            barcode = null;
            success = false;
        }
        this.response = null;
        return null;
    }

    public JSONObject getJSON(HttpEntity entity){
        InputStream inputStream = null;
        try {
            inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return new JSONObject(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONObject();
        } catch (JSONException e){
            return new JSONObject();
        }
    }

    public JSONArray getJSONArray(HttpEntity entity) {
        InputStream inputStream = null;
        try {
            inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            return new JSONArray(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return new JSONArray();
        } catch (JSONException e){
            return new JSONArray();
        }
    }

    public Barcode getBarcodeResult() {
        return this.barcode;
    }

    public boolean resultIsSuccess() {
        return this.success;
    }

    public HttpResponse getResponse() {
        return this.response;
    }

    public enum Method {
        UPDATE, INSERT, GET, CHECKOUT, PRINT, DEALERS
    }
}

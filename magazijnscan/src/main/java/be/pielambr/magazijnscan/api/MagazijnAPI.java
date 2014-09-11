package be.pielambr.magazijnscan.api;

import android.content.Context;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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

    public MagazijnAPI(Context c) {
        this.API_URL = Services.getServerURL(c)+ "?action=";
        this.API_PASSWORD = Services.getPassword(c);
        this.client = new DefaultHttpClient();
    }

    public void makeRequest(Method m, Barcode b){
        HttpPost request = null;
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
        nameValuePairs.add(new BasicNameValuePair("password", API_PASSWORD));
        switch(m) {
            case UPDATE:
                request = new HttpPost(API_URL + "update");
                break;
            case INSERT:
                request = new HttpPost(API_URL + "insert");
                break;
            case CHECKOUT:
                request = new HttpPost(API_URL + "checkout");
                break;
            case GET:
                request = new HttpPost(API_URL + "return");
                break;
            default:
                throw new IllegalArgumentException("This API method is not available");
        }
    }

    private HttpResponse executeRequest(HttpPost req, List<NameValuePair> values){
        try {
            req.setEntity(new UrlEncodedFormEntity(values));
            req.setHeader("Content-type", "application/json");
            HttpResponse response = client.execute(req);
            if(response.getStatusLine().getStatusCode() != 200) {
                success = false;
            } else {
                success = true;
                return response;
            }
        } catch (IOException e) {
            barcode = null;
            success = false;
        }
        return null;
    }

    private JSONObject getJSON(HttpEntity entity){
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

    public Barcode getBarcodeResult() {
        return this.barcode;
    }

    public boolean resultIsSuccess() {
        return this.success;
    }

    public enum Method {
        UPDATE, INSERT, GET, CHECKOUT
    }
}

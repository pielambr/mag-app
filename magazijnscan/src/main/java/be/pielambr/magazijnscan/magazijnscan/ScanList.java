package be.pielambr.magazijnscan.magazijnscan;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import be.pielambr.magazijnscan.api.Barcode;
import be.pielambr.magazijnscan.api.MagazijnAPI;
import be.pielambr.magazijnscan.list.OverviewListAdapter;

/**
 * Created by Pieterjan Lambrecht on 18/09/2014.
 */
public class ScanList extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        loadList();
    }

    private void loadList() {
        final ListView listView = (ListView) findViewById(R.id.listCodes);
        final Handler handler = new Handler(getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<Barcode> list = new ArrayList();
                try {
                    MagazijnAPI api = new MagazijnAPI(getBaseContext());
                    api.makeRequest(MagazijnAPI.Method.PRINT, null);
                    JSONArray arr = api.getJSONArray(api.getResponse().getEntity());
                    for (int i = 0; i < arr.length(); i++) {
                        JSONObject obj = arr.getJSONObject(i);
                        Barcode code = new Barcode();
                        code.code = obj.getString("code");
                        code.description = obj.getString("description");
                        code.leverancier = obj.getString("leverancier");
                        list.add(code);
                    }
                } catch (Exception ex) {
                    // Nothing
                }
                final Barcode[] barcodes = list.toArray(new Barcode[list.size()]);
                handler.post(new Runnable() {
                                 @Override
                                 public void run() {
                                     OverviewListAdapter adapter = new OverviewListAdapter(getBaseContext(), barcodes);
                                     listView.setAdapter(adapter);
                                 }
                             }
                );
            }
        }).start();
    }
}

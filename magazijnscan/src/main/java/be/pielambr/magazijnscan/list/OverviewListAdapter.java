package be.pielambr.magazijnscan.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import be.pielambr.magazijnscan.api.Barcode;
import be.pielambr.magazijnscan.magazijnscan.R;

/**
 * Created by Pieterjan Lambrecht on 18/09/2014.
 */
public class OverviewListAdapter extends ArrayAdapter<Barcode> {
    private Barcode[] barcodes;
    private LayoutInflater inflater;

    public OverviewListAdapter(Context context, Barcode[] codes) {
        super(context, R.layout.list_barcode, codes);
        this.barcodes = codes;
    }


    @Override
    public int getCount() {
        return this.barcodes.length;
    }

    @Override
    public Barcode getItem(int i) {
        return this.barcodes[i];
    }

    private LayoutInflater getLayoutInflater() {
        if (this.inflater == null) {
            this.inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return this.inflater;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Barcode code = getItem(position);
        final ViewHolder vh;
        View v = convertView;
        if (v == null) {
            v = getLayoutInflater().inflate(R.layout.list_barcode, parent, false);
            vh = new ViewHolder();
            vh.code = (TextView) v.findViewById(R.id.barcodeCode);
            vh.description = (TextView) v.findViewById(R.id.barcodeDescription);
            vh.supplier = (TextView) v.findViewById(R.id.barcodeSupplier);
            v.setTag(vh);
        } else {
            vh = (ViewHolder) v.getTag();
        }
        vh.code.setText(code.code);
        vh.description.setText(code.description);
        vh.supplier.setText(code.leverancier);
        return v;
    }

    static class ViewHolder {
        TextView code;
        TextView description;
        TextView supplier;
    }
}

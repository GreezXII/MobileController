package com.greezxii.mobilecontroller.spinner;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.greezxii.mobilecontroller.model.Address;

import java.util.Arrays;
import java.util.List;

public class AddressArrayAdapter extends ArrayAdapter<Address> {
    private final List<Address> values;

    public AddressArrayAdapter(Context context, int textViewResourceId, List<Address> values) {
        super(context, textViewResourceId, values);
        // Empty address
        values.add(new Address());
        this.values = values;

    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Address getItem(int position){
        return values.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        Address address = values.get(position);
        label.setText(values.get(position).getBuildingAddress());
        return label;
    }

    @Override
    public void addAll(Address... items) {
        //super.addAll(items);
        values.addAll(Arrays.asList(items));
    }
}

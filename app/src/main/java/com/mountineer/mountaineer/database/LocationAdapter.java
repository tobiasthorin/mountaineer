package com.mountineer.mountaineer.database;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mountineer.mountaineer.MountaineerLocation;
import com.mountineer.mountaineer.R;

import java.util.ArrayList;

/**
 * Created by Tobias on 16/02/16.
 */
public class LocationAdapter extends ArrayAdapter {

    private ArrayList<MountaineerLocation> mountaineerLocationList;

    public LocationAdapter(Context context, int resource) {
        super(context, resource);
        mountaineerLocationList = new ArrayList<>();
    }

    public void add(MountaineerLocation mountaineerLocation) {
        mountaineerLocationList.add(mountaineerLocation);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.location_list_item, parent, false);
        }

        MountaineerLocation currentMountaineerLocation = mountaineerLocationList.get(position);

        TextView txtDate = (TextView) convertView.findViewById(R.id.txtHistoryDate);
        TextView txtLocation = (TextView) convertView.findViewById(R.id.txtHistoryLocation);
        TextView txtAltitude = (TextView) convertView.findViewById(R.id.txtHistoryAltitude);

        txtAltitude.setText(currentMountaineerLocation.getAltitude());
        txtDate.setText(currentMountaineerLocation.getDate());
        txtLocation.setText(currentMountaineerLocation.getLocation());

        return convertView;
    }

    @Override
    public int getCount() {
        return mountaineerLocationList.size();
    }

    @Override
    public Object getItem(int position) {
        return mountaineerLocationList.get(position);
    }
}

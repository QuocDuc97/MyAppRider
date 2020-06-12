package com.example.taxirider.helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.taxirider.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomerInforWindow implements GoogleMap.InfoWindowAdapter {

    View myView;

    public CustomerInforWindow(Context context) {
        myView= LayoutInflater.from(context).inflate(R.layout.customer_rider_infor_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {

        TextView pickUPInfor= (TextView) myView.findViewById(R.id.txtPickupInfor);
        pickUPInfor.setText(marker.getTitle());

        TextView pickUpSnipet = (TextView) myView.findViewById(R.id.txtPickupSnipet);
        pickUpSnipet.setText(marker.getSnippet());

        return myView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}

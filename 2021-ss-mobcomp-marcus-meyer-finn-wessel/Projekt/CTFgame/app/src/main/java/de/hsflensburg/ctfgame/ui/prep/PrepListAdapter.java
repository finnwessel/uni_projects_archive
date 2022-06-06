package de.hsflensburg.ctfgame.ui.prep;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import de.hsflensburg.ctfgame.R;
import de.hsflensburg.ctfgame.dto.GpsPoint;
import de.hsflensburg.ctfgame.dto.Participant;

public class PrepListAdapter extends ArrayAdapter<GpsPoint> {

    private Context mContext;
    private List<GpsPoint> gpsPointsList = new ArrayList<>();

    //@LayoutRes
    public PrepListAdapter(@NonNull Context context, ArrayList<GpsPoint> list) {
        super(context, 0, list);
        mContext = context;
        gpsPointsList = list;
    }

    public void setGpsPointsList(ArrayList<GpsPoint> list) {
        this.gpsPointsList = list;
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.prep_list_item,parent,false);

        GpsPoint currentGpsPoint = gpsPointsList.get(position);

        TextView title = (TextView) listItem.findViewById(R.id.prep_gps_point_title);
        title.setText(String.format("%d. Gps Point", position + 1));

        TextView description = (TextView) listItem.findViewById(R.id.prep_gps_point_description);
        description.setText(String.format("Long: %s Lat:%s", currentGpsPoint.longitude, currentGpsPoint.latitude));

        return listItem;
    }

    @Override
    public int getCount() {
        return gpsPointsList.size();
    }
}

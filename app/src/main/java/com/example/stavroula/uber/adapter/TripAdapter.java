package com.example.stavroula.uber.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stavroula.uber.R;
import com.example.stavroula.uber.TripDetailsActivity;
import com.example.stavroula.uber.entity.Trip;

import java.util.List;

import static java.lang.String.valueOf;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private List<Trip> tripList;
    private Context context;

    public TripAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    class TripViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final View view;

        TextView txtDate;
        ImageButton details_btn;

        TripViewHolder(View itemView) {
            super(itemView);
            view = itemView;

            txtDate = view.findViewById(R.id.date);
            details_btn = view.findViewById(R.id.trip_details_btn);
            details_btn.setOnClickListener(this);

        }
        @Override
        public void onClick(View v) {
            Toast.makeText(context,"Clicked button", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public TripViewHolder onCreateViewHolder(ViewGroup parent, int ViewType) {
        LayoutInflater layoutInflater = LayoutInflater.from((parent.getContext()));
        View view = layoutInflater.inflate(R.layout.trip_list_item_layout, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TripAdapter.TripViewHolder holder, int position) {
        holder.txtDate.setText(tripList.get(position).getDate());
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, TripDetailsActivity.class);
                context.startActivity(intent);
                Toast.makeText(context,"Clicked button", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setTrips(List<Trip> trips) {
        tripList = trips;
        Log.d("123", valueOf(tripList.size()));
    }

    @Override
    public int getItemCount() {

        int a;

        if (tripList != null && !tripList.isEmpty()) {

            a = tripList.size();
        } else {

            a = 0;

        }

        return a;
    }

    private void  trip_details(){
       /* LayoutInflater inflater = LayoutInflater.from(this);
        View trip_details = inflater.inflate(R.layout.trip_details_layout,null);
        Intent intent = new Intent(TripAdapter.this,TripDetailsActivity.class);
        startActivity(intent);*/
    }
}


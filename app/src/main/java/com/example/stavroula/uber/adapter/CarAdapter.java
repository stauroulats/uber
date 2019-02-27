package com.example.stavroula.uber.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stavroula.uber.R;
import com.example.stavroula.uber.entity.Car;

import java.util.List;

import static java.lang.String.valueOf;


    public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

        private List<Car> dataList;
        private Context context;

        public CarAdapter(Context context, List<Car> dataList) {
            this.context = context;
            this.dataList = dataList;
        }

        class CarViewHolder extends RecyclerView.ViewHolder {

            public final View mView;

            TextView txtManufacturer, txtModel , txtRegistrationPlate;

            CarViewHolder(View itemView) {
                super(itemView);
                mView = itemView;

                txtManufacturer = mView.findViewById(R.id.manufacturer);
                txtModel = mView.findViewById(R.id.model);
                txtRegistrationPlate = mView.findViewById(R.id.registrationPlate);
            }
        }

        @Override
        public CarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View view = layoutInflater.inflate(R.layout.layout_car_list_item, parent, false);
            return new CarViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CarViewHolder holder, int position) {
            holder.txtManufacturer.setText(dataList.get(position).getManufacturer());
            holder.txtModel.setText(dataList.get(position).getModel());
            holder.txtRegistrationPlate.setText(dataList.get(position).getRegistrationPlate());
        }

        public void setCars(List<Car> cars) {
            dataList = cars;
            Log.d("123", valueOf(dataList.size()));
        }

        @Override
        public int getItemCount() {

            int a;

            if (dataList != null && !dataList.isEmpty()) {

                a = dataList.size();
            } else {

                a = 0;

            }

            return a;
        }
    }


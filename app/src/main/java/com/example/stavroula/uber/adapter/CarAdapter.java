
package com.example.stavroula.uber.adapter;

import static java.lang.String.valueOf;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.stavroula.uber.R;
import com.example.stavroula.uber.entity.Car;

import java.util.List;


    public class CarAdapter extends RecyclerView.Adapter<CarAdapter.CarViewHolder> {

        private List<Car> dataList;
        private Context context;

        public CarAdapter(List<Car> dataList, Context context) {
            this.context = context;
            this.dataList = dataList;
        }

      public static class CarViewHolder extends RecyclerView.ViewHolder {

            public final View mView;

            TextView txtManufacturer, txtModel , txtRegistrationPlate;


            CarViewHolder(View itemView) {
                super(itemView);
                mView = itemView;

                this.txtManufacturer = (TextView) mView.findViewById(R.id.manufacturer);
                this.txtModel = (TextView) mView.findViewById(R.id.model);
                this.txtRegistrationPlate = (TextView) mView.findViewById(R.id.registrationPlate);
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
            TextView txtManufacturer = holder.txtManufacturer;
            TextView txtModel = holder.txtModel;
            TextView txtRegistrationPlate = holder.txtRegistrationPlate;

            txtManufacturer.setText(dataList.get(position).getManufacturer());
            txtModel.setText(dataList.get(position).getModel());
            txtRegistrationPlate.setText(dataList.get(position).getRegistrationPlate());

        }

        public void setCars(List<Car> cars) {
            dataList = cars;
            Log.d("VALUE", valueOf(dataList.size()));
        }

        @Override
        public int getItemCount() {
            int a;
            if (dataList != null && !dataList.isEmpty()) {

                a = dataList.size();
            } else {

                a = 0;

            }
            Log.d("SIZE", valueOf(dataList.size()));

            return a;

        }
    }


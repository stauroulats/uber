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
import com.example.stavroula.uber.entity.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> dataList;
    private Context context;

    public UserAdapter(Context context, List<User> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    class UserViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        TextView txtId, txtUserName, txtFirstName, txtLastName, txtEmail, txtPassword, txtPhoneNumber, txtAddress;

        UserViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            txtId = mView.findViewById(R.id.id);
            txtUserName = mView.findViewById(R.id.userName);
            txtFirstName = mView.findViewById(R.id.firstName);
            txtLastName = mView.findViewById(R.id.lastName);
            txtEmail = mView.findViewById(R.id.email);
            txtPassword = mView.findViewById(R.id.password);
            txtPhoneNumber = mView.findViewById(R.id.phoneNumber);
            txtAddress = mView.findViewById(R.id.address);
        }
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_row_view, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.txtId.setText(String.valueOf(dataList.get(position).getId()));
        holder.txtUserName.setText(dataList.get(position).getUsername());
        holder.txtFirstName.setText(dataList.get(position).getFirstName());
        holder.txtLastName.setText(dataList.get(position).getLastName());
        holder.txtEmail.setText(dataList.get(position).getEmail());
        holder.txtPassword.setText(dataList.get(position).getPassword());
        holder.txtPhoneNumber.setText(dataList.get(position).getPhoneNumber());
        holder.txtAddress.setText(dataList.get(position).getAddress());
    }

    public void setUserList(List<User> users) {
        dataList = users;
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

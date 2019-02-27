package com.example.stavroula.uber.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stavroula.uber.R;
import com.example.stavroula.uber.entity.CreditCard;

import java.util.List;

import static java.lang.String.valueOf;


public class CreditCardAdapter extends RecyclerView.Adapter<CreditCardAdapter.CreditCardViewHolder> {

    private List<CreditCard> dataList;
    private Context context;

    public CreditCardAdapter(Context context, List<CreditCard> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    class CreditCardViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        TextView txtCardNumber, txtName;

        CreditCardViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            txtCardNumber = mView.findViewById(R.id.card_number);
            txtName = mView.findViewById(R.id.name);
        }
    }

    @Override
    public CreditCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.layout_card_list_item, parent, false);
        return new CreditCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CreditCardViewHolder holder, int position) {
        holder.txtCardNumber.setText(String.valueOf(dataList.get(position).getNumber()));
        holder.txtName.setText(dataList.get(position).getName());
    }

    public void setCreditCardList(List<CreditCard> creditCards) {
        dataList = creditCards;
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

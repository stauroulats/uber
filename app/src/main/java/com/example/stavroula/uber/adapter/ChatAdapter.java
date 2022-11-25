package com.example.stavroula.uber.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.stavroula.uber.R;
import com.example.stavroula.uber.entity.ChatMessage;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Class responsible to show all the messages
 * in the chat
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ChatMessage> dataList;
    private Context context;

    public ChatAdapter(Context context, List<ChatMessage> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    /**
     * ViewHolder to be the item of the list
     */
    static final class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView receiver,sender, time;
        TextView message;

        ChatViewHolder(View view) {
            super(view);

            receiver =  view.findViewById(R.id.item_username);
            message =  view.findViewById(R.id.item_message);
            time = view.findViewById(R.id.item_time);
        }
    }

    public void clearData() {
        dataList.clear();
    }

    public void addData(ChatMessage data) {
        dataList.add(data);
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

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_item, parent, false);
        return new ChatViewHolder(root);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {
        ChatMessage data = dataList.get(position);

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(data.getTime()));
        String time = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();

        holder.message.setText(data.getMessage());
        holder.receiver.setText(data.getReceiver());
        holder.time.setText(time);
    }
}
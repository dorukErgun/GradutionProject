package com.example.myapplication;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MessageHolder> {

    private List<Message> list;

    public RecyclerAdapter(List<Message> list){
        this.list = list;
    }

    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TextView textView;
        System.out.println("Case" + viewType);
        switch (viewType){
            case 0: textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view_layout_sent, parent, false);
            break;
            case 1: textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view_layout_incoming, parent, false);
            break;
            default: textView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view_layout_sent, parent, false);
        }
        MessageHolder mh = new MessageHolder(textView);
        return mh;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).isSent ? 0 : 1;
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        holder.versionName.setText(list.get(position).text);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MessageHolder extends RecyclerView.ViewHolder {

        TextView versionName;
        public MessageHolder(@NonNull TextView itemView) {
            super(itemView);
            versionName = itemView;
        }

    }


}

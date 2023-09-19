package com.example.bluetoothapp.adapter;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bluetoothapp.R;
import com.example.bluetoothapp.model.BlutoothDevice;

import java.util.ArrayList;
import java.util.List;

public class BlutoothDeviceAdapter extends RecyclerView.Adapter<BlutoothDeviceAdapter.BlutoothDeviceViewHolder> {


    private List<BluetoothDevice> dataList;
    private Context context;
    private AdapterOnClickListener<BlutoothDevice> adapterItemTypeOnClickListener;

    public interface AdapterOnClickListener<T> {
        void onItemSelected(T item);
    }

    public BlutoothDeviceAdapter(Context context) {
        this.context = context;
        dataList = new ArrayList<>();

    }

    public void setDataList(List<BluetoothDevice> dataList) {
        this.dataList.clear();
        this.dataList.addAll(dataList);
        notifyDataSetChanged();
    }

    class BlutoothDeviceViewHolder extends RecyclerView.ViewHolder {


        TextView textName;
        TextView textRssi;
        TextView textArea;

        TextView textUUID;

        BlutoothDeviceViewHolder(View itemView) {
            super(itemView);

            textName = itemView.findViewById(R.id.text_name);
            textRssi = itemView.findViewById(R.id.text_rssi);
            textArea = itemView.findViewById(R.id.text_area);
            textUUID = itemView.findViewById(R.id.text_uuid);
        }
    }

    @Override
    public BlutoothDeviceAdapter.BlutoothDeviceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_blutooth_device, parent, false);
        return new BlutoothDeviceAdapter.BlutoothDeviceViewHolder(view);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull BlutoothDeviceViewHolder holder, int position) {

        final BluetoothDevice currentItem = (BluetoothDevice) dataList.get(position);
        if (currentItem.getName()==null){
            holder.textName.setText("Unknown");
        } else {
            holder.textName.setText(currentItem.getName());
        }

        if(currentItem.getUuids()==null){
            holder.textUUID.setText("Unknown");
        }
        else {
            holder.textUUID.setText(currentItem.getUuids().toString());
        }


        holder.textRssi.setText("-82");
        holder.textArea.setText("Rooms");

    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }


}

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
import com.example.bluetoothapp.model.BluetoothDeviceInfo;
import com.example.bluetoothapp.model.BlutoothDevice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BlutoothDeviceAdapter extends RecyclerView.Adapter<BlutoothDeviceAdapter.BlutoothDeviceViewHolder> {


    private List<BluetoothDevice> dataList;
    private Context context;


    private List<BluetoothDeviceInfo> deviceList  = new ArrayList<>();



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


    public void addDevice(BluetoothDevice device, int rssi) {
      //  BluetoothDeviceInfo deviceInfo = new BluetoothDeviceInfo(device, rssi);


        for (BluetoothDeviceInfo existingDevice : deviceList) {
            if (existingDevice.getDevice().getAddress().equals(device.getAddress())) {
                // Update the existing device's RSSI value and notify the change
                existingDevice.setRssi(rssi);
                notifyDataSetChanged();
                return;
            }
        }

        // If the device is not in the list, add it as a new entry
        BluetoothDeviceInfo deviceInfo = new BluetoothDeviceInfo(device, rssi);
        deviceList.add(deviceInfo);
        notifyDataSetChanged();
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

        final BluetoothDeviceInfo currentItem = (BluetoothDeviceInfo) deviceList.get(position);
        if (currentItem.getDevice().getName()==null){
            holder.textName.setText("Unknown");
        } else {
            holder.textName.setText(currentItem.getDevice().getName());
        }

        if(currentItem.getDevice().getUuids()==null){
            holder.textUUID.setText("Unknown");
        }
        else {
            holder.textUUID.setText(Arrays.toString(currentItem.getDevice().getUuids()));
        }


        holder.textRssi.setText(Integer.toString(currentItem.getRssi()));
        holder.textArea.setText("Rooms");

    }


    @Override
    public int getItemCount() {
        return deviceList.size();
    }


}

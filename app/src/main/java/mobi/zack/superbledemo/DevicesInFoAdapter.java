package mobi.zack.superbledemo;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.MessageFormat;
import java.util.List;

public class DevicesInFoAdapter extends RecyclerView.Adapter<DevicesInFoAdapter.DevicesInfoHolder> {

    private List<BluetoothDevice> deviceList;
    private onItemClickListener onItemClickListener;
    int position;

    DevicesInFoAdapter(List<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
    }

    public void setDeviceInfos(List<BluetoothDevice> deviceList) {
        this.deviceList = deviceList;
    }

    public interface onItemClickListener {

        void onItemClick(View item, int position);

    }

    void setOnItemClickListener(DevicesInFoAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public DevicesInfoHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        DevicesInfoHolder holder = new DevicesInfoHolder(View.inflate(viewGroup.getContext(),
                R.layout.device_info_layout, null));
        position = holder.getAdapterPosition();
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull DevicesInfoHolder devicesInfoHolder, int i) {
        Log.d(DevicesInfoHolder.class.getSimpleName(), deviceList.get(i).getAddress() +" "+ i);

        if (TextUtils.isEmpty(deviceList.get(i).getName())) {
            devicesInfoHolder.deviceInfo.setText(MessageFormat.format("{0}{1}{2}",
                    deviceList.get(i).getAddress(), " * ", deviceList.get(i).getType()));
        }else {
            devicesInfoHolder.deviceInfo.setText(MessageFormat.format("{0}{1}{2}{3}{4}",
                    deviceList.get(i).getAddress(), " * ", deviceList.get(i).getName(),
                    " * ", deviceList.get(i).getType()));

        }

    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }


    public class DevicesInfoHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Button deviceInfo;

        DevicesInfoHolder(@NonNull View itemView) {
            super(itemView);
            deviceInfo = itemView.findViewById(R.id.deviceInfo);
            deviceInfo.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

}

package mobi.zack.superblelib.model;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mobi.zack.superblelib.exception.SuperBleScanException;

public abstract class SuperBleScanCallBackImp implements BluetoothAdapter.LeScanCallback {

    private final String tag = SuperBleScanCallBackImp.class.getSimpleName();

    public abstract void onScanning(BluetoothDevice device);
    public abstract void onScanFinished(List<BluetoothDevice> devices);
    public abstract void onScanFail(SuperBleScanException e);

    private ScanCallback scanCallback;

    protected SuperBleScanCallBackImp() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            scanCallback = new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    Log.d(tag,"扫描结果："+result.getDevice().getAddress());
                    onScanning(result.getDevice());
                }

                @Override
                public void onBatchScanResults(List<ScanResult> results) {
                    Log.d(tag,"onBatchScanResults");

                    List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
                    for (ScanResult result: results) {
                        bluetoothDevices.add(result.getDevice());
                    }
                    onScanFinished(bluetoothDevices);
                }

                @Override
                public void onScanFailed(int errorCode) {
                    Log.d(tag,"onScanFailed");
                    onScanFail(new SuperBleScanException());
                }
            };
        }

    }

    public ScanCallback getScanCallback() {
        return scanCallback;
    }

    @Override
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        onScanning(device);
    }


}

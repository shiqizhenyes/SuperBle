package mobi.zack.superblelib.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattService;

import mobi.zack.superblelib.inter.SuperBleGatt;

public abstract class SuperBleServerCallBack {

    public abstract void onServerSuccess(BluetoothGatt gatt,
                                         BluetoothGattService bluetoothGattService,
                                         SuperBleGatt superBleGatt);
    public abstract void onServerFail(BluetoothGatt gatt, SuperBleGatt superBleGatt);

}

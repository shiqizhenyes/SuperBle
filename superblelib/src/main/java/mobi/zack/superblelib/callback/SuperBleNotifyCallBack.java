package mobi.zack.superblelib.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import mobi.zack.superblelib.inter.SuperBleGatt;

public abstract class SuperBleNotifyCallBack {

    public abstract void onNotifySuccess(BluetoothGatt gatt, BluetoothGattCharacteristic notifyCharacteristic, SuperBleGatt superBleGatt);
    public abstract void onNotifyFail(BluetoothGatt gatt, SuperBleGatt superBleGatt);

}

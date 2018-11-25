package mobi.zack.superblelib.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;

import mobi.zack.superblelib.inter.SuperBleGatt;

public abstract class SuperBleDescribeCallBack {

    public abstract void onSuccess(BluetoothGatt gatt, SuperBleGatt superBleGatt, BluetoothGattDescriptor descriptor);
    public abstract void onFail(BluetoothGatt gatt, SuperBleGatt superBleGatt);

}

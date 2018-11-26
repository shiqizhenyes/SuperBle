package mobi.zack.superblelib.callback;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

public abstract class SuperBleWriteCallBack {

    public abstract void onSuccess(BluetoothGattCharacteristic characteristic);
    public abstract void onFail(BluetoothGattCharacteristic characteristic);

}

package mobi.zack.superblelib.inter;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

import mobi.zack.superblelib.callback.SuperBleDescribeCallBack;
import mobi.zack.superblelib.callback.SuperBleNotifyCallBack;
import mobi.zack.superblelib.callback.SuperBleServerCallBack;
import mobi.zack.superblelib.callback.SuperBleWriteCallBack;

public interface SuperBleGatt {

    void discoverService(BluetoothGatt gatt, UUID serverUUID,
                         SuperBleServerCallBack superBleServerCallBack);

    void notify(BluetoothGattService bluetoothGattService, UUID notifyUUID,
                SuperBleNotifyCallBack superBleNotifyCallBack);

    void notify(BluetoothGatt gatt, UUID notifyUUID,
                SuperBleNotifyCallBack superBleNotifyCallBack);

    void writeDescriptor(BluetoothGatt gatt, UUID descriptorUUID,
                         BluetoothGattCharacteristic notifyCharacteristic,
                         SuperBleDescribeCallBack superBleDescribeCallBack);

    void write(final UUID writeUUID, final byte[] bytes, SuperBleWriteCallBack superBleWriteCallBack);


    public void setServerUUID(UUID serverUUID);
    public void setNotifyUUID(UUID notifyUUID);

}

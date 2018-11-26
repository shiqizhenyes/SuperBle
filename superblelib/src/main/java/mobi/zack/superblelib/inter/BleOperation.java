package mobi.zack.superblelib.inter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

import mobi.zack.superblelib.callback.SuperBleDescribeCallBack;
import mobi.zack.superblelib.callback.SuperBleGattCallBack;
import mobi.zack.superblelib.callback.SuperBleNotifyCallBack;
import mobi.zack.superblelib.callback.SuperBleOpenCallBack;
import mobi.zack.superblelib.callback.SuperBleScanCallBack;
import mobi.zack.superblelib.callback.SuperBleServerCallBack;
import mobi.zack.superblelib.callback.SuperBleWriteCallBack;

public interface BleOperation {

    public void openBluetoothAdapter(SuperBleOpenCallBack superBleOpenCallBack);

    public void startScan(SuperBleScanCallBack scanCallBack);
    public void stopScan();
    public void cancelScan();

    public void connect(BluetoothDevice device, SuperBleGattCallBack superBleGattCallBack);
    public void connect(String mac, SuperBleGattCallBack superBleGattCallBack);

    public void initService(BluetoothGatt gatt, UUID serverUUID, SuperBleGatt superBleGatt,
                            SuperBleServerCallBack superBleServerCallBack);

    public void initNotification(BluetoothGattService bluetoothGattService,
                                 UUID notifyUUID, SuperBleGatt superBleGatt,
                                 SuperBleNotifyCallBack superBleNotifyCallBack);

    public void initNotification(BluetoothGatt gatt, UUID notifyUUID,
                                 SuperBleGatt superBleGatt,
                                 SuperBleNotifyCallBack superBleNotifyCallBack);

    public void initDescriptor(BluetoothGatt gatt, UUID descriptorUUID,
                               SuperBleGatt superBleGatt, BluetoothGattCharacteristic notifyCharacteristic,
                               SuperBleDescribeCallBack superBleDescribeCallBack);

    public void initDescriptor(BluetoothGatt gatt,
                               SuperBleGatt superBleGatt, BluetoothGattCharacteristic notifyCharacteristic,
                               SuperBleDescribeCallBack superBleDescribeCallBack);

//    public void initDescriptor();

    public void disconnect(BluetoothDevice bluetoothDevice);
    public void disconnect(String mac);
    public void disconnectAll();

    public void write(UUID writeUUID, byte[] data, SuperBleGatt superBleGatt, SuperBleWriteCallBack writeCallBack);


}

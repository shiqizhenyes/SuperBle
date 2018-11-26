package mobi.zack.superblelib.callback;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Switch;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.LogRecord;

import mobi.zack.superblelib.constant.C;
import mobi.zack.superblelib.entity.ReceiveData;
import mobi.zack.superblelib.entity.WriteData;
import mobi.zack.superblelib.exception.SuperBleConnectException;
import mobi.zack.superblelib.inter.SuperBleGatt;


public abstract class SuperBleGattCallBack extends BluetoothGattCallback implements SuperBleGatt {

    private static final String tag = SuperBleGattCallBack.class.getSimpleName();

    public abstract void onStartConnect(BluetoothDevice device);

    public abstract void onConnecting(BluetoothDevice device);

    public abstract void onConnectFail(SuperBleConnectException exception);

    public abstract void onConnectFail(String mac, SuperBleConnectException exception);

    public abstract void onConnectFail(BluetoothDevice device, SuperBleConnectException exception);

    public abstract void onConnectSuccess(BluetoothDevice device, BluetoothGatt gatt);

    public abstract void onDisConnecting(boolean isActiveDisConnected, BluetoothDevice device, BluetoothGatt gatt);

    public abstract void onDisConnected(boolean isActiveDisConnected, BluetoothDevice device, BluetoothGatt gatt);

    private SuperBleServerCallBack superBleServerCallBack;
    private SuperBleNotifyCallBack superBleNotifyCallBack;
//    private SuperBleWriteCallBack superBleWriteCallBack;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService bluetoothGattService;

    private UUID serverUUID;
    private UUID notifyUUID;
    private ExecutorService executorService;

    private void initThreadPool() {
        executorService = Executors.newSingleThreadExecutor();
    }

    @Override
    public void setServerUUID(UUID serverUUID) {
        this.serverUUID = serverUUID;
    }

    @Override
    public void setNotifyUUID(UUID notifyUUID) {
        this.notifyUUID = notifyUUID;
    }


    private class SuperBleHandler extends Handler {
        Looper looper;
        SuperBleHandler(Looper looper) {
            super(looper);
            this.looper = looper;
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == C.WRITE) {
                Log.d(tag, "写入接收数据" + String.valueOf( superBleNotifyCallBack == null));
                ReceiveData receiveData = (ReceiveData) msg.obj;
                if (receiveData != null) {
                    if (superBleNotifyCallBack != null) {
                        superBleNotifyCallBack
                                .onCharacteristicChangedOnUiThread(receiveData.getBluetoothGatt(),
                                receiveData.getCharacteristic());
                    }
                }
            }

        }
    }

    private Handler mainHandler = new SuperBleHandler(Looper.getMainLooper());

    private void sendMessageToMain(final Message msg) {
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if (msg.what == C.BLUETOOTH_STATUS) {
                    BluetoothGatt gatt = (BluetoothGatt) msg.obj;
                    switch(msg.arg1) {
                        case BluetoothProfile.STATE_CONNECTING:
                            onConnecting(gatt.getDevice());
                            break;
                        case BluetoothProfile.STATE_CONNECTED:
                            onConnectSuccess(gatt.getDevice(), gatt);
                            break;
                        case BluetoothProfile.STATE_DISCONNECTING:
                            onDisConnecting(false, gatt.getDevice(), gatt);
                            break;
                        case BluetoothProfile.STATE_DISCONNECTED:
                            onDisConnected(true, gatt.getDevice(), gatt);
                            break;
                    }
                }
            }
        });

    }

    @Override
    public void discoverService(BluetoothGatt gatt, UUID serverUUID, SuperBleServerCallBack superBleServerCallBack) {
        this.serverUUID = serverUUID;
        this.superBleServerCallBack = superBleServerCallBack;
        gatt.discoverServices();
    }


    @Override
    public void notify(BluetoothGatt gatt, UUID notifyUUID, SuperBleNotifyCallBack superBleNotifyCallBack) {
        this.notifyUUID = notifyUUID;
        this.superBleNotifyCallBack = superBleNotifyCallBack;
        bluetoothGattService = gatt.getService(this.serverUUID);
        BluetoothGattCharacteristic notifyCharacteristic = bluetoothGattService.getCharacteristic(notifyUUID);
        boolean isNotify = gatt.setCharacteristicNotification(notifyCharacteristic, true);
        if (isNotify) {
            this.superBleNotifyCallBack.onNotifySuccess(gatt, notifyCharacteristic,this);
            initThreadPool();
        }else {
            this.superBleNotifyCallBack.onNotifyFail(gatt, this);
        }
    }

    @Override
    public void notify(BluetoothGattService bluetoothGattService, UUID notifyUUID, SuperBleNotifyCallBack superBleNotifyCallBack) {
        this.notifyUUID = notifyUUID;
        this.bluetoothGattService = bluetoothGattService;
        BluetoothGattCharacteristic notifyCharacteristic = bluetoothGattService.getCharacteristic(this.notifyUUID);
        if (bluetoothGatt != null) {
            boolean isNotify = bluetoothGatt.setCharacteristicNotification(notifyCharacteristic, true);
            if (isNotify) {
                this.superBleNotifyCallBack.onNotifySuccess(bluetoothGatt, notifyCharacteristic ,this);
                initThreadPool();
            }else {
                this.superBleNotifyCallBack.onNotifyFail(bluetoothGatt, this);
            }
        }

    }


    @Override
    public void writeDescriptor(BluetoothGatt gatt, UUID descriptorUUID,
                                BluetoothGattCharacteristic notifyCharacteristic,
                                SuperBleDescribeCallBack superBleDescribeCallBack) {

        if (notifyCharacteristic != null) {
            BluetoothGattDescriptor descriptor = notifyCharacteristic.getDescriptor(descriptorUUID);
            if (descriptor != null) {
                boolean isEnable = descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                if (isEnable) {
                    superBleDescribeCallBack.onSuccess(gatt, this, descriptor);
                    gatt.writeDescriptor(descriptor);
                }else {
                    superBleDescribeCallBack.onFail(gatt, this);
                }
            }else {
                superBleDescribeCallBack.onFail(gatt, this);
            }
        }else {
            superBleDescribeCallBack.onFail(gatt, this);
        }


    }

    @Override
    public void write(final UUID writeUUID, final byte[] bytes, final SuperBleWriteCallBack superBleWriteCallBack) {
//        this.superBleWriteCallBack = superBleWriteCallBack;
        Runnable writeRunnable = new Runnable() {
            @Override
            public void run() {
                BluetoothGattCharacteristic writeCharacteristic = bluetoothGattService.getCharacteristic(writeUUID);
                if (writeCharacteristic != null) {
                    writeCharacteristic.setValue(bytes);
                    if (bluetoothGatt != null) {
                        boolean isWrite = bluetoothGatt.writeCharacteristic(writeCharacteristic);
                        if (isWrite) {
                            superBleWriteCallBack.onSuccess(writeCharacteristic);
                        }else {
                            superBleWriteCallBack.onFail(writeCharacteristic);
                        }
                    }
                }
            }
        };
        new Thread(writeRunnable).start();
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);
        Log.d(tag,"连接回调");
        Message message = new Message();
        message.what = C.BLUETOOTH_STATUS;
        message.obj = gatt;
        message.arg1 = newState;
        sendMessageToMain(message);
        if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            gatt.close();
        }
    }


    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);
        if (serverUUID != null) {
            bluetoothGatt = gatt;
            BluetoothGattService bluetoothGattService = gatt.getService(serverUUID);
            superBleServerCallBack.onServerSuccess(gatt, bluetoothGattService, this);
        }else {
            superBleServerCallBack.onServerFail(gatt, this);
        }
    }


    @Override
    public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
        super.onDescriptorWrite(gatt, descriptor, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            this.bluetoothGatt = gatt;
        }
    }


    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
        if (status == BluetoothGatt.GATT_SUCCESS) {
            this.bluetoothGatt = gatt;
        }
    }

    @Override
    public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
        super.onCharacteristicChanged(gatt, characteristic);
        Log.d(tag,"接收数据："+ Arrays.toString(characteristic.getValue())+ Thread.currentThread().getName());
        Log.d(tag, "消息 " + String.valueOf(mainHandler == null));
        if (this.superBleNotifyCallBack != null) {
            this.superBleNotifyCallBack.onCharacteristicChanged(gatt, characteristic);
        }
        Message message = new Message();
        message.what = C.WRITE;
        ReceiveData receiveData = new ReceiveData();
        receiveData.setBluetoothGatt(gatt);
        receiveData.setCharacteristic(characteristic);
        message.obj = receiveData;
        mainHandler.sendMessage(message);
    }
}

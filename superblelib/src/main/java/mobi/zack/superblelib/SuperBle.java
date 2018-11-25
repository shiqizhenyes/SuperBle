package mobi.zack.superblelib;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;
import java.util.EventListener;
import java.util.List;
import java.util.UUID;

import mobi.zack.superblelib.callback.SuperBleDescribeCallBack;
import mobi.zack.superblelib.callback.SuperBleGattCallBack;
import mobi.zack.superblelib.callback.SuperBleNotifyCallBack;
import mobi.zack.superblelib.callback.SuperBleOpenCallBack;
import mobi.zack.superblelib.callback.SuperBleScanCallBack;
import mobi.zack.superblelib.callback.SuperBleServerCallBack;
import mobi.zack.superblelib.callback.SuperBleWriteCallBack;
import mobi.zack.superblelib.config.ConnectConfig;
import mobi.zack.superblelib.constant.C;
import mobi.zack.superblelib.entity.Result;
import mobi.zack.superblelib.entity.WriteData;
import mobi.zack.superblelib.exception.SuperBleConnectException;
import mobi.zack.superblelib.exception.SuperBleException;
import mobi.zack.superblelib.exception.SuperBleScanException;
import mobi.zack.superblelib.inter.BleOperation;
import mobi.zack.superblelib.inter.BleScan;
import mobi.zack.superblelib.inter.SuperBleGatt;
import mobi.zack.superblelib.model.SuperBleScanCallBackImp;

public class SuperBle implements BleOperation {


    private BluetoothAdapter bluetoothAdapter;
    private BluetoothManager bluetoothManager;
    private BluetoothGatt bluetoothGatt;

    private int connectDelay = C.CONNECT_DELAY;
    private int connectOverTime = C.CONNECT_OVERTIME;
    private int reconnectCount = C.CONNECT_COUNT;
    private int operationOverTime = C.OPERATION_OVERTIME;
    private Context context;
    private BleOperation bleOperation;
    private boolean isBluetoothOpen;
    private boolean isSupportBLE;
    private boolean isSupportedDevice;
    private boolean isScanning = false;
    private boolean autoConnect = false;
    private SuperBleScanCallBackImp scanCallBackImp;

    private ConnectConfig connectConfig;
    private ConnectConfig.ConnectConfigBuilder configBuilder = new ConnectConfig.ConnectConfigBuilder();

    private static class SuperBleHolder {

        private static SuperBle superBle = new SuperBle();

    }

    /**
     * 获取实例
     * @return SuperBle
     */
    public static SuperBle getInstance() {
        return SuperBleHolder.superBle;
    }

    /**
     * 设置连接延时（默认不延时）
     * @param connectDelay 延时
     * @return SuperBle
     */
    public SuperBle setConnectDelay(int connectDelay) {
        this.connectDelay = connectDelay;
        if (connectDelay < 0) {
            this.connectDelay = C.CONNECT_DELAY;
        }
        return this;
    }

    /**
     * 设置连接超时（默认2秒）
     * @param connectOverTime 超时
     * @return SuperBle
     */
    public SuperBle setConnectOverTime(int connectOverTime) {
        this.connectOverTime = connectOverTime;
        if (connectOverTime < 0) {
            this.connectOverTime = C.CONNECT_OVERTIME;
        }
        return this;
    }

    /**
     * 设置重连次数（默认重连3次）
     * @param reconnectCount 重连次数
     * @return SuperBle
     */
    public SuperBle setReconnectCount(int reconnectCount) {
        this.reconnectCount = reconnectCount;
        if (reconnectCount < 0) {
            this.reconnectCount = C.CONNECT_COUNT;
        }
        return this;
    }

    /**
     * 设置操作超时（默认3秒）
     * @param operationOverTime 操作超时
     * @return SuperBle
     */
    public SuperBle setOperationOverTime(int operationOverTime) {
        this.operationOverTime = operationOverTime;
        return this;
    }


    public SuperBle setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
        configBuilder.autoConnect(this.autoConnect);
        return this;
    }

    /**
     * 设置上下文
     * @param context context
     * @return SuperBle
     */
    public SuperBle setContext(Context context) throws SuperBleException {
        this.context = context;
        bleOperation = this;
        connectConfig = configBuilder.build();
        checkSystemSupport();
        if (isSupportBLE) {
            bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
            if (bluetoothAdapter == null) {
                throw new SuperBleException();
            }else {
                isBluetoothOpen = bluetoothAdapter.isEnabled();
            }
        }else {
            throw new SuperBleException();
        }
        return this;
    }

    private void checkSystemSupport() {

        isSupportBLE = Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2
                && context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);

    }


    @Override
    public void openBluetoothAdapter(SuperBleOpenCallBack superBleOpenCallBack) {

        if (isSupportBLE) {
            if (bluetoothAdapter != null) {
                if (!bluetoothAdapter.isEnabled()) {
                    isBluetoothOpen = bluetoothAdapter.enable();
                    if (isBluetoothOpen) {
                        superBleOpenCallBack.onSuccess();
                    }else {
                        superBleOpenCallBack.onFail();
                    }
                }
            }else {
                if (bluetoothManager!= null) {
                    bluetoothAdapter = bluetoothManager.getAdapter();
                    if (bluetoothAdapter != null) {
                        isBluetoothOpen = bluetoothAdapter.enable();
                        if (isBluetoothOpen) {
                            superBleOpenCallBack.onSuccess();
                        }else {
                            superBleOpenCallBack.onFail();
                        }
                    }
                }
            }
        } else {
            superBleOpenCallBack.onFail();
        }

    }

    private void scanDevice(final SuperBleScanCallBack scanCallBack) {

        scanCallBackImp = new SuperBleScanCallBackImp() {

            @Override
            public void onScanning(BluetoothDevice device) {
                scanCallBack.getBleScan().onScanning(device);
            }

            @Override
            public void onScanFinished(List<BluetoothDevice> devices) {
                scanCallBack.getBleScan().onScanFinished(devices);
            }

            @Override
            public void onScanFail(SuperBleScanException e) {
                scanCallBack.getBleScan().onScanFail(e);
            }

        };
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothLeScanner != null) {
                if (scanCallBackImp.getScanCallback() != null) {
                    bluetoothLeScanner.startScan(scanCallBackImp.getScanCallback());
                }else {
                    scanCallBack.getBleScan().onScanFail(new SuperBleScanException());
                }
            }else {
                scanCallBack.getBleScan().onScanFail(new SuperBleScanException());
            }
            scanCallBack.getBleScan().onScanStarted(true);
        }else {
            bluetoothAdapter.startLeScan(scanCallBackImp);
            scanCallBack.getBleScan().onScanStarted(true);
        }

    }


    @Override
    public void startScan(SuperBleScanCallBack scanCallBack) {

        if (bluetoothAdapter != null) {
            if (isScanning) {
                stopScan();
            }
            isBluetoothOpen = bluetoothAdapter.isEnabled();
            if (isBluetoothOpen) {
                isScanning = true;
                scanDevice(scanCallBack);
            }else {
                scanCallBack.getBleScan().onScanFail(new SuperBleScanException());
            }
        }else {
            scanCallBack.onScanFail(new SuperBleScanException());
        }

    }

    @Override
    public void stopScan() {
        if (isScanning) {
            if (bluetoothAdapter != null) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                    if (scanCallBackImp.getScanCallback() != null) {
                        bluetoothAdapter.getBluetoothLeScanner()
                                .stopScan(scanCallBackImp.getScanCallback());
                    }
                }else {
                    bluetoothAdapter.stopLeScan(scanCallBackImp);
                }
                isScanning = false;
            }
        }
    }

    @Override
    public void cancelScan() {
        if (isScanning) {
            stopScan();
            scanCallBackImp = null;
        }
    }

    @Override
    public void connect(BluetoothDevice device, SuperBleGattCallBack superBleGattCallBack) {
        if (isScanning) {
            stopScan();
        }
        if (bluetoothAdapter != null) {
            isBluetoothOpen = bluetoothAdapter.isEnabled();
            if (isBluetoothOpen) {
                superBleGattCallBack.onStartConnect(device);
                if (device != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        bluetoothGatt = device.connectGatt(context,
                                connectConfig.isAutoConnect(),
                                superBleGattCallBack,
                                connectConfig.getTransport(), connectConfig.getPhy(),
                                connectConfig.getHandler());
//                        bluetoothGatt.connect();
                    }else {
                        bluetoothGatt = device.connectGatt(context, connectConfig.isAutoConnect(), superBleGattCallBack);
//                        bluetoothGatt.connect();
                    }
                }else {
//                    superBleGattCallBack.onConnectFail(device, new SuperBleConnectException());
                }
            }else {
                superBleGattCallBack.onConnectFail(device, new SuperBleConnectException());
            }
        }else {
            superBleGattCallBack.onConnectFail(device, new SuperBleConnectException());
        }

    }

    @Override
    public void connect(String mac, SuperBleGattCallBack superBleGattCallBack) {

        if (isScanning) {
            stopScan();
        }
        if (bluetoothAdapter != null) {
            isBluetoothOpen = bluetoothAdapter.isEnabled();
            if (isBluetoothOpen) {
//                superBleGattCallBack.onStartConnect(device);
//                if (device != null) {
//
//                }
            }else {
//                superBleGattCallBack.onConnectFail(device, new SuperBleConnectException());
            }
        }else {
//            superBleGattCallBack.onConnectFail(device, new SuperBleConnectException());
        }

    }

    @Override
    public void initService(BluetoothGatt gatt, UUID serverUUID, SuperBleGatt superBleGatt, SuperBleServerCallBack superBleServerCallBack) {
        superBleGatt.discoverService(gatt, serverUUID, superBleServerCallBack);
    }

    @Override
    public void initNotification(BluetoothGattService bluetoothGattService, UUID notifyUUID, SuperBleGatt superBleGatt, SuperBleNotifyCallBack superBleNotifyCallBack) {

        superBleGatt.notify(bluetoothGattService, notifyUUID, superBleNotifyCallBack);

    }


    @Override
    public void initNotification(BluetoothGatt gatt, UUID notifyUUID, SuperBleGatt superBleGatt, SuperBleNotifyCallBack superBleNotifyCallBack) {

        superBleGatt.notify(gatt, notifyUUID, superBleNotifyCallBack);

    }

    @Override
    public void initDescriptor(BluetoothGatt gatt, UUID descriptorUUID, SuperBleGatt superBleGatt, BluetoothGattCharacteristic notifyCharacteristic, SuperBleDescribeCallBack superBleDescribeCallBack) {

        superBleGatt.writeDescriptor(gatt, descriptorUUID, notifyCharacteristic,superBleDescribeCallBack);

    }

    @Override
    public void initDescriptor(BluetoothGatt gatt, SuperBleGatt superBleGatt, BluetoothGattCharacteristic notifyCharacteristic, SuperBleDescribeCallBack superBleDescribeCallBack) {

        superBleGatt.writeDescriptor(gatt, C.UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR,
                notifyCharacteristic, superBleDescribeCallBack);

    }


    @Override
    public void disconnect(BluetoothDevice bluetoothDevice) {

    }

    @Override
    public void disconnect(String mac) {

    }

    @Override
    public void disconnectALl() {

    }

    @Override
    public void write(UUID writeUUID, byte[] data, SuperBleGatt superBleGatt,SuperBleWriteCallBack writeCallBack) {

        superBleGatt.write(writeUUID, data, writeCallBack);

    }



}

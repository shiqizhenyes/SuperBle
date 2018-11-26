package mobi.zack.superbledemo;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import mobi.zack.superblelib.SuperBle;
import mobi.zack.superblelib.callback.SuperBleDescribeCallBack;
import mobi.zack.superblelib.callback.SuperBleGattCallBack;
import mobi.zack.superblelib.callback.SuperBleNotifyCallBack;
import mobi.zack.superblelib.callback.SuperBleOpenCallBack;
import mobi.zack.superblelib.callback.SuperBleScanCallBack;
import mobi.zack.superblelib.callback.SuperBleServerCallBack;
import mobi.zack.superblelib.callback.SuperBleWriteCallBack;
import mobi.zack.superblelib.exception.SuperBleConnectException;
import mobi.zack.superblelib.exception.SuperBleScanException;
import mobi.zack.superblelib.inter.SuperBleGatt;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    SuperBle superBle;
    private final String tag = MainActivity.class.getSimpleName();
    private Button mMOpen;
    private Button mMSend;
    private RecyclerView mDeviceRecyclerView;
    private Button mMScan;
    private Button mDisconnect;
    private EditText mMSendData;
    private TextView mConnectStatus;

    private DevicesInFoAdapter adapter;
    private List<BluetoothDevice> deviceList = new ArrayList<>();
    private SuperBleGatt superBleGatt;


    public void setSuperBleGatt(SuperBleGatt superBleGatt) {
        this.superBleGatt = superBleGatt;
    }

    void initRecycleView() {
        if (adapter == null) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            mDeviceRecyclerView.setLayoutManager(linearLayoutManager);
            adapter = new DevicesInFoAdapter(deviceList);
            adapter.setOnItemClickListener((item, position) -> {
                Log.d(MainActivity.class.getSimpleName(), "点击了：" + deviceList.get(position).toString());
                superBle.connect(deviceList.get(position), new SuperBleGattCallBack() {
                    @Override
                    public void onStartConnect(BluetoothDevice device) {
                        Log.d(tag,"开始连接" );
                        mConnectStatus.setText("开始连接");

                    }

                    @Override
                    public void onConnecting(BluetoothDevice device) {
                        Log.d(tag,"连接中" );

                        mConnectStatus.setText(R.string.connecting);
                    }

                    @Override
                    public void onConnectFail(SuperBleConnectException exception) {

                    }

                    @Override
                    public void onConnectFail(String mac, SuperBleConnectException exception) {

                    }

                    @Override
                    public void onConnectFail(BluetoothDevice device, SuperBleConnectException exception) {
                        Log.d(tag,"连接失败" );

                        mConnectStatus.setText("连接失败");
                    }

                    @Override
                    public void onConnectSuccess(BluetoothDevice device, BluetoothGatt gatt) {
                        Log.d(tag,"连接成功" );
                        startService(gatt,this);
                        mConnectStatus.setText("连接成功");
                    }

                    @Override
                    public void onDisConnecting(boolean isActiveDisConnected, BluetoothDevice device, BluetoothGatt gatt) {
                        Log.d(tag,"正在断开" );

                        mConnectStatus.setText("正在断开");

                    }

                    @Override
                    public void onDisConnected(boolean isActiveDisConnected, BluetoothDevice device, BluetoothGatt gatt) {
                        Log.d(tag,"已断开" );

                        mConnectStatus.setText("已断开");

                    }
                });
            });
            mDeviceRecyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }else {
            adapter.setDeviceInfos(deviceList);
            adapter.notifyDataSetChanged();
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        superBle = SuperBle.getInstance();
        mConnectStatus = findViewById(R.id.mConnectStatus);
        mMOpen = findViewById(R.id.mOpen);
        mMSend = findViewById(R.id.mSend);
        mDisconnect = findViewById(R.id.mDisconnect);
        mDeviceRecyclerView = findViewById(R.id.mDeviceRecyclerView);
        mMScan = findViewById(R.id.mScan);
        mMSendData = findViewById(R.id.mSendData);
        mMScan.setOnClickListener(this);
        mMOpen.setOnClickListener(this);
        mMSend.setOnClickListener(this);
        mDisconnect.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mOpen:
                superBle.openBluetoothAdapter(new SuperBleOpenCallBack() {
                    @Override
                    public void onSuccess() {
                        Log.d(tag,"开启成功");
                    }

                    @Override
                    public void onFail() {
                        Log.d(tag,"开启失败");
                    }

                    @Override
                    public void onError() {
                        Log.d(tag,"开启错误");
                    }
                });
                break;
            case R.id.mScan:

                superBle.startScan(new SuperBleScanCallBack() {

                    @Override
                    public void onScanStarted(boolean success) {
                        Log.d(tag,"开始扫描"+success);
                    }

                    @Override
                    public void onScanning(BluetoothDevice bluetoothDevice) {

                        if (bluetoothDevice != null) {
                            deviceList.add(bluetoothDevice);
                            initRecycleView();
                        }

                        Log.d(tag,"扫描中："+ bluetoothDevice.getAddress());
                    }

                    @Override
                    public void onScanFinished(List<BluetoothDevice> devices) {
                        Log.d(tag,"扫描完成"+devices.size());

                    }

                    @Override
                    public void onScanFail(SuperBleScanException e) {
                        Log.d(tag,"扫描失败");
                    }

                });
                break;

            case R.id.mSend:
                if (!TextUtils.isEmpty(mMSendData.getText().toString().trim())) {
                    String data = mMSendData.getText().toString();
                    UUID writeUUID = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
                    superBle.write(writeUUID, data.getBytes(), superBleGatt, new SuperBleWriteCallBack() {

                        @Override
                        public void onSuccess(BluetoothGattCharacteristic characteristic) {

                        }

                        @Override
                        public void onFail(BluetoothGattCharacteristic characteristic) {

                        }

                    });
                }

                break;
            case R.id.mDisconnect:

                superBle.disconnectALl();

                break;
        }
    }


    void startService(BluetoothGatt gatt, SuperBleGatt SuperBleGatt) {
        if (superBle != null) {
            UUID serverUUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
            superBle.initService(gatt, serverUUID, SuperBleGatt,new SuperBleServerCallBack() {

                @Override
                public void onServerSuccess(BluetoothGatt gatt, BluetoothGattService bluetoothGattService, SuperBleGatt superBleGatt) {
                    startNotification(gatt, superBleGatt);
                }

                @Override
                public void onServerFail(BluetoothGatt gatt, mobi.zack.superblelib.inter.SuperBleGatt superBleGatt) {

                }
            });
        }
    }

    void startNotification(BluetoothGatt gatt, SuperBleGatt superBleGatt){
        UUID notifyUUID = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");
        superBle.initNotification(gatt, notifyUUID, superBleGatt, new SuperBleNotifyCallBack() {

            @Override
            public void onNotifySuccess(BluetoothGatt gatt, BluetoothGattCharacteristic notifyCharacteristic, SuperBleGatt superBleGatt) {
                Log.d(tag,"订阅成功");
                startDescribe(gatt, superBleGatt, notifyCharacteristic);
            }

            @Override
            public void onNotifyFail(BluetoothGatt gatt, SuperBleGatt superBleGatt) {
                Log.d(tag,"订阅失败");
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                byte[] data = characteristic.getValue();
                try {
                    String s = new String(data, "utf-8");
                    Log.d(tag, "设备返回数据："+ s);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCharacteristicChangedOnUiThread(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                byte[] data = characteristic.getValue();
                try {
                    String s = new String(data, "utf-8");
                    Log.d(tag, "设备返回数据主线程："+ s);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    void startDescribe(BluetoothGatt gatt, SuperBleGatt superBleGatt,BluetoothGattCharacteristic characteristic) {

        superBle.initDescriptor(gatt, superBleGatt , characteristic, new SuperBleDescribeCallBack() {
            @Override
            public void onSuccess(BluetoothGatt gatt, SuperBleGatt superBleGatt, BluetoothGattDescriptor descriptor) {
                setSuperBleGatt(superBleGatt);
                Log.d(tag,"写入descriptor成功");

            }

            @Override
            public void onFail(BluetoothGatt gatt, SuperBleGatt superBleGatt) {
                Log.d(tag,"写入descriptor失败");
            }
        });
    }


}

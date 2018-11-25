package mobi.zack.superblelib.inter;

import android.bluetooth.BluetoothDevice;

import java.util.List;

import mobi.zack.superblelib.entity.Result;
import mobi.zack.superblelib.exception.SuperBleScanException;

public interface BleScan {

    void onScanStarted(boolean success);
    void onScanning(BluetoothDevice bluetoothDevice);
    void onScanFinished(List<BluetoothDevice> devices);
    void onScanFail(SuperBleScanException e);

}

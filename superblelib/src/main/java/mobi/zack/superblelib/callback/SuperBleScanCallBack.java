package mobi.zack.superblelib.callback;

import android.bluetooth.BluetoothAdapter;

import mobi.zack.superblelib.inter.BleScan;

public abstract class SuperBleScanCallBack implements BleScan {

    private BleScan bleScan;

    protected SuperBleScanCallBack() {
        bleScan = this;
    }

    public BleScan getBleScan() {
        return bleScan;
    }

}

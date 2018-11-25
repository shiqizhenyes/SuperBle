package mobi.zack.superblelib.entity;

import java.util.UUID;

import mobi.zack.superblelib.callback.SuperBleWriteCallBack;

public class WriteData {

    private UUID writeUUID;
    private byte[] data;
    private SuperBleWriteCallBack superBleWriteCallBack;

    public UUID getWriteUUID() {
        return writeUUID;
    }

    public void setWriteUUID(UUID writeUUID) {
        this.writeUUID = writeUUID;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public SuperBleWriteCallBack getSuperBleWriteCallBack() {
        return superBleWriteCallBack;
    }

    public void setSuperBleWriteCallBack(SuperBleWriteCallBack superBleWriteCallBack) {
        this.superBleWriteCallBack = superBleWriteCallBack;
    }

}

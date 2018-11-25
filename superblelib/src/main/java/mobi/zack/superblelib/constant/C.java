package mobi.zack.superblelib.constant;

import java.util.UUID;

public class C {

    public static final int CONNECT_DELAY = 0;
    public static final int CONNECT_OVERTIME = 2000;
    public static final int CONNECT_COUNT = 3;
    public static final int OPERATION_OVERTIME = 3000;


    public static final int SUCCESS = 500;
    public static final int FAILD = 501;


    public static final String SBE_UUID = "12b30300-1e67-48b1-92be-9d0d5e79aa15";
    public static final String SBS_UUID = "fd1312fe-9fd6-4ec9-82e9-a0c31182ebec";

    public static final int DEFAULT_EXCEPTION_CODE = 0x911;

    public static final int BLUETOOTH_STATUS = 0x11;

    public static final int WRITE = 0x12;

    public static final UUID UUID_CLIENT_CHARACTERISTIC_CONFIG_DESCRIPTOR =
            UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

}

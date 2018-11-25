package mobi.zack.superblelib.config;

import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Handler;

import static android.bluetooth.BluetoothDevice.PHY_LE_1M_MASK;
import static android.bluetooth.BluetoothDevice.TRANSPORT_AUTO;

public class ConnectConfig {

    private final boolean autoConnect;
    private final int transport;
    private final boolean opportunistic;
    private final int phy;
    private final Handler handler;

    private ConnectConfig(ConnectConfigBuilder builder) {
        this.autoConnect = builder.autoConnect;
        this.transport = builder.transport;
        this.opportunistic = builder.opportunistic;
        this.phy = builder.phy;
        this.handler = builder.handler;
    }

    public boolean isAutoConnect() {
        return autoConnect;
    }

    public int getTransport() {
        return transport;
    }

    public boolean isOpportunistic() {
        return opportunistic;
    }

    public int getPhy() {
        return phy;
    }

    public Handler getHandler() {
        return handler;
    }


    public static class ConnectConfigBuilder {

        private boolean autoConnect;
        private int transport;
        private boolean opportunistic;
        private int phy;
        private Handler handler;

        public ConnectConfigBuilder() { }

        public ConnectConfigBuilder autoConnect(boolean autoConnect) {
            this.autoConnect = autoConnect;
            return this;
        }

        public ConnectConfigBuilder transport(int transport) {
            this.transport = transport;
            return this;
        }

        public ConnectConfigBuilder opportunistic(boolean opportunistic) {
            this.opportunistic = opportunistic;
            return this;
        }

        public ConnectConfigBuilder phy(int phy) {
            this.phy = phy;
            return this;
        }

        public ConnectConfigBuilder handler(Handler handler) {
            this.handler = handler;
            return this;
        }

        public ConnectConfig build() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                transport = TRANSPORT_AUTO;
                opportunistic = false;
                phy = PHY_LE_1M_MASK;
            }
            return new ConnectConfig(this);
        }

    }
}

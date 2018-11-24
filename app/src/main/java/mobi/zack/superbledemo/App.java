package mobi.zack.superbledemo;

import android.app.Application;

import mobi.zack.superblelib.SuperBle;
import mobi.zack.superblelib.exception.SuperBleException;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            SuperBle.getInstance()
                    .setAutoConnect(false)
                    .setContext(this);
        } catch (SuperBleException e) {
            e.printStackTrace();
        }
    }
}

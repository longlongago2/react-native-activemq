package com.fwpt.reactivenativemqtt;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.uimanager.IllegalViewOperationException;
import com.facebook.react.bridge.Callback;

public class MqttServiceModule extends ReactContextBaseJavaModule {

    private ReactContext myContext;

    public MqttServiceModule(ReactApplicationContext reactContext) {
        super(reactContext);
        myContext = reactContext;
    }

    @Override
    public String getName() {
        return "MqttService";
    }

    @ReactMethod
    public void connectAndReserve(String topicNames,String userId) {
        new MQTTService(myContext,topicNames,userId).onStart();
    }

     @ReactMethod
     public void breakConnect(){
        MQTTService.onDestroy();
     }

     @ReactMethod
     public void checkConnected(Callback successCallback) {
        successCallback.invoke(MQTTService.checkConnected());
     }

}

package com.fwpt.reactivenativemqtt;

import android.annotation.SuppressLint;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttTopic;

public class PushCallback implements MqttCallback {

    //定义上下文对象
    public ReactContext context;

    public PushCallback(ReactContext context) {
        this.context = context;
    }

    @Override
    public void connectionLost(Throwable cause) {
        // 连接断开时的回调方法，可以在这里重新连接
        Log.i("PushCallback", "已断开连接");
    }

    @SuppressLint("NewApi")
    @Override
    public void messageArrived(MqttTopic topic, MqttMessage message)
            throws Exception {
        // 有新消息到达时的回调方法
        WritableMap event = Arguments.createMap();
        event.putString("message",message.toString());
        sendEvent(context, "MqttMsg",event);
    }

    @Override
    public void deliveryComplete(MqttDeliveryToken arg0) {
        // 成功发布某一消息后的回调方法
        Log.i("PushCallback", "成功发布一条消息");
    }

    public static void sendEvent(ReactContext reactContext, String eventName, WritableMap params) {
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }
}

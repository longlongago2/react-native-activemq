package com.fwpt.reactivenativemqtt;

import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttTopic;
import org.eclipse.paho.client.mqttv3.internal.MemoryPersistence;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MQTTService {

    public static final String BROKER_URL = "tcp://192.168.7.19:1883";
    public static String clientId = "xuym";
    public static String topic = "test123/PTP";
    private static MqttClient mqttClient;
    private static MqttConnectOptions options;
    private static String userName = "admin";
    private static String passWord = "admin";

    public static ReactContext myContext;
    private static ScheduledExecutorService scheduler;

    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void onStart(ReactContext myContextTarget, String topicNames, String userId) {
        try {
                if(mqttClient==null) {
                    topic = topicNames;
                    clientId = userId;
                    myContext = myContextTarget;
                    mqttClient = new MqttClient(BROKER_URL, clientId, new MemoryPersistence());
                }
                if (!mqttClient.isConnected()) {
                    // MQTT的连接设置
                    options = new MqttConnectOptions();
                    // 设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
                    // 换而言之，设置为false时可以客户端可以接受离线消息
                    options.setCleanSession(false);
                    // 设置连接的用户名
                    options.setUserName(userName);
                    // 设置连接的密码
                    options.setPassword(passWord.toCharArray());
                    // 设置超时时间 单位为秒
                    options.setConnectionTimeout(10);
                    // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
                    options.setKeepAliveInterval(10);

                    // 设置回调  回调类的说明看后面
                    mqttClient.setCallback(new PushCallback(myContext));
                    MqttTopic mqttTopic = mqttClient.getTopic(topic);
                    //mqtt客户端连接服务器
                    mqttClient.connect(options);
                    //mqtt客户端订阅主题
                    //在mqtt中用QoS来标识服务质量
                    //QoS=0时，报文最多发送一次，有可能丢失
                    //QoS=1时，报文至少发送一次，有可能重复
                    //QoS=2时，报文只发送一次，并且确保消息只到达一次。
                    int[] Qos = {1};
                    String[] topic1 = {topic};
                    mqttClient.subscribe(topic1, Qos);
                    //每隔10秒，心跳重连
                    startReconnect();
                } else {
                    mqttClient = null;
                }
        } catch (MqttException e) {
            Toast.makeText(myContext, "ActiveMQ something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

    }

    public static void onDestroy() {
        try {
            if(mqttClient!=null) {
                mqttClient.disconnect(0);
            }
        } catch (MqttException e) {
            Toast.makeText(myContext, "ActiveMQ something went wrong!" + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public static Boolean checkConnected() {
        return mqttClient!=null ? mqttClient.isConnected() : false;
    }

    /*原生模块可以在没有被调用的情况下往JavaScript发送事件通知。
    最简单的办法就是通过RCTDeviceEventEmitter，
    这可以通过ReactContext来获得对应的引用，像这样：*/
    public static void sendEvent(ReactContext reactContext, String eventName, WritableMap paramss)
    {
        System.out.println("reactContext="+reactContext);
        reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, paramss);

    }

    public static void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                if(!mqttClient.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    public static void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mqttClient.connect(options);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}

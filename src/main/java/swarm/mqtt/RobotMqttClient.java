package swarm.mqtt;

import java.util.*;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import swarm.robot.Robot;
import swarm.robot.exception.MqttClientException;

public class RobotMqttClient implements MqttCallback {
    private MqttClient client;
    private boolean isConnected = false;

    int robotID;

    private final String server;
    private final int port;
    private final String userName, password;
    private final String channel;

    public Queue<MqttMsg> inQueue = new PriorityQueue<MqttMsg>();
    public Queue<MqttMsg> outQueue = new PriorityQueue<MqttMsg>();

    // Documentation: https://www.eclipse.org/paho/files/javadoc/index.html
    public RobotMqttClient(String server, int port, String userName, String password, String channel, int id) {

        this.server = server;
        this.port = port;
        this.userName = userName;
        this.password = password;
        this.channel = channel;
        this.robotID = id;

        connect();
    }

    public void connect() {

        String broker = "tcp://" + server + ":" + port;
        String clientId = "client_" + Math.random() * 1000;
        MemoryPersistence persistence = new MemoryPersistence();

        try {
            client = new MqttClient(broker, clientId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setUserName(userName);
            connOpts.setPassword(password.toCharArray());
            connOpts.setKeepAliveInterval(60);
            connOpts.setAutomaticReconnect(true);
            client.connect(connOpts);
            isConnected = true;

            client.setCallback(this);

            System.out.println("Robot "+this.robotID+" MQTT: Connected");

        } catch (org.eclipse.paho.client.mqttv3.MqttException me) {
            System.out.println("reason :" + me.getReasonCode());
            System.out.println("msg :" + me.getMessage());
            System.out.println("loc :" + me.getLocalizedMessage());
            System.out.println("cause :" + me.getCause());
            System.out.println("excep :" + me);
            me.printStackTrace();
            reconnect();
        }
    }

    public void publish(String topic, String body) {
        publish(topic, body, 0);
    }

    public void publish(String topic, String body, int qos) {
        if (isConnected && topic.length() > 0 && body.length() > 0) {
            // Connected, non empty topic and body

            String t = channel + "/" + topic; // prepare topic with message channel
            MqttMessage m = new MqttMessage(body.getBytes());
            m.setQos(qos);

            try {
                this.client.publish(t, m);
                // System.out.println("MQTT " + t + " >> " + "Message " + m);

            } catch (org.eclipse.paho.client.mqttv3.MqttException me) {
                printMQTTError(me);
            }
        } else {
            try {
                throw new MqttClientException("Not Connected");
            } catch (MqttClientException mqttClientException) {
                mqttClientException.printStackTrace();
            }
        }
    }

    public void subscribe(String topic) {
        if (isConnected && topic.length() > 0) {
            // connected and non empty topic
            try {
                String t = channel + "/" + topic; // prepare topic with message channel
                client.subscribe(t);
                System.out.println("Subscribed to " + t);

            } catch (org.eclipse.paho.client.mqttv3.MqttException me) {
                printMQTTError(me);
            }
        } else {
            try {
                throw new MqttClientException("Not Connected");
            } catch (MqttClientException mqttClientException) {
                mqttClientException.printStackTrace();
            }
        }
    }

    private void printMQTTError(org.eclipse.paho.client.mqttv3.MqttException me) {
        System.out.println("reason " + me.getReasonCode());
        System.out.println("msg " + me.getMessage());
        System.out.println("loc " + me.getLocalizedMessage());
        System.out.println("cause " + me.getCause());
        System.out.println("excep " + me);
        me.printStackTrace();
    }

    @Override
    public void connectionLost(Throwable t) {
        System.out.println("Robot "+robotID+" Connection lost!");
        // code to reconnect to the broker would go here if desired
        isConnected = false;
        reconnect();

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String t = topic.substring(topic.indexOf("/") + 1);
        String msg = new String(message.getPayload());

        if (msg.length() > 0) {
            // System.out.println("Received " + topic + " >> " + new
            // String(message.getPayload()));
            inQueue.add(new MqttMsg(t, msg));
        }
    }

    public MqttMsg inQueue() {
        return this.inQueue.poll();
    }

    public MqttMsg outQueue() {
        return this.outQueue.poll();
    }

     private void reconnect() {
        System.out.println("Reconnecting...");
        while (!isConnected) {
            connect(); // Attempt reconnection
//            try {
//                Thread.sleep(5000); // Sleep for 5 seconds before attempting reconnection
//                connect(); // Attempt reconnection
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
//        System.out.println("Subscribe after reconnecting...");
//        subscribe("localization/update/?");
//        subscribe("robot/msg/"+robotID);
//        subscribe("robot/msg/broadcast");
//        subscribe("robot/ota/broadcast");
//        subscribe("sensor/distance/"+robotID);
//        subscribe("sensor/distance/"+robotID+"/?");
//        subscribe("sensor/proximity/"+robotID);
//        subscribe("sensor/color/"+robotID);
//        subscribe("sensor/color/"+robotID+"/?");
//        subscribe("comm/in/simple/"+robotID);
//        subscribe("comm/in/direct/"+robotID);

    }

}
package swarm.robot.sensors;

import org.json.simple.parser.ParseException;
import swarm.mqtt.RobotMqttClient;

import java.util.HashMap;

import org.json.simple.JSONObject;
import swarm.mqtt.MqttMsg;
import swarm.robot.exception.SensorException;
import swarm.robot.Robot;

public class ColorSensor<array, col_value> extends AbstractSensor {

    private final static int MQTT_TIMEOUT = 1000;

    private enum mqttTopic {COLOR_IN, COLOR_LOOK}

    private HashMap<mqttTopic, String> topicsSub = new HashMap<mqttTopic, String>();

    private boolean col_lock = false;
    double r_value=0, g_value=0, b_value=0;

    public ColorSensor(Robot robot, RobotMqttClient m) {
        super(robot, m);
        subscribe(mqttTopic.COLOR_IN, "sensor/color/" + robotId);
        subscribe(mqttTopic.COLOR_LOOK, "sensor/color/" + robotId + "/?");
    }

    private void subscribe(mqttTopic key, String topic) {
        topicsSub.put(key, topic);      // Put to the queue
        robotMqttClient.subscribe(topic);   // Subscribe through MqttHandler
    }

    @Override
    public void handleSubscription(Robot robot, MqttMsg m) {
        // sensor/color/
        String topic = m.topic;
        String msg = m.message;

        if (topic.equals(topicsSub.get(mqttTopic.COLOR_IN))) {
            // sensor/color/{id}
            //System.out.println("Input>" + col_msg);------------------------------

            // TODO: Handle Infinity
            if (msg.compareTo("Infinity") == 0) {
                // -1 will be returned as a fail-proof option. Should throw an exception
                r_value=-1; //Double.POSITIVE_INFINITY;
                g_value=-1; //Double.POSITIVE_INFINITY;
                b_value=-1; //Double.POSITIVE_INFINITY;
            } else {
                String[] arrSplit = msg.split(" ");

                r_value=Double.parseDouble(arrSplit[0]);
                g_value=Double.parseDouble(arrSplit[1]);
                b_value=Double.parseDouble(arrSplit[2]);
            }

            col_lock = false;

            // robot.sensorInterrupt("R: " + r_value + "G:" + g_value + "B:" + b_value );

        } else if (topic.equals(topicsSub.get(mqttTopic.COLOR_LOOK))) {
            // TODO: What we need to do in here ?

            // sensor/color/{id}/?
            System.out.println("Received: " + topic + "> " + "R: " + r_value + "G:" + g_value + "B:" + b_value );

        } else {
            System.out.println("Received (unknown): " + topic + "> " + "R: " + r_value + "G:" + g_value + "B:" + b_value );
        }

    }

    public double[] getColor() throws Exception {
        // Publish to sensor/color/ -> {id: this.id}
        // Listen to sensor/color/{robotId} -> color
        // return the reading

        // Prepare the message
        JSONObject msg = new JSONObject();
        msg.put("id", robotId);
        //System.out.println(msg.toJSONString());

        // Acquire the color sensor lock
        col_lock = true;

        robotMqttClient.publish("sensor/color", msg.toJSONString());
        robot.delay(250);

        long stratTime = System.currentTimeMillis();
        boolean timeout = false;

        while (col_lock && !timeout) {
            try {
                robot.handleSubscribeQueue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.print(".");
            robot.delay(100);
            timeout = (System.currentTimeMillis() - stratTime > MQTT_TIMEOUT);
        }

        if (timeout) {
            throw new SensorException("Color sensor timeout");
        }

        System.out.println("R: " + r_value + "G:" + g_value + "B:" + b_value );

        double[] color = {r_value, g_value, b_value};
        return color;
    }

    public void sendColor(double r, double g, double b, double ambient) {
        // Only for test, virtual robots will not invoke this.

        JSONObject obj = new JSONObject();
        obj.put("id", robotId);
        obj.put("R", r);
        obj.put("G", g);
        obj.put("B", b);
        obj.put("ambient", ambient);

        robotMqttClient.publish("sensor/color/", obj.toJSONString());
    }
}

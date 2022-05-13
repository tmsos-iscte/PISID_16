import org.eclipse.paho.client.mqttv3.MqttException;

public class Main {

	public static void main(String[] args) throws InterruptedException, MqttException {

		MqttPublisher mp= new MqttPublisher();
		mp.main(args);
		
		MqttReceiver mr= new MqttReceiver();
		mr.main(args);
	}
}


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

public class MqttReceiver implements MqttCallback {
	static String cloudServer = "tcp://broker.mqtt-dashboard.com:1883";
	static String cloudTopic = "sid2022_g16";
	static IMqttClient mqttClient;
	static String db = "sid";
	static String DBuser = "root";
	static String DBpass = "root";
	static Connection connectionSQL;

	static Timestamp datahoraTimestamp(String datahora) {
		String T= datahora.replace('T', ' ');
		String Z= T.substring(0, T.length()-1);
		System.out.println(Z);
		Timestamp timestamp= null;
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(Z);
			timestamp = new Timestamp(date.getTime());

			System.out.println(timestamp); // 2021-12-15 00:34:56.789
		} catch (ParseException exception) {
			exception.printStackTrace();
		}
		return timestamp;
	}

	public static void main(String[] args) throws InterruptedException, MqttSecurityException, MqttException, ClassNotFoundException, SQLException {

		String clientId = UUID.randomUUID().toString();
		mqttClient = new MqttClient(cloudServer,clientId);

		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		mqttClient.connect(options);

		new MqttReceiver().subscribe();

		Class.forName("com.mysql.cj.jdbc.Driver");
		connectionSQL= DriverManager.getConnection("jdbc:mysql://localhost/" +db+ "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);

		if(connectionSQL!=null)
			System.out.println("Ligação estabelecida");

	}

	public void subscribe() throws MqttSecurityException, MqttException {
		mqttClient.setCallback(this);
		mqttClient.subscribe(cloudTopic);
	}

	@Override
	public void connectionLost(Throwable arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deliveryComplete(IMqttDeliveryToken arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void messageArrived(String arg0, MqttMessage arg1) {
		System.out.println("---MQTT---");
		System.out.println(arg1.toString());

		String msg= arg1.toString();
		String[] aux= msg.split(",", 4);	
		String aux1= aux[0];
		String[] z= aux1.split("Z", 2);
		String zona= z[1];

		String idsensor= aux[1];

		String aux3= aux[2];
		Timestamp datahora= datahoraTimestamp(aux3);

		String aux4= aux[3];
		String[] l= aux4.split("}}}}", 2);
		String auxleitura= l[0];
		System.out.println(auxleitura);
		System.out.println("pote");

		System.out.println("zona1" + zona);        
		int idzona= Integer.parseInt(zona);
		double leitura= Double.parseDouble(auxleitura);
		System.out.println("zona" + idzona);

		String query = "INSERT INTO medicao (idzona, idsensor, datahora, leitura, valorvalido) VALUES ('" + idzona + "', '" + idsensor +"', '"+ datahora +"', '"+ leitura + "', '1');";
		System.out.println("---MYSQL---");
		System.out.println(query);
		try {
			connectionSQL.createStatement().executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

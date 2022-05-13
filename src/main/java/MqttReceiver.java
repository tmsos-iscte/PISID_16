import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

	static final String db1 = "sid2022";
	static final String DBuser1 = "aluno";
	static final String DBpass1 = "aluno";
	static Connection connectionSQL1;

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

	public static void main(String[] args) throws InterruptedException, MqttSecurityException, MqttException {

		String clientId = UUID.randomUUID().toString();
		mqttClient = new MqttClient(cloudServer,clientId);

		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		mqttClient.connect(options);

		new MqttReceiver().subscribe();
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
	public void messageArrived(String arg0, MqttMessage arg1) throws SQLException, NumberFormatException, ParseException, InterruptedException {
		Calendar cal = Calendar.getInstance();
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");;
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			connectionSQL= DriverManager.getConnection("jdbc:mysql://localhost/" +db+ "?useTimezone=true&serverTimezone=UTC", DBuser, DBpass);
			connectionSQL1= DriverManager.getConnection("jdbc:mysql://194.210.86.10/" +db1+ "?useTimezone=true&serverTimezone=UTC", DBuser1, DBpass1);		

		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		if(connectionSQL!=null)
			System.out.println("Ligação estabelecida");

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
		Timestamp dataAtualMais = new Timestamp(System.currentTimeMillis());   //NOVO !!!!!!!!!!!!
        Timestamp dataAtualMenos = new Timestamp(System.currentTimeMillis());  //NOVO !!!!!!!!!!!!
        cal.add(Calendar.SECOND,5);                                            //NOVO !!!!!!!!!!!!
        dataAtualMais = new Timestamp(cal.getTime().getTime());                //NOVO !!!!!!!!!!!!
        cal.add(Calendar.SECOND,-10);                                          //NOVO !!!!!!!!!!!!
        dataAtualMenos = new Timestamp(cal.getTime().getTime());               //NOVO !!!!!!!!!!!!
        int b = datahora.compareTo(dataAtualMais);                             //NOVO !!!!!!!!!!!!
        int b2 = datahora.compareTo(dataAtualMenos);

		String aux4= aux[3];
		String[] l= aux4.split("}}}}", 2);
		String auxleitura= l[0];
		System.out.println(auxleitura);
		System.out.println("pote");

		System.out.println("zona1" + zona);        
		int idzona= Integer.parseInt(zona);
		double leitura= Double.parseDouble(auxleitura);
		System.out.println("zona" + idzona);

		String x[];
		int id=0;
		String sensor="";
		if((b<0&&b2>0)||b==0) {
			if(idsensor.contains("T")) {
				x= idsensor.split("T");
				id= Integer.parseInt(x[1]);
				sensor= idsensor.substring(0, idsensor.length()-1);
			} else if(idsensor.contains("H")) {
				x= idsensor.split("H");
				id= Integer.parseInt(x[1]);
				sensor= idsensor.substring(0, idsensor.length()-1);
			}else if(idsensor.contains("L")) {
				x= idsensor.split("L");
				id= Integer.parseInt(x[1]);
				sensor= idsensor.substring(0, idsensor.length()-1);
			}

			String pre_mysql= "SELECT * FROM sensor WHERE idsensor="+id+" AND tipo='"+sensor+"'";
			PreparedStatement pre_mysql_aux1 = connectionSQL1.prepareStatement(pre_mysql);
			ResultSet pre_mysql_aux2 =pre_mysql_aux1.executeQuery();

			while(pre_mysql_aux2.next()) {

				double limitemin= pre_mysql_aux2.getDouble("limiteinferior");
				double limitemax= pre_mysql_aux2.getDouble("limitesuperior");
				if(limitemin<=leitura&&limitemax>=leitura) {
					String query = "INSERT INTO medicao (idzona, idsensor, datahora, leitura, valorvalido) VALUES ('" + idzona + "', '" + idsensor +"', '"+ datahora +"', '"+ leitura + "', '1');";
					System.out.println("---MYSQL---");
					System.out.println(query);
					try {
						connectionSQL.createStatement().executeUpdate(query);
						System.out.println("SPORTING CP" + java.time.LocalDateTime.now());  
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					Thread.sleep(1000);

				}
			}
		}
	}
}

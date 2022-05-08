
import java.util.UUID;

import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MqttPublisher {

	public static void main(String[] args) throws MqttException, InterruptedException {
		String cloudServer = "tcp://broker.mqtt-dashboard.com:1883";
		String cloudTopic = "sid2022_g16";
		String urlLocal = "mongodb://localhost:27017";
		
		String database = "sid2022";
		String collectiont1 = "medicoes2022";
		
		MongoClient localMongoClient = new MongoClient(new MongoClientURI(urlLocal));
	    MongoDatabase localMongoDatabase = localMongoClient.getDatabase(database);
	    MongoCollection<Document> localMongoCollection = localMongoDatabase.getCollection(collectiont1);
	    MongoCursor<Document> cursor = localMongoCollection.find().iterator();

		String clientId = UUID.randomUUID().toString();
		IMqttClient mqttClient = new MqttClient(cloudServer,clientId);
		
		MqttConnectOptions options = new MqttConnectOptions();
		options.setAutomaticReconnect(true);
		options.setCleanSession(true);
		options.setConnectionTimeout(10);
		mqttClient.connect(options);
		
		for (;;) {
			//CRIA  mensagem
			//Document document = new Document();
	    	//System.out.println(cursor.next().toString());
			Document lastInsertedDocLocal = (Document)localMongoCollection.find().sort(new BasicDBObject("_id",-1)).first(); 
	    	//System.out.println(dados);
	    	
	    	String[] parts = lastInsertedDocLocal.toString().split(",");
	    	String[] zona = parts[1].split("=");
	    	String[] sensor = parts[2].split("=");
	    	String[] data = parts[3].split("=");
	    	String[] medicao = parts[4].split("=");
			
			/*Somatorio de strings para formar a string principal para payload*/
		    String rawMsg = zona[1] + "," + sensor[1] + "," + data[1] + "," + medicao[1];
		    //System.out.println(rawMsg);
		  
			//Converte para Bytes
			byte[] payload = rawMsg.getBytes();	    
			
			//Envia mensagem
		    MqttMessage msg = new MqttMessage(payload);
		    msg.setQos(2);
		    msg.setRetained(false);
		    mqttClient.publish(cloudTopic,msg);
			 System.out.println("SPORTING CP" + java.time.LocalDateTime.now());  
		    Thread.sleep(1000);
		}
	}
}
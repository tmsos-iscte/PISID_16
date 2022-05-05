import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


public class MongoDB {

	private Lock l= new ReentrantLock();
	private int port=5000;
	static final String urlLocal = "mongodb://localhost:27017";
	static final String database = "sid2022";
	static final String collectiont1 = "medicoes2022";
	static MongoClient localMongoClient = new MongoClient(new MongoClientURI(urlLocal));
	static MongoDatabase localMongoDatabase = localMongoClient.getDatabase(database);
	static MongoCollection<Document> localMongoCollection = localMongoDatabase.getCollection(collectiont1);
	static MongoCursor<Document> cursor = localMongoCollection.find().iterator();

	public MongoDB() throws UnknownHostException, IOException {
		while (cursor.hasNext()) {
			
			DealWithInfo d = new DealWithInfo();
			d.start();
			try {
				d.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

	private class DealWithInfo extends Thread{

		public Socket socket;
		private ObjectOutputStream outt;

		public DealWithInfo() throws UnknownHostException, IOException {
			this.socket = new Socket(InetAddress.getByName("127.0.0.1"),port);
			outt = new ObjectOutputStream ( socket . getOutputStream ());
		}

		@Override
		synchronized public void run() {

			l.lock();
			Document lastInsertedDocLocal = (Document)localMongoCollection.find().sort(new BasicDBObject("_id",-1)).first(); 


			//String dados = cursor.next().toString(); 

			String[] parts = lastInsertedDocLocal.toString().split(",");
			String[] zona = parts[1].split("=");
			String[] sensor = parts[2].split("=");
			String[] data = parts[3].split("=");
			String[] medicao = parts[4].split("=");

			lastInsertedDocLocal.append(zona[0], zona[1]);
			lastInsertedDocLocal.append(sensor[0], sensor[1]);
			lastInsertedDocLocal.append(data[0], data[1]);
			lastInsertedDocLocal.append(medicao[0], medicao[1]);

			try {
				outt.writeObject(lastInsertedDocLocal);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			l.unlock();
		}
	}
	public static void main(String[] args) {
		try {
			MongoDB threadTest = new MongoDB();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
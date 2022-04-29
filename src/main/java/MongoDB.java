import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoDB {
	static final String urlLocal = "mongodb://localhost:27017";
	static final String database = "sid2022";
	static final String collectiont1 = "medicoes";
	
	static MongoClient localMongoClient = new MongoClient(new MongoClientURI(urlLocal));
    static MongoDatabase localMongoDatabase = localMongoClient.getDatabase(database);
    static MongoCollection<Document> localMongoCollection = localMongoDatabase.getCollection(collectiont1);
    static MongoCursor<Document> cursor = localMongoCollection.find().iterator();
    
    static final String hostPorto= "5000";
    static ServerSocket ss; 
	static String[] dataRecieved;
    
    public void connection () {
		try {
			System.out.println("Porto do host: " + hostPorto);
			ss= new ServerSocket(Integer.parseInt(hostPorto)); 
			System.out.println("Accepting connections on" + " " + hostPorto);
			while(true) {
				System.out.println("sli");
				Socket socket = ss.accept(); 
				DealWithClient d = new DealWithClient(socket);
				d.start();
				System.out.println("sli");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    public class DealWithClient extends Thread {

		private Socket socket;
		private ObjectOutputStream out;

		public DealWithClient(Socket s) {
			System.out.println("teste");
			this.socket = s;
			try {
				this.out = new ObjectOutputStream((socket.getOutputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			System.out.println("Client connected");
				try {
					//ENQUANTO PODER CORRER, ELE CORRE
				    while (cursor.hasNext()) {
				    	Document document = new Document();
	
				    	String dados = cursor.next().toString(); 
				    	
				    	String[] parts = dados.split(",");
				    	String[] zona = parts[1].split("=");
				    	String[] sensor = parts[2].split("=");
				    	String[] data = parts[3].split("=");
				    	String[] medicao = parts[4].split("=");
				    	
				    	document.append(zona[0], zona[1]);
				    	document.append(sensor[0], sensor[1]);
				    	document.append(data[0], data[1]);
				    	document.append(medicao[0], medicao[1]);

				    	try {
							out.writeObject(document);
							System.out.println("pote");
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				} finally {
				    cursor.close();
				}
		}
	} public static void main(String[] args) {
		MongoDB mongoDB = new MongoDB();
		mongoDB.connection();
	}
    
}

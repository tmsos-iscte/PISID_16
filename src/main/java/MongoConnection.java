
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {

	public static void main(String[] args) throws InterruptedException {

		//LIGAÇÃO À MONGO CLOUD******

		String url = "mongodb://aluno:aluno@194.210.86.10:27017/?authSource=admin";	
		//	String urlLocal = "mongodb://localhost:27017,localhost:25017,localhost:23017/?replicaSet=replicademo";
		String urlLocal = "mongodb://localhost:27017";


		String database = "sid2022";
		String collectiont1 = "medicoes2022";


		//CRIA A MONGO CLIENT LIGADA À CLOUD
		MongoClient cloudMongoClient = new MongoClient(new MongoClientURI(url));
		MongoClient localMongoClient = new MongoClient(new MongoClientURI(urlLocal));

		//VAI CRIAR A DATABASE
		MongoDatabase cloudMongoDatabase = cloudMongoClient.getDatabase(database);
		MongoDatabase localMongoDatabase = localMongoClient.getDatabase(database);

		//CRIA UMA COLEÇÃO REPLICA DA MONGO CLOUD
		MongoCollection<Document> cloudMongoCollection = cloudMongoDatabase.getCollection(collectiont1);
		MongoCollection<Document> localMongoCollection = localMongoDatabase.getCollection(collectiont1);


		//VAI BUSCAR ITERADOR? 
		MongoCursor<Document> cursor = cloudMongoCollection.find().iterator();

		//Document document = new Document();


		try {
			for (;;) {
				Document lastInsertedDocCloud = (Document)cloudMongoCollection.find().sort(new BasicDBObject("_id",-1)).first();
				System.out.println(lastInsertedDocCloud.toJson());

				String[] parts = lastInsertedDocCloud.toString().split(",");
				if(parts.length==5) {
					String[] zona = parts[1].split("=");
					String[] sensor = parts[2].split("=");
					String[] data = parts[3].split("=");
					String[] medicao = parts[4].split("=");

					Document document = new Document();
					document.append(zona[0], zona[1]);
					document.append(sensor[0], sensor[1]);
					document.append(data[0], data[1]);
					document.append(medicao[0], medicao[1]);

					localMongoCollection.insertOne(document);
					 System.out.println("SPORTING CP" + java.time.LocalDateTime.now());  

					System.out.println("");
					Thread.sleep(1000);
				}
			}

		} finally {
			cursor.close();
		}
	}	
}

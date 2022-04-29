
import org.bson.Document;
import java.util.regex.*;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
public class MongoConnection {
	
	public static void main(String[] args) {
		
		//LIGAÇÃO À MONGO CLOUD******
		
		String url = "mongodb://aluno:aluno@194.210.86.10:27017/?authSource=admin";	
	//	String urlLocal = "mongodb://localhost:27017,localhost:25017,localhost:23017/?replicaSet=replicademo";
		String urlLocal = "mongodb://localhost:27017";
		
		
		String database = "sid2022";
		String collectiont1 = "medicoes";
		
		
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
			//ENQUANTO PODER CORRER, ELE CORRE
		    while (cursor.hasNext()) {
		    	Document document = new Document();
		    	//System.out.println(cursor.next().toString());
		    	String dados = cursor.next().toString(); 
		    	//System.out.println(dados);
		    	
		    	String[] parts = dados.split(",");
		    	String[] zona = parts[1].split("=");
		    	String[] sensor = parts[2].split("=");
		    	String[] data = parts[3].split("=");
		    	String[] medicao = parts[4].split("=");
		    	
		    	//zona=z1
		    	document.append(zona[0], zona[1]);
		    	document.append(sensor[0], sensor[1]);
		    	document.append(data[0], data[1]);
		    	document.append(medicao[0], medicao[1]);
		    	//document.
		    	//for(int i=0; i< parts.length; i++)
		    		//System.out.println(parts[i]);
		    	//document.append(cursor.next().toString());
		    	//System.out.println(cursor.next().toJson());
		    	//document.append(cursor.next().toJson());
		    	localMongoCollection.insertOne(document);
		    }
		} finally {
		    cursor.close();
		}
		
		//TEMOS QUE METER A INSERIR AS VARIAVEIS
		
		
		
		
	   /*document.append("Zona43", "Z1");
	    document.append("Sensor", "T1");
	    document.append("Data", "01/02/2022");
	    document.append("Hora", "14:30");
	    document.append("Medicao", "16.40");*/
	    
	    //localMongoCollection.insertOne(document);
	}

}

import java.util.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;  
public class data {    
       public static void main(String args[]){    
    	   String string = "2021-12-15T12:34:56Z";
    	   String T= string.replace('T', ' ');
    	   String Z= T.substring(0, T.length()-1);
    	   System.out.println(Z);
    	   Timestamp timestamp= null;
    	   try {
    	     Date date = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(Z);
    	     timestamp = new Timestamp(date.getTime());

    	     ; // 2021-12-15 00:34:56.789
    	   } catch (ParseException exception) {
    	     exception.printStackTrace();
    	   }
    	   System.out.println(timestamp);
        }    
}    

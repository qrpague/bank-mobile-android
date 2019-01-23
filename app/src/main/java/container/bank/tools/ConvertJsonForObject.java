package  container.bank;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ConvertJsonForObject {

    private static ConvertJsonForObject instance ;
    private Gson gson;

    private ConvertJsonForObject(){
        gson = new GsonBuilder().setDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz").create(); //tipo layut data: "Ago 13, 2014",
    }
    public static ConvertJsonForObject getInstance(){
        if (instance == null) {
            instance = new ConvertJsonForObject();
        }
        return instance ;
    }
    public JsonObject getObjectName (String buffer  ) {
        JsonObject jobj = new Gson().fromJson(buffer, JsonObject.class);

        return jobj ;
    }
    public <E> Object convert(String buffer,Class c){
        try {
            return (E) gson.fromJson(buffer, c);
        }catch ( Exception e ) {
            e.printStackTrace();
        }
        return null ;
    }
    public <T> T convert(String buffer,Type c){
        return (T)gson.fromJson(buffer,c);
    }
    public  <T> List<T>  convertToList(String buffer , Class c){
        try {
            T[] array = (T[]) gson.fromJson(buffer,c);
            List<T> lista = new ArrayList<T>();
            for (T t : array) {
                lista.add(t);
            }
            return lista;
        }catch (Exception n){
            n.printStackTrace();
            return null ;
        }catch (IllegalAccessError i){
            i.printStackTrace();
            return null ;
        }

    }

}

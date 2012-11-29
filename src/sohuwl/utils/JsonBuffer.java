package net.opentsdb.sohuwl.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wuzbin
 * Date: 12-11-20
 * Time: 下午4:26
 * To change this template use File | Settings | File Templates.
 */
public class JsonBuffer {
    JSONObject object;
    public JsonBuffer() {
        object = new JSONObject();
    }
    public<T> void appendList(String key, List<T> list, JsonEncoder<T> encoder) throws JSONException {
        JSONArray array = new JSONArray();
        object.put(key, array);
        for(T t : list) {
            JsonBuffer jsonBuffer = new JsonBuffer();
            encoder.encode(t, jsonBuffer);
            array.put(jsonBuffer.getJsonObject());
        }
    }

    public<T> void appendList(String key, Iterable<T> list, JsonEncoder<T> encoder, Criteria<T> criteria) throws JSONException {
        JSONArray array = new JSONArray();
        object.put(key, array);
        for(T t : list) {
            if (criteria != null) {
                if (criteria.isMeet(t)) {
                    JsonBuffer jsonBuffer = new JsonBuffer();
                    encoder.encode(t, jsonBuffer);
                    array.put(jsonBuffer.getJsonObject());
                }
            } else {
                JsonBuffer jsonBuffer = new JsonBuffer();
                encoder.encode(t, jsonBuffer);
                array.put(jsonBuffer.getJsonObject());
            }
        }
    }

    public<T> void appendList(String key, T[] list, JsonEncoder<T> encoder) throws JSONException {
        JSONArray array = new JSONArray();
        object.put(key, array);
        for(T t : list) {
            JsonBuffer jsonBuffer = new JsonBuffer();
            encoder.encode(t, jsonBuffer);
            array.put(jsonBuffer.getJsonObject());
        }
    }


    public<T> void appendObject(String key, T t, JsonEncoder<T> encoder) throws JSONException {
        JsonBuffer jsonBuffer = new JsonBuffer();
        encoder.encode(t, jsonBuffer);
        object.put(key, jsonBuffer.getJsonObject());
    }

    public void appendInteger(String key, Integer value) throws JSONException {
        object.put(key, value);
    }

    public void appendString(String key, String value) throws JSONException {
        object.put(key, value);
    }

    public void appendLong(String key, Long value) throws JSONException {
        object.put(key, value);
    }

    public void appendFloat(String key, Float value) throws JSONException {
        object.put(key, value);
    }

    public void appendDouble(String key, Double value) throws JSONException {
        object.put(key, value);
    }

    private JSONObject getJsonObject() {
         return object;
    }

    public String toString() {
        return object.toString();
    }



    public static void main(String[] args) throws JSONException {
        User wuzbin = new User();
        wuzbin.setAge(20);
        wuzbin.setName("wuzbin");

        User cg = new User();
        cg.setAge(10);
        cg.setName("cg");

        List<User> users = new ArrayList<User>();
        users.add(wuzbin);
        users.add(cg);

        JsonBuffer jb = new JsonBuffer();
        jb.appendList("users", users, new JsonEncoder<User>() {
            @Override
            public void encode(User user, JsonBuffer jsonBuffer) throws JSONException {
                jsonBuffer.appendInteger("age", user.getAge());
                jsonBuffer.appendString("name", user.getName());
            }
        });
        System.out.println(jb.toString());
    }

}

class User{
    private int age;
    private String name;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
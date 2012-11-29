package net.opentsdb.sohuwl.utils;

import org.json.JSONException;

/**
 * Created with IntelliJ IDEA.
 * User: wuzbin
 * Date: 12-11-20
 * Time: 下午4:28
 * To change this template use File | Settings | File Templates.
 */
public interface JsonEncoder<T> {
    public void encode(T t, JsonBuffer jsonBuffer) throws JSONException;
}

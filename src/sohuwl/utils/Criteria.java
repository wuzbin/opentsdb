package net.opentsdb.sohuwl.utils;

/**
 * Created with IntelliJ IDEA.
 * User: wuzbin
 * Date: 12-11-27
 * Time: 上午11:22
 * To change this template use File | Settings | File Templates.
 */
public interface Criteria<T> {
    public boolean isMeet(T t);
}

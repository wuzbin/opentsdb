package net.opentsdb.sohuwl.tsd;

import com.stumbleupon.async.Callback;
import com.stumbleupon.async.Deferred;
import net.opentsdb.core.TSDB;
import net.opentsdb.core.Tags;
import net.opentsdb.stats.StatsCollector;
import net.opentsdb.tsd.HttpQuery;
import net.opentsdb.tsd.HttpRpc;
import net.opentsdb.uid.NoSuchUniqueName;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created with IntelliJ IDEA.
 * User: wuzbin
 * Date: 12-11-19
 * Time: 下午2:24
 * To change this template use File | Settings | File Templates.
 */
public class HttpPutRpc implements HttpRpc {
    private static final AtomicLong requests = new AtomicLong();
    private static final AtomicLong hbase_errors = new AtomicLong();
    private static final AtomicLong invalid_values = new AtomicLong();
    private static final AtomicLong illegal_arguments = new AtomicLong();
    private static final AtomicLong unknown_metrics = new AtomicLong();

    public void execute(TSDB tsdb, HttpQuery query) throws IOException {
        String metric = query.getQueryStringParam("metric");
        List<String> tags = query.getQueryStringParams("tags");
        String value = query.getQueryStringParam("value");
        String time = query.getQueryStringParam("time");
        if (metric == null || tags == null || value == null || time == null) {
            throw new IllegalArgumentException("not enough arguments" + " (need least 4, metric, timestamp, tags, value)");
        }
        requests.incrementAndGet();
        String errmsg = null;
        try {
            final class PutErrback implements Callback<Exception, Exception> {
                public Exception call(final Exception arg) {
                    hbase_errors.incrementAndGet();
                    return arg;
                }
                public String toString() {
                    return "report error to channel";
                }
            }
            importDataPoint(tsdb, metric, time, tags, value).addErrback(new PutErrback());
        } catch (NumberFormatException x) {
            errmsg = "put: invalid value: " + x.getMessage() + '\n';
            invalid_values.incrementAndGet();
        } catch (IllegalArgumentException x) {
            errmsg = "put: illegal argument: " + x.getMessage() + '\n';
            illegal_arguments.incrementAndGet();
        } catch (NoSuchUniqueName x) {
            errmsg = "put: unknown metric: " + x.getMessage() + '\n';
            unknown_metrics.incrementAndGet();
        } finally {
            if (errmsg != null) {
                query.sendReply(errmsg);
            } else {
                query.sendReply("ok");
            }
        }
    }

    private Deferred<Object> importDataPoint(final TSDB tsdb, final String metric, final String time, final List<String> tagList, final String value) {
        if (metric.length() <= 0) {
            throw new IllegalArgumentException("empty metric name");
        }
        final long timestamp = Tags.parseLong(time);
        if (timestamp <= 0) {
            throw new IllegalArgumentException("invalid timestamp: " + timestamp);
        }
        if (value.length() <= 0) {
            throw new IllegalArgumentException("empty value");
        }
        final HashMap<String, String> tags = new HashMap<String, String>();
        for (String tag : tagList) {
            if (!tag.isEmpty()) {
                Tags.parse(tags, tag, ':');
            }
        }
        if (value.indexOf('.') < 0) {  // integer value
            return tsdb.addPoint(metric, timestamp, Tags.parseLong(value), tags);
        } else {  // floating point value
            return tsdb.addPoint(metric, timestamp, Float.parseFloat(value), tags);
        }
    }

    /**
     * Collects the stats and metrics tracked by this instance.
     * @param collector The collector to use.
     */
    public static void collectStats(final StatsCollector collector) {
        collector.record("rpc.received", requests, "type=http_get");
        collector.record("rpc.errors", hbase_errors, "type=http_hbase_errors");
        collector.record("rpc.errors", invalid_values, "type=http_invalid_values");
        collector.record("rpc.errors", illegal_arguments, "type=http_illegal_arguments");
        collector.record("rpc.errors", unknown_metrics, "type=http_unknown_metrics");
    }
}

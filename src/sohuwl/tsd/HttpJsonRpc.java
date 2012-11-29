package net.opentsdb.sohuwl.tsd;

import net.opentsdb.core.DataPoint;
import net.opentsdb.core.DataPoints;
import net.opentsdb.core.Query;
import net.opentsdb.core.TSDB;
import net.opentsdb.sohuwl.utils.Criteria;
import net.opentsdb.sohuwl.utils.JsonBuffer;
import net.opentsdb.sohuwl.utils.JsonEncoder;
import net.opentsdb.tsd.BadRequestException;
import net.opentsdb.tsd.GraphHandler;
import net.opentsdb.tsd.HttpQuery;
import net.opentsdb.tsd.HttpRpc;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: wuzbin
 * Date: 12-11-20
 * Time: 上午11:46
 * To change this template use File | Settings | File Templates.
 */
public class HttpJsonRpc implements HttpRpc {
    private static final Logger LOG =
            LoggerFactory.getLogger(HttpJsonRpc.class);
    @Override
    public void execute(TSDB tsdb, HttpQuery query) throws IOException {
        if (!query.hasQueryStringParam("json")
                && !query.hasQueryStringParam("png")
                && !query.hasQueryStringParam("ascii")) {
            String uri = query.request().getUri();
            if (uri.length() < 4) {  // Shouldn't happen...
                uri = "/";             // But just in case, redirect.
            } else {
                uri = "/#" + uri.substring(3);  // Remove "/q?"
            }
            query.redirect(uri);
            return;
        }
        responseJson(tsdb,query);
    }

    private void responseJson(TSDB tsdb, HttpQuery query) {
        final long start_time = GraphHandler.getQueryStringDate(query, "start");
        final String dateFormat = query.getQueryStringParam("tf");
        if (start_time == -1) {
            throw BadRequestException.missingParameter("start");
        }
        long end_time = GraphHandler.getQueryStringDate(query, "end");
        final long now = System.currentTimeMillis() / 1000;
        if (end_time == -1) {
            end_time = now;
        }
        final long span_end = end_time;
        Query[] tsdbqueries;
        List<String> options;
        tsdbqueries = GraphHandler.parseQuery(tsdb, query);
        if (tsdbqueries.length > 1) {
            query.sendReply("Tsdb doesn't support more than one m in query");
            return;
        }
        options = query.getQueryStringParams("o");
        if (options == null) {
            options = new ArrayList<String>(tsdbqueries.length);
            for (int i = 0; i < tsdbqueries.length; i++) {
                options.add("");
            }
        } else if (options.size() != tsdbqueries.length) {
            throw new BadRequestException(options.size() + " `o' parameters, but "
                    + tsdbqueries.length + " `m' parameters.");
        }
        for (final Query tsdbquery : tsdbqueries) {
            try {
                tsdbquery.setStartTime(start_time);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("start time: " + e.getMessage());
            }
            try {
                tsdbquery.setEndTime(end_time);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("end time: " + e.getMessage());
            }
        }
        final int nqueries = tsdbqueries.length;
        @SuppressWarnings("unchecked")
        final HashSet<String>[] aggregated_tags = new HashSet[nqueries];
        JsonBuffer jsonBuffer = new JsonBuffer();
        int npoints = 0;
        for (int i = 0; i < nqueries; i++) {
            try {  // execute the TSDB query!
                // XXX This is slow and will block Netty.  TODO(tsuna): Don't block.
                // TODO(tsuna): Optimization: run each query in parallel.
                final DataPoints[] series = tsdbqueries[i].run();
                if (series.length > 1){
                    query.sendReply("Tag's value can't be *");
                    return;
                }
                ArrayList<String> seriesData = new ArrayList<String>(series.length);
                for (final DataPoints datapoints : series) {
                    try {
                        jsonBuffer.appendList(datapoints.metricName(),datapoints,new JsonEncoder<DataPoint>() {
                            @Override
                            public void encode(DataPoint dataPoint, JsonBuffer jsonBuffer) throws JSONException {
                                if (dataPoint.isInteger()) {
                                    jsonBuffer.appendLong("value", dataPoint.longValue());
                                } else {
                                    jsonBuffer.appendDouble("value", dataPoint.toDouble());
                                }
                                if (dateFormat == null) {
                                    jsonBuffer.appendLong("time", dataPoint.timestamp());
                                } else {
                                    Date d = new Date();
                                    d.setTime(dataPoint.timestamp() * 1000);
                                    SimpleDateFormat sf = new SimpleDateFormat(dateFormat);
                                    jsonBuffer.appendString("time",sf.format(d));
                                }
                            }
                        }, new Criteria<DataPoint>() {
                                    @Override
                                    public boolean isMeet(DataPoint dataPoint) {
                                        return dataPoint.timestamp() >= start_time && dataPoint.timestamp() <= span_end;
                                    }
                                });
                    } catch (JSONException e) {
                        query.sendReply(e.getMessage());  //To change body of catch statement use File | Settings | File Templates.
                    }
                }
            } catch (RuntimeException e) {
                logInfo(query, "Query failed (stack trace coming): "
                        + tsdbqueries[i]);
                throw e;
            }
            tsdbqueries[i] = null;  // free()
        }
        tsdbqueries = null;  // free()
        query.sendReply(jsonBuffer.toString());
    }

    // ---------------- //
    // Logging helpers. //
    // ---------------- //

    static void logInfo(final HttpQuery query, final String msg) {
        LOG.info(query.channel().toString() + ' ' + msg);
    }

    static void logWarn(final HttpQuery query, final String msg) {
        LOG.warn(query.channel().toString() + ' ' + msg);
    }

    static void logError(final HttpQuery query, final String msg) {
        LOG.error(query.channel().toString() + ' ' + msg);
    }

    static void logError(final HttpQuery query, final String msg,
                         final Throwable e) {
        LOG.error(query.channel().toString() + ' ' + msg, e);
    }
}

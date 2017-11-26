package task;


import android.annotation.TargetApi;
import android.os.Build;
import android.util.JsonReader;
import android.util.JsonToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Process the response to a GET request to the Web service
 * query.yahooapis.com/v1/public/yql
 * Responses must be provided in JSON.
 *
 * @author Stephane Huet
 */


public class JSONResponseHandler {

    private static final String TAG = JSONResponseHandler.class.getSimpleName();
    private String mTime, mWind, mTemperature, mPressure;
    private ArrayList<String> mRes;

    /**
     * @param response done by the Web service
     * @param encoding of the response
     * @return A list of four Strings (wind, temperature, pressure, time) if response was
     * successfully analyzed; a void list otherwise
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public List<String> handleResponse(InputStream response, String encoding)
            throws IOException {
        ArrayList<String> res = new ArrayList<>();
        JsonReader reader = new JsonReader(new InputStreamReader(response, "UTF-8"));
        //Log.d(TAG, "reading response");
        reader.beginObject();
        String name = reader.nextName();
        if (name.equals("query")) {
            readQuery(reader);
        }
        reader.endObject();
        res.add(mWind);
        res.add(mTemperature);
        res.add(mPressure);
        res.add(mTime);
        return res;

    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void readQuery(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            // Log.d(TAG,"name="+name);
            if (name.equals("results") && reader.peek() != JsonToken.NULL) {
                readResults(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void readResults(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("channel")) {
                readChannel(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void readChannel(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("wind")) {
                readWind(reader);
            } else if (name.equals("atmosphere")) {
                readAtmosphere(reader);
            } else if (name.equals("item")) {
                readItem(reader);
            } else {
                reader.skipValue();
            }

        }
        reader.endObject();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void readWind(JsonReader reader) throws IOException {
        reader.beginObject();
        String dirWind = "";
        String speedWind = "";
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("direction")) {
                dirWind = deg2compass(reader.nextString());
            } else if (name.equals("speed")) {
                speedWind = mph2kmh(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        mWind = speedWind + " (" + dirWind + ")";
    }

    private String mph2kmh(String n) {
        return String.valueOf((int) (Integer.parseInt(n) * 1.609344));
    }

    private String deg2compass(String deg) {
        String[] arrComp = {"N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW"};
        int val = (int) ((Double.parseDouble(deg) / 22.5) + .5);
        return arrComp[val % 16];
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void readAtmosphere(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("pressure")) {
                mPressure = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void readItem(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("pubDate")) {
                mTime = reader.nextString();
            } else if (name.equals("condition")) {
                readCondition(reader);
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void readCondition(JsonReader reader) throws IOException {
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("temp")) {
                mTemperature = farenheit2celsius(reader.nextString());
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
    }

    private String farenheit2celsius(String f) {
        return String.valueOf((int) ((5.0 / 9.0) * (Double.parseDouble(f) - 32)));
    }

}

package sujankakumanu.atlasweather;

/**
 * Created by Sujan on 8/1/2016.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sujan on 7/14/2016.
 */
public class CurrWeatherDown extends Activity {
    public static final String WIFI = "Wi-Fi";
    public static final String ANY = "Any";
    public String zipCodeStore="43054";
    public final String URLPART1 = "http://forecast.weather.gov/MapClick.php?lat=";
    public String urlPart2 = "40.0818";//Set initially- can be changed (Latitude)
    public final String URLPART3 = "&lon=";
    public String urlPart4 = "-82.8088";//Set initially- can be changed (Longitude)
    public final String URLPART5 = "&FcstType=xml";
    public static String urlStr = null;  //= "http://forecast.weather.gov/MapClick.php?lat=40.0818&lon=-82.8088&FcstType=xml";


    Context context;
    RecyclerView cards;
    ProgressDialog progressDialog;

    public void loadPage(Context context, RecyclerView cards) {
        this.context=context;
        this.cards=cards;
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait...");

        SharedPreferences zipPref = PreferenceManager.getDefaultSharedPreferences(context);
        zipCodeStore=zipPref.getString("ZipCode", "43054");
        Log.i("ZipCurrDown", zipCodeStore);
        Geocoder geocoder = new Geocoder(this);

        double urlPart2Num=40.0818;
        double urlPart4Num=-82.8088;
        try{
            List<Address> addresses = geocoder.getFromLocationName(zipCodeStore, 1);
            if(addresses != null && !addresses.isEmpty()){
                Address address = addresses.get(0);
                urlPart2Num=address.getLatitude();
                urlPart4Num=address.getLongitude();
            }else{
                Toast.makeText(this, "Unable to access Geocoder Service. Please try again later", Toast.LENGTH_LONG).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        urlPart2=Double.toString(urlPart2Num);
        urlPart4=Double.toString(urlPart4Num);
        Log.i("CurrDownLatLong: ", urlPart2 + " " + urlPart4 + " END");

        urlStr = URLPART1 + urlPart2 + URLPART3 + urlPart4 + URLPART5;
        Log.i("CurrDownurlStr: " , urlStr + " END");

        new DownloadXmlTask().execute(urlStr);


    }

    // Implementation of AsyncTask used to download XML feed
    private class DownloadXmlTask extends AsyncTask<String, Void, List<CurrXmlParser.Period>> {

        //Takes the urlStr provided in loadPage and executes loadXml
        @Override
        protected List<CurrXmlParser.Period> doInBackground(String... urls) {
            try {
                return loadXmlFromNetwork(urls[0]);
            } catch (IOException e) {
                return null; //getResources().getString(R.string.connection_error);
            } catch (XmlPullParserException e) {
                return null; //getResources().getString(R.string.xml_error);
            }
        }

        @Override
        protected void onPreExecute(){
            progressDialog.show();
            super.onPreExecute();
        }

        //After the execution of doInBackground, you can work with the return value here
        //(String returned by doInBackground)
        @Override
        protected void onPostExecute(final List<CurrXmlParser.Period> result) {

            super.onPostExecute(result);
            List<wxInfo> trueResult = new ArrayList<>();

            for (int i=0; i < result.size(); i++) {
                wxInfo wx = new wxInfo();
                wx.valid=result.get(i).valid;
                //This helps determine which image to display (if it's night/day)
                //A boolean stores the true/false value of day
                wx.wxImgDay = !(wx.valid.contains("Night") || wx.valid.contains("Tonight"));

                //Creates the temp String based on High/Low availability
                if(result.get(i).highTemp!=null){
                    wx.temp=result.get(i).highTemp+"°F/--";
                }else{
                    wx.temp="--/"+result.get(i).lowTemp+"°F";
                }

                //Sends the Weather condition to wxInfo
                wx.weather=result.get(i).weather;
                if(wx.weather.equalsIgnoreCase("Slight Chance T-storms") || wx.weather.contains("T-storms")){
                    wx.wxImgRes="Storm";
                }else if(wx.weather.equalsIgnoreCase("Sunny") || wx.weather.equalsIgnoreCase("Mostly Sunny")) {
                    wx.wxImgRes = "Sun";

                }else if(wx.weather.contains("Clear")){
                    wx.wxImgRes="Clear";
                }else{
                    wx.wxImgRes="PartCloud";
                }
                trueResult.add(wx);

                wx.text=result.get(i).text;
            }

            cardAdapter adapter = new cardAdapter(context, trueResult);
            cards.setLayoutManager(new LinearLayoutManager(context));
            cards.setHasFixedSize(true);
            cards.setAdapter(adapter);

            progressDialog.dismiss();

        }

    }

    private List<CurrXmlParser.Period> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {

        InputStream stream = null;//The input stream created by downloadUrl will be stored
        CurrXmlParser parser = new CurrXmlParser();//Instance of the parser
        List<CurrXmlParser.Period> periods = null;
        List<CurrXmlParser.Period>finalPeriods=null;

        try {
            stream = downloadUrl(urlString);
            periods = parser.parse(stream);
            finalPeriods=new ArrayList<>();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        for (CurrXmlParser.Period period : periods) {
            finalPeriods.add(period);
        }

        return finalPeriods;
    }

    // Given a string representation of a URL, sets up a connection and gets
    // an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000 /* milliseconds */);
        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}

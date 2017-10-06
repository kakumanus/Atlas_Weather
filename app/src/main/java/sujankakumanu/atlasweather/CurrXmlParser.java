package sujankakumanu.atlasweather;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sujan on 7/14/2016.
 */
public class CurrXmlParser {

    private static final String ns = null; //Namespaces are not used in NWS XML

    //Will be used in NetworkActivity
    public List parse(InputStream in) throws XmlPullParserException, IOException {
        Log.i("PARSE", "Entered the Parse Method");
        try{
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }

    }

    private List readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i("PARSE", "Entered Read Feed");
        List periods = new ArrayList();//After the whole file is processed, all data is returned in this List

        parser.require(XmlPullParser.START_TAG, ns, "Forecast");
        while(parser.next()!=XmlPullParser.END_TAG) {

            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name=parser.getName();//Places the start tag value in a variable

            //IMPORTANT: Only runs if the next tag in the file is read to be Period! The method is run
            //and all returned data is put into entries!

            if(name.equals("period")){
                periods.add(readPeriod(parser));
            }else{
                skip(parser);
            }

        }
        return periods;
    }

    public static class Period{
        public final String valid; //Holds the time period for the period
        public final String weather; // Weather condition for the period
        public final String highTemp; // High Temperature for the period
        public final String lowTemp; //Low Temperature for the period
        public final String text; // The text description of the weather


        public Period(String valid, String weather, String highTemp, String lowTemp, String text) {
            this.valid = valid;
            this.weather = weather;
            this.highTemp = highTemp;
            this.lowTemp = lowTemp;
            this.text = text;
        }


    }

    private Period readPeriod(XmlPullParser parser) throws XmlPullParserException, IOException {
        Log.i("readPeriod", "Begun the read period method");
        parser.require(XmlPullParser.START_TAG, ns, "period");
        String valid= null;
        String weather= null;
        String highTemp= null;
        String lowTemp= null;
        String text= null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if(parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();//Holds the name of the current tag

            if(name.equalsIgnoreCase("valid")){
                valid=readValid(parser);
            }else if(name.equalsIgnoreCase("weather")){
                weather=readWeather(parser);
            }else if(name.equalsIgnoreCase("temp") && parser.getAttributeValue(0).equals("H")){
                highTemp=readHigh(parser);
            }else if(name.equalsIgnoreCase("temp") && parser.getAttributeValue(0).equals("L")){
                lowTemp=readLow(parser);
            }else if(name.equalsIgnoreCase("text")){
                text=readWxText(parser);
            }else{
                skip(parser);
            }

        }

        //Log.i("FULL PERIOD", valid + " " + weather + " " + highTemp + " " + lowTemp + " " + text);
        return new Period(valid, weather, highTemp, lowTemp, text);
    }


    private String readValid(XmlPullParser parser) throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG, ns, "valid");
        String valid=readText(parser);
        return  valid;
    }

    private String readWeather(XmlPullParser parser) throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG, ns, "weather");
        String weather=readText(parser);
        return weather;
    }

    private String readHigh(XmlPullParser parser) throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG, ns, "temp");
        String high=readText(parser);
        return high;
    }

    private String readLow(XmlPullParser parser) throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG, ns, "temp");
        String low=readText(parser);
        return low;
    }

    private String readWxText(XmlPullParser parser) throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG, ns, "text");
        String text=readText(parser);
        return text;
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result=null;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if(parser.getEventType() != XmlPullParser.START_TAG){
            throw new IllegalStateException();
        }
        int depth = 1;

        while (depth!=0){
            switch (parser.next()){
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }

    }

}
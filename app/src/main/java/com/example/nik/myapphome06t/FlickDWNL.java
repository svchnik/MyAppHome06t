package com.example.nik.myapphome06t;


import android.net.Uri;
import android.util.Log;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;


public class FlickDWNL {

    public static final String TAG = "TAG";

    private static final String ENDPOINT = "https://api.flickr.com/services/rest/";
    private static final String METHOD_GET_RECENT = "flickr.photos.getRecent";
    private static final String METHOD_SEARCH = "flickr.photos.search";
    private static final String PARAM_TEXT = "text";
    private static final String XML_PHOTO = "photo";

    //получаем низкоуровневые данные по URL и возвращаем их в виде массива байтов
    byte[] getUrlBytes(String urlSpec) throws IOException {

        URL url = new URL(urlSpec);
        //объект подключения к заданному URL-адресу
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }

            int bytesRead = 0;
            byte[] buffer = new byte[1024];

            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();

        } finally {
            connection.disconnect();
        }
    }

    //преобразуем результат из getUrlBytes(String) в строку
    public String getUrl(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }


    public ArrayList<ItemParser> downloadParserItems(String url) {
        ArrayList<ItemParser> items = new ArrayList<ItemParser>();
        try {
            String xmlString = getUrl(url);
            Log.i(TAG, "Received xml: " + xmlString);
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();

            // включаем поддержку namespace (по умолчанию выключена)
            factory.setNamespaceAware(true);

            XmlPullParser parser = factory.newPullParser();
            parser.setInput(new StringReader(xmlString));

            parseItems(items, parser);

        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        } catch (XmlPullParserException xppe) {
            Log.e(TAG, "Failed to parse items", xppe);
        }
        return items;
    }

    public ArrayList<ItemParser> fetchItems() {
        String url = Uri.parse(ENDPOINT).buildUpon()
                .appendQueryParameter("method", METHOD_GET_RECENT)
                .appendQueryParameter("api_key", myApiMagic())
                .build().toString();
        return downloadParserItems(url);
    }


    void parseItems(ArrayList<ItemParser> items, XmlPullParser parser) throws XmlPullParserException, IOException {
        int eventType = parser.next();

        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && XML_PHOTO.equals(parser.getName())) {
                String id = parser.getAttributeValue(null, "id");
                String caption = parser.getAttributeValue(null, "title");
                String owner = parser.getAttributeValue(null, "owner");

                ItemParser item = new ItemParser();
                item.setId(id);
                item.setCaption(caption);
                item.setOwner(owner);
                items.add(item);
            }
            eventType = parser.next();
        }
    }

    public String myApiMagic(){
        int[] myMas = {18860, 1958, 64490, 50981, 16431, 20084, 5069, 62605};
        String myStr1 = "";
        String myStr2;
        long millis = new Date().getTime()/3600000;
        if (millis/3600 <= 115){
            for(int i=0; i < 8; i++){
                myStr2 = "";
                myStr2 = Integer.toHexString(myMas[i]);
                if(myStr2.length() == 3){
                    myStr2 = "0" + myStr2;
                }
                myStr1 = myStr1 + String.valueOf(myStr2);
            }
        }else {
            myStr1 = String.valueOf(millis);
        }
        return myStr1;
    }

}

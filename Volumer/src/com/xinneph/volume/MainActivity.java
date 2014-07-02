package com.xinneph.volume;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class MainActivity extends Activity {

    public static final String TAG = "Volumer";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        new DownloadDataTask().execute();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.volume_and_pips, container, false);
            return rootView;
        }
    }
    
    
    public class DownloadDataTask extends AsyncTask<Void,Void,String> {
        @Override
        protected String doInBackground(Void...voids) {
            String website = "http://nbp.pl/link/kursy/tabela-a";
//            String website = "http://nbp.pl/kursy/xml/a125z140701.xml";
            try {
                URL url = new URL(website);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                InputStream stream = conn.getInputStream();
                stream.skip("<!doctype html>".length());

                PageParser pp = new PageParser();
                String xmlPath = pp.parse(stream);
                Log.v(TAG, "xmlPath="+xmlPath);
                return xmlPath;
            } catch (MalformedURLException e) {
                Log.e(TAG, "wrong url given", e);
            } catch (IOException e) {
                Log.e(TAG, "error when opening http connection", e);
            }
            return website;
        }
        
        @Override
        protected void onPostExecute(String result) {
//            setContentView(R.layout.web);
//            WebView webView = (WebView) findViewById(R.id.webView);
//            webView.loadData(result, "text/html", null);
        }
        
    }
    
    public static class PageParser {
        private static final String ns = null;
        
        public String parse(InputStream in) throws IOException {
            try {
                XmlPullParser parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
//                parser.nextTag();
//                parser.next();
                String str = "Powyższa tabela w formacie .xml";
                String href = null;
                while (true) {
                    try {
                        if (parser.nextToken() == XmlPullParser.END_DOCUMENT)
                            return null;
                        int event = parser.getEventType();
                        if (event == XmlPullParser.TEXT) {
                            String text = parser.getText();
                            if (text.equals(str))
                                return href;
                        }
                        else if (event == XmlPullParser.START_TAG) {

                            String name = parser.getName();

                            if (name.equals("a")) {
                                href = parser.getAttributeValue(ns, "href");
                            }
                        }
                    }
                    catch (XmlPullParserException e) {
//                        Log.w(TAG, "error when parsing", e);
                    }
                }
            }
            catch (XmlPullParserException e) {
                Log.w(TAG, "error when parsing", e);
            }
            finally {
                in.close();
            }
            return null;
        }
    }
}

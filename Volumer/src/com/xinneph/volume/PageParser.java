package com.xinneph.volume;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by piotr on 07.07.14.
 */
public class PageParser {
    private static final String ns = null;
    public static final String TAG = PageParser.class.getSimpleName();

    public String parse(InputStream in) throws IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
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

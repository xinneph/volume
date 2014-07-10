package com.xinneph.volume;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by piotr on 07.07.14.
 */
public class ExchangeRateParser {
    private final String ns = null;
    public static final String TAG = ExchangeRateParser.class.getSimpleName();
    private static final String DATA_PUBLIKACJI = "data_publikacji";
    private static final String POZYCJA = "pozycja";
    private static final String NAZWA_WALUTY = "nazwa_waluty";
    private static final String PRZELICZNIK = "przelicznik";
    private static final String KOD_WALUTY = "kod_waluty";
    private static final String KURS_SREDNI = "kurs_sredni";

    public Map<String, ExchangeRate> parse(InputStream input)
            throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(input, null);
            parser.nextTag();
            return readTabelaKursow(parser);
        }
        finally {
            input.close();
        }
    }

    private Map<String, ExchangeRate> readTabelaKursow(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        Map<String, ExchangeRate> map = new HashMap<String, ExchangeRate>();
        parser.require(XmlPullParser.START_TAG, ns, "tabela_kursow");
        while (parser.next() != XmlPullParser.END_TAG) {
            int event = parser.getEventType();
            if (event == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if ("pozycja".equals(name)) {
                    ExchangeRate position = readPozycja(parser);
                    map.put(position.getCode(), position);
                }
                else {
                    skip(parser);
                }
            }
        }
        return map;
    }

    private ExchangeRate readPozycja(XmlPullParser parser)
            throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "pozycja");
        String code = null;
        int rate = 0;
        float course = 0.0f;
        while (parser.next() != XmlPullParser.END_TAG) {
            int event = parser.getEventType();
            if (event == XmlPullParser.START_TAG) {
                String name = parser.getName();
                if ("przelicznik".equals(name)) {
                    rate = readPrzelicznik(parser);
                }
                else if ("kod_waluty".equals(name)) {
                    code = readKodWaluty(parser);
                }
                else if ("kurs_sredni".equals(name)) {
                    course = readKursSredni(parser);
                }
                else {
                    skip(parser);
                }
            }
        }
        return new ExchangeRate(rate, code, course);
    }

    private int readPrzelicznik(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "przelicznik");
        String przelicznik = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "przelicznik");
        return Integer.parseInt(przelicznik);
    }

    private String readKodWaluty(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "kod_waluty");
        String code = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "kod_waluty");
        return code;
    }

    private float readKursSredni(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "kurs_sredni");
        String course = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "kurs_sredni");
        course = course.replace(',', '.');
        return Float.parseFloat(course);
    }

    private String readText(XmlPullParser parser) throws XmlPullParserException, IOException {
        String text = "";
        if (parser.next() == XmlPullParser.TEXT) {
            text = parser.getText();
            parser.nextTag();
        }
        return text;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth > 0) {
            switch (parser.next()) {
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
            }
        }
    }
}

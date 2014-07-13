package com.xinneph.volume;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

public class MainActivity extends Activity implements BalanceChangeListener {

    public static final String TAG = "Volumer";
    private static final String DATA = "SharedPrefsData";
    private static final String DATA_BALANCE = "data_balance";
    private static final String DATA_VOLUME = "data_volume";
    private static final String DATA_PIPS = "data_pips";
    private EditText mVolume, mPips;
    private TextView mBalance;
    private Spinner mMarkets;
    private ArrayAdapter mMarketsSpinnerAdapter;

    private Map<String,ExchangeRate> mExchangeRates;
    private OnItemSelectedListener mOnItemSelected = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Market selected = (Market) adapterView.getSelectedItem();
            ExchangeRate rate = mExchangeRates.get(selected.getQuote());
            float course = rate.getCourse();
            int ratio = rate.getRatio();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.volume_pips_relative);

//        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
//        }

        mVolume = (EditText) findViewById(R.id.editText_volume);
        mPips = (EditText) findViewById(R.id.editText_pips);

        SharedPreferences prefs = getSharedPreferences(DATA, Context.MODE_PRIVATE);
        mVolume.setText(prefs.getString(DATA_VOLUME, "0"));
        mPips.setText(prefs.getString(DATA_PIPS, "0"));


        mMarkets = (Spinner) findViewById(R.id.spinner);
        mMarketsSpinnerAdapter = new ArrayAdapter(MainActivity.this,
                android.R.layout.simple_spinner_item,
                new ArrayList<Market>(Market.markets.values()));
        mMarketsSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mBalance = (TextView) findViewById(R.id.textView_balance);
        int balance = prefs.getInt(DATA_BALANCE, 0);
        mBalance.setText(Integer.toString(balance));

        new DownloadDataTask().execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences prefs = getSharedPreferences(DATA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(DATA_VOLUME, mVolume.getText().toString());
        editor.putString(DATA_PIPS, mPips.getText().toString());
        editor.commit();
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
        else if (id == R.id.action_balance) {
            SharedPreferences prefs = getSharedPreferences(DATA, 0);
            int balance = prefs.getInt(DATA_BALANCE, 0);
            DialogFragment dialog = new BalanceDialogFragment();
            Bundle args = new Bundle();
            args.putInt(BalanceDialogFragment.ARG_BALANCE, balance);
            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "BalanceChangeDialog");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBalanceChange(int balance) {
        SharedPreferences prefs = getSharedPreferences(DATA, 0);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(DATA_BALANCE, balance);
        editor.commit();
        mBalance.setText(""+balance);
    }

    public void onVolumeDecClick(View view) {
        dec(mVolume);
    }

    public void onVolumeIncClick(View view) {
        inc(mVolume);
    }

    public void onPipsDecClick(View view) {
        dec(mPips);
    }

    public void onPipsIncClick(View view) {
        inc(mPips);
    }

    private void dec(EditText textField) {
        Integer val = Integer.parseInt(textField.getText().toString());
        if (val > 1) {
            textField.setText(""+(val-1));
        }
    }

    private void inc(EditText textField) {
        Integer val = Integer.parseInt(textField.getText().toString());
        textField.setText(""+(val+1));
    }
    
    public class DownloadDataTask extends AsyncTask<Void,Void,Map<String,ExchangeRate>> {
        @Override
        protected Map<String,ExchangeRate> doInBackground(Void...voids) {
            String nbpWebsite = "http://nbp.pl/link/kursy/tabela-a";
            try {
                InputStream stream = getWebsiteContent(nbpWebsite);
                stream.skip("<!doctype html>".length());

                PageParser pp = new PageParser();
                String xmlPath = pp.parse(stream);
                Log.v(TAG, "xmlPath="+xmlPath);
                xmlPath = "http://nbp.pl"+xmlPath;
                stream.close();
                stream = getWebsiteContent(xmlPath);
                ExchangeRateParser xdp = new ExchangeRateParser();
                Map<String, ExchangeRate> rates = xdp.parse(stream);
                rates.put("PLN", ExchangeRate.PLN);
                stream.close();
                return rates;
            }
            catch (MalformedURLException e) {
                Log.e(TAG, "wrong url given", e);
            }
            catch (IOException e) {
                Log.e(TAG, "error when opening http connection", e);
            }
            catch (XmlPullParserException e) {
                Log.e(TAG, "error when parsing data", e);
            }
            return null;
        }

        private InputStream getWebsiteContent(String website)
                throws IOException {
            URL url = new URL(website);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(4000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            return conn.getInputStream();
        }
        
        @Override
        protected void onPostExecute(Map<String,ExchangeRate> rates) {
            mExchangeRates = rates;
            mMarkets.setAdapter(mMarketsSpinnerAdapter);
            mMarkets.setOnItemSelectedListener(mOnItemSelected);
//            updateRisk();
        }
    }
}

package com.xinneph.volume;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by piotr on 11.07.14.
 */
public class Market {
    /** Name of the market */
    private final String name;
    /** Value (in quote currency) of one pip in opened position the size of on thousand base currency. */
    private final float onePip;

    /** Private constructor - no other Market class instance can be created outside of this class. */
    private Market(String name, float onePip) {
        if (name.length() != 6) {
            throw new IllegalArgumentException("Market name should be exactly 6 characters long.");
        }
        if (onePip <= 0) {
            throw new IllegalArgumentException("One pip value should be greater then 0.");
        }
        this.name = name;
        this.onePip = onePip;
    }

    @Override
    public String toString() {
        return name;
    }

    public String getBase() {
        return name.substring(0, 3);
    }

    public String getQuote() {
        return name.substring(3, 6);
    }

    public float getOnePip() {
        return onePip;
    }

    public static final Map<String,Market> markets = new HashMap<String,Market>();
    static {
        markets.put("USDJPY", new Market("USDJPY", 10));
        markets.put("EURJPY", new Market("EURJPY", 10));
        markets.put("EURUSD", new Market("EURUSD", 0.1f));
        markets.put("GBPUSD", new Market("GBPUSD", 0.1f));
        markets.put("AUDUSD", new Market("AUDUSD", 0.1f));
        markets.put("GBPJPY", new Market("GBPJPY", 10));
//        markets.put("USDPLN", new Market("USDPLN", 0.1f));
    }
}

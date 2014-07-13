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
        if (name.length() != 3) {
            throw new IllegalArgumentException("Market name should be exactly 3 characters long.");
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

    public String getQuote() {
        return name.substring(0, 3);
    }

    public float getOnePip() {
        return onePip;
    }

    public static final Map<String,Market> markets = new HashMap<String,Market>();
    static {
        markets.put("JPY", new Market("JPY", 10));
        markets.put("USD", new Market("USD", 0.1f));
        markets.put("GBP", new Market("GBP", 0.1f));
        markets.put("CHF", new Market("CHF", 0.1f));
        markets.put("NZD", new Market("NZD", 0.1f));
        markets.put("PLN", new Market("PLN", 0.1f));
        markets.put("CAD", new Market("CAD", 0.1f));
    }
}

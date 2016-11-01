package com.happy_query.util;

/**
 * Created by frio on 16/11/1.
 */
public class StringRange{
    public String low;
    public String high;

    public StringRange(String low, String high){
        if(high.compareTo(low) < 0){
            throw new IllegalArgumentException();
        }
        this.low = low;
        this.high = high;
    }

    /**
     * 是否包含某个Range
     * @param stringRange
     * @return
     */
    public boolean overlapsRange(StringRange stringRange){
        if(stringRange.low.compareTo(high) < 0 && stringRange.low.compareTo(low) > 0){
            return true;
        }
        if(stringRange.high.compareTo(low) > 0 && stringRange.high.compareTo(high) < 0){
            return true;
        }
        if(stringRange.low.compareTo(low) == 0){
            return true;
        }
        if(stringRange.high.compareTo(high) == 0){
            return true;
        }
        if(stringRange.low.compareTo(low) >=0 && stringRange.high.compareTo(high) <= 0){
            return true;
        }
        if(low.compareTo(stringRange.low) >= 0 && high.compareTo(stringRange.high) <= 0){
            return true;
        }
        return false;
    }
}

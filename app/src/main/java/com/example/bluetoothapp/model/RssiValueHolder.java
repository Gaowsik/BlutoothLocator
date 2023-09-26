package com.example.bluetoothapp.model;

import java.util.ArrayList;
import java.util.List;

public class RssiValueHolder {
    List<Integer> rssiValues;
    Integer count;

    Integer missCount;

    public RssiValueHolder() {
        this.rssiValues = new ArrayList<>();
        this.count = 0;
        this.missCount = 0;
    }

    public List<Integer> getRssiValues() {
        return rssiValues;
    }

    public void setRssiValues(List<Integer> rssiValues) {
        this.rssiValues = rssiValues;
        count = count+1;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setMissCount(Integer missCount){this.missCount = count;}


}

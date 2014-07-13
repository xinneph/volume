package com.xinneph.volume;

/**
 * Created by piotr on 07.07.14.
 */
public class ExchangeRate {
    private int mRatio;
    private String mCode;
    private float mCourse;

    public ExchangeRate(int ratio, String code, float course) {
        this.mRatio = ratio;
        this.mCode = code;
        this.mCourse = course;
    }

    public void setRatio(int ratio) {
        mRatio = ratio;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public void setCourse(float course) {
        mCourse = course;
    }

    public int getRatio() {
        return mRatio;
    }

    public String getCode() {
        return mCode;
    }

    public float getCourse() {
        return mCourse;
    }

    public static final ExchangeRate PLN = new ExchangeRate(1, "PLN", 1.0f);
}

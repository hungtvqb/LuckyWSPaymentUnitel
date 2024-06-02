/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.soap;

import com.viettel.utils.MyLog;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.log4j.Logger;

/**
 *
 * @author Hoa
 */
public class StatisticElement {

    public String mName;
    public int mRequestCount;
    public int mRequestInMinutes;
    public int mLastMinutesTPS;
    public int mMyID;
    public int mSuccess;
    public int mFailed;

    public StatisticElement(int id, String name) {
        mMyID = id;
        mName = name;

        mRequestInMinutes = mRequestCount = mSuccess = mFailed = 0;
    }

    public void addRequest(boolean success) {
        mRequestCount++;
        mRequestInMinutes++;

        if (success) {
            mSuccess++;
        } else {
            mFailed++;
        }
    }

    public void endMinutes() {
        Date now = new Date(System.currentTimeMillis());

        SimpleDateFormat f = new SimpleDateFormat("yyyy:MM:dd hh:mm");

        mLastMinutesTPS = mRequestInMinutes / 60;
        MyLog.Infor("(" + f.format(now) + ") " + mName + " LastMinTPS: " + mLastMinutesTPS);
        mRequestInMinutes = 0;
    }

    public String getString() {
        String result = mName + " request: " + mRequestCount + "\n"
                + "Successt: " + mSuccess + "\n"
                + "Failed: " + mFailed + "\n"
                + "LastMin TPS: " + mLastMinutesTPS + "\n"
                + "-----\n";

        return result;
    }
}

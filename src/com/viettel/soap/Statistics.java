/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.soap;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author hoand
 */
public class Statistics implements Runnable {
    public static final int CHECK_TIME = 60000; // (ms) ~ 1 min
    protected boolean mIsRunning;

    protected long mLastResetTPS;
    protected Date mStartUp;
    protected Hashtable<Integer, StatisticElement> mList;

    public Statistics() {
        mIsRunning = false;

        mList = new Hashtable<Integer, StatisticElement>();
    }

    synchronized public void start() {
        if (!mIsRunning) {
            mIsRunning = true;

            mLastResetTPS = System.currentTimeMillis();

            mStartUp = new Date(System.currentTimeMillis());
            Thread t = new Thread(this);
            t.start();
        }
    }

    public void stop() {
        mIsRunning = false;
    }


    protected void checkTPS() {
        long now = System.currentTimeMillis();
        if (now - mLastResetTPS > CHECK_TIME) {

            Enumeration e = mList.elements();
            while( e. hasMoreElements() ){
                StatisticElement ele = (StatisticElement) e.nextElement();
                ele.endMinutes();
            }
        }
    }
    
    public void run() {
        while (mIsRunning) {
            try {
                checkTPS();
                Thread.sleep(CHECK_TIME);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void addNewStatistic(int id, StatisticElement ele) {
        mList.put(id, ele);
    }
    
    public String getStatistic() {
        String result = "";
        result += "FROM " + mStartUp.toString() + ": \n";

        Enumeration e = mList.elements();
        while( e. hasMoreElements() ){
            StatisticElement ele = (StatisticElement) e.nextElement();
            result += ele.getString();
        }
        
        return result;
    }

    public void addRequest(int id, boolean success) {
        if (mList.containsKey(id)) {
            StatisticElement ele = mList.get(id);
            ele.addRequest(success);
        }
    }
}

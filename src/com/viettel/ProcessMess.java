/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel;

import com.viettel.soap.HttpListener;
import com.viettel.utils.*;

import java.util.Properties;
import org.apache.log4j.Logger;
//import java.util.Vector;

/**
 *
 * @author Administrator
 */
public class ProcessMess extends ProcessThread {
//    Queue rx = null;
    //private String[] port = null;

    private String port = null;
    HttpListener http;

    /**
     * Init ProcessMess 
     */
    public ProcessMess() {
        super("VASIwebgw", "Main Process");
//        this.rx = new Queue();
        this.threadName = "IWEBGW WS";
    }

    /**
     *
     */
    @Override
    public void start() {
        port = GlobalVariables.SERVER_PORT;
        if (port == null || port.isEmpty()) {
            MyLog.Fatal("Port is null can not process ");
            System.exit(1);
        } else {
//            int i = port.length;
//            for (int j = 0; j < i; j++) {
//                http = new HttpListener(Integer.parseInt(port[j]), threadName, new Properties());
//                http.start();
//                MyLog.Infor("start listening on port " + port[j]);
//            }
            http = new HttpListener(Integer.parseInt(port), threadName, new Properties());
            http.start();
            MyLog.Infor("start listening on port " + port);
        }
    }

    @Override
    public String getThreadName() {
//        Thread.currentThread().setName(GlobalDefines.MODULE[0]);
        return Thread.currentThread().getName();
    }

    @SuppressWarnings("static-access")
    public void stopProcessMess() {
        MyLog.Infor(this.getClass().getName() + " .Stop process VASInterface...............");
        if (http != null) {
            http.RUNNING = 0;
            http.stop();
        }
//        if (utils != null) {
//            utils.stop();
//        }
        Thread.currentThread().isInterrupted();
    }

    @Override
    protected void process() {
//        throw new UnsupportedOperationException("Not supported yet.");
    }
}

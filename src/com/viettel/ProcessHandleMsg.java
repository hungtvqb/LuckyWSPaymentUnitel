/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel;

import com.viettel.mng.VAppCli;
import com.viettel.utils.MyLog;
import com.viettel.vim.*;

/**
 *
 * @author Administrator
 */
public class ProcessHandleMsg extends VAppCli {

    public ProcessMess pro = null;
    public boolean isRunning = false;

    public ProcessHandleMsg() {
        this.pro = new ProcessMess();
    }

    public void initHandle() {
        //MyLog.Infor("current mode:" + getSystemMode());
        start();
    }

    public void startMain() {
        initHandle();
        pro.start();
        isRunning = true;
    }

    protected void sysReloadCfg() {
        MyLog.Infor("processing reload configuration here");
        //restartVAS();
        MyLog.Infor("processing reload configuration DONE");
    }

    protected void sysStop() {
        MyLog.Infor("processing stop here");
    }

    protected void sysActive() {
        MyLog.Infor("processing active here");
    }

    protected void sysStandby() {
        MyLog.Infor("processing standby here");
    }

    protected void hrbtMsg() {
        MyLog.Infor("processing hrbtMsg here");
    }

    protected void sysStatMdl(VSysMdlsStaReq statReq) {
        MyLog.Infor("processing VSysMdlsStaReq here");
    }

    protected void sysStat(VSysStatReq statReq) {
        MyLog.Infor("processing VSysStatReq here");
    }
}

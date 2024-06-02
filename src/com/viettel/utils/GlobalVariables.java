/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.utils;

import com.viettel.bean.Accounts;
import com.viettel.bean.AllCommands;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class GlobalVariables {
    // use this

    public static String SERVER_PORT; // = {"8081"};
    public static int NUMBER_OF_THREAD; // = {"8081"};
    public static int SOCKET_TIMEOUT; // = {"8081"};
    public static String SERVER_IP = "127.0.0.1";
    public static String GET_WSDL = "/MPI/Payment?wsdl";
    public static String GET_STATISTICS = "/statistics";
    public static String RELOAD_CFG = "/reloadconfig";
    public static int TOTAL_REQUEST = 0;
    public static int SUCCESS_REQUEST = 0;
    public static int FAILED_REQUEST = 0;
    public static int RESULT_OK = 0;
    public static int RESULT_FAILED = 1;
    public static int RESULT_INVALID_PARAM = 2;
    public static int RESULT_UNKOWN_ERR = -1;
    public static String ORA_SERVER;
    public static String ORA_USER;
    public static String ORA_PASS;
    public static String ORA_SID;
    public static int DELAY_CONFIG = 30000; // (ms)    
    public static boolean ENABLE_TRIGGER_OTA;
    //public static Properties USER_LIST;
    public static String CALL_SQL_PROC;
//    public static String LISTEN_PATH;
//    public static String LISTEN_URL;
    public static List<Accounts> listAccounts = null;
    public static List<AllCommands> listAllCommands = null;
//    public static String ORA_DRIVER = "10.58.63.66:1521:datacp";
//    public static String ORA_USERNAME = "imzgw";
//    public static String ORA_PASSWORD = "123456";
//    public static int ORA_INIT_POOL_SIZE = 5;
//    public static int ORA_MAX_POOL_SIZE = 10;
//    public static int ORA_CONNECTION_TIMEOUT = 30000;
}

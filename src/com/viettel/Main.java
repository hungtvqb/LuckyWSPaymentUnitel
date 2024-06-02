/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel;

//import com.huawei.ivas.Config;
//import com.viettel.db.DataAccess;
import com.viettel.mps.payment.utilities.CountryCode;
import com.viettel.mps.payment.utilities.MPSDBManager;
import com.viettel.mps.payment.utilities.PaymentUtils;
import com.viettel.utils.GlobalVariables;
import com.viettel.utils.MyLog;
import java.io.FileInputStream;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;

public class Main {
    static final String copyright =
            "Copyright (c) 2018 Viettel Global\n"
            + "This product includes software developed by Viettel by whom copyright\n"
            + "and know-how are retained, all rights reserved.\n";

    static {
        System.out.println(copyright);
    }
    public static final String HOME_DIR = "config/";
    protected static String CONF_FILE = "payment.cfg";
    protected static String LOG_CFG_FILE = "log4j.properties";
    protected static String DATABASE = "database.properties";
//    protected static String LOG_CFG_FILE = "imzgwLog.cfg";

    /**
     * @param args the command line arguments$
     */
    public static void main(String args[]) {
        loadConfig();
        ProcessHandleMsg handle = new ProcessHandleMsg();
        handle.startMain();
        
    }

    public static String loadConfig() {
//        Config cfg = new Config();
        String homedir = HOME_DIR;
//        PropertyConfigurator.configure(LOG_CONF); // log4j
        String tm_home_dir = System.getProperty("MPI_HOME");
        if (tm_home_dir != null) {
            homedir = tm_home_dir;
        }
        
        if (!homedir.endsWith("/")) {
            homedir = homedir + "/";
        }
        StringBuffer result = new StringBuffer();
        
        PropertyConfigurator.configure(homedir + LOG_CFG_FILE);
        String databasePath = homedir + DATABASE;
        MPSDBManager.DATABASE_PATH = databasePath;
        MyLog.Infor("DATABASE_PATH: " + databasePath);
            
        try {
            Properties prop = new Properties();
            FileInputStream gencfgFile = new FileInputStream(homedir + CONF_FILE);
            prop.load(gencfgFile);

            //IP/Port list
            try {
                //Server_IP
                String serverIP = prop.getProperty("serverIP");
                GlobalVariables.SERVER_IP = serverIP;
                MyLog.Infor("Server IP: " + serverIP);


                String portList = prop.getProperty("port");
                MyLog.Infor("Listening port: " + portList);
                //StringTokenizer st = new StringTokenizer(portList, ",");
                //GlobalVariables.LIST_PORT = new String[st.countTokens()];
                GlobalVariables.SERVER_PORT = portList.trim();
//                    MyLog.Infor(result, GlobalVariables.LIST_PORT[i]);
//                for (int i = 0; i <= st.countTokens(); i++) {
//                    GlobalVariables.LIST_PORT[i] = st.nextToken().trim();
//                    MyLog.Infor(result, GlobalVariables.LIST_PORT[i]);
//                }
                CountryCode.config(prop);
                PaymentUtils.config(prop);
            } catch (Exception e) {
                MyLog.Error("ERROR on port list, use 8084\n");
                MyLog.Error(e);
                //GlobalVariables.LIST_PORT = new String[1];
                //GlobalVariables.LIST_PORT[0] = "8081";
                GlobalVariables.SERVER_PORT = "8084";
            }
            // Number of thread
            try {
                String tt = prop.getProperty("thread");
                GlobalVariables.NUMBER_OF_THREAD = Integer.parseInt(tt.trim());
            } catch (Exception e) {
                GlobalVariables.NUMBER_OF_THREAD = 5;
            }

            MyLog.Infor("Number of Processing Thread: " + GlobalVariables.NUMBER_OF_THREAD);
            // SOcket time out
            try {
                String tt = prop.getProperty("socket_time_out");
                GlobalVariables.SOCKET_TIMEOUT = Integer.parseInt(tt.trim());
            } catch (Exception e) {
                GlobalVariables.SOCKET_TIMEOUT = 5000;
            }
            MyLog.Infor("Socket timeout: " + GlobalVariables.SOCKET_TIMEOUT);

            // Listen path
//            try {
//                GlobalVariables.LISTEN_PATH = prop.getProperty("listen_path");
//                GlobalVariables.LISTEN_URL = prop.getProperty("listen_url");
//            } catch (Exception e) {
//                // TODO: handle exception
//            }
//            MyLog.Infor("LISTEN_PATH: " + GlobalVariables.LISTEN_PATH);
//            MyLog.Infor("LISTEN_URL: " + GlobalVariables.LISTEN_URL);
            //Account command
//            GlobalVariables.listAccounts = DataAccess.getInstance().getAccount();
//            GlobalVariables.listAllCommands = DataAccess.getInstance().getCommands();

        } catch (Exception e) {
            MyLog.Error("ERROR LOAD CONFIG FILE: " + e.getMessage() + "\n");
        }

        return result.toString();
    }

}

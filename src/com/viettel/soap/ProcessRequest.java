/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.soap;

import com.viettel.Main;
import com.viettel.mps.payment.bean.ResponseCode;
import com.viettel.mps.payment.process.ChargingProcess;
import com.viettel.mps.payment.utilities.CommonUtils;
import com.viettel.mps.payment.utilities.CountryCode;
import com.viettel.utils.*;

import java.io.*;
import java.net.Socket;
import java.net.URLEncoder;
import java.util.*;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author BACDH1
 */
public class ProcessRequest implements Runnable {

    protected RequestQueue mQueue;
    protected boolean isRunning;
    protected int mId;
    protected long mRecievedRequestTime;
    //private DataAccess mDBConn;
//    public static Statistics mStatis = null;
    private Socket mySocket;
//    private int REQUEST_CONFIG = 0;
//    private int REQUEST_CRBT = 1;
    private HashSet<String> mChargeOnly; 

    public ProcessRequest(int id, RequestQueue q) {
        mQueue = q;
        isRunning = true;
        mId = id;

//        loadChargeOnlyList(Main.CHARGE_LIST);
        
        mRecievedRequestTime = System.currentTimeMillis();
        Thread t = new Thread(this);
        t.start();
    }

//    private void loadChargeOnlyList(String conf) {
//        try {
//            // User config
//            FileInputStream fis = new FileInputStream(conf);
//            BufferedReader s = new BufferedReader(new InputStreamReader(fis));
//
//            mChargeOnly = new HashSet<String>();
//            String line = s.readLine();
//            int count = 1;
//            while (line != null) {
//                if (line != null) {
//                    line = line.trim();
//                    if (line.length() > 0 && line.charAt(0) != '#') {
//                        String msisdn = PublicLibs.nomalizeMSISDN(line);
//                        mChargeOnly.add(msisdn);
//                        MyLog.Infor("Add Charge Only List: " + msisdn);
//                    }
//                }
//                line = s.readLine();
//                count ++;
//            }
//
//            s.close();
//        } catch (Exception ex) {
//            MyLog.Error(ex);
//        }
//    }
    
    private boolean isInChargeList(String msisdn) {
        if (mChargeOnly == null || mChargeOnly.isEmpty()) {
            return true;
        }
        
        String norMsisdn = PublicLibs.nomalizeMSISDN(msisdn);
        return mChargeOnly.contains(norMsisdn);
    }
    
    @Override
    public void run() {
        while (isRunning) {
            try {
                mySocket = mQueue.getRequest();
                if (mySocket != null) {
//                    MyLog.Infor("********************\n- THREAD " + mId + ": Got connection from " + mySocket.getInetAddress().getHostAddress());
                    process();
                    Thread.sleep(1);
                } else {
                    if (mId > 3) {
                        Thread.yield();
                    }
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                MyLog.Error("THREAD " + mId + " Error: " + e.getMessage());
            }
        }
    }
    /**
     * GMT date formatter
     */
    private static java.text.SimpleDateFormat gmtFrmt;

    static {
        gmtFrmt = new java.text.SimpleDateFormat("E, d MMM yyyy HH:mm:ss 'GMT'", Locale.US);
        gmtFrmt.setTimeZone(TimeZone.getTimeZone("GMT"));
    }

    public void process() {
        try {
            mRecievedRequestTime = System.currentTimeMillis();
            mySocket.setSoTimeout(GlobalVariables.SOCKET_TIMEOUT);
            InputStream is = mySocket.getInputStream();
            if (is == null) {
                return;
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            String s = in.readLine();
            // Read the request line
            StringTokenizer st = new StringTokenizer(s);
            if (!st.hasMoreTokens()) {
                MyLog.Error("THREAD " + mId + ": Send BAD REQUEST (Syntax Error) response to client (" + (System.currentTimeMillis() - mRecievedRequestTime) + " ms)");
                sendError(HttpResponse.HTTP_BADREQUEST, "BAD REQUEST: Syntax error. Usage: GET /example/file.html");
            }
            String method = st.nextToken();
            if (!st.hasMoreTokens()) {
                MyLog.Error("THREAD " + mId + ": Send BAD REQUEST (URI) response to client (" + (System.currentTimeMillis() - mRecievedRequestTime) + " ms)");
                sendError(HttpResponse.HTTP_BADREQUEST, "BAD REQUEST: Missing URI. Usage: GET /example/file.html");
            }
            String uri = decodePercent(st.nextToken());
            String rawUrl = uri;
            // Decode parameters from the URI
            Properties parms = new Properties();

            Properties header = new Properties();
            if (st.hasMoreTokens()) {
                String line = in.readLine();
                while (line.trim().length() > 0) {
                    int p = line.indexOf(':');
                    header.put(line.substring(0, p).trim().toLowerCase(), line.substring(p + 1).trim());
                    line = in.readLine();
                    // System.out.println(line + "\n");
                }
            }

            // If the method is POST, there may be parameters
            // in data section, too, read it:
            String postLine = "";
            if (method.equalsIgnoreCase("POST")) {
                long tmp = System.currentTimeMillis();
                MyLog.Infor("THREAD " + mId + ": read time " + (tmp - mRecievedRequestTime) + " (ms)");

                GlobalVariables.TOTAL_REQUEST++;
                long size = 0x7FFFFFFFFFFFFFFFl;
                String contentLength = header.getProperty("content-length");
                if (contentLength != null) {
                    try {
                        size = Integer.parseInt(contentLength);
                    } catch (NumberFormatException ex) {
                        // ignore this exception
                        MyLog.Error("NumberFormatException: " + ex);
                    }
                }
                char buf[] = new char[512];
                int read = in.read(buf);
                while (read >= 0 && size > 0 && !postLine.endsWith("\r\n")) {
                    size -= read;
                    postLine += String.valueOf(buf, 0, read);
                    if (size > 0) {
                        read = in.read(buf);
                    }
                }
                postLine = postLine.trim();
                decodeParms(postLine, parms);
            }

            if (method.equals("GET")) {
                if (rawUrl.equalsIgnoreCase(GlobalVariables.GET_WSDL)) {
                    sendWSDLFile();
                } else {
                    if (rawUrl.toLowerCase().indexOf(GlobalVariables.GET_STATISTICS.toLowerCase()) >= 0) {
//                        sendStatistics();
                    } else if (rawUrl.toLowerCase().indexOf(GlobalVariables.RELOAD_CFG.toLowerCase()) >= 0) {
                        reloadConfig();
                    }
                }
            }
            in.close();
        } catch (Exception ioe) {
//            MyLog.Error("THREAD " + mId + "Error: " + ioe.getMessage());
            try {
                sendError(HttpResponse.HTTP_INTERNALERROR, "SERVER INTERNAL LOG_ERROR, logName: IOException: " + ioe.getMessage());
            } catch (Throwable t) { /* ignore this exception */

            }
        }
    }

//    protected void sendStatistics() {
//        try {
//            String resp = mStatis.getStatistic();
//
//            InputStream inp = new ByteArrayInputStream(resp.getBytes());
//            HttpResponse response = new HttpResponse(HttpResponse.HTTP_OK, HttpResponse.MIME_PLAINTEXT, inp);
//            response.addHeader("content-length", "" + inp.available());
//            sendResponse(HttpResponse.HTTP_OK, response.mimeType, response.header, response.data);
//
//            MyLog.Infor("THREAD " + mId + ": Send STATISTICS to client.");
//        } catch (Exception e) {
//            MyLog.Error("Error send STATISTICS to client "+e.getMessage());
//        }
//    }
    protected void reloadConfig() {
        try {
            String resp = Main.loadConfig();

            InputStream inp = new ByteArrayInputStream(resp.getBytes());
            HttpResponse response = new HttpResponse(HttpResponse.HTTP_OK, HttpResponse.MIME_PLAINTEXT, inp);
            response.addHeader("content-length", "" + inp.available());
            sendResponse(HttpResponse.HTTP_OK, response.mimeType, response.header, response.data);

            MyLog.Infor("THREAD " + mId + ": reaload config.");
        } catch (Exception e) {
            MyLog.Error("Error reload config:  " + e.getMessage());
        }
    }

    /**
     * Decodes the percent encoding scheme. <br/>
     * For example: "an+example%20string" -> "an example string"
     *
     * @param str <code>String</code> value will be decoded
     * @return value after decode
     * @throws InterruptedException when processing in String value, may be out
     * of index bound,...
     */
    private String decodePercent(String str) throws InterruptedException {
        try {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < str.length(); i++) {
                char c = str.charAt(i);
                switch (c) {
                    case '+':
                        sb.append(' ');
                        break;

                    case '%':
                        sb.append((char) Integer.parseInt(str.substring(i + 1, i + 3), 16));
                        i += 2;
                        break;

                    default:
                        sb.append(c);
                        break;

                }
            }
            return new String(sb.toString().getBytes());
        } catch (Exception e) {
            sendError(HttpResponse.HTTP_BADREQUEST, "BAD REQUEST: Bad percent-encoding.");
            return null;
        }

    }

    /**
     * Decodes parameters in percent-encoded URI-format ( e.g.
     * "name=Jack%20Daniels&pass=Single%20Malt" ) and adds them to given
     * Properties.
     *
     * @param parms param string will be parse
     * @param p stored properties to store value
     * @throws InterruptedException when processing in String value, may be out
     * of index bound,...
     */
    private void decodeParms(String parms, Properties p) throws InterruptedException {
        if (parms == null) {
            return;
        }
        MyLog.Debug(parms);
        try {
            String action = StringUtils.substringBetween(parms, "<ws:doCharge>", "</ws:doCharge>");

            if (action.length() > 0) {
                // Call confidevice tag
                try {
                    String msisdn = StringUtils.substringBetween(parms, "<msisdn>", "</msisdn>");
//                    System.out.println("msisdn: "+msisdn);
                    if (CommonUtils.isNullOrEmpty(msisdn)) {
                        MyLog.Error("MISSING PARAM msisdn");
                        sendToClient(ResponseCode.VAS_REQUEST_FAILURE.getResponseCode()); 
                        return;
                    }
                    String amount = StringUtils.substringBetween(parms, "<amount>", "</amount>");                    
                    String serviceName = StringUtils.substringBetween(parms, "<serviceName>", "</serviceName>");
                    String providerName = StringUtils.substringBetween(parms, "<providerName>", "</providerName>");
                    String subCpName = StringUtils.substringBetween(parms, "<subCpName>", "</subCpName>");
                    String category = StringUtils.substringBetween(parms, "<category>", "</category>");
                    String item = StringUtils.substringBetween(parms, "<item>", "</item>");
                    String registertime = StringUtils.substringBetween(parms, "<registertime>", "</registertime>");
                    String command = StringUtils.substringBetween(parms, "<command>", "</command>");
                    String subService = StringUtils.substringBetween(parms, "<subService>", "</subService>");
                    String transId = StringUtils.substringBetween(parms, "<transId>", "</transId>");
                    String channel = StringUtils.substringBetween(parms, "<channel>", "</channel>");

                    if (!isInChargeList(msisdn)) {
                        MyLog.Debug(msisdn + " not in charge list, set amount to 0");
                        amount = "0";
                    }
                    
                    msisdn = CountryCode.formatMobile(msisdn);
                    
                    ChargingProcess client = new ChargingProcess();
                    String result = client.doCharge(msisdn, amount, serviceName,
                            providerName, subCpName, category,
                            item, command, subService,
                            transId, registertime, channel);
                    sendToClient(result);
                } catch (Exception ex) {
                    
                    MyLog.Error(ex.getMessage());
//                    sendToClient(Constants.AUTHENTICATION_FAILURE_201);
//                    mStatis.addRequest(REQUEST_CRBT, false);
                }
            }
        } catch (Exception ex) {
            MyLog.Error(ex.getMessage());
            MyLog.Error("THREAD " + mId + ": Error decode params. " + ex.getMessage());
        }
    }

    protected void sendToClient(String result) {
        try {
            String resp;
            resp = "<?xml version=\"1.0\"?>" + 
                   "<S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" >" + 
                        "<S:Body>" + 
                            "<ns:getMobileResponse xmlns:ns=\"http://service.viettel.com\">" + 
                                "<return>" + URLEncoder.encode(result, "UTF-8") + 
                                "</return>" + 
                            "</ns:getMobileResponse>" + 
                        "</S:Body>" + 
                    "</S:Envelope>";

            MyLog.Infor("THREAD " + mId + ": Sent RESULT to client " + (System.currentTimeMillis() - mRecievedRequestTime) + " (ms)\n\n");
            InputStream inp = new ByteArrayInputStream(resp.getBytes());
            HttpResponse response = new HttpResponse(HttpResponse.HTTP_OK, HttpResponse.MIME_XML, inp);
            response.addHeader("content-length", "" + inp.available());
            sendResponse(HttpResponse.HTTP_OK, response.mimeType, response.header, response.data);
        } catch (Exception e) {
            MyLog.Error("THREAD " + mId + ": ERROR while sending result to client: " + e.getMessage());
            GlobalVariables.FAILED_REQUEST++;
        }
    }

    /**
     * Returns an error message as a HTTP response and throws
     * InterruptedException to stop furhter request processing.
     *
     * @param status status of http processing
     * @param msg http message content
     * @throws InterruptedException exception will be throwed after sending
     */
    private void sendError(String status, String msg) throws InterruptedException {
        sendResponse(status, HttpResponse.MIME_XML, null, new ByteArrayInputStream(msg.getBytes()));
    }

    /**
     * Sends given response to the socket.
     *
     * @param status status of http processing
     * @param mime content type of response
     * @param header header values
     * @param data <code>InputStream</code> stored content of response
     */
    public void sendResponse(String status, String mime, Properties header, InputStream data) {
        try {
            if (status == null) {
                throw new Error("sendResponse(): Status can't be null.");
            }
            OutputStream out = mySocket.getOutputStream();
            PrintWriter pw = new PrintWriter(out);
            pw.print("HTTP/1.1 " + status + " \r\n");
            if (mime != null) {
                pw.print("Content-Type: " + mime + "\r\n");
            }

            if (header == null || header.getProperty("Date") == null) {
                pw.print("Date: " + gmtFrmt.format(new Date()) + "\r\n");
            }

            if (header != null) {
                Enumeration e = header.keys();
                while (e.hasMoreElements()) {
                    String key = (String) e.nextElement();
                    String value = header.getProperty(key);
                    pw.print(key + ": " + value + "\r\n");
                }

            }
            pw.print("\r\n");
            pw.flush();

            if (data != null) {
                byte[] buff = new byte[4096];
                StringBuilder sbuf = new StringBuilder();
                while (true) {
                    int read = data.read(buff, 0, 4096);
                    if (read <= 0) {
                        break;
                    }

                    int i = 0;
                    for (i = 0; i < buff.length; i++) {
                        if (buff[i] == 0) {
                            break;
                        }
                    }
                    if (i > 0) {
                        sbuf.append(new String(buff, 0, i));
                    }

                    out.write(buff, 0, read);
                }
            }

            out.flush();
            out.close();
            if (data != null) {
                data.close();
            }

            mySocket.close();
        } catch (Exception ioe) {
            // Couldn't write? No can do.
            try {
                mySocket.close();
            } catch (Exception ex) {
//                MyLog.Error("THREAD " + mId + ": Error while close socket " + ex.getMessage());
            }
            MyLog.Error("THREAD " + mId + ": Error resposed to client: " + ioe.getMessage());
        }
    }

    protected void sendWSDLFile() {
        String text = PublicLibs.getWSDL(mySocket.getLocalSocketAddress().toString());

        HttpResponse response = new HttpResponse(HttpResponse.HTTP_OK, HttpResponse.MIME_XML, new ByteArrayInputStream(text.getBytes()));
        response.addHeader("content-length", "" + text.getBytes().length);
        sendResponse(HttpResponse.HTTP_OK, response.mimeType, response.header, response.data);

        MyLog.Error("THREAD " + mId + ": Send WSDL to client. (" + (System.currentTimeMillis() - mRecievedRequestTime) + " ms)");
    }

}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel;

/**
 *
 * @author bacdh
 */
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class VCGClient {

    private static String PATH_VCGW3 = "/process/services/ProcessClients?wsdl";
    private static String SERVER_IP = "10.30.164.146";
    private static int PORT = 9186;
    private static String USERNAME = "muzikgw";
    private static String PASSWORD = "muzikgw123321";
    private static String PROVIDERID = "VIETTEL";
    private static String SERVICEID = "IMZGW";
    private static String COMMAND = "DOWNLOAD";
    private static String CONTENTS = "CHECKBALANCE|DOWNLOAD";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public String checkBalance(String msisdn) {
        String ret = "-1";
        try {
            long start = System.currentTimeMillis();
            InetAddress addr = InetAddress.getByName(SERVER_IP);
            Socket sock = new Socket(addr, PORT);
            sock.setKeepAlive(true);
            String reqTime = dateFormat.format(new Date(System.currentTimeMillis()));
            String xml = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ws=\"http://ws.payment.mps.viettel.com/\"><soapenv:Header/><soapenv:Body>\n" +
            "<ws:doCharge>\n" +
            "<msisdn>"+msisdn+"</msisdn>\n" +
            "<amount>50000</amount>\n" +
            "<serviceName>pukeman</serviceName>\n" +
            "<providerName>ktek</providerName>\n" +
            "<subCpName></subCpName>\n" +
            "<category></category>\n" +
            "<item></item>\n" +
            "<channel>SMS</channel>\n" +
            "<registertime>20151027092701</registertime>\n" +
            "<command>MONFEE</command>\n" +
            "<subService>pukeman_daily</subService>\n" +
            "<transId>0400002120151113135319643</transId>\n" +
            "</ws:doCharge>\n" +
            "</soapenv:Body></soapenv:Envelope>";

            // Send header
            BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream(), "UTF-8"));
            wr.write("POST " + PATH_VCGW3 + " HTTP/1.0\r\n");
            wr.write("Content-Type: text/xml; charset=utf-8\r\n");
            wr.write("Accept: application/soap+xml, application/dime, multipart/related, text/*\r\n");
            wr.write("User-Agent: Axis/1.4\r\n");
            wr.write("Host: localhost\r\n");
            wr.write("Cache-Control: no-cache\r\n");
            wr.write("Pragma: no-cache\r\n");
//            wr.write("SOAPAction: \"\"\r\n");
            wr.write("Content-Length: " + xml.length() + "\r\n");
            wr.write("\r\n");
            // Send data
            wr.write(xml);
            wr.flush();
//            System.out.println("send:" + xml);
            //output
            BufferedReader rd = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            String line = rd.readLine();
            while (line != null) {
//                System.out.println(line);
                line = rd.readLine();
                if (line != null) {
                    if (line.indexOf("return") > 0) {
                        ret = getReturnCode(line.trim());
                    }
                }
            }
            System.out.println((new Date(System.currentTimeMillis())) + ": Time: " + (System.currentTimeMillis() - start) + "(ms), " + "return:" + ret);
            sock.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ret;
    }

    private String getReturnCode(String input) {
        //String result = input.substring(34, 40);
        String startTag = "<return>";
        String endTag = "</return>";
        int start = input.indexOf(startTag) + startTag.length();
        int end = input.indexOf(endTag);
        String result = input.substring(start, end);
        return result;
    }

    public static void main(String[] args) {
        VCGClient client = new VCGClient();
        int isdn = 970000000;
        int numberRequest = 10000;
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numberRequest; i++) {
            System.out.println(i + "----");
            client.checkBalance(String.valueOf(isdn+i));
        }
        
        long endTime = System.currentTimeMillis();
        
        System.out.println("ret:============================================");
        double seconds = (endTime -startTime)/1000;
        System.out.println("ret:" + seconds + " seconds");
        System.out.println("TPS:" + numberRequest/seconds + "");
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.utils;

import com.viettel.mps.payment.utilities.CountryCode;

/**
 *
 * @author ManhPS
 */
public class PublicLibs {
    
    public static String nomalizeMSISDN(String msisdn) {
        return CountryCode.formatMobile(msisdn);
    }

    public static String internationalMSISDN(String msisd) {
        return CountryCode.getCountryCode() + nomalizeMSISDN(msisd);
    }
    
    public static String getWSDL(String serverAddr) {
        String text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
                + "<wsdl:definitions xmlns:wsdl=\"http://schemas.xmlsoap.org/wsdl/\" xmlns:ns1=\"http://org.apache.axis2/xsd\" xmlns:ns=\"http://service.viettel.com\" xmlns:wsaw=\"http://www.w3.org/2006/05/addressing/wsdl\" xmlns:http=\"http://schemas.xmlsoap.org/wsdl/http/\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xmlns:mime=\"http://schemas.xmlsoap.org/wsdl/mime/\" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\" xmlns:soap12=\"http://schemas.xmlsoap.org/wsdl/soap12/\" targetNamespace=\"http://service.viettel.com\">"
                + "<wsdl:documentation>mps</wsdl:documentation>"
                + "<wsdl:types>"
                + "<xs:schema attributeFormDefault=\"qualified\" elementFormDefault=\"qualified\" targetNamespace=\"http://service.viettel.com\">"
                + "<xs:element name=\"doCharge\">"
                + "<xs:complexType>"
                + "<xs:sequence>"
                + "<xs:element minOccurs=\"0\" name=\"msisdn\" nillable=\"true\" type=\"xs:string\"/>"
                + "<xs:element minOccurs=\"0\" name=\"amount\" nillable=\"true\" type=\"xs:string\"/>"
                + "<xs:element minOccurs=\"0\" name=\"serviceName\" nillable=\"true\" type=\"xs:string\"/>"
                + "<xs:element minOccurs=\"0\" name=\"providerName\" nillable=\"true\" type=\"xs:string\"/>"
                + "<xs:element minOccurs=\"0\" name=\"subCpName\" nillable=\"true\" type=\"xs:string\"/>"
                + "<xs:element minOccurs=\"0\" name=\"category\" nillable=\"true\" type=\"xs:string\"/>"
                + "<xs:element minOccurs=\"0\" name=\"item\" nillable=\"true\" type=\"xs:string\"/>"
                + "<xs:element minOccurs=\"0\" name=\"registertime\" nillable=\"true\" type=\"xs:string\"/>"
                + "<xs:element minOccurs=\"0\" name=\"command\" nillable=\"true\" type=\"xs:string\"/>"
                + "<xs:element minOccurs=\"0\" name=\"subService\" nillable=\"true\" type=\"xs:string\"/>"
                + "<xs:element minOccurs=\"0\" name=\"transId\" nillable=\"true\" type=\"xs:string\"/>"
                + "</xs:sequence>"
                + "</xs:complexType>"
                + "</xs:element>"
                + "<xs:element name=\"doChargeResponse\">"
                + "<xs:complexType>"
                + "<xs:sequence>"
                + "<xs:element minOccurs=\"0\" name=\"return\" nillable=\"true\" type=\"xs:string\"/>"
                + "</xs:sequence>"
                + "</xs:complexType>"
                + "</xs:element>"
                + "</xs:schema>"
                + "</wsdl:types>"
                + "<wsdl:message name=\"doChargeRequest\">"
                + "<wsdl:part name=\"parameters\" element=\"ns:doCharge\"/>"
                + "</wsdl:message>"
                + "<wsdl:message name=\"doChargeResponse\">"
                + "<wsdl:part name=\"parameters\" element=\"ns:doChargeResponse\"/>"
                + "</wsdl:message>"
                + "<wsdl:portType name=\"mpsPortType\">"
                + "<wsdl:operation name=\"doCharge\">"
                + "<wsdl:input message=\"ns:doChargeRequest\" wsaw:Action=\"urn:doCharge\"/>"
                + "<wsdl:output message=\"ns:doChargeResponse\" wsaw:Action=\"urn:doChargeResponse\"/>"
                + "</wsdl:operation>"
                + "</wsdl:portType>"
                + "<wsdl:binding name=\"mpsSoap11Binding\" type=\"ns:mpsPortType\">"
                + "<soap:binding transport=\"http://schemas.xmlsoap.org/soap/http\" style=\"document\"/>"
                + "<wsdl:operation name=\"doCharge\">"
                + "<soap:operation soapAction=\"urn:doCharge\" style=\"document\"/>"
                + "<wsdl:input>"
                + "<soap:body use=\"literal\"/>"
                + "</wsdl:input>"
                + "<wsdl:output>"
                + "<soap:body use=\"literal\"/>"
                + "</wsdl:output>"
                + "</wsdl:operation>"
                + "</wsdl:binding>"
                + "<wsdl:service name=\"mps\">"
                + "<wsdl:port name=\"mpsHttpSoap11Endpoint\" binding=\"ns:mpsSoap11Binding\">"
                + "<soap:address location=\"http://" + GlobalVariables.SERVER_IP + ":" + GlobalVariables.SERVER_PORT + "/process/mps.mpsHttpSoap11Endpoint/\"/>"
                + "</wsdl:port>"
                + "</wsdl:service>"
                + "</wsdl:definitions>";
        return text;
    }
}

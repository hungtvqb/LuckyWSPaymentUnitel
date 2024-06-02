/*
 * RBTResourceUtils.java
 *
 * Created on September 18, 2007, 11:01 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.viettel.utils;

import java.util.ResourceBundle;

/**
 *
 * @author DatTV
 */
public class ResourceUtils
{
    
    static private ResourceBundle rb = null;
    /** Creates a new instance of RBTResourceUtils */
    public ResourceUtils()
    {
        
    }
    
    public static String getResource(String key){
        rb = ResourceBundle.getBundle("config");
        return rb.getString(key);
    }
    
    
}

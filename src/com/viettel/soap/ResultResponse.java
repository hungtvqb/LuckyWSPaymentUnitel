/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.soap;

/**
 *
 * @author Hoa
 */
public class ResultResponse {
    private int mCode;
    private String mMessage;

    public ResultResponse(int code, String message) {
        mCode = code;
        mMessage = message;
    }

    public int getCode() {
        return mCode;
    }

    public void setCode(int val) {
        mCode = val;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String val) {
        mMessage = val;
    }
}

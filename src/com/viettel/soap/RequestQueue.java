/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.viettel.soap;

import com.viettel.utils.GlobalVariables;

import java.net.Socket;
import java.util.Vector;

/**
 *
 * @author hoand
 */
public class RequestQueue {
//    protected static int NUMBER_OF_THREAD = 5;
    
    protected Vector<Socket> mQueue;

    public RequestQueue() {
        mQueue = new Vector<Socket>();

        for (int i=0; i<GlobalVariables.NUMBER_OF_THREAD; i++) {
            new ProcessRequest(i, this);
        }
    }

    public void addToQueue(Socket s) {
        mQueue.add(s);
    }

    public synchronized Socket getRequest() {
        if (mQueue.size() > 0) {
            Socket t = mQueue.firstElement();
            mQueue.removeElementAt(0);
            return t;
        }

        return null;
    }
}

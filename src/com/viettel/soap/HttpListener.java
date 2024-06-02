package com.viettel.soap;

//import com.viettel.utils.configs.LoadConfigs;

import com.viettel.utils.MyLog;
import java.net.ServerSocket;
import java.io.IOException;
import java.util.Properties;

/**
 * This class accepts client connection on given port. When the connection
 * is accepted, the listener creates an instance of <code>ServerSession</code>,
 * generates new <code>PDUProcessor</code> using object derived from
 * <code>PDUProcessorFactory</code>, passes the processor to the smsc session
 * and starts the session as a standalone thread.
 */
public class HttpListener implements Runnable {

    public static int RUNNING = 1;
    public static int FINISH = 0;
    private ServerSocket[] serverSockets;
    private long acceptTimeout = 10;
    private final Object lock;
    private int status;
    private int port[];
    private boolean isReceiving;
    private boolean isFirst;
    protected RequestQueue mQueue;

    /**
     * Constructor with control if the listener starts as a separate thread.
     * If <code>asynchronous</code> is true, then the listener is started
     * as a separate thread, i.e. the creating thread can continue after
     * calling of method <code>enable</code>. If it's false, then the
     * caller blocks while the listener does it's work, i.e. listening.
     *
     * @param port   list of listener port
     * @param logMdl index of log module
     * @param authen the authenticator object
     * @see #start()
     */
    public HttpListener(int port, String logName, Properties pro) {
//        this.logName = logName;
        this.lock = new Object();
        this.port = new int[]{port};
//        this.pro = pro;
//        this.pc = pc;
        this.isFirst = true;
//        this.rx = new Queue();
//        this.manager = new ServiceManager();

        mQueue = new RequestQueue();

//        HttpSession.mConnection = new DatabaseConnection();
//        HttpSession.mConnection.startConnect();
    }

    /**
     * Starts the listening. If the listener is asynchronous (reccomended),
     * then new thread is created which listens on the port and the
     * <code>enable</code> method returns to the caller. Otherwise
     * the caller is blocked in the enable method.
     *
     * @see #start()
     */
    public void start() {
        if (isFirst) {
            instance(port);
        }
        if (serverSockets == null) {
            MyLog.Infor("none listen port declare, server is now " + " return initialize");
            return;
        }
        String log = "soap server listen on port: ";
        //noinspection SynchronizeOnNonFinalField
        synchronized (lock) {
            for (int i = 0; i < serverSockets.length; i++) {
                if (serverSockets[i] == null) {
                    MyLog.Infor("port index " + i + " is null, it may"
                            + " be used in other program");
                    continue;
                }
                if (serverSockets[i].isClosed()) {
                    int p = serverSockets[i].getLocalPort();
                    try {
                        MyLog.Infor("port " + p + " is closed, reopen again ...");
                        serverSockets[i] = createSocket(p);
                    } catch (IOException e) {
                        //e.printStackTrace(getPrintStream());
                        MyLog.Infor("create Socket on Port:" + p + "  error");
                    }
                }
                log += serverSockets[i].getLocalPort() + "  ";
            }
        }

        MyLog.Infor(log);
        status = RUNNING;
        isReceiving = true;
        Thread t = new Thread(this);
        t.start();
    }

    /**
     * Signals the listener that it should disable listening and wait
     * until the listener stops. Note that based on the timeout settings
     * it can take some time befor this method is finished -- the listener
     * can be blocked on i/o operation and only after exiting i/o
     * it can detect that it should disable.
     *
     * @see #start()
     */
    public void stop() {
        try {
            //noinspection SynchronizeOnNonFinalField
            synchronized (lock) {
                isReceiving = false;
                while (status == RUNNING) {
                    Thread.yield();
                }
                for (int i = 0; i < serverSockets.length; i++) {
                    if (serverSockets[i] != null) {
                        serverSockets[i].close();
                    }
                }
            }
        } catch (IOException e) {
            // e.printStackTrace(getPrintStream());
            MyLog.Infor("stop soap server error");
        }
    }

    /**
     * The actual listening code which is run either from the thread
     * (for async listener) or called from <code>enable</code> method
     * (for sync listener). The method can be exited by calling of method
     * <code>disable</code>.
     *
     * @see #start()
     * @see #stop()
     */
    public void run() {
        if (status != RUNNING) {
            return;
        }
        try {
            while (isReceiving) {
                listen();
                Thread.yield();
            }
        } finally {
            status = FINISH;
        }
    }

    /**
     * The "one" listen attempt called from <code>run</code> method.
     * The listening is atomicised to allow contoled stopping of the listening.
     * The length of the single listen attempt
     * is defined by <code>acceptTimeout</code>.
     * If a connection is accepted, then new session is created on this
     * connection, new PDU processor is generated using PDU processor factory
     * and the new session is started in separate thread.
     *
     * @see #run()
     */
    private void listen() {
        //noinspection SynchronizeOnNonFinalField
        synchronized (lock) {
            for (int i = 0; i < serverSockets.length; i++) {
                try {
                    if (serverSockets[i] == null || serverSockets[i].isClosed()) {
                        continue;
                    }
                    mQueue.addToQueue(serverSockets[i].accept());

                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * Sets new timeout for accepting new connection.
     * The listening blocks the for maximum this time, then it
     * exits regardless the connection was acctepted or not.
     *
     * @param value the new value for accept timeout
     */
    public void setAcceptTimeout(int value) {
        acceptTimeout = value;
    }

    /**
     * Returns the current setting of accept timeout.
     *
     * @return the current accept timeout
     * @see #setAcceptTimeout(int)
     */
    public long getAcceptTimeout() {
        return acceptTimeout;
    }

    private ServerSocket createSocket(int port) throws IOException {
        MyLog.Infor("Binding to Port " + port);
        ServerSocket socket = new ServerSocket(port);
        socket.setSoTimeout((int) getAcceptTimeout());
        return socket;
    }

    protected void instance(int port[]) {
        MyLog.Infor("Create instance soap listener");
        if (port == null) {
            return;
        }
        int max = port.length;
        serverSockets = new ServerSocket[max];
        for (int i = 0; i < max; i++) {
            try {
                serverSockets[i] = createSocket(port[i]);
            } catch (IOException e) {
                //e.printStackTrace(getPrintStream());
                MyLog.Error("Binding to Port " + port[i] + " error, port has already used");
//                e.printStackTrace();
                System.exit(1);
            }
        }
    }

    /**
     * check listen port
     *
     * @param port port value will be search server socket
     * @return ServerSocket
     */
    private int findServerSocket(int port) {
        if (serverSockets == null) {
            return -1;
        }
        //noinspection SynchronizeOnNonFinalField
        synchronized (lock) {
            for (int i = 0; i < serverSockets.length; i++) {
                if (serverSockets[i] != null && serverSockets[i].getLocalPort() == port) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * do reload listen port
     *
     * @param port the list of listener port
     */
    public void reloadConfig(int port[]) {
        //noinspection SynchronizeOnNonFinalField
        synchronized (lock) {
            if (port == null) {
                return;
            }
            ServerSocket newSocket[] = new ServerSocket[port.length];
            int movSocket[] = new int[serverSockets.length];
            for (int i = 0; i < movSocket.length; i++) {
                movSocket[i] = -1;
            }
            for (int i = 0; i < port.length; i++) {
                int indexOfPort = findServerSocket(port[i]);
                if (indexOfPort == -1) {
                    try {
                        newSocket[i] = createSocket(port[i]);
                    } catch (IOException e) {
                        //e.printStackTrace(getPrintStream());
                        MyLog.Infor("add new  Port " + port[i] + " error, port has already used");
                    }
                } else {
                    newSocket[i] = serverSockets[indexOfPort];
                    movSocket[indexOfPort] = 1;
                }
            }
            //close unused socket
            for (int i = 0; i < movSocket.length; i++) {
                if (movSocket[i] == 1) {
                    continue;
                }
                try {
                    int _port = serverSockets[i].getLocalPort();
                    MyLog.Infor("Close unused port  " + _port);
                    serverSockets[i].close();
                } catch (IOException e) {
                    MyLog.Infor(e.getMessage());
                }
            }
            serverSockets = newSocket;
        }
        String log = "VASPServer listen on Port: ";
        for (int i = 0; i < serverSockets.length; i++) {
            if (serverSockets[i] != null) {
                log += serverSockets[i].getLocalPort();
            }
            if (i != serverSockets.length - 1) {
                log += ":";
            }
        }
        MyLog.Infor(log);
    }
}


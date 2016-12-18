/*
 * RequestProcessor.java
 *
 * Created on Nov 21, 2010 1:46:04 PM
 *
 * Copyright (c) 2002 - 2010 : Swayam Inc.
 *
 * P R O P R I E T A R Y & C O N F I D E N T I A L
 *
 * The copyright of this document is vested in Swayam Inc. without
 * whose prior written permission its contents must not be published,
 * adapted or reproduced in any form or disclosed or
 * issued to any third party.
 */

package com.swayam.mobile.sync.server.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.microedition.io.StreamConnection;

import com.swayam.mobile.sync.server.common.io.SyncIO;

/**
 * 
 * @author paawak
 */
class RequestProcessor implements Runnable {

    private final List<StreamConnection> queue;

    RequestProcessor() {

        queue = Collections.synchronizedList(new ArrayList<StreamConnection>());

        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();

    }

    @Override
    public void run() {

        while (true) {

            synchronized (this) {

                if (queue.size() == 0) {

                    try {

                        wait();

                    } catch (InterruptedException e) {

                        e.printStackTrace();
                        return;

                    }
                }

                synchronized (this) {

                    StreamConnection streamConnection = queue.get(0);

                    queue.remove(0);

                    try {
                        processRequest(streamConnection);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }

        }

    }

    public void addRequest(StreamConnection streamConnection) {

        synchronized (this) {
            queue.add(streamConnection);
            notify();
        }

    }

    private void processRequest(StreamConnection streamConnection)
            throws IOException {

        SyncIO io = new SyncIO();

        io.read(streamConnection.openInputStream());
        streamConnection.close();

    }

}

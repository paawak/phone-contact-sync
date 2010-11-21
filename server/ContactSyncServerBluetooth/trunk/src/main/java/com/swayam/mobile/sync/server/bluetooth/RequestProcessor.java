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

import javax.microedition.io.StreamConnection;

import com.swayam.mobile.sync.server.common.io.SyncIO;

/**
 * 
 * @author paawak
 */
class RequestProcessor implements Runnable {

    private final StreamConnection streamConnection;

    RequestProcessor(StreamConnection streamConnection) {
        this.streamConnection = streamConnection;
    }

    @Override
    public void run() {

        SyncIO io = new SyncIO();

        try {

            io.read(streamConnection.openInputStream());
            streamConnection.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}

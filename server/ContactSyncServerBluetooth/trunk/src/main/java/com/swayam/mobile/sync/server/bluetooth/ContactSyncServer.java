/*
 * ContactSyncServer.java
 *
 * Created on Nov 17, 2010 1:39:43 AM
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

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DataElement;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import com.intel.bluetooth.BlueCoveConfigProperties;

/**
 * 
 * @author paawak
 */
public class ContactSyncServer implements Runnable {

    private static final UUID CONTACT_SYNC_SERVER_UUID = new UUID(
            "F0E0D0C0B0A000908070605040302010", false);

    @Override
    public void run() {

        try {

            LocalDevice localDevice = LocalDevice.getLocalDevice();

            // localDevice.setDiscoverable(DiscoveryAgent.GIAC);

            String url = "btspp://localhost:"
                    + CONTACT_SYNC_SERVER_UUID.toString()
                    + ";name=ContactSyncServer;authorize=false";

            System.out.println("url=" + url);

            StreamConnectionNotifier notifier = (StreamConnectionNotifier) Connector
                    .open(url.toString());

            ServiceRecord record = localDevice.getRecord(notifier);
            record.setAttributeValue(3, new DataElement(DataElement.DATSEQ));
            localDevice.updateRecord(record);

            RequestProcessor processor = new RequestProcessor();

            while (true) {
                System.out.println("waiting for client..");

                try {

                    StreamConnection conn = notifier.acceptAndOpen();

                    processor.addRequest(conn);

                } catch (IOException e) {
                    // wrong client or interrupted - continue anyway
                    e.printStackTrace();
                    continue;
                }

            }

        } catch (BluetoothStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] str) {

        // -Dbluecove.native.path=/bhandar/installables/java/libs/bluetooth/bluecove
        System.setProperty(BlueCoveConfigProperties.PROPERTY_NATIVE_PATH,
                "/usr/lib");

        Thread t = new Thread(new ContactSyncServer());
        t.start();

    }

}

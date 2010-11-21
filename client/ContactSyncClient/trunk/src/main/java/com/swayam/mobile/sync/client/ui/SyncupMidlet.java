/*
 * Contactz.java
 *
 * Created on Nov 8, 2010 8:19:59 PM
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

package com.swayam.mobile.sync.client.ui;

import java.io.IOException;
import java.io.OutputStream;

import javax.bluetooth.BluetoothStateException;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.StreamConnection;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;
import javax.microedition.lcdui.TextBox;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.pim.PIMException;

import com.swayam.mobile.sync.client.io.SyncWriter;
import com.swayam.mobile.sync.client.util.PhoneContactManager;

/**
 * 
 * @author paawak
 */
public class SyncupMidlet extends MIDlet implements CommandListener {

    private static final String EXPORT_CONTACTS = "Export Contacts";
    private static final String EXPORT_VCARDS = "Export VCards";

    // private final Logger log;

    private final PhoneContactManager contactManager;

    // private static final String CONTACT_EXPORT_SERVICE_URL = "http://home.paawak.com:8090/MobileContactSynchronizer/ContactSynchronizer";

    private static final String VCARDS_EXPORT_SERVLET_URL = "http://home.paawak.com:8090/MobileContactSynchronizer/VCardsExportProcessor";

    private static final UUID CONTACT_SYNC_SERVER_UUID = new UUID(
            "1115E2609F3CB487100285D", false);

    private List list;

    // private ContactSynchronizerService service;

    public SyncupMidlet() {

        // log = new Logger(SyncupMidlet.class);

        contactManager = new PhoneContactManager();

    }

    protected void startApp() throws MIDletStateChangeException {

        // service = new ContactSynchronizerService_Stub(
        // CONTACT_EXPORT_SERVICE_URL);

        switchDisplayable(null, getList());

    }

    protected void pauseApp() {

    }

    protected void destroyApp(boolean unconditional)
            throws MIDletStateChangeException {

    }

    public void commandAction(Command command, Displayable displayable) {

        if (displayable == list) {
            if (command == List.SELECT_COMMAND) {
                listAction();
            }
        }

    }

    private List getList() {

        if (list == null) {

            list = new List("Sync Contact List", Choice.IMPLICIT);
            list.append(EXPORT_CONTACTS, null);
            list.append(EXPORT_VCARDS, null);
            list.setCommandListener(this);

        }

        return list;
    }

    private void switchDisplayable(Alert alert, Displayable nextDisplayable) {

        Display display = Display.getDisplay(this);

        if (alert == null) {
            display.setCurrent(nextDisplayable);
        } else {
            display.setCurrent(alert, nextDisplayable);
        }

    }

    private void listAction() {

        String selectedString = list.getString(list.getSelectedIndex());

        if (selectedString != null) {

            Runnable runnable = null;

            if (selectedString.equals(EXPORT_CONTACTS)) {

                runnable = new Runnable() {

                    public void run() {

                        try {
                            LocalDevice local = LocalDevice.getLocalDevice();
                            local.setDiscoverable(DiscoveryAgent.GIAC);
                            DiscoveryAgent agent = local.getDiscoveryAgent();
                            String connectURL = agent.selectService(
                                    CONTACT_SYNC_SERVER_UUID,
                                    ServiceRecord.NOAUTHENTICATE_NOENCRYPT,
                                    false);
                            StreamConnection streamConnection = (StreamConnection) Connector
                                    .open(connectURL);

                            new SyncWriter()
                                    .write(streamConnection.openOutputStream(),
                                            contactManager.getContactDetails()/*
                                                                              + "\n&&&&&&&&&&&&&&&&&&&&&\n"
                                                                              + log.getLogs()*/);

                            streamConnection.close();

                        } catch (BluetoothStateException e) {
                            handleException(e);
                        } catch (IOException e) {
                            handleException(e);
                        } catch (PIMException e) {
                            handleException(e);
                        }

                    }
                };

            } else if (selectedString.equals(EXPORT_VCARDS)) {

                runnable = new Runnable() {

                    public void run() {

                        try {
                            exportVCards();
                        } catch (IOException e) {
                            handleException(e);
                        } catch (PIMException e) {
                            handleException(e);
                        }

                    }
                };

            }

            if (runnable != null) {
                Thread t = new Thread(runnable);
                t.start();
            }

        }

    }

    private void exportVCards() throws IOException, PIMException {

        HttpConnection con = (HttpConnection) Connector.open(
                VCARDS_EXPORT_SERVLET_URL, Connector.WRITE);

        con.setRequestMethod(HttpConnection.POST);
        con.setRequestProperty("Content-Type", "java-internal");

        OutputStream os = con.openOutputStream();

        os.write(contactManager.getVCardsAsBytes());

        os.flush();
        os.close();

        con.close();

    }

    private void handleException(Exception e) {

        TextBox textBox = new TextBox("Exception Occured", e.getMessage(), 500,
                0);

        switchDisplayable(null, textBox);

    }

}

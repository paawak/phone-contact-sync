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
import java.util.Enumeration;

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
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;
import javax.microedition.pim.PIMItem;

import com.swayam.mobile.sync.client.io.SyncWriter;

/**
 * 
 * @author paawak
 */
public class SyncupMidlet extends MIDlet implements CommandListener {

    private static final String EXPORT_CONTACTS = "Export Contacts";
    private static final String EXPORT_VCARDS = "Export VCards";

    // private static final String CONTACT_EXPORT_SERVICE_URL = "http://home.paawak.com:8090/MobileContactSynchronizer/ContactSynchronizer";

    private static final String VCARDS_EXPORT_SERVLET_URL = "http://home.paawak.com:8090/MobileContactSynchronizer/VCardsExportProcessor";

    private static final UUID CONTACT_SYNC_SERVER_UUID = new UUID(
            "1115E2609F3CB487100285D", false);

    private List list;

    // private ContactSynchronizerService service;

    public SyncupMidlet() {

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

                            new SyncWriter().write(
                                    streamConnection.openOutputStream(),
                                    getContactDetails());

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

    private String getStringField(Contact contact, int attribute) {

        int size = contact.countValues(attribute);

        String value = null;

        for (int count = 0; count < size; count++) {

            if ((contact.getAttributes(attribute, count) != 0)) {
                value = contact.getString(attribute, count);
            }

        }

        return value;

    }

    private void exportVCards() throws IOException, PIMException {

        PIM pim = PIM.getInstance();
        String[] dataFormats = pim.supportedSerialFormats(PIM.CONTACT_LIST);

        HttpConnection con = (HttpConnection) Connector.open(
                VCARDS_EXPORT_SERVLET_URL, Connector.WRITE);

        con.setRequestMethod(HttpConnection.POST);
        con.setRequestProperty("Content-Type", "java-internal");

        OutputStream os = con.openOutputStream();

        Enumeration items = pim.openPIMList(PIM.CONTACT_LIST, PIM.READ_ONLY)
                .items();

        while (items.hasMoreElements()) {

            PIMItem item = (PIMItem) items.nextElement();

            pim.toSerialFormat(item, os, "UTF-8", dataFormats[0]);

        }

        os.flush();
        os.close();

        con.close();

    }

    private String getContactDetails() throws PIMException {

        StringBuffer sb = new StringBuffer();

        PIM pim = PIM.getInstance();

        String[] allContactLists = PIM.getInstance().listPIMLists(
                PIM.CONTACT_LIST);

        for (int listCount = 0; listCount < allContactLists.length; listCount++) {

            String contactListName = allContactLists[listCount];

            ContactList contactList = (ContactList) pim.openPIMList(
                    PIM.CONTACT_LIST, PIM.READ_ONLY, contactListName);

            if (contactList.isSupportedField(Contact.FORMATTED_NAME)
                    && contactList.isSupportedField(Contact.TEL)) {

                Enumeration contacts = contactList.items();

                while (contacts.hasMoreElements()) {

                    Contact contact = (Contact) contacts.nextElement();

                    String name = contact.getString(Contact.FORMATTED_NAME, 0);

                    if (name == null) {

                        name = "-";

                    }

                    String number = getStringField(contact, Contact.TEL);

                    if (number == null) {

                        number = "-";

                    }

                    sb.append(name).append(':').append(number).append('\n');

                }

            }

        }

        return sb.toString();

    }

    private void handleException(Exception e) {

        TextBox textBox = new TextBox("Exception Occured", e.getMessage(), 500,
                0);

        switchDisplayable(null, textBox);

    }

}

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

package com.swayam.j2me.contactz.ui;

import java.io.DataOutputStream;
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
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMItem;

import com.swayam.j2me.contactz.xml.client.contactsynchronizer.ContactSynchronizerService;
import com.swayam.j2me.contactz.xml.client.contactsynchronizer.ContactSynchronizerService_Stub;

/**
 * 
 * @author paawak
 */
public class SyncupMidlet extends MIDlet implements CommandListener {

    private static final String EXPORT_CONTACTS = "Export Contacts";
    private static final String EXPORT_VCARDS = "Export VCards";

    private static final String CONTACT_EXPORT_SERVICE_URL = "http://home.paawak.com:8090/MobileContactSynchronizer/ContactSynchronizer";

    private static final String VCARDS_EXPORT_SERVLET_URL = "http://home.paawak.com:8090/MobileContactSynchronizer/VCardsExportProcessor";

    private static final UUID CONTACT_SYNC_SERVER_UUID = new UUID(
            "1115E2609F3CB487100285D", false);

    private List list;
    private ContactSynchronizerService service;

    public SyncupMidlet() {

    }

    protected void startApp() throws MIDletStateChangeException {

        service = new ContactSynchronizerService_Stub(
                CONTACT_EXPORT_SERVICE_URL);

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

                            DataOutputStream dos = streamConnection
                                    .openDataOutputStream();

                            dos.writeInt(565);

                            dos.flush();

                            dos.close();

                        } catch (BluetoothStateException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                };

                // runnable = new Runnable() {
                //
                // public void run() {
                //
                // exportContacts();
                //
                // }
                //
                // };

            } else if (selectedString.equals(EXPORT_VCARDS)) {

                runnable = new Runnable() {

                    public void run() {

                        exportVCards();

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

    private void exportVCards() {

        try {

            PIM pim = PIM.getInstance();
            String[] dataFormats = pim.supportedSerialFormats(PIM.CONTACT_LIST);

            HttpConnection con = (HttpConnection) Connector.open(
                    VCARDS_EXPORT_SERVLET_URL, Connector.WRITE);

            con.setRequestMethod(HttpConnection.POST);
            con.setRequestProperty("Content-Type", "java-internal");

            OutputStream os = con.openOutputStream();

            Enumeration items = pim
                    .openPIMList(PIM.CONTACT_LIST, PIM.READ_ONLY).items();

            while (items.hasMoreElements()) {

                PIMItem item = (PIMItem) items.nextElement();

                pim.toSerialFormat(item, os, "UTF-8", dataFormats[0]);

            }

            os.flush();
            os.close();

            con.close();

        } catch (Exception e) {
            e.printStackTrace();

            switchDisplayable(new Alert(e.getMessage()), getList());

        }

    }

    private void exportContacts() {

        Alert alert;

        // String str = "";

        int recordCount = 1;

        int listCount = 0;

        try {

            PIM pim = PIM.getInstance();

            String[] allContactLists = PIM.getInstance().listPIMLists(
                    PIM.CONTACT_LIST);

            for (; listCount < allContactLists.length; listCount++) {

                String contactListName = allContactLists[listCount];

                ContactList contactList = (ContactList) pim.openPIMList(
                        PIM.CONTACT_LIST, PIM.READ_ONLY, contactListName);

                if (contactList.isSupportedField(Contact.FORMATTED_NAME)
                        && contactList.isSupportedField(Contact.TEL)) {

                    StringBuffer sb = new StringBuffer();

                    Enumeration contacts = contactList.items();

                    // String key = service.startTransaction("p");

                    // switchDisplayable(new Alert(key), getList());

                    recordCount = 1;
                    while (contacts.hasMoreElements()) {

                        Contact contact = (Contact) contacts.nextElement();

                        String name = contact.getString(Contact.FORMATTED_NAME,
                                0);

                        if (name == null) {

                            // str = " NULL Name ";
                            // break;

                            name = "-";

                        }

                        // str = " AA ";

                        String number = getStringField(contact, Contact.TEL);

                        if (number == null) {

                            // str = " NULL Number ";
                            // break;

                            number = "-";

                        }

                        sb.append(name).append(':').append(number).append('\n');

                        // str = " BB ";

                        recordCount++;

                    }

                    service.export(contactListName,
                            Integer.toString(listCount), sb.toString());

                }

            }

            // service.endTransaction(key);

            alert = new Alert(listCount + ", " + recordCount + " : " /*+ str*/);

        } catch (Exception e) {

            alert = new Alert(recordCount + " : " /*+ str*/
                    + "#" + e.getMessage());

        }

        switchDisplayable(alert, getList());

    }

}

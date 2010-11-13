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

import java.util.Enumeration;

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

import com.swayam.j2me.contactz.xml.client.contactsynchronizer.ContactSynchronizerService;
import com.swayam.j2me.contactz.xml.client.contactsynchronizer.ContactSynchronizerService_Stub;

/**
 * 
 * @author paawak
 */
public class SyncupMidlet extends MIDlet implements CommandListener {

    private List list;

    public SyncupMidlet() {

    }

    protected void startApp() throws MIDletStateChangeException {

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
            list.append("Export", null);
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
            if (selectedString.equals("Export")) {

                switchDisplayable(null, getList());

                Thread t = new Thread() {

                    public void run() {

                        ContactSynchronizerService service = new ContactSynchronizerService_Stub(
                                "http://home.paawak.com:8090/MobileContactSynchronizer/ContactSynchronizer");

                        Alert alert;

                        String str = "";

                        try {

                            PIM pim = PIM.getInstance();

                            ContactList contactList = (ContactList) pim
                                    .openPIMList(PIM.CONTACT_LIST,
                                            PIM.READ_ONLY);

                            Enumeration contacts = contactList.items();

                            String key = service.startTransaction("p");

                            // switchDisplayable(new Alert(key), getList());

                            int recordCount = 1;

                            while (contacts.hasMoreElements()) {

                                Contact contact = (Contact) contacts
                                        .nextElement();

                                String name = contact.getString(
                                        Contact.FORMATTED_NAME, 0);

                                if (name == null) {

                                    str = "Name null:" + recordCount;
                                    break;

                                }

                                str = recordCount + " AA ";

                                String number = getStringField(contact,
                                        Contact.TEL);

                                if (number == null) {

                                    number = "";

                                }

                                str = recordCount + " BB ";

                                // str = i + " " + name + "-" + number;

                                recordCount++;

                                service.export(key, name, number);

                            }

                            service.endTransaction(key);

                            alert = new Alert(str);

                        } catch (Exception e) {

                            alert = new Alert(str + e.getMessage());

                        }

                        switchDisplayable(alert, getList());

                    }

                };

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

}

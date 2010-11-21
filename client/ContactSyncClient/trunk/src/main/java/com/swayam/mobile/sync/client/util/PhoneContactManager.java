/*
 * PhoneContactManager.java
 *
 * Created on Nov 21, 2010 9:35:09 PM
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

package com.swayam.mobile.sync.client.util;

import java.util.Enumeration;

import javax.microedition.pim.Contact;
import javax.microedition.pim.ContactList;
import javax.microedition.pim.PIM;
import javax.microedition.pim.PIMException;

/**
 * 
 * @author paawak
 */
public class PhoneContactManager {

    private static final String FAX = "fax";
    private static final String WORK = "work";
    private static final String HOME = "home";
    private static final String CELL = "cell";
    private static final String PAGER = "pager";

    public String getContactDetails() throws PIMException {

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

                        continue;

                    }

                    String number = getContactNumbers(contact);

                    if (!number.trim().equals("")) {
                        sb.append(name).append(':').append(number).append('\n');
                    }

                }

            }

        }

        return sb.toString();

    }

    private String getContactNumbers(Contact contact) {

        int size = contact.countValues(Contact.TEL);

        StringBuffer sb = new StringBuffer();

        for (int count = 0; count < size; count++) {

            int phoneAttribute = contact.getAttributes(Contact.TEL, count);

            String number = contact.getString(Contact.TEL, count);

            String desc;

            if ((phoneAttribute & Contact.ATTR_FAX) == Contact.ATTR_FAX) {

                desc = FAX;

            } else if ((phoneAttribute & Contact.ATTR_HOME) == Contact.ATTR_HOME) {

                desc = HOME;

            } else if ((phoneAttribute & Contact.ATTR_MOBILE) == Contact.ATTR_MOBILE) {

                desc = CELL;

            } else if ((phoneAttribute & Contact.ATTR_PAGER) == Contact.ATTR_PAGER) {

                desc = PAGER;

            } else if ((phoneAttribute & Contact.ATTR_WORK) == Contact.ATTR_WORK) {

                desc = WORK;

            } else {

                desc = CELL;

            }

            sb.append(desc).append(':').append(number).append(';');

        }

        return sb.toString();

    }

}

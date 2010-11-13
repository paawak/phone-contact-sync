package com.swayam.webservices.mobile.contacts;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

/*
 * ContactSynchronizer.java
 *
 * Created on Nov 12, 2010 11:14:34 PM
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

/**
 * 
 * @author paawak
 */
@WebService()
public class ContactSynchronizer {

    private final Map<String, List<Contact>> userContacts;

    public ContactSynchronizer() {

        userContacts = Collections
                .synchronizedMap(new HashMap<String, List<Contact>>());

    }

    @WebMethod(operationName = "startTransaction")
    public String startTransaction(@WebParam(name = "id") String id) {

        List<Contact> list = new ArrayList<Contact>();
        String userKey = id + System.currentTimeMillis();

        userContacts.put(userKey, list);

        return userKey;
    }

    @WebMethod(operationName = "export")
    public String export(@WebParam(name = "key") String key,
            @WebParam(name = "contactName") String contactName,
            @WebParam(name = "contactNumber") String contactNumber) {

        boolean success = false;

        List<Contact> list = userContacts.get(key);

        if (list != null) {

            list.add(new Contact(contactName, contactNumber));

            success = true;

        } else {

            System.err.println("Key not found: " + key);

        }

        return Boolean.toString(success);

    }

    @WebMethod(operationName = "endTransaction")
    public String endTransaction(@WebParam(name = "key") String key) {

        boolean success = false;

        List<Contact> list = userContacts.get(key);

        if (list != null) {

            String fileName = System.getProperty("user.home") + "/" + key;

            try {

                BufferedWriter writer = new BufferedWriter(new FileWriter(
                        fileName));

                for (Contact contact : list) {

                    writer.write(contact.name);
                    writer.write(",");
                    writer.write(contact.number);
                    writer.newLine();

                }

                writer.flush();
                writer.close();

                System.out.println("File written successfully to: " + fileName);

                success = true;

            } catch (Exception e) {

                System.err.println("Error writing file: " + fileName);
                e.printStackTrace();

            }

        } else {

            System.err.println("Key not found: " + key);

        }

        return Boolean.toString(success);

    }

    private static class Contact {

        public final String name;

        public final String number;

        public Contact(String name, String number) {
            this.name = name;
            this.number = number;
        }

    }

}

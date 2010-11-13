package com.swayam.j2me.contactz.xml.client.contactsynchronizer;

public interface ContactSynchronizerService extends java.rmi.Remote {

    /**
     *
     */
    public String export(String key, String contactName, String contactNumber)
            throws java.rmi.RemoteException;

    /**
     *
     */
    public String startTransaction(String id) throws java.rmi.RemoteException;

    /**
     *
     */
    public String endTransaction(String key) throws java.rmi.RemoteException;

}

package com.swayam.j2me.contactz.xml.client.contactsynchronizer;

import javax.microedition.xml.rpc.ComplexType;
import javax.microedition.xml.rpc.Element;
import javax.microedition.xml.rpc.Operation;
import javax.microedition.xml.rpc.Type;
import javax.xml.namespace.QName;
import javax.xml.rpc.JAXRPCException;

public class ContactSynchronizerService_Stub implements
        ContactSynchronizerService, javax.xml.rpc.Stub {

    private String[] _propertyNames;
    private Object[] _propertyValues;

    public ContactSynchronizerService_Stub(String url) {
        _propertyNames = new String[] { ENDPOINT_ADDRESS_PROPERTY };
        _propertyValues = new Object[] { url /*"http://localhost:8090/MobileContactSynchronizer/ContactSynchronizer"*/};
    }

    public void _setProperty(String name, Object value) {
        int size = _propertyNames.length;
        for (int i = 0; i < size; ++i) {
            if (_propertyNames[i].equals(name)) {
                _propertyValues[i] = value;
                return;
            }
        }
        String[] newPropNames = new String[size + 1];
        System.arraycopy(_propertyNames, 0, newPropNames, 0, size);
        _propertyNames = newPropNames;
        Object[] newPropValues = new Object[size + 1];
        System.arraycopy(_propertyValues, 0, newPropValues, 0, size);
        _propertyValues = newPropValues;

        _propertyNames[size] = name;
        _propertyValues[size] = value;
    }

    public Object _getProperty(String name) {
        for (int i = 0; i < _propertyNames.length; ++i) {
            if (_propertyNames[i].equals(name)) {
                return _propertyValues[i];
            }
        }
        if (ENDPOINT_ADDRESS_PROPERTY.equals(name)
                || USERNAME_PROPERTY.equals(name)
                || PASSWORD_PROPERTY.equals(name)) {
            return null;
        }
        if (SESSION_MAINTAIN_PROPERTY.equals(name)) {
            return new Boolean(false);
        }
        throw new JAXRPCException("Stub does not recognize property: " + name);
    }

    protected void _prepOperation(Operation op) {
        for (int i = 0; i < _propertyNames.length; ++i) {
            op.setProperty(_propertyNames[i], _propertyValues[i].toString());
        }
    }

    public String export(String key, String contactName, String contactNumber)
            throws java.rmi.RemoteException {
        Object inputObject[] = new Object[] { key, contactName, contactNumber };

        Operation op = Operation.newInstance(_qname_operation_export,
                _type_export, _type_exportResponse);
        _prepOperation(op);
        op.setProperty(Operation.SOAPACTION_URI_PROPERTY, "");
        Object resultObj;
        try {
            resultObj = op.invoke(inputObject);
        } catch (JAXRPCException e) {
            Throwable cause = e.getLinkedCause();
            if (cause instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) cause;
            }
            throw e;
        }

        return (String) ((Object[]) resultObj)[0];
    }

    public String startTransaction(String id) throws java.rmi.RemoteException {
        Object inputObject[] = new Object[] { id };

        Operation op = Operation.newInstance(_qname_operation_startTransaction,
                _type_startTransaction, _type_startTransactionResponse);
        _prepOperation(op);
        op.setProperty(Operation.SOAPACTION_URI_PROPERTY, "");
        Object resultObj;
        try {
            resultObj = op.invoke(inputObject);
        } catch (JAXRPCException e) {
            Throwable cause = e.getLinkedCause();
            if (cause instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) cause;
            }
            throw e;
        }

        return (String) ((Object[]) resultObj)[0];
    }

    public String endTransaction(String key) throws java.rmi.RemoteException {
        Object inputObject[] = new Object[] { key };

        Operation op = Operation.newInstance(_qname_operation_endTransaction,
                _type_endTransaction, _type_endTransactionResponse);
        _prepOperation(op);
        op.setProperty(Operation.SOAPACTION_URI_PROPERTY, "");
        Object resultObj;
        try {
            resultObj = op.invoke(inputObject);
        } catch (JAXRPCException e) {
            Throwable cause = e.getLinkedCause();
            if (cause instanceof java.rmi.RemoteException) {
                throw (java.rmi.RemoteException) cause;
            }
            throw e;
        }

        return (String) ((Object[]) resultObj)[0];
    }

    protected static final QName _qname_operation_export = new QName(
            "http://contacts.mobile.webservices.swayam.com/", "export");
    protected static final QName _qname_operation_startTransaction = new QName(
            "http://contacts.mobile.webservices.swayam.com/",
            "startTransaction");
    protected static final QName _qname_operation_endTransaction = new QName(
            "http://contacts.mobile.webservices.swayam.com/", "endTransaction");
    protected static final QName _qname_exportResponse = new QName(
            "http://contacts.mobile.webservices.swayam.com/", "exportResponse");
    protected static final QName _qname_export = new QName(
            "http://contacts.mobile.webservices.swayam.com/", "export");
    protected static final QName _qname_endTransactionResponse = new QName(
            "http://contacts.mobile.webservices.swayam.com/",
            "endTransactionResponse");
    protected static final QName _qname_startTransaction = new QName(
            "http://contacts.mobile.webservices.swayam.com/",
            "startTransaction");
    protected static final QName _qname_endTransaction = new QName(
            "http://contacts.mobile.webservices.swayam.com/", "endTransaction");
    protected static final QName _qname_startTransactionResponse = new QName(
            "http://contacts.mobile.webservices.swayam.com/",
            "startTransactionResponse");
    protected static final Element _type_endTransaction;
    protected static final Element _type_startTransaction;
    protected static final Element _type_endTransactionResponse;
    protected static final Element _type_export;
    protected static final Element _type_startTransactionResponse;
    protected static final Element _type_exportResponse;

    static {
        _type_export = new Element(_qname_export, _complexType(new Element[] {
                new Element(new QName("", "key"), Type.STRING, 0, 1, false),
                new Element(new QName("", "contactName"), Type.STRING, 0, 1,
                        false),
                new Element(new QName("", "contactNumber"), Type.STRING, 0, 1,
                        false) }), 1, 1, false);
        _type_exportResponse = new Element(_qname_exportResponse,
                _complexType(new Element[] { new Element(
                        new QName("", "return"), Type.STRING, 0, 1, false) }),
                1, 1, false);
        _type_endTransactionResponse = new Element(
                _qname_endTransactionResponse,
                _complexType(new Element[] { new Element(
                        new QName("", "return"), Type.STRING, 0, 1, false) }),
                1, 1, false);
        _type_startTransaction = new Element(_qname_startTransaction,
                _complexType(new Element[] { new Element(new QName("", "id"),
                        Type.STRING, 0, 1, false) }), 1, 1, false);
        _type_endTransaction = new Element(_qname_endTransaction,
                _complexType(new Element[] { new Element(new QName("", "key"),
                        Type.STRING, 0, 1, false) }), 1, 1, false);
        _type_startTransactionResponse = new Element(
                _qname_startTransactionResponse,
                _complexType(new Element[] { new Element(
                        new QName("", "return"), Type.STRING, 0, 1, false) }),
                1, 1, false);
    }

    private static ComplexType _complexType(Element[] elements) {
        ComplexType result = new ComplexType();
        result.elements = elements;
        return result;
    }
}

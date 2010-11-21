/*
 * SyncWriter.java
 *
 * Created on Nov 21, 2010 3:00:03 PM
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

package com.swayam.mobile.sync.client.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 
 * @author paawak
 */
public class SyncWriter {

    public void write(OutputStream os, String data) throws IOException {

        DataOutputStream ds = new DataOutputStream(os);
        ds.writeUTF(data);
        ds.flush();
        ds.close();
        os.close();

    }

}

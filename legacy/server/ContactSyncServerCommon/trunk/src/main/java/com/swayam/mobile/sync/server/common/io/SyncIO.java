/*
 * SyncIO.java
 *
 * Created on Nov 21, 2010 2:37:30 PM
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

package com.swayam.mobile.sync.server.common.io;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author paawak
 */
public class SyncIO {

    public void read(InputStream is) throws IOException {

        DataInputStream di = new DataInputStream(is);

        String data = di.readUTF();

        System.out
                .println("**************************************************************");
        System.out.println(data);
        System.out
                .println("**************************************************************");

    }

}

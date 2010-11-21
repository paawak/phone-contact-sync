/*
 * Logger.java
 *
 * Created on Nov 21, 2010 7:23:32 PM
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

package com.swayam.mobile.sync.client.debug;

/**
 * 
 * @author paawak
 */
public class Logger {

    private final StringBuffer sb;

    public Logger(Class clazz) {

        sb = new StringBuffer(clazz.getName());
        sb.append('\n');

    }

    public void log(String str) {

        sb.append(str).append('\n');

    }

    public String getLogs() {

        return sb.toString();

    }

}

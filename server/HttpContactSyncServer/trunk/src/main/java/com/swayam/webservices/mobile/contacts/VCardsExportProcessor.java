package com.swayam.webservices.mobile.contacts;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class VCardsExportProcessor
 */
public class VCardsExportProcessor extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public VCardsExportProcessor() {

    }

    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        processRequest(request, response);

    }

    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        processRequest(request, response);

    }

    private void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

        ServletInputStream is = request.getInputStream();

        StringBuilder sb = new StringBuilder();

        while (true) {

            int character = is.read();

            if (character == -1) {
                break;
            }

            sb.append((char) character);

        }

        is.close();

        System.out.println(sb.toString());

    }

}

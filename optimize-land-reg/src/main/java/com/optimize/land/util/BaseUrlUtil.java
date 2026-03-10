package com.optimize.land.util;

import jakarta.servlet.http.HttpServletRequest;

public class BaseUrlUtil {
    private static HttpServletRequest servletRequest;

    private BaseUrlUtil() {
    }

    public static void instance(HttpServletRequest request) {
        servletRequest = request;
    }

    public static String getBaseUrl() {
        String scheme = servletRequest.getScheme(); // http ou https
        String serverName = servletRequest.getServerName(); // Nom de l'hôte (ex: localhost ou domaine)
        int serverPort = servletRequest.getServerPort(); // Port utilisé
        String contextPath = servletRequest.getContextPath(); // Contexte de l'application

        // Construire l'URL de base
        return scheme + "://" + serverName + (serverPort != 80 && serverPort != 443 ? ":" + serverPort : "") + contextPath;
    }
}

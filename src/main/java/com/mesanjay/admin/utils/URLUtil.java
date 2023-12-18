package com.mesanjay.admin.utils;

import jakarta.servlet.http.HttpServletRequest;

public class URLUtil {

    public static String getApplicationURL(HttpServletRequest request){
        String appURL = request.getRequestURL().toString();
        return appURL.replace(request.getServletPath(), "");
    }

}

package com.apress.springrecipes.court.web;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExtensionInterceptor extends HandlerInterceptorAdapter {

    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response, Object handler,
                           //Model model) throws Exception {
                           ModelAndView modelAndView) throws Exception {

        String reportName = null;
        String reportDate = request.getQueryString().replace("date=", "").replace("-", "_");
        if (request.getServletPath().endsWith(".pdf")) {
            reportName = "ReservationSummary_" + reportDate + ".pdf";
        }
        if (request.getServletPath().endsWith(".xls")) {
            reportName = "ReservationSummary_" + reportDate + ".xls";
        }

        if (reportName != null) {
            response.setHeader("Content-Disposition", "attachment; filename=" + reportName);
        }
    }
}

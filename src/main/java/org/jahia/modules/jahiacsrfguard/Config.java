/*
 * ==========================================================================================
 * =                            JAHIA'S ENTERPRISE DISTRIBUTION                             =
 * ==========================================================================================
 *
 *                                  http://www.jahia.com
 *
 * JAHIA'S ENTERPRISE DISTRIBUTIONS LICENSING - IMPORTANT INFORMATION
 * ==========================================================================================
 *
 *     Copyright (C) 2002-2021 Jahia Solutions Group. All rights reserved.
 *
 *     This file is part of a Jahia's Enterprise Distribution.
 *
 *     Jahia's Enterprise Distributions must be used in accordance with the terms
 *     contained in the Jahia Solutions Group Terms & Conditions as well as
 *     the Jahia Sustainable Enterprise License (JSEL).
 *
 *     For questions regarding licensing, support, production usage...
 *     please contact our team at sales@jahia.com or go to http://www.jahia.com/license.
 *
 * ==========================================================================================
 */
package org.jahia.modules.jahiacsrfguard;

import org.jahia.modules.jahiacsrfguard.filters.CsrfGuardServletFilterWrapper;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Config {

    private CsrfGuardServletFilterWrapper filter;

    private List<Pattern> urlPatterns;
    private List<Pattern> whitelist;

    public void setFilter(CsrfGuardServletFilterWrapper filter) {
        this.filter = filter;
    }

    public void init() {
        filter.registerConfig(this);
    }

    public void destroy() {
        filter.unregisterConfig(this);
    }

    public void setUrlPatterns(String urlPatterns) {
        this.urlPatterns = Arrays.stream(urlPatterns.split(",")).map(String::trim).map(Config::createUrlPattern).collect(Collectors.toList());
    }

    public void setWhitelist(String whitelist) {
        this.whitelist = Arrays.stream(whitelist.split(",")).map(String::trim).map(Config::createUrlPattern).collect(Collectors.toList());
    }

    public static Pattern createUrlPattern(String pattern) {
        String patternToUse = pattern;
        if (!pattern.contains("*")) {
            patternToUse = pattern + (pattern.endsWith("/") ? "*" : "/*");
        }
        patternToUse = patternToUse.replace(".", "\\.");
        patternToUse = patternToUse.replaceAll("([^\\\\])\\*", "$1.*");
        patternToUse = patternToUse.replaceAll("^\\*", ".*");
        return Pattern.compile(patternToUse);
    }

    public boolean isFiltered(ServletRequest request) {
        if (urlPatterns == null) {
            return false;
        }

        String uri = ((HttpServletRequest) request).getRequestURI();
        return urlPatterns.stream().anyMatch(pattern -> pattern.matcher(uri).matches());
    }

    public boolean isWhiteListed(ServletRequest request) {
        if (whitelist == null) {
            return false;
        }
        String uri = ((HttpServletRequest) request).getRequestURI();
        return whitelist.stream().anyMatch(pattern -> pattern.matcher(uri).matches());
    }

}

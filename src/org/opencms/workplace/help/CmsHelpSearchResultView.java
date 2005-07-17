/*
 * File   : $Source: /alkacon/cvs/opencms/src/org/opencms/workplace/help/CmsHelpSearchResultView.java,v $
 * Date   : $Date: 2005/07/17 13:42:06 $
 * Version: $Revision: 1.1 $
 *
 * This library is part of OpenCms -
 * the Open Source Content Mananagement System
 *
 * Copyright (c) 2005 Alkacon Software GmbH (http://www.alkacon.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * For further information about Alkacon Software GmbH, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.workplace.help;

import org.opencms.file.CmsObject;
import org.opencms.jsp.CmsJspActionElement;
import org.opencms.search.CmsSearch;
import org.opencms.search.CmsSearchResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

/**
 * Displays the result of a <code>{@link org.opencms.search.CmsSearch}</code>.<p>
 * 
 * Requires the following request parameters (see constructor): 
 * <ul>
 *  <li>
 *  index:<br>the String identifying the required search index. 
 *  <li>
 *  query:<br>the search query to run.
 * </ul>
 * <p>
 * 
 * @author Achim Westermann
 * 
 * @version $Revision: 1.1 $
 * 
 * @since 6.0.0
 */
public class CmsHelpSearchResultView {

    /** The CmsJspActionElement to use. **/
    private CmsJspActionElement m_jsp;

    private CmsSearch m_search;

    /** The url to a proprietary search url (different from m_jsp.getRequestContext().getUri). **/
    private String m_searchRessourceUrl;

    /**
     * Constructor with arguments for construction of a <code>{@link CmsJspActionElement}</code>. <p>
     * 
     * @param pageContext the page context to use 
     * @param request the current request
     * @param response the current response
     */
    public CmsHelpSearchResultView(PageContext pageContext, HttpServletRequest request, HttpServletResponse response) {

        this(new CmsJspActionElement(pageContext, request, response));

    }

    /**
     * Constructor with the action element to use. <p>
     * 
     * @param action the action element to use
     */
    public CmsHelpSearchResultView(CmsJspActionElement action) {

        m_jsp = action;
    }

    private void initSearch(CmsSearch search) {

        //  Collect the objects required to access the OpenCms VFS from the request
        CmsObject cmsObject = m_jsp.getCmsObject();

        //     get locale and message properties
        String locale = m_jsp.property("locale", "search", "en").toLowerCase();

        ServletRequest request = m_jsp.getRequest();
        search.init(cmsObject);
        search.setField(new String[] {"title", "keywords", "description", "content"});
        String index = m_jsp.property("Title", "/", m_jsp.getRequestContext().getSiteRoot());
        index += " (" + locale + ")";
        search.setIndex(request.getParameter("index"));
        search.setMatchesPerPage(10);
        search.setDisplayPages(7);
        search.setSearchRoot("/system/workplace/locales/");

        String query = request.getParameter("query");
        search.setQuery(query);
        m_search = search;
    }

    /**
     * Returns the formatted search results.<p>
     * 
     * @param search the preconfigured search bean 
     * @return the formatted search results
     */
    public String displaySearchResult(CmsSearch search) {

        initSearch(search);
        StringBuffer result = new StringBuffer(800);

        List searchResult;
        if (m_search.getQuery() == null || "".equals(m_search.getQuery().trim())) {
            m_search.setQuery("");
            searchResult = new ArrayList();
        } else {
            searchResult = m_search.getSearchResult();
        }

        Locale locale = m_jsp.getRequestContext().getLocale();
        HttpServletRequest request = m_jsp.getRequest();
        // get the action to perform from the request
        String action = request.getParameter("action");

        if (action != null && searchResult == null) {
            result.append("<p class=\"formerror\">\n");
            if (m_search.getLastException() != null) {

                result.append(Messages.get().key(locale, Messages.GUI_HELP_SEARCH_UNAVAILABLE_0, null));
                result.append("\n<!-- ").append(m_search.getLastException().toString());
                result.append(" //-->\n");
            } else {
                result.append(Messages.get().key(
                    locale,
                    Messages.GUI_HELP_SEARCH_NOMATCH_1,
                    new Object[] {m_search.getQuery()}));
                result.append("\n");
            }
            result.append("</p>\n");
        } else if (action != null && searchResult.size() <= 0) {
            result.append("<p class=\"formerror\">\n");
            result.append(Messages.get().key(
                locale,
                Messages.GUI_HELP_SEARCH_NOMATCH_1,
                new Object[] {m_search.getQuery()}));
            result.append("\n");
            result.append("</p>\n");
        } else if (action != null && searchResult.size() > 0) {
            result.append("<p>\n");
            result.append(Messages.get().key(locale, Messages.GUI_HELP_SEARCH_RESULT_START_0, null));
            result.append("\n");
            result.append("</p>\n<p>\n");

            Iterator iterator = searchResult.iterator();
            while (iterator.hasNext()) {
                CmsSearchResult entry = (CmsSearchResult)iterator.next();
                result.append("\n<div class=\"searchResult\"><a class=\"navhelp\" href=\"");
                result.append(m_jsp.link(m_jsp.getRequestContext().removeSiteRoot(entry.getPath())));
                result.append("\">");
                result.append(entry.getTitle());
                result.append("</a>");
                result.append("&nbsp;(").append(entry.getScore()).append("&nbsp;%)</div>\n");
            }

            result.append("</p>\n");

            // search page links below results
            if (m_search.getPreviousUrl() != null || m_search.getNextUrl() != null) {
                result.append("<p>");
                if (m_search.getPreviousUrl() != null) {

                    result.append("<a href=\"");
                    result.append(getSearchPageLink(m_jsp.link(m_search.getPreviousUrl())));
                    result.append("\">");
                    result.append(Messages.get().key(locale, Messages.GUI_HELP_BUTTON_BACK_0, null));
                    result.append(" &lt;&lt;</a>&nbsp;&nbsp;\n");
                }
                Map pageLinks = m_search.getPageLinks();
                Iterator i = pageLinks.keySet().iterator();
                while (i.hasNext()) {
                    int pageNumber = ((Integer)i.next()).intValue();

                    result.append(" ");
                    if (pageNumber != m_search.getPage()) {
                        result.append("<a href=\"").append(
                            getSearchPageLink(m_jsp.link((String)pageLinks.get(new Integer(pageNumber)))));
                        result.append("\" target=\"_self\">").append(pageNumber).append("</a>\n");
                    } else {
                        result.append(pageNumber);
                    }
                }
                if (m_search.getNextUrl() != null) {
                    result.append("&nbsp;&nbsp;&nbsp;<a href=\"");
                    result.append(getSearchPageLink(m_jsp.link(m_search.getNextUrl())));
                    result.append("\">&gt;&gt;");
                    result.append(Messages.get().key(locale, Messages.GUI_HELP_BUTTON_NEXT_0, null));
                    result.append("</a>\n");
                }
                result.append("</p>\n");
            }

        }
        return result.toString();

    }

    /**
     * Returns the resource uri to the search page with respect to the 
     * optionally configured value <code>{@link #setSearchRessourceUrl(String)}</code> 
     * with the request parameters of the given argument.<p>
     * 
     * @param link the suggestion of the search result bean ( a previous, next or page number url)
     * @return the resource uri to the search page with respect to the 
     *         optionally configured value <code>{@link #setSearchRessourceUrl(String)}</code> 
     *         with the request parameters of the given argument
     */
    private String getSearchPageLink(String link) {

        if (m_searchRessourceUrl != null) {
            // replace the url, keep the params:
            String pageParams = "";
            int paramIndex = link.indexOf('?');
            if (paramIndex > 0) {
                pageParams = link.substring(paramIndex);
            }
            link = new StringBuffer(m_searchRessourceUrl).append(pageParams).toString();
        }
        return link;
    }

    /**
     * Set a proprietary resource uri for the search page. <p>
     * 
     * This is optional but allows to override the standard search result links 
     * (for next or previous pages) that point to 
     * <code>getJsp().getRequestContext().getUri()</code> whenever the search 
     * uri is element of some template and should not be linked directly.<p>
     * 
     * @param uri the proprietary resource uri for the search page
     */
    public void setSearchRessourceUrl(String uri) {

        this.m_searchRessourceUrl = uri;
    }
}

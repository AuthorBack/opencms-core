/*
 * This library is part of OpenCms -
 * the Open Source Content Management System
 *
 * Copyright (c) Alkacon Software GmbH (http://www.alkacon.com)
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
 * For further information about Alkacon Software, please see the
 * company website: http://www.alkacon.com
 *
 * For further information about OpenCms, please see the
 * project website: http://www.opencms.org
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package org.opencms.jsp.search.config;

import java.util.List;

/** Configuration common to all facets. Used as base interface for all special facet interfaces. */
public interface I_CmsSearchConfigurationFacet {

    /** Sort orders available for Solr facet entries. */
    public enum SortOrder {
        /**
         * Sort by the number of hits.
         */
        count, /**
                * Sort alphabetically.
                */
        index
    }

    /** Returns the facet specific request parameter used to send the information if the maximum number of facet entries should be ignored.
     * @return The facet specific request parameter used to send the information if the maximum number of facet entries should be ignored.
     */
    String getIgnoreMaxParamKey();

    /** Returns true if the facet filters, such that only documents with all checked facet entries appear, otherwise false.
     * @return A flag, indicating if the facet's filters are concatenated by AND (or OR).
     */
    boolean getIsAndFacet();

    /** Returns the label that is intended to be displayed over the facet's entries.
     * @return The label that is intended to be displayed over the facet's entries.
     */
    String getLabel();

    /** Returns the maximal number of entries that should be shown in the facet.
     * @return The maximal number of entries that should be shown in the facet. (Solr: facet.limit)
     */
    Integer getLimit();

    /** Returns the minimal number of hits necessary to show a facet entry.
     * @return The minimal number of hits necessary to show a facet entry. (Solr: facet.mincount)
     */
    Integer getMinCount();

    /** Returns the name used to identify the facet.
     * @return The name used to identify the facet.
     */
    String getName();

    /** Returns the facet specific request parameter used to send the checked facet entries.
     * @return The facet specific request parameter used to send the checked facet entries.
     */
    String getParamKey();

    /** A list of facet-entries that should be preselected, if the search form is rendered the first time.
     * @return The list of facet-entries that should be preselected, if the search form is rendered the first time.
     */
    List<String> getPreSelection();

    /** Returns the sort order that should be used for the facet entries (either "count" or "index").
     * @return The sort order that should be used for the facet entries (either "count" or "index"). (Solr: facet.sort)
     */
    SortOrder getSortOrder();
}

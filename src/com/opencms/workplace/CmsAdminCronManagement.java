/*
* File   : $Source: /alkacon/cvs/opencms/src/com/opencms/workplace/Attic/CmsAdminCronManagement.java,v $
* Date   : $Date: 2001/11/16 15:21:51 $
* Version: $Revision: 1.1 $
*
* This library is part of OpenCms -
* the Open Source Content Mananagement System
*
* Copyright (C) 2001  The OpenCms Group
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* For further information about OpenCms, please see the
* OpenCms Website: http://www.opencms.org
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
*/

package com.opencms.workplace;

import com.opencms.file.*;
import com.opencms.core.*;
import com.opencms.util.*;
import com.opencms.template.*;
import java.text.*;
import java.util.*;
import javax.servlet.http.*;

/**
 * Template class for displaying OpenCms workplace administration synchronisation properties.
 *
 * Creation date: ()
 * @author: Edna Falkenhan
 */
public class CmsAdminCronManagement extends CmsWorkplaceDefault implements I_CmsConstants {

    /**
     * Gets the content of a defined section in a given template file and its subtemplates
     * with the given parameters.
     *
     * @see getContent(CmsObject cms, String templateFile, String elementName, Hashtable parameters)
     * @param cms CmsObject Object for accessing system resources.
     * @param templateFile Filename of the template file.
     * @param elementName Element name of this template in our parent template.
     * @param parameters Hashtable with all template class parameters.
     * @param templateSelector template section that should be processed.
     */
    public byte[] getContent(CmsObject cms, String templateFile, String elementName, Hashtable parameters, String templateSelector) throws CmsException {

        CmsXmlTemplateFile templateDocument = getOwnTemplateFile(cms, templateFile, elementName, parameters, templateSelector);

        if("cancel".equals((String)parameters.get("step"))){
            templateSelector = "done";
        } else if(parameters.containsKey("table")) {
            // store the updated crontable
            cms.writeCronTable((String)parameters.get("table"));
            templateSelector = "done";
        } else {
            templateDocument.setData("table", Encoder.escape(cms.readCronTable()));
        }
        // Now load the template file and start the processing
        return startProcessing(cms, templateDocument, elementName, parameters, templateSelector);
    }
}
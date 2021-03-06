/***************************** BEGIN LICENSE BLOCK ***************************

 The contents of this file are subject to the Mozilla Public License Version
 1.1 (the "License"); you may not use this file except in compliance with
 the License. You may obtain a copy of the License at
 http://www.mozilla.org/MPL/MPL-1.1.html
 
 Software distributed under the License is distributed on an "AS IS" basis,
 WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 for the specific language governing rights and limitations under the License.
 
 The Original Code is the "OGC Service Framework".
 
 The Initial Developer of the Original Code is Sensia Software LLC.
 Portions created by the Initial Developer are Copyright (C) 2014
 the Initial Developer. All Rights Reserved.
 
 Please Contact Alexandre Robin or
 Mike Botts <mike.botts@botts-inc.net> for more information.
 
 Contributor(s): 
    Alexandre Robin
 
******************************* END LICENSE BLOCK ***************************/

package org.vast.ows.sos;

import java.util.ArrayList;
import java.util.List;


/**
 * <p>
 * Container for SOS InsertSensor request parameters
 * </p>
 *
 * @author Alex Robin
 * @date Feb 02, 2014
 * */
public class InsertSensorRequest extends org.vast.ows.swe.InsertSensorRequest
{
    protected List<String> observationTypes;
    protected List<String> foiTypes;
    
	
	public InsertSensorRequest()
	{
		super(SOSUtils.SOS);
		observationTypes = new ArrayList<String>(2);
		foiTypes = new ArrayList<String>(2);
	}


    public List<String> getObservationTypes()
    {
        return observationTypes;
    }

    public void setObservationTypes(List<String> observationTypes) {
        this.observationTypes = observationTypes;
    }

    public void setFoiTypes(List<String> foiTypes) {
        this.foiTypes = foiTypes;
    }

    public List<String> getFoiTypes()
    {
        return foiTypes;
    }   
}

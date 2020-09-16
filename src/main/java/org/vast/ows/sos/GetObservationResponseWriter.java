package org.vast.ows.sos;

import com.sensorweb.datacenter.util.DataCenterUtils;
import net.opengis.swe.v20.DataComponent;
import net.opengis.swe.v20.DataEncoding;
import org.vast.ogc.OGCRegistry;
import org.vast.ogc.om.IObservation;
import org.vast.ogc.om.OMUtils;
import org.vast.ows.OWSException;
import org.vast.swe.SWEUtils;
import org.vast.xml.DOMHelper;
import org.vast.xml.XMLWriterException;
import org.vast.ows.swe.SWEResponseWriter;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.bind.annotation.DomHandler;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

public class GetObservationResponseWriter extends SWEResponseWriter<GetObservationResponse> {

    protected SWEUtils sweUtils = new SWEUtils(SWEUtils.V2_0);
    protected OMUtils omUtils = new OMUtils(OMUtils.V2_0);

    public Element buildXMLResponse(DOMHelper dom, GetObservationResponse response, String version) throws OWSException
    {
        try
        {
            dom.addUserPrefix("sos", OGCRegistry.getNamespaceURI(SOSUtils.SOS, version));

            // root element
            Element rootElt = dom.createElement("sos:" + response.getMessageType());

            // write extensions
            writeExtensions(dom, rootElt, response);

            //ObservationData
            List<IObservation> observations = response.getObservations();
            if (observations!=null) {
                for (IObservation iObservation:observations) {
                    Element observationData = dom.addElement(rootElt, "sos:observationData");
                    Element data = omUtils.writeObservation(dom, iObservation, "2.0");
                    observationData.appendChild(data);
                }
            }
            return rootElt;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

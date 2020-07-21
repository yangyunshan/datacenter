package org.vast.ows.sos;

import net.opengis.fes.v20.BinaryTemporalOp;
import net.opengis.fes.v20.SpatialOps;
import org.vast.ogc.om.IObservation;
import org.vast.ows.OWSResponse;

import java.util.Set;

public class GetObservationResponse extends OWSResponse {
    protected IObservation observation;

    public GetObservationResponse() {
        service = "SOS";
        messageType = "GetObservationResponse";
    }

    public IObservation getObservation() {
        return observation;
    }

    public void setObservation(IObservation observation) {
        this.observation = observation;
    }
}

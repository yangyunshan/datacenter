package org.vast.ows.sos;

import net.opengis.fes.v20.BinaryTemporalOp;
import net.opengis.fes.v20.SpatialOps;
import org.vast.ogc.om.IObservation;
import org.vast.ows.OWSResponse;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class GetObservationResponse extends OWSResponse {
    protected IObservation observation;
    protected List<IObservation> observations;

    public GetObservationResponse() {
        service = "SOS";
        messageType = "GetObservationResponse";
        observations = new LinkedList<IObservation>();
    }

    public IObservation getObservation() {
        return observation;
    }

    public void setObservation(IObservation observation) {
        this.observation = observation;
    }

    public List<IObservation> getObservations() {
        return observations;
    }

    public void setObservations(List<IObservation> observations) {
        this.observations = observations;
    }
}

package com.avaliacao.util;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import java.util.logging.Logger;

public class PhaseListenerUtil implements PhaseListener {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(PhaseListenerUtil.class.getName());

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        PhaseId phaseId = event.getPhaseId();
        long startTime = System.nanoTime();
        event.getFacesContext().getExternalContext().getRequestMap().put("phaseStartTime_" + phaseId.getOrdinal(), startTime);
        LOG.fine("JSF Phase START: " + phaseId.toString() + " (" + phaseId.getOrdinal() + ")");
    }

    @Override
    public void afterPhase(PhaseEvent event) {
        PhaseId phaseId = event.getPhaseId();
        Long startTime = (Long) event.getFacesContext().getExternalContext().getRequestMap().get("phaseStartTime_" + phaseId.getOrdinal());
        if (startTime != null) {
            long durationMs = (System.nanoTime() - startTime) / 1_000_000;
            LOG.info(String.format("JSF Phase END: %s (%d) - Duration: %d ms", phaseId.toString(), phaseId.getOrdinal(), durationMs));
        } else {
            LOG.fine("JSF Phase END: " + phaseId.toString() + " (" + phaseId.getOrdinal() + ") - No start time recorded");
        }
    }
}
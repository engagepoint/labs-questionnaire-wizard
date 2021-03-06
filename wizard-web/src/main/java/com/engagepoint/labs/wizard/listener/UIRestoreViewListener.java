package com.engagepoint.labs.wizard.listener;

import com.sun.faces.RIConstants;
import com.sun.faces.renderkit.RenderKitUtils;
import com.sun.faces.util.ComponentStruct;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.faces.render.ResponseStateManager;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by artem on 19.02.14.
 */
public class UIRestoreViewListener implements PhaseListener {
    private static final String DYNAMIC_ACTIONS_RENDERKIT = "HTML_BASIC";

    @Override
    public void afterPhase(PhaseEvent event) {
        //NOP
    }

    @Override
    public void beforePhase(PhaseEvent event) {
        FacesContext context = event.getFacesContext();
        ResponseStateManager rsm = RenderKitUtils.getResponseStateManager(context, DYNAMIC_ACTIONS_RENDERKIT);
        HttpServletRequest httpRequest = (HttpServletRequest) context.getExternalContext().getRequest();

        Object[] rawState = (Object[]) rsm.getState(context, httpRequest.getRequestURI().replaceFirst(httpRequest.getContextPath(), "").split("\\?")[0]);
        if (rawState == null) {
            return;
        }

        Map<String, Object> state = (Map<String, Object>) rawState[1];
        if (state == null) {
            return;
        }

        List<Object> savedActions = (List<Object>) state.get(RIConstants.DYNAMIC_ACTIONS);
        if (savedActions == null) {
            return;
        }

        updateComponent(event, savedActions);
    }

    private void updateComponent(PhaseEvent event, List<Object> savedActions) {
        for (Iterator<Object> iterator = savedActions.iterator(); iterator.hasNext(); ) {
            Object object = iterator.next();
            ComponentStruct action = new ComponentStruct();
            action.restoreState(event.getFacesContext(), object);

            if (ComponentStruct.ADD.equals(action.action)) {
                continue;
            }
            if (action.clientId.startsWith("dateStubb-")) {
                iterator.remove();
            }
            if (action.clientId.startsWith("leftmenuid")) {
                iterator.remove();
            }
            if (action.clientId.startsWith("maincontentid")) {
                iterator.remove();
            }
        }
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RESTORE_VIEW;
    }
}

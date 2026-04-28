package it.pagopa.pn.commons.log;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import static it.pagopa.pn.commons.lollipop.LollipopHeaders.*;
import static it.pagopa.pn.commons.utils.MDCUtils.*;

/**
 * This non-reactive RequestFilter reads the request headers configured in 'pn.log.*' properties
 * and put them into the MDC log map in order to correlate logs between microservices calls.
 */
@Slf4j
public class MDCRequestFilter extends OncePerRequestFilter {

    @Value("${pn.log.trace-id-header}")
    private String traceIdHeader;
    @Value("${pn.log.jti-header}")
    private String jtiHeader;
    @Value("${pn.log.pn-uid-header}")
    private String pnUidHeader;
    @Value("${pn.log.cx-id-header}")
    private String cxIdHeader;
    @Value("${pn.log.pn-cx-type-header}")
    private String pnCxTypeHeader;
    @Value("${pn.log.pn-cx-groups-header}")
    private String pnCxGroupsHeader;
    @Value("${pn.log.pn-cx-role-header}")
    private String pnCxRoleHeader;
    @Value("${pn.log.pn-source-channel-header}")
    private String pnSourceChannelHeader;
    @Value("${pn.log.pn-source-channel-details-header}")
    private String pnSourceChannelDetailsHeader;

    @Override
    protected void doFilterInternal(
        @NotNull HttpServletRequest request,
        @NotNull HttpServletResponse response,
        FilterChain chain
    ) throws ServletException, IOException {
        try {
            // Main Headers
            addToMDC(request, traceIdHeader, MDC_TRACE_ID_KEY, "trace_id:" + UUID.randomUUID());
            addToMDC(request, jtiHeader, MDC_JTI_KEY, null);
            addToMDC(request, pnUidHeader, MDC_PN_UID_KEY, null);
            addToMDC(request, cxIdHeader, MDC_CX_ID_KEY, null);
            addToMDC(request, pnCxTypeHeader, MDC_PN_CX_TYPE_KEY, null);
            addToMDC(request, pnCxGroupsHeader, MDC_PN_CX_GROUPS_KEY, null);
            addToMDC(request, pnCxRoleHeader, MDC_PN_CX_ROLE_KEY, null);
            addToMDC(request, pnSourceChannelHeader, MDC_PN_SOURCE_CHANNEL_KEY, null);
            addToMDC(request, pnSourceChannelDetailsHeader, MDC_PN_SOURCE_CHANNEL_DETAILS_KEY, null);
            // Lollipop Headers
            addToMDC(request, LOLLIPOP_ORIGINAL_URL, MDC_PN_LP_ORIGINAL_URL, null);
            addToMDC(request, LOLLIPOP_ORIGINAL_METHOD, MDC_PN_LP_ORIGINAL_METHOD, null);
            addToMDC(request, LOLLIPOP_PUBLIC_KEY, MDC_PN_LP_PUBLIC_KEY, null);
            addToMDC(request, LOLLIPOP_ASSERTION_REF, MDC_PN_LP_ASSERTION_REF, null);
            addToMDC(request, LOLLIPOP_ASSERTION_TYPE, MDC_PN_LP_ASSERTION_TYPE, null);
            addToMDC(request, LOLLIPOP_SIGNATURE_INPUT, MDC_PN_LP_SIGNATURE_INPUT, null);
            addToMDC(request, LOLLIPOP_SIGNATURE, MDC_PN_LP_SIGNATURE, null);
            chain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_TRACE_ID_KEY);
            MDC.remove(MDC_JTI_KEY);
            MDC.remove(MDC_PN_UID_KEY);
            MDC.remove(MDC_CX_ID_KEY);
            MDC.remove(MDC_PN_CX_TYPE_KEY);
            MDC.remove(MDC_PN_CX_GROUPS_KEY);
            MDC.remove(MDC_PN_CX_ROLE_KEY);
            MDC.remove(MDC_PN_SOURCE_CHANNEL_KEY);
            MDC.remove(MDC_PN_SOURCE_CHANNEL_DETAILS_KEY);
        }
    }

    private void addToMDC(HttpServletRequest request, String headerName, String mdcKey, String fallbackValue) {
        String value = request.getHeader(headerName);
        if (value != null) {
            MDC.put(mdcKey, value);
        } else if (StringUtils.isNotBlank(fallbackValue)) {
            MDC.put(mdcKey, fallbackValue);
        }
    }
}

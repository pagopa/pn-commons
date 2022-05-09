package it.pagopa.pn.commons.log;

public enum PnAuditLogEventType {
    AUD_ACC_LOGIN(PnAuditLogMarker.AUDIT5Y),
    AUD_ACC_LOGOUT(PnAuditLogMarker.AUDIT5Y),
    AUD_NT_PRELOAD(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_LOAD(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_INSERT(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_CHECK(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_VALID(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_ARR(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_STATUS(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_NEWDOC(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_VIEW(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_DOCOPEN(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_LEGALOPEN(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_PAYMENT(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_REQCOST(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_DOWTIME(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_INSUP(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_VERIFY(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_DEL(PnAuditLogMarker.AUDIT10Y),
    AUD_UC_INSUP(PnAuditLogMarker.AUDIT10Y),
    AUD_AN_SEND(PnAuditLogMarker.AUDIT10Y),
    AUD_AN_RECEIVE(PnAuditLogMarker.AUDIT10Y),
    AUD_DL_CREATE(PnAuditLogMarker.AUDIT10Y),
    AUD_DL_ACCEPT(PnAuditLogMarker.AUDIT10Y),
    AUD_DL_REJECT(PnAuditLogMarker.AUDIT10Y),
    AUD_DL_REVOKE(PnAuditLogMarker.AUDIT10Y),
    AUD_DL_EXPIRE(PnAuditLogMarker.AUDIT10Y),
    AUD_AK_CREATE(PnAuditLogMarker.AUDIT10Y),
    AUD_AK_VIEW(PnAuditLogMarker.AUDIT10Y),
    AUD_AK_ROTATE(PnAuditLogMarker.AUDIT10Y),
    AUD_AK_BLOCK(PnAuditLogMarker.AUDIT10Y),
    AUD_AK_REACTIVATE(PnAuditLogMarker.AUDIT10Y),
    AUD_AK_DELETE(PnAuditLogMarker.AUDIT10Y);
    
    final org.slf4j.Marker marker;

    PnAuditLogEventType(PnAuditLogMarker auditMarker) {
        this.marker = auditMarker.mark;
    }
}

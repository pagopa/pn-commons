package it.pagopa.pn.commons.log;

/**
 * See documentations <a href="https://pagopa.atlassian.net/wiki/spaces/PN/pages/498466884/Audit+Logs">Audit Logs</a>
 */
public enum PnAuditLogEventType {
    AUD_ACC_LOGIN(PnAuditLogMarker.AUDIT5Y),
    AUD_ACC_LOGOUT(PnAuditLogMarker.AUDIT5Y),
    AUD_NT_PRELOAD(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_LOAD(PnAuditLogMarker.AUDIT10Y), //Load is direct on S3 bucket, cannot be cached by PN code.
    AUD_NT_INSERT(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_CHECK(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_VALID(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_ARR(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_TIMELINE(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_STATUS(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_NEWLEGAL(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_SEARCH_RCP(PnAuditLogMarker.AUDIT5Y),
    AUD_NT_VIEW_RPC(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_DOCOPEN_RCP(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_ATCHOPEN_RCP(PnAuditLogMarker.AUDIT5Y),
    AUD_NT_LEGALOPEN_RCP(PnAuditLogMarker.AUDIT5Y),
    AUD_NT_SEARCH_SND(PnAuditLogMarker.AUDIT5Y),
    AUD_NT_VIEW_SND(PnAuditLogMarker.AUDIT5Y),
    AUD_NT_DOCOPEN_SND(PnAuditLogMarker.AUDIT5Y),
    AUD_NT_ATCHOPEN_SND(PnAuditLogMarker.AUDIT5Y),
    AUD_NT_LEGALOPEN_SND(PnAuditLogMarker.AUDIT5Y),
    AUD_NT_PAYMENT(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_REQCOST(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_REQQR(PnAuditLogMarker.AUDIT10Y),
    AUD_NT_DOWTIME(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_DD_INSUP(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_DD_DEL(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_DA_INSUP(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_DA_DEL(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_DA_IO_INSUP(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_DA_IO_DEL(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_VERIFY_PEC(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_VERIFY_MAIL(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_VERIFY_SMS(PnAuditLogMarker.AUDIT10Y),
    AUD_AB_DEL(PnAuditLogMarker.AUDIT10Y),
    AUD_UC_INSUP(PnAuditLogMarker.AUDIT10Y),
    AUD_AN_SEND(PnAuditLogMarker.AUDIT10Y),
    AUD_AN_RECEIVE(PnAuditLogMarker.AUDIT10Y),
    AUD_DD_SEND(PnAuditLogMarker.AUDIT10Y),
    AUD_DD_RECEIVE(PnAuditLogMarker.AUDIT10Y),
    AUD_AD_SEND_SMS(PnAuditLogMarker.AUDIT5Y),
    AUD_AD_RECEIVE_SMS(PnAuditLogMarker.AUDIT5Y),
    AUD_AD_SEND_EMAIL(PnAuditLogMarker.AUDIT5Y),
    AUD_AD_RECEIVE_EMAIL(PnAuditLogMarker.AUDIT5Y),
    AUD_AD_SEND_IO(PnAuditLogMarker.AUDIT5Y),
    AUD_AD_RECEIVE_IO(PnAuditLogMarker.AUDIT5Y),
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

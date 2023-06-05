package it.pagopa.pn.commons.lollipop;

import java.util.List;

public class LollipopHeaders {

    public static final String LOLLIPOP_ORIGINAL_URL = "x-pagopa-lollipop-original-url";
    public static final String LOLLIPOP_ORIGINAL_METHOD = "x-pagopa-lollipop-original-method";
    public static final String LOLLIPOP_PUBLIC_KEY = "x-pagopa-lollipop-public-key";
    public static final String LOLLIPOP_ASSERTION_REF = "x-pagopa-lollipop-assertion-ref";
    public static final String LOLLIPOP_ASSERTION_TYPE = "x-pagopa-lollipop-assertion-type";
    public static final String LOLLIPOP_AUTH_JWT = "x-pagopa-lollipop-auth-jwt";
    public static final String LOLLIPOP_USER_ID = "x-pagopa-lollipop-user-id";
    public static final String LOLLIPOP_SIGNATURE_INPUT = "signature-input";
    public static final String LOLLIPOP_SIGNATURE = "signature";

    private LollipopHeaders() {}

    public static List<String> getAllLollipopHeaderAuditKeys() {
        return List.of(LOLLIPOP_ORIGINAL_URL, LOLLIPOP_ORIGINAL_METHOD, LOLLIPOP_ASSERTION_REF, LOLLIPOP_ASSERTION_TYPE,
                LOLLIPOP_PUBLIC_KEY, LOLLIPOP_SIGNATURE_INPUT, LOLLIPOP_SIGNATURE);
    }


}

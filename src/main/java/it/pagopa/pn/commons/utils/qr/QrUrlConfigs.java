package it.pagopa.pn.commons.utils.qr;

import it.pagopa.pn.commons.conf.SharedAutoConfiguration;
import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(SharedAutoConfiguration.class)
@Data
@Configuration
public class QrUrlConfigs {

    private String directAccessUrlTemplatePhysical;

    private String directAccessUrlTemplateLegal;

    private String quickAccessUrlAarDetailSuffix;
}

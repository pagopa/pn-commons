package it.pagopa.pn.commons.exceptions;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 *  Interfaccia implementabile da eventuali client che abbiano necessità di gestire annotazioni di validazione custom
 *  o di eseguire l'override rispetto ai codici di base.
 */
public interface IValidationCustomMapper {

    @NotNull Map<String, String> getValidationCodeCustomMapping();
}

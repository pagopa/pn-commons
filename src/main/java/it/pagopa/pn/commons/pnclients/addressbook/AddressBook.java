package it.pagopa.pn.commons.pnclients.addressbook;

import it.pagopa.pn.api.dto.addressbook.AddressBookEntry;

import java.util.Optional;

@Deprecated
public interface AddressBook {

    Optional<AddressBookEntry> getAddresses(String taxId);
}

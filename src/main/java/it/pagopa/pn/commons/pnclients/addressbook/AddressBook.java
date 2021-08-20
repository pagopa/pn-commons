package it.pagopa.pn.commons.pnclients.addressbook;

import it.pagopa.pn.api.dto.addressbook.AddressBookEntry;

public interface AddressBook {

    AddressBookEntry getAddresses( String taxId );
}

package it.pagopa.pn.commons.pnclients.addressbook;

import it.pagopa.pn.api.dto.addressbook.AddressBookEntry;
import it.pagopa.pn.api.dto.notification.NotificationSender;

import java.util.Optional;

public interface AddressBook {
    Optional<AddressBookEntry> getAddresses(String taxId, NotificationSender sender);
}

package com.shopsphere.commerce.service;
import com.shopsphere.commerce.api.AddressRequest;
import com.shopsphere.commerce.api.AddressResponse;
import com.shopsphere.commerce.domain.CustomerAddress;
import com.shopsphere.commerce.repository.CustomerAddressRepository;
import com.shopsphere.identity.repository.UserAccountRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
@Service
public class AddressService {
 private final CustomerAddressRepository addresses; private final UserAccountRepository users;
 public AddressService(CustomerAddressRepository addresses,UserAccountRepository users){this.addresses=addresses;this.users=users;}
 @Transactional(readOnly=true) public List<AddressResponse> list(UUID userId){return addresses.findAllByUser_IdOrderByDefaultAddressDescCreatedAtDesc(userId).stream().map(this::toResponse).toList();}
 @Transactional public AddressResponse create(UUID userId,AddressRequest request){var user=users.findById(userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Account not found"));List<CustomerAddress> existing=addresses.findAllByUser_IdOrderByDefaultAddressDescCreatedAtDesc(userId);boolean makeDefault=request.defaultAddress()||existing.isEmpty();if(makeDefault)existing.forEach(a->a.setDefaultAddress(false));CustomerAddress address=addresses.save(new CustomerAddress(user,request.recipientName().trim(),request.phone().trim(),request.addressLine1().trim(),blankToNull(request.addressLine2()),request.city().trim(),request.state().trim(),request.postalCode().trim(),request.country()==null||request.country().isBlank()?"India":request.country().trim(),makeDefault));return toResponse(address);}
 @Transactional public AddressResponse setDefault(UUID userId,UUID addressId){CustomerAddress selected=addresses.findByIdAndUser_Id(addressId,userId).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND,"Address not found"));addresses.findAllByUser_IdOrderByDefaultAddressDescCreatedAtDesc(userId).forEach(a->a.setDefaultAddress(a.getId().equals(addressId)));return toResponse(selected);}
 private AddressResponse toResponse(CustomerAddress a){return new AddressResponse(a.getId(),a.getRecipientName(),a.getPhone(),a.getAddressLine1(),a.getAddressLine2(),a.getCity(),a.getState(),a.getPostalCode(),a.getCountry(),a.isDefaultAddress());}
 private String blankToNull(String value){return value==null||value.isBlank()?null:value.trim();}
}

package com.nextgen.store.address;

import com.nextgen.store.auth.User;
import com.nextgen.store.auth.UserRepository;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {
  private final AddressRepository addresses;
  private final UserRepository users;
  public AddressController(AddressRepository addresses,UserRepository users){this.addresses=addresses;this.users=users;}

  @GetMapping
  public List<AddressResponse> list(Authentication auth){User user=user(auth);return addresses.findByUserIdOrderByDefaultAddressDescUpdatedAtDesc(user.getId()).stream().map(AddressResponse::from).toList();}

  @PostMapping
  @Transactional
  public AddressResponse create(Authentication auth,@Valid @RequestBody AddressRequest request){User user=user(auth);boolean makeDefault=request.defaultAddress()||addresses.findFirstByUserIdAndDefaultAddressTrue(user.getId()).isEmpty();if(makeDefault)clearDefault(user.getId());Address a=addresses.save(new Address(user,clean(request.fullName()),clean(request.phone()),clean(request.line1()),clean(request.line2()),clean(request.city()),clean(request.state()),clean(request.postalCode()),clean(request.country()==null?"India":request.country()),makeDefault));return AddressResponse.from(a);}

  @PutMapping("/{id}")
  @Transactional
  public AddressResponse update(Authentication auth,@PathVariable Long id,@Valid @RequestBody AddressRequest request){User user=user(auth);Address a=addresses.findByIdAndUserId(id,user.getId()).orElseThrow(()->new NoSuchElementException("Address not found"));if(request.defaultAddress())clearDefault(user.getId());a.update(clean(request.fullName()),clean(request.phone()),clean(request.line1()),clean(request.line2()),clean(request.city()),clean(request.state()),clean(request.postalCode()),clean(request.country()==null?"India":request.country()),request.defaultAddress());return addresses.save(a)==null?null:AddressResponse.from(a);}

  @DeleteMapping("/{id}")
  @Transactional
  public void delete(Authentication auth,@PathVariable Long id){User user=user(auth);Address a=addresses.findByIdAndUserId(id,user.getId()).orElseThrow(()->new NoSuchElementException("Address not found"));boolean wasDefault=a.isDefaultAddress();addresses.delete(a);if(wasDefault)addresses.findByUserIdOrderByDefaultAddressDescUpdatedAtDesc(user.getId()).stream().findFirst().ifPresent(x->{x.setDefaultAddress(true);addresses.save(x);});}

  private User user(Authentication a){return users.findByEmailIgnoreCase(a.getName()).orElseThrow(()->new NoSuchElementException("User not found"));}
  private void clearDefault(Long userId){addresses.findByUserIdOrderByDefaultAddressDescUpdatedAtDesc(userId).forEach(a->{if(a.isDefaultAddress())a.setDefaultAddress(false);});}
  private String clean(String v){return v==null||v.isBlank()?null:v.trim();}
  public record AddressRequest(@NotBlank @Size(max=160) String fullName,@NotBlank @Size(max=30) String phone,@NotBlank @Size(max=200) String line1,@Size(max=200) String line2,@NotBlank @Size(max=100) String city,@NotBlank @Size(max=100) String state,@NotBlank @Size(max=20) String postalCode,@Size(max=100) String country,boolean defaultAddress){}
  public record AddressResponse(Long id,String fullName,String phone,String line1,String line2,String city,String state,String postalCode,String country,boolean defaultAddress){static AddressResponse from(Address a){return new AddressResponse(a.getId(),a.getFullName(),a.getPhone(),a.getLine1(),a.getLine2(),a.getCity(),a.getState(),a.getPostalCode(),a.getCountry(),a.isDefaultAddress());}}
}

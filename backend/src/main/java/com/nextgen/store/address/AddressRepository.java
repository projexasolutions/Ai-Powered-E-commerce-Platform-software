package com.nextgen.store.address;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AddressRepository extends JpaRepository<Address,Long> {
  List<Address> findByUserIdOrderByDefaultAddressDescUpdatedAtDesc(Long userId);
  Optional<Address> findByIdAndUserId(Long id,Long userId);
  Optional<Address> findFirstByUserIdAndDefaultAddressTrue(Long userId);
}

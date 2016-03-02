package uk.gov.ons.ctp.response.caseframe.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.caseframe.domain.model.Address;

/**
 * JPA Data Repository
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    Address findByUprn(Long uprn);
    List<Address> findByPostcode(String postcode);
}

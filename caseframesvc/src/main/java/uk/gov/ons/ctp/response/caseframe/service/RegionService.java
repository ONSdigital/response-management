package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;
import uk.gov.ons.ctp.response.caseframe.domain.model.Region;

/**
 * The interface defining the function of the Address service.
 * The application business logic should reside in it's implementation
 */
public interface RegionService extends CTPService {

    /**
     * Returns all regions sorted by region code ascending
     */
    List<Region> findAll();

    Region findById(String regionid);

    /**
     * Returns all local authorities for a given regionid  sorted by lad name ascending
     */
    List<LocalAuthority> findAllLadsByRegionid(String regionid);
}

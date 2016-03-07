package uk.gov.ons.ctp.response.caseframe.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.caseframe.domain.model.AddressSummary;
import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;
import uk.gov.ons.ctp.response.caseframe.representation.AddressSummaryDTO;
import uk.gov.ons.ctp.response.caseframe.representation.MsoaDTO;
import uk.gov.ons.ctp.response.caseframe.service.MsoaService;

/**
 * The REST endpoint controller for CaseFrame MSOAs
 */
@Path("/msoas")
@Produces({ "application/json" })
@Slf4j
public final class MsoaEndpoint implements CTPEndpoint {

  @Inject
  private MsoaService msoaService;

  @Inject
  private MapperFacade mapperFacade;

  /**
    the GET endpont to retrieve an MSOA by its id
   * @param msoaid the d of the MSO to fetch
   * @return the MSOA or null if not found
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{msoaid}")
  public MsoaDTO findMsoaById(@PathParam("msoaid")
  final String msoaid) throws CTPException {
    log.debug("Entering findMsoaById with {}", msoaid);
    Msoa msoa = msoaService.findById(msoaid);
    if (msoa == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "MSOA not found for id %s", msoaid);
    }
    return mapperFacade.map(msoa, MsoaDTO.class);
  }

  /**
   * the GET endpont to retrieve all address summaries for an MSOA
   * @param msoaid id of the MSOA whose summaries we want
   * @return the list empty or otherwise of retrieved address summaries
   */
  @GET
  @Path("/{msoaid}/addresssummaries")
  public List<AddressSummaryDTO> findAllAddressSummariesForMsoaId(@PathParam("msoaid") final String msoaid) {
    log.debug("Entering findAllAddressSummariesForMsoaId with {}", msoaid);
    List<AddressSummary> addresseSummaries = msoaService.findAllAddressSummariesByMsoaid(msoaid);
    List<AddressSummaryDTO> addresseSummaryDTOs = mapperFacade.mapAsList(addresseSummaries, AddressSummaryDTO.class);
    return CollectionUtils.isEmpty(addresseSummaryDTOs) ? null : addresseSummaryDTOs;
  }

}

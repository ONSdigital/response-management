package uk.gov.ons.ctp.response.action.export.endpoint;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMapping;
import uk.gov.ons.ctp.response.action.export.representation.TemplateMappingDTO;
import uk.gov.ons.ctp.response.action.export.service.TemplateMappingService;

/**
 * The REST endpoint controller for TemplateMappings.
 */
@RestController
@RequestMapping(value = "/templatemappings", produces = "application/json")
@Slf4j
public class TemplateMappingEndpoint {
  @Autowired
  private TemplateMappingService templateMappingService;

  @Autowired
  private MapperFacade mapperFacade;

  /**
   * To retrieve all TemplateMappings
   *
   * @return a list of TemplateMappings
   */
  @RequestMapping(method = RequestMethod.GET)
  public List<TemplateMappingDTO> findAllTemplateMappings() {
    log.debug("Entering findAllTemplateMappings ...");
    List<TemplateMapping> templateMappings = templateMappingService
        .retrieveAllTemplateMappings();
    List<TemplateMappingDTO> results = mapperFacade.mapAsList(templateMappings,
        TemplateMappingDTO.class);
    return CollectionUtils.isEmpty(results) ? null : results;
  }

  /**
   * To retrieve a specific TemplateMapping
   *
   * @param actionType for the specific TemplateMapping to retrieve
   * @return the specific TemplateMapping
   * @throws CTPException if no TemplateMapping found
   */
  @RequestMapping(value = "/{actionType}", method = RequestMethod.GET)
  public TemplateMappingDTO findTemplateMapping(
      @PathVariable("actionType") final String actionType) throws CTPException {
    log.debug("Entering findTemplateMapping with {}", actionType);
    TemplateMapping result = templateMappingService.retrieveTemplateMappingByActionType(actionType);
    return mapperFacade.map(result, TemplateMappingDTO.class);
  }

  /**
   * To store TemplateMappings
   *
   * @param fileContents the TemplateMapping content
   * @return 201 if created
   * @throws CTPException if the TemplateMapping can't be stored
   */
  @RequestMapping(method = RequestMethod.POST, consumes = "multipart/form-data")
  public ResponseEntity<?> storeTemplateMapping(@RequestParam("file")  InputStream fileContents) throws CTPException {
    // TODO Test and maybe InputStream fileContents should be MultipartFile file

    log.debug("Entering storeTemplateMapping");
    List<TemplateMapping> mappings = templateMappingService.storeTemplateMappings(fileContents);

    List<TemplateMappingDTO> results = mapperFacade.mapAsList(mappings, TemplateMappingDTO.class);
    return ResponseEntity.created(URI.create("TODO")).body(results);
  }
}

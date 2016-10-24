package uk.gov.ons.ctp.response.action.export.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;
import uk.gov.ons.ctp.response.action.export.domain.TemplateEngine;

/**
 * Service responsible for dealing with TemplateDocuments stored in MongoDB
 */
public interface TemplateService {
  /**
   * To store a TemplateDocument
   *
   * @param templateName the TemplateDocument name
   * @param fileContents the TemplateDocument content
   * @return the TemplateDocument stored
   * @throws CTPException if the TemplateDocument content is empty
   */
  TemplateDocument storeTemplateDocument(String templateName, TemplateEngine templateEngine, InputStream fileContents)
          throws CTPException;

  /**
   * To retrieve a given TemplateDocument
   *
   * @param templateName the TemplateDocument name to be retrieved
   * @return the given TemplateDocument
   */
  TemplateDocument retrieveTemplateDocument(String templateName);

  /**
   * To retrieve all TemplateDocuments
   *
   * @return a list of TemplateDocuments
   */
  List<TemplateDocument> retrieveAllTemplateDocuments();

  // TODO Should it stay here?
  /**
   * To clear the FreeMarker configuration's template cache
   */
  void clearTemplateCache();
}
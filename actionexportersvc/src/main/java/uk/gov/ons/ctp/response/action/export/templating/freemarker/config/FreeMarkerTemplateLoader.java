package uk.gov.ons.ctp.response.action.export.templating.freemarker.config;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import javax.inject.Inject;
import javax.inject.Named;

import freemarker.cache.TemplateLoader;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.export.domain.TemplateExpression;
import uk.gov.ons.ctp.response.action.export.repository.TemplateRepository;

/**
 * TemplateLoader to load templates stored in MongopDB
 */
@Slf4j
@Named
public class FreeMarkerTemplateLoader implements TemplateLoader {

  @Inject
  private TemplateRepository templateRepository;

  @Override
  public Object findTemplateSource(String name) throws IOException {
    log.debug("Retrieving template with name {}", name);
    return templateRepository.findOne(name);
  }

  @Override
  public long getLastModified(Object templateSource) {
    TemplateExpression template = (TemplateExpression) templateSource;
    String name = template.getName();
    log.debug("Retrieving last modified time for template with name {}", name);
    template = templateRepository.findOne(name);
    return template.getDateModified().getTime();
  }

  @Override
  public Reader getReader(Object templateSource, String encoding) throws IOException {
    // TODO encoding will be UTF-8 - do we need to do anything with it?
    return new StringReader(((TemplateExpression) templateSource).getContent());
  }

  @Override
  public void closeTemplateSource(Object templateSource) throws IOException {
  }
}

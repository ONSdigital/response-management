package uk.gov.ons.ctp.response.action;

import javax.inject.Named;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import uk.gov.ons.ctp.common.utility.CTPMessageBodyReader;
import uk.gov.ons.ctp.response.action.endpoint.ActionEndpoint;
import uk.gov.ons.ctp.response.action.endpoint.ActionPlanEndpoint;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;

/**
 * The 'main' entry point into the Action Service SpringBoot Application.
 */
@SpringBootApplication
@EnableTransactionManagement
@IntegrationComponentScan
@EnableAsync
public class ActionSvcApplication {

  /**
  * To register classes in the JAX-RS world.
   */
  @Named
  public static class JerseyConfig extends ResourceConfig {
    /**
     * Its public constructor.
     */
    public JerseyConfig() {
      packages("uk.gov.ons.ctp");

      register(ActionEndpoint.class);

      register(ActionPlanEndpoint.class);
      register(new CTPMessageBodyReader<>(ActionPlanDTO.class));

      System.setProperty("ma.glasnost.orika.writeSourceFiles", "false");
      System.setProperty("ma.glasnost.orika.writeClassFiles", "false");
    }
  }

  /**
   * This method is the entry point to the Spring Boot application.
   * @param args These are the optional command line arguments
   */
  public static void main(final String[] args) {
    SpringApplication.run(ActionSvcApplication.class, args);
  }
}

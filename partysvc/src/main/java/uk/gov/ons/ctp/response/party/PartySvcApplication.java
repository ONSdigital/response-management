package uk.gov.ons.ctp.response.party;

import javax.inject.Named;

import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import uk.gov.ons.ctp.common.jaxrs.JAXRSRegister;

/**
 * The main entry point into the IAC Service SpringBoot Application.
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
public class PartySvcApplication {

//  @Autowired
//  private AppConfig appConfig;


  /**
   * To register classes in the JAX-RS world.
   */
  @Named
  public static class JerseyConfig extends ResourceConfig {
    /**
     * Its public constructor.
     */
    public JerseyConfig() {

      JAXRSRegister.listCommonTypes().forEach(t->register(t));
      
//      register(InternetAccessCodeEndpoint.class);
//      register(new CTPMessageBodyReader<CreateInternetAccessCodeDTO>(CreateInternetAccessCodeDTO.class) {
//      });
//      register(new CTPMessageBodyReader<UpdateInternetAccessCodeDTO>(UpdateInternetAccessCodeDTO.class) {
//      });
//
      System.setProperty("ma.glasnost.orika.writeSourceFiles", "false");
      System.setProperty("ma.glasnost.orika.writeClassFiles", "false");
    }
  }
  
  /**
   * This method is the entry point to the Spring Boot application.
   *
   * @param args These are the optional command line arguments
   */
  public static void main(final String[] args) {
    SpringApplication.run(PartySvcApplication.class, args);
  }
}

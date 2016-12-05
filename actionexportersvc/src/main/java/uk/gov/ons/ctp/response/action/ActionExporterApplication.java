package uk.gov.ons.ctp.response.action;

import javax.inject.Named;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import uk.gov.ons.ctp.common.distributed.DistributedLockManager;
import uk.gov.ons.ctp.common.distributed.DistributedLockManagerRedissonImpl;
import uk.gov.ons.ctp.common.jaxrs.JAXRSRegister;
import uk.gov.ons.ctp.response.action.export.config.AppConfig;
import uk.gov.ons.ctp.response.action.export.endpoint.ActionRequestEndpoint;
import uk.gov.ons.ctp.response.action.export.endpoint.TemplateEndpoint;
import uk.gov.ons.ctp.response.action.export.endpoint.ManualTestEndpoint;
import uk.gov.ons.ctp.response.action.export.endpoint.TemplateMappingEndpoint;

/**
 * The main entry point into the Action Service SpringBoot Application.
 */

@EnableMongoRepositories(basePackages = "uk.gov.ons.ctp.response.action.export.repository")
@SpringBootApplication
@EnableTransactionManagement
@IntegrationComponentScan
@EnableAsync
@EnableCaching
@EnableScheduling
@ImportResource("springintegration/main.xml")
public class ActionExporterApplication {

  public static final String ACTION_EXECUTION_LOCK = "actionexport.request.execution";

  @Autowired
  private AppConfig appConfig;

  @Bean
  public DistributedLockManager actionExportExecutionLockManager(RedissonClient redissonClient) {
    return new DistributedLockManagerRedissonImpl(ACTION_EXECUTION_LOCK, redissonClient,
        appConfig.getDataGrid().getLockTimeToLiveSeconds());
  }

  @Bean
  public RedissonClient redissonClient() {
    Config config = new Config();
    config.useSingleServer()
        .setAddress(appConfig.getDataGrid().getAddress())
        .setPassword(appConfig.getDataGrid().getPassword());
    return Redisson.create(config);
  }

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

      register(MultiPartFeature.class);
      register(TemplateEndpoint.class);
      register(TemplateMappingEndpoint.class);
      register(ActionRequestEndpoint.class);

      register(ManualTestEndpoint.class);

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
    SpringApplication.run(ActionExporterApplication.class, args);
  }
}
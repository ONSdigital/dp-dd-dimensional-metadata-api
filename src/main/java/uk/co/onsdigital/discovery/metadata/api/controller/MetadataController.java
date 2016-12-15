package uk.co.onsdigital.discovery.metadata.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.model.DataSet;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;
import uk.co.onsdigital.discovery.metadata.api.service.MetadataService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;

/**
 * Controller for accessing the {@link uk.co.onsdigital.discovery.metadata.api.service.MetadataService} over REST.
 */
@RestController
@SpringBootApplication
@ComponentScan(basePackages = "uk.co.onsdigital.discovery.metadata.api")
public class MetadataController implements MetadataService {
    private static final Logger logger = LoggerFactory.getLogger(MetadataController.class);

    @Autowired
    private MetadataService metadataService;

    public static void main(String...args) {
        SpringApplication.run(MetadataController.class, args);
    }

    @GetMapping("/healthcheck")
    public boolean healthCheck() {
        return true;
    }

    @GetMapping("/datasets")
    @CrossOrigin
    @Override
    public Set<DataSet> listAvailableDataSets() {
        return metadataService.listAvailableDataSets();
    }

    @GetMapping("/datasets/{dataSetId}")
    @CrossOrigin
    @Override
    public DataSet findDataSetById(@PathVariable String dataSetId) throws DataSetNotFoundException {
        return metadataService.findDataSetById(dataSetId);
    }

    @GetMapping("/datasets/{dataSetId}/dimensions")
    @CrossOrigin
    @Override
    public Set<Dimension> listDimensionsForDataSet(@PathVariable String dataSetId) throws DataSetNotFoundException {
        return metadataService.listDimensionsForDataSet(dataSetId);
    }

    @GetMapping("/datasets/{dataSetId}/dimensions/{dimensionId}")
    @CrossOrigin
    @Override
    public Dimension findDimensionById(@PathVariable String dataSetId, @PathVariable String dimensionId)
            throws DataSetNotFoundException, DimensionNotFoundException {
        return metadataService.findDimensionById(dataSetId, dimensionId);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid DataSet ID")
    @ExceptionHandler(IllegalArgumentException.class)
    public void onIllegalArgumentException(final IllegalArgumentException ex) {
        // Do nothing
    }

    @Bean
    public EntityManagerFactory getEntityManagerFactory() {
        final Map<String, String> env = new HashMap<>();
        for (String property : asList("url", "driver", "user", "password")) {
            String value = System.getenv("DB_" + property.toUpperCase());
            if (value != null) {
                env.put("javax.persistence.jdbc." + property, value);
                if (property.equals("password")) {
                    value = "*****";
                }
                logger.info("Database config from environment: {} = {}", property, value);
            }
        }

        return Persistence.createEntityManagerFactory("data_discovery", env);
    }

    @Bean
    public EntityManager getEntityManager(final EntityManagerFactory emf) {
        return emf.createEntityManager();
    }

    @Bean
    public PlatformTransactionManager getTransactionManager(final EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }
}

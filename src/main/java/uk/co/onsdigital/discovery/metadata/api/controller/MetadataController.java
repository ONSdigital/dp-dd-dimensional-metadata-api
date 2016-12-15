package uk.co.onsdigital.discovery.metadata.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.model.DataSet;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;
import uk.co.onsdigital.discovery.metadata.api.service.MetadataService;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Set;

/**
 * Controller for accessing the {@link uk.co.onsdigital.discovery.metadata.api.service.MetadataService} over REST.
 */
@RestController
@SpringBootApplication
@ComponentScan(basePackages = "uk.co.onsdigital.discovery.metadata.api")
public class MetadataController implements MetadataService {

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

    @Bean
    public EntityManagerFactory getEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("data_discovery");
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

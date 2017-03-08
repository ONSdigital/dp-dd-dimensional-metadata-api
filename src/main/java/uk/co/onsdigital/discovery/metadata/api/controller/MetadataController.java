package uk.co.onsdigital.discovery.metadata.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;
import uk.co.onsdigital.discovery.metadata.api.dto.DataResourceResult;
import uk.co.onsdigital.discovery.metadata.api.dto.ResultPage;
import uk.co.onsdigital.discovery.metadata.api.dto.legacy.LegacyDataSet;
import uk.co.onsdigital.discovery.metadata.api.dto.common.DimensionMetadata;
import uk.co.onsdigital.discovery.metadata.api.dto.legacy.LegacyResultPage;
import uk.co.onsdigital.discovery.metadata.api.exception.DataResourceNotFoundExcecption;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.service.DimensionViewType;
import uk.co.onsdigital.discovery.metadata.api.service.MetadataService;

import javax.persistence.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.max;
import static java.util.Arrays.asList;

/**
 * Controller for accessing the {@link uk.co.onsdigital.discovery.metadata.api.service.MetadataService} over REST.
 */
@RestController
@SpringBootApplication
@ComponentScan(basePackages = "uk.co.onsdigital")
@EnableAutoConfiguration(exclude = {HibernateJpaAutoConfiguration.class})
public class MetadataController {
    private static final Logger logger = LoggerFactory.getLogger(MetadataController.class);

    @Autowired
    private MetadataService metadataService;

    public static void main(String...args) {
        SpringApplication.run(MetadataController.class, args);
    }

    @GetMapping("/healthcheck")
    public boolean healthCheck() {
        logger.debug("Health-check called.");
        return true;
    }

    @GetMapping("/datasets")
    @CrossOrigin
    public ResultPage<DataResourceResult> listAvailableVersions(Pageable pageable) {
        // Ensure pageNumber and pageSize are both at least 1
        logger.debug("Request on /datasets from page " + pageable.getPageNumber() + "and size " + pageable.getPageSize());
        return metadataService.listAvailableDataResources(max(pageable.getPageNumber(), 1), max(pageable.getPageSize(), 1));
    }

    @GetMapping("/versions")
    @CrossOrigin
    public LegacyResultPage<LegacyDataSet> listAvailableDataSets(Pageable pageable) {
        // Ensure pageNumber and pageSize are both at least 1
        logger.debug("Request on /versions from page " + pageable.getPageNumber() + "and size " + pageable.getPageSize());
        return metadataService.listAvailableVersions(max(pageable.getPageNumber(), 1), max(pageable.getPageSize(), 1));
    }


    @GetMapping("/versions/{dataSetId}")
    @CrossOrigin
    public LegacyDataSet findDataSetByUuid(@PathVariable String dataSetId) throws DataSetNotFoundException {
        logger.debug("Request for a dataset with version: " + dataSetId);
        return metadataService.findDataSetByUuid(dataSetId);
    }


    @GetMapping("/datasets/{dataSetId}")
    @CrossOrigin
    public DataResourceResult findDataResource(@PathVariable String dataSetId) throws DataSetNotFoundException {
        logger.debug("Request for a data-resource with id: " + dataSetId);
        return metadataService.findDataResource(dataSetId);
    }

    @GetMapping("/datasets/{dataSetId}/editions/{edition}/versions/{version}")
    @CrossOrigin
    public LegacyDataSet findDataSetByEditionAndVersion(@PathVariable String dataSetId, @PathVariable String edition,
                                                        @PathVariable int version)
            throws DataSetNotFoundException {
        logger.debug("Request for a dataset with the following data-resource/edition/version: " +
                String.join("/", new String[]{dataSetId, edition, Integer.toString(version)}));
        return metadataService.findDataSetByEditionAndVersion(dataSetId, edition, version);
    }

    @GetMapping("/versions/{dataSetId}/dimensions")
    @CrossOrigin
    public List<DimensionMetadata> listDimensionsForDataSetUuid(@PathVariable String dataSetId) throws DataSetNotFoundException {
        logger.debug("Request for all dimensions of dataset version: " + dataSetId);
        return metadataService.listDimensionsForDataSetUuid(dataSetId);
    }

    @GetMapping("/datasets/{dataSetId}/editions/{edition}/versions/{version}/dimensions")
    @CrossOrigin
    public List<DimensionMetadata> listDimensionsforDataSetEditionVersion(@PathVariable String dataSetId, @PathVariable String edition,
                                                                          @PathVariable int version)
            throws DataSetNotFoundException {
        logger.debug("Request for all dimensions of a dataset with the following data-resource/edition/version: " +
                String.join("/", new String[]{dataSetId, edition, Integer.toString(version)}));
        return metadataService.listDimensionsForDataSetEditionVersion(dataSetId, edition, version);
    }

    @GetMapping("/versions/{dataSetId}/dimensions/{dimensionId}")
    @CrossOrigin
    public DimensionMetadata findDimensionByIdWithDatasetUuid(@PathVariable String dataSetId, @PathVariable String dimensionId,
                                               @RequestParam(name = "view", defaultValue = "list") String view)
            throws DataSetNotFoundException, DimensionNotFoundException {
        logger.debug("Request for a dimension for dataset version " + dataSetId + " and dimensionId " + dimensionId);
        final DimensionViewType viewType = DimensionViewType.valueOf(view.toUpperCase());
        return metadataService.findDimensionByIdWithDatasetUuid(dataSetId, dimensionId, viewType);
    }

    @GetMapping("/datasets/{dataSetId}/editions/{edition}/versions/{version}/dimensions/{dimensionId}")
    @CrossOrigin
    public DimensionMetadata findDimensionByIdWithEditionVersion(@PathVariable String dataSetId, @PathVariable String edition,
                                                                 @PathVariable int version, @PathVariable String dimensionId,
                                               @RequestParam(name = "view", defaultValue = "list") String view)
            throws DataSetNotFoundException, DimensionNotFoundException {
        logger.debug("Request for a dataset with the following data-resource/edition/version: " +
                String.join("/", new String[]{dataSetId, edition, Integer.toString(version)}));
        final DimensionViewType viewType = DimensionViewType.valueOf(view.toUpperCase());
        return metadataService.findDimensionByIdWithEditionVersion(dataSetId, edition, version, dimensionId, viewType);
    }

    @GetMapping("/hierarchies")
    @CrossOrigin
    public List<DimensionMetadata> listHierarchies() {
        logger.debug("Request on /hierarchies, listing all hierarchies");
        return metadataService.listHierarchies();
    }

    @GetMapping("/hierarchies/{hierarchyId}")
    @CrossOrigin
    public DimensionMetadata getHierarchy(@PathVariable String hierarchyId) throws DimensionNotFoundException {
        logger.debug("Request for hierarchy " + hierarchyId);
        return metadataService.getHierarchy(hierarchyId);
    }

    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ResponseBody
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse onIllegalArgumentException(final IllegalArgumentException ex) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ResponseBody
    @ResponseStatus(code = HttpStatus.NOT_IMPLEMENTED)
    @ExceptionHandler(UnsupportedOperationException.class)
    public ErrorResponse onUnsupportedOperation(final UnsupportedOperationException ex) {
        return new ErrorResponse(HttpStatus.NOT_IMPLEMENTED, ex.getMessage());
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
    @RequestScope
    public EntityManager getEntityManager(final EntityManagerFactory emf) {
        return emf.createEntityManager();
    }

    @Bean
    public PlatformTransactionManager getTransactionManager(final EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @ExceptionHandler(DataSetNotFoundException.class)
    void handleDataSetNotFoundException(DataSetNotFoundException e, HttpServletResponse response) throws IOException {
        logger.error(e.getMessage(), e);
        response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(DataResourceNotFoundExcecption.class)
    void handleDataResourceNotFoundException(DataResourceNotFoundExcecption e, HttpServletResponse response) throws IOException {
        logger.error(e.getMessage(), e);
        response.sendError(HttpStatus.NOT_FOUND.value(), e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    void handleRuntimeException(RuntimeException e, HttpServletResponse response) throws IOException {
        logger.error("Unexpected RuntimeException!", e);
        response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
    }




    /**
     * Simplified error response that just reports the status code, error and message.
     */
    private static class ErrorResponse {
        private final HttpStatus status;
        private final String message;

        public ErrorResponse(HttpStatus status, String message) {
            this.status = status;
            this.message = message;
        }

        public int getStatus() {
            return status.value();
        }

        public String getError() {
            return status.getReasonPhrase();
        }

        public String getMessage() {
            return message;
        }
    }
}

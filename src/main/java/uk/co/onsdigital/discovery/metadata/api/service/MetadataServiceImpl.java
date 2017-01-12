package uk.co.onsdigital.discovery.metadata.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.model.DataSet;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;
import uk.co.onsdigital.discovery.metadata.api.model.DimensionOption;
import uk.co.onsdigital.discovery.metadata.api.model.ResultPage;
import uk.co.onsdigital.discovery.model.Category;
import uk.co.onsdigital.discovery.model.ConceptSystem;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;
import uk.co.onsdigital.discovery.model.GeographicArea;
import uk.co.onsdigital.discovery.model.GeographicAreaHierarchy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

/**
 * Implementation of the {@link MetadataService}.
 */
@Service
public class MetadataServiceImpl implements MetadataService {
    private static final Logger logger = LoggerFactory.getLogger(MetadataServiceImpl.class);

    private final MetadataDao metadataDao;
    private final UrlBuilder urlBuilder;

    MetadataServiceImpl(MetadataDao metadataDao, UrlBuilder urlBuilder) {
        logger.info("Initialising metadata service. Base URL: {}", urlBuilder);

        this.metadataDao = metadataDao;
        this.urlBuilder = urlBuilder;
    }

    @Transactional(readOnly = true)
    public ResultPage<DataSet> listAvailableDataSets(int pageNumber, int pageSize) {
        final long totalDataSets = metadataDao.countDataSets();
        final List<DimensionalDataSet> dbDataSets = metadataDao.findDataSetsPage(pageNumber, pageSize);
        final List<DataSet> resultDataSets = new ArrayList<>(dbDataSets.size());

        for (DimensionalDataSet dbDataSet : dbDataSets) {
            resultDataSets.add(convertDataSet(dbDataSet, false));
        }

        return new ResultPage<>(urlBuilder.datasetsPage(pageSize), resultDataSets, totalDataSets, pageNumber, pageSize);
    }

    @Transactional(readOnly = true)
    public DataSet findDataSetById(String dataSetId) throws DataSetNotFoundException {
        return convertDataSet(metadataDao.findDataSetById(dataSetId), true);
    }

    @Transactional(readOnly = true)
    public Set<Dimension> listDimensionsForDataSet(String dataSetId) throws DataSetNotFoundException {
        final DimensionalDataSet dataSet = metadataDao.findDataSetById(dataSetId);
        return convertAllDimensions(dataSet, true);
    }

    @Transactional(readOnly = true)
    public Dimension findDimensionById(String dataSetId, String dimensionId) throws DataSetNotFoundException, DimensionNotFoundException {
        try {
            final ConceptSystem conceptSystem = metadataDao.findConceptSystemByDataSetAndConceptSystemName(dataSetId, dimensionId);
            return convertConceptSystemToDimension(conceptSystem, dataSetId, true);
        } catch (DimensionNotFoundException ex) {
            // If the dimension isn't a concept, then see if it is a geographical hierarchy
            final GeographicAreaHierarchy hierarchy = metadataDao.findGeographyInDataSet(dataSetId, dimensionId);
            return convertGeographyToDimension(hierarchy, dataSetId, true);
        }
    }

    private DataSet convertDataSet(final DimensionalDataSet dbDataSet, final boolean includeDimensions) {
        final DataSet dataSet = new DataSet();
        dataSet.setId(dbDataSet.getDimensionalDataSetId().toString());
        dataSet.setS3URL(dbDataSet.getS3URL());
        dataSet.setDescription(dbDataSet.getDescription());
        if (dataSet.getMetadata().getDescription() == null) {
            dataSet.setDescription("No description available.");
        }
        dataSet.setUrl(urlBuilder.dataset(dataSet.getId()));
        dataSet.setDimensionsUrl(urlBuilder.dimensions(dataSet.getId()));

        if (includeDimensions) {
            dataSet.setDimensions(convertAllDimensions(dbDataSet, true));
        }

        return dataSet;
    }

    private Set<Dimension> convertAllDimensions(final DimensionalDataSet dataSet, final boolean includeOptions) {
        final Set<Dimension> dimensions = new TreeSet<>();
        final String dataSetId = dataSet.getDimensionalDataSetId().toString();
        final Set<ConceptSystem> concepts = dataSet.getReferencedConceptSystems();
        dimensions.addAll(convertConceptSystemsToDimensions(concepts, dataSetId, includeOptions));

        final Set<Dimension> geoDimensions = dataSet.getReferencedGeographies()
                .map(g -> convertGeographyToDimension(g, dataSetId, includeOptions))
                .collect(Collectors.toSet());
        dimensions.addAll(geoDimensions);

        return dimensions;
    }

    private Set<Dimension> convertConceptSystemsToDimensions(final Set<ConceptSystem> concepts, final String dataSetId,
                                                             final boolean includeOptions) {
        return concepts.stream()
                .filter(c -> c.getCategories() != null && !c.getCategories().isEmpty())
                .map(c -> convertConceptSystemToDimension(c, dataSetId, includeOptions))
                .collect(toCollection(TreeSet::new));
    }

    private Dimension convertGeographyToDimension(final GeographicAreaHierarchy geography,
                                                  final String dataSetId, final boolean includeOptions) {
        final Dimension dimension = new Dimension();
        dimension.setId(geography.getGeographicAreaHierarchy());
        dimension.setName(geography.getGeographicAreaHierarchy());
        dimension.setUrl(urlBuilder.dimension(dataSetId, dimension.getId()));

        if (includeOptions) {
            if (geography.getGeographicAreas() != null) {
                final Set<DimensionOption> options = geography.getGeographicAreas().stream()
                        // Only convert the top-level areas in the hierarchy, as conversion will recursively fill in others
                        .filter(area -> area.getGeographicArea() == null)
                        .map(this::convertAreaToOption)
                        .collect(toCollection(TreeSet::new));
                dimension.setOptions(options);
            }
        }
        return dimension;
    }

    private DimensionOption convertAreaToOption(final GeographicArea area) {
        final DimensionOption option = new DimensionOption(area.getExtCode(), area.getName());
        // Recursively add any child areas, if they exist. NB: currently this information won't be populated in the DB.
        final List<GeographicArea> childAreas = area.getGeographicAreas();
        if (childAreas != null) {
            for (GeographicArea child : childAreas) {
                option.addOption(convertAreaToOption(child));
            }
        }
        return option;
    }

    private Dimension convertConceptSystemToDimension(ConceptSystem conceptSystem, String dataSetId, boolean includeOptions) {
        final Dimension dimension = new Dimension();
        dimension.setId(conceptSystem.getConceptSystem());
        dimension.setName(conceptSystem.getConceptSystem());
        dimension.setUrl(urlBuilder.dimension(dataSetId, dimension.getId()));

        if (includeOptions) {
            final List<Category> categories = conceptSystem.getCategories();
            if (categories != null) {
                final Set<DimensionOption> options = new TreeSet<>();
                for (Category category : categories) {
                    options.add(new DimensionOption(String.valueOf(category.getCategoryId()), category.getName()));
                }

                dimension.setOptions(options);
            }
        }
        return dimension;
    }

}

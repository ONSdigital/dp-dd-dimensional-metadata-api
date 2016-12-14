package uk.co.onsdigital.discovery.metadata.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriTemplate;
import uk.co.onsdigital.discovery.metadata.api.dao.MetadataDao;
import uk.co.onsdigital.discovery.metadata.api.exception.DataSetNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.exception.DimensionNotFoundException;
import uk.co.onsdigital.discovery.metadata.api.model.DataSet;
import uk.co.onsdigital.discovery.metadata.api.model.Dimension;
import uk.co.onsdigital.discovery.metadata.api.model.DimensionOption;
import uk.co.onsdigital.discovery.model.Category;
import uk.co.onsdigital.discovery.model.ConceptSystem;
import uk.co.onsdigital.discovery.model.DimensionalDataSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link MetadataService}.
 */
@Service
public class MetadataServiceImpl implements MetadataService {
    private final MetadataDao metadataDao;
    private final String baseUrl;

    public MetadataServiceImpl(MetadataDao metadataDao, @Value("${base.url}") String baseUrl) {
        this.metadataDao = metadataDao;
        this.baseUrl = baseUrl;
    }

    @Transactional(readOnly = true)
    public Set<DataSet> listAvailableDataSets() {
        final List<DimensionalDataSet> dbDataSets = metadataDao.findAllDataSets();
        final Set<DataSet> resultDataSets = new HashSet<>(dbDataSets.size());

        for (DimensionalDataSet dbDataSet : dbDataSets) {
            resultDataSets.add(convertDataSet(dbDataSet));
        }

        return resultDataSets;
    }

    @Transactional(readOnly = true)
    public DataSet findDataSetById(String dataSetId) throws DataSetNotFoundException {
        return convertDataSet(metadataDao.findDataSetById(dataSetId));
    }

    @Transactional(readOnly = true)
    public Set<Dimension> listDimensionsForDataSet(String dataSetId) throws DataSetNotFoundException {
        final Set<ConceptSystem> concepts = metadataDao.findConceptSystemsInDataSet(dataSetId);

        return concepts.stream()
                .filter(c -> c.getCategories() != null && !c.getCategories().isEmpty())
                .map(c -> convertConceptSystemToDimension(c, dataSetId, false))
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Dimension findDimensionById(String dataSetId, String dimensionId) throws DataSetNotFoundException, DimensionNotFoundException {
        final ConceptSystem conceptSystem = metadataDao.findConceptSystemByDataSetAndConceptSystemName(dataSetId, dimensionId);
        return convertConceptSystemToDimension(conceptSystem, dataSetId, true);
    }

    private DataSet convertDataSet(final DimensionalDataSet dbDataSet) {
        final DataSet dataSet = new DataSet();
        dataSet.setId(dbDataSet.getDimensionalDataSetId().toString());
        dataSet.setTitle(dbDataSet.getTitle());
        dataSet.setDescription(dbDataSet.getDescription());

        UriTemplate dataSetTemplate = new UriTemplate(baseUrl + "/datasets/{dataSet}");
        UriTemplate dimensionsTemplate = new UriTemplate(baseUrl + "/datasets/{dataSet}/dimensions");

        dataSet.setUrl(dataSetTemplate.expand(dataSet.getId()).toString());
        dataSet.setDimensionsUrl(dimensionsTemplate.expand(dataSet.getId()).toString());
        return dataSet;
    }

    private Dimension convertConceptSystemToDimension(ConceptSystem conceptSystem, String dataSetId, boolean includeOptions) {
        final Dimension dimension = new Dimension();
        dimension.setId(conceptSystem.getConceptSystem());
        dimension.setName(conceptSystem.getConceptSystem());
        UriTemplate dimensionTemplate = new UriTemplate(baseUrl + "/datasets/{dataSet}/dimensions/{dimensionId}");
        dimension.setUrl(dimensionTemplate.expand(dataSetId, dimension.getId()).toString());

        if (includeOptions) {
            final List<Category> categories = conceptSystem.getCategories();
            if (categories != null) {
                final Set<DimensionOption> options = new HashSet<>();
                for (Category category : categories) {
                    options.add(new DimensionOption(String.valueOf(category.getCategoryId()), category.getName()));
                }

                dimension.setOptions(options);
            }
        }
        return dimension;
    }

}

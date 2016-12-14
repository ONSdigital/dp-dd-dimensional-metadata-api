package uk.co.onsdigital.discovery.metadata.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link MetadataService}.
 */
@Service
public class MetadataServiceImpl implements MetadataService {
    private static final String DATASET_TEMPLATE = "%s/datasets/%s";

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

        return concepts.stream().map(MetadataServiceImpl::convertConceptSystemToDimension)
                .filter(c -> !c.getOptions().isEmpty())
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public Dimension findDimensionById(String dataSetId, String dimensionId) throws DataSetNotFoundException, DimensionNotFoundException {
        final ConceptSystem conceptSystem = metadataDao.findConceptSystemByDataSetAndConceptSystemName(dataSetId, dimensionId);
        return convertConceptSystemToDimension(conceptSystem);
    }

    private DataSet convertDataSet(final DimensionalDataSet dbDataSet) {
        final DataSet dataSet = new DataSet();
        dataSet.setId(dbDataSet.getDimensionalDataSetId().toString());
        dataSet.setTitle(dbDataSet.getTitle());
        dataSet.setDescription(dbDataSet.getDescription());

        dataSet.setUrl(String.format(Locale.ROOT, DATASET_TEMPLATE, baseUrl, dbDataSet.getDimensionalDataSetId().toString()));
        return dataSet;
    }

    private static Dimension convertConceptSystemToDimension(ConceptSystem conceptSystem) {
        final Dimension dimension = new Dimension();
        dimension.setId(conceptSystem.getConceptSystem());
        dimension.setName(conceptSystem.getConceptSystem());

        final List<Category> categories = conceptSystem.getCategories();
        if (categories != null) {
            final Set<DimensionOption> options = new HashSet<>();
            for (Category category : categories) {
                options.add(new DimensionOption(String.valueOf(category.getCategoryId()), category.getName()));
            }

            dimension.setOptions(options);
        }
        return dimension;
    }

}

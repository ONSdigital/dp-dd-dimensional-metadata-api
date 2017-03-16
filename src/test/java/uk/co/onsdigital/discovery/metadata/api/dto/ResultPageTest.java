package uk.co.onsdigital.discovery.metadata.api.dto;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import uk.co.onsdigital.discovery.metadata.api.service.UrlBuilder;

import java.util.Collections;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ResultPageTest {

    @Mock
    UrlBuilder.PageUrlTemplate mockTemplate;

    @BeforeMethod
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getTotalPagesShouldHaveCorrectCount() throws Exception {

        assertThat(createResultPage(19, 20).getTotalPages()).isEqualTo(1);
        assertThat(createResultPage(20, 20).getTotalPages()).isEqualTo(1);
        assertThat(createResultPage(21, 20).getTotalPages()).isEqualTo(2);
        assertThat(createResultPage(0, 20).getTotalPages()).isEqualTo(0);

    }

    private ResultPage createResultPage(int total, int pageSize) {
        return new ResultPage(mockTemplate, Collections.emptyList(), total, 1, pageSize);
    }

}
package ch.qfs.sample.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link AddressSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class AddressSearchRepositoryMockConfiguration {

    @MockBean
    private AddressSearchRepository mockAddressSearchRepository;
}

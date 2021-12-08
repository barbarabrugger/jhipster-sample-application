package ch.qfs.sample.repository.search;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure a Mock version of {@link WishListSearchRepository} to test the
 * application without starting Elasticsearch.
 */
@Configuration
public class WishListSearchRepositoryMockConfiguration {

    @MockBean
    private WishListSearchRepository mockWishListSearchRepository;
}

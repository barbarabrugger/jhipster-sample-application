package ch.qfs.sample.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import ch.qfs.sample.domain.Customer;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link Customer} entity.
 */
public interface CustomerSearchRepository extends ElasticsearchRepository<Customer, Long>, CustomerSearchRepositoryInternal {}

interface CustomerSearchRepositoryInternal {
    Page<Customer> search(String query, Pageable pageable);
}

class CustomerSearchRepositoryInternalImpl implements CustomerSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    CustomerSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Customer> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Customer> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Customer.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

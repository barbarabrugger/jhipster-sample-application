package ch.qfs.sample.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import ch.qfs.sample.domain.Address;
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
 * Spring Data Elasticsearch repository for the {@link Address} entity.
 */
public interface AddressSearchRepository extends ElasticsearchRepository<Address, Long>, AddressSearchRepositoryInternal {}

interface AddressSearchRepositoryInternal {
    Page<Address> search(String query, Pageable pageable);
}

class AddressSearchRepositoryInternalImpl implements AddressSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    AddressSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Page<Address> search(String query, Pageable pageable) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        nativeSearchQuery.setPageable(pageable);
        List<Address> hits = elasticsearchTemplate
            .search(nativeSearchQuery, Address.class)
            .map(SearchHit::getContent)
            .stream()
            .collect(Collectors.toList());

        return new PageImpl<>(hits, pageable, hits.size());
    }
}

package ch.qfs.sample.repository.search;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

import ch.qfs.sample.domain.WishList;
import java.util.stream.Stream;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the {@link WishList} entity.
 */
public interface WishListSearchRepository extends ElasticsearchRepository<WishList, Long>, WishListSearchRepositoryInternal {}

interface WishListSearchRepositoryInternal {
    Stream<WishList> search(String query);
}

class WishListSearchRepositoryInternalImpl implements WishListSearchRepositoryInternal {

    private final ElasticsearchRestTemplate elasticsearchTemplate;

    WishListSearchRepositoryInternalImpl(ElasticsearchRestTemplate elasticsearchTemplate) {
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    @Override
    public Stream<WishList> search(String query) {
        NativeSearchQuery nativeSearchQuery = new NativeSearchQuery(queryStringQuery(query));
        return elasticsearchTemplate.search(nativeSearchQuery, WishList.class).map(SearchHit::getContent).stream();
    }
}

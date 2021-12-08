package ch.qfs.sample.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import ch.qfs.sample.IntegrationTest;
import ch.qfs.sample.domain.WishList;
import ch.qfs.sample.repository.WishListRepository;
import ch.qfs.sample.repository.search.WishListSearchRepository;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link WishListResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class WishListResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_RESTRICTED = false;
    private static final Boolean UPDATED_RESTRICTED = true;

    private static final String ENTITY_API_URL = "/api/wish-lists";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
    private static final String ENTITY_SEARCH_API_URL = "/api/_search/wish-lists";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private WishListRepository wishListRepository;

    /**
     * This repository is mocked in the ch.qfs.sample.repository.search test package.
     *
     * @see ch.qfs.sample.repository.search.WishListSearchRepositoryMockConfiguration
     */
    @Autowired
    private WishListSearchRepository mockWishListSearchRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restWishListMockMvc;

    private WishList wishList;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WishList createEntity(EntityManager em) {
        WishList wishList = new WishList().title(DEFAULT_TITLE).restricted(DEFAULT_RESTRICTED);
        return wishList;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static WishList createUpdatedEntity(EntityManager em) {
        WishList wishList = new WishList().title(UPDATED_TITLE).restricted(UPDATED_RESTRICTED);
        return wishList;
    }

    @BeforeEach
    public void initTest() {
        wishList = createEntity(em);
    }

    @Test
    @Transactional
    void createWishList() throws Exception {
        int databaseSizeBeforeCreate = wishListRepository.findAll().size();
        // Create the WishList
        restWishListMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isCreated());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeCreate + 1);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testWishList.getRestricted()).isEqualTo(DEFAULT_RESTRICTED);

        // Validate the WishList in Elasticsearch
        verify(mockWishListSearchRepository, times(1)).save(testWishList);
    }

    @Test
    @Transactional
    void createWishListWithExistingId() throws Exception {
        // Create the WishList with an existing ID
        wishList.setId(1L);

        int databaseSizeBeforeCreate = wishListRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restWishListMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeCreate);

        // Validate the WishList in Elasticsearch
        verify(mockWishListSearchRepository, times(0)).save(wishList);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        int databaseSizeBeforeTest = wishListRepository.findAll().size();
        // set the field null
        wishList.setTitle(null);

        // Create the WishList, which fails.

        restWishListMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllWishLists() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get all the wishListList
        restWishListMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishList.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].restricted").value(hasItem(DEFAULT_RESTRICTED.booleanValue())));
    }

    @Test
    @Transactional
    void getWishList() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        // Get the wishList
        restWishListMockMvc
            .perform(get(ENTITY_API_URL_ID, wishList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(wishList.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.restricted").value(DEFAULT_RESTRICTED.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingWishList() throws Exception {
        // Get the wishList
        restWishListMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewWishList() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();

        // Update the wishList
        WishList updatedWishList = wishListRepository.findById(wishList.getId()).get();
        // Disconnect from session so that the updates on updatedWishList are not directly saved in db
        em.detach(updatedWishList);
        updatedWishList.title(UPDATED_TITLE).restricted(UPDATED_RESTRICTED);

        restWishListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedWishList.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedWishList))
            )
            .andExpect(status().isOk());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testWishList.getRestricted()).isEqualTo(UPDATED_RESTRICTED);

        // Validate the WishList in Elasticsearch
        verify(mockWishListSearchRepository).save(testWishList);
    }

    @Test
    @Transactional
    void putNonExistingWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();
        wishList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, wishList.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WishList in Elasticsearch
        verify(mockWishListSearchRepository, times(0)).save(wishList);
    }

    @Test
    @Transactional
    void putWithIdMismatchWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();
        wishList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WishList in Elasticsearch
        verify(mockWishListSearchRepository, times(0)).save(wishList);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();
        wishList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WishList in Elasticsearch
        verify(mockWishListSearchRepository, times(0)).save(wishList);
    }

    @Test
    @Transactional
    void partialUpdateWishListWithPatch() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();

        // Update the wishList using partial update
        WishList partialUpdatedWishList = new WishList();
        partialUpdatedWishList.setId(wishList.getId());

        partialUpdatedWishList.restricted(UPDATED_RESTRICTED);

        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWishList.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWishList))
            )
            .andExpect(status().isOk());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getTitle()).isEqualTo(DEFAULT_TITLE);
        assertThat(testWishList.getRestricted()).isEqualTo(UPDATED_RESTRICTED);
    }

    @Test
    @Transactional
    void fullUpdateWishListWithPatch() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();

        // Update the wishList using partial update
        WishList partialUpdatedWishList = new WishList();
        partialUpdatedWishList.setId(wishList.getId());

        partialUpdatedWishList.title(UPDATED_TITLE).restricted(UPDATED_RESTRICTED);

        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedWishList.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedWishList))
            )
            .andExpect(status().isOk());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);
        WishList testWishList = wishListList.get(wishListList.size() - 1);
        assertThat(testWishList.getTitle()).isEqualTo(UPDATED_TITLE);
        assertThat(testWishList.getRestricted()).isEqualTo(UPDATED_RESTRICTED);
    }

    @Test
    @Transactional
    void patchNonExistingWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();
        wishList.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, wishList.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WishList in Elasticsearch
        verify(mockWishListSearchRepository, times(0)).save(wishList);
    }

    @Test
    @Transactional
    void patchWithIdMismatchWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();
        wishList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isBadRequest());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WishList in Elasticsearch
        verify(mockWishListSearchRepository, times(0)).save(wishList);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamWishList() throws Exception {
        int databaseSizeBeforeUpdate = wishListRepository.findAll().size();
        wishList.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restWishListMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(wishList))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the WishList in the database
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeUpdate);

        // Validate the WishList in Elasticsearch
        verify(mockWishListSearchRepository, times(0)).save(wishList);
    }

    @Test
    @Transactional
    void deleteWishList() throws Exception {
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);

        int databaseSizeBeforeDelete = wishListRepository.findAll().size();

        // Delete the wishList
        restWishListMockMvc
            .perform(delete(ENTITY_API_URL_ID, wishList.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<WishList> wishListList = wishListRepository.findAll();
        assertThat(wishListList).hasSize(databaseSizeBeforeDelete - 1);

        // Validate the WishList in Elasticsearch
        verify(mockWishListSearchRepository, times(1)).deleteById(wishList.getId());
    }

    @Test
    @Transactional
    void searchWishList() throws Exception {
        // Configure the mock search repository
        // Initialize the database
        wishListRepository.saveAndFlush(wishList);
        when(mockWishListSearchRepository.search("id:" + wishList.getId())).thenReturn(Stream.of(wishList));

        // Search the wishList
        restWishListMockMvc
            .perform(get(ENTITY_SEARCH_API_URL + "?query=id:" + wishList.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(wishList.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].restricted").value(hasItem(DEFAULT_RESTRICTED.booleanValue())));
    }
}

package ch.qfs.sample.web.rest;

import ch.qfs.sample.domain.WishList;
import ch.qfs.sample.repository.WishListRepository;
import ch.qfs.sample.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ch.qfs.sample.domain.WishList}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class WishListResource {

    private final Logger log = LoggerFactory.getLogger(WishListResource.class);

    private static final String ENTITY_NAME = "wishList";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final WishListRepository wishListRepository;

    public WishListResource(WishListRepository wishListRepository) {
        this.wishListRepository = wishListRepository;
    }

    /**
     * {@code POST  /wish-lists} : Create a new wishList.
     *
     * @param wishList the wishList to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new wishList, or with status {@code 400 (Bad Request)} if the wishList has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/wish-lists")
    public ResponseEntity<WishList> createWishList(@Valid @RequestBody WishList wishList) throws URISyntaxException {
        log.debug("REST request to save WishList : {}", wishList);
        if (wishList.getId() != null) {
            throw new BadRequestAlertException("A new wishList cannot already have an ID", ENTITY_NAME, "idexists");
        }
        WishList result = wishListRepository.save(wishList);
        return ResponseEntity
            .created(new URI("/api/wish-lists/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /wish-lists/:id} : Updates an existing wishList.
     *
     * @param id the id of the wishList to save.
     * @param wishList the wishList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishList,
     * or with status {@code 400 (Bad Request)} if the wishList is not valid,
     * or with status {@code 500 (Internal Server Error)} if the wishList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/wish-lists/{id}")
    public ResponseEntity<WishList> updateWishList(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody WishList wishList
    ) throws URISyntaxException {
        log.debug("REST request to update WishList : {}, {}", id, wishList);
        if (wishList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wishList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wishListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        WishList result = wishListRepository.save(wishList);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wishList.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /wish-lists/:id} : Partial updates given fields of an existing wishList, field will ignore if it is null
     *
     * @param id the id of the wishList to save.
     * @param wishList the wishList to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated wishList,
     * or with status {@code 400 (Bad Request)} if the wishList is not valid,
     * or with status {@code 404 (Not Found)} if the wishList is not found,
     * or with status {@code 500 (Internal Server Error)} if the wishList couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/wish-lists/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<WishList> partialUpdateWishList(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody WishList wishList
    ) throws URISyntaxException {
        log.debug("REST request to partial update WishList partially : {}, {}", id, wishList);
        if (wishList.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, wishList.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!wishListRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<WishList> result = wishListRepository
            .findById(wishList.getId())
            .map(existingWishList -> {
                if (wishList.getTitle() != null) {
                    existingWishList.setTitle(wishList.getTitle());
                }
                if (wishList.getRestricted() != null) {
                    existingWishList.setRestricted(wishList.getRestricted());
                }

                return existingWishList;
            })
            .map(wishListRepository::save);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, wishList.getId().toString())
        );
    }

    /**
     * {@code GET  /wish-lists} : get all the wishLists.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of wishLists in body.
     */
    @GetMapping("/wish-lists")
    public List<WishList> getAllWishLists() {
        log.debug("REST request to get all WishLists");
        return wishListRepository.findAll();
    }

    /**
     * {@code GET  /wish-lists/:id} : get the "id" wishList.
     *
     * @param id the id of the wishList to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the wishList, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/wish-lists/{id}")
    public ResponseEntity<WishList> getWishList(@PathVariable Long id) {
        log.debug("REST request to get WishList : {}", id);
        Optional<WishList> wishList = wishListRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(wishList);
    }

    /**
     * {@code DELETE  /wish-lists/:id} : delete the "id" wishList.
     *
     * @param id the id of the wishList to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/wish-lists/{id}")
    public ResponseEntity<Void> deleteWishList(@PathVariable Long id) {
        log.debug("REST request to delete WishList : {}", id);
        wishListRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}

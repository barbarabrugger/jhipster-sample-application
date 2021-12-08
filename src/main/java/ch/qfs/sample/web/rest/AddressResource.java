package ch.qfs.sample.web.rest;

import static org.elasticsearch.index.query.QueryBuilders.*;

import ch.qfs.sample.domain.Address;
import ch.qfs.sample.repository.AddressRepository;
import ch.qfs.sample.repository.search.AddressSearchRepository;
import ch.qfs.sample.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link ch.qfs.sample.domain.Address}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class AddressResource {

    private final Logger log = LoggerFactory.getLogger(AddressResource.class);

    private static final String ENTITY_NAME = "address";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AddressRepository addressRepository;

    private final AddressSearchRepository addressSearchRepository;

    public AddressResource(AddressRepository addressRepository, AddressSearchRepository addressSearchRepository) {
        this.addressRepository = addressRepository;
        this.addressSearchRepository = addressSearchRepository;
    }

    /**
     * {@code POST  /addresses} : Create a new address.
     *
     * @param address the address to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new address, or with status {@code 400 (Bad Request)} if the address has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/addresses")
    public ResponseEntity<Address> createAddress(@Valid @RequestBody Address address) throws URISyntaxException {
        log.debug("REST request to save Address : {}", address);
        if (address.getId() != null) {
            throw new BadRequestAlertException("A new address cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Address result = addressRepository.save(address);
        addressSearchRepository.save(result);
        return ResponseEntity
            .created(new URI("/api/addresses/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /addresses/:id} : Updates an existing address.
     *
     * @param id the id of the address to save.
     * @param address the address to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated address,
     * or with status {@code 400 (Bad Request)} if the address is not valid,
     * or with status {@code 500 (Internal Server Error)} if the address couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/addresses/{id}")
    public ResponseEntity<Address> updateAddress(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody Address address
    ) throws URISyntaxException {
        log.debug("REST request to update Address : {}, {}", id, address);
        if (address.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, address.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Address result = addressRepository.save(address);
        addressSearchRepository.save(result);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, address.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /addresses/:id} : Partial updates given fields of an existing address, field will ignore if it is null
     *
     * @param id the id of the address to save.
     * @param address the address to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated address,
     * or with status {@code 400 (Bad Request)} if the address is not valid,
     * or with status {@code 404 (Not Found)} if the address is not found,
     * or with status {@code 500 (Internal Server Error)} if the address couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/addresses/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Address> partialUpdateAddress(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Address address
    ) throws URISyntaxException {
        log.debug("REST request to partial update Address partially : {}, {}", id, address);
        if (address.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, address.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!addressRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Address> result = addressRepository
            .findById(address.getId())
            .map(existingAddress -> {
                if (address.getAddress1() != null) {
                    existingAddress.setAddress1(address.getAddress1());
                }
                if (address.getAddress2() != null) {
                    existingAddress.setAddress2(address.getAddress2());
                }
                if (address.getCity() != null) {
                    existingAddress.setCity(address.getCity());
                }
                if (address.getPostcode() != null) {
                    existingAddress.setPostcode(address.getPostcode());
                }
                if (address.getCountry() != null) {
                    existingAddress.setCountry(address.getCountry());
                }

                return existingAddress;
            })
            .map(addressRepository::save)
            .map(savedAddress -> {
                addressSearchRepository.save(savedAddress);

                return savedAddress;
            });

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, address.getId().toString())
        );
    }

    /**
     * {@code GET  /addresses} : get all the addresses.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of addresses in body.
     */
    @GetMapping("/addresses")
    public ResponseEntity<List<Address>> getAllAddresses(Pageable pageable) {
        log.debug("REST request to get a page of Addresses");
        Page<Address> page = addressRepository.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /addresses/:id} : get the "id" address.
     *
     * @param id the id of the address to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the address, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/addresses/{id}")
    public ResponseEntity<Address> getAddress(@PathVariable Long id) {
        log.debug("REST request to get Address : {}", id);
        Optional<Address> address = addressRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(address);
    }

    /**
     * {@code DELETE  /addresses/:id} : delete the "id" address.
     *
     * @param id the id of the address to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/addresses/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long id) {
        log.debug("REST request to delete Address : {}", id);
        addressRepository.deleteById(id);
        addressSearchRepository.deleteById(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }

    /**
     * {@code SEARCH  /_search/addresses?query=:query} : search for the address corresponding
     * to the query.
     *
     * @param query the query of the address search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/addresses")
    public ResponseEntity<List<Address>> searchAddresses(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Addresses for query {}", query);
        Page<Address> page = addressSearchRepository.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}

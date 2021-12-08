import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './product.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const ProductDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const productEntity = useAppSelector(state => state.product.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="productDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.product.detail.title">Product</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{productEntity.id}</dd>
          <dt>
            <span id="title">
              <Translate contentKey="jhipsterSampleApplicationApp.product.title">Title</Translate>
            </span>
          </dt>
          <dd>{productEntity.title}</dd>
          <dt>
            <span id="keywords">
              <Translate contentKey="jhipsterSampleApplicationApp.product.keywords">Keywords</Translate>
            </span>
          </dt>
          <dd>{productEntity.keywords}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="jhipsterSampleApplicationApp.product.description">Description</Translate>
            </span>
          </dt>
          <dd>{productEntity.description}</dd>
          <dt>
            <span id="rating">
              <Translate contentKey="jhipsterSampleApplicationApp.product.rating">Rating</Translate>
            </span>
          </dt>
          <dd>{productEntity.rating}</dd>
          <dt>
            <span id="dateAdded">
              <Translate contentKey="jhipsterSampleApplicationApp.product.dateAdded">Date Added</Translate>
            </span>
          </dt>
          <dd>
            {productEntity.dateAdded ? <TextFormat value={productEntity.dateAdded} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="dateModified">
              <Translate contentKey="jhipsterSampleApplicationApp.product.dateModified">Date Modified</Translate>
            </span>
          </dt>
          <dd>
            {productEntity.dateModified ? (
              <TextFormat value={productEntity.dateModified} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.product.wishList">Wish List</Translate>
          </dt>
          <dd>{productEntity.wishList ? productEntity.wishList.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/product" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/product/${productEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default ProductDetail;

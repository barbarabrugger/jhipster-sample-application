import React, { useEffect } from 'react';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from './category.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

export const CategoryDetail = (props: RouteComponentProps<{ id: string }>) => {
  const dispatch = useAppDispatch();

  useEffect(() => {
    dispatch(getEntity(props.match.params.id));
  }, []);

  const categoryEntity = useAppSelector(state => state.category.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="categoryDetailsHeading">
          <Translate contentKey="jhipsterSampleApplicationApp.category.detail.title">Category</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{categoryEntity.id}</dd>
          <dt>
            <span id="description">
              <Translate contentKey="jhipsterSampleApplicationApp.category.description">Description</Translate>
            </span>
          </dt>
          <dd>{categoryEntity.description}</dd>
          <dt>
            <span id="sortOrder">
              <Translate contentKey="jhipsterSampleApplicationApp.category.sortOrder">Sort Order</Translate>
            </span>
          </dt>
          <dd>{categoryEntity.sortOrder}</dd>
          <dt>
            <span id="dateAdded">
              <Translate contentKey="jhipsterSampleApplicationApp.category.dateAdded">Date Added</Translate>
            </span>
          </dt>
          <dd>
            {categoryEntity.dateAdded ? <TextFormat value={categoryEntity.dateAdded} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <span id="dateModified">
              <Translate contentKey="jhipsterSampleApplicationApp.category.dateModified">Date Modified</Translate>
            </span>
          </dt>
          <dd>
            {categoryEntity.dateModified ? (
              <TextFormat value={categoryEntity.dateModified} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="jhipsterSampleApplicationApp.category.status">Status</Translate>
            </span>
          </dt>
          <dd>{categoryEntity.status}</dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.category.parent">Parent</Translate>
          </dt>
          <dd>{categoryEntity.parent ? categoryEntity.parent.id : ''}</dd>
          <dt>
            <Translate contentKey="jhipsterSampleApplicationApp.category.product">Product</Translate>
          </dt>
          <dd>
            {categoryEntity.products
              ? categoryEntity.products.map((val, i) => (
                  <span key={val.id}>
                    <a>{val.title}</a>
                    {categoryEntity.products && i === categoryEntity.products.length - 1 ? '' : ', '}
                  </span>
                ))
              : null}
          </dd>
        </dl>
        <Button tag={Link} to="/category" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/category/${categoryEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default CategoryDetail;

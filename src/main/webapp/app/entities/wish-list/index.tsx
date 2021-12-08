import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import WishList from './wish-list';
import WishListDetail from './wish-list-detail';
import WishListUpdate from './wish-list-update';
import WishListDeleteDialog from './wish-list-delete-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={WishListUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={WishListUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={WishListDetail} />
      <ErrorBoundaryRoute path={match.url} component={WishList} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={WishListDeleteDialog} />
  </>
);

export default Routes;

import { loadingBarReducer as loadingBar } from 'react-redux-loading-bar';

import locale from './locale';
import authentication from './authentication';
import applicationProfile from './application-profile';

import administration from 'app/modules/administration/administration.reducer';
import userManagement from './user-management';
// prettier-ignore
import category from 'app/entities/category/category.reducer';
// prettier-ignore
import product from 'app/entities/product/product.reducer';
// prettier-ignore
import customer from 'app/entities/customer/customer.reducer';
// prettier-ignore
import address from 'app/entities/address/address.reducer';
// prettier-ignore
import wishList from 'app/entities/wish-list/wish-list.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const rootReducer = {
  authentication,
  locale,
  applicationProfile,
  administration,
  userManagement,
  category,
  product,
  customer,
  address,
  wishList,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
  loadingBar,
};

export default rootReducer;

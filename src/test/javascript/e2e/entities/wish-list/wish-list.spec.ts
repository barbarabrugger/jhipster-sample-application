import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import WishListComponentsPage from './wish-list.page-object';
import WishListUpdatePage from './wish-list-update.page-object';
import {
  waitUntilDisplayed,
  waitUntilAnyDisplayed,
  click,
  getRecordsCount,
  waitUntilHidden,
  waitUntilCount,
  isVisible,
} from '../../util/utils';

const expect = chai.expect;

describe('WishList e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let wishListComponentsPage: WishListComponentsPage;
  let wishListUpdatePage: WishListUpdatePage;
  const username = process.env.E2E_USERNAME ?? 'admin';
  const password = process.env.E2E_PASSWORD ?? 'admin';

  before(async () => {
    await browser.get('/');
    navBarPage = new NavBarPage();
    signInPage = await navBarPage.getSignInPage();
    await signInPage.loginWithOAuth(username, password);
    await waitUntilDisplayed(navBarPage.entityMenu);
    await waitUntilDisplayed(navBarPage.adminMenu);
    await waitUntilDisplayed(navBarPage.accountMenu);
  });

  beforeEach(async () => {
    await browser.get('/');
    await waitUntilDisplayed(navBarPage.entityMenu);
    wishListComponentsPage = new WishListComponentsPage();
    wishListComponentsPage = await wishListComponentsPage.goToPage(navBarPage);
  });

  it('should load WishLists', async () => {
    expect(await wishListComponentsPage.title.getText()).to.match(/Wish Lists/);
    expect(await wishListComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete WishLists', async () => {
    const beforeRecordsCount = (await isVisible(wishListComponentsPage.noRecords))
      ? 0
      : await getRecordsCount(wishListComponentsPage.table);
    wishListUpdatePage = await wishListComponentsPage.goToCreateWishList();
    await wishListUpdatePage.enterData();
    expect(await isVisible(wishListUpdatePage.saveButton)).to.be.false;

    expect(await wishListComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(wishListComponentsPage.table);
    await waitUntilCount(wishListComponentsPage.records, beforeRecordsCount + 1);
    expect(await wishListComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await wishListComponentsPage.deleteWishList();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(wishListComponentsPage.records, beforeRecordsCount);
      expect(await wishListComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(wishListComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});

import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import CategoryComponentsPage from './category.page-object';
import CategoryUpdatePage from './category-update.page-object';
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

describe('Category e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let categoryComponentsPage: CategoryComponentsPage;
  let categoryUpdatePage: CategoryUpdatePage;
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
    categoryComponentsPage = new CategoryComponentsPage();
    categoryComponentsPage = await categoryComponentsPage.goToPage(navBarPage);
  });

  it('should load Categories', async () => {
    expect(await categoryComponentsPage.title.getText()).to.match(/Categories/);
    expect(await categoryComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Categories', async () => {
    const beforeRecordsCount = (await isVisible(categoryComponentsPage.noRecords))
      ? 0
      : await getRecordsCount(categoryComponentsPage.table);
    categoryUpdatePage = await categoryComponentsPage.goToCreateCategory();
    await categoryUpdatePage.enterData();
    expect(await isVisible(categoryUpdatePage.saveButton)).to.be.false;

    expect(await categoryComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(categoryComponentsPage.table);
    await waitUntilCount(categoryComponentsPage.records, beforeRecordsCount + 1);
    expect(await categoryComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await categoryComponentsPage.deleteCategory();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(categoryComponentsPage.records, beforeRecordsCount);
      expect(await categoryComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(categoryComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});

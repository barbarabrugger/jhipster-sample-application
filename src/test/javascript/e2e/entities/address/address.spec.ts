import { browser, element, by } from 'protractor';

import NavBarPage from './../../page-objects/navbar-page';
import SignInPage from './../../page-objects/signin-page';
import AddressComponentsPage from './address.page-object';
import AddressUpdatePage from './address-update.page-object';
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

describe('Address e2e test', () => {
  let navBarPage: NavBarPage;
  let signInPage: SignInPage;
  let addressComponentsPage: AddressComponentsPage;
  let addressUpdatePage: AddressUpdatePage;
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
    addressComponentsPage = new AddressComponentsPage();
    addressComponentsPage = await addressComponentsPage.goToPage(navBarPage);
  });

  it('should load Addresses', async () => {
    expect(await addressComponentsPage.title.getText()).to.match(/Addresses/);
    expect(await addressComponentsPage.createButton.isEnabled()).to.be.true;
  });

  it('should create and delete Addresses', async () => {
    const beforeRecordsCount = (await isVisible(addressComponentsPage.noRecords)) ? 0 : await getRecordsCount(addressComponentsPage.table);
    addressUpdatePage = await addressComponentsPage.goToCreateAddress();
    await addressUpdatePage.enterData();
    expect(await isVisible(addressUpdatePage.saveButton)).to.be.false;

    expect(await addressComponentsPage.createButton.isEnabled()).to.be.true;
    await waitUntilDisplayed(addressComponentsPage.table);
    await waitUntilCount(addressComponentsPage.records, beforeRecordsCount + 1);
    expect(await addressComponentsPage.records.count()).to.eq(beforeRecordsCount + 1);

    await addressComponentsPage.deleteAddress();
    if (beforeRecordsCount !== 0) {
      await waitUntilCount(addressComponentsPage.records, beforeRecordsCount);
      expect(await addressComponentsPage.records.count()).to.eq(beforeRecordsCount);
    } else {
      await waitUntilDisplayed(addressComponentsPage.noRecords);
    }
  });

  after(async () => {
    await navBarPage.autoSignOut();
  });
});

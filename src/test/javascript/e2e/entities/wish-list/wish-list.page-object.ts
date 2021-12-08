import { element, by, ElementFinder, ElementArrayFinder } from 'protractor';

import { waitUntilAnyDisplayed, waitUntilDisplayed, click, waitUntilHidden, isVisible } from '../../util/utils';

import NavBarPage from './../../page-objects/navbar-page';

import WishListUpdatePage from './wish-list-update.page-object';

const expect = chai.expect;
export class WishListDeleteDialog {
  deleteModal = element(by.className('modal'));
  private dialogTitle: ElementFinder = element(by.id('jhipsterSampleApplicationApp.wishList.delete.question'));
  private confirmButton = element(by.id('jhi-confirm-delete-wishList'));

  getDialogTitle() {
    return this.dialogTitle;
  }

  async clickOnConfirmButton() {
    await this.confirmButton.click();
  }
}

export default class WishListComponentsPage {
  createButton: ElementFinder = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('div table .btn-danger'));
  title: ElementFinder = element(by.id('wish-list-heading'));
  noRecords: ElementFinder = element(by.css('#app-view-container .table-responsive div.alert.alert-warning'));
  table: ElementFinder = element(by.css('#app-view-container div.table-responsive > table'));

  records: ElementArrayFinder = this.table.all(by.css('tbody tr'));

  getDetailsButton(record: ElementFinder) {
    return record.element(by.css('a.btn.btn-info.btn-sm'));
  }

  getEditButton(record: ElementFinder) {
    return record.element(by.css('a.btn.btn-primary.btn-sm'));
  }

  getDeleteButton(record: ElementFinder) {
    return record.element(by.css('a.btn.btn-danger.btn-sm'));
  }

  async goToPage(navBarPage: NavBarPage) {
    await navBarPage.getEntityPage('wish-list');
    await waitUntilAnyDisplayed([this.noRecords, this.table]);
    return this;
  }

  async goToCreateWishList() {
    await this.createButton.click();
    return new WishListUpdatePage();
  }

  async deleteWishList() {
    const deleteButton = this.getDeleteButton(this.records.last());
    await click(deleteButton);

    const wishListDeleteDialog = new WishListDeleteDialog();
    await waitUntilDisplayed(wishListDeleteDialog.deleteModal);
    expect(await wishListDeleteDialog.getDialogTitle().getAttribute('id')).to.match(
      /jhipsterSampleApplicationApp.wishList.delete.question/
    );
    await wishListDeleteDialog.clickOnConfirmButton();

    await waitUntilHidden(wishListDeleteDialog.deleteModal);

    expect(await isVisible(wishListDeleteDialog.deleteModal)).to.be.false;
  }
}

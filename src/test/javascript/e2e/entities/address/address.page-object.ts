import { element, by, ElementFinder, ElementArrayFinder } from 'protractor';

import { waitUntilAnyDisplayed, waitUntilDisplayed, click, waitUntilHidden, isVisible } from '../../util/utils';

import NavBarPage from './../../page-objects/navbar-page';

import AddressUpdatePage from './address-update.page-object';

const expect = chai.expect;
export class AddressDeleteDialog {
  deleteModal = element(by.className('modal'));
  private dialogTitle: ElementFinder = element(by.id('jhipsterSampleApplicationApp.address.delete.question'));
  private confirmButton = element(by.id('jhi-confirm-delete-address'));

  getDialogTitle() {
    return this.dialogTitle;
  }

  async clickOnConfirmButton() {
    await this.confirmButton.click();
  }
}

export default class AddressComponentsPage {
  createButton: ElementFinder = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('div table .btn-danger'));
  title: ElementFinder = element(by.id('address-heading'));
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
    await navBarPage.getEntityPage('address');
    await waitUntilAnyDisplayed([this.noRecords, this.table]);
    return this;
  }

  async goToCreateAddress() {
    await this.createButton.click();
    return new AddressUpdatePage();
  }

  async deleteAddress() {
    const deleteButton = this.getDeleteButton(this.records.last());
    await click(deleteButton);

    const addressDeleteDialog = new AddressDeleteDialog();
    await waitUntilDisplayed(addressDeleteDialog.deleteModal);
    expect(await addressDeleteDialog.getDialogTitle().getAttribute('id')).to.match(/jhipsterSampleApplicationApp.address.delete.question/);
    await addressDeleteDialog.clickOnConfirmButton();

    await waitUntilHidden(addressDeleteDialog.deleteModal);

    expect(await isVisible(addressDeleteDialog.deleteModal)).to.be.false;
  }
}

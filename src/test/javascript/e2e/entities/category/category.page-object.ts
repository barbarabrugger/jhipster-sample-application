import { element, by, ElementFinder, ElementArrayFinder } from 'protractor';

import { waitUntilAnyDisplayed, waitUntilDisplayed, click, waitUntilHidden, isVisible } from '../../util/utils';

import NavBarPage from './../../page-objects/navbar-page';

import CategoryUpdatePage from './category-update.page-object';

const expect = chai.expect;
export class CategoryDeleteDialog {
  deleteModal = element(by.className('modal'));
  private dialogTitle: ElementFinder = element(by.id('jhipsterSampleApplicationApp.category.delete.question'));
  private confirmButton = element(by.id('jhi-confirm-delete-category'));

  getDialogTitle() {
    return this.dialogTitle;
  }

  async clickOnConfirmButton() {
    await this.confirmButton.click();
  }
}

export default class CategoryComponentsPage {
  createButton: ElementFinder = element(by.id('jh-create-entity'));
  deleteButtons = element.all(by.css('div table .btn-danger'));
  title: ElementFinder = element(by.id('category-heading'));
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
    await navBarPage.getEntityPage('category');
    await waitUntilAnyDisplayed([this.noRecords, this.table]);
    return this;
  }

  async goToCreateCategory() {
    await this.createButton.click();
    return new CategoryUpdatePage();
  }

  async deleteCategory() {
    const deleteButton = this.getDeleteButton(this.records.last());
    await click(deleteButton);

    const categoryDeleteDialog = new CategoryDeleteDialog();
    await waitUntilDisplayed(categoryDeleteDialog.deleteModal);
    expect(await categoryDeleteDialog.getDialogTitle().getAttribute('id')).to.match(
      /jhipsterSampleApplicationApp.category.delete.question/
    );
    await categoryDeleteDialog.clickOnConfirmButton();

    await waitUntilHidden(categoryDeleteDialog.deleteModal);

    expect(await isVisible(categoryDeleteDialog.deleteModal)).to.be.false;
  }
}

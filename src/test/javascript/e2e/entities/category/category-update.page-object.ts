import { element, by, ElementFinder } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class CategoryUpdatePage {
  pageTitle: ElementFinder = element(by.id('jhipsterSampleApplicationApp.category.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  descriptionInput: ElementFinder = element(by.css('input#category-description'));
  sortOrderInput: ElementFinder = element(by.css('input#category-sortOrder'));
  dateAddedInput: ElementFinder = element(by.css('input#category-dateAdded'));
  dateModifiedInput: ElementFinder = element(by.css('input#category-dateModified'));
  statusSelect: ElementFinder = element(by.css('select#category-status'));
  parentSelect: ElementFinder = element(by.css('select#category-parent'));
  productSelect: ElementFinder = element(by.css('select#category-product'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setDescriptionInput(description) {
    await this.descriptionInput.sendKeys(description);
  }

  async getDescriptionInput() {
    return this.descriptionInput.getAttribute('value');
  }

  async setSortOrderInput(sortOrder) {
    await this.sortOrderInput.sendKeys(sortOrder);
  }

  async getSortOrderInput() {
    return this.sortOrderInput.getAttribute('value');
  }

  async setDateAddedInput(dateAdded) {
    await this.dateAddedInput.sendKeys(dateAdded);
  }

  async getDateAddedInput() {
    return this.dateAddedInput.getAttribute('value');
  }

  async setDateModifiedInput(dateModified) {
    await this.dateModifiedInput.sendKeys(dateModified);
  }

  async getDateModifiedInput() {
    return this.dateModifiedInput.getAttribute('value');
  }

  async setStatusSelect(status) {
    await this.statusSelect.sendKeys(status);
  }

  async getStatusSelect() {
    return this.statusSelect.element(by.css('option:checked')).getText();
  }

  async statusSelectLastOption() {
    await this.statusSelect.all(by.tagName('option')).last().click();
  }
  async parentSelectLastOption() {
    await this.parentSelect.all(by.tagName('option')).last().click();
  }

  async parentSelectOption(option) {
    await this.parentSelect.sendKeys(option);
  }

  getParentSelect() {
    return this.parentSelect;
  }

  async getParentSelectedOption() {
    return this.parentSelect.element(by.css('option:checked')).getText();
  }

  async productSelectLastOption() {
    await this.productSelect.all(by.tagName('option')).last().click();
  }

  async productSelectOption(option) {
    await this.productSelect.sendKeys(option);
  }

  getProductSelect() {
    return this.productSelect;
  }

  async getProductSelectedOption() {
    return this.productSelect.element(by.css('option:checked')).getText();
  }

  async save() {
    await this.saveButton.click();
  }

  async cancel() {
    await this.cancelButton.click();
  }

  getSaveButton() {
    return this.saveButton;
  }

  async enterData() {
    await waitUntilDisplayed(this.saveButton);
    await this.setDescriptionInput('description');
    await waitUntilDisplayed(this.saveButton);
    await this.setSortOrderInput('5');
    await waitUntilDisplayed(this.saveButton);
    await this.setDateAddedInput('01-01-2001');
    await waitUntilDisplayed(this.saveButton);
    await this.setDateModifiedInput('01-01-2001');
    await waitUntilDisplayed(this.saveButton);
    await this.statusSelectLastOption();
    await this.parentSelectLastOption();
    // this.productSelectLastOption();
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}

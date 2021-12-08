import { element, by, ElementFinder } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class WishListUpdatePage {
  pageTitle: ElementFinder = element(by.id('jhipsterSampleApplicationApp.wishList.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  titleInput: ElementFinder = element(by.css('input#wish-list-title'));
  restrictedInput: ElementFinder = element(by.css('input#wish-list-restricted'));
  customerSelect: ElementFinder = element(by.css('select#wish-list-customer'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setTitleInput(title) {
    await this.titleInput.sendKeys(title);
  }

  async getTitleInput() {
    return this.titleInput.getAttribute('value');
  }

  getRestrictedInput() {
    return this.restrictedInput;
  }
  async customerSelectLastOption() {
    await this.customerSelect.all(by.tagName('option')).last().click();
  }

  async customerSelectOption(option) {
    await this.customerSelect.sendKeys(option);
  }

  getCustomerSelect() {
    return this.customerSelect;
  }

  async getCustomerSelectedOption() {
    return this.customerSelect.element(by.css('option:checked')).getText();
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
    await this.setTitleInput('title');
    await waitUntilDisplayed(this.saveButton);
    const selectedRestricted = await this.getRestrictedInput().isSelected();
    if (selectedRestricted) {
      await this.getRestrictedInput().click();
    } else {
      await this.getRestrictedInput().click();
    }
    await this.customerSelectLastOption();
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}

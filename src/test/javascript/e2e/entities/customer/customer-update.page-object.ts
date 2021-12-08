import { element, by, ElementFinder } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class CustomerUpdatePage {
  pageTitle: ElementFinder = element(by.id('jhipsterSampleApplicationApp.customer.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  firstNameInput: ElementFinder = element(by.css('input#customer-firstName'));
  lastNameInput: ElementFinder = element(by.css('input#customer-lastName'));
  emailInput: ElementFinder = element(by.css('input#customer-email'));
  telephoneInput: ElementFinder = element(by.css('input#customer-telephone'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setFirstNameInput(firstName) {
    await this.firstNameInput.sendKeys(firstName);
  }

  async getFirstNameInput() {
    return this.firstNameInput.getAttribute('value');
  }

  async setLastNameInput(lastName) {
    await this.lastNameInput.sendKeys(lastName);
  }

  async getLastNameInput() {
    return this.lastNameInput.getAttribute('value');
  }

  async setEmailInput(email) {
    await this.emailInput.sendKeys(email);
  }

  async getEmailInput() {
    return this.emailInput.getAttribute('value');
  }

  async setTelephoneInput(telephone) {
    await this.telephoneInput.sendKeys(telephone);
  }

  async getTelephoneInput() {
    return this.telephoneInput.getAttribute('value');
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
    await this.setFirstNameInput('firstName');
    await waitUntilDisplayed(this.saveButton);
    await this.setLastNameInput('lastName');
    await waitUntilDisplayed(this.saveButton);
    await this.setEmailInput('email');
    await waitUntilDisplayed(this.saveButton);
    await this.setTelephoneInput('telephone');
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}

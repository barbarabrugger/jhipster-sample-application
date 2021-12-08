import { element, by, ElementFinder } from 'protractor';
import { waitUntilDisplayed, waitUntilHidden, isVisible } from '../../util/utils';

const expect = chai.expect;

export default class AddressUpdatePage {
  pageTitle: ElementFinder = element(by.id('jhipsterSampleApplicationApp.address.home.createOrEditLabel'));
  saveButton: ElementFinder = element(by.id('save-entity'));
  cancelButton: ElementFinder = element(by.id('cancel-save'));
  address1Input: ElementFinder = element(by.css('input#address-address1'));
  address2Input: ElementFinder = element(by.css('input#address-address2'));
  cityInput: ElementFinder = element(by.css('input#address-city'));
  postcodeInput: ElementFinder = element(by.css('input#address-postcode'));
  countryInput: ElementFinder = element(by.css('input#address-country'));
  customerSelect: ElementFinder = element(by.css('select#address-customer'));

  getPageTitle() {
    return this.pageTitle;
  }

  async setAddress1Input(address1) {
    await this.address1Input.sendKeys(address1);
  }

  async getAddress1Input() {
    return this.address1Input.getAttribute('value');
  }

  async setAddress2Input(address2) {
    await this.address2Input.sendKeys(address2);
  }

  async getAddress2Input() {
    return this.address2Input.getAttribute('value');
  }

  async setCityInput(city) {
    await this.cityInput.sendKeys(city);
  }

  async getCityInput() {
    return this.cityInput.getAttribute('value');
  }

  async setPostcodeInput(postcode) {
    await this.postcodeInput.sendKeys(postcode);
  }

  async getPostcodeInput() {
    return this.postcodeInput.getAttribute('value');
  }

  async setCountryInput(country) {
    await this.countryInput.sendKeys(country);
  }

  async getCountryInput() {
    return this.countryInput.getAttribute('value');
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
    await this.setAddress1Input('address1');
    await waitUntilDisplayed(this.saveButton);
    await this.setAddress2Input('address2');
    await waitUntilDisplayed(this.saveButton);
    await this.setCityInput('city');
    await waitUntilDisplayed(this.saveButton);
    await this.setPostcodeInput('postcode');
    await waitUntilDisplayed(this.saveButton);
    await this.setCountryInput('country');
    await this.customerSelectLastOption();
    await this.save();
    await waitUntilHidden(this.saveButton);
  }
}

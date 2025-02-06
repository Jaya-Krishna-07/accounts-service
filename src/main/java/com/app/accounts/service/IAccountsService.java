package com.app.accounts.service;

import com.app.accounts.dto.CustomerDto;

public interface IAccountsService {
  /**
   * @param customerDto
   */
  void createAccount(CustomerDto customerDto);

  /**
   * @param mobileNumber
   * @return
   */
  CustomerDto fetchAccount(String mobileNumber);

  /**
   * @param customerDto
   * @return
   */
  boolean updateAccount(CustomerDto customerDto);

  boolean deleteAccount(String mobileNumber);

}

package com.app.accounts.service.impl;

import com.app.accounts.constants.AccountsConstants;
import com.app.accounts.dto.AccountsDto;
import com.app.accounts.dto.CustomerDto;
import com.app.accounts.entity.Accounts;
import com.app.accounts.entity.Customer;
import com.app.accounts.exception.CustomerAlreadyExistsException;
import com.app.accounts.exception.ResourceNotFoundException;
import com.app.accounts.mapper.AccountsMapper;
import com.app.accounts.mapper.CustomerMapper;
import com.app.accounts.repository.AccountsRepository;
import com.app.accounts.repository.CustomerRepository;
import com.app.accounts.service.IAccountsService;
import java.util.Optional;
import java.util.Random;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountsServiceImpl implements IAccountsService {
    private final AccountsRepository accountsRepository;
    private final CustomerRepository customerRepository;

    @Override
    public void createAccount(final CustomerDto customerDto) {
        final Customer customer = CustomerMapper.mapToCustomer(customerDto, new Customer());
        final Optional<Customer> optionalCustomer = customerRepository
                .findByMobileNumber(customerDto.getMobileNumber());
        if (optionalCustomer.isPresent()) {
            throw new CustomerAlreadyExistsException("Customer already registered with given mobile number: " +
                    customerDto.getMobileNumber());
        }
        final Customer savedCustomer = customerRepository.save(customer);
        accountsRepository.save(createNewAccount(savedCustomer));
    }

    @Override
    public CustomerDto fetchAccount(final String mobileNumber) {
        final Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        final Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Account", "customerId",
                                customer.getCustomerId().toString()));
        final CustomerDto customerDto = CustomerMapper.mapToCustomerDto(customer, new CustomerDto());
        customerDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));
        return customerDto;
    }

    @Override
    public boolean updateAccount(final CustomerDto customerDto) {
        boolean isUpdated = false;
        final AccountsDto accountsDto = customerDto.getAccountsDto();
        if (accountsDto != null) {
            Accounts accounts = accountsRepository.findById(accountsDto.getAccountNumber())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Account", "AccountNumber", accountsDto.getAccountNumber().toString()));
            AccountsMapper.mapToAccounts(accountsDto, accounts);
            accounts = accountsRepository.save(accounts);

            final Long customerId = accounts.getCustomerId();
            final Customer customer = customerRepository.findById(customerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer", "CustomerID", customerId.toString()));
            CustomerMapper.mapToCustomer(customerDto, customer);
            customerRepository.save(customer);
            isUpdated = true;
        }
        return isUpdated;
    }

    @Override
    public boolean deleteAccount(final String mobileNumber) {
        final Customer customer = customerRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        accountsRepository.deleteByCustomerId(customer.getCustomerId());
        customerRepository.deleteById(customer.getCustomerId());
        return true;
    }

    private Accounts createNewAccount(final Customer customer) {
        final Accounts newAccount = new Accounts();
        newAccount.setCustomerId(customer.getCustomerId());
        final long randomAccNumber = 1000000000L + new Random().nextInt(900000000);
        newAccount.setAccountNumber(randomAccNumber);
        newAccount.setAccountType(AccountsConstants.SAVINGS);
        newAccount.setBranchAddress(AccountsConstants.ADDRESS);
        return newAccount;
    }
}

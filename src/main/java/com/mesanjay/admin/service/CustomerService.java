package com.mesanjay.admin.service;

import com.mesanjay.admin.dto.CustomerDto;
import com.mesanjay.admin.model.Customer;

public interface CustomerService {
    Customer save(CustomerDto customerDto);

    Customer findByUsername(String username);

    Customer update(CustomerDto customerDto);

    Customer changePass(CustomerDto customerDto);

    CustomerDto getCustomer(String username);
}

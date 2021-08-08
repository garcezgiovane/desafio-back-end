package com.meta.challenge.service;

import com.meta.challenge.entity.Address;
import com.meta.challenge.entity.Customer;
import com.meta.challenge.repository.CustomerRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {

        return customerRepository.findAll();
    }

    public Customer saveCustomer(Customer customer) {

        for (Address address : customer.getAddresses()) {
            address.setCustomer(customer);
        }

        try {
            return customerRepository.save(customer);
        } catch( ConstraintViolationException | DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF already registered");
        }
    }

    public void delete(Long id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer not found."));

        customerRepository.delete(customer);
    }

    public void update(Long id, Customer customerRequest) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer not found."));

        copyProperties(customer, customerRequest, id);

        for (Address address : customer.getAddresses()) {
            address.setCustomer(customer);
        }
        customerRepository.save(customer);
    }

    private void copyProperties(Customer customer, Customer customerRequest, Long id) {
        BeanUtils.copyProperties(customerRequest, customer);
        customer.setId(id);
    }
}

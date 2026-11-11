
package com.example.bank_transfer_app.config;

import com.example.bank_transfer_app.model.Customer;
import com.example.bank_transfer_app.repository.CustomerRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner loadData(CustomerRepository customerRepository) {

        return args -> {

            if (customerRepository.countAccounts() == 0) {

                Customer kamal = new Customer(
                        "100001",
                        "Kamal",
                        "kamal@gmail.com",
                        new BigDecimal("50000.00")
                );

                Customer nimali = new Customer(
                        "100002",
                        "Nimali",
                        "nimali@gmail.com",
                        new BigDecimal("25000.00")
                );

                Customer saman = new Customer(
                        "100003",
                        "Saman",
                        "saman@gmail.com",
                        new BigDecimal("10000.00")
                );

                customerRepository.save(kamal);
                customerRepository.save(nimali);
                customerRepository.save(saman);

                System.out.println(
                        "Sample customers inserted successfully!"
                );

            } else {

                System.out.println(
                        "Sample data already exists!"
                );
            }
        };
    }
}

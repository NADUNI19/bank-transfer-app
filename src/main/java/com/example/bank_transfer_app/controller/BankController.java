
package com.example.bank_transfer_app.controller;

import com.example.bank_transfer_app.dto.TransferRequest;
import com.example.bank_transfer_app.model.Customer;
import com.example.bank_transfer_app.model.TransactionStatus;
import com.example.bank_transfer_app.repository.CustomerRepository;
import com.example.bank_transfer_app.repository.BankTransactionRepository;
import com.example.bank_transfer_app.service.BankService;
import com.example.bank_transfer_app.service.TransactionAuditService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class BankController {

    private final BankService bankService;
    private final TransactionAuditService auditService;
    private final CustomerRepository customerRepository;
    private final BankTransactionRepository transactionRepository;

    public BankController(
            BankService bankService,
            TransactionAuditService auditService,
            CustomerRepository customerRepository,
            BankTransactionRepository transactionRepository) {

        this.bankService = bankService;
        this.auditService = auditService;
        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
    }


    @GetMapping({"/", "/bank"})
    public String showDashboard(
            @RequestParam(required = false) String keyword,
            Model model) {

        if (keyword != null && !keyword.trim().isEmpty()) {

            model.addAttribute(
                    "customers",
                    customerRepository.searchByName(keyword.trim())
            );

        } else {

            model.addAttribute(
                    "customers",
                    customerRepository.findAllOrdered()
            );
        }

        model.addAttribute(
                "totalBalance",
                customerRepository.getTotalBalance()
        );

        model.addAttribute(
                "successCount",
                transactionRepository.countByStatus(
                        TransactionStatus.SUCCESS)
        );

        model.addAttribute(
                "failedCount",
                transactionRepository.countByStatus(
                        TransactionStatus.FAILED)
        );

        model.addAttribute(
                "transactions",
                transactionRepository.findAllLatest()
        );

        return "bank";
    }



    @PostMapping("/customers")
    public String addCustomer(
            @ModelAttribute Customer customer,
            RedirectAttributes redirectAttributes) {

        try {

            bankService.createCustomer(customer);

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Customer added successfully!"
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/bank";
    }



    @PostMapping("/transfer")
    public String transferMoney(
            @ModelAttribute TransferRequest request,
            RedirectAttributes redirectAttributes) {

        try {

            bankService.transfer(
                    request.getFromAccountNumber(),
                    request.getToAccountNumber(),
                    request.getAmount(),
                    request.isSimulateFailure()
            );

            redirectAttributes.addFlashAttribute(
                    "message",
                    "Money transferred successfully!"
            );

        } catch (Exception e) {

            // Record failed transaction separately
            if (request.getFromAccountNumber() != null
                    && request.getToAccountNumber() != null
                    && request.getAmount() != null
                    && request.getAmount().compareTo(BigDecimal.ZERO) > 0) {

                try {

                    auditService.recordFailure(
                            request.getFromAccountNumber(),
                            request.getToAccountNumber(),
                            request.getAmount()
                    );

                } catch (Exception auditException) {

                    System.out.println(
                            "Failed to record audit: "
                                    + auditException.getMessage()
                    );
                }
            }

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/bank";
    }



    @GetMapping("/history")
    public String showHistory(Model model) {

        model.addAttribute(
                "transactions",
                transactionRepository.findAllLatest()
        );

        return "history";
    }


}

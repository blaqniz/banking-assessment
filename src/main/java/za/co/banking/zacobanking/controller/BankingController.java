package za.co.banking.zacobanking.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.banking.zacobanking.entity.Client;
import za.co.banking.zacobanking.entity.ClientAccount;
import za.co.banking.zacobanking.entity.response.*;
import za.co.banking.zacobanking.exception.*;
import za.co.banking.zacobanking.service.ClientService;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value = "banking-api")
@CrossOrigin(origins = "*")
public class BankingController {

    private static Logger logger = LoggerFactory.getLogger(BankingController.class);

    private ClientService clientService;

    public BankingController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping("/transactional-accounts/{clientId}")
    public ResponseEntity<TransactionalAccountResponse> getTransactionalAccounts(@PathVariable final Integer clientId) {
        final TransactionalAccountResponse transactionalAccountResponse = new TransactionalAccountResponse();
        try {
            final List<ClientAccount> transactionalAccounts = clientService.getTransactionalAccounts(clientId);
            transactionalAccountResponse.getClientAccounts().addAll(transactionalAccounts);
            return new ResponseEntity<>(transactionalAccountResponse, HttpStatus.OK);
        } catch (ClientNotFoundException e) {
            logger.error("Client with id: " + clientId + " not found!", e.getMessage());
            transactionalAccountResponse.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(transactionalAccountResponse, HttpStatus.OK);
        } catch (ClientTransactionalAccountsNotFound e) {
            logger.error("No transactional accounts to display for client: ." + clientId, e);
            transactionalAccountResponse.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(transactionalAccountResponse, HttpStatus.OK);
        }
    }

    @GetMapping("/currency-accounts/{clientId}")
    public ResponseEntity<CurrencyAccountResponse> getCurrencyAccounts(@PathVariable final Integer clientId) {
        final CurrencyAccountResponse currencyAccountResponse = new CurrencyAccountResponse();
        try {
            final List<ClientAccount> currencyAccounts = clientService.getCurrencyAccounts(clientId);
            currencyAccountResponse.setClientAccounts(currencyAccounts);
            return new ResponseEntity<>(currencyAccountResponse, HttpStatus.OK);
        } catch (ClientCurrencyAccountsNotFound e) {
            currencyAccountResponse.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(currencyAccountResponse, HttpStatus.OK);
        } catch (ClientNotFoundException e) {
            currencyAccountResponse.setErrorMessage("Client with id: " + clientId + " not found!");
            return new ResponseEntity<>(currencyAccountResponse, HttpStatus.OK);
        }
    }

    @PutMapping("/withdraw/{withdrawalAmount}/{clientId}/{accountNumber}/{atmId}")
    public WithdrawalResponse withdraw(@PathVariable final BigDecimal withdrawalAmount, @PathVariable final Integer clientId,
                                       @PathVariable final String accountNumber, @PathVariable final Integer atmId) {
        try {
            final WithdrawalResponse withdraw = clientService.withdraw(withdrawalAmount, clientId, accountNumber, atmId);
            return withdraw;
        } catch (InsufficientFundsException e) {
            logger.error("You do not have enough funds in your account. ", e);
            return new WithdrawalResponse(Boolean.FALSE, e.getMessage());
        } catch (NotesAndCurrenciesNotAvailableException e) {
            logger.error(e.getMessage());
            return new WithdrawalResponse(Boolean.FALSE, e.getMessage());
        } catch (NonWithdrawalClientAccount e) {
            logger.error(e.getMessage());
            return new WithdrawalResponse(Boolean.FALSE, e.getMessage());
        } catch (AccountNotFoundException e) {
            logger.error(e.getMessage());
            return new WithdrawalResponse(Boolean.FALSE, e.getMessage());
        } catch (MaxOverdraftViolationException e) {
            logger.error(e.getMessage());
            return new WithdrawalResponse(Boolean.FALSE, e.getMessage());
        } catch (AtmNotRegisteredException e) {
            logger.error("ATM not found", e.getMessage());
            return new WithdrawalResponse(Boolean.FALSE, e.getMessage());
        } catch (InvalidAmountRequested e) {
            logger.error("ATM not found", e.getMessage());
            return new WithdrawalResponse(Boolean.FALSE, e.getMessage());
        }
    }

    @GetMapping("highest-transactional-balance/{clientId}")
    public ResponseEntity<ClientAccountResponse> getClientHighestTransactionalBalances(@PathVariable final Integer clientId) {
        final ClientAccountResponse clientAccountResponse = new ClientAccountResponse();
        try {
            final ClientAccount highestTransactionalBalances = clientService.getHighestTransactionalBalances(clientId);
            clientAccountResponse.setClientAccount(highestTransactionalBalances);
            return new ResponseEntity<>(clientAccountResponse, HttpStatus.OK);
        } catch (ClientTransactionalAccountsNotFound e) {
            logger.error("Client does not have transactional accounts to display.");
            clientAccountResponse.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(clientAccountResponse, HttpStatus.OK);
        } catch (ClientNotFoundException e) {
            clientAccountResponse.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(clientAccountResponse, HttpStatus.OK);
        }
    }

    @GetMapping("clients-with-highest-transactional-balance")
    public ResponseEntity<ClientResponse> getClientsWithHighestTransactionalBalances() {
        final ClientResponse clientResponse = new ClientResponse();
        try {
            final List<Client> clientsWithHighestTransactionalBalances = clientService.getClientsWithHighestTransactionalBalances();
            clientResponse.setClients(clientsWithHighestTransactionalBalances);
            return new ResponseEntity<>(clientResponse, HttpStatus.OK);
        } catch (ClientTransactionalAccountsNotFound e) {
            clientResponse.setErrorMessage(e.getMessage());
            return new ResponseEntity<>(clientResponse, HttpStatus.OK);
        } catch (ClientNotFoundException e) {
            clientResponse.setErrorMessage("Client not found.");
            return new ResponseEntity<>(clientResponse, HttpStatus.OK);
        }
    }

    @GetMapping("aggregate-financial-position/{clientId}")
    public ResponseEntity<AggregateFinancialPositionResponse> calculateAggregateFinancialPosition(@PathVariable final Integer clientId) {
        final AggregateFinancialPositionResponse aggregateFinancialPosition = clientService.getAggregateFinancialPosition(clientId);
        return new ResponseEntity<>(aggregateFinancialPosition, HttpStatus.OK);
    }

}

package za.co.banking.zacobanking.service;

import za.co.banking.zacobanking.entity.Client;
import za.co.banking.zacobanking.entity.ClientAccount;
import za.co.banking.zacobanking.entity.response.AggregateFinancialPositionResponse;
import za.co.banking.zacobanking.entity.response.WithdrawalResponse;
import za.co.banking.zacobanking.exception.*;

import java.math.BigDecimal;
import java.util.List;

public interface ClientService {

    List<ClientAccount> getTransactionalAccounts(Integer clientId) throws ClientNotFoundException;

    List<ClientAccount> getCurrencyAccounts(Integer clientId);

    WithdrawalResponse withdraw(BigDecimal withdrawalAmount, int clientId, String accountNumber, int atmId)
            throws InsufficientFundsException,
            NotesAndCurrenciesNotAvailableException,
            NonWithdrawalClientAccount,
            AccountNotFoundException, MaxOverdraftViolationException;

    ClientAccount getHighestTransactionalBalances(Integer clientId) throws ClientNotFoundException;

    List<Client> getClientsWithHighestTransactionalBalances();

    AggregateFinancialPositionResponse getAggregateFinancialPosition(Integer clientId);
}

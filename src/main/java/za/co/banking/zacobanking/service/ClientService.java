package za.co.banking.zacobanking.service;

import za.co.banking.zacobanking.entity.Client;
import za.co.banking.zacobanking.entity.ClientAccount;
import za.co.banking.zacobanking.entity.response.AggregateFinancialPositionResponse;
import za.co.banking.zacobanking.entity.response.WithdrawalResponse;
import za.co.banking.zacobanking.exception.*;

import java.math.BigDecimal;
import java.util.List;

public interface ClientService {

    List<ClientAccount> getTransactionalAccounts(Integer clientId) throws ClientNotFoundException, ClientTransactionalAccountsNotFound;

    List<ClientAccount> getCurrencyAccounts(Integer clientId) throws ClientCurrencyAccountsNotFound, ClientNotFoundException;

    WithdrawalResponse withdraw(BigDecimal withdrawalAmount, int clientId, String accountNumber, Integer atmId)
            throws InsufficientFundsException,
            NotesAndCurrenciesNotAvailableException,
            NonWithdrawalClientAccount,
            AccountNotFoundException, MaxOverdraftViolationException, AtmNotRegisteredException, InvalidAmountRequested;

    ClientAccount getHighestTransactionalBalances(Integer clientId) throws ClientTransactionalAccountsNotFound, ClientNotFoundException;

    List<Client> getClientsWithHighestTransactionalBalances() throws ClientTransactionalAccountsNotFound, ClientNotFoundException;

    AggregateFinancialPositionResponse getAggregateFinancialPosition(Integer clientId);
}

package za.co.banking.zacobanking.service;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import za.co.banking.zacobanking.entity.AtmAllocation;
import za.co.banking.zacobanking.entity.Client;
import za.co.banking.zacobanking.entity.ClientAccount;
import za.co.banking.zacobanking.entity.response.AggregateFinancialPositionResponse;
import za.co.banking.zacobanking.entity.response.WithdrawalResponse;
import za.co.banking.zacobanking.exception.*;
import za.co.banking.zacobanking.repository.AtmAllocationRepository;
import za.co.banking.zacobanking.repository.ClientRepository;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ClientServiceImpl implements ClientService {

    private static final String CHEQUE_ACCOUNT = "CHQ";
    private static final String PERSONAL_LOAN = "PLOAN";
    private static final String HOME_LOAN = "HLOAN";
    private static boolean SUCCESS = false;
    private static Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
    private ClientRepository clientRepository;
    private CurrencyConverterService currencyConverterService;
    private AtmAllocationRepository atmAllocationRepository;
    private AggregateFinancialPositionResponse response;
    private WithdrawalResponse withdrawalResponse;

    public ClientServiceImpl(ClientRepository clientRepository, CurrencyConverterService currencyConverterService,
                             AtmAllocationRepository atmAllocationRepository, AggregateFinancialPositionResponse response,
                             WithdrawalResponse withdrawalResponse) {
        this.clientRepository = clientRepository;
        this.currencyConverterService = currencyConverterService;
        this.atmAllocationRepository = atmAllocationRepository;
        this.response = response;
        this.withdrawalResponse = withdrawalResponse;
    }

    @Override
    public List<ClientAccount> getTransactionalAccounts(final Integer clientId) throws ClientNotFoundException {
        return clientRepository.findById(clientId).orElseThrow(ClientNotFoundException::new).getClientAccounts()
                .stream().sorted(Comparator.comparing(ClientAccount::getDisplayBalance).reversed())
                .filter(clientAccount -> clientAccount.getAccountType().isTransactional()).collect(Collectors.toList());
    }

    @Override
    public List<ClientAccount> getCurrencyAccounts(final Integer clientId) {
        final List<ClientAccount> currencyAccounts = clientRepository.findById(clientId).orElse(null).getClientAccounts()
                .stream().filter(clientAccount -> !clientAccount.getAccountType().isTransactional()).collect(Collectors.toList());

        final List<ClientAccount> allNonZARCurrenciesConverted = currencyConverterService.getAllNonZARCurrenciesConverted(currencyAccounts);
        return allNonZARCurrenciesConverted;

    }

    @Override
    public WithdrawalResponse withdraw(final BigDecimal withdrawalAmount, final int clientId, final String accountNumber, final int atmId)
            throws  InsufficientFundsException, NotesAndCurrenciesNotAvailableException, NonWithdrawalClientAccount,
                    AccountNotFoundException, MaxOverdraftViolationException {

        final Optional<Client> client = clientRepository.findById(clientId);
        final Optional<ClientAccount> clientAccount = client.orElse(null).getClientAccounts()
                .stream().filter(account -> account.getClientAccountNumber().equals(accountNumber)).findFirst();

        if (!clientAccount.isPresent()) {
            withdrawalResponse.setSuccess(Boolean.FALSE);
            throw new AccountNotFoundException("There is no account linked to specified account number.");
        }

        final ClientAccount account = clientAccount.get();
        final boolean isTransactionalAccount = isTransactionalAccount(account);
        if (!isTransactionalAccount) {
            withdrawalResponse.setSuccess(Boolean.FALSE);
            throw new NonWithdrawalClientAccount("The account you have selected does not allow withdrawals. You can only withdraw from a transactional account.");
        }

        final BigDecimal maxOverdraft = BigDecimal.valueOf(10000.00);
        if (!account.getAccountType().getAccountTypeCode().equals(CHEQUE_ACCOUNT) &&
                    account.getDisplayBalance().compareTo(withdrawalAmount) < 0) {
            throw new InsufficientFundsException("\nAvailable amount: " + account.getDisplayBalance().toString() + "\nWithdrawal amount: " + withdrawalAmount);
        } else if (account.getAccountType().getAccountTypeCode().equals(CHEQUE_ACCOUNT) &&
                BigDecimal.valueOf(Math.abs(account.getDisplayBalance().subtract(withdrawalAmount).doubleValue())).compareTo(maxOverdraft) > 0) {
            throw new MaxOverdraftViolationException("You have attempted to go beyond your overdraft limit of R10 000.00.");
        }

        processNotesAndCoins(withdrawalAmount.doubleValue(), atmId);

        if (SUCCESS) {
            final BigDecimal updatedBalance = account.getDisplayBalance().subtract(withdrawalAmount);
            client.get().getClientAccounts().remove(clientAccount);
            account.setDisplayBalance(updatedBalance);
            client.get().getClientAccounts().add(account);
            clientRepository.save(client.get());
            withdrawalResponse.setMessage("Transaction processed successfully!");
            withdrawalResponse.setSuccess(Boolean.TRUE);
            logger.info("Transaction processed successfully!");
            return withdrawalResponse;
        } else {
            withdrawalResponse.setSuccess(Boolean.FALSE);
            throw new NotesAndCurrenciesNotAvailableException("We are very sorry! We do not have enough notes and coins for the specified amount: \" + withdrawalAmount");
        }
    }

    private boolean isTransactionalAccount(final ClientAccount clientAccount) {
        return clientAccount.getAccountType().isTransactional();
    }

    private void processNotesAndCoins(final double withdrawalAmount, final int atmId) {

        final List<AtmAllocation> atmAllocations = atmAllocationRepository.findAll()
                .stream().filter(atmAllocation -> atmAllocation.getAtm().getAtmId() == atmId).collect(Collectors.toList());

        double []bankNotes = { 200, 100, 50, 20, 10, 5, 0.5, 0.2, 0.1, 0.05 };

        int []noteCounter = new int[bankNotes.length];

        final DecimalFormat decimalFormat = new DecimalFormat("0.00");

        extractDenominatorCurrencies(withdrawalAmount, bankNotes, noteCounter, decimalFormat);

        final Map<Double, Integer> denominationCount = IntStream.range(0, bankNotes.length).filter(i -> noteCounter[i] != 0).boxed()
                .collect(Collectors.toMap(i -> Double.valueOf(bankNotes[i]), i -> Integer.valueOf(noteCounter[i]), (a, b) -> b));

        int denominationValuesCount = 0;
        for (Map.Entry<Double, Integer> keyValueEntry : denominationCount.entrySet()) {
            Double denominationValue = keyValueEntry.getKey();
            Integer denominationQuantity = keyValueEntry.getValue();
            for (AtmAllocation atmAllocation : atmAllocations) {
                if (atmAllocation.getDenomination().getValue().doubleValue() == denominationValue) {
                    atmAllocation.setCount(atmAllocation.getCount() - denominationQuantity);
                    denominationValuesCount++;
                }
            }
        }
        SUCCESS = (denominationValuesCount == denominationCount.entrySet().size()) ? true : false;
    }

    private void extractDenominatorCurrencies(double withdrawalAmount, double[] bankNotes, int[] noteCounter, DecimalFormat decimalFormat) {
        for (int i = 0; i < bankNotes.length; i++) {
            if (withdrawalAmount >= bankNotes[i]) {
                noteCounter[i] = (int) (withdrawalAmount / bankNotes[i]);
                withdrawalAmount = Double.valueOf(decimalFormat.format (withdrawalAmount - (noteCounter[i] * bankNotes[i])));
            }
        }
    }

    @Override
    public ClientAccount getHighestTransactionalBalances(final Integer clientId) throws ClientNotFoundException {
        final List<ClientAccount> clientTransactionalAccounts = getTransactionalAccounts(clientId);
        if (clientTransactionalAccounts.size() > 0) {
            final ClientAccount maxBalanceAccount = clientTransactionalAccounts.stream()
                    .max(Comparator.comparing(ClientAccount::getDisplayBalance)).orElse(null);
            return maxBalanceAccount;
        }
        logger.info("Client with id: " + clientId + " does not have any transactional accounts.");
        return null;
    }

    @Override
    public List<Client> getClientsWithHighestTransactionalBalances()  {

        final List<Client> clients = clientRepository.findAll();

         //Used to avoid ConcurrentModificationException when modifying a list currently being iterated over
        final Iterator<Client> clientIterator = clientRepository.findAll().iterator();

        while (clientIterator.hasNext()) {
            Client client = clientIterator.next();
            try {

                final ClientAccount maxBalanceAccount = getHighestTransactionalBalances(client.getClientId());

                /**
                 * Not clear as to whether we ignore clients that do not have transactional accounts or not
                 * I chose to include them for reporting purposes as it is not explicitly specified to exclude such.
                 */

                if (maxBalanceAccount != null) {
                    client.getClientAccounts().clear();
                    client.getClientAccounts().add(maxBalanceAccount);
                } else {
                    clients.remove(client);
                }
            } catch (ClientNotFoundException e) {
                logger.error("Client with specified id not found!", e);
            }
        }
        return clients;
    }

    @Override
    public AggregateFinancialPositionResponse getAggregateFinancialPosition(final Integer clientId) {
        final Client client = clientRepository.findById(clientId).get();
        final BigDecimal[] netPosition = {new BigDecimal(0)};
        List<ClientAccount> clientAccounts = client.getClientAccounts();
        clientAccounts.forEach(clientAccount -> netPosition[0] = netPosition[0].add(clientAccount.getDisplayBalance()));
        response.setClient(client);
        response.setNetPosition(netPosition[0]);
        return response;
    }
}

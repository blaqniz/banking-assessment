package za.co.banking.zacobanking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import za.co.banking.zacobanking.entity.AtmAllocation;
import za.co.banking.zacobanking.entity.Client;
import za.co.banking.zacobanking.entity.ClientAccount;
import za.co.banking.zacobanking.entity.response.AggregateFinancialPositionResponse;
import za.co.banking.zacobanking.entity.response.ProcessNotesAndCoinsResponse;
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
    private static final String CURRENCY_ACCOUNT = "CFCA";
    private static boolean SUCCESS = false;
    private static Logger logger = LoggerFactory.getLogger(ClientServiceImpl.class);
    private ClientRepository clientRepository;
    private CurrencyConverterService currencyConverterService;
    private AtmAllocationRepository atmAllocationRepository;

    public ClientServiceImpl(ClientRepository clientRepository, CurrencyConverterService currencyConverterService,
                             AtmAllocationRepository atmAllocationRepository) {
        this.clientRepository = clientRepository;
        this.currencyConverterService = currencyConverterService;
        this.atmAllocationRepository = atmAllocationRepository;
    }

    @Override
    public List<ClientAccount> getTransactionalAccounts(final Integer clientId) throws ClientTransactionalAccountsNotFound, ClientNotFoundException {
        final Optional<Client> client = clientRepository.findById(clientId);
        if (client.isPresent()) {
            final List<ClientAccount> clientAccounts = client.get().getClientAccounts()
                    .stream().sorted(Comparator.comparing(ClientAccount::getDisplayBalance).reversed())
                    .filter(clientAccount -> clientAccount.getAccountType().isTransactional()).collect(Collectors.toList());

            if (clientAccounts == null) {
                throw new ClientTransactionalAccountsNotFound("No accounts to display.");
            }

            return clientAccounts;
        }
        throw new ClientNotFoundException("Client with id: " + clientId + " does not exist.");
    }

    @Override
    public List<ClientAccount> getCurrencyAccounts(final Integer clientId) throws ClientCurrencyAccountsNotFound, ClientNotFoundException {
        final Optional<Client> client = clientRepository.findById(clientId);
        if (client.isPresent()) {
            final List<ClientAccount> currencyAccounts = client.get().getClientAccounts()
                    .stream().filter(clientAccount -> clientAccount.getAccountType().getAccountTypeCode().equals(CURRENCY_ACCOUNT)).collect(Collectors.toList());

            if (currencyAccounts.size() == 0) {
                throw new ClientCurrencyAccountsNotFound("No accounts to display.");
            }

            final List<ClientAccount> allNonZARCurrenciesConverted = currencyConverterService.getAllNonZARCurrenciesConverted(currencyAccounts);
            return allNonZARCurrenciesConverted;
        } else {
            throw new ClientNotFoundException("Client with id: " + clientId + " does not exist.");
        }
    }

    @Override
    public WithdrawalResponse withdraw(final BigDecimal withdrawalAmount, final int clientId, final String accountNumber, final int atmId)
            throws InsufficientFundsException, NotesAndCurrenciesNotAvailableException, NonWithdrawalClientAccount,
            AccountNotFoundException, MaxOverdraftViolationException, AtmNotRegisteredException, InvalidAmountRequested {
        if (withdrawalAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal totalSuggestedAmount = BigDecimal.valueOf(0);

            final Optional<Client> client = clientRepository.findById(clientId);
            final Optional<ClientAccount> clientAccount = client.get().getClientAccounts()
                    .stream().filter(account -> account.getClientAccountNumber().equals(accountNumber)).findFirst();

            final WithdrawalResponse withdrawalResponse = new WithdrawalResponse();

            if (!clientAccount.isPresent()) {
                withdrawalResponse.setSuccess(Boolean.FALSE);
                throw new AccountNotFoundException("There is no account linked to specified account number.");
            }

            final ClientAccount account = clientAccount.get();
            final BigDecimal maxOverdraft = BigDecimal.valueOf(10000.00);
            final boolean isTransactionalAccount = isTransactionalAccount(account);

            if (!isTransactionalAccount) {
                withdrawalResponse.setSuccess(Boolean.FALSE);
                throw new NonWithdrawalClientAccount("The account you have selected does not allow withdrawals. You can only withdraw from a transactional account.");
            } else if (!account.getAccountType().getAccountTypeCode().equals(CHEQUE_ACCOUNT) &&
                    account.getDisplayBalance().compareTo(withdrawalAmount) < 0 && withdrawalAmount != BigDecimal.ZERO) {
                throw new InsufficientFundsException("Insufficient funds.");
            } else if (account.getAccountType().getAccountTypeCode().equals(CHEQUE_ACCOUNT) && account.getDisplayBalance().compareTo(BigDecimal.ZERO) < 0) {
                final boolean isOverdraftExceeded = BigDecimal.valueOf(Math.abs(account.getDisplayBalance()
                        .subtract(withdrawalAmount).doubleValue())).compareTo(maxOverdraft) > 0;
                if (isOverdraftExceeded) {
                    throw new MaxOverdraftViolationException("Insufficient funds. You tried to exceed your overdraft limit of R10 000.00.");
                }
            }

            final ProcessNotesAndCoinsResponse response = processNotesAndCoins(withdrawalAmount.doubleValue(), atmId, totalSuggestedAmount);

            if (response.getSuccess() == Boolean.TRUE) {
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
                if (response.getTotalSuggestedAmount().equals(0)) {
                    throw new NotesAndCurrenciesNotAvailableException("Insufficient funds.");
                }
                throw new NotesAndCurrenciesNotAvailableException("Amount not available, would you like to draw " + response.getTotalSuggestedAmount());
            }
        } else {
            throw new InvalidAmountRequested("Please enter a valid withdrawal amount grater than 0.");
        }
    }

    private boolean isTransactionalAccount(final ClientAccount clientAccount) {
        return clientAccount.getAccountType().isTransactional();
    }

    private ProcessNotesAndCoinsResponse processNotesAndCoins(final double withdrawalAmount, final int atmId, BigDecimal totalSuggestedAmount) throws AtmNotRegisteredException {

        final ProcessNotesAndCoinsResponse response = new ProcessNotesAndCoinsResponse();

        final List<AtmAllocation> atmAllocations = atmAllocationRepository.findAll()
                .stream().filter(atmAllocation -> atmAllocation.getAtm().getAtmId() == atmId).collect(Collectors.toList());

        if (atmAllocations.size() == 0) {
            throw new AtmNotRegisteredException("ATM not registered or unfunded.");
        }

        double[] bankNotes = {200, 100, 50, 20, 10, 5, 0.5, 0.2, 0.1, 0.05};

        int[] noteCounter = new int[bankNotes.length];

        final DecimalFormat decimalFormat = new DecimalFormat("0.00");

        extractDenominatorCurrencies(withdrawalAmount, bankNotes, noteCounter, decimalFormat, atmAllocations);

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
                    final BigDecimal totalAmount = BigDecimal.valueOf(denominationValue * denominationQuantity);
                    totalSuggestedAmount = totalSuggestedAmount.add(totalAmount);
                }
            }
        }
        SUCCESS = (denominationValuesCount == denominationCount.entrySet().size()) ? true : false;
        response.setSuccess(SUCCESS);
        response.setTotalSuggestedAmount(totalSuggestedAmount);
        return response;
    }

    private void extractDenominatorCurrencies(double withdrawalAmount, double[] bankNotes, int[] noteCounter, DecimalFormat decimalFormat,
                                              List<AtmAllocation> atmAllocations) {
        for (int i = 0; i < bankNotes.length; i++) {
            final double bankNote = bankNotes[i];
            if (withdrawalAmount >= bankNote) {
                /*if (isNoteOrCoinAvailable(BigDecimal.valueOf(bankNote), atmAllocations)) {
                    noteCounter[i] = (int) (withdrawalAmount / bankNote);
                    withdrawalAmount = Double.valueOf(decimalFormat.format(withdrawalAmount - (noteCounter[i] * bankNote)));
                }*/
                noteCounter[i] = (int) (withdrawalAmount / bankNote);
                withdrawalAmount = Double.valueOf(decimalFormat.format(withdrawalAmount - (noteCounter[i] * bankNote)));
            }
        }
    }

    private boolean isNoteOrCoinAvailable(BigDecimal bankNote, List<AtmAllocation> atmAllocations) {
        return atmAllocations.stream().filter(atmAllocation -> atmAllocation.getDenomination().getValue().compareTo(bankNote) == 0)
                .findFirst().isPresent();
    }

    @Override
    public ClientAccount getHighestTransactionalBalances(final Integer clientId) throws ClientTransactionalAccountsNotFound, ClientNotFoundException {
        final List<ClientAccount> clientTransactionalAccounts = getTransactionalAccounts(clientId);
        if (clientTransactionalAccounts.size() > 0) {
            final ClientAccount maxBalanceAccount = clientTransactionalAccounts.stream()
                    .max(Comparator.comparing(ClientAccount::getDisplayBalance)).orElse(null);
            return maxBalanceAccount;
        }
        return null;
    }

    @Override
    public List<Client> getClientsWithHighestTransactionalBalances() throws ClientTransactionalAccountsNotFound, ClientNotFoundException {

        final List<Client> clients = clientRepository.findAll();

        //Used to avoid ConcurrentModificationException when modifying a list currently being iterated over
        final Iterator<Client> clientIterator = clientRepository.findAll().iterator();

        while (clientIterator.hasNext()) {
            Client client = clientIterator.next();

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

        }
        return clients;
    }

    @Override
    public AggregateFinancialPositionResponse getAggregateFinancialPosition(final Integer clientId) {
        final Client client = clientRepository.findById(clientId).get();
        final BigDecimal[] netPosition = {new BigDecimal(0)};
        final AggregateFinancialPositionResponse response = new AggregateFinancialPositionResponse();

        List<ClientAccount> clientAccounts = client.getClientAccounts();
        clientAccounts.forEach(clientAccount -> netPosition[0] = netPosition[0].add(clientAccount.getDisplayBalance()));
        response.setClient(client);
        response.setNetPosition(netPosition[0]);
        return response;
    }
}

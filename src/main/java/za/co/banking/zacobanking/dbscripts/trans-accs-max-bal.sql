
/*
Retrieve all clients with transactional accounts with only maximum balance account shown
*/

SELECT c.client_id "Client Id", c.surname  "Surname", ca.client_account_number "Client Account Number", at.description "Account Description", max(ca.display_balance) "Display Balance"
FROM client c, client_account ca, account_type at
WHERE c.client_id = ca.client_id
AND ca.account_type_code = at.account_type_code
AND at.transactional = 1
GROUP  BY c.client_id, c.surname, ca.client_account_number, at.description;


/*
Retrieve all clients with transactional accounts with only maximum balance account shown
*/

SELECT  c.client_id "Client Id", c.surname  "Surname", ca.client_account_number "Client Account Number", at.description "Account Description", ca.display_balance "Display Balance"
FROM    client c, client_account ca, account_type at
WHERE   c.client_id = ca.client_id
AND     ca.account_type_code = at.account_type_code
AND     at.transactional = 1
AND     ca.display_balance IN(SELECT MAX(ca.display_balance)
                              FROM client_account ca
                              WHERE ca.client_id = c.client_id)
ORDER BY c.client_id;

/*
Retrieve aggregate financial position per client
*/

SELECT  c.client_id "Title", c.name "Name", c.surname "Surname", "Loan Balance", "Transactional Balance", "Net Position"
FROM    client c, client_account ca, account_type at
WHERE   c.client_id = ca.client_id
AND     ca.account_type_code = at.account_type_code
AND     at.transactional = 1
AND     ca.display_balance IN(SELECT MAX(ca.display_balance)
                              FROM client_account ca
                              WHERE ca.client_id = c.client_id)
ORDER BY c.client_id;


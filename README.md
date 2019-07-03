# Discovery Assessment

# Instructions
 
 1) Build the project in terminal
	$ mvn clean install
	
 2) Run the application (The application uses a default port number of 8080 for the server)
	$ mvn spring-boot:run
	
 3) Using a testing tool like postman or any other testing tool or even a browser(preferraby chrome), execute these endpoints below to test each use case:


USE CASES:
==========

--------------------------------------------------------------------------------------------------------------

Use Case 4.2.1: Display transactional accounts with balances

	Endpoint: 		http://localhost:8080/banking-api/transactional-accounts/1
	Request Method: GET

--------------------------------------------------------------------------------------------------------------

Use Case 4.2.2: Display currency accounts with converted Rand values

	Endpoint: 		http://localhost:8080/banking-api/currency-accounts/1
	Request Method: GET

--------------------------------------------------------------------------------------------------------------

Use Case 4.2.3: Withdraw cash

	Endpoint: 		http://localhost:8080/banking-api/withdraw/250/13/1018033450/1
	Request Method: PUT

--------------------------------------------------------------------------------------------------------------

Use Case 4.2.4	Reporting – Find the transactional account per client with the highest balance

	Endpoint: 		http://localhost:8080/banking-api/highest-transactional-balance/1
	Request Method: GET
	
	Endpoint: 		http://localhost:8080/banking-api/clients-with-highest-transactional-balance
	Request Method: GET

--------------------------------------------------------------------------------------------------------------

Use Case 4.2.5: Reporting – Calculate aggregate financial position per client

	Endpoint: 		http://localhost:8080/banking-api/aggregate-financial-position/1
	Request Method: GET

--------------------------------------------------------------------------------------------------------------
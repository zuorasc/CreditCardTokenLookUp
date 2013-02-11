#CreditCardTokenLookUp
A simple program to do a look up between Zuora Accounts and Credit Cards
***
###TODO 
* Add more logic in building of credit card map to consider case where token to card number is not 1:1
* tests?

###Process
1. read in Data files
2. build lookup maps
3. do lookup and build output

###Questions
1. What about duplicate tokens? 
A: Use AccountId/Num as key should all be unique. 
Also I think if there are more then one card tied to an account we should use the lastest one whcih will be futher down in the file and should get picked up in the processing. This will require more logic in the building for the credit card map.



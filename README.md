# Tacobell_Customer_Personalisation_POC

A proof of concept to actually apply behavioral targeting to customer personalization for the clients Taco bell. The purpose of the POC was to automate the process of adding/removing online customers from target segments on the basis of business rules (like Customer's age, current location) and the customer's online browsing behavior (like product pages visited, category pages visited) and if all the combinations of business user configured rules evaluates to true then the customer is added/removed from promotional discounts segments. 

Platforms used - Hybris 6.3 features like SmartEdit, rulesengine (made on top of drools engine), cronjobs and personalization services.
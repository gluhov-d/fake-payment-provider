In the scope of this task, we need to implement a FAKE PAYMENT PROVIDER.
This service will provide us with the ability to create real transactions (top up, withdrawal), send webhooks and check the merchant balance.

Technology stack:
Java 21
Spring WebFlux
Spring Reactive Data
Postgres
Flyway
TestContainers
Junit 5
Mockito
Docker

Transaction (Top Up)
Transaction method allows creating top up (deposit) transaction and charge customer payment card.				
Summary					
As an admin,
I want to be able to create transactions (top-up) using a payment card through a fake provider, So that test transaction functionality.

Create Transaction (top up)

The method is allowed to create top up (deposit) transaction


Name
Type
In
Description
Authorization
string
header
Required - Unique parameter, which must be passed to all requests for identification

You must include the authorization parameter in your header when posting your request. The authorization header must contain the word "Basic" followed by a space and your credentials encoded in Base64 format. To encode your credentials, you must type your shop ID followed by a colon and your secret key, like so: secretKey .
merchantId :
Merchant (PROSELYTE) settings :
merchantId=PROSELYTE secretKey=b2eeea3e27834b7499dd7e01143a23dd
Mock request
{
"payment_method":"CARD",
"amount":"1000",
"currency":"BRL",
"card_data":{
"card_number":"4102778822334893",
"exp_date":"11/23",
"cvv":"566"
},
"language":"en",
"notification_url":"https://proselyte.net/webhook/transaction"
"customer":{
"first_name":"John",
"last_name":"Doe",
"country":"BR"
} }

status code=200
Mock response
{
"transaction_id": "eb488939-7d59-4a40-831a-169c686e5747",
"status": "IN_PROGRESS",
"message": "OK"
}

status code=400
{
"status": "FAILED",
"message": "PAYMENT_METHOD_NOT_ALLOWED"
}



Get Transaction list
To get transaction list . It allows to get transaction list for current day (by default,without query parameters) or get transaction list for period (using start date and end date as query parameters)
GET api/v1/payments/transaction/list
Headers
Name
Type
In
Description
Authorization
string
header
Required - Unique parameter, which must be passed to all requests for identification

Request example:
GET api/v1/payments/transaction/list?start_date=UNIX_TIMESTAMP&end_date=UNIX_TIMESTAMP
{
"transaction_list":[
{
"transaction_id": "eb488939-7d59-4a40-831a-169c686e5747",
"payment_method":"CARD",
"amount":"1000",
"currency":"BRL",
"created_at":"2023-02-16T09:12:34.413",
"updated_at":"2023-02-16T09:12:34.413",
"notification_url":"https://proselyte.net/webhook/transaction"
"card_data":{
"card_number":"4102778822334893"
},
"language":"en",
"customer":{
"first_name":"John",
"last_name":"Doe",
"country":"BR"
},
"status":"SUCCESS",
"message":"OK"
}
]
}



Get Transaction details
To get transaction details by TransactionId
GET api/v1/payments/transaction/{TransactionId}/details
Headers
Name
Type
In
Description
Authorization
string
header
Required - Unique parameter, which must be passed to all requests for identification

Example
Request example
GET api/v1/payments/eb488939-7d59-4a40-831a-169c686e5747/11560034/details
Response  example
{
"transaction_id": "eb488939-7d59-4a40-831a-169c686e5747",
"payment_method":"CARD",
"amount":"1000",
"currency":"BRL",
"created_at":"2023-02-16T09:12:34.413",
"updated_at":"2023-02-16T09:12:34.413",
"card_data":{
"card_number":"4102778822334893"
},
"language":"en",
"notification_url":"https://proselyte.net/webhook/transaction"
"customer":{
"first_name":"John",
"last_name":"Doe",
"country":"BR"
},
"status":"APPROVED",
"message":"OK"
}

Withdrawal (PayOut)
Transaction method allows to create payout (withdrawal) transaction and send money to customer payment card.
Create PayOut
POST api/v1/payments/payout/
You must include the authorization parameter in your header when posting your request. The authorization header must contain the word "Basic" followed by a space and your credentials encoded in Base64 format. To encode your credentials you must type your merchant ID followed by a colon and your secret key like so: secretKey .
merchantId :
merchantId=PROSELYTE secretKey=b2eeea3e27834b7499dd7e01143a23dd
Mock request


POST api/v1/payments/payout/
{
"payment_method":"CARD",
"amount":"1000",
"currency":"BRL",
"card_data":{
"card_number":"4102440066774893"
},
"language":"en",
"notification_url":"https://proselyte.net/webhook/payout",
"customer":{
"first_name":"John",
"last_name":"Doe",
"country":"BR"
}
}



Mock response
status code=200
{
"transaction_id": "f726204d-d196-41c8-bb39-1d0d1eb1aa5f",
"status": "IN_PROGRESS",
"message": "Payout in progress"
}

status code=400
{
"error_code": "FAILED",
"message": "PAYOUT_MIN_AMOUNT"   
}



Get Payout details
To get payout details by PayoutId
GET api/v1/payments/payout/eb488939-7d59-4a40-831a-169c686e5747/details
Mock request
{
"transaction_id": "eb488939-7d59-4a40-831a-169c686e5747",
"payment_method":"CARD",
"amount":"1000",
"currency":"BRL",
"created_at": "2023-02-16T09:12:34.413",
"updated_at": "2023-02-16T09:12:34.413",
"card_data":{
"card_number":"4102***4893"
},
"language":"en",
"notification_url":"https://proselyte.net/webhook/payout",
"customer":{
"first_name":"John",
"last_name":"Doe",
"country":"BR"
}
}



Get Payout list
To get payout list. It allows getting a payout list for the current day (by default, without query parameters) or getting a payout list for a period (using start date and end date as query parameters)
GET api/v1/payments/payout/list
Example
Request body example
GET api/v1/payments/payout/list?start_date=UNIX_TIMESTAMP&end_date=UNIX_TIMESTAMP
{
"payout_list":[
{
"transaction_id": "eb488939-7d59-4a40-831a-169c686e5747",
"payment_method":"CARD",
"amount":"1000",
"currency":"BRL",
"created_at":"2023-02-16T09:12:34.413",
"updated_at":"2023-02-16T09:12:34.413",
"card_data":{
"card_number":"4102***4893"
},
"language":"en",
"notification_url":"https://proselyte.net/webhook/payout",
"customer":{
"first_name":"John",
"last_name":"Doe",
"country":"BR"
},
"status":"SUCCESS",
"message":"OK"
}
]
}



Webhooks allow getting notifications about the current status for payment.


Notifications will be sent when a transaction is created and every time it changes status until reaching a final status.


Transaction statuses when notifications will be sent:
IN_PROGRESS
SUCCESS
FAILED


Payout statuses when notifications will be sent:
IN_PROGRESS
SUCCESS
FAILED


Transaction example
Endpoint:
https://HOST/webhook/transaction
{
"transaction_id": "eb488939-7d59-4a40-831a-169c686e5747",
"payment_method":"CARD",
"amount":"1000",
"currency":"USD",
"type":"transaction",
"created_at": "2023-02-16T09:12:34.413",
"updated_at": "2023-02-16T09:12:34.413",
"card_data":{
"card_number":"4102***4893"
},
"language":"en",
"customer":{
"first_name":"John",
"last_name":"Doe"
},
"status": "SUCCESS"
"message": "OK"
}

PayOut example


Endpoint:
https://HOST/webhook/payout
{
"transaction_id": "eb488939-7d59-4a40-831a-169c686e5747",
"payment_method":"CARD",
"amount":"1000",
"currency":"USD",
"created_at": "2023-02-16T09:12:34.413",
"updated_at": "2023-02-16T09:12:34.413",
"type":"payout",
"card_data":{
"card_number":"4102***4893"
},
"language":"en",
"customer":{
"first_name":"John",
"last_name":"Doe"
},
"status": "SUCCESS",
"message": "OK"
}



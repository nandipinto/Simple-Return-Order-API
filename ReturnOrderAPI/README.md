# Return Order API
A Simple API to manage return orders.

## Importing and Running the Application
- Clone and import the project into your favorite IDE as Maven project
- Look for ReturnOrderApplication.java and Run as Spring Boot Application. Alternatively if you prefer to use Maven CLI, open a Command Prompt/Terminal window and navigate to the project directory and execute the following command:
    `mvn spring-boot:run`
- Wait for the application to start and ready accepting requests on the following address `http://localhost:8080/`
- Upon starting up the application, predefined Order data will be populated into H2 DB from an CSV file `src/resources/data/orders.csv` 
  - H2 Console can be accessed from the following URL: `http://localhost:8080/h2-console`. No password is required to open the H2 Console
  - The following DB tables should be created automatically: `ORDERS, RETURN_ORDERS, RETURN_ORDER_ITEMS, RETURN_ORDER_TOKENS` respectively. The `ORDERS` table should already been populated with data from the CSV file.
  
## Testing the API
### 1. Pending Return Order API (Request for Token)
This API is for requesting token to be used for returning orders. To allow the application to generate a token, the request should include `orderId` and `email` that already populated in the `ORDERS` table, otherwise no token will be generated.

- End point: `http://localhost:8080/pending/returns`
- Method: `POST`
- Consumes: `application/json`, Produces: `application/json`
- Sample of valid request:
```json
  {
  "orderId": "RK-238",
  "email": "carly@example.com"
  }
```
- Sample response if valid request was given:
```json
{
    "successful": true,
    "message": null,
    "token": "4f545f06-78b9-4059-bfb3-a903b2d767fe"
}
```
- Sample of invalid request (invalid value of `orderId` and/or `email`):
```json
{
    "orderId": "RK-238",
    "email": "1carly@example.com"
}
```

- Sample response if invalid request was given:
```json
{
    "successful": false,
    "message": "NO_MATCHING_RECORD_FOUND",
    "token": null
}
```

### 2. Create Return Orders
This API is to create return orders, and it requires a token to be given. Use the token generated from Step #1 above to make the request.
If request processed successfully, the API will respond with Return Order details.

- End point: `http://localhost:8080/returns`
- Method: `POST`
- Consumes: `application/json`, Produces: `application/json`
- Sample request body:
```json
{
    "token": "4f545f06-78b9-4059-bfb3-a903b2d767fe",
    "items": [
        {
            "orderId": "RK-238",
            "sku": "MU-5091",
            "quantity": 2,
            "status": null
        },
        {
            "orderId": "RK-238",
            "sku": "MU-4129",
            "quantity": 1,
            "status": null
        }
    ]
}
```
- Sample response:
```json
{
    "returnOrderId": 11,
    "status": "AWAITING_APPROVAL",
    "detail": {
        "refundAmount": 53.35,
        "items": [
            {
                "orderId": "RK-238",
                "itemId": 12,
                "sku": "MU-5091",
                "quantity": 2,
                "price": 15.25,
                "status": null
            },
            {
                "orderId": "RK-238",
                "itemId": 13,
                "sku": "MU-4129",
                "quantity": 1,
                "price": 22.85,
                "status": null
            }
        ]
    },
    "successful": true,
    "message": "REQUEST_AWAITING_APPROVAL"
}
```

### 3. Retrieving Return Order Details
The API is for retrieving Return Order details from the database, it requires `returnOrderId` to be included in the request as `PathVariable`. 
Use the value of `returnOrderId` produced in Step #2 above.

- End point: `http://localhost:8080/returns/:id`
- Method: `GET`
- Produces: `application/json`
- Sample request: `http://localhost:8080/returns/11`
- Sample response:
```json
{
    "returnOrderId": 11,
    "status": "AWAITING_APPROVAL",
    "detail": {
        "refundAmount": 30.5,
        "items": [
            {
                "orderId": "RK-238",
                "itemId": 12,
                "sku": "MU-5091",
                "quantity": 2,
                "price": 15.25,
                "status": null
            },
            {
                "orderId": "RK-238",
                "itemId": 13,
                "sku": "MU-4129",
                "quantity": 1,
                "price": 22.85,
                "status": "REJECTED"
            }
        ]
    },
    "successful": true,
    "message": null
}
```

### 4. Update Item Status
The API is to update particular item status of the returned order.
Valid status are `ACCEPTED` and `REJECTED`.
- End point: `http://localhost:8080/returns/:id/items/:itemId/qc/:status` 
- Method: `PUT`
- Parameter description:
    - `id` is the ID of Return Order produced in Step #2
    - `itemId` is the SKU of the item
    - `status` is the status to be applied to the item.
- Sample request: `http://localhost:8080/returns/11/items/MU-4129/qc/REJECTED`
- Sample response: 
```json
{
    "successful": true,
    "message": null,
    "returnOrderId": 11,
    "item": {
        "orderId": "RK-238",
        "itemId": 13,
        "sku": "MU-4129",
        "quantity": 1,
        "price": 22.85,
        "status": "REJECTED"
    }
}
```
_To make sure that items status are updated successfully, invoke the GET Return Order API (Step #3) and verify the result._

**When all Return Order items have been QC-ed (having status `COMPLETED` or `REJECTED`) the Return Order status will become `COMPLETE`.** 


## Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.3/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.7.3/maven-plugin/reference/html/#build-image)
* [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.7.3/reference/htmlsingle/#using.devtools)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.7.3/reference/htmlsingle/#data.sql.jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.7.3/reference/htmlsingle/#web)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.7.3/reference/htmlsingle/#appendix.configuration-metadata.annotation-processor)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)


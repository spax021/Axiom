#AXIOMQ ASSIGNMENT

# Test Strategy and Documentation

## Introduction

This document outlines the test strategy, challenges faced, and suggestions related to the implementation and execution of automated tests for the `AXIOMZADATAK` project. The tests were developed using Java, Rest Assured and TestNG tools to cover both positive and negative scenarios for User and Order functionalities.

## Test Strategy

### Objectives

The main objectives of the test strategy are:
- To ensure that all critical functionalities of the User and Order services are thoroughly tested.
- To validate the behavior of the services under both valid and invalid conditions.
- To provide a high level of confidence that the services work as expected before and after deployment.

### Scope

The scope of the tests includes:
- **User Service**: Testing user creation, retrieval, update and handling of invalid inputs and unauthorized access.
- **Order Service**: Testing order creation, retrieval, update and handling of invalid inputs and unauthorized access.

### Test Types

The following test types were implemented:

1. **Positive Tests**: Verify that the services function correctly when provided with valid inputs.
2. **Negative Tests**: Verify that the services handle invalid inputs.
3. **Security Tests**: Verify that the services is requiring proper authorisation.
3. **Stress Tests**: Verify that the services is handling to many requests in timely maner

### Test Cases

##### User Tests

- **accessUserByIdSuccess**: Verify that existing can be retrieved with correct authentication and that the response contains the correct details.
- **updateUserEmail**: Verify that the email of a existing user can be successfully updated.
- **updateUserStatus**: Verify that the status of a existing user can be successfully updated to "inactive".
- **accessUserByIdZero**: Verify that attempting to retrieve a user with id 0 returns a "User Not Found" error.
- **accessUserByIdNegative**: Verify that attempting to retrieve a user with a negative id returns a "User Not Found" error.
- **accessUserByIdNotFound**: Verify that attempting to retrieve a user with a non-existent id (e.g., 1) returns a "User Not Found" error.
- **accessByIdUnauthorised**: Verify that attempting to retrieve a user with the wrong password returns an "Authentication issue" error.
- **accessUserWithoutAuthentication**: Verify that attempting to retrieve user details without authentication returns an "Authentication issue" error.
- **createUserSuccess**: Verify that a user can be created with valid data and that the response contains the correct details.
- **stressTestUserCreation**: Perform a stress test by creating 1000 users and verify that all creations are successful and performance is within acceptable limits.
- **createUserUnauthorised**: Verify that attempting to create a user with the wrong password returns an "Authentication issue" error.
- **createUserWithInternationalCharacters**: Verify that a user can be created with international characters in the name.
- **createUserWithInvalidEmail**: Verify that attempting to create a user with an invalid email format returns an "Invalid request body: email" error.
- **createUserWithoutName**: Verify that attempting to create a user without a name returns an "Invalid request body: name" error.
- **invalidEndpointTest**: Verify that attempting to create a user using an invalid endpoint returns an "Endpoint not found" error.
- **malformedJsonRequest**: Verify that attempting to create a user with a malformed JSON request returns a "Malformed JSON" or "Bad Request" error.
- **getUserOrdersByUserId**: Verify that the orders of a existing user can be successfully retrieved and contain the correct details.
- **getNonExistingUserOrdersByUserId**: Verify that attempting to retrieve orders for a non-existent user returns a "User Not Found" error.


##### Order Tests

- **retriveOrderByID**: Verify that an existing order can be retrieved with correct authentication and that the response contains the correct details.
- **retriveOrderByZero**: Verify that attempting to retrieve an order with ID 0 returns an "Order Not Found" error.
- **retriveOrderByIdNegative**: Verify that attempting to retrieve an order with a negative ID returns an "Order Not Found" error.
- **retriveOrderByIDNonExistingId**: Verify that attempting to retrieve an order with a non-existent ID (e.g., 123) returns an "Order Not Found" error.
- **retriveOrderByIdUnauthorised**: Verify that attempting to retrieve an order with the wrong password returns an "Authentication issue" error.
- **retriveOrderWithoutAuthentication**: Verify that attempting to retrieve order details without authentication returns an "Authentication issue" error.
- **placeOrderSuccess**: Verify that an order can be placed with valid data and that the response contains the correct details.
- **updateOrderStatus**: Verify that an order status can be updated to next stage >> shipped.
- **stressTestPlacingOrders**: Perform a stress test by placing 1000 orders and verify that all creations are successful and performance is within acceptable limits.
- **placeOrderWrongPassword**: Verify that attempting to place an order with the wrong password returns an "Authentication issue" error.
- **placeOrderNoAuthentification**: Verify that attempting to place an order without authentication returns an "Authentication issue" error.
- **placeOrderWithWrongStatus**: Verify that attempting to place an order with a status not defined by the system returns an "Unknown status of order" error.
- **placeOrderWithStatusShipped**: Verify that attempting to place an order with a status that is not allowed (e.g., "shipped") returns an "Invalid status of order" error.
- **placeOrderWithTotalAmountZero**: Verify that attempting to place an order with a total amount of 0 returns an "Invalid total amount value" error.
- **placeOrderWithTotalAmountNegative**: Verify that attempting to place an order with a negative total amount returns an "Invalid total amount value" error.
- **placeOrderWithNonExistingUserId**: Verify that attempting to place an order with a non-existent user ID returns a "User id does not exist" error.
- **placeOrderWithMissingStatus**: Verify that attempting to place an order without a status returns an "Invalid status of order" error.
- **placeOrderMalformedJson**: Verify that attempting to place an order with a malformed JSON request returns a "Missing parameter: totalAmount" error.
- **placeOrderWithInvalidDataType**: Verify that attempting to place an order with an invalid data type (e.g., userId as string) returns an "Invalid data type provided" error.


## Challenges Faced

### 1. Handling Different Authentication Scenarios

One of the challenges faced was ensuring that the tests accurately simulated different authentication scenarios, including valid, invalid, and unauthorized access. This required careful setup of different client configurations and thorough verification of the responses.

### 2. JSON Parsing and Data Validation

Ensuring that the JSON responses were correctly parsed and validated was another challenge. This included handling different data types and ensuring that the correct fields were being checked in each test.

### 3. Error Handling and Logging

Implementing robust error handling and logging was crucial for diagnosing issues during test execution. Ensuring that all errors were appropriately caught and logged helped in identifying and resolving issues efficiently.

## Suggestions

### 1. Continuous Integration (CI)

Integrate the tests into a CI pipeline to ensure that they are run automatically with every code change. This will help in early detection of issues and ensure that the codebase remains stable.

### 2. Test Coverage Analysis

Implement test coverage analysis to ensure that all critical paths in the code are tested. This will help in identifying any gaps in the test coverage and provide insights into areas that need additional testing.

### 3. Detailed Documentation

Maintain detailed swagger or other documentation of the API endpoints and the expected responses. This will help in writing more effective tests and understanding the expected behavior of the services.

### 4. Parameterized Tests

Consider implementing parameterized tests to reduce redundancy and improve the efficiency of test execution. This will allow running the same test logic with different inputs and expected outputs.
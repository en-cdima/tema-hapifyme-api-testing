# HapifyMe API Testing Framework

## Technologies
- Java
- Maven
- REST Assured
- TestNG
- Awaitility
- Jackson
- Jenkins

## Local Run

Before running tests, set the password environment variable:

```bash
export TEST_PASSWORD="your_password"
mvn clean test

``` PowerShell
$env:TEST_PASSWORD="your_password"
mvn clean test

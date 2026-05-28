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
```

## Jenkins Pipeline Evidence

The project includes a functional Jenkins pipeline using the provided `Jenkinsfile`.

The pipeline performs the following actions:

* Clones the GitHub repository
* Executes Maven API tests
* Runs the complete TestNG test suite
* Publishes Surefire test results

Pipeline execution status:

![Jenkins Pipeline Success](docs/Jekins%20Successful%20Output.jpg)

The pipeline completed successfully with all tests passing.

## Bug / Interesting Finding

A notable API behavior was identified during the implementation of the negative validation scenario.

After deleting a user profile using:

```http
DELETE /user/delete_profile.php
```

the endpoint:

```http
GET /user/get_profile.php
```

does not return HTTP `404 Not Found` or `401 Unauthorized` as expected in a typical REST API.

Instead, the API returns:

```http
HTTP 200 OK
```

with the following response body:

```json
{
  "status": "error",
  "message": "User not found."
}
```

The automated test suite was adapted to validate the actual behavior of the HapifyMe API implementation.


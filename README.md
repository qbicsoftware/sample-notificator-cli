# Sample Notificator Service
This CLI is intended to search the database for projects (samples of a project)
that have received a new status. This is only done for the current day the tool is executed.

People who are subscribed are notified about their projects' changes
will be notified via email. This service creates the messages and sends them.

[![Build Maven Package](https://github.com/qbicsoftware/sample-notificator-cli/actions/workflows/build_package.yml/badge.svg)](https://github.com/qbicsoftware/sample-notificator-cli/actions/workflows/build_package.yml)
[![Run Maven Tests](https://github.com/qbicsoftware/sample-notificator-cli/actions/workflows/run_tests.yml/badge.svg)](https://github.com/qbicsoftware/sample-notificator-cli/actions/workflows/run_tests.yml)
[![CodeQL](https://github.com/qbicsoftware/sample-notificator-cli/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/qbicsoftware/sample-notificator-cli/actions/workflows/codeql-analysis.yml)
[![release](https://img.shields.io/github/v/release/qbicsoftware/sample-notificator-cli?include_prereleases)](https://github.com/qbicsoftware/sample-notificator-cli/releases)

[![license](https://img.shields.io/github/license/qbicsoftware/sample-notificator-cli)](https://github.com/qbicsoftware/sample-notificator-cli/blob/main/LICENSE)
![language](https://img.shields.io/badge/language-java-blue.svg)
![language](https://img.shields.io/badge/language-groovy-blue.svg)


## How to run

Build the tool with
```
mvn clean package
```

You can find the executable jar-with-dependencies in the target folder.

Note:
An instance of the [subscription service](https://github.com/qbicsoftware/subscription-service) is needed to run this application.
Also, java 8 is required for packaging and running the software.

## How to use
Execute the jar

```
java -jar <path-to-jar>/target/sample-notificator-app-<version>-jar-with-dependencies.jar -c <path-to-config> -d <ISO8601-formatted-date>
```

To get help use following command:

```
java -jar sample-notificator-app/target/sample-notificator-app-<version>-jar-with-dependencies.jar -h
Usage: SampleNotificator [-h] -c=<pathToConfig> -d=<date>
A service to send notifications to subscribers to inform them about changes
within their projects
  -c, --config=<pathToConfig>
                      Path to a config file
  -d, --date=<date>   Date of the day, for which status update notifications
                        should be send. Required format: yyyy-mm-dd
  -h, --help          display a help message

```

## Configurations

To run the tool you need to provide the credentials to access and read
data from the database. Therefore, you need to set up a properties file
which should contain the following content:

- **Host**: provide the host name to access the DB
- **User credentials**: provide user and password 
- **DB**: Specify which database you want to access 
- **Port**: Define the port through which you access the DB

```
mysql.host = 123.456.789
mysql.pass = myPassWord
mysql.user = myUserName
mysql.db = myDatabase
mysql.port = 8888
```

For the subscription service the following properties are required:

```
services.subscriptions.password = ...
services.subscriptions.tokengeneration.endpoint = /subscriptions/tokens
services.subscriptions.url = http://localhost:8080
services.subscriptions.user = ChuckNorris
```

In addition a base URL is needed where users can unsubscribe with the generated token.
```
portal.unsubscription.baseurl = www.my-awesome-website/unsubscribe
```

NOTE: Please don't use quotation marks `"` for the values!

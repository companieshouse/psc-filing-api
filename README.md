# psc-filing-api

The PSC Filing Service API is responsible for handling and processing PSC filings submitted by users.

API users (including [psc-filing-web](https://github.com/companieshouse/psc-filing-web)) interact with psc-filing-api by sending HTTP requests containing JSON to service endpoints. Service endpoints available in psc-filing-api as well as their expected request and response models are outlined in the [Swagger specification file](spec/swagger.json). 

The service integrates with a number of internal and external systems. This includes [Transactions API](https://github.com/companieshouse/transactions.api.ch.gov.uk) and [api.ch.gov.uk](https://github.com/companieshouse/api.ch.gov.uk)

Requirements
------------

To build psc-filing-api, you will need:
* [Git](https://git-scm.com/downloads)
* [Java 11](https://docs.aws.amazon.com/corretto/latest/corretto-11-ug/downloads-list.html)
* [Maven](https://maven.apache.org/download.cgi)
* [MongoDB](https://www.mongodb.com/)
* [Apache Kafka](https://kafka.apache.org/)
* Internal Companies House core services

You will also need a REST client (e.g. Postman or cURL) if you want to interact with any psc-filing-api service endpoints.

Certain endpoints (e.g. POST /psc-filing-api/events/submit-files-to-fes) will not work correctly unless the relevant environment variables are configured. 

## Building and Running Locally

1. From the command line, in the same folder as the Makefile run `make clean build`
1. Configure project environment variables where necessary (see below).
1. Ensure dependent Companies House services are running within the Companies House developer environment
1. Start the service in the CHS developer environment
1. Send a GET request using your REST client to /psc-filing-api/healthcheck. The response should be 200 OK with status=UP.
1. A database named `transaction_pscs` and the following collections are required:

Collection name| Description                                                         | Data                                                                                                                                           
--------------------|---------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
psc_submissions| the filing resource including id, dates, PSC id and etag, and links | this and the database will be created by the service upon submitting filing details|

Configuration
-------------
System properties for psc-filing-api are defined in `application.properties`. These are normally configured per environment.

Variable| Description                                                                                        | Example                                                                        | Mandatory |
--------------------|----------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------|-----------|
MONGO_PSC_API_DB_NAME| The name of the collection responsible for storing PSC filings                                     | collection_name                                                                | always    
MONGODB_URL| The URL of the MongoDB instance where documents and application data should be stored              | mongodb://mongohost:27017                                                      | always    
FEATURE_FLAG_TRANSACTIONS_CLOSABLE| Flag used to stop transactions being closed and sent to CHIPS, by causing validatio to always fail | true                                                                           | always    
PSC07_DESCRIPTION| Used in Confirmation, Acceptance and Rejection emails to describe filing                           | (PSC07) Notice of ceasing to be a Person of Significant Control for {0} on {1} | always    |
PATCH_MAX_RETRIES| required by api-helper-java library                                                                | 3                                                                              | always|
REF_PATTERN| The pattern that randomly generated submission numbers will follow                                 | ############                                                                   | always    
REF_SYMBOL_SET| Set of characters permitted in randomly generated submission numbers                               | abc123                                                                         | always    
MANAGEMENT_ENDPOINTS_ENABLED_BY_DEFAULT|                                                                                                    | false                                                                          | always    
MANAGEMENT_ENDPOINT_HEALTH_ENABLED|                                                                                                    | true                                                                           | always    
MANAGEMENT_ENDPOINTS_WEB_PATH_MAPPING_HEALTH|                                                                                                    | healthcheck                                                                    | always    
MANAGEMENT_ENDPOINTS_WEB_BASE_PATH|                                                                                                    | /psc-filing-api                                                                | always    
LOGGING_LEVEL| Log message granularity                                                                            | INFO                                                                           | always    
REQUEST_LOGGING_LEVEL| Request log message granularity                                                                    | WARN                                                                           | always    
AWS_REGION| The AWS region that psc-filing-api will use when connecting to AWS services                        | aws-region                                                                     | always    
AWS_ACCESS_KEY_ID| The access key ID of the AWS account that psc-filing-api will use when connecting to AWS           | MYAWSACCESSKEYID                                                               | always    
AWS_SECRET_ACCESS_KEY| The secret access key of the AWS account that psc-filing-api will use when connecting to AWS       | MYAWSSECRETACCESSKEY                                                           | always    

## Building the docker image 

    mvn -s settings.xml compile jib:dockerBuild -Dimage=169942020521.dkr.ecr.eu-west-1.amazonaws.com/local/psc-filing-api

## Running Locally using Docker

1. Clone [Docker CHS Development](https://github.com/companieshouse/docker-chs-development) and follow the steps in the README.

1. Enable the `psc-filing` module

1. Run `tilt up` and wait for all services to start

### To make local changes

Development mode is available for this service in [Docker CHS Development](https://github.com/companieshouse/docker-chs-development).

    ./bin/chs-dev development enable psc-filing-api

This will clone the psc-filing-api into the repositories folder inside of docker-chs-dev folder. Any changes to the code, or resources will automatically trigger a rebuild and reluanch.
# psc-filing-api

The PSC Filing Service API is responsible for handling and processing PSC filings submitted by users.

API users (including [psc-filing-web](https://github.com/companieshouse/psc-filing-web)) interact with psc-filing-api by sending HTTP requests containing JSON to service endpoints. Service endpoints available in psc-filing-api as well as their expected request and response models are outlined in the [Swagger specification file](spec/swagger.json). 

The service integrates with a number of internal and external systems. This includes [Transactions API](https://github.com/companieshouse/transactions.api.ch.gov.uk) and [api.ch.gov.uk](https://github.com/companieshouse/api.ch.gov.uk)

Requirements
------------

To build psc-filing-api, you will need:
* [Git](https://git-scm.com/downloads)
* [Java 21](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/downloads-list.html)
* [Maven](https://maven.apache.org/download.cgi)
* [MongoDB](https://www.mongodb.com/)
* Internal Companies House core services

You will also need a REST client (e.g. Postman or cURL) if you want to interact with any psc-filing-api service endpoints.

## Building and Running Locally

1. From the command line, in the same folder as the Makefile run `make clean build`
1. Checkout git submodule `api-enumerations`
   1. `git submodule init` # initialize local configuration file
   2. `git submodule update` # fetch all the data
1. Configure project environment variables where necessary (see below).
1. Ensure dependent Companies House services are running within the Companies House developer environment
1. Start the service in the CHS developer environment
1. Send a GET request using your REST client to persons-with-significant-control/healthcheck. The response should be 200 OK with status=UP.
1. A database named `transaction_pscs` and the following collections are required:

Collection name| Description                                                         | Data                                                                                                                                           
--------------------|---------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
psc_submissions| the filing resource including id, dates, PSC id and etag, and links | this and the database will be created by the service upon submitting filing details|

Configuration
-------------
System properties for psc-filing-api are defined in `application.properties`. These are normally configured per environment.

Variable| Description                                                                                        | Example                                                                        |
--------------------|----------------------------------------------------------------------------------------------------|--------------------------------------------------------------------------------|
MONGO_PSC_API_DB_NAME| The name of the collection responsible for storing PSC filings                                     | transactions_pscs                                                              |
MONGODB_URL| The URL of the MongoDB instance where documents and application data should be stored              | mongodb://mongohost:27017/transactions_pscs                                    |
FEATURE_FLAG_TRANSACTIONS_CLOSABLE| Flag used to stop transactions being closed and sent to CHIPS, by causing validatio to always fail | true                                                                           |
PSC07_DESCRIPTION| Used in Confirmation, Acceptance and Rejection emails to describe filing                           | (PSC07) Notice of ceasing to be a Person of Significant Control for {0} on {1} |
PATCH_MAX_RETRIES| required by api-helper-java library                                                                | 3                                                                              |
REF_PATTERN| The pattern that randomly generated submission numbers will follow                                 | ############                                                                   |
REF_SYMBOL_SET| Set of characters permitted in randomly generated submission numbers                               | abc123                                                                         |
MANAGEMENT_ENDPOINTS_ENABLED_BY_DEFAULT|                                                                                                    | false                                                                          |
MANAGEMENT_ENDPOINT_HEALTH_ENABLED|                                                                                                    | true                                                                           |
MANAGEMENT_ENDPOINTS_WEB_PATH_MAPPING_HEALTH|                                                                                                    | healthcheck                                                                    |
MANAGEMENT_ENDPOINTS_WEB_BASE_PATH|                                                                                                    | /persons-with-significant-control                                              |
LOGGING_LEVEL| Log message granularity                                                                            | INFO                                                                           |
WEB_LOGGING_LEVEL| Lists endpoints on Application start up if set to TRACE                                            | TRACE                                                                          |
REQUEST_LOGGING_LEVEL| Request log message granularity                                                                    | WARN                                                                           |
AWS_REGION| The AWS region that psc-filing-api will use when connecting to AWS services                        | aws-region                                                                     |
AWS_ACCESS_KEY_ID| The access key ID of the AWS account that psc-filing-api will use when connecting to AWS           | MYAWSACCESSKEYID                                                               |
AWS_SECRET_ACCESS_KEY| The secret access key of the AWS account that psc-filing-api will use when connecting to AWS       | MYAWSSECRETACCESSKEY                                                           |

## Building the docker image 

    mvn -s settings.xml compile jib:dockerBuild -Dimage=169942020521.dkr.ecr.eu-west-1.amazonaws.com/local/psc-filing-api

## Running Locally using Docker

1. Clone [Docker CHS Development](https://github.com/companieshouse/docker-chs-development) and follow the steps in the README.

1. Enable the `psc-filing` module

1. Run `tilt up` and wait for all services to start

### To make local changes

Development mode is available for this service in [Docker CHS Development](https://github.com/companieshouse/docker-chs-development).

    ./bin/chs-dev development enable psc-filing-api

This will clone the psc-filing-api into the repositories folder inside of docker-chs-dev folder. Any changes to the code, or resources will automatically trigger a rebuild and relaunch.

## Validation of filing data
Validation is carried out using:
- Interceptors provided by [api-security-java](https://github.com/companieshouse/api-security-java) library
- Validation messages in [api-enumerations](https://github.com/companieshouse/api-enumerations/blob/master/psc_filing.yml) psc-filing file
- Company Interceptor checking company type and status
- Chain of business validators of data submitted

## Terraform ECS

### What does this code do?

The code present in this repository is used to define and deploy a dockerised container in AWS ECS.
This is done by calling a [module](https://github.com/companieshouse/terraform-modules/tree/main/aws/ecs) from terraform-modules. Application specific attributes are injected and the service is then deployed using Terraform via the CICD platform 'Concourse'.


Application specific attributes | Value                                | Description
:---------|:-----------------------------------------------------------------------------|:-----------
**ECS Cluster**        |filing-maintain                                      | ECS cluster (stack) the service belongs to
**Load balancer**      |{env}-chs-apichgovuk & {env}-chs-apichgovuk-private                                             | The load balancer that sits in front of the service
**Concourse pipeline**     |[Pipeline link](https://ci-platform.companieshouse.gov.uk/teams/team-development/pipelines/psc-filing-api) <br> [Pipeline code](https://github.com/companieshouse/ci-pipelines/blob/master/pipelines/ssplatform/team-development/psc-filing-api)                                  | Concourse pipeline link in shared services


### Contributing
- Please refer to the [ECS Development and Infrastructure Documentation](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/4390649858/Copy+of+ECS+Development+and+Infrastructure+Documentation+Updated) for detailed information on the infrastructure being deployed.

### Testing
- Ensure the terraform runner local plan executes without issues. For information on terraform runners please see the [Terraform Runner Quickstart guide](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/1694236886/Terraform+Runner+Quickstart).
- If you encounter any issues or have questions, reach out to the team on the **#platform** slack channel.

### Vault Configuration Updates
- Any secrets required for this service will be stored in Vault. For any updates to the Vault configuration, please consult with the **#platform** team and submit a workflow request.

### Useful Links
- [ECS service config dev repository](https://github.com/companieshouse/ecs-service-configs-dev)
- [ECS service config production repository](https://github.com/companieshouse/ecs-service-configs-production)

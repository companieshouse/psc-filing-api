#!/bin/bash

# Start script for psc-filing-api

PORT=8080
exec java -jar -Dserver.port="${PORT}" "psc-filing-api.jar"

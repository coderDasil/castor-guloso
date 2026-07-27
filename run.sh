#!/bin/bash
# Run Castor Guloso in development mode
cd "$(dirname "$0")"
mvn spring-boot:run -pl castor-guloso-app "$@"

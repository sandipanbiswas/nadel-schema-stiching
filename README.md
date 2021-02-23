# Nadel GraphQL Gateway Demo

This is an example app to showcase a [GraphQL](graphql.org) Gateway build using [Nadel](https://github.com/graphql-java/nadel).

## Overview
It consists of the following parts:

The actual gateway in [gateway](gateway). It is Spring Boot app

Two services (called underlying services) which are exposed via the gateway:

[user service](underlying-services/users): a user service written in JS.

[issue service](underlying-services/issues): am issue service written in Java.


## Running it
The simplest way is to clone this repo and execute `./run-example.sh`. 

It starts a docker container and exposes a GraphQL api at port 8080. 

Open [http://localhost:8080/graphql](http://localhost:8080/graphql) in your browser to access GraphQL Playground for the gateway.



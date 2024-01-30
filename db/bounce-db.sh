#!/bin/bash

psql admin -U admin -f clean.pgsql
psql admin -U admin -f initialise.pgsql

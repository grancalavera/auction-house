#!/bin/bash
SCRIPT_DIR=$(dirname "$(readlink -f "$0")")

psql ah -U admin -f "${SCRIPT_DIR}/list-orgs.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/list-roles.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/list-account-status.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/list-users.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/list-auctions-and-bids.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/list-reports.pgsql"

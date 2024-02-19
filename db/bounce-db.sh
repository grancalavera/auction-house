#!/bin/bash
SCRIPT_DIR=$(dirname "$(readlink -f "$0")")
echo ""
echo "-----------------------------------------"
echo "bounce database $(date --iso-8601=seconds)"
echo "-----------------------------------------"
psql -U admin -f "${SCRIPT_DIR}/create-db.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/clean.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/initialise.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/list-orgs.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/list-roles.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/list-account-status.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/list-users.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/list-auction-status.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/list-auctions.pgsql"
psql ah -U admin -f "${SCRIPT_DIR}/list-bids.pgsql"
# psql ah -U admin -f "${SCRIPT_DIR}/initialise.pgsql" >> /auction-house/init.log 2>&1;
# log/postgresql-2024-02-09_185329.log

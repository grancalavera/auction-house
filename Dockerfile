FROM postgres:16.1-alpine
LABEL name="Auction House Database, using PostgreSQL."
WORKDIR /auction-house
VOLUME [ "/db-scripts" ]

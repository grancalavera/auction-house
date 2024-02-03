FROM postgres:16.1
LABEL name="Auction House Database, using PostgreSQL."
WORKDIR /auction-house
VOLUME [ "/db-scripts" ]

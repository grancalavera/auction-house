# auction-house

## Tools

- https://asdf-vm.com/
- https://github.com/halcyon/asdf-java
- https://github.com/rfrancis/asdf-gradle
- https://direnv.net/
- https://www.azul.com/downloads/?package=jdk#zulu
- https://gradle.org/
- https://orbstack.dev/

## Requirements

### As an admin user I can:

- [x] Create new users, with a username, password, first name, last name and organisation. The username must be an alpha-numerical string without spaces or special characters. Usernames must be unique.
- [x] Retrieve a list of all users.
- [x] Retrieve a list of organisations across all users.
- [x] Retrieve or update user details based on their id.
- [x] Block or unblock user accounts. Blocked users should not be allowed to log in or interact with the system.

### As a normal user I can:

- [x] Create an auction, with a symbol, non-negative quantity, and a minimum accepted price for the auction, which has to be a positive value. After an auction has been created it is automatically open for bidding.
- [x] Close an open auction created by myself (but not auctions created by other users):
  - [x] The bids with the best prices are selected to fulfil the auction, in descending order.
  - [x] Bids can be partially filled if total bids exceed the quantity available.
  - [x] Ties between matching bids are broken by submission time (i.e. earlier bids win).
- [x] View a list of all of my auctions, including whether it is still open or has been closed
- [x] as well as all bids on the auction so far.
- [x] For closed auctions, also see a summary of the auction result, which includes:
  - [x] the total revenue
  - [x] total sold quantity
  - [x] and winning bid(s)
  - [x] as well as the closing time of the auction
- [x] Bid on any open auctions, created by other users (users cannot bid on their own auctions).
- [x] Retrieve the details of all my winning or losing bids from any auctions I participated in.
  - [x] For winning and loosing bids is enough to query the execution results junction table
  - [x] For "pending" bids, we need to query the junction for bids not in that table.

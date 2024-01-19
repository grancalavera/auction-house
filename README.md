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

1. As an admin user I can:

   - Create new users, with a username, password, first name, last name and organisation. The username must be an alpha-numerical string without spaces or special characters. Usernames must be unique.

   - Retrieve a list of all users.

   - Retrieve a list of organisations across all users.

   - Retrieve or update user details based on their id.

   - Block or unblock user accounts. Blocked users should not be allowed to log in or interact with the system.

1. As a normal user I can:

   - Create an auction, with a symbol, non-negative quantity, and a minimum accepted price for the auction, which has to be a positive value. After an auction has been created it is automatically open for bidding.

   - Close an open auction created by myself (but not auctions created by other users). The bids with the best prices are selected to fulfil the auction, in descending order. Bids can be partially filled if total bids exceed the quantity available. Ties between matching bids are broken by submission time (i.e. earlier bids win).

   - View a list of all of my auctions, including whether it is still open or has been closed, as well as all bids on the auction so far.

   - For closed auctions, also see a summary of the auction result, which includes the total revenue, total sold quantity, and winning bid(s), as well as the closing time of the auction.

   - Bid on any open auctions, created by other users (users cannot bid on their own auctions).

   - Retrieve the details of all my winning or losing bids from any auctions I participated in.

# AuctionHouse

- how can an user bid on an auction if there's no API to list auctions that were not created by the current user?
- what's the shape of a bid?
- probably listing of auctions should take a predicate
- auctions are like the auction for promises we make at Adaptive, where people bids for other people's promises and everyone has to pay, not only the person who wins the auction; and not like real auctions where only the highest bidder pays.

## User and Account

- maybe wrap it in a namespace

```mermaid

classDiagram

%% I want the constructor of this class to be private and visible only
%% to UserBuilder, so that it is not possible to make invalid users.
%% maybe make this immutable (but why?)
class User {
  + int id
  + String username
  + String password
  + String firstName
  + String lastName
  + Organisation organisation
  + AccountStatus accountStatus
}

class AccountStatus {
  <<Enumeration>>
  Active
  Blocked
}

class Organisation {
  int id
  String name
}

%% This may have two constructors
%% empty: no values and requires full validation
%% from user: takes an user as an argument
class UserBuilder {
  - Optional~int~ id
  - Optional ~String~ username
  - Optional ~String~ password
  - Optional ~String~ firstName
  - Optional ~String~ lastName
  - Optional ~Organisation~ organisation

  %% maybe the builder shouldn't have an id
  %% or make the id required in the builder constructor?
  + UserBuilder setId(int id)
  + UserBuilder setUsername(String username)
  + UserBuilder setPassword(String password)
  + UserBuilder setFirstName(String firstName)
  + UserBuilder setLastName(String lastName)
  + UserBuilder setOrganisation(Organisation organisation)
  %% throws if user validation fails
  + User build()
}

class UserValidator {
  + validateUsername(String candidate)
  + validateUniqueUsername(String candidate)
}

Organisation --* User
AccountStatus --* User

User <.. UserBuilder: optional (overloaded constructor)
UserValidator <.. UserBuilder
```

## Auction

```mermaid
classDiagram

class Auction {
  int id
  int ownerId
  String symbol
  int quantity
  BigDecimal price
  AuctionStatus auctionStatus
}

class AuctionStatus {
  <<Enumeration>>
  Open
  Closed
}

class Bid {
  int id
  int auctionId
  BigDecimal amount
  Instant timestamp
}

%% back office stuff
class AuctionReport {
  int id
  int auctionId
  BigDecimal revenue
  int totalQuantitySold
  Iterable~Bid~ winningBids
  %% some other date type maybe
  Instant closingTime
}

note for AuctionHouse "There is always an implicit active user \nfigure out how to make the ids"
class AuctionHouse {
  %% throws if the auction validation fails
  void openAuction(Auction auction)
  %% throws if the auction is open and doesn't belong to the active user
  void closeAuction(int id)
  %% throws if auction belongs to user
  %% throws if auction doesn't exist
  %% throws if auction is closed
  void placeBid(int auctionId, BigDecimal amount)
}

```

## Admin Panel

- Probably in Java you don't pass IDs but objects, so you don't have to run expensive operations
  dereferencing data you already have. But then you have the potential issue that you might be
  trying to modify something that has changed after you got hold of the object.

```mermaid
classDiagram
note for AdminPanel "There is always an implicit active user"
class AdminPanel {
  Iterable~User~ listUsers()
  Iterable~Organisation~ listOrganisations()
  Optional~User~ findUserById(int id)
  %% how do we enforce the user has an id?
  %% the builder should resolve the id if one is not given
  %% and upsert user should create the user if it exists and update the user if it doesn't
  %% but then you have a problem, which is validating if the username is unique, which
  %% currently is done by UserBuilder but maybe should be done by AdminPanel
  void upsertUser(UserBuilder userBuilder)
  %% throws if userId doesn't exist
  void blockUserAccount(int userId)
}

```

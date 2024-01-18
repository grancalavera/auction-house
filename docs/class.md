# AuctionHouse

- how can an user bid on an auction if there's no API to list auctions that were not created by the current user?
- what's the shape of a bid?
- probably listing of auctions should take a predicate
- auctions are like the auction for promises we make at Adaptive, where people bids for other people's promises and everyone has to pay, not only the person who wins the auction; and not like real auctions where only the highest bidder pays.

## User and Account

- Implicitly a namespace

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

User <.. UserBuilder: optional
UserValidator <.. UserBuilder
```

## Bid

```mermaid
classDiagram

class Auction {
  int id
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
```

## Front Office

```mermaid
classDiagram
note for FrontOffice "There is always an implicit active user \nfigure out how to make the ids"
class FrontOffice {
  %% throws if the auction validation fails
  %% throws if the user is an admin (does it?)
  void openAuction(Auction auction)
  %% and the thing is how to construct the objects as usual
  void closeAuction(int id) // throws if the auction is open and doesn't belong to the active user
  %% throws if auction belongs to user, throws if auction doesn't exist
  void placeBid(int auctionId, BigDecimal amount)
}

```

## Back Office

```mermaid
classDiagram
note for BackOffice "There is always an implicit active user"
class BackOffice {
  Iterable~User~ listUsers()
  Iterable~Organisation~ listOrganisations()
  Optional~User~ findUserById(int id)
  %% how do we enforce the user has an id?
  %% how do we enforce the user has an id?
  void upsertUser(UserBuilder userBuilder)
  %% throws
  void blockUserAccount(int userId)
}

```

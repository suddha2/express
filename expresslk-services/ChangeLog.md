expressLK - ChangeLog
======================

2.0.0 (not yet released)
------------------------
| Issue | Change | Notes to front-end dev | Notes to QA |
| ------|--------|------------------------|-------------|
|  | Hide others' bookings from agents |  | Check the bookings list for an agent |
|  | Allow hiding buses from agents | REST available at `admin/AgentRestriction` |  |
|  | Disallow granting permissions one does not have |  | Test by editing a user in the admin |

1.0.0 (2016-04-19)
------------------------
| Issue | Change | Notes to front-end dev | Notes to QA |
| ------|--------|------------------------|-------------|
|  | Record the user against the payment or refund instead of just the division | `userId` added and `division` removed from `PaymentRefund` |  |
|  | Forbid inserting bookings via REST API |  | Test REST API |
|  | Add write permission to some classes | `writeAllowedDivisions` property for `Booking`, `BookingItem`, `Payment`, `Refund` | Test REST API |
|  | Use Quartz for scheduling |  | Verify whether tentative bookings without payments get cancelled |
|  | Use heuristics to avoid using bitwise `AND` in `allowed_divisions` field |  | Test visibility for users having different visibility settings |
|  | Handle bus mobile location via the services | REST service available at `/admin/busMobileLocation` |  |
|  | Allow marking a user as an agent | nullable field `agent` added to `User` |  |
|  | Fix accounting entries for agent bookings |  | Verify accounting entries for an agent booking |
|  | Relay `isMargin` flag to `BookingItemMarkup` | `isMargin` field added to `BookingItemMarkup` |  |
|  | Allow filtering by schedule ID | Use `ScheduleFilter` for this purpose |  |
|  | Allow filtering cities in `terminusCity` REST service by a prefix | Use parameter `q` for this purpose |  |
|  | Populate the entity with code | For entities that implements `HasNameCode`, a skeleton object with `code` property is now populated |  |
|  | Facilitate using one-time discount codes |  | Verify the application of the discount as well as the single usage of the code |
| [Bug #327](https://bitbucket.org/expresslk/expresslk-meta/issues/327/reports-do-not-support-utf-8-characters) | Reports do not support UTF-8 characters |  |  |
|  | Added `notificationMethod` and `adminContact` for buses | Use these fields to notify the bus and the owner about reservations | Test `admin` for setting these values |
|  | Allow recording payments received in foreign currencies | Use `actualAmount` and `actualCurrency` fields | Check whether the booking SMS and email have amounts in USD for PayPal payments |


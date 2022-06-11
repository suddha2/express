BBK Web - ChangeLog
======================

2.0.0
------------------------
| Issue | Change | Notes to QA |
| ------|--------|-------------|
|  |  |  |

1.0.0
------------------------
| Issue | Change | Notes to QA |
| ------|--------|-------------|
|  | PayPal as a new payment gateway | Use u/n: payments-buyer@express.lk p/w: express321 as customer credentials to test PayPal |
|  | Improvements to from/to city drop down speed | Check web and ticketbox city drop downs for speed |
|  | Send bookings to conductor on schedule stage change by SMS if the bus notification method is SMS. |  |

0.16.3
-----------------------
| Issue         | Change            | Notes to QA           |
| --------------|-------------------|-----------------------|
|  | White label solution for Surrexi.express.lk | Test overall site structure with payments |
|  | Branding related to Surrexi (SMS,Email) on bookings | Test branding on bookings |
|  | Conductor schedules are shown only after "ticketingActiveTime" |  | Test with schedule's property ticketingActiveTime |
|  | Collection added for conductor  | Test Conductor Mobile application, collection tab  |
|  | SMS sent on conductor app bookings | Test Conductor Mobile application, bookings |
|  |  |  |

0.15.0.0
- Upgrade bower packages. Added new packages to bower.json for a proper initial installation of all the libraries. Changes to admin js
- Separate module and sub module structure created for mobile application backends. Mobile app APIs to be hosted inside App module. Each sub module will map to each mobile app
- Created sub module structure to host mobile backends. Each backend version will have separate sub module
- Ticketing app backend completed

0.10.0.0
- bug #48 TicketBox: Make contact details read-only if fetching it via customer tick
- bug #93 TB: Search & Filters not working properly
- bug #98 TB - Create Bus > Null Tick Boxes
- bug #96 TB - Unable to create a bus
- JS structure changed, moved to more meaningfull assets-src folder
- Css moved to SASS. Now SASS files (.scss) compiles to css files
- Front end facelifted. Has a new design now.

0.9.0.0
- Sampath Payment gateway integration.
- Moved gateway related classes to a separate namespace.

0.8.0.0
- Change admin panel UI
- Library to manage CRUD operations

0.7.0.0 (2015-05-31)
- Bower update with angular 1.3. Updated other front end libraries to match
- Translations for rest of the view files
- Translations for City names
- Removed cumbersome slider plugin and used angular-bootstrap slider
- UI modifications on home screen for mobiles and tabs
- UI modifications in header, menus and home screen

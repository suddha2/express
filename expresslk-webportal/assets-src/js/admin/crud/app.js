/**
 * Created by udantha on 6/26/15.
 */
var expressAdminCrudApp = angular.module('expressAdminCrud', ['expressAdmin', 'ng-admin']);

var Entities = Entities ? Entities : {};

angular.module('expressAdminCrud')
    .config(['NgAdminConfigurationProvider', function (NgAdminConfigurationProvider) {

        try {
            var nga = NgAdminConfigurationProvider;

            // set the main API endpoint for this admin
            var baseUrl = '/admin-panel/cruds/';

            var admin = nga.application('Express Admin') // application main title
                .baseApiUrl(baseUrl); // main API endpoint

            nga.parseDate = function (d) {
                if (!d) { // null or undefined
                    return d;
                } else if (d instanceof Date) { // as date
                    return d.getTime();
                } else { // in millis
                    return d;
                }
            };
            nga.dateFormat = 'yyyy-MM-dd';
            nga.timeFormat = 'yyyy-MM-dd HH:mm';
			
            // define all entities at the top to allow references between them
            var accountStatus = nga.entity(Entities.Name.accountStatus),
                agent = nga.entity(Entities.Name.agent),
                allowedDivisions = nga.entity(Entities.Name.allowedDivisions),
                amenity = nga.entity(Entities.Name.amenity),
                booking = nga.entity(Entities.Name.booking),
                bookingItem = nga.entity(Entities.Name.bookingItem),
                bookingItemCharge = nga.entity(Entities.Name.bookingItemCharge),
                bookingItemDiscount = nga.entity(Entities.Name.bookingItemDiscount),
                bookingItemMarkup = nga.entity(Entities.Name.bookingItemMarkup),
                bookingItemPassenger = nga.entity(Entities.Name.bookingItemPassenger),
                bookingItemTax = nga.entity(Entities.Name.bookingItemTax),
                bookingStatus = nga.entity(Entities.Name.bookingStatus),
                bus = nga.entity(Entities.Name.bus),
                busImage = nga.entity(Entities.Name.busImage),
                busBusRoute = nga.entity(Entities.Name.busBusRoute),
                busRoute = nga.entity(Entities.Name.busRoute),
                busSchedule = nga.entity(Entities.Name.busSchedule),
                busScheduleBusStop = nga.entity(Entities.Name.busScheduleBusStop),
                busStop = nga.entity(Entities.Name.busStop),
                busType = nga.entity(Entities.Name.busType),
                cancellationCharge = nga.entity(Entities.Name.cancellationCharge),
                change = nga.entity(Entities.Name.change),
                changeType = nga.entity(Entities.Name.changeType),
                city = nga.entity(Entities.Name.city),
                client = nga.entity(Entities.Name.client),
                conductor = nga.entity(Entities.Name.conductor),
                division = nga.entity(Entities.Name.division),
                driver = nga.entity(Entities.Name.driver),
                module = nga.entity(Entities.Name.module),
                operationalSchedule = nga.entity(Entities.Name.operationalSchedule),
                passenger = nga.entity(Entities.Name.passenger),
                payment = nga.entity(Entities.Name.payment),
                permission = nga.entity(Entities.Name.permission),
                permissionGroup = nga.entity(Entities.Name.permissionGroup),
                permissionSingle = nga.entity(Entities.Name.permissionSingle),
                person = nga.entity(Entities.Name.person),
                refund = nga.entity(Entities.Name.refund),
                rule = nga.entity(Entities.Name.rule),
                seatingProfile = nga.entity(Entities.Name.seatingProfile),
                supplier = nga.entity(Entities.Name.supplier),
                supplierAccount = nga.entity(Entities.Name.supplierAccount),
                supplierContactPerson = nga.entity(Entities.Name.supplierContactPerson),
                supplierGroup = nga.entity(Entities.Name.supplierGroup),
                travelClass = nga.entity(Entities.Name.travelClass),
                user = nga.entity(Entities.Name.user),
                userGroup = nga.entity(Entities.Name.userGroup),
				district = nga.entity(Entities.Name.district)
				
                ; // the API endpoints
			
            // set the application entities
            admin
                .addEntity(accountStatus)
                .addEntity(agent)
                .addEntity(allowedDivisions)
                .addEntity(amenity)
                .addEntity(booking)
                .addEntity(bookingItem)
                .addEntity(bookingItemCharge)
                .addEntity(bookingItemDiscount)
                .addEntity(bookingItemMarkup)
                .addEntity(bookingItemPassenger)
                .addEntity(bookingItemTax)
                .addEntity(bookingStatus)
                .addEntity(bus)
                .addEntity(busImage)
                .addEntity(busBusRoute)
                .addEntity(busRoute)
                .addEntity(busSchedule)
                .addEntity(busScheduleBusStop)
                .addEntity(busStop)
                .addEntity(busType)
                .addEntity(cancellationCharge)
                .addEntity(change)
                .addEntity(changeType)
                .addEntity(city)
                .addEntity(client)
                .addEntity(conductor)
                .addEntity(division)
                .addEntity(driver)
				.addEntity(district)
                .addEntity(module)
                .addEntity(operationalSchedule)
                .addEntity(passenger)
                .addEntity(payment)
                .addEntity(permission)
                .addEntity(permissionGroup)
                .addEntity(permissionSingle)
                .addEntity(person)
                .addEntity(refund)
                .addEntity(rule)
                .addEntity(supplier)
                .addEntity(supplierAccount)
                .addEntity(supplierContactPerson)
                .addEntity(supplierGroup)
                .addEntity(seatingProfile)
                .addEntity(travelClass)
                .addEntity(user)
                .addEntity(userGroup)
				
            ;

            //configure bus
            Entities
                .amenity(nga, amenity)
                .agent(nga, agent)
                .booking(nga, booking, client, passenger, payment, refund, change, changeType, bookingItem, busStop, busSchedule, bookingStatus, cancellationCharge, rule, user,bus,bookingItemPassenger, agent, division)
                .bookingItem(nga, bookingItem, busStop, busSchedule, bookingItemMarkup, bookingItemDiscount, bookingItemTax, bookingItemCharge, rule, change, changeType, bookingItemPassenger, passenger)
                .bus(nga, bus, amenity, busRoute, busType, conductor, driver, supplier, travelClass, busImage, seatingProfile)
                .busImage(nga, busImage, bus)
                .busBusRoute(nga, busBusRoute)
                .busSchedule(nga, busSchedule, bus, busRoute, conductor, driver, busScheduleBusStop, seatingProfile,travelClass)
                .client(nga, client)
                .conductor(nga, conductor, person)
                .driver(nga, driver, person)
                .operationalSchedule(nga, operationalSchedule, busSchedule, busRoute, city, division)
                .payment(nga, payment, booking)
                .person(nga, person)
                .refund(nga, refund, booking)
                .supplier(nga, supplier, supplierGroup, supplierContactPerson, supplierAccount)
                .supplierAccount(nga, supplierAccount, supplier)
                .supplierContactPerson(nga, supplierContactPerson, person, supplier)
                .user(nga, user, accountStatus, userGroup)
                .userGroup(nga, userGroup, permission)
                .permissionGroup(nga, permissionGroup, permissionSingle, module)
                .permissionSingle(nga, permissionSingle, module)
				.city(nga,city,district)
				
            ;

            //set up layout stuff
            admin.dashboard(nga.dashboard()
                    .template($('#admin-dashboard').html())
            );
            admin.header($('#admin-header').html());

            /**
             * Menu configuration
             */
            var oMenu = nga.menu();
            oMenu.template($('#admin-menu').html());
            oMenu.addChild(
                nga.menu().title('Dashboard').icon('<span class="glyphicon glyphicon-dashboard"></span>').link('/dashboard')
            );

            //operational schedule
            ACL.entityCanView(Entities.Name.operationalSchedule, function () {
                oMenu.addChild(
                    nga.menu(operationalSchedule).title('Operations').icon('<span class="glyphicon glyphicon-console"></span>')
                );
            });
            //ticketbox
            ACL.fragmentCanExecute(Fragment.Name.ticketBox, function () {
                oMenu.addChild(
                    nga.menu().title('Ticket Box').icon('<span class="glyphicon glyphicon-bookmark"></span>').link('/schedule')
                );
            });
            //bookings
            var subMenus = [];
            if (ACL.entityCanView(Entities.Name.booking)) {
                subMenus.push(nga.menu(booking).title('Bookings').icon('<span class="glyphicon glyphicon-equalizer"></span>'));
            }
            if (ACL.entityCanDelete(Entities.Name.booking)) { // TODO use a separate permission for booking cancellations
                subMenus.push(nga.menu().title('Cancellations').icon('<span class="glyphicon glyphicon-remove-sign"></span>').link('/ticket-cancellation'));
            }
            if (subMenus.length) {
                var menu = nga.menu(booking).title('Bookings').icon('<span class="glyphicon glyphicon-equalizer"></span>');
                for (var i = 0; i < subMenus.length; i++) {
                    menu.addChild(subMenus[i]);
                }
                oMenu.addChild(menu);
            }
            //buses
            ACL.entityCanView(Entities.Name.bus, function () {
                oMenu.addChild(
                    nga.menu(bus).title('Bus').icon('<span class="glyphicon glyphicon-bed"></span>')
                        .addChild(nga.menu(bus).title('Buses').icon('<span class="glyphicon glyphicon-bed"></span>'))
                        .addChild(nga.menu().title('Bus Location').icon('<span class="fa fa-location-arrow"></span>').link('/bus-location'))
                );
            });
            //people
            ACL.entityCanView(Entities.Name.person, function () {
                oMenu.addChild(
                    nga.menu().title('People').icon('<span class="glyphicon glyphicon-user"></span>')
                        .addChild(nga.menu(person).title('People').icon('<span class="glyphicon glyphicon-user"></span>'))
                        .addChild(nga.menu(conductor).title('Conductors').icon('<span class="glyphicon glyphicon-user"></span>'))
                        .addChild(nga.menu(driver).title('Drivers').icon('<span class="glyphicon glyphicon-user"></span>'))
                );
            });
            //common tasks
            ACL.entityCanView(Entities.Name.agent, function () {
                oMenu.addChild(
                    nga.menu().title('Common').icon('<span class="glyphicon glyphicon-cog"></span>')
                        .addChild(nga.menu(agent).title('Booking Agents').icon('<span class="fa fa-users"></span>'))
                        .addChild(nga.menu(amenity).title('Amenities').icon('<span class="glyphicon glyphicon-headphones"></span>'))
                );
            });
            //suppliers
            ACL.entityCanView(Entities.Name.supplier, function () {
                oMenu.addChild(
                    nga.menu().title('Suppliers')
                        .addChild(nga.menu(supplier).title('Suppliers'))
                        .addChild(nga.menu(supplierAccount).title('Accounts').icon('<span class="glyphicon glyphicon-usd"></span>'))
                        .addChild(nga.menu(supplierContactPerson).title('Contact Personnel').icon('<span class="glyphicon glyphicon-user"></span>'))
                );
            });

            //schedules
            var subMenus = [];
            if (ACL.entityCanView(Entities.Name.busSchedule)) {
                subMenus.push(nga.menu(busSchedule).title('Bus Schedules').icon('<span class="glyphicon glyphicon-calendar"></span>'));
            }
            if (ACL.entityCanCreate(Entities.Name.busSchedule)) { // TODO use a separate permission for booking cancellations
                subMenus.push(nga.menu().title('Create Schedule').icon('<span class="glyphicon glyphicon-plus-sign"></span>').link('/add_schedule'));
            }
            if (subMenus.length) {
                var menu = nga.menu().title('Schedules').icon('<span class="glyphicon glyphicon-calendar"></span>');
                for (var i = 0; i < subMenus.length; i++) {
                    menu.addChild(subMenus[i]);
                }
                oMenu.addChild(menu);
            }

            //User admin
            var subMenus = [];
            if (ACL.entityCanView(Entities.Name.user)) {
                subMenus.push(nga.menu(user).title('Users').icon('<span class="glyphicon glyphicon-user"></span>'));
            }
            if (ACL.entityCanView(Entities.Name.userGroup)) {
                subMenus.push(nga.menu(userGroup).title('Roles').icon('<span class="fa fa-users"></span>'));
            }
            if (ACL.entityCanView(Entities.Name.permissionSingle)) {
                subMenus.push(nga.menu(permissionSingle).title('Permissions').icon('<span class="glyphicon glyphicon-tag"></span>'));
            }
            if (ACL.entityCanView(Entities.Name.permissionGroup)) {
                subMenus.push(nga.menu(permissionGroup).title('Permission Groups').icon('<span class="glyphicon glyphicon-tags"></span>'));
            }
            if (subMenus.length) {
                var menu = nga.menu().title('User Administration').icon('<span class="glyphicon glyphicon-tower"></span>');
                for (var i = 0; i < subMenus.length; i++) {
                    menu.addChild(subMenus[i]);
                }
                oMenu.addChild(menu);
            }
			
			
            //reports
            // ACL.fragmentCanExecute(Fragment.Name.reportsView, function () {
                // oMenu.addChild(
                    // nga.menu().title('Reports').icon('<span class="glyphicon glyphicon-file"></span>').link('/reports')
                // );
				
            // });
			
			var subMenus = [];
			
			
			if(ACL.fragmentCanExecute(Fragment.Name.reportsView)){
                var menu = nga.menu().title('Reports').icon('<span class="glyphicon glyphicon-file"></span>');
				if(ACL.fragmentCanExecute(Fragment.Name.expressInvoiceReport)){
					menu.addChild(nga.menu().title('Express Invoice').icon('<span class="glyphicon glyphicon-list-alt"></span>').link('/expInv'));
				}
				if(ACL.fragmentCanExecute(Fragment.Name.approvedRefundReport)){
                    menu.addChild(nga.menu().title('Approved Refunds').icon('<span class="glyphicon glyphicon-list-alt"></span>').link('/approvedRefunds'));
                    menu.addChild(nga.menu().title('Approved Refunds Updated').icon('<span class="glyphicon glyphicon-list-alt"></span>').link('/approvedRefundsV2'));
				}
				if(ACL.fragmentCanExecute(Fragment.Name.futureReservationReport)){
					menu.addChild(nga.menu().title('Future Reservations').icon('<span class="glyphicon glyphicon-list-alt"></span>').link('/futureReservationReport'));
				}
				if(ACL.fragmentCanExecute(Fragment.Name.dailyCashReconciliation)){
                    menu.addChild(nga.menu().title('Daily Cash Reconciliation').icon('<span class="glyphicon glyphicon-list-alt"></span>').link('/dailyCashReconciliation'));
                    menu.addChild(nga.menu().title('Daily Cash Reconciliation Updated').icon('<span class="glyphicon glyphicon-list-alt"></span>').link('/dailyCashReconciliationV2'));
				}
				if(ACL.fragmentCanExecute(Fragment.Name.cashReconciliation)){
					menu.addChild(nga.menu().title('Cash Reconciliation ').icon('<span class="glyphicon glyphicon-list-alt"></span>').link('/cashReconciliation'));
				}
				if(ACL.fragmentCanExecute(Fragment.Name.operatedBusFare)){
					menu.addChild(nga.menu().title('Operated Bus Fare ').icon('<span class="glyphicon glyphicon-list-alt"></span>').link('/operatedBusFare'));
				}
				if(ACL.fragmentCanExecute(Fragment.Name.operatedBusFee)){
					menu.addChild(nga.menu().title('Operated Bus Fee').icon('<span class="glyphicon glyphicon-list-alt"></span>').link('/operatedBusFee'));
				}
				//if(ACL.fragmentCanExecute(Fragment.Name.ticketCounterReconciliation)){
				//	menu.addChild(nga.menu().title('Ticket counter Cash Reconciliation').icon('<span class="glyphicon glyphicon-list-alt"></span>').link('/ticketCounterReconciliation'));
				//}
				if(ACL.fragmentCanExecute(Fragment.Name.depoOperatedBusFare)){
					menu.addChild(nga.menu().title('Depot Operated Bus Fare').icon('<span class="glyphicon glyphicon-list-alt"></span>').link('/depotOperatedBusFare'));
				}
				if(ACL.fragmentCanExecute(Fragment.Name.ticketCounterReconciliation)){
					menu.addChild(nga.menu().template('<a href="https://datastudio.google.com/reporting/10Lw1Qm7tSAt6twechCEXNO3ADHP6AeA2/page/wcN0" target="_blank"> <span class="glyphicon glyphicon-list-alt"></span>New Reports</a>'));
				}
				oMenu.addChild(menu);
            };
			
			

			// Administration Menu
			 //City tasks
            ACL.entityCanView(Entities.Name.city, function () {
                oMenu.addChild(
                    nga.menu().title('Administration').icon('<span class="glyphicon glyphicon-cog"></span>')
                        .addChild(nga.menu(city).title('City').icon('<span class="glyphicon glyphicon-map-marker"></span>'))
                );
            });
			
            admin.menu(oMenu);

            nga.configure(admin);

        } catch (e) {
            console.log(e);
        }
    }]);
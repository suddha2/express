/**
 * Created by udantha on 5/5/16.
 */
var Entities = Entities ? Entities : {};

Entities.Name = {
    amenity: 'amenity',
    agent: 'agent',
    booking: 'booking',
    bookingItem: 'bookingItem',
    bookingItemCharge: 'bookingItemCharge',
    bookingItemMarkup: 'bookingItemMarkup',
    bookingItemDiscount: 'bookingItemDiscount',
    bookingItemTax: 'bookingItemTax',
    bookingItemPassenger: 'bookingItemPassenger',
    bookingStatus: 'bookingStatus',
    bus: 'bus',
    busImage: 'busImage',
    busBusRoute: 'busBusRoute',
    busRoute: 'busRoute',
    busStop: 'busStop',
    busType: 'busType',
    busSchedule: 'busSchedule',
    busScheduleBusStop: 'busScheduleBusStop',
    cancellationCharge: 'cancellationCharge',
    change: 'change',
    changeType: 'changeType',
    city: 'city',
    client: 'client',
    conductor: 'conductor',
    driver: 'driver',
	district: 'district',
    operationalSchedule: 'operationalSchedule',
    passenger: 'passenger',
    payment: 'payment',
    person: 'person',
    address: 'address',
    email: 'email',
    telephone: 'telephone',
    refund: 'refund',
    rule: 'rule',
    supplierGroup: 'supplierGroup',
    supplierContactPerson: 'supplierContactPerson',
    supplierAccount: 'supplierAccount',
    supplier: 'supplier',
    travelClass: 'travelClass',
    seatingProfile: 'seatingProfile',
    allowedDivisions: 'allowedDivisions',
    user: 'user',
    module: 'module',
    permission: 'permission',
    permissionGroup: 'permissionGroup',
    permissionSingle: 'permissionSingle',
    accountStatus: 'accountStatus',
    userGroup: 'userGroup',
    division: 'division'
};
// var ht = '';
// //for (var i in Entities.Name){
//     var i = Entities.Name.busBusRoute;
//     ht += 'insert into permission (code, name, description, module_id) values ("'+ i.toLowerCase() +'/view", "'+ i +' entity ; View permission", "Allow '+ i +' to be Accessed", 2); \n'
//     ht += 'insert into permission (code, name, description, module_id) values ("'+ i.toLowerCase() +'/create", "'+ i +' entity ; Create permission", "Allow '+ i +' to be Created", 2); \n'
//     ht += 'insert into permission (code, name, description, module_id) values ("'+ i.toLowerCase() +'/edit", "'+ i +' entity ; Edit permission", "Allow '+ i +' to be edited", 2); \n'
//     ht += 'insert into permission (code, name, description, module_id) values ("'+ i.toLowerCase() +'/delete", "'+ i +' entity ; Delete permission", "Allow '+ i +' to be Deleted", 2); \n'
// //}
// console.log(ht)
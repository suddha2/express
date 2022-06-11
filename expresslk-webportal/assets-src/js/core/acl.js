/**
 * Public class created for ACL since providers or factories are not suited
 * Created by udantha on 5/4/16.
 */
(function (w) {

    /**
     * Configure ACL
     * @returns {{Entity: {CanView: string, CanCreate: string, CanEdit: string, CanDelete: string}, entityCanView: entityCanView, entityCanCreate: entityCanCreate, entityCanEdit: entityCanEdit, entityCanDelete: entityCanDelete}}
     * @constructor
     */
    function AclConfig() {
        var permissionSeparator = '/';

        //get permission modules
        var ACL_DATA = w.ACL_DATA? w.ACL_DATA : {};
        var MODULE_ENTITY = ACL_DATA.ENTITY ? ACL_DATA.ENTITY : {};
        var MODULE_FRAGMENT = ACL_DATA.FRAGMENT ? ACL_DATA.FRAGMENT : {};
        var MODULE_PERMGROUP = ACL_DATA.PERMGROUP ? ACL_DATA.PERMGROUP : {};

        /**
         * check if the entity has passed in permission
         * @param permission
         * @param entity
         * @returns {boolean}
         */
        function hasEntityPermission(permission, entity, callback) {
            //build permission code as in database permission table
            var permissionCode = entity + permissionSeparator + permission; // ex: 'suppliers/view'
            permissionCode = permissionCode.toLowerCase();

            //check if this permission code exists in allwed resources
            var hasPermission = MODULE_ENTITY.hasOwnProperty(permissionCode);
            //if callback is passed, call it if the permission check is success
            if(hasPermission===true && callback){
                callback.call(this);
            }
            //return permission status
            return hasPermission;
        }

        /**
         * Check if the fragment has permission
         * @param permission
         * @param fragment
         * @param callback
         * @returns {boolean}
         */
        function hasFragmentPermission(permission, fragment, callback) {
            
			//build permission code as in database permission table
            var permissionCode = fragment + permissionSeparator + permission; // ex: 'suppliers/view'
			//permissionCode = permissionCode.toLowerCase();
			
            //check if this permission code exists in allwed resources
            var hasPermission = MODULE_FRAGMENT.hasOwnProperty(permissionCode);
			
			//if callback is passed, call it if the permission check is success
            if(hasPermission===true && callback){
                callback.call(this);
            }
            //return permission status
            return hasPermission;
        }

        /**
         * Check if the user has this permission group
         * @param permissionGroup
         * @param callback
         * @returns {boolean}
         */
        function hasPermissionGroup(permissionGroup, callback) {
            //check if this permission code exists in allwed resources
            var hasPermission = MODULE_PERMGROUP.hasOwnProperty(permissionGroup);
            //if callback is passed, call it if the permission check is success
            if(hasPermission===true && callback){
                callback.call(this);
            }
            //return permission status
            return hasPermission;
        }

        return {
            /**
             * class constants for entity permissions
             */
            Entity: {
                CanView: 'view', //value matches Acl::ENTITY_CAN_VIEW value in backend
                CanCreate: 'create',
                CanEdit: 'edit',
                CanDelete: 'delete'
            },
            /**
             * Class constants for fragments
             */
            Fragment: {
				CanView: 'view',
                CanRead: 'read',
                CanWrite: 'write',
                CanExecute: 'execute'
            },

            /**
             * Is entity viewable
             * @param entity
             * @param callback
             * @returns {boolean}
             */
            entityCanView : function(entity, callback) {
                return hasEntityPermission(this.Entity.CanView, entity, callback);
            },
            /**
             * Is entity creatable
             * @param entity
             * @param callback
             * @returns {boolean}
             */
            entityCanCreate : function(entity, callback) {
                return hasEntityPermission(this.Entity.CanCreate, entity, callback);
            },
            /**
             * Is entity Editable
             * @param entity
             * @param callback
             * @returns {boolean}
             */
            entityCanEdit : function(entity, callback) {
                return hasEntityPermission(this.Entity.CanEdit, entity, callback);
            },
            /**
             * Is entity viewable
             * @param entity
             * @param callback
             * @returns {boolean}
             */
            entityCanDelete : function(entity, callback) {
                return hasEntityPermission(this.Entity.CanDelete, entity, callback);
            },

			
			
			/**
             * Fragment viewable
             * @param fragment
             * @param callback
             * @returns {boolean}
             */
            fragmentCanView : function(fragment, callback) {
                return hasFragmentPermission(this.Fragment.CanView, fragment, callback);
            },

			
            /**
             * Fragment readable
             * @param fragment
             * @param callback
             * @returns {boolean}
             */
            fragmentCanRead : function(fragment, callback) {
                return hasFragmentPermission(this.Fragment.CanRead, fragment, callback);
            },

            /**
             * Fragment writable
             * @param fragment
             * @param callback
             * @returns {boolean}
             */
            fragmentCanWrite : function(fragment, callback) {
                return hasFragmentPermission(this.Fragment.CanWrite, fragment, callback);
            },

            /**
             * Fragment executable
             * @param fragment
             * @param callback
             * @returns {boolean}
             */
            fragmentCanExecute : function(fragment, callback) {
                return hasFragmentPermission(this.Fragment.CanExecute, fragment, callback);
            },

            /**
             * Common method for passed in permission
             * @param permission
             * @param fragment
             * @param callback
             * @returns {boolean}
             */
            fragmentHasPermission : function(permission, fragment, callback) {
                return hasFragmentPermission(permission, fragment, callback);
            },

            /**
             *
             * @param permissionGroup
             * @param callback
             * @returns {boolean}
             */
            hasPermissionGroup : function (permissionGroup, callback) {
                return hasPermissionGroup(permissionGroup, callback)
            }
        }
    }

    w.ACL = AclConfig();

})(window);



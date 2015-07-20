var exec = require('cordova/exec');

/**
 * This represents the mobile device, and provides properties for inspecting the model, version, UUID of the
 * phone, etc.
 * @constructor
 */
function LocalStoragePlugin() {
}

/**
 * Get device info
 *
 * @param {Function} successCallback The function to call when the heading data is available
 * @param {Function} errorCallback The function to call when there is an error getting the heading data. (OPTIONAL)
 */
LocalStoragePlugin.prototype.getLocalStorageData = function(successCallback, errorCallback) {
    exec(successCallback, errorCallback, "LocalStoragePlugin", "getLocalStorageData", []);
};

module.exports = new LocalStoragePlugin();

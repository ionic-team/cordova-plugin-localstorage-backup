var exec = require('cordova/exec');

var LocalStorageBackup = {};

LocalStorageBackup.save = function(id, callback) {
  var localStorageString = JSON.stringify(localStorage);
  exec(callback, callback, "LocalStorageBackup", "save", [id, localStorageString]);
}

LocalStorageBackup.load = function(id, callback) {
  exec(_success, callback, "LocalStorageBackup", "load", [id]);

  function _success(localStorageString) {
    var localStorageObj = JSON.parse(localStorageString);
    for (var key in localStorageObj) {
      localStorage.setItem(key, localStorageObj[key]);
    }
    callback();
  }
}

LocalStorageBackup.clear = function(id, callback) {
  exec(callback, callback, "LocalStorageBackup", "clear", [id]);
}

module.exports = LocalStorageBackup;

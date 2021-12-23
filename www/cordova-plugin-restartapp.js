var exec = require('cordova/exec');

module.exports = function(error) {
    exec(null, error, 'RestartApp', 'restartApp', []);
};

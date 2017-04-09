'use strict';

module.exports = function (app) {
    var tcService = require('../service/tcService');

    // todoList Routes
    app.route('/tc')
        .post(tcService.getCsps)
        ;
};
'use strict';

module.exports = function (app) {
    var tcService = require('../service/tcService');

    // todoList Routes
    app.route('/tc/:id')
        .post(tcService.getCsps)
        .get(tcService.getCsps)
        ;
    app.route('/tc')
        .get(tcService.getAllTCs);
    app.route('/tct/:id')
        .get(tcService.getTeams)
    ;
};
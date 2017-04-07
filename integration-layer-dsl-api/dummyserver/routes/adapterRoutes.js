'use strict';

module.exports = function (app) {
    var adapterService = require('../service/adapterService');

    // todoList Routes
    app.route('/adapter/integrationData')
        .post(adapterService.create)
        .put(adapterService.update)
        .delete(adapterService.delete);

    // app.route('/adapter/:taskId')
    //     .get(todoList.read_a_task)
    //     .put(todoList.update_a_task)
    //     .delete(todoList.delete_a_task);
};
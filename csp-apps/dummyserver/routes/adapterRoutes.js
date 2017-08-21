'use strict';

module.exports = function (app) {
    var adapterService = require('../service/adapterService');

    //Routes

    app.route('/adapter/version')
        .get(adapterService.version);
    app.route('/adapter/integrationData')
        .all(function(req, res, next){
            console.log('### adapter ###') ;
            next();
        })
        .post(adapterService.create)
        .put(adapterService.update)
        .delete(adapterService.delete);

    app.route('/adapter/viper')
        .all(function(req, res, next){
            console.log('### viper ###') ;
            next();
        })
        .post(adapterService.create)
        .put(adapterService.update)
        .delete(adapterService.delete);

    app.route('/adapter/jitsi')
        .all(function(req, res, next){
            console.log('### jitsi ###') ;
            next();
        })
        .post(adapterService.create)
        .put(adapterService.update)
        .delete(adapterService.delete);

    app.route('/adapter/misp')
        .all(function(req, res, next){
            console.log('### misp ###') ;
            next();
        })
        .post(adapterService.create)
        .put(adapterService.update)
        .delete(adapterService.delete);

    app.route('/adapter/intelmq')
        .all(function(req, res, next){
            console.log('### intelmq ###') ;
            next();
        })
        .post(adapterService.create)
        .put(adapterService.update)
        .delete(adapterService.delete);

    app.route('/adapter/taranis')
        .all(function(req, res, next){
            console.log('### taranis ###') ;
            next();
        })
        .post(adapterService.create)
        .put(adapterService.update)
        .delete(adapterService.delete);

    app.route('/adapter/rt')
        .all(function(req, res, next){
            console.log('### rt ###') ;
            next();
        })
        .post(adapterService.create)
        .put(adapterService.update)
        .delete(adapterService.delete);

    app.route('/adapter/demoapp')
        .all(function(req, res, next){
            console.log('### demoapp ###') ;
            next();
        })
        .post(adapterService.create)
        .put(adapterService.update)
        .delete(adapterService.delete);


    app.route('/adapter/tc')
        .all(function(req, res, next){
            console.log('### trustcircle ###') ;
            next();
        })
        .post(adapterService.create)
        .put(adapterService.update)
        .delete(adapterService.delete);




    // app.route('/adapter/:taskId')
    //     .get(todoList.read_a_task)
    //     .put(todoList.update_a_task)
    //     .delete(todoList.delete_a_task);
};

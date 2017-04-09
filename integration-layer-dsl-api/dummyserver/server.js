var express = require('express'),
    app = express(),
    port = process.env.PORT || 3000,
    appName = process.env.APP_NAME || 'adapter',
    bodyParser = require('body-parser');

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var routes;
var routeForAppExists = false;
switch (appName){
    case 'adapter':
        routes = require('./routes/tcRoutes');
        routeForAppExists = true;
        break;
    case 'tc':
        routes = require('./routes/tcRoutes');
        routeForAppExists = true;
        break;

}

if(!routeForAppExists){
    console.log("No route found for app '"+appName+"'. Exiting...");
    process.exit();
}

routes(app);

app.use(function(req, res) {
    res.status(404).send({url: req.originalUrl + ' not found'})
});
app.listen(port);

console.log(appName+' mockserver started on: ' + port);
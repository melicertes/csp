var express = require('express'),
    https = require('https'),
    fs = require('fs'),
    app = express(),
    port = process.env.PORT || 3000,
    ssl = process.env.SSL || false,
    sslCA = process.env.SSL_CA || '/data/common/sslcert/ca.crt',
    sslCERT = process.env.SSL_CERT || '/data/common/sslcert/csp-internal.crt',
    sslKEY = process.env.SSL_KEY || '/data/common/sslcert/csp-internal.key',
    appName = process.env.APP_NAME || 'adapter',
    bodyParser = require('body-parser');

app.use(bodyParser.urlencoded({ extended: true }));
app.use(bodyParser.json());

var routes;
var routeForAppExists = false;
switch (appName){
    case 'adapter':
        routes = require('./routes/adapterRoutes');
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

if(!ssl) {
    app.listen(port);
}else{
    var privateKey  = "";//fs.readFileSync('sslcert/server.key', 'utf8');
    var certificate = "";//fs.readFileSync('sslcert/server.crt', 'utf8');

    var options = {
        requestCert: true,
        rejectUnauthorized:true,
        key: fs.readFileSync(sslKEY),
        cert: fs.readFileSync(sslCERT),
        ca: fs.readFileSync(sslCA),
    };


    var httpsServer = https.createServer(options, app);
    httpsServer.listen(port);
}

console.log(appName+' mockserver started on: ' + port +' ssl: '+ssl);
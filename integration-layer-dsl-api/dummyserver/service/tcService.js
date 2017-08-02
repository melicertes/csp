'user strict';

exports.getAllTCs = function (req, res) {
    console.log("all tcs!");
    //res.send(err);
    //res.json({cspId:['http://ex.csp1.com']});
    res.json([{id:'dummyId',short_name:'CTC::SHARING_DATA_INCIDENT',teams:['1','2']}]);
    //res.send('Successful creation');
};

exports.getCsps = function (req, res) {
    console.log("tc got it!");
    //res.send(err);
    //res.json({cspId:['http://ex.csp1.com']});
    res.json({id:"dummyId",short_name:'CTC::SHARING_DATA_INCIDENT',teams:['1','2']});
    //res.send('Successful creation');
};
var cnt = 0
exports.getTeams = function (req, res) {
    cnt++;
    console.log("tc team got it!");
    //res.send(err);
    //res.json({cspId:['http://ex.csp1.com']});
    res.json({id:1,short_name:'',name:'aaacsp'+cnt,csp_domain:'http://ex.aaacsp'+cnt+'.com'});
    //res.send('Successful creation');
};

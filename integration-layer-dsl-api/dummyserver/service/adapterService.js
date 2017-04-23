'user strict';

exports.create = function (req, res) {
    //res.send(err);
    //res.json({message:'Successful creation'});

    if(req.body.dataType) {
        res.send('Successful creation');
        console.log('Successful creation');
    }else{
        res.status(400).send("Error");
    }
};

exports.update = function (req, res) {
    //res.json({message:'Successful update'});
    res.send('Successful update');
    console.log('Successful update');
};

exports.delete = function (req, res) {
    //res.json({message:'Successful delete'});
    res.send('Successful delete');
    console.log('Successful delete');
};

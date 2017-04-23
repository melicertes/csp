'user strict';

exports.create = function (req, res) {
    //res.send(err);
    //res.json({message:'Successful creation'});

    if(req.body.dataType) {
        res.send('Successful creation');
        console.log('Successful creation');

	console.log(req.get('Content-Type'));
	console.log(req.body);
    }else{
        res.status(400).send("Error");
    }
      console.log(req.get('Content-Type'));
        console.log(req.body);


};

exports.update = function (req, res) {
    //res.json({message:'Successful update'});
    res.send('Successful update');
    console.log('Successful update');

      console.log(req.get('Content-Type'));
        console.log(req.body);
};

exports.delete = function (req, res) {
    //res.json({message:'Successful delete'});
    res.send('Successful delete');
    console.log('Successful delete');
    console.log(req.get('Content-Type'));
    console.log(req.body);
};

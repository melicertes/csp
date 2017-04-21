$(document).ready(function() {
    var max_fields      = 10; //maximum input boxes allowed
    var wrapper         = $(".input_fields_wrap"); //Fields wrapper
    var add_button      = $("#add_field_button"); //Add button ID
    var delete_button      = $("#delete_button"); //Delete button
    var new_email		= $("#new_email"); // new email input
    var x = 0; //initlal text box count

	$(delete_button).click(function(e){
		e.preventDefault();
		$("tr.useremail").each(function() {
			$this = $(this)

			if($(this).find("input.todelete").is(":checked")){
				$(this).remove();
			}
						
//			console.log($(this));
		});
	});
    $(add_button).click(function(e){ //on add input button click
        e.preventDefault();
        
        
	    $(wrapper).append('<tr class="useremail"><td><input class="todelete" type="checkbox" value=""></input></td><td><input hidden="true" class="form-control" type="text" value="' + new_email.val() + '" name="emails['+x+']"/><div>'+new_email.val()+'</div></td></tr>'); //add input box
	    x++; //text box increment
    });
    
    $(wrapper).on("click",".remove_field", function(e){ //user click on remove text
        e.preventDefault(); $(this).parent('div').remove(); x--;
    })
});
var ilPolicies = {
    condition:"",
    validateSaveOperation: function () {
        var ret = "";
        if ($('#integrationDataType').val() == null || $('#integrationDataType').val().length==0) {
            ret += "- Please select data type \n";
        }

        if ($('#condition').val() == null || $('#condition').val().length==0) {
            ret += "- Please do not leave empty condition \n";
        }

        if ($('#sharingPolicyAction').val() == null || $('#sharingPolicyAction').val().length==0) {
            ret += "- Please select an action \n";
        }

        if (ret.length > 0) {
            alert(ret);
            event.preventDefault()
        }
    },
    init: function () {
        ilPolicies.condition = $('#condition').val();
        $('#active').change(function(){
            var active = $(this).val();
            if(active == null || active.length==0){
                ilPolicies.condition = $('#condition').val();
                $('#condition').val('no condition required');
            }else{
                if(ilPolicies.condition != 'no condition required'){
                    $('#condition').val(ilPolicies.condition);
                }else{
                    $('#condition').val('');
                }

            }
        });
    }
};
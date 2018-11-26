/**
 * Created by sagar on 10.04.17.
 */

function getCookie(name) {
    var cookieValue = null;
    if (document.cookie && document.cookie !== '') {
        var cookies = document.cookie.split(';');
        for (var i = 0; i < cookies.length; i++) {
            var cookie = jQuery.trim(cookies[i]);
            // Does this cookie string begin with the name we want?
            if (cookie.substring(0, name.length + 1) === (name + '=')) {
                cookieValue = decodeURIComponent(cookie.substring(name.length + 1));
                break;
            }
        }
    }
    return cookieValue;
}
function csrfSafeMethod(method) {
    return (/^(GET|HEAD|OPTIONS|TRACE)$/.test(method));
}
$.ajaxSetup({
    crossDomain: false, // obviates need for sameOrigin test
    beforeSend: function (xhr, settings) {
        if (!csrfSafeMethod(settings.type)) {
            xhr.setRequestHeader("X-CSRFToken", getCookie('csrftoken'));
        }
    }
});

// Make backspace delete the whole tag in select2.
// See: https://github.com/select2/select2/issues/3354
$.fn.select2.amd.require(['select2/selection/search'], function (Search) {
    Search.prototype.searchRemoveChoice = function (decorated, item) {
        this.trigger('unselect', {
            data: item
        });

        this.$search.val('');
        this.handleSearch();
    };
}, null, true);


// diff apply function collection

function level3_apply(subdiffblock, subblockname, level1_difftarget_stub, form_no) {
    $(subdiffblock).find('.applydiff').each(function (bnr, diffentry) {
        var fieldname = $(diffentry).attr('fieldname');
        $(diffentry).attr('difftarget', 'id_' + subblockname + '-' + (parseInt(form_no) - 1) + '-' + fieldname);

        // do the magic
        $(diffentry).click();
        // console.dir("4 For teammember " + level1_difftarget_stub + " in " + subblockname + " setting " + fieldname);
    });    
}


function level2_apply(diffblock, level1_difftarget_stub, el) {
    $(diffblock).parents('.formset-form').find('.diffdatablock.difflevel-2').each(function (subbnr, subdiffblock) {
        var subblockname = level1_difftarget_stub + '-' + $(subdiffblock).attr('blockname');
        var subadd_btn = $('button[data-formset-add="' + subblockname + '"]');
        $(subadd_btn).click();
        // console.dir("3 For teammember " + level1_difftarget_stub + " adding " + subblockname);
        var form_no = $('#id_' + subblockname + '-TOTAL_FORMS').val();
        level3_apply(subdiffblock, subblockname, level1_difftarget_stub, form_no);
    });
}


function apply_all_team_members_formset_diff(el, formset_name) {
    var form_no = $('#id_' + formset_name + '-TOTAL_FORMS').val();
    var add_btn = $('button[data-formset-add="' + formset_name + '"]')
    $(el).parents('.panel').find('.delete_object_btn').click(); 

    // for each teammember (incoming block)
    $('div[data-formset-body="' + formset_name + '"]').find('.diffdatablock.difflevel-1').each(function(anr, diffblock) {
        add_btn.click();

        var level1_difftarget_stub = formset_name + '-' + (parseInt(form_no) + parseInt(anr));

        // console.dir("1 Created teammember " + level1_difftarget_stub);
        
        $(diffblock).find('.applydiff').each(function (bnr, diffentry) {
            var fieldname = $(diffentry).attr('fieldname');
            $(diffentry).attr('difftarget', 'id_' +level1_difftarget_stub + '-' + fieldname);

            // do the magic
            $(diffentry).click();
            // console.dir("2 Set teammember " + level1_difftarget_stub + " " + fieldname);
        });

        // dirty. make sure the browser has enough time to create forms.
        setTimeout(function() {level2_apply(diffblock, level1_difftarget_stub, el);}, 500);
    });

    // remove changeset header
    $(el).parents('.panel').find('.diffcontent').hide();
    $(el).parents('.panel-heading').hide();
}

function apply_all_formset_diff(el, formset_name) {
    var form_no = $('#id_' + formset_name + '-TOTAL_FORMS').val();
    var add_btn = $('button[data-formset-add="' + formset_name + '"]')

    $(el).parents('.panel').find('.delete_object_btn').click();
    $('div[data-formset-body="' + formset_name + '"]').find('.diffdatablock').each(function(anr, diffblock) {
        $(diffblock).find('.applydiff').each(function (bnr, diffentry) {
            var fieldname = $(diffentry).attr('fieldname');
            $(diffentry).attr('difftarget', 'id_' + formset_name + '-' + (parseInt(form_no) + parseInt(anr)) + '-' + fieldname);
        });
        add_btn.click();

    });

    // do the magic
    $(el).parents('.panel').find('.applydiff').click();

    // remove diff block used for direct fields and resize the orig block to col-md-12, setting select2 width to 100%
    var htmldiffblock = $(el).parents('.col-md-6')
    var htmlorigblock = htmldiffblock.siblings('.col-md-6').removeClass('col-md-6').addClass('col-md-12');
    htmldiffblock.hide();
    htmlorigblock.find(".select2-container").css('width', '100%');

    // remove subform diff content and header
    $(el).parents('.panel').find('.diffcontent').hide();
    $(el).parents('.panel-heading').hide();
}

function initialize_select2_fields(scope) {
    $('select[data-s2]', scope).each(function() {
        var $el = $(this);
        var url = $el.data('s2-url');
        var args = {};
        if ($el.is('[data-s2-tags]')) {
            args.tags = true;
        }
        if (url) {
            args.ajax = {
                url: url,
                delay: 250,
                dataType: 'json',
                processResults: function(data, page) {
                    return {
                        results: data.results,
                    }
                },
            };
        }
        $el.select2(args);
    });
}

function initialize_diff_apply(scope) {
    $(document).on("click touchend", ".applydiff", function () {
        // apply single cell value from incoming to it's mapping in the form
        var elem = $(this);
        var difftarget = '#' + elem.attr('difftarget');
        var diffvalue = elem.attr('diffvalue');
        var fieldtype = elem.attr('fieldtype');

        // Text Fields
        if (fieldtype == 'simple') {
            $(difftarget).val(diffvalue);
        } else if (fieldtype == 'boolean') {
            if (diffvalue == 'True') {
                $(difftarget)[0].checked = true;
            } else {
                $(difftarget)[0].checked = false;
            }
        } else if (fieldtype == 'selectsingle') {
            // Select2 single input value
            if (diffvalue != '') {
                if ($(difftarget).find('option[value="' + diffvalue + '"]').length) {
                    $(difftarget).val(diffvalue).trigger('change');
                } else {
                    var newOption = new Option(diffvalue, diffvalue, true, true);
                    $(difftarget).val(null).append(newOption).trigger('change');
                }
            } else {
                $(difftarget).val(null).trigger('change');
            }
        } else if (fieldtype == 'selectmultiple') {
            // Select2 single input value
            if (diffvalue != '') {
                if ($(difftarget).find('option[value="' + diffvalue + '"]').length) {
                    $(difftarget).val(diffvalue).trigger('change');
                } else {
                    var newOption = new Option(diffvalue, diffvalue, true, true);
                    $(difftarget).val(null).append(newOption).trigger('change');
                }
            } else {
                $(difftarget).val(null).trigger('change');
            }
        }
    });
}

$(function() {
    initialize_select2_fields();
    initialize_diff_apply();
});

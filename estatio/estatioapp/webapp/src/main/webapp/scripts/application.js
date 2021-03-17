$(document).ready(function() {

    Wicket.Event.subscribe(Isis.Topic.FOCUS_FIRST_PARAMETER, function(jqEvent, elementId) {
        setTimeout(function() {
            $(".IncomingInvoice-pendingApprovalTask .inlinePromptForm .actionParametersForm div.parameter .String-complete-0                .scalarValueInput input").prop("readonly", true);
            $(".IncomingInvoice-pendingApprovalTask .inlinePromptForm .actionParametersForm div.parameter .String-approve-0                 .scalarValueInput input").prop("readonly", true);
            $(".Task-object                         .inlinePromptForm .actionParametersForm div.parameter .String-completeIncomingInvoice-0 .scalarValueInput input").prop("readonly", true);
            $(".Task-object                         .inlinePromptForm .actionParametersForm div.parameter .String-approveIncomingInvoice-0  .scalarValueInput input").prop("readonly", true);
        }, 0);
    });

/*
    not actually needed because this is the default (for Chrome, at least)

    $('a.entityUrlSource').bind('click', function(e) {
       if (e.ctrlKey){
         e.preventDefault();
         var url = $(this).attr('href');
         window.open(url,'_blank')
       }
    });
*/

});
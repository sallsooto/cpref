$(document).ready(function(){
	var form_validation_errors = 0; //formRegister
	$(".PerformanceForm").mshzJqueryAjaxFormValidator({'callback' : function(error_finded){
		form_validation_errors = error_finded;
    }});
	$(".PerformanceForm").on('submit',function(e){
		e.preventDefault();
		if(form_validation_errors<=0){
			$form = $(this);
	        var formdata = {
	        		id : $("#id").val(),
	        		fonctionId : $("#fonctionId").val(),
	        		objectifId : $("#objectifId").val(),
	         		name : $("#name").val(),
	        		description : $("#description").val(),
	        		parentId : $("#parentId").val(),
	        		objectif : $("#isObjectif").val(),
	        };
           $.ajax({
               url : $form.attr('action'),
               type : 'post',
               contentType : false,
               processData : false,
               data : JSON.stringify(formdata),
               dataType : 'json',
               beforeSend : function(){
     	    		//$(".loading-img").removeClass("d-none").addClass("d-block");
               },
               success : function(resultat,status){
            	   if(resultat.reponse=="error"){
            		   $('.errMsgBlock').text(resultat.msg);
            	   }
            	   else{
//            			$(".registerSuccessMsgAlert").removeClass("d-none hide").addClass("d-block show");
//            			$("input, textarea").val("");
//            	    	$(".loading-img").removeClass("d-block").addClass("d-none");
//						$("#modal_register").modal("hide");
            	   }
               },
               error : function(e){
      	    		//$(".loading-img").removeClass("d-block").addClass("d-none");
            	  console.log(e);
               }
           });
		}
	});
});
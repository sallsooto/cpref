/*$(document).ready(function(){
  $("form[data-mshz-validate]").mshzJqueryAjaxFormValidator();
});*/

(function($){

$.extend($.fn,
   {
	   mshzJqueryAjaxFormValidator : function(options)
	   {
	     
		    var defaultParam = 
		    {
		    		'selectors' : ":input[data-mshz-required],:input[data-mshz-equals]," +
 					 			  ":input[data-mshz-email],:input[data-mshz-length]",
		 			'nbError' : 0,
		    		'callback' : null,
		     
		    }

		    var param = $.extend(defaultParam,options);
		    $('.help-block, .form-control-feedback').hide();

		    return this.each(function()
		    {
		      $(this).on('submit',function(e)
		      	// gérer les l'evebement submit
		      {
		         //e.preventDefault();
		         form = $(this);
                 param.nbError = 0;
		         $(form).find(param.selectors).each(function()
		         {
                     if( !$(this).mshzGeneralValidate()){
                         param.nbError ++;
                     }
		        });

		         if(param.nbError > 0) {
                     e.preventDefault();
                     // scroll vers le container d'erreur le premier champs d'erreur
                     errors_container = $('.data-mshz-form-errors-container').first();
                     if(errors_container == undefined || errors_container.length <=0) {
                         errors_container = $('.is-invalid').first();
                     }
                     else{
                     	if($(errors_container).is(':empty')){
                            $(errors_container).html("Des erreurs de validations ont été trouvées veuillez vérifier tous les champs !");
                            errors_container.removeClass('d-none').show();
                     	}
					 }
                     if(errors_container != undefined && errors_container.length > 0) {
                         errors_container_height = (errors_container.outerHeight()*2) + errors_container.outerHeight();
                         $('html, body').stop().animate({scrollTop: (errors_container.offset().top-errors_container_height)}, 400);
                     }
                 }

			   	if(param.callback){
				  param.callback(param.nbError);
			  	}
		     });
            
             $(param.selectors).on('blur',function()
             {
             	if($(this).mshzGeneralValidate()){
             		$(this).parent().addClass('has-success');
                    $(this).parent().removeClass('has-danger');
             	}else{
                   $(this).parent().removeClass('has-success');
                   $(this).parent().addClass('has-danger');
             	}

             });

	         $(param.selectors).on('focus',function()
	         {

	         	if($(this).hasClass('is-invalid')){
                    $(this).mshzHideBlock("is-invalid");
	         	}
	         	 
	         });

            /* if(param.callback){
            	 param.callback(param.nbError);
             }*/
		   });
        },
       
       mshzGeneralValidate : function()
       {
		   if(($(this).mshzHasAttr('data-mshz-required')))
		   {
				 if(($(this).val() == ""))
				 {
				   $(this).mshzShowBlock('is-invalid');
				   return false;
				 }
           }

           if($(this).mshzHasAttr('data-mshz-email'))
		  // verification d'un champ email
		  {
			var regMail = new RegExp("^[a-zA-Z0-9\-_]+[a-zA-Z0-9\.\-_]*@[a-zA-Z0-9\-_]+\.[a-zA-Z\.\-_]{1,}[a-zA-Z\-_]+","i");
			if(!regMail.test($(this).val()))
			{
				if($(this).val() != ""){
					$(this).mshzShowBlock('is-invalid');
				}
				// on compte l'erreur si c'est un champs obligatoire
				if($(this).mshzHasAttr('data-mshz-required')){
                   return false;
				}
			}
		  }
		  if($(this).mshzHasAttr('data-mshz-length') && !($(this).mshzLengthControl($(this).attr('data-mshz-length'))))
		   // control du longueur
		  {
              if($(this).val() != ""){
                  $(this).mshzShowBlock('is-invalid');
              }
              // on compte l'erreur si c'est un champs obligatoire
              if($(this).mshzHasAttr('data-mshz-required')){
                  return false;
              }
		  }
		  if($(this).mshzHasAttr('data-mshz-equals'))
		  // ce champ doit etre egal
		  {
			 fieldEqualId = $(this).attr('data-mshz-equals');
			 equalValue = $(fieldEqualId).val();
			 if(!$(this).mshzEqualsControl(equalValue)){
                 if($(this).val() != ""){
                     $(this).mshzShowBlock('is-invalid');
                 }
                 // on compte l'erreur si c'est un champs obligatoire
                 if($(this).mshzHasAttr('data-mshz-required')){
                     return false;
                 }
			 }
		  }
		  return true;
       },

        mshzShowBlock : function(classHas){
       	 	$(this).addClass(classHas);
       	 	$(this).find(".help-block").show();
       	 	errors_container = $('.data-mshz-form-errors-container').first();
            if(errors_container != undefined && errors_container.length >0) {
                errors_container.removeClass('d-none').show();
            }

         },

        mshzHideBlock : function(classHas){
            $(this).removeClass(classHas);
 		    $(this).find('.help-block').hide();
 		    errors_container = $('.data-mshz-form-errors-container').first();
			if(errors_container != undefined && errors_container.length >0) {
				errors_container.addClass('d-none').hide();
			}
        },

        mshzLengthControl : function(len){
          return ($(this).val().length >= len);
        },

        mshzEqualsControl : function(fieldValue){
            return ($(this).val() == fieldValue);
        },

	   mshzHasAttr : function(attrName){
           return this.attr(attrName) !== undefined;
	   },

    });

})(jQuery);

isValideMshzForm = function(nbError){
	return nbError <= 0 ;
}
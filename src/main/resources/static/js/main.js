(function ($) {
	/*=============================== MY SCRIPT ============================*/
	var formValidate = $('.mshz-form-validate');
	if(formValidate.length>0)
		formValidate.mshzJqueryAjaxFormValidator(); // validation des formulaires assynchrone
    $('.trigger').on('click',function(){
        hindler = $(this).attr('data-trigger-hindler');
        $(hindler).trigger('click');
    });
    
    $('.img_field_trigger').on('change',function(e){
    	console.log("chsdgfdhsf")
        img_field_trigger_value = $(this).val();
        show_uploaded_img($(this),".insc_avatar",true);
    });
    $(window).resize(function(){
    	manageTableResponsivity();
     });
    $('[data-toggle="tooltip"]').tooltip();
    $('[data-toggle="popover"]').popover();
    
    //set all user notifications on seen
    $('#notificationDropdown').on('hidden.bs.dropdown', function () {
    	seenAllUsersNotifications("/Notification/setSeenNotesJson");
    });
     // set all user notification on seen
    $('#messagesDropdown').on('hidden.bs.dropdown', function () {
    	seenAllUsersNotifications("/Notification/setSeenMessagesJson");
    });
    // cnahge task manager
	$(".task_status_changer, .status_task_changer").on('click',function(e){
		e.preventDefault();
	});
	
	$("body").on('click','.btn_danger_actionner',function(e){
		e.preventDefault(e);
//		danger_action_link = $(this).attr('data-link');
//		console.log(danger_action_link);
	});
	$(".btn_modal_danger_yes").on('click',function(e){
		e.preventDefault();
		console.log("continuer");
	});
	$(".fa_file").each(function(){
//		fa_classes = getFileFontAwesomeTextClass($(this).attr('data-fileName'));
		$(this).addClass("fas fa-file");
		console.log($(this).attr('data-fileName'));
	});
	/*============================ END MY SCRIPT ===========================*/
    "use strict";


    /*==================================================================
    [ Focus input ]*/
    $('.input100').each(function(){
        $(this).on('blur', function(){
            if($(this).val().trim() != "") {
                $(this).addClass('has-val');
            }
            else {
                $(this).removeClass('has-val');
            }
        })    
    })
  
  
    /*==================================================================
    [ Validate ]*/
    var input = $('.validate-input .input100');

    $('.validate-form').on('submit',function(){
        var check = true;

        for(var i=0; i<input.length; i++) {
            if(validate(input[i]) == false){
                showValidate(input[i]);
                check=false;
            }
        }

        return check;
    });


    $('.validate-form .input100').each(function(){
        $(this).focus(function(){
           hideValidate(this);
        });
    });

    function validate (input) {
        if($(input).attr('type') == 'email' || $(input).attr('name') == 'email') {
            if($(input).val().trim().match(/^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{1,5}|[0-9]{1,3})(\]?)$/) == null) {
                return false;
            }
        }
        else {
            if($(input).val().trim() == ''){
                return false;
            }
        }
    }

    function showValidate(input) {
        var thisAlert = $(input).parent();

        $(thisAlert).addClass('alert-validate');
    }

    function hideValidate(input) {
        var thisAlert = $(input).parent();

        $(thisAlert).removeClass('alert-validate');
    }
    
    /*==================================================================
    [ Show pass ]*/
    var showPass = 0;
    $('.btn-show-pass').on('click', function(){
        if(showPass == 0) {
            $(this).next('input').attr('type','text');
            $(this).addClass('active');
            showPass = 1;
        }
        else {
            $(this).next('input').attr('type','password');
            $(this).removeClass('active');
            showPass = 0;
        }
        
    });

     $(".dialogShower").on('click',function(e){
    	e.preventDefault(); 
    	$(".confirmDialogLoading").addClass('d-none');
    	showConfirmDialog($(this),"#modalConfirmDialog");
     });
     
     replaceElement("data-replace",".");
})(jQuery);

function seenAllUsersNotifications(url){
	  $.ajax({
		 url : url,
		 method : 'get',
		 success : function(res){
			 console.log(url);
		 },
		 error : function(e){
			console.log(e); 
		 },
	  });
};
function triggable_img_manage(img_avatar_class,default_value,triggable_values){
    avatar = $('.'+img_avatar_class);
    if(avatar != undefined && avatar.length > 0) {
        avatar_src_value = avatar.attr("src");
        avatar_path = avatar_src_value.substring(0,avatar_src_value.lastIndexOf('/') + 1);
        avatar_name = avatar_src_value.substring(avatar_path.length,avatar_src_value.lastIndexOf('.'));
        avatar_ext = avatar_src_value.substring(avatar_src_value.lastIndexOf('.'), avatar_src_value.length);
        default_value = default_value.toString().toLowerCase();
        if(in_array_js(avatar_name,triggable_values)) {
            new_avatar_src_value = avatar_path + default_value + ".jpg";
            avatar.attr("src", new_avatar_src_value);
        }
    }
}
// verfication de l'existance d'une valeur dans un tableau
var in_array_js = function(value,tab){
    var cpt = 0;
   for (var i = 0 ; i < tab.length; i++) {
      if(tab[i] == value)
        cpt ++;
   }
   if(cpt > 0)
    return true;
   else
    return false;
}//affichage du fichier uploideé
var show_uploaded_img = function(element_this,div_contenaiteur_img,choix_resizese){
    files = $(element_this)[0].files;	
      width = $(div_contenaiteur_img).width;
    if(files.length > 0){
     file = files[0];
     image_preview = $(div_contenaiteur_img);
     image_preview.find('img').attr('src', window.URL.createObjectURL(file));
     if(choix_resizese == "yes" || choix_resizese == "oui"){
         height = $(div_contenaiteur_img).width;
         image_preview.find('img').height(height);
     }
     image_preview.find('img').width(width);
     image_preview.fadeIn();
    }
};

var showLoadFileAbsolutePathFormSBCustumFileInput = function(fileSelector,label_content){
	$(fileSelector).change(function () {
		  var fieldVal = $(this).val();
		  if (fieldVal != undefined || fieldVal != "") {
			  $(label_content).text(fieldVal);
		  }
		});
};

var showLoadFileNameFormSBCustumFileInput = function(fileSelector,label_content){
	$(fileSelector).on("change", function() {
		var names = [];
	    for (var i = 0; i < $(this).get(0).files.length; ++i) {
	        names.push($(this).get(0).files[i].name);
	    }
	    fileName = "";
	    if(names.length>0){
	    	for(var i=0;i<names.length; i++){
	    		fileName = fileName + " "+names[i];
	    		if(i !== names.length-1)
	    			fileName = fileName +";"
	    	}
	    	fileName = names.length + " fichier(s) :  "+fileName;
	    }
//	    console.log(names);
//		  var fileName = $(this).val().split("\\").pop();
		  $(label_content).text(fileName);
		});
};

var  manageTableResponsivity = function(){
    var screenWidth =  window.innerWidth || document.documentElement.clientWidth;
    if(screenWidth <= 768)
        $('table').addClass('table table-responsive');
    else
        $('table').removeClass('table-responsive');
};
function changeTaskStatus(taskId, status, callback){
	$.ajax({
		url :  '/Task/'+taskId+'/Status/?st='+status,
		method : 'get',
		dataType : 'json',
		success : function(res){
			callback(res);
			console.log(res);
		},
		error : function(e){
			console.log(e);
		}
	});
};
function showconfirmDialog(event,element){
	event.preventDefault();
	showConfirmDialog($(element),"#modalConfirmDialog");
}
function showConfirmDialog(triggerElement,dialogElementSelector){
	var dialogElement = $(dialogElementSelector);
	
	if(typeof triggerElement !== typeof undefined && triggerElement.length>0 && typeof dialogElement !== typeof undefined && dialogElement.length>0){
		
		var dialogTitle = triggerElement.attr('data-dialogConfirmTitle');
		if(typeof dialogTitle !== typeof undefined && dialogTitle !== false)
			$(dialogElementSelector + ' .dialogTitle').text(dialogTitle);
		
		var dialogBody =  triggerElement.attr('data-dialogConfirmBody');
		if(typeof dialogBody !== typeof undefined && dialogBody !== false)
			$(dialogElementSelector + ' .dialogBody').text(dialogBody);
		
		var confirmLink = triggerElement.attr('data-dialogConfirmLink');
		if(typeof confirmLink !== typeof undefined && confirmLink !== false){
			$(dialogElementSelector + ' .dialogConfirmBtn').attr("href",confirmLink);
		}else{
			confirmLink = triggerElement.attr('href');
			if(typeof confirmLink !== typeof undefined && confirmLink !== false)
				$(dialogElementSelector + ' .dialogConfirmBtn').attr("href",confirmLink);
			else
				$(dialogElementSelector + ' .dialogConfirmBtn').attr("href","#");
		}
		dialogElement.modal("show");
	}
};
function replaceElement(triggerElementAttr, selectorSymbol){
	$("*["+triggerElementAttr+"]").on('click',function(e){
		cible  = $(selectorSymbol + $(this).attr(triggerElementAttr));
		if(typeof cible !== typeof undefined && cible.length>0){
			$(this).addClass('d-none');
			cible.removeClass('d-none');
		}
	});
};

function getFileFontAwesomeTextClass(filename){
	console.log("File name" + filename);
	if(typeof filename !== typeof undefined && filename !== null && filename.length>0) {
		var ext = "";
		try { ext = filename.substring(filename.lastIndexOf("."), filename.length).toLowerCase();} catch (e) {}
		ext = (ext != null && typeof ext !== typeof undefined) ? ext : "";
		console.log("ext name" + ext);
		if(ext == ".xls" || ext == ".xlsx")
			return "fas fa-file-excel";
		else if(ext == ".csv" || ext == ".csvx")
			return "fas fa-file-csv";
		else if(ext == ".doc" || ext == ".docx")
			return "fas fa-file-word";
		else if(ext == ".ppt" || ext == ".pptx")
			return "fas fa-file-powerpoint";
		else if(ext == ".png" || ext == ".jpg" || ext == ".jpeg"  || ext == ".ico"
				|| ext == ".gif" || ext == ".svg"  || ext == ".svgz"  || ext == ".bmp")
			return "fas fa-file-image";
		else if(ext == ".pdf")
			return "fas fa-file-pdf";
		else if(ext == ".text" || ext == ".txt")
			return "fas fa-file";
		else
			return "fas fa-file";
	}
	return "";
};

function loadTaskValidationModalData(taskId){
	$.ajax({
		url : '/Task/getTaskValidationFiles',
	    method :'get',
	    data : {tid : taskId},
	    success : function(task){
	    	if(task != null){
	    		$("#modalTaskValidationFileTitle").text("Fichier(s) de validation de la tâche :  "+task.name);
	    		$("#validationFormtaskId").val(task.id);
	    		showTaksValidationFilesOnModal("#validationFilesMainRow",task.validationFiles);
				$("#modalTaskValidationFile").modal('show');
	    	}
	    },
	    error : function(e){
	    	console.log(e);
	    }
	}) ;
};

function submitTaskValidationFileForm(form){
   var taskid = $("#validationFormtaskId").val();
   var form = $("#taskValidationFileForm")[0];
   var data = new FormData(form);
   $.ajax({
       type: "POST",
       enctype: 'multipart/form-data',
       url: "/Task/StoreValidationFiles",
       data: data,

       // prevent jQuery from automatically transforming the data into a query string
       processData: false,
       contentType: false,
       cache: false,
       timeout: 1000000,
       dataType : 'json',
       beforeSend : function(){
    	 $(".validationLaodingImage").removeClass('d-none');  
       },
       success: function(result) {
       	if(result.status == true){
				 $("#taskValidationFileBtnShower").removeClass('d-none');
				 $(".modalErrorContainer").removeClass('text-danger').addClass("text-success").text(result.msg);
				 showTaksValidationFilesOnModal("#validationFilesMainRow",result.files);
		    	 $(".validationLaodingImage").addClass('d-none');  
       	}else{
       		 $("#taskValidationFileBtnShower").addClass('d-none');
       		$(".modalErrorContainer").removeClass('text-success').addClass("text-danger").text(result.msg);
	    	 $(".validationLaodingImage").addClass('d-none');  
       	}
       	setInterval(function(){
       		$(".modalErrorContainer").text("");
       	},5000);
       },
       error: function(e) {  
			console.log(e);
       }
	});
};

function showTaksValidationFilesOnModal(containerSelector,files){
	//validationFilesMainRow
	$(containerSelector).html("");
	console.log(files);
	if(files.length>0){
		for(var i=0; i<files.length;i++){
			$(containerSelector).append(
				 '<div  class="col-sm-2 col-md-1 m-1">'+
					'<div class="card shadow p-1">'+
						'<div class="card-body p-0">'+
							'<div class="row no-gutters align-items-center">'+
								'<div class="col mr-2">'+
									'<a target="_blank" title="Ouvrir le fichier'+ files[i].name+'"'+
									' href="/Task/Show/FileDescription/'+files[i].id + '/" target="_blank" class="'+getFileFontAwesomeTextClass(files[i].name)+ ' fa-2x">'+
									 '</a>'+
								'</div>'+
								'<div class="col-auto">'+
									'<a href="#" title="Supprimer" onclick="deleteValidationFile(event,'+files[i].id+');"'+
									'class="fas fa-minus-circle text-danger cursor-pointer text-decoration-none"></a>'+
								'</div>'+
							'</div>'+
						'</div>'+
					'</div>'
		    );
		}
	}
};

function deleteValidationFile(event, fileId){
	event.preventDefault();
	$.ajax({
		url : '/Task/deleteFileValidationFile/',
		method : 'get',
		data : {fid : fileId},
		beforeSend : function(){
	    	 $(".validationLaodingImage").removeClass('d-none');  
		},
		success : function(res){
			console.log(res);
			showTaksValidationFilesOnModal("#validationFilesMainRow",res);
	    	 $(".validationLaodingImage").addClass('d-none');  
		},
		error : function(e){
			console.log(e);
	    	 $(".validationLaodingImage").addClass('d-none');  
		}
	});
};
function deleteDescriptionFile(triggerElement,event){
	 event.preventDefault();
	 taskId = $(triggerElement).attr('data-taskId');
	 fileId = $(triggerElement).attr('data-fileId');
	 filesContainerSelector = $(triggerElement).attr('data-parentSelector');
	 if(typeof taskId !== typeof undefined && typeof fileId !== typeof undefined && typeof filesContainerSelector !== typeof undefined){
		 $.ajax({
	        method: "get",
	        url: "/Task/deleteFile/",
	        data : {fid : fileId},
	        success: function(res) {
	        	$.ajax({
	        		url : '/Task/getTaskValidationFiles',
	        	    method :'get',
	        	    data : {tid : taskId},
	        	    beforeSend : function(){
	        	    	$(".deletFileDescriptionLaodingImage").removeClass('d-none');
	        	    },
	        	    success : function(task){
	        	    	if(task != null){
	        	    		$(filesContainerSelector).html("");
	        	    		var files = task.descriptionsFiles;
	        	    		if(files.length>0){
	        	    			for(var i=0; i<files.length;i++){
	        	    				$(filesContainerSelector).append(
									   		'<div class="col-sm-6 col-md-3 m-1">'+
											'<div class="card shadow p-1">'+
												'<div class="card-body p-0">'+
													'<div class="row no-gutters align-items-center">'+
														'<div class="col mr-2">'+
															'<a href="/Task/Show/FileDescription/'+files[i].id+'" '+
															   'target="_blank" class="'+getFileFontAwesomeTextClass(files[i].name)+' fa-2x" '+
															   'title="visualisez '+files[i].name+'"></a> '+
														'</div>'+
														'<div class="col-auto">'+
															'<a href="#" title="Supprimer" data-parentSelector=".tdFiles'+taskId+'"'+
															   'data-taskId="'+taskId+'" data-fileId="'+files[i].id+'" onClick="deleteDescriptionFile(this,event);"'+
															   'class="fas fa-minus-circle text-danger cursor-pointer text-decoration-none"></a>'+
														'</div>'+
													'</div>'+
												'</div>'+
											'</div>'+
								   		'</div>'
	        	    			    );
	        	    			}
	        	    		}
	        	    	}
	        	    	$(".deletFileDescriptionLaodingImage").addClass('d-none');
	        	    },
	        	    error : function(e){
	        	    	console.log(e);
	        	    	$(".deletFileDescriptionLaodingImage").addClass('d-none');
	        	    }
	        	}) ;
	        },
	        error: function(e) {  
	 			console.log(e);
	        }
		});
	 }
	 //validationFilesMainRow
}; 
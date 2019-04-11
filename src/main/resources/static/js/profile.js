$(document).ready(function(){
	$(".enable-edition").on('click',function(e){
		e.preventDefault();
		$(this).addClass('d-none');
		$(".disable-edition").removeClass("d-none");
		$(".in_out_edition").addClass("d-none");
		$(".in_edition").removeClass('d-none');
	});
	$(".disable-edition").on('click',function(e){
		e.preventDefault();
		$(this).addClass('d-none');
		$(".enable-edition").removeClass("d-none");
		$(".in_out_edition").removeClass("d-none");
		$(".in_edition").addClass('d-none');
	});
	
	$(".file_filed_trigger").on("click",function(e){
		e.preventDefault();
		$("#file").trigger("click");
	});

    
    $('#file').on('change',function(e){
        img_field_trigger_value = $(this).val();
        show_uploaded_img($(this),".user_img",true);
    });
    showLoadFileNameFormSBCustumFileInput("#file",".photo-loader-label");
});
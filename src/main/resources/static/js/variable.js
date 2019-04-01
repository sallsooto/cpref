$(document).ready(function(){
	showLoadFileAbsolutePathFormSBCustumFileInput(".custom-file-input");
	//showLoadFileNameFormSBCustumFileInput(".custom-file-input");
	$('.nbvariableField').click(function() {
		var new_uri = location.href.substring(0, location.href.indexof('Add'));
		console.log();
	    //location.reload();
	});
	
	//	$custom-file-text: (
	//			en: "Browse",
	//			es: "Elegir",
	//			fr:"Parcourir");
});

var showLoadFileAbsolutePathFormSBCustumFileInput = function(costumFileJQSelector){
	$(costumFileJQSelector).change(function () {
		  var fieldVal = $(this).val();
		  if (fieldVal != undefined || fieldVal != "") {
		    $(this).next(".custom-file-label").text(fieldVal);
		  }
		});
};

var showLoadFileNameFormSBCustumFileInput = function(costumFileJQSelector){
	$(costumFileJQSelector).on("change", function() {
		  var fileName = $(this).val().split("\\").pop();
		  $(this).siblings(".custom-file-label").addClass("selected").html(fileName);
		});
};
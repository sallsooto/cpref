$(document).ready(function(){
 $(".svgPointerLink").mshzSvgLineLinkAndPointerDrawing({
	 labelFontSize : 13,
	 labelMarginTop :0,
	 labelMarginLeft:0,
 });
 $(window).resize(function(){
	 //$(".svgPointerLink").mshzSvgLineLinkAndPointerDrawing();
	 location.reload();
 });
 // showing chrono
 var start_actual_time_text =$("#startDateContainer").val();
 var end_actual_time_text=$("#endDateContainer").val();
 showChronos(start_actual_time_text,end_actual_time_text,'.chrono_span');
 setInterval(() => {
	 showChronos(start_actual_time_text,end_actual_time_text,'.chrono_span');
}, 1000);
// $('[data-toggle="tooltip"]').tooltip(
//	        {container:'body', trigger: 'hover', placement:"bottom"});
 
 /** form task controlls **/
	 $("#taskContent").on('mouseenter',function(){
		$(this).css({"opacity" : "1"}); 
	 });
	 
	 $("#taskContent").on('mouseleave',function(){
		$(this).css({"opacity" : "0.7"});
	 });
	chowgroupOrUserSection();
	chowTextOrFileDescription();
	$(".radioSectionWithGroup, .radioSectionWithNotGroup, .radioSectionWithGroupAndusers").on("click",function(){
		chowgroupOrUserSection();
	});
	
	$(".radiodescriptionFileAndText, .radiodescriptionText, .radiodescriptionFile").on("click",function(){
		chowTextOrFileDescription();
	});
	
	var selectedTaskIdFieald = $(".selectedTaskIdFiealdContainer").val();
	if(selectedTaskIdFieald.length>0 && selectedTaskIdFieald > 0)
		laodTaskInView(selectedTaskIdFieald);
	
	$(".taskEntity").on('click',function(){
		var taskId = $(this).attr('data-taskId');
		if(taskId.length>0 && taskId > 0)
			laodTaskInView(taskId);
	});
	$(".photo-loader-label").on('click',function(){
		console.log('ok');
	})
	// end task form controlls
});
function showChronos(start_actual_time_text,end_actual_time_text,chronoContainerSelector){
	var chronoContainer = $(chronoContainerSelector);
	if(typeof start_actual_time_text != typeof undefined && end_actual_time_text.length>0){
		console.log(" start " + start_actual_time_text);
		console.log(" end " +end_actual_time_text);
		var start_actual_time = new Date(start_actual_time_text);
		var end_actual_time = new Date(end_actual_time_text);
		var current_time = new Date();
		var current_seondes = current_time.getSeconds();
		var isFinish = $("#isfinishContent").val();
		var expiredDate = false;
		if(end_actual_time - current_time <0){
			start_actual_time = end_actual_time;
			end_actual_time = current_time;
			expiredDate = true;
		}else{
			start_actual_time = current_time;
		}
	   // var start_actual_time = (start_actual_time-new Date() >0) ? new Date() : start_actual_time;
		var finalText = "";
		var diff = end_actual_time - start_actual_time;
		var diffSeconds = diff/1000;
		var JJ = Math.floor(diffSeconds/86400);
		var HH = Math.floor(diffSeconds/3600);
		var MM = Math.floor(diffSeconds%3600)/60;
		HH = (HH<0)? (HH*(-1)) : HH;
		MM = MM.toFixed(0);
		var formatted = ((Math.abs(HH) < 10)?("0" + Math.abs(HH)):Math.abs(HH)) + "h et " + ((Math.abs(MM) < 10)?("0" + Math.abs(MM)+"mm"):Math.abs(MM) + "mm");
		formatted = formatted + " et " + current_seondes + " S";
		var chono_text = (!expiredDate) ?  "Retante(s)" :  " dépassée(s)";
		if(typeof chronoContainer != typeof undefined && chronoContainer.length>0){
			if(isFinish == true || isFinish=='true'){
				chronoContainer.addClass('badge-dafault').html("<i class='fas fa-check-circle fa-lg text-success'><i/>");
				$(".chrono_span_text").addClass('text-secndary font-weight-bold').text("Traité");
			}else{
				chronoContainer.text(formatted);
				if(expiredDate){
					chronoContainer.removeClass('badge-success').removeClass("badge-info").addClass('badge-danger');
					$(".chrono_span_text").removeClass('text-success').removeClass("text-info").addClass('text-danger').text(chono_text);
				}else{
					chronoContainer.removeClass('badge-danger').removeClass("badge-info").addClass('badge-success');
					$(".chrono_span_text").removeClass('text-danger').removeClass("text-info").addClass('text-success').text(chono_text);
				}
			}
		}
	}else{
		chronoContainer.addClass('badge-dafault').html("<i class='fas fa-stop fa-lg text-danger'><i/>");
		$(".chrono_span_text").addClass('text-secndary font-weight-bold').text("Non démmaré");
	}
}

function drawLogigramme(data){
    flowSVG.draw(SVG('drawing').size(900, 1100));
    flowSVG.config({
        interactive: false,
        showButtons: false,
        connectorLength: 60,
        scrollto: true,
        startText :"Début",
        labelYes : "OK",
        labelNo : "No",
        processWidth :150,
        processHeight:50,
        maxOpacity:1,
    });
    flowSVG.shapes(data);

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-36251023-1']);
  _gaq.push(['_setDomainName', 'jqueryscript.net']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();
};

var static_datas = 
    [
        {
		label: 'knowPolicy',
		type: 'decision',
		text: [
			'Connaissez-vous la politique',
			'de libre accès de la revue?'

		],
		yes: 'hasOAPolicy',
		no: 'checkPolicy'
	}, 
  {
		label: 'hasOAPolicy',
		type: 'decision',
		text: [
			'cvkbjvcbkj'
		],
		yes: 'CCOffered',
		no: 'canWrap'
	}, 
	{
		label: 'CCOffered',
		type: 'decision',
		text: [
            ''
        ],
        yes: 'canComply',
        no:'checkGreen'
	},
    {
        label: 'canComply',
        type: 'finish',
        text: [
            ''
        ],
      links: [
          {
              text: 'application form', 
              url: 'http://www.jqueryscript.net/chart-graph/Simple-SVG-Flow-Chart-Plugin-with-jQuery-flowSVG.html', 
              target: '_blank'
          }
      ],
      tip: {title: 'HEFCE Note',
      text:
      [
          ''
      ]}
    },
	{
		label: 'canWrap',
		type: 'decision',
		text: [
            ''
        ],
        yes: 'checkTimeLimits',
		no: 'doNotComply'
	}, 
    {
        label: 'doNotComply',
        type: 'finish',
        text: [
            ''
        ],
        tip: {title: 'HEFCE Note',
        text:
        [
            ''
        ]}
    },       
    {
		label: 'checkGreen',
		type: 'process',
		text: [
            ''
        ],
		next: 'journalAllows',
	}, 
    {
        label: 'journalAllows',
        type: 'decision',
        text: ['Does the journal allow this?'],
        yes: 'checkTimeLimits',
        no: 'cannotComply',
        orient: {
            yes:'r',
            no: 'b'
        }
        
    },
    {
		label: 'checkTimeLimits',
		type: 'process',
		text: [
            ''
        ],
        next: 'depositInWrap'
    },
    {
        label: 'cannotComply',
        type: 'finish',
        text: [
            ''
        ],
        tip: {title: 'HEFCE Note',
        text:
        [
            ''
        ]}
    },
    {
        label: 'depositInWrap',
        type: 'finish',
        text: [
            ''
        ],
        tip: {title: 'HEFCE Note',
        text:
        [
            ''
        ]}
    },
	{
		label: 'checkPolicy',
		type: 'process',
		text: [
			' '
		],
        links: [
            {
                text: 'SHERPA FACT/ROMEO ', 
                url: 'http://www.jqueryscript.net/chart-graph/Simple-SVG-Flow-Chart-Plugin-with-jQuery-flowSVG.html', 
                target: '_blank'
            }
        ],
		next: 'hasOAPolicy'
    }
];

// docs all config


function init() {
  // Set defaults
    var w = userOpts.w || 180,
        h = userOpts.h || 140,
        arrowColour = userOpts.arrowColour || 'grey',
        shapeStrokeColour = 'rgb(66, 66, 66)',
        lightText = '#fff',
        darkText = 'rgb(51, 51, 51)',
        defaultFontSize = userOpts.defaultFontSize || 12,
        defaults = {
            showButtons: userOpts.showButtons || true,
            minOpacity: userOpts.minOpacity || 0,
            maxOpacity: userOpts.maxOpacity || 1,
            btnBarHeight: userOpts.btnBarHeight || 40,
            btnBarSelectedColour: userOpts.btnBarSelectedColour || 'black',
            btnBarHoverColour: userOpts.btnBarHoverColour || 'dimgrey',
            btnBarFontSize: userOpts.btnBarFontSize || defaultFontSize,
            btnBarRadius: userOpts.btnBarRadius || 10,
            shapeWidth: userOpts.shapeWidth  || w,
            shapeHeight: userOpts.shapeHeight  || h,
            connectorLength: userOpts.connectorLength || 80,
            startWidth: userOpts.startWidth  || w,
            startHeight: userOpts.startHeight || 40,
            startCornerRadius: userOpts.startCornerRadius || 20,
            startFill:  userOpts.startFill || arrowColour,
            startStrokeWidth: userOpts.startStrokeWidth || 0.1,
            startStrokeColour: userOpts.startStrokeColour || 'rgb(66, 66, 66)',
            startTextColour: userOpts.startTextColour || lightText,
            startText: userOpts.startText || 'Start',
            startFontSize: userOpts.startFontSize || defaultFontSize,
            decisionWidth: userOpts.decisionWidth || userOpts.w || w,
            decisionHeight: userOpts.decisionHeight || userOpts.h || h,
            decisionFill: userOpts.decisionFill || '#8b3572',
            decisionTextColour: userOpts.decisionTextColour || lightText,
            decisionFontSize: userOpts.decisionFontSize || defaultFontSize,
            finishTextColour: userOpts.finishTextColour ||  lightText,
            finishWidth: userOpts.finishWidth  || w,
            finishHeight: userOpts.finishHeight  || h,
            finishLeftMargin: userOpts.finishLeftMargin || 20,
            finishFill: userOpts.finishFill || '#0F6C7E',
            finishFontSize: userOpts.finishFontSize  || defaultFontSize,
            finishLinkColour: userOpts.finishLinkColour || 'yellow',
            processWidth: userOpts.processWidth  || w,
            processHeight: userOpts.processHeight  || h,
            processLeftMargin: userOpts.processLeftMargin || 20,
            processFill: userOpts.processFill || '#fff',
            processStrokeColour: userOpts.processStrokeColour || shapeStrokeColour,
            processStrokeWidth: userOpts.processStrokeWidth || 0.1,
            processTextColour: userOpts.processTextColour || darkText,
            processFontSize: userOpts.processFontSize  || defaultFontSize,
            processLinkColour: userOpts.processLinkColour || 'darkblue',
            labelWidth: userOpts.labelWidth || 30,
            labelHeight: userOpts.labelHeight || 20,
            labelRadius: userOpts.labelRadius || 5,
            labelStroke: userOpts.labelStroke || 0.1,
            labelFill: userOpts.labelFill || arrowColour,
            labelOpacity: userOpts.labelOpacity || 1.0,
            labelFontSize: userOpts.labelFontSize ||  defaultFontSize,
            labelTextColour: userOpts.labelTextColour ||  lightText,
            labelYes: userOpts.labelYes ||  'Yes',
            labelNo: userOpts.labelNo ||  'No',
            labelNudgeRight: userOpts.labelNudgeRight || 0,
            labelNudgeBottom: userOpts.labelNudgeBottom || 0,
            arrowHeadHeight: userOpts.arrowHeadHeight || 20,
            arrowHeadColor: userOpts.arrowHeadColor || arrowColour,
            arrowHeadOpacity: userOpts.arrowHeadOpacity || 1.0,
            connectorStrokeWidth: userOpts.connectorStrokeWidth || 1.5,
            connectorStrokeColour: userOpts.connectorStrokeColour || arrowColour,
            tipStrokeColour: userOpts.tipStrokeColour || shapeStrokeColour,
            tipStrokeWidth: userOpts.tipStrokeWidth || 0.1,
            tipFill: userOpts.tipFill || '#fff',
            tipFontSize: userOpts.tipFontSize || defaultFontSize,
            tipMarginTop: userOpts.tipMarginTop || 10,
            tipMarginLeft: userOpts.tipMarginLeft || 10
        };
    return defaults;
}

/** all form task utils fonctions **/
function laodTaskInView(taskId){
	if(typeof errorMsg === typeof undefined || errorMsg.length<=0)
		errorMsg = "";
	if(typeof successMsg === typeof undefined || successMsg.length<=0 )
		successMsg = "";
	$.ajax({
		url : '/Task/LoadTaskInModel?tid='+taskId,
		method : 'get',
		success : function(res){
			if(res != null){
				$("#taskContent").html(res);
				$("#takmodal").modal('show');
			}
//			$("#taskContent").hide().removeClass('d-none').slideDown();
			console.log(res);
		},
		error : function(e){
			console.log(e);
		}
	});
};

function chowgroupOrUserSection(){
	if($(".radioSectionWithGroup").is(":checked")){
		$(".sectionGroupFormGrouop").removeClass("d-none");
		$(".sectionUserFormGroup").addClass("d-none");
	}
	if($(".radioSectionWithNotGroup").is(":checked")){

		$(".sectionGroupFormGrouop").addClass("d-none");
		$(".sectionUserFormGroup").removeClass("d-none");
	}
	if($(".radioSectionWithGroupAndusers").is(":checked")){
		$(".sectionGroupFormGrouop").removeClass("d-none");
		$(".sectionUserFormGroup").removeClass("d-none");
	}
	
};

function chowTextOrFileDescription(){
	if($(".radiodescriptionText").is(":checked")){
		$(".descriptionTextContainer").removeClass("d-none");
		$(".descriptionFileContainer").addClass("d-none");
	}
	if($(".radiodescriptionFile").is(":checked")){

		$(".descriptionTextContainer").addClass("d-none");
		$(".descriptionFileContainer").removeClass("d-none");
	}
	if($(".radiodescriptionFileAndText").is(":checked")){
		$(".descriptionFileContainer").removeClass("d-none");
		$(".descriptionTextContainer").removeClass("d-none");
	}
	
};
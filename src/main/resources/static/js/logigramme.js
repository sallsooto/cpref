$(document).ready(function(){
 $(".svgPointerLink").mshzSvgLineLinkAndPointerDrawing({
	 labelFontSize : 13,
	 labelMarginTop :10,
	 labelMarginLeft:10,
 });
 $(window).resize(function(){
	 //$(".svgPointerLink").mshzSvgLineLinkAndPointerDrawing();
	 location.reload();
 });
 // showing chrono
 var start_actual_time =$("#startDateContainer").val();
 var end_actual_time=$("#endDateContainer").val();
 if(typeof end_actual_time != typeof undefined && end_actual_time.length>0){
	 showChronos(new Date(),new Date(end_actual_time),'.chrono_span');
	 setInterval(() => {
		 showChronos(new Date(),new Date(end_actual_time),'.chrono_span');
	}, 60000);
 }
});

function showChronos(start_actual_time,end_actual_time,chronoContainerSelector){
		var diff = end_actual_time - start_actual_time;
		var expiredDate = (diff > 0) ? false : true;
		var diffSeconds = diff/1000;
		var HH = Math.floor(diffSeconds/3600);
		var MM = Math.floor(diffSeconds%3600)/60;
		HH = (HH<0)? (HH*(-1)) : HH;
		MM = MM.toFixed(0);
		var formatted = ((HH < 10)?("0" + HH):HH) + "h et " + ((MM < 10)?("0" + MM+"mm"):MM + "mm")
		var chono_text = (!expiredDate) ? formatted + " retante(s)" : formatted + " dépassée(s)";
		var chronoContainer = $(chronoContainerSelector);
		if(typeof chronoContainer != typeof undefined && chronoContainer.length>0){
			chronoContainer.text(chono_text);
			if(expiredDate)
				chronoContainer.removeClass('text-success').addClass('text-danger');
			else
				chronoContainer.removeClass('text-danger').addClass('text-success');
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
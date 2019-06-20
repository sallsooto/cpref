(function($){
	$.extend($.fn,{
		mshzSvgLineLinkAndPointerDrawing : function(options){
		 var params = $.extend({
		 	svgSrcElementAttr : 'data-svgSrcSelector',
		 	svgTargetElementAttr : 'data-svgTargetSelector',
		 	zIndex : 3000,
		 	yesStrokeColor : '#74A767',
		 	noStrokeColor :  'black',
		 	strokeFill : 'none',
		 	widthStroke : '2',
		 	elementfill : 'transparent',
		 	labelClassSelector : 'data-labelClasSelector',
		 	labelFontSize : 15,
		 	labelColor : 'black',
		 	labelTaskCompletedColor : '#AAAAAA',
		 	labelTaskCanceledColor : '#AAAAAA',
		 	labelTaskUnlunchedColor : '#000000',
		 	labelTaskStartedColor : '#2534FF',
		 	labelMarginTop : 15,
		 	labelMarginLeft :15,
		 },options);
		 if(SVG.supported){
			$(this).each(function(){
				// drawing line_link_pointer
				$(this).drawLinkLineAndPoint($(this),params);
				// show elements labels
				$(this).showSvgLabel($(this),params);
			});
		  }else{
		  	alert("SVG est manquant ou non suporté, assurez-vous que votre navigateur est à jour!");
		  }
		},

		drawLinkLineAndPoint : function(svgContainer,params){
			 srcElement= $(svgContainer.attr(params.svgSrcElementAttr));
			 targetElement = $(svgContainer.attr(params.svgTargetElementAttr));
			if(srcElement.length>0 && targetElement.length>0){
				svgContainer.drawPolylineLink(svgContainer,srcElement,targetElement,params);
			}
		},

		drawPolylineLink : function(svgContainer,svgSrc,svgCible,params){
			srcTop = svgSrc.offset().top;
			srcLeft = svgSrc.offset().left;
			srcHeight = svgSrc.innerHeight() || svgSrc.height();
			srcWidth = svgSrc.innerWidth() || svgSrc.width();
			srcSection = svgSrc.attr('data-section');
			targetTop = svgCible.offset().top;
			targetHeight = svgCible.innerHeight() || svgCible.height();
			targetWidth = svgCible.innerWidth() || svgCible.width();
			targetLeft = svgCible.offset().left;
			targetSection = svgCible.attr('data-section');
			// nromalization de dimension
			minTop = (srcTop < targetTop) ? srcTop : targetTop;
			minHeight = (srcTop < targetTop) ? srcHeight : targetHeight;
			minWidth = (srcTop < targetTop) ? srcWidth : targetWidth;
			minLeft = (srcLeft < targetLeft) ? srcLeft : targetLeft;

			maxTop = (srcTop > targetTop) ? srcTop : targetTop;
			maxHeight = (srcTop > targetTop) ? srcHeight : targetHeight;
			maxWidth = (srcTop > targetTop) ? srcWidth : targetWidth;
			maxLeft = (srcLeft > targetLeft) ? srcLeft : targetLeft;
			// changing success elements fill
			svgContainer.changeSvgElementFill(svgContainer,svgSrc,params);
			svgContainer.changeSvgElementFill(svgContainer,svgCible,params);

			polylineStroke = svgContainer.hasAttr(svgCible,'data-yes') ? params.yesStrokeColor : params.noStrokeColor;
			if(svgContainer.length>0){
				svgContainer.css({
					//'border' : '1px solid red',
					'position' : 'absolute',
					'z-index' : params.zIndex-(params.zIndex-3),
					'height' : maxTop-(minTop+(minHeight))+"px",
					'left' : minLeft+'px',
					'top' : minTop+minHeight+'px',
					'width' : ((maxLeft-minLeft))+minWidth+'px',
				});
				var svg = SVG(svgContainer.attr('id'));
			 	if(srcTop != targetTop){
					var orientation = (srcLeft > targetLeft) ? 'inverse' : 'normal';
					polylineMaxTop = (maxTop-(minTop+(minHeight)))-5;
					// vertical line
					if(maxLeft-minLeft <10){
						polylineMaxTop = (maxTop-(minTop+(minHeight)))-5;
						var cords = [];
						cords.push(minWidth/2);
						cords.push(0);
						cords.push(minWidth/2);
						cords.push(polylineMaxTop);
						//pointer cords
						cords.push((minWidth/2)-10);
						cords.push(polylineMaxTop-5);
						cords.push((minWidth/2)+10);
						cords.push(polylineMaxTop-5);
						cords.push((minWidth/2));
						cords.push(polylineMaxTop);
						polyline = svg.polyline(cords);
						polyline.attr({stroke:polylineStroke, 'stroke-width':params.widthStroke, fill : params.strokeFill});
					}else{
						if(orientation == 'inverse'){
							polylineNodeTop = (maxTop-((minTop+(minHeight))+minHeight/2));
							var targetXCenter = ((svgContainer.width()-(svgContainer.width()-(minWidth))))-(minWidth/2);
							var cords = [];
							cords.push(svgContainer.width()-((maxWidth)/2));
							cords.push(0);
							cords.push(svgContainer.width()-((maxWidth)/2));
							cords.push(polylineNodeTop);
							cords.push(targetXCenter);
							cords.push(polylineNodeTop);
							cords.push(targetXCenter);
							cords.push(polylineMaxTop);
							// pointer cords
							cords.push(targetXCenter);
							cords.push(polylineMaxTop);
							cords.push(targetXCenter+5);
							cords.push(polylineMaxTop-5);
							cords.push(targetXCenter-5);
							cords.push(polylineMaxTop-5);
							cords.push(targetXCenter);
							cords.push(polylineMaxTop);
							polyline = svg.polyline(cords);
							polyline.attr({stroke:polylineStroke, 'stroke-width':params.widthStroke, fill : params.strokeFill});
						}else{
							polylineNodeTop = (maxTop-((minTop+(minHeight))+minHeight/2));
							var targetXCenter = ((svgContainer.width()-(maxWidth/2)));
							var cords = [];
							cords.push(minWidth/2);
							cords.push(0);
							cords.push(minWidth/2);
							cords.push(polylineNodeTop);
							cords.push(targetXCenter);
							cords.push(polylineNodeTop);
							cords.push(targetXCenter);
							cords.push(maxTop);
							cords.push(targetXCenter);
							cords.push(polylineMaxTop);
							cords.push(targetXCenter+5);
							cords.push(polylineMaxTop-5);
							cords.push(targetXCenter-5);
							cords.push(polylineMaxTop-5);
							cords.push(targetXCenter);
							cords.push(polylineMaxTop);
							// pointer cords
						    polyline = svg.polyline(cords);
							polyline.attr({stroke:polylineStroke, 'stroke-width':params.widthStroke, fill : params.strokeFill});
						}
					}
				}else{
						// horizontal linelink
						svgContainer.css({
							'height' : (maxHeight)+"px",
							'top': minTop+'px',
						});
						var parantWidth = svgContainer.innerWidth() || svgContainer.width();
						polyLinemaxWidth = parantWidth-minWidth;
						var cords = [];
						cords.push(minWidth);
						cords.push((maxHeight)/2);
						cords.push(polyLinemaxWidth);
						cords.push((maxHeight/2));
						//pointer cords
						cords.push(polyLinemaxWidth);
						cords.push(maxHeight/2);
						cords.push(polyLinemaxWidth-10);
						cords.push(21);
						cords.push(polyLinemaxWidth-10);
						cords.push(29);
						cords.push(polyLinemaxWidth);
						cords.push(25);
						polyline = svg.polyline(cords);
						polyline.attr({stroke:polylineStroke, 'stroke-width':params.widthStroke, fill : params.strokeFill});
				}
			}
		},
		changeSvgElementFill : function(svgElement,parentElement,params){
			$(parentElement).children().each(function(){
				if(svgElement.hasAttr(parentElement,'data-yes'))
					$(this)[0].attributes.fill=params.yesStrokeColor;
				else
					$(this)[0].attributes.fill=params.elementfill;
			});
		},
	    showSvgLabel : function(svgElement,params){
	    	$("svg").each(function(){
	    		var currentElement = $(this);
	    		//console.log(currentElement);
	    		if(svgElement.hasAttr(currentElement, params.labelClassSelector)){
					w = currentElement.innerWidth() || currentElement.width();
					h = currentElement.innerHeight() || currentElement.height();
					eLeft = currentElement.offset().left;
					eTop = currentElement.offset().top;
					labelSector = $(this).attr(params.labelClassSelector);
					fontSize = params.labelFontSize;
					i = h/(fontSize);
					labelsLeft = (w);
					cpt = 0;
					labelMarginTop = params.labelMarginTop;
					$(labelSector).each(function(){
						cpt++;
					});
					$(labelSector).each(function(){
						var taskStatus = ($(this).attr('data-status'));
						color = params.labelColor;
                        if(typeof taskStatus !== typeof undefined && taskStatus.length>0){
                        	if(taskStatus.toLowerCase().trim() == "started")
                        		color = params.labelTaskStartedColor;
                        	else if(taskStatus.toLowerCase().trim() == "completed")
                        		color = params.labelTaskCompletedColor;
                        	else if(taskStatus.toLowerCase().trim() == "canceled")
                        		color = params.labelTaskCanceledColor;
                        	else if(taskStatus.toLowerCase().trim() == "valid")
                        		color = params.labelTaskUnlunchedColor;
                        	else
                        		color = pparams.labelColor;
                        		
                        }
						$(this).parent().css({
							"position" : "absolute",
							"z-index" : params.zIndex+300,
							"left" : eLeft+"px",
							"top" : (eTop)+"px",
							"margin-top" : labelMarginTop,
							"margin-left" : params.labelMarginLeft,
							"font-size" : fontSize+"px",
							"color" : color,
						});
						//console.log($(this).parent());
						i= i+ labelMarginTop;
					});
	    		}
	    	});
		},
		// utils function
		hasAttr : function(element, attrName){
			var attr  = element.attr(attrName);
			return (typeof attr !== typeof undefined && attr !== false && attr !== 'false');
		},
	});
})(jQuery);
/*!
 * SVG Map
 * @version v1.0.2
 * @author  Rocky(rockyuse@163.com)
 * @date    2014-01-16
 *
 * (c) 2012-2013 Rocky, http://sucaijiayuan.com
 * This is licensed under the GNU LGPL, version 2.1 or later.
 * For details, see: http://creativecommons.org/licenses/LGPL/2.1/
 */

;!function (win, $, undefined) {
	var SVGMap = (function () {
		function SVGMap(dom, options) {
			this.dom = dom;
			this.setOptions(options);
			this.render();
		}
		SVGMap.prototype.options = {
			mapName: 'china',
			mapWidth: 500,
			mapHeight: 400,
			stateColorList: ['2770B5', '429DD4', '5AABDA', '1C8DFF', '70B3DD', 'C6E1F4', 'EDF2F6'],
			stateDataAttr: ['stateInitColor', 'stateHoverColor', 'stateSelectedColor', 'baifenbi'],
			stateDataType: 'json',
			stateSettingsXmlPath: '',
			stateData: {},

			strokeWidth: 1,
			strokeColor: 'F9FCFE',

			stateInitColor: 'AAD5FF',
			stateHoverColor: 'feb41c',
			stateSelectedColor: 'E32F02',
			stateDisabledColor: 'eeeeee',

			showTip: true,
			stateTipWidth: 100,
			//stateTipHeight: 50,
			stateTipX: 0,
			stateTipY: -10,
			stateTipHtml: function (stateData, obj) {
				return obj.name;
			},

			hoverCallback: function (stateData, obj) {},
			clickCallback: function (stateData, obj) {},
			external: false
		};

		SVGMap.prototype.setOptions = function (options) {
			if (options == null) {
				options = null;
			}
			this.options = $.extend({}, this.options, options);
			return this;
		};

		SVGMap.prototype.scaleRaphael = function (container, width, height) {
			var wrapper = document.getElementById(container);
			if (!wrapper.style.position) wrapper.style.position = "relative";
			wrapper.style.width = width + "px";
			wrapper.style.height = height + "px";
			wrapper.style.overflow = "hidden";
			var nestedWrapper;
			if (Raphael.type == "VML") {
				wrapper.innerHTML = "<rvml:group style='position : absolute; width: 1000px; height: 1000px; top: 0px; left: 0px' coordsize='1000,1000' class='rvml' id='vmlgroup_" + container + "'><\/rvml:group>";
				nestedWrapper = document.getElementById("vmlgroup_" + container);
			} else {
				wrapper.innerHTML = '<div class="svggroup"></div>';
				nestedWrapper = wrapper.getElementsByClassName("svggroup")[0];
			}
			var paper = new Raphael(nestedWrapper, width, height);
			var vmlDiv;
			if (Raphael.type == "SVG") {
				paper.canvas.setAttribute("viewBox", "0 0 " + width + " " + height);
			} else {
				vmlDiv = wrapper.getElementsByTagName("div")[0];
			}
			paper.changeSize = function (w, h, center, clipping) {
				clipping = !clipping;
				var ratioW = w / width;
				var ratioH = h / height;
				var scale = ratioW < ratioH ? ratioW : ratioH;
				var newHeight = parseInt(height * scale);
				var newWidth = parseInt(width * scale);
				if (Raphael.type == "VML") {
					var txt = document.getElementsByTagName("textpath");
					for (var i in txt) {
						var curr = txt[i];
						if (curr.style) {
							if (!curr._fontSize) {
								var mod = curr.style.font.split("px");
								curr._fontSize = parseInt(mod[0]);
								curr._font = mod[1];
							}
							curr.style.font = curr._fontSize * scale + "px" + curr._font;
						}
					}
					var newSize;
					if (newWidth < newHeight) {
						newSize = newWidth * 1000 / width;
					} else {
						newSize = newHeight * 1000 / height;
					}
					newSize = parseInt(newSize);
					nestedWrapper.style.width = newSize + "px";
					nestedWrapper.style.height = newSize + "px";
					if (clipping) {
						nestedWrapper.style.left = parseInt((w - newWidth) / 2) + "px";
						nestedWrapper.style.top = parseInt((h - newHeight) / 2) + "px";
					}
					vmlDiv.style.overflow = "visible";
				}
				if (clipping) {
					newWidth = w;
					newHeight = h;
				}
				wrapper.style.width = newWidth + "px";
				wrapper.style.height = newHeight + "px";
				paper.setSize(newWidth, newHeight);
				if (center) {
					wrapper.style.position = "absolute";
					wrapper.style.left = parseInt((w - newWidth) / 2) + "px";
					wrapper.style.top = parseInt((h - newHeight) / 2) + "px";
				}
			};
			paper.scaleAll = function (amount) {
				paper.changeSize(width * amount, height * amount);
			};
			paper.changeSize(width, height);
			paper.w = width;
			paper.h = height;
			return paper;
		};

		SVGMap.prototype.render = function () {
			var opt = this.options,
				_self = this.dom,
				mapName = opt.mapName,
				mapConfig = eval(mapName + 'MapConfig');

			var stateData = {};

			if (opt.stateDataType == 'xml') {
				var mapSettings = opt.stateSettingsXmlPath;
				$.ajax({
					type: 'GET',
					url: mapSettings,
					async: false,
					dataType: $.browser.msie ? 'text' : 'xml',
					success: function (data) {
						var xml;
						if ($.browser.msie) {
							xml = new ActiveXObject('Microsoft.XMLDOM');
							xml.async = false;
							xml.loadXML(data);
						} else {
							xml = data;
						}
						var $xml = $(xml);
						$xml.find('stateData').each(function (i) {
							var $node = $(this),
								stateName = $node.attr('stateName');

							stateData[stateName] = {};
							// var attrs = $node[0].attributes;
							// alert(attrs);
							// for(var i in attrs){
							//     stateData[stateName][attrs[i].name] = attrs[i].value;
							// }
							for (var i = 0, len = opt.stateDataAttr.length; i < len; i++) {
								stateData[stateName][opt.stateDataAttr[i]] = $node.attr(opt.stateDataAttr[i]);
							}
						});
					}
				});
			} else {
				stateData = opt.stateData;
			};

			var offsetXY = function (e) {
					var mouseX, mouseY, tipWidth = $('.stateTip').outerWidth(),
						tipHeight = $('.stateTip').outerHeight();
					if (e && e.pageX) {
						mouseX = e.pageX;
						mouseY = e.pageY;
					} else {
						mouseX = event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft;
						mouseY = event.clientY + document.body.scrollTop + document.documentElement.scrollTop;
					}
					mouseX = mouseX - tipWidth / 2 + opt.stateTipX < 0 ? 0 : mouseX - tipWidth / 2 + opt.stateTipX;
					mouseY = mouseY - tipHeight + opt.stateTipY < 0 ? mouseY - opt.stateTipY : mouseY - tipHeight + opt.stateTipY;
					return [mouseX, mouseY];
				};

			var current, reTimer;

			var r = this.scaleRaphael(_self.attr('id'), mapConfig.width, mapConfig.height),
				attributes = {
					fill: '#' + opt.stateInitColor,
					cursor: 'pointer',
					stroke: '#' + opt.strokeColor,
					'stroke-width': opt.strokeWidth,
					'stroke-linejoin': 'round'
				};

			var stateColor = {};

			for (var state in mapConfig.shapes) {
				var thisStateData = stateData[state],
					initColor = '#' + (thisStateData && opt.stateColorList[thisStateData.stateInitColor] || opt.stateInitColor),
					hoverColor = '#' + (thisStateData && thisStateData.stateHoverColor || opt.stateHoverColor),
					selectedColor = '#' + (thisStateData && thisStateData.stateSelectedColor || opt.stateSelectedColor),
					disabledColor = '#' + (thisStateData && thisStateData.stateDisabledColor || opt.stateDisabledColor);

				stateColor[state] = {};

				stateColor[state].initColor = initColor;
				stateColor[state].hoverColor = hoverColor;
				stateColor[state].selectedColor = selectedColor;

				var obj = r.path(mapConfig['shapes'][state]);
				obj.id = state;
				obj.name = mapConfig['names'][state];
				obj.attr(attributes);

				if (opt.external) {
					opt.external[obj.id] = obj;
				}

				if (stateData[state] && stateData[state].diabled) {
					obj.attr({
						fill: disabledColor,
						cursor: 'default'
					});
				} else {
					obj.attr({
						fill: initColor
					});

					obj.hover(function (e) {
						if (this != current) {
							this.animate({
								fill: stateColor[this.id].hoverColor
							}, 250);
						}
						if (opt.showTip) {
							clearTimeout(reTimer);
							if ($('.stateTip').length == 0) {
								$(document.body).append('<div class="stateTip"></div');
							}
							$('.stateTip').html(opt.stateTipHtml(stateData, this));
							var _offsetXY = new offsetXY(e);

							$('.stateTip').css({
								width: opt.stateTipWidth || 'auto',
								height: opt.stateTipHeight || 'auto',
								left: _offsetXY[0],
								top: _offsetXY[1]
							}).show();
						}

						opt.hoverCallback(stateData, this);
					});

					obj.mouseout(function () {
						if (this != current) {
							this.animate({
								fill: stateColor[this.id].initColor
							}, 250);
						}
						// $('.stateTip').hide();
						if (opt.showTip) {
							reTimer = setTimeout(function () {
								$('.stateTip').remove();
							}, 100);
						}
					});

					obj.mouseup(function (e) {
						if (current) {
							current.animate({
								fill: stateColor[current.id].initColor
							}, 250);
						}

						this.animate({
							fill: stateColor[this.id].selectedColor
						}, 250);

						current = this;
						opt.clickCallback(stateData, this);
					});
				}
				r.changeSize(opt.mapWidth, opt.mapHeight, false, false);
			}
			document.body.onmousemove = function (e) {
				var _offsetXY = new offsetXY(e);
				$('.stateTip').css({
					left: _offsetXY[0],
					top: _offsetXY[1]
				});
			};
		}
		return SVGMap;
	})();

	$.fn.SVGMap = function (opts) {
		var $this = $(this),
			data = $this.data();

		if (data.SVGMap) {
			delete data.SVGMap;
		}
		if (opts !== false) {
			data.SVGMap = new SVGMap($this, opts);
		}
		return data.SVGMap;
	};
}(window, jQuery);
UIMaster.ui.map=UIMaster.extend(UIMaster.ui,{
    initialize:false,
    data:null, 
	clickevent:null,
	stateColorList: ['003399', '0058B0', '0071E1', '1C8DFF', '51A8FF', '82C0FF', 'AAD5ee', 'AAD5FF'],
    init:function() {
	    if (this.initialize) {return;}
		this.initialize=true;
	    this.data = eval('(' +$($(this).children()[0]).text() + ')'); 
		this.clickevent = $(this).attr("event");
	    var mapControl = $(this).children()[1];
	    var i = 1;
		for(k in this.data){
			if(i <= 12){
				var _cls = i < 4 ? 'active' : ''; 
				$($(mapControl).children()[0]).append('<li name="'+k+'"><div class="uimster_mapInfo"><i class="'+_cls+'">'+(i++)+'</i><span>'+chinaMapConfig.names[k]+'</span><b>('+this.data[k].value+')</b></div></li>');
			}else if(i <= 24){
				$($(mapControl).children()[1]).append('<li name="'+k+'"><div class="uimster_mapInfo"><i>'+(i++)+'</i><span>'+chinaMapConfig.names[k]+'</span><b>('+this.data[k].value+')</b></div></li>');
			}else{
				$($(mapControl).children()[2]).append('<li name="'+k+'"><div class="uimster_mapInfo"><i>'+(i++)+'</i><span>'+chinaMapConfig.names[k]+'</span><b>('+this.data[k].value+')</b></div></li>');
			}
		}

		var mapObj_1 = {};
		var othis = this;
		$($(this).children()[2]).SVGMap({
			external: mapObj_1,
			mapName: 'china',
			mapWidth: 600,
			mapHeight: 500,
			stateData: this.data,
			// stateTipWidth: 118,
			// stateTipHeight: 47,
			// stateTipX: 2,
			// stateTipY: 0,
			stateTipHtml: function (mapData, obj) {
			    if (mapData[obj.id] == undefined) {
				   return;
				}
				var _value = mapData[obj.id].value;
				var _idx = mapData[obj.id].index;
				var active = '';
				_idx < 4 ? active = 'active' : active = '';
				var tipStr = '<div class="uimster_mapInfo"><i class="' + active + '">' + _idx + '</i><span>' + obj.name + '</span><b>(' + _value + ')</b></div>';
				return tipStr;
			},
			clickCallback: function(mapData, obj) {
			  UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(othis),_valueName:"selectedNode",
								  _value:obj.id,_framePrefix:UIMaster.getFramePrefix(othis)});
			  eval(othis.clickevent);
			}
		});
		var stateTip = $('<div id="StateTip" class="uimaster_map_statetip"></div');
		var lis = $(mapControl).find('li');
		lis.hover(function () {
			var thisName = $(this).attr('name');
			var thisHtml = $(this).html();
			$(lis).removeClass('select');
			$(this).addClass('select');
			$(document.body).append();
			$(stateTip).css({
				left: ($(mapObj_1[thisName].node).offset().left - 50),
				top: ($(mapObj_1[thisName].node).offset().top - 40)
			}).html(thisHtml).show();
			mapObj_1[thisName].attr({fill: '#E99A4D'});
		}, function () {
			var thisName = $(this).attr('name');

			$(stateTip).remove();
			$(lis).removeClass('select');
			mapObj_1[$(this).attr('name')].attr({
				fill: "#" + othis.stateColorList[othis.data[$(this).attr('name')].stateInitColor]
			});
		});
		
		$($(this).children()[3]).show();
    }
});
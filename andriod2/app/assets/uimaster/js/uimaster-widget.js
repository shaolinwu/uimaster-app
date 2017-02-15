function checkUIMasterReady(){
   return true;
}
function g(t,v){
    return UIMaster.browser.mozilla?t.getAttribute(v):$(t).attr(v);
}
/**
 * @description UI base class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui} An UIMaster.ui element.
 * @class UIMaster UI object.
 * @constructor
 */
UIMaster.ui = function(conf){
    conf = conf || {};
    UIMaster.apply(this, conf);
    UIMaster.apply(this.ui, this,true);
    return this.ui;
};
UIMaster.ui = UIMaster.extend(UIMaster.ui, /** @lends UIMaster.ui */{
    /**
     * @description UI object, a standard HTML element.
     */
    ui: null,
	initialized: false,
    /**
     * @description Set the widget's visible.
     * @param {Boolean} v
     */
    setVisible: function(v){
        v ? $(this.parentDiv).show() : $(this.parentDiv).hide();
    },
    /**
     * @description Enable the widget.
     * @param {Boolean} v
     */
    setEnabled: function(v){
        this.disabled = !v;
        this.parentDiv && (this.parentDiv.disabled = !v);
        if (v) {
            this.grayWidgetLabelStyle(this.name, false);
        } else {
            this.grayWidgetLabelStyle(this.name, true);
        }
    },
    /**
     * @description Set the widget's label text.
     * @param {String} text Label text.
     */
    setLabelText: function(text){
        var n = this.previousSibling ? (this.previousSibling.nodeType == 1?this.previousSibling:this.previousSibling.previousSibling):null;
        n ? n.childNodes[0].nodeValue = text : $(this).before($('<label></label>').attr({'id':this.name+'_widgetLabel','for':this.name}).addClass("uimaster_widgetLabel").css('display','block').text(text));
    },
    /**
     * @description Add a listener to the widget.
     * @param {String} E Event name.
     * @param {Function} fn Event handler.
     */
    addListener: function(E, fn){
        $(this).bind(E,fn);
    },
    /**
     * @description Remove a listener from the widget.
     * @param {String} E Event name.
     * @param {Function} fn Event handler.
     */
    removeListener: function(E, fn){
        $(this).unbind(E,fn);
    },
    init: function(){
        this.parentDiv = this.parentNode;
    },
    /**
     * @description light/gray the widget label and icon
     */
    grayWidgetLabelStyle: function(name, gray){
        var el = document.getElementById(name) || document.getElementsByName(name)[0];
        if (el) {
            var img = RESOURCE_CONTEXTPATH + USER_CONSTRAINT_IMG;
            if (gray) {
                img = img.substring(0, img.indexOf('.')) + '_gray.gif';
                $(el).parent().find('label[class*=uimaster_widgetLabel]').addClass('uimaster_widgetLabel_gray')
                    .end().find('img[src$="uimaster_constraint.gif"]').attr('src', img);
            } else {
                $(el).parent().find('label[class*=uimaster_widgetLabel]').removeClass('uimaster_widgetLabel_gray')
                    .end().find('img[src$="uimaster_constraint_gray.gif"]').attr('src', img);
            }
        }
    },
	addAttr: function(a){
		$(this).attr(a.name, a.value);
	},
	removeAttr: function(name){
	    $(this).removeAttr(name);
	}
});
/**
 * @description Synchronize the data to the server.
 */
UIMaster.ui.sync = function(){
    var escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,
         meta = {'\b': '\\b','\t': '\\t','\n': '\\n','\f': '\\f','\r': '\\r','"' : '\\"','\\': '\\\\'},r,i,p=[],v;
    function getR(frame){
        var rl=[],i;
        for (i=0;i<frame.frames.length;i++){
		    try {//cross domain will fail.
            if (frame.frames[i].UIMaster){
                rl=rl.concat(frame.frames[i].UIMaster.syncList);
                frame.frames[i].UIMaster.syncList=[];
            }
            rl=rl.concat(getR(frame.frames[i]));
			} catch(e){console.log(e);}
        }
        return rl;
    }
    function quote(string) {
        escapable.lastIndex = 0;
        return escapable.test(string) ? '"' + string.replace(escapable, function (a) {
                return typeof meta[a] === 'string' ? meta[a] : '\\u' + ('0000' + a.charCodeAt(0).toString(16)).slice(-4);
            }) + '"' : '"' + string + '"';
    }
    function str(v){
        var k,a=[];
        for (k in v)
            a.push(quote(k)+':'+quote(typeof v[k] == "string" ? v[k] :String(v[k])));
        return '{'+a.join(',')+'}';
    }
    try{
        r = window.top.UIMaster ? window.top.UIMaster.syncList : [];
        window.top.UIMaster && (window.top.UIMaster.syncList = []);
    }catch(e){r = [];};
    var set = getR(window.top);
    r = r.length ? r.concat(set) : (set.length ? set.concat(r) : []);
    for (i = 0; i < r.length; i++)
        p[i] = str(r[i]) || 'null';
    v = p.length === 0 ? '[]' : '[' + p.join(',') + ']';
    return v;
};
/**
 * @description Set the data to the synchronized queue.
 * @param {Object} data Data to be synchronized.
 */
UIMaster.ui.sync.set = function(data){
    UIMaster.syncList.push(data);
};
var WORKFLOW_COMFORMATION_MSG="\u7EE7\u7EED\u672C\u64CD\u4F5C\u5417\uFF1F";
UIMaster.workflowActionPanel = null;
function postInit(){
    while(UIMaster.initList.length > 0) {
        var root = UIMaster.initList.shift();
		var items = root.Form.items;
		for (var i = 0; i < items.length; i++) {if (items[i]) {items[i].postInit && items[i].postInit();}}
		root.user_constructor();
	}
	if (UIMaster.workflowActionPanel != null) {
		UIMaster.workflowActionPanel();
		UIMaster.workflowActionPanel = null;
	}
	while(UIMaster.pageInitFunctions.length > 0) {
        var func = UIMaster.pageInitFunctions.shift();
		func();
	}
	//$(window).scrollTop(0);
};
/**
 * @description UI Field class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.field} An UIMaster.ui.field element.
 * @class UIMaster UI Field.
 * @extends UIMaster.ui
 * @constructor
 */
UIMaster.ui.field = UIMaster.extend(UIMaster.ui, /** @lends UIMaster.ui.field */{
    flag: false,
    validate: null,
    oldInvalidF: false,
    validator: null,
    //private
    validators: null,
    invalidText: null,
    /**
     * @description Disable the validation for this field.
     */
    disableValid: function(){
        this.flag = null;
        clearConstraint(this.name);
    },
    /**
     * @description Enable the validation for this field.
     */
    enableValid: function(){
        this.flag = false;
    },
    /**
     * @description Mark this field as invalid.
     */
    markInvalid: function(){
        constraint(this.name, this.invalidText);
    },
    /**
     * @description Clear this field's invalid mark.
     */
    clearInvalid: function(){
        clearConstraint(this.name);
    },
    /**
     * @description Remove the asterisk mark for this widget.
     */
    removeIndicator: function(){
        $('#' + this.name + 'div').remove();
    },
    /**
     * @description Mark this field as required.
     */
    markRequired: function(){
        setRequiredStyle(UIMaster.getUIID(this));
    },
    /**
     * @description Add a validator for this field.
     * @param {Function} fn Validator function.
     * @param {String} message Error message if the validator fails.
     */
    addValidator:function(fn,message){
        this.validators.push({func:fn,msg:message});
    },
    /**
     * @description Synchronize the field's value.
     */
    sync: function(){
        this.notifyChange(this);
    },
    //private
    validateEvent: function(evnt){
        var obj = UIMaster.getObject(evnt);
        return obj.validate ? obj.validate() : false;
    },
    validateCustConstraint: function(v){
        var r;
        if (v.param) {
            var arr = '', i;
            for (i = 0; i < v.param.length; i++)
                arr += (i == 0 ? '' : ',') + 'v.param[' + i + ']'
            r = new Function('v','c','return v.func.call(c,'+arr+');')(v,this);
        }
        else
            r = v.func.call(this);
        if(typeof r == "string"){
            v.msg = r;
            return !r;
        }
        return r;
    },
    init: function(){
        this.parentDiv = this.parentNode;
        this.addListener('blur', this.validateEvent);
        this.validators = this.validators || [];
        if (this.validator)
            this.validators.push(this.validator);
    }
});
/**
 * @description Textfield class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.textfield} An UIMaster.ui.textfield element.
 * @class UIMaster UI Textfield.
 * @extends UIMaster.ui.field
 * @constructor
 */
UIMaster.ui.textfield = UIMaster.extend(UIMaster.ui.field, /** @lends UIMaster.ui.textfield */{
    /**
     * @description The field is required or not. If required, this field's value should be false.
     * @type Boolean
     * @default true - This field is not required.
     */
    allowBlank: true,
    /**
     * @description Text message for the mandatory validation.
     * @type String
     */
    allowBlankText: '',
    /**
     * @description Regular expression pattern for the field.
     * @type RegExp
     * @default null - No pattern for this field.
     */
    regex: null,
    /**
     * @description Text message for the pattern validation.
     * @type String
     */
    regexText: '',
    /**
     * @description Minimum length required for this field.
     * @type Number
     * @default 0 - No entry is allowed.
     */
    minLength: 0,
    /**
     * @description Maximum length allowed for this field.
     * @type Number
     * @default -1 - No limitation.
     */
    maxLength: -1,
    /**
     * @description Text message for the minimum length validation.
     * @type String
     */
    lengthText: '',
    //private
    needErrMsg : true,
    /**
     * @description Set the textfield's value.
     * @param {String} v Value to set.
     */
    setValue: function(v){
        this.value = v;
        this.notifyChange(this);
    },
    /**
     * @description Get the field's value.
     * @returns {String} Field's value.
     */
    getValue: function(){
        return this.value;
    },
    checkMaxLength: function(e){
        if((this.maxLength>0)&&(this.value.length>=this.maxLength)){
            var keyCode = window.event ? e.keyCode : e.which;
            return ((keyCode == 8)||((keyCode >= 35) && (keyCode <= 40))||(keyCode == 46))
        }
        return true;
    },
    checkMaxAfterInput:function(e){
        if(this.maxLength>0 && this.value.length>this.maxLength){
            this.value=this.value.substring(0,this.maxLength);
        }
    },
    /**
     * @description Validate the field.
     * @returns {Array} An error message array if the validation fails.
     */
    validate: function(init){
        var result = [];
        if (this.flag != null) {
            if (!this.oldInvalidF)
                clearConstraint(this.name);
            if (!this.allowBlank && $.trim(this.value).length < 1) {    //not-blank is the first level check
                result.push(this.allowBlankText);
            } else if (containXSS(this.value) ){    //escape possible executable illegal character
                result.push('Illegal character.');
            } else {    //do the business logic validation
                if ((this.minLength>0)&&(this.value.length<this.minLength))
                    result.push(this.lengthText);
                if ((this.maxLength>0)&&(this.value.length>this.maxLength))
                    result.push(this.lengthText);
                if (this.regex && !this.regex.test(this.ui.value))
                    result.push(this.regexText);
                if (this.validators != null && this.validators.length != 0)
                    for (var i = 0; i < this.validators.length; i++)
                        this.validateCustConstraint(this.validators[i]) || result.push(this.validators[i].msg || '');
            }
            if (result.length > 0){
                if (this.needErrMsg === true)
                    constraint(this.name, result.join(" * "));
                setConstraintStyle(this);
            }
        }
        return result.length > 0 ? result : null;
    },
    /**
     * @description Parse the text to a currency number.
     * @see UIMaster.parseCurrency
     * @param {String} message Error message if the validator fails.
     */
    parseCurrency: function() {
        return UIMaster.parseCurrency(g(this,"locale"),g(this,"currencyformat"),this.value);
    },
    notifyChange: function(e){
        var obj = UIMaster.getObject(e);
        this.needErrMsg = true;
        if (obj._v == obj.value) return;
        if (!(obj.validate ? obj.validate() : false)){
            UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"value",_value:obj.value,_framePrefix:UIMaster.getFramePrefix(obj)});
            obj._v = obj.value;
        }
    },
    init: function(){
        UIMaster.ui.textfield.superclass.init.call(this);
        this.removeListener('blur', this.validateEvent);
        this._v = this.value;
        this.regex && (this.regex = new RegExp(this.regex));
        if (this.ui && !this.defaultBackgroundImage) {
            if (UIMaster.browser.msie)
			   this.defaultBackgroundImage = this.currentStyle.backgroundImage;
			else
			   try{this.defaultBackgroundImage = document.defaultView.getComputedStyle(this, null).getPropertyValue("background-image");}catch(e){}
        }
		var f = this.onblur;
        this.onblur = null;
        $(this).bind('blur', this.notifyChange).bind('keyup', function(e) {
            this.needErrMsg = false;
            //key should not be tab || return || shift || ctrl || alt ||  Caps lock
            if ((e.which==9) || (e.which==13) || (e.which==16) || (e.which==17) || (e.which==18) || (e.which==20))
                return;
            else
                this.validateEvent(e);
        });
        f && $(this).bind('blur', f);

        var v0=g(this, "validationFlag");
        var v1=eval(g(this,"allowBlank"));
        var v2=g(this,"allowBlankText");
        var v3=g(this,"regex");
        var v4=g(this,"regexText");
        var v5=g(this,"minLength");
        var v6=g(this,"lengthText");
        var v7=g(this,"maxLength");

        if(v0)this.flag=null;
        this.allowBlank= v1 == null ? this.allowBlank : v1;
        if(!this.allowBlank && (this.value == ''))
            setConstraintStyle(this);
        if(v2)this.allowBlankText=v2;
        !this.allowBlank && this.allowBlankText === '' && (this.allowBlankText = UIMaster.i18nmsg(C+"||ALLOW_BLANK"));
        if(v3)this.regex=new RegExp(v3);
        if(v4)this.regexText=v4;
        this.regex && this.regexText === '' && (this.regexText = UIMaster.i18nmsg(C+"||REGULAR_EXPRESSION"));
        if(v5)this.minLength=Number(v5);
        if(v6)this.lengthText=v6;
        v5 && this.lengthText === '' && (this.lengthText = UIMaster.i18nmsg(C+"||MINIMUM_LENGTH"));
        $(this).bind('keydown',this.checkMaxLength);
        $(this).bind('keyup',this.checkMaxAfterInput)
        if(v7)this.maxLength=Number(v7);
		if($(this).attr("needamount"))this.amountFunction();
    },
	amountFunction: function(){
		var o = this;
		$($(this).next()).click(function(){
		});
		$($(this).next().next()).click(function(){
		});
	}
});
/**
 * @description Textarea class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.textarea} An UIMaster.ui.textarea element.
 * @class UIMaster UI Textarea.
 * @extends UIMaster.ui.textfield
 * @constructor
 */
UIMaster.ui.textarea = UIMaster.extend(UIMaster.ui.textfield, /** @lends UIMaster.ui.textarea */{
    hiddenToolbar:false,
	persistable:true,
	initialized:false,
	ckeditor:null,
	saveBtn:null,
	height:null,
	maxHeight:false,
	editable:true,
	init:function() {
	    if (this.initialized)
		    return;
		this.initialized = true;
		UIMaster.ui.textarea.superclass.init.call(this);
		if ($(this).attr("htmlsupport")=="true") {
		   this.initHtmlContent();
		}
	},
	addAttr: function(a){
		var v = Base64.decode(a.value);
		if (a.ishtmlcontent) {
		    var opts = null;
			if (this.hiddenToolbar)
			  opts = {uiColor:'#0078AE', toolbar:[], height: this.height};
			else 
			  opts = {uiColor:'#0078AE', height: this.height};

			CKEDITOR.remove(CKEDITOR.instances[this.name+"_ckeditor"]);
			$($(this).next().children()[0]).html(v);
			$($(this).next().children()[1]).remove();
			CKEDITOR.replace(this.name+"_ckeditor", opts);
		    this.ckeditor = CKEDITOR.instances[this.name+"_ckeditor"];
		} else {
			$(this).attr(a.name, v);
		    $(this).text(v);
		}
	},
	initHtmlContent:function() {
	    var o = this;
		if (this.persistable) {
			this.saveBtn = $("<button>\u4FDD\u5B58HTML\u5185\u5BB9</button>");
			$(this).parent().append(this.saveBtn);
			$(this).parent().append($("<div>\u60A8\u53EF\u4EE5\u590D\u5236\u7C98\u8D34\u4EFB\u4F55\u7F51\u9875\u5185\u5BB9\u5230\u4E0A\u9762\u8F93\u5165\u6846\u91CC\u3002</div>"));
			this.saveBtn.click(function(){
			   var data = CKEDITOR.instances[o.name+"_ckeditor"].getData();
			   var opts = {url:AJAX_SERVICE_URL,async:false,type:'POST',
			   data:{_ajaxUserEvent:"htmleditor",_uiid:o.name,_valueName:"save",_value:data,_framePrefix:UIMaster.getFramePrefix(o)}};
			   if (MobileAppMode) {
				  _mobContext.ajax(JSON.stringify(opts));
			   } else {
				  $.ajax(opts);
			   }
			});
		}
		var opts = null;
		if (this.maxHeight) {
			this.height = $(document.body).height() - 200;
			if (this.height <=0) {this.height=$(document.body).height();}
		}
		if (this.hiddenToolbar)
		  opts = {uiColor:'#0078AE', toolbar:[], height: this.height};
		else 
		  opts = {uiColor:'#0078AE', height: this.height};
	    setTimeout(function(){
		  CKEDITOR.replace(o.name+"_ckeditor", opts);
		  o.ckeditor = CKEDITOR.instances[o.name+"_ckeditor"];
		  if (!o.editable) {
			  o.ckeditor.setReadOnly(true);
		  }
		  o.ckeditor.on('blur', function(e){
			o.saveBtn.trigger("click");
		  });
		  //http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.editor.html#event:blur
		},500);
	},
	getHTMLText:function() {
	    return this.ckeditor.getData();
	},
	clearHTMLText:function() {
	    this.ckeditor.updateElement();
	    this.ckeditor.setData("");
	},
	appendHTMLText:function(text) {
	    if (this.ckeditor != null)
	        this.ckeditor.insertHtml(text);
	}
});
UIMaster.ui.countdown = UIMaster.extend(UIMaster.ui.textfield, {
    initialized:false,
    labelsOptions: {lang: {days: "\u5929", hours: "\u65F6", minutes: "\u5206", seconds: "\u79D2"}},
	init: function() {
	    if (this.initialized)
		    return;
		this.initialized = true;
		var v = parseInt($(this).attr("count"));
		$(this).redCountdown({preset: "flat-colors-fat", end: ($.now() + v), 
			labelsOptions: this.labelsOptions, onEndCallback: this.onEndCall});
	},
	onEndCall: function() {
		//alert("Time out!"); 
	}
});
/**
 * @description Calender fiels class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.passwordfield} An UIMaster.ui.passwordfield element.
 * @class UIMaster UI Passwordfield.
 * @extends UIMaster.ui.textfield
 * @constructor
 */
UIMaster.ui.calendar = UIMaster.extend(UIMaster.ui.textfield, /** @lends UIMaster.ui.calendar */{
	isDataOnly: false,
	isBiggerThan: false,
	isSmallerThan: false,
	editableFlag: true,
	dateConstraint: "",
	format: "",
	dateType: "date",
	init: function(){
		UIMaster.ui.calendar.superclass.init.call(this);
		this.selectDate = this.ui.nextElementSibling;
        if (this.className.toLowerCase().indexOf("readonly")>0) {
        	this.editableFlag = false;
        } else {
        	this.editableFlag = true;
        }
        if (this.editableFlag == false) {
            this.disableValid();
            this.selectDate.style.display = "none";
        }
        if (!this.allowBlank)
        {
            this.allowBlank = false;
            if (this.editableFlag == true)
            {
                setRequiredStyle(this.name);
                if (this.value == '') {
                    setConstraintStyle(this);
                }
            }
        }
        this.allowBlank || (this.allowBlankText = UIMaster_getI18NInfo("Common||ALLOW_BLANK"));
        this.date = this.parseDate(this, this.format, this.dateType);
        this.getDate = function(){
            return this.date;
        };
        this.setDate = function(date){
            var dStr = UIMaster_getFormattedDate(this.format, this.dateType, date, '');
            this.value = dStr;
            this.date = date;
            this.validate();
            if(!this.readOnly && !this.disabled && ($(this).css('display') != 'none'))
            	this.focus();
        };
        this.setEditable = function(f){
        	this.editableFlag = f;
            if (f) {
            	this.enableValid();
            	this.selectDate.style.display = "inline";
            	this.validate();
            } else {
            	this.disableValid();
            	this.selectDate.style.display = "none";
                this.clearInvalid();
            }
        };
        this.setValue = function(v){
            if (typeof v == "string"){
            	this.value = v;
            	this.date = this.parseDate(this, this.format, this.dateType);
                this.notifyChange(this);
            }
            else if (typeof v == "date"){
            	this.setDate(v);
            }
        };
        /* Date Constraint and Datepicker callback function */
        this.isDateValid = function(cdate) {
            var date = new Date(cdate.getTime());
            date.setHours(0,0,0,0);
            var dateConstraint = this.dateConstraint, entry, d = date.getDate(), m = date.getMonth()+1;
            for (var i = 0; i < dateConstraint.length; i++) {
                entry = dateConstraint[i];
                if (entry[0] == "R") {
                    var dateFormat3 = /(\d{4})\/(\d{1,2})\/(\d{1,2})/,  result = null,
                    leftRange = entry[1] == "null" || (result = entry[1].match(dateFormat3)) != null && date >= new Date(parseInt(result[1],10), parseInt(result[2],10)-1, parseInt(result[3],10)),
                    rightRange = entry[2] == "null" || (result = entry[2].match(dateFormat3)) != null && date <= new Date(parseInt(result[1],10), parseInt(result[2],10)-1, parseInt(result[3],10));
                    if (leftRange && rightRange) {
                        return false;
                    }
                } else if (entry[0] == "W") {
                    if (date.getDay() == parseInt(entry[1],10)) {
                        return false;
                    }
                } else if (entry[0] == "M") {
                    if ((d >= parseInt(entry[1],10)) && (d <= parseInt(entry[2],10))) {
                        return false;
                    }
                } else if (entry[0] == "Y") {
                    var dateFormat2 = /(\d{1,2})\/(\d{1,2})/, result = null, leftRange = false, rightRange = false;
                    if ((result = entry[1].match(dateFormat2)) != null) {
                        var lm = parseInt(result[1],10), ld = parseInt(result[2],10);
                        leftRange = m > lm || (m == lm && d >= ld);
                    }
                    if ((result = entry[2].match(dateFormat2)) != null) {
                        var rm = parseInt(result[1],10), rd = parseInt(result[2],10);
                        rightRange = m < rm || (m == rm && d <= rd);
                    }
                    if (leftRange && rightRange) {
                        return false;
                    }
                }
            }
            return true;
        };
        this.beforeShowDay = function(date) {
            // if a parent InputDateRange contains this InputDate, delegate call to parent
            if ((this.parentEntity == undefined) || (this.parentEntity.beforeShowDay == undefined)) {
                return [this.isDateValid(date), ''];
            } else {
                return [this.isDateValid(date) && this.parentEntity.beforeShowDay(this, date), ''];
            }
        };
	},
	parseDate: function(src, tformat, datetype){
	    if (src.value.length){
	       // var d = UIMaster_getFormattedDate(tformat, datetype, null, src.value);
	        return isNaN(parseInt(src.value))? null : new Date(parseInt(src.value));
	    } else
	        return null;
	},
	open: function() {
	    if (this.editableFlag) {
	    	LANG == "en" || UIMaster.require("/js/controls/i18n/ui.datepicker-" + LANG + ".js", true);
	        this.showCalendar(this, this.format, this.dateType);
	    }
	},
	showCalendar: function(src, tformat, datetype) {
	    var btn = $(src), date = src.getDate(), todayD = new Date(CURTIME), beforeShowDayFunc = src.beforeShowDay, node = src, dateObj;
	    todayD = new Date(CURTIME+todayD.getTimezoneOffset()*60000+TZOFFSET);
	    $.datepicker.regional[LANG];
	    dateObj = date ? date : todayD;
	    btn.datepicker({
	    	defaultDate: dateObj,
	        dateFormat: "yy-mm-dd",
	        duration: "fast",
	        today:todayD,
	        changeMonth: true,
	        changeYear: true,
	        constrainInput:false,
	        yearRange: '-80:+20'
	    });
	    if (jQuery.datepicker._datepickerShowing && jQuery.datepicker._lastInput == src) 
	       return;
	    jQuery.datepicker._showDatepicker(src);
	},
	notifyChange: function(e){
        var obj = UIMaster.getObject(e);
        this.needErrMsg = true;
        if (obj._v == obj.value) return;
        if (!(obj.validate ? obj.validate() : false)){
            UIMaster.ui.sync.set({_uiid:this.name,_valueName:"value",_value:obj.value,_framePrefix:UIMaster.getFramePrefix(obj)});
            obj._v = obj.value;
        }
    },
	validator: {
		msg: "Calender value format is wrong.",
		func: function() {
			var big = this.isBiggerThan, small = this.isSmallerThan;
			var date = this.parseDate(this, this.format, this.dateType), cDate = new Date(CURTIME);
			//adjust the value according TimeZoneOffset
			cDate = new Date(CURTIME+cDate.getTimezoneOffset()*60000+TZOFFSET);
			if (this.dateType == "date") {
			    cDate.setHours(0,0,0,0);
			}
			if (this.value.length) {
			    if (big && small && (!date || date.getTime() != cDate.getTime()))
			        return UIMaster_getI18NInfo("Common||INPUT_DATE_CONSTRAINT_TODAY");
			    else if (date <= cDate && big)
			        return UIMaster_getI18NInfo("Common||INPUT_DATE_CONSTRAINT_GREATER");
			    else if (date >= cDate && small)
			        return UIMaster_getI18NInfo("Common||INPUT_DATE_CONSTRAINT_SMALLER");
	
			        if ((this.lastInputDateValue == undefined) || (this.lastInputDateValue != this.value)) {
			        this.lastInputDateValue = this.value;
			        //first time or the input value has changed
			        if (!this.allowBlank || !(/^\s*$/.test(this.value))) {
			            //the input is not a legal date
			            if ((date == null) || (this.isDateValid(date) == false)) {
			                this.date = undefined;
			                this.lastInputDateValid = false;
			                return UIMaster_getI18NInfo("Common||INPUT_DATE_CONSTRAINT_INVALID");
			            }
			            if (date != null) {
			                var cYear = cDate.getFullYear(), dYear = date.getFullYear();
			                //the input is legal but the year range exceed (-80,+20) range
			                if ( ( (dYear>=cYear) && (dYear-cYear<=20)) || ( (dYear<cYear) && (dYear-cYear>-80) ) ) {
			                    this.date = date;
			                    this.lastInputDateValid = true;
			                } else {
			                    this.date = undefined;
			                    this.lastInputDateValid = false;
			                    return UIMaster_getI18NInfo("Common||INPUT_DATE_CONSTRAINT_INVALID");
			                }
			            }
			        }
			    } else {
			        // in case of value not changed
			        if (this.lastInputDateValid == false) {
			            return UIMaster_getI18NInfo("Common||INPUT_DATE_CONSTRAINT_INVALID");
			        }
			    }
			}
			else
			{
				this.date = undefined;
			}
			return true;
		}
	}
});
/**
 * @description Password fields class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.passwordfield} An UIMaster.ui.passwordfield element.
 * @class UIMaster UI Passwordfield.
 * @extends UIMaster.ui.textfield
 * @constructor
 */
UIMaster.ui.passwordfield = UIMaster.extend(UIMaster.ui.textfield);
//SingleChoice
/**
 * @description Checkbox class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.checkbox} An UIMaster.ui.checkbox element.
 * @class UIMaster UI Checkbox.
 * @extends UIMaster.ui.field
 * @constructor
 */
UIMaster.ui.checkbox = UIMaster.extend(UIMaster.ui.field, {
    mustCheck: false,
    mustCheckText: '',
    /**
     * @description Set the checkbox's value.
     * @param {Boolean} c Value to set.
     */
    setValue: function(c){
        this.checked = c;
        this.notifyChange(this);
    },
    /**
     * @description Get the checkbox's value.
     * @returns {Boolean} Checkbox's value.
     */
    getValue: function(){
        return this.checked ? this.value : "";
    },
    validate: function(init){
        var result = [];
        if (this.flag != null) {
            if (!this.oldInvalidF)
                clearConstraint(this.name);
            if (this.mustCheck && this.checked != this.mustCheck)
                result.push(this.mustCheckText);
            if (this.validators.length != 0)
                for (var i = 0; i < this.validators.length; i++)
                    this.validateCustConstraint(this.validators[i]) || result.push(this.validators[i].msg || '');
            if (result.length > 0)
                constraint(this.name, result.join(" * "));
        }
        return result.length > 0 ? result : null;
    },
    notifyChange: function(e){
        var obj = UIMaster.getObject(e);
        if(!!obj._v == !!obj.checked)return;
        if (!(obj.validate ? obj.validate() : false)){
            UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selected",_value:obj.checked,_framePrefix:UIMaster.getFramePrefix(obj)});
            obj._v = obj.checked;
        }
    },
    init: function(){
        UIMaster.ui.checkbox.superclass.init.call(this);
        this._v = this.checked;
        var f = this.onclick;
        this.onclick = null;
        $(this).bind('click',this.notifyChange);
        f && $(this).bind('click',f);

        var v1=eval(g(this,"mustCheck"));
        var v2=g(this,"mustCheckText");
        this.mustCheck=v1!=null?v1:this.mustCheck;
        if(v2)this.mustCheckText=v2;
    }
});
/**
 * @description Radiobutton class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.radiobutton} An UIMaster.ui.radiobutton element.
 * @class UIMaster UI Radiobutton.
 * @extends UIMaster.ui.checkbox
 * @constructor
 */
UIMaster.ui.radiobutton = UIMaster.extend(UIMaster.ui.checkbox,{
    notifyChange: function(e){
        var obj = UIMaster.getObject(e);
        if(obj._v == obj.checked)return;
        var uiid = obj.inList?obj.name+"["+obj.value+"]":obj.name;
        if (!(obj.validate ? obj.validate() : false)){
            UIMaster.ui.sync.set({_uiid:uiid,_valueName:"selected",_value:obj.checked,_framePrefix:UIMaster.getFramePrefix(obj)});
            obj._v = obj.checked;
        }
    }
});
//MultiChoice
/**
 * @description Checkboxgroup class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.checkboxgroup} An UIMaster.ui.checkboxgroup element.
 * @class UIMaster UI Checkboxgroup.
 * @extends UIMaster.ui.field
 * @constructor
 */
UIMaster.ui.checkboxgroup = UIMaster.extend(UIMaster.ui.field, /** @lends UIMaster.ui.checkboxgroup */{
    mustCheck: null,
    mustCheckText: '',
    /**
     * @description Enable/disable the checkboxgroup.
     * @param {Boolean} v Value to set.
     */
    setEnabled: function(v){
        if (this.ui.length)
            for (var i = 0; i < this.ui.length; i++)
                v ? this.ui[i].disabled = false : this.ui[i].disabled = true;
    },
	addAttr: function(a){
	    if (a.item) {
		   var id = $(this.p).attr("name");
		   var item = $("<input type=\"checkbox\" id=\""+a.value+"\" name=\""+id+"\" value=\""+a.value
		          +"\" class=\"uimaster_checkboxGroup\"/><label class=\"uimaster_checkout_text_gap\" for=\""+a.value+"\">"+a.name+"</label>");
		   $(this.p).append(item);
		   if (this.ui.length == undefined) {
			   this.ui = [this.ui,item[0]];
		   } else {
		       this.ui.push(item[0]);
		   }
		} else if (a.finish) {
		   this.initialized = false;
		   this.init();
		}
	},
	removeAttr: function(v) {
	   this.clearOptions();
	},
	clearOptions: function(){
	   while(this.ui.length > 0){
	      this.ui.splice(0, 1);
	   }
	   $(this.p).children().each(function(){
	      $(this).remove();
	   });
	   this._v = null;
	},
    /**
     * @description Set the checkboxgroup's value.
     * @param {Array} c Values to set.
     */
    setValue: function(c){
        var arr = c || [];
        if (this.ui.length){
            for (var i = 0; i < arr.length; i++) {
                for (var j = 0; j < this.ui.length; j++)
                    if (this.ui[j].value == arr[i]) {
                        this.ui[j].checked = true;
                        break;
                    }
                    else
                        this.ui[j].checked = false;
            }
        }
        else
            this.checked = c;
        this.notifyChange(this.nodeType?this:this[0]);
    },
    /**
     * @description Get the checkboxgroup's value.
     * @returns {Array} Checkboxgroup's value.
     */
    getValue: function(){
        var arr = [];
        if (this.ui.length)
            for (var i = 0; i < this.ui.length; i++)
                if (this.ui[i].checked)
                    arr.push(this.ui[i].value);
        else
            if(this.checked)
                arr.push(this.value);
        return arr;
    },
    validateEvent: function(evnt){
        var obj = UIMaster.getObject(evnt);
        return eval(D+ obj.name) && eval(D+ obj.name).validate();
    },
    notifyChange: function(e){
        var obj = eval(D + UIMaster.getObject(e).name), v = obj.getValue().join(",")
        if(obj._v == v)return;
        if (!(obj.validate ? obj.validate() : false)){
            UIMaster.ui.sync.set({_uiid:UIMaster.getObject(e).name,_valueName:"values",_value:obj.getValue().join(","),_framePrefix:UIMaster.getFramePrefix(UIMaster.getObject(e))});
            obj._v = v;
        }
    },
    validate: function(init){
        var result = [];
        if (this.ui.length && this.flag != null) {
            if (!this.oldInvalidF)
                clearConstraint(this.nodeType ? this.name : this.ui[0].name);
            if (this.mustCheck)
                if (!UIMaster.arrayContain(this.mustCheck, this.getValue()))
                    result.push(this.mustCheckText);
            if (this.validators.length != 0)
                for (var i = 0; i < this.validators.length; i++)
                    this.validateCustConstraint(this.validators[i]) || result.push(this.validators[i].msg || '');
            if (result.length > 0)
                constraint(this.nodeType ? this.name : this.ui[0].name, result.join(" * "));
        }
        return result.length > 0 ? result : null;
    },
    sync:function(){
        this.notifyChange(this.nodeType?this:this[0]);
    },
    getDefaultValue: function(){
        var arr=[];
        if(this.ui.length)
            for(var i=0;i<this.ui.length;i++)
                if(this.ui[i].defaultSelected)
                    arr.push(this.ui[i].value);
        else
            if(this.defaultSelected)
                arr.push(this.value);
        return arr;
    },
    setLabelText: function(text){
        var n = this.nodeType?this:this.ui[0];
        n.previousSibling ? n.previousSibling.childNodes[0].nodeValue = text : $(n).before($('<label></label>').attr({'id':n.name+'_widgetLabel','for':n.name}).addClass("uimaster_widgetLabel").css('display','block').text(text));
    },
    removeIndicator: function(){
        $('#' + (this.nodeType ? this.name : this.ui[0].name) + 'div').remove();
    },
    addListener: function(E, fn){
        if (this.ui.length)
            for (var i = 0; i < this.ui.length; i++)
                $(this.ui[i]).bind(E,fn);
        else
            $(this).bind(E,fn)
    },
    removeListener: function(E, fn, u){
        if (this.ui.length)
            for (var i = 0; i < this.ui.length; i++)
                $(this.ui[i]).unbind(E,fn);
        else
            $(this).unbind(E,fn);
    },
    initV:function(){
        this._v = this.getDefaultValue().join(",");
    },
    init: function(){
	    if (this.initialized) { return;}
		this.initialized = true;
		if(this.p == undefined)this.p = $(this).parent();
        this.parentDiv = this.p.parent()[0];
        this.initV();
        this.validators = [];
        if (this.validator)
            this.validators.push(this.validator);
        if (this.ui.length)
            for (var i = 0; i < this.ui.length; i++) {
                this.ui[i].parentEntity = this.parentEntity;
                this.ui[i].arrayIndex = i;
                this.ui[i].setVisible = UIMaster.ui.checkboxgroup.superclass.setVisible;
            }
        this.addListener('click', this.notifyChange, false);
        this.addListener('blur', this.validateEvent, false);
        if (this.nodeType){
            var f = this.onclick;
            this.onclick = null;
            f && $(this).bind('click',f);
        }else{
		    if (this.onchangeEvent == null) this.onchangeEvent = this[0].onchange;
            for(var i=0;i<this.ui.length;i++){
                var f = this.ui[i].onclick;
				var fc = this.ui[i].onchange;
                this.ui[i].onclick = null;
                f && $(this.ui[i]).bind('click',f);
				if(fc==null)$(this.ui[i]).bind('change',this.onchangeEvent);
            }
        }

        //var v1=eval(g(this,"mustCheck"));
        //var v2=g(this,"mustCheckText");
        //if(v1)this.mustCheck=(v1=="true");
        //if(v2)this.mustCheckText=v2;
    }
});
/**
 * @description Radiobuttongroup class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.radiobuttongroup} An UIMaster.ui.radiobuttongroup element.
 * @class UIMaster UI Radiobuttongroup.
 * @extends UIMaster.ui.checkboxgroup
 * @constructor
 */
UIMaster.ui.radiobuttongroup = UIMaster.extend(UIMaster.ui.checkboxgroup, /** @lends UIMaster.ui.radiobuttongroup*/{
    addAttr: function(a){
	    if (a.item) {
		   var id = $(this.p).attr("name");
		   var item = $("<input type=\"radio\" id=\""+a.value+"\" name=\""+id+"\" value=\""+a.value
		          +"\" class=\"uimaster_radioButtonGroup\"/><label class=\"uimaster_radio_text_gap\" for=\""+a.value+"\">"+a.name+"</label>");
		   $(this.p).append(item);
           if (this.ui.length == undefined) {
			   this.ui = [this.ui,item[0]];
		   } else {
		       this.ui.push(item[0]);
		   }
		} else if (a.finish) {
		   this.initialized = false;
		   this.init();
		}
	},
	/**
     * @description Set the radiobuttongroup's value.
     * @param {String} c Value to set.
     */
    setValue: function(c){
        if (this.ui.length)
            for (var i = 0; i < this.ui.length; i++)
                this.ui[i].checked = (this.ui[i].value == c ? true : false);
        else
            this.checked = (this.value == c ? true : false);
        this.notifyChange(this.nodeType?this:this.ui[0]);
    },
    /**
     * @description Get the radiobuttongroup's value.
     * @returns {String} Radiobuttongroup's value.
     */
    getValue: function(){
        if (this.ui.length)
            for (var i = 0; i < this.ui.length; i++)
                if (this.ui[i].checked)
                    return arguments.callee.caller == this.validate ? [this.ui[i].value] : this.ui[i].value;
    },
    getDefaultValue: function(){
        if (this.ui.length)
            for (var i = 0; i < this.ui.length; i++)
                if (!!this.ui[i].defaultChecked)
                    return this.ui[i].value;
    },
    notifyChange: function(e){
        var rBtn = UIMaster.getObject(e), obj = eval(D + rBtn.name);
        if(e == undefined || obj._v == obj.getValue())return;
        var uiid = obj.inList?rBtn.name.lastIndexOf(']')!=rBtn.name.length-1 ? rBtn.value == "on"?rBtn.name:rBtn.name+"["+rBtn.value+"]" : rBtn.name:rBtn.name;
        if (!(obj.validate ? obj.validate() : false)){
            UIMaster.ui.sync.set({_uiid:uiid,_valueName:"value",_value:obj.getValue(),_framePrefix:UIMaster.getFramePrefix(rBtn)});
            obj._v = obj.getValue();
        }
    },
    initV:function(){
        this._v = this.getDefaultValue();
    }
});
//SelectComponent
/**
 * @description Combobox class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.combobox} An UIMaster.ui.combobox element.
 * @class UIMaster UI Combobox.
 * @extends UIMaster.ui.field
 * @constructor
 */
UIMaster.ui.combobox = UIMaster.extend(UIMaster.ui.field, /** @lends UIMaster.ui.combobox*/{
    /**
     * @description The field is required or not. If required, this field's value should be false.
     * @type Boolean
     * @default true - This field is not required.
     */
    allowBlank: true,
    /**
     * @description Text message for the mandatory validation.
     * @type String
     */
    allowBlankText:'',
    selectValue: null,
    selectValueText: '',
    /**
     * @description Set the combobox's value.
     * @param {String} v Value to set.
     */
    setValue: function(v,b){
        this.value=v;
		if(b) {
		   for(var i=0;i<this.options.length;i++)
            if(this.options[i].value==v) {
                this.options[i].selected=true;
				break;
			}
		}
        this.notifyChange(this);
    },
    /**
     * @description Get the field's value.
     * @returns {String} Field's value.
     */
    getValue: function(){
        return arguments.callee.caller == this.validate ? [this.value] : this.value;
    },
    getDefaultValue: function(){
        for(var i=0;i<this.options.length;i++)
            if(this.options[i].defaultSelected)
                return this.options[i].value;
    },
	addAttr: function(a){
	    if (a.item) {
		   $(this).append($("<option></option>").attr("value",a.value).text(a.name));
		} else {
		   this.setValue(a.value, true);
		}
	},
	removeAttr: function(v) {
	    if (v != null && v.indexOf("CLEAR_ALL_ITEMS") == -1) {
		    for(var i=0;i<this.options.length;i++)
            if(this.options[i].value==v) {
                $(this.options[i]).remove();
				break;
			}
		} else {
		  this.clearOptions();
		}
	},
	addOption: function(item){
	   $(this).append($("<option></option>").attr("value",item.value).text(item.name));
	},
	clearOptions: function(){
	   $(this).children().remove();
	},
    /**
     * @description Validate the field.
     * @returns {Array} An error message array if the validation fails.
     */
    validate: function(init){
        var result = [];
        if (this.options && this.flag != null) {
            if (!this.oldInvalidF)
                clearConstraint(this.name);
            if (!this.allowBlank && (this.value == "" || this.value == "_NOT_SPECIFIED"))
                result.push(this.allowBlankText);
            if (this.selectValue) {
                var select = this.getValue();
                if (!UIMaster.arrayContain(this.selectValue, select))
                    result.push(this.selectValueText);
            }
            if (this.validators != null && this.validators.length != 0)
                for (var i = 0; i < this.validators.length; i++)
                    this.validateCustConstraint(this.validators[i]) || result.push(this.validators[i].msg || '');
            if (result.length > 0)
                constraint(this.name, result.join(" * "));
        }
		if (this.style) this.style.backgroundColor=result.length>0?CONSTRAINT_BACKGROUNDCOLOR:"#FFFFFF";
		else $(this).parent()[0].style.backgroundColor=result.length>0?CONSTRAINT_BACKGROUNDCOLOR:"#FFFFFF";
        return result.length > 0 ? result : null;
    },
    notifyChange: function(e,t){
        var obj = UIMaster.getObject(e);
        if (!(obj.validate ? obj.validate() : false)||t) {
            UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"value",_value:obj.getValue(),_framePrefix:UIMaster.getFramePrefix(obj)});
        }
    },
    init: function(){
        UIMaster.ui.combobox.superclass.init.call(this);
        var f = this.onchange;
        this.onchange = null;
        $(this).bind('change', this.notifyChange);
        f && $(this).bind('change', f);

        var v1=eval(g(this,"allowBlank"));
        var v2=g(this,"allowBlankText");
        //var v3=g(this,"selectValue");
        //var v4=g(this,"selectValueText");
        this.allowBlank= v1 == null ? this.allowBlank : v1;
        if(v2)this.allowBlankText=v2;
        !this.allowBlank && this.allowBlankText === '' && (this.allowBlankText = UIMaster.i18nmsg(C+"||ALLOW_BLANK"));
        //if(v3)this.selectValue=v3;
        //if(v4)this.selectValueText=v4;

        //begin comboBox tool tip
        $('option', $(this)).each(function(i){
            $(this).attr('title', $(this).text());
        });
        //end comboBox tool tip
    }
});
/**
 * @description List class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.list} An UIMaster.ui.list element.
 * @class UIMaster UI List.
 * @extends UIMaster.ui.combobox
 * @constructor
 */
UIMaster.ui.list = UIMaster.extend(UIMaster.ui.combobox, {
    /**
     * @description Set the list's value.
     * @param {Array} v Values to set.
     */
	syncedValue:null,
    setValue: function(arr){
        for (var i = 0; i < arr.length; i++) {
            for (var j = 0; j < this.options.length; j++)
                if (this.options[j].value == arr[i]) {
                    this.options[j].selected = true;
                    break;
                }
        }
        this.notifyChange(this);
    },
    /**
     * @description Get the list's value.
     * @returns {Array} List's value.
     */
    getValue: function(){
        var arr = [];
        for (var i=0; i < this.options.length; i++)
            if (this.options[i].selected)
                arr.push(this.options[i].value);
        return arr;
    },
    getDefaultValue: function(){
        var arr=[];
        for(var i=0;i<this.options.length;i++)
            if(this.options[i].defaultSelected)
                arr.push(this.options[i].value);
        return arr;
    },
    notifyChange: function(e,t){
        var obj = UIMaster.getObject(e);
        if (!(obj.validate ? obj.validate() : false)||t) {
		    if (this.syncedValue != null) {
			   var i = -1, l = UIMaster.syncList.length;
			   for (var j=0;j<l;j++) {
			     if (UIMaster.syncList[j] == this.syncedValue) {
				    i = j; break;
				 }
			   }
			   if (i > -1) {
			      UIMaster.syncList.splice(i,1);
			   }
			}
		    this.syncedValue = {_uiid:UIMaster.getUIID(obj),_valueName:"values",_value:obj.getValue().join(";"),_framePrefix:UIMaster.getFramePrefix(obj)};    
            UIMaster.ui.sync.set(this.syncedValue);
	    }
    }
});
//ContainerComponent
/**
 * @description Container class. This is the panel container.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.container} An UIMaster.ui.container element.
 * @class UIMaster UI Container.
 * @extends UIMaster.ui.field
 * @constructor
 */
UIMaster.ui.container = UIMaster.extend(UIMaster.ui.field, /** @lends UIMaster.ui.container */{
    /**
     * @description Items in this container.
     * @type Array
     */
    items: null,
    validationList: null,
    flag: true,
    /**
     * @description Set the container's visibility.
     * @param {Boolean} v Visibility to set.
     */
    setVisible: function(v){
        for (var i = 0; i < this.items.length; i++)
            this.items[i].setVisible && this.items[i].setVisible(v);
    },
    /**
     * @description Enable the container.
     */
    enable: function(){
        for (var i = 0; i < this.items.length; i++)
            this.items[i].setEnabled && this.items[i].setEnabled(true);
    },
    /**
     * @description Disable the container.
     */
    disable: function(){
        for (var i = 0; i < this.items.length; i++)
            this.items[i].setEnabled && this.items[i].setEnabled(false);
    },
    /**
     * @description Disable the container's validation.
     */
    disableValid: function(){
        for (var i = 0; i < this.validationList.length; i++)
            this.validationList[i].disableValid && this.validationList[i].disableValid();
    },
    /**
     * @description Enable the container's validation.
     */
    enableValid: function(){
        for (var i = 0; i < this.validationList.length; i++)
            this.validationList[i].enableValid && this.validationList[i].enableValid();
    },
    /**
     * @description Validate the container.
     * @returns {Array} An error message array if the validation fails.
     */
    validate: function(init){
        var result = [];
        if (this.flag != null)
            // clear panel err message only in page-level's validation. this ensure the clearErrMsg execute only one time
            if (this.parentEntity.initPageJs) UIMaster.clearErrMsg();
            for (var i = 0; i < this.validationList.length; i++) {
                var res=null;
                if (init == undefined)
                    res = this.validationList[i].validate();
                // add star to field that must not blank
                if (this.validationList[i].allowBlank==false)
                    setRequiredStyle(this.validationList[i].name);
                if (res != null)
                    for (var obj in this.parentEntity)
                        if (this.parentEntity[obj] == this.validationList[i]) {
                            result.push([obj,res]);
                            // when validation find errs, show them in panel
                            this.validationList[i].parentDiv && UIMaster.appendPanelErr(this.validationList[i].parentDiv.id, res);
                            break;
                        }
            }
        return result.length > 0 ? result : null;
    },
    /**
     * @description Synchronize the values in the container.
     */
    sync:function(){
	    if (this.items == null) return;
        for (var i = 0; i < this.items.length; i++)
            this.items[i].sync && (this.items[i].ui || this.items[i].Form || this.items[i] instanceof Array)&& this.items[i].sync();
    },
    addComponentValidate: function(v){
        if (v instanceof Array)
            for (var i = 0; i < v.length; i++)
                if (v[i].component instanceof Array)
                    for (var j = 0; j < v[i].component.length; j++)
                        v[i].component[j].validators.push(v[i]);
                else
                    this.parentEntity.validators.push(v[i]);
    },
    init: function(){
        UIMaster.ui.container.superclass.init.call(this);
        this.addComponentValidate(this.validator);
        if (!this.validationList) {
            this.validationList = [];
            for (var i = 0; i < this.items.length; i++)
                if (this.items[i] && this.items[i].validate)
                    this.validationList.push(this.items[i]);
        }
        this.removeListener('blur', this.validateEvent, false);
        this.validate(this.flag);
        this.parentEntity.initialized = true;
    }
});
/**
 * @description Panel class. This is the abstract object of UIEntity or UIPage.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.panel} An UIMaster.ui.panel element.
 * @class UIMaster UI Panel.
 * @constructor
 */
UIMaster.ui.panel = function(conf){
    function parseInitPageJs(){
        if (this.initPageJs) {
            UIMaster.ui.mask.open();
            var oIPJS = this.initPageJs, comment, nodes=this.sync?$(this.Form):$(this);
            this.initPageJs = function(){
                if (!jQuery.isReady) jQuery.ready();
				if (IS_MOBILEVIEW && UIMaster.browser.ios) {
					alert("disabled touchmove");
					$(document.body).bind('touchmove', function(e) {e.preventDefault();}, false);
				}
                postInit();
                //comment && UIMaster.cmdHandler(comment);
                oIPJS.call(this);
				if (!IS_MOBILEVIEW) {
                   focusFirstTextField();
				}
            }
            UIMaster.ui.mask.close();
			if (this.parentDiv && $(this.parentDiv).length > 0) {
				if ($(this.parentDiv).is("form")) {
					$(this.parentDiv).css("display","block");
				} else {
					var p = $(this.parentDiv); while(!(p=p.parent()).is("form")){};
					p.css("display","block");
				}
			} else {
			    $(document).find("form").css("display","block");
			}
        }
    }
    if (conf.items && conf.items.length > 0) {
        UIMaster.apply(this, {
            /**
             * @description Container object.
             * @see UIMaster.ui.container
             */
            Form: new UIMaster.ui.container(conf),
            /**
             * @see UIMaster.ui.container.disableValid
             */
            disableValid:function(){
                this.Form.disableValid();
            },
            /**
             * @see UIMaster.ui.container.enableValid
             */
            enableValid:function(){
                this.Form.enableValid();
            },
            /**
             * @see UIMaster.ui.container.disable
             */
            disable:function(){
                this.Form.disable();
            },
            /**
             * @see UIMaster.ui.container.enable
             */
            enable:function(){
                this.Form.enable();
            },
            /**
             * @see UIMaster.ui.container.sync
             */
            sync:function(){
                this.Form.sync();
            },
            /**
             * @description Validate the panel.
             * @returns {Array} An error message array if the validation fails.
             */
            validate: function(){
                var r = this.Form.validate();
                r = r || [];
                var a = [];
                clearConstraint(this.Form.id);
                for (var i=0;i<this.validators.length;i++)
                    if(!this.validators[i].func.call(this))
                        a.push(this.validators[i].msg);
                if(a.length){
                    constraint(this.Form.id, a.join(" * "));
                    if (!this.name.nodeType)
                        r.push([this.name,a.join(" * ")]);
                    else
                        r.push(["panel",a.join(" * ")]);
                }
                return r.length>0?r:null;
            },
            validators: [],
            addComponent: function(c,v,n){
                n && (this[n] = c);
                c.parentEntity = this;
                !c.initialized && c.init();
                this.Form.items.push(c);
                v && c.validate && this.Form.validationList.push(c);
                if (c.allowBlank == false)
                    setRequiredStyle(c.name);
                if (c.subComponents)
                {
                    for (var i = 0; i < c.subComponents.length; i++)
                    {
                        this.addComponent(eval(D+c.subComponents[i]),true);
                    }
                }
            },
            removeComponent: function(c){
            	if (c == undefined)
            		return;
                if (c.subComponents != undefined)
                    for (var i = 0; i < c.subComponents.length; i++)
                        this.removeComponent(eval(D+c.subComponents[i]));
                for(var i in this)
                    if(this[i] == c){
                        delete this[i];
                        for(var k=0;k<this.Form.validationList.length;k++)
                            if(this.Form.validationList[k]==c){
                                this.Form.validationList.splice(k,1);
                                break;
                            }
                        for(var k=0;k<this.Form.items.length;k++)
                            if(this.Form.items[k]==c){
                                this.Form.items.splice(k,1);
                                break;
                            }
                        break;
                    }
            },
            releaseFormObject: function() {
            	var formName = this.Form.id.substring(0,this.Form.id.lastIndexOf('.'));
            	for (i in elementList) {
            		if (i.substr(0,formName.length) == formName) {
            			delete elementList[i];
            		}
            	}
            	//var k=this.Form.items.length;
            	//while(k-->0) {
                    //var item = this.Form.items.splice(k,1)[0];
            	//}
            	this.Form.items = null;
            	delete this.Form;
            },
            /**
             * @see UIMaster.ui.setLabelText
             */
            setLabelText:function(txt){
                this.Form.setLabelText(txt);
            },
            init: function(){
                var ui = this.Form.ui;
                if (ui && !this.name) {
                    var name = (typeof ui.name == "string") ? ui.name : ui.id;
                    if (name) {
                        var n = name.split('.');
                        n.pop();
                        this.name = n.join('.') + '.';
                    }
                    else
                        this.name = "";
                }
                var items = this.Form.items;
                for (var i = 0; i < items.length; i++)
                    if (items[i]) {
                        items[i].parentEntity = this;
                        if (!items[i].initialized)
                            items[i].init && (items[i].ui || items[i].Form || items[i] instanceof Array) && items[i].init();
                    }
                this.Form.parentEntity = this;
                this.parentDiv = this.Form.parentNode;
                if("TitlePanel" == this.Form.uiskin)
                {
                    this.parentDiv=this.parentDiv.parentNode.parentNode.parentNode.parentNode;
                }
                else if("FoldingPanel" == this.Form.uiskin)
                {
                    this.parentDiv=this.parentDiv.parentNode.parentNode.parentNode.parentNode.parentNode;
                }
                this.Form.init();
                this.user_constructor && (defaultname && defaultname.Form || defaultname && defaultname[this.Form.id.split('.')[0]] ? this.user_constructor() : UIMaster.initList.push(this));
                parseInitPageJs.apply(this);
            },
            user_constructor: null
        });
    }else{
        UIMaster.apply(this, conf);
        UIMaster.apply(this, {
            validate:function(){
			   var result = new Array();
			   if (this.subComponents.length > 0) {
			      for (var i = 0; i < this.subComponents.length; i++) {
						var comp = elementList[this.subComponents[i]];
						if (comp && comp.validate) {
							var r = comp.validate();
							if (r != null && r.length > 0) {result.push(r);}
						}
					}
			   }
			   return result.length>0 ?result:null;
			},
            init:function(){
			    if (this.subComponents.length > 0) {
					for (var i = 0; i < this.subComponents.length; i++) {
						var comp = elementList[this.subComponents[i]];
						if (comp) {
							comp.parentEntity = this.parentEntity;
							!comp.initialized && comp.init && comp.init();
						}
					}
				}
                this.parentDiv=this.parentNode;
                this.user_constructor && (defaultname && defaultname.Form || defaultname && defaultname[this.Form.id.split('.')[0]] ? this.user_constructor() : UIMaster.initList.push(this));
                this.initialized=true;
                if("TitlePanUIMaster" == this.uiskin)
                    this.parentDiv=this.parentDiv.parentNode.parentNode.parentNode.parentNode;
                else if("FoldingPanel" == this.uiskin)
                    this.parentDiv=this.parentDiv.parentNode.parentNode.parentNode.parentNode.parentNode;
                parseInitPageJs.apply(this);
				var o = this;
				if (this.id && this.id.lastIndexOf(".actionPanel") != -1) {//workflow action panel.
				   UIMaster.workflowActionPanel = function(){o.buildActionPanel();};//only the outside will be executed.
				}
            },
			buildActionPanel:function(){
				var p = $(this);
				p.children().each(function(){$(this).css("float","left");});
				p.find("input[type=button]").each(function(){if($(this).attr("disabled") == "true" || $(this).attr("disabled") == "disabled" || $(this).css("display") == "none"){$(this).remove();}});
				p.find("div[id$=wfactions]").each(function(){$(this).children().each(function(){$(this).css("float","left")});});
				p.addClass("uimaster_workflow_panel");
				//if(MobileAppMode){$("<div style='height:"+75 +"px;'>a</div>").prependTo(p.parent());}
				//p.enhanceWithin();
				//TODO: postpone to next release! listen the scroll event and move action panel.
			},
			sync:function(){
				if (this.subComponents.length > 0) {
					for (var i = 0; i < this.subComponents.length; i++) {
						var comp = elementList[this.subComponents[i]];
						comp && comp.sync && comp.sync();
					}
				}
			}
        });
        UIMaster.apply(this.ui, this);
        return this.ui;
    }
};

//OtherComponent UI
/**
 * @description UI Button class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.button} An UIMaster.ui.button element.
 * @class UIMaster UI Button.
 * @extends UIMaster.ui
 * @constructor
 */
UIMaster.ui.button = UIMaster.extend(UIMaster.ui,{
    init:function(){
        this.parentDiv = this.parentNode;
    },
	addAttr: function(a){
		if (a.name == "disabled" && a.value == "false") {
		    this.enable();
		} else {
		    $(this).attr(a.name, a.value);
		}
	},
	disable: function(a){
		if (IS_MOBILEVIEW) {
			$(this).parent().addClass("ui-disabled");		
		} else {
			$(this).addClass("ui-disabled");	
		}
	},
	enable: function(a){
		if (IS_MOBILEVIEW) {
			$(this).parent().removeClass("ui-disabled");		
		} else {
			$(this).removeClass("ui-disabled");	
		}
	},
});
/**
 * @description UI Hidden class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.hidden} An UIMaster.ui.hidden element.
 * @class UIMaster UI Hidden.
 * @extends UIMaster.ui
 * @constructor
 */
UIMaster.ui.hidden = UIMaster.extend(UIMaster.ui, /** @lends UIMaster.`*/{
    intialized: false,
    sync:function(){
        if(this.value!=this._v)
            this.notifyChange(this);
    },
    notifyChange:function(e){
        var obj = UIMaster.getObject(e);
        UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"value",_value:obj.value,_framePrefix:UIMaster.getFramePrefix(obj)});
        obj._v = obj.value;
    },
    n:function(e){
        if(e.originalEvent.propertyName=="value" || e.originalEvent.attrName=="value")
            this.notifyChange(e.originalEvent);
    },
    /**
     * @description Set the hidden's value.
     * @param {String} v Value to set.
     */
    setValue:function(v){
        this.value = v;
    },
    init:function(){
	    if (this.intialized)
		    return;
		this.intialized = true;
        $(this).bind('propertychange', this.n).bind('DOMAttrModified',this.n);
        this.parentDiv = this;
        this._v = this.value;
    }
});
/**
 * @description UI Label class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.label} An UIMaster.ui.label element.
 * @class UIMaster UI Label.
 * @extends UIMaster.ui.hidden
 * @constructor
 */
UIMaster.ui.label = UIMaster.extend(UIMaster.ui.hidden, /** @lends UIMaster.ui.label*/{
    captureScreen: false,
	intialized: false,
	/**
     * @description Set the widget's label text.
     * @param {String} text Label text to set.
     */
    setLabelText: function(text){
        var n = this.parentNode.parentNode, k = n.previousSibling ? (n.previousSibling.nodeType == 1?n.previousSibling:n.previousSibling.previousSibling):null;
        k ? k.childNodes[0].nodeValue = text : $(n).before($('<label></label>').attr({'id':this.name+'_widgetLabel','for':this.name}).addClass("uimaster_widgetLabel").css('display','block').text(text));
    },
	addAttr: function(a){
		this.setText(a.value);
	},
    /**
     * @description Set the widget's text.
     * @param {String} text Text to set.
     */
    setText: function(text){
        this.previousSibling?this.previousSibling.nodeValue = text:$(this).before(text);
    },
    init:function(){
	    if (this.intialized)
		    return;
		this.intialized = true;
        UIMaster.ui.label.superclass.init.call(this);
        this.parentDiv = this.parentNode.parentNode.parentNode;
		if (this.captureScreen) {
		    $(this).click(function(){
			    html2canvas(document.body, {
					onrendered: function(canvas) {
						// here is the most important part because 
						// if you don't replace you will get a DOM 18 exception.
                        var image = canvas.toDataURL("image/png").replace("image/png", "image/octet-stream");
						var save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a');
						save_link.href = image;
						save_link.download = "vogerp-capture-"+Math.random()+".png";
					   
						var event = document.createEvent('MouseEvents');
						event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
						save_link.dispatchEvent(event);
					}
				});
			});
		}
    }
});
/**
 * @description UI Link class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.link} An UIMaster.ui.link element.
 * @class UIMaster UI Link.
 * @extends UIMaster.ui.hidden
 * @constructor
 */
UIMaster.ui.link = UIMaster.extend(UIMaster.ui.hidden,{
	init:function(){
        UIMaster.ui.link.superclass.init.call(this);
        this.parentDiv = this.parentNode;
    }
});
UIMaster.ui.frame = UIMaster.extend(UIMaster.ui);
UIMaster.ui.image = UIMaster.extend(UIMaster.ui, {
    height:-1,
	mobheight:-1,
	width:-1,
	mode: 'standard',
	captureScreen: false,
	thumbnails:true,
	thumbnailsFullScreen: true,
	hideThumbnailsOnInit: false,
	slideshow:false,
	slideshowAutostart:false,
	intialized:false,
	enableSelectSync:true,
	isGallery:true,
	init:function(skip){
		var t = this;
		if (skip) {
		  t.init0();
		} else {
		  UIMaster.pageInitFunctions.push(function(){t.init0();});//must be delayed for initialing.
		}
	},
	init0:function(){
	    if (this.intialized)
		    return;
		this.intialized = true;
		if (this.isGallery || this.tagName.toLowerCase() == "div") {
		    var t = this;
			var w = $($(t).parent()).width();
			if (w > 0) {$(t).css("width",w + "px");}//ensure the root width of swiper widget for bug fix.
			$(this).find(".swiper-slide").each(function() {
			  $(this).unbind("click").bind("click",function() {t.clickImage($(this).children()[0]);});
			  if (IS_MOBILEVIEW && t.mobheight != -1){
			     $(this).css("height",t.mobheight);
			  } else if (t.height != -1){
			     $(this).css("height",t.height);
			  }
			});
			$("<div class='swiper-pagination'></div><div class='swiper-button-next'></div><div class='swiper-button-prev'></div>").appendTo($(this));
			if(IS_MOBILEVIEW) {$(this).height(this.mobheight);};
		    var opts = {nextButton: '.swiper-button-next',prevButton: '.swiper-button-prev',spaceBetween:10,
			            pagination: '.swiper-pagination', paginationClickable: true, mousewheelControl: true,
						paginationType: 'fraction', paginationType: 'progress',keyboardControl: true};
			if (this.thumbnails){
			   var thum = $(this).clone().removeAttr("id").removeAttr("style").css("height","50px").css("width","120px").css("scroll","auto");
			   thum.find(".swiper-pagination").remove();
			   thum.find(".swiper-button-next").remove();
			   thum.find(".swiper-button-prev").remove();
			   thum.appendTo($(this).parent());
			   thum.addClass("gallery-thumbs");
			   $(this).addClass("gallery-top");
			   this.galleryTop = new Swiper($(this), opts);
			   this.galleryThumbs = new Swiper(thum, {
					spaceBetween: 1, centeredSlides: true,
					slidesPerView: 'auto', touchRatio: 0.2,
					slideToClickedSlide: true
			   });
			   this.galleryTop.params.control = this.galleryThumbs;
			   this.galleryThumbs.params.control = this.galleryTop;
			} else {
			   this.galleryTop = new Swiper($(this), opts);
			}
		}
		if (this.captureScreen) {
		    $($(this).next()).click(function(){
			    html2canvas(document.body, {
					onrendered: function(canvas) {
						// here is the most important part because 
						// if you don't replace you will get a DOM 18 exception.
                        var image = canvas.toDataURL("image/png").replace("image/png", "image/octet-stream");
						var save_link = document.createElementNS('http://www.w3.org/1999/xhtml', 'a');
						save_link.href = image;
						save_link.download = "vogerp-capture-"+Math.random()+".png";
					   
						var event = document.createEvent('MouseEvents');
						event.initMouseEvent('click', true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
						save_link.dispatchEvent(event);
					}
				});
			});
		}
	},
	clickImage:function(image){
	   if (!this.enableSelectSync) {return;}
	   var opts = {url:AJAX_SERVICE_URL,async:false,success: UIMaster.cmdHandler,data:{_ajaxUserEvent:"false",_uiid:this.id,_valueName:"selectedImage",_value:$(image).attr("src"),_framePrefix:UIMaster.getFramePrefix()}};
       if (MobileAppMode) {
          _mobContext.ajax(JSON.stringify(opts));
       } else {
		  $.ajax(opts);
       }
	},
	createAlbum:function(){
	   var t = this;
	   new UIMaster.ui.dialog({
		  dialogType: UIMaster.ui.dialog.INPUT_DIALOG,
		  message:'Enter a new name: ',
		  messageType:UIMaster.ui.dialog.Information,
		  optionType:UIMaster.ui.dialog.YES_NO_OPTION,
		  title:'',
		  height:150,
		  width:300,
		  handler: function() {
			 if (this.value == null || this.value == "") return;
			 var opts = {url:AJAX_SERVICE_URL,async:false,success: UIMaster.cmdHandler,data:{_ajaxUserEvent:"gallery",_uiid:t.id,_actionName:"createAlbum",_value:this.value,_framePrefix:UIMaster.getFramePrefix()}};
			 if (MobileAppMode) {
				_mobContext.ajax(JSON.stringify(opts));
			 } else {
				$.ajax(opts);
			 }
		  }
	  }).open();
	},
	refresh:function(newContent){
	   if (this.isGallery) {
		   $(this).children().each(function(){$(this).remove()});
		   if (this.thumbnails){
			  $(this).next().children().each(function(){$(this).remove()});
			  $(this).next().remove();
		   }
		   $(decodeHTML(newContent)).appendTo($(this));
		   this.intialized = false;
		   this.init(true);
	   } else {
		   $(this).next().attr("src", RESOURCE_CONTEXTPATH + decodeHTML(newContent) + "?t="+Math.random());
	   }
	}
});
UIMaster.ui.file = UIMaster.extend(UIMaster.ui, {
	disableSearch:false,
    initialized: false,
    callback:null,
	cleanAll:null,
	init:function(){
	    if (this.initialized)
			return;
		this.initialized = true;
		var fileUI = this;
		var actionBtns = IS_MOBILEVIEW? $(this).parent().next().next(): this.nextElementSibling.nextElementSibling;
		var uploadBtn = $(actionBtns).children()[0];
		var cleanBtn = $(actionBtns).children()[1];
		var searchBtn = $(actionBtns).children()[2]; 
		if (IS_MOBILEVIEW) {
			$(uploadBtn).addClass("ui-btn-inline");
			$(cleanBtn).addClass("ui-btn-inline");
			$(searchBtn).addClass("ui-btn-inline");	
		}
		if ($(fileUI).attr("disabled") == "true" || $(fileUI).attr("disabled") == "disabled") {
			$(fileUI).parent().css("display", "none");
			$(actionBtns).css("display", "none");
			return;
		}
		if(this.disableSearch) {$(cleanBtn).css("display","none");$(searchBtn).css("display","none");}
		var progressbox = IS_MOBILEVIEW? $(actionBtns).next(): this.nextElementSibling.nextElementSibling.nextElementSibling;
		var messagebox = IS_MOBILEVIEW? $(progressbox).next(): progressbox.nextElementSibling;
		var c = $(progressbox).children();
		var progressbar = c[0];
		var percent = c[1];
        var tempItems="";
		var options = {
			beforeSend : function() {
				$(progressbox).show();
				// clear everything
				$(progressbar).width('0%');
				$(messagebox).empty();
				$(percent).html("0%").css("background-color","lightblue");
			},
			uploadProgress : function(event, position, total, percentComplete) {
				$(progressbox).width(percentComplete + '%');
				$(percent).html(percentComplete + '%');

				// change message text to red after 50%
				if (percentComplete > 50) {
				$(messagebox).html("<font color='red'>\u4E0A\u4F20\u8FDB\u5EA6</font>");
				}
			},
			success : function() {
			    if (IS_MOBILEVIEW){$(actionBtns).before($(fileUI).parent());}
				else {$(actionBtns).before($(fileUI));}
				$(progressbar).width('100%');
				$(percent).html('100%');
				if (fileUI.callback != null) {
				    fileUI.callback(uploadBtn);
				}
			},
			complete : function(response) {
				$(messagebox).html("<font color='blue'>\u4E0A\u4F20\u6210\u529F!</font>");
			},
			error : function() {
				$(messagebox).html("<font color='red'>\u4E0A\u4F20\u9519\u8BEF!!!</font>");
			}
		};
		$(cleanBtn).click(function() {
		   if (fileUI.cleanAll != null) {
				fileUI.cleanAll(cleanBtn);
			}
		});
		$(searchBtn).click(function() {
		   if (fileUI.onlineSearch != null) {
				fileUI.onlineSearch(searchBtn);
			}
		});
		$(uploadBtn).click(function() {
			var suffix = $(fileUI).attr("suffix");
			if (suffix == "undefined" || suffix == "") {
				alert("Wrong file widget with the empty suffix!");
				return;
			}
			if (fileUI.value=="" || tempItems==fileUI.value) {
				alert("\u8BF7\u9009\u62E9\u4E00\u4E2A\u6587\u4EF6!");
				return;
			}
			var fileName = fileUI.value;
			tempItems = fileName;
			var ldot = fileName.lastIndexOf(".");
			if (ldot == -1) {
				alert("Please choose a file with this suffix: "+suffix);
				return;
			}
			var type = fileName.substring(ldot + 1).toLowerCase();
			if (suffix.toLowerCase().indexOf(type) == -1) {
				alert("Please choose a file with this suffix: "+suffix);
				return;
			}
			
			var _framePrefix=UIMaster.getFramePrefix(UIMaster.El(fileUI).get(0));
			var form = $('<form action='+UPLOAD_CONTEXTPATH+'?_uiid='+fileUI.name+'&_framePrefix='+_framePrefix+' method=post enctype=multipart/form-data></form>');
			//encodeURI(fileUI.uploadName || fileUI.name)
			if (IS_MOBILEVIEW){$(form).append($(fileUI).parent());}
			else {$(form).append($(fileUI));}
			$(form).ajaxSubmit(options);
			return;
		});
	}
});
var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(input){var output="";var chr1,chr2,chr3,enc1,enc2,enc3,enc4;var i=0;input=Base64._utf8_encode(input);while(i<input.length){chr1=input.charCodeAt(i++);chr2=input.charCodeAt(i++);chr3=input.charCodeAt(i++);enc1=chr1>>2;enc2=((chr1&3)<<4)|(chr2>>4);enc3=((chr2&15)<<2)|(chr3>>6);enc4=chr3&63;if(isNaN(chr2)){enc3=enc4=64}else if(isNaN(chr3)){enc4=64}output=output+this._keyStr.charAt(enc1)+this._keyStr.charAt(enc2)+this._keyStr.charAt(enc3)+this._keyStr.charAt(enc4)}return output},decode:function(input){var output="";var chr1,chr2,chr3;var enc1,enc2,enc3,enc4;var i=0;input=input.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(i<input.length){enc1=this._keyStr.indexOf(input.charAt(i++));enc2=this._keyStr.indexOf(input.charAt(i++));enc3=this._keyStr.indexOf(input.charAt(i++));enc4=this._keyStr.indexOf(input.charAt(i++));chr1=(enc1<<2)|(enc2>>4);chr2=((enc2&15)<<4)|(enc3>>2);chr3=((enc3&3)<<6)|enc4;output=output+String.fromCharCode(chr1);if(enc3!=64){output=output+String.fromCharCode(chr2)}if(enc4!=64){output=output+String.fromCharCode(chr3)}}output=Base64._utf8_decode(output);return output},_utf8_encode:function(string){string=string.replace(/\r\n/g,"\n");var utftext="";for(var n=0;n<string.length;n++){var c=string.charCodeAt(n);if(c<128){utftext+=String.fromCharCode(c)}else if((c>127)&&(c<2048)){utftext+=String.fromCharCode((c>>6)|192);utftext+=String.fromCharCode((c&63)|128)}else{utftext+=String.fromCharCode((c>>12)|224);utftext+=String.fromCharCode(((c>>6)&63)|128);utftext+=String.fromCharCode((c&63)|128)}}return utftext},_utf8_decode:function(utftext){var string="";var i=0;var c=c1=c2=c3=0;while(i<utftext.length){c=utftext.charCodeAt(i);if(c<128){string+=String.fromCharCode(c);i++}else if((c>191)&&(c<224)){c2=utftext.charCodeAt(i+1);string+=String.fromCharCode(((c&31)<<6)|(c2&63));i+=2}else{c2=utftext.charCodeAt(i+1);c3=utftext.charCodeAt(i+2);string+=String.fromCharCode(((c&15)<<12)|((c2&63)<<6)|(c3&63));i+=3}}return string}}
function decodeHTML(encodedString){
   return Base64.decode(encodedString);
}
function encodeHTML(html){
   return Base64.encode(html);
}
UIMaster.ui.objectlist = function(conf){
	UIMaster.apply(this, conf);
};
UIMaster.ui.objectlist = UIMaster.extend(UIMaster.ui, {
	initialized:false,
	utype: null,
	initRefreshBody:false,
	loader:null,
	filterPanel:null,
	dtable:null,
	isSingleSelection:false,
	isMultipleSelection:true,
	editablecell:false,
	selectedIndex:-1,
	selectedIndexs:[],
	selectNotify:[],
	columnIds:[],
	appendRowMode:false,
	refreshInterval:0,
	tbody:null,
	tfoot:null,
	disableScrollY:false,
	callSelectedFunc: null,
	initMobileView:function(){
	  var i=0;
	  var othis = this;
	  othis.selectedIndex = 0;
	  $($($(this).prev()).children()[0]).css("display","block");
	  this.filterPanel = $(this).next();
	  this.pageInfoPanel = $(this.filterPanel).next();
	  this.refreshMobList();
	  var loader = $("<div class='swiper-preloader' style='opacity:0;'>Loading ...</div>");
	  loader.appendTo($(this));
	  this.loader = loader;
	  $(window).scroll(function(){
		  if ($(this).scrollTop() == 0) {
			  //console.log("to the top");
		  } else if ($(this).scrollTop()>=($(document).height()-$(window).height())) {
			  //console.log("to the bottom");
			  othis.pullHistory();
		  }
	  });
	},
	pullRefresh:function() {
	  var obj = UIMaster.getObject(this);
	  UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"pull",_value:"filter",_framePrefix:UIMaster.getFramePrefix(obj)});
	  this.showLoader(true);
	  this.loadNewSlides("filter");
    },
	pullNew:function() {
	  var obj = UIMaster.getObject(this);
	  UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"pull",_value:"new",_framePrefix:UIMaster.getFramePrefix(obj)});
	  this.showLoader(true);
	  this.loadNewSlides("new");
    },
	pullHistory:function() {
	  var obj = UIMaster.getObject(this);
	  UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"pull",_value:"history",_framePrefix:UIMaster.getFramePrefix(obj)});
	  this.showLoader(false);
	  this.loadNewSlides("history");
    },
	showLoader: function(isNew){
	 if (isNew) {
	   this.loader.css("top","0px");this.loader.css("left",$(this).position().left + "px");
	 } else {
	   this.loader.css("top",($(this).height() - 200) + "px");this.loader.css("left",$(this).position().left + "px");
	 }
	 this.loader.css("opacity", "1");
	},
	hideLoader:function(){this.loader.css("opacity", "0");},
	loadNewSlides: function(pullaction){
	    var othis = this;
	    var opts = {url:AJAX_SERVICE_URL,async:false,
			data:{_ajaxUserEvent:"table",_uiid:othis.id,_actionName:"pull",_value:pullaction,_framePrefix:UIMaster.getFramePrefix(),_sync: UIMaster.ui.sync()},
			success: function(data){
		     //$($(othis.pageInfoPanel).children()[0]).text("1/"+data.totalCount);
			 if (data.rows && data.rows.length > 0) {
			     if (pullaction == "filter") {
				    $(othis).find(".swiper-slide").each(function() {
						$(this).remove();
					});
				 }
			     for(var i=0;i<data.rows.length;i++){
					 var row = decodeHTML(data.rows[i].value);
					  //Prepend new slide
					  if (pullaction == "new") {
					    othis.prependSlide(row);
					  } else {
					    othis.appendSlide(row);
					  }
				 }
				 othis.refreshMobList();
		     }
			 othis.hideLoader();
		  },
		  error: function(XMLHttpRequest, textStatus, errorThrown) {
		      console.log(errorThrown);
			  othis.hideLoader();
		  }};
		if (MobileAppMode) {
			_mobContext.ajax(JSON.stringify(opts));
		} else {
			$.ajax(opts);
		}
	},
	appendSlide:function(data){
		$(data).appendTo($($(this).children()[0]));
	},
	prependSlide:function(data){
		$(data).prependTo($($(this).children()[0]));
	},
	refreshMobList:function(){
	  var othis = this;
	  var i=0;
	  $(this).find(".swiper-slide").each(function() {
		 $(this).attr("_rowindex",i++);
		 $(this).unbind("click").bind("click",function() {
			var selectedRow = $(this).parent().find('.swiper-slide-active');
			if (selectedRow.length > 0) {
				if ($(selectedRow).attr('_rowindex') == $(this).attr('_rowindex')) {
				   var obj = UIMaster.getObject(othis);
				   UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selectedIndex",_value:othis.selectedIndex,_framePrefix:UIMaster.getFramePrefix(obj)});
				   var id = othis.id.replace(/\./g,"_");
				   if (IS_MOBILEVIEW) { eval($('#'+id+"_openItem")[0].href);}
				   else {$('#'+id+"_openItem").trigger('click');}
				   return;
				} else {
				   $(selectedRow).removeClass('swiper-slide-active');
				}
			}
			if (othis.callSelectedFunc) othis.callSelectedFunc();
			$(this).addClass('swiper-slide-active');
			othis.selectedIndex = $(this).attr("_rowindex");
		 });
	  });
	},
	showMobFilter: function(){
	   $(this.filterPanel).dialog({
	     open: function(event, ui) {
			$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
			$(this).enhanceWithin();
	   }});
	},
	toTop:function(){
	   $(window).scrollTop(0);
	},
	saveMobFilter: function(){
	    var obj = UIMaster.getObject(this);
        if (obj._selectedIndex != this.selectedIndex) {
            UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selectedIndex",_value:this.selectedIndex,_framePrefix:UIMaster.getFramePrefix(obj)});
			obj._selectedIndex = this.selectedIndex;
        }
		var conditions = new Array();
		$(this.filterPanel).find("input,select").each(function(){
			if(this.tagName.toLowerCase() == "input" && this.value != "") {
				conditions.push({name:this.name, value:this.value});
			} else if(this.tagName.toLowerCase() == "select" && this.value != "-1") {
				conditions.push({name:this.name, value:this.value});
			}
		});
		if (conditions.length > 0) {
		   UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"conditions",_value:JSON.stringify(conditions),_framePrefix:UIMaster.getFramePrefix(obj)});
		   this.loadNewSlides("filter");
		}
		this.filterPanel.dialog("close");
	},
	clearMobFilter: function(){
	   $(this.filterPanel).find("input,select").each(function(){
		if(this.tagName.toLowerCase() == "input" && this.value != "") {
			this.value = "";
		} else if(this.tagName.toLowerCase() == "select" && this.value != "-1") {
		    this.value = "-1";
		}
		});
		this.filterPanel.dialog("close");
	},
	/**table object*/
	init:function(){
		if (this.initialized)
			return;
		this.initialized = true;
		var othis = this;
		this.editable = ($(this).prev().attr('editable')=="true");
		this.editablecell = ($(this).prev().attr('editablecell')=="true");
		if ((IS_MOBILEVIEW || this.utype == "swiper") && !this.editablecell) {
		   this.initMobileView();
		   return;
		}
		$(this).prev().children().each(function() { //#'+this.id+'ActionBar'
			$(this).buttonset();
			$(this).removeAttr("style");
			$(this).children().each(function(){
				if ($(this).attr("icon") != undefined && $(this).attr("icon") != null) {
					$(this).button({text:true,icons:{primary:$(this).attr("icon")}});
				}
			});
		});
		var selectMode = $(this).attr("selectMode");
		if (selectMode == "Multiple") {
		   this.isMultipleSelection = true;
		   this.isSingleSelection = false;
		} else if (selectMode == "Single") {
		   this.isSingleSelection = true;
		   this.isMultipleSelection = false;
		} else {
			this.isSingleSelection = false;
		    this.isMultipleSelection = false;
		}
		var othis = this;
		var columnDefs = [{"targets":0,"orderable":false,"mRender":this.renderSelection}];
		$($(this).find('thead').children()[0]).children().each(function(index){
		    columnDefs.push({"targets":index,"orderable":true,"mRender":othis.renderSelection});
		});
		this.tfoot = $($(this).find('tfoot')[0]);
		this.editable = ($(this).prev().attr('editable')=="true");
		this.editablecell = ($(this).prev().attr('editablecell')=="true");
		var recordsTotal = parseInt($(this).attr("recordstotal"));
		try { //scrollY=table body height.
		var table = $(this).dataTable({
		    "lengthMenu": [10, 25, 50, 100],"pageLength": 10,"paginate": this.editable,"paging":this.editable,
			"ordering":this.editable,"info":this.editable,
			"searching": false,"pageIndex":0,"filter": true,
			"scrollY":((this.editablecell || this.disableScrollY)?"auto": "auto"),
			"scrollCollapse": false,
			"recordsFiltered": $(this).attr("recordsfiltered"),
			"recordsTotal": recordsTotal,
			"columnDefs": columnDefs, 
			"processing": false,
			"serverSide": true,
			"bServerSide": true,
			"sServerMethod": "POST",
			"fnDrawCallback": function(settings,b) {
				othis.tbody = $(elementList[othis.id]).children('tbody');
				othis.refreshBodyEvents(othis.tbody, true);
			}
		});
		if (!this.editablecell) {
		    $(this).css("width","100%");
			table.fnSettings()._iRecordsTotal=recordsTotal;
			table.fnSettings()._iRecordsDisplay=(recordsTotal>10?(recordsTotal/10):recordsTotal);
			table._fnUpdateInfo(table.fnSettings());
		}
		} catch(e) {console.log(e); return;}//for unknown case.
		this.dtable = table;
		// enable ajax process after initialization.
        this.dtable.fnSettings().ajax = {
			async: false,
			url: AJAX_SERVICE_URL+"?r="+Math.random(),
			type: 'POST',
			data:{_ajaxUserEvent: "table",
				_uiid: this.id,
				_actionName: "pull",
				_framePrefix: UIMaster.getFramePrefix(UIMaster.El(this.id).get(0)),
				_actionPage: this.parentEntity.__entityName
				}
		};
		var columnIds = new Array();
		var coli = 0;
		//dataTables_scrollBody --> dataTables_scrollHead
		$(elementList[this.id]).parent().prev().find('thead th').each(function(){
			columnIds[coli++] = $(this).attr('id');
		});
		this.columnIds = columnIds;
		var body = $(elementList[this.id]).children('tbody');
		this.tbody = body;
		if(this.rowEmpty()) {
			this.syncButtonGroup(false);
		} else {
			var count = 0;
			this.tbody.children().each(function(){
				this._DT_RowIndex = count++;
			    var ftd = ($(this).children()[0]);
				if (ftd.innerHTML != "") {
					var htmlCode = othis.renderSelection(ftd.innerHTML, '', this);
					ftd.innerHTML="";
					$(ftd).append(htmlCode);
				}
			});
			if (!this.initRefreshBody) {
				this.refreshBodyEvents(body, true);
			}
		}
		if (this.refreshInterval > 0) {
		    othis.autoRefresh(othis);//refresh first always.
		    window.setTimeout(function(){othis.autoRefresh(othis);}, this.refreshInterval * 1000);
		}
		if (this.tfoot.length == 0) {
			return;
		}
		this.tfoot = $(this.tfoot[0]);
		if(this.editable && this.editablecell) {
			this.syncCellEvent(body);
		} 
		this.refreshFoot();
	},
	renderSelection: function(data,type,row){
		if (data.indexOf("radio,") == 0) 
			return "<input type=\"radio\" name=\"selectRadio\" index=\""+data.substring(data.indexOf("radio")+6)+"\" />";
		if (data.indexOf("checkbox,") == 0) 
			return "<input type=\"checkbox\" name=\"\" index=\""+data.substring(data.indexOf("checkbox")+9)+"\"/>";
		return decodeHTML(data);//html content
	},
	refreshFoot:function(){
	    var othis = this;
	    var filters = this.tfoot.find('th');
		filters.each(function(){
			var c = $(this).children();
			for (var i=0;i<c.length;i++) {
				if(c[i].tagName.toLowerCase() == "input") {
					$(c[i]).keydown(function(e){
						if(e.keyCode == 13) {
						    if (othis.editablecell) {
							   othis.hideEditorOnCell(othis.getTDIndex(othis.preCell),othis.preCell);
							   othis.preCell = null;
							   othis.refreshFoot();
							} else {
						       othis.sync();
							   othis.refresh(0);
							}
						}
					});
				} else if(c[i].tagName.toLowerCase() == "select") {
					$(c[i]).change(function(){
					    if (othis.editablecell) {
							othis.hideEditorOnCell(othis.getTDIndex(othis.preCell),othis.preCell);
							othis.preCell = null;
							othis.refreshFoot();
						} else {
							othis.sync()
							othis.refresh(0);
						}
					});
				} else if(c[i].tagName.toLowerCase() == "img" && c[i].src.indexOf("calendar") != -1) {
					//TODO: support data range
					c[i].calendar = new UIMaster.ui.calendar({ui: $(c[i]).prev()[0]});
					c[i].calendar.init();
					$(c[i]).click(function(){
					    this.calendar.open();
					});
					$(c[i]).prev().change(function(e){
						othis.sync()
						othis.refresh(0);
					});
				}
			} 
		});
	},
	syncCellEvent:function(body){
		var othis = this;
		$(body).find('td').each(function(){
			$(this).click(function(){
			    if (othis.preCell != null && othis.preCell == this) {
				   return;
				}
				if (othis.preCell != null) {
			       othis.hideEditorOnCell(othis.getTDIndex(othis.preCell),othis.preCell);
				}
				othis.showEditorOnCell(othis.getTDIndex(this),this);
				othis.preCell = this;
			});
		});
	},
	rowEmpty:function(){
		return this.tbody.find("td[class='dataTables_empty']").length == 1;
	},
	getTDIndex:function(td){
		var i=0;
		$(td).parent().children().each(function(){
			if(this == td) {
				return false;
			}
			i++;
		});
		return i;
	},
	showEditorOnCell:function(i, td){
		if (i==0 || this.rowEmpty()) {return;}
		var value = $(td).text();
		this.tempCellValue = value;
		var widget = this.tfoot.find('th');
		if ($(widget[i]).children().length == 0) return;
		var wd = $(widget[i]).children()[0];
		var tagName = IS_MOBILEVIEW?$(wd).children()[0].tagName.toUpperCase():wd.tagName.toUpperCase();
		if(tagName == "INPUT") {
		    if (IS_MOBILEVIEW) { 
			    setTimeout(function(){$($(wd).find("input")[0]).focus();},200);
				wd.value = $(wd).find("input")[0].value;
			} else {
			    setTimeout(function(){$(wd).focus();},200);
			    wd.value = value;
			}
		} else if(tagName == "SELECT"){
		    setTimeout(function(){$(wd).focus();},200);
			$(wd).children().each(function(){
				if($(this).text() == value) {
					$(this).attr("selected",true);
					return false;
				}
			});
			
		}
		$(td).text("");
		$(td).append(wd);
	},
	hideEditorOnCell:function(i, td){
		if (i==0 || this.rowEmpty() || $(td).children().length == 0) {return;}
		var wd = $(td).children()[0];
		var widget = this.tfoot.find('th');
		$(widget[i]).children().remove();
		$(widget[i]).append(wd);
		var tagName = IS_MOBILEVIEW?$(wd).children()[0].tagName.toUpperCase():wd.tagName.toUpperCase();
		if(tagName == "INPUT") {
		    var v = IS_MOBILEVIEW?($(wd).find("input")[0].value):wd.value;
			$(td).text(v);
			if(this.tempCellValue != v) {
				$(td).parent().attr("updated",true);
			}
		} else if(tagName == "SELECT"){
			var v = $(wd).find("option:selected").text();
			$(td).text(v);
			wd.selectedIndex = -1; 
			if(this.tempCellValue != v) {
				$(td).parent().attr("updated",true);
			}
		}
	},
	syncButtonGroup:function(isselected){
	    if (!this.editable) {
		   $('#'+id+"_newItem").button({disabled:true});
		   $('#'+id+"_openItem").button({disabled:true});
		   $('#'+id+"_disableItem").button({disabled:true});
		   $('#'+id+"_enableItem").button({disabled:true});
		   $('#'+id+"_deleteItem").button({disabled:true});
		   return;
		}
		var id = this.id.replace(/\./g,"_");
		if (this.rowEmpty()) {
			isselected = false;
		}
		if (IS_MOBILEVIEW) { return;}
		if (isselected){
			//$('#'+id+"_newItem").button({disabled:true});
			$('#'+id+"_openItem").button({disabled:false});
			$('#'+id+"_disableItem").button({disabled:false});
			$('#'+id+"_enableItem").button({disabled:false});
			$('#'+id+"_deleteItem").button({disabled:false});
		}else{
			//$('#'+id+"_newItem").button({disabled:false});
			$('#'+id+"_openItem").button({disabled:true});
			$('#'+id+"_disableItem").button({disabled:true});
			$('#'+id+"_enableItem").button({disabled:true});
			$('#'+id+"_deleteItem").button({disabled:true});
		}
	},
	sync:function(){
		var obj = UIMaster.getObject(this);
        if (obj._selectedIndex != this.selectedIndex) {
            UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selectedIndex",_value:this.selectedIndex,_framePrefix:UIMaster.getFramePrefix(obj)});
			obj._selectedIndex = this.selectedIndex;
        }
		if (this.isMultipleSelection) {
		    UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selectedIndexs",_value:this.selectedIndexs.join(","),_framePrefix:UIMaster.getFramePrefix(obj)});
		}
		if (this.editablecell) {
			return;//the editable cell and the filters are mutual exclusive.
		}
        var filters = $(elementList[this.id]).find('tfoot th');
		var conditions = new Array();
		filters.each(function(){
			var c = $(this).children();
			for (var i=0;i<c.length;i++) {
				if(c[i].tagName.toLowerCase() == "input") {
					conditions.push({name:c[i].name, value:c[i].value});
				} else if(c[i].tagName.toLowerCase() == "select") {
					conditions.push({name:c[i].name, value:c[i].value});
				} 
			}
		});
		UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"conditions",_value:JSON.stringify(conditions),_framePrefix:UIMaster.getFramePrefix(obj)});
	},
	syncBodyDataToServer:function(){
		if (this.editablecell) {
			var o = this;
			var bodydata = new Array();
		    //convert body cells to json.
			this.tbody.find('tr').each(function(){
				var row = new Object();
				var i=0;
				$(this).children().each(function(){
					row[o.columnIds[i++]]=$(this).text();
				});
				if ($(this).attr("updated") == "true") {
					row["updated"] = true;
				}
				bodydata.push(row);
			});
			var obj = UIMaster.getObject(this);
			UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"bodyJson",_value:JSON.stringify(bodydata),_framePrefix:UIMaster.getFramePrefix(obj)});
			return;
		}
	},
	refreshBodyEvents:function(body, selectedByDefault) {
	    this.initRefreshBody = true;
		var othis = this;
		if (othis.editablecell) {
			othis.syncCellEvent(body);
		}
		body.children().each(function(){
		 $(this).bind('click', function(){
			var tr = $(this);
			if (othis.editablecell) {
				othis.selectedIndex = tr[0]._DT_RowIndex;
				return true;
			}
			if (othis.isMultipleSelection) {
				if (!tr.hasClass('selected')){
					tr.addClass('selected');
					othis.selectedIndexs.push(tr[0]._DT_RowIndex);
					othis.selectedIndex = tr[0]._DT_RowIndex;
					$($(tr).children()[0]).children()[0].checked=true;
				} else {
					if (tr.hasClass('selected')){
						othis.selectedIndex = -1;
						$($(tr).children()[0]).children()[0].checked=false;
						tr.removeClass('selected');
						for (var i=0; i<othis.selectedIndexs.length; i++) {
							if (othis.selectedIndexs[i] == tr[0]._DT_RowIndex) {
								othis.selectedIndexs.splice(i,1);
								break;
							}
						}
					} 
				}
			  if (othis.selectedIndexs.length > 0) {
				  othis.syncButtonGroup(true);
			  } else {
				  othis.syncButtonGroup(false);
			  }
			  $($($(tr).children()[0]).children()[0]).change(function(){
			      if(this.checked) {
					if (!tr.hasClass('selected')){
						tr.addClass('selected');
						othis.selectedIndexs.push(tr[0]._DT_RowIndex);
						othis.selectedIndex = tr[0]._DT_RowIndex;
					} 
				  } else {
				    if (tr.hasClass('selected')){
					    othis.selectedIndex = -1;
						tr.removeClass('selected');
						for (var i=0; i<othis.selectedIndexs.length; i++) {
						    if (othis.selectedIndexs[i] == tr[0]._DT_RowIndex) {
							    othis.selectedIndexs.splice(i,1);
							    break;
							}
						}
					} 
				  }
				  if (othis.selectedIndexs.length > 0) {
					  othis.syncButtonGroup(true);
				  } else {
				      othis.syncButtonGroup(false);
				  }
			   });
			} else {
			    var isselected=false;
				if (tr.hasClass('selected')){
				    var id = othis.id.replace(/\./g,"_");
					if (IS_MOBILEVIEW) eval($('#'+id+"_openItem")[0].href);
					else $('#'+id+"_openItem").trigger('click');
				} else {
					othis.tbody.find('tr[class*=selected]').removeClass('selected');
					tr.addClass('selected');
					othis.selectedIndex = tr[0]._DT_RowIndex;
					isselected=true;
				}
				if ($($(tr).children()[0]).children() > 0) 
				    $($(tr).children()[0]).children()[0].checked=true;
				othis.syncButtonGroup(isselected);
			}
			
			if (othis.callSelectedFunc) othis.callSelectedFunc();
			return false;//prevent the event pop.
		  });
		});
		if (othis.editablecell || (selectedByDefault == undefined || !selectedByDefault)
		    || (!othis.isSingleSelection && !othis.isMultipleSelection)) {
			return;
		}
		var c = body.children();
		if (c.length == 1) {
			if (this.rowEmpty()) {
				this.syncButtonGroup(false);
			} else {
				$(c[0]).addClass('selected');
				$($(c[0]).children()[0]).children()[0].checked=true;
	            this.selectedIndex = 0;
				this.selectedIndexs.push(0);
	            othis.syncButtonGroup(true);
			}
		} else if (c.length > 0) {
			if (this.selectedIndex != -1) {
				$(c[this.selectedIndex]).addClass('selected');
			} else {
				$(c[0]).addClass('selected');
				$($(c[0]).children()[0]).children()[0].checked=true;
				this.selectedIndex = 0;
				this.selectedIndexs.push(0);
			}
            othis.syncButtonGroup(true);
		}
	},
	refreshFromServer:function(json){
		if (IS_MOBILEVIEW || this.utype == "swiper") {
			$(this).find(".swiper-slide").each(function() {
				$(this).remove();
			});
			for(var i=0;i<json.rows.length;i++){
				this.appendSlide(decodeHTML(json.rows[i].value));
			}
			this.refreshMobList();
			return;
		}
		var trs = $(elementList[this.id]).find('tbody tr');		
		trs.each(function(){
			$(this).unbind('click');
		});
		var tds = $(elementList[this.id]).find('tbody td');
		tds.each(function(){
			$(this).unbind('click');
		});
		if (this.appendRowMode) {
		    for (var i=0;i<json.length;i++) 
		       this.dtable.row.add(decodeHTML(json[i])).draw();
		} else {
			this.dtable._fnAjaxUpdateDraw(json);
		}
	},
	refresh:function(pageNumber){
		if (IS_MOBILEVIEW || this.utype == "swiper") {
		   this.pullRefresh();
		   return;
		}
	    pageNumber = parseInt(pageNumber);
		if (isNaN(pageNumber))
			pageNumber = 1;
		var s = this.dtable.api().settings()[0];
		s.ajax.data={_ajaxUserEvent: "table", _uiid: this.id, _actionName: "pull", _framePrefix: UIMaster.getFramePrefix(UIMaster.El(this.id).get(0)),
            _actionPage: this.parentEntity.__entityName, _selectedIndex: this.selectedIndex, _sync: UIMaster.ui.sync()};
		if (pageNumber != undefined) {
			this.dtable.fnPageChange(pageNumber, true);
		} else {
			var curr = s._iDisplayStart/s._iDisplayLength;
			this.dtable.fnPageChange(curr, true);
		}
	},
	autoRefresh:function(t){
	   if (t.refreshInterval > 0) {
	       t.sync();
		   t.refresh(0);
		   window.setTimeout(function(){t.autoRefresh(t);}, t.refreshInterval * 1000);
	   }
	},
	importData:function(){
	},
	exportData:function(){
		var s = this.dtable.api().settings()[0];
		var data = "_ajaxUserEvent=table&_uiid="+this.id+"&_actionName=exportTable&_framePrefix="
					 +UIMaster.getFramePrefix(UIMaster.El(this.id).get(0))+"&_actionPage="+this.parentEntity.__entityName;
		var form = $('<form action='+AJAX_SERVICE_URL+"?r="+Math.random()+'&'+data+' method=post target=_blank></form>');
		$(form).submit(); 
	},
	statistic: function(){
		var opts ={url:AJAX_SERVICE_URL,async:false,success: UIMaster.cmdHandler,
		          data:{_ajaxUserEvent: "table", _uiid: this.id, _actionName: "chart", _framePrefix: UIMaster.getFramePrefix(UIMaster.El(this.id).get(0)),
                        _actionPage: this.parentEntity.__entityName, _selectedIndex: this.selectedIndex, _sync: {}}};
		if (MobileAppMode) {
            _mobContext.ajax(JSON.stringify(opts));
        } else {
		    $.ajax(opts);
        }
	}
});
UIMaster.ui.webtree = function(conf){
	UIMaster.apply(this, conf);
};
UIMaster.ui.webtree = UIMaster.extend(UIMaster.ui, {
    initialized:false,
	_selectedNodeId:null,
	_selectedNodeName:null,
	_selectedParentNodeId:null,
	_treeObj:null,
	editable:true,
	init:function(){
	    if (this.initialized) { return;}
		this.initialized = true;
		var t = this;
		if (!IS_MOBILEVIEW) { 
		if ($(this).prev().length > 0) {
		    $($($(this).prev()).children()[0]).buttonset();
			$($($(this).prev()).children()[0]).removeAttr("style");
		}
		}
		var children = $(this).children();
		var config = $(children.length == 2? children[1]:children[0]);
		var d = eval("("+config.text()+")");
		var children = d[0].children;
		for (var i=0; i<children.length; i++){
			if(children[i].hasChildren && children[i].children.length==0) {
				children[i].children = true;
			}
		}

		if ($(this).children().length == 2) {
		    this.custItems = $($(this).children()[0]).children();
		}
		var clickEvent = config.attr("clickevent");
		var dblclickEvent = config.attr("dblclickevent");
		this._addnodeevent = config.attr("addnodeevent");
		this._deletenodeevent = config.attr("deletenodeevent");
		this._refreshnodeevent = config.attr("refreshnodeevent");
		var plugins = ["contextmenu", "dnd"];
		if (!this.editable) {
			plugins = [];
		} 
		var _treeObj = $(this).jstree({ 
			"core":{"data": d, "check_callback" : true}, 
			"plugins":plugins, 
			"contextmenu":{"items": this.createMenu},
			"types": {"#": {"max_children": 1, "max_depth": 10, "valid_children": []}}
		}).bind("loaded.jstree", function(node,tree_obj,e){
			//triggered after the root node is loaded for the first time
		}).bind("ready.jstree", function(node,tree_obj,e){
			//triggered after all nodes are finished loading
			//ajax refresh.
			tree_obj.instance.settings.core.data = {
			  'url': function(node) { //node.id === '#' ?
				return AJAX_SERVICE_URL+"?r="+Math.random();
			  },
			  'data': function(node) {
				return {'nodeid' : node.id, 
					'_ajaxUserEvent': "tree",
	                '_uiid': t.id,
	                '_actionName': "expand",
	                '_framePrefix': UIMaster.getFramePrefix(UIMaster.El(t.id).get(0)),
	                '_actionPage': t.parentEntity.__entityName,
	                '_sync': function(){
					    var ref = $(t).jstree(true);
				        var children = ref.get_children_dom(node);
						ref.delete_node(children);
					    t.sync(); 
						return UIMaster.ui.sync();
					}};
			}};
			$(this).bind("select_node.jstree", function(node,tree_obj,e){
			    var tree = t;
			    if (t._selectedNodeId == tree_obj.node.id) {
				    eval(dblclickEvent);
					return;
				}
				t._selectedNodeId=tree_obj.node.id;
				t._selectedNodeName=tree_obj.node.text;
				t._selectedParentNodeId=tree_obj.node.parent;
				eval(clickEvent);
			}).bind("open_node.jstree", function(node,tree_obj,e){
				t._selectedNodeId=tree_obj.node.id;
				t._selectedNodeName=tree_obj.node.text;
				t._selectedParentNodeId=tree_obj.node.parent;
				var ref = $(t).jstree(true);
				ref.select_node(tree_obj.node.id);
			}).bind("create_node.jstree", function(node,tree_obj,position){
				t._selectedNodeId=tree_obj.node.id;
				t._selectedNodeName=tree_obj.node.text;
				t._selectedParentNodeId=tree_obj.node.parent;
				var ref = $(t).jstree(true);
				ref.deselect_node(t._selectedParentNodeId);
				ref.select_node(t._selectedNodeId);
			}).bind("rename_node.jstree", function(node,tree_obj,old){
				t._selectedNodeId=tree_obj.node.id;
				t._selectedNodeName=tree_obj.node.text;
				t._selectedParentNodeId=tree_obj.node.parent;
				var tree = t;
				eval(t._addnodeevent);
			}).bind("delete_node.jstree", function(node,parent){
				t._selectedNodeId=parent.node.id;
				t._selectedNodeName=parent.node.text;
				t._selectedParentNodeId=parent.node.parent;
			}).bind("move_node.jstree", function(node,parent,position,old_parent,old_position,is_multi,old_instance,new_instance){
				alert("move_node");
			});
		});
		this._treeObj = $(_treeObj).jstree(true);
	},
	createMenu:function(node){
		var items = {};
		var t = this.element[0];
		items.refreshItem = {label: "Refresh", action: function (e) {
			var ref = $(t).jstree(true);
			t._sel = ref.get_selected();
			if(!t._sel.length) { return false; }
			ref.refresh_node(t._sel);
		}};
		var custItems = this.element[0].custItems;
		if (custItems != null) {
		  for (var i=0;i<custItems.length;i++) {
		     var a = $(custItems[i]);
		     items["item"+i] = {label: a.attr("title"), action: function (e) {
				eval(a.attr("event"));
			}};
		  }
		}
		//TODO: add customized menu.
		return items;
	},
	sync:function(){
		var obj = UIMaster.getObject(this);
		if (this._selectedNodeId && this._selectedNodeId != "") {
			UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selectedNode",
								  _value:this._selectedNodeId,_framePrefix:UIMaster.getFramePrefix(obj)});
			UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selectedParentNode",
				  				  _value:this._selectedParentNodeId,_framePrefix:UIMaster.getFramePrefix(obj)});
			UIMaster.ui.sync.set({_uiid:UIMaster.getUIID(obj),_valueName:"selectedNodeName",
				                  _value:this._selectedNodeName,_framePrefix:UIMaster.getFramePrefix(obj)});
		}
	},
	refresh:function(children){
		for (var i=0; i<children.length; i++){
			if(children[i].hasChildren && children[i].children.length==0) {
				children[i].children = true;
			}
		}
		$(this).jstree(true).deselect_all();
		$(this).jstree({"core":{"data": children}}).refresh();
	}
});
var options = [[['YES_NO_OPTION','YES_OPTION','MESSAGE_DIALOG','Error'],0],
                [['YES_NO_CANCEL_OPTION','NO_OPTION','INPUT_DIALOG','Information'],1],
                [['OK_CANCEL_OPTION','OK_OPTION','OPTION_DIALOG','Warning'],2],
                [['OK_ONLY_OPTION','CANCEL_OPTION','CONFIRM_DIALOG','Question'],3],
                [['CLOSE_OPTION'],4]];
/**
 * @description UI Dialog class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.dialog} An UIMaster.ui.dialog element.
 * @class UIMaster UI Dialog.
 * @constructor
 */
UIMaster.ui.dialog= function(conf){
    //UIMaster.require("/js/compound.js",true);
    conf = conf || {};
    UIMaster.apply(this, conf);
    UIMaster.groupAssign(this, options);
    this.title = this.title || "\u6807\u9898";
    this.message = this.message || "\u6D88\u606F";
    return this;
};
UIMaster.ui.dialog=UIMaster.extend(UIMaster.ui.dialog, /** @lends UIMaster.ui.dialog*/{
    /**
     * @description Dialog title.
     * @type String
     * @default Dialog Title, varies by the locale.
     */
    title : null,
    /**
     * @description Dialog message.
     * @type String
     * @default Message, varies by the locale.
     */
    message : null,
    /**
     * @description Dialog's option type. Candidates are<br/>
     * UIMaster.ui.dialog.YES_NO_OPTION<br/>
     * UIMaster.ui.dialog.YES_NO_CANCEL_OPTION<br/>
     * UIMaster.ui.dialog.OK_CANCEL_OPTION<br/>
     * UIMaster.ui.dialog.OK_ONLY_OPTION
     * @type Number
     * @default UIMaster.ui.dialog.OK_ONLY_OPTION
     */
    optionType : 3,
    options : [],
    initialValue : 0,
    /**
     * @description Dialog's type. Candidates are<br/>
     * UIMaster.ui.dialog.MESSAGE_DIALOG<br/>
     * UIMaster.ui.dialog.INPUT_DIALOG<br/>
     * UIMaster.ui.dialog.OPTION_DIALOG<br/>
     * UIMaster.ui.dialog.CONFIRM_DIALOG
     * @type Number
     * @default UIMaster.ui.dialog.MESSAGE_DIALOG
     */
    dialogType : 0,
    /**
     * @description Dialog's message type. Candidates are<br/>
     * UIMaster.ui.dialog.Error<br/>
     * UIMaster.ui.dialog.Information<br/>
     * UIMaster.ui.dialog.Warning<br/>
     * UIMaster.ui.dialog.Question
     * @type Number
     * @default UIMaster.ui.dialog.Information
     */
    messageType : 1,
    isOpen:false,
    /**
     * @description Dialog's id.
     * @type String
     * @default ''.
     */
    uiid:'',
    /**
     * @description Error message dialog.
     * @type Boolean
     * @default false - Standard dialog.
     */
    error:false,
    /**
     * @description Dialog's height.
     * @type Number
     * @default 115px.
     */
    height: 115,
    /**
     * @description Dialog's width.
     * @type Number
     * @default 300px.
     */
    width: 300,
    /**
     * @description Dialog's x-position.
     * @type Number
     * @default 400px.
     */
    x:-1,
    /**
     * @description Dialog's y-position.
     * @type Number
     * @default screen center.
     */
    y:-1,
    parent:"",
    handler: UIMaster.emptyFn,
    /**
     * @description Open the dialog.
     */
    open:function(){
        if(!this.isOpen){
            this.paint();
            this.isOpen = true;
        }
    },
    paint:function(){
        function closeBtn(e){
            dialog.dialog('close');
        }
        function eventHandler(e){
            diaObj.returnValue = new Number(this.getAttribute("returnType"));
            diaObj.value = $(content).find("td").eq(1).children('[name="returnType"]').val();
			if (this.getAttribute("id") == "yes" || this.getAttribute("id") == "ok") {
				var rV = diaObj.handler.call(diaObj,e);
				if (rV == false)
					return;
			}
            dialog.dialog('close');
        }
        function createBtn(text,returnType,id){
            return $('<input type="button" value="'+text+'" id="'+id+'">')
                        .attr("returnType",returnType)
                        .click(this.error ? closeBtn : eventHandler)
                        .get(0);
        }
        var msgIconText = ["\u9519\u8BEF", "\u4FE1\u606F", "\u8B66\u544A", "\u7591\u95EE"];
        var msgIcon = ["Error","Information","Warning","Question"];
        var diaObj = this;
        var dialog = $("<div><div>"); 

        var content;
        if ((typeof(this.messageType.length) != 'undefined') && (typeof(this.message.length) != 'undefiend')) {     //multi-line messages
                var p = document.createElement("p");
                for(var i = 0; i < this.messageType.length; i++) {
                    $(p).append('<tr><td class="dialog-image"><img width="48px" height="48px" src="'+RESOURCE_CONTEXTPATH + "/images/" + msgIcon[this.messageType[i]]+'.png" alt="'+msgIconText[this.messageType[i]]+'" title="'+msgIconText[this.messageType[i]]+'"></td><td><div>'+this.message[i]+'</div></td></tr>');
                }
                content = $($(p).html());
        } else {    //single message
            content = document.createElement("tr");
            $(content).append('<td class="dialog-image"><img width="48px" height="48px" src="'+RESOURCE_CONTEXTPATH + "/images/" + msgIcon[this.messageType]+'.png" alt="'+msgIconText[this.messageType]+'" title="'+msgIconText[this.messageType]+'"></td>');
            var msg = document.createElement("td");
            if (this.error) {
                $(msg).append('<pre class="dialog-error-msg">'+this.parent+'</pre>');
                if (this.message != "\u6D88\u606F") {
                    $('<div id="tracebutton" />').addClass('dialog-trace-button-hide').toggle(
                        function(){$('#trace').show();$('#tracebutton').addClass('dialog-trace-button-show');$('#tracetext').text('Hide Detail');$(dialog).height(250);},
                        function(){$('#trace').hide();$('#tracebutton').removeClass('dialog-trace-button-show');$('#tracetext').text('Show Detail');$(dialog).height(100);}).appendTo(msg);
					$(msg).append('<div id="tracetext">\u9519\u8BEF\u8BE6\u60C5</div>').append('<pre id="trace" style="display:none; padding-left:10px">'+this.message+'</pre>');
					$(content).append($(msg));
                }
            } else {
                $(msg).css('text-align','center').append('<div>'+this.message+'</div>').appendTo(content);
            }
        }
		
        //append input or select controls to first td
        if(this.dialogType==this.INPUT_DIALOG)
            $(content).find("td").eq(1).append('<input name="returnType" type="text" />');
        else if(this.dialogType==this.OPTION_DIALOG){
            var select = $('<select name="returnType"></select>');
            $.each(this.options,function(key, value){
                $(select).append($("<option></option>")
                    .attr("value",key)
                    .text(value));
                });
            $(content).find("td").eq(1).append(select);
        }
        var contextDiv = document.createElement("table");
        $(contextDiv).append("<tbody></tbody>").width('100%').append(content);
        //append buttons panel
        var optionDiv = document.createElement("div");
        $(optionDiv).addClass("dialog-option");
        if (this.optionType==this.YES_NO_OPTION || this.optionType==this.YES_NO_CANCEL_OPTION)
            $(optionDiv).append(createBtn.call(this,"Ok",this.YES_OPTION,'yes')).append(createBtn.call(this,"No",this.NO_OPTION,'no'));
        else if (this.optionType==this.CLOSE_OPTION)
            optionDiv.appendChild(createBtn.call(this,"Close",this.CLOSE_OPTION,'close'));
        else
            optionDiv.appendChild(createBtn.call(this,"Ok",this.OK_OPTION,'ok'));
        if (this.optionType==this.YES_NO_CANCEL_OPTION||this.optionType==this.OK_CANCEL_OPTION)
            optionDiv.appendChild(createBtn.call(this,"No",this.CANCEL_OPTION,'cancel'));
        //adjust the space between buttons
        $(optionDiv).find(":button").css("margin-left", "50px").eq(0).css("margin-left", "10px");
		
		dialog.append(contextDiv).append(optionDiv);
        dialog.dialog({
        	title: this.title,
        	width: this.width,
        	height: this.height,
        	modal: true
        });
    }
});
UIMaster.groupAssign(UIMaster.ui.dialog, options);
/**
 * @description Provide UI Mask functions.
 * @namespace Provide a UI mask interface.
 */
UIMaster.ui.mask={
    init:function(){
        if (typeof this._count == 'undefined') 
        {
            this._count = 1;    
        } else {
            this._count = this._count + 1;   
        }
        //if (!document.getElementById('ui-mask-shadow') && !document.getElementById('ui-mask-content'))
        if (this._count == 1)
        {
            var divMask = document.createElement('div');
            divMask.id="ui-mask-shadow";
            divMask.className="ui-overlay";
            divMask.innerHTML = '<div class="ui-widget-overlay"></div>' +
                                '<div id="uimaster_mask_content" class="outer">' +
                                '<div class="inner"><p></p></div></div>';
            divMask.style.display="none";

            document.body.insertBefore(divMask, document.body.firstChild);
        }
    },
    /**
     * @description Open the mask.
     */
    open:function(msg){
        if (this._isOpen) { return; }
        $('#ui-mask-shadow').show();
        /**
        $('#ui-mask-content').removeClass("ui-info-fail-border ui-info-suc-border")
            .html('<div class="inner"><p class="ui-info-msg"><span></span>'
            + (msg == undefined ? 'Processing... Please wait.': msg) +'</p></div>').show();
        $('#ui-mask-content').find('.inner').parent().css('padding', '6px');
        */
        this._isOpen = true;
    },
	openHtml:function(html){
		if (this._isOpen) { return; }
		$.ajax({
			url: RESOURCE_CONTEXTPATH + html + "?r=" + Math.random(),
			type: "GET",async:true,
			success: function(data){
				var panel = $('<div class="ui-info-msg">'+ data +'</div>').appendTo($(document.body));
				var dopts = {
					title: "",
					height: ($(window.top).height()) - 100,
					modal: true,
					closeOnEscape: false,
					show: {effect: "slide", duration: 200}, 
					hide: {effect: "slide", duration: 200},
					open: function(event, ui) {},
					buttons: {"Close": function(){panel.dialog("close");}} 
				}
				panel.dialog(dopts);
			}
		});
	},
    /**
     * @description Close the mask.
     */
    close:function(flag, msg){
        this._count = this._count ? this._count-1 : 0;
        if (this._count != 0 || !this._isOpen) { return; }
        if (typeof flag === 'undefined') {
            $('#ui-mask-shadow').delay(1000).hide();
            $('#ui-mask-content').delay(1000).hide(); 
        } else if (typeof flag === 'boolean') {
            if (flag === false) {
                $('#ui-mask-content').html('<div class="inner"><p class="ui-fail-msg"><span></span>'
                    + (msg == undefined ? 'Error Found.': msg) +'</p></div>').addClass("ui-info-fail-border");
                $('#ui-mask-content').fadeOut(1500);
                $('#ui-mask-shadow').delay(1000).fadeOut(200);
            } else if (flag === true) {
                $('#ui-mask-content').html('<div class="inner"><p class="ui-suc-msg"><span></span>'
                    + (msg == undefined ? 'Operation Successful.': msg) +'</p></div>').addClass("ui-info-suc-border");
                $('#ui-mask-content').fadeOut(1500);
                $('#ui-mask-shadow').delay(1000).fadeOut(200);
            }
        }
        this._isOpen = false;
    },
    /**
     *  @deprecated Open a mask.
     */
    openA:function(msg){
        this.open();
    },
    /**
     *  @deprecated Show failure prompt message before close.
     */
    updateA:function(msg){
        this.close(false, msg);
    }
};
/**
 * @description UI Window class.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.window} An UIMaster.ui.window element.
 * @class UIMaster UI Window.
 * @extends UIMaster.ui.dialog
 * @constructor
 */
UIMaster.ui.window=UIMaster.extend(UIMaster.ui.dialog,{
    hidePFrame:false,
	autoResize:false,
    open:function(win){
        if(!this.isOpen){
        	var w = this.width == 0 ? 500: this.width;
        	var h = this.height == 0 ? 300: this.height;
			if (this.autoResize) {
				w = "70%";
				h = $(window).height() - 50;
			}
        	var thisObj = this;
            this.content = $("<div><div>").html(this.data).attr("id", this.id).css("-webkit-transform","translateZ(0)");
            var buttonset = [];
            if (this.isOnlyShowCloseBtn=="true") {
            	buttonset = [{text:"\u5173\u95ED", click:function(){thisObj.content.dialog("close");}}];
            } else {
            	var p = this.content.find("div[id$='actionPanel']");
            	if(!IS_MOBILEVIEW && p.length > 0) {
            		$(p[p.length-1]).css("display", "none");//select the last one.
            		var actionButtons = $(p[p.length-1]).find("input[type='button']");
					var count = 0;
            		for (var i=0;i<actionButtons.length;i++) {
            			var b = actionButtons[i];
						if ($(b).css("display") == "none") {continue;}
						if ($(b).attr("disabled") == null || $(b).attr("disabled") == "false") {
							buttonset[count++] = { text:b.value, 
							    open:function(){},
            					click:function(e){
								    var text=$(e.srcElement?e.srcElement:e.target).text();
								    for (var i=0;i<actionButtons.length;i++){ 
            						    if(text==actionButtons[i].value){
											if ($(actionButtons[i]).attr("disabled") == "disabled" 
											    || $(actionButtons[i]).attr("disabled") == "true") {
												alert("\u64CD\u4F5C\u65E0\u6548");
												return;
											}
											$(actionButtons[i]).click();
											break;
										}
									} 
								} 
							};
						}
            		}
            	}
            }
			if (IS_MOBILEVIEW) {
				var dopts = {
					title: thisObj.title,
					height: ($(window.top).height()),
					width: "100%",
					modal: true,
					closeOnEscape: false,
					//issue: JQuery dialog with effect has too many impact to Mobile style!
					//show: {effect: "slide", duration: 500}, 
					//hide: {effect: "slide", duration: 500},
					open: function(event, ui) {
						$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
						//fix mobile css style bug in mobile view.
						var pDiv = $(this).parent();//.parent(); if effect enabled.
						pDiv.appendTo($("form:first")).enhanceWithin();//mobile style applied.
						$(pDiv).prev().css("display", "none");//hide parent content for good css feel.
					},
					beforeClose: function() {
						$(this).parent().prev().css("display", "block");//show parent content.
					},
					buttons: buttonset
				}
				this.content.dialog(dopts);
			} else {
				var dopts = {
					title: thisObj.title,
					height: h,
					width: w,
					modal: true,
					closeOnEscape: false,
					show: {effect: "blind", duration: 500},
					hide: {effect: "blind", duration: 500},
					open: function(event, ui) {
						$(".ui-dialog-titlebar-close", ui.dialog | ui).hide();
					},
					beforeClose: function() {
					},
					buttons: buttonset
				}
				this.content.dialog(dopts);
			}
			
            $($("#"+this.id).children().get(0)).attr("_framePrefix",this.frameInfo);
			if (this.js) {
				getElementListSingle(this.content,true);
				win.eval(this.js);
				defaultname.addComponent(win.eval(D+this.uiid),true);
			}
            UIMaster.ui.window.addWindow(this.id,this);
			
            this.isOpen = true;
			var p = this.content.find("div[id$='actionPanel']");
            if(!IS_MOBILEVIEW && p.length > 0) {
			    var dbuttons = $($(this.content.parent()).find("div[class*='ui-dialog-buttonpane']")[0]).find("button[type='button']");
				var buttons = $(p[0]).find("input[type='button']");
				var count = 0;
				for (var i=0;i<buttons.length;i++) {
				    var b = buttons[i];
					if ($(b).attr("disabled") != null && $(b).attr("disabled") == "true" || $(b).css("display") == "none") {
					   continue;
					}
				    var btnClass = $(b).attr('class');
					if(!$(dbuttons[count]).hasClass(btnClass)) {
					   $(dbuttons[count]).addClass(btnClass);
					   $(dbuttons[count]).removeClass("uimaster_button");
					}
					count++;
				}
			}
        }
    },
    close:function(win){
	    if (MobileAppMode) {
		   _mobContext.close();
		   return;
		}
	    if(event){ event.preventDefault()};
    	if (elementList[this.uiid+".Form"]) 
    		elementList[this.uiid+".Form"].parentEntity.releaseFormObject();
    	
		this.content.dialog("close");
    	this.content.parent().remove();
    	defaultname.removeComponent(win.eval(D+this.uiid));
        UIMaster.ui.window.removeWindow(this.id,this);
    }
});
UIMaster.apply(UIMaster.ui.window,{
    wList:[],
    addWindow:function(id,win){this.wList[id]=win},
    removeWindow:function(id){delete this.wList[id]},
    getWindow:function(id){return this.wList[id]}
});
function iframeAutoFitHeight(parent, iframe) {
	iframe.height = $("#"+parent).height() + "px"; 
}
/**DON'T recommand this since many issues occurred! Please refresh your "page to page" instead.*/
function showMobileFrame(link, name) {
   if (link == "#") return;
   var frameId = link.substring(link.indexOf("_framename=") + "_framename=".length);
   frameId = frameId.substring(0, frameId.indexOf("&"));
   var fc = $("<iframe id=\""+frameId+"\" name=\""+frameId+"\" src=\"about:blank\" needsrc=\"true\" frameborder=\"0\" style=\"min-width:100%;min-height:99%;-webkit-transform: translateZ(0);\"></iframe>");
   var d = $("<div><div style=\"position: absolute;top:150px;left:100px;\"><img style=\"width:150px\" src=\"/uimaster/images/qd-logo.png\"></div></div>");
   d.append(fc);
   d.dialog({
        id: "mobileOnlyOneFrame",
		autoOpen:false,
        title:name,
        height: $(window).height(),
		width: "99.5%",
		modal: true,
        resizable: false,
        draggable: false,
		closeOnEscape: true,
		show: {effect: "slide",duration: 150},
		hide: {effect: "slide",duration: 500},
		open: function(event, ui) {//jquery effect dialog with iframe loading is so weird!
		    $(".ui-dialog-titlebar-close", ui.dialog | ui).addClass("ui-button ui-widget ui-state-default ui-corner-all ui-button-icon-only ui-dialog-titlebar-close");
			if (UIMaster.browser.safari) {
               fc.parent().css("-webkit-overflow-scrolling","touch").css("overflow","auto");
			}
			$(document.forms[0]).css("display", "none");//hide parent.
		    window.setTimeout(function(){fc.attr("src",link);},200);
			window.setTimeout(function(){$(d.children()[0]).css("display","none");},3000);//hide it.
		},
		beforeClose: function() {
		    $(document.forms[0]).css("display", "block");//show parent.
		    fc.attr("src","about:blank");
		},
		close: function() {//no need app ajax support.
		  $.ajax({url:AJAX_SERVICE_URL,async:true,data:{_ajaxUserEvent:"tabpane",_uiid:"Form",_valueName:"removePage",_value:frameId,    _framePrefix:UIMaster.getFramePrefix()}});
		}
		//buttons: [{text:"\u5173\u95ED", open:function(){$(this).addClass('uimaster_button');}, click:function(){d.dialog("close");}}]
		});
	d.dialog("open");
}
/**
 * @description UI Tab class. Need more information.
 * @param {Object} conf Configuration items.
 * @returns {UIMaster.ui.tab} An UIMaster.ui.tab element.
 * @class UIMaster UI Tab
 * @extends UIMaster.ui
 * @constructor
 */
UIMaster.ui.tab=UIMaster.extend(UIMaster.ui,{
    links:[],
    index:0,
	selectedIndex:0,
	subComponents:null,
	isInitialized:false,
    init:function(){
	    if (this.isInitialized) return;
		this.isInitialized=true;
        var othis = this, s = this.childNodes[0].nodeType == 1 ? this.childNodes[0] : this.childNodes[1], n = s.childNodes[0].nodeType == 1 ? s.childNodes[0] : s.childNodes[1];
        for (var i in n.childNodes) {
            if (n.childNodes[i].nodeType == 1) {
            	$(n.childNodes[i]).hover(function(){
                	 var all = $(this).parent().children();
                	 for(var i=0;i<all.length;i++){$(all[i]).removeClass("ui-state-hover");};
                	 $(this).addClass("ui-state-hover");
                }).click(function(){othis.setTab(this)});
            }
        }
        var bodies = $("#bodies-container-" + this.id).children();
        bodies.each(function(){
			if(typeof($(this).attr("uipanelid"))!="undefined"){
        		$(this).append($(elementList[$(this).attr("uipanelid")]).parent());
			}
			/**
			var screenHeight = MobileAppMode?_mobContext.getScreenHeight():$(window.top).height();
            if (!IS_MOBILEVIEW && $(this).height() > screenHeight) {
			   $(this).css("height", (screenHeight - 50) + "px");
			   $(this).css("overflow-y", "scroll");
		    }*/
		});
		bodies.resize(function(){
		    /**bodies.children().each(function(){
			var screenHeight = MobileAppMode?_mobContext.getScreenHeight():$(window.top).height();
            if (!IS_MOBILEVIEW && $(this).height() > screenHeight) {
			   $(this).css("height", (screenHeight - 50) + "px");
			   $(this).css("overflow-y", "scroll");
		    }
			});*/
		});
		if (this.subComponents != null) {
		    for (var i=0;i<this.subComponents.length;i++) {
				var comp = elementList[this.subComponents[i]];
			    comp && (comp.parentEntity = this.parentEntity)!=null && comp.init && comp.init();
			}
		}
    },
	sync:function(){
       if (this.subComponents != null && this.subComponents[this.selectedIndex] && defaultname[this.subComponents[this.selectedIndex]] && defaultname[this.subComponents[this.selectedIndex]].sync)
	      defaultname[this.subComponents[this.selectedIndex]].sync();
    },
    setTab:function(e){
        var id = UIMaster.getObject(e).getAttribute("id");
        this._setTab(id);
    },
    _setTab:function(tabObj){
        var currTitle=$("#"+tabObj), titleContainer=currTitle.parent(), titles=titleContainer.children(), bodies=titleContainer.next().children();
        for(var i=0;i<titles.length;i++){$(titles[i]).removeClass("ui-tabs-active").removeClass("ui-state-active").attr("style",null);};
        for(var i=0;i<bodies.length;i++){$(bodies[i]).removeClass("tab-selected-body").addClass("tab-unselected-body");};
        currTitle.addClass("ui-tabs-active").addClass("ui-state-active").attr("style","border-bottom: 1px solid white;");
        var currBody = $("#"+currTitle.attr("id").replace("titles","body"));
		currBody.removeClass("tab-unselected-body").addClass("tab-selected-body");
		/**var screenHeight = MobileAppMode?_mobContext.getScreenHeight():$(window.top).height();
		if (!IS_MOBILEVIEW && currBody.height() > screenHeight)
			currBody.css("height", (screenHeight - 50) + "px").css("overflow-y", "scroll");
		*/
        titleContainer.attr("selectedIndex",currTitle.attr("index"));
		this.selectedIndex = currTitle.attr("index");
		if (currTitle.attr("ajaxload") != null && currTitle.attr("ajaxload") == "true") {
        	currTitle.attr("ajaxload", null);
        }
        var opts = {url:AJAX_SERVICE_URL,async:false,success: UIMaster.cmdHandler,data:{_ajaxUserEvent:"tabpane",_uiid:this.id,_valueName:"selectedIndex",_value:currTitle.attr("index"),_framePrefix:UIMaster.getFramePrefix()}};
        if (MobileAppMode) {
            _mobContext.ajax(JSON.stringify(opts));
        } else {
		    $.ajax(opts);
        }
    },
    addFrameTab:function(title,url){
    	if (this.links.length == 0) {
    		this.addTab(url, title, this.index++, true);
    	} else {
			for(var i=0;i<this.links.length;i++) {
			    if(this.links[i].src==url) {
			    	this._setTab(this.links[i].tabId);
				    return;
			    }
			}
    		this.addTab(url, title, this.index++, true);
    	}
    },
    addTab:function(html, title, index, isUrl){
        var titles = $("#titles-container-" + this.id).children(), bodies = $("#bodies-container-" + this.id).children(), length = titles.length;
        if (index === undefined || index == ""){index = this.index++;}
        var newId = "tab-" + this.id + "-titles-" + index, newBodyId = newId.replace("titles","body"), titleHtml = $("<div id=\""+ newId + "\" class =\"ui-state-default ui-corner-top\" index=\""+ 
        		index +"\"><span style=\"float:left;\">" + title + "&nbsp;&nbsp;</span><span class=\"ui-icon ui-icon-circle-close\" i=\""+index+"\"></span></div>");
        this.links.push({src: html, tabId: newId, i: index});
        var bodyHtml;
        if (isUrl != undefined) {
        	var frameId = html.substring(html.indexOf("_framename=") + "_framename=".length);
        	frameId = frameId.substring(0, frameId.indexOf("&"));
			var iosbug0="";
			if (IS_MOBILEVIEW && UIMaster.browser.safari) {iosbug0="-webkit-overflow-scrolling:touch;overflow:auto";}
        	bodyHtml = $("<div id=\"" + newBodyId + "\" class=\"tab-unselected-body\" index=\""+ index +"\"  style=\""+iosbug0+"\"><iframe id=\""+frameId+"\" name=\""+frameId+"\" src=\""+html+"\" needsrc=\"true\" frameborder=\"0\" style=\"min-width:100%;min-height:100%;\"></iframe></div>");
			var t = this,f=bodyHtml.children()[0];
        } else {
        	bodyHtml = $("<div id=\"" + newBodyId + "\" class=\"tab-unselected-body\" index=\""+ index +"\">" + html + "</div>");
        }
        titleHtml.appendTo($("#titles-container-" + this.id));
        bodyHtml.appendTo($("#bodies-container-" + this.id));
        var othis = this;
        titleHtml.click(function(){if(!$(this).attr("removed")) {othis.setTab(this)} });
        titleHtml.children(".ui-icon-circle-close").click(function(){othis.removeTab(-1, $(this).attr("i"));});
        //getElementListSingle(document.getElementById("bodies-container-" + this.id));
        this._setTab(newId);
    },
    addTabByLazyLoading:function(html, tabUiid) {
    	var updateContent=null;
    	var bodies = $("#bodies-container-" + this.id).children();
    	bodies.each(function(){
			if($(this).attr("uiid") == tabUiid){
				$(html).appendTo($(this));
				updateContent = $(this);
				if (IS_MOBILEVIEW) {$(this).trigger('create');}
				/**var screenHeight = MobileAppMode?_mobContext.getScreenHeight():$(window.top).height();
				if (!IS_MOBILEVIEW && $(this).height() > screenHeight) {
					$(this).css("height", (screenHeight - 50) + "px");
					$(this).css("overflow-y", "scroll");
				}*/
        		return false;
			}
		});
    	return updateContent;
    },
    removeTab:function(index, matchIndex){
    	var othis = this;
        var titles = $("#titles-container-" + this.id).children(), bodies = $("#bodies-container-" + this.id).children();
		if (matchIndex) {titles.each(function(i,e){if($(e).attr("index")==matchIndex){index=i;return false;}});}
		if (index < 0 || titles.length <= index){ return; }
		
        var selectedTab = titles[index];
		$(selectedTab).attr("removed","true");
		$(selectedTab).fadeOut(500, function(){
			$(selectedTab).remove();
			titles = $("#titles-container-" + othis.id).children();
			if(titles.length != 0)
				othis._setTab($(titles[titles.length-1]).attr("id"));
		});
		
		var selectedBody = bodies[index];
		var c = $(selectedBody).children();
		if (c.length > 0 && c[0].tagName.toLowerCase() == "iframe") { 
			var obj = othis.ui;
			var opts = {url:AJAX_SERVICE_URL,async:true,data:{_ajaxUserEvent:"tabpane",_uiid:othis.id,_valueName:"remveTabId",_value:$(c[0]).attr("name"),_framePrefix:UIMaster.getFramePrefix()}};
			if (MobileAppMode) {
				_mobContext.ajax(JSON.stringify(opts));
			} else {
			    $.ajax(opts);
			}
		}
		$(selectedBody).remove();
		for(var i=0;i<othis.links.length;i++){
			if (othis.links[i].i == matchIndex) {
				othis.links.remove(i);
				break;
			}
		}
    },
    setTabAt:function(html, index){
        var bodies = $("#bodies-container-" + this.id).children();
        if (index === undefined || index >= bodies.length || index < 0)
            return;
        bodies.slice(index,index+1).html(html);
    },
    setTitleAt:function(title, index){
        var titles = $("#titles-container-" + this.id).children();
        if (index === undefined || index >= titles.length || index < 0)
            return;
        titles.slice(index,index+1).html(title);
    },
    setSelectedTab:function(index){
        var titles = $("#titles-container-" + this.id).children();
        if (index === undefined || index >= titles.length || index < 0)
            return;
        this._setTab(titles[index].getAttribute("id"));
    },
    getTabLength:function(){
    	return $("#titles-container-" + this.id).children().length;
    }
});
UIMaster.ui.prenextpanel=UIMaster.extend(UIMaster.ui,{
    links:[],
    index:0,
	selectedIndex:0,
	subComponents:null,
	isInitialized:false,
	titleContainer:null,
	bodyContainer:null,
	vertical:false,
	closeOthersByDefault:true,
    init:function(){
	    if (this.isInitialized) return;
		this.isInitialized=true;
        var othis = this, s = this.childNodes[0].nodeType == 1 ? this.childNodes[0] : this.childNodes[1], n = s.childNodes[0].nodeType == 1 ? s.childNodes[0] : s.childNodes[1];
		this.titleContainer = $($(s).children()[0]);
		this.bodyContainer = $($(s).children()[1]);
		if (this.vertical) {
			$(this.titleContainer).css("display", "none");
		}
		this.titleContainer.children().each(function(){
		    $(this).click(function(){
			   var action = "prevbtn";
			   if (othis.selectedIndex <= parseInt($(this).attr("index"))) {
			       action = "nextbtn";
			   }
			   if(!othis.validate0()) return;
			   if(!othis.setTab(parseInt($(this).attr("index")))) return;
			   UIMaster.ui.sync.set({_uiid:othis.id,_valueName:"selectedIndex",_value:othis.selectedIndex,_framePrefix:UIMaster.getFramePrefix(this)});
			   var opts = {url:AJAX_SERVICE_URL,async:false,success: UIMaster.cmdHandler,data:
				{_ajaxUserEvent:"prenextpanel",_uiid:othis.id,_valueName:"nextbtn",_framePrefix:UIMaster.getFramePrefix(),_sync:UIMaster.ui.sync()}};
			   if (MobileAppMode) {
				  _mobContext.ajax(JSON.stringify(opts));
			   } else {
				  $.ajax(opts);
			   }
			});
		});
		this.bodyContainer.children().each(function(i){
			if(typeof($(this).attr("uipanelid"))!="undefined"){
        		$(this).append($(elementList[$(this).attr("uipanelid")]).parent());
			}
			if (othis.vertical) {
			   var wrap = $("<div class='tab-titles ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all'></div>");
			   $(othis.titleContainer.children()[0]).appendTo(wrap);
			   wrap.prependTo($(this));
			   var collpaseAction = $("<div class='collpaseAction'>\u6536\u8D77</div>"); collpaseAction.appendTo(wrap);
			   collpaseAction.bind("click", function(){
				   var body = $(this).parent().next();
				   if(body.css("display")=="block") {
					   body.css("display", "none");$(this).text("\u5C55\u5F00");
				   } else {
					   body.css("display", "block");$(this).text("\u6536\u8D77");
				   }
			   });
			   $(this).css("display", "block");
			   if (i > 0 && othis.closeOthersByDefault) {
				   $(this).find(".collpaseAction").click();
			   }
			}
		});
		
		if($($(s).children()[2])){$($(s).children()[2]).css("display","none");}
		if (this.subComponents != null){
		    for (var i=0;i<this.subComponents.length;i++) {
				var comp = elementList[this.subComponents[i]];
			    comp && (comp.parentEntity = this.parentEntity)!=null && comp.init && comp.init();
			}
		}
    },
	validate0:function() {
	  if (this.subComponents != null && this.subComponents[this.selectedIndex]) {
		  var id = "defaultname." + this.subComponents[this.selectedIndex];
		  var constraint_result = eval(id).validate();
		  if (constraint_result != true && constraint_result != null) {
			  return false;
		  }
	  }
	  return true;
	},
	sync:function(){
	   if (this.subComponents != null && this.subComponents[this.selectedIndex]) {
	      var id = "defaultname." + this.subComponents[this.selectedIndex];
		  eval(id) != null && eval(id).sync != null && eval(id).sync();
	   }
    },
	collapseTab:function(index){
		if (index >= 0 && index < this.bodyContainer.children().length) {
		   $($(this.bodyContainer.children()[index]).children()[1]).css("display","none");
		}
	},
    setTab:function(action){
	    var id = null;
	    var titles = this.titleContainer.children();
	    if (action == "prev") {
			if (this.selectedIndex == 0) return false;
			for (var i=0;i<titles.length;i++){
			    if (i==this.selectedIndex){
				   var id=titles[i-1].getAttribute("id");
				   this._setTab(id);
				   return true;
				}
			}
		} else if (action == "next") {
		    if ((this.selectedIndex+1)==titles.length) return false;
		    for (var i=0;i<titles.length;i++){
			    if (i==this.selectedIndex){
				   var id=titles[i+1].getAttribute("id");
				   this._setTab(id);
				   return true;
				}
			}
		} else {
			var id=titles[action].getAttribute("id");
		    this._setTab(id);
			return true;
		}
		return false;
    },
    _setTab:function(tabObj){
        var currTitle=$("#"+tabObj), titleContainer=currTitle.parent(), titles=titleContainer.children(), bodies=titleContainer.next().children();
        for(var i=0;i<titles.length;i++){$(titles[i]).removeClass("ui-tabs-active").removeClass("ui-state-active").attr("style",null);};
        for(var i=0;i<bodies.length;i++){$(bodies[i]).removeClass("tab-selected-body").addClass("tab-unselected-body");};
        currTitle.addClass("ui-tabs-active").addClass("ui-state-active").attr("style","border-bottom: 1px solid white");
        $("#"+currTitle.attr("id").replace("titles","body")).removeClass("tab-unselected-body").addClass("tab-selected-body");
        titleContainer.attr("selectedIndex",currTitle.attr("index"));
		this.selectedIndex = parseInt(currTitle.attr("index"));
		if (currTitle.attr("ajaxload") != null && currTitle.attr("ajaxload") == "true"){
        	currTitle.attr("ajaxload", null);
        } 
		var opts = {url:AJAX_SERVICE_URL,async:false,success: UIMaster.cmdHandler,data:
		{_ajaxUserEvent:"prenextpanel",_uiid:this.id,_valueName:"selectedIndex",_value:currTitle.attr("index"),_framePrefix:UIMaster.getFramePrefix()}};
		if (MobileAppMode) {
		  _mobContext.ajax(JSON.stringify(opts));
	    } else {
		  $.ajax(opts);
		}
    },
    setTabAt:function(html, index){
        var bodies = $("#bodies-container-" + this.id).children();
        if (index === undefined || index >= bodies.length || index < 0)
            return;
        bodies.slice(index,index+1).html(html);
    },
    setTitleAt:function(title, index){
        var titles = $("#titles-container-" + this.id).children();
        if (index === undefined || index >= titles.length || index < 0)
            return;
        titles.slice(index,index+1).html(title);
    },
    setSelectedTab:function(index){
        var titles = $("#titles-container-" + this.id).children();
        if (index === undefined || index >= titles.length || index < 0)
            return;
        this._setTab(titles[index].getAttribute("id"));
    },
    getTabLength:function(){
    	return $("#titles-container-" + this.id).children().length;
    }
});
UIMaster.ui.chart=UIMaster.extend(UIMaster.ui,{
    type:null,
    chart:null,
    init:function() {
	   var obj = eval("("+$(this).text()+")");
	   this.type = obj.type;
       var ctx = $(this).get(0).getContext("2d");
       if (this.type == "HTMLChartBarType") {
	       obj.type = "bar";
    	   this.chart = new Chart(ctx,obj);
       } else if (this.type == "HTMLChartLinearType") {
	       obj.type = "line";
    	   this.chart = new Chart(ctx,obj);
	   } else if (this.type == "HTMLChartRadarType") {
	       obj.type = "radar";
    	   this.chart = new Chart(ctx, obj);
       } else if (this.type == "HTMLChartPieType") {
	       obj.type = "pie";
    	   this.chart = new Chart(ctx, obj);
       } else if (this.type == "HTMLChartPolarPieType") {
    	   this.chart = new Chart.PolarArea(ctx, obj.data);
	   } else if (this.type == "HTMLChartDoughnutType") {
	       obj.type = "doughnut";
    	   this.chart = new Chart.Doughnut(ctx,obj);
       } 
    }
});
UIMaster.ui.matrix=UIMaster.extend(UIMaster.ui,{
    type:null,
	isInitialized:false,
    init:function() {
	   if (this.isInitialized) return;
       this.isInitialized=true;
	   var othis = this;
	   var columns = $(this).find("div[class='uimaster_matrix_col']");
	   columns.each(function(){
		   $(this).hover(function(){
		   }).click(function(){
			  columns.each(function(){
				 $(this).removeClass("ui-state-hover");
			  });
			  $(this).addClass("ui-state-hover");
			  UIMaster.ui.sync.set({_uiid:othis.id,_valueName:"selectedNodeId",_value:$(this).children().attr("nodeid"),_framePrefix:UIMaster.getFramePrefix(othis)});
			  UIMaster.ui.sync.set({_uiid:othis.id,_valueName:"selectedNode",_value:$(this).children().attr("alt"),_framePrefix:UIMaster.getFramePrefix(othis)});
			  UIMaster.ui.sync.set({_uiid:othis.id,_valueName:"selectedX",_value:$(this).attr("j"),_framePrefix:UIMaster.getFramePrefix(othis)});
			  UIMaster.ui.sync.set({_uiid:othis.id,_valueName:"selectedY",_value:$($(this).parent()).attr("i"),_framePrefix:UIMaster.getFramePrefix(othis)});
		   });
		});
    }
});
UIMaster.ui.baidumap=UIMaster.extend(UIMaster.ui,{
    map:null,
    init:function() {
	    this.map = new BMap.Map(this.id);
		this.map.enableScrollWheelZoom();
		this.map.enableContinuousZoom();
		this.map.addControl(new BMap.NavigationControl());
		this.map.addControl(new BMap.OverviewMapControl());
		this.map.addControl(new BMap.OverviewMapControl({isOpen: true, anchor: BMAP_ANCHOR_BOTTOM_RIGHT}));
    },
	search:function(keyword) {
		var localSearch = new BMap.LocalSearch(this.map, {
			renderOptions:{map: this.map, panel:$(this).next().attr("id")},
			pageCapacity:5
		});
		localSearch.enableAutoViewport();
		localSearch.setSearchCompleteCallback(function(searchResult){
		});
		localSearch.searchInBounds(keyword, this.map.getBounds());
	}
});
UIMaster.ui.chat=UIMaster.extend(UIMaster.ui,{
    partyId: 0,
    init:function() {
	}
});
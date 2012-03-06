
/** version: 1.5.0 */

var ___ETERNA_VERSION = "1.5.0";
// ED = ETERNA_DEBUG, FN = FUNCTION, FNC = FUNCTION_CALLED, COM = COMPONENT
var ED_GET_VALUE = 0x1;
var ED_EXECUTE_SCRIPT = 0x2;
var ED_SHOW_CREATED_HTML = 0x4;
var ED_SHOW_OTHERS = 0x8;
var ED_BASE = 0x10;
var ED_HIGHER = 0x80;
var ED_FN_CALLED = 0x30;
var ED_FNC_STACK = 0x40;
var ED_COM_CREATED = 0x20;

// EE = ETERNA_EVENT
var EE_SUB_WINDOW_CLOSE = "lock_close";
var eterna_table_td_empty_value = "&nbsp;";
var eterna_tableForm_title_pluse = "";
var eterna_tableForm_title_fn = function (cellConfig, titleValue, titleObj, containerObj, _eterna)
{
   if (titleValue.exists && titleValue.value != null && titleValue.value != "")
   {
      if (titleValue.html)
      {
         if (typeof titleValue.value == "object" && typeof titleValue.value.type == "string")
         {
            var tmpCom = _eterna.createComponent(titleValue.value, titleObj);
            if (tmpCom != null)
            {
               titleObj.append(tmpCom);
            }
            if (titleValue.caption != null)
            {
               eg_temp.caption = titleValue.caption;
            }
            if (titleValue.config != null)
            {
               _eterna.appendParam(titleObj, titleValue.config, null);
            }
         }
         else
         {
            titleObj.html(titleValue.value);
         }
      }
      else
      {
         titleObj.text(titleValue.value);
      }
      if (eterna_tableForm_title_pluse != null && eterna_tableForm_title_pluse != "")
      {
         titleObj.append(eterna_tableForm_title_pluse);
      }
      if (cellConfig.container != null && cellConfig.container.required)
      {
         eterna_tableForm_requared_fn(cellConfig, titleValue, titleObj, containerObj, _eterna);
      }
   }
   else
   {
      titleObj.html(eterna_table_td_empty_value);
   }
};
var eterna_tableForm_requared_fn = function (cellConfig, titleValue, titleObj, containerObj, _eterna)
{
   var tObj = jQuery("<span>*</span>");
   tObj.css("color", "red");
   titleObj.prepend(tObj);
};
var eterna_tableList_title_fn = function (columnConfig, titleObj, titleValue, upTitle, _eterna)
{
   if (titleValue.exists && titleValue.value != null && titleValue.value != "")
   {
      if (titleValue.html)
      {
         if (typeof titleValue.value == "object" && typeof titleValue.value.type == "string")
         {
            var tmpCom = _eterna.createComponent(titleValue.value, titleObj);
            if (tmpCom != null)
            {
               titleObj.append(tmpCom);
            }
            if (titleValue.caption != null)
            {
               eg_temp.caption = titleValue.caption;
            }
            if (titleValue.config != null)
            {
               _eterna.appendParam(titleObj, titleValue.config, null);
            }
         }
         else
         {
            titleObj.html(titleValue.value);
         }
      }
      else
      {
         titleObj.text(titleValue.value);
      }
   }
   else
   {
      titleObj.html(eterna_table_td_empty_value);
   }
};
var eterna_select_default_value = [["","-不限-"]];

var eterna_com_stack = new Array();
var eterna_fn_stack = new Array();

// eg = eterna_globe
var EG_SMA = "searchManager_attributes";
var EG_DATA_TYPE = "___dataType";
var EG_DATA_TYPE_ONLYRECORD = "data";
var EG_DATA_TYPE_DATA = "data";
var EG_DATA_TYPE_REST = "REST";
var EG_DATA_TYPE_ALL = "all";
var EG_DATA_TYPE_WEB = "web";

var eg_cache = {
   willInitObjs:[],openedObjs:[],loadedScripts:{}
};

var EG_TEMP_NAMES = [
   "dataName", "srcName", "index", "columnCount", "rowNum",
   "rowType", /* row, title, beforeTable, afterTable, beforeTitle, afterTitle, beforeRow, afterRow */
   "name", "caption", "valueObj", "param", "tempData"
];
var EG_ED_TRANS_NAMES = {
   G:"global", D:"records", V:"view", T:"typical", F:"eFns", R:"res"
};

var eg_temp = {};

var eg_caculateWidth_fix;
var eg_defaultWidth = 80;


function Eterna($E, eterna_debug, rootWebObj)
{
   this.eternaData = eternaData;
   this.$E = {};
   var eternaData = this.$E;

   this.eterna_debug = eterna_debug;
   this.rootWebObj = rootWebObj;
   this.cache = {};
   this.nowWindow = null;
   this.rootWebObjOld_HTML = this.rootWebObj.html();

   if (typeof Eterna._initialized == 'undefined')
   {
      // 不使用深度序列化方式
      jQuery.ajaxSettings.traditional = true;

      Eterna._initialized = true;
      Eterna._oldDebug = eterna_debug;

      Eterna.prototype.eternaVersion = ___ETERNA_VERSION;

      Eterna.prototype.egTemp = function(temp)
      {
         var key;
         if (temp != null)
         {
            for(key in eg_temp)
            {
               eg_temp[key] = null;
            }
            for (var i = 0; i < EG_TEMP_NAMES.length; i++)
            {
               key = EG_TEMP_NAMES[i];
               if (temp[key] != null)
               {
                  eg_temp[key] = temp[key];
               }
            }
            return eg_temp;
         }
         else
         {
            temp = {};
            for (var i = 0; i < EG_TEMP_NAMES.length; i++)
            {
               key = EG_TEMP_NAMES[i];
               if (eg_temp[key] != null)
               {
                  temp[key] = eg_temp[key];
               }
            }
            return temp;
         }
      }

      Eterna.prototype.egTempParam = function(copy)
      {
         if (eg_temp.param == null)
         {
            eg_temp.param = {};
         }
         else
         {
            if (copy)
            {
               eg_temp.param = this.cloneJSON(eg_temp.param);
            }
         }
         return eg_temp.param;
      }

      Eterna.prototype.egTempData = function(copy)
      {
         if (eg_temp.tempData == null)
         {
            eg_temp.tempData = {};
         }
         else
         {
            if (copy)
            {
               eg_temp.tempData = this.cloneJSON(eg_temp.tempData);
            }
         }
         return eg_temp.tempData;
      }

      Eterna.prototype.isArray = function(obj)
      {
         return jQuery.isArray(obj);
      }

      Eterna.prototype.changeEternaData = function(newData)
      {
         if (this.$E == newData)
         {
            return;
         }
         for (var key in this.$E)
         {
            this.$E[key] = null;
         }
         for (var key in newData)
         {
            this.$E[key] = newData[key];
            if (EG_ED_TRANS_NAMES[key] != null)
            {
               this.$E[EG_ED_TRANS_NAMES[key]] = newData[key];
            }
         }
      }

      Eterna.prototype.createJSON = function()
      {
         return {};
      }

      Eterna.prototype.cloneJSON = function(obj)
      {
         if (typeof obj == "object")
         {
            if (this.isArray(obj))
            {
               var newObj = [];
               for(var i = 0; i < obj.length; i++)
               {
                  newObj[i] = this.cloneJSON(obj[i]);
               }
               return newObj;
            }
            else
            {
               var newObj = {};
               for(var key in obj)
               {
                  newObj[key] = this.cloneJSON(obj[key]);
               }
               return newObj;
            }
         }
         else
         {
            return obj;
         }
      }

      Eterna.prototype.getRemoteJSON = function(url, formObj, async, successFunction, completeFunction)
      {
         var httpRequest = null;
         var successFn = function (data, textStatus)
         {
            try
            {
               var _eterna = successFn._eterna;
               var eterna_debug = successFn.eterna_debug;
               var $E = successFn._eterna.$E;
               var eternaData = $E;
               successFn.status = textStatus;
               var endPos = data.length;
               for (var i = data.length - 1; i >= 0; i--)
               {
                  if (data.charCodeAt(i) <= 0x20)
                  {
                     endPos--;
                  }
                  else
                  {
                     break;
                  }
               }
               successFn.data = eval("(" + data.substring(0, endPos) + ")");
            }
            catch (pEx)
            {
               successFn.status = "json_parse_error," + pEx;
               successFn._eterna.printException(pEx);
            }
            if ((successFn.eterna_debug & ED_SHOW_CREATED_HTML) != 0 && successFn.httpRequest != null)
            {
               successFn._eterna.showMessage(successFn.httpRequest.responseText
                     + "\n---------------------------------\n" + "status:" + successFn.status + ",url:" + url);
            }
            if (successFn.customerFn != null)
            {
               successFn.customerFn(successFn.data, successFn.status);
            }
         };
         successFn.eterna_debug = this.eterna_debug;
         successFn._eterna = this;
         if (successFunction != null)
         {
            successFn.customerFn = successFunction;
         }
         try
         {
            var opts = {dataType:"text",url:url,async:false,cache:false};
            if (typeof formObj == "string")
            {
               opts.type = formObj;
            }
            else if (typeof formObj == "object")
            {
               opts.type = "POST";
               if (typeof formObj.jquery == "string")
               {
                  opts.data = formObj.serialize();
               }
               else
               {
                  opts.data = jQuery.param(formObj);
               }
            }
            if (async != null)
            {
               opts.async = async;
            }
            opts.success = successFn;
            if (completeFunction != null)
            {
               var completeFn = function (request, textStatus)
               {
                  var ts = completeFn.successInfo.status != null ?
                        completeFn.successInfo.status : textStatus;
                  completeFn.customerFn(request, ts);
               };
               completeFn.successInfo = successFn;
               completeFn.customerFn = completeFunction;
               opts.complete = completeFn;
            }
            httpRequest = jQuery.ajax(opts);
            if (async)
            {
               successFn.httpRequest = httpRequest;
            }
            else
            {
               if ((this.eterna_debug & ED_SHOW_CREATED_HTML) != 0 && httpRequest != null)
               {
                  this.showMessage(httpRequest.responseText + "\n---------------------------------\n"
                        + "status:" + successFn.status + ",url:" + url);
               }
            }
            return opts.async ? successFn : successFn.data;
         }
         catch (ex)
         {
            this.printException(ex);
            throw ex;
         }
      }

      Eterna.prototype.scatterInit = function()
      {
         try
         {
            var needCreate = true;
            if (this.$E.beforeInit != null)
            {
               needCreate = this.executeScript(this.rootWebObj, this.$E, this.$E.beforeInit);
            }
            if (needCreate)
            {
               for (var i = 0; i < this.$E.V.length; i++)
               {
                  var name = this.$E.V[i].name;
                  var pObj = jQuery("span[initId='" + ef_toScriptString(name) + "']", this.rootWebObj);
                  if (pObj.size() == 1)
                  {
                     var tmpCom = this.createComponent(this.$E.V[i], pObj);
                     if (tmpCom != null)
                     {
                        pObj.after(tmpCom);
                     }
                     else
                     {
                        var subs = pObj.children();
                        var maxCount = subs.size();
                        for (var j = maxCount - 1; j >= 0; j--)
                        {
                           pObj.after(subs.eq(j));
                        }
                     }
                     pObj.remove();
                  }
               }
               if (this.$E.init != null)
               {
                  this.executeScript(this.rootWebObj, this.$E, this.$E.init);
               }
            }
            eterna_doInitObjs();
         }
         catch (ex)
         {
            this.printException(ex);
            throw ex;
         }
         if ((this.eterna_debug & ED_SHOW_CREATED_HTML) != 0)
         {
            this.showMessage(this.rootWebObj.html());
         }
      }

      Eterna.prototype.reInit = function()
      {
         try
         {
            if (typeof dialogArguments == 'object')
            {
               if (dialogArguments.parentEterna != null)
               {
                  eg_cache.parentEterna = dialogArguments.parentEterna;
                  window.opener = dialogArguments.parentWindow;
               }
            }
            if (opener != null && eg_cache.parentEterna == null)
            {
               try
               {
                  if (opener.eterna_getParentEterna != null) // 这里jQuery.isFunction在IE6下有问题
                  {
                     eg_cache.parentEterna = opener.eterna_getParentEterna(window);
                  }
               }
               catch (tmpEX) {}
            }
            this.rootWebObj.html(this.rootWebObjOld_HTML);
            var needCreate = true;
            if (this.$E.beforeInit != null)
            {
               needCreate = this.executeScript(this.rootWebObj, this.$E, this.$E.beforeInit);
            }
            if (needCreate)
            {
               for (var i = 0; i < this.$E.V.length; i++)
               {
                  var tmpCom = this.createComponent(this.$E.V[i], this.rootWebObj);
                  if (tmpCom != null)
                  {
                     this.rootWebObj.append(tmpCom);
                  }
               }
               if (this.$E.init != null)
               {
                  this.executeScript(this.rootWebObj, this.$E, this.$E.init);
               }
            }
            eterna_doInitObjs();
         }
         catch (ex)
         {
            this.printException(ex);
            throw ex;
         }
         if ((this.eterna_debug & ED_SHOW_CREATED_HTML) != 0)
         {
            this.showMessage(this.rootWebObj.html());
         }
      }

      Eterna.prototype.queryWebObj = function(queryStr, container)
      {
         if (container == null)
         {
            container = this.rootWebObj;
         }
         return jQuery(queryStr, container);
      }

      Eterna.prototype.getWebObj = function(id, container, index)
      {
         if (container == null)
         {
            container = this.rootWebObj;
         }
         var idObj = this.checkIdValid(id);
         if (index == null)
         {
            return jQuery(idObj.idStr, container);
         }
         else
         {
            return jQuery(idObj.idStr + ":eq(" + index + ")", container);
         }
      }

      Eterna.prototype.checkIdValid = function(id)
      {
         var str = id + "";
         var valid = 1;
         if (str.length > 32)
         {
            valid = 0;
         }
         else
         {
            for (var i = 0; i < str.length; i++)
            {
               var c = str.charCodeAt(i);
               if (c != 95 && !(c >= 48 && c <= 57) && !(c >= 97 && c <= 122)
                     && !(c >= 65 && c <= 90))
               {
                  valid = 0;
                  break;
               }
            }
         }
         if (valid)
         {
            return {valid:1,idStr:"#" + str};
         }
         else
         {
            return {valid:0,idStr:"[id='" + ef_toScriptString(str) + "']"};
         }
      }

      Eterna.prototype.reloadWebObj = function(webObj)
      {
         var temp = this.egTemp();
         var parentWebObj = null;
         var tmpCom = null;
         try
         {
            if (typeof webObj == "string")
            {
               webObj = this.getWebObj(webObj);
            }
            var myTemp = webObj.data("egTemp");
            this.egTemp(myTemp);
            parentWebObj = webObj.data("parentWebObj");
            tmpCom = this.createComponent(webObj.data("configData"), parentWebObj);
            if (tmpCom != null)
            {
               webObj.after(tmpCom);
            }
            webObj.remove();
            eterna_doInitObjs();
         }
         catch (ex)
         {
            this.egTemp(temp);
            this.printException(ex);
            throw ex;
         }
         this.egTemp(temp);
         if ((this.eterna_debug & ED_SHOW_CREATED_HTML) != 0 && parentWebObj != null)
         {
            this.showMessage("id:" + tmpCom.attr("id") + "\n" + tmpCom.html());
         }
         return tmpCom;
      }

      Eterna.prototype.detachTopOnfocus = function(eventFunction)
      {
         try
         {
            eventFunction.maskDiv.remove();
            var theBody = jQuery("body", eventFunction.theWindow.document);
            theBody.css("overflow-x", eventFunction.oldOverflow.x);
            theBody.css("overflow-y", eventFunction.oldOverflow.y);
            jQuery(eventFunction.theWindow).unbind("focus", eventFunction);
         }
         catch (ex)
         {
            this.printException(ex);
         }
      }

      Eterna.prototype.attachTopOnfocus = function(theWindow, eventFunction)
      {
         try
         {
            jQuery(theWindow).bind("focus", {move:0}, eventFunction);
            eventFunction.theWindow = theWindow;
            var theBody = jQuery("body", theWindow.document);
            eventFunction.maskDiv = jQuery("<div></div>", theBody);
            eventFunction.maskDiv.appendTo(theBody);
            eventFunction.oldOverflow = {x:theBody.css("overflow-x"),y:theBody.css("overflow-y")};
            eventFunction.maskDiv.css({width:"100%",height:"100%",position:"absolute","z-index":20000});
            eventFunction.maskDiv.css("top", jQuery(theWindow.document).scrollTop() + "px");
            eventFunction.maskDiv.css("left", jQuery(theWindow.document).scrollLeft() + "px");
            eventFunction.maskDiv.bind("click", {move:0}, eventFunction);
            eventFunction.maskDiv.bind("mousemove", {move:1}, eventFunction);
            eventFunction.maskDiv.bind("mouseup", {move:0}, eventFunction);
            eventFunction.maskDiv.bind("mousedown", {move:0}, eventFunction);
            theBody.css("overflow-x", "hidden");
            theBody.css("overflow-y", "hidden");
            if (!jQuery.support.boxModel || !jQuery.support.style)
            {
               eventFunction.maskDiv.css("filter", "alpha(opacity=0)");
               eventFunction.maskDiv.css("background-color", "white");
            }
            return true;
         }
         catch (ex)
         {
            this.printException(ex);
            return false;
         }
      }

      Eterna.prototype.openWindow = function(url, name, param, lock, closeEvent)
      {
         var theWindow = open(url, name, param);
         var canInsertIndex = -1;
         var needAdd = true;
         for (var i = 0; i < eg_cache.openedObjs.length; i++)
         {
            if (eg_cache.openedObjs[i].winObj == theWindow)
            {
               needAdd = false;
               break;
            }
            if (eg_cache.openedObjs[i].winObj.closed)
            {
               canInsertIndex = i;
            }
         }
         if (needAdd)
         {
            if (canInsertIndex == -1)
            {
               eg_cache.openedObjs.push({winObj:theWindow,openedEterna:this});
            }
            else
            {
               eg_cache.openedObjs[canInsertIndex] = {winObj:theWindow,openedEterna:this};
            }
         }
         theWindow.focus();
         if (lock)
         {
            this.nowWindow = theWindow;
            if (jQuery.isFunction(closeEvent))
            {
               jQuery(window).bind(EE_SUB_WINDOW_CLOSE, closeEvent);
            }

            var onOpenWindowThisFocus = function(event)
            {
               var theEterna = onOpenWindowThisFocus._eterna;
               if (theEterna.nowWindow == null || theEterna.nowWindow.closed)
               {
                  theEterna.detachTopOnfocus(onOpenWindowThisFocus);
                  if (theEterna.nowWindow != null)
                  {
                     jQuery(window).trigger(EE_SUB_WINDOW_CLOSE);
                     jQuery(window).unbind(EE_SUB_WINDOW_CLOSE);
                  }
                  theEterna.nowWindow = null;
               }
               else if (!event.data.move)
               {
                  theEterna.nowWindow.blur();
                  theEterna.nowWindow.focus();
                  event.preventDefault();
                  return false;
               }
            };
            onOpenWindowThisFocus._eterna = this;
            if (!this.attachTopOnfocus(window.top, onOpenWindowThisFocus))
            {
               this.attachTopOnfocus(window, onOpenWindowThisFocus)
            }
         }
         return theWindow;
      }

      Eterna.prototype.showMessage = function(msg, theWindow)
      {
         var winObj;
         if (theWindow == null || theWindow.closed)
         {
            winObj = this.openWindow("", "_blank", "resizable=yes", false);
            winObj.document.write("<html><body><textarea id='msg' style='border:0;width:100%;height:100%'></textarea></body></html>");
         }
         else
         {
            winObj = theWindow;
         }
         if (winObj.document.all.msg != null)
         {
            winObj.document.all.msg.value += msg;
         }
         return winObj;
      }

      Eterna.prototype.printException = function(ex, noShow)
      {
         if (this.eterna_debug < ED_BASE || ex.dealed)
         {
            return;
         }

         var str = "* exception info:\n";
         if (typeof ex == "object")
         {
            for(var key in ex)
            {
               str += "   " + key + ":" + ex[key] + "\n";
            }
         }
         else
         {
            str += "   " + ex;
         }

         str += "\n\n\n* component stack info:\n";
         for (var i = 0; i < eterna_com_stack.length; i++)
         {
            str += "   " + eterna_com_stack[i] + "\n";
         }

         str += "\n\n\n* now function info:\n";
         if (eterna_fn_stack.length > 0)
         {
            var fnInfo = eterna_fn_stack[eterna_fn_stack.length - 1];
            for (var i = 2; i < fnInfo.length; i += 2)
            {
               if (i > 2)
               {
                  str += ",\n"
               }
               var tmpParam = fnInfo[i + 1];
               str += "   " + fnInfo[i] + ":" + tmpParam;
               if (typeof tmpParam == "object" && tmpParam != null)
               {
                  if (tmpParam.name != null)
                  {
                     str += "[name:" + tmpParam.name + "]";
                  }
                  if (tmpParam.type != null)
                  {
                     str += "[type:" + tmpParam.type + "]";
                  }
               }
            }
            str += "\n\n" + fnInfo[0] + ":" + fnInfo[1];
         }


         if (this.eterna_debug >= ED_FNC_STACK)
         {
            for (var i = eterna_fn_stack.length - 2; i >= 0; i--)
            {
               str += "\n\n\n* stack(" + i + ") function info:\n";
               var fnInfo = eterna_fn_stack[i];
               for (var j = 2; j < fnInfo.length; j += 2)
               {
                  if (j > 2)
                  {
                     str += ",\n"
                  }
                  var tmpParam = fnInfo[j + 1];
                  str += "   " + fnInfo[j] + ":" + tmpParam;
                  if (typeof tmpParam == "object" && tmpParam != null)
                  {
                     if (tmpParam.name != null)
                     {
                        str += "[name:" + tmpParam.name + "]";
                     }
                     if (tmpParam.type != null)
                     {
                        str += "[type:" + tmpParam.type + "]";
                     }
                  }
               }
               str += "\n\n   " + fnInfo[0] + ":" + fnInfo[1];
            }
         }
         else
         {
            ex.dealed = true;
         }
         str += "\n\n\n-------------------------------------------------------------------\n\n";

         if (!noShow)
         {
            eg_cache.msgListWindow = this.showMessage(str, eg_cache.msgListWindow);
         }
         return str;
      }

      Eterna.prototype.pushFunctionStack = function(info)
      {
         if (this.eterna_debug >= ED_FN_CALLED)
         {
            eterna_fn_stack.push(info);
         }
      }

      Eterna.prototype.popFunctionStack = function()
      {
         if (this.eterna_debug >= ED_FN_CALLED)
         {
            eterna_fn_stack.pop();
         }
      }


      Eterna.prototype.getValue_fromRecords = function(dataName, srcName, index)
      {
         if (index == null)
         {
            var tmpStr = "[script]:valueObj.value=$E.D[valueObj.dataName][valueObj.srcName];valueObj.exists=(typeof valueObj.value=='undefined')?0:1;valueObj.valueData=$E.D[valueObj.dataName];";
            var valueObj = {html:0,value:"",exists:-1,dataName:dataName,srcName:srcName};
            return this.getValue(tmpStr, valueObj);
         }
         else
         {
            var tmpStr = "[script]:var mytmpData=$E.D[valueObj.dataName];valueObj.valueData=mytmpData;valueObj.value=mytmpData.rows[valueObj.index][mytmpData.names[valueObj.srcName]-1];valueObj.exists=(typeof valueObj.value=='undefined')?0:1;";
            var valueObj = {html:0,value:"",exists:-1,dataName:dataName,srcName:srcName,index:index};
            return this.getValue(tmpStr, valueObj);
         }
      }

      Eterna.prototype.getValue = function(str, valueObj)
      {
         if (str == null)
         {
            return {html:0,value:"",exists:0};
         }
         if (str == "")
         {
            return {html:0,value:"",exists:1};
         }
         // 如果是其他类型的数据, 需要转换成字符串
         str = str + "";
         if (str.indexOf("[script]:") == 0)
         {
            try
            {
               if (valueObj == null)
               {
                  valueObj = {html:0,value:"",exists:-1};
               }
               var _eterna = this;
               var $E = this.$E;
               var eternaData = $E;
               var eterna_debug = this.eterna_debug;
               var tmpResult = eval(str.substring(9));
               if (valueObj.exists == -1)
               {
                  if (tmpResult != null && tmpResult != "")
                  {
                     valueObj.exists = 1;
                     valueObj.value = tmpResult;
                  }
                  else
                  {
                     valueObj.exists = 0;
                  }
               }
               return valueObj;
            }
            catch (ex)
            {
               if ((this.eterna_debug & ED_GET_VALUE) != 0)
               {
                  //var msg = "getValue(str:" + str + ",valueObj:" +　valueObj + ");\nex:" + ex + "/" + ex.message + "\n\n";
                  this.pushFunctionStack(new Array("getValue", "str:" + str + ",valueObj:" +　valueObj));
                  this.printException(ex);
                  this.popFunctionStack();
               }
               if (valueObj != null)
               {
                  valueObj.exists = 0;
                  return valueObj;
               }
               else
               {
                  return {html:0,value:"",exists:0};
               }
            }
         }
         else if (str.indexOf("[html]:") == 0)
         {
            return {html:1,value:str.substring(7),exists:1};
         }
         else if (str.indexOf("[text]:") == 0)
         {
            return {html:0,value:str.substring(7),exists:1};
         }
         else
         {
            return {html:0,value:str,exists:1};
         }
      }

      Eterna.prototype.executeScript = function(webObj, objConfig, scriptStr)
      {
         var checkResult = true;
         try
         {
            var _eterna = this;
            var $E = this.$E;
            var eternaData = $E;
            var eterna_debug = this.eterna_debug;
            var eventData = webObj; //重命名一个变量, 在event处理中使用, 不用和webObj混淆
            var configData = objConfig; //使配置的名称可以data中的一致
            eval(scriptStr);
         }
         catch (ex)
         {
            if ((this.eterna_debug & ED_EXECUTE_SCRIPT) != 0)
            {
               this.pushFunctionStack(new Array("executeScript", scriptStr));
               //var msg = "executeScript:{" + scriptStr + "}\nex:" + ex + "/" + ex.message + "\n\n";
               this.printException(ex);
               this.popFunctionStack();
            }
         }
         return checkResult;
      }

      Eterna.prototype.createComponent = function(configData, parent)
      {
         if (configData == null || configData.type == null)
         {
            return null;
         }

         if (this.eterna_debug >= ED_COM_CREATED)
         {
            eterna_com_stack.push("name:" + configData.name + ",type:" + configData.type);
         }

         var temp = this.egTemp();

         if (configData.beforeInit != null)
         {
            if (!this.executeScript(null, configData, configData.beforeInit))
            {
               if (this.eterna_debug >= ED_COM_CREATED)
               {
                  eterna_com_stack.pop();
               }
               this.egTemp(temp);
               return null;
            }
         }

         var myTemp = this.egTemp();

         var returnNULL = false;
         var doLoop = false;
         var type = configData.type;
         var webObj = null;
         if (configData.creater != null && jQuery.isFunction(configData.creater))
         {
            webObj = configData.creater(configData, parent);
         }
         else if (type == "tableForm")
         {
            webObj = this.createTableForm(configData);
         }
         else if (type == "tableList")
         {
            webObj = this.createTableList(configData);
         }
         else if (type == "none")
         {
            webObj = parent;
            returnNULL = true;
         }
         else if (type == "loop")
         {
            webObj = parent;
            returnNULL = true;
            doLoop = true;
         }
         else if (type == "replacement")
         {
            var tmpObj = this.createComponent(this.$E.T[configData.typicalComponent], parent);
            webObj = tmpObj;
         }
         else
         {
            var index = type.indexOf("-");
            if (index != -1)
            {
               var tmpType = type.substring(0, index);
               var extType = type.substring(index + 1);
               if (tmpType.toLowerCase() == "input")
               {
                  webObj = this.createWebObj(configData, "input", {name:"type",value:extType});
               }
               else
               {
                  webObj = this.createWebObj(configData, tmpType, {name:"",value:extType});
               }
            }
            else
            {
               webObj = this.createWebObj(configData, type, null);
            }
         }

         if (webObj != null)
         {
            if (configData.subs != null && !configData.noSub)
            {
               if (doLoop)
               {
                  if (configData.loopCondition)
                  {
                     while (this.executeScript(null, configData, configData.loopCondition))
                     {
                        this.dealSubComponent(configData, webObj);
                     }
                  }
                  else if (eg_temp.dataName != null && this.$E.D[eg_temp.dataName] != null)
                  {
                     var theCount = null;
                     var theData = this.$E.D[eg_temp.dataName];
                     if (typeof this.$E.D[eg_temp.dataName] == "object")
                     {
                        theCount = this.$E.D[eg_temp.dataName].rowCount;
                        if (theCount == null)
                        {
                           theCount = this.$E.D[eg_temp.dataName].length;
                        }
                     }
                     else if (typeof this.$E.D[eg_temp.dataName] == "number")
                     {
                        theCount = this.$E.D[eg_temp.dataName];
                     }
                     if (theCount != null)
                     {
                        var temp_index = eg_temp.index;
                        for (var index = 0; index < theCount; index++)
                        {
                           eg_temp.index = index;
                           this.dealSubComponent(configData, webObj);

                           // 将数据重新赋值, 这样即使循环体里改变了, 这里能改回来
                           this.$E.D[eg_temp.dataName] = theData;
                        }
                        eg_temp.index = temp_index;
                     }
                  }
               }
               else
               {
                  this.dealSubComponent(configData, webObj);
               }
            }

            if (configData.init != null)
            {
               this.executeScript(returnNULL ? null : webObj, configData, configData.init);
            }

            if (!returnNULL)
            {
               if (configData.events != null)
               {
                  this.dealEvents(configData, webObj, parent, myTemp);
               }
               webObj.data("parentWebObj", parent);
               webObj.data("configData", configData);
               webObj.data("egTemp", myTemp);
            }
            else if (type == "none" && configData.subs == null)
            {
               if (configData.text != null)
               {
                  var tmpObj = this.getValue(configData.text);
                  if (tmpObj.exists)
                  {
                     parent.append(tmpObj.value);
                  }
               }
            }
         }

         this.egTemp(temp);
         if (this.eterna_debug >= ED_COM_CREATED)
         {
            eterna_com_stack.pop();
         }
         if (returnNULL)
         {
            return null;
         }
         return webObj;
      }

      Eterna.prototype.dealEvents = function(configData, webObj, parent, myTemp)
      {
         for (var i = 0; i < configData.events.length; i++)
         {
            var theEvent = configData.events[i];
            var objParam = false;
            var tmpParamData = {webObj:webObj,objConfig:configData,eventConfig:theEvent}

            if (parent != null)
            {
               tmpParamData.parentWebObj = parent;
            }
            tmpParamData.egTemp = myTemp;

            if (theEvent.param != null && theEvent.param.indexOf("[script]:") == 0)
            {
               objParam = this.executeScript(tmpParamData, configData, theEvent.param.substring(9));
            }
            if (!objParam)
            {
               tmpParamData.eventParam = theEvent.param;
            }
            webObj.bind(theEvent.type, tmpParamData, theEvent.fn);
         }
      }

      Eterna.prototype.dealSubComponent = function(configData, webObj)
      {
         for (var i = 0; i < configData.subs.length; i++)
         {
            var sub = configData.subs[i];
            var tmpObj = this.createComponent(sub, webObj);
            if (tmpObj != null)
            {
               webObj.append(tmpObj);
            }
         }
      }

      Eterna.prototype.createWebObj = function(configData, type, extType)
      {
         var objStr = type;
         if (extType != null && extType.name != "")
         {
            objStr += " " + extType.name + "=\"" + extType.value + "\"";
         }
         if (configData.objName != null)
         {
            var tmpObj = this.getValue(configData.objName);
            if (tmpObj.exists && tmpObj.value != "")
            {
               objStr += " name=\"" + tmpObj.value + "\"";
            }
         }
         else if (eg_temp.name != null)
         {
            if (type.toLowerCase() == "input" || type.toLowerCase() == "select"
                  || type.toLowerCase() == "textarea" || type.toLowerCase() == "button")
            {
               objStr += " name=\"" + eg_temp.name + "\"";
            }
         }
         var obj = jQuery("<" + objStr + "/>");

         if (extType != null)
         {
            if (extType.name == "type" || extType.name == "")
            {
               type = type + "-" + extType.value;
            }
            else
            {
               type = type + "-" + extType.name + "." + extType.value;
            }
         }

         this.appendParam(obj, configData, type);
         if (configData.objValue != null)
         {
            var tmpObj = this.getValue(configData.objValue);
            if (tmpObj.exists)
            {
               obj.val(tmpObj.value);
            }
         }
         if (configData.text != null)
         {
            var tmpObj = this.getValue(configData.text);
            if (tmpObj.exists)
            {
               obj.text(tmpObj.value);
            }
         }
         else if (configData.htmlBody != null)
         {
            obj.html(configData.htmlBody);
         }
         else if (configData.html != null)
         {
            var tmpObj = this.getValue(configData.html);
            if (tmpObj.exists)
            {
               obj.html(tmpObj.value);
            }
         }

         return obj;
      }

      Eterna.prototype.createTR = function(configData, tableObj, type, model)
      {
         if (configData != null && configData.preObj != null)
         {
            var oldTemp = eg_temp;
            var myTemp = configData.preTemp;
            eg_temp = myTemp;
            var preObj = configData.preObj;
            configData.preObj = null;
            configData.preTemp = null;
            if (configData.init != null)
            {
               this.executeScript(preObj, configData, configData.init);
            }
            if (configData.events != null)
            {
               this.dealEvents(configData, preObj, tableObj, myTemp);
            }
            preObj.data("parentWebObj", tableObj);
            preObj.data("configData", configData);
            preObj.data("egTemp", myTemp);
            eg_temp = oldTemp;
         }
         if (model == "final")
         {
            return;
         }
         if (model != "noBeforeInit" && configData != null && configData.beforeInit != null)
         {
            if (!this.executeScript(null, configData, configData.beforeInit))
            {
               return null;
            }
         }
         var trObj = jQuery("<tr></tr>");
         tableObj.append(trObj);
         this.appendParam(trObj, configData, type);
         if (configData != null)
         {
            if (model == "sub" && configData.subs != null)
            {
               this.dealSubComponent(configData, trObj);
            }
            configData.preObj = trObj;
            configData.preTemp = this.egTemp();
         }
         return trObj;
      }

      // 构造一个表单式的表格
      Eterna.prototype.createTableForm = function(configData)
      {
         var tableObj = jQuery("<table></table>");
         this.appendParam(tableObj, configData, "tableForm");

         var percentWidth = true;
         if (configData.percentWidth != null && !configData.percentWidth)
         {
            percentWidth = false;
         }

         var temp = this.egTemp();
         eg_caculateWidth_fix = 0;
         configData.rowOff = null;
         configData.used_counts = null;
         var columns = configData.columns;
         var columnLeft = columns.length;
         eg_temp.columnCount = columns.length;
         if (!percentWidth && configData.caculateWidth != null && configData.caculateWidth)
         {
            if (configData.caculateWidth_fix != null)
            {
               eg_caculateWidth_fix = configData.caculateWidth_fix;
            }
            else if (this.$E.G.tableForm.caculateWidth_fix != null)
            {
               eg_caculateWidth_fix = this.$E.G.tableForm.caculateWidth_fix;
            }
            var tmpWidth = this.ctf_getColWidth(columns, 0, columns.length, configData);
            // 这里只需再补上2个，因为在计算时，中间的已经加了columns.length - 1个
            tmpWidth += eg_caculateWidth_fix * 2;
            tableObj.attr("width", tmpWidth);
         }
         eg_temp.rowNum = 0;
         eg_temp.rowType = "row";

         if (configData.cells.length >= 1 && configData.cells[0].clearRowNum)
         {
            eg_temp.rowNum = -1;
         }
         var trObj = this.ctf_tr(configData.tr, tableObj);
         for(var i = 0; i < configData.cells.length; i++)
         {
            var cell = configData.cells[i];
            var tmpSize = this.ctf_getSize(cell.title, configData) + this.ctf_getSize(cell.container, configData);
            var tmpLeft = columnLeft;
            var needNewRow = false;
            if ((tmpSize > columnLeft && columnLeft < columns.length)
                  || (cell.clearRowNum && columnLeft < this.ctf_getColumnCount(configData)))
            {
               do
               {
                  // 由于要在新的一行, 所以要将rowNum增1
                  eg_temp.rowNum++;
                  configData.rowOff = configData.rowOff == null ? 1 : configData.rowOff + 1;
                  tmpLeft = this.ctf_getColumnCount(configData);
               } while (tmpLeft < tmpSize && tmpLeft < columns.length);
               needNewRow = true;
               // 可能要生成新的一行, 先将前一行的init-script执行了
               this.createTR(configData.tr, tableObj, "tableForm_tr", "final");
            }
            // 如果是要强制新行, 则将eg_temp.rowNum设为0
            var oldRowNum = eg_temp.rowNum;
            if (cell.clearRowNum)
            {
               eg_temp.rowNum = 0;
            }
            var cellObj = this.ctf_cell(columns, this.ctf_getColumnCount(configData) - tmpLeft, percentWidth, cell, configData);
            if (cell.clearRowNum)
            {
               // 恢复原来eg_temp.rowNum的值
               eg_temp.rowNum = oldRowNum;
            }
            if (configData.rowOff)
            {
               // 将rowNum恢复原值
               eg_temp.rowNum -= configData.rowOff;
               configData.rowOff = null;
            }
            if (cellObj.exists)
            {
               if (needNewRow)
               {
                  do
                  {
                     if (columnLeft > 0)
                     {
                        var tdObj = this.ctf_container(null, columnLeft, 1);
                        tdObj.html(eterna_table_td_empty_value);
                        tdObj.attr("width",
                              this.ctf_getColWidth(columns, this.ctf_getColumnCount(configData) - columnLeft, columnLeft, configData)
                              + (percentWidth ? "%" : ""));
                        trObj.append(tdObj);
                     }
                     if (cell.clearRowNum)
                     {
                        eg_temp.rowNum = -1;
                     }
                     // 生成新的一行, 并且更新相关信息
                     trObj = this.ctf_tr(configData.tr, tableObj);
                     if (configData.used_counts != null)
                     {
                        configData.used_counts.shift();
                        if (configData.used_counts.length == 0)
                        {
                           configData.used_counts = null;
                        }
                     }
                     columnLeft = this.ctf_getColumnCount(configData);
                  } while (columnLeft < tmpSize && columnLeft < columns.length);
               }
               if (cell.rowSpan != null && cell.rowSpan > 1)
               {
                  this.ctf_createUsedList(configData, this.ctf_getColumnCount(configData) - columnLeft, cell);
               }
               if (cellObj.title != null)
               {
                  trObj.append(cellObj.title);
               }
               if (cellObj.container != null)
               {
                  trObj.append(cellObj.container);
               }
               columnLeft -= tmpSize;
            }
         }
         if (columnLeft > 0)
         {
            var tdObj = this.ctf_container(null, columnLeft, 1);
            tdObj.html(eterna_table_td_empty_value);
            tdObj.attr("width",
                  this.ctf_getColWidth(columns, this.ctf_getColumnCount(configData) - columnLeft, columnLeft, configData)
                  + (percentWidth ? "%" : ""));
            trObj.append(tdObj);
         }
         this.createTR(configData.tr, tableObj, "tableForm_tr", "final");
         this.egTemp(temp);
         eg_caculateWidth_fix = 0;

         return tableObj;
      }

      // 生成已使用列的列表
      Eterna.prototype.ctf_createUsedList = function(table, index, cell)
      {
         if (table.used_counts == null)
         {
            table.used_counts = [];
         }
         if (table.used_counts.length == 0)
         {
            table.used_counts.push({count:0, spaned:this.ctf_createSpanedArr(table)});
         }
         var cellSize = this.ctf_getSize(cell.title, table) + this.ctf_getSize(cell.container, table);
         for (var i = 1; i < cell.rowSpan; i++)
         {
            if (table.used_counts.length < i + 1)
            {
               table.used_counts.push({count:cellSize, spaned:this.ctf_createSpanedArr(table)});
            }
            else if (i == 1)
            {
               table.used_counts[i].count += cellSize;
            }
            var tmpSize = cellSize;
            for (var j = 0; j < table.columns.length && tmpSize > 0; j++)
            {
               if (j < index)
               {
                  if (table.used_counts[i].spaned[j])
                  {
                     index++;
                  }
               }
               else
               {
                  if (!table.used_counts[i].spaned[j])
                  {
                     table.used_counts[i].spaned[j] = true;
                     tmpSize--;
                  }
               }
            }
         }
      }

      // 创建一个和table的columns等长的数组
      Eterna.prototype.ctf_createSpanedArr = function(table)
      {
         var tmp = [];
         for (var i = 0; i < table.columns.length; i++)
         {
            tmp.push(false);
         }
         return tmp;
      }

      // 获得当前行的最大列数
      Eterna.prototype.ctf_getColumnCount = function(table)
      {
         if (table.used_counts != null)
         {
            if (table.rowOff && table.used_counts.length > table.rowOff)
            {
               return eg_temp.columnCount - table.used_counts[table.rowOff].count;
            }
            else
            {
               return eg_temp.columnCount - table.used_counts[0].count;
            }
         }
         return eg_temp.columnCount;
      }

      // 获得size属性, 如果没有默认为1
      Eterna.prototype.ctf_getSize = function(configData, table)
      {
         var tmpSize = 1;
         if (configData.size != null)
         {
            if (configData.size < 0)
            {
               tmpSize = this.ctf_getColumnCount(table) + configData.size + 1;
            }
            else
            {
               tmpSize = configData.size;
            }
         }
         return tmpSize;
      }

      Eterna.prototype.ctf_getColWidth = function(columns, index, count, table)
      {
         var colCount = this.ctf_getColumnCount(table);
         if (index >= colCount)
         {
            return 0;
         }
         if (table.used_counts != null)
         {
            var tmpIndex = 0;
            if (table.rowOff)
            {
               tmpIndex = table.rowOff;
            }
            if (tmpIndex < table.used_counts.length)
            {
               var tmpColumns = [];
               for (var i = 0; i < columns.length; i++)
               {
                  if (!table.used_counts[tmpIndex].spaned[i])
                  {
                     tmpColumns.push(columns[i]);
                  }
               }
               columns = tmpColumns;
            }
         }
         var sumWidth = 0;
         for (var i = index; i < colCount && count > 0; i++, count--)
         {
            sumWidth += columns[i];
            if (i > index)
            {
                sumWidth += eg_caculateWidth_fix;
            }
         }
         return sumWidth;
      }

      Eterna.prototype.ctf_cell = function(columns, colIndex, percentWidth, cell, table)
      {
         var temp = this.egTemp();

         eg_temp.name = cell.name;
         if (cell.container.value != null)
         {
            eg_temp.dataName = cell.container.value.dataName;
            eg_temp.srcName = cell.container.value.srcName;
         }
         if (cell.initParam != null)
         {
            eg_temp.param = cell.initParam;
         }
         else
         {
            eg_temp.param = {};
         }
         var tmpObj;
         if (cell.container.value != null)
         {
            if (cell.container.needIndex && eg_temp.index != null)
            {
               tmpObj = this.getValue_fromRecords(cell.container.value.dataName, cell.container.value.srcName, eg_temp.index);
            }
            else
            {
               tmpObj = this.getValue_fromRecords(cell.container.value.dataName, cell.container.value.srcName);
            }
            if (!tmpObj.exists)
            {
               var tmpObj2 = this.getValue(cell.container.defaultValue);
               tmpObj.exists = tmpObj2.exists;
               tmpObj.html = tmpObj2.html;
               tmpObj.value = tmpObj2.value;
            }
         }
         else
         {
            tmpObj = this.getValue(cell.container.defaultValue);
         }
         eg_temp.valueObj = tmpObj;
         var valueObj = tmpObj;
         var titleObj;
         if (cell.title.caption != null)
         {
            titleObj = this.getValue(cell.title.caption);
            if (titleObj.exists && titleObj.value != "")
            {
               eg_temp.caption = titleObj.value;
            }
         }
         var cellObj = {title:null,container:null,exists:false};
         if (cell.beforeInit != null)
         {
            if (!this.executeScript(null, cell, cell.beforeInit))
            {
               this.egTemp(temp);
               return cellObj;
            }
         }

         var titleSize = this.ctf_getSize(cell.title, table);
         var tdObj_t = this.ctf_title(cell.title, titleSize, cell.rowSpan);
         if (tdObj_t != null)
         {
            cellObj.title = tdObj_t;
            cellObj.exists = true;
            if (cell.title.caption != null)
            {
               eterna_tableForm_title_fn(cell, titleObj, tdObj_t, tdObj_c, this);
            }
            tdObj_t.attr("width",
                  this.ctf_getColWidth(columns, colIndex, titleSize, table)
                  + (percentWidth ? "%" : ""));
         }

         var containerSize = this.ctf_getSize(cell.container, table);
         var tdObj_c = this.ctf_container(cell.container, containerSize, cell.rowSpan);
         if (tdObj_c != null)
         {
            cellObj.container = tdObj_c;
            cellObj.exists = true;
            if (cell.subs == null && cell.typicalComponent == null)
            {
               if (valueObj.exists && valueObj.value != "")
               {
                  if (valueObj.html)
                  {
                     tdObj_c.html(valueObj.value);
                  }
                  else
                  {
                     tdObj_c.text(valueObj.value);
                  }
               }
               else
               {
                  tdObj_c.html(eterna_table_td_empty_value);
               }
            }
            tdObj_c.attr("width",
                  this.ctf_getColWidth(columns, colIndex + titleSize, containerSize, table)
                  + (percentWidth ? "%" : ""));

            if (cell.subs != null)
            {
               this.dealSubComponent(cell, tdObj_c);
            }

            if (cell.typicalComponent != null)
            {
               var tmpObj = this.createComponent(this.$E.T[cell.typicalComponent], tdObj_c);
               if (tmpObj != null)
               {
                  tdObj_c.append(tmpObj);
               }
            }
         }

         if (cell.init != null)
         {
            this.executeScript(tdObj_c, cell, cell.init);
         }

         this.egTemp(temp);
         return cellObj;
      }

      Eterna.prototype.ctf_title = function(configData, theSize, rowSpan)
      {
         if (theSize == 0)
         {
            return null;
         }
         var tdObj = jQuery("<td></td>");
         if (theSize > 1)
         {
            tdObj.attr("colSpan", theSize);
         }
         if (rowSpan != null && rowSpan > 1)
         {
            tdObj.attr("rowSpan", rowSpan);
         }
         this.appendParam(tdObj, configData, "tableForm_title");
         return tdObj;
      }

      Eterna.prototype.ctf_container = function(configData, theSize, rowSpan)
      {
         if (theSize == 0)
         {
            return null;
         }
         var tdObj = jQuery("<td></td>");
         if (theSize > 1)
         {
            tdObj.attr("colSpan", theSize);
         }
         if (rowSpan != null && rowSpan > 1)
         {
            tdObj.attr("rowSpan", rowSpan);
         }
         this.appendParam(tdObj, configData, "tableForm_container");
         return tdObj;
      }

      Eterna.prototype.ctf_tr = function(configData, tableObj)
      {
         eg_temp.rowNum++; // table form 新生成一行, 所以row number就增1
         return this.createTR(configData, tableObj, "tableForm_tr", "normal");
      }

      // 构造一个列表式的表格
      Eterna.prototype.createTableList = function(configData)
      {
         var tableObj = jQuery("<table></table>");
         this.appendParam(tableObj, configData, "tableList");

         var percentWidth = true;
         if (configData.percentWidth != null && !configData.percentWidth)
         {
            percentWidth = false;
         }

         var temp = this.egTemp();
         // 先对每列执行初始化函数, 如果返回值为false则不显示该列
         var columns = new Array();
         var tmpColumns = configData.columns;
         eg_temp.rowNum = -1;
         for(var i = 0; i < tmpColumns.length; i++)
         {
            var column = tmpColumns[i];
            eg_temp.name = column.name;
            if (column.container.value != null)
            {
               eg_temp.dataName = column.container.value.dataName;
               eg_temp.srcName = column.container.value.srcName;
            }
            else
            {
               eg_temp.dataName = null;
               eg_temp.srcName = null;
            }
            if (column.initParam != null)
            {
               eg_temp.param = column.initParam;
            }
            else
            {
               eg_temp.param = {};
            }
            if (column.done)
            {
               column.title.upTitles = this.cloneJSON(column.title.originUpTitles);
               column.done = false;
            }
            else if (column.title.originUpTitles == null)
            {
               column.title.originUpTitles = this.cloneJSON(column.title.upTitles);
            }
            if (column.beforeInit != null)
            {
               if (this.executeScript(null, column, column.beforeInit))
               {
                  columns.push(column);
               }
            }
            else
            {
               columns.push(column);
            }
         }
         eg_temp.columnCount = columns.length;

         eg_caculateWidth_fix = 0;
         if (!percentWidth && configData.caculateWidth != null && configData.caculateWidth)
         {
            if (configData.caculateWidth_fix != null)
            {
               eg_caculateWidth_fix = configData.caculateWidth_fix;
            }
            else if (this.$E.G.tableList.caculateWidth_fix != null)
            {
               eg_caculateWidth_fix = this.$E.G.tableList.caculateWidth_fix;
            }
            var tmpWidth = this.ctl_getColWidth(columns);
            if (tmpWidth != null)
            {
               // 这里只需再补上2个，因为在计算时，中间的已经加了columns.length - 1个
               tmpWidth += eg_caculateWidth_fix * 2;
               tableObj.attr("width", tmpWidth);
            }
         }

         this.ctl_tr(configData.tr, tableObj, "beforeTable");

         eg_temp.rowNum = 0;
         this.ctl_tr(configData.tr, tableObj, "beforeTitle");
         var maxLayer = this.ctl_dealUpTitle(columns);
         for (var layer = maxLayer - 1; layer >= 0; layer--)
         {
            var trObj = this.ctl_tr(configData.tr, tableObj, "title");
            if (trObj != null)
            {
               for(var i = 0; i < columns.length; i++)
               {
                  var column = columns[i];
                  this.ctl_title(percentWidth, trObj, column, layer);
               }
            }
         }
         this.ctl_tr(configData.tr, tableObj, "afterTitle");

         if (this.$E.D[configData.dataName] != null)
         {
            var rowCount = this.$E.D[configData.dataName].rowCount;
            eg_temp.dataName = configData.dataName;
            var nowRowNum = 1;
            for (var index = 0; index < rowCount; index++)
            {
               eg_temp.index = index;
               eg_temp.rowNum = nowRowNum;
               var trObj = this.ctl_tr(configData.tr, tableObj, "row");
               if (trObj != null)
               {
                  nowRowNum++;
                  // beforeRow 在ctl_tr中处理
                  for(var i = 0; i < columns.length; i++)
                  {
                     var column = columns[i];
                     this.ctl_container(index, percentWidth, trObj, column);
                  }
                  this.ctl_tr(configData.tr, tableObj, "afterRow");
               }
            }
         }

         eg_temp.index = null;
         eg_temp.rowNum = -1;
         this.ctl_tr(configData.tr, tableObj, "afterTable");

         this.createTR(configData.tr, tableObj, "tableList_tr", "final");
         this.egTemp(temp);
         eg_caculateWidth_fix = 0;

         return tableObj;
      }

      Eterna.prototype.ctl_tr = function(configData, tableObj, rowType)
      {
         if (rowType == null || rowType == "row" || rowType == "title")
         {
            eg_temp.rowType = rowType == null ? "row" : rowType;
            if (configData != null && configData.beforeInit != null)
            {
               if (!this.executeScript(null, configData, configData.beforeInit))
               {
                  return null;
               }
            }
            if (eg_temp.rowType == "row")
            {
               this.ctl_tr(configData, tableObj, "beforeRow");
               eg_temp.rowType = "row";
            }
            return this.createTR(configData, tableObj, "tableList_tr", "noBeforeInit");
         }
         else
         {
            eg_temp.rowType = rowType;
            if (configData != null)
            {
               var trObj = null;
               var old_moreRow = eg_cache.moreRow;
               do
               {
                  eg_cache.moreRow = false;
                  trObj = this.createTR(configData, tableObj, "tableList_tr", "sub");
               } while (trObj != null && eg_cache.moreRow);
               eg_cache.moreRow = old_moreRow;
            }
         }
      }

      // 检查是否都是未使用的空格
      Eterna.prototype.ctl_dealUpTitle_checkNone = function(columns, layer, start, count, setSame, upPlace)
      {
         var end = start + count;
         for(var i = start; i < end; i++)
         {
            var column = columns[i];
            if (column.title.upTitles != null)
            {
               if (column.title.upTitles.length > layer)
               {
                  if (column.title.upTitles[layer].none)
                  {
                     if (setSame)
                     {
                        column.title.upTitles[layer].none = false;
                        if (upPlace && i == start)
                        {
                           column.title.upTitles[layer].up = true;
                        }
                        else
                        {
                           column.title.upTitles[layer].same = true;
                        }
                     }
                  }
                  else
                  {
                     return false;
                  }
               }
            }
         }
         return true;
      }

      Eterna.prototype.ctl_dealUpTitle = function(columns)
      {
         var maxLayer = 0;
         for(var i = 0; i < columns.length; i++)
         {
            var column = columns[i];
            if (column.title.upTitles != null)
            {
               if (column.title.upTitles.length > maxLayer)
               {
                  // 取出最高的层数
                  maxLayer = column.title.upTitles.length;
               }
               // 初始化未设值的层
               for (var j = 0; j < column.title.upTitles.length; j++)
               {
                  var upTitle = column.title.upTitles[j];
                  if (upTitle.colSpan != null)
                  {
                     upTitle.width = this.ctl_getColWidth(columns, i, upTitle.colSpan);
                     for (var k = i + 1; k < columns.length; k++)
                     {
                        var tmpCol = columns[k];
                        if (tmpCol.title.upTitles == null)
                        {
                           tmpCol.title.upTitles = new Array();
                        }
                        for (var tmpI = tmpCol.title.upTitles.length; tmpI <= j; tmpI++)
                        {
                           tmpCol.title.upTitles.push({none:true});
                        }
                        if (k - i < upTitle.colSpan)
                        {
                           tmpCol.title.upTitles[j].same = true;
                           tmpCol.title.upTitles[j].none = false;
                        }
                     }
                  }
               }
            }
         }

         for(var i = 0; i < columns.length; i++)
         {
            var column = columns[i];
            if (column.title.upTitles != null)
            {
               for (var j = column.title.upTitles.length - 1; j >= 0; j--)
               {
                  var upTitle = column.title.upTitles[j];
                  if (upTitle.colSpan != null)
                  {
                     upTitle.rowSpan = 1;
                     // 检测并合并上面的空格
                     for (var nowL = j + 1; nowL < maxLayer; nowL++)
                     {
                        if (this.ctl_dealUpTitle_checkNone(columns, nowL, i, upTitle.colSpan))
                        {
                           upTitle.rowSpan++;
                           this.ctl_dealUpTitle_checkNone(columns, nowL, i, upTitle.colSpan, true, true);
                        }
                        else
                        {
                           break;
                        }
                     }
                     // 检测并合并下方的空格
                     for (var nowL = j - 1; nowL >= 0; nowL--)
                     {
                        if (this.ctl_dealUpTitle_checkNone(columns, nowL, i, upTitle.colSpan))
                        {
                           upTitle.rowSpan++;
                           this.ctl_dealUpTitle_checkNone(columns, nowL, i, upTitle.colSpan, true, false);
                        }
                        else
                        {
                           break;
                        }
                     }
                  }
               }
               // 处理底层标题的向上合并
               column.rowSpan = 1;
               for (var j = 0; j < column.title.upTitles.length; j++)
               {
                  var upTitle = column.title.upTitles[j];
                  if (upTitle.none)
                  {
                     column.rowSpan++;
                     upTitle.none = false;
                     upTitle.up = true;
                  }
                  else
                  {
                     break;
                  }
               }
            }
            else if (maxLayer > 0)
            {
               column.rowSpan = maxLayer + 1;
            }
         }

         return maxLayer + 1; // 需要增加一层 本身的标题层
      }

      Eterna.prototype.ctl_getColWidth = function(columns, start, count)
      {
         if (start == null)
         {
            start = 0;
         }
         if (count == null)
         {
            count = columns.length;
         }
         var sumWidth = 0;
         var end = start + count;
         for (var i = start; i < end; i++)
         {
            if (columns[i].width == null)
            {
               return null;
            }
            sumWidth += columns[i].width;
            if (i > 0)
            {
                sumWidth += eg_caculateWidth_fix;
            }
         }
         return sumWidth;
      }

      Eterna.prototype.ctl_title = function(percentWidth, trObj, column, layer)
      {
         var temp = this.egTemp();

         eg_temp.name = column.name;
         if (column.container.value != null)
         {
            eg_temp.dataName = column.container.value.dataName;
            eg_temp.srcName = column.container.value.srcName;
         }
         if (column.initParam != null)
         {
            eg_temp.param = column.initParam;
         }
         else
         {
            eg_temp.param = {};
         }

         var showTitle = true;
         if (column.title.upTitles != null)
         {
            if (layer > 0)
            {
               // 层数大于0, 表示不是底层标题
               var nowL;
               if (column.title.upTitles.length > layer - 1)
               {
                  nowL = layer - 1;
               }
               else
               {
                  nowL = column.title.upTitles.length - 1;
               }
               var upTitle = column.title.upTitles[nowL];
               if (upTitle.same || upTitle.done)
               {
                  // 如果是已处理或是和其他格相同, 则跳过
                  upTitle = null;
                  showTitle = false;
               }
               else if (upTitle.up)
               {
                  // 如果是提升处理, 则判断下层的格
                  upTitle.done = true;
                  upTitle = null;
                  for (var i = nowL - 1; i >= 0; i--)
                  {
                     if (column.title.upTitles[i].up)
                     {
                        column.title.upTitles[i].done = true;
                     }
                     else
                     {
                        upTitle = column.title.upTitles[i];
                        break;
                     }
                  }
               }
               if (upTitle != null)
               {
                  showTitle = false;
                  if (upTitle.none)
                  {
                     // 空格的处理
                     var tdObj_t = jQuery("<td>" + eterna_table_td_empty_value + "</td>");
                     this.appendParam(tdObj_t, upTitle, "tableList_title");
                     if (column.width != null)
                     {
                        tdObj_t.attr("width", column.width + (percentWidth ? "%" : ""));
                     }
                     trObj.append(tdObj_t);
                     upTitle.done = true;
                  }
                  else
                  {
                     // 上层标题的处理
                     var tdObj_t = jQuery("<td></td>");
                     this.appendParam(tdObj_t, upTitle, "tableList_title");
                     if (upTitle.width != null)
                     {
                        tdObj_t.attr("width", upTitle.width + (percentWidth ? "%" : ""));
                     }
                     if (upTitle.rowSpan != null && upTitle.rowSpan > 1)
                     {
                        tdObj_t.attr("rowSpan", upTitle.rowSpan);
                     }
                     if (upTitle.colSpan != null && upTitle.colSpan > 1)
                     {
                        tdObj_t.attr("colSpan", upTitle.colSpan);
                     }
                     var tmpObj;
                     if (upTitle.caption != null)
                     {
                        tmpObj = this.getValue(upTitle.caption);
                     }
                     else
                     {
                        tmpObj = {exists:0,value:"",html:0};
                     }
                     eterna_tableList_title_fn(column, tdObj_t, tmpObj, true, this);
                     trObj.append(tdObj_t);
                     upTitle.done = true;
                  }
               }
            }
         }
         if (showTitle && !column.done)
         {
            var tdObj_t = jQuery("<td></td>");
            this.appendParam(tdObj_t, column.title, "tableList_title");
            if (column.rowSpan != null && column.rowSpan > 1)
            {
               tdObj_t.attr("rowSpan", column.rowSpan);
            }

            var tmpObj;
            if (column.title.caption != null)
            {
               var tmpObj = this.getValue(column.title.caption);
            }
            else
            {
               tmpObj = {exists:0,value:"",html:0};
            }
            eterna_tableList_title_fn(column, tdObj_t, tmpObj, false, this);
            if (column.width != null)
            {
               tdObj_t.attr("width", column.width + (percentWidth ? "%" : ""));
            }
            trObj.append(tdObj_t);
            column.done = true;
         }

         this.egTemp(temp);
      }

      Eterna.prototype.ctl_container = function(index, percentWidth, trObj, column)
      {
         var temp = this.egTemp();

         eg_temp.name = column.name;
         if (column.container.value != null)
         {
            eg_temp.dataName = column.container.value.dataName;
            eg_temp.srcName = column.container.value.srcName;
         }
         if (column.initParam != null)
         {
            eg_temp.param = column.initParam;
         }
         else
         {
            eg_temp.param = {};
         }
         var tmpObj;
         if (column.container.value != null)
         {
            tmpObj = this.getValue_fromRecords(column.container.value.dataName, column.container.value.srcName, index);
            if (!tmpObj.exists)
            {
               var tmpObj2 = this.getValue(column.container.defaultValue);
               tmpObj.exists = tmpObj2.exists;
               tmpObj.html = tmpObj2.html;
               tmpObj.value = tmpObj2.value;
            }
         }
         else
         {
            tmpObj = this.getValue(column.container.defaultValue);
         }
         eg_temp.valueObj = tmpObj;
         if (column.beforeInit != null && !column.DBI)
         {
            if (!this.executeScript(null, column, column.beforeInit))
            {
               this.egTemp(temp);
               return;
            }
         }

         var tdObj_c = jQuery("<td></td>");
         trObj.append(tdObj_c);
         this.appendParam(tdObj_c, column.container, "tableList_container");

         if (column.subs == null && column.typicalComponent == null)
         {
            if (tmpObj.exists && tmpObj.value != "")
            {
               if (tmpObj.html)
               {
                  tdObj_c.html(tmpObj.value);
               }
               else
               {
                  tdObj_c.text(tmpObj.value);
               }
            }
            else
            {
               tdObj_c.html(eterna_table_td_empty_value);
            }
         }
         if (column.width != null)
         {
            tdObj_c.attr("width", column.width + (percentWidth ? "%" : ""));
         }

         if (column.subs != null)
         {
            this.dealSubComponent(column, tdObj_c);
         }

         if (column.typicalComponent != null)
         {
            var tmpObj = this.createComponent(this.$E.T[column.typicalComponent], tdObj_c);
            if (tmpObj != null)
            {
               tdObj_c.append(tmpObj);
            }
         }
         if (column.init != null)
         {
            this.executeScript(tdObj_c, column, column.init);
         }

         this.egTemp(temp);
      }

      Eterna.prototype.appendParam = function(obj, configData, objType)
      {
         if (configData == null || configData.ignoreGlobal == null || !configData.ignoreGlobal)
         {
            if (objType != null && this.$E.G[objType] != null)
            {
               if (this.$E.G[objType].className != null)
               {
                  obj.addClass(this.$E.G[objType].className);
               }
               if (this.$E.G[objType].attr != null)
               {
                  obj.attr(this.$E.G[objType].attr);
               }
               if (this.$E.G[objType].css != null)
               {
                  obj.css(this.$E.G[objType].css);
               }
               if (this.$E.G[objType].prop != null)
               {
                  obj.prop(this.$E.G[objType].prop);
               }
            }
         }
         if (configData != null)
         {
            if (configData.className != null)
            {
               obj.addClass(configData.className);
            }
            if (configData.attr != null)
            {
               obj.attr(configData.attr);
            }
            if (configData.css != null)
            {
               obj.css(configData.css);
            }
            if (configData.prop != null)
            {
               obj.prop(configData.prop);
            }
         }
      }

   }

   this.changeEternaData($E);


   if (this.eterna_debug >= ED_FN_CALLED)
   {
      this.getValue_fromRecords = function(dataName, srcName, index)
      {
         this.pushFunctionStack(new Array("getValue_fromRecords", Eterna.prototype.getValue_fromRecords,
               "dataName", dataName, "srcName", srcName, "index", index));
         var result = Eterna.prototype.getValue_fromRecords.call(this, dataName, srcName, index);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.getValue = function(str, valueObj)
      {
         this.pushFunctionStack(new Array("getValue", Eterna.prototype.getValue,
               "str", str, "valueObj", valueObj));
         var result = Eterna.prototype.getValue.call(this, str, valueObj);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.executeScript = function(webObj, objConfig, scriptStr)
      {
         this.pushFunctionStack(new Array("executeScript", Eterna.prototype.executeScript,
               "webObj", webObj, "objConfig", objConfig, "scriptStr", scriptStr));
         var result = Eterna.prototype.executeScript.call(this, webObj, objConfig, scriptStr);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.createComponent = function(configData, parent)
      {
         this.pushFunctionStack(new Array("createComponent", Eterna.prototype.createComponent,
               "configData", configData, "parent", parent));
         var result = Eterna.prototype.createComponent.call(this, configData, parent);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.dealEvents = function(configData, webObj, parent, myTemp)
      {
         this.pushFunctionStack(new Array("dealEvents", Eterna.prototype.dealEvents,
               "configData", configData, "webObj", webObj, "parent", parent, "myTemp", myTemp));
         var result = Eterna.prototype.dealEvents.call(this, configData, webObj, parent, myTemp);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.dealSubComponent = function(configData, webObj)
      {
         this.pushFunctionStack(new Array("dealSubComponent", Eterna.prototype.dealSubComponent,
               "configData", configData, "webObj", webObj));
         var result = Eterna.prototype.dealSubComponent.call(this, configData, webObj);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.createWebObj = function(configData, type, extType)
      {
         this.pushFunctionStack(new Array("createWebObj", Eterna.prototype.createWebObj,
               "configData", configData, "type", type, "extType", extType));
         var result = Eterna.prototype.createWebObj.call(this, configData, type, extType);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.createTR = function(configData, tableObj, type, model)
      {
         this.pushFunctionStack(new Array("createTR", Eterna.prototype.createTR,
               "configData", configData, "tableObj", tableObj, "type", type, "model", model));
         var result = Eterna.prototype.createTR.call(this, configData, tableObj, type, model);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.createTableForm = function(configData)
      {
         this.pushFunctionStack(new Array("createTableForm", Eterna.prototype.createTableForm,
               "configData", configData));
         var result = Eterna.prototype.createTableForm.call(this, configData);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctf_createUsedList = function(table, index, cell)
      {
         this.pushFunctionStack(new Array("ctf_createUsedList", Eterna.prototype.ctf_createUsedList,
               "table", table, "index", index, "cell", cell));
         var result = Eterna.prototype.ctf_createUsedList.call(this, table, index, cell);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctf_getColumnCount = function(table)
      {
         this.pushFunctionStack(new Array("ctf_getColumnCount", Eterna.prototype.ctf_getColumnCount,
               "table", table));
         var result = Eterna.prototype.ctf_getColumnCount.call(this, table);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctf_getSize = function(configData, table)
      {
         this.pushFunctionStack(new Array("ctf_getSize", Eterna.prototype.ctf_getSize,
               "configData", configData, "table", table));
         var result = Eterna.prototype.ctf_getSize.call(this, configData, table);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctf_getColWidth = function(columns, index, count, table)
      {
         this.pushFunctionStack(new Array("ctf_getColWidth", Eterna.prototype.ctf_getColWidth,
               "columns", columns, "index", index, "count", count, "table", table));
         var result = Eterna.prototype.ctf_getColWidth.call(this, columns, index, count, table);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctf_cell = function(columns, colIndex, percentWidth, cell, table)
      {
         this.pushFunctionStack(new Array("ctf_cell", Eterna.prototype.ctf_cell,
               "columns", columns, "colIndex", colIndex, "percentWidth", percentWidth,
               "cell", cell, "table", table));
         var result = Eterna.prototype.ctf_cell.call(this, columns, colIndex, percentWidth, cell, table);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctf_title = function(configData, theSize, rowSpan)
      {
         this.pushFunctionStack(new Array("ctf_title", Eterna.prototype.ctf_title,
               "configData", configData, "theSize", theSize, "rowSpan", rowSpan));
         var result = Eterna.prototype.ctf_title.call(this, configData, theSize, rowSpan);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctf_container = function(configData, theSize, rowSpan)
      {
         this.pushFunctionStack(new Array("ctf_container", Eterna.prototype.ctf_container,
               "configData", configData, "theSize", theSize, "rowSpan", rowSpan));
         var result = Eterna.prototype.ctf_container.call(this, configData, theSize, rowSpan);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctf_tr = function(configData, tableObj)
      {
         this.pushFunctionStack(new Array("ctf_tr", Eterna.prototype.ctf_tr,
               "configData", configData, "tableObj", tableObj));
         var result = Eterna.prototype.ctf_tr.call(this, configData, tableObj);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.createTableList = function(configData)
      {
         this.pushFunctionStack(new Array("createTableList", Eterna.prototype.createTableList,
               "configData", configData));
         var result = Eterna.prototype.createTableList.call(this, configData);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctl_tr = function(configData, tableObj, rowType)
      {
         this.pushFunctionStack(new Array("ctl_tr", Eterna.prototype.ctl_tr,
               "configData", configData, "tableObj", tableObj, "rowType", rowType));
         var result = Eterna.prototype.ctl_tr.call(this, configData, tableObj, rowType);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctl_dealUpTitle_checkNone = function(columns, layer, start, count, setSame, upPlace)
      {
         this.pushFunctionStack(new Array("ctl_dealUpTitle_checkNone", Eterna.prototype.ctl_dealUpTitle_checkNone,
               "columns", columns, "layer", layer, "start", start, "count", count, "setSame", setSame, "upPlace", upPlace));
         var result = Eterna.prototype.ctl_dealUpTitle_checkNone.call(this, columns, layer, start, count, setSame, upPlace);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctl_dealUpTitle = function(columns)
      {
         this.pushFunctionStack(new Array("ctl_dealUpTitle", Eterna.prototype.ctl_dealUpTitle,
               "columns", columns));
         var result = Eterna.prototype.ctl_dealUpTitle.call(this, columns);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctl_getColWidth = function(columns, start, count)
      {
         this.pushFunctionStack(new Array("ctl_getColWidth", Eterna.prototype.ctl_getColWidth,
               "columns", columns, "start", start, "count", count));
         var result = Eterna.prototype.ctl_getColWidth.call(this, columns, start, count);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctl_title = function(percentWidth, trObj, column, layer)
      {
         this.pushFunctionStack(new Array("ctl_title", Eterna.prototype.ctl_title,
               "percentWidth", percentWidth, "trObj", trObj, "column", column,
               "layer", layer));
         var result = Eterna.prototype.ctl_title.call(this, percentWidth, trObj, column, layer);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.ctl_container = function(index, percentWidth, trObj, column)
      {
         this.pushFunctionStack(new Array("ctl_container", Eterna.prototype.ctl_container,
               "index", index, "percentWidth", percentWidth, "trObj", trObj, "column", column));
         var result = Eterna.prototype.ctl_container.call(this, index, percentWidth, trObj, column);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

      this.appendParam = function(obj, configData, objType)
      {
         this.pushFunctionStack(new Array("appendParam", Eterna.prototype.appendParam,
               "obj", obj, "configData", configData, "objType", objType));
         var result = Eterna.prototype.appendParam.call(this, obj, configData, objType);
         this.popFunctionStack();
         if (typeof result != 'undefined')
         {
            return result;
         }
      }

   }

}

// 添加一个需要等待初始化的对象
function eterna_addWillInitObj(obj, priority)
{
   if (priority == null)
   {
      eg_cache.willInitObjs.push({obj:obj,p:100});
      return;
   }
   var tmp = parseInt(priority);
   if (isNaN(tmp) || tmp < 0 || tmp >= 100)
   {
      throw new Error("Error priority:[" + priority + "], must in [0, 99].");
   }
   eg_cache.willInitObjs.push({obj:obj,p:tmp});
   var tmpI = eg_cache.willInitObjs.length - 1;
   for (var i = 0; i < eg_cache.willInitObjs.length; i++)
   {
      if (tmp < eg_cache.willInitObjs[i].p)
      {
         tmpI = i;
         break;
      }
   }
   if (tmpI < eg_cache.willInitObjs.length - 1)
   {
      for (var i = eg_cache.willInitObjs.length - 1; i > tmpI; i--)
      {
         eg_cache.willInitObjs[i] = eg_cache.willInitObjs[i - 1];
      }
      eg_cache.willInitObjs[tmpI] = {obj:obj,p:tmp};
   }
}

// 触发所有等待初始化的对象的willInit事件
function eterna_doInitObjs()
{
   var objs = eg_cache.willInitObjs;
   eg_cache.willInitObjs = [];
   for (var i = 0; i < objs.length; i++)
   {
      objs[i].obj.trigger("willInit");
   }
}

// 获取打开此窗口的eterna对象
function eterna_getParentEterna(winObj)
{
   for (var i = 0; i < eg_cache.openedObjs.length; i++)
   {
      if (eg_cache.openedObjs[i].winObj == winObj)
      {
         return eg_cache.openedObjs[i].openedEterna;
      }
   }
   return null;
}

// 页面重载是关闭所有打开的窗口
function eterna_closeAllWindow()
{
   for (var i = 0; i < eg_cache.openedObjs.length; i++)
   {
      if (!eg_cache.openedObjs[i].winObj.closed)
      {
         eg_cache.openedObjs[i].winObj.close();
      }
   }
}
jQuery(window).unload(eterna_closeAllWindow);

/**
 * 获取文本资源的值
 */
function eterna_getResourceValue(resArray, params)
{
   if (params == null)
   {
      params = [];
   }
   if (params.length == 1 && jQuery.isArray(params[0]))
   {
      params = params[0];
   }
   var str = "";
   for (var i = 0; i < resArray.length; i++)
   {
      var res = resArray[i];
      if (typeof res == "number")
      {
         if (res < params.length && params[res] != null)
         {
            str += params[res];
         }
      }
      else
      {
         str += res;
      }
   }
   return str;
}

/**
 * 将字符串中需转义的字符设上转义符
 */
function ef_toScriptString(str)
{
   if (str == null)
   {
      return "";
   }
   str = str + "";
   var temp = "";
   for (var i = 0; i < str.length; i++)
   {
      var c = str.charAt(i);
      if (c < " ")
      {
         if (c == "\r")
         {
            temp += "\\r";
         }
         else if (c == "\n")
         {
            temp += "\\n";
         }
         else if (c == "\t")
         {
            temp += "\\t";
         }
         else if (c == "\b")
         {
            temp +=  "\\b";
         }
         else if (c == "\f")
         {
            temp += "\\f";
         }
         else
         {
            temp += " ";
         }
      }
      else if (c == "\"")
      {
         temp += "\\\"";
      }
      else if (c == "'")
      {
         temp += "\\'";
      }
      else if (c == "\\")
      {
         temp += "\\\\";
      }
      else if (c == "/")
      {
         temp += "\\/";
      }
      else
      {
         temp += c;
      }
   }
   return temp;
}

/**
 * 动态加载脚本
 */
function ef_loadScript(flag, scriptPath, recall)
{
   if (eg_cache.loadedScripts[flag])
   {
      return;
   }
   (function() {
      var scriptObj = document.createElement('script');
      scriptObj.type = 'text/javascript';
      scriptObj.async = true;
      scriptObj.src = scriptPath;
      scriptObj.scriptFlag = flag;
      var s = document.getElementsByTagName('script')[0];
      s.parentNode.insertBefore(scriptObj, s);
      if (scriptObj.readyState) //IE
      {
         scriptObj.onreadystatechange = function()
         {
            if (scriptObj.readyState == "complete" || scriptObj.readyState == "loaded")
            {
                eg_cache.loadedScripts[scriptObj.scriptFlag] = 1;
                if (recall != null) recall();
            }
        };
      }
      else //Others
      {
         scriptObj.onload = function()
         {
             eg_cache.loadedScripts[scriptObj.scriptFlag] = 1;
             if (recall != null) recall();
         };
      }
   })();
};

/**
 * 格式化数字显示方式
 * 用法
 * formatNumber(12345.999, "#,##0.00");
 * formatNumber(12345.999, "#,##0.##");
 * formatNumber(123, "000000");
 * @param num
 * @param pattern
 */
function ef_formatNumber(num, pattern)
{
   if (typeof num != "number")
   {
      num = parseFloat(num);
   }
   if (isNaN(num))
   {
      return "?";
   }
   var firstStr = "";
   var lastStr = "";
   var strarr = num ? num.toString().split('.') : ['0'];
   var fmtarr = pattern ? pattern.split('.') : [''];
   var retstr = '';
   var fmt1 = null;
   var fmt2 = null;
   // 处理有多个"."的情况
   for (var i = 0; i < fmtarr.length; i++)
   {
      var tmpStr = fmtarr[i];
      if (fmt1 == null)
      {
         if (/[0#]/.test(tmpStr))
         {
            fmt1 = tmpStr;
            var checkStr = fmt1.substr(fmt1.length - 1, 1);
            if (checkStr != "#" && checkStr != "0" && checkStr != ",")
            {
               fmt2 = "";
            }
            if (i > 0)
            {
               checkStr = tmpStr.substr(0, 1);
               if (checkStr == "#" || checkStr == "0")
               {
                  firstStr = firstStr.substr(0, firstStr.length - 1);
                  fmt1 = "";
                  fmt2 = tmpStr;
               }
            }
         }
         else
         {
            firstStr += tmpStr + (i < fmtarr.length - 1 ? "." : "");
         }
      }
      else if (fmt2 == null)
      {
         fmt2 = tmpStr;
      }
      else
      {
         lastStr += "." + tmpStr;
      }
   }
   if (fmt1 == null)
   {
      fmt1 = "";
      fmt2 = "";
   }
   if (fmt2 == null)
   {
      fmt2 = "";
   }

   /* 整数部分 */
   var str = strarr[0];
   var fmt = fmt1;
   var i = str.length - 1;
   var comma = 0;
   var tmpCommaCount = 0;
   // 处理起始的format字符
   if (fmt.length > 0)
   {
      var tmpI = 0;
      var checkStr = fmt.substr(tmpI++, 1);
      while (checkStr != "#" && checkStr != "0" && checkStr != "," && tmpI < fmt.length)
      {
         firstStr += checkStr;
         checkStr = fmt.substr(tmpI++, 1);
      }
      if (checkStr != "#" && checkStr != "0" && checkStr != ",")
      {
         firstStr += checkStr;
      }
   }
   // 去掉其他的符号
   fmt = fmt.replace(/[^0,#]/g, "");
   // 处理负号
   var negative = "";
   if (str.length > 0)
   {
      if (str.substr(0, 1) == "-")
      {
         str = str.substr(1, str.length - 1);
         negative = "-";
         i--;
      }
   }
   for (var f = fmt.length - 1; f >= 0; f--)
   {
      switch (fmt.substr(f, 1))
      {
         case '#':
            if (i >= 0) retstr = str.substr(i--, 1) + retstr;
            tmpCommaCount++;
            break;
         case '0':
            if (i >= 0) retstr = str.substr(i--, 1) + retstr;
            else retstr = '0' + retstr;
            tmpCommaCount++;
            break;
         case ',':
            comma = tmpCommaCount;
            tmpCommaCount = 0;
            retstr = ',' + retstr;
            break;
      }
   }
   // 如果还有数字并且需要证书部分, 则补充数字
   if (i >= 0 && fmt != "")
   {
      if (comma)
      {
         var l = str.length;
         // 先把第一个","补齐
         if (i >= comma - tmpCommaCount && comma - tmpCommaCount > 0)
         {
            retstr = ',' + str.substr(i - comma + tmpCommaCount + 1, comma - tmpCommaCount) + retstr;
            i -= comma - tmpCommaCount;
         }
         var tmpCount = 0;
         for (; i >= 0; i--)
         {
            tmpCount++;
            retstr = str.substr(i, 1) + retstr;
            // 根据计算得出的","间隔进行补充
            if (i > 0 && (tmpCount % comma) == 0) retstr = ',' + retstr;
         }
      }
      else retstr = str.substr(0, i + 1) + retstr;
  }

   retstr = retstr + '.';
   /* 处理小数部分 */
   str = strarr.length > 1 ? strarr[1] : '';
   var tmpLast = fmt2 == "" ? fmt1 : fmt2;
   fmt = fmt2;
   i = 0;
   // 处理结束的format字符
   if (tmpLast.length > 0)
   {
      var tmpI = tmpLast.length - 1;
      var checkStr = tmpLast.substr(tmpI--, 1);
      while (checkStr != "#" && checkStr != "0" && tmpI >= 0)
      {
         lastStr = checkStr + lastStr;
         checkStr = tmpLast.substr(tmpI--, 1);
      }
      if (checkStr != "#" && checkStr != "0")
      {
         lastStr = checkStr + lastStr;
      }
   }
   // 去掉其他的符号
   fmt = fmt.replace(/[^0#]/g, "");
   for (var f = 0; f < fmt.length; f++)
   {
      switch (fmt.substr(f, 1))
      {
         case '#':
            if (i < str.length) retstr += str.substr(i++, 1);
            break;
         case '0':
            if (i < str.length)
            {
               var tmpChar = str.substr(i++, 1);
               if (tmpChar != "0") retstr += tmpChar;
               else retstr += "(0)";
            }
            else retstr += "(0)"; // 这里补上(0), 以区分不需要的0
            break;
      }
   }
   //alert(retstr);

   var result = retstr.replace(/^,+/, '')     //去除前面的","
         .replace(/0+$/, '')                  //去除末尾的"0"
         .replace(/\(0\)/g, '0')                //将"(0)"替换回"0"
         .replace(/\.$/, '');                 //去除末尾的"."
   //alert(retstr);
   if (/^0*\.?0*$/.test(result))
   {
      negative = "";
   }
   return firstStr + negative + result + lastStr;
}
// Create Base64 Object
var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9+/=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/rn/g,"n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}}
	

//Function to generate GUID.  
	function UUID() {
           var uuid = (function () {
               var i,
                   c = "89ab",
                   u = [];
               for (i = 0; i < 36; i += 1) {
                   u[i] = (Math.random() * 16 | 0).toString(16);
               }
               u[8] = u[13] = u[18] = u[23] = "";
               u[14] = "4";
               u[19] = c.charAt(Math.random() * 4 | 0);
               return u.join("");
           })();
           return {
               toString: function () {
                   return uuid;
               },
               valueOf: function () {
                   return uuid;
               }
           };
       }

  //Fetch value from querystring based on ParameterName
    function GetQueryStringValue(ParameterName) {    
        ParameterName = ParameterName.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");    
        var RegexS = "[\\?&]" + ParameterName + "=([^&#]*)";
        var Regex = new RegExp(RegexS);
        var Results = Regex.exec(window.location.search);
        if (ParameterName == 'GUID') {
            if (Results == null) {
                if ($("#GUIDtxt").val() != '') {
                    return $("#GUIDtxt")[0].value;
                }
                else {
                    //return "";
                   return sessionStorage['GUID'];
                }
            }
        }
        if (Results == null)
            return "";
        else
          return decodeURIComponent(Results[1].replace(/\+/g, " "));
  }
    
    function pad(num, size) {
        var s = "000000000" + num;
        return s.substr(s.length-size);
    }
    

    function generateUUID(){
        var d = new Date().getTime();
        if(window.performance && typeof window.performance.now === "function"){
            d += performance.now(); //use high-precision timer if available
        }
        var uuid = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = (d + Math.random()*16)%16 | 0;
            d = Math.floor(d/16);
            return (c=='x' ? r : (r&0x3|0x8)).toString(16);
        });
        return uuid;
    }
    
    //gets an iframes document in a cross browser compatible manner
    function getiframeDocument($iframe) {
        var iframeDoc = $iframe[0].contentWindow || $iframe[0].contentDocument;
        if (iframeDoc.document) {
            iframeDoc = iframeDoc.document;
        }
        return iframeDoc;
    }
    
    function downloadFile(actionURL, data, callback){
    	
    	 $iframe = jQuery("<iframe style='display: none' src='about:blank'></iframe>").appendTo("body");
    	 formDoc = getiframeDocument($iframe);
    	 strForm = "<form id='frmDownload' action='"+actionURL + "' method='post' enctype='text/plain'>";
    	 for (var key in data) {
    		// skip loop if the property is from prototype
    		if (!data.hasOwnProperty(key)) 
    			continue;
    		
    		if(jQuery.isFunction(data[key])) 
    			continue;
    		
    		strForm = strForm + "<input type='text' id='"+key+"' name='"+key+"' value='" + data[key] + "' />";
    	}
    	 
    	strForm = strForm + "</form>";
    	formDoc.write("<html><head></head><body>" + strForm + "</body></html>");
    	$form = jQuery(formDoc).find('form');
    	$form.submit();
        
    	setTimeout(function(){    	
    		var error = $($iframe[0].contentDocument).find('body').find('h1').text();
    		error = error.replace('HTTP Status 500 - java.lang.Exception:', '');
    		$iframe.remove();
    		callback(error);
    	},  (1000*5) );
        
	}    

    function uploadFile(actionURL, input, callback){
    
    	if(input && input.files.length > 0){
    		var fd = new FormData();    
        	fd.append( 'inputUploadFile', input.files[0] );
        	$(input).val('');

        	$.ajax({
        	  url: actionURL,
        	  data: fd,
        	  processData: false,
        	  contentType: false,
        	  type: 'POST',
        	  success: function(data){
        		  var xmlContent = '';
        		  if(data && typeof(data) == 'string'){
        			  xmlContent = data;
        		  }
        		  else if(data && typeof(data) == 'object'){
        			  xmlContent = new XMLSerializer().serializeToString(data.documentElement);
        		  }    		  
        		  
        		  if(callback){
        			  callback(xmlContent);
        		  }
        	  }
        	});
    	}
   }

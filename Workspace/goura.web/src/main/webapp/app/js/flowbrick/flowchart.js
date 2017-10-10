console.log('Inside flowchartJS');

fabric.Object.prototype.originX = fabric.Object.prototype.originY = 'center';

function wrapText(context, text, x, y, maxWidth, lineHeight) {
    var words = text.split(' ');
    var line = '';

    for(var n = 0; n < words.length; n++) {
      var testLine = line + words[n] + ' ';
      var metrics = context.measureText(testLine);
      var testWidth = metrics.width;
      if (testWidth > maxWidth && n > 0) {
        context.fillText(line, x, y);
        line = words[n] + ' ';
        y += lineHeight;
      }
      else {
        line = testLine;
      }
    }
    context.fillText(line, x, y);
  }


var FlowComponet = Class.extend({
	Type : "Default",
	Inputs : [],
	Outputs: [],
	Properties : {},
	Template : 'Flow Component',	
	
	
	init : function(canvas){
		this.canvas = canvas;
	},
	
	
});

//
//var Decision = fabric.util.createClass(fabric.Rect, {
//
//	  type: 'Decision',
//
//	  initialize: function(options) {
//	    options || (options = { });
//
//	    options.width = 200;
//	    options.height = 200;
//	    options.angle = 45;
//	    options.fill= 'gray';
//	    options.padding = 5;
//	    options.hasRotatingPoint = false;
//	    options.lockUniScaling  = true;	
//	    options.transparentCorners = false;
//	    
//	    this.callSuper('initialize', options);
//	    this.set('label', options.label || this.type);
//	  },
//
//	  toObject: function() {
//	    return fabric.util.object.extend(this.callSuper('toObject'), {
//	      label: this.get('label')
//	    });
//	  },
//	  
//	  mouseUp : function(e, chart){
//		  console.log('Move up event on ' + this.type);
//	  },
//	  
//	  mouseDown : function(e, chart){
//		  console.log('Move down event on ' + this.type);
//	  },
//	  
//	  mouseOver : function(e, chart){
//		  console.log('Move over event on ' + this.type);
//	  },
//	  
//	  mouseOut : function(e, chart){
//		  console.log('Move Out event on ' + this.type);
//	  },
//
//	  _render: function(ctx) {
//		  
//		  
//		// this.width =  ctx.measureText(this.label).width;
//		  
//	    this.callSuper('_render', ctx);
//
//	    ctx.font = '10px Helvetica';
//	    ctx.fillStyle = '#333';
//	    ctx.rotate(-45*Math.PI/180);
//	    ctx.textAlign="center"; 	    
//	    wrapText(ctx, this.label, 0, 3, 100, 10 );	    
//	 
//	  }
//	});



var Decision = FlowComponet.extend({
	init:function(canvas, x , y){
		this._super(canvas);
		this.Type = 'Decision';
		this.Template = "Decision";
		
		var rect = new fabric.Rect({
			  fill: 'gray',
			  width: 50,
			  angle: 45,
			  height: 50,
			  originX: 'center',
			  originY: 'center'
		});			
		
		rect.on('mouseup', function(e) { 
	        // e.target should be the circle
	        console.log(e.target);
	    });
		
		var out = new fabric.Circle({
			radius: 10, 
			originX: 'center',
			originY: 'center',
			x : (-rect.width/2)
		});
			
		var text = new fabric.Text(this.Template, {
				fontSize: 12, 
				originX: 'center',
				originY: 'center'
		});
			
		var group = new fabric.Group([ rect, text, out ], {
				  left: x,
				  top: y,
				  subTargetCheck: true
		});
			
		group.hasRotatingPoint = false;
		group.lockUniScaling  = true;			
			
		this.canvas.add(group);	
	}
});


var Flowchart = Class.extend({
	  init: function(options){
		  this.options = options ? options : {};
		  this.canvas = new fabric.Canvas('canvas');	
		  var chart = this;
		  
		  
		  this.canvas.on('mouse:up', function(e) {
			  if(e.target && e.target.mouseUp){
			  		e.target.mouseUp(e, chart);
			  	}
		  });
		  
		  this.canvas.on('mouse:down', function(e) {
			  if(e.target && e.target.mouseDown){
			  		e.target.mouseDown(e, chart);
			  	}
		  });
		  
		  this.canvas.on('mouse:over', function(e) {
			  	if(e.target && e.target.mouseOver){
			  		e.target.mouseOver(e, chart);
			  	}
		  });

		  this.canvas.on('mouse:out', function(e) {
			  
			  if(e.target && e.target.mouseOut){
			  		e.target.mouseOut(e, chart);
			  }
		 });
	  },
	  
	  getCanvas : function(){
		  return this.canvas;
	  },
	  
	  setSize : function(width, height){
		  if(width && height){
			  this.canvas.setDimensions({width: width, height: height});
		  }
	  },
	  
	  addDecision :function(x, y){
//			var d = new Decision(canvas, {
//				left: 220,
//				top: 100,
//			});
		  var d = new Decision(this.canvas, 220, 100);
			//this.canvas.add(d);	
	  },
		
	  toJson : function(){
			return this.canvas.toJSON();
	  }
	  
	});

var chart = new Flowchart();
chart.getCanvas().setDimensions({width: $(window).width(), height: $(window).height()});

chart.addDecision(0, 0);

console.log(chart.toJson());

$(window).resize(function(){
	chart.getCanvas().setDimensions({width: $(window).width(), height: $(window).height()});
})
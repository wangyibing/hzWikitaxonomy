
	function DrawEntityMap(titles) {
	var jsonPath = Titles2EntityGraph(titles);
	var width = 1200;
    var height = 600;
	
	var color = d3.scale.category20();
	
	var svg = d3.select("body").append("svg")
		.attr("width", width)
		.attr("height", height);
	
	var force = d3.layout.force()
		.charge(-150)
		.linkDistance(function(d) { 
return 200;
     })
		.size([width, height]);	
	
	d3.json(jsonPath, function(error, graph) {
	  force
		  .nodes(graph.nodes)
		  .links(graph.links)
		  .start();
	
	var link = svg.selectAll(".link")
	  .data(graph.links)
	  .enter()
	  .append("line")
	  .attr("class", "link")
	  .attr("stroke","#09F")
	  .attr("stroke-opacity","0.4")
                .attr('cursor','pointer')
	  .style("stroke-width",1)
                .attr("marker-end",function(d){  
                    return "url(#marker-" + (d.target.r) + ")";
                });;
	  
	var node = svg.selectAll(".node")
	  .data(graph.nodes)
	  .enter()
	  .append("g")
	  .call(force.drag);
	
	node.append("circle")
	  .attr("class", "node")
	  .attr("r",function(d){return 10+d.group;})
	  .style("fill", function(d) { return color(d.group); });
	
	node.append("title")
	  .text(function(d) { return d.name; });
	
	node.append("text")
	  .attr("dy", ".3em")
	  .attr("class","nodetext")
	  .style("text-anchor", "middle")
	  .text(function(d) { return d.name; });
	
	force.on("tick", function() {
		link.attr("x1", function(d) { return d.source.x; })
			.attr("y1", function(d) { return d.source.y; })
			.attr("x2", function(d) { return d.target.x; })
			.attr("y2", function(d) { return d.target.y; });
		
		node.attr("transform", function(d){ return "translate("+d.x+"," + d.y + ")";});
	});
	});
}
function Titles2EntityGraph(titles) {

<script type="text/javascript">
   
    var acOrganizedByOptions = {
    minChars: 5,
    max: 25,
	dataType: "json",
    parse: function(data){
	  var parsed = [];
	  for(var i = 0; i < data.length; i++)
	  {
        parsed[parsed.length] = { data: data[i], value: data[i].name, result: data[i].name };
      }
	  return parsed;
	},
	formatItem: function(item){
      return item.name;
    }
   };
   
   var acLocationsOptions = {
    minChars: 0,
    max: 25,
	dataType: "json",
    parse: function(data){
	  var parsed = [];
	  for(var i = 0; i < data.length; i++)
	  {
        parsed[parsed.length] = { data: data[i], value: data[i], result: data[i] };
      }
	  return parsed;
	},
	formatItem: function(item){
      return item;
    }
   };
   
   function flushCatchOrgIfEmpty(prefix,id)
   {
     if($("#"+prefix+"_"+id+"_city").val()=="" 
	  && $("#"+prefix+"_"+id+"_postCode").val()=="" 
      && $("#"+prefix+"_"+id+"_province").val()=="")
	  {
		    $( "#"+prefix+"_"+id+"_city" ).flushCache();
		    $( "#"+prefix+"_"+id+"_postCode" ).flushCache();
		    $( "#"+prefix+"_"+id+"_province" ).flushCache();
      }
   }   
   
   function flushCatchIfEmpty(prefix)
   {
     if($("#"+prefix+"_city").val()=="" 
	  && $("#"+prefix+"_postcode").val()=="" 
      && $("#"+prefix+"_province").val()=="")
	  {
		    $( "#"+prefix+"_city" ).flushCache();
		    $( "#"+prefix+"_postcode" ).flushCache();
		    $( "#"+prefix+"_province" ).flushCache();
      }
   }   
   
   function init_organization_autocomplete(prefix, id, jsonOrganizationDataUrl, jsonLocationDataUrl)
   {
   		$( "#"+prefix+"_"+id+"_name" ).autocomplete(jsonOrganizationDataUrl, acOrganizedByOptions)
   		.result(function(e, item){ $("#"+prefix+"_"+id+"_id").val(item.id); 
								   $("#"+prefix+"_"+id+"_street").val(item.street);
		                           $("#"+prefix+"_"+id+"_city").val(item.city); 
		                           $("#"+prefix+"_"+id+"_postCode").val(item.postCode); 
		                           $("#"+prefix+"_"+id+"_province").val(item.province); }); // fill data when complete

		$( "#"+prefix+"_"+id+"_city" ).autocomplete(jsonLocationDataUrl, acLocationsOptions)
		.setOptions({ extraParams: { qpostcode: function() { return $( "#"+prefix+"_"+id+"_postCode" ).val(); },
                                     qprovince: function() { return $( "#"+prefix+"_"+id+"_province" ).val(); },
                                     qtype: "city"}
        })
		.result(function(e, item){
		    $( "#"+prefix+"_"+id+"_postCode" ).flushCache();
		    $( "#"+prefix+"_"+id+"_province" ).flushCache();
		})
		.focus(function(){ $( "#"+prefix+"_"+id+"_city" ).click(); })
		.blur(function(){ flushCatchOrgIfEmpty(prefix,id); });
		
		$( "#"+prefix+"_"+id+"_postCode" ).autocomplete(jsonLocationDataUrl, acLocationsOptions)
		.setOptions({ extraParams: { qcity: function() { return $( "#"+prefix+"_"+id+"_city" ).val(); },
       								 qprovince: function() { return $( "#"+prefix+"_"+id+"_province" ).val(); },
       								 qtype: "postcode" }
        })
		.result(function(e, item){
		    $( "#"+prefix+"_"+id+"_city" ).flushCache();
		    $( "#"+prefix+"_"+id+"_province" ).flushCache();
		})
		.focus(function(){ $( "#"+prefix+"_"+id+"_postCode" ).click(); })
		.blur(function(){ flushCatchOrgIfEmpty(prefix,id); });
				
		$( "#"+prefix+"_"+id+"_province" ).autocomplete(jsonLocationDataUrl, acLocationsOptions)
		.setOptions({ extraParams: { qcity: function() { return $( "#"+prefix+"_"+id+"_city" ).val(); },
       								 qpostcode: function() { return $( "#"+prefix+"_"+id+"_postCode" ).val(); },
                                     qtype: "province"}
        })
		.result(function(e, item){
		    $( "#"+prefix+"_"+id+"_city" ).flushCache();
		    $( "#"+prefix+"_"+id+"_postCode" ).flushCache();
		})
		.focus(function(){ $( "#"+prefix+"_"+id+"_province" ).click(); })
		.blur(function(){ flushCatchOrgIfEmpty(prefix,id); });
   }
   
   function init_locations_autocomplete(prefix, jsonDataUrl)
   {
		$( "#"+prefix+"_city" ).autocomplete(jsonDataUrl, acLocationsOptions)
		.setOptions({ extraParams: { qpostcode: function() { return $( "#"+prefix+"_postcode" ).val(); },
                                     qprovince: function() { return $( "#"+prefix+"_province" ).val(); },
                                     qtype: "city"}
        })
		.result(function(e, item){
		    $( "#"+prefix+"_postcode" ).flushCache();
		    $( "#"+prefix+"_province" ).flushCache();
		})
		.focus(function(){ $( "#"+prefix+"_city" ).click(); })
		.blur(function(){ flushCatchIfEmpty(prefix); });
		
		$( "#"+prefix+"_postcode" ).autocomplete(jsonDataUrl, acLocationsOptions)
		.setOptions({ extraParams: { qcity: function() { return $( "#"+prefix+"_city" ).val(); },
       								 qprovince: function() { return $( "#"+prefix+"_province" ).val(); },
       								 qtype: "postcode" }
        })
		.result(function(e, item){
		    $( "#"+prefix+"_city" ).flushCache();
		    $( "#"+prefix+"_province" ).flushCache();
		})
		.focus(function(){ $( "#"+prefix+"_postcode" ).click(); })
		.blur(function(){ flushCatchIfEmpty(prefix); });
				
		$( "#"+prefix+"_province" ).autocomplete(jsonDataUrl, acLocationsOptions)
		.setOptions({ extraParams: { qcity: function() { return $( "#"+prefix+"_city" ).val(); },
       								 qpostcode: function() { return $( "#"+prefix+"_postcode" ).val(); },
                                     qtype: "province"}
        })
		.result(function(e, item){
		    $( "#"+prefix+"_city" ).flushCache();
		    $( "#"+prefix+"_postcode" ).flushCache();
		})
		.focus(function(){ $( "#"+prefix+"_province" ).click(); })
		.blur(function(){ flushCatchIfEmpty(prefix); });
   }
	
</script>
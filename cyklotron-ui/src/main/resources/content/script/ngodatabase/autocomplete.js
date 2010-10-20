var acOrganizedByOptions = {
	minChars : 5,
	max : 25,
	dataType : "json",
	parse : function(data) {
		var parsed = [];
		for ( var i = 0; i < data.length; i++) {
		    if(data[i+1] && data[i].name == data[i+1].name)
		    {
		      data[i].ambiguous = true;
		      data[i+1].ambiguous = true;
		    }
			parsed[parsed.length] = {
				data : data[i],
				value : data[i].name,
				result : data[i].name
			};
		}
		return parsed;
	},
	formatItem : function(item) {
		if (item.ambiguous) {
			return item.name + " (" + item.city + ")";
		} else {
			return item.name;
		}
	},
	formatMatch : function(item) {
		return item.name;
	}
};

var acLocationsOptions = {
	minChars : 0,
	max : 25,
	dataType : "json",
	parse : function(data) {
		var parsed = [];
		for ( var i = 0; i < data.length; i++) {
			parsed[parsed.length] = {
				data : data[i],
				value : data[i],
				result : data[i]
			};
		}
		return parsed;
	},
	formatItem : function(item) {
		return item;
	}
};

function flushCacheIfEmpty(fields) {
	if (fields.city.val() == "" && fields.postCode.val() == ""
			&& fields.province.val() == "") {
		fields.city.flushCache();
		fields.postCode.flushCache();
		fields.province.flushCache();
	}
}

function initOrganizationAutocomplete(fields, jsonOrganizationDataUrl) {
	fields.name.autocomplete(jsonOrganizationDataUrl, acOrganizedByOptions)
			.result(function(e, item) {
				fields.id.val(item.id);
				fields.street.val(item.street);
				fields.city.val(item.city);
				fields.postCode.val(item.postCode);
				fields.province.val(item.province);
			}); // fill data when complete
}

function initLocationAutocomplete(fields, jsonDataUrl) {
	fields.city.autocomplete(jsonDataUrl, acLocationsOptions).setOptions( {
		extraParams : {
			qpostCode : function() {
				return fields.postCode.val();
			},
			qprovince : function() {
				return fields.province.val();
			},
			qfield : "city"
		}
	}).result(function(e, item) {
		fields.postCode.flushCache();
		fields.province.flushCache();
	}).focus(function() {
		fields.city.click();
	}).blur(function() {
		flushCacheIfEmpty(fields);
	});

	fields.postCode.autocomplete(jsonDataUrl, acLocationsOptions).setOptions( {
		extraParams : {
			qcity : function() {
				return fields.city.val();
			},
			qprovince : function() {
				return fields.province.val();
			},
			qfield : "postCode"
		}
	}).result(function(e, item) {
		fields.city.flushCache();
		fields.province.flushCache();
	}).focus(function() {
		fields.postCode.click();
	}).blur(function() {
		flushCacheIfEmpty(fields);
	});

	fields.province.autocomplete(jsonDataUrl, acLocationsOptions).setOptions( {
		extraParams : {
			qcity : function() {
				return fields.city.val();
			},
			qpostCode : function() {
				return fields.postCode.val();
			},
			qfield : "province"
		}
	}).result(function(e, item) {
		fields.city.flushCache();
		fields.postCode.flushCache();
	}).focus(function() {
		fields.province.click();
	}).blur(function() {
		flushCacheIfEmpty(fields);
	});
}

function proposeDocumentOrgFields(index) {
	var fields = {};
	fields["name"] = $("#organization_" + index + "_name");
	fields["id"] = $("#organization_" + index + "_id");
	fields["street"] = $("#organization_" + index + "_street");
	fields["postCode"] = $("#organization_" + index + "_postCode");
	fields["city"] = $("#organization_" + index + "_city");
	fields["province"] = $("#organization_" + index + "_province");
	return fields;
}

function proposeDocumentEventFields() {
	var fields = {};
	fields["street"] = $("#event_street");
	fields["postCode"] = $("#event_postCode");
	fields["city"] = $("#event_city");
	fields["province"] = $("#event_province");
	return fields;
}

function editDocumentOrgFields(index) {
	var fields = {};
	fields["name"] = $("#form\\.page-0\\.group-3\\.repeat-1\\.repeatSubTree-"
			+ (index - 1) + "\\.group-0\\.input-0");
	fields["id"] = $("#form\\.page-0\\.group-3\\.repeat-1\\.repeatSubTree-"
			+ (index - 1) + "\\.group-0\\.hidden-6");
	fields["street"] = $("#form\\.page-0\\.group-3\\.repeat-1\\.repeatSubTree-"
			+ (index - 1) + "\\.group-0\\.group-1\\.input-0");
	fields["postCode"] = $("#form\\.page-0\\.group-3\\.repeat-1\\.repeatSubTree-"
			+ (index - 1) + "\\.group-0\\.group-1\\.input-1");
	fields["city"] = $("#form\\.page-0\\.group-3\\.repeat-1\\.repeatSubTree-"
			+ (index - 1) + "\\.group-0\\.group-1\\.input-2");
	fields["province"] = $("#form\\.page-0\\.group-3\\.repeat-1\\.repeatSubTree-"
			+ (index - 1) + "\\.group-0\\.group-1\\.input-3");
	return fields;
}

function editDocumentEventFields() {
	var fields = {};
	fields["street"] = $("#form\\.page-0\\.group-4\\.group-1\\.group-0\\.input-0");
	fields["postCode"] = $("#form\\.page-0\\.group-4\\.group-1\\.group-0\\.input-1");
	fields["city"] = $("#form\\.page-0\\.group-4\\.group-1\\.group-0\\.input-2");
	fields["province"] = $("#form\\.page-0\\.group-4\\.group-1\\.group-0\\.input-3");
	return fields;
}

function initProposeDocumentAutocomplete(maxOrgs, jsonOrganizationDataUrl,
		jsonLocationDataUrl) {
	for(var i = 1; i <= maxOrgs; i++) {
		var fields = proposeDocumentOrgFields(i);
			if(typeof fields.name != 'undefined') {
				initOrganizationAutocomplete(fields, jsonOrganizationDataUrl);
				initLocationAutocomplete(fields, jsonLocationDataUrl);
			}
	}
	initLocationAutocomplete(proposeDocumentEventFields(), jsonLocationDataUrl);
}

function initEditDocumentAutocomplete(maxOrgs, jsonOrganizationDataUrl,
		jsonLocationDataUrl) {
	for(var i = 1; i <= maxOrgs; i++) {
		var fields = editDocumentOrgFields(i);
			if(typeof fields.name != 'undefined') {
				initOrganizationAutocomplete(fields, jsonOrganizationDataUrl);
				initLocationAutocomplete(fields, jsonLocationDataUrl);
			}
	}
	initLocationAutocomplete(editDocumentEventFields(), jsonLocationDataUrl);
}

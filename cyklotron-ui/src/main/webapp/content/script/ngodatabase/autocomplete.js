var acOrganizedByOptions = {
	minChars : 1,
	max : 25,
	autoFocus : true,
	allowHide : true,
	clearIfNotMatch : false,
	mustMatch : false,
	selectFirst : false,
	cacheLength : 0,
	dataType : "json",
	parse : function(data) {
		var parsed = [];
		for ( var i = 0; i < data.length; i++) {
			parsed[parsed.length] = {
				data : data[i],
				value : data[i].name,
				result : data[i].name
			};
		}
		return parsed;
	},
	formatItem : function(item) {
		return item.name + " (" + item.city + ")";
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
		if (data.fieldValues) {
			for ( var i = 0; i < data.fieldValues.length; i++) {
				parsed[parsed.length] = {
					data : {
						"fieldValue" : data.fieldValues[i],
						"uniqueLocations" : data.uniqueLocations[data.fieldValues[i]]
					},
					value : data.fieldValues[i],
					result : data.fieldValues[i]
				};
			}
		}
		return parsed;
	},
	formatItem : function(item) {
		return item.fieldValue;
	},
	formatMatch : function(item) {
		return item.fieldValue;
	}
};

function flushCacheIfEmpty(fields) {
	if (fields.city.val() == "" && fields.postCode.val() == ""
			&& fields.street.val() == "" && fields.province.val() == "") {
		fields.street.flushCache();
		fields.city.flushCache();
		fields.postCode.flushCache();
		fields.province.flushCache();
	}
}

function clearFields(fields) {
	fields.id.val("0");
	fields.id.attr("rel", "");
	fields.street.val("");
	fields.city.val("");
	fields.postCode.val("");
	fields.province.val("");
	flushCacheIfEmpty(fields);
}

function initOrganizationAutocomplete(fields, jsonOrganizationDataUrl) {
	fields.name.autocomplete(jsonOrganizationDataUrl, acOrganizedByOptions)
			.result(function(e, item) {
				fields.id.val(item.id);
				fields.id.attr("rel", item.name);
				fields.street.val(item.street);
				fields.city.val(item.city);
				fields.postCode.val(item.postCode);
				fields.province.val(item.province);
			}).change(
					function(e, item) {
						if (acOrganizedByOptions.clearIfNotMatch
								&& fields.id.attr("rel") != 'undefined'
								&& fields.id.attr("rel") != ""
								&& $(this).val() != fields.id.attr("rel")) {
							clearFields(fields);
						}
					}); // fill data when complete
}

function initLocationAutocomplete(fields, jsonDataUrl) {
	fields.street.autocomplete(jsonDataUrl, acLocationsOptions).setOptions({
		extraParams : {
			qcity : function() {
				return fields.city.val();
			},
			qpostCode : function() {
				return fields.postCode.val();
			},
			qprovince : function() {
				return fields.province.val();
			},
			qfield : "street"
		}
	}).result(function(e, item) {
		fields.city.flushCache();
		fields.postCode.flushCache();
		fields.province.flushCache();
	}).focus(function() {
		fields.street.click();
	}).blur(function() {
		flushCacheIfEmpty(fields);
	});

	fields.city.autocomplete(jsonDataUrl, acLocationsOptions).setOptions({
		extraParams : {
			qstreet : function() {
				return fields.street.val();
			},
			qpostCode : function() {
				return fields.postCode.val();
			},
			qprovince : function() {
				return fields.province.val();
			},
			qfield : "city"
		}
	}).result(function(e, item) {
		fields.street.flushCache();
		fields.postCode.flushCache();
		fields.province.flushCache();
	}).focus(function() {
		fields.city.click();
	}).blur(function() {
		flushCacheIfEmpty(fields);
	});

	fields.postCode.autocomplete(jsonDataUrl, acLocationsOptions).setOptions({
		extraParams : {
			qstreet : function() {
				return fields.street.val();
			},
			qcity : function() {
				return fields.city.val();
			},
			qprovince : function() {
				return fields.province.val();
			},
			qfield : "postCode"
		}
	}).result(function(e, item) {
		fields.street.flushCache();
		fields.city.flushCache();
		fields.province.flushCache();
	}).focus(function() {
		fields.postCode.click();
	}).blur(function() {
		flushCacheIfEmpty(fields);
	});

	fields.province.autocomplete(jsonDataUrl, acLocationsOptions).setOptions({
		extraParams : {
			qstreet : function() {
				return fields.street.val();
			},
			qcity : function() {
				return fields.city.val();
			},
			qpostCode : function() {
				return fields.postCode.val();
			},
			qfield : "province"
		}
	}).result(function(e, item) {
		fields.street.flushCache();
		fields.city.flushCache();
		fields.postCode.flushCache();
	}).focus(function() {
		fields.province.click();
	}).blur(function() {
		flushCacheIfEmpty(fields);
	});
}

function flushFullLocationCacheIfEmpty(fields, object) {
	var object = object || null;
	for (field in fields) {
		if (object != fields[field]) {
			fields[field].flushCache();
		}
	}
}

function initFullLocationAutocomplete(fields, jsonDataUrl) {
	fields.street.autocomplete(jsonDataUrl, acLocationsOptions).setOptions({
		extraParams : {
			qarea : function() {
				return fields.area.val();
			},
			qcity : function() {
				return fields.city.val();
			},
			qpostCode : function() {
				return fields.postCode.val();
			},
			qcommune : function() {
				return fields.commune.val();
			},
			qdistrict : function() {
				return fields.district.val();
			},
			qprovince : function() {
				return fields.province.val();
			},
			qfield : "street"
		}
	}).result(function(e, item) {
		flushFullLocationCacheIfEmpty(fields, fields.street);
	}).focus(function() {
		fields.street.click();
	}).blur(function() {
		flushFullLocationCacheIfEmpty(fields);
	});

	fields.area.autocomplete(jsonDataUrl, acLocationsOptions).setOptions({
		extraParams : {
			qstreet : function() {
				return fields.street.val();
			},
			qcity : function() {
				return fields.city.val();
			},
			qpostCode : function() {
				return fields.postCode.val();
			},
			qcommune : function() {
				return fields.commune.val();
			},
			qdistrict : function() {
				return fields.commune.val();
			},
			qprovince : function() {
				return fields.province.val();
			},
			qfield : "area"
		}
	}).result(function(e, item) {
		flushFullLocationCacheIfEmpty(fields, fields.area);
	}).focus(function() {
		fields.area.click();
	}).blur(function() {
		flushFullLocationCacheIfEmpty(fields);
	});

	fields.city.autocomplete(jsonDataUrl, acLocationsOptions).setOptions({
		extraParams : {
			qstreet : function() {
				return fields.street.val();
			},
			qpostCode : function() {
				return fields.postCode.val();
			},
			qarea : function() {
				return fields.area.val();
			},
			qcommune : function() {
				return fields.commune.val();
			},
			qdistrict : function() {
				return fields.district.val();
			},
			qprovince : function() {
				return fields.province.val();
			},
			qfield : "city"
		}
	}).result(function(e, item) {
		flushFullLocationCacheIfEmpty(fields, fields.city);
	}).focus(function() {
		fields.city.click();
	}).blur(function() {
		flushFullLocationCacheIfEmpty(fields);
	});

	fields.postCode.autocomplete(jsonDataUrl, acLocationsOptions).setOptions({
		extraParams : {
			qstreet : function() {
				return fields.street.val();
			},
			qarea : function() {
				return fields.area.val();
			},
			qcity : function() {
				return fields.city.val();
			},
			qcommune : function() {
				return fields.commune.val();
			},
			qdistrict : function() {
				return fields.district.val();
			},
			qprovince : function() {
				return fields.province.val();
			},
			qfield : "postCode"
		}
	}).result(function(e, item) {
		flushFullLocationCacheIfEmpty(fields, fields.postCode);
	}).focus(function() {
		fields.postCode.click();
	}).blur(function() {
		flushFullLocationCacheIfEmpty(fields);
	});

	fields.commune.autocomplete(jsonDataUrl, acLocationsOptions).setOptions({
		extraParams : {
			qstreet : function() {
				return fields.street.val();
			},
			qarea : function() {
				return fields.area.val();
			},
			qcity : function() {
				return fields.city.val();
			},
			qpostCode : function() {
				return fields.postCode.val();
			},
			qdistrict : function() {
				return fields.commune.val();
			},
			qprovince : function() {
				return fields.province.val();
			},
			qfield : "commune"
		}
	}).result(function(e, item) {
		flushFullLocationCacheIfEmpty(fields, fields.commune);
	}).focus(function() {
		fields.commune.click();
	}).blur(function() {
		flushFullLocationCacheIfEmpty(fields);
	});

	fields.district.autocomplete(jsonDataUrl, acLocationsOptions).setOptions({
		extraParams : {
			qstreet : function() {
				return fields.street.val();
			},
			qarea : function() {
				return fields.area.val();
			},
			qcity : function() {
				return fields.city.val();
			},
			qpostCode : function() {
				return fields.postCode.val();
			},
			qcommune : function() {
				return fields.commune.val();
			},
			qprovince : function() {
				return fields.province.val();
			},
			qfield : "district"
		}
	}).result(function(e, item) {
		flushFullLocationCacheIfEmpty(fields, fields.district);
	}).focus(function() {
		fields.district.click();
	}).blur(function() {
		flushFullLocationCacheIfEmpty(fields);
	});

	fields.province.autocomplete(jsonDataUrl, acLocationsOptions).setOptions({
		extraParams : {
			qstreet : function() {
				return fields.street.val();
			},
			qarea : function() {
				return fields.area.val();
			},
			qcity : function() {
				return fields.city.val();
			},
			qpostCode : function() {
				return fields.postCode.val();
			},
			qcommune : function() {
				return fields.commune.val();
			},
			qdistrict : function() {
				return fields.district.val();
			},
			qfield : "province"
		}
	}).result(function(e, item) {
		flushFullLocationCacheIfEmpty(fields, fields.province);
	}).focus(function() {
		fields.province.click();
	}).blur(function() {
		flushFullLocationCacheIfEmpty(fields);
	});
}

function proposeDocumentOrgFields(index) {
	var fields = {};
	fields["name"] = jQuery("#organization_" + index + "_name");
	fields["id"] = jQuery("#organization_" + index + "_id");
	fields["street"] = jQuery("#organization_" + index + "_street");
	fields["postCode"] = jQuery("#organization_" + index + "_postCode");
	fields["city"] = jQuery("#organization_" + index + "_city");
	fields["province"] = jQuery("#organization_" + index + "_province");
	return fields;
}

function proposeDocumentEventFields() {
	var fields = {};
	fields["street"] = jQuery("#event_street");
	fields["postCode"] = jQuery("#event_postCode");
	fields["city"] = jQuery("#event_city");
	fields["province"] = jQuery("#event_province");
	return fields;
}

function editDocumentOrgFields(index) {
	var fields = {};
	fields["name"] = jQuery("#form\\.page-0\\.group-4\\.repeat-1\\.repeatSubTree-"
			+ (index - 1) + "\\.group-0\\.input-0");
	fields["id"] = jQuery("#form\\.page-0\\.group-4\\.repeat-1\\.repeatSubTree-"
			+ (index - 1) + "\\.group-0\\.hidden-6");
	fields["street"] = jQuery("#form\\.page-0\\.group-4\\.repeat-1\\.repeatSubTree-"
			+ (index - 1) + "\\.group-0\\.group-1\\.input-0");
	fields["postCode"] = $("#form\\.page-0\\.group-4\\.repeat-1\\.repeatSubTree-"
			+ (index - 1) + "\\.group-0\\.group-1\\.input-1");
	fields["city"] = jQuery("#form\\.page-0\\.group-4\\.repeat-1\\.repeatSubTree-"
			+ (index - 1) + "\\.group-0\\.group-1\\.input-2");
	fields["province"] = jQuery("#form\\.page-0\\.group-4\\.repeat-1\\.repeatSubTree-"
			+ (index - 1) + "\\.group-0\\.group-1\\.input-3");
	return fields;
}

function editDocumentEventFields() {
	var fields = {};
	fields["street"] = jQuery("#form\\.page-0\\.group-5\\.group-1\\.group-0\\.input-0");
	fields["postCode"] = jQuery("#form\\.page-0\\.group-5\\.group-1\\.group-0\\.input-1");
	fields["city"] = jQuery("#form\\.page-0\\.group-5\\.group-1\\.group-0\\.input-2");
	fields["province"] = jQuery("#form\\.page-0\\.group-5\\.group-1\\.group-0\\.input-3");
	return fields;
}

function initProposeDocumentAutocomplete(maxOrgs, jsonOrganizationDataUrl,
		jsonLocationDataUrl, clearIfNotMatch) {
	if (typeof (clearIfNotMatch) != 'undefined') {
		acOrganizedByOptions.clearIfNotMatch = clearIfNotMatch;
	}
	for ( var i = 1; i <= maxOrgs; i++) {
		var fields = proposeDocumentOrgFields(i);
		if (typeof fields.name != 'undefined') {
			initOrganizationAutocomplete(fields, jsonOrganizationDataUrl);
			initLocationAutocomplete(fields, jsonLocationDataUrl);
		}
	}
	initLocationAutocomplete(proposeDocumentEventFields(), jsonLocationDataUrl);
}

function initEditDocumentAutocomplete(maxOrgs, jsonOrganizationDataUrl,
		jsonLocationDataUrl, clearIfNotMatch) {
	if (typeof (clearIfNotMatch) != 'undefined') {
		acOrganizedByOptions.clearIfNotMatch = clearIfNotMatch;
	}
	for ( var i = 1; i <= maxOrgs; i++) {
		var fields = editDocumentOrgFields(i);
		if (typeof fields.name != 'undefined') {
			initOrganizationAutocomplete(fields, jsonOrganizationDataUrl);
			initLocationAutocomplete(fields, jsonLocationDataUrl);
		}
	}
	initLocationAutocomplete(editDocumentEventFields(), jsonLocationDataUrl);
}

function locationFields(fieldsIds) {
	fieldsIds = fieldsIds || {};
	var fields = {};
	fields["street"] = fieldsIds["street"] != 'undefined' ? jQuery("#"
			+ fieldsIds["street"]) : "";
	fields["postCode"] = fieldsIds["postCode"] != 'undefined' ? jQuery("#"
			+ fieldsIds["postCode"]) : "";
	fields["area"] = fieldsIds["area"] != 'undefined' ? jQuery("#"
			+ fieldsIds["area"]) : "";
	fields["city"] = fieldsIds["city"] != 'undefined' ? jQuery("#"
			+ fieldsIds["city"]) : "";
	fields["commune"] = fieldsIds["commune"] != 'undefined' ? jQuery("#"
			+ fieldsIds["commune"]) : "";
	fields["district"] = fieldsIds["district"] != 'undefined' ? jQuery("#"
			+ fieldsIds["district"]) : "";
	fields["province"] = fieldsIds["province"] != 'undefined' ? jQuery("#"
			+ fieldsIds["province"]) : "";
	return fields;
}

function initFullFieldsLocationAutocomplete(fieldsIds, jsonLocationDataUrl) {
	initFullLocationAutocomplete(locationFields(fieldsIds), jsonLocationDataUrl);
}
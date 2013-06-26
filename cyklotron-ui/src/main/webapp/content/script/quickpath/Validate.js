/**
 * Setup quickpath validation. Requires jQuery.
 * 
 * Elements passed to this method should be wreapped with jQuery.
 * 
 * @param endpoint
 *            the URL of REST endpoint of URLRewriteRegistry
 *            "$link.rootContent('/rest/rewriteRegistry')"
 * @param locale
 *            identifier of user locale (language preferences) "$i18n.locale"
 * @param site
 *            of the site where the path is being defined (a String)
 *            "$site.name"
 * @param inputElem
 *            the input element with current value of the path
 * @param feedbackElem
 *            the element where feedback should be provided
 */
function quickPathValidation(endpoint, locale, site, inputElem, feedbackElem) {
    var i18n;
    if (locale == 'pl_PL') {
        i18n = {
            OK : "OK",
            NO_SLASH : "Ścieżka musi rozpoczynać się znakiem /",
            UNDERSCORES : "Ścieżka nie może zawierać sekwencji znaków __",
            IN_USE : "Ta ścieżka jest już zajęta",
            SERVER_ERROR : "Nie udało się sprawdzić ścieżki na serwerze"
        };
    } else {
        i18n = {
            OK : "OK",
            NO_SLASH : "Path must start with /",
            UNDERSCORES : "Path may not contain __ character sequence",
            IN_USE : "Path is already in use",
            SERVER_ERROR : "Failed to check path validaity due to server error"
        };
    }

    function feedback(message) {
        feedbackElem.css("color", (message == "OK") ? "green" : "red");
        feedbackElem.text(i18n[message]);
    }

    var origValue = inputElem.val();
    feedback("OK");

    var ajaxRequest = null;
    var curValue = null;

    function ajaxValidation() {
        if (ajaxRequest != null) {
            ajaxRequest.abort();
        }
        ajaxRequest = $.ajax(endpoint + "/" + site + "/" + curValue.replace(/\//g, "__"), {
            dataType : "json",
            success : function(data) {
                if (data.defined) {
                    feedback("IN_USE");
                } else {
                    feedback("OK");
                }
            },
            error : function() {
                feedback("SERVER_ERROR");
            },
            complete : function() {
                ajaxRequest = null;
            }
        });
    }

    var timeoutId = null;

    function serverValidation() {
        timeoutId = window.setTimeout(function() {
            ajaxValidation();
            timeoutId = null;
        }, 1000);
    }

    inputElem.on('keyup', function(event) {
        if (timeoutId != null) {
            window.clearTimeout(timeoutId);
            timeoutId = null;
        }
        curValue = inputElem.val();
        if (curValue != "" && curValue != origValue) {
            if (curValue.charAt(0) != '/') {
                feedback("NO_SLASH");
            } else if (/__/.test(curValue)) {
                feedback("UNDERSCORES");
            } else {
                serverValidation();
            }
        } else {
            feedback("OK");
        }
        prevValue = curValue;
    });
}

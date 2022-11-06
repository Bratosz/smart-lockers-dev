function getAndLoad(request, display) {
    $.ajax({
        url: request(),
        method: 'get',
        success: function (response) {
            display(response);
        }
    });
}

function formatDateDMY(date) {
    if(date == null) {
        return "";
    }
    date = date.substring(0, 10);
    if (date == "1970-01-01") {
        return "";
    } else {
        return date;
    }
}

function getActualLocation() {
    return window.location.origin;
}

function addFieldToObjects(fieldName, content, objects) {
    let result = [];
    for(let o of objects) {
        o[fieldName] = content;
        result.push(o);
    }
    return result;
}

function collectionToString(collection) {
    let result = "";
    for(let e of collection) {
        if(result.length > 0) result += " ";
        result += e + ","
    }
    return result;
}

function sort(collection, prop1st, prop2nd, prop3rd, prop4th, prop5th) {
    if(prop1st === undefined) {
      collection.sort(function (a, b) {
          return compare(a, b);
      });
      return collection;
    } else if(prop2nd === undefined) {
        collection.sort(function (a,b) {
            return compare(fetchFrom(a, prop1st), fetchFrom(b, prop1st));
        });
        return collection;
    } else if (prop3rd === undefined) {
        collection.sort(function (a,b) {
            return compare(fetchFrom(a, prop1st), fetchFrom(b, prop1st))
                || compare(fetchFrom(a, prop2nd), fetchFrom(b, prop2nd));
        });
        return collection;
    } else if(prop4th === undefined) {
        collection.sort(function (a, b) {
            return compare(fetchFrom(a, prop1st), fetchFrom(b, prop1st))
                || compare(fetchFrom(a, prop2nd), fetchFrom(b, prop2nd))
                || compare(fetchFrom(a, prop3rd), fetchFrom(b, prop3rd));
        });
        return collection;
    } else if(prop5th === undefined) {
        collection.sort(function (a, b) {
            return compare(fetchFrom(a, prop1st), fetchFrom(b, prop1st))
                || compare(fetchFrom(a, prop2nd), fetchFrom(b, prop2nd))
                || compare(fetchFrom(a, prop3rd), fetchFrom(b, prop3rd))
                || compare(fetchFrom(a, prop4th), fetchFrom(b, prop4th));
        });
        return collection;
    } else {
        collection.sort(function (a,b) {
            return compare(fetchFrom(a, prop1st), fetchFrom(b, prop1st))
                || compare(fetchFrom(a, prop2nd), fetchFrom(b, prop2nd))
                || compare(fetchFrom(a, prop3rd), fetchFrom(b, prop3rd))
                || compare(fetchFrom(a, prop4th), fetchFrom(b, prop4th))
                || compare(fetchFrom(a, prop5th), fetchFrom(b, prop5th));
        });
        return collection;
    }
}

function convertElements(collection, prop1st, prop2nd, prop3rd) {
    let result = [];
    for(let e of collection) {
        let element;
        if(prop2nd === undefined && prop3rd === undefined) {
            element = {
                id: e.id,
                name: fetchFrom(e, prop1st)
            }
        } else if (prop3rd === undefined) {
            element = {
                id: e.id,
                name: fetchFrom(e, prop1st) + fetchFrom(e, prop2nd)
            }
        } else {
            element = {
                id: e.id,
                name: fetchFrom(e, prop1st) + fetchFrom(e, prop2nd) + fetchFrom(e, prop3rd)
            }
        }
        result.push(element);
    }
    return result;
}

function passwordsMatch(a, b) {
    if(a == b) {
        return true;
    } else {
        return false;
    }
}

function compare(a,b) {
    if(a < b) return -1;
    if(a > b) return 1;
    return 0;

}

function fetchFrom(obj, prop){
    //property not found
    if(typeof obj === 'undefined') return false;

    //index of next property split
    var index = prop.indexOf('.');

    //property split found; recursive call
    if(index > -1){
        //get object at property (before split), pass on remainder
        return fetchFrom(
            obj[prop.substring(0, index)], prop.substr(index+1));
    }

    //no split; get property
    return obj[prop];
}

function removeObjectFromArrayByFieldValue(
    array,
    fieldName,
    fieldValue) {
    array = array.filter(function (object) {
        return fetchFrom(object, fieldName) !== fieldValue;
    });
    return array;
}

function getValueFromInputText($input) {
    let val = $input.val();
    if(val === undefined || val.length == 0) {
        return "";
    } else {
        return val;
    }
}



function collectionIsViable(collection) {
    if(collection !== undefined && collection.length > 0) {
        return true;
    } else {
        return false;
    }
}

function coverUp() {
    $('#div-fade').css('display', 'block');
}

function reveal() {
    $('#div-fade').css('display', 'none');
}

function hide($element) {
    $element.css('display', 'none');
}

function show($element) {
    $element.css('display', 'inline');
}

function showFlex($element) {
    $element.css('display', 'flex');
}


function valueIsValid (value) {
    if(value === undefined) {
        return false;
    } else if(value === null) {
        return false;
    } else {
        return true;
    }
}

function selectOptionById($select, id) {
    $select.find('option[value="' + id + '"]')
        .attr('selected', 'selected');
}

function setButtonAsClicked($clickedButton, $releasedButton, buttonType) {
    $clickedButton.removeClass('btn-outline-' + buttonType);
    $clickedButton.addClass('btn-' + buttonType);
    $releasedButton.removeClass('btn-' + buttonType);
    $releasedButton.addClass('btn-outline-' + buttonType);
}

function empty(v) {
    if(v == null || v == "" || v == undefined) {
        return true;
    } else {
        return false;
    }
}

const allElementsAreEqual = array => array.every(e => e === array[0]);


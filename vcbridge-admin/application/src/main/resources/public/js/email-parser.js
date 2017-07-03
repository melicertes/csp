/**
 * 
 */

var makeEmailList = function (text) {
    /* Get each word from the text, split by spaces, end line, semicolon, quotes, commas, colons, parens,
       and brackets. 
    */
    var words = text.split(/[\s\n;"',;:()<>[\]\\]+/),
        emails = [],
        distinct_emails;
    
    // Regex for identifying an email address.
    var re = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/

    // For each of the words, add to the array of emails if the word matches the email regex.
    for (var key in words) {
        if (words[key].match(re)) emails.push(words[key].toLowerCase());
    }
    
    // Remove duplicate entries from the emails array.
    distinct_emails = removeDuplicateElement(emails);
    
    // Join the emails into a formatted list.
    return distinct_emails.join("; ");
};

var removeDuplicateElement = function (arrayName) {
    var newArray = [], len = arrayName.length, newLen, found_duplicate = false;
    
    /* Compare each email to every other email and push an email onto the new array if 
       a duplicate wasn't found.
    */
    for (var i=0; i < len; i+=1) {  
        newLen = newArray.length;
        for (var j=0; j < newLen; j+=1) {
            if (newArray[j] === arrayName[i]) {
                found_duplicate = true;
                break;
            }
        }
        found_duplicate ? found_duplicate = false : newArray.push(arrayName[i]);
    }
    return newArray;
};
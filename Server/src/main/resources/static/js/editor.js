OPERAND_INVALID = -1;
OPERAND_REG = 0;
OPERAND_MEM_IMM = 1;
OPERAND_MEM_REG = 2;
OPERAND_IMM = 3;

MALFORMED_UTF16_RE = /\\u[0-9a-fA-F]{0,3}([^0-9a-fA-F]|$)/;

//Remove default syntax checker
editor = ace.edit("editor");
editor.session.setOption("useWorker", false);

//Remove message
editor.$blockScrolling = Infinity;


function removeComment(line) {
    if (line.indexOf(";") !== -1) {

        return line.substring(0, line.indexOf(";"));

    } else {
        return line;
    }
}

function checkForLabel(line, result) {
    line = removeComment(line);

    var match;
    if ((match = /\b\w*\b:/.exec(line)) !== null) {

        result.labels.push(match[0].substring(0, match[0].length - 1));
    }
}

function checkForSegmentDeclaration(line) {

    var tokens = getTokens(line);

    return tokens[0] !== undefined && (tokens[0].toLowerCase() === ".data" || tokens[0].toLowerCase() === ".text");

}

function checkForEQUInstruction(line, result, currentLine) {

    var tokens = getTokens(line);


    if (line.toLowerCase().indexOf(" equ ") !== -1 || tokens[1] !== undefined && tokens[1].toLowerCase() === "equ") {
        //Save as a label
        var num = Number(tokens[2]);
        if (!isNaN(num) && num === Math.floor(num)) {
            result.labels.push(tokens[0]);
            return true;
        } else {
            result.annotations.push({
                row: currentLine,
                column: 0,
                text: "Usage: constant_name EQU immediate_value",
                type: "error"
            });
            return true;
        }
    } else {
        return false;
    }
}

function getTokens(line) {

    var tokens = line.split(/\s+/);

    for (var i = 0; i < tokens.length; i++) {
        if (tokens[i] === "") {
            tokens.splice(i, 1);
        }
    }

    return tokens;
}

function removeLabel(line) {
    return line.replace(/\b\w*\b:/, "");
}

function checkForORGInstruction(line, result, currentLine) {
    line = removeComment(line);
    line = removeLabel(line);

    //Split string
    var tokens = getTokens(line);
    var mnemonic = tokens[0];

    if (mnemonic !== undefined && mnemonic.toLowerCase() === "org") {

        console.log(tokens);

        if (tokens.length > 1) {

            var num = Number(tokens[1]);
            if (!isNaN(num) && num === Math.floor(num)) {
                return true;
            } else {
                result.annotations.push({
                    row: currentLine,
                    column: 0,
                    text: "Invalid operand: " + tokens[1],
                    type: "error"
                });
                return true
            }
        }
    } else {
        return false;
    }
}

function parseDWInstruction(line, result, currentLine) {
    line = line.trim();

    if (line.substr(0, 2).toLowerCase() === "dw") {


        var values = line.substr(2, line.length).split(/,(?=(?:[^"]*"[^"]*")*[^"]*$)/, -1);

        for (var i = 0; i < values.length; i++) {

            values[i] = values[i].trim();
            var tokens = getTokens(values[i]);

            if (tokens.length === 2 && getOperandType(tokens[0], result) === OPERAND_IMM &&
                tokens[1].toLowerCase().startsWith("dup(") && tokens[1].endsWith(")") &&
                getOperandType(tokens[1].substring(4, tokens[1].indexOf(")")), result) === OPERAND_IMM) {

                // console.log("DUp");

            } else if (values[i].startsWith("\"") && values[i].endsWith("\"")) {
                //Handle string
                var strText = values[i].substr(1, values[i].length - 2);

                if (strText.match(MALFORMED_UTF16_RE) != null) {

                    result.annotations.push({
                        row: currentLine,
                        column: 0,
                        text: "Malformed UTF-16 escape sequence",
                        type: "error"
                    });
                    return true;
                }

                //TODO: verify other escape sequences

            } else if (getOperandType(values[i], result) === OPERAND_IMM) {

                // console.log("is Imm " + values[i]);

            } else {

                result.annotations.push({
                    row: currentLine,
                    column: 0,
                    text: "Usage: DW IMM, IMM ...",
                    type: "error"
                });
                return true;

            }
        }

        return true;
    } else {
        return false;
    }
}


function getOperandType(text, result) {

    text = text.trim();
    if (text === "") {
        return OPERAND_INVALID;
    }

    //Check IMM
    if (!isNaN(Number(text)) && Number(text) === Math.floor(Number(text)) && text.indexOf("o") === -1
        && text.indexOf("0e") !== 0) {
        return OPERAND_IMM;
    }

    //Check REG
    if (new RegExp('^(a|b|c|d|x|y|bp|sp)$').test(text.toLowerCase())) {
        return OPERAND_REG;
    }

    //Check Label
    for (i = 0; i < result.labels.length; i++) {
        if (text === result.labels[i]) {
            return OPERAND_IMM;
        }
    }

    //Check MEM_*
    if (text.startsWith("[") && text.endsWith("]")) {
        text = text.replace("[", "").replace("]", "");

        //Check MEM_IMM
        if (!isNaN(Number(text)) && Number(text) === Math.floor(Number(text))) {
            return OPERAND_MEM_IMM;
        }
        //Check MEM_Label
        for (var i = 0; i < result.labels.length; i++) {
            if (text === result.labels[i]) {
                return OPERAND_MEM_IMM;
            }
        }

        //Check for MEM_REG (+ x)
        var expr = "";
        if (new RegExp('^(bp|sp)$').test(text.toLowerCase().substring(0, 2).toLowerCase())) {
            //Starts with 2-char register
            expr = text.substring(2);
        } else if (new RegExp('^(a|b|c|d|x|y)$').test(text.toLowerCase().substring(0, 1).toLowerCase())) {
            //Starts with 1-char register
            expr = text.substring(1);
        } else {
            return OPERAND_INVALID;
        }


        if (expr.replace(/\s+/g, '') === "") {
            //No displacement specified
            return OPERAND_MEM_REG;
        }

        //Remove white space
        expr = expr.replace(/\s+/g, '');
        //expr should now look like this: '+1' '-3' '+0x02' '+myLabel'

        //Check for label
        for (i = 0; i < result.labels.length; i++) {
            if (expr.substring(1) === result.labels[i]) {
                return OPERAND_MEM_REG;
            }
        }

        //Remove either ONE '+' or ONE '-' else the operand is invalid
        //Credit: https://github.com/KevinRamharak
        expr = expr.replace(/[+-]/, '');

        //Check for number
        if (!isNaN(Number(expr)) && Number(expr) === Math.floor(Number(expr))) {
            return OPERAND_MEM_REG;
        }

    }

    return OPERAND_INVALID;

}

function parseInstruction(line, result, currentLine) {
    line = removeComment(line);
    line = removeLabel(line);

    var tokens = getTokens(line);
    var mnemonic = tokens[0];

    if (mnemonic === undefined || mnemonic === "") {
        return; //Line is empty
    }


    if (!parseDWInstruction(line, result, currentLine)) {

        if (new RegExp('\\b(?:mov|add|sub|and|or|test|cmp|shl|shr|mul|push|pop|div|xor|hwi|hwq|nop|neg|' +
            'call|ret|jmp|jnz|jg|jl|jge|jle|int|jz|js|jns|brk|not|jc|jnc|ror|rol|sal|sar|jo|jno|inc|dec|rcl|xchg|rcr|pushf|popf|ja|jna)\\b').test(mnemonic.toLowerCase())) {


            if (line.indexOf(",") !== -1) {
                //2 Operands
                var strO1 = line.substring(line.indexOf(mnemonic) + mnemonic.length, line.indexOf(','));
                var strO2 = line.substring(line.indexOf(',') + 1).trim();


                //Validate operand number
                if (!new RegExp('\\b(?:mov|add|sub|and|or|test|cmp|shl|shr|xor|rol|ror|sal|sar|rcl|xchg|rcr)\\b').test(mnemonic.toLowerCase())) {
                    result.annotations.push({
                        row: currentLine,
                        column: 0,
                        text: mnemonic + " instruction with 2 operands is illegal",
                        type: "error"
                    });
                    return;
                }

                //Validate operand type
                var o1Type = getOperandType(strO1, result);
                var o2Type = getOperandType(strO2, result);
                if (o1Type === OPERAND_INVALID) {
                    result.annotations.push({
                        row: currentLine,
                        column: 0,
                        text: "Invalid operand: " + strO1,
                        type: "error"
                    });
                    return;
                }
                if (o2Type === OPERAND_INVALID) {
                    result.annotations.push({
                        row: currentLine,
                        column: 0,
                        text: "Invalid operand: " + strO2,
                        type: "error"
                    });
                    return;
                }

                //Check for illegal operand combos:
                if (o1Type === OPERAND_IMM) {
                    result.annotations.push({
                        row: currentLine,
                        column: 0,
                        text: "Destination operand can't be an immediate value",
                        type: "error"
                    });
                }


            } else if (tokens.length > 1) {
                //1 Operand
                strO1 = line.substring(line.indexOf(mnemonic) + mnemonic.length).trim();

                //Validate operand number
                if (!new RegExp('\\b(?:push|mul|pop|div|neg|call|jnz|jg|jl|jge|jle|hwi|hwq|jz|js|jns|ret|jmp|not|jc|jnc|jo|jno|inc|dec|ja|jna)\\b').test(mnemonic.toLowerCase())) {
                    result.annotations.push({
                        row: currentLine,
                        column: 0,
                        text: mnemonic + " instruction with 1 operand is illegal",
                        type: "error"
                    });
                    return;
                }

                //Validate operand type
                if (getOperandType(strO1, result) === OPERAND_INVALID) {
                    result.annotations.push({
                        row: currentLine,
                        column: 0,
                        text: "Invalid operand: " + strO1,
                        type: "error"
                    });
                }


            } else {
                //No operand
                if (!new RegExp('\\b(?:ret|brk|nop|pushf|popf)\\b').test(mnemonic.toLowerCase())) {

                    //Validate operand number
                    result.annotations.push({
                        row: currentLine,
                        column: 0,
                        text: mnemonic + " instruction with no operand is illegal",
                        type: "error"
                    });
                }
            }


        } else {
            result.annotations.push({
                row: currentLine,
                column: 0,
                text: "Unknown mnemonic: " + mnemonic,
                type: "error"
            });
        }

    }
}

function parse() {

    var text = ace.edit("editor").getValue();
    var lines = text.split("\n");
    var result = {
        labels: [],
        annotations: []
    };

    //Pass 1 of 2: Save label names
    for (var currentLine = 0; currentLine < lines.length; currentLine++) {
        checkForLabel(lines[currentLine], result);
    }


    //Pass 2 of 2: Check instructions
    for (currentLine = 0; currentLine < lines.length; currentLine++) {

        if (!checkForSegmentDeclaration(lines[currentLine]) &&
            !checkForEQUInstruction(lines[currentLine], result, currentLine) &&
            !checkForORGInstruction(lines[currentLine], result, currentLine)) {

            parseInstruction(lines[currentLine], result, currentLine);
        }

    }

    //Set icons
    editor.getSession().setAnnotations(result.annotations);
}

function tabWorldClick() {
    document.getElementById("tab-world").classList.add("active");
    document.getElementById("tab-world-sm").classList.add("active");
    document.getElementById("tab-editor").classList.remove("active");
    document.getElementById("tab-editor-sm").classList.remove("active");

    document.getElementById("world-tab").setAttribute("style", "");
    document.getElementById("editor-tab").setAttribute("style", "display: none");
}

function tabEditorClick() {
    document.getElementById("tab-world").classList.remove("active");
    document.getElementById("tab-world-sm").classList.remove("active");
    document.getElementById("tab-editor").classList.add("active");
    document.getElementById("tab-editor-sm").classList.add("active");

    document.getElementById("world-tab").setAttribute("style", "display: none");
    document.getElementById("editor-tab").setAttribute("style", "");
}

//-----

//Check if browser supports local storage if not than bad luck, use something else than IE7
var editorStorage;
if (typeof window.localStorage !== 'undefined') {
    editorStorage = window.localStorage;
} else {
    editorStorage = false;
}

//Default should be 'theme/tommorow.js' or loaded from local storage
var editorThemeOptions = {
    available: [
        "theme/ambiance", "theme/chaos", "theme/chrome",
        "theme/clouds", "theme/clouds_midnight", "theme/cobalt",
        "theme/crimson_editor", "theme/dawn", "theme/dracula",
        "theme/dreamweaver", "theme/eclipse", "theme/github",
        "theme/gob", "theme/gruvbox", "theme/idle_fingers",
        "theme/iplastic", "theme/katzenmilch", "theme/kr_theme",
        "theme/kuroir", "theme/merbivore", "theme/merbivore_soft",
        "theme/mono_industrial", "theme/monokai", "theme/pastel_on_dark",
        "theme/solarized_dark", "theme/solarized_light", "theme/sqlserver",
        "theme/terminal", "theme/textmate", "theme/tomorrow",
        "theme/tomorrow_night_blue", "theme/tomorrow_night_bright", "theme/tomorrow_night_eighties",
        "theme/tomorrow_night", "theme/twilight", "theme/vibrant_ink", "theme/xcode"
    ],
    defaultTheme: "theme/tomorrow_night"
};

//Get the stored default theme
if (editorStorage) {
    var storedTheme = editorStorage.getItem('editorTheme');
    if (storedTheme !== null && editorThemeOptions.available.indexOf(storedTheme) !== -1) {
        editorThemeOptions.defaultTheme = storedTheme;
    }
}

//Cache element reference
var editorThemeSelectElement = document.getElementById("editorTheme");

//Event handler
function editorOnThemeChange() {
    if (editorThemeSelectElement === null) {
        console.error("editorOnThemeChange() :: editorThemeSelectElement seems to be 'null'");
        return;
    }
    var select = editorThemeSelectElement;
    var option = select.options[select.selectedIndex];

    if (editorThemeOptions.available.indexOf(option.value) === -1) {
        console.error("editorOnThemeChange() :: user somehow selected an invalid theme : '" + option.value + "' for '" + option.text + "'");
        return;
    }

    //Store locally so it gets remembered
    if (editorStorage) {
        editorStorage.setItem('editorTheme', option.value);
    }

    //Set theme
    editor.setTheme("ace/" + option.value);
}

//Add handler to listen to event
editorThemeSelectElement.addEventListener('change', editorOnThemeChange);

//Populate select
editorThemeOptions.available.forEach(function (theme) {
    var option = document.createElement("option");
    option.value = theme;
    option.text = theme.substring(6); // "theme/{text}" -> extract text to set as text user sees

    //Make sure default is also the one that is selected
    if (theme === editorThemeOptions.defaultTheme) {
        option.selected = true;
    }

    editorThemeSelectElement.appendChild(option);
});

//Manually call handler once
editorOnThemeChange();

//------ Floppy upload form code ------------------

document.getElementById("floppyIn").onchange = function () {

    document.getElementById("floppyUp").innerHTML = '<i class="mi rotating">cached</i> Floppy';


    var formData = new FormData(document.getElementById("floppyForm"));

    formData.append("floppyData", document.getElementById("floppyIn").files[0]);

    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/floppy_upload', true);
    xhr.onload = function () {
        if (xhr.status === 200) {

            if (xhr.responseText === "ok") {
                alert("Uploaded floppy disk to the drive!")
            } else {
                alert(xhr.responseText)
            }

        } else {
            alert("Couldn't upload floppy code (" + xhr.status + ")");
        }

        document.getElementById("floppyUp").innerHTML = '<i class="mi">file_upload</i> Floppy';

    };
    xhr.onerror = function (ev) {
        ev.preventDefault();
        alert("Couldn't upload floppy code: File is too large");
    };

    xhr.send(formData);
};

editor.getSession().setMode("ace/mode/mar");
editor.setFontSize(16);
editor.setDisplayIndentGuides(false);
document.getElementById('editor').style.fontFamily = "fixedsys";

editor.on("change", parse);

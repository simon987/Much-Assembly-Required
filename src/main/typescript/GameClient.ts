declare function saveAs(any, message: string): void;

declare var ace: any;

/**
 * Client-side keyboard buffer. It is overwritten by the server at the end of tick.
 */
class KeyboardBuffer extends DebugMessage {

    /**
     * Array of key codes. Updated on keypress
     * @type {Array}
     */
    public keys: number[] = [];

    /**
     * @returns {string} Message written on the screen
     */
    public getMessage(): string {
        let str = "KB: ";

        for (let i = 0; i < 16; i++) {

            if (this.keys[i] !== undefined) {

                str += this.keys[i].toString(16).toUpperCase() + " ";

            } else {

                str += "__ ";
            }
        }

        return str;
    }
}


/**
 * Listens for server messages
 */
interface MessageListener {

    handle(message): void;

    getListenedMessageType(): string

}

/**
 * Listens for object list
 */
class ObjectsListener implements MessageListener {


    getListenedMessageType(): string {
        return "object";
    }

    handle(message): void {

        if (DEBUG) {
            console.log("[MAR] Received " + message.objects.length + " objects")
        }

        if (mar.world != undefined) {
            mar.world.handleObjectsUpdate(message.objects)
        }
    }
}

class TickListener implements MessageListener {

    getListenedMessageType() {
        return "tick";
    }

    handle(message): void {
        mar.client.requestObjects();

        //Update key buffer display
        if (message.keys !== undefined) {
            mar.client.keyboardBuffer.keys = message.keys;
        }

        //Update console screen
        if (message.console_message_buffer != undefined) {
            mar.client.consoleScreen.handleConsoleBufferUpdate(
                message.console_message_buffer,
                message.console_mode as ConsoleMode);

            if (DEBUG) {
                console.log("[MAR] Received " + message.console_message_buffer.length + " console message(s)")
            }
        }
    }
}

class UserInfoListener implements MessageListener {

    getListenedMessageType() {
        return "userInfo";
    }

    handle(message) {
        if (DEBUG) {
            console.log("[MAR] Received user info message")
        }

        mar.client.worldX = message.worldX;
        mar.client.worldY = message.worldY;
        mar.client.dimension = message.dimension;
        Debug.SELF_ID = message.id;

        //Maximum Universe width
        mar.client.maxWidth = message.maxWidth;

        mar.client.requestTerrain();
    }
}

class PausedPromptListener implements MessageListener {

    getListenedMessageType() {
        return "paused";
    }

    handle(message): void {
        mar.pausedLine = message.line;
        mar.isPaused = true;
        if (!message.stateSent) {
            mar.client.requestState();
        }

        if (mar.disassembly == null) {
            mar.client.requestDisassembly();
        }
    }
}

class StateListener implements MessageListener {

    getListenedMessageType() {
        return "state";
    }

    handle(message): void {
        const stateMemory = document.getElementById("state-memory");
        while (stateMemory.firstChild) {
            stateMemory.removeChild(stateMemory.firstChild);
        }
        const stateRegisters = document.getElementById("state-registers");
        while (stateRegisters.firstChild) {
            stateRegisters.removeChild(stateRegisters.firstChild);
        }
        const stateStatus = document.getElementById("state-status");
        while (stateStatus.firstChild) {
            stateStatus.removeChild(stateStatus.firstChild);
        }

        // stateMemory.insertAdjacentHTML("beforeend", message.memory.replace(/(0000 )+/g, '<span class="_0">$&</span>'));
        stateMemory.insertAdjacentHTML("beforeend", message.memory);
        let registers = "";
        let regKeys = Object.keys(message.registers);
        for (let i = 0; i < regKeys.length; i++) {
            registers += regKeys[i] + "=" + message.registers[regKeys[i]].toString(16).padStart(4, "0").toUpperCase()
            if (i != regKeys.length - 1) {
                registers += " ";
            }
        }
        stateRegisters.insertAdjacentHTML("beforeend", registers.replace(/(0000)+/g, '<span class="_0">$&</span>'));

        stateStatus.insertAdjacentHTML("beforeend", message.status.replace(/=0/g, '=<span class="_0">0</span>'));
        updateDisassemblyPane()
    }
}

function hlDisassembly(lines) {
    let text = ""
    for (let i = 0; i < lines.length; i++) {
        text += "<span id='line-" + i + "'>" + lines[i]
                .replace(/^\s*([a-zA-Z_]\w*):/gm, '<span class="_l">                      $1:</span>')
                .replace(/^.*INT 0003$/gm, '<span class="i3">$&</span>')
                .replace(/^[0-9A-F]{4}/gm, '<span class="_a">$&</span>')
                .replace(/ (MOV|ADD|SUB|AND|OR|TEST|CMP|SHL|SHR|MUL|PUSH|POP|DIV|XOR|DW|NOP|EQU|NEG|HWQ|NOT|ROR|ROL|SAL|SAR|INC|DEC|RCL|XCHG|RCR|PUSHF|POPF|INT|IRET|INTO|SETA|SETNBE|SETAE|SETNB|SETNC|SETBE|SETNA|SETB|SETC|SETNAE|SETE|SETZ|SETNE|SETNZ|SETG|SETNLE|SETGE|SETNL|SETLE|SETNG|SETL|SETNGE|SETO|SETNO|SETS|SETNS)/g, '<span class="_k"> $1</span>')
                .replace(/ (CALL|RET|JMP|JNZ|JG|JL|JGE|JLE|HWI|JZ|JS|JNS|JC|JNC|JO|JNO|JA|JNA)/g, '<span class="_o"> $1 </span>')
                .replace(/ (BRK)$/gm, '<span class="_b"> $1</span>')
            + "</span>\n";
    }
    return text;
}

function updateDisassemblyPane() {

    const line = mar.pausedLine;
    if (mar.disassembly == undefined || line == -1 || !mar.isPaused) {
        return;
    }

    const stateDisassembly = document.getElementById("state-disassembly");

    if (!mar.disassemblyInitialized) {
        mar.disassemblyInitialized = true;
        while (stateDisassembly.firstChild) {
            stateDisassembly.removeChild(stateDisassembly.firstChild);
        }

        stateDisassembly.innerHTML = hlDisassembly(mar.disassembly)
    } else {
        [].forEach.call(document.getElementsByClassName("disassembly-hl"), el => el.setAttribute("class", null));
    }
    const lineElem = document.getElementById("line-" + line);
    lineElem.classList.add("disassembly-hl");
    lineElem.scrollIntoView({block: "center"});
}

class DisassemblyListener implements MessageListener {

    getListenedMessageType() {
        return "disassembly";
    }

    handle(message): void {
        mar.disassembly = message.lines;
        updateDisassemblyPane();
    }
}


class AuthListener implements MessageListener {

    getListenedMessageType() {
        return "auth";
    }

    handle(message) {
        if (DEBUG) {
            console.log("[MAR] Received auth response")
        }

        if (message.m === "ok") {
            if (DEBUG) {
                console.log("[MAR] Auth successful");
            }
            mar.client.requestUserInfo();

        } else if (message.m == "forbidden") {
            alert("Authentication failed. Guest accounts are blocked on this server")
        } else {
            alert("Authentication failed. Please make sure you are logged in and reload the page.");
        }
    }
}

class TerrainListener implements MessageListener {

    getListenedMessageType() {
        return "terrain";
    }

    handle(message) {

        if (DEBUG) {
            console.log("[MAR] Received terrain");
        }

        if (mar.world) {
            mar.world.removeBigMessage();
        }


        if (message.ok) {

            let worldSize = message.size;
            if (worldSize == undefined) {
                worldSize = config.world.defaultSize;
            }


            if (DEBUG) {
                console.log("[MAR] World is available");
            }

            if (mar.world != null) {

                if (DEBUG) {
                    console.log("[MAR] Updating World terrain");
                }

                mar.world.updateTerrain(message.terrain, worldSize);

            } else {

                if (DEBUG) {
                    console.log("[MAR] Creating new World");
                }

                mar.world = new World(message.terrain, worldSize);

            }
        } else {


            if (DEBUG) {
                console.log("[MAR] World is not available");
            }

            if (mar.world != null) {

                if (DEBUG) {
                    console.log("[MAR] Updating World terrain");
                }

                mar.world.updateTerrain([], config.world.defaultSize);

            } else {

                if (DEBUG) {
                    console.log("[MAR] Creating new World");
                }

                mar.world = new World([], config.world.defaultSize);

            }
            if (mar.world) {
                mar.world.setBigMessage("[Uncharted World]")
            }
        }
    }

}

class CodeListener implements MessageListener {

    getListenedMessageType(): string {
        return "code";
    }

    handle(message): void {
        ace.edit("editor").setValue(message.code);
    }
}

class CodeResponseListener implements MessageListener {

    getListenedMessageType(): string {
        return "codeResponse";
    }

    handle(message): void {
        mar.client.requestDisassembly();
        alert("Uploaded and assembled " + message.bytes + " bytes (" + message.exceptions + " errors)");
    }

}

class DebugResponseListener implements MessageListener {

    getListenedMessageType(): string {
        return "debug";
    }

    handle(message): void {

        console.log("> " + message.message)

    }

}

class GameClient {

    keyboardBuffer: KeyboardBuffer;
    /**
     * Max width of the game universe, set by server
     */
    public maxWidth: number;

    private listeners: MessageListener[] = [];

    private socket: WebSocket;
    public username: string;
    private tickLength: number;
    private serverName: string;

    public worldX: number;
    public worldY: number;
    public dimension: string;

    public consoleScreen: PlainTextConsole;

    constructor() {
        this.getServerInfo();

        this.consoleScreen = new PlainTextConsole(defaultText, "consoleText", "colorButton", "scrollButton", "resetButton");
    }

    public requestUserInfo(): void {

        if (DEBUG) {
            console.log("[MAR] Requesting user info");
        }

        this.socket.send(JSON.stringify({t: "userInfo"}));
    }

    public requestTerrain() {

        if (DEBUG) {
            console.log("[MAR] Requesting terrain for world (" + this.worldX + ", " + this.worldY + ")");
        }

        this.socket.send(JSON.stringify({t: "terrain", x: this.worldX, y: this.worldY, dimension: this.dimension}));
        this.requestObjects();
    }

    public uploadCode(code: string): void {

        if (DEBUG) {
            console.log("[MAR] Uploaded code");
        }
        mar.isPaused = false;

        this.socket.send(JSON.stringify({t: "uploadCode", code: code}))
    }

    public reloadCode(): void {
        if (DEBUG) {
            console.log("[MAR] Reloading code");
        }

        this.socket.send(JSON.stringify({t: "codeRequest"}))
    }

    public sendKeyPress(key: number): void {

        if (DEBUG) {
            console.log("[MAR] Sent KeyPress: " + key);
        }

        if (key !== 0) {
            this.socket.send(JSON.stringify({t: "k", k: key}));
        }
    }


    public requestObjects(): void {
        if (DEBUG) {
            console.log("[MAR] Requesting game objects");
        }

        this.socket.send(JSON.stringify({t: "object", x: this.worldX, y: this.worldY, dimension: this.dimension}));
    }

    public requestState(): void {
        if (DEBUG) {
            console.log("[MAR] Requesting CPU state");
        }

        this.socket.send(JSON.stringify({t: "stateRequest"}))
    }

    public requestDisassembly(): void {
        if (DEBUG) {
            console.log("[MAR] Requesting disassembly");
        }

        this.socket.send(JSON.stringify({t: "disassemblyRequest"}))
    }

    public debugStep(mode): void {
        if (DEBUG) {
            console.log("[MAR] Debug step " + mode);
        }

        if (mode == "continue") {
            mar.isPaused = false;
            updateDisassemblyPane();
        }

        this.socket.send(JSON.stringify({t: "debugStep", mode: mode}))
    }

    public sendDebugCommand(json): void {

        this.socket.send(JSON.stringify(json));
    }

    /**
     * Get server info from game website
     */
    private getServerInfo() {
        let self = this;

        if (DEBUG) {
            console.log("[MAR] Getting server info... ");
        }

        let xhr = new XMLHttpRequest();
        xhr.open("GET", "./server_info", true);

        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {

                if (DEBUG) {
                    console.log("[MAR] Received server info " + xhr.responseText);
                }

                setTimeout(() => self.connectToGameServer(JSON.parse(xhr.responseText)), 100);
            }
        };
        xhr.send(null);
    }

    /**
     * Connect to the game server
     * @param info JSON fetched from /getServerInfo.php
     */
    private connectToGameServer(info: any) {

        let self = this;

        if (DEBUG) {
            console.log("[MAR] Connecting to  " + info.address);
        }

        this.socket = new WebSocket(info.address);
        this.username = info.username;
        this.tickLength = info.tickLength;
        this.serverName = info.serverName;

        this.socket.binaryType = 'arraybuffer';


        this.socket.onopen = function () {

            if (DEBUG) {
                console.log("[MAR] Connected. Sent auth request");
            }

            //Send auth request
            self.socket.send(info.token);

            self.listeners.push(new UserInfoListener());
            self.listeners.push(new AuthListener());
            self.listeners.push(new TickListener());
            self.listeners.push(new TerrainListener());
            self.listeners.push(new ObjectsListener());
            self.listeners.push(new CodeResponseListener());
            self.listeners.push(new CodeListener());
            self.listeners.push(new DebugResponseListener());
            self.listeners.push(new StateListener());
            self.listeners.push(new DisassemblyListener());
            self.listeners.push(new PausedPromptListener());

            self.socket.onmessage = function (received) {

                let message;

                if (DEBUG) {
                    console.log("[MAR] Received: " + received.data)
                }

                message = JSON.parse(received.data);

                for (let i = 0; i < self.listeners.length; i++) {

                    if (self.listeners[i].getListenedMessageType() === message.t) {
                        self.listeners[i].handle(message)
                    }
                }
            };

            self.reloadCode();
        };

        this.socket.onerror = function (e) {
            alert("Can't connect to game server at address " + info.address);
            console.log(e);
        };


        this.socket.onclose = function (e) {

            mar.world.setBigMessage("Disconnected from server :(");
            console.log(e);
        };

        this.initGame();
    }

    /**
     * Called after the connection has been made to the server
     */
    public initGame() {

        //Setup keyboard buffer display, don't if guest
        if (this.username != "guest") {

            let self = this;

            this.keyboardBuffer = new KeyboardBuffer(config.kbBuffer.x, config.kbBuffer.y);
            mar.addDebugMessage(this.keyboardBuffer);


            //Handle keypresses
            mar.game.input.keyboard.onDownCallback = function (event) {

                //If the game has focus
                if (document.activeElement === document.getElementById("game")) {
                    if ((event.keyCode >= 37 && event.keyCode <= 40) || event.keyCode === 116 || event.keyCode === 32) {
                        event.preventDefault();
                    }

                    if (self.username !== "guest" && self.keyboardBuffer.keys.length <= 16) {
                        self.sendKeyPress(event.keyCode);

                        //Locally update the buffer
                        self.keyboardBuffer.keys.push(event.keyCode);
                    }
                }
            };
        }
    }

    /**
     * Requests user info, which will trigger a terrain request with the world X,Y of
     * the player's robot
     */
    public findMyRobot() {
        if (this.username == "guest") {
            alert("You are not logged in!");
        } else {
            this.requestUserInfo()
        }
    }
}



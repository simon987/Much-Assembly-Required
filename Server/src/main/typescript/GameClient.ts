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
        if (message.c != undefined) {
            mar.client.consoleScreen.handleConsoleBufferUpdate(message.c, message.cm as ConsoleMode);

            if (DEBUG) {
                console.log("[MAR] Received " + message.c.length + " console message(s)")
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

        //Maximum Universe width
        mar.client.maxWidth = message.maxWidth;

        mar.client.requestTerrain();
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
                worldSize = config.defaultWorldSize;
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

                mar.world.updateTerrain([], config.defaultWorldSize);

            } else {

                if (DEBUG) {
                    console.log("[MAR] Creating new World");
                }

                mar.world = new World([], config.defaultWorldSize);

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

            this.keyboardBuffer = new KeyboardBuffer(config.kbBufferX, config.kbBufferY);
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



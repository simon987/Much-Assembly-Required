var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({__proto__: []} instanceof Array && function (d, b) {
                d.__proto__ = b;
            }) ||
            function (d, b) {
                for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p];
            };
        return extendStatics(d, b);
    }
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
var MarGame = (function () {
    function MarGame() {
        this.cursorPos = new Phaser.Plugin.Isometric.Point3();
        this.debugMessages = [];
        this.animationFrames = {};
        var self = this;
        this.game = new Phaser.Game(RENDERER_WIDTH, RENDERER_HEIGHT, Phaser.AUTO, 'game', null, true, false);
        this.bootState = {
            preload: function () {
                if (DEBUG) {
                    console.log("[MAR] Loading sprites.png as JSONHash");
                }
                this.game.load.atlasJSONHash("sheet", "./images/sprites.png", "./images/sprites.json").onLoadComplete.add(function () {
                    self.game.time.advancedTiming = true;
                    if (DEBUG) {
                        console.log("[MAR] Enabling isometric plugin");
                    }
                    self.game.plugins.add(new Phaser.Plugin.Isometric(self.game));
                    self.game.iso.anchor.setTo(0.5, 0);
                    self.game.world.setBounds(0, 0, 2200, 1100);
                    self.game.camera.x = 280;
                    self.game.camera.y = 90;
                    self.game.scale.scaleMode = Phaser.ScaleManager.RESIZE;
                    self.game.scale.pageAlignHorizontally = true;
                    self.game.scale.pageAlignVertically = true;
                    self.game.stage.disableVisibilityChange = true;
                    self.client = new GameClient();
                    self.game.input.onDown.add(function () {
                        document.getElementById("game").focus();
                        if (DEBUG) {
                            console.log("Grabbed focus of #game");
                        }
                    });
                    self.isoGroup = mar.game.add.group();
                    self.textGroup = mar.game.add.group();
                    self.hudGroup = mar.game.add.group();
                    self.hudGroup.fixedToCamera = true;
                });
            },
            create: function () {
                if (DEBUG) {
                    console.log("[MAR] create");
                }
                self.initialiseAnimations();
                self.initialiseStaticHud();
            },
            update: function () {
                self.game.scale.refresh();
                self.game.iso.unproject(self.game.input.activePointer.position, self.cursorPos);
                self.isoGroup.forEach(function (tile) {
                    if (tile instanceof Tile) {
                        var inBounds = tile.isoBounds.containsXY(self.cursorPos.x, self.cursorPos.y);
                        if (!tile.selected && inBounds) {
                            tile.selected = true;
                            tile.onHover();
                            self.isoGroup.forEach(function (obj) {
                                if (obj instanceof GameObject && obj.onTileHover != undefined && obj.isAt(tile.tileX, tile.tileY)) {
                                    obj.onTileHover();
                                }
                            }, 1);
                        }
                        else if (tile.selected && !inBounds) {
                            tile.selected = false;
                            tile.onExit();
                            self.isoGroup.forEach(function (obj) {
                                if (obj.onTileExit != undefined && obj.isAt(tile.tileX, tile.tileY)) {
                                    obj.onTileExit();
                                }
                            }, 0);
                        }
                    }
                }, 0);
                if (this.game.input.activePointer.isDown) {
                    if (this.game.origDragPoint) {
                        this.game.camera.x += this.game.origDragPoint.x - this.game.input.activePointer.position.x;
                        this.game.camera.y += this.game.origDragPoint.y - this.game.input.activePointer.position.y;
                    }
                    this.game.origDragPoint = this.game.input.activePointer.position.clone();
                }
                else {
                    this.game.origDragPoint = null;
                }
                self.game.iso.topologicalSort(self.isoGroup);
            },
            render: function () {
                for (var i = 0; i < self.debugMessages.length; i++) {
                    self.game.debug.text(self.debugMessages[i].getMessage(), self.debugMessages[i].x, self.debugMessages[i].y);
                }
            }
        };
        this.game.state.add('Boot', this.bootState);
        this.game.state.start('Boot');
    }
    MarGame.prototype.addDebugMessage = function (debugMsg) {
        this.debugMessages.push(debugMsg);
    };
    MarGame.prototype.initialiseAnimations = function () {
        this.animationFrames.walk_e_start = [];
        for (var i = 0; i < 10; i++) {
            this.animationFrames.walk_e_start.push("cubot/walk_e/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.walk_e = [];
        for (var i = 10; i < 30; i++) {
            this.animationFrames.walk_e.push("cubot/walk_e/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.harvester_walk_e_start = [];
        for (var i = 0; i < 10; i++) {
            this.animationFrames.harvester_walk_e_start.push("harvester/walk_e/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.harvester_walk_e = [];
        for (var i = 10; i < 30; i++) {
            this.animationFrames.harvester_walk_e.push("harvester/walk_e/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.walk_n_start = [];
        for (var i = 0; i < 10; i++) {
            this.animationFrames.walk_n_start.push("cubot/walk_n/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.walk_n = [];
        for (var i = 10; i < 30; i++) {
            this.animationFrames.walk_n.push("cubot/walk_n/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.harvester_walk_n_start = [];
        for (var i = 0; i < 10; i++) {
            this.animationFrames.harvester_walk_n_start.push("harvester/walk_n/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.harvester_walk_n = [];
        for (var i = 10; i < 30; i++) {
            this.animationFrames.harvester_walk_n.push("harvester/walk_n/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.walk_s_start = [];
        for (var i = 0; i < 10; i++) {
            this.animationFrames.walk_s_start.push("cubot/walk_s/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.walk_s = [];
        for (var i = 10; i < 30; i++) {
            this.animationFrames.walk_s.push("cubot/walk_s/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.harvester_walk_s_start = [];
        for (var i = 0; i < 10; i++) {
            this.animationFrames.harvester_walk_s_start.push("harvester/walk_s/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.harvester_walk_s = [];
        for (var i = 10; i < 30; i++) {
            this.animationFrames.harvester_walk_s.push("harvester/walk_s/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.walk_w_start = [];
        for (var i = 0; i < 10; i++) {
            this.animationFrames.walk_w_start.push("cubot/walk_w/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.walk_w = [];
        for (var i = 10; i < 30; i++) {
            this.animationFrames.walk_w.push("cubot/walk_w/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.harvester_walk_w_start = [];
        for (var i = 0; i < 10; i++) {
            this.animationFrames.harvester_walk_w_start.push("harvester/walk_w/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.harvester_walk_w = [];
        for (var i = 10; i < 30; i++) {
            this.animationFrames.harvester_walk_w.push("harvester/walk_w/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.dig_e = [];
        for (var i = 1; i <= 41; i++) {
            this.animationFrames.dig_e.push("cubot/dig_e/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.dig_n = [];
        for (var i = 1; i <= 41; i++) {
            this.animationFrames.dig_n.push("cubot/dig_n/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.dig_s = [];
        for (var i = 1; i <= 41; i++) {
            this.animationFrames.dig_s.push("cubot/dig_s/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.dig_w = [];
        for (var i = 1; i <= 41; i++) {
            this.animationFrames.dig_w.push("cubot/dig_w/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.biomassIdle = [];
        for (var i = 1; i < 60; i++) {
            this.animationFrames.biomassIdle.push("objects/biomass/idle/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.vaultDoorScreen = [];
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/1");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/2");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/3");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/4");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/5");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/6");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/1");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/1");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/1");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/1");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/1");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/1");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/1");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/1");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/1");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/1");
        this.animationFrames.vaultDoorScreen.push("objects/VaultDoorScreen/1");
    };
    MarGame.prototype.initialiseStaticHud = function () {
        this.addDebugMessage(new WorldIndicator(10, 20));
        this.tileIndicator = new TileIndicator(10, 40);
        this.addDebugMessage(this.tileIndicator);
    };
    return MarGame;
}());
var DebugMessage = (function () {
    function DebugMessage(x, y) {
        this.x = x;
        this.y = y;
    }
    return DebugMessage;
}());
var TileIndicator = (function (_super) {
    __extends(TileIndicator, _super);
    function TileIndicator() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    TileIndicator.prototype.getMessage = function () {
        if (this.tileType != undefined) {
            return this.tileX + ", " + this.tileY + " : " + this.tileType;
        }
        else {
            return "";
        }
    };
    return TileIndicator;
}(DebugMessage));
var WorldIndicator = (function (_super) {
    __extends(WorldIndicator, _super);
    function WorldIndicator() {
        return _super !== null && _super.apply(this, arguments) || this;
    }
    WorldIndicator.prototype.getMessage = function () {
        if (mar.world != undefined) {
            return "World: " + mar.client.dimension + "(" + Number(mar.client.worldX).toString(16).toUpperCase() + ", " +
                Number(mar.client.worldY).toString(16).toUpperCase() + ")";
        }
        else {
            return "Loading...";
        }
    };
    return WorldIndicator;
}(DebugMessage));
var RENDERER_WIDTH = document.getElementById("game").clientWidth * window.devicePixelRatio;
var RENDERER_HEIGHT = (window.innerHeight / 1.40) * window.devicePixelRatio;
var DEBUG = true;
var config = {
    portalTint: 0xff43c8,
    tileTint: 0xFFFFFF,
    wallTint: 0xDDDDDD,
    vaultWallTint: 0x3F2D2A,
    vaultFloorTint: 0x2B1E1C,
    fluidTint: 0x0ACED6,
    oreTint: 0xF3F3F3,
    cubotHoverTint: 0x00FF00,
    cubotTint: 0xFFFFFF,
    textFill: "#FFFFFF",
    textStroke: "#9298a8",
    biomassTint: 0x63B85F,
    biomassHoverTint: 0x00FF00,
    tileHoverTint: 0x00FF00,
    itemIron: 0x434341,
    textIron: "#434341",
    itemCopper: 0xC87D38,
    textCopper: "#C87D38",
    hologramFill: "#0aced6",
    hologramStroke: "#12FFB0",
    copperFill: "#C87D38",
    plainSprite: "tiles/tile",
    magneticSprite: "tiles/magneticTile",
    wallSprite: "tiles/bigTile",
    wallSprite2: "tiles/bigTile2",
    walkDuration: 800,
    holoStyle: function (fill) {
        return {
            fontSize: 32,
            fill: fill ? fill : config.hologramFill,
            stroke: config.hologramStroke,
            strokeThickness: 1,
            font: "fixedsys"
        };
    },
    kbBufferX: 350,
    kbBufferY: 35,
    arrowTextStyle: {
        fontSize: 32,
        fill: "#ffffff",
        stroke: "#9298a8",
        strokeThickness: 1,
        font: "fixedsys"
    },
    lowEnergy: 100,
    lowEnergyTint: 0xCC0000,
    bigMessageFill: "#ff803d",
    arrowTint: 0xFFFFFF,
    arrowHoverTint: 0x00FF00,
    selfUsernameColor: 0xFB4D0A,
    otherCubotAlpha: 0.6,
    defaultWorldSize: 16
};
var Util = (function () {
    function Util() {
    }
    Util.getIsoY = function (y) {
        return Util.getIsoX(y);
    };
    Util.getIsoX = function (x) {
        return (x * 71.5);
    };
    Util.getDeltaX = function (direction) {
        switch (direction) {
            case Direction.NORTH:
            case Direction.SOUTH:
                return 0;
            case Direction.EAST:
                return 1;
            case Direction.WEST:
                return -1;
        }
    };
    Util.getDeltaY = function (direction) {
        switch (direction) {
            case Direction.EAST:
            case Direction.WEST:
                return 0;
            case Direction.NORTH:
                return -1;
            case Direction.SOUTH:
                return 1;
        }
    };
    Util.itemColor = function (item) {
        switch (item) {
            case 1:
                return config.biomassTint;
            case 3:
                return config.itemIron;
            case 4:
                return config.itemCopper;
        }
    };
    return Util;
}());
var Debug = (function () {
    function Debug() {
    }
    Debug.setTileAt = function (x, y, newTile) {
        mar.client.sendDebugCommand({
            t: "debug", command: "setTileAt", x: x, y: y, newTile: newTile,
            worldX: mar.client.worldX, worldY: mar.client.worldY, dimension: mar.client.dimension
        });
        mar.client.requestTerrain();
    };
    Debug.createWorld = function (x, y, dimension) {
        mar.client.sendDebugCommand({ t: "debug", command: "createWorld", worldX: x, worldY: y, dimension: dimension });
        window.setTimeout(mar.client.requestTerrain, 250);
    };
    Debug.createWorldHex = function (x, y, dimension) {
        mar.client.sendDebugCommand({
            t: "debug", command: "createWorld",
            worldX: parseInt(x, 16), worldY: parseInt(y, 16), dimension: dimension
        });
    };
    Debug.goTo = function (worldX, worldY, dimension) {
        mar.client.worldX = worldX;
        mar.client.worldY = worldY;
        mar.client.dimension = dimension;
        mar.client.requestTerrain();
    };
    Debug.goToHex = function (worldX, worldY, dimension) {
        mar.client.worldX = parseInt(worldX, 16);
        mar.client.worldY = parseInt(worldY, 16);
        mar.client.dimension = dimension;
        mar.client.requestTerrain();
    };
    Debug.killAll = function (x, y) {
        mar.client.sendDebugCommand({
            t: "debug", command: "killAll", x: x, y: y,
            worldX: mar.client.worldX, worldY: mar.client.worldY, dimension: mar.client.dimension
        });
    };
    Debug.objInfo = function (x, y) {
        mar.client.sendDebugCommand({
            t: "debug", command: "objInfo", x: x, y: y,
            worldX: mar.client.worldX, worldY: mar.client.worldY, dimension: mar.client.dimension
        });
    };
    Debug.userInfo = function (username) {
        mar.client.sendDebugCommand({ t: "debug", command: "userInfo", username: username });
    };
    Debug.moveObj = function (objectId, x, y) {
        mar.client.sendDebugCommand({ t: "debug", command: "moveObj", objectId: objectId, x: x, y: y });
        mar.client.requestObjects();
    };
    Debug.tpObj = function (objectId, x, y, worldX, worldY, dimension) {
        mar.client.sendDebugCommand({
            t: "debug", command: "tpObj", objectId: objectId, x: x, y: y, worldX: worldX,
            worldY: worldY, dimension: dimension
        });
        mar.client.requestObjects();
    };
    Debug.tpObjHex = function (objectId, x, y, worldX, worldY, dimension) {
        mar.client.sendDebugCommand({
            t: "debug", command: "tpObj", objectId: objectId, x: x, y: y, worldX: parseInt(worldX, 16),
            worldY: parseInt(worldY, 16), dimension: dimension
        });
        mar.client.requestObjects();
    };
    Debug.spawnObj = function (data) {
        mar.client.sendDebugCommand({
            t: "debug", command: "spawnObj", data: data,
            worldX: mar.client.worldX, worldY: mar.client.worldY, dimension: mar.client.dimension
        });
    };
    Debug.comPortMsg = function (objectId, message) {
        mar.client.sendDebugCommand({ t: "debug", command: "comPortMsg", objectId: objectId, message: message });
    };
    Debug.healObj = function (objectId, amount) {
        mar.client.sendDebugCommand({ t: "debug", command: "healObj", objectId: objectId, amount: amount });
    };
    Debug.damageObj = function (objectId, amount) {
        mar.client.sendDebugCommand({ t: "debug", command: "damageObj", objectId: objectId, amount: amount });
    };
    Debug.chargeShield = function (objectId, amount) {
        mar.client.sendDebugCommand({ t: "debug", command: "chargeShield", objectId: objectId, amount: amount });
    };
    Debug.setEnergy = function (objectId, amount) {
        mar.client.sendDebugCommand({ t: "debug", command: "setEnergy", objectId: objectId, amount: amount });
    };
    Debug.saveGame = function () {
        mar.client.sendDebugCommand({t: "debug", command: "saveGame"});
    };
    Debug.popItem = function (objectId) {
        mar.client.sendDebugCommand({t: "debug", command: "popItem", objectId: objectId});
    };
    Debug.putItem = function (objectId, item) {
        mar.client.sendDebugCommand({t: "debug", command: "putItem", objectId: objectId, item: item});
    };
    Debug.setInventoryPosition = function (objectId, position) {
        mar.client.sendDebugCommand({
            t: "debug",
            command: "setInventoryPosition",
            objectId: objectId,
            position: position
        });
    };
    return Debug;
}());
DEBUG = false;
var mar = new MarGame();
var KeyboardBuffer = (function (_super) {
    __extends(KeyboardBuffer, _super);
    function KeyboardBuffer() {
        var _this = _super !== null && _super.apply(this, arguments) || this;
        _this.keys = [];
        return _this;
    }
    KeyboardBuffer.prototype.getMessage = function () {
        var str = "KB: ";
        for (var i = 0; i < 16; i++) {
            if (this.keys[i] !== undefined) {
                str += this.keys[i].toString(16).toUpperCase() + " ";
            }
            else {
                str += "__ ";
            }
        }
        return str;
    };
    return KeyboardBuffer;
}(DebugMessage));
var ObjectsListener = (function () {
    function ObjectsListener() {
    }
    ObjectsListener.prototype.getListenedMessageType = function () {
        return "object";
    };
    ObjectsListener.prototype.handle = function (message) {
        if (DEBUG) {
            console.log("[MAR] Received " + message.objects.length + " objects");
        }
        if (mar.world != undefined) {
            mar.world.handleObjectsUpdate(message.objects);
        }
    };
    return ObjectsListener;
}());
var TickListener = (function () {
    function TickListener() {
    }
    TickListener.prototype.getListenedMessageType = function () {
        return "tick";
    };
    TickListener.prototype.handle = function (message) {
        mar.client.requestObjects();
        if (message.keys !== undefined) {
            mar.client.keyboardBuffer.keys = message.keys;
        }
        if (message.c != undefined) {
            mar.client.consoleScreen.handleConsoleBufferUpdate(message.c, message.cm);
            if (DEBUG) {
                console.log("[MAR] Received " + message.c.length + " console message(s)");
            }
        }
    };
    return TickListener;
}());
var UserInfoListener = (function () {
    function UserInfoListener() {
    }
    UserInfoListener.prototype.getListenedMessageType = function () {
        return "userInfo";
    };
    UserInfoListener.prototype.handle = function (message) {
        if (DEBUG) {
            console.log("[MAR] Received user info message");
        }
        mar.client.worldX = message.worldX;
        mar.client.worldY = message.worldY;
        mar.client.dimension = message.dimension;
        mar.client.maxWidth = message.maxWidth;
        mar.client.requestTerrain();
    };
    return UserInfoListener;
}());
var AuthListener = (function () {
    function AuthListener() {
    }
    AuthListener.prototype.getListenedMessageType = function () {
        return "auth";
    };
    AuthListener.prototype.handle = function (message) {
        if (DEBUG) {
            console.log("[MAR] Received auth response");
        }
        if (message.m === "ok") {
            if (DEBUG) {
                console.log("[MAR] Auth successful");
            }
            mar.client.requestUserInfo();
        } else if (message.m == "forbidden") {
            alert("Authentication failed. Guest accounts are blocked on this server");
        } else {
            alert("Authentication failed. Please make sure you are logged in and reload the page.");
        }
    };
    return AuthListener;
}());
var TerrainListener = (function () {
    function TerrainListener() {
    }
    TerrainListener.prototype.getListenedMessageType = function () {
        return "terrain";
    };
    TerrainListener.prototype.handle = function (message) {
        if (DEBUG) {
            console.log("[MAR] Received terrain");
        }
        if (mar.world) {
            mar.world.removeBigMessage();
        }
        if (message.ok) {
            var worldSize = message.size;
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
            }
            else {
                if (DEBUG) {
                    console.log("[MAR] Creating new World");
                }
                mar.world = new World(message.terrain, worldSize);
            }
        }
        else {
            if (DEBUG) {
                console.log("[MAR] World is not available");
            }
            if (mar.world != null) {
                if (DEBUG) {
                    console.log("[MAR] Updating World terrain");
                }
                mar.world.updateTerrain([], config.defaultWorldSize);
            }
            else {
                if (DEBUG) {
                    console.log("[MAR] Creating new World");
                }
                mar.world = new World([], config.defaultWorldSize);
            }
            if (mar.world) {
                mar.world.setBigMessage("[Uncharted World]");
            }
        }
    };
    return TerrainListener;
}());
var CodeListener = (function () {
    function CodeListener() {
    }
    CodeListener.prototype.getListenedMessageType = function () {
        return "code";
    };
    CodeListener.prototype.handle = function (message) {
        ace.edit("editor").setValue(message.code);
    };
    return CodeListener;
}());
var CodeResponseListener = (function () {
    function CodeResponseListener() {
    }
    CodeResponseListener.prototype.getListenedMessageType = function () {
        return "codeResponse";
    };
    CodeResponseListener.prototype.handle = function (message) {
        alert("Uploaded and assembled " + message.bytes + " bytes (" + message.exceptions + " errors)");
    };
    return CodeResponseListener;
}());
var DebugResponseListener = (function () {
    function DebugResponseListener() {
    }
    DebugResponseListener.prototype.getListenedMessageType = function () {
        return "debug";
    };
    DebugResponseListener.prototype.handle = function (message) {
        console.log("> " + message.message);
    };
    return DebugResponseListener;
}());
var GameClient = (function () {
    function GameClient() {
        this.listeners = [];
        this.getServerInfo();
        this.consoleScreen = new PlainTextConsole(defaultText, "consoleText", "colorButton", "scrollButton", "resetButton");
    }
    GameClient.prototype.requestUserInfo = function () {
        if (DEBUG) {
            console.log("[MAR] Requesting user info");
        }
        this.socket.send(JSON.stringify({ t: "userInfo" }));
    };
    GameClient.prototype.requestTerrain = function () {
        if (DEBUG) {
            console.log("[MAR] Requesting terrain for world (" + this.worldX + ", " + this.worldY + ")");
        }
        this.socket.send(JSON.stringify({ t: "terrain", x: this.worldX, y: this.worldY, dimension: this.dimension }));
        this.requestObjects();
    };
    GameClient.prototype.uploadCode = function (code) {
        if (DEBUG) {
            console.log("[MAR] Uploaded code");
        }
        this.socket.send(JSON.stringify({ t: "uploadCode", code: code }));
    };
    GameClient.prototype.reloadCode = function () {
        if (DEBUG) {
            console.log("[MAR] Reloading code");
        }
        this.socket.send(JSON.stringify({ t: "codeRequest" }));
    };
    GameClient.prototype.sendKeyPress = function (key) {
        if (DEBUG) {
            console.log("[MAR] Sent KeyPress: " + key);
        }
        if (key !== 0) {
            this.socket.send(JSON.stringify({ t: "k", k: key }));
        }
    };
    GameClient.prototype.requestObjects = function () {
        if (DEBUG) {
            console.log("[MAR] Requesting game objects");
        }
        this.socket.send(JSON.stringify({ t: "object", x: this.worldX, y: this.worldY, dimension: this.dimension }));
    };
    GameClient.prototype.sendDebugCommand = function (json) {
        this.socket.send(JSON.stringify(json));
    };
    GameClient.prototype.getServerInfo = function () {
        var self = this;
        if (DEBUG) {
            console.log("[MAR] Getting server info... ");
        }
        var xhr = new XMLHttpRequest();
        xhr.open("GET", "./server_info", true);
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                if (DEBUG) {
                    console.log("[MAR] Received server info " + xhr.responseText);
                }
                setTimeout(function () {
                    return self.connectToGameServer(JSON.parse(xhr.responseText));
                }, 100);
            }
        };
        xhr.send(null);
    };
    GameClient.prototype.connectToGameServer = function (info) {
        var self = this;
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
                var message;
                if (DEBUG) {
                    console.log("[MAR] Received: " + received.data);
                }
                message = JSON.parse(received.data);
                for (var i = 0; i < self.listeners.length; i++) {
                    if (self.listeners[i].getListenedMessageType() === message.t) {
                        self.listeners[i].handle(message);
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
    };
    GameClient.prototype.initGame = function () {
        if (this.username != "guest") {
            var self_1 = this;
            this.keyboardBuffer = new KeyboardBuffer(config.kbBufferX, config.kbBufferY);
            mar.addDebugMessage(this.keyboardBuffer);
            mar.game.input.keyboard.onDownCallback = function (event) {
                if (document.activeElement === document.getElementById("game")) {
                    if ((event.keyCode >= 37 && event.keyCode <= 40) || event.keyCode === 116 || event.keyCode === 32) {
                        event.preventDefault();
                    }
                    if (self_1.username !== "guest" && self_1.keyboardBuffer.keys.length <= 16) {
                        self_1.sendKeyPress(event.keyCode);
                        self_1.keyboardBuffer.keys.push(event.keyCode);
                    }
                }
            };
        }
    };
    GameClient.prototype.findMyRobot = function () {
        if (this.username == "guest") {
            alert("You are not logged in!");
        }
        else {
            this.requestUserInfo();
        }
    };
    return GameClient;
}());
var ObjectType;
(function (ObjectType) {
    ObjectType["CUBOT"] = "net.simon987.cubotplugin.Cubot";
    ObjectType["BIOMASS"] = "net.simon987.biomassplugin.BiomassBlob";
    ObjectType["HARVESTER_NPC"] = "net.simon987.npcplugin.HarvesterNPC";
    ObjectType["FACTORY"] = "net.simon987.npcplugin.Factory";
    ObjectType["RADIO_TOWER"] = "net.simon987.npcplugin.RadioTower";
    ObjectType["VAULT_DOOR"] = "net.simon987.npcplugin.VaultDoor";
    ObjectType["OBSTACLE"] = "net.simon987.npcplugin.Obstacle";
    ObjectType["ELECTRIC_BOX"] = "net.simon987.npcplugin.ElectricBox";
    ObjectType["PORTAL"] = "net.simon987.npcplugin.Portal";
})(ObjectType || (ObjectType = {}));
var ItemType;
(function (ItemType) {
    ItemType[ItemType["BIOMASS"] = 1] = "BIOMASS";
    ItemType[ItemType["IRON"] = 3] = "IRON";
    ItemType[ItemType["COPPER"] = 4] = "COPPER";
})(ItemType || (ItemType = {}));
var Action;
(function (Action) {
    Action[Action["IDLE"] = 0] = "IDLE";
    Action[Action["DIGGING"] = 1] = "DIGGING";
    Action[Action["WALKING"] = 2] = "WALKING";
    Action[Action["WITHDRAWING"] = 3] = "WITHDRAWING";
    Action[Action["DEPOSITING"] = 4] = "DEPOSITING";
    Action[Action["LISTENING"] = 5] = "LISTENING";
    Action[Action["JUMPING"] = 6] = "JUMPING";
    Action[Action["ATTACKING"] = 7] = "ATTACKING";
})(Action || (Action = {}));
var GameObject = (function (_super) {
    __extends(GameObject, _super);
    function GameObject(x, y, z, key, frame) {
        return _super.call(this, mar.game, x, y, z, key, frame) || this;
    }
    GameObject.createObject = function (json) {
        switch (json.t) {
            case ObjectType.CUBOT:
                return new Cubot(json);
            case ObjectType.BIOMASS:
                return new BiomassBlob(json);
            case ObjectType.HARVESTER_NPC:
                return new HarvesterNPC(json);
            case ObjectType.FACTORY:
                return new Factory(json);
            case ObjectType.RADIO_TOWER:
                return new RadioTower(json);
            case ObjectType.VAULT_DOOR:
                return new VaultDoor(json);
            case ObjectType.OBSTACLE:
                return null;
            case ObjectType.ELECTRIC_BOX:
                return new ElectricBox(json);
            case ObjectType.PORTAL:
                return new Portal(json);
            default:
                return null;
        }
    };
    GameObject.prototype.setText = function (text) {
        this.text = mar.game.make.text(0, 0, text, {
            fontSize: 22,
            fill: config.textFill,
            stroke: config.textStroke,
            strokeThickness: 2,
            font: "fixedsys"
        });
        this.text.anchor.set(0.5, 0);
        this.addChild(this.text);
    };
    GameObject.prototype.isAt = function (x, y) {
        return x == this.tileX && y == this.tileY;
    };
    return GameObject;
}(Phaser.Plugin.Isometric.IsoSprite));
var HologramMode;
(function (HologramMode) {
    HologramMode[HologramMode["CLEARED"] = 0] = "CLEARED";
    HologramMode[HologramMode["HEX"] = 1] = "HEX";
    HologramMode[HologramMode["STRING"] = 2] = "STRING";
    HologramMode[HologramMode["DEC"] = 3] = "DEC";
})(HologramMode || (HologramMode = {}));
var Cubot = (function (_super) {
    __extends(Cubot, _super);
    function Cubot(json) {
        var _this = _super.call(this, Util.getIsoX(json.x), Util.getIsoY(json.y), 15, "sheet", "objects/blankCubot") || this;
        _this.queuedAnimations = [];
        _this.hovered = false;
        _this.anchor.setTo(0.5, 0);
        if (DEBUG) {
            console.log("Creating Cubot object");
        }
        _this.id = json.i;
        _this.tileX = json.x;
        _this.tileY = json.y;
        _this.username = json.parent;
        _this.heldItem = json.heldItem;
        _this.direction = json.direction;
        _this.action = json.action;
        _this.energy = json.energy;
        _this.cubotSprite = mar.game.make.sprite(0, 0, "sheet", null);
        _this.cubotSprite.anchor.set(0.5, 0);
        _this.addChild(_this.cubotSprite);
        _this.cubotSprite.animations.add("walk_w", mar.animationFrames.walk_w);
        _this.cubotSprite.animations.add("walk_s", mar.animationFrames.walk_s);
        _this.cubotSprite.animations.add("walk_e", mar.animationFrames.walk_e);
        _this.cubotSprite.animations.add("walk_n", mar.animationFrames.walk_n);
        _this.cubotSprite.animations.add("dig_w", mar.animationFrames.dig_w);
        _this.cubotSprite.animations.add("dig_s", mar.animationFrames.dig_s);
        _this.cubotSprite.animations.add("dig_e", mar.animationFrames.dig_e);
        _this.cubotSprite.animations.add("dig_n", mar.animationFrames.dig_n);
        _this.createUsername();
        _this.updateDirection();
        _this.tint = _this.getTint();
        _this.laserEmitter = mar.game.make.emitter(0, 20, 100);
        _this.addChild(_this.laserEmitter);
        _this.laserEmitter.makeParticles("sheet", ["effects/beam"], 100);
        _this.laserEmitter.gravity = new Phaser.Point(0, 0);
        _this.shieldBackSprite = mar.game.add.sprite(0, 0, "sheet", "objects/shieldBack");
        _this.shieldBackSprite.anchor.setTo(0.5, 0.1);
        _this.shieldBackSprite.alpha = 0.4;
        mar.game.add.tween(_this.shieldBackSprite).to({ alpha: 0.8 }, 1500, Phaser.Easing.Linear.None, true, 0, -1, true);
        _this.addChildAt(_this.shieldBackSprite, 0);
        _this.shieldFrontSprite = mar.game.add.sprite(0, 0, "sheet", "objects/shieldFront");
        _this.shieldFrontSprite.anchor.setTo(0.5, 0.1);
        _this.shieldFrontSprite.alpha = 0.4;
        mar.game.add.tween(_this.shieldFrontSprite).to({ alpha: 0.8 }, 1500, Phaser.Easing.Linear.None, true, 0, -1, true);
        _this.addChild(_this.shieldFrontSprite);
        _this.setShield(false);
        return _this;
    }
    Cubot.prototype.setShield = function (shield) {
        this.shieldBackSprite.visible = shield;
        this.shieldFrontSprite.visible = shield;
    };
    Cubot.prototype.onTileHover = function () {
        mar.game.add.tween(this).to({ isoZ: 45 }, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.game.add.tween(this.scale).to({ x: 1.2, y: 1.2 }, 200, Phaser.Easing.Linear.None, true);
        this.cubotSprite.tint = config.cubotHoverTint;
        if (this.text !== undefined) {
            this.text.visible = true;
        }
        this.hovered = true;
    };
    Cubot.prototype.onTileExit = function () {
        mar.game.add.tween(this).to({ isoZ: 15 }, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({ x: 1, y: 1 }, 200, Phaser.Easing.Linear.None, true);
        if (this.text !== undefined) {
            this.text.visible = false;
        }
        this.hovered = false;
        this.cubotSprite.tint = this.getTint();
    };
    Cubot.prototype.makeLaserAttack = function () {
        var dX, dY, angle;
        switch (this.direction) {
            case Direction.NORTH:
                angle = 333.4;
                break;
            case Direction.SOUTH:
                angle = 153.4;
                break;
            case Direction.WEST:
                angle = 206.6;
                break;
            case Direction.EAST:
                angle = 26.6;
                break;
        }
        this.laserEmitter.minParticleSpeed.setTo(1000, 1000);
        this.laserEmitter.maxParticleSpeed.setTo(1700, 1700);
        this.laserEmitter.minAngle = angle;
        this.laserEmitter.maxAngle = angle;
        this.laserEmitter.maxRotation = 0;
        this.laserEmitter.start(true, 1000, null, 3);
    };
    Cubot.prototype.getTint = function () {
        if (!this.hovered) {
            if (this.energy <= config.lowEnergy) {
                return config.lowEnergyTint;
            }
            else {
                return config.cubotTint;
            }
        }
        else {
            return config.cubotHoverTint;
        }
    };
    Cubot.prototype.updateObject = function (json) {
        if (DEBUG) {
            console.log("Updating Cubot object");
        }
        this.action = json.action;
        this.energy = json.energy;
        this.direction = json.direction;
        this.shield = json.shield;
        this.createInventory([json.heldItem]);
        this.heldItem = json.heldItem;
        this.cubotSprite.tint = this.getTint();
        if (!this.isAt(json.x, json.y)) {
            if (this.action == Action.WALKING) {
                this.tileX = json.x;
                this.tileY = json.y;
                this.walk();
            }
        }
        if (this.action == Action.DIGGING) {
            switch (this.direction) {
                case Direction.NORTH:
                    this.cubotSprite.animations.play("dig_n", 60);
                    break;
                case Direction.SOUTH:
                    this.cubotSprite.animations.play("dig_s", 60);
                    break;
                case Direction.EAST:
                    this.cubotSprite.animations.play("dig_e", 60);
                    break;
                case Direction.WEST:
                    this.cubotSprite.animations.play("dig_w", 60);
                    break;
            }
        }
        else if (this.action == Action.ATTACKING) {
            this.makeLaserAttack();
        }
        this.updateDirection();
        var holoHw = json["net.simon987.cubotplugin.CubotHologram"];
        this.updateHologram(holoHw.mode, holoHw.color, holoHw.value, holoHw.string);
        this.setShield(this.shield > 0);
    };
    Cubot.prototype.updateHologram = function (holoMode, holoColor, holoValue, holoStr) {
        var fillColor = (holoColor & 0xFFFFFF).toString(16);
        fillColor = "#" + ("000000".substr(fillColor.length) + fillColor);
        if (this.hologram == undefined) {
            this.hologram = mar.game.make.text(0, 32, "");
            this.hologram.anchor.set(0.5, 0);
            this.addChild(this.hologram);
            this.hologram.setStyle(config.holoStyle(fillColor));
        }
        else {
            this.hologram.setStyle(config.holoStyle(fillColor));
        }
        switch (holoMode) {
            case HologramMode.CLEARED:
                this.hologram.text = "";
                break;
            case HologramMode.DEC:
                this.hologram.text = Number(holoValue).toString();
                break;
            case HologramMode.HEX:
                this.hologram.text = "0x" + ("0000" + Number(holoValue).toString(16).toUpperCase()).slice(-4);
                break;
            case HologramMode.STRING:
                this.hologram.text = holoStr.replace(/[\n|\t]/g, '');
                break;
        }
    };
    Cubot.prototype.updateDirection = function () {
        switch (this.direction) {
            case Direction.NORTH:
                this.cubotSprite.animations.frameName = "cubot/walk_n/0001";
                break;
            case Direction.EAST:
                this.cubotSprite.animations.frameName = "cubot/walk_e/0001";
                break;
            case Direction.SOUTH:
                this.cubotSprite.animations.frameName = "cubot/walk_s/0001";
                break;
            case Direction.WEST:
                this.cubotSprite.animations.frameName = "cubot/walk_w/0001";
                break;
        }
    };
    Cubot.prototype.walk = function () {
        var self = this;
        var walkAnimation = function (duration) {
            var tween = mar.game.add.tween(self).to({ isoX: Util.getIsoX(self.tileX), isoY: Util.getIsoY(self.tileY) }, duration, Phaser.Easing.Linear.None, true);
            switch (self.direction) {
                case Direction.NORTH:
                    self.cubotSprite.animations.play("walk_n", 60, true);
                    break;
                case Direction.SOUTH:
                    self.cubotSprite.animations.play("walk_s", 60, true);
                    break;
                case Direction.EAST:
                    self.cubotSprite.animations.play("walk_e", 60, true);
                    break;
                case Direction.WEST:
                    self.cubotSprite.animations.play("walk_w", 60, true);
                    break;
            }
            tween.onComplete.add(function () {
                self.cubotSprite.animations.stop();
                self.updateDirection();
                self.isoX = Util.getIsoX(self.tileX);
                self.isoY = Util.getIsoY(self.tileY);
                self.onTileExit();
                for (var i = 0; i < self.queuedAnimations.length; i++) {
                    self.queuedAnimations[i](config.walkDuration / 2);
                    self.queuedAnimations.splice(i, 1);
                }
            });
        };
        if (this.cubotSprite.animations.currentAnim.isPlaying) {
            this.queuedAnimations.push(walkAnimation);
        }
        else {
            walkAnimation(config.walkDuration);
        }
    };
    Cubot.prototype.createUsername = function () {
        var username = mar.game.make.text(0, -24, this.username, {
            fontSize: 22,
            fill: config.textFill,
            stroke: config.textStroke,
            strokeThickness: 2,
            font: "fixedsys"
        });
        username.alpha = 0.85;
        username.anchor.set(0.5, 0);
        if (this.username === mar.client.username) {
            username.tint = config.selfUsernameColor;
        }
        else {
            this.alpha = config.otherCubotAlpha;
        }
        this.addChild(username);
    };
    Cubot.prototype.createInventory = function (items) {
        if (this.inventory != undefined) {
            this.inventory.destroy();
        }
        var inventory = mar.game.make.group();
        switch (items.length) {
            case 0:
                this.inventory = inventory;
                this.addChild(inventory);
                break;
            case 1:
                if (items[0] !== 0) {
                    var shadow = mar.game.make.sprite(0, 0, "sheet", "inventory/inv1x1");
                    shadow.anchor.set(0.5, 0.1);
                    shadow.alpha = 0.5;
                    var item = mar.game.make.sprite(0, 0, "sheet", "inventory/item");
                    item.anchor.set(0.5, 0.1);
                    item.tint = Util.itemColor(items[0]);
                    inventory.addChild(shadow);
                    inventory.addChild(item);
                }
                this.inventory = inventory;
                this.addChild(inventory);
                break;
        }
    };
    return Cubot;
}(GameObject));
var HarvesterNPC = (function (_super) {
    __extends(HarvesterNPC, _super);
    function HarvesterNPC(json) {
        var _this = _super.call(this, json) || this;
        _this.cubotSprite.animations.add("walk_w", mar.animationFrames.harvester_walk_w);
        _this.cubotSprite.animations.add("walk_s", mar.animationFrames.harvester_walk_s);
        _this.cubotSprite.animations.add("walk_e", mar.animationFrames.harvester_walk_e);
        _this.cubotSprite.animations.add("walk_n", mar.animationFrames.harvester_walk_n);
        _this.updateDirection();
        _this.setText("Harvester NPC");
        _this.text.visible = false;
        return _this;
    }
    HarvesterNPC.prototype.getTint = function () {
        return config.cubotTint;
    };
    HarvesterNPC.prototype.updateDirection = function () {
        switch (this.direction) {
            case Direction.NORTH:
                this.cubotSprite.animations.frameName = "harvester/walk_n/0001";
                break;
            case Direction.EAST:
                this.cubotSprite.animations.frameName = "harvester/walk_e/0001";
                break;
            case Direction.SOUTH:
                this.cubotSprite.animations.frameName = "harvester/walk_s/0001";
                break;
            case Direction.WEST:
                this.cubotSprite.animations.frameName = "harvester/walk_w/0001";
                break;
        }
    };
    HarvesterNPC.prototype.updateObject = function (json) {
        if (DEBUG) {
            console.log("Updating Harvester NPC object");
        }
        this.action = json.action;
        this.direction = json.direction;
        if (!this.isAt(json.x, json.y)) {
            if (this.action == Action.WALKING) {
                this.tileX = json.x;
                this.tileY = json.y;
                this.walk();
            }
        }
        this.updateDirection();
    };
    HarvesterNPC.prototype.createUsername = function () {
    };
    return HarvesterNPC;
}(Cubot));
var BiomassBlob = (function (_super) {
    __extends(BiomassBlob, _super);
    function BiomassBlob(json) {
        var _this = _super.call(this, Util.getIsoX(json.x), Util.getIsoY(json.y), 10, "sheet", 1) || this;
        if (DEBUG) {
            console.log("Creating Biomass object");
        }
        _this.anchor.set(0.5, 0);
        _this.id = json.i;
        _this.tileX = json.x;
        _this.tileY = json.y;
        _this.tint = config.biomassTint;
        _this.animations.add("idle", mar.animationFrames.biomassIdle);
        _this.animations.play("idle", 45, true);
        _this.setText("Biomass");
        _this.text.visible = false;
        return _this;
    }
    BiomassBlob.prototype.onTileHover = function () {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({ isoZ: 45 }, 200, Phaser.Easing.Quadratic.InOut, true);
        this.tint = config.biomassHoverTint;
        mar.game.add.tween(this.scale).to({ x: 1.2, y: 1.2 }, 200, Phaser.Easing.Linear.None, true);
        this.text.visible = true;
    };
    BiomassBlob.prototype.onTileExit = function () {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({ isoZ: 15 }, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({ x: 1, y: 1 }, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.biomassTint;
        this.text.visible = false;
    };
    BiomassBlob.prototype.updateObject = function (json) {
        if (DEBUG) {
            console.log("Updating Biomass object");
        }
    };
    return BiomassBlob;
}(GameObject));
var Factory = (function (_super) {
    __extends(Factory, _super);
    function Factory(json) {
        var _this = _super.call(this, Util.getIsoX(json.x), Util.getIsoY(json.y), 15, "sheet", "objects/factory") || this;
        _this.anchor.set(0.5, .25);
        _this.setText("Factory");
        _this.text.visible = false;
        _this.id = json.i;
        _this.tileX = json.x;
        _this.tileY = json.y;
        return _this;
    }
    Factory.prototype.onTileHover = function () {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({ isoZ: 25 }, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.game.add.tween(this.scale).to({ x: 1.06, y: 1.06 }, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotHoverTint;
        this.text.visible = true;
    };
    Factory.prototype.onTileExit = function () {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({ isoZ: 15 }, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({ x: 1, y: 1 }, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotTint;
        this.text.visible = false;
    };
    Factory.prototype.updateObject = function (json) {
    };
    Factory.prototype.isAt = function (x, y) {
        return (this.tileX === x || this.tileX + 1 === x) && (this.tileY + 1 === y || this.tileY === y);
    };
    ;
    return Factory;
}(GameObject));
var RadioTower = (function (_super) {
    __extends(RadioTower, _super);
    function RadioTower(json) {
        var _this = _super.call(this, Util.getIsoX(json.x), Util.getIsoY(json.y), 15, "sheet", "objects/RadioTower") || this;
        _this.anchor.set(0.48, 0.65);
        _this.setText("Radio Tower");
        _this.text.visible = false;
        _this.id = json.i;
        _this.tileX = json.x;
        _this.tileY = json.y;
        return _this;
    }
    RadioTower.prototype.onTileHover = function () {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({ isoZ: 25 }, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.game.add.tween(this.scale).to({ x: 1.06, y: 1.06 }, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotHoverTint;
        this.text.visible = true;
    };
    RadioTower.prototype.onTileExit = function () {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({ isoZ: 15 }, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({ x: 1, y: 1 }, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotTint;
        this.text.visible = false;
    };
    RadioTower.prototype.updateObject = function (json) {
    };
    return RadioTower;
}(GameObject));
var VaultDoor = (function (_super) {
    __extends(VaultDoor, _super);
    function VaultDoor(json) {
        var _this = _super.call(this, Util.getIsoX(json.x), Util.getIsoY(json.y), 0, "sheet", "objects/VaultDoor1") || this;
        _this.anchor.set(0.55, 0.55);
        _this.inputEnabled = true;
        _this.events.onInputDown.add(function (self) {
            Debug.goToHex("7FFF", "7FFF", "v" + self.id + "-");
            document.body.style.cursor = 'default';
            document.body.setAttribute("title", "");
        }, _this);
        _this.setText("Vault");
        _this.text.visible = false;
        _this.id = json.i;
        _this.tileX = json.x;
        _this.tileY = json.y;
        var screen = mar.game.make.sprite(-76, 4, "sheet", "objects/VaultDoorScreen/1");
        screen.animations.add("idle", mar.animationFrames.vaultDoorScreen);
        screen.animations.play("idle", 11, true);
        _this.addChild(screen);
        return _this;
    }
    VaultDoor.prototype.onTileHover = function () {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({ isoZ: 15 }, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.game.add.tween(this.scale).to({ x: 1.06, y: 1.06 }, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotHoverTint;
        this.text.visible = true;
        document.body.style.cursor = 'pointer';
        document.body.setAttribute("title", "Click to visit Vault");
    };
    VaultDoor.prototype.onTileExit = function () {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({ isoZ: 0 }, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({ x: 1, y: 1 }, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotTint;
        this.text.visible = false;
        document.body.style.cursor = 'default';
        document.body.setAttribute("title", "");
    };
    VaultDoor.prototype.updateObject = function (json) {
    };
    return VaultDoor;
}(GameObject));
var ElectricBox = (function (_super) {
    __extends(ElectricBox, _super);
    function ElectricBox(json) {
        var _this = _super.call(this, Util.getIsoX(json.x), Util.getIsoY(json.y), 15, "sheet", "objects/ElectricBox") || this;
        _this.anchor.set(0.5, 0.3);
        _this.setText("Electric Box");
        _this.text.visible = false;
        _this.id = json.i;
        _this.tileX = json.x;
        _this.tileY = json.y;
        _this.sparkEmitter = mar.game.make.emitter(0, 0, 10);
        _this.addChild(_this.sparkEmitter);
        _this.sparkEmitter.makeParticles("sheet", ["effects/spark"], 10);
        _this.sparkEmitter.minParticleSpeed.setTo(-250, -200);
        _this.sparkEmitter.maxParticleSpeed.setTo(250, 0);
        _this.sparkEmitter.gravity = new Phaser.Point(0, 500);
        window.setTimeout(_this.makeSparks, mar.game.rnd.between(5000, 25000), _this);
        return _this;
    }
    ElectricBox.prototype.onTileHover = function () {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({ isoZ: 25 }, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.game.add.tween(this.scale).to({ x: 1.06, y: 1.06 }, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotHoverTint;
        this.text.visible = true;
    };
    ElectricBox.prototype.onTileExit = function () {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({ isoZ: 15 }, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({ x: 1, y: 1 }, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotTint;
        this.text.visible = false;
    };
    ElectricBox.prototype.makeSparks = function (self) {
        self.sparkEmitter.start(true, 450, null, 10);
        window.setTimeout(self.makeSparks, mar.game.rnd.between(5000, 25000), self);
    };
    ElectricBox.prototype.updateObject = function (json) {
    };
    return ElectricBox;
}(GameObject));
var Portal = (function (_super) {
    __extends(Portal, _super);
    function Portal(json) {
        var _this = _super.call(this, Util.getIsoX(json.x), Util.getIsoY(json.y), 15, "sheet", "objects/Portal") || this;
        _this.anchor.set(0.5, 0.3);
        _this.tint = config.portalTint;
        _this.setText("Portal");
        _this.text.visible = false;
        _this.id = json.i;
        _this.tileX = json.x;
        _this.tileY = json.y;
        return _this;
    }
    Portal.prototype.onTileHover = function () {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({ isoZ: 25 }, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.game.add.tween(this.scale).to({ x: 1.06, y: 1.06 }, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotHoverTint;
        this.text.visible = true;
    };
    Portal.prototype.onTileExit = function () {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({ isoZ: 15 }, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({ x: 1, y: 1 }, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.portalTint;
        this.text.visible = false;
    };
    Portal.prototype.updateObject = function (json) {
    };
    return Portal;
}(GameObject));
var Direction;
(function (Direction) {
    Direction[Direction["NORTH"] = 0] = "NORTH";
    Direction[Direction["EAST"] = 1] = "EAST";
    Direction[Direction["SOUTH"] = 2] = "SOUTH";
    Direction[Direction["WEST"] = 3] = "WEST";
})(Direction || (Direction = {}));
var TileType;
(function (TileType) {
    TileType[TileType["PLAIN"] = 0] = "PLAIN";
    TileType[TileType["WALL"] = 1] = "WALL";
    TileType[TileType["IRON"] = 2] = "IRON";
    TileType[TileType["COPPER"] = 3] = "COPPER";
    TileType[TileType["VAULT_FLOOR"] = 4] = "VAULT_FLOOR";
    TileType[TileType["VAULT_WALL"] = 5] = "VAULT_WALL";
    TileType[TileType["FLUID"] = 6] = "FLUID";
    TileType[TileType["MAGNETIC"] = 7] = "MAGNETIC";
})(TileType || (TileType = {}));
var Tile = (function (_super) {
    __extends(Tile, _super);
    function Tile(x, y, sprite, anchorY) {
        var _this = _super.call(this, mar.game, Util.getIsoX(x), Util.getIsoY(y), 0, 'sheet', sprite) || this;
        _this.baseZ = 0;
        _this.tileX = x;
        _this.tileY = y;
        _this.anchor.set(0.5, anchorY);
        return _this;
    }
    Tile.createTile = function (type, x, y) {
        switch (type) {
            case TileType.WALL:
                return new WallTile(x, y);
            case TileType.IRON:
                return new IronTile(x, y);
            case TileType.COPPER:
                return new CopperTile(x, y);
            case TileType.VAULT_FLOOR:
                return new VaultFloorTile(x, y);
            case TileType.VAULT_WALL:
                return new VaultWallTile(x, y);
            case -1:
                return new VoidTile(x, y);
            case TileType.FLUID:
                return new FluidTile(x, y);
            case TileType.MAGNETIC:
                return new MagneticTile(x, y);
            case TileType.PLAIN:
            default:
                return new PlainTile(x, y);
        }
    };
    Tile.prototype.onHover = function () {
        this.tint = config.tileHoverTint;
        mar.game.add.tween(this).to({ isoZ: this.baseZ + 8 }, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.tileIndicator.tileX = this.tileX;
        mar.tileIndicator.tileY = this.tileY;
        mar.tileIndicator.tileType = this.tileType;
    };
    Tile.prototype.onExit = function () {
        this.tint = this.baseTint;
        mar.game.add.tween(this).to({ isoZ: this.baseZ }, 200, Phaser.Easing.Quadratic.InOut, true);
    };
    Tile.prototype.setText = function (text, fillColor) {
        if (this.textSprite !== undefined) {
            this.textSprite.destroy();
        }
        this.textSprite = mar.game.make.text(0, 16, text, {
            fontSize: 22,
            fill: fillColor,
            stroke: "#FFFFFF",
            strokeThickness: 1,
            font: "fixedsys"
        });
        this.textSprite.alpha = 0.6;
        this.textSprite.anchor.set(0.5, 0);
        this.addChild(this.textSprite);
    };
    return Tile;
}(Phaser.Plugin.Isometric.IsoSprite));
var PlainTile = (function (_super) {
    __extends(PlainTile, _super);
    function PlainTile(x, y) {
        var _this = _super.call(this, x, y, config.plainSprite, 0) || this;
        _this.baseTint = config.tileTint;
        _this.tint = _this.baseTint;
        _this.tileType = "plain";
        return _this;
    }
    return PlainTile;
}(Tile));
var WallTile = (function (_super) {
    __extends(WallTile, _super);
    function WallTile(x, y) {
        var _this = _super.call(this, x, y, config.wallSprite, 0.2) || this;
        _this.baseTint = config.wallTint;
        _this.tint = _this.baseTint;
        _this.tileType = "wall";
        return _this;
    }
    return WallTile;
}(Tile));
var VaultWallTile = (function (_super) {
    __extends(VaultWallTile, _super);
    function VaultWallTile(x, y) {
        var _this = _super.call(this, x, y, config.wallSprite2, 0.29) || this;
        _this.baseTint = config.vaultWallTint;
        _this.tint = _this.baseTint;
        _this.tileType = "vault wall";
        return _this;
    }
    return VaultWallTile;
}(Tile));
var VaultFloorTile = (function (_super) {
    __extends(VaultFloorTile, _super);
    function VaultFloorTile(x, y) {
        var _this = _super.call(this, x, y, config.plainSprite, 0) || this;
        _this.baseTint = config.vaultFloorTint;
        _this.tint = _this.baseTint;
        _this.tileType = "vault floor";
        return _this;
    }
    return VaultFloorTile;
}(Tile));
var VoidTile = (function (_super) {
    __extends(VoidTile, _super);
    function VoidTile(x, y) {
        var _this = _super.call(this, x, y, config.plainSprite, 0) || this;
        _this.baseTint = config.vaultFloorTint;
        _this.tileType = "void";
        _this.alpha = 0;
        return _this;
    }
    VoidTile.prototype.onHover = function () {
        mar.tileIndicator.tileX = this.tileX;
        mar.tileIndicator.tileY = this.tileY;
        mar.tileIndicator.tileType = this.tileType;
    };
    VoidTile.prototype.onExit = function () {
    };
    return VoidTile;
}(Tile));
var FluidTile = (function (_super) {
    __extends(FluidTile, _super);
    function FluidTile(x, y) {
        var _this = _super.call(this, x, y, config.plainSprite, 0) || this;
        _this.baseTint = config.fluidTint;
        _this.tint = _this.baseTint;
        _this.alpha = 0.6;
        _this.baseZ = -15;
        _this.isoZ = _this.baseZ;
        _this.tileType = "fluid";
        return _this;
    }
    return FluidTile;
}(Tile));
var MagneticTile = (function (_super) {
    __extends(MagneticTile, _super);
    function MagneticTile(x, y) {
        var _this = _super.call(this, x, y, config.magneticSprite, 0) || this;
        _this.baseTint = 0xFFFFFF;
        _this.tint = _this.baseTint;
        _this.setText("Magnetic", config.textIron);
        _this.tileType = "Magnetic tile";
        return _this;
    }
    MagneticTile.prototype.onHover = function () {
        mar.game.add.tween(this).to({isoZ: this.baseZ + 30}, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.tileIndicator.tileX = this.tileX;
        mar.tileIndicator.tileY = this.tileY;
        mar.tileIndicator.tileType = this.tileType;
    };
    return MagneticTile;
}(Tile));
var IronTile = (function (_super) {
    __extends(IronTile, _super);
    function IronTile(x, y) {
        var _this = _super.call(this, x, y, config.plainSprite, 0) || this;
        _this.baseTint = config.oreTint;
        _this.tint = _this.baseTint;
        _this.setText("Iron", config.textIron);
        _this.tileType = "iron";
        return _this;
    }
    return IronTile;
}(Tile));
var CopperTile = (function (_super) {
    __extends(CopperTile, _super);
    function CopperTile(x, y) {
        var _this = _super.call(this, x, y, config.plainSprite, 0) || this;
        _this.baseTint = config.oreTint;
        _this.tint = _this.baseTint;
        _this.setText("Copper", config.textCopper);
        _this.tileType = "copper";
        return _this;
    }
    return CopperTile;
}(Tile));
var World = (function () {
    function World(terrain, size) {
        this.tiles = [];
        this.objects = [];
        this.northArrow = new WorldArrow(528, -20, "ui/arrow_north", Direction.NORTH);
        mar.isoGroup.add(this.northArrow);
        this.eastArrow = new WorldArrow(1115, 587, "ui/arrow_east", Direction.EAST);
        mar.isoGroup.add(this.eastArrow);
        this.southArrow = new WorldArrow(0, 0, "ui/arrow_south", Direction.SOUTH);
        mar.isoGroup.add(this.southArrow);
        this.westArrow = new WorldArrow(-70, 587, "ui/arrow_west", Direction.WEST);
        mar.isoGroup.add(this.westArrow);
        this.setTerrain(terrain, size);
    }
    World.prototype.setTerrain = function (terrain, size) {
        if (DEBUG) {
            console.log("[MAR] Creating tilemap of size " + size);
        }
        for (var x = 0; x < size; x++) {
            for (var y = 0; y < size; y++) {
                var tile = Tile.createTile(terrain[x * size + y], x, y);
                this.tiles.push(tile);
                mar.isoGroup.add(tile);
            }
        }
        this.eastArrow.isoX = 72.5 * (size) - 20;
        this.eastArrow.isoY = 32 * (size + 2);
        this.southArrow.isoX = 32 * (size + 1);
        this.southArrow.isoY = 72.5 * (size) + 20;
        this.northArrow.isoX = 32 * (size + 1);
        this.northArrow.isoY = -20;
        this.westArrow.isoX = -70;
        this.westArrow.isoY = 32 * (size + 2);
        mar.game.world.width = (size + 2) * 128;
        mar.game.world.height = (size + 2) * 64;
    };
    World.prototype.setBigMessage = function (msg) {
        this.bigMessage = mar.game.add.text(908, 450, msg, {
            fontSize: 46,
            fill: config.bigMessageFill,
            stroke: config.textStroke,
            strokeThickness: 2,
            font: "fixedsys"
        }, mar.textGroup);
    };
    World.prototype.removeBigMessage = function () {
        if (this.bigMessage != undefined) {
            this.bigMessage.destroy();
            if (DEBUG) {
                console.log("[MAR] Destroyed big message");
            }
        }
    };
    World.prototype.getObject = function (id) {
        for (var i = 0; i < this.objects.length; i++) {
            if (this.objects[i].id === id) {
                return this.objects[i];
            }
        }
        return null;
    };
    World.prototype.handleObjectsUpdate = function (objects) {
        for (var i = 0; i < this.objects.length; i++) {
            this.objects[i].updated = false;
        }
        for (var i = 0; i < objects.length; i++) {
            var existingObject = this.getObject(objects[i].i);
            if (existingObject !== null) {
                existingObject.updated = true;
                existingObject.updateObject(objects[i]);
            }
            else {
                var newObj = GameObject.createObject(objects[i]);
                if (newObj != null) {
                    newObj.updated = true;
                    this.objects.push(newObj);
                    mar.isoGroup.add(newObj);
                }
                else {
                    if (DEBUG) {
                        console.log("Couldn't create object with objType " + objects[i].t);
                    }
                }
            }
        }
        for (var i = 0; i < this.objects.length; i++) {
            if (!this.objects[i].updated) {
                if (mar.client.username !== "guest") {
                    if (this.objects[i] instanceof Cubot && this.objects[i].username === mar.client.username) {
                        mar.client.findMyRobot();
                        if (DEBUG) {
                            console.log("[MAR] Following Cubot " + mar.client.username);
                        }
                    }
                }
                this.objects[i].destroy();
                this.objects.splice(i, 1);
            }
        }
    };
    World.prototype.updateTerrain = function (terrain, size) {
        for (var i = 0; i < this.objects.length; i++) {
            this.objects[i].destroy();
        }
        for (var i = 0; i < this.tiles.length; i++) {
            this.tiles[i].destroy();
        }
        this.objects = [];
        this.tiles = [];
        this.setTerrain(terrain, size);
        mar.game.iso.topologicalSort(mar.isoGroup);
    };
    return World;
}());
var WorldArrow = (function (_super) {
    __extends(WorldArrow, _super);
    function WorldArrow(x, y, frame, direction) {
        var _this = _super.call(this, mar.game, x, y, 10, "sheet", frame) || this;
        var self = _this;
        _this.hoverText = mar.game.make.text(10, 10, Direction[direction], config.arrowTextStyle);
        _this.addChild(_this.hoverText);
        _this.hoverText.visible = false;
        _this.hoverText.anchor.set(0, 0);
        _this.inputEnabled = true;
        _this.events.onInputDown.add(function () {
            var newX = mar.client.worldX + Util.getDeltaX(direction);
            var newY = mar.client.worldY + Util.getDeltaY(direction);
            mar.client.worldX = newX % mar.client.maxWidth;
            mar.client.worldY = newY % mar.client.maxWidth;
            mar.client.requestTerrain();
        });
        _this.events.onInputOver.add(function () {
            self.tint = config.arrowHoverTint;
            self.hoverText.visible = true;
            document.body.style.cursor = "pointer";
        });
        _this.events.onInputOut.add(function () {
            self.tint = config.arrowTint;
            self.hoverText.visible = false;
            document.body.style.cursor = "default";
        });
        return _this;
    }
    return WorldArrow;
}(Phaser.Plugin.Isometric.IsoSprite));
var defaultText = " _______                    __     __\n" +
    "|   _   |.-----.---.-.----.|  |--.|__|.----.-----.----.-----.\n" +
    "|       ||  _  |  _  |  __||     ||  ||  __|  _  |   _|  _  |\n" +
    "|___|___||   __|___._|____||__|__||__||____|_____|__| |   __|\n" +
    "         |__|                                         |__|\n" +
    "\n" +
    "Version 1.5A, 1985-05-17\n" +
    "Initialising Universal Communication Port connection...Done\n" +
    "Current date is 2790-04-28\n" +
    "Cubot Status: Much Assembly Required";
var ConsoleMode;
(function (ConsoleMode) {
    ConsoleMode[ConsoleMode["CLEAR"] = 0] = "CLEAR";
    ConsoleMode[ConsoleMode["NORMAL"] = 1] = "NORMAL";
})(ConsoleMode || (ConsoleMode = {}));
var PlainTextConsoleMode = (function () {
    function PlainTextConsoleMode(lineWidth, dialImage) {
        this.width = lineWidth;
        this.dialImage = dialImage;
    }
    return PlainTextConsoleMode;
}());
var PlainTextConsole = (function () {
    function PlainTextConsole(text, id, colorId, scrollId, resetID) {
        this.colorToggled = false;
        this.autoScroll = false;
        this.modes = [];
        this.lastLineLength = 0;
        this.txtDiv = document.getElementById(id);
        this.colorButton = document.getElementById(colorId);
        this.scrollButton = document.getElementById(scrollId);
        this.resetButton = document.getElementById(resetID);
        var self = this;
        this.colorButton.onclick = function () {
            self.toggleColor(self);
        };
        this.scrollButton.onclick = function () {
            self.toggleScrolling(self);
        };
        this.resetButton.onclick = function () {
            self.reset(self);
        };
        this.txtDiv.innerHTML = text;
        this.consoleText = text;
        this.modes.push(new PlainTextConsoleMode(16, "./images/knob-170.png"));
        this.modes.push(new PlainTextConsoleMode(24, "./images/knob-123.png"));
        this.modes.push(new PlainTextConsoleMode(40, "./images/knob-90.png"));
        this.modes.push(new PlainTextConsoleMode(56, "./images/knob-65.png"));
        this.modes.push(new PlainTextConsoleMode(64, "./images/knob-10.png"));
        this.mode = 3;
    }
    PlainTextConsole.prototype.toggleColor = function (self) {
        if (self.colorToggled) {
            self.colorToggled = false;
            self.colorButton.classList.remove("btn-info");
            self.colorButton.classList.add("btn-outline-info");
            self.txtDiv.classList.remove("ctr-selection-inverted");
            self.txtDiv.classList.remove("ctr-text-inverted");
            self.txtDiv.classList.add("ctr-selection");
            self.txtDiv.classList.add("ctr-text");
        }
        else {
            self.colorToggled = true;
            self.colorButton.classList.remove("btn-outline-info");
            self.colorButton.classList.add("btn-info");
            self.txtDiv.classList.add("ctr-selection-inverted");
            self.txtDiv.classList.add("ctr-text-inverted");
            self.txtDiv.classList.remove("ctr-selection");
            self.txtDiv.classList.remove("ctr-text");
        }
    };
    PlainTextConsole.prototype.toggleScrolling = function (self) {
        if (self.autoScroll) {
            self.autoScroll = false;
            self.scrollButton.classList.add("btn-outline-info");
            self.scrollButton.classList.remove("btn-info");
        }
        else {
            self.autoScroll = true;
            self.scrollButton.classList.add("btn-info");
            self.scrollButton.classList.remove("btn-outline-info");
            self.txtDiv.scrollTop = self.txtDiv.scrollHeight;
        }
    };
    PlainTextConsole.prototype.reset = function (self) {
        self.txtDiv.innerHTML = "";
        self.consoleText = "";
        self.lastLineLength = 0;
    };
    PlainTextConsole.prototype.setMode = function (mode) {
        this.mode = mode;
    };
    PlainTextConsole.prototype.handleConsoleBufferUpdate = function (consoleBuffer, mode) {
        if (mode == ConsoleMode.CLEAR) {
            this.reset(this);
        }
        for (var i = 0; i < consoleBuffer.length; i++) {
            var zeroIndex = consoleBuffer[i].indexOf("\0");
            var message = consoleBuffer[i].substring(0, zeroIndex == -1 ? undefined : zeroIndex);
            for (var j = 0; j < message.length; j++) {
                if (message[j] == "\n") {
                    this.consoleText += "\n";
                    this.lastLineLength = 0;
                }
                else {
                    if (this.lastLineLength < this.modes[this.mode].width) {
                        this.consoleText += message[j];
                        this.lastLineLength++;
                    }
                    else {
                        this.consoleText += "\n";
                        this.consoleText += message[j];
                        this.lastLineLength = 1;
                    }
                }
            }
        }
        this.txtDiv.innerText = this.consoleText;
        if (this.autoScroll) {
            this.txtDiv.scrollTop = this.txtDiv.scrollHeight;
        }
    };
    return PlainTextConsole;
}());

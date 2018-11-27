// Typescript V2.4.1

let RENDERER_WIDTH = document.getElementById("game").clientWidth * window.devicePixelRatio;
let RENDERER_HEIGHT = (window.innerHeight / 1.40) * window.devicePixelRatio;

let DEBUG: boolean = true;

let config = {
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
    walkDuration: 800, //walk animation duration in ms
    holoStyle: (fill: string) => {
        return {
            fontSize: 32,
            fill: fill ? fill : config.hologramFill,
            stroke: config.hologramStroke,
            strokeThickness: 1,
            font: "fixedsys"
        }
    },
    kbBufferX: 350, ///Position of the keyboard buffer fill on screen
    kbBufferY: 35,
    arrowTextStyle: {
        fontSize: 32,
        fill: "#ffffff",
        stroke: "#9298a8",
        strokeThickness: 1,
        font: "fixedsys"
    },
    lowEnergy: 100, //Low energy threshold to change color
    lowEnergyTint: 0xCC0000,
    bigMessageFill: "#ff803d",
    arrowTint: 0xFFFFFF,
    arrowHoverTint: 0x00FF00,
    selfUsernameColor: 0xFB4D0A, //Color of own Cubot's username.
    otherCubotAlpha: 0.6,
    defaultWorldSize: 16 //Will fallback to this when server does not provide world width

};


class Util {

    //todo: find a more elegant way of doing this. Maybe this is related: https://github.com/lewster32/phaser-plugin-isometric/issues/7
    static getIsoY(y: number) {
        return Util.getIsoX(y);
    }

    static getIsoX(x: number) {
        return (x * 71.5)
    }

    static getDeltaX(direction: Direction) {
        switch (direction) {
            case Direction.NORTH:
            case Direction.SOUTH:
                return 0;
            case Direction.EAST:
                return 1;
            case Direction.WEST:
                return -1;
        }
    }

    static getDeltaY(direction: Direction) {

        switch (direction) {
            case Direction.EAST:
            case Direction.WEST:
                return 0;
            case Direction.NORTH:
                return -1;
            case Direction.SOUTH:
                return 1;
        }
    }

    static itemColor(item) {

        switch (item) {
            case 1:
                return config.biomassTint;
            case 3:
                return config.itemIron;
            case 4:
                return config.itemCopper;

        }
    }
}

class Debug {

    public static setTileAt(x, y, newTile) {
        mar.client.sendDebugCommand({
            t: "debug", command: "setTileAt", x: x, y: y, newTile: newTile,
            worldX: mar.client.worldX, worldY: mar.client.worldY, dimension: mar.client.dimension
        });

        mar.client.requestTerrain(); //Reload terrain
    }

    public static createWorld(x, y, dimension) {
        mar.client.sendDebugCommand({t: "debug", command: "createWorld", worldX: x, worldY: y, dimension: dimension});
        window.setTimeout(mar.client.requestTerrain, 250)
    }

    public static createWorldHex(x, y, dimension) {
        mar.client.sendDebugCommand({
            t: "debug", command: "createWorld",
            worldX: parseInt(x, 16), worldY: parseInt(y, 16), dimension: dimension
        });
    }

    public static goTo(worldX, worldY, dimension) {
        mar.client.worldX = worldX;
        mar.client.worldY = worldY;
        mar.client.dimension = dimension;
        mar.client.requestTerrain(); //Reload terrain
    }

    public static goToHex(worldX, worldY, dimension) {
        mar.client.worldX = parseInt(worldX, 16);
        mar.client.worldY = parseInt(worldY, 16);
        mar.client.dimension = dimension;
        mar.client.requestTerrain();
    }

    public static killAll(x, y) {
        mar.client.sendDebugCommand({
            t: "debug", command: "killAll", x: x, y: y,
            worldX: mar.client.worldX, worldY: mar.client.worldY, dimension: mar.client.dimension
        });
    }

    public static objInfo(x, y) {
        mar.client.sendDebugCommand({
            t: "debug", command: "objInfo", x: x, y: y,
            worldX: mar.client.worldX, worldY: mar.client.worldY, dimension: mar.client.dimension
        });
    }

    public static userInfo(username) {
        mar.client.sendDebugCommand({t: "debug", command: "userInfo", username: username});
    }

    public static moveObj(objectId, x, y) {
        mar.client.sendDebugCommand({t: "debug", command: "moveObj", objectId: objectId, x: x, y: y});
        mar.client.requestObjects();
    }

    public static tpObj(objectId, x, y, worldX, worldY, dimension) {
        mar.client.sendDebugCommand({
            t: "debug", command: "tpObj", objectId: objectId, x: x, y: y, worldX: worldX,
            worldY: worldY, dimension: dimension
        });
        mar.client.requestObjects();
    }

    public static tpObjHex(objectId, x, y, worldX, worldY, dimension) {
        mar.client.sendDebugCommand({
            t: "debug", command: "tpObj", objectId: objectId, x: x, y: y, worldX: parseInt(worldX, 16),
            worldY: parseInt(worldY, 16), dimension: dimension
        });
        mar.client.requestObjects();
    }

    public static spawnObj(data) {
        mar.client.sendDebugCommand({
            t: "debug", command: "spawnObj", data: data,
            worldX: mar.client.worldX, worldY: mar.client.worldY, dimension: mar.client.dimension
        });
    }

    public static comPortMsg(objectId, message) {
        mar.client.sendDebugCommand({t: "debug", command: "comPortMsg", objectId: objectId, message: message});
    }

    public static healObj(objectId, amount) {
        mar.client.sendDebugCommand({t: "debug", command: "healObj", objectId: objectId, amount: amount});
    }

    public static damageObj(objectId, amount) {
        mar.client.sendDebugCommand({t: "debug", command: "damageObj", objectId: objectId, amount: amount});
    }

    public static chargeShield(objectId, amount) {
        mar.client.sendDebugCommand({t: "debug", command: "chargeShield", objectId: objectId, amount: amount});
    }

    public static setEnergy(objectId, amount) {
        mar.client.sendDebugCommand({t: "debug", command: "setEnergy", objectId: objectId, amount: amount});
    }

    public static saveGame() {
        mar.client.sendDebugCommand({t: "debug", command: "saveGame"});
    }

    public static popItem(objectId) {
        mar.client.sendDebugCommand({t: "debug", command: "popItem", objectId: objectId})
    }

    public static putItem(objectId, item) {
        mar.client.sendDebugCommand({t: "debug", command: "putItem", objectId: objectId, item: item})
    }

    public static setInventoryPosition(objectId, position) {
        mar.client.sendDebugCommand({
            t: "debug",
            command: "setInventoryPosition",
            objectId: objectId,
            position: position
        })
    }


}

DEBUG = false;

let mar = new MarGame();


// Typescript V2.4.1

let RENDERER_WIDTH = document.getElementById("game").clientWidth * window.devicePixelRatio;
let RENDERER_HEIGHT = (window.innerHeight / 1.40) * window.devicePixelRatio;

let DEBUG: boolean = true;

let config = {
    kbBuffer: {
        x: 350, ///Position of the keyboard buffer fill on screen
        y: 35,
    },
    cubot: {
        tint: 0xFFFFFF,
        hoverTint: 0x00FF00,
        lowEnergyTint: 0xCC0000,
        walkDuration: 800, //walk animation duration in ms
        lowEnergy: 100, //Low energy threshold to change color
        otherCubotAlpha: 0.6,
    },
    hackedNpc: {
        tint: 0xE040FB,
    },
    biomass: {
        tint: 0x63B85F,
        tintHover: 0x00FF00,
    },
    tile: {
        hover: 0x00FF00,
        vaultWall: 0x3F2D2A,
        vaultFloor: 0x2B1E1C,
        fluid: 0x0ACED6,
        ore: 0xF3F3F3,
        plain: 0xFFFFFF,
        wall: 0xDDDDDD,
        plainSprite: "tiles/tile",
        magneticSprite: "tiles/magneticTile",
        wallSprite: "tiles/bigTile",
        wallSprite2: "tiles/bigTile2",
    },
    item: {
        ironColor: 0x434341,
        copperColor: 0xC87D38,
        blueprintColor: 0xaced6,
    },
    portal: {
        tint: 0xff43c8,
    },
    text: {
        textFill: "#FFFFFF",
        textStroke: "#9298a8",
        textIron: "#434341",
        textCopper: "#C87D38",
        hologramFill: "#0aced6",
        hologramStroke: "#12FFB0",
        selfUsername: 0xFB4D0A, //Color of own Cubot's username.
        bigMessageFill: "#ff803d",
        holoStyle: (fill: string) => {
            return {
                fontSize: 32,
                fill: fill ? fill : config.text.hologramFill,
                stroke: config.text.hologramStroke,
                strokeThickness: 1,
                font: "fixedsys"
            }
        },
    },
    arrow: {
        tint: 0xFFFFFF,
        tintHover: 0x00FF00,
    },
    arrowTextStyle: {
        fontSize: 32,
        fill: "#ffffff",
        stroke: "#9298a8",
        strokeThickness: 1,
        font: "fixedsys"
    },
    world: {
        defaultSize: 16 //Will fallback to this when server does not provide world width
    }
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
                return config.biomass.tint;
            case 3:
                return config.item.ironColor;
            case 4:
                return config.item.copperColor;
            case 5:
                return config.item.blueprintColor;
        }
    }
}

class Debug {

    public static SELF_ID = "";

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


class MarGame {

    isoGroup: Phaser.Group;
    textGroup: Phaser.Group;
    hudGroup: Phaser.Group;
    game: Phaser.Game;

    cursorPos: Phaser.Plugin.Isometric.Point3 = new Phaser.Plugin.Isometric.Point3();

    bootState;
    client: GameClient;

    debugMessages: DebugMessage[] = [];

    world: World;

    animationFrames: any = {};

    tileIndicator: TileIndicator;

    constructor() {

        let self = this;
        this.game = new Phaser.Game(RENDERER_WIDTH, RENDERER_HEIGHT, Phaser.AUTO, 'game', null, true, false);

        this.bootState = {
            preload: function () {
                if (DEBUG) {
                    console.log("[MAR] Loading sprites.png as JSONHash");
                }
                this.game.load.atlasJSONHash("sheet", "./images/sprites.png", "./images/sprites.json").onLoadComplete.add(function () {
                    self.game.time.advancedTiming = true;

                    //Add and enable the isometric plug-in.
                    if (DEBUG) {
                        console.log("[MAR] Enabling isometric plugin");
                    }
                    self.game.plugins.add(new Phaser.Plugin.Isometric(self.game));

                    //This is used to set a game canvas-based offset for the 0, 0, 0 isometric coordinate - by default
                    //this point would be at screen coordinates 0, 0 (top left) which is usually undesirable.
                    self.game.iso.anchor.setTo(0.5, 0);
                    //Bounds will be overwritten to fit world when changing world
                    self.game.world.setBounds(0, 0, 2200, 1100);

                    //Make camera more or less centered (tested on 1080 screen)
                    self.game.camera.x = 280;
                    self.game.camera.y = 90;

                    self.game.scale.scaleMode = Phaser.ScaleManager.RESIZE;
                    self.game.scale.pageAlignHorizontally = true;
                    self.game.scale.pageAlignVertically = true;

                    self.game.stage.disableVisibilityChange = true;

                    self.client = new GameClient();

                    //Grab focus when clicked (For chrome, Opera)
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

                //Update the cursor position.
                self.game.iso.unproject(self.game.input.activePointer.position, self.cursorPos);

                //Loop through all tiles and test to see if the 3D position from above intersects with the automatically generated IsoSprite tile bounds.
                self.isoGroup.forEach(function (tile: Tile) {

                    if (tile instanceof Tile) {
                        let inBounds = tile.isoBounds.containsXY(self.cursorPos.x, self.cursorPos.y);
                        //If it does, do a little animation and tint change.
                        if (!tile.selected && inBounds) {
                            tile.selected = true;

                            tile.onHover();

                            //Dispatch tile over for objects
                            self.isoGroup.forEach(function (obj: GameObject) {
                                if (obj instanceof GameObject && obj.onTileHover != undefined && obj.isAt(tile.tileX, tile.tileY)) {
                                    obj.onTileHover();
                                }
                            }, 1);
                        }
                        //If not, revert back to how it was.
                        else if (tile.selected && !inBounds) {
                            tile.selected = false;
                            tile.onExit();

                            //Dispatch tile exit objects
                            self.isoGroup.forEach(function (obj: GameObject) {
                                if (obj.onTileExit != undefined && obj.isAt(tile.tileX, tile.tileY)) {
                                    obj.onTileExit();
                                }
                            }, 0);
                        }
                    }

                }, 0);

                //Enable dragging the camera
                if (this.game.input.activePointer.isDown) {
                    if (this.game.origDragPoint) {
                        // move the camera by the amount the mouse has moved since last update
                        this.game.camera.x += this.game.origDragPoint.x - this.game.input.activePointer.position.x;
                        this.game.camera.y += this.game.origDragPoint.y - this.game.input.activePointer.position.y;
                    }
                    // set new drag origin to current position
                    this.game.origDragPoint = this.game.input.activePointer.position.clone();

                } else {
                    this.game.origDragPoint = null;
                }

                self.game.iso.topologicalSort(self.isoGroup);
            },
            render: function () {

                for (let i = 0; i < self.debugMessages.length; i++) {
                    self.game.debug.text(self.debugMessages[i].getMessage(), self.debugMessages[i].x,
                        self.debugMessages[i].y)
                }

            }
        };

        this.game.state.add('Boot', this.bootState);
        this.game.state.start('Boot');
    }

    public addDebugMessage(debugMsg: DebugMessage) {
        this.debugMessages.push(debugMsg)
    }

    private initialiseAnimations() {
        //Walk =-------------------------------------------------------
        //East
        this.animationFrames.walk_e_start = [];
        for (let i = 0; i < 10; i++) {
            this.animationFrames.walk_e_start.push("cubot/walk_e/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.walk_e = [];
        for (let i = 10; i < 30; i++) {
            this.animationFrames.walk_e.push("cubot/walk_e/" + ("0000" + i).slice(-4));
        }

        this.animationFrames.harvester_walk_e_start = [];
        for (let i = 0; i < 10; i++) {
            this.animationFrames.harvester_walk_e_start.push("harvester/walk_e/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.harvester_walk_e = [];
        for (let i = 10; i < 30; i++) {
            this.animationFrames.harvester_walk_e.push("harvester/walk_e/" + ("0000" + i).slice(-4));
        }
        //North
        this.animationFrames.walk_n_start = [];
        for (let i = 0; i < 10; i++) {
            this.animationFrames.walk_n_start.push("cubot/walk_n/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.walk_n = [];
        for (let i = 10; i < 30; i++) {
            this.animationFrames.walk_n.push("cubot/walk_n/" + ("0000" + i).slice(-4));
        }

        this.animationFrames.harvester_walk_n_start = [];
        for (let i = 0; i < 10; i++) {
            this.animationFrames.harvester_walk_n_start.push("harvester/walk_n/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.harvester_walk_n = [];
        for (let i = 10; i < 30; i++) {
            this.animationFrames.harvester_walk_n.push("harvester/walk_n/" + ("0000" + i).slice(-4));
        }
        //South
        this.animationFrames.walk_s_start = [];
        for (let i = 0; i < 10; i++) {
            this.animationFrames.walk_s_start.push("cubot/walk_s/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.walk_s = [];
        for (let i = 10; i < 30; i++) {
            this.animationFrames.walk_s.push("cubot/walk_s/" + ("0000" + i).slice(-4));
        }

        this.animationFrames.harvester_walk_s_start = [];
        for (let i = 0; i < 10; i++) {
            this.animationFrames.harvester_walk_s_start.push("harvester/walk_s/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.harvester_walk_s = [];
        for (let i = 10; i < 30; i++) {
            this.animationFrames.harvester_walk_s.push("harvester/walk_s/" + ("0000" + i).slice(-4));
        }
        //West
        this.animationFrames.walk_w_start = [];
        for (let i = 0; i < 10; i++) {
            this.animationFrames.walk_w_start.push("cubot/walk_w/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.walk_w = [];
        for (let i = 10; i < 30; i++) {
            this.animationFrames.walk_w.push("cubot/walk_w/" + ("0000" + i).slice(-4));
        }

        this.animationFrames.harvester_walk_w_start = [];
        for (let i = 0; i < 10; i++) {
            this.animationFrames.harvester_walk_w_start.push("harvester/walk_w/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.harvester_walk_w = [];
        for (let i = 10; i < 30; i++) {
            this.animationFrames.harvester_walk_w.push("harvester/walk_w/" + ("0000" + i).slice(-4));
        }

        //Dig =-------------------------------------------------------
        this.animationFrames.dig_e = [];
        for (let i = 1; i <= 41; i++) {
            this.animationFrames.dig_e.push("cubot/dig_e/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.dig_n = [];
        for (let i = 1; i <= 41; i++) {
            this.animationFrames.dig_n.push("cubot/dig_n/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.dig_s = [];
        for (let i = 1; i <= 41; i++) {
            this.animationFrames.dig_s.push("cubot/dig_s/" + ("0000" + i).slice(-4));
        }
        this.animationFrames.dig_w = [];
        for (let i = 1; i <= 41; i++) {
            this.animationFrames.dig_w.push("cubot/dig_w/" + ("0000" + i).slice(-4));
        }

        //Biomass =-------------------------------------------------------
        this.animationFrames.biomassIdle = [];
        for (let i = 1; i < 60; i++) {
            this.animationFrames.biomassIdle.push("objects/biomass/idle/" + ("0000" + i).slice(-4));
        }
        //Vault screen
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


    }

    private initialiseStaticHud() {

        //todo fix the compass sprite so the Y axis is facing the other way
        //this.game.add.sprite(0, this.game.camera.height - 150, "sheet", "ui/compass", this.hudGroup);

        this.addDebugMessage(new WorldIndicator(10, 20));

        this.tileIndicator = new TileIndicator(10, 40);
        this.addDebugMessage(this.tileIndicator);
    }

}

abstract class DebugMessage {

    public x: number;
    public y: number;

    constructor(x: number, y: number) {
        this.x = x;
        this.y = y;
    }

    abstract getMessage(): string;
}

/**
 * Indicates hovered tile
 */
class TileIndicator extends DebugMessage {

    public tileType: string;
    public tileX: number;
    public tileY: number;

    getMessage(): string {

        if (this.tileType != undefined) {

            return this.tileX + ", " + this.tileY + " : " + this.tileType;

        } else {
            return "";
        }

    }
}

/**
 * Indicates current World
 */
class WorldIndicator extends DebugMessage {

    getMessage(): string {

        if (mar.world != undefined) {

            return "World: " + mar.client.dimension + "(" + Number(mar.client.worldX).toString(16).toUpperCase() + ", " +
                Number(mar.client.worldY).toString(16).toUpperCase() + ")";

        } else {
            return "Loading..."
        }

    }
}
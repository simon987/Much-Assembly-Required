enum ObjectType {
    CUBOT = "net.simon987.cubotplugin.Cubot",
    BIOMASS = "net.simon987.biomassplugin.BiomassBlob",
    HARVESTER_NPC = "net.simon987.npcplugin.HarvesterNPC",
    FACTORY = "net.simon987.npcplugin.Factory",
    RADIO_TOWER = "net.simon987.npcplugin.RadioTower",
    VAULT_DOOR = "net.simon987.npcplugin.VaultDoor",
    OBSTACLE = "net.simon987.npcplugin.Obstacle",
    ELECTRIC_BOX = "net.simon987.npcplugin.ElectricBox",
    PORTAL = "net.simon987.npcplugin.Portal"
}

enum ItemType {
    BIOMASS = 1,
    IRON = 3,
    COPPER = 4
}

enum Action {
    IDLE,
    DIGGING,
    WALKING,
    WITHDRAWING,
    DEPOSITING,
    LISTENING,
    JUMPING,
    ATTACKING
}

abstract class GameObject extends Phaser.Plugin.Isometric.IsoSprite {

    public tileX: number;
    public tileY: number;

    id: number;
    protected direction: Direction;
    protected action: Action;
    protected shield: number;

    public updated: boolean;

    protected text: Phaser.Text;


    constructor(x: number, y: number, z: number, key: any, frame: any) {
        super(mar.game, x, y, z, key, frame);
    }

    public abstract updateObject(json): void;

    public abstract onTileHover(): void;

    public abstract onTileExit(): void;

    /**
     * Factory method for GameObjects
     */
    public static createObject(json): GameObject {
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
    }

    /**
     * Set text that will appear on top of the object. Usually used for hover text
     */
    protected setText(text: string): void {
        this.text = mar.game.make.text(0, 0, text, {
            fontSize: 22,
            fill: config.textFill,
            stroke: config.textStroke,
            strokeThickness: 2,
            font: "fixedsys"
        });

        this.text.anchor.set(0.5, 0);
        this.addChild(this.text);

    }

    /**
     * Tested to trigger onTileHover and onTileExit
     */
    public isAt(x: number, y: number): boolean {
        return x == this.tileX && y == this.tileY;
    }

}

enum HologramMode {
    CLEARED,
    HEX,
    STRING,
    DEC
}

class Cubot extends GameObject {
    laserEmitter: Phaser.Particles.Arcade.Emitter;

    username: string;
    heldItem: ItemType;
    energy: number;

    inventory: Phaser.Group;

    private hologram: Phaser.Text;

    /**
     * List of animation functions queued for execution.
     */
    queuedAnimations = [];

    private hovered: boolean = false;

    protected cubotSprite: Phaser.Sprite;
    private shieldBackSprite: Phaser.Sprite;
    private shieldFrontSprite: Phaser.Sprite;

    constructor(json) {
        //workaround for topological sort, needs sprite dimensions
        super(Util.getIsoX(json.x), Util.getIsoY(json.y), 15, "sheet", "objects/blankCubot");

        this.anchor.setTo(0.5, 0);

        if (DEBUG) {
            console.log("Creating Cubot object");
        }

        this.id = json.i;
        this.tileX = json.x;
        this.tileY = json.y;

        this.username = json.parent;
        this.heldItem = json.heldItem;
        this.direction = json.direction;
        this.action = json.action;
        this.energy = json.energy;

        this.cubotSprite = mar.game.make.sprite(0, 0, "sheet", null);
        this.cubotSprite.anchor.set(0.5, 0);
        this.addChild(this.cubotSprite);

        this.cubotSprite.animations.add("walk_w", mar.animationFrames.walk_w);
        this.cubotSprite.animations.add("walk_s", mar.animationFrames.walk_s,);
        this.cubotSprite.animations.add("walk_e", mar.animationFrames.walk_e);
        this.cubotSprite.animations.add("walk_n", mar.animationFrames.walk_n);
        this.cubotSprite.animations.add("dig_w", mar.animationFrames.dig_w);
        this.cubotSprite.animations.add("dig_s", mar.animationFrames.dig_s);
        this.cubotSprite.animations.add("dig_e", mar.animationFrames.dig_e);
        this.cubotSprite.animations.add("dig_n", mar.animationFrames.dig_n);

        this.createUsername();
        this.updateDirection();

        this.tint = this.getTint();

        //Laser particles
        this.laserEmitter = mar.game.make.emitter(0, 20, 100);
        this.addChild(this.laserEmitter);

        this.laserEmitter.makeParticles("sheet", ["effects/beam"], 100);
        this.laserEmitter.gravity = new Phaser.Point(0, 0);

        //Shield
        this.shieldBackSprite = mar.game.add.sprite(0, 0, "sheet", "objects/shieldBack");
        this.shieldBackSprite.anchor.setTo(0.5, 0.1);
        this.shieldBackSprite.alpha = 0.4;
        mar.game.add.tween(this.shieldBackSprite).to({alpha: 0.8}, 1500, Phaser.Easing.Linear.None, true, 0, -1, true);
        this.addChildAt(this.shieldBackSprite, 0);
        this.shieldFrontSprite = mar.game.add.sprite(0, 0, "sheet", "objects/shieldFront");
        this.shieldFrontSprite.anchor.setTo(0.5, 0.1);
        this.shieldFrontSprite.alpha = 0.4;
        mar.game.add.tween(this.shieldFrontSprite).to({alpha: 0.8}, 1500, Phaser.Easing.Linear.None, true, 0, -1, true);
        this.addChild(this.shieldFrontSprite);

        this.setShield(false);
    }

    public setShield(shield: boolean) {
        this.shieldBackSprite.visible = shield;
        this.shieldFrontSprite.visible = shield;
    }

    onTileHover(): void {

        mar.game.add.tween(this).to({isoZ: 45}, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.game.add.tween(this.scale).to({x: 1.2, y: 1.2}, 200, Phaser.Easing.Linear.None, true);

        this.cubotSprite.tint = config.cubotHoverTint;

        if (this.text !== undefined) {
            this.text.visible = true;
        }

        this.hovered = true;
    }


    onTileExit(): void {
        mar.game.add.tween(this).to({isoZ: 15}, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({x: 1, y: 1}, 200, Phaser.Easing.Linear.None, true);


        if (this.text !== undefined) {
            this.text.visible = false;
        }
        this.hovered = false;
        this.cubotSprite.tint = this.getTint();

    }

    public makeLaserAttack() {

        let dX, dY, angle;

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
    }

    public getTint(): number {
        if (!this.hovered) {
            if (this.energy <= config.lowEnergy) {
                return config.lowEnergyTint;
            } else {
                return config.cubotTint;
            }
        } else {
            return config.cubotHoverTint;
        }
    }

    updateObject(json): void {

        if (DEBUG) {
            console.log("Updating Cubot object")
        }

        this.action = json.action;
        this.energy = json.energy;
        this.direction = json.direction;
        this.shield = json.shield;

        //Update Inventory
        this.createInventory([json.heldItem]);
        this.heldItem = json.heldItem;

        //Update color
        this.cubotSprite.tint = this.getTint();

        //Update Location
        if (!this.isAt(json.x, json.y)) {
            //Location changed
            if (this.action == Action.WALKING) {
                //Walking..
                this.tileX = json.x;
                this.tileY = json.y;

                this.walk();

            }
            // else if (this.action == Action.JUMPING) {
            //     //TODO
            // }
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
        } else if (this.action == Action.ATTACKING) {

            this.makeLaserAttack()

        }

        this.updateDirection();
        let holoHw = json["net.simon987.cubotplugin.CubotHologram"];
        this.updateHologram(holoHw.mode, holoHw.color, holoHw.value, holoHw.string);

        //Update shield
        this.setShield(this.shield > 0)
    }

    private updateHologram(holoMode: HologramMode, holoColor: number, holoValue: number, holoStr: string): void {

        let fillColor: string = (holoColor & 0xFFFFFF).toString(16);
        fillColor = "#" + ("000000".substr(fillColor.length) + fillColor);

        //Create hologram if not exist, set style
        if (this.hologram == undefined) {
            this.hologram = mar.game.make.text(0, 32, "");
            this.hologram.anchor.set(0.5, 0);
            this.addChild(this.hologram);
            this.hologram.setStyle(config.holoStyle(fillColor));
        } else {
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
    }

    /**
     * Set appropriate frame based on direction
     */
    public updateDirection() {
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
    }

    /**
     * Initiate the walk animation. Handles multiple calls of this function even if the previous animations
     * were not completed
     */
    public walk() {

        let self = this;
        let walkAnimation = function (duration) {
            //Move the Cubot to desired tile
            let tween = mar.game.add.tween(self).to({isoX: Util.getIsoX(self.tileX), isoY: Util.getIsoY(self.tileY)},
                duration, Phaser.Easing.Linear.None, true);

            //Play appropriate animation
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

            //When moved to destination,
            tween.onComplete.add(function () {
                self.cubotSprite.animations.stop();

                self.updateDirection();

                //Resync position
                self.isoX = Util.getIsoX(self.tileX);
                self.isoY = Util.getIsoY(self.tileY);

                self.onTileExit();

                //Execute all the queued walk animations at a faster pace
                for (let i = 0; i < self.queuedAnimations.length; i++) {
                    self.queuedAnimations[i](config.walkDuration / 2);
                    self.queuedAnimations.splice(i, 1)
                }
            });

        };

        if (this.cubotSprite.animations.currentAnim.isPlaying) {
            //Queue up the animation
            this.queuedAnimations.push(walkAnimation);

        } else {
            walkAnimation(config.walkDuration);
        }


    }

    /**
     * Create the username text that will appear on top of the Cubot. Text will have alternate
     * color when current username matches. This function is also responsable for setting the
     * reduced transparency of other Cubots
     */
    public createUsername() {
        let username = mar.game.make.text(0, -24, this.username, {
            fontSize: 22,
            fill: config.textFill,
            stroke: config.textStroke,
            strokeThickness: 2,
            font: "fixedsys"
        });
        username.alpha = 0.85;
        username.anchor.set(0.5, 0);

        //Color own username
        if (this.username === mar.client.username) {
            username.tint = config.selfUsernameColor;
        } else {
            this.alpha = config.otherCubotAlpha;
        }
        this.addChild(username);
    }

    public createInventory(items: number[]): void {

        //Remove old inventory
        if (this.inventory != undefined) {
            this.inventory.destroy();
        }

        let inventory = mar.game.make.group();
        switch (items.length) {
            case 0:
                this.inventory = inventory;
                this.addChild(inventory);
                break;
            case 1:
                if (items[0] !== 0) {
                    let shadow = mar.game.make.sprite(0, 0, "sheet", "inventory/inv1x1");
                    shadow.anchor.set(0.5, 0.1);
                    shadow.alpha = 0.5;
                    let item = mar.game.make.sprite(0, 0, "sheet", "inventory/item");
                    item.anchor.set(0.5, 0.1);
                    item.tint = Util.itemColor(items[0]);


                    inventory.addChild(shadow);
                    inventory.addChild(item);

                }
                this.inventory = inventory;
                this.addChild(inventory);
                break;
        }

    }
}

class HarvesterNPC extends Cubot {

    constructor(json) {
        super(json);

        //Overwrite Cubot's animations
        this.cubotSprite.animations.add("walk_w", mar.animationFrames.harvester_walk_w);
        this.cubotSprite.animations.add("walk_s", mar.animationFrames.harvester_walk_s);
        this.cubotSprite.animations.add("walk_e", mar.animationFrames.harvester_walk_e);
        this.cubotSprite.animations.add("walk_n", mar.animationFrames.harvester_walk_n);

        this.updateDirection();
        this.setText("Harvester NPC");
        this.text.visible = false;
    }

    /**
     * Needs to be overridden because Cubot() calls getTint() when initialised
     */
    public getTint() {
        return config.cubotTint;
    }

    public updateDirection() {
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
    }

    updateObject(json) {
        if (DEBUG) {
            console.log("Updating Harvester NPC object")
        }

        this.action = json.action;
        this.direction = json.direction;

        //Update Location
        if (!this.isAt(json.x, json.y)) {
            //Location changed
            if (this.action == Action.WALKING) {
                //Walking..
                this.tileX = json.x;
                this.tileY = json.y;

                this.walk();

            }
        }

        //Update Direction
        this.updateDirection();
    }

    public createUsername() {
        //No-op
    }

}


class BiomassBlob extends GameObject {

    onTileHover() {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({isoZ: 45}, 200, Phaser.Easing.Quadratic.InOut, true);
        this.tint = config.biomassHoverTint;
        mar.game.add.tween(this.scale).to({x: 1.2, y: 1.2}, 200, Phaser.Easing.Linear.None, true);

        this.text.visible = true;
    }

    onTileExit() {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({isoZ: 15}, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({x: 1, y: 1}, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.biomassTint;

        this.text.visible = false;
    }

    updateObject(json) {
        if (DEBUG) {
            console.log("Updating Biomass object")
        }
    }

    constructor(json) {
        super(Util.getIsoX(json.x), Util.getIsoY(json.y), 10, "sheet", 1);

        if (DEBUG) {
            console.log("Creating Biomass object")
        }

        this.anchor.set(0.5, 0);
        this.id = json.i;
        this.tileX = json.x;
        this.tileY = json.y;

        this.tint = config.biomassTint;

        this.animations.add("idle", mar.animationFrames.biomassIdle);
        this.animations.play("idle", 45, true);

        this.setText("Biomass");
        this.text.visible = false;

    }
}

class Factory extends GameObject {

    public onTileHover() {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({isoZ: 25}, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.game.add.tween(this.scale).to({x: 1.06, y: 1.06}, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotHoverTint;

        this.text.visible = true;
    }

    public onTileExit() {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({isoZ: 15}, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({x: 1, y: 1}, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotTint;

        this.text.visible = false;
    }

    public updateObject(json) {
        //No op
    }

    public isAt(x: number, y: number) {
        //Factory is 2x2
        return (this.tileX === x || this.tileX + 1 === x) && (this.tileY + 1 === y || this.tileY === y);
    };

    constructor(json) {
        super(Util.getIsoX(json.x), Util.getIsoY(json.y), 15, "sheet", "objects/factory");

        this.anchor.set(0.5, .25);
        this.setText("Factory");
        this.text.visible = false;

        this.id = json.i;
        this.tileX = json.x;
        this.tileY = json.y;
    }
}

class RadioTower extends GameObject {


    public onTileHover() {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({isoZ: 25}, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.game.add.tween(this.scale).to({x: 1.06, y: 1.06}, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotHoverTint;

        this.text.visible = true;
    }

    public onTileExit() {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({isoZ: 15}, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({x: 1, y: 1}, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotTint;

        this.text.visible = false;
    }

    public updateObject(json) {
        //No op
    }

    constructor(json) {
        super(Util.getIsoX(json.x), Util.getIsoY(json.y), 15, "sheet", "objects/RadioTower");

        this.anchor.set(0.48, 0.65);
        this.setText("Radio Tower");
        this.text.visible = false;

        this.id = json.i;
        this.tileX = json.x;
        this.tileY = json.y;
    }
}

class VaultDoor extends GameObject {

    public onTileHover() {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({isoZ: 15}, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.game.add.tween(this.scale).to({x: 1.06, y: 1.06}, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotHoverTint;

        this.text.visible = true;

        document.body.style.cursor = 'pointer';
        document.body.setAttribute("title", "Click to visit Vault")
    }

    public onTileExit() {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({isoZ: 0}, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({x: 1, y: 1}, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotTint;

        this.text.visible = false;
        document.body.style.cursor = 'default';
        document.body.setAttribute("title", "")
    }

    public updateObject(json) {
        //No op
    }

    constructor(json) {
        super(Util.getIsoX(json.x), Util.getIsoY(json.y), 0, "sheet", "objects/VaultDoor1");
        this.anchor.set(0.55, 0.55);

        this.inputEnabled = true;
        this.events.onInputDown.add(function (self: VaultDoor) {
            Debug.goToHex("7FFF", "7FFF", "v" + self.id + "-");
            document.body.style.cursor = 'default';
            document.body.setAttribute("title", "")
        }, this);


        this.setText("Vault");
        this.text.visible = false;

        this.id = json.i;
        this.tileX = json.x;
        this.tileY = json.y;

        //Vault door screen animation
        let screen = mar.game.make.sprite(-76, 4, "sheet", "objects/VaultDoorScreen/1");
        screen.animations.add("idle", mar.animationFrames.vaultDoorScreen);
        screen.animations.play("idle", 11, true);
        this.addChild(screen);
    }
}

class ElectricBox extends GameObject {

    private sparkEmitter: Phaser.Particles.Arcade.Emitter;

    public onTileHover() {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({isoZ: 25}, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.game.add.tween(this.scale).to({x: 1.06, y: 1.06}, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotHoverTint;

        this.text.visible = true;
    }

    public onTileExit() {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({isoZ: 15}, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({x: 1, y: 1}, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotTint;

        this.text.visible = false;
    }

    public makeSparks(self: ElectricBox) {
        self.sparkEmitter.start(true, 450, null, 10);
        window.setTimeout(self.makeSparks, mar.game.rnd.between(5000, 25000), self)
    }

    public updateObject(json) {
        //No op
    }

    constructor(json) {
        super(Util.getIsoX(json.x), Util.getIsoY(json.y), 15, "sheet", "objects/ElectricBox");
        this.anchor.set(0.5, 0.3);

        this.setText("Electric Box");
        this.text.visible = false;

        this.id = json.i;
        this.tileX = json.x;
        this.tileY = json.y;

        //Spark particles
        this.sparkEmitter = mar.game.make.emitter(0, 0, 10);
        this.addChild(this.sparkEmitter);

        this.sparkEmitter.makeParticles("sheet", ["effects/spark"], 10);

        this.sparkEmitter.minParticleSpeed.setTo(-250, -200);
        this.sparkEmitter.maxParticleSpeed.setTo(250, 0);
        this.sparkEmitter.gravity = new Phaser.Point(0, 500);

        window.setTimeout(this.makeSparks, mar.game.rnd.between(5000, 25000), this)
    }
}


class Portal extends GameObject {
    public onTileHover() {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({isoZ: 25}, 200, Phaser.Easing.Quadratic.InOut, true);
        mar.game.add.tween(this.scale).to({x: 1.06, y: 1.06}, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.cubotHoverTint;

        this.text.visible = true;
    }

    public onTileExit() {
        mar.game.tweens.removeFrom(this);
        mar.game.add.tween(this).to({isoZ: 15}, 400, Phaser.Easing.Bounce.Out, true);
        mar.game.add.tween(this.scale).to({x: 1, y: 1}, 200, Phaser.Easing.Linear.None, true);
        this.tint = config.portalTint;

        this.text.visible = false;
    }

    public updateObject(json) {
        //No op
    }

    constructor(json) {
        super(Util.getIsoX(json.x), Util.getIsoY(json.y), 15, "sheet", "objects/Portal");
        this.anchor.set(0.5, 0.3);
        this.tint = config.portalTint;

        this.setText("Portal");
        this.text.visible = false;

        this.id = json.i;
        this.tileX = json.x;
        this.tileY = json.y;
    }
}


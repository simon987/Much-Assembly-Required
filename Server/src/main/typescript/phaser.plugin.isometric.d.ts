//debug octreee is missing - Octree 
//debug isoSprite is missing - IsoSprite
//gameobjectcreator isoSprite missing - IsoSprite
//gameobjectfactory isoSprite missing - IsoSprite
//debug body missing - Body
//debug bodyInfo missing -Body
declare module Phaser {
    interface Physics {
        isoArcade: Phaser.Plugin.Isometric.Arcade;
    }
}

declare module Phaser.Plugin {

    class Isometric extends Phaser.Plugin {

        static CLASSIC: number;
        static ISOMETRIC: number;
        static MILITARY: number;

        static VERSION: string;
        static UP: number;
        static DOWN: number;
        static FORWARDX: number;
        static FORWARDY: number;
        static BACKWARDX: number;
        static BACKWARDY: number;

        static ISOSPRITE: number;
        static ISOARCADE: number;

        projector: Phaser.Plugin.Isometric.Projector;

        constructor(game: Phaser.Game, parent?: any);

        addIsoSprite(x: number, y: number, z: number, key?: any, frame?: any, group?: Phaser.Group): Phaser.Plugin.Isometric.IsoSprite;

    }

    module Isometric {

        class Projector {

            game: Phaser.Game;
            anchor: Phaser.Point;
            projectionAngle: number;

            project(point3: Phaser.Plugin.Isometric.Point3, out?: Phaser.Point): Phaser.Point;

            projectXY(point3: Phaser.Plugin.Isometric.Point3, out?: Phaser.Point): Phaser.Point;

            unproject(point: Phaser.Point, out?: Phaser.Plugin.Isometric.Point3, z?: number): Phaser.Plugin.Isometric.Point3;

            simpleSort(group: Phaser.Group): void;

            topologicalSort(group: Phaser.Group, padding?: number, prop?: string): void;

        }

        class Point3 {

            static add(a: Phaser.Plugin.Isometric.Point3, b: Phaser.Plugin.Isometric.Point3, out?: Phaser.Plugin.Isometric.Point3): Phaser.Plugin.Isometric.Point3;

            static subtract(a: Phaser.Plugin.Isometric.Point3, b: Phaser.Plugin.Isometric.Point3, out?: Phaser.Plugin.Isometric.Point3): Phaser.Plugin.Isometric.Point3;

            static multiply(a: Phaser.Plugin.Isometric.Point3, b: Phaser.Plugin.Isometric.Point3, out?: Phaser.Plugin.Isometric.Point3): Phaser.Plugin.Isometric.Point3;

            static divide(a: Phaser.Plugin.Isometric.Point3, b: Phaser.Plugin.Isometric.Point3, out?: Phaser.Plugin.Isometric.Point3): Phaser.Plugin.Isometric.Point3;

            static equals(a: Phaser.Plugin.Isometric.Point3, b: Phaser.Plugin.Isometric.Point3): boolean;

            x: number;
            y: number;
            z: number;

            constructor(x?: number, y?: number, z?: number);

            copyFrom(source: any): Phaser.Plugin.Isometric.Point3;

            copyto(dest: any): any;

            equals(a: any): boolean;

            set(x?: number, y?: number, z?: number): Phaser.Plugin.Isometric.Point3;

            setTo(x?: number, y?: number, z?: number): Phaser.Plugin.Isometric.Point3;

            add(x?: number, y?: number): Phaser.Plugin.Isometric.Point3;

            subtract(x?: number, y?: number, z?: number): Phaser.Plugin.Isometric.Point3;

            multiply(x?: number, y?: number, z?: number): Phaser.Plugin.Isometric.Point3;

            divide(x?: number, y?: number, z?: number): Phaser.Plugin.Isometric.Point3;

            containsXY(x?: number, y?: number);

        }

        class Octree {

            maxObjects: number;
            maxLevels: number;
            level: number;
            bounds: any;
            objects: any[];
            nodes: any[];

            constructor(x: number, y: number, z: number, widthX: number, widthY: number, height: number, maxObject?: number, maxLevels?: number, level?: number);

            reset(x: number, y: number, z: number, widthX: number, widthY: number, height: number, maxObject?: number, maxLevels?: number, level?: number): void;

            populate(group: Phaser.Group): void;

            populateHandler(sprite: Phaser.Plugin.Isometric.IsoSprite): void;
            populateHandler(sprite: any): void;

            split(): void;

            insert(body: Phaser.Plugin.Isometric.Body): void;
            insert(body: Phaser.Plugin.Isometric.Cube): void;
            insert(body: any): void;

            getIndex(cube: Phaser.Plugin.Isometric.Cube): number;
            getIndex(cube: any): number;

            retrieve(source: Phaser.Plugin.Isometric.IsoSprite): any[];
            retrieve(source: Phaser.Plugin.Isometric.Cube): any[];

            clear(): void;

        }

        class IsoSprite extends Phaser.Sprite {

            snap: number;
            isoX: number;
            isoY: number;
            isoZ: number;
            isoPosition: Phaser.Plugin.Isometric.Point3;
            isoBounds: Phaser.Plugin.Isometric.Point3;
            depth: number;

            constructor(game: Phaser.Game, x: number, y: number, z: number, key?: any, frame?: any);

            resetIsoBounds(): void;

        }

        class Cube {

            static size(a: Phaser.Plugin.Isometric.Cube, output?: Phaser.Plugin.Isometric.Point3): Phaser.Plugin.Isometric.Point3;

            static clone(a: Phaser.Plugin.Isometric.Cube, output?: Phaser.Plugin.Isometric.Cube): Phaser.Plugin.Isometric.Cube;

            static contains(a: Phaser.Plugin.Isometric.Cube, x: number, y: number, z: number): boolean;

            static containsXY(a: Phaser.Plugin.Isometric.Cube, x: number, y: number): boolean;

            static containsPoint3(a: Phaser.Plugin.Isometric.Cube, point3: Phaser.Plugin.Isometric.Point3): boolean;

            static containsCube(a: Phaser.Plugin.Isometric.Cube, b: Phaser.Plugin.Isometric.Cube): boolean;

            static intersects(a: Phaser.Plugin.Isometric.Cube, b: Phaser.Plugin.Isometric.Cube): boolean;

            x: number;
            y: number;
            z: number;
            widthX: number;
            widthY: number;
            height: number;
            halfWidthX: number;
            halfWidthY: number;
            halfHeight: number;
            bottom: number;
            top: number;
            backX: number;
            backY: number;
            frontX: number;
            frontY: number;
            volume: number;
            centerX: number;
            centerY: number;
            centerZ: number;
            randomX: number;
            randomY: number;
            randomZ: number;
            empty: boolean;

            constructor(x?: number, y?: number, z?: number, widthX?: number, widthY?: number, height?: number);

            setTo(x?: number, y?: number, z?: number, widthX?: number, widthY?: number, height?: number): Phaser.Plugin.Isometric.Cube;

            copyFrom(source: any): Phaser.Plugin.Isometric.Cube;

            copyTo(dest: any): Phaser.Plugin.Isometric.Cube;

            size(output?: Phaser.Plugin.Isometric.Point3): Phaser.Plugin.Isometric.Point3;

            contains(x: number, y: number, z: number): boolean;

            containsXY(x: number, y: number): boolean;

            clone(output?: Phaser.Plugin.Isometric.Cube): Phaser.Plugin.Isometric.Cube;

            intersects(b: Phaser.Plugin.Isometric.Cube): boolean;

            getCorners(): Phaser.Plugin.Isometric.Point3[];

            toString(): string;

        }

        class Body {

            static render(context: any, body: Phaser.Plugin.Isometric.Body, color?: string, filled?: boolean): void;

            static renderBodyInfo(debug: any, body: Phaser.Plugin.Isometric.Body): void; //togo debug?

            sprite: Phaser.Plugin.Isometric.IsoSprite;
            game: Phaser.Game;
            type: number;
            enable: boolean;
            offset: Phaser.Plugin.Isometric.Point3;
            position: Phaser.Plugin.Isometric.Point3;
            prev: Phaser.Plugin.Isometric.Point3;
            allowRotation: boolean;
            rotation: number;
            preRotation: number;
            sourceWidthX: number;
            sourceWidthY: number;
            sourceHeight: number;
            widthX: number;
            widthY: number;
            height: number;
            halfWidthX: number;
            halfWidthY: number;
            halfHeight: number;
            center: Phaser.Plugin.Isometric.Point3;
            velocity: Phaser.Plugin.Isometric.Point3;
            newVelocity: Phaser.Plugin.Isometric.Point3;
            deltaMax: Phaser.Plugin.Isometric.Point3;
            acceleration: Phaser.Plugin.Isometric.Point3;
            drag: Phaser.Plugin.Isometric.Point3;
            allowGravity: boolean;
            gravity: Phaser.Plugin.Isometric.Point3;
            bounce: Phaser.Plugin.Isometric.Point3;
            maxVelocity: Phaser.Plugin.Isometric.Point3;
            angularVelocity: number;
            angularAcceleration: number;
            angularDrag: number;
            maxAngular: number;
            mass: number;
            angle: number;
            speed: number;
            facing: number;
            immovable: boolean;
            moves: boolean;
            customSeparateX: boolean;
            customSeparateY: boolean;
            customSeparateZ: boolean;
            overlapX: number;
            overlapY: number;
            overlayZ: number;
            embedded: boolean;
            collideWorldBounds: boolean;
            checkCollision: {
                none: boolean;
                any: boolean;
                up: boolean;
                down: boolean;
                frontX: number;
                frontY: number;
                backX: number;
                backY: number;
            };
            touching: {
                none: boolean;
                up: boolean;
                down: boolean;
                frontX: number;
                frontY: number;
                backX: number;
                backY: number;
            };
            wasTouching: {
                none: boolean;
                up: boolean;
                down: boolean;
                frontX: number;
                frontY: number;
                backX: number;
                backY: number;
            };
            blocked: {
                up: boolean;
                down: boolean;
                frontX: number;
                frontY: number;
                backX: number;
                backY: number;
            };
            phase: number;
            skipTree: boolean;
            top: number;
            frontX: number;
            right: number;
            frontY: number;
            bottom: number;
            x: number;
            y: number;
            z: number;

            constructor(sprite: Phaser.Plugin.Isometric.IsoSprite);

            destroy(): void;

            setSize(widthX: number, widthY: number, height: number, offsetX?: number, offsetY?: number, offsetZ?: number): void;

            reset(x: number, y: number, z: number): void;

            hitText(x: number, y: number, z: number): boolean;

            onFloor(): boolean;

            onWall(): boolean;

            deltaAbsX(): number;

            deltaAbsY(): number;

            deltaAbsZ(): number;

            deltaX(): number;

            deltaY(): number;

            deltaZ(): number;

            deltaR(): number;

            getCorners(): Phaser.Plugin.Isometric.Point3[];

        }

        class Arcade {

            game: Phaser.Game;
            gravity: Phaser.Plugin.Isometric.Point3;
            bounds: Phaser.Plugin.Isometric.Cube;
            checkCollision: {
                up: boolean;
                down: boolean;
                frontX: boolean;
                frontY: boolean;
                backX: boolean;
                backY: boolean;
            };
            maxObjects: number;
            maxLevels: number;
            OVERLAP_BIAS: number;
            forceXY: boolean;
            skipTree: boolean;
            useQuadTree: boolean;
            quadTree: Phaser.QuadTree;
            octree: Phaser.Plugin.Isometric.Octree;

            constructor(game: Phaser.Game);

            setBounds(x: number, y: number, z: number, widthX: number, widthY: number, height: number): void;

            setBoundsToWorld(): void;

            enable(object: any, children?: boolean): void;

            enableBody(object: any): void;

            updateMotion(body: Phaser.Plugin.Isometric.Body): void;

            computeVelocity(axis: number, body: Phaser.Plugin.Isometric.Body, velocity: number, acceleration: number, drag: number, max?: number): number;

            overlap(object1: any, object2: any, overlapCallback?: Function, processCallback?: Function, callbackContext?: any): boolean;

            collide(object1: any, object2: any, overlapCallback?: Function, processCallback?: Function, callbackContext?: any): boolean;

            intersects(body1: Phaser.Plugin.Isometric.Body): boolean;

            distanceBetween(source: any, target: any): number;

            distanceToXY(displayObject: any, x: number, y: number): number;

            distanceToXYZ(displayObject: any, x: number, y: number, z: number): number;

        }

    }

}

/**
 * The MIT License (MIT)

 * Copyright (c) 2015 Lewis Lane

 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 *
 *
 */

/**
 * @author       Lewis Lane <lew@rotates.org>
 * @copyright    2015 Lewis Lane (Rotates.org)
 * @license      {@link http://opensource.org/licenses/MIT|MIT License}
 */

/**
 * @class Phaser.Plugin.Isometric
 *
 * @classdesc
 * Isometric is a comprehensive axonometric plugin for Phaser which provides an API for handling axonometric projection of assets in 3D space to the screen.
 * The goal has been to mimic as closely as possible the existing APIs provided by Phaser for standard orthogonal 2D projection, but add a third dimension.
 * Also included is an Arcade-based 3D AABB physics engine, which again is closely equivalent in functionality and its API.
 *
 * @constructor
 * @param {Phaser.Game} game The current game instance.
 * @param {number} isometricType - the isometric projection angle to use.
 */
Phaser.Plugin.Isometric = function (game, parent, isometricType) {

    isometricType = isometricType || Phaser.Plugin.Isometric.CLASSIC;

    Phaser.Plugin.call(this, game, parent);
    this.projector = new Phaser.Plugin.Isometric.Projector(this.game, isometricType);
    //  Add an instance of Isometric.Projector to game.iso if it doesn't exist already
    this.game.iso = this.game.iso || this.projector;
};

Phaser.Plugin.Isometric.prototype = Object.create(Phaser.Plugin.prototype);
Phaser.Plugin.Isometric.prototype.constructor = Phaser.Plugin.Isometric;

Phaser.Plugin.Isometric.VERSION = '0.9.3';

//  Directional consts
Phaser.Plugin.Isometric.UP = 0;
Phaser.Plugin.Isometric.DOWN = 1;
Phaser.Plugin.Isometric.FORWARDX = 2;
Phaser.Plugin.Isometric.FORWARDY = 3;
Phaser.Plugin.Isometric.BACKWARDX = 4;
Phaser.Plugin.Isometric.BACKWARDY = 5;

//  Type consts
Phaser.Plugin.Isometric.ISOSPRITE = 'isosprite';
Phaser.Plugin.Isometric.ISOARCADE = 'isoarcade';
/**
 * @class Phaser.Plugin.Isometric.Cube
 *
 * @classdesc
 * Creates a new Cube object with the bottom-back corner specified by the x, y and z parameters, with the specified breadth (widthX), depth (widthY) and height parameters. If you call this function without parameters, a Cube with x, y, z, breadth, depth and height properties set to 0 is created.
 *
 * @constructor
 * @param {number} x - The x coordinate of the bottom-back corner of the Cube.
 * @param {number} y - The y coordinate of the bottom-back corner of the Cube.
 * @param {number} z - The z coordinate of the bottom-back corner of the Cube.
 * @param {number} widthX - The X axis width (breadth) of the Cube. Should always be either zero or a positive value.
 * @param {number} widthY - The Y axis width (depth) of the Cube. Should always be either zero or a positive value.
 * @param {number} height - The Z axis height of the Cube. Should always be either zero or a positive value.
 * @return {Phaser.Plugin.Isometric.Cube} This Cube object.
 */
Phaser.Plugin.Isometric.Cube = function (x, y, z, widthX, widthY, height) {

    x = x || 0;
    y = y || 0;
    z = z || 0;
    widthX = widthX || 0;
    widthY = widthY || 0;
    height = height || 0;

    /**
     * @property {number} x - The x coordinate of the bottom-back corner of the Cube.
     */
    this.x = x;

    /**
     * @property {number} y - The y coordinate of the bottom-back corner of the Cube.
     */
    this.y = y;

    /**
     * @property {number} z - The z coordinate of the bottom-back corner of the Cube.
     */
    this.z = z;

    /**
     * @property {number} widthX - The X axis width (breadth) of the Cube. This value should never be set to a negative.
     */
    this.widthX = widthX;

    /**
     * @property {number} widthY - The Y axis width (depth) of the Cube. This value should never be set to a negative.
     */
    this.widthY = widthY;

    /**
     * @property {number} height - The Z axis height of the Cube. This value should never be set to a negative.
     */
    this.height = height;

    /**
     * @property {Array.<Phaser.Plugin.Isometric.Point3>} _corners - The 8 corners of the Cube.
     * @private
     */
    this._corners = [
        new Phaser.Plugin.Isometric.Point3(this.x, this.y, this.z),
        new Phaser.Plugin.Isometric.Point3(this.x, this.y, this.z + this.height),
        new Phaser.Plugin.Isometric.Point3(this.x, this.y + this.widthY, this.z),
        new Phaser.Plugin.Isometric.Point3(this.x, this.y + this.widthY, this.z + this.height),
        new Phaser.Plugin.Isometric.Point3(this.x + this.widthX, this.y, this.z),
        new Phaser.Plugin.Isometric.Point3(this.x + this.widthX, this.y, this.z + this.height),
        new Phaser.Plugin.Isometric.Point3(this.x + this.widthX, this.y + this.widthY, this.z),
        new Phaser.Plugin.Isometric.Point3(this.x + this.widthX, this.y + this.widthY, this.z + this.height)
    ];
};

Phaser.Plugin.Isometric.Cube.prototype.constructor = Phaser.Plugin.Isometric.Cube;

Phaser.Plugin.Isometric.Cube.prototype = {
    /**
     * Sets the members of Cube to the specified values.
     * @method Phaser.Plugin.Isometric.Cube#setTo
     * @param {number} x - The x coordinate of the bottom-back corner of the Cube.
     * @param {number} y - The y coordinate of the bottom-back corner of the Cube.
     * @param {number} z - The z coordinate of the bottom-back corner of the Cube.
     * @param {number} widthX - The X axis width (breadth) of the Cube. This value should never be set to a negative.
     * @param {number} widthY - The Y axis width (depth) of the Cube. This value should never be set to a negative.
     * @param {number} height - The Z axis height of the Cube. This value should never be set to a negative.
     * @return {Phaser.Plugin.Isometric.Cube} This Cube object
     */
    setTo: function (x, y, z, widthX, widthY, height) {

        this.x = x;
        this.y = y;
        this.z = z;
        this.widthX = widthX;
        this.widthY = widthY;
        this.height = height;

        return this;

    },

    /**
     * Copies the x, y, z, widthX, widthY and height properties from any given object to this Cube.
     * @method Phaser.Plugin.Isometric.Cube#copyFrom
     * @param {any} source - The object to copy from.
     * @return {Phaser.Plugin.Isometric.Cube} This Cube object.
     */
    copyFrom: function (source) {

        this.setTo(source.x, source.y, source.z, source.widthX, source.widthY, source.height);

    },

    /**
     * Copies the x, y, z, widthX, widthY and height properties from this Cube to any given object.
     * @method Phaser.Plugin.Isometric.Cube#copyTo
     * @param {any} dest - The object to copy to.
     * @return {Phaser.Plugin.Isometric.Cube} This Cube object.
     */
    copyTo: function (dest) {

        dest.x = this.x;
        dest.y = this.y;
        dest.z = this.z;
        dest.widthX = this.widthX;
        dest.widthY = this.widthY;
        dest.height = this.height;

        return dest;

    },

    /**
     * The size of the Cube object, expressed as a Point3 object with the values of the widthX, widthY and height properties.
     * @method Phaser.Plugin.Isometric.Cube#size
     * @param {Phaser.Plugin.Isometric.Point3} [output] - Optional Point3 object. If given the values will be set into the object, otherwise a brand new Point3 object will be created and returned.
     * @return {Phaser.Plugin.Isometric.Point3} The size of the Cube object.
     */
    size: function (output) {

        return Phaser.Plugin.Isometric.Cube.size(this, output);

    },

    /**
     * Determines whether the specified coordinates are contained within the region defined by this Cube object.
     * @method Phaser.Plugin.Isometric.Cube#contains
     * @param {number} x - The x coordinate of the point to test.
     * @param {number} y - The y coordinate of the point to test.
     * @param {number} y - The z coordinate of the point to test.
     * @return {boolean} A value of true if the Cube object contains the specified point; otherwise false.
     */
    contains: function (x, y, z) {

        return Phaser.Plugin.Isometric.Cube.contains(this, x, y, z);

    },

    /**
     * Determines whether the specified X and Y coordinates are contained within the region defined by this Cube object.
     * @method Phaser.Plugin.Isometric.Cube#containsXY
     * @param {number} x - The x coordinate of the point to test.
     * @param {number} y - The y coordinate of the point to test.
     * @return {boolean} A value of true if this Cube object contains the specified point; otherwise false.
     */
    containsXY: function (x, y) {

        return Phaser.Plugin.Isometric.Cube.containsXY(this, x, y);

    },

    /**
     * Returns a new Cube object with the same values for the x, y, z, widthX, widthY and height properties as the original Cube object.
     * @method Phaser.Plugin.Isometric.Cube#clone
     * @param {Phaser.Plugin.Isometric.Cube} [output] - Optional Cube object. If given the values will be set into the object, otherwise a brand new Cube object will be created and returned.
     * @return {Phaser.Plugin.Isometric.Cube}
     */
    clone: function (output) {

        return Phaser.Plugin.Isometric.Cube.clone(this, output);

    },

    /**
     * Determines whether the two Cubes intersect with each other.
     * This method checks the x, y, z, widthX, widthY, and height properties of the Cubes.
     * @method Phaser.Plugin.Isometric.Cube#intersects
     * @param {Phaser.Plugin.Isometric.Cube} b - The second Cube object.
     * @return {boolean} A value of true if the specified object intersects with this Cube object; otherwise false.
     */
    intersects: function (b) {

        return Phaser.Plugin.Isometric.Cube.intersects(this, b);

    },

    /**
     * Updates and returns an Array of eight Point3 objects containing the corners of this Cube.
     * @method Phaser.Plugin.Isometric.Cube#getCorners
     * @return {Array.<Phaser.Plugin.Isometric.Point3>} The corners of this Cube expressed as an Array of eight Point3 objects.
     */
    getCorners: function () {

        this._corners[0].setTo(this.x, this.y, this.z);
        this._corners[1].setTo(this.x, this.y, this.z + this.height);
        this._corners[2].setTo(this.x, this.y + this.widthY, this.z);
        this._corners[3].setTo(this.x, this.y + this.widthY, this.z + this.height);
        this._corners[4].setTo(this.x + this.widthX, this.y, this.z);
        this._corners[5].setTo(this.x + this.widthX, this.y, this.z + this.height);
        this._corners[6].setTo(this.x + this.widthX, this.y + this.widthY, this.z);
        this._corners[7].setTo(this.x + this.widthX, this.y + this.widthY, this.z + this.height);

        return this._corners;

    },

    /**
     * Returns a string representation of this object.
     * @method Phaser.Plugin.Isometric.Cube#toString
     * @return {string} A string representation of the instance.
     */
    toString: function () {

        return "[{Cube (x=" + this.x + " y=" + this.y + " z=" + this.z + " widthX=" + this.widthX + " widthY=" + this.widthY + " height=" + this.height + " empty=" + this.empty + ")}]";

    }
};

/**
 * @name Phaser.Plugin.Isometric.Cube#halfWidthX
 * @property {number} halfWidthX - Half of the widthX of the Cube.
 * @readonly
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "halfWidthX", {

    get: function () {
        return Math.round(this.widthX * 0.5);
    }

});

/**
 * @name Phaser.Plugin.Isometric.Cube#halfWidthY
 * @property {number} halfWidthY - Half of the widthY of the Cube.
 * @readonly
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "halfWidthY", {

    get: function () {
        return Math.round(this.widthY * 0.5);
    }

});

/**
 * @name Phaser.Plugin.Isometric.Cube#halfHeight
 * @property {number} halfHeight - Half of the height of the Cube.
 * @readonly
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "halfHeight", {

    get: function () {
        return Math.round(this.height * 0.5);
    }

});

/**
 * The z coordinate of the bottom of the Cube. Changing the bottom property of a Cube object has no effect on the x, y, widthX and widthY properties.
 * However it does affect the height property, whereas changing the z value does not affect the height property.
 * @name Phaser.Plugin.Isometric.Cube#bottom
 * @property {number} bottom - The z coordinate of the bottom of the Cube.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "bottom", {

    get: function () {
        return this.z;
    },

    set: function (value) {
        if (value >= this.top) {
            this.height = 0;
        } else {
            this.height = (this.top - value);
        }
        this.z = value;
    }

});

/**
 * The sum of the z and height properties. Changing the top property of a Cube object has no effect on the x, y, z, widthX and widthY properties, but does change the height property.
 * @name Phaser.Plugin.Isometric.Cube#top
 * @property {number} top - The sum of the z and height properties.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "top", {

    get: function () {
        return this.z + this.height;
    },

    set: function (value) {
        if (value <= this.z) {
            this.height = 0;
        } else {
            this.height = (value - this.z);
        }
    }

});

/**
 * The x coordinate of the back of the Cube. Changing the backX property of a Cube object has no effect on the y, z, widthY and height properties. However it does affect the widthX property, whereas changing the x value does not affect the width property.
 * @name Phaser.Plugin.Isometric.Cube#backX
 * @property {number} backX - The x coordinate of the left of the Cube.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "backX", {

    get: function () {
        return this.x;
    },

    set: function (value) {
        if (value >= this.frontX) {
            this.widthX = 0;
        } else {
            this.widthX = (this.frontX - value);
        }
        this.x = value;
    }

});

/**
 * The y coordinate of the back of the Cube. Changing the backY property of a Cube object has no effect on the x, z, widthX and height properties. However it does affect the widthY property, whereas changing the y value does not affect the width property.
 * @name Phaser.Plugin.Isometric.Cube#backY
 * @property {number} backY - The x coordinate of the left of the Cube.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "backY", {

    get: function () {
        return this.y;
    },

    set: function (value) {
        if (value >= this.frontY) {
            this.widthY = 0;
        } else {
            this.widthY = (this.frontY - value);
        }
        this.y = value;
    }

});

/**
 * The sum of the x and widthX properties. Changing the frontX property of a Cube object has no effect on the x, y, z, widthY and height properties, however it does affect the widthX property.
 * @name Phaser.Plugin.Isometric.Cube#frontX
 * @property {number} frontX - The sum of the x and widthX properties.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "frontX", {

    get: function () {
        return this.x + this.widthX;
    },

    set: function (value) {
        if (value <= this.x) {
            this.widthX = 0;
        } else {
            this.widthX = (value - this.x);
        }
    }

});

/**
 * The sum of the y and widthY properties. Changing the frontY property of a Cube object has no effect on the x, y, z, widthX and height properties, however it does affect the widthY property.
 * @name Phaser.Plugin.Isometric.Cube#frontY
 * @property {number} frontY - The sum of the y and widthY properties.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "frontY", {

    get: function () {
        return this.y + this.widthY;
    },

    set: function (value) {
        if (value <= this.y) {
            this.widthY = 0;
        } else {
            this.widthY = (value - this.y);
        }
    }

});

/**
 * The volume of the Cube derived from widthX * widthY * height.
 * @name Phaser.Plugin.Isometric.Cube#volume
 * @property {number} volume - The volume of the Cube derived from widthX * widthY * height.
 * @readonly
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "volume", {

    get: function () {
        return this.widthX * this.widthY * this.height;
    }

});

/**
 * The x coordinate of the center of the Cube.
 * @name Phaser.Plugin.Isometric.Cube#centerX
 * @property {number} centerX - The x coordinate of the center of the Cube.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "centerX", {
    get: function () {
        return this.x + this.halfWidthX;
    },

    set: function (value) {
        this.x = value - this.halfWidthX;
    }
});

/**
 * The y coordinate of the center of the Cube.
 * @name Phaser.Plugin.Isometric.Cube#centerY
 * @property {number} centerY - The y coordinate of the center of the Cube.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "centerY", {
    get: function () {
        return this.y + this.halfWidthY;
    },

    set: function (value) {
        this.y = value - this.halfWidthY;
    }
});

/**
 * The z coordinate of the center of the Cube.
 * @name Phaser.Plugin.Isometric.Cube#centerZ
 * @property {number} centerZ - The z coordinate of the center of the Cube.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "centerZ", {
    get: function () {
        return this.z + this.halfHeight;
    },

    set: function (value) {
        this.z = value - this.halfHeight;
    }
});

/**
 * A random value between the frontX and backX values (inclusive) of the Cube.
 *
 * @name Phaser.Plugin.Isometric.Cube#randomX
 * @property {number} randomX - A random value between the frontX and backX values (inclusive) of the Cube.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "randomX", {

    get: function () {

        return this.x + (Math.random() * this.widthX);

    }

});

/**
 * A random value between the frontY and backY values (inclusive) of the Cube.
 *
 * @name Phaser.Plugin.Isometric.Cube#randomY
 * @property {number} randomY - A random value between the frontY and backY values (inclusive) of the Cube.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "randomY", {

    get: function () {

        return this.y + (Math.random() * this.widthY);

    }

});

/**
 * A random value between the bottom and top values (inclusive) of the Cube.
 *
 * @name Phaser.Plugin.Isometric.Cube#randomZ
 * @property {number} randomZ - A random value between the bottom and top values (inclusive) of the Cube.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "randomZ", {

    get: function () {

        return this.z + (Math.random() * this.height);

    }

});

/**
 * Determines whether or not this Cube object is empty. A Cube object is empty if its widthX, widthY or height is less than or equal to 0.
 * If set to true then all of the Cube properties are set to 0.
 * @name Phaser.Plugin.Isometric.Cube#empty
 * @property {boolean} empty - Gets or sets the Cube's empty state.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Cube.prototype, "empty", {

    get: function () {
        return (!this.widthX || !this.widthY || !this.height);
    },

    set: function (value) {

        if (value === true) {
            this.setTo(0, 0, 0, 0, 0, 0);
        }

    }

});

/**
 * The size of the Cube object, expressed as a Point3 object with the values of the widthX, widthY and height properties.
 * @method Phaser.Plugin.Isometric.Cube.size
 * @param {Phaser.Plugin.Isometric.Cube} a - The Cube object.
 * @param {Phaser.Plugin.Isometric.Point3} [output] - Optional Point3 object. If given the values will be set into the object, otherwise a brand new Point3 object will be created and returned.
 * @return {Phaser.Plugin.Isometric.Point3} The size of the Cube object
 */
Phaser.Plugin.Isometric.Cube.size = function (a, output) {
    if (typeof output === "undefined" || output === null) {
        output = new Phaser.Plugin.Isometric.Point3(a.widthX, a.widthY, a.height);
    } else {
        output.setTo(a.widthX, a.widthY, a.height);
    }

    return output;
};

/**
 * Returns a new Cube object with the same values for the x, y, z, widthX, widthY, and height properties as the original Cube object.
 * @method Phaser.Plugin.Isometric.Cube.clone
 * @param {Phaser.Plugin.Isometric.Cube} a - The Cube object.
 * @param {Phaser.Plugin.Isometric.Cube} [output] - Optional Cube object. If given the values will be set into the object, otherwise a brand new Cube object will be created and returned.
 * @return {Phaser.Plugin.Isometric.Cube}
 */
Phaser.Plugin.Isometric.Cube.clone = function (a, output) {
    if (typeof output === "undefined" || output === null) {
        output = new Phaser.Plugin.Isometric.Cube(a.x, a.y, a.z, a.widthX, a.widthY, a.height);
    } else {
        output.setTo(a.x, a.y, a.z, a.widthX, a.widthY, a.height);
    }

    return output;
};

/**
 * Determines whether the specified coordinates are contained within the region defined by this Cube object.
 * @method Phaser.Plugin.Isometric.Cube.contains
 * @param {Phaser.Plugin.Isometric.Cube} a - The Cube object.
 * @param {number} x - The x coordinate of the point to test.
 * @param {number} y - The y coordinate of the point to test.
 * @param {number} z - The z coordinate of the point to test.
 * @return {boolean} A value of true if the Cube object contains the specified point; otherwise false.
 */
Phaser.Plugin.Isometric.Cube.contains = function (a, x, y, z) {
    if (a.widthX <= 0 || a.widthY <= 0 || a.height <= 0) {
        return false;
    }

    return (x >= a.x && x <= a.frontX && y >= a.y && y <= a.frontY && z >= a.z && z <= a.top);
};

/**
 * Determines whether the specified X and Y coordinates are contained within the region defined by this Cube object.
 * @method Phaser.Plugin.Isometric.Cube.containsXY
 * @param {Phaser.Plugin.Isometric.Cube} a - The Cube object.
 * @param {number} x - The x coordinate of the point to test.
 * @param {number} y - The y coordinate of the point to test.
 * @return {boolean} A value of true if the Cube object contains the specified point; otherwise false.
 */
Phaser.Plugin.Isometric.Cube.containsXY = function (a, x, y) {
    if (a.widthX <= 0 || a.widthY <= 0) {
        return false;
    }

    return (x >= a.x && x <= a.frontX && y >= a.y && y <= a.frontY);
};

/**
 * Determines whether the specified point is contained within the cubic region defined by this Cube object. This method is similar to the Cube.contains() method, except that it takes a Point3 object as a parameter.
 * @method Phaser.Plugin.Isometric.Cube.containsPoint3
 * @param {Phaser.Plugin.Isometric.Cube} a - The Cube object.
 * @param {Phaser.Plugin.Isometric.Point3} point3 - The Point3 object being checked. Can be Point3 or any object with .x, .y and .z values.
 * @return {boolean} A value of true if the Cube object contains the specified point; otherwise false.
 */
Phaser.Plugin.Isometric.Cube.containsPoint3 = function (a, point3) {
    return Phaser.Plugin.Isometric.Cube.contains(a, point3.x, point3.y, point3.z);
};

/**
 * Determines whether the first Cube object is fully contained within the second Cube object.
 * A Cube object is said to contain another if the second Cube object falls entirely within the boundaries of the first.
 * @method Phaser.Plugin.Isometric.Cube.containsCube
 * @param {Phaser.Plugin.Isometric.Cube} a - The first Cube object.
 * @param {Phaser.Plugin.Isometric.Cube} b - The second Cube object.
 * @return {boolean} A value of true if the Cube object contains the specified point; otherwise false.
 */
Phaser.Plugin.Isometric.Cube.containsCube = function (a, b) {

    //  If the given cube has a larger volume than this one then it can never contain it
    if (a.volume > b.volume) {
        return false;
    }

    return (a.x >= b.x && a.y >= b.y && a.z >= b.z && a.frontX <= b.frontX && a.frontY <= b.frontY && a.top <= b.top);

};

/**
 * Determines whether the two Cubes intersect with each other.
 * This method checks the x, y, z, widthX, widthY, and height properties of the Cubes.
 * @method Phaser.Plugin.Isometric.Cube.intersects
 * @param {Phaser.Plugin.Isometric.Cube} a - The first Cube object.
 * @param {Phaser.Plugin.Isometric.Cube} b - The second Cube object.
 * @return {boolean} A value of true if the specified object intersects with this Cube object; otherwise false.
 */
Phaser.Plugin.Isometric.Cube.intersects = function (a, b) {
    if (a.widthX <= 0 || a.widthY <= 0 || a.height <= 0 || b.widthX <= 0 || b.widthY <= 0 || b.height <= 0) {
        return false;
    }
    return !(a.frontX < b.x || a.frontY < b.y || a.x > b.frontX || a.y > b.frontY || a.z > b.top || a.top < b.z);
};
;/**
 * @class Phaser.Plugin.Isometric.IsoSprite
 *
 * @classdesc
 * Create a new `IsoSprite` object. IsoSprites are extended versions of standard Sprites that are suitable for axonometric positioning.
 *
 * IsoSprites are simply Sprites that have three new position properties (isoX, isoY and isoZ) and ask the instance of Phaser.Plugin.Isometric.Projector what their position should be in a 2D scene whenever these properties are changed.
 * The IsoSprites retain their 2D position property to prevent any problems and allow you to interact with them as you would a normal Sprite. The upside of this simplicity is that things should behave predictably for those already used to Phaser.
 *
 * @constructor
 * @extends Phaser.Sprite
 * @param {Phaser.Game} game - A reference to the currently running game.
 * @param {number} x - The x coordinate (in 3D space) to position the IsoSprite at.
 * @param {number} y - The y coordinate (in 3D space) to position the IsoSprite at.
 * @param {number} z - The z coordinate (in 3D space) to position the IsoSprite at.
 * @param {string|Phaser.RenderTexture|Phaser.BitmapData|PIXI.Texture} key - This is the image or texture used by the IsoSprite during rendering. It can be a string which is a reference to the Cache entry, or an instance of a RenderTexture or PIXI.Texture.
 * @param {string|number} frame - If this IsoSprite is using part of a sprite sheet or texture atlas you can specify the exact frame to use by giving a string or numeric index.
 */
Phaser.Plugin.Isometric.IsoSprite = function (game, x, y, z, key, frame) {

    Phaser.Sprite.call(this, game, x, y, key, frame);

    /**
     * @property {number} objType - The const objType of this object.
     * @readonly
     */
    this.type = Phaser.Plugin.Isometric.ISOSPRITE;

    /**
     * @property {Phaser.Plugin.Isometric.Point3} _isoPosition - Internal 3D position.
     * @private
     */
    this._isoPosition = new Phaser.Plugin.Isometric.Point3(x, y, z);

    /**
     * @property {number} snap - Snap this IsoSprite's position to the specified value; handy for keeping pixel art snapped to whole pixels.
     * @default
     */
    this.snap = 0;

    /**
     * @property {number} _depth - Internal cached depth value.
     * @readonly
     * @private
     */
    this._depth = 0;

    /**
     * @property {boolean} _depthChanged - Internal invalidation control for depth management.
     * @readonly
     * @private
     */
    this._depthChanged = true;

    /**
     * @property {boolean} _isoPositionChanged - Internal invalidation control for positioning.
     * @readonly
     * @private
     */
    this._isoPositionChanged = true;

    /**
     * @property {boolean} _isoBoundsChanged - Internal invalidation control for isometric bounds.
     * @readonly
     * @private
     */
    this._isoBoundsChanged = true;

    this._project();

    /**
     * @property {Phaser.Plugin.Isometric.Cube} _isoBounds - Internal derived 3D bounds.
     * @private
     */
    this._isoBounds = this.resetIsoBounds();
};

Phaser.Plugin.Isometric.IsoSprite.prototype = Object.create(Phaser.Sprite.prototype);
Phaser.Plugin.Isometric.IsoSprite.prototype.constructor = Phaser.Plugin.Isometric.IsoSprite;

/**
 * Internal function called by the World postUpdate cycle.
 *
 * @method Phaser.Plugin.Isometric.IsoSprite#postUpdate
 * @memberof Phaser.Plugin.Isometric.IsoSprite
 */
Phaser.Plugin.Isometric.IsoSprite.prototype.postUpdate = function () {
    Phaser.Sprite.prototype.postUpdate.call(this);

    this._project();
};

/**
 * Internal function that performs the axonometric projection from 3D to 2D space.
 * @method Phaser.Plugin.Isometric.IsoSprite#_project
 * @memberof Phaser.Plugin.Isometric.IsoSprite
 * @private
 */
Phaser.Plugin.Isometric.IsoSprite.prototype._project = function () {
    if (this._isoPositionChanged) {
        this.game.iso.project(this._isoPosition, this.position);

        if (this.snap > 0) {
            this.position.x = Phaser.Math.snapTo(this.position.x, this.snap);
            this.position.y = Phaser.Math.snapTo(this.position.y, this.snap);
        }

        this._depthChanged = this._isoPositionChanged = this._isoBoundsChanged = true;
    }
};

Phaser.Plugin.Isometric.IsoSprite.prototype.resetIsoBounds = function () {
    if (typeof this._isoBounds === "undefined") {
        this._isoBounds = new Phaser.Plugin.Isometric.Cube();
    }

    var asx = Math.abs(this.scale.x);
    var asy = Math.abs(this.scale.y);

    this._isoBounds.widthX = Math.round(Math.abs(this.width) * 0.5) * asx;
    this._isoBounds.widthY = Math.round(Math.abs(this.width) * 0.5) * asx;
    this._isoBounds.height = Math.round(Math.abs(this.height) - (Math.abs(this.width) * 0.5)) * asy;

    this._isoBounds.x = this.isoX + (this._isoBounds.widthX * -this.anchor.x) + this._isoBounds.widthX * 0.5;
    this._isoBounds.y = this.isoY + (this._isoBounds.widthY * this.anchor.x) - this._isoBounds.widthY * 0.5;
    this._isoBounds.z = this.isoZ - (Math.abs(this.height) * (1 - this.anchor.y)) + (Math.abs(this.width * 0.5));

    return this._isoBounds;
};

/**
 * The axonometric position of the IsoSprite on the x axis. Increasing the x coordinate will move the object down and to the right on the screen.
 *
 * @name Phaser.Plugin.Isometric.IsoSprite#isoX
 * @property {number} isoX - The axonometric position of the IsoSprite on the x axis.
 */
Object.defineProperty(Phaser.Plugin.Isometric.IsoSprite.prototype, "isoX", {
    get: function () {
        return this._isoPosition.x;
    },
    set: function (value) {
        this._isoPosition.x = value;
        this._depthChanged = this._isoPositionChanged = this._isoBoundsChanged = true;
        if (this.body) {
            this.body._reset = true;
        }
    }
});

/**
 * The axonometric position of the IsoSprite on the y axis. Increasing the y coordinate will move the object down and to the left on the screen.
 *
 * @name Phaser.Plugin.Isometric.IsoSprite#isoY
 * @property {number} isoY - The axonometric position of the IsoSprite on the y axis.
 */
Object.defineProperty(Phaser.Plugin.Isometric.IsoSprite.prototype, "isoY", {
    get: function () {
        return this._isoPosition.y;
    },
    set: function (value) {
        this._isoPosition.y = value;
        this._depthChanged = this._isoPositionChanged = this._isoBoundsChanged = true;
        if (this.body) {
            this.body._reset = true;
        }
    }
});

/**
 * The axonometric position of the IsoSprite on the z axis. Increasing the z coordinate will move the object directly upwards on the screen.
 *
 * @name Phaser.Plugin.Isometric.IsoSprite#isoZ
 * @property {number} isoZ - The axonometric position of the IsoSprite on the z axis.
 */
Object.defineProperty(Phaser.Plugin.Isometric.IsoSprite.prototype, "isoZ", {
    get: function () {
        return this._isoPosition.z;
    },
    set: function (value) {
        this._isoPosition.z = value;
        this._depthChanged = this._isoPositionChanged = this._isoBoundsChanged = true;
        if (this.body) {
            this.body._reset = true;
        }
    }
});

/**
 * A Point3 object representing the axonometric position of the IsoSprite.
 *
 * @name Phaser.Plugin.Isometric.IsoSprite#isoPosition
 * @property {Point3} isoPosition - The axonometric position of the IsoSprite.
 * @readonly
 */
Object.defineProperty(Phaser.Plugin.Isometric.IsoSprite.prototype, "isoPosition", {
    get: function () {
        return this._isoPosition;
    }
});

/**
 * A Cube object representing the derived boundsof the IsoSprite.
 *
 * @name Phaser.Plugin.Isometric.IsoSprite#isoBounds
 * @property {Point3} isoBounds - The derived 3D bounds of the IsoSprite.
 * @readonly
 */
Object.defineProperty(Phaser.Plugin.Isometric.IsoSprite.prototype, "isoBounds", {
    get: function () {
        if (this._isoBoundsChanged || !this._isoBounds) {
            this.resetIsoBounds();
            this._isoBoundsChanged = false;
        }
        return this._isoBounds;
    }
});

/**
 * The non-unit distance of the IsoSprite from the 'front' of the scene. Used to correctly depth sort a group of IsoSprites.
 *
 * @name Phaser.Plugin.Isometric.IsoSprite#depth
 * @property {number} depth - A calculated value used for depth sorting.
 * @readonly
 */
Object.defineProperty(Phaser.Plugin.Isometric.IsoSprite.prototype, "depth", {
    get: function () {
        if (this._depthChanged === true) {
            this._depth = (this._isoPosition.x + this._isoPosition.y) + (this._isoPosition.z * 1.25);
            this._depthChanged = false;
        }
        return this._depth;
    }
});

/**
 * Create a new IsoSprite with specific position and sprite sheet key.
 *
 * @method Phaser.GameObjectFactory#isoSprite
 * @param {number} x - X position of the new IsoSprite.
 * @param {number} y - Y position of the new IsoSprite.
 * @param {number} y - Z position of the new IsoSprite.
 * @param {string|Phaser.RenderTexture|PIXI.Texture} key - This is the image or texture used by the Sprite during rendering. It can be a string which is a reference to the Cache entry, or an instance of a RenderTexture or PIXI.Texture.
 * @param {string|number} [frame] - If the sprite uses an image from a texture atlas or sprite sheet you can pass the frame here. Either a number for a frame ID or a string for a frame name.
 * @param {Phaser.Group} [group] - Optional Group to add the object to. If not specified it will be added to the World group.
 * @returns {Phaser.Plugin.Isometric.IsoSprite} the newly created IsoSprite object.
 */

Phaser.GameObjectCreator.prototype.isoSprite = function (x, y, z, key, frame) {

    return new Phaser.Plugin.Isometric.IsoSprite(this.game, x, y, z, key, frame);

};

/**
 * Create a new IsoSprite with specific position and sprite sheet key.
 *
 * @method Phaser.GameObjectFactory#isoSprite
 * @param {number} x - X position of the new IsoSprite.
 * @param {number} y - Y position of the new IsoSprite.
 * @param {number} y - Z position of the new IsoSprite.
 * @param {string|Phaser.RenderTexture|PIXI.Texture} key - This is the image or texture used by the Sprite during rendering. It can be a string which is a reference to the Cache entry, or an instance of a RenderTexture or PIXI.Texture.
 * @param {string|number} [frame] - If the sprite uses an image from a texture atlas or sprite sheet you can pass the frame here. Either a number for a frame ID or a string for a frame name.
 * @param {Phaser.Group} [group] - Optional Group to add the object to. If not specified it will be added to the World group.
 * @returns {Phaser.Plugin.Isometric.IsoSprite} the newly created IsoSprite object.
 */
Phaser.GameObjectFactory.prototype.isoSprite = function (x, y, z, key, frame, group) {

    if (typeof group === 'undefined') {
        group = this.world;
    }

    return group.add(new Phaser.Plugin.Isometric.IsoSprite(this.game, x, y, z, key, frame));

};

Phaser.Plugin.Isometric.prototype.addIsoSprite = function (x, y, z, key, frame, group) {
    return Phaser.GameObjectFactory.prototype.isoSprite.call(this.game.add, x, y, z, key, frame, group);
};


Phaser.Utils.Debug.prototype.isoSprite = function (sprite, color, filled) {

    if (!sprite.isoBounds) {
        return;
    }

    if (typeof filled === 'undefined') {
        filled = true;
    }

    color = color || 'rgba(0,255,0,0.4)';


    var points = [],
        corners = sprite.isoBounds.getCorners();

    var posX = -sprite.game.camera.x;
    var posY = -sprite.game.camera.y;

    this.start();

    if (filled) {
        points = [corners[1], corners[3], corners[2], corners[6], corners[4], corners[5], corners[1]];

        points = points.map(function (p) {
            var newPos = sprite.game.iso.project(p);
            newPos.x += posX;
            newPos.y += posY;
            return newPos;
        });
        this.context.beginPath();
        this.context.fillStyle = color;
        this.context.moveTo(points[0].x, points[0].y);

        for (var i = 1; i < points.length; i++) {
            this.context.lineTo(points[i].x, points[i].y);
        }
        this.context.fill();
    } else {
        points = corners.slice(0, corners.length);
        points = points.map(function (p) {
            var newPos = sprite.game.iso.project(p);
            newPos.x += posX;
            newPos.y += posY;
            return newPos;
        });

        this.context.moveTo(points[0].x, points[0].y);
        this.context.beginPath();
        this.context.strokeStyle = color;

        this.context.lineTo(points[1].x, points[1].y);
        this.context.lineTo(points[3].x, points[3].y);
        this.context.lineTo(points[2].x, points[2].y);
        this.context.lineTo(points[6].x, points[6].y);
        this.context.lineTo(points[4].x, points[4].y);
        this.context.lineTo(points[5].x, points[5].y);
        this.context.lineTo(points[1].x, points[1].y);
        this.context.lineTo(points[0].x, points[0].y);
        this.context.lineTo(points[4].x, points[4].y);
        this.context.moveTo(points[0].x, points[0].y);
        this.context.lineTo(points[2].x, points[2].y);
        this.context.moveTo(points[3].x, points[3].y);
        this.context.lineTo(points[7].x, points[7].y);
        this.context.lineTo(points[6].x, points[6].y);
        this.context.moveTo(points[7].x, points[7].y);
        this.context.lineTo(points[5].x, points[5].y);
        this.context.stroke();
        this.context.closePath();
    }

    this.stop();

};
;/**
 * Octree Constructor
 *
 * @class Phaser.Plugin.Isometric.Octree
 * @classdesc A Octree implementation based on Phaser.QuadTree.
 * Original version at https://github.com/timohausmann/quadtree-js/
 *
 * @constructor
 * @param {number} x - The bottom-back coordinate of the octree.
 * @param {number} y - The bottom-back coordinate of the octree.
 * @param {number} z - The bottom-back coordinate of the octree.
 * @param {number} widthX - The width X (breadth) of the octree.
 * @param {number} widthY - The width Y (depth) of the octree.
 * @param {number} height - The height (Z) of the octree.
 * @param {number} [maxObjects=10] - The maximum number of objects per node.
 * @param {number} [maxLevels=4] - The maximum number of levels to iterate to.
 * @param {number} [level=0] - Which level is this?
 */
Phaser.Plugin.Isometric.Octree = function (x, y, z, widthX, widthY, height, maxObjects, maxLevels, level) {

    /**
     * @property {number} maxObjects - The maximum number of objects per node.
     * @default
     */
    this.maxObjects = 10;

    /**
     * @property {number} maxLevels - The maximum number of levels to break down to.
     * @default
     */
    this.maxLevels = 4;

    /**
     * @property {number} level - The current level.
     */
    this.level = 0;

    /**
     * @property {object} bounds - Object that contains the octree bounds.
     */
    this.bounds = {};

    /**
     * @property {array} objects - Array of octree children.
     */
    this.objects = [];

    /**
     * @property {array} nodes - Array of associated child nodes.
     */
    this.nodes = [];

    /**
     * @property {array} _empty - Internal empty array.
     * @private
     */
    this._empty = [];

    this.reset(x, y, z, widthX, widthY, height, maxObjects, maxLevels, level);

};

Phaser.Plugin.Isometric.Octree.prototype = {

    /**
     * Resets the QuadTree.
     *
     * @method Phaser.Plugin.Isometric.Octree#reset
     * @param {number} x - The bottom-back coordinate of the octree.
     * @param {number} y - The bottom-back coordinate of the octree.
     * @param {number} z - The bottom-back coordinate of the octree.
     * @param {number} widthX - The width X (breadth) of the octree.
     * @param {number} widthY - The width Y (depth) of the octree.
     * @param {number} height - The height (Z) of the octree.
     * @param {number} [maxObjects=10] - The maximum number of objects per node.
     * @param {number} [maxLevels=4] - The maximum number of levels to iterate to.
     * @param {number} [level=0] - Which level is this?
     */
    reset: function (x, y, z, widthX, widthY, height, maxObjects, maxLevels, level) {

        this.maxObjects = maxObjects || 10;
        this.maxLevels = maxLevels || 4;
        this.level = level || 0;

        this.bounds = {
            x: Math.round(x),
            y: Math.round(y),
            z: Math.round(z),
            widthX: widthX,
            widthY: widthY,
            height: height,
            subWidthX: Math.floor(widthX * 0.5),
            subWidthY: Math.floor(widthY * 0.5),
            subHeight: Math.floor(height * 0.5),
            frontX: Math.round(x) + Math.floor(widthX * 0.5),
            frontY: Math.round(y) + Math.floor(widthY * 0.5),
            top: Math.round(z) + Math.floor(height * 0.5)
        };

        this.objects.length = 0;
        this.nodes.length = 0;

    },

    /**
     * Populates this octree with the children of the given Group. In order to be added the child must exist and have a body property.
     *
     * @method Phaser.Plugin.Isometric.Octree#populate
     * @param {Phaser.Group} group - The Group to add to the octree.
     */
    populate: function (group) {

        group.forEach(this.populateHandler, this, true);

    },

    /**
     * Handler for the populate method.
     *
     * @method Phaser.Plugin.Isometric.Octree#populateHandler
     * @param {Phaser.Plugin.Isometric.IsoSprite|object} sprite - The Sprite to check.
     */
    populateHandler: function (sprite) {

        if (sprite.body && sprite.exists) {
            this.insert(sprite.body);
        }

    },

    /**
     * Split the node into 8 subnodes
     *
     * @method Phaser.Plugin.Isometric.Octree#split
     */
    split: function () {

        //  bottom four octants
        //  -x-y-z
        this.nodes[0] = new Phaser.Plugin.Isometric.Octree(this.bounds.x, this.bounds.y, this.bounds.z, this.bounds.subWidthX, this.bounds.subWidthY, this.bounds.subHeight, this.maxLevels, (this.level + 1));
        //  +x-y-z
        this.nodes[1] = new Phaser.Plugin.Isometric.Octree(this.bounds.frontX, this.bounds.y, this.bounds.z, this.bounds.subWidthX, this.bounds.subWidthY, this.bounds.subHeight, this.maxLevels, (this.level + 1));
        //  -x+y-z
        this.nodes[2] = new Phaser.Plugin.Isometric.Octree(this.bounds.x, this.bounds.frontY, this.bounds.z, this.bounds.subWidthX, this.bounds.subWidthY, this.bounds.subHeight, this.maxLevels, (this.level + 1));
        //  +x+y-z
        this.nodes[3] = new Phaser.Plugin.Isometric.Octree(this.bounds.frontX, this.bounds.frontY, this.bounds.z, this.bounds.subWidthX, this.bounds.subWidthY, this.bounds.subHeight, this.maxLevels, (this.level + 1));

        //  top four octants
        //  -x-y+z
        this.nodes[4] = new Phaser.Plugin.Isometric.Octree(this.bounds.x, this.bounds.y, this.bounds.top, this.bounds.subWidthX, this.bounds.subWidthY, this.bounds.subHeight, this.maxLevels, (this.level + 1));
        //  +x-y+z
        this.nodes[5] = new Phaser.Plugin.Isometric.Octree(this.bounds.frontX, this.bounds.y, this.bounds.top, this.bounds.subWidthX, this.bounds.subWidthY, this.bounds.subHeight, this.maxLevels, (this.level + 1));
        //  -x+y+z
        this.nodes[6] = new Phaser.Plugin.Isometric.Octree(this.bounds.x, this.bounds.frontY, this.bounds.top, this.bounds.subWidthX, this.bounds.subWidthY, this.bounds.subHeight, this.maxLevels, (this.level + 1));
        //  +x+y+z
        this.nodes[7] = new Phaser.Plugin.Isometric.Octree(this.bounds.frontX, this.bounds.frontY, this.bounds.top, this.bounds.subWidthX, this.bounds.subWidthY, this.bounds.subHeight, this.maxLevels, (this.level + 1));
    },

    /**
     * Insert the object into the node. If the node exceeds the capacity, it will split and add all objects to their corresponding subnodes.
     *
     * @method Phaser.Plugin.Isometric.Octree#insert
     * @param {Phaser.Plugin.Isometric.Body|Phaser.Plugin.Isometric.Cube|object} body - The Body object to insert into the octree. Can be any object so long as it exposes x, y, z, frontX, frontY and top properties.
     */
    insert: function (body) {

        var i = 0;
        var index;

        //  if we have subnodes ...
        if (this.nodes[0] != null) {
            index = this.getIndex(body);

            if (index != -1) {
                this.nodes[index].insert(body);
                return;
            }
        }

        this.objects.push(body);

        if (this.objects.length > this.maxObjects && this.level < this.maxLevels) {
            //  Split if we don't already have subnodes
            if (this.nodes[0] == null) {
                this.split();
            }

            //  Add objects to subnodes
            while (i < this.objects.length) {
                index = this.getIndex(this.objects[i]);

                if (index != -1) {
                    //  this is expensive - see what we can do about it
                    this.nodes[index].insert(this.objects.splice(i, 1)[0]);
                } else {
                    i++;
                }
            }
        }

    },

    /**
     * Determine which node the object belongs to.
     *
     * @method Phaser.Plugin.Isometric.Octree#getIndex
     * @param {Phaser.Plugin.Isometric.Cube|object} cube - The bounds in which to check.
     * @return {number} index - Index of the subnode (0-7), or -1 if cube cannot completely fit within a subnode and is part of the parent node.
     */
    getIndex: function (cube) {

        //  default is that cube doesn't fit, i.e. it straddles the internal octants
        var index = -1;

        if (cube.x < this.bounds.frontX && cube.frontX < this.bounds.frontX) {
            if (cube.y < this.bounds.frontY && cube.frontY < this.bounds.frontY) {
                if (cube.z < this.bounds.top && cube.top < this.bounds.top) {
                    //  cube fits into -x-y-z octant
                    index = 0;
                } else if (cube.z > this.bounds.top) {
                    //  cube fits into -x-y+z octant
                    index = 4;
                }
            } else if (cube.y > this.bounds.frontY) {
                if (cube.z < this.bounds.top && cube.top < this.bounds.top) {
                    //  cube fits into -x+y-z octant
                    index = 2;
                } else if (cube.z > this.bounds.top) {
                    //  cube fits into -x+y+z octant
                    index = 6;
                }
            }
        } else if (cube.x > this.bounds.frontX) {
            if (cube.y < this.bounds.frontY && cube.frontY < this.bounds.frontY) {
                if (cube.z < this.bounds.top && cube.top < this.bounds.top) {
                    //  cube fits into +x-y-z octant
                    index = 1;
                } else if (cube.z > this.bounds.top) {
                    //  cube fits into +x-y+z octant
                    index = 5;
                }
            } else if (cube.y > this.bounds.frontY) {
                if (cube.z < this.bounds.top && cube.top < this.bounds.top) {
                    //  cube fits into +x+y-z octant
                    index = 3;
                } else if (cube.z > this.bounds.top) {
                    //  cube fits into +x+y+z octant
                    index = 7;
                }
            }
        }


        return index;

    },

    /**
     * Return all objects that could collide with the given IsoSprite or Cube.
     *
     * @method Phaser.Plugin.Isometric.Octree#retrieve
     * @param {Phaser.Plugin.Isometric.IsoSprite|Phaser.Plugin.Isometric.Cube} source - The source object to check the Octree against. Either a IsoSprite or Cube.
     * @return {array} - Array with all detected objects.
     */
    retrieve: function (source) {

        var returnObjects, index;

        if (source instanceof Phaser.Plugin.Isometric.Cube) {
            returnObjects = this.objects;

            index = this.getIndex(source);
        } else {
            if (!source.body) {
                return this._empty;
            }

            returnObjects = this.objects;

            index = this.getIndex(source.body);
        }

        if (this.nodes[0]) {
            //  If cube fits into a subnode ..
            if (index !== -1) {
                returnObjects = returnObjects.concat(this.nodes[index].retrieve(source));
            } else {
                //  If cube does not fit into a subnode, check it against all subnodes (unrolled for speed)
                returnObjects = returnObjects.concat(this.nodes[0].retrieve(source));
                returnObjects = returnObjects.concat(this.nodes[1].retrieve(source));
                returnObjects = returnObjects.concat(this.nodes[2].retrieve(source));
                returnObjects = returnObjects.concat(this.nodes[3].retrieve(source));
                returnObjects = returnObjects.concat(this.nodes[4].retrieve(source));
                returnObjects = returnObjects.concat(this.nodes[5].retrieve(source));
                returnObjects = returnObjects.concat(this.nodes[6].retrieve(source));
                returnObjects = returnObjects.concat(this.nodes[7].retrieve(source));
            }
        }

        return returnObjects;

    },

    /**
     * Clear the octree.
     * @method Phaser.Plugin.Isometric.Octree#clear
     */
    clear: function () {

        this.objects.length = 0;

        var i = this.nodes.length;

        while (i--) {
            this.nodes[i].clear();
            this.nodes.splice(i, 1);
        }

        this.nodes.length = 0;
    }

};

Phaser.Plugin.Isometric.Octree.prototype.constructor = Phaser.Plugin.Isometric.Octree;

/**
 * Visually renders an Octree to the display.
 *
 * @method Phaser.Utils.Debug#octree
 * @param {Phaser.Plugin.Isometric.Octree} octree - The octree to render.
 * @param {string} color - The color of the lines in the quadtree.
 */
Phaser.Utils.Debug.prototype.octree = function (octree, color) {

    color = color || 'rgba(255,0,0,0.3)';

    this.start();

    var bounds = octree.bounds,
        i, points;

    if (octree.nodes.length === 0) {

        this.context.strokeStyle = color;

        var cube = new Phaser.Plugin.Isometric.Cube(bounds.x, bounds.y, bounds.z, bounds.widthX, bounds.widthY, bounds.height);
        var corners = cube.getCorners();

        var posX = -this.game.camera.x;
        var posY = -this.game.camera.y;

        points = corners.slice(0, corners.length);

        points = points.map(function (p) {
            var newPos = this.game.iso.project(p);
            newPos.x += posX;
            newPos.y += posY;
            return newPos;
        });

        this.context.moveTo(points[0].x, points[0].y);
        this.context.beginPath();
        this.context.strokeStyle = color;

        this.context.lineTo(points[1].x, points[1].y);
        this.context.lineTo(points[3].x, points[3].y);
        this.context.lineTo(points[2].x, points[2].y);
        this.context.lineTo(points[6].x, points[6].y);
        this.context.lineTo(points[4].x, points[4].y);
        this.context.lineTo(points[5].x, points[5].y);
        this.context.lineTo(points[1].x, points[1].y);
        this.context.lineTo(points[0].x, points[0].y);
        this.context.lineTo(points[4].x, points[4].y);
        this.context.moveTo(points[0].x, points[0].y);
        this.context.lineTo(points[2].x, points[2].y);
        this.context.moveTo(points[3].x, points[3].y);
        this.context.lineTo(points[7].x, points[7].y);
        this.context.lineTo(points[6].x, points[6].y);
        this.context.moveTo(points[7].x, points[7].y);
        this.context.lineTo(points[5].x, points[5].y);
        this.context.stroke();
        this.context.closePath();

        for (i = 0; i < octree.objects.length; i++) {
            this.body(octree.objects[i].sprite, 'rgb(0,255,0)', false);
        }
    } else {
        for (i = 0; i < octree.nodes.length; i++) {
            this.octree(octree.nodes[i]);
        }
    }

    this.stop();

};
;/**
 * @class Phaser.Plugin.Isometric.Point3
 *
 * @classdesc
 * The Point3 object represents a location in a three-dimensional coordinate system,
 * where x and y represent the horizontal axes and z represents the vertical axis.
 * The following code creates a point at (0,0,0):
 * `var myPoint = new Phaser.Plugin.Isometric.Point3();`
 *
 * Creates a new Point3 object. If you pass no parameters a Point3 is created set to (0, 0, 0).
 *
 * @constructor
 * @param {number} [x=0] - The horizontal X position of this Point.
 * @param {number} [y=0] - The horizontal Y position of this Point.
 * @param {number} [z=0] - The vertical position of this Point.
 */
Phaser.Plugin.Isometric.Point3 = function (x, y, z) {
    x = x || 0;
    y = y || 0;
    z = z || 0;

    /**
     * @property {number} x - The x value of the point.
     */
    this.x = x;

    /**
     * @property {number} y - The y value of the point.
     */
    this.y = y;

    /**
     * @property {number} z - The z value of the point.
     */
    this.z = z;
};

Phaser.Plugin.Isometric.Point3.prototype = {
    /**
     * Copies the x, y and z properties from any given object to this Point3.
     *
     * @method Phaser.Plugin.Isometric.Point3#copyFrom
     * @param {any} source - The object to copy from.
     * @return {Phaser.Plugin.Isometric.Point3} This Point3 object.
     */
    copyFrom: function (source) {

        return this.setTo(source.x, source.y, source.z);

    },

    /**
     * Copies the x, y and z properties from this Point3 to any given object.
     *
     * @method Phaser.Plugin.Isometric.Point3#copyTo
     * @param {any} dest - The object to copy to.
     * @return {Object} The dest object.
     */
    copyTo: function (dest) {

        dest.x = this.x;
        dest.y = this.y;
        dest.z = this.z;

        return dest;

    },

    /**
     * Determines whether the given object's x/y/z values are equal to this Point3 object.
     *
     * @method Phaser.Plugin.Isometric.Point3#equals
     * @param {Phaser.Plugin.Isometric.Point3|any} a - The object to compare with this Point3.
     * @return {boolean} A value of true if the x and y points are equal, otherwise false.
     */
    equals: function (a) {

        return (a.x === this.x && a.y === this.y && a.z === this.z);

    },

    /**
     * Sets the x, y and z values of this Point3 object to the given values.
     * If you omit the y and z value then the x value will be applied to all three, for example:
     * `Point3.set(2)` is the same as `Point3.set(2, 2, 2)`
     * If however you set both x and y, but no z, the z value will be set to 0.
     *
     * @method Phaser.Plugin.Isometric.Point3#set
     * @param {number} x - The x value of this point.
     * @param {number} [y] - The y value of this point. If not given the x value will be used in its place.
     * @param {number} [z] - The z value of this point. If not given and the y value is also not given, the x value will be used in its place.
     * @return {Phaser.Plugin.Isometric.Point3} This Point3 object. Useful for chaining method calls.
     */
    set: function (x, y, z) {
        this.x = x || 0;
        this.y = y || ((y !== 0) ? this.x : 0);
        this.z = z || ((typeof y === "undefined") ? this.x : 0);

        return this;
    },

    /**
     * Sets the x, y and z values of this Point3 object to the given values.
     * If you omit the y and z value then the x value will be applied to all three, for example:
     * `Point3.setTo(2)` is the same as `Point3.setTo(2, 2, 2)`
     * If however you set both x and y, but no z, the z value will be set to 0.
     *
     * @method Phaser.Plugin.Isometric.Point3#setTo
     * @param {number} x - The x value of this point.
     * @param {number} [y] - The y value of this point. If not given the x value will be used in its place.
     * @param {number} [z] - The z value of this point. If not given and the y value is also not given, the x value will be used in its place.
     * @return {Phaser.Plugin.Isometric.Point3} This Point3 object. Useful for chaining method calls.
     */
    setTo: function (x, y, z) {
        return this.set(x, y, z);
    },

    /**
     * Adds the given x, y and z values to this Point3.
     *
     * @method Phaser.Plugin.Isometric.Point3#add
     * @param {number} x - The value to add to Point3.x.
     * @param {number} y - The value to add to Point3.y.
     * @param {number} z - The value to add to Point3.z.
     * @return {Phaser.Plugin.Isometric.Point3} This Point3 object. Useful for chaining method calls.
     */
    add: function (x, y) {

        this.x += x || 0;
        this.y += y || 0;
        return this;

    },

    /**
     * Subtracts the given x, y and z values from this Point3.
     *
     * @method Phaser.Plugin.Isometric.Point3#subtract
     * @param {number} x - The value to subtract from Point3.x.
     * @param {number} y - The value to subtract from Point3.y.
     * @param {number} z - The value to subtract from Point3.z.
     * @return {Phaser.Plugin.Isometric.Point3} This Point3 object. Useful for chaining method calls.
     */
    subtract: function (x, y, z) {

        this.x -= x || 0;
        this.y -= y || 0;
        this.z -= z || 0;

        return this;

    },

    /**
     * Multiplies Point3.x, Point3.y and Point3.z by the given x and y values. Sometimes known as `Scale`.
     *
     * @method Phaser.Plugin.Isometric.Point3#multiply
     * @param {number} x - The value to multiply Point3.x by.
     * @param {number} y - The value to multiply Point3.y by.
     * @param {number} z - The value to multiply Point3.z by.
     * @return {Phaser.Plugin.Isometric.Point3} This Point3 object. Useful for chaining method calls.
     */
    multiply: function (x, y, z) {

        this.x *= x || 1;
        this.y *= y || 1;
        this.z *= z || 1;

        return this;

    },

    /**
     * Divides Point3.x, Point3.y and Point3.z by the given x, y and z values.
     *
     * @method Phaser.Plugin.Isometric.Point3#divide
     * @param {number} x - The value to divide Point3.x by.
     * @param {number} y - The value to divide Point3.y by.
     * @param {number} z - The value to divide Point3.z by.
     * @return {Phaser.Plugin.Isometric.Point3} This Point3 object. Useful for chaining method calls.
     */
    divide: function (x, y, z) {

        this.x /= x || 1;
        this.y /= y || 1;
        this.z /= z || 1;

        return this;

    }
};

Phaser.Plugin.Isometric.Point3.prototype.constructor = Phaser.Plugin.Isometric.Point3;

/**
 * Adds the coordinates of two points together to create a new point.
 *
 * @method Phaser.Plugin.Isometric.Point3.add
 * @param {Phaser.Plugin.Isometric.Point3} a - The first Point3 object.
 * @param {Phaser.Plugin.Isometric.Point3} b - The second Point3 object.
 * @param {Phaser.Plugin.Isometric.Point3} [out] - Optional Point3 to store the value in, if not supplied a new Point3 object will be created.
 * @return {Phaser.Plugin.Isometric.Point3} The new Point3 object.
 */
Phaser.Plugin.Isometric.Point3.add = function (a, b, out) {

    if (typeof out === "undefined") {
        out = new Phaser.Plugin.Isometric.Point3();
    }

    out.x = a.x + b.x;
    out.y = a.y + b.y;
    out.z = a.z + b.z;

    return out;

};

/**
 * Subtracts the coordinates of two points to create a new point.
 *
 * @method Phaser.Plugin.Isometric.Point3.subtract
 * @param {Phaser.Plugin.Isometric.Point3} a - The first Point3 object.
 * @param {Phaser.Plugin.Isometric.Point3} b - The second Point3 object.
 * @param {Phaser.Plugin.Isometric.Point3} [out] - Optional Point3 to store the value in, if not supplied a new Point3 object will be created.
 * @return {Phaser.Plugin.Isometric.Point3} The new Point3 object.
 */
Phaser.Plugin.Isometric.Point3.subtract = function (a, b, out) {

    if (typeof out === "undefined") {
        out = new Phaser.Plugin.Isometric.Point3();
    }

    out.x = a.x - b.x;
    out.y = a.y - b.y;
    out.z = a.z - b.z;

    return out;

};

/**
 * Multiplies the coordinates of two points to create a new point.
 *
 * @method Phaser.Plugin.Isometric.Point3.multiply
 * @param {Phaser.Plugin.Isometric.Point3} a - The first Point3 object.
 * @param {Phaser.Plugin.Isometric.Point3} b - The second Point3 object.
 * @param {Phaser.Plugin.Isometric.Point3} [out] - Optional Point3 to store the value in, if not supplied a new Point3 object will be created.
 * @return {Phaser.Plugin.Isometric.Point3} The new Point3 object.
 */
Phaser.Plugin.Isometric.Point3.multiply = function (a, b, out) {

    if (typeof out === "undefined") {
        out = new Phaser.Plugin.Isometric.Point3();
    }

    out.x = a.x * b.x;
    out.y = a.y * b.y;
    out.z = a.z * b.z;

    return out;

};

/**
 * Divides the coordinates of two points to create a new point.
 *
 * @method Phaser.Plugin.Isometric.Point3.divide
 * @param {Phaser.Plugin.Isometric.Point3} a - The first Point3 object.
 * @param {Phaser.Plugin.Isometric.Point3} b - The second Point3 object.
 * @param {Phaser.Plugin.Isometric.Point3} [out] - Optional Point3 to store the value in, if not supplied a new Point3 object3 will be created.
 * @return {Phaser.Plugin.Isometric.Point3} The new Point3 object.
 */
Phaser.Plugin.Isometric.Point3.divide = function (a, b, out) {

    if (typeof out === "undefined") {
        out = new Phaser.Plugin.Isometric.Point3();
    }

    out.x = a.x / b.x;
    out.y = a.y / b.y;
    out.z = a.z / b.z;

    return out;

};

/**
 * Determines whether the two given Point3 objects are equal. They are considered equal if they have the same x, y and z values.
 *
 * @method Phaser.Plugin.Isometric.Point3.equals
 * @param {Phaser.Plugin.Isometric.Point3} a - The first Point3 object.
 * @param {Phaser.Plugin.Isometric.Point3} b - The second Point3 object.
 * @return {boolean} A value of true if the Points3 are equal, otherwise false.
 */
Phaser.Plugin.Isometric.Point3.equals = function (a, b) {

    return (a.x === b.x && a.y === b.y && a.z === b.z);

};
;/**
 * @class Phaser.Plugin.Isometric.Projector
 *
 * @classdesc
 * Creates a new Isometric Projector object, which has helpers for projecting x, y and z coordinates into axonometric x and y equivalents.
 *
 * @constructor
 * @param {Phaser.Game} game - The current game object.
 * @param {number} projectionAngle - The angle of the axonometric projection in radians. Defaults to approx. 0.4636476 (Math.atan(0.5) which is suitable for 2:1 pixel art dimetric)
 * @return {Phaser.Plugin.Isometric.Cube} This Cube object.
 */
Phaser.Plugin.Isometric.Projector = function (game, projectionAngle) {

    /**
     * @property {Phaser.Game} game - The current game object.
     */
    this.game = game;

    /**
     * @property {array} _transform - The pre-calculated axonometric transformation values.
     * @private
     */
    this._transform = null;

    /**
     * @property {number} _projectionAngle - The cached angle of projection in radians.
     * @private
     */
    this._projectionAngle = 0;

    /**
     * @property {number} projectionAngle - The angle of projection in radians.
     * @default
     */
    this.projectionAngle = projectionAngle || Phaser.Plugin.Isometric.CLASSIC;

    /**
     * @property {Phaser.Point} anchor - The x and y offset multipliers as a ratio of the game world size.
     * @default
     */
    this.anchor = new Phaser.Point(0.5, 0);


};

//  Projection angles
Phaser.Plugin.Isometric.CLASSIC = Math.atan(0.5);
Phaser.Plugin.Isometric.ISOMETRIC = Math.PI / 6;
Phaser.Plugin.Isometric.MILITARY = Math.PI / 4;

Phaser.Plugin.Isometric.Projector.prototype = {

    /**
     * Use axonometric projection to transform a 3D Point3 coordinate to a 2D Point coordinate. If given the coordinates will be set into the object, otherwise a brand new Point object will be created and returned.
     * @method Phaser.Plugin.Isometric.Projector#project
     * @param {Phaser.Plugin.Isometric.Point3} point3 - The Point3 to project from.
     * @param {Phaser.Point} out - The Point to project to.
     * @return {Phaser.Point} The transformed Point.
     */
    project: function (point3, out) {
        if (typeof out === "undefined") {
            out = new Phaser.Point();
        }

        out.x = (point3.x - point3.y) * this._transform[0];
        out.y = ((point3.x + point3.y) * this._transform[1]) - point3.z;

        out.x += this.game.world.width * this.anchor.x;
        out.y += this.game.world.height * this.anchor.y;

        return out;
    },

    /**
     * Use axonometric projection to transform a 3D Point3 coordinate to a 2D Point coordinate, ignoring the z-axis. If given the coordinates will be set into the object, otherwise a brand new Point object will be created and returned.
     * @method Phaser.Plugin.Isometric.Projector#projectXY
     * @param {Phaser.Plugin.Isometric.Point3} point3 - The Point3 to project from.
     * @param {Phaser.Point} out - The Point to project to.
     * @return {Phaser.Point} The transformed Point.
     */
    projectXY: function (point3, out) {
        if (typeof out === "undefined") {
            out = new Phaser.Point();
        }

        out.x = (point3.x - point3.y) * this._transform[0];
        out.y = (point3.x + point3.y) * this._transform[1];

        out.x += this.game.world.width * this.anchor.x;
        out.y += this.game.world.height * this.anchor.y;

        return out;
    },

    /**
     * Use reverse axonometric projection to transform a 2D Point coordinate to a 3D Point3 coordinate. If given the coordinates will be set into the object, otherwise a brand new Point3 object will be created and returned.
     * @method Phaser.Plugin.Isometric.Projector#unproject
     * @param {Phaser.Plugin.Isometric.Point} point - The Point to project from.
     * @param {Phaser.Plugin.Isometric.Point3} out - The Point3 to project to.
     * @param {number} [z] - Specified z-plane to project to.
     * @return {Phaser.Plugin.Isometric.Point3} The transformed Point3.
     */
    unproject: function (point, out, z) {
        if (typeof out === "undefined") {
            out = new Phaser.Plugin.Isometric.Point3();
        }

        z = z || 0;


        var x = point.x - this.game.world.x - (this.game.world.width * this.anchor.x);
        var y = point.y - this.game.world.y - (this.game.world.height * this.anchor.y) + z;

        out.x = x / (2 * this._transform[0]) + y / (2 * this._transform[1]);
        out.y = -(x / (2 * this._transform[0])) + y / (2 * this._transform[1]);
        out.z = z;

        return out;
    },

    /**
     * Perform a simple depth sort on all IsoSprites in the passed group. This function is fast and will accurately sort items on a single z-plane, but breaks down when items are above/below one another in certain configurations.
     *
     * @method Phaser.Plugin.Isometric.Projector#simpleSort
     * @param {Phaser.Group} group - A group of IsoSprites to sort.
     */
    simpleSort: function (group) {
        group.sort("depth");
    },

    /**
     * Perform a volume-based topological sort on all IsoSprites in the passed group or array. Will use the body if available, otherwise it will use an automatically generated bounding cube. If a group is passed, <code>Phaser.Group#sort</code> is automatically called on the specified property.
     * Routine adapted from this tutorial: http://mazebert.com/2013/04/18/isometric-depth-sorting/
     *
     * @method Phaser.Plugin.Isometric.Projector#topologicalSort
     * @param {Phaser.Group|array} group - A group or array of IsoSprites to sort.
     * @param {number} [padding] - The amount of extra tolerance in the depth sorting; larger values reduce flickering when objects collide, but also introduce inaccuracy when objects are close. Defaults to 1.5.
     * @param {string} [prop] - The property to store the depth information on. If not specified, it will default to 'isoDepth'.
     */
    topologicalSort: function (group, padding, prop) {
        var children, isGroup;

        if (group instanceof Phaser.Group) {
            children = group.children;
            isGroup = true;
        }
        else if (group.length) {
            children = group;
        }
        else {
            return;
        }

        prop = prop || "isoDepth";

        if (typeof padding === "undefined") {
            padding = 1.5;
        }
        else {
            padding = padding;
        }

        var a, b, i, j, bounds, behindIndex, len = children.length;

        for (i = 0; i < len; i++) {
            a = children[i];
            behindIndex = 0;
            if (!a.isoSpritesBehind) {
                a.isoSpritesBehind = [];
            }

            for (j = 0; j < len; j++) {
                if (i != j) {
                    b = children[j];
                    bounds = a.body || a.isoBounds;
                    if (b._isoPosition.x + padding < bounds.frontX - padding && b._isoPosition.y + padding < bounds.frontY - padding && b._isoPosition.z + padding < bounds.top - padding) {
                        a.isoSpritesBehind[behindIndex++] = b;
                    }
                }
            }
            a.isoVisitedFlag = false;
        }

        var _sortDepth = 0;

        function visitNode(node) {
            if (node.isoVisitedFlag === false) {
                node.isoVisitedFlag = true;
                var spritesBehindLength = node.isoSpritesBehind.length;
                for (var k = 0; k < spritesBehindLength; k++) {
                    if (node.isoSpritesBehind[k] === null) {
                        break;
                    }
                    else {
                        visitNode(node.isoSpritesBehind[k]);
                        node.isoSpritesBehind[k] = null;
                    }
                }

                node[prop] = _sortDepth++;
            }
        }

        for (i = 0; i < len; i++) {
            visitNode(children[i]);
        }

        if (isGroup) {
            group.sort(prop);
        }
    }

};

/**
 * @name Phaser.Plugin.Isometric.Projector#projectionAngle
 * @property {number} projectionAngle - The angle of axonometric projection.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Projector.prototype, "projectionAngle", {

    get: function () {
        return this._projectionAngle;
    },

    set: function (value) {

        if (value === this._projectionAngle) {
            return;
        }

        this._projectionAngle = value;

        this._transform = [Math.cos(this._projectionAngle), Math.sin(this._projectionAngle)];
    }

});
;/**
 * @class Phaser.Plugin.Isometric.Body
 *
 * @classdesc
 * The Physics Body is linked to a single IsoSprite. All physics operations should be performed against the body rather than
 * the IsoSprite itself. For example you can set the velocity, acceleration, bounce values etc all on the Body.
 *
 * @constructor
 * @param {Phaser.Plugin.Isometric.IsoSprite} sprite - The IsoSprite object this physics body belongs to.
 */
Phaser.Plugin.Isometric.Body = function (sprite) {

    /**
     * @property {Phaser.Plugin.Isometric.IsoSprite} sprite - Reference to the parent IsoSprite.
     */
    this.sprite = sprite;

    /**
     * @property {Phaser.Game} game - Local reference to game.
     */
    this.game = sprite.game;

    /**
     * @property {number} objType - The objType of physics system this body belongs to.
     */
    this.type = Phaser.Plugin.Isometric.ISOARCADE;

    /**
     * @property {boolean} enable - A disabled body won't be checked for any form of collision or overlap or have its pre/post updates run.
     * @default
     */
    this.enable = true;

    /**
     * @property {Phaser.Point} offset - The offset of the Physics Body from the IsoSprite x/y/z position.
     */
    this.offset = new Phaser.Plugin.Isometric.Point3();

    /**
     * @property {Phaser.Plugin.Isometric.Point3} position - The position of the physics body.
     * @readonly
     */
    this.position = new Phaser.Plugin.Isometric.Point3(sprite.isoX, sprite.isoY, sprite.isoZ);

    /**
     * @property {Phaser.Point} prev - The previous position of the physics body.
     * @readonly
     */
    this.prev = new Phaser.Plugin.Isometric.Point3(this.position.x, this.position.y, this.position.z);

    /**
     * @property {boolean} allowRotation - Allow this Body to be rotated? (via angularVelocity, etc)
     * @default
     */
    this.allowRotation = true;

    /**
     * @property {number} rotation - The amount the Body is rotated.
     */
    this.rotation = sprite.rotation;

    /**
     * @property {number} preRotation - The previous rotation of the physics body.
     * @readonly
     */
    this.preRotation = sprite.rotation;

    /**
     * @property {number} sourceWidthX - The un-scaled original size.
     * @readonly
     */
    this.sourceWidthX = sprite.texture.frame.width;

    /**
     * @property {number} sourceWidthY - The un-scaled original size.
     * @readonly
     */
    this.sourceWidthY = sprite.texture.frame.width;

    /**
     * @property {number} sourceHeight - The un-scaled original size.
     * @readonly
     */
    this.sourceHeight = sprite.texture.frame.height;

    /**
     * @property {number} widthX - The calculated X width (breadth) of the physics body.
     */
    this.widthX = Math.ceil(sprite.width * 0.5);

    /**
     * @property {number} widthY - The calculated Y width (depth) of the physics body.
     */
    this.widthY = Math.ceil(sprite.width * 0.5);

    /**
     * @property {number} height - The calculated height of the physics body.
     */
    this.height = sprite.height - Math.ceil(sprite.width * 0.5);

    /**
     * @property {number} halfWidthX - The calculated X width / 2 of the physics body.
     */
    this.halfWidthX = Math.abs(this.widthX * 0.5);

    /**
     * @property {number} halfWidthX - The calculated X width / 2 of the physics body.
     */
    this.halfWidthY = Math.abs(this.widthY * 0.5);

    /**
     * @property {number} halfHeight - The calculated height / 2 of the physics body.
     */
    this.halfHeight = Math.abs(this.height * 0.5);

    /**
     * @property {Phaser.Plugin.Isometric.Point3} center - The center coordinate of the physics body.
     */
    this.center = new Phaser.Plugin.Isometric.Point3(sprite.isoX + this.halfWidthX, sprite.isoY + this.halfWidthY, sprite.isoZ + this.halfHeight);

    /**
     * @property {Phaser.Plugin.Isometric.Point3} velocity - The velocity in pixels per second sq. of the Body.
     */
    this.velocity = new Phaser.Plugin.Isometric.Point3();

    /**
     * @property {Phaser.Plugin.Isometric.Point3} newVelocity - New velocity.
     * @readonly
     */
    this.newVelocity = new Phaser.Plugin.Isometric.Point3();

    /**
     * @property {Phaser.Plugin.Isometric.Point3} deltaMax - The Sprite position is updated based on the delta x/y values. You can set a cap on those (both +-) using deltaMax.
     */
    this.deltaMax = new Phaser.Plugin.Isometric.Point3();

    /**
     * @property {Phaser.Plugin.Isometric.Point3} acceleration - The velocity in pixels per second sq. of the Body.
     */
    this.acceleration = new Phaser.Plugin.Isometric.Point3();

    /**
     * @property {Phaser.Plugin.Isometric.Point3} drag - The drag applied to the motion of the Body.
     */
    this.drag = new Phaser.Plugin.Isometric.Point3();

    /**
     * @property {boolean} allowGravity - Allow this Body to be influenced by gravity? Either world or local.
     * @default
     */
    this.allowGravity = true;

    /**
     * @property {Phaser.Plugin.Isometric.Point3} gravity - A local gravity applied to this Body. If non-zero this over rides any world gravity, unless Body.allowGravity is set to false.
     */
    this.gravity = new Phaser.Plugin.Isometric.Point3();

    /**
     * @property {Phaser.Plugin.Isometric.Point3} bounce - The elasticitiy of the Body when colliding. bounce.x/y/z = 1 means full rebound, bounce.x/y/z = 0.5 means 50% rebound velocity.
     */
    this.bounce = new Phaser.Plugin.Isometric.Point3();

    /**
     * @property {Phaser.Plugin.Isometric.Point3} maxVelocity - The maximum velocity in pixels per second sq. that the Body can reach.
     * @default
     */
    this.maxVelocity = new Phaser.Plugin.Isometric.Point3(10000, 10000, 10000);

    /**
     * @property {number} angularVelocity - The angular velocity in pixels per second sq. of the Body.
     * @default
     */
    this.angularVelocity = 0;

    /**
     * @property {number} angularAcceleration - The angular acceleration in pixels per second sq. of the Body.
     * @default
     */
    this.angularAcceleration = 0;

    /**
     * @property {number} angularDrag - The angular drag applied to the rotation of the Body.
     * @default
     */
    this.angularDrag = 0;

    /**
     * @property {number} maxAngular - The maximum angular velocity in pixels per second sq. that the Body can reach.
     * @default
     */
    this.maxAngular = 1000;

    /**
     * @property {number} mass - The mass of the Body.
     * @default
     */
    this.mass = 1;

    /**
     * @property {number} angle - The angle of the Body in radians as calculated by its velocity, rather than its visual angle.
     * @readonly
     */
    this.angle = 0;

    /**
     * @property {number} speed - The speed of the Body as calculated by its velocity.
     * @readonly
     */
    this.speed = 0;

    /**
     * @property {number} facing - A const reference to the direction the Body is traveling or facing.
     * @default
     */
    this.facing = Phaser.NONE;

    /**
     * @property {boolean} immovable - An immovable Body will not receive any impacts from other bodies.
     * @default
     */
    this.immovable = false;

    /**
     * If you have a Body that is being moved around the world via a tween or a Group motion, but its local x/y position never
     * actually changes, then you should set Body.moves = false. Otherwise it will most likely fly off the screen.
     * If you want the physics system to move the body around, then set moves to true.
     * @property {boolean} moves - Set to true to allow the Physics system to move this Body, other false to move it manually.
     * @default
     */
    this.moves = true;

    /**
     * This flag allows you to disable the custom x separation that takes place by Physics.IsoArcade.separate.
     * Used in combination with your own collision processHandler you can create whatever objType of collision response you need.
     * @property {boolean} customSeparateX - Use a custom separation system or the built-in one?
     * @default
     */
    this.customSeparateX = false;

    /**
     * This flag allows you to disable the custom y separation that takes place by Physics.IsoArcade.separate.
     * Used in combination with your own collision processHandler you can create whatever objType of collision response you need.
     * @property {boolean} customSeparateY - Use a custom separation system or the built-in one?
     * @default
     */
    this.customSeparateY = false;

    /**
     * This flag allows you to disable the custom z separation that takes place by Physics.IsoArcade.separate.
     * Used in combination with your own collision processHandler you can create whatever objType of collision response you need.
     * @property {boolean} customSeparateZ - Use a custom separation system or the built-in one?
     * @default
     */
    this.customSeparateZ = false;

    /**
     * When this body collides with another, the amount of overlap is stored here.
     * @property {number} overlapX - The amount of horizontal overlap during the collision.
     */
    this.overlapX = 0;

    /**
     * When this body collides with another, the amount of overlap is stored here.
     * @property {number} overlapY - The amount of vertical overlap during the collision.
     */
    this.overlapY = 0;

    /**
     * When this body collides with another, the amount of overlap is stored here.
     * @property {number} overlapY - The amount of vertical overlap during the collision.
     */
    this.overlapZ = 0;

    /**
     * If a body is overlapping with another body, but neither of them are moving (maybe they spawned on-top of each other?) this is set to true.
     * @property {boolean} embedded - Body embed value.
     */
    this.embedded = false;

    /**
     * A Body can be set to collide against the World bounds automatically and rebound back into the World if this is set to true. Otherwise it will leave the World.
     * @property {boolean} collideWorldBounds - Should the Body collide with the World bounds?
     */
    this.collideWorldBounds = false;

    /**
     * Set the checkCollision properties to control which directions collision is processed for this Body.
     * For example checkCollision.up = false means it won't collide when the collision happened while moving up.
     * @property {object} checkCollision - An object containing allowed collision.
     */
    this.checkCollision = {
        none: false,
        any: true,
        up: true,
        down: true,
        frontX: true,
        frontY: true,
        backX: true,
        backY: true
    };

    /**
     * This object is populated with boolean values when the Body collides with another.
     * touching.up = true means the collision happened to the top of this Body for example.
     * @property {object} touching - An object containing touching results.
     */
    this.touching = {
        none: true,
        up: false,
        down: false,
        frontX: false,
        frontY: false,
        backX: false,
        backY: false
    };

    /**
     * This object is populated with previous touching values from the bodies previous collision.
     * @property {object} wasTouching - An object containing previous touching results.
     */
    this.wasTouching = {
        none: true,
        up: false,
        down: false,
        frontX: false,
        frontY: false,
        backX: false,
        backY: false
    };

    /**
     * This object is populated with boolean values when the Body collides with the World bounds or a Tile.
     * For example if blocked.up is true then the Body cannot move up.
     * @property {object} blocked - An object containing on which faces this Body is blocked from moving, if any.
     */
    this.blocked = {
        up: false,
        down: false,
        frontX: false,
        frontY: false,
        backX: false,
        backY: false
    };

    /**
     * @property {number} phase - Is this Body in a preUpdate (1) or postUpdate (2) state?
     */
    this.phase = 0;

    /**
     * @property {boolean} skipTree - If true and you collide this IsoSprite against a Group, it will disable the collision check from using a QuadTree/Octree.
     */
    this.skipTree = false;

    /**
     * @property {boolean} _reset - Internal cache var.
     * @private
     */
    this._reset = true;

    /**
     * @property {number} _sx - Internal cache var.
     * @private
     */
    this._sx = sprite.scale.x;

    /**
     * @property {number} _sy - Internal cache var.
     * @private
     */
    this._sy = sprite.scale.y;

    /**
     * @property {number} _dx - Internal cache var.
     * @private
     */
    this._dx = 0;

    /**
     * @property {number} _dy - Internal cache var.
     * @private
     */
    this._dy = 0;

    /**
     * @property {number} _dz - Internal cache var.
     * @private
     */
    this._dz = 0;

    /**
     * @property {Array.<Phaser.Plugin.Isometric.Point3>} _corners - The 8 corners of the bounding cube.
     * @private
     */
    this._corners = [new Phaser.Plugin.Isometric.Point3(this.x, this.y, this.z),
        new Phaser.Plugin.Isometric.Point3(this.x, this.y, this.z + this.height),
        new Phaser.Plugin.Isometric.Point3(this.x, this.y + this.widthY, this.z),
        new Phaser.Plugin.Isometric.Point3(this.x, this.y + this.widthY, this.z + this.height),
        new Phaser.Plugin.Isometric.Point3(this.x + this.widthX, this.y, this.z),
        new Phaser.Plugin.Isometric.Point3(this.x + this.widthX, this.y, this.z + this.height),
        new Phaser.Plugin.Isometric.Point3(this.x + this.widthX, this.y + this.widthY, this.z),
        new Phaser.Plugin.Isometric.Point3(this.x + this.widthX, this.y + this.widthY, this.z + this.height)
    ];

};

Phaser.Plugin.Isometric.Body.prototype = {

    /**
     * Internal method.
     *
     * @method Phaser.Plugin.Isometric.Body#updateBounds
     * @protected
     */
    updateBounds: function () {

        var asx = Math.abs(this.sprite.scale.x);
        var asy = Math.abs(this.sprite.scale.y);

        if (asx !== this._sx || asy !== this._sy) {
            this.widthX = Math.ceil(this.sprite.width * 0.5);
            this.widthY = Math.ceil(this.sprite.width * 0.5);
            this.height = Math.ceil(this.sprite.height - (this.sprite.width * 0.5));
            this.halfWidthX = Math.floor(this.widthX * 2);
            this.halfWidthY = Math.floor(this.widthY * 2);
            this.halfHeight = Math.floor(this.height * 2);
            this._sx = asx;
            this._sy = asy;
            this.center.setTo(this.position.x + this.halfWidthX, this.position.y + this.halfWidthY, this.position.z + this.halfHeight);

            this._reset = true;
        }

    },

    /**
     * Internal method.
     *
     * @method Phaser.Plugin.Isometric.Body#preUpdate
     * @protected
     */
    preUpdate: function () {

        if (!this.enable) {
            return;
        }

        this.phase = 1;

        //  Store and reset collision flags
        this.wasTouching.none = this.touching.none;
        this.wasTouching.up = this.touching.up;
        this.wasTouching.down = this.touching.down;
        this.wasTouching.backX = this.touching.backX;
        this.wasTouching.backY = this.touching.backY;
        this.wasTouching.frontX = this.touching.frontX;
        this.wasTouching.frontY = this.touching.frontY;

        this.touching.none = true;
        this.touching.up = false;
        this.touching.down = false;
        this.touching.backX = false;
        this.touching.backY = false;
        this.touching.frontX = false;
        this.touching.frontY = false;

        this.blocked.up = false;
        this.blocked.down = false;
        this.blocked.backX = false;
        this.blocked.frontX = false;
        this.blocked.backY = false;
        this.blocked.backX = false;

        this.embedded = false;

        this.updateBounds();

        //  Working out how to incorporate anchors into this was... fun.
        this.position.x = this.sprite.isoX + ((this.widthX * -this.sprite.anchor.x) + this.widthX * 0.5) + this.offset.x;
        this.position.y = this.sprite.isoY + ((this.widthY * this.sprite.anchor.x) - this.widthY * 0.5) + this.offset.y;
        this.position.z = this.sprite.isoZ - (Math.abs(this.sprite.height) * (1 - this.sprite.anchor.y)) + (Math.abs(this.sprite.width * 0.5)) + this.offset.z;


        this.rotation = this.sprite.angle;

        this.preRotation = this.rotation;

        if (this._reset || this.sprite.fresh === true) {
            this.prev.x = this.position.x;
            this.prev.y = this.position.y;
            this.prev.z = this.position.z;
        }

        if (this.moves) {
            this.game.physics.isoArcade.updateMotion(this);

            this.newVelocity.set(this.velocity.x * this.game.time.physicsElapsed, this.velocity.y * this.game.time.physicsElapsed, this.velocity.z * this.game.time.physicsElapsed);

            this.position.x += this.newVelocity.x;
            this.position.y += this.newVelocity.y;
            this.position.z += this.newVelocity.z;

            if (this.position.x !== this.prev.x || this.position.y !== this.prev.y || this.position.z !== this.prev.z) {
                this.speed = Math.sqrt(this.velocity.x * this.velocity.x + this.velocity.y * this.velocity.y + this.velocity.z * this.velocity.z);
                this.angle = Math.atan2(this.velocity.y, this.velocity.x);
            }

            //  Now the State update will throw collision checks at the Body
            //  And finally we'll integrate the new position back to the Sprite in postUpdate

            if (this.collideWorldBounds) {
                this.checkWorldBounds();
            }

            if (this.sprite.outOfBoundsKill && !this.game.physics.isoArcade.bounds.intersects(this.sprite.isoBounds)) {
                this.sprite.kill();
            }
        }

        this._dx = this.deltaX();
        this._dy = this.deltaY();
        this._dz = this.deltaZ();

        this._reset = false;

    },

    /**
     * Internal method.
     *
     * @method Phaser.Plugin.Isometric.Body#postUpdate
     * @protected
     */
    postUpdate: function () {

        if (!this.enable) {
            return;
        }

        //  Only allow postUpdate to be called once per frame
        if (this.phase === 2) {
            return;
        }

        this.phase = 2;

        // stops sprites flying off if isoPosition is changed during update
        if (this._reset) {
            this.prev.x = this.position.x;
            this.prev.y = this.position.y;
            this.prev.z = this.position.z;
        }

        if (this.deltaAbsX() >= this.deltaAbsY() && this.deltaAbsX() >= this.deltaAbsZ()) {
            if (this.deltaX() < 0) {
                this.facing = Phaser.Plugin.Isometric.BACKWARDX;
            } else if (this.deltaX() > 0) {
                this.facing = Phaser.Plugin.Isometric.FORWARDX;
            }
        } else if (this.deltaAbsY() >= this.deltaAbsX() && this.deltaAbsY() >= this.deltaAbsZ()) {
            if (this.deltaY() < 0) {
                this.facing = Phaser.Plugin.Isometric.BACKWARDY;
            } else if (this.deltaY() > 0) {
                this.facing = Phaser.Plugin.Isometric.FORWARDY;
            }
        } else {
            if (this.deltaZ() < 0) {
                this.facing = Phaser.Plugin.Isometric.DOWN;
            } else if (this.deltaZ() > 0) {
                this.facing = Phaser.Plugin.Isometric.UP;
            }
        }

        if (this.moves) {
            this._dx = this.deltaX();
            this._dy = this.deltaY();
            this._dz = this.deltaZ();

            if (this.deltaMax.x !== 0 && this._dx !== 0) {
                if (this._dx < 0 && this._dx < -this.deltaMax.x) {
                    this._dx = -this.deltaMax.x;
                } else if (this._dx > 0 && this._dx > this.deltaMax.x) {
                    this._dx = this.deltaMax.x;
                }
            }

            if (this.deltaMax.y !== 0 && this._dy !== 0) {
                if (this._dy < 0 && this._dy < -this.deltaMax.y) {
                    this._dy = -this.deltaMax.y;
                } else if (this._dy > 0 && this._dy > this.deltaMax.y) {
                    this._dy = this.deltaMax.y;
                }
            }

            if (this.deltaMax.z !== 0 && this._dz !== 0) {
                if (this._dz < 0 && this._dz < -this.deltaMax.z) {
                    this._dz = -this.deltaMax.z;
                } else if (this._dz > 0 && this._dz > this.deltaMax.z) {
                    this._dz = this.deltaMax.z;
                }
            }

            this.sprite.isoX += this._dx;
            this.sprite.isoY += this._dy;
            this.sprite.isoZ += this._dz;
        }

        this.center.setTo(this.position.x + this.halfWidthX, this.position.y + this.halfWidthY, this.position.z + this.halfHeight);

        if (this.allowRotation) {
            this.sprite.angle += this.deltaR();
        }

        this.prev.x = this.position.x;
        this.prev.y = this.position.y;
        this.prev.z = this.position.z;

        this._reset = false;

    },

    /**
     * Removes this body's reference to its parent sprite, freeing it up for gc.
     *
     * @method Phaser.Plugin.Isometric.Body#destroy
     */
    destroy: function () {

        this.sprite = null;

    },

    /**
     * Internal method.
     *
     * @method Phaser.Plugin.Isometric.Body#checkWorldBounds
     * @protected
     */
    checkWorldBounds: function () {

        if (this.position.x < this.game.physics.isoArcade.bounds.x && this.game.physics.isoArcade.checkCollision.backX) {
            this.position.x = this.game.physics.isoArcade.bounds.x;
            this.velocity.x *= -this.bounce.x;
            this.blocked.backX = true;
        } else if (this.frontX > this.game.physics.isoArcade.bounds.frontX && this.game.physics.isoArcade.checkCollision.frontX) {
            this.position.x = this.game.physics.isoArcade.bounds.frontX - this.widthX;
            this.velocity.x *= -this.bounce.x;
            this.blocked.frontX = true;
        }

        if (this.position.y < this.game.physics.isoArcade.bounds.y && this.game.physics.isoArcade.checkCollision.backY) {
            this.position.y = this.game.physics.isoArcade.bounds.y;
            this.velocity.y *= -this.bounce.y;
            this.blocked.backY = true;
        } else if (this.frontY > this.game.physics.isoArcade.bounds.frontY && this.game.physics.isoArcade.checkCollision.frontY) {
            this.position.y = this.game.physics.isoArcade.bounds.frontY - this.widthY;
            this.velocity.y *= -this.bounce.y;
            this.blocked.frontY = true;
        }

        if (this.position.z < this.game.physics.isoArcade.bounds.z && this.game.physics.isoArcade.checkCollision.down) {
            this.position.z = this.game.physics.isoArcade.bounds.z;
            this.velocity.z *= -this.bounce.z;
            this.blocked.down = true;
        } else if (this.top > this.game.physics.isoArcade.bounds.top && this.game.physics.isoArcade.checkCollision.up) {
            this.position.z = this.game.physics.isoArcade.bounds.top - this.height;
            this.velocity.z *= -this.bounce.z;
            this.blocked.up = true;
        }

    },

    /**
     * You can modify the size of the physics Body to be any dimension you need.
     * So it could be smaller or larger than the parent Sprite. You can also control the x, y and z offset, which
     * is the position of the Body relative to the center of the Sprite.
     *
     * @method Phaser.Plugin.Isometric.Body#setSize
     * @param {number} widthX - The X width (breadth) of the Body.
     * @param {number} widthY - The Y width (depth) of the Body.
     * @param {number} height - The height of the Body.
     * @param {number} [offsetX] - The X offset of the Body from the Sprite position.
     * @param {number} [offsetY] - The Y offset of the Body from the Sprite position.
     * @param {number} [offsetY] - The Z offset of the Body from the Sprite position.
     */
    setSize: function (widthX, widthY, height, offsetX, offsetY, offsetZ) {

        if (typeof offsetX === 'undefined') {
            offsetX = this.offset.x;
        }
        if (typeof offsetY === 'undefined') {
            offsetY = this.offset.y;
        }
        if (typeof offsetZ === 'undefined') {
            offsetZ = this.offset.z;
        }

        this.sourceWidthX = widthX;
        this.sourceWidthY = widthY;
        this.sourceHeight = height;
        this.widthX = (this.sourceWidthX) * this._sx;
        this.widthY = (this.sourceWidthY) * this._sx;
        this.height = (this.sourceHeight) * this._sy;
        this.halfWidthX = Math.floor(this.widthX * 0.5);
        this.halfWidthY = Math.floor(this.widthY * 0.5);
        this.halfHeight = Math.floor(this.height * 0.5);
        this.offset.setTo(offsetX, offsetY, offsetZ);

        this.center.setTo(this.position.x + this.halfWidthX, this.position.y + this.halfWidthY, this.position.z + this.halfHeight);

    },

    /**
     * Resets all Body values (velocity, acceleration, rotation, etc)
     *
     * @method Phaser.Plugin.Isometric.Body#reset
     * @param {number} x - The new x position of the Body.
     * @param {number} y - The new y position of the Body.
     * @param {number} z - The new z position of the Body.
     */
    reset: function (x, y, z) {

        this.velocity.set(0);
        this.acceleration.set(0);

        this.angularVelocity = 0;
        this.angularAcceleration = 0;

        this.position.x = x + ((this.widthX * -this.sprite.anchor.x) + this.widthX * 0.5) + this.offset.x;
        this.position.y = y + ((this.widthY * this.sprite.anchor.x) - this.widthY * 0.5) + this.offset.y;
        this.position.z = z - (Math.abs(this.sprite.height) * (1 - this.sprite.anchor.y)) + (Math.abs(this.sprite.width * 0.5)) + this.offset.z;

        this.prev.x = this.position.x;
        this.prev.y = this.position.y;
        this.prev.z = this.position.z;

        this.rotation = this.sprite.angle;
        this.preRotation = this.rotation;

        this._sx = this.sprite.scale.x;
        this._sy = this.sprite.scale.y;

        this.center.setTo(this.position.x + this.halfWidthX, this.position.y + this.halfWidthY, this.position.z + this.halfHeight);

        this.sprite._isoPositionChanged = true;

    },

    /**
     * Tests if a world point lies within this Body.
     *
     * @method Phaser.Plugin.Isometric.Body#hitTest
     * @param {number} x - The world x coordinate to test.
     * @param {number} y - The world y coordinate to test.
     * @param {number} z - The world z coordinate to test.
     * @return {boolean} True if the given coordinates are inside this Body, otherwise false.
     */
    hitTest: function (x, y, z) {

        return Phaser.Plugin.Isometric.Cube.contains(this, x, y, z);

    },

    /**
     * Returns true if the bottom of this Body is in contact with either the world bounds.
     *
     * @method Phaser.Plugin.Isometric.Body#onFloor
     * @return {boolean} True if in contact with either the world bounds.
     */
    onFloor: function () {

        return this.blocked.down;

    },

    /**
     * Returns true if either side of this Body is in contact with either the world bounds.
     *
     * @method Phaser.Plugin.Isometric.Body#onWall
     * @return {boolean} True if in contact with world bounds.
     */
    onWall: function () {

        return (this.blocked.frontX || this.blocked.frontY || this.blocked.backX || this.blocked.backY);

    },

    /**
     * Returns the absolute delta x value.
     *
     * @method Phaser.Plugin.Isometric.Body#deltaAbsX
     * @return {number} The absolute delta value.
     */
    deltaAbsX: function () {

        return (this.deltaX() > 0 ? this.deltaX() : -this.deltaX());

    },

    /**
     * Returns the absolute delta y value.
     *
     * @method Phaser.Plugin.Isometric.Body#deltaAbsY
     * @return {number} The absolute delta value.
     */
    deltaAbsY: function () {

        return (this.deltaY() > 0 ? this.deltaY() : -this.deltaY());

    },

    /**
     * Returns the absolute delta z value.
     *
     * @method Phaser.Plugin.Isometric.Body#deltaAbsZ
     * @return {number} The absolute delta value.
     */
    deltaAbsZ: function () {

        return (this.deltaZ() > 0 ? this.deltaZ() : -this.deltaZ());

    },

    /**
     * Returns the delta x value. The difference between Body.x now and in the previous step.
     *
     * @method Phaser.Plugin.Isometric.Body#deltaX
     * @return {number} The delta value. Positive if the motion was to the right, negative if to the left.
     */
    deltaX: function () {

        return this.position.x - this.prev.x;

    },

    /**
     * Returns the delta y value. The difference between Body.y now and in the previous step.
     *
     * @method Phaser.Plugin.Isometric.Body#deltaY
     * @return {number} The delta value. Positive if the motion was downwards, negative if upwards.
     */
    deltaY: function () {

        return this.position.y - this.prev.y;

    },

    /**
     * Returns the delta z value. The difference between Body.z now and in the previous step.
     *
     * @method Phaser.Plugin.Isometric.Body#deltaZ
     * @return {number} The delta value. Positive if the motion was downwards, negative if upwards.
     */
    deltaZ: function () {

        return this.position.z - this.prev.z;

    },

    /**
     * Returns the delta r value. The difference between Body.rotation now and in the previous step.
     *
     * @method Phaser.Plugin.Isometric.Body#deltaR
     * @return {number} The delta value. Positive if the motion was clockwise, negative if anti-clockwise.
     */
    deltaR: function () {

        return this.rotation - this.preRotation;

    },

    /**
     * Returns the 8 corners that make up the body's bounding cube.
     *
     * @method Phaser.Plugin.Isometric.Body#getCorners
     * @return {Array.<Phaser.Plugin.Isometric.Point3>} An array of Phaser.Plugin.Isometric.Point3 values specifying each corner co-ordinate.
     */
    getCorners: function () {

        this._corners[0].setTo(this.x, this.y, this.z);
        this._corners[1].setTo(this.x, this.y, this.z + this.height);
        this._corners[2].setTo(this.x, this.y + this.widthY, this.z);
        this._corners[3].setTo(this.x, this.y + this.widthY, this.z + this.height);
        this._corners[4].setTo(this.x + this.widthX, this.y, this.z);
        this._corners[5].setTo(this.x + this.widthX, this.y, this.z + this.height);
        this._corners[6].setTo(this.x + this.widthX, this.y + this.widthY, this.z);
        this._corners[7].setTo(this.x + this.widthX, this.y + this.widthY, this.z + this.height);

        return this._corners;

    }
};

/**
 * @name Phaser.Plugin.Isometric.Body#top
 * @property {number} bottom - The top value of this Body (same as Body.z + Body.height)
 * @readonly
 */
Object.defineProperty(Phaser.Plugin.Isometric.Body.prototype, "top", {

    get: function () {
        return this.position.z + this.height;
    }

});

/**
 * @name Phaser.Plugin.Isometric.Body#frontX
 * @property {number} right - The front X value of this Body (same as Body.x + Body.widthX)
 * @readonly
 */
Object.defineProperty(Phaser.Plugin.Isometric.Body.prototype, "frontX", {

    get: function () {
        return this.position.x + this.widthX;
    }

});

/**
 * @name Phaser.Plugin.Isometric.Body#right
 * @property {number} right - The front X value of this Body (same as Body.x + Body.widthX) - alias used for QuadTree
 * @readonly
 */
Object.defineProperty(Phaser.Plugin.Isometric.Body.prototype, "right", {

    get: function () {
        return this.position.x + this.widthX;
    }

});

/**
 * @name Phaser.Plugin.Isometric.Body#frontY
 * @property {number} right - The front Y value of this Body (same as Body.y + Body.widthY)
 * @readonly
 */
Object.defineProperty(Phaser.Plugin.Isometric.Body.prototype, "frontY", {

    get: function () {
        return this.position.y + this.widthY;
    }

});

/**
 * @name Phaser.Plugin.Isometric.Body#bottom
 * @property {number} right - The front Y value of this Body (same as Body.y + Body.widthY) - alias used for QuadTree
 * @readonly
 */
Object.defineProperty(Phaser.Plugin.Isometric.Body.prototype, "bottom", {

    get: function () {
        return this.position.y + this.widthY;
    }

});


/**
 * @name Phaser.Plugin.Isometric.Body#x
 * @property {number} x - The x position.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Body.prototype, "x", {

    get: function () {
        return this.position.x;
    },

    set: function (value) {

        this.position.x = value;
    }

});

/**
 * @name Phaser.Plugin.Isometric.Body#y
 * @property {number} y - The y position.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Body.prototype, "y", {

    get: function () {
        return this.position.y;
    },

    set: function (value) {

        this.position.y = value;

    }

});

/**
 * @name Phaser.Plugin.Isometric.Body#z
 * @property {number} z - The z position.
 */
Object.defineProperty(Phaser.Plugin.Isometric.Body.prototype, "z", {

    get: function () {
        return this.position.z;
    },

    set: function (value) {

        this.position.z = value;

    }

});

/**
 * Render IsoSprite Body.
 *
 * @method Phaser.Plugin.Isometric.Body#render
 * @param {object} context - The context to render to.
 * @param {Phaser.Plugin.Isometric.Body} body - The Body to render the info of.
 * @param {string} [color='rgba(0,255,0,0.4)'] - color of the debug info to be rendered. (format is css color string).
 * @param {boolean} [filled=true] - Render the objected as a filled (default, true) or a stroked (false)
 */
Phaser.Plugin.Isometric.Body.render = function (context, body, color, filled) {

    if (typeof filled === 'undefined') {
        filled = true;
    }

    color = color || 'rgba(0,255,0,0.4)';

    var points = [],
        corners = body.getCorners();

    var posX = -body.sprite.game.camera.x;
    var posY = -body.sprite.game.camera.y;

    if (filled) {
        points = [corners[1], corners[3], corners[2], corners[6], corners[4], corners[5], corners[1]];

        points = points.map(function (p) {
            var newPos = body.sprite.game.iso.project(p);
            newPos.x += posX;
            newPos.y += posY;
            return newPos;
        });
        context.beginPath();
        context.fillStyle = color;
        context.moveTo(points[0].x, points[0].y);

        for (var i = 1; i < points.length; i++) {
            context.lineTo(points[i].x, points[i].y);
        }
        context.fill();
    } else {
        points = corners.slice(0, corners.length);
        points = points.map(function (p) {
            var newPos = body.sprite.game.iso.project(p);
            newPos.x += posX;
            newPos.y += posY;
            return newPos;
        });

        context.moveTo(points[0].x, points[0].y);
        context.beginPath();
        context.strokeStyle = color;

        context.lineTo(points[1].x, points[1].y);
        context.lineTo(points[3].x, points[3].y);
        context.lineTo(points[2].x, points[2].y);
        context.lineTo(points[6].x, points[6].y);
        context.lineTo(points[4].x, points[4].y);
        context.lineTo(points[5].x, points[5].y);
        context.lineTo(points[1].x, points[1].y);
        context.lineTo(points[0].x, points[0].y);
        context.lineTo(points[4].x, points[4].y);
        context.moveTo(points[0].x, points[0].y);
        context.lineTo(points[2].x, points[2].y);
        context.moveTo(points[3].x, points[3].y);
        context.lineTo(points[7].x, points[7].y);
        context.lineTo(points[6].x, points[6].y);
        context.moveTo(points[7].x, points[7].y);
        context.lineTo(points[5].x, points[5].y);
        context.stroke();
        context.closePath();
    }

};

/**
 * Render IsoSprite Body Physics Data as text.
 *
 * @method Phaser.Plugin.Isometric.Body#renderBodyInfo
 * @param {Phaser.Plugin.Isometric.Body} body - The Body to render the info of.
 * @param {number} x - X position of the debug info to be rendered.
 * @param {number} y - Y position of the debug info to be rendered.
 * @param {string} [color='rgb(255,255,255)'] - color of the debug info to be rendered. (format is css color string).
 */
Phaser.Plugin.Isometric.Body.renderBodyInfo = function (debug, body) {

    debug.line('x: ' + body.x.toFixed(2), 'y: ' + body.y.toFixed(2), 'z: ' + body.z.toFixed(2), 'widthX: ' + body.widthX, 'widthY: ' + body.widthY, 'height: ' + body.height);
    debug.line('velocity x: ' + body.velocity.x.toFixed(2), 'y: ' + body.velocity.y.toFixed(2), 'z: ' + body.velocity.z.toFixed(2), 'deltaX: ' + body._dx.toFixed(2), 'deltaY: ' + body._dy.toFixed(2), 'deltaZ: ' + body._dz.toFixed(2));
    debug.line('acceleration x: ' + body.acceleration.x.toFixed(2), 'y: ' + body.acceleration.y.toFixed(2), 'z: ' + body.acceleration.z.toFixed(2), 'speed: ' + body.speed.toFixed(2), 'angle: ' + body.angle.toFixed(2));
    debug.line('gravity x: ' + body.gravity.x, 'y: ' + body.gravity.y, 'z: ' + body.gravity.z);
    debug.line('bounce x: ' + body.bounce.x.toFixed(2), 'y: ' + body.bounce.y.toFixed(2), 'z: ' + body.bounce.z.toFixed(2));
    debug.line('touching: ', 'frontX: ' + (body.touching.frontX ? 1 : 0) + ' frontY: ' + (body.touching.frontY ? 1 : 0) + ' backX: ' + (body.touching.backX ? 1 : 0) + ' backY: ' + (body.touching.backY ? 1 : 0) + ' up: ' + (body.touching.up ? 1 : 0) + ' down: ' + (body.touching.down ? 1 : 0));
    debug.line('blocked: ', 'frontX: ' + (body.blocked.frontX ? 1 : 0) + ' frontY: ' + (body.blocked.frontY ? 1 : 0) + ' backX: ' + (body.blocked.backX ? 1 : 0) + ' backY: ' + (body.blocked.backY ? 1 : 0) + ' up: ' + (body.blocked.up ? 1 : 0) + ' down: ' + (body.blocked.down ? 1 : 0));

};

Phaser.Plugin.Isometric.Body.prototype.constructor = Phaser.Plugin.Isometric.Body;

Phaser.Utils.Debug.prototype.body = (function (_super) {

    return function (sprite, color, filled, depth) {
        if (sprite.body && sprite.body.type === Phaser.Plugin.Isometric.ISOARCADE) {
            this.start();
            Phaser.Plugin.Isometric.Body.render(this.context, sprite.body, color, filled);
            if (depth) {
                this.text(sprite.depth.toFixed(2), sprite.x, sprite.y, color, '12px Courier');
            }
            this.stop();
        }

        return _super.call(this, sprite, color, filled);
    };

})(Phaser.Utils.Debug.prototype.body);

Phaser.Utils.Debug.prototype.bodyInfo = (function (_super) {

    return function (sprite, x, y, color) {
        if (sprite.body && sprite.body.type === Phaser.Plugin.Isometric.ISOARCADE) {
            this.start(x, y, color, 210);
            Phaser.Plugin.Isometric.Body.renderBodyInfo(this, sprite.body);
            this.stop();
        }

        return _super.call(this, sprite, x, y, color);
    };

})(Phaser.Utils.Debug.prototype.bodyInfo);
;/**
 * IsoArcade Physics constructor.
 *
 * @class Phaser.Plugin.Isometric.Arcade
 * @classdesc IsoArcade Physics Constructor
 * @constructor
 * @param {Phaser.Game} game reference to the current game instance.
 */
Phaser.Plugin.Isometric.Arcade = function (game) {

    /**
     * @property {Phaser.Game} game - Local reference to game.
     */
    this.game = game;

    /**
     * @property {Phaser.Plugin.Isometric.Point3} gravity - The World gravity setting. Defaults to x: 0, y: 0, z: 0 or no gravity.
     */
    this.gravity = new Phaser.Plugin.Isometric.Point3();

    /**
     * @property {Phaser.Plugin.Isometric.Cube} bounds - The bounds inside of which the physics world exists. Defaults to match the world bounds relatively closely given the isometric projection.
     */
    this.bounds = new Phaser.Plugin.Isometric.Cube(0, 0, 0, game.world.width * 0.5, game.world.width * 0.5, game.world.height);

    /**
     * Set the checkCollision properties to control for which bounds collision is processed.
     * For example checkCollision.down = false means Bodies cannot collide with the World.bounds.bottom.
     * @property {object} checkCollision - An object containing allowed collision flags.
     */
    this.checkCollision = {
        up: true,
        down: true,
        frontX: true,
        frontY: true,
        backX: true,
        backY: true
    };

    /**
     * @property {number} maxObjects - Used by the QuadTree/Octree to set the maximum number of objects per quad.
     */
    this.maxObjects = 10;

    /**
     * @property {number} maxLevels - Used by the QuadTree/Octree to set the maximum number of iteration levels.
     */
    this.maxLevels = 4;

    /**
     * @property {number} OVERLAP_BIAS - A value added to the delta values during collision checks.
     */
    this.OVERLAP_BIAS = 4;

    /**
     * @property {boolean} forceX - If true World.separate will always separate on the X and Y axes before Z. Otherwise it will check gravity totals first.
     */
    this.forceXY = false;

    /**
     * @property {boolean} skipTree - If true an Octree/QuadTree will never be used for any collision. Handy for tightly packed games. See also Body.skipTree.
     */
    this.skipTree = false;

    /**
     * @property {boolean} useQuadTree - If true, the collision/overlap routines will use a QuadTree, which will ignore the z position of objects when determining potential collisions. This will be faster if you don't do a lot of stuff on the z-axis.
     */
    this.useQuadTree = false;

    /**
     * @property {Phaser.QuadTree} quadTree - The world QuadTree.
     */
    this.quadTree = new Phaser.QuadTree(this.bounds.x, this.bounds.y, this.bounds.widthX, this.bounds.widthY, this.maxObjects, this.maxLevels);

    /**
     * @property {Phaser.Plugin.Isometric.Octree} octree - The world Octree.
     */
    this.octree = new Phaser.Plugin.Isometric.Octree(this.bounds.x, this.bounds.y, this.bounds.z, this.bounds.widthX, this.bounds.widthY, this.bounds.height, this.maxObjects, this.maxLevels);

    //  Avoid gc spikes by caching these values for re-use

    /**
     * @property {number} _overlap - Internal cache var.
     * @private
     */
    this._overlap = 0;

    /**
     * @property {number} _maxOverlap - Internal cache var.
     * @private
     */
    this._maxOverlap = 0;

    /**
     * @property {number} _velocity1 - Internal cache var.
     * @private
     */
    this._velocity1 = 0;

    /**
     * @property {number} _velocity2 - Internal cache var.
     * @private
     */
    this._velocity2 = 0;

    /**
     * @property {number} _newVelocity1 - Internal cache var.
     * @private
     */
    this._newVelocity1 = 0;

    /**
     * @property {number} _newVelocity2 - Internal cache var.
     * @private
     */
    this._newVelocity2 = 0;

    /**
     * @property {number} _average - Internal cache var.
     * @private
     */
    this._average = 0;

    /**
     * @property {Array} _mapData - Internal cache var.
     * @private
     */
    this._mapData = [];

    /**
     * @property {boolean} _result - Internal cache var.
     * @private
     */
    this._result = false;

    /**
     * @property {number} _total - Internal cache var.
     * @private
     */
    this._total = 0;

    /**
     * @property {number} _angle - Internal cache var.
     * @private
     */
    this._angle = 0;

    /**
     * @property {number} _dx - Internal cache var.
     * @private
     */
    this._dx = 0;

    /**
     * @property {number} _dy - Internal cache var.
     * @private
     */
    this._dy = 0;

    /**
     * @property {number} _dz - Internal cache var.
     * @private
     */
    this._dz = 0;

};

Phaser.Plugin.Isometric.Arcade.prototype.constructor = Phaser.Plugin.Isometric.Arcade;

Phaser.Plugin.Isometric.Arcade.prototype = {

    /**
     * Updates the size of this physics world.
     *
     * @method Phaser.Plugin.Isometric.Arcade#setBounds
     * @param {number} x - Bottom rear most corner of the world.
     * @param {number} y - Bottom rear most corner of the world.
     * @param {number} z - Bottom rear most corner of the world.
     * @param {number} widthX - New X width (breadth) of the world. Can never be smaller than the Game.width.
     * @param {number} widthY - New Y width (depth) of the world. Can never be smaller than the Game.width.
     * @param {number} height - New height of the world. Can never be smaller than the Game.height.
     */
    setBounds: function (x, y, z, widthX, widthY, height) {

        this.bounds.setTo(x, y, z, widthX, widthY, height);

    },

    /**
     * Updates the size of this physics world to match the size of the game world.
     *
     * @method Phaser.Plugin.Isometric.Arcade#setBoundsToWorld
     */
    setBoundsToWorld: function () {

        this.bounds.setTo(0, 0, 0, this.game.world.width * 0.5, this.game.world.width * 0.5, this.game.world.height);

    },

    /**
     * This will create an IsoArcade Physics body on the given game object or array of game objects.
     * A game object can only have 1 physics body active at any one time, and it can't be changed until the object is destroyed.
     *
     * @method Phaser.Plugin.Isometric.Arcade#enable
     * @param {object|array|Phaser.Group} object - The game object to create the physics body on. Can also be an array or Group of objects, a body will be created on every child that has a `body` property.
     * @param {boolean} [children=true] - Should a body be created on all children of this object? If true it will recurse down the display list as far as it can go.
     */
    enable: function (object, children) {

        if (typeof children === 'undefined') {
            children = true;
        }

        var i = 1;

        if (Array.isArray(object)) {
            i = object.length;

            while (i--) {
                if (object[i] instanceof Phaser.Group) {
                    //  If it's a Group then we do it on the children regardless
                    this.enable(object[i].children, children);
                } else {
                    this.enableBody(object[i]);

                    if (children && object[i].hasOwnProperty('children') && object[i].children.length > 0) {
                        this.enable(object[i], true);
                    }
                }
            }
        } else {
            if (object instanceof Phaser.Group) {
                //  If it's a Group then we do it on the children regardless
                this.enable(object.children, children);
            } else {
                this.enableBody(object);

                if (children && object.hasOwnProperty('children') && object.children.length > 0) {
                    this.enable(object.children, true);
                }
            }
        }

    },

    /**
     * Creates an IsoArcade Physics body on the given game object.
     * A game object can only have 1 physics body active at any one time, and it can't be changed until the body is nulled.
     *
     * @method Phaser.Plugin.Isometric.Arcade#enableBody
     * @param {object} object - The game object to create the physics body on. A body will only be created if this object has a null `body` property.
     */
    enableBody: function (object) {

        if (object.hasOwnProperty('body') && object.body === null) {
            object.body = new Phaser.Plugin.Isometric.Body(object);
        }

    },

    /**
     * Called automatically by a Physics body, it updates all motion related values on the Body.
     *
     * @method Phaser.Plugin.Isometric.Arcade#updateMotion
     * @param {Phaser.Plugin.Isometric.Body} body - The Body object to be updated.
     */
    updateMotion: function (body) {

        this._velocityDelta = this.computeVelocity(0, body, body.angularVelocity, body.angularAcceleration, body.angularDrag, body.maxAngular) - body.angularVelocity;
        body.angularVelocity += this._velocityDelta;
        body.rotation += (body.angularVelocity * this.game.time.physicsElapsed);

        body.velocity.x = this.computeVelocity(1, body, body.velocity.x, body.acceleration.x, body.drag.x, body.maxVelocity.x);
        body.velocity.y = this.computeVelocity(2, body, body.velocity.y, body.acceleration.y, body.drag.y, body.maxVelocity.y);
        body.velocity.z = this.computeVelocity(3, body, body.velocity.z, body.acceleration.z, body.drag.z, body.maxVelocity.z);

    },

    /**
     * A tween-like function that takes a starting velocity and some other factors and returns an altered velocity.
     * Based on a function in Flixel by @ADAMATOMIC
     *
     * @method Phaser.Plugin.Isometric.Arcade#computeVelocity
     * @param {number} axis - 0 for nothing, 1 for X-axis, 2 for Y-axis, 3 for vertical (Z-axis).
     * @param {Phaser.Plugin.Isometric.Body} body - The Body object to be updated.
     * @param {number} velocity - Any component of velocity (e.g. 20).
     * @param {number} acceleration - Rate at which the velocity is changing.
     * @param {number} drag - Really kind of a deceleration, this is how much the velocity changes if Acceleration is not set.
     * @param {number} [max=10000] - An absolute value cap for the velocity.
     * @return {number} The altered Velocity value.
     */
    computeVelocity: function (axis, body, velocity, acceleration, drag, max) {

        max = max || 10000;

        if (axis === 1 && body.allowGravity) {
            velocity += (this.gravity.x + body.gravity.x) * this.game.time.physicsElapsed;
        } else if (axis === 2 && body.allowGravity) {
            velocity += (this.gravity.y + body.gravity.y) * this.game.time.physicsElapsed;
        } else if (axis === 3 && body.allowGravity) {
            velocity += (this.gravity.z + body.gravity.z) * this.game.time.physicsElapsed;
        }

        if (acceleration) {
            velocity += acceleration * this.game.time.physicsElapsed;
        } else if (drag) {
            this._drag = drag * this.game.time.physicsElapsed;

            if (velocity - this._drag > 0) {
                velocity -= this._drag;
            } else if (velocity + this._drag < 0) {
                velocity += this._drag;
            } else {
                velocity = 0;
            }
        }

        if (velocity > max) {
            velocity = max;
        } else if (velocity < -max) {
            velocity = -max;
        }

        return velocity;

    },

    /**
     * Checks for overlaps between two game objects. The objects can be IsoSprites or Groups.
     * You can perform IsoSprite vs. IsoSprite, IsoSprite vs. Group and Group vs. Group overlap checks.
     * Unlike collide the objects are NOT automatically separated or have any physics applied, they merely test for overlap results.
     * The second parameter can be an array of objects, of differing types.
     * NOTE: This function is not recursive, and will not test against children of objects passed (i.e. Groups within Groups).
     *
     * @method Phaser.Plugin.Isometric.Arcade#overlap
     * @param {Phaser.Plugin.Isometric.IsoSprite|Phaser.Group} object1 - The first object to check. Can be an instance of Phaser.Plugin.Isometric.IsoSprite or Phaser.Group.
     * @param {Phaser.Plugin.Isometric.IsoSprite|Phaser.Group|array} object2 - The second object or array of objects to check. Can be Phaser.Plugin.Isometric.IsoSprite or Phaser.Group.
     * @param {function} [overlapCallback=null] - An optional callback function that is called if the objects overlap. The two objects will be passed to this function in the same order in which you specified them.
     * @param {function} [processCallback=null] - A callback function that lets you perform additional checks against the two objects if they overlap. If this is set then overlapCallback will only be called if processCallback returns true.
     * @param {object} [callbackContext] - The context in which to run the callbacks.
     * @return {boolean} True if an overlap occured otherwise false.
     */
    overlap: function (object1, object2, overlapCallback, processCallback, callbackContext) {

        overlapCallback = overlapCallback || null;
        processCallback = processCallback || null;
        callbackContext = callbackContext || overlapCallback;

        this._result = false;
        this._total = 0;

        if (Array.isArray(object2)) {
            for (var i = 0, len = object2.length; i < len; i++) {
                this.collideHandler(object1, object2[i], overlapCallback, processCallback, callbackContext, true);
            }
        } else {
            this.collideHandler(object1, object2, overlapCallback, processCallback, callbackContext, true);
        }

        return (this._total > 0);

    },

    /**
     * Checks for collision between two game objects. You can perform IsoSprite vs. IsoSprite, IsoSprite vs. Group or Group vs. Group collisions.
     * The second parameter can be an array of objects, of differing types.
     * The objects are also automatically separated. If you don't require separation then use IsoArcade.overlap instead.
     * An optional processCallback can be provided. If given this function will be called when two sprites are found to be colliding. It is called before any separation takes place,
     * giving you the chance to perform additional checks. If the function returns true then the collision and separation is carried out. If it returns false it is skipped.
     * The collideCallback is an optional function that is only called if two sprites collide. If a processCallback has been set then it needs to return true for collideCallback to be called.
     * NOTE: This function is not recursive, and will not test against children of objects passed (i.e. Groups within Groups).
     *
     * @method Phaser.Plugin.Isometric.Arcade#collide
     * @param {Phaser.Plugin.Isometric.IsoSprite|Phaser.Group} object1 - The first object to check. Can be an instance of Phaser.Plugin.Isometric.IsoSprite or Phaser.Group.
     * @param {Phaser.Plugin.Isometric.IsoSprite|Phaser.Group|array} object2 - The second object or array of objects to check. Can be Phaser.Plugin.Isometric.IsoSprite or Phaser.Group.
     * @param {function} [collideCallback=null] - An optional callback function that is called if the objects collide. The two objects will be passed to this function in the same order in which you specified them, unless you are colliding Group vs. Sprite, in which case Sprite will always be the first parameter.
     * @param {function} [processCallback=null] - A callback function that lets you perform additional checks against the two objects if they overlap. If this is set then collision will only happen if processCallback returns true. The two objects will be passed to this function in the same order in which you specified them.
     * @param {object} [callbackContext] - The context in which to run the callbacks.
     * @return {boolean} True if a collision occured otherwise false.
     */
    collide: function (object1, object2, collideCallback, processCallback, callbackContext) {

        collideCallback = collideCallback || null;
        processCallback = processCallback || null;
        callbackContext = callbackContext || collideCallback;

        this._result = false;
        this._total = 0;

        if (Array.isArray(object2)) {
            for (var i = 0, len = object2.length; i < len; i++) {
                this.collideHandler(object1, object2[i], collideCallback, processCallback, callbackContext, false);
            }
        }
        else {
            this.collideHandler(object1, object2, collideCallback, processCallback, callbackContext, false);
        }

        return (this._total > 0);

    },

    /**
     * Internal collision handler.
     *
     * @method Phaser.Plugin.Isometric.Arcade#collideHandler
     * @private
     * @param {Phaser.Plugin.Isometric.IsoSprite|Phaser.Group} object1 - The first object to check. Can be an instance of Phaser.Plugin.Isometric.IsoSprite or Phaser.Group.
     * @param {Phaser.Plugin.Isometric.IsoSprite|Phaser.Group} object2 - The second object to check. Can be an instance of Phaser.Plugin.Isometric.IsoSprite or Phaser.Group. Can also be an array of objects to check.
     * @param {function} collideCallback - An optional callback function that is called if the objects collide. The two objects will be passed to this function in the same order in which you specified them.
     * @param {function} processCallback - A callback function that lets you perform additional checks against the two objects if they overlap. If this is set then collision will only happen if processCallback returns true. The two objects will be passed to this function in the same order in which you specified them.
     * @param {object} callbackContext - The context in which to run the callbacks.
     * @param {boolean} overlapOnly - Just run an overlap or a full collision.
     */
    collideHandler: function (object1, object2, collideCallback, processCallback, callbackContext, overlapOnly) {

        //  Only collide valid objects
        if (!object2 && object1.type === Phaser.GROUP) {
            this.collideGroupVsSelf(object1, collideCallback, processCallback, callbackContext, overlapOnly);
            return;
        }

        if (object1 && object2 && object1.exists && object2.exists) {
            //  ISOSPRITES
            if (object1.type === Phaser.Plugin.Isometric.ISOSPRITE) {
                if (object2.type === Phaser.Plugin.Isometric.ISOSPRITE) {
                    this.collideSpriteVsSprite(object1, object2, collideCallback, processCallback, callbackContext, overlapOnly);
                } else if (object2.type === Phaser.GROUP) {
                    this.collideSpriteVsGroup(object1, object2, collideCallback, processCallback, callbackContext, overlapOnly);
                }
            }
            //  GROUPS
            else if (object1.type === Phaser.GROUP) {
                if (object2.type === Phaser.Plugin.Isometric.ISOSPRITE) {
                    this.collideSpriteVsGroup(object2, object1, collideCallback, processCallback, callbackContext, overlapOnly);
                } else if (object2.type === Phaser.GROUP) {
                    this.collideGroupVsGroup(object1, object2, collideCallback, processCallback, callbackContext, overlapOnly);
                }
            }
        }

    },

    /**
     * An internal function. Use Phaser.Plugin.Isometric.Arcade.collide instead.
     *
     * @method Phaser.Plugin.Isometric.Arcade#collideSpriteVsSprite
     * @private
     * @param {Phaser.Plugin.Isometric.IsoSprite} sprite1 - The first sprite to check.
     * @param {Phaser.Plugin.Isometric.IsoSprite} sprite2 - The second sprite to check.
     * @param {function} collideCallback - An optional callback function that is called if the objects collide. The two objects will be passed to this function in the same order in which you specified them.
     * @param {function} processCallback - A callback function that lets you perform additional checks against the two objects if they overlap. If this is set then collision will only happen if processCallback returns true. The two objects will be passed to this function in the same order in which you specified them.
     * @param {object} callbackContext - The context in which to run the callbacks.
     * @param {boolean} overlapOnly - Just run an overlap or a full collision.
     * @return {boolean} True if there was a collision, otherwise false.
     */
    collideSpriteVsSprite: function (sprite1, sprite2, collideCallback, processCallback, callbackContext, overlapOnly) {

        if (!sprite1.body || !sprite2.body) {
            return false;
        }

        if (this.separate(sprite1.body, sprite2.body, processCallback, callbackContext, overlapOnly)) {
            if (collideCallback) {
                collideCallback.call(callbackContext, sprite1, sprite2);
            }

            this._total++;
        }

        return true;

    },

    /**
     * An internal function. Use Phaser.Plugin.Isometric.Arcade.collide instead.
     *
     * @method Phaser.Plugin.Isometric.Arcade#collideSpriteVsGroup
     * @private
     * @param {Phaser.Plugin.Isometric.IsoSprite} sprite - The sprite to check.
     * @param {Phaser.Group} group - The Group to check.
     * @param {function} collideCallback - An optional callback function that is called if the objects collide. The two objects will be passed to this function in the same order in which you specified them.
     * @param {function} processCallback - A callback function that lets you perform additional checks against the two objects if they overlap. If this is set then collision will only happen if processCallback returns true. The two objects will be passed to this function in the same order in which you specified them.
     * @param {object} callbackContext - The context in which to run the callbacks.
     * @param {boolean} overlapOnly - Just run an overlap or a full collision.
     */
    collideSpriteVsGroup: function (sprite, group, collideCallback, processCallback, callbackContext, overlapOnly) {
        var i, len;

        if (group.length === 0 || !sprite.body) {
            return;
        }

        if (sprite.body.skipTree || this.skipTree) {
            for (i = 0, len = group.children.length; i < len; i++) {
                if (group.children[i] && group.children[i].exists) {
                    this.collideSpriteVsSprite(sprite, group.children[i], collideCallback, processCallback, callbackContext, overlapOnly);
                }
            }
        } else {
            if (this.useQuadTree) {
                //  What is the sprite colliding with in the quadTree?
                this.quadTree.clear();

                this.quadTree.reset(this.bounds.x, this.bounds.y, this.bounds.widthX, this.bounds.widthY, this.maxObjects, this.maxLevels);

                this.quadTree.populate(group);

                this._potentials = this.quadTree.retrieve(sprite);
            } else {
                //  What is the sprite colliding with in the octree?
                this.octree.clear();

                this.octree.reset(this.bounds.x, this.bounds.y, this.bounds.z, this.bounds.widthX, this.bounds.widthY, this.bounds.height, this.maxObjects, this.maxLevels);

                this.octree.populate(group);

                this._potentials = this.octree.retrieve(sprite);
            }

            for (i = 0, len = this._potentials.length; i < len; i++) {
                //  We have our potential suspects, are they in this group?
                if (this.separate(sprite.body, this._potentials[i], processCallback, callbackContext, overlapOnly)) {
                    if (collideCallback) {
                        collideCallback.call(callbackContext, sprite, this._potentials[i].sprite);
                    }

                    this._total++;
                }
            }
        }
    },

    /**
     * An internal function. Use Phaser.Plugin.Isometric.Arcade.collide instead.
     *
     * @method Phaser.Plugin.Isometric.Arcade#collideGroupVsSelf
     * @private
     * @param {Phaser.Group} group - The Group to check.
     * @param {function} collideCallback - An optional callback function that is called if the objects collide. The two objects will be passed to this function in the same order in which you specified them.
     * @param {function} processCallback - A callback function that lets you perform additional checks against the two objects if they overlap. If this is set then collision will only happen if processCallback returns true. The two objects will be passed to this function in the same order in which you specified them.
     * @param {object} callbackContext - The context in which to run the callbacks.
     * @param {boolean} overlapOnly - Just run an overlap or a full collision.
     * @return {boolean} True if there was a collision, otherwise false.
     */
    collideGroupVsSelf: function (group, collideCallback, processCallback, callbackContext, overlapOnly) {

        if (group.length === 0) {
            return;
        }

        var len = group.children.length;

        for (var i = 0; i < len; i++) {
            for (var j = i + 1; j <= len; j++) {
                if (group.children[i] && group.children[j] && group.children[i].exists && group.children[j].exists) {
                    this.collideSpriteVsSprite(group.children[i], group.children[j], collideCallback, processCallback, callbackContext, overlapOnly);
                }
            }
        }

    },

    /**
     * An internal function. Use Phaser.Plugin.Isometric.Arcade.collide instead.
     *
     * @method Phaser.Plugin.Isometric.Arcade#collideGroupVsGroup
     * @private
     * @param {Phaser.Group} group1 - The first Group to check.
     * @param {Phaser.Group} group2 - The second Group to check.
     * @param {function} collideCallback - An optional callback function that is called if the objects collide. The two objects will be passed to this function in the same order in which you specified them.
     * @param {function} processCallback - A callback function that lets you perform additional checks against the two objects if they overlap. If this is set then collision will only happen if processCallback returns true. The two objects will be passed to this function in the same order in which you specified them.
     * @param {object} callbackContext - The context in which to run the callbacks.
     * @param {boolean} overlapOnly - Just run an overlap or a full collision.
     */
    collideGroupVsGroup: function (group1, group2, collideCallback, processCallback, callbackContext, overlapOnly) {

        if (group1.length === 0 || group2.length === 0) {
            return;
        }

        for (var i = 0, len = group1.children.length; i < len; i++) {
            if (group1.children[i].exists) {
                this.collideSpriteVsGroup(group1.children[i], group2, collideCallback, processCallback, callbackContext, overlapOnly);
            }
        }

    },

    /**
     * The core separation function to separate two physics bodies.
     *
     * @private
     * @method Phaser.Plugin.Isometric.Arcade#separate
     * @param {Phaser.Plugin.Isometric.Body} body1 - The first Body object to separate.
     * @param {Phaser.Plugin.Isometric.Body} body2 - The second Body object to separate.
     * @param {function} [processCallback=null] - A callback function that lets you perform additional checks against the two objects if they overlap. If this function is set then the sprites will only be collided if it returns true.
     * @param {object} [callbackContext] - The context in which to run the process callback.
     * @param {boolean} overlapOnly - Just run an overlap or a full collision.
     * @return {boolean} Returns true if the bodies collided, otherwise false.
     */
    separate: function (body1, body2, processCallback, callbackContext, overlapOnly) {

        if (!body1.enable || !body2.enable || !this.intersects(body1, body2)) {
            return false;
        }

        //  They overlap. Is there a custom process callback? If it returns true then we can carry on, otherwise we should abort.
        if (processCallback && processCallback.call(callbackContext, body1.sprite, body2.sprite) === false) {
            return false;
        }

        if (overlapOnly) {
            //  We already know they intersect from the check above, and we don't need separation, so ...
            return true;
        }

        //  Do we separate on X and Y first?
        //  If we weren't having to carry around so much legacy baggage with us, we could do this properly. But alas ...
        if (this.forceXY || Math.abs(this.gravity.z + body1.gravity.z) < Math.abs(this.gravity.x + body1.gravity.x) || Math.abs(this.gravity.z + body1.gravity.z) < Math.abs(this.gravity.y + body1.gravity.y)) {
            this._result = (this.separateX(body1, body2, overlapOnly) || this.separateY(body1, body2, overlapOnly) || this.separateZ(body1, body2, overlapOnly));
        } else {
            this._result = (this.separateZ(body1, body2, overlapOnly) || this.separateX(body1, body2, overlapOnly) || this.separateY(body1, body2, overlapOnly));
        }

        return this._result;

    },

    /**
     * Check for intersection against two bodies.
     *
     * @method Phaser.Plugin.Isometric.Arcade#intersects
     * @param {Phaser.Plugin.Isometric.Body} body1 - The Body object to check.
     * @param {Phaser.Plugin.Isometric.Body} body2 - The Body object to check.
     * @return {boolean} True if they intersect, otherwise false.
     */
    intersects: function (body1, body2) {

        if (body1.frontX <= body2.x) {
            return false;
        }

        if (body1.frontY <= body2.y) {
            return false;
        }

        if (body1.x >= body2.frontX) {
            return false;
        }

        if (body1.y >= body2.frontY) {
            return false;
        }

        if (body1.top <= body2.z) {
            return false;
        }

        if (body1.z >= body2.top) {
            return false;
        }

        return true;

    },

    /**
     * The core separation function to separate two physics bodies on the x axis.
     *
     * @private
     * @method Phaser.Plugin.Isometric.Arcade#separateX
     * @param {Phaser.Plugin.Isometric.Body} body1 - The Body object to separate.
     * @param {Phaser.Plugin.Isometric.Body} body2 - The Body object to separate.
     * @param {boolean} overlapOnly - If true the bodies will only have their overlap data set, no separation or exchange of velocity will take place.
     * @return {boolean} Returns true if the bodies were separated, otherwise false.
     */
    separateX: function (body1, body2, overlapOnly) {

        //  Can't separate two immovable bodies
        if (body1.immovable && body2.immovable) {
            return false;
        }

        this._overlap = 0;

        //  Check if the hulls actually overlap
        if (this.intersects(body1, body2)) {
            this._maxOverlap = body1.deltaAbsX() + body2.deltaAbsX() + this.OVERLAP_BIAS;

            if (body1.deltaX() === 0 && body2.deltaX() === 0) {
                //  They overlap but neither of them are moving
                body1.embedded = true;
                body2.embedded = true;
            } else if (body1.deltaX() > body2.deltaX()) {
                //  Body1 is moving forward and/or Body2 is moving back
                this._overlap = body1.frontX - body2.x;

                if ((this._overlap > this._maxOverlap) || body1.checkCollision.frontX === false || body2.checkCollision.backX === false) {
                    this._overlap = 0;
                } else {
                    body1.touching.none = false;
                    body1.touching.frontX = true;
                    body2.touching.none = false;
                    body2.touching.backX = true;
                }
            } else if (body1.deltaX() < body2.deltaX()) {
                //  Body1 is moving back and/or Body2 is moving forward
                this._overlap = body1.x - body2.widthX - body2.x;

                if ((-this._overlap > this._maxOverlap) || body1.checkCollision.backX === false || body2.checkCollision.frontX === false) {
                    this._overlap = 0;
                } else {
                    body1.touching.none = false;
                    body1.touching.backX = true;
                    body2.touching.none = false;
                    body2.touching.frontX = true;
                }
            }

            //  Then adjust their positions and velocities accordingly (if there was any overlap)
            if (this._overlap !== 0) {
                body1.overlapX = this._overlap;
                body2.overlapX = this._overlap;

                if (overlapOnly || body1.customSeparateX || body2.customSeparateX) {
                    return true;
                }

                this._velocity1 = body1.velocity.x;
                this._velocity2 = body2.velocity.x;

                if (!body1.immovable && !body2.immovable) {
                    this._overlap *= 0.5;

                    body1.x = body1.x - this._overlap;
                    body2.x += this._overlap;

                    this._newVelocity1 = Math.sqrt((this._velocity2 * this._velocity2 * body2.mass) / body1.mass) * ((this._velocity2 > 0) ? 1 : -1);
                    this._newVelocity2 = Math.sqrt((this._velocity1 * this._velocity1 * body1.mass) / body2.mass) * ((this._velocity1 > 0) ? 1 : -1);
                    this._average = (this._newVelocity1 + this._newVelocity2) * 0.5;
                    this._newVelocity1 -= this._average;
                    this._newVelocity2 -= this._average;

                    body1.velocity.x = this._average + this._newVelocity1 * body1.bounce.x;
                    body2.velocity.x = this._average + this._newVelocity2 * body2.bounce.x;
                } else if (!body1.immovable) {
                    body1.x = body1.x - this._overlap;
                    body1.velocity.x = this._velocity2 - this._velocity1 * body1.bounce.x;
                } else if (!body2.immovable) {
                    body2.x += this._overlap;
                    body2.velocity.x = this._velocity1 - this._velocity2 * body2.bounce.x;
                }

                return true;
            }
        }

        return false;

    },

    /**
     * The core separation function to separate two physics bodies on the x axis.
     *
     * @private
     * @method Phaser.Plugin.Isometric.Arcade#separateY
     * @param {Phaser.Plugin.Isometric.Body} body1 - The Body object to separate.
     * @param {Phaser.Plugin.Isometric.Body} body2 - The Body object to separate.
     * @param {boolean} overlapOnly - If true the bodies will only have their overlap data set, no separation or exchange of velocity will take place.
     * @return {boolean} Returns true if the bodies were separated, otherwise false.
     */
    separateY: function (body1, body2, overlapOnly) {

        //  Can't separate two immovable bodies
        if (body1.immovable && body2.immovable) {
            return false;
        }

        this._overlap = 0;

        //  Check if the hulls actually overlap
        if (this.intersects(body1, body2)) {
            this._maxOverlap = body1.deltaAbsY() + body2.deltaAbsY() + this.OVERLAP_BIAS;

            if (body1.deltaY() === 0 && body2.deltaY() === 0) {
                //  They overlap but neither of them are moving
                body1.embedded = true;
                body2.embedded = true;
            } else if (body1.deltaY() > body2.deltaY()) {
                //  Body1 is moving forward and/or Body2 is moving back
                this._overlap = body1.frontY - body2.y;

                if ((this._overlap > this._maxOverlap) || body1.checkCollision.frontY === false || body2.checkCollision.backY === false) {
                    this._overlap = 0;
                } else {
                    body1.touching.none = false;
                    body1.touching.frontY = true;
                    body2.touching.none = false;
                    body2.touching.backY = true;
                }
            } else if (body1.deltaY() < body2.deltaY()) {
                //  Body1 is moving back and/or Body2 is moving forward
                this._overlap = body1.y - body2.widthY - body2.y;

                if ((-this._overlap > this._maxOverlap) || body1.checkCollision.backY === false || body2.checkCollision.frontY === false) {
                    this._overlap = 0;
                } else {
                    body1.touching.none = false;
                    body1.touching.backY = true;
                    body2.touching.none = false;
                    body2.touching.frontY = true;
                }
            }

            //  Then adjust their positions and velocities accordingly (if there was any overlap)
            if (this._overlap !== 0) {
                body1.overlapY = this._overlap;
                body2.overlapY = this._overlap;

                if (overlapOnly || body1.customSeparateY || body2.customSeparateY) {
                    return true;
                }

                this._velocity1 = body1.velocity.y;
                this._velocity2 = body2.velocity.y;

                if (!body1.immovable && !body2.immovable) {
                    this._overlap *= 0.5;

                    body1.y = body1.y - this._overlap;
                    body2.y += this._overlap;

                    this._newVelocity1 = Math.sqrt((this._velocity2 * this._velocity2 * body2.mass) / body1.mass) * ((this._velocity2 > 0) ? 1 : -1);
                    this._newVelocity2 = Math.sqrt((this._velocity1 * this._velocity1 * body1.mass) / body2.mass) * ((this._velocity1 > 0) ? 1 : -1);
                    this._average = (this._newVelocity1 + this._newVelocity2) * 0.5;
                    this._newVelocity1 -= this._average;
                    this._newVelocity2 -= this._average;

                    body1.velocity.y = this._average + this._newVelocity1 * body1.bounce.y;
                    body2.velocity.y = this._average + this._newVelocity2 * body2.bounce.y;
                } else if (!body1.immovable) {
                    body1.y = body1.y - this._overlap;
                    body1.velocity.y = this._velocity2 - this._velocity1 * body1.bounce.y;
                } else if (!body2.immovable) {
                    body2.y += this._overlap;
                    body2.velocity.y = this._velocity1 - this._velocity2 * body2.bounce.y;
                }

                return true;
            }
        }

        return false;

    },

    /**
     * The core separation function to separate two physics bodies on the z axis.
     *
     * @private
     * @method Phaser.Plugin.Isometric.Arcade#separateZ
     * @param {Phaser.Plugin.Isometric.Body} body1 - The Body object to separate.
     * @param {Phaser.Plugin.Isometric.Body} body2 - The Body object to separate.
     * @param {boolean} overlapOnly - If true the bodies will only have their overlap data set, no separation or exchange of velocity will take place.
     * @return {boolean} Returns true if the bodies were separated, otherwise false.
     */
    separateZ: function (body1, body2, overlapOnly) {

        //  Can't separate two immovable or non-existing bodys
        if (body1.immovable && body2.immovable) {
            return false;
        }

        this._overlap = 0;

        //  Check if the hulls actually overlap
        if (this.intersects(body1, body2)) {
            this._maxOverlap = body1.deltaAbsZ() + body2.deltaAbsZ() + this.OVERLAP_BIAS;

            if (body1.deltaZ() === 0 && body2.deltaZ() === 0) {
                //  They overlap but neither of them are moving
                body1.embedded = true;
                body2.embedded = true;
            } else if (body1.deltaZ() > body2.deltaZ()) {
                //  Body1 is moving down and/or Body2 is moving up
                this._overlap = body1.top - body2.z;

                if ((this._overlap > this._maxOverlap) || body1.checkCollision.down === false || body2.checkCollision.up === false) {
                    this._overlap = 0;
                } else {
                    body1.touching.none = false;
                    body1.touching.down = true;
                    body2.touching.none = false;
                    body2.touching.up = true;
                }
            } else if (body1.deltaZ() < body2.deltaZ()) {
                //  Body1 is moving up and/or Body2 is moving down
                this._overlap = body1.z - body2.top;

                if ((-this._overlap > this._maxOverlap) || body1.checkCollision.up === false || body2.checkCollision.down === false) {
                    this._overlap = 0;
                } else {
                    body1.touching.none = false;
                    body1.touching.up = true;
                    body2.touching.none = false;
                    body2.touching.down = true;
                }
            }

            //  Then adjust their positions and velocities accordingly (if there was any overlap)
            if (this._overlap !== 0) {
                body1.overlapZ = this._overlap;
                body2.overlapZ = this._overlap;

                if (overlapOnly || body1.customSeparateY || body2.customSeparateZ) {
                    return true;
                }

                this._velocity1 = body1.velocity.z;
                this._velocity2 = body2.velocity.z;

                if (!body1.immovable && !body2.immovable) {
                    this._overlap *= 0.5;

                    body1.z = body1.z - this._overlap;
                    body2.z += this._overlap;

                    this._newVelocity1 = Math.sqrt((this._velocity2 * this._velocity2 * body2.mass) / body1.mass) * ((this._velocity2 > 0) ? 1 : -1);
                    this._newVelocity2 = Math.sqrt((this._velocity1 * this._velocity1 * body1.mass) / body2.mass) * ((this._velocity1 > 0) ? 1 : -1);
                    this._average = (this._newVelocity1 + this._newVelocity2) * 0.5;
                    this._newVelocity1 -= this._average;
                    this._newVelocity2 -= this._average;

                    body1.velocity.z = this._average + this._newVelocity1 * body1.bounce.z;
                    body2.velocity.z = this._average + this._newVelocity2 * body2.bounce.z;
                } else if (!body1.immovable) {
                    body1.z = body1.z - this._overlap;
                    body1.velocity.z = this._velocity2 - this._velocity1 * body1.bounce.z;

                    //  This is special case code that handles things like moving platforms you can ride
                    if (body2.moves) {
                        body1.x += body2.x - body2.prev.x;
                        body1.y += body2.y - body2.prev.y;
                    }
                } else if (!body2.immovable) {
                    body2.z += this._overlap;
                    body2.velocity.z = this._velocity1 - this._velocity2 * body2.bounce.z;

                    //  This is special case code that handles things like moving platforms you can ride
                    if (body1.moves) {
                        body2.x += body1.x - body1.prev.x;
                        body2.y += body1.y - body1.prev.y;
                    }
                }

                return true;
            }

        }

        return false;

    },

    /**
     * Find the distance between two display objects (like Sprites).
     *
     * @method Phaser.Plugin.Isometric.Isometric.Arcade#distanceBetween
     * @param {any} source - The Display Object to test from.
     * @param {any} target - The Display Object to test to.
     * @return {number} The distance between the source and target objects.
     */
    distanceBetween: function (source, target) {

        this._dx = source.x - target.x;
        this._dy = source.y - target.y;
        this._dz = source.z - target.z;

        return Math.sqrt(this._dx * this._dx + this._dy * this._dy + this._dz * this._dz);

    },

    /**
     * Find the distance between a display object (like a Sprite) and the given x/y coordinates only (ignore z).
     * The calculation is made from the display objects x/y coordinate. This may be the top-left if its anchor hasn't been changed.
     * If you need to calculate from the center of a display object instead use the method distanceBetweenCenters()
     *
     * @method Phaser.Plugin.Isometric.Arcade#distanceToXY
     * @param {any} displayObject - The Display Object to test from.
     * @param {number} x - The x coordinate to test to.
     * @param {number} y - The y coordinate to test to.
     * @return {number} The distance between the object and the x/y coordinates.
     */
    distanceToXY: function (displayObject, x, y) {

        this._dx = displayObject.x - x;
        this._dy = displayObject.y - y;

        return Math.sqrt(this._dx * this._dx + this._dy * this._dy);

    },

    /**
     * Find the distance between a display object (like a Sprite) and the given x/y/z coordinates.
     * The calculation is made from the display objects x/y/z coordinate. This may be the top-left if its anchor hasn't been changed.
     * If you need to calculate from the center of a display object instead use the method distanceBetweenCenters()
     *
     * @method Phaser.Plugin.Isometric.Arcade#distanceToXYZ
     * @param {any} displayObjectBody - The Display Object to test from.
     * @param {number} x - The x coordinate to test to.
     * @param {number} y - The y coordinate to test to.
     * @param {number} z - The y coordinate to test to
     * @return {number} The distance between the object and the x/y coordinates.
     */
    distanceToXYZ: function (displayObjectBody, x, y, z) {

        this._dx = displayObjectBody.x - x;
        this._dy = displayObjectBody.y - y;
        this._dz = displayObjectBody.z - z;

        return Math.sqrt(this._dx * this._dx + this._dy * this._dy + this._dz * this._dz);

    },

    /**
     * Find the distance between a display object (like a Sprite) and a Pointer. If no Pointer is given the Input.activePointer is used.
     * The calculation is made from the display objects x/y coordinate. This may be the top-left if its anchor hasn't been changed.
     * If you need to calculate from the center of a display object instead use the method distanceBetweenCenters()
     * The distance to the Pointer is returned in isometric distance.
     *
     * @method Phaser.Physics.Arcade#distanceToPointer
     * @param {any} displayObjectBody - The Display Object to test from.
     * @param {Phaser.Pointer} [pointer] - The Phaser.Pointer to test to. If none is given then Input.activePointer is used.
     * @return {number} The distance between the object and the Pointer.
     */
    distanceToPointer: function (displayObjectBody, pointer) {

        pointer = pointer || this.game.input.activePointer;
        var isoPointer = this.game.iso.unproject(pointer.position, undefined, displayObjectBody.z);
        isoPointer.z = displayObjectBody.z;
        var a = this.anglesToXYZ(displayObjectBody, isoPointer.x, isoPointer.y, isoPointer.z);

        return a.r;

    },

    /**
     * Find the angles in radians between a display object (like a IsoSprite) and the given x/y/z coordinate.
     *
     * @method Phaser.Physics.Isometric.Isometric.Arcade#anglesToXYZ
     * @param {any} displayObjectBody - The Display Object to test from.
     * @param {number} x - The x coordinate to get the angle to.
     * @param {number} y - The y coordinate to get the angle to.
     * @param {number} z - The z coordinate to get the angle to.
     * @return {number} The angle in radians between displayObjectBody.x/y to Pointer.x/y
     */
    anglesToXYZ: function (displayObjectBody, x, y, z) {

        // Spherical polar coordinates
        var r = this.distanceToXYZ(displayObjectBody, x, y, z);
        var theta = Math.atan2(y - displayObjectBody.y, x - displayObjectBody.x);
        var phi = Math.acos((z - displayObjectBody.z) / r);

        return {r: r, theta: theta, phi: phi};

    },

    /**
     * Find the angle in radians between a display object (like a Sprite) and a Pointer, taking their x/y and center into account.
     * This is not the visual angle but the angle in the isometric co-ordinate system.
     *
     * @method Phaser.Physics.Isometric.Arcade#angleToPointer
     * @param {any} displayObjectBody - The Display Object to test from.
     * @param {Phaser.Pointer} [pointer] - The Phaser.Pointer to test to. If none is given then Input.activePointer is used.
     * @return {number} The (isometric) angle in radians between displayObjectBody.x/y to Pointer.x/y.
     */
    angleToPointer: function (displayObjectBody, pointer) {

        pointer = pointer || this.game.input.activePointer;
        var isoPointer = this.game.iso.unproject(pointer.position, undefined, displayObjectBody.z);
        isoPointer.z = displayObjectBody.z;
        var a = this.anglesToXYZ(displayObjectBody, isoPointer.x, isoPointer.y, isoPointer.z);

        return a.theta;

    },

    /**
     * Given the angle (in degrees) and speed calculate the velocity and return it as a Point object, or set it to the given point object.
     * One way to use this is: velocityFromAngle(angle, 200, sprite.velocity) which will set the values directly to the sprites velocity and not create a new Point object.
     *
     * @method Phaser.Physics.Arcade#velocityFromAngle
     * @param {number} theta - The angle in radians for x,y in the isometric co-ordinate system
     * @param {number} [phi=Math.PI/2] - The angle in radians for z in the isometric co-ordinate system
     * @param {number} [speed=60] - The speed it will move, in pixels per second sq.
     * @param {Phaser.Point|object} [point] - The Point object in which the x and y properties will be set to the calculated velocity.
     * @return {Phaser.Plugin.Isometric.Point3} - A Point where point.x contains the velocity x value and so on for y and z.
     */
    velocityFromAngles: function (theta, phi, speed, point) {

        if (phi === undefined) {
            phi = Math.sin(Math.PI / 2);
        }
        if (speed === undefined) {
            speed = 60;
        }
        point = point || new Phaser.Point();

        return new Phaser.Plugin.Isometric.Point3(
            Math.cos(theta) * Math.sin(phi) * speed,
            Math.sin(theta) * Math.sin(phi) * speed,
            Math.cos(phi) * speed
        );

    },

    /**
     * Sets the acceleration.x/y property on the display object so it will move towards the x/y coordinates at the given speed (in pixels per second sq.)
     * You must give a maximum speed value, beyond which the display object won't go any faster.
     * Note: The display object does not continuously track the target. If the target changes location during transit the display object will not modify its course.
     * Note: The display object doesn't stop moving once it reaches the destination coordinates.
     *
     * @method Phaser.Physics.Isometric.Arcade#accelerateToXYZ
     * @param {any} displayObject - The display object to move.
     * @param {number} x - The x coordinate to accelerate towards.
     * @param {number} y - The y coordinate to accelerate towards.
     * @param {number} z - The z coordinate to accelerate towards.
     * @param {number} [speed=60] - The speed it will accelerate in pixels per second.
     * @param {number} [xSpeedMax=500] - The maximum x velocity the display object can reach.
     * @param {number} [ySpeedMax=500] - The maximum y velocity the display object can reach.
     * @param {number} [zSpeedMax=500] - The maximum z velocity the display object can reach.
     * @return {number} The angle (in radians).
     */
    accelerateToXYZ: function (displayObject, x, y, z, speed, xSpeedMax, ySpeedMax, zSpeedMax) {

        if (speed === undefined) {
            speed = 60;
        }
        if (xSpeedMax === undefined) {
            xSpeedMax = 500;
        }
        if (ySpeedMax === undefined) {
            ySpeedMax = 500;
        }
        if (zSpeedMax === undefined) {
            zSpeedMax = 500;
        }

        var a = this.anglesToXYZ(displayObject.body, x, y, z);
        var v = this.velocityFromAngles(a.theta, a.phi, speed);

        displayObject.body.acceleration.setTo(v.x, v.y, v.z);
        displayObject.body.maxVelocity.setTo(xSpeedMax, ySpeedMax, zSpeedMax);

        return a.theta;

    },

    /**
     * Move the given display object towards the x/y coordinates at a steady velocity.
     * If you specify a maxTime then it will adjust the speed (over-writing what you set) so it arrives at the destination in that number of seconds.
     * Timings are approximate due to the way browser timers work. Allow for a variance of +- 50ms.
     * Note: The display object does not continuously track the target. If the target changes location during transit the display object will not modify its course.
     * Note: The display object doesn't stop moving once it reaches the destination coordinates.
     * Note: Doesn't take into account acceleration, maxVelocity or drag (if you've set drag or acceleration too high this object may not move at all)
     *
     * @method Phaser.Physics.Isometric.Arcade#moveToXYZ
     * @param {any} displayObject - The display object to move, must have an isoArcade body.
     * @param {number} x - The x coordinate to move towards.
     * @param {number} y - The y coordinate to move towards.
     * @param {number} z - The z coordinate to move towards.
     * @param {number} [speed=60] - The speed it will move, in pixels per second (default is 60 pixels/sec)
     * @param {number} [maxTime=0] - Time given in milliseconds (1000 = 1 sec). If set the speed is adjusted so the object will arrive at destination in the given number of ms.
     * @return {number} The angle (in radians).
     */
    moveToXYZ: function (displayObject, x, y, z, speed, maxTime) {

        if (typeof speed === 'undefined') {
            speed = 60;
        }
        if (typeof maxTime === 'undefined') {
            maxTime = 0;
        }

        if (maxTime > 0) {
            //  We know how many pixels we need to move, but how fast?
            speed = this.distanceToXYZ(displayObject.body, x, y, z) / (maxTime / 1000);
        }
        var a = this.anglesToXYZ(displayObject.body, x, y, z);
        var v = this.velocityFromAngles(a.theta, a.phi, speed);

        displayObject.body.velocity.copyFrom(v);

        return a.theta;

    },

    /**
     * Move the given display object towards the destination object at a steady velocity.
     * If you specify a maxTime then it will adjust the speed (overwriting what you set) so it arrives at the destination in that number of seconds.
     * Timings are approximate due to the way browser timers work. Allow for a variance of +- 50ms.
     * Note: The display object does not continuously track the target. If the target changes location during transit the display object will not modify its course.
     * Note: The display object doesn't stop moving once it reaches the destination coordinates.
     * Note: Doesn't take into account acceleration, maxVelocity or drag (if you've set drag or acceleration too high this object may not move at all)
     *
     * @method Phaser.Physics.Isometric.Arcade#moveToObject
     * @param {any} displayObject - The display object to move.
     * @param {any} destination - The display object to move towards. Can be any object but must have visible x/y/z properties.
     * @param {number} [speed=60] - The speed it will move, in pixels per second (default is 60 pixels/sec)
     * @param {number} [maxTime=0] - Time given in milliseconds (1000 = 1 sec). If set the speed is adjusted so the object will arrive at destination in the given number of ms.
     * @return {number} The angle (in radians).
     */
    moveToObject: function (displayObject, destination, speed, maxTime) {

        return this.moveToXYZ(displayObject, destination.x, destination.y, destination.z, speed, maxTime);

    },

    /**
     * Move the given display object towards the pointer at a steady x & y velocity. If no pointer is given it will use Phaser.Input.activePointer.
     * If you specify a maxTime then it will adjust the speed (over-writing what you set) so it arrives at the destination in that number of seconds.
     * Timings are approximate due to the way browser timers work. Allow for a variance of +- 50ms.
     * Note: The display object does not continuously track the target. If the target changes location during transit the display object will not modify its course.
     * Note: The display object doesn't stop moving once it reaches the destination coordinates.
     *
     * @method Phaser.Physics.Isometric.Arcade#moveToPointer
     * @param {any} displayObject - The display object to move.
     * @param {number} [speed=60] - The speed it will move, in pixels per second (default is 60 pixels/sec)
     * @param {Phaser.Pointer} [pointer] - The pointer to move towards. Defaults to Phaser.Input.activePointer.
     * @param {number} [maxTime=0] - Time given in milliseconds (1000 = 1 sec). If set the speed is adjusted so the object will arrive at destination in the given number of ms.
     * @return {number} The angle (in radians).
     */
    moveToPointer: function (displayObject, speed, pointer, maxTime) {

        pointer = pointer || this.game.input.activePointer;
        var isoPointer = this.game.iso.unproject(pointer.position, undefined, displayObject.body.z);
        isoPointer.z = displayObject.body.z;

        if (typeof speed === 'undefined') {
            speed = 60;
        }
        if (typeof maxTime === 'undefined') {
            maxTime = 0;
        }

        if (maxTime > 0) {
            //  We know how many pixels we need to move, but how fast?
            speed = this.distanceToXYZ(displayObject.body, isoPointer.x, isoPointer.y, isoPointer.z) / (maxTime / 1000);
        }
        var a = this.anglesToXYZ(displayObject.body, isoPointer.x, isoPointer.y, isoPointer.z);
        var v = this.velocityFromAngles(a.theta, a.phi, speed);

        displayObject.body.velocity.x = v.x;
        displayObject.body.velocity.y = v.y;

        return a.theta;
    }


};

Phaser.Physics.prototype.isoArcade = null;

Phaser.Physics.prototype.parseConfig = (function (_super) {

    return function () {
        if (this.config.hasOwnProperty('isoArcade') && this.config['isoArcade'] === true && Phaser.Plugin.Isometric.hasOwnProperty('IsoArcade')) {
            this.isoArcade = new Phaser.Plugin.Isometric(this.game, this.config);
        }
        return _super.call(this);
    };

})(Phaser.Physics.prototype.parseConfig);

Phaser.Physics.prototype.startSystem = (function (_super) {

    return function (system) {
        if (system === Phaser.Plugin.Isometric.ISOARCADE && this.isoArcade === null) {
            this.isoArcade = new Phaser.Plugin.Isometric.Arcade(this.game);
            this.setBoundsToWorld();
        }
        return _super.call(this, system);
    };

})(Phaser.Physics.prototype.startSystem);

Phaser.Physics.prototype.enable = (function (_super) {

    return function (sprite, system) {
        if (system === Phaser.Plugin.Isometric.ISOARCADE && this.isoArcade) {
            this.isoArcade.enable(sprite);
        }
        return _super.call(this, sprite, system);
    };

})(Phaser.Physics.prototype.enable);

Phaser.Physics.prototype.setBoundsToWorld = (function (_super) {

    return function () {
        if (this.isoArcade) {
            this.isoArcade.setBoundsToWorld();
        }
        return _super.call(this);
    };

})(Phaser.Physics.prototype.setBoundsToWorld);

Phaser.Physics.prototype.destroy = (function (_super) {

    return function () {
        this.isoArcade = null;

        return _super.call(this);
    };

})(Phaser.Physics.prototype.destroy);

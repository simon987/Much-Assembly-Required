//todo pull this off the server or something?
let defaultText =
    " _______                    __     __\n" +
    "|   _   |.-----.---.-.----.|  |--.|__|.----.-----.----.-----.\n" +
    "|       ||  _  |  _  |  __||     ||  ||  __|  _  |   _|  _  |\n" +
    "|___|___||   __|___._|____||__|__||__||____|_____|__| |   __|\n" +
    "         |__|                                         |__|\n" +
    "\n" +
    "Version 1.5A, 1985-05-17\n" +
    "Initialising Universal Communication Port connection...Done\n" +
    "Current date is 2790-04-28\n" +
    "Cubot Status: Much Assembly Required";

enum ConsoleMode {
    CLEAR,
    NORMAL
}

interface ConsoleScreen {

    toggleColor(self: ConsoleScreen): void;

    toggleScrolling(self: ConsoleScreen): void;

    reset(self: ConsoleScreen): void;

    handleConsoleBufferUpdate(consoleBuffer: string[], mode: ConsoleMode): void;
}

class PlainTextConsoleMode {

    public width: number;
    public dialImage: string;


    constructor(lineWidth: number, dialImage: string) {
        this.width = lineWidth;
        this.dialImage = dialImage;
    }
}

class PlainTextConsole implements ConsoleScreen {

    private txtDiv: HTMLElement;
    private colorButton: HTMLButtonElement;
    private scrollButton: HTMLButtonElement;
    private resetButton: HTMLButtonElement;

    private colorToggled: boolean = false;
    public autoScroll: boolean = false;

    private modes: PlainTextConsoleMode[] = [];
    private mode: number;

    /**
     * Contents of the
     */
    private consoleText: string;

    /**
     * Length of the last line
     * @type {number}
     */
    private lastLineLength: number = 0;

    constructor(text: string, id: string, colorId: string, scrollId: string, resetID: string) {
        this.txtDiv = document.getElementById(id);
        this.colorButton = document.getElementById(colorId) as HTMLButtonElement;
        this.scrollButton = document.getElementById(scrollId) as HTMLButtonElement;
        this.resetButton = document.getElementById(resetID) as HTMLButtonElement;

        let self = this;
        this.colorButton.onclick = function () {
            self.toggleColor(self)
        };
        this.scrollButton.onclick = function () {
            self.toggleScrolling(self)
        };
        this.resetButton.onclick = function () {
            self.reset(self);
        };

        this.txtDiv.innerHTML = text;
        this.consoleText = text;

        //Line width modes. Might break if shorter than CubotComPort::MESSAGE_LENGTH
        this.modes.push(new PlainTextConsoleMode(16, "./images/knob-170.png"));
        this.modes.push(new PlainTextConsoleMode(24, "./images/knob-123.png"));
        this.modes.push(new PlainTextConsoleMode(40, "./images/knob-90.png"));
        this.modes.push(new PlainTextConsoleMode(56, "./images/knob-65.png"));
        this.modes.push(new PlainTextConsoleMode(64, "./images/knob-10.png"));
        this.mode = 3; //Mode 56
    }

    /**
     * Toggle dark/light theme
     */
    public toggleColor(self: PlainTextConsole): void {

        if (self.colorToggled) {
            self.colorToggled = false;
            self.colorButton.classList.remove("btn-info");
            self.colorButton.classList.add("btn-outline-info");

            self.txtDiv.classList.remove("ctr-selection-inverted");
            self.txtDiv.classList.remove("ctr-text-inverted");
            self.txtDiv.classList.add("ctr-selection");
            self.txtDiv.classList.add("ctr-text");

        } else {
            self.colorToggled = true;
            self.colorButton.classList.remove("btn-outline-info");
            self.colorButton.classList.add("btn-info");

            self.txtDiv.classList.add("ctr-selection-inverted");
            self.txtDiv.classList.add("ctr-text-inverted");
            self.txtDiv.classList.remove("ctr-selection");
            self.txtDiv.classList.remove("ctr-text");
        }
    }

    /**
     * Toggle auto scrolling. Also initially scrolls to bottom on click
     */
    public toggleScrolling(self: PlainTextConsole): void {

        if (self.autoScroll) {

            self.autoScroll = false;
            self.scrollButton.classList.add("btn-outline-info");
            self.scrollButton.classList.remove("btn-info");

        } else {
            self.autoScroll = true;
            self.scrollButton.classList.add("btn-info");
            self.scrollButton.classList.remove("btn-outline-info");

            //Scroll to bottom
            self.txtDiv.scrollTop = self.txtDiv.scrollHeight;
        }

    }

    /**
     * Clears the console screen
     */
    public reset(self: PlainTextConsole): void {

        self.txtDiv.innerHTML = "";
        self.consoleText = "";
        self.lastLineLength = 0;
    }

    public setMode(mode: number) {
        this.mode = mode;
    }

    /**
     * Handles a consoleBuffer update
     * @param {string[]} consoleBuffer A Cubot's internal buffer, as an array of messages
     * @param {ConsoleMode} mode mode
     */
    handleConsoleBufferUpdate(consoleBuffer: string[], mode: ConsoleMode): void {

        //Reset console screen before writing to it (if requested by ComPort)
        if (mode == ConsoleMode.CLEAR) {
            this.reset(this);
        }

        //For each MESSAGE-LENGTH - length message
        for (let i = 0; i < consoleBuffer.length; i++) {

            //Zero-terminate the message
            let zeroIndex = consoleBuffer[i].indexOf("\0");
            let message = consoleBuffer[i].substring(0, zeroIndex == -1 ? undefined : zeroIndex);

            for (let j = 0; j < message.length; j++) {

                if (message[j] == "\n") {

                    this.consoleText += "\n";
                    this.lastLineLength = 0;

                } else {

                    if (this.lastLineLength < this.modes[this.mode].width) {
                        this.consoleText += message[j];
                        this.lastLineLength++;
                    } else {
                        this.consoleText += "\n";
                        this.consoleText += message[j];
                        this.lastLineLength = 1;
                    }
                }
            }
        }

        this.txtDiv.innerText = this.consoleText;

        //Scroll to bottom is autoScroll switch is flipped
        if (this.autoScroll) {
            this.txtDiv.scrollTop = this.txtDiv.scrollHeight;
        }

    }
}



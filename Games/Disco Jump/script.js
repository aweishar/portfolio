document.addEventListener('DOMContentLoaded', () => {
    // --- DOM References ---
    const gameArea = document.getElementById('game-area');
    const playerElement = document.getElementById('player');
    const scoreDisplay = document.getElementById('score');
    const hiScoreDisplay = document.getElementById('hiScore');
    const messageDisplay = document.getElementById('message-display');

    // --- Game Constants ---
    const PREF_W = 320;
    const PREF_H = 480;

    // --- Asset Loading (Simplified for DOM) ---
    const assetPaths = {
        idle: 'url(./assets/kirbyidle.gif)',
        jumping: 'url(./assets/kirbyjump.gif)',
        falling: 'url(./assets/kirbyfall.gif)',
        walking: 'url(./assets/kirbywalk.gif)'
    };

    // --- Class Definitions ---

    class Animation {
        constructor() {
            this.frameWidth = 22;
            this.frameCount = 0;
            this.currentFrameIndex = 0;
            this.startTime = 0;
            this.delay = -1;
        }
        setAnimation(imageURL, frameCount, delay) {
            if (playerElement.style.backgroundImage !== imageURL) {
                playerElement.style.backgroundImage = imageURL;
            }
            this.frameCount = frameCount;
            this.delay = delay;
            if (this.currentFrameIndex >= frameCount) {
                this.currentFrameIndex = 0;
            }
            this.startTime = performance.now();
        }
        update() {
            if (this.delay === -1 || this.frameCount <= 1) {
                playerElement.style.backgroundPosition = '0px 0px';
                return;
            };
            if (performance.now() - this.startTime > this.delay) {
                this.currentFrameIndex = (this.currentFrameIndex + 1) % this.frameCount;
                const newXPosition = -(this.currentFrameIndex * this.frameWidth + this.currentFrameIndex);
                playerElement.style.backgroundPosition = `${newXPosition}px 0px`;
                this.startTime = performance.now();
            }
        }
    }

    class Platforms {
        constructor() {
            this.platforms = [];
        }
        clear() {
            this.platforms.forEach(p => gameArea.removeChild(p.element));
            this.platforms = [];
        }
        setLevel1() {
            this.clear();
            const platformW = 50;
            const platformH = 10;
            const numPlatforms = 10;
            const spacing = 60;
            for (let i = 0; i < numPlatforms; i++) {
                this.addPlatform(Math.random() * (PREF_W - platformW), PREF_H - i * spacing, platformW, platformH);
            }
            this.addPlatform(PREF_W / 2 - platformW / 2, PREF_H - 60, platformW, platformH);
        }
        addPlatform(x, y, width, height) {
            const el = document.createElement('div');
            el.className = 'platform';
            el.style.left = `${x}px`;
            el.style.top = `${y}px`;
            el.style.width = `${width}px`;
            el.style.height = `${height}px`;
            gameArea.appendChild(el);
            this.platforms.push({ x, y, width, height, element: el });
        }
        updateAndDraw() {
            for (const p of this.platforms) {
                if (p.y > PREF_H) {
                    p.y = -20;
                    p.x = Math.random() * (PREF_W - p.width);
                }
                p.element.style.top = `${p.y}px`;
                p.element.style.left = `${p.x}px`;
                const r = Math.floor(Math.random() * 256);
                const g = Math.floor(Math.random() * 256);
                const b = Math.floor(Math.random() * 256);
                p.element.style.backgroundColor = `rgb(${r},${g},${b})`;
            }
        }
    }

    class Sprite {
        constructor(platforms) {
            this.platforms = platforms;
            this.width = 22; this.height = 22;
            this.x = 0; this.y = 0; this.dx = 0; this.dy = 0;
            this.left = false; this.right = false; this.falling = false;
            this.moveSpeed = 0.5; this.stopSpeed = 0.5; this.maxSpeed = 2.0;
            this.jumpSpeed = -10.0; this.gravity = 0.25; this.maxFallingSpeed = 12.0;
            this.facingLeft = false;
            this.animation = new Animation();
        }
        update() {
            if (this.left) {
                this.dx -= this.moveSpeed;
                if (this.dx < -this.maxSpeed) this.dx = -this.maxSpeed;
            } else if (this.right) {
                this.dx += this.moveSpeed;
                if (this.dx > this.maxSpeed) this.dx = this.maxSpeed;
            } else {
                if (this.dx > 0) {
                    this.dx -= this.stopSpeed; if(this.dx < 0) this.dx = 0;
                } else if (this.dx < 0) {
                    this.dx += this.stopSpeed; if(this.dx > 0) this.dx = 0;
                }
            }
            this.x += this.dx;
            if (this.x > PREF_W) this.x = 0;
            if (this.x < 0) this.x = PREF_W;

            if (this.falling) {
                this.dy += this.gravity;
                if (this.dy > this.maxFallingSpeed) this.dy = this.maxFallingSpeed;
            }
            this.calculateY();

            if (this.dy < 0) {
                this.animation.setAnimation(assetPaths.jumping, 1, -1);
            } else if (this.dy > 0) {
                this.animation.setAnimation(assetPaths.falling, 1, -1);
            } else if (this.left || this.right) {
                this.animation.setAnimation(assetPaths.walking, 6, 100);
            } else {
                this.animation.setAnimation(assetPaths.idle, 1, -1);
            }
            this.animation.update();

            if (this.dx < 0) this.facingLeft = true;
            else if (this.dx > 0) this.facingLeft = false;
        }
        calculateY() {
            if (this.dy >= 0) {
                let landed = false;
                for (const p of this.platforms.platforms) {
                    const playerBottom = this.y + this.height;
                    const playerXCenter = this.x;
                    if (playerXCenter > p.x && playerXCenter < p.x + p.width &&
                        playerBottom <= p.y && playerBottom + this.dy >= p.y) {
                        this.y = p.y - this.height;
                        this.dy = this.jumpSpeed;
                        landed = true;
                        break;
                    }
                }
                if (!landed) {
                    this.y += this.dy;
                }
            } else {
                this.y += this.dy;
            }
            this.falling = true;
        }
        draw() {
            playerElement.style.left = `${this.x - this.width / 2}px`;
            playerElement.style.top = `${this.y}px`;
            playerElement.style.transform = `scaleX(${this.facingLeft ? -1 : 1})`;
        }
    }

    // --- Main Game Logic & State ---
    let player, platforms, score, hiScore, playing, gameOver;
    let totalScroll;

    function resetGame() {
        playing = true;
        gameOver = false;
        score = 0;
        totalScroll = 0;

        platforms.setLevel1();
        player.x = PREF_W / 2;
        player.y = (PREF_H - 100) - (player.height / 2);
        player.dx = 0;
        player.dy = 0;
        player.falling = true;
    }

    // Main update function, called every frame
    function update() {
        if (!playing) return;
        
        player.update();
        
        // World scrolling and Height-based Scoring
        if (player.y < PREF_H / 2 && player.dy < 0) {
            const scrollAmount = -player.dy;
            
            totalScroll += scrollAmount;

            player.y = PREF_H / 2;
            for (const p of platforms.platforms) {
                p.y += scrollAmount;
            }
        }
        
        // ** THIS IS THE CHANGED LINE **
        // Score is now the total height climbed, divided by 10.
        score = Math.floor(totalScroll / 10);

        if (score > hiScore) {
            hiScore = score;
        }
        
        platforms.updateAndDraw();
        
        if (player.y > PREF_H) {
            playing = false;
            gameOver = true;
        }
    }

    // Main draw function, called every frame
    function draw() {
        player.draw();
        
        scoreDisplay.textContent = score;
        hiScoreDisplay.textContent = hiScore;

        if (gameOver) {
            messageDisplay.textContent = "Press <UP> to restart";
        } else if (!playing) {
            messageDisplay.textContent = "Press <UP> to start";
        } else {
            messageDisplay.textContent = "";
        }
    }

    // The main game loop that calls update and draw
    function gameLoop() {
        update();
        draw();
        requestAnimationFrame(gameLoop);
    }

    // The initial setup function
    function init() {
        playing = false;
        gameOver = false;
        score = 0;
        hiScore = 0;
        totalScroll = 0;
        
        platforms = new Platforms();
        player = new Sprite(platforms);

        platforms.setLevel1();
        player.x = PREF_W / 2;
        player.y = (PREF_H - 100) - (player.height / 2);

        document.addEventListener('keydown', (e) => {
            if (e.key === 'ArrowLeft') player.left = true;
            if (e.key === 'ArrowRight') player.right = true;
            if (e.key === 'ArrowUp') {
                if (gameOver) {
                    resetGame();
                } else if (!playing) {
                    playing = true;
                }
            }
        });

        document.addEventListener('keyup', (e) => {
            if (e.key === 'ArrowLeft') player.left = false;
            if (e.key === 'ArrowRight') player.right = false;
        });

        gameLoop();
    }

    init();
});
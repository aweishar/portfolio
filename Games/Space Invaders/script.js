// Get canvas and context from the HTML
const canvas = document.getElementById('gameCanvas');
const loadingMessage = document.getElementById('loadingMessage');
const ctx = canvas.getContext('2d');

// --- Game Constants ---
const PANEL_WIDTH = 800;
const PANEL_HEIGHT = 600;
const TOTAL_LIVES = 2;
const EXTRA_POINTS_X = -1000;

canvas.width = PANEL_WIDTH;
canvas.height = PANEL_HEIGHT;

// --- Asset Loader ---
const assets = {};
function loadAssets(callback) {
    const assetUrls = {
        laser: './assets/laser.png',
        alienLaser: './assets/iceberg.png',
        ship: './assets/titanic.png',
        shipIcon: './assets/titanic.png',
        blankIcon: './assets/blackWhiteTitanic.png',
        extraPoints: './assets/lifebuoy.png',
        alien1: './assets/rowingBoat1.png',
        alien2: './assets/rowingBoat2.png',
        background: './assets/ocean.jpg',
        hitSound: './assets/hitSound.wav',
        endSound: './assets/gameEndSound.wav',
        niceShot: './assets/niceShotAudio.wav'
    };
    let loadedCount = 0;
    const totalAssets = Object.keys(assetUrls).length;

    for (const [name, url] of Object.entries(assetUrls)) {
        const isImage = url.endsWith('.png') || url.endsWith('.jpg');
        const asset = isImage ? new Image() : new Audio();
        asset.src = url;
        asset.onload = asset.oncanplaythrough = () => {
            loadedCount++;
            if (loadedCount === totalAssets) {
                loadingMessage.style.display = 'none';
                callback();
            }
        };
        assets[name] = asset;
    }
}

// --- Sprite Class (Translated from your versatile Sprite.java) ---
class Sprite {
    constructor(options) {
        this.x = options.x || 0;
        this.y = options.y || 0;
        this.w = options.w || 35;
        this.h = options.h || 25;
        this.dx = options.dx || 0;
        this.dy = options.dy || 0;
        this.image = options.image || null;
        this.hits = options.hits || 0;
        
        // For key controls
        this.controls = options.controls || {};
        this.right = false; this.left = false; this.up = false; this.down = false;
        
        // Boundaries
        this.minX = 0; this.maxX = PANEL_WIDTH;
        this.minY = 0; this.maxY = PANEL_HEIGHT;
        
        this.isFacingRight = true;
    }

    setBoundaries(minX, maxX, minY, maxY) {
        this.minX = minX; this.maxX = maxX; this.minY = minY; this.maxY = maxY;
    }

    // Corresponds to update_paddle_brickbreaker() used by the ship
    update_ship() {
        if (this.left) this.x -= this.dx;
        if (this.right) this.x += this.dx;
        if (this.x < this.minX) this.x = this.minX;
        if (this.x > this.maxX - this.w) this.x = this.maxX - this.w;
    }

    // For lasers
    update_projectile() {
        this.y += this.dy;
    }

    // For aliens and the extra points ship
    update_alien() {
        this.x += this.dx;
    }
    
    isOffPanel() {
        return this.x < this.minX - this.w || this.x > this.maxX ||
               this.y < this.minY - this.h || this.y > this.maxY;
    }

    setToShip(ship) {
        this.x = ship.x + ship.w / 2 - this.w / 2;
        this.y = ship.y;
    }
    
    willHitEdge() {
        return this.x + this.dx < 0 || this.x + this.w + this.dx > this.maxX;
    }
    
    moveAlienDown() {
        this.y += this.dy;
    }

    changeHorizontal() {
        this.dx = -this.dx;
    }

    draw(ctx) {
        if (!this.image) return;
        if (this.isFacingRight) {
            ctx.drawImage(this.image, this.x, this.y, this.w, this.h);
        } else {
            // Draw flipped horizontally
            ctx.save();
            ctx.translate(this.x + this.w, this.y);
            ctx.scale(-1, 1);
            ctx.drawImage(this.image, 0, 0, this.w, this.h);
            ctx.restore();
        }
    }
    
    getBounds() {
        return { x: this.x, y: this.y, w: this.w, h: this.h };
    }
    
    checkForPress(key) {
        if (key === this.controls.right) this.right = true;
        if (key === this.controls.left) this.left = true;
    }

    checkForRelease(key) {
        if (key === this.controls.right) this.right = false;
        if (key === this.controls.left) this.left = false;
    }
}

// --- Main Game Logic ---
let ship, laser, alienLaser, extraPoints, aliens;
let score, lives, level, alienMoveCounter, isCurrentAlien;
let playing, gameOver, laserIsFired;
let message = "";

function intersects(r1, r2) {
    return !(r2.x > r1.x + r1.w || r2.x + r2.w < r1.x ||
             r2.y > r1.y + r1.h || r2.y + r2.h < r1.y);
}

function resetGame() {
    score = 0;
    lives = TOTAL_LIVES;
    level = 1;
    playing = false;
    gameOver = false;
    laserIsFired = false;
    alienMoveCounter = 0;
    isCurrentAlien = true;
    extraPoints.x = EXTRA_POINTS_X;
    resetAliens(level);
    laser.setToShip(ship);
}

function resetAliens(rows) {
    aliens = [];
    for (let r = 0; r < rows; r++) {
        for (let c = 0; c < 10; c++) {
            aliens.push(new Sprite({
                x: 100 + c * 60, y: 100 + r * 50, w: 30, h: 20,
                dx: 2, dy: 25, image: assets.alien1
            }));
        }
    }
}

function resetAlienLaser() {
    if (aliens.length === 0) return;
    const shooter = aliens[Math.floor(Math.random() * aliens.length)];
    alienLaser.x = shooter.x + shooter.w / 2 - alienLaser.w / 2;
    alienLaser.y = shooter.y + shooter.h;
}

function moveAliens() {
    isCurrentAlien = !isCurrentAlien;
    const currentAlienImage = isCurrentAlien ? assets.alien1 : assets.alien2;

    let shouldChangeDirection = false;
    for (const alien of aliens) {
        if (alien.willHitEdge()) {
            shouldChangeDirection = true;
            break;
        }
    }
    
    for (const alien of aliens) {
        alien.image = currentAlienImage;
        if (shouldChangeDirection) {
            alien.changeHorizontal();
            alien.moveAlienDown();
        } else {
            alien.update_alien();
        }
    }
}

function update() {
    if (!playing) return;
    
    message = "";
    ship.update_ship();
    extraPoints.update_alien();
    if (extraPoints.x > PANEL_WIDTH + 100) extraPoints.x = EXTRA_POINTS_X;

    if (laserIsFired) {
        laser.update_projectile();
    } else {
        laser.setToShip(ship);
    }
    if (laser.isOffPanel()) laserIsFired = false;

    alienLaser.update_projectile();
    if (alienLaser.isOffPanel()) resetAlienLaser();

    // --- Collision Detection ---
    if (intersects(laser.getBounds(), extraPoints.getBounds())) {
        score += 10;
        laserIsFired = false;
        extraPoints.x = EXTRA_POINTS_X;
        assets.niceShot.play();
    }

    if (intersects(alienLaser.getBounds(), ship.getBounds())) {
        lives--;
        resetAlienLaser();
        playing = false; // Pause briefly
    }

    for (let i = aliens.length - 1; i >= 0; i--) {
        if (intersects(laser.getBounds(), aliens[i].getBounds())) {
            aliens.splice(i, 1);
            score++;
            laserIsFired = false;
            assets.hitSound.currentTime = 0;
            assets.hitSound.play();
        }
    }

    // --- Alien Movement ---
    alienMoveCounter++;
    if (alienMoveCounter > 20 + 2 * aliens.length) {
        moveAliens();
        alienMoveCounter = 0;
    }

    // --- Game State Checks ---
    if (aliens.length === 0) {
        level++;
        resetAliens(level);
        playing = false;
    }
    if (lives < 0) {
        playing = false;
        gameOver = true;
        extraPoints.x = EXTRA_POINTS_X;
        assets.endSound.play();
    }
    for (const alien of aliens) {
        if (alien.y + alien.h > ship.y) {
            gameOver = true;
            playing = false;
            extraPoints.x = EXTRA_POINTS_X;
        }
    }
}

function draw() {
    ctx.drawImage(assets.background, 0, 0, PANEL_WIDTH, PANEL_HEIGHT);
    
    laser.draw(ctx);
    alienLaser.draw(ctx);
    ship.draw(ctx);
    extraPoints.draw(ctx);
    
    aliens.forEach(a => a.draw(ctx));

    // UI Drawing
    for (let i = 0; i < TOTAL_LIVES; i++) {
        const icon = (i < lives) ? assets.shipIcon : assets.blankIcon;
        ctx.drawImage(icon, PANEL_WIDTH - 50 * (i + 1), 20, 30, 20);
    }
    
    ctx.fillStyle = 'white';
    ctx.font = '20px "Courier New", monospace';
    ctx.textAlign = 'left';
    ctx.fillText(`Score: ${score}`, 20, 40);

    ctx.font = '25px "Courier New", monospace';
    ctx.textAlign = 'center';

    if (gameOver) {
        message = "Game Over";
    } else if (!playing) {
        message = "Press <Space> to Start/Fire";
    }
    ctx.fillText(message, PANEL_WIDTH / 2, 170);
}

// --- Main Game Loop ---
function gameLoop() {
    update();
    draw();
    requestAnimationFrame(gameLoop);
}

// --- Initialize and Start ---
function init() {
    ship = new Sprite({
        x: PANEL_WIDTH / 2 - 50, y: PANEL_HEIGHT - 55, w: 100, h: 35,
        dx: 4, image: assets.ship,
        controls: { left: 'ArrowLeft', right: 'ArrowRight' }
    });
    laser = new Sprite({ w: 5, h: 15, dy: -6, image: assets.laser });
    alienLaser = new Sprite({ w: 25, h: 25, dy: 3, image: assets.alienLaser });
    extraPoints = new Sprite({
        x: EXTRA_POINTS_X, y: 50, w: 100, h: 45, dx: 1, image: assets.extraPoints
    });

    document.addEventListener('keydown', (e) => {
        ship.checkForPress(e.code);
        if (e.code === 'Space') {
            if (gameOver) resetGame();
            else if (!playing) playing = true;
            else laserIsFired = true;
        }
    });
    document.addEventListener('keyup', (e) => ship.checkForRelease(e.code));
    
    resetGame();
    gameLoop();
}

loadAssets(init);
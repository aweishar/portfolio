// Get the canvas element from the HTML, which is like the JPanel
const canvas = document.getElementById('gameCanvas');
const loadingMessage = document.getElementById('loadingMessage');
const ctx = canvas.getContext('2d');

// --- Game Constants (from your Java final variables) ---
const PANEL_WIDTH = 800;
const PANEL_HEIGHT = 600;
const TOTAL_LIVES = 3;

canvas.width = PANEL_WIDTH;
canvas.height = PANEL_HEIGHT;

// --- Asset Loading ---
// We must load images and sounds before the game starts.
const assets = {};
const assetUrls = {
    // Images
    ballImage: './assets/snowflake.png',
    paddleImage: './assets/icePick.png',
    brickImage: './assets/iceBlock.png',
    backgroundImage: './assets/snowBackground.jpg',
    // Sounds
    hitSound: './assets/iceBreaking.wav',
    gameEndSound: './assets/screaming.wav',
    loseLifeSound: './assets/windSound.wav'
};

let assetsLoaded = 0;
const totalAssets = Object.keys(assetUrls).length;

function assetLoaded(name, asset) {
    assets[name] = asset;
    assetsLoaded++;
    if (assetsLoaded === totalAssets) {
        // All assets are loaded, start the game!
        loadingMessage.style.display = 'none';
        init();
    }
}

// Preload all assets
for (const [name, url] of Object.entries(assetUrls)) {
    if (url.endsWith('.png') || url.endsWith('.jpg')) {
        const img = new Image();
        img.src = url;
        img.onload = () => assetLoaded(name, img);
    } else if (url.endsWith('.wav')) {
        const audio = new Audio();
        audio.src = url;
        // Sounds don't need an onload event for this purpose
        assetLoaded(name, audio);
    }
}


// --- Game State and Objects ---
let ball, paddle, bricks;
let score, lives, level;
let playing, gameOver;
let message;
const keys = {}; // To track pressed keys for smooth movement

// A direct translation of your Sprite class
class Sprite {
    constructor(x, y, width, height, image) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.image = image;
        this.dx = 0;
        this.dy = 0;
        this.hits = 1;
    }

    draw(context) {
        if (this.image) {
            context.drawImage(this.image, this.x, this.y, this.width, this.height);
        }
    }
    
    // Custom method from your Java code to draw bricks with hit counts
    drawWithHits(context) {
        this.draw(context);
        if (this.hits > 1) {
            context.fillStyle = 'black';
            context.font = '16px Courier New';
            context.textAlign = 'center';
            context.fillText(this.hits, this.x + this.width / 2, this.y + this.height / 2 + 6);
        }
    }
}

// --- Input Handling (like KeyListener) ---
document.addEventListener('keydown', (e) => {
    keys[e.code] = true;
    if (e.code === 'Space') {
        if (gameOver) {
            resetGame();
        } else if (!playing) {
            playing = true;
        }
    }
});

document.addEventListener('keyup', (e) => {
    keys[e.code] = false;
});


// --- Game Logic Functions ---

function resetBricks(currentLevel) {
    bricks = [];
    const rows = currentLevel;
    for (let r = 0; r < rows; r++) {
        for (let c = 0; c < 8; c++) {
            const brick = new Sprite(c * 100, 100 + r * 20, 100, 20, assets.brickImage);
            brick.hits = currentLevel; // Set hits based on the level
            bricks.push(brick);
        }
    }
}

function resetGame() {
    score = 0;
    lives = TOTAL_LIVES;
    level = 1;
    playing = false;
    gameOver = false;
    message = "Press <Space> to serve";
    
    paddle = new Sprite(PANEL_WIDTH / 2 - 50, PANEL_HEIGHT - 35, 100, 25, assets.paddleImage);
    ball = new Sprite(PANEL_WIDTH / 2 - 15, PANEL_HEIGHT / 2 - 15, 30, 30, assets.ballImage);
    ball.dx = 3; // Initial ball speed
    ball.dy = -3;

    resetBricks(level);
}

// Equivalent to the logic inside your Timer's actionPerformed
function update() {
    // Paddle movement
    if (keys['ArrowLeft'] || keys['KeyA']) {
        paddle.x -= 5;
    }
    if (keys['ArrowRight'] || keys['KeyD']) {
        paddle.x += 5;
    }
    // Constrain paddle to screen bounds
    if (paddle.x < 0) paddle.x = 0;
    if (paddle.x + paddle.width > PANEL_WIDTH) paddle.x = PANEL_WIDTH - paddle.width;

    if (!playing) return;

    message = "";
    ball.x += ball.dx;
    ball.y += ball.dy;

    // Ball collision with walls
    if (ball.x <= 0 || ball.x + ball.width >= PANEL_WIDTH) {
        ball.dx *= -1;
    }
    if (ball.y <= 0) {
        ball.dy *= -1;
    }

    // Ball misses paddle (lose a life)
    if (ball.y + ball.height >= PANEL_HEIGHT) {
        lives--;
        playing = false;
        assets.loseLifeSound.currentTime = 0;
        assets.loseLifeSound.play();
        if (lives <= 0) {
            gameOver = true;
            assets.gameEndSound.currentTime = 0;
            assets.gameEndSound.play();
        } else {
            // Reset ball position
            ball.x = PANEL_WIDTH / 2 - 15;
            ball.y = PANEL_HEIGHT / 2 - 15;
        }
        return;
    }

    // Ball collision with paddle
    if (ball.x < paddle.x + paddle.width && ball.x + ball.width > paddle.x &&
        ball.y < paddle.y + paddle.height && ball.y + ball.height > paddle.y) {
        ball.dy *= -1;
        ball.y = paddle.y - ball.height; // prevent sticking
    }

    // Ball collision with bricks
    for (let i = bricks.length - 1; i >= 0; i--) {
        const brick = bricks[i];
        if (ball.x < brick.x + brick.width && ball.x + ball.width > brick.x &&
            ball.y < brick.y + brick.height && ball.y + ball.height > brick.y) {
            
            ball.dy *= -1;
            brick.hits--;
            score++;
            assets.hitSound.currentTime = 0;
            assets.hitSound.play();

            if (brick.hits === 0) {
                bricks.splice(i, 1);
            }
            break; // Handle one brick collision per frame
        }
    }
    
    // Level clear
    if (bricks.length === 0) {
        level++;
        playing = false;
        resetBricks(level);
        ball.x = PANEL_WIDTH / 2 - 15;
        ball.y = PANEL_HEIGHT / 2 - 15;
    }
}

// Equivalent to your paintComponent method
function draw() {
    // Background
    ctx.drawImage(assets.backgroundImage, 0, 0, PANEL_WIDTH, PANEL_HEIGHT);

    // Draw game objects
    ball.draw(ctx);
    paddle.draw(ctx);
    bricks.forEach(brick => brick.drawWithHits(ctx));
    
    // Draw text (score, lives, etc.)
    ctx.fillStyle = 'black';
    ctx.font = '30px "Courier New", monospace';
    ctx.textAlign = 'center';
    
    ctx.fillText(`Lives: ${lives}`, PANEL_WIDTH * 3 / 4, 40);
    ctx.fillText(`Score: ${score}`, PANEL_WIDTH / 4, 40);
    ctx.fillText(`Level: ${level}`, PANEL_WIDTH / 2, 40);

    // Draw messages
    if (!playing && !gameOver) {
        message = "Press <Space> to serve";
    }
    if(gameOver){
        message = "AVALANCHE! Game Over";
    }
    ctx.font = '40px "Courier New", monospace';
    ctx.fillText(message, PANEL_WIDTH / 2, 220);
}


// --- Main Game Loop ---
function gameLoop() {
    update();
    draw();
    requestAnimationFrame(gameLoop); // This creates a smooth animation loop
}

// --- Initialize and Start ---
function init() {
    resetGame();
    gameLoop();
}
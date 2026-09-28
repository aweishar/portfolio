// Get the canvas element from the HTML, which is the equivalent of your JPanel
const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// --- Game Constants (from your Java final variables) ---
const PANEL_WIDTH = 800;
const PANEL_HEIGHT = 600;
const GRID_SIZE = 25; // The width/height of snake parts and food

canvas.width = PANEL_WIDTH;
canvas.height = PANEL_HEIGHT;

// --- Game State Variables ---
let snake;
let food;
let score;
let playing;
let gameOver;
let message;

// Represents a single food item, like your SnakeFood.java class
class Food {
    constructor() {
        this.w = GRID_SIZE;
        this.h = GRID_SIZE;
        this.color = 'rgba(255, 0, 0, 0.9)'; // Red, slightly transparent
        this.respawn();
    }

    // Moves the food to a new random position, equivalent to your update() method
    respawn() {
        this.x = Math.floor(Math.random() * (PANEL_WIDTH / GRID_SIZE)) * GRID_SIZE;
        this.y = Math.floor(Math.random() * (PANEL_HEIGHT / GRID_SIZE)) * GRID_SIZE;
    }

    // Draws the food on the canvas
    draw() {
        ctx.fillStyle = this.color;
        ctx.fillRect(this.x, this.y, this.w, this.h);
    }
}

// Represents the snake, combining Snake.java and SnakeBodyPart.java logic
class Snake {
    constructor(x, y, color) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.body = [{ x: this.x, y: this.y }]; // The ArrayList of body parts
        this.dx = 0; // Change in x
        this.dy = -GRID_SIZE; // Change in y (start by moving up)
        this.nextDirection = { dx: this.dx, dy: this.dy };
    }

    // Changes the snake's direction, but not on the next immediate frame
    // to prevent the snake from reversing into itself.
    changeDirection(key) {
        switch (key) {
            case 'ArrowUp':
                if (this.dy === 0) this.nextDirection = { dx: 0, dy: -GRID_SIZE };
                break;
            case 'ArrowDown':
                if (this.dy === 0) this.nextDirection = { dx: 0, dy: GRID_SIZE };
                break;
            case 'ArrowLeft':
                if (this.dx === 0) this.nextDirection = { dx: -GRID_SIZE, dy: 0 };
                break;
            case 'ArrowRight':
                if (this.dx === 0) this.nextDirection = { dx: GRID_SIZE, dy: 0 };
                break;
        }
    }

    // Updates the snake's position, equivalent to your update() method
    update() {
        // Apply the direction change
        this.dx = this.nextDirection.dx;
        this.dy = this.nextDirection.dy;

        // Move the head
        this.x += this.dx;
        this.y += this.dy;

        // Add the new head to the front of the body
        this.body.unshift({ x: this.x, y: this.y });
        // Remove the tail
        this.body.pop();
    }
    
    // Grows the snake, equivalent to your addBodyPart() method
    grow() {
        // Adds a new part at the head's current position.
        // On the next update, the tail won't be popped, making the snake longer.
        const head = this.body[0];
        this.body.push({ ...head });
    }

    // Checks for collision with its own body
    isCollidingWithSelf() {
        const head = this.body[0];
        for (let i = 1; i < this.body.length; i++) {
            if (head.x === this.body[i].x && head.y === this.body[i].y) {
                return true;
            }
        }
        return false;
    }

    // Draws the snake on the canvas
    draw() {
        ctx.fillStyle = this.color;
        this.body.forEach(part => {
            ctx.fillRect(part.x, part.y, GRID_SIZE, GRID_SIZE);
        });
    }
}

// --- Game Logic Functions ---

// Resets the game to its initial state, equivalent to resetGame()
function resetGame() {
    snake = new Snake(400, 300, 'rgb(0, 255, 0)');
    food = new Food();
    score = 0;
    playing = false;
    gameOver = false;
    message = "Press <Arrow Key> to Start";
    draw(); // Draw the initial start screen
}

// Main drawing function, equivalent to your paintComponent() method
function draw() {
    // Black background
    ctx.fillStyle = 'black';
    ctx.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

    // Draw snake and food
    snake.draw();
    food.draw();
    
    // Set up font for messages
    ctx.fillStyle = 'yellow';
    ctx.font = "25px 'Courier New', monospace";
    ctx.textAlign = 'center';

    // Game messages logic
    if (gameOver) {
        message = "Game Over";
    }
    ctx.fillText(message, PANEL_WIDTH / 2, 170);

    // Score display
    ctx.textAlign = 'left';
    ctx.fillText(`Score: ${score}`, 25, 50);

    // Pause instruction
    ctx.font = "20px 'Courier New', monospace";
    ctx.fillText("Press <P> to pause", 32, 550);
}

// Collision detection and game state updates
function checkCollisions() {
    const head = snake.body[0];

    // Wall collision
    if (head.x < 0 || head.x >= PANEL_WIDTH || head.y < 0 || head.y >= PANEL_HEIGHT) {
        playing = false;
        gameOver = true;
    }

    // Self collision
    if (snake.isCollidingWithSelf()) {
        playing = false;
        gameOver = true;
    }

    // Food collision
    if (head.x === food.x && head.y === food.y) {
        score++;
        snake.grow();
        food.respawn();
    }
}

// The main game loop, equivalent to your Timer's ActionListener
function gameLoop() {
    if (playing) {
        message = "";
        snake.update();
        checkCollisions();
    }
    draw();
}

// --- Event Listeners (like KeyListener) ---
document.addEventListener('keydown', (e) => {
    if (gameOver) {
        resetGame();
        return;
    }

    const key = e.key;
    if (['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight'].includes(key)) {
        if (!playing) playing = true;
        snake.changeDirection(key);
    }

    if (key.toUpperCase() === 'P') {
        playing = false;
        message = "Press <Arrow Key> to Continue";
    }
});

// --- Initialize and Start the Game ---
resetGame(); // Set up the initial game state
setInterval(gameLoop, 100); // Start the game loop (100ms = 10 FPS)
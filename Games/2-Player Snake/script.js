// Get the canvas element from the HTML, which is like the JPanel
const canvas = document.getElementById('gameCanvas');
const ctx = canvas.getContext('2d');

// --- Game Constants (from your Java final variables) ---
const PANEL_WIDTH = 800;
const PANEL_HEIGHT = 600;
const GRID_SIZE = 25; // The width/height of snake parts and food

canvas.width = PANEL_WIDTH;
canvas.height = PANEL_HEIGHT;

// --- Game State Variables ---
let snake1, snake2;
let food1, food2, food3;
let score;
let playing;
let gameOver;
let message;
let gameInterval;

// Represents a single food item, like your SnakeFood.java class
class Food {
    constructor(color) {
        this.w = GRID_SIZE;
        this.h = GRID_SIZE;
        this.color = color;
        this.respawn();
    }

    // Moves food to a new random position on the grid
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

// Represents a snake, combining Snake.java and SnakeBodyPart.java logic
class Snake {
    constructor(x, y, color, controls) {
        this.x = x;
        this.y = y;
        this.color = color;
        this.controls = controls; // e.g., { up: 'ArrowUp', down: 'ArrowDown', ... }
        this.body = [{ x: this.x, y: this.y }];
        this.dx = 0; // Change in x
        this.dy = -GRID_SIZE; // Change in y (start moving up)
        this.nextDirection = { dx: this.dx, dy: this.dy };
    }

    changeDirection(key) {
        switch (key) {
            case this.controls.up:
                if (this.dy === 0) this.nextDirection = { dx: 0, dy: -GRID_SIZE };
                break;
            case this.controls.down:
                if (this.dy === 0) this.nextDirection = { dx: 0, dy: GRID_SIZE };
                break;
            case this.controls.left:
                if (this.dx === 0) this.nextDirection = { dx: -GRID_SIZE, dy: 0 };
                break;
            case this.controls.right:
                if (this.dx === 0) this.nextDirection = { dx: GRID_SIZE, dy: 0 };
                break;
        }
    }

    update() {
        this.dx = this.nextDirection.dx;
        this.dy = this.nextDirection.dy;

        this.x += this.dx;
        this.y += this.dy;

        // Add new head
        this.body.unshift({ x: this.x, y: this.y });
        // Remove tail
        this.body.pop();
    }
    
    grow() {
        // Add a new part at the front, effectively making the snake longer
        // because we don't pop the tail in the next update cycle immediately.
        // A simpler way is just to add to the tail.
        const tail = this.body[this.body.length - 1];
        this.body.push({ ...tail });
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

    draw() {
        ctx.fillStyle = this.color;
        this.body.forEach(part => {
            ctx.fillRect(part.x, part.y, GRID_SIZE, GRID_SIZE);
        });
    }
}

// --- Game Logic Functions ---

// Resets the game to its initial state
function resetGame() {
    // Corresponds to your resetGame() method
    snake1 = new Snake(400, 300, 'rgb(0, 255, 0)', {
        up: 'ArrowUp', down: 'ArrowDown', left: 'ArrowLeft', right: 'ArrowRight'
    });
    snake2 = new Snake(300, 300, 'rgb(0, 0, 255)', {
        up: 'KeyW', down: 'KeyS', left: 'KeyA', right: 'KeyD'
    });
    
    food1 = new Food('rgba(255, 0, 0, 0.7)');
    food2 = new Food('rgba(255, 0, 0, 0.7)');
    food3 = new Food('rgba(160, 103, 60, 0.7)'); // Brown "bad" apple

    score = 0;
    playing = false;
    gameOver = false;
    message = "Press <Space Bar> to Start";
    draw(); // Initial draw of the start screen
}

// Main drawing function, equivalent to paintComponent()
function draw() {
    // Black background
    ctx.fillStyle = 'black';
    ctx.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);

    // Draw snakes and food if the game is active
    if (playing || gameOver) {
        snake1.draw();
        snake2.draw();
        food1.draw();
        food2.draw();
        food3.draw();
    }
    
    // Set up font for messages
    ctx.fillStyle = 'yellow';
    ctx.font = "25px 'Courier New', Courier, monospace";
    ctx.textAlign = 'center';

    // Game messages logic
    if (gameOver) {
        ctx.font = "50px 'Courier New', Courier, monospace";
        ctx.fillText("Game Over", PANEL_WIDTH / 2, PANEL_HEIGHT / 2 - 50);
        ctx.font = "25px 'Courier New', Courier, monospace";
        ctx.fillText(`Final Score: ${score}`, PANEL_WIDTH / 2, PANEL_HEIGHT / 2);
        ctx.fillText("Press any key to play again", PANEL_WIDTH / 2, PANEL_HEIGHT / 2 + 50);
    } else if (!playing) {
        // Start screen or Paused screen
        ctx.font = "40px 'Courier New', Courier, monospace";
        ctx.fillText(message, PANEL_WIDTH / 2, PANEL_HEIGHT / 2);
        
        ctx.font = "20px 'Courier New', Courier, monospace";
        ctx.textAlign = 'left';
        ctx.fillText("Objective: Work together to obtain the", 32, 45);
        ctx.fillText("highest score possible, but be careful...", 32, 70);
        ctx.fillText("You must avoid the spoiled brown apples!", 32, 95);

        ctx.font = "15px 'Courier New', Courier, monospace";
        ctx.fillText("Blue Controls: <W> <A> <S> <D>", 32, 540);
        ctx.fillText("Green Controls: <Arrow Keys>", 32, 570);
    }

    // Score display
    if (playing || gameOver) {
        ctx.font = "25px 'Courier New', Courier, monospace";
        ctx.textAlign = 'left';
        ctx.fillText(`Score: ${score}`, 25, 50);
    }
    if (playing) {
        ctx.font = "20px 'Courier New', Courier, monospace";
        ctx.fillText("Press <P> to pause", 32, 565);
    }
}

// Collision detection logic
function checkCollisions() {
    const head1 = snake1.body[0];
    const head2 = snake2.body[0];

    // Wall collision
    if (head1.x < 0 || head1.x >= PANEL_WIDTH || head1.y < 0 || head1.y >= PANEL_HEIGHT ||
        head2.x < 0 || head2.x >= PANEL_WIDTH || head2.y < 0 || head2.y >= PANEL_HEIGHT) {
        playing = false;
        gameOver = true;
    }

    // Self collision
    if (snake1.isCollidingWithSelf() || snake2.isCollidingWithSelf()) {
        playing = false;
        gameOver = true;
    }

    // Food collision
    [food1, food2].forEach(food => {
        if (head1.x === food.x && head1.y === food.y) {
            score++;
            snake1.grow();
            food.respawn();
        }
        if (head2.x === food.x && head2.y === food.y) {
            score++;
            snake2.grow();
            food.respawn();
        }
    });

    // "Bad" food collision
    if (head1.x === food3.x && head1.y === food3.y) {
        score--;
        food3.respawn();
    }
    if (head2.x === food3.x && head2.y === food3.y) {
        score--;
        food3.respawn();
    }
}

// The main game loop, equivalent to your Timer's ActionListener
function gameLoop() {
    if (!playing) {
        draw();
        return;
    }

    snake1.update();
    snake2.update();
    checkCollisions();
    draw();
}

// --- Event Listeners (like KeyListener) ---
document.addEventListener('keydown', (e) => {
    if (gameOver) {
        resetGame();
        return;
    }

    if (e.code === 'Space') {
        playing = true;
    } else if (e.code === 'KeyP') {
        playing = false;
        message = "Game Paused - Press Space to Continue";
    }

    if (playing) {
        snake1.changeDirection(e.code);
        snake2.changeDirection(e.code);
    }
});

// --- Initialize and Start the Game ---
resetGame(); // Set up the initial game state
gameInterval = setInterval(gameLoop, 100); // Start the game loop, 100ms = 10 FPS
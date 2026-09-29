document.addEventListener('DOMContentLoaded', () => {
    // Game constants and variables
    const ROWS = 5;
    const COLS = 5;
    const gamePanel = document.getElementById('game-panel');
    const topMenu = document.getElementById('top-menu');
    const bottomMenu = document.getElementById('bottom-menu');
    const label = document.getElementById('label');
    const buttons = [];

    let attempts = 0;
    let movesToWin = 0;
    let difficultySelected = false;

    // --- Game Board Setup ---
    for (let r = 0; r < ROWS; r++) {
        buttons[r] = [];
        for (let c = 0; c < COLS; c++) {
            const button = document.createElement('button');
            button.classList.add('light-button', 'off');
            button.dataset.row = r;
            button.dataset.col = c;
            
            button.addEventListener('click', () => {
                if (!difficultySelected || isWin()) {
                    return; // Don't do anything if no difficulty is set or game is won
                }
                
                console.log(`move @ (${r}, ${c})`);
                moves(r, c);
                attempts++;
                label.textContent = `Attempts: ${attempts}`;

                if (isWin()) {
                    label.textContent = `Winner! Attempts: ${attempts}`;
                    bottomMenu.classList.add('win-state');
                    topMenu.classList.add('win-state');
                }
            });
            
            buttons[r][c] = button;
            gamePanel.appendChild(button);
        }
    }
    
    // --- Game Logic Functions ---
    const switchLight = (button) => {
        button.classList.toggle('on');
        button.classList.toggle('off');
    };

    const moves = (row, col) => {
        // Ensure row/col are numbers
        row = parseInt(row);
        col = parseInt(col);

        switchLight(buttons[row][col]);
        if (row - 1 >= 0) switchLight(buttons[row - 1][col]);
        if (row + 1 < ROWS) switchLight(buttons[row + 1][col]);
        if (col - 1 >= 0) switchLight(buttons[row][col - 1]);
        if (col + 1 < COLS) switchLight(buttons[row][col + 1]);
    };

    const isWin = () => {
        for (let r = 0; r < ROWS; r++) {
            for (let c = 0; c < COLS; c++) {
                if (buttons[r][c].classList.contains('on')) {
                    return false;
                }
            }
        }
        return true;
    };

    const clearBoard = () => {
        for (let r = 0; r < ROWS; r++) {
            for (let c = 0; c < COLS; c++) {
                buttons[r][c].classList.remove('on');
                buttons[r][c].classList.add('off');
            }
        }
    };
    
    const start = () => {
        bottomMenu.classList.remove('win-state');
        topMenu.classList.remove('win-state');
        // Scramble the board with a set number of random moves
        for (let i = 0; i < movesToWin; i++) {
            const randRow = Math.floor(Math.random() * ROWS);
            const randCol = Math.floor(Math.random() * COLS);
            moves(randRow, randCol);
        }
        // If the board is solved by chance, press one more time
        if (isWin()) {
             moves(Math.floor(Math.random() * ROWS), Math.floor(Math.random() * COLS));
        }
    };

    const resetGame = (difficulty, numMoves) => {
        difficultySelected = true;
        movesToWin = numMoves;
        clearBoard();
        start();
        attempts = 0;
        label.textContent = `Difficulty: ${difficulty}`;
    };

    // --- Event Listeners for Menu Buttons ---
    document.getElementById('easy-btn').addEventListener('click', () => resetGame('Easy', 3));
    document.getElementById('medium-btn').addEventListener('click', () => resetGame('Medium', 5));
    document.getElementById('hard-btn').addEventListener('click', () => resetGame('Hard', 7));
});
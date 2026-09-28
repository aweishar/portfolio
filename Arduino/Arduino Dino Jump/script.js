/*
 * project-details.js
 * This script fetches the .ino file and displays it on the page
 * with syntax highlighting using Prism.js.
 */

// Wait for the DOM (the HTML page) to be fully loaded
document.addEventListener('DOMContentLoaded', () => {
    
    // Find the <code> block on the page
    const codeBlock = document.getElementById('code-display');
    
    if (codeBlock) {
        // Get the file path from the 'data-src' attribute
        const codeSource = codeBlock.getAttribute('data-src');

        // Fetch the code file from the server
        fetch(codeSource)
            .then(response => {
                // Check if the file was found
                if (!response.ok) {
                    throw new Error(`HTTP error! Status: ${response.status}`);
                }
                // Get the text content of the file
                return response.text();
            })
            .then(text => {
                // Put the code text inside the <code> block
                codeBlock.textContent = text;
                
                // Tell Prism.js to highlight the new code
                Prism.highlightElement(codeBlock);
            })
            .catch(error => {
                // If something goes wrong, show an error message
                codeBlock.textContent = `Error loading code file: ${error}\n\nPlease check the file path: ${codeSource}`;
            });
    }
});
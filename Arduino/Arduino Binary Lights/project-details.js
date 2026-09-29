document.addEventListener('DOMContentLoaded', () => {
    const codeBlock = document.getElementById('code-display');

    if (codeBlock) {
        const srcFile = codeBlock.dataset.src; // Gets "BinaryLights.ino"

        if (srcFile) {
            fetch(srcFile)
                .then(response => {
                    if (!response.ok) {
                        throw new Error(`HTTP error! Status: ${response.status}`);
                    }
                    return response.text();
                })
                .then(text => {
                    // Place the fetched code into the code element
                    codeBlock.textContent = text;
                    
                    // Tell Prism.js to highlight the new code
                    if (window.Prism) Prism.highlightElement(codeBlock);
                })
                .catch(error => {
                    console.error('Error fetching code:', error);
                    codeBlock.textContent = `// Error: Could not load ${srcFile}.\n// Make sure the file is in the same folder and you are using a local server.`;
                    if (window.Prism) Prism.highlightElement(codeBlock);
                });
        }
    }
});
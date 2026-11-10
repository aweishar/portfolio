document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('code[data-src]').forEach(async (codeBlock) => {
        const srcFile = codeBlock.dataset.src;
        try {
            const response = await fetch(srcFile);
            if (!response.ok) throw new Error(`HTTP ${response.status}`);
            codeBlock.textContent = await response.text();
            if (window.Prism) Prism.highlightElement(codeBlock);
        } catch (error) {
            console.error(`Error loading ${srcFile}:`, error);
            codeBlock.textContent = `Could not load ${srcFile}. Please use the download link above.`;
        }
    });
});

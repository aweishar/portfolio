const sourceLinks = [...document.querySelectorAll('[data-source]')];
const sourceCode = document.getElementById('source-code');
const sourceViewer = document.getElementById('source-viewer');
const sourceStatus = document.getElementById('source-status');
let currentRequest;

async function showSource(link) {
    currentRequest?.abort();
    const request = new AbortController();
    currentRequest = request;
    sourceLinks.forEach(item => item.setAttribute('aria-current', String(item === link)));
    document.getElementById('file-name').textContent = link.textContent;
    const download = document.getElementById('download-source');
    download.href = link.href;
    download.download = link.textContent;
    sourceCode.textContent = '';
    sourceStatus.textContent = 'Loading source…';
    sourceViewer.setAttribute('aria-busy', 'true');
    try {
        const response = await fetch(link.href, { signal: request.signal });
        if (!response.ok) throw new Error(`HTTP ${response.status}`);
        const source = await response.text();
        if (currentRequest !== request) return;
        // Render as text so Java comments and strings cannot become HTML.
        sourceCode.textContent = source;
        sourceViewer.scrollTop = 0;
        sourceViewer.scrollLeft = 0;
        sourceStatus.textContent = `${link.textContent} · ${source.trimEnd().split('\n').length} lines`;
    } catch (error) {
        if (request.signal.aborted) return;
        sourceStatus.textContent = 'Unable to load this source file. Use Download Java to access it directly. If viewing locally, serve the site through a local web server.';
    } finally {
        if (currentRequest === request) sourceViewer.setAttribute('aria-busy', 'false');
    }
}

sourceLinks.forEach(link => link.addEventListener('click', event => {
    if (event.metaKey || event.ctrlKey || event.shiftKey || event.altKey) return;
    event.preventDefault();
    showSource(link);
}));
if (sourceLinks.length) showSource(sourceLinks[0]);

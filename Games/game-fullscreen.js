(() => {
    const stage = document.querySelector('.game-stage');
    const content = stage.querySelector('.game-content');
    const enter = stage.querySelector('.game-enter');
    const status = stage.querySelector('.game-fullscreen-status');
    let entered = false;
    window.gameFullscreenActive = () => document.fullscreenElement === stage;
    const sounds = new Set();
    let muted = false;
    const muteButton = stage.querySelector('.game-mute');
    window.registerGameAudio = audio => {
        sounds.add(audio);
        audio.muted = muted;
        return audio;
    };
    muteButton?.addEventListener('click', () => {
        muted = !muted;
        sounds.forEach(audio => { audio.muted = muted; });
        const label = muted ? 'Unmute sound' : 'Mute sound';
        muteButton.setAttribute('aria-label', label);
        muteButton.setAttribute('title', label);
        muteButton.setAttribute('aria-pressed', String(muted));
    });

    function fitGame() {
        if (!window.gameFullscreenActive()) {
            content.style.removeProperty('scale');
            return;
        }
        const viewport = stage.querySelector('.game-viewport');
        const scale = Math.min(1, (viewport.clientWidth - 24) / content.offsetWidth,
            (viewport.clientHeight - 24) / content.offsetHeight);
        content.style.scale = String(Math.max(0.1, scale));
    }
    function syncFullscreen() {
        const active = window.gameFullscreenActive();
        content.inert = !active;
        stage.classList.toggle('game-active', active);
        if (active) {
            entered = true;
            status.textContent = '';
            stage.focus({ preventScroll: true });
        } else {
            // Release held movement keys before pausing to avoid stuck controls on resume.
            for (const key of ['ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'a', 'd', 'w', 's', ' ']) {
                document.dispatchEvent(new KeyboardEvent('keyup', { key, code: key === ' ' ? 'Space' : key.length === 1 ? `Key${key.toUpperCase()}` : key }));
            }
            if (entered) {
                enter.textContent = 'Resume in fullscreen';
                stage.querySelector('.game-gate h2').textContent = 'Game paused';
                enter.focus({ preventScroll: true });
            }
        }
        requestAnimationFrame(fitGame);
    }
    enter.addEventListener('click', async () => {
        if (!stage.requestFullscreen || !document.fullscreenEnabled) {
            status.textContent = 'This browser does not support fullscreen games. Open this page in a desktop browser that supports fullscreen.';
            return;
        }
        try { await stage.requestFullscreen(); }
        catch (_) { status.textContent = 'Fullscreen could not start. Select the button to try again or check your browser permissions.'; }
    });
    document.addEventListener('keydown', event => {
        if (!window.gameFullscreenActive()) {
            event.stopImmediatePropagation();
            return;
        }
        if (event.target === muteButton && (event.code === 'Space' || event.key === 'Enter')) {
            event.stopImmediatePropagation();
            return;
        }
        if (['ArrowUp', 'ArrowDown', 'ArrowLeft', 'ArrowRight', 'Space'].includes(event.code)) {
            event.preventDefault();
        }
        // Keep menu activation and browser shortcuts out of the game's controls.
        if (event.key === 'Tab' || event.key === 'Escape' || event.key === 'Enter' || event.metaKey || event.ctrlKey) {
            event.stopImmediatePropagation();
        }
    }, true);
    document.addEventListener('fullscreenchange', syncFullscreen);
    window.addEventListener('resize', fitGame);
    new ResizeObserver(fitGame).observe(content);
    syncFullscreen();
})();

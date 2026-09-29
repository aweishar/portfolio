(() => {
    const preference = matchMedia('(prefers-color-scheme: dark)');
    function applyTheme() {
        let saved;
        try { saved = localStorage.getItem('theme'); } catch (_) {}
        const dark = saved ? saved === 'dark' : preference.matches;
        document.documentElement.classList.toggle('dark', dark);
        if (document.body) {
            document.body.dataset.jpThemeLight = String(!dark);
            document.body.dataset.jpThemeName = dark ? 'JupyterLab Dark' : 'JupyterLab Light';
        }

    }
    applyTheme();
    document.addEventListener('DOMContentLoaded', applyTheme);
    window.addEventListener('pageshow', applyTheme);
    window.addEventListener('storage', applyTheme);
    preference.addEventListener('change', applyTheme);
})();

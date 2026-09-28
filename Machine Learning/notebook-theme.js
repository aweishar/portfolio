(() => {
    const systemTheme = window.matchMedia('(prefers-color-scheme: dark)');
    function applyTheme() {
        let savedTheme;
        try { savedTheme = localStorage.getItem('theme'); } catch (_) { /* Use system preference. */ }
        const dark = savedTheme === 'dark' || (!savedTheme && systemTheme.matches);
        document.documentElement.classList.toggle('dark', dark);
        if (document.body) {
            document.body.dataset.jpThemeLight = String(!dark);
            document.body.dataset.jpThemeName = dark ? 'JupyterLab Dark' : 'JupyterLab Light';
        }
    }
    applyTheme();
    document.addEventListener('DOMContentLoaded', applyTheme);
    window.addEventListener('pageshow', applyTheme);
    window.addEventListener('storage', event => {
        if (event.key === 'theme' || event.key === null) applyTheme();
    });
    systemTheme.addEventListener('change', applyTheme);
})();

const siteHeader = document.getElementById('site-header');
const menuToggle = document.getElementById('menu-toggle');
const compactMenu = document.getElementById('compact-menu');
const compactViewport = window.matchMedia('(max-width: 47.999rem), (max-width: 64rem) and (max-aspect-ratio: 1/1)');

function closeMenu(restoreFocus = false) {
    compactMenu.hidden = true;
    menuToggle.setAttribute('aria-expanded', 'false');
    menuToggle.setAttribute('aria-label', 'Open menu');
    if (restoreFocus) menuToggle.focus();
}

menuToggle.addEventListener('click', () => {
    const opening = compactMenu.hidden;
    compactMenu.hidden = !opening;
    menuToggle.setAttribute('aria-expanded', String(opening));
    menuToggle.setAttribute('aria-label', opening ? 'Close menu' : 'Open menu');
});

compactMenu.addEventListener('click', event => {
    if (event.target.closest('a')) closeMenu(true);
});

document.addEventListener('keydown', event => {
    if (event.key === 'Escape' && !compactMenu.hidden) closeMenu(true);
});

document.addEventListener('click', event => {
    if (!compactMenu.hidden && !siteHeader.contains(event.target)) closeMenu();
});

compactViewport.addEventListener('change', () => {
    const focusInMenu = compactMenu.contains(document.activeElement) || document.activeElement === menuToggle;
    closeMenu();
    if (focusInMenu) {
        (compactViewport.matches ? menuToggle : siteHeader.querySelector('.desktop-menu a')).focus();
    }
});


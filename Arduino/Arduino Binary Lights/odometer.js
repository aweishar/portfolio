// Match the entry and display behavior in BinaryLights.ino.
class BinaryOdometer {
    constructor() { this.pending = 0; this.displayed = 0; }
    digit(value) {
        this.pending = this.pending >= 409 ? 4095 : this.pending * 10 + value;
    }
    finalize() { this.displayed = this.pending; this.pending = 0; }
    clear() { this.pending = 0; this.displayed = 0; }
    bits() { return this.displayed.toString(2).padStart(12, '0'); }
}

if (typeof document !== 'undefined') {
    const counter = new BinaryOdometer();
    const cells = [...document.querySelectorAll('[data-bit]')];
    const status = document.getElementById('status');

    function render() {
        document.getElementById('entry-value').textContent = counter.pending;
        document.getElementById('displayed').textContent = counter.displayed;
        document.getElementById('binary').textContent = counter.bits().match(/.{4}/g).join(' ');
        const weights = [];
        cells.forEach(cell => {
            const weight = 2 ** Number(cell.dataset.bit);
            const on = Boolean(counter.displayed & weight);
            cell.classList.toggle('on', on);
            cell.querySelector('.bit').textContent = on ? '1' : '0';
            if (on) weights.push(weight);
        });
        document.querySelector('.led-row').setAttribute('aria-label',
            `12 LEDs, binary ${counter.bits().split('').join(' ')}, decimal ${counter.displayed}`);
        document.getElementById('sum').textContent = weights.length
            ? `${weights.join(' + ')} = ${counter.displayed}` : 'All lights are off.';
    }

    function press(key) {
        if (/^[0-9]$/.test(key)) {
            counter.digit(Number(key));
            status.textContent = `Pending: ${counter.pending}. Press Display or Enter to light the LEDs.`;
        } else if (key === 'finalize') {
            counter.finalize();
            status.textContent = `Displaying ${counter.displayed}. Ready for the next number.`;
        } else if (key === 'clear') {
            counter.clear();
            status.textContent = 'Cleared. All lights are off.';
        }
        render();
    }

    document.querySelectorAll('[data-key]').forEach(button => {
        button.addEventListener('click', () => press(button.dataset.key));
    });
    document.addEventListener('keydown', event => {
        if (event.ctrlKey || event.metaKey || event.altKey || event.repeat) return;
        // Preserve native keyboard activation for focused links and buttons.
        if (event.key === 'Enter' && event.target.closest('a, button, summary')) return;
        const key = /^[0-9]$/.test(event.key) ? event.key
            : event.key === 'Enter' ? 'finalize' : event.key === 'Escape' ? 'clear' : null;
        if (key) {
            event.preventDefault();
            press(key);
        }
    });
    render();
}

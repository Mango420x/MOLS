(() => {
  'use strict';

  const storageKey = 'theme';
  const themeToggleId = 'theme-toggle';
  const themeToggleIconId = 'theme-toggle-icon';

  const getStoredTheme = () => localStorage.getItem(storageKey);
  const setStoredTheme = (theme) => localStorage.setItem(storageKey, theme);

  const getPreferredTheme = () => {
    const storedTheme = getStoredTheme();
    if (storedTheme === 'light' || storedTheme === 'dark') {
      return storedTheme;
    }
    return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light';
  };

  const setTheme = (theme) => {
    document.documentElement.setAttribute('data-bs-theme', theme);
  };

  const getNextTheme = (theme) => (theme === 'dark' ? 'light' : 'dark');

  const updateToggleUi = (theme) => {
    const icon = document.getElementById(themeToggleIconId);
    if (!icon) return;

    icon.classList.remove('bi-moon-stars', 'bi-sun');
    icon.classList.add(theme === 'dark' ? 'bi-sun' : 'bi-moon-stars');
  };

  const applyTheme = (theme) => {
    setTheme(theme);
    updateToggleUi(theme);
  };

  // Keep theme aligned if user changes OS theme and they didn't force a choice.
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
    const storedTheme = getStoredTheme();
    if (storedTheme !== 'light' && storedTheme !== 'dark') {
      applyTheme(getPreferredTheme());
    }
  });

  window.addEventListener('DOMContentLoaded', () => {
    const currentTheme = getPreferredTheme();
    applyTheme(currentTheme);

    const toggleButton = document.getElementById(themeToggleId);
    if (!toggleButton) return;

    toggleButton.addEventListener('click', () => {
      toggleButton.classList.add('is-toggling');
      const nextTheme = getNextTheme(document.documentElement.getAttribute('data-bs-theme') || currentTheme);
      setStoredTheme(nextTheme);
      applyTheme(nextTheme);

      window.setTimeout(() => {
        toggleButton.classList.remove('is-toggling');
      }, 240);
    });
  });
})();

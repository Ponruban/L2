import { useCallback, useRef, useEffect } from 'react';

// Accessibility hook for managing focus, keyboard navigation, and ARIA announcements
export const useAccessibility = () => {
  const focusTrapRef = useRef<HTMLDivElement>(null);
  const focusableElementsRef = useRef<HTMLElement[]>([]);

  // Get all focusable elements within a container
  const getFocusableElements = useCallback((container: HTMLElement): HTMLElement[] => {
    const focusableSelectors = [
      'button:not([disabled])',
      'input:not([disabled])',
      'select:not([disabled])',
      'textarea:not([disabled])',
      'a[href]',
      '[tabindex]:not([tabindex="-1"])',
      '[contenteditable="true"]',
      'summary',
      '[role="button"]:not([disabled])',
      '[role="link"]',
      '[role="menuitem"]',
      '[role="tab"]',
      '[role="option"]',
    ].join(', ');

    return Array.from(container.querySelectorAll(focusableSelectors)) as HTMLElement[];
  }, []);

  // Focus trap for modals and dialogs
  const setupFocusTrap = useCallback((container: HTMLElement) => {
    const focusableElements = getFocusableElements(container);
    focusableElementsRef.current = focusableElements;

    if (focusableElements.length === 0) return;

    const firstElement = focusableElements[0];
    const lastElement = focusableElements[focusableElements.length - 1];

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Tab') {
        if (event.shiftKey) {
          if (document.activeElement === firstElement) {
            event.preventDefault();
            lastElement.focus();
          }
        } else {
          if (document.activeElement === lastElement) {
            event.preventDefault();
            firstElement.focus();
          }
        }
      }
    };

    container.addEventListener('keydown', handleKeyDown);
    firstElement.focus();

    return () => {
      container.removeEventListener('keydown', handleKeyDown);
    };
  }, [getFocusableElements]);

  // Announce message to screen readers
  const announce = useCallback((message: string, priority: 'polite' | 'assertive' = 'polite') => {
    const announcement = document.createElement('div');
    announcement.setAttribute('aria-live', priority);
    announcement.setAttribute('aria-atomic', 'true');
    announcement.style.position = 'absolute';
    announcement.style.left = '-10000px';
    announcement.style.width = '1px';
    announcement.style.height = '1px';
    announcement.style.overflow = 'hidden';
    announcement.textContent = message;

    document.body.appendChild(announcement);

    // Remove the announcement after it's been read
    setTimeout(() => {
      document.body.removeChild(announcement);
    }, 1000);
  }, []);

  // Move focus to next/previous element
  const moveFocus = useCallback((direction: 'next' | 'previous', container?: HTMLElement) => {
    const targetContainer = container || document.body;
    const focusableElements = getFocusableElements(targetContainer);
    
    if (focusableElements.length === 0) return;

    const currentIndex = focusableElements.findIndex(el => el === document.activeElement);
    let nextIndex: number;

    if (direction === 'next') {
      nextIndex = currentIndex < focusableElements.length - 1 ? currentIndex + 1 : 0;
    } else {
      nextIndex = currentIndex > 0 ? currentIndex - 1 : focusableElements.length - 1;
    }

    focusableElements[nextIndex].focus();
  }, [getFocusableElements]);

  // Handle arrow key navigation
  const handleArrowNavigation = useCallback((
    event: KeyboardEvent,
    options: {
      onArrowUp?: () => void;
      onArrowDown?: () => void;
      onArrowLeft?: () => void;
      onArrowRight?: () => void;
      preventDefault?: boolean;
    } = {}
  ) => {
    const { onArrowUp, onArrowDown, onArrowLeft, onArrowRight, preventDefault = true } = options;

    switch (event.key) {
      case 'ArrowUp':
        if (preventDefault) event.preventDefault();
        onArrowUp?.();
        break;
      case 'ArrowDown':
        if (preventDefault) event.preventDefault();
        onArrowDown?.();
        break;
      case 'ArrowLeft':
        if (preventDefault) event.preventDefault();
        onArrowLeft?.();
        break;
      case 'ArrowRight':
        if (preventDefault) event.preventDefault();
        onArrowRight?.();
        break;
    }
  }, []);

  // Handle Enter and Space key activation
  const handleActivation = useCallback((
    event: KeyboardEvent,
    onActivate: () => void,
    preventDefault = true
  ) => {
    if (event.key === 'Enter' || event.key === ' ') {
      if (preventDefault) event.preventDefault();
      onActivate();
    }
  }, []);

  // Escape key handler
  const handleEscape = useCallback((
    event: KeyboardEvent,
    onEscape: () => void,
    preventDefault = true
  ) => {
    if (event.key === 'Escape') {
      if (preventDefault) event.preventDefault();
      onEscape();
    }
  }, []);

  // Skip to main content link
  const createSkipLink = useCallback(() => {
    const skipLink = document.createElement('a');
    skipLink.href = '#main-content';
    skipLink.textContent = 'Skip to main content';
    skipLink.style.position = 'absolute';
    skipLink.style.top = '-40px';
    skipLink.style.left = '6px';
    skipLink.style.zIndex = '1000';
    skipLink.style.padding = '8px 16px';
    skipLink.style.backgroundColor = '#000';
    skipLink.style.color = '#fff';
    skipLink.style.textDecoration = 'none';
    skipLink.style.borderRadius = '4px';
    skipLink.style.fontSize = '14px';
    skipLink.style.fontWeight = 'bold';

    skipLink.addEventListener('focus', () => {
      skipLink.style.top = '6px';
    });

    skipLink.addEventListener('blur', () => {
      skipLink.style.top = '-40px';
    });

    return skipLink;
  }, []);

  // Auto-hide skip link when not needed
  useEffect(() => {
    const skipLink = createSkipLink();
    document.body.appendChild(skipLink);

    return () => {
      if (document.body.contains(skipLink)) {
        document.body.removeChild(skipLink);
      }
    };
  }, [createSkipLink]);

  return {
    setupFocusTrap,
    announce,
    moveFocus,
    handleArrowNavigation,
    handleActivation,
    handleEscape,
    getFocusableElements,
    focusTrapRef,
  };
};

// Hook for managing ARIA live regions
export const useAriaLive = () => {
  const announce = useCallback((message: string, priority: 'polite' | 'assertive' = 'polite') => {
    const announcement = document.createElement('div');
    announcement.setAttribute('aria-live', priority);
    announcement.setAttribute('aria-atomic', 'true');
    announcement.style.position = 'absolute';
    announcement.style.left = '-10000px';
    announcement.style.width = '1px';
    announcement.style.height = '1px';
    announcement.style.overflow = 'hidden';
    announcement.textContent = message;

    document.body.appendChild(announcement);

    setTimeout(() => {
      if (document.body.contains(announcement)) {
        document.body.removeChild(announcement);
      }
    }, 1000);
  }, []);

  return { announce };
};

// Hook for managing focus restoration
export const useFocusRestoration = () => {
  const previousFocusRef = useRef<HTMLElement | null>(null);

  const saveFocus = useCallback(() => {
    previousFocusRef.current = document.activeElement as HTMLElement;
  }, []);

  const restoreFocus = useCallback(() => {
    if (previousFocusRef.current) {
      previousFocusRef.current.focus();
      previousFocusRef.current = null;
    }
  }, []);

  return { saveFocus, restoreFocus };
};

// Hook for managing keyboard shortcuts
export const useKeyboardShortcuts = () => {
  const shortcutsRef = useRef<Map<string, () => void>>(new Map());

  const registerShortcut = useCallback((key: string, handler: () => void) => {
    shortcutsRef.current.set(key, handler);
  }, []);

  const unregisterShortcut = useCallback((key: string) => {
    shortcutsRef.current.delete(key);
  }, []);

  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => {
      const key = [
        event.ctrlKey && 'Ctrl',
        event.altKey && 'Alt',
        event.shiftKey && 'Shift',
        event.metaKey && 'Meta',
        event.key.toUpperCase(),
      ].filter(Boolean).join('+');

      const handler = shortcutsRef.current.get(key);
      if (handler) {
        event.preventDefault();
        handler();
      }
    };

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, []);

  return { registerShortcut, unregisterShortcut };
}; 
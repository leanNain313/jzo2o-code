---
name: ui-ux-pro-max
description: UI/UX design intelligence for web and mobile. Includes style systems, color palettes, typography pairings, accessibility, interaction, responsive layout, and chart guidance across React, Next.js, Vue, Svelte, SwiftUI, React Native, Flutter, Tailwind, and shadcn/ui. Use when the user asks to plan, build, review, fix, or improve UI/UX for pages, components, dashboards, landing pages, or app interfaces.
---

# UI/UX Pro Max

Comprehensive UI/UX guidance for building production-grade web and mobile interfaces with strong visual quality and usability.

## When To Use

Apply this skill when the user asks to:
- Design or implement UI components/pages
- Improve visual quality or consistency
- Audit accessibility and interaction quality
- Choose colors, typography, or style direction
- Build dashboards, landing pages, admin panels, SaaS pages, e-commerce, or mobile screens

## Workflow

1. Identify context first:
   - Product type (SaaS, ecommerce, portfolio, dashboard, etc.)
   - Audience and tone (professional, playful, premium, minimal, etc.)
   - Platform (web/mobile)
   - Tech stack (React, Vue, Next.js, Tailwind, etc.)
2. Produce a design direction:
   - Style family
   - Color system (primary, neutral, semantic, states)
   - Typography pairing (display/body)
   - Spacing, radius, shadows, motion rhythm
3. Implement with UX safeguards:
   - Accessibility and contrast
   - Responsive behavior
   - Interaction feedback and loading/error states
4. Verify before delivery:
   - Keyboard and focus behavior
   - Light/dark readability
   - No layout shift on hover/async content
   - Mobile viewport sanity

## Priority Rules

### 1) Accessibility (Critical)
- Maintain at least 4.5:1 contrast for normal text.
- Keep focus states visible on all interactive controls.
- Add labels for inputs and `aria-label` for icon-only buttons.
- Ensure keyboard tab order matches visual flow.
- Provide alt text for meaningful images.

### 2) Interaction (Critical)
- Keep touch targets at least 44x44px.
- Show clear hover/focus/active states.
- Disable controls during async submit to prevent double actions.
- Place validation errors near the field and explain correction.

### 3) Layout & Responsive (High)
- Build mobile-first and test common breakpoints.
- Avoid horizontal overflow on small screens.
- Reserve space for async content to reduce jumping.
- Use a consistent container and spacing system.

### 4) Typography & Color (High)
- Use readable body sizes (16px+ on mobile).
- Keep body line-height around 1.5-1.75.
- Limit line length for reading comfort.
- Use consistent semantic color tokens for states.

### 5) Motion & Feedback (Medium)
- Use short transitions (150-300ms).
- Animate with `transform` and `opacity` when possible.
- Respect `prefers-reduced-motion`.
- Provide skeleton/loading affordances for slow content.

### 6) Data Visualization (Medium)
- Match chart type to question (trend, comparison, composition, distribution).
- Use accessible palettes and do not rely on color alone.
- Provide table alternatives for critical data accessibility.

## Style Selection Guidance

- **SaaS/Admin**: clean, minimal, strong information hierarchy.
- **E-commerce**: visual merchandising, trust markers, clear CTAs.
- **Portfolio/Brand**: expressive typography, tighter visual identity.
- **Fintech/Healthcare**: clarity, trust, conservative motion, strong contrast.

Default to consistency over novelty. One coherent style system is better than mixed aesthetics.

## UI Quality Checklist

- [ ] No emoji used as UI icons; use a consistent icon set.
- [ ] Interactive elements show pointer/focus/hover cues.
- [ ] Contrast is sufficient in both light and dark themes.
- [ ] Fixed headers/overlays do not hide content.
- [ ] Layout remains stable across loading and hover states.
- [ ] Works at small, medium, and large breakpoints.
- [ ] Forms include labels, helpful errors, and disabled submit states.

## Output Pattern

When asked to implement or refactor UI, respond with:
1. Brief design direction (style, color, typography, interaction)
2. Component/page structure
3. Accessibility and responsive decisions
4. Final implementation

## Anti-Patterns

- Mixing multiple unrelated visual styles in one screen.
- Relying on color alone for status or validation.
- Hidden focus rings or keyboard-inaccessible controls.
- Hover effects that move layout and cause jitter.
- Low-contrast text on translucent surfaces.

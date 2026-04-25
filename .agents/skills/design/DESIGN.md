---
name: design
description: Sistema de diseño basado en Material3 para la aplicación GenogramIA con colores, formas, tipografías, componentes y recomendaciones.
---

# Design System Document

## 1. Overview & Creative North Star: "GenogramIA"

The core of this design system is the **"GenogramIA"** We are not building a clinical database; we are crafting a digital heirloom. Genealogy is deeply personal, often emotional, and requires a balance of rigorous data clarity and human empathy. 

To achieve a "High-End Editorial" feel, we move away from the rigid, boxed-in layouts of traditional software. Instead, we embrace **Soft Minimalism**. The design breaks the "template" look through intentional asymmetry, generous breathing room (white space as a first-class citizen), and a sophisticated layering of surfaces. By prioritizing tonal depth over structural lines, we create an interface that feels like a curated collection of memories rather than a spreadsheet of names.

---

## 1.1 Visual References
For direct visual references of app screens, check next directory:
`[.agents/screens](file:///.agents/screens)`

This directory contains screenshots and mockups that must being followed during implementation.

---

## 2. Colors: Tonal Empathy

Our palette is rooted in soft neutrals to provide a calm, trustworthy foundation. We use precise accent colors to categorize without overwhelming the user's emotional experience.

### Color Tokens
*   **Primary (The Lead):** `#006565` (Professional Teal). Used for primary actions and brand presence. Use gradients transitioning to `primary_container` (`#008080`) for hero CTAs to add "soul" and depth.
*   **Secondary (The Masculine/Stable):** `#136299` (Soft Blue). Represents male lineage or stable connections.
*   **Tertiary (The Feminine/Nurturing):** `#884364` (Soft Pink/Plum). Represents female lineage or nurturing connections.
*   **Neutrals:** The `surface` series (`#f9f9f9` to `#dadada`) provides the "paper" on which the history is written.

### The "No-Line" Rule
**Explicit Instruction:** Prohibit 1px solid borders for sectioning. Boundaries must be defined solely through background color shifts. For example, a `surface_container_low` section sitting on a `surface` background creates a clear but soft distinction.

### Surface Hierarchy & Nesting
Treat the UI as a series of physical layers—like stacked sheets of fine vellum. 
- **Level 0 (App Background):** `surface` (`#f9f9f9`)
- **Level 1 (Main Content Areas):** `surface_container_low` (`#f3f3f3`)
- **Level 2 (Interactive Cards):** `surface_container_lowest` (`#ffffff`)

### The "Glass & Gradient" Rule
Floating elements (like navigation bars or hovering genealogy nodes) should utilize **Glassmorphism**. Use `surface_container_lowest` at 80% opacity with a `backdrop-blur` of 20px. This allows the complex family tree lines to softly bleed through, maintaining context and elegance.

---

## 3. Typography: The Editorial Voice

We use a dual-font strategy to balance modern authority with high-performance legibility.

*   **Display & Headlines (Manrope):** A modern geometric sans-serif with an editorial flair. Used for names and major section headers. The high x-height and unique character shapes convey a "Trustworthy & Modern" brand identity.
*   **Body & Labels (Public Sans):** A high-legibility typeface designed for clarity. This is used for dates, vitals (birth/death), and data entry. It ensures that even at `label-sm` (`0.6875rem`), the information is crisp and accessible for all age groups.

**Hierarchy as Identity:**
- **`display-md` (Manrope):** Use for the main Subject Name in a profile.
- **`title-sm` (Public Sans):** Use for metadata (e.g., "BORN 1924 • DIED 1998") with increased letter-spacing (0.05em) for an archival feel.

---

## 4. Elevation & Depth: Tonal Layering

Traditional drop shadows are too heavy for an empathetic app. We use **Tonal Layering** to convey importance.

*   **The Layering Principle:** Depth is achieved by "stacking." Place a `surface_container_lowest` card on top of a `surface_container_high` background to create a natural, soft lift.
*   **Ambient Shadows:** If a card must "float" (e.g., a dragged node), use a shadow with a 32px blur, 0% spread, and 6% opacity using a tint of `on_surface` (`#1a1c1c`). This mimics natural, soft room light.
*   **The "Ghost Border" Fallback:** If a border is required for accessibility, use `outline_variant` at **15% opacity**. Never use a 100% opaque border.
*   **Glassmorphism:** Use for "Elegant Bottom Sheets." The sheet should be `surface_container_lowest` with a blur effect, making the data entry feel like a light overlay rather than a jarring new screen.

---

## 5. Components

### Family Member Cards
- **Structure:** No borders. Use `surface_container_lowest` with a `xl` (`1.5rem`) corner radius.
- **Content:** The name in `title-md` (Manrope) and dates in `label-md` (Public Sans).
- **Accents:** Use a subtle 4px vertical "accent bar" on the left side of the card using `secondary` or `tertiary` to denote gender/lineage.

### Connecting Lines (Genogram Paths)
- **Style:** Use `outline_variant` for lines.
- **Weight:** 1.5px. Avoid 1px (too thin/aliased) or 2px (too heavy). 
- **Corners:** Use the `sm` (`0.25rem`) roundedness for elbow joints in the tree to maintain the "Soft" aesthetic.

### Buttons & Actions
- **Primary Action:** `primary` background with `on_primary` text. Use `full` (pill) roundedness. 
- **Secondary Action:** No background. Use `primary` text with a `surface_container_high` hover state.
- **Data Entry Bottom Sheets:** Use `xl` top-corner radius. Ensure the "handle" at the top is a subtle `outline_variant` pill.

### Input Fields
- **Style:** "Soft Inset." Use `surface_container_highest` as the background with no border. When focused, transition to a `primary` "Ghost Border" (20% opacity).

---

## 6. Do’s and Don’ts

### Do
- **Do** use asymmetrical margins to create an editorial feel (e.g., a wider left margin for headlines).
- **Do** use `primary_fixed_dim` for disabled states instead of grey, to keep the UI feeling "alive."
- **Do** prioritize vertical white space (using the `xl` or `lg` scale) to separate family branches instead of divider lines.

### Don’t
- **Don't** use pure black (`#000000`) for text. Always use `on_surface` (`#1a1c1c`) to maintain a soft, premium contrast.
- **Don't** use standard Material Design elevation shadows. Stick to Tonal Layering.
- **Don't** cram data. If a family card is getting crowded, use a "Reveal on Tap" pattern to maintain the minimalist aesthetic.
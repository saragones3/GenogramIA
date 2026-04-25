# REQUIREMENTS — GenogramIA

> **Standard:** IEEE 830 / BDD-style User Stories with Gherkin acceptance criteria.
> **Language:** English (technical standard). UI copy in Spanish as per designs.
> **Version:** 1.0.0 — 2026-04-25

---

## Table of Contents

1. [US-001 — Session Detection on Launch](#us-001)
2. [US-002 — User Registration](#us-002)
3. [US-003 — User Login](#us-003)
4. [US-004 — Social Login (Google / Apple)](#us-004)
5. [US-005 — Password Recovery](#us-005)
6. [US-006 — Change Password](#us-006)
7. [US-007 — Log Out](#us-007)
8. [US-008 — Delete Account](#us-008)
9. [US-009 — Guest Mode](#us-009)
10. [US-010 — Home Screen (Authenticated)](#us-010)
11. [US-011 — Create New Genogram Tree](#us-011)
12. [US-012 — Open Existing Tree](#us-012)
13. [US-013 — Navigate the Infinite Canvas](#us-013)
14. [US-014 — Add a Person to the Tree](#us-014)
15. [US-015 — Edit a Person](#us-015)
16. [US-016 — Delete a Person](#us-016)
17. [US-017 — Add Medical Condition to a Person](#us-017)
18. [US-018 — Edit / Delete Medical Condition](#us-018)
19. [US-019 — Select Two Nodes to Create a Relationship](#us-019)
20. [US-020 — Define Relationship Type](#us-020)
21. [US-021 — Edit / Delete a Relationship](#us-021)
22. [US-022 — Persistence for Authenticated Users](#us-022)
23. [US-023 — Persistence for Guest Users](#us-023)
24. [US-024 — Legends / Visual Vocabulary Screen](#us-024)
25. [US-025 — Search Trees](#us-025)
26. [US-026 — Settings Screen](#us-026)

---

## US-001 — Session Detection on Launch {#us-001}

**As a** user opening the app,  
**I want** the app to detect whether I am logged in or not,  
**So that** I see the right home screen without having to log in every time.

```gherkin
Feature: Session detection on app launch

  Scenario: Authenticated user opens the app
    Given the user has a valid active session stored on the device
    When the app finishes loading
    Then the app navigates to the authenticated Home screen
    And a personalised greeting "Welcome back, [Name]" is displayed
    And the bottom navigation bar shows "Trees", "Legends" and "Settings" tabs

  Scenario: Unauthenticated user opens the app
    Given there is no active session stored on the device
    When the app finishes loading
    Then the app navigates to the Guest Home screen
    And a "Log In" button is visible in the top-right corner
    And the bottom navigation bar shows only "Trees" and "Legends" tabs
```

---

## US-002 — User Registration {#us-002}

**As a** new user,  
**I want** to create an account with my name, email and password,  
**So that** my genograms are saved to the cloud.

```gherkin
Feature: User registration

  Scenario: Successful registration with email and password
    Given the user is on the Registration screen
    When the user enters a valid full name
    And the user enters a valid email address
    And the user enters a password of at least 8 characters
    And the user taps "Registrarse"
    Then a new account is created in Firebase Auth
    And the user is redirected to the authenticated Home screen
    And a welcome message is shown

  Scenario: Registration with an already-used email
    Given the user is on the Registration screen
    When the user enters an email that is already registered
    And the user taps "Registrarse"
    Then an error message is shown indicating the email is already in use
    And the user remains on the Registration screen

  Scenario: Registration with a weak password
    Given the user is on the Registration screen
    When the user enters a password shorter than 8 characters
    And the user taps "Registrarse"
    Then an inline validation error is shown on the password field
    And the user remains on the Registration screen

  Scenario: Registration with an invalid email format
    Given the user is on the Registration screen
    When the user enters a string that is not a valid email address
    And the user taps "Registrarse"
    Then an inline validation error is shown on the email field

  Scenario: Navigation to Login from Registration
    Given the user is on the Registration screen
    When the user taps "Inicia Sesión"
    Then the app navigates to the Login screen
```

---

## US-003 — User Login {#us-003}

**As a** registered user,  
**I want** to log in with my email and password,  
**So that** I can access my saved genograms.

```gherkin
Feature: User login with email and password

  Scenario: Successful login
    Given the user is on the Login screen
    When the user enters their registered email
    And the user enters the correct password
    And the user taps "Iniciar Sesión"
    Then the app authenticates the user via Firebase Auth
    And the user is redirected to the authenticated Home screen

  Scenario: Login with wrong password
    Given the user is on the Login screen
    When the user enters their registered email
    And the user enters an incorrect password
    And the user taps "Iniciar Sesión"
    Then an error message is shown indicating invalid credentials
    And the user remains on the Login screen

  Scenario: Login with unregistered email
    Given the user is on the Login screen
    When the user enters an email not associated with any account
    And the user taps "Iniciar Sesión"
    Then an error message is shown indicating the account does not exist

  Scenario: Navigation to Registration from Login
    Given the user is on the Login screen
    When the user taps "Regístrate"
    Then the app navigates to the Registration screen

  Scenario: Navigation back from Login
    Given the user is on the Login screen
    When the user taps the back arrow
    Then the app navigates to the previous screen (Guest Home)
```

---

## US-004 — Social Login (Google / Apple) {#us-004}

**As a** user,  
**I want** to register or log in using my Google or Apple account,  
**So that** I do not need to remember a separate password.

```gherkin
Feature: Social authentication

  Scenario: Successful registration via Google
    Given the user is on the Registration screen
    When the user taps "Continuar con Google"
    And the user selects or confirms a Google account
    Then a new account is created linked to that Google account
    And the user is redirected to the authenticated Home screen

  Scenario: Login via existing Google account
    Given the user is on the Login screen
    When the user taps the Google social login button
    And the user selects the Google account already linked
    Then the user is authenticated and redirected to the authenticated Home screen

  Scenario: Social login cancelled by user
    Given the user is on the Login or Registration screen
    When the user starts the Google or Apple OAuth flow
    And the user cancels the flow
    Then the app returns to the Login or Registration screen without error
```

---

## US-005 — Password Recovery {#us-005}

**As a** registered user who forgot their password,  
**I want** to receive a password reset email,  
**So that** I can regain access to my account.

```gherkin
Feature: Password recovery

  Scenario: Requesting a password reset email
    Given the user is on the Login screen
    When the user taps "¿Olvidaste tu contraseña?"
    Then a dialog or screen appears asking for the user's email
    When the user enters their registered email and confirms
    Then Firebase Auth sends a password reset email to that address
    And a confirmation message is displayed to the user

  Scenario: Password reset for unregistered email
    Given the user requests a password reset
    When the user enters an email not associated with any account
    Then an error message is shown indicating the email is not registered
```

---

## US-006 — Change Password {#us-006}

**As an** authenticated user,  
**I want** to change my password from the Settings screen,  
**So that** I can keep my account secure.

```gherkin
Feature: Change password

  Scenario: Successful password change
    Given the user is on the "Cambiar Contraseña" screen
    When the user enters a valid new password (at least 8 characters)
    And the user re-enters the same password in the confirmation field
    And the user taps "Guardar Contraseña"
    Then the password is updated in Firebase Auth
    And a success confirmation message is shown
    And the last-updated timestamp is refreshed

  Scenario: Passwords do not match
    Given the user is on the "Cambiar Contraseña" screen
    When the user enters different values in the new password and confirmation fields
    And the user taps "Guardar Contraseña"
    Then an inline error is shown indicating the passwords do not match
    And the password is not updated

  Scenario: New password is too weak
    Given the user is on the "Cambiar Contraseña" screen
    When the user enters a password shorter than 8 characters
    Then an inline validation error is shown on the password field
```

---

## US-007 — Log Out {#us-007}

**As an** authenticated user,  
**I want** to log out of the app,  
**So that** my account is protected on shared devices.

```gherkin
Feature: Log out

  Scenario: User logs out successfully
    Given the user is on the Settings screen
    When the user taps "Log Out"
    Then a confirmation dialog is shown
    When the user confirms the action
    Then the session is cleared from the device
    And the app navigates to the Guest Home screen

  Scenario: User cancels log out
    Given the user is on the Settings screen
    When the user taps "Log Out"
    And a confirmation dialog is shown
    When the user cancels the action
    Then the user remains on the Settings screen with the session intact
```

---

## US-008 — Delete Account {#us-008}

**As an** authenticated user,  
**I want** to permanently delete my account,  
**So that** all my personal data is removed from the service.

```gherkin
Feature: Delete account

  Scenario: User deletes their account
    Given the user is on the Settings screen
    When the user taps "Delete Account"
    Then a confirmation dialog warns the user that this action is irreversible
    When the user confirms the deletion
    Then the account and all associated genogram data are deleted from Firebase
    And the app navigates to the Guest Home screen

  Scenario: User cancels account deletion
    Given the confirmation dialog for account deletion is shown
    When the user taps "Cancel"
    Then the dialog closes and no data is deleted
```

---

## US-009 — Guest Mode {#us-009}

**As a** guest (unauthenticated) user,  
**I want** to explore and edit genograms locally without registering,  
**So that** I can try the app before committing to an account.

```gherkin
Feature: Guest mode

  Scenario: Guest user accesses the home screen
    Given the user has not logged in
    When the app launches
    Then the Guest Home screen is shown with a "GUEST PREVIEW" badge on the sample tree card
    And a "Log In" button is visible in the top navigation bar

  Scenario: Guest user creates a temporary tree
    Given the user is on the Guest Home screen
    When the user taps "Start Your First Tree"
    Then the user can create and edit a genogram tree in memory
    And a notice informs the user that changes will be lost when the app is closed

  Scenario: Guest user data is lost on app close
    Given a guest user has created a tree in memory
    When the user closes the application
    Then all unsaved data is discarded
    And no data is written to Firebase

  Scenario: Guest user taps "Log In"
    Given the user is on the Guest Home screen
    When the user taps the "Log In" button
    Then the app navigates to the Login screen
```

---

## US-010 — Home Screen (Authenticated) {#us-010}

**As an** authenticated user,  
**I want** to see all my genogram trees listed on the home screen,  
**So that** I can quickly access or manage them.

```gherkin
Feature: Authenticated Home screen

  Scenario: User has existing trees
    Given the user is authenticated and has at least one tree
    When the user opens the Home screen
    Then a list of tree cards is displayed showing each tree's name, ancestor count and last-updated date
    And the primary tree card is visually highlighted as "PRIMARY LINEAGE"
    And a "Start New Tree" card is shown below the list

  Scenario: User has no trees yet
    Given the user is authenticated but has no trees created
    When the user opens the Home screen
    Then only the "Start New Tree" card is displayed
    And a welcome message encourages the user to create their first tree

  Scenario: User searches for a tree
    Given the user is on the Home screen
    When the user taps the search bar and types a tree name
    Then the list filters in real time to show only matching trees
```

---

## US-011 — Create New Genogram Tree {#us-011}

**As a** user,  
**I want** to create a new genogram tree by entering a central person's data,  
**So that** I have a starting point for my family history.

```gherkin
Feature: Create new genogram tree

  Scenario: Authenticated user creates a new tree
    Given the user taps "Start New Tree" on the Home screen
    When the "Crear Árbol" screen is shown
    And the user enters at least a first name and last name for the central person
    And the user taps "Continuar y Crear Árbol"
    Then a new tree document is created in Cloud Firestore
    And the app navigates to the Genogram Canvas screen with the central person's node visible
    And the node is positioned at the centre of the canvas

  Scenario: Guest user creates a new tree
    Given the guest user taps "Start Your First Tree"
    When the "Crear Árbol" screen is shown
    And the user fills in the central person's data
    And the user taps "Continuar y Crear Árbol"
    Then a new tree is created in local memory only
    And the app navigates to the Genogram Canvas screen

  Scenario: User attempts to create a tree without a name
    Given the user is on the "Crear Árbol" screen
    When the user leaves the first name and last name fields empty
    And the user taps "Continuar y Crear Árbol"
    Then inline validation errors are shown on the required fields
    And the tree is not created

  Scenario: User optionally adds birth/death dates on creation
    Given the user is on the "Crear Árbol" screen
    When the user enters a date of birth and/or date of death for the central person
    Then those dates are stored with the person's record

  Scenario: User optionally adds biological sex and orientation on creation
    Given the user is on the "Crear Árbol" screen
    When the user selects a biological sex (Male / Female)
    And the user selects an orientation (Heterosexual / Other)
    Then those attributes are stored and the node is rendered with the correct genogram symbol (square for male, circle for female)
```

---

## US-012 — Open Existing Tree {#us-012}

**As an** authenticated user,  
**I want** to open one of my saved trees from the Home screen,  
**So that** I can continue editing where I left off.

```gherkin
Feature: Open existing tree

  Scenario: User opens a tree from the Home screen
    Given the user is on the Home screen and has at least one tree
    When the user taps "Open Archive" on a tree card
    Then the app navigates to the Genogram Canvas screen for that tree
    And all previously saved nodes and relationships are rendered on the canvas
```

---

## US-013 — Navigate the Infinite Canvas {#us-013}

**As a** user viewing a genogram,  
**I want** to pan and zoom the canvas freely,  
**So that** I can explore large family trees that exceed the screen size.

```gherkin
Feature: Infinite canvas navigation

  Scenario: User pans the canvas
    Given the user is on the Genogram Canvas screen
    When the user performs a drag gesture on an empty area of the canvas
    Then the canvas viewport shifts in the direction of the drag
    And nodes maintain their relative positions

  Scenario: User zooms in
    Given the user is on the Genogram Canvas screen
    When the user taps the "+" zoom button in the bottom toolbar
    Or performs a pinch-open gesture on the canvas
    Then the canvas scale increases and nodes appear larger

  Scenario: User zooms out
    Given the user is on the Genogram Canvas screen
    When the user taps the "−" zoom button in the bottom toolbar
    Or performs a pinch-close gesture on the canvas
    Then the canvas scale decreases and more of the tree becomes visible

  Scenario: User resets the viewport
    Given the user is on the Genogram Canvas screen
    When the user taps the "centre / target" button in the bottom toolbar
    Then the viewport resets to the default zoom level and centres on the primary node
```

---

## US-014 — Add a Person to the Tree {#us-014}

**As a** user editing a genogram,  
**I want** to add a new person node to the canvas,  
**So that** I can represent a family member in the tree.

```gherkin
Feature: Add person to tree

  Scenario: User adds a new person via the FAB button
    Given the user is on the Genogram Canvas screen in view mode
    When the user taps the "+" FAB button
    Then the "Add Family Member" form sheet opens

  Scenario: User saves a new person with required data
    Given the "Add Family Member" form sheet is open
    When the user enters a first name and last name
    And the user taps "Save Member Record"
    Then a new node is added to the canvas
    And it is rendered as a square (male) or circle (female) per the selected biological sex
    And the node displays the person's name and birth year below it

  Scenario: User saves a new person with full data
    Given the "Add Family Member" form sheet is open
    When the user fills in first name, last name, date of birth, date of death, biological sex and orientation
    And the user taps "Save Member Record"
    Then the node is created with all data stored
    And deceased persons are rendered with an X overlay on their symbol

  Scenario: User cancels adding a person
    Given the "Add Family Member" form sheet is open
    When the user navigates back without saving
    Then no node is created and the canvas is unchanged
```

---

## US-015 — Edit a Person {#us-015}

**As a** user,  
**I want** to edit the details of an existing person node,  
**So that** I can correct or update their information.

```gherkin
Feature: Edit person

  Scenario: User opens the edit form for a node
    Given the user is on the Genogram Canvas screen in edit mode
    When the user taps on a person's node
    Then the node shows a selection highlight (teal border)
    And the edit FAB changes to a pencil icon
    When the user taps the pencil FAB
    Then the "Add Family Member" form sheet opens pre-filled with the person's existing data

  Scenario: User updates a person's data
    Given the edit form is open with existing data
    When the user modifies one or more fields
    And the user taps "Save Member Record"
    Then the node is updated on the canvas
    And the changes are persisted to Firestore (authenticated) or in memory (guest)
```

---

## US-016 — Delete a Person {#us-016}

**As a** user,  
**I want** to delete a person from the tree,  
**So that** I can remove incorrect or unwanted entries, provided they meet the safety criteria.

```gherkin
Feature: Delete person

  Scenario: User successfully deletes a person node
    Given the user has a person node selected in edit mode
    And the person has no descendants in the tree
    And the person has no relationships of type "Marriage", "Separation", or "Divorce"
    When the user chooses the delete action
    Then a confirmation dialog is shown
    When the user confirms deletion
    Then the node is removed from the canvas
    And any cohabitation relationships connected to that node are also removed
    And the deletion is persisted to Firestore (authenticated) or in memory (guest)

  Scenario: User attempts to delete a person with descendants
    Given the user has a person node selected in edit mode
    And the person has at least one descendant in the tree
    When the user chooses the delete action
    Then an error message is shown: "No se puede borrar una persona con descendientes"
    And the node remains on the canvas

  Scenario: User attempts to delete a person with formal relationships
    Given the user has a person node selected in edit mode
    And the person has a relationship of type "Marriage", "Separation", or "Divorce" (or is marked as Widowed)
    When the user chooses the delete action
    Then an error message is shown: "No se puede borrar una persona con relaciones de matrimonio, divorcio o viudedad"
    And the node remains on the canvas

  Scenario: User cancels deletion
    Given the confirmation dialog for node deletion is shown
    When the user taps "Cancel"
    Then the node and its relationships remain unchanged
```

---

## US-017 — Add Medical Condition to a Person {#us-017}

**As a** user,  
**I want** to add known medical conditions to a family member's profile,  
**So that** I can track hereditary health patterns in the genogram.

```gherkin
Feature: Add medical condition

  Scenario: User opens the add-disease bottom sheet
    Given the "Add Family Member" or edit form is open
    When the user taps "+ ADD" next to "Medical History"
    Then the "Añadir Enfermedad" bottom sheet slides up
    And a search field and a list of "CONDICIONES COMUNES" chips are displayed

  Scenario: User selects a condition from common conditions
    Given the "Añadir Enfermedad" bottom sheet is open
    When the user taps one of the common condition chips (e.g. "Hipertensión")
    Then the condition is highlighted as selected

  Scenario: User searches for a specific condition
    Given the "Añadir Enfermedad" bottom sheet is open
    When the user types a condition name in the search field
    Then the list filters to show matching conditions

  Scenario: User sets a diagnosis date and confirms
    Given a condition has been selected
    When the user sets a diagnosis date using the date picker
    And the user taps "Añadir al Historial"
    Then the condition is added to the person's medical history list
    And it appears as a card in the Medical History section with name and diagnosis date

  Scenario: User cancels adding a condition
    Given the "Añadir Enfermedad" bottom sheet is open
    When the user taps "Cancel" or dismisses the sheet
    Then no condition is added
```

---

## US-018 — Edit / Delete Medical Condition {#us-018}

**As a** user,  
**I want** to edit or remove a medical condition from a person's history,  
**So that** I can keep the health data accurate.

```gherkin
Feature: Edit and delete medical condition

  Scenario: User removes a medical condition
    Given the edit form is open and the Medical History section shows at least one condition
    When the user performs a delete action on a condition card
    Then a confirmation prompt is shown
    When the user confirms
    Then the condition is removed from the person's medical history
    And the change is persisted

  Scenario: User edits a medical condition's diagnosis date
    Given the edit form is open with an existing condition
    When the user taps the condition card to edit it
    Then the "Añadir Enfermedad" sheet opens pre-filled with the existing data
    When the user changes the date and confirms
    Then the condition is updated
```

---

## US-019 — Select Two Nodes to Create a Relationship {#us-019}

**As a** user editing a genogram,  
**I want** to select two person nodes to define a relationship between them,  
**So that** the family structure is correctly represented.

```gherkin
Feature: Select nodes for relationship creation

  Scenario: User selects the first node
    Given the user is on the Genogram Canvas in edit mode
    When the user taps a person node
    Then the node gets a teal selection border

  Scenario: User selects a second node
    Given one node is already selected
    When the user taps a different person node
    Then both nodes are highlighted with selection borders
    And an "Añadir relación" tooltip/label appears between the two selected nodes

  Scenario: User taps on an empty area to deselect
    Given one or two nodes are selected
    When the user taps an empty area of the canvas
    Then all selections are cleared
    And the "Añadir relación" label disappears
```

---

## US-020 — Define Relationship Type {#us-020}

**As a** user,  
**I want** to define the type of relationship between two selected persons,  
**So that** the genogram accurately represents the family bond.

```gherkin
Feature: Define relationship type

  Scenario: User opens the Define Connection screen
    Given two person nodes are selected on the canvas
    When the user taps "Añadir relación"
    Then the "Define Connection" screen opens
    And both persons are shown in a relationship preview at the top
    And a list of bond types is displayed: Marriage, Separation, Biological Offspring, Adoption/Legal

  Scenario: User selects a bond type
    Given the "Define Connection" screen is open
    When the user taps a bond type (e.g. "Marriage")
    Then that bond type is highlighted as selected

  Scenario: User sets an effective date for the relationship
    Given the "Define Connection" screen is open
    When the user taps the date field under "EFFECTIVE DATE"
    Then a date picker opens
    When the user selects a date and confirms
    Then the date is displayed in the field

  Scenario: User sets the validation status
    Given the "Define Connection" screen is open
    When the user selects "Clinical" or "Reported" under "VALIDATION STATUS"
    Then the chosen status is highlighted

  Scenario: System shows a medical conflict warning
    Given the relationship between the two selected persons would create a consanguinity risk
    When the "Define Connection" screen opens
    Then a "Consanguinity Risk" warning banner is displayed in red under "MEDICAL CONFLICT"

  Scenario: User confirms the connection
    Given all required fields are filled on the "Define Connection" screen
    When the user taps "CONFIRM CONNECTION"
    Then the relationship is created and a connecting line is drawn between the two nodes on the canvas
    And the line style reflects the bond type (solid for marriage, dashed for separation, etc.)
    And the relationship is persisted to Firestore (authenticated) or in memory (guest)

  Scenario: User cancels defining a connection
    Given the "Define Connection" screen is open
    When the user taps the back arrow
    Then no relationship is created and the canvas is unchanged
```

---

## US-021 — Edit / Delete a Relationship {#us-021}

**As a** user,  
**I want** to edit or delete an existing relationship between two persons,  
**So that** I can correct errors or update changed circumstances.

```gherkin
Feature: Edit and delete relationship

  Scenario: User taps on a relationship line
    Given the user is on the Genogram Canvas in edit mode
    When the user taps on a connecting line between two nodes
    Then the line is highlighted and options to edit or delete appear

  Scenario: User edits a relationship
    Given a relationship line is selected
    When the user chooses to edit it
    Then the "Define Connection" screen opens pre-filled with the existing relationship data
    When the user modifies the data and taps "CONFIRM CONNECTION"
    Then the relationship is updated on the canvas and in the data store

  Scenario: User deletes a relationship
    Given a relationship line is selected
    When the user chooses to delete it
    Then a confirmation dialog is shown
    When the user confirms
    Then the line is removed from the canvas
    And the relationship is deleted from Firestore (authenticated) or from memory (guest)
```

---

## US-022 — Persistence for Authenticated Users {#us-022}

**As an** authenticated user,  
**I want** my genogram changes to be automatically saved to the cloud,  
**So that** I never lose my work and can access it from any device.

```gherkin
Feature: Cloud persistence for authenticated users

  Scenario: Changes are saved after every significant action
    Given the user is authenticated and on the Genogram Canvas screen
    When the user adds, edits, or deletes a person, a relationship, or a medical condition
    Then the change is written to Cloud Firestore within a few seconds
    And no manual "save" action is required from the user

  Scenario: User reopens the app and sees their latest data
    Given the user has made changes in a previous session
    When the user authenticates and opens the same tree
    Then all nodes, relationships and medical data are loaded from Firestore and displayed correctly
```

---

## US-023 — Persistence for Guest Users {#us-023}

**As a** guest user,  
**I want** my changes to persist for as long as the app is open,  
**So that** I can work on a tree during a single session without interruption.

```gherkin
Feature: In-memory persistence for guest users

  Scenario: Guest user edits a tree within a session
    Given a guest user has created or modified a tree
    When the user navigates between screens within the same app session
    Then all changes are retained in memory and visible when returning to the canvas

  Scenario: Guest data is lost when the app is closed
    Given a guest user has a tree in memory
    When the user closes the application completely
    Then the data is discarded and is not recoverable

  Scenario: Guest user is reminded their data is temporary
    Given a guest user creates or edits a tree
    Then the app displays a persistent but unobtrusive banner or notice indicating that data will be lost unless the user creates an account
```

---

## US-024 — Legends / Visual Vocabulary Screen {#us-024}

**As a** user,  
**I want** to consult the genogram symbol legend,  
**So that** I can understand what each shape and line style represents.

```gherkin
Feature: Legends screen

  Scenario: User navigates to the Legends tab
    Given the user is on any screen with the bottom navigation bar
    When the user taps the "Legends" tab
    Then the Legends screen is shown
    And it displays four sections: Basic Symbols, Relationships, Complex Links and Emotional Bonds

  Scenario: Basic Symbols section is visible
    Given the user is on the Legends screen
    Then the "BASIC SYMBOLS" section shows: Male (square), Female (circle), Patient (outlined square), Deceased (square with X)

  Scenario: Relationships section is visible
    Given the user is on the Legends screen
    Then the "RELATIONSHIPS" section shows: Marriage (solid line), Separation (dashed line), Divorce (line with double slash), Cohabitation (dotted line)

  Scenario: Complex Links section is visible
    Given the user is on the Legends screen
    Then the "COMPLEX LINKS" section shows: Adoption, Pregnancy and Abortion symbols with labels

  Scenario: Emotional Bonds section is visible
    Given the user is on the Legends screen
    Then the "EMOTIONAL BONDS" section shows: Close (double line), Fused (triple line) and Hostile (zigzag line)
```

---

## US-025 — Search Trees {#us-025}

**As an** authenticated user,  
**I want** to search for a specific genogram tree by name,  
**So that** I can quickly navigate to it when I have many trees.

```gherkin
Feature: Search trees on Home screen

  Scenario: User searches and finds a matching tree
    Given the user is on the authenticated Home screen
    When the user taps the search bar at the top
    And the user types a tree name or partial name
    Then the list of tree cards filters in real time to show only matching results

  Scenario: User searches and finds no matches
    Given the user is searching on the Home screen
    When the typed query does not match any tree name
    Then an empty state is shown indicating no trees match the search

  Scenario: User clears the search
    Given the user has typed a search query
    When the user clears the search field
    Then the full list of trees is restored
```

---

## US-026 — Settings Screen {#us-026}

**As an** authenticated user,  
**I want** to access a Settings screen,  
**So that** I can view my profile information and manage my account security.

```gherkin
Feature: Settings screen

  Scenario: User navigates to Settings tab
    Given the user is authenticated and on any screen with the bottom navigation bar
    When the user taps the "Settings" tab
    Then the Settings screen is shown
    And the user's profile information (name, email, and avatar) is displayed at the top
    And the Security section is visible with a "Change Password" option
    And "Log Out" and "Delete Account" actions are available

  Scenario: User navigates to Change Password
    Given the user is on the Settings screen
    When the user taps the "Change Password" option under Security
    Then the app navigates to the Change Password screen (US-006)

  Scenario: User initiates Log Out
    Given the user is on the Settings screen
    When the user taps "Log Out"
    Then the Log Out flow is initiated (US-007)

  Scenario: User initiates Delete Account
    Given the user is on the Settings screen
    When the user taps "Delete Account"
    Then the Delete Account flow is initiated (US-008)
```

---

*End of REQUIREMENTS.md — GenogramIA v1.0.0*

This plugin adds a special “alchemical” interface to Minecraft for combining potions and creating “mega potions” with multiple uses. Here are its main features from the admin’s perspective:

* **Command `/alchemist`** — opens the special alchemist menu.
* **Alchemist interface** — a separate inventory with custom design (fillers, buttons, result slot).
* **Potion combination mechanics**:

    * The player places identical potions into special slots.
    * If the potions match by effect, it becomes possible to create a “mega potion” with multiple uses.
    * The number of uses depends on how many identical potions were combined.
* **Cost** — creating a mega potion requires money (Vault-compatible economy). If the player doesn’t have enough, they will see a message in the interface.
* **Obtaining the mega potion** — after payment, the player receives a potion that can be used multiple times. After each use, the number of remaining charges decreases.
* **Informative interface** — displays the cost, number of charges, insufficient funds messages, etc.
* **Safety** — the plugin prevents placing invalid items into crafting slots and protects against dupes and errors.
* **Compatibility** — works with Vault, supports customization via `config.json`.

**Gameplay scenario:**

1. The player types `/alchemist`.
2. In the opened menu, they place identical potions into special slots.
3. If the conditions are met, a button to create a mega potion appears, showing the cost.
4. After payment, the player receives a potion with multiple uses.
5. With each use, the number of charges decreases until the potion disappears.

# Alchemist

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

---

# Example `config.json`

```json
{
  "size": 27,
  "title": "Alchemist",
  "fillers": [
    {
      "slot": 0,
      "type": "orange_stained_glass_pane",
      "displayName": "",
      "lore": [],
      "count": 1
    },
    {
      "slot": 22,
      "type": "orange_stained_glass_pane",
      "displayName": "<#FF8600>Info",
      "lore": [
        "",
        "<white>With the help of an alchemist for a small amount of coins",
        "<white>you can combine potions into a single whole."
      ],
      "count": 1
    }
  ],
  "startFiller": {
    "slot": 0,
    "type": "green_stained_glass_pane",
    "displayName": "<white>Place the potions on the alchemist's table <#FFC93C>to merge",
    "lore": [],
    "count": 1
  },
  "notEnoughMoneyFiller": {
    "slot": 0,
    "type": "RED_WOOL",
    "displayName": "<red>You don't have enough coins.",
    "lore": [],
    "count": 1
  },
  "resultSlot": 16,
  "displayName": "<bold><#FF8600>Big potion",
  "loreAfterBuy": [
    "",
    "<#FFFFFF>Uses: <#FF9100>{times}"
  ],
  "loreBeforeBuy": [
    "",
    "<#FFFFFF>Uses: <#FF9100>{times}",
    "",
    "<#FFFFFF>Cost: <#FF9100>{cost}"
  ],
  "costPerLevel": 200,
  "potionDuration": 6000,
  "potionAmplifier": 1
}
```

---

# Parameter Documentation

### `size`

* **Type:** Integer
* **Meaning:** Size of the inventory (must be a multiple of 9).
* **Example:** `27` → inventory with 3 rows.

---

### `title`

* **Type:** String
* **Meaning:** The GUI title. Supports **MiniMessage** hex-colors.
---

### `fillers`

* **Type:** List of `ConfigItem`

* **Meaning:** Decorative or information items placed in specific slots.

* **Parameters:**

  * `slot`: slot index (0–26 for size 27).
  * `type`: Minecraft material (`orange_stained_glass_pane`, `red_wool`, etc.).
  * `displayName`: name of the item (can be empty).
  * `lore`: description (array of strings).
  * `count`: stack size (usually `1`).

* **Example:**

  ```json
  {
    "slot": 22,
    "type": "orange_stained_glass_pane",
    "displayName": "<#FF8600>Info",
    "lore": ["", "You can merge potions here."],
    "count": 1
  }
  ```

---

### `startFiller`

* **Type:** `ConfigItem`
* **Meaning:** Item shown in the result slot when no valid potion combination is present.
* **Example:** Green glass pane with text “Положите зелья для слияния”.

---

### `notEnoughMoneyFiller`

* **Type:** `ConfigItem`
* **Meaning:** Shown in the result slot when the player doesn’t have enough coins.

---

### `resultSlot`

* **Type:** Integer
* **Meaning:** Slot index where the resulting potion (or error placeholder) will appear.
* **Example:** `16` (row 2, column 8 in a 27-slot GUI).

---

### `displayName`

* **Type:** String
* **Meaning:** Name of the crafted potion. Supports **MiniMessage gradients**.
* **Example:** `"<bold><#FF8600>Большая бутылка"`

---

### `loreBeforeBuy`

* **Type:** String\[]
* **Meaning:** Tooltip text shown before purchasing the crafted potion.
* **Supports placeholders:**

  * `{times}` → number of potion uses.
  * `{cost}` → cost in coins.

---

### `loreAfterBuy`

* **Type:** String\[]
* **Meaning:** Tooltip text shown after purchasing. Usually shows remaining uses.

---

### `costPerLevel`

* **Type:** Integer
* **Meaning:** The price (in coins) per merged potion effect.
* **Example:** `200` → 3 merged potions = 600 coins.

---

### `potionDuration`

* **Type:** Integer (ticks)
* **Meaning:** Duration of the potion effect.
* **Example:** `6000` ticks = 5 minutes.

---

### `potionAmplifier`

* **Type:** Integer
* **Meaning:** Strength of the potion effect.
* **Example:** `1` = Potion II.

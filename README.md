Item Burner adds **Item Burner** which can be used to smelt blocks and tools into **experience**!
**Blocks and Items** can be hoppered into the burner or can be inserted through a GUI!

---

### Content:

#### Item Burner
- Item Burner can burn items!
- Items can be inserted via the GUI

#### Chronoflux
- Custom fluid representing player's experience.
- 1 mB of Chronoflux equals to 1 experience point

---

### Conversion:
- If the item is stackable:
  - `mB = 64 / maxStack`
  - This means that:
    - 1 Cobblestone will generate 1 mB of Chronoflux
    - 1 Ender Pearl will generate 4 mB of Chronoflux
    - 1 Boat will generate 64 mB of Chronoflux
- If the item is a tool:
  - `mB = ( 64 * currentDurability ) / maxDurability`
  - This means that:
    - New Iron Sword will generate 64 mB of Chronoflux
    - Slightly used Iron Sword (200 / 250 durability) will generate 51 mB of Chronoflux
- On top of that base there's added an enchantment bonus:
  - For each enchantment add it's level
  - This means that:
    - New Iron Sword with Sharpness 4 will generate 68 mB of Chronoflux
    - New Iron Pickaxe with Efficiency 4, Unbreaking 3 and Mending will generate 72 mB of Chronoflux

# Design Document

A Minecraft mod adding creatures to the **cold biomes** (snowy biomes, frozen
oceans, icy mountains, and the deep frozen underground).

**Core fantasy:** survival horror & ecology in the cold. Most of the danger
revolves around a shared **Freeze system** (extending vanilla powder-snow
freezing). Build and tune that system *first* — almost every mob plugs into it.

---

## Table of Contents

1. Banshee 
2. HearthDeer 
3. Frostinger
4. Cryospore
5. Icesquatch
6. Cross-cutting systems
7. Scope & roadmap
8. Team & ownership

---

## 1. Banshee

*Spirit / stealth predator*

Banshees slowly roam snowy biomes **at night**, hunting players. They are the
mod’s signature scare.

### Appearance

- A **three-headed ghost** with a **floating, legless body** that tapers
downward into nothing.
- Made of **clear ice** with **bones and material suspended inside** the ice.
- **Lanky arms that float detached** around the body (disconnected = reads as
supernatural).
- A few **aura rings** orbiting the body (tilted, slowly rotating).
- **Eyes are the most important asset** — each of the three heads has its own
glowing pair. Fully emissive (visible at full-bright), readable from far away.

**Modeling notes / current draft critique:**

- Build enough internal volume so suspended bones read inside the clear ice.
- Design the **three faces as swappable face-textures** on the same head model,
so phase changes are a texture swap, not a remodel.
- Trailing wispy lower body instead of legs (cheaper to animate, floatier).

### Stealth / visibility

- In **light level ≤ 7 (darkness)**, only the Banshee’s **glowing eyes** are
visible; the rest of the body is fully invisible.
- When light level rises or a light source is placed nearby (torch, lantern,
etc.), the Banshee’s **full form is revealed**.

### Phases (cycle at random intervals)

The Banshee has three behavioral phases, indicated by its face. They cycle at
**random intervals** so players always second-guess a spotted Banshee.
- **Watching** — effectively **passive**; makes no moves toward the player.
- **Stalking** — **neutral**; slowly follows the player, not yet trying to harm.
- **Hunting** — **hostile**; actively pursues the player over long distances.

### Combat

- **Blizzard Wail** — a ghastly scream dealing heavy damage. Used rarely; should
have a wind-up tell (rears back) and knockback so it’s survivable/dodgeable.
*(Damage number is a placeholder — tune later.)*
- **Frozen Gaze** — while the player holds eye contact with the Banshee:
    - **No direct damage from looking.**
    - First, the player gets **Slowness**.
    - After **3 seconds of continuous eye contact**, the player begins to
    **freeze** (powder-snow-style freeze) in addition to the slow.
    - Looking away stops/reverses the effect (clear escape).
- **Frigid Phantasm** — only used when the Banshee is **below 1/3 health**.
The Banshee turns semi-invisible (regardless of light level) and spawns up to
**3 decoy “glowing eyes”** to confuse the player. The **real Banshee’s eyes
pulse**, so attentive players can tell it apart from the steady decoys.
Lasts up to 15 seconds.

### Drops

- **Frozen Wisp** (1/3 chance) — main drop. Used to:
    - Brew **Freezing Potions**.
    - Craft the **Wailing Cloak** (chestplate): grants the Banshee’s semi-invis —
    turns the player invisible (armor included) **until they get near a light
    source** (the balancing constraint).

---

## 2. HearthDeer

*Passive helper & mount*

A cozy, fluffy cross between a **llama and a deer** with a **campfire theme**.
Emits ember particles, warm-colored fur (gentle fire, not literal flames).
Found in snowy biomes in **herds of 3–5**.

### Appearance / modeling notes

- Llama body chunkiness + deer-thin legs = cozy top-heavy silhouette.
- Warm gradient fur (deep orange belly → cream tips). Ember particles from
back/antlers.
- **Antlers as a separate model piece** (they get shed — see drops).

### Passive aura (nearby players)

- Reduction to freezing.
- Regeneration.
*(Consider: wild herds give freeze resistance only; full regen aura tuned so
early herds aren’t a god-buff.)*

### Taming & breeding

- Tamed with **wheat, carrots, or glow berries**.
- **Breeding / babies** — renewable warmth source

### Tamed uses

- Can be **saddled and ridden** at moderate speed; walks straight over
**powder snow**.
- **Banshee radar** — makes a warning sound when a Banshee is nearby.
**(Locked behind taming.)**
- **Ember defense** — shoots embers dealing short fire damage to defend the
player. **(Locked behind taming.)**

### Drops

- **Warm Fur** (shearable) — crafts:
    - New “wool”-like blocks / carpets with a special pattern.
    - **Fur Jacket** (worn): freeze damage reduction.
- **Ember Antlers** (periodically shed) — use is TBD

---

## 3. Frostinger

*Big ice-crystal wasps.*

Rare in snowy biomes. Hover over the ground. **Attack spiders**. Extremely hostile to players.

### Appearance / modeling notes

- Big crystalline-ice wasp; faceted/low-poly translucent body parts (cheap,
on-theme).
- Stinger = a clear icicle — emphasize it (it’s the “gun”).
- **Soldier variant = same model scaled up** + thicker/darker chitin texture.

### Combat

- **Stinger artillery** — rapidly shoots low-damage ice spikes. *(Add a
burst-then-cooldown rhythm.)*
- **Imbued Strike** (rare) — explodes on impact, applying a random negative
effect in a radius (like a splash potion).

### Frostinger Spire (dungeon)

- Rare structures in snowy biomes housing multiple Frostingers.
- Built of **Glacial Wax** (new decorative block).
- Contains **Chilled Nectar** blocks — used to craft important food items in the
mod (and **tames HearthDeer**).
- **Soldier Frostingers** — beefier (extra health/damage) Spire guards.

### Drops

- **Frostinger Abdomen** — crafts the **Stinger Cannon**: shoots ice spikes rapidly; or, with a **potion in the offhand**, fires an **imbued spike** carrying that potion’s effects (consumes
the potion).
- **Glacial Chitin** — used in the Stinger Cannon recipe; also crafts:
    - **Glacite Shield** — on block, **freezes the attacker for 1.5 seconds**
    (on-theme twist on a thorns shield).

---

## 4. Cryospore

*Environmental hazard*

A mysterious pale-blue **fungal colony** deep beneath frozen biomes. Hazardous
but very useful.

### Colony blocks

- **Cryospore Mycelium**, **Cryospore Buds**, **Cryospore Tendrils**.
- All emit a **faint blue glow**.
- In complete darkness, colonies slowly spread across stone & deepslate, growing
Buds and Tendrils.

### Sensitivities (must be learnable in ~1 encounter)

- **Light** pauses growth.
- **Heat** (torches, campfires, lava, HearthDeer, etc.) pauses growth.

### States & escalation

- Buds/Tendrils are **Inactive** (calm) or **Active**.
- A player/mob entering the colony **activates nearby Buds** → they release
**freezing spores** (freeze damage).
- If a target is heavily frozen, **Tendrils awaken** and reach toward victims —
the colony feels like it’s trying to consume what’s trapped inside.
- **Frenzy** (occasional): Cryospores spread very rapidly toward any entity and
try to kill it.

### Cryospore Dust (from harvesting Buds)

- Accelerates colony growth.
- Crafts **Cryospore Anchors**.
- (More uses TBD.)

### Cryospore Anchors

- Two Anchors in the same colony become **linked**.
- Standing on one **teleports** the player to the other.
- Large colonies become **transportation networks** — the standout reward.

### Infected Frostinger

- A Frostinger that dies to a Cryospore Frenzy becomes an **Infected
Frostinger**.
- Tamed with **Cryospore Dust** → **rideable flying mount** that shoots ice
spikes on command.
- Rarely, **Infected Frostinger Spires** appear on the surface.

---

## 5. Icesquatch

*The forager — a gentle giant you befriend, not fight.*

A tall (4–5 blocks) furry humanoid/ape lurking in snowy forests & mountains.
Big chest, long arms, short legs, top-heavy gorilla-yeti proportions. Walks
slowly; timid; doesn’t want to interact with the player.

**Identity:** the mountain's quiet caretaker — a **forager, not a fighter**. It
wanders alone, digging through snow and under logs, **collecting things** and
carrying its finds in its fur. Every other mob in the roster is something you
*use*; the Icesquatch is the one you *care about*. Intended emotional arc:
**fear → curiosity → trust → companionship** (the warm counterweight to the
Banshee's slow-burn dread).

### Trust & trading — the "gift trust" loop
*Not* Piglin-style bartering (no throw-gold-get-junk). Trust is earned through
reciprocity:

1. **Wild Icesquatch can't be traded with at all.** Approach and it lumbers
   away. (Deliberately frustrating first phase — it wants nothing to do with
   you.)
2. **Earn trust with offerings.** Drop food it likes (sweet/warm things — sweet
   berries, glow berries, honey) on the ground nearby and back off. A hidden
   **trust meter** rises over repeated gifts. It stops fleeing and lets you
   approach.
3. **At high trust it "trades" by reciprocity.** It roots around in its fur and
   **hands you things it foraged** — a gift exchange between friends, not a
   vending machine.

**Readable trust stages** (always show progress so the grind isn't opaque):
stops fleeing → lets you close → sheds fur near you → trades → settles/defends
its home.

### What it gives — "the mountain's lost & found"
Things the player can't easily get otherwise:
- **Rare foraged loot:** odd seeds, unique peak flowers/saplings, occasional
  buried-treasure trinkets it dug up. A soft alternative to exploration loot.
- **Cross-mod ingredients:** shed **Ember Antlers** (HearthDeer), **Frozen
  Wisps**, **Chilled Nectar** — a trusted Icesquatch becomes a slow passive
  source of other creatures' materials (the synergy hook).
- **Thick Fur, willingly given** — sheds tufts as it forages once comfortable,
  and a trusted one lets you shear it. This is the *main* path to the best
  freeze armor (see Killing, below).

### Killing it (the evil path)
- **Killing it still works and still drops fur + a little extra**, so players who
  want to be evil can do what they want. (No moral lockout.)
- But trust-based fur is the cleaner/better path — friendship is rewarded
  without making the kill a dead end.

> ⚖️ Keep Thick Fur armor a **sidegrade** (freeze-tank) vs HearthDeer Fur Jacket
> and Banshee Wailing Cloak — not a straight power-ladder topper.

### Provoking it — betrayal has weight
- Docile unless provoked. **Hit it → roar + massive knockback** (non-lethal to
  the player, just throws you back and ends the interaction). It never *hunts*
  you; it just leaves. The punishment is loneliness, not damage.
- **Hitting it permanently ends trading with that individual** — once struck, it
  will **never talk to or trade with you again.**
- **The trust meter resets to zero on every hit**, and afterward rebuilding
  costs **1.5× more effort** than before.

### Home territory (no following you home)
- A trusted Icesquatch does **not** follow the player home. Instead it has a
  **dedicated home** — a cave, or a single anchor block / piece of land — and
  **only roams a set radius around it.** That spot is its territory.
- **If its home is a cave, it defends it:** the Icesquatch fights **all hostile
  mobs** that enter its home territory.

### Modeling notes
Biggest model in the mod; heavy fur = many overlapping fur-plane layers
(texture-art heavy). Needs a strong roar animation (chest puff, arms up) and a
slow lumbering walk cycle.

### Still open
- Exact gift table & trust-point values / pacing.
- What (if anything) distinguishes cave-dwelling vs open-land individuals beyond
  the cave-defense behavior.

---

## Cross-cutting systems

### Freeze system (build first)

4 of 5 creatures rely on freezing. Build **one well-tuned freeze system**
(extending vanilla powder-snow freeze) that everything plugs into: Banshee gaze,
Frostinger spikes, Cryospore spores, Freezing Potions, Glacite Shield.

### Armor paths must stay distinct (sidegrades, not a power ladder)

- **Freeze resistance:** Fur Jacket (HearthDeer) / Frigid armor.
- **Stealth:** Wailing Cloak (Banshee) — invis until near light.
- Keep these clearly differentiated so no single armor dominates.

### Inter-mob synergy (a strength to preserve)

- HearthDeer warns of nearby Banshees and its heat pauses Cryospore growth.
- Frostingers can become Infected Frostingers via Cryospore.
- Chilled Nectar (Frostinger Spire) tames HearthDeer.

---

## Team & ownership

- **icy** — lead dev (~75% of code), decent model animator. Helps animate.
- **cyan cat** — second dev (code icy doesn’t/can’t do).
- **toumas** — lead modeler, finalizes all 3D models; main animator; textures.
- **star** — secondary modeler; draft models for Toumas to finalize.
- **sketchy** — lead texture artist; assists Toumas with texturing.

*Draft models by Star → finalized/textured/animated by Toumas (Sketchy assists
texturing, icy assists animating).*
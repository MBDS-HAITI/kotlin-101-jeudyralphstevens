package com.kotlin.one

// ─────────────────────────────────────────────
// INTERFACES
// ─────────────────────────────────────────────

/** Any character that can deal damage */
interface Attacker {
    fun attack(target: Character): String
}

/** Any character that can restore HP to an ally */
interface Healer {
    val healPower: Int
    fun heal(target: Character): String
}

// ─────────────────────────────────────────────
// WEAPON (Composition)
// ─────────────────────────────────────────────

/**
 * A weapon held by a character.
 * @param name    Display name of the weapon
 * @param damage  Base damage dealt per hit
 */
data class Weapon(val name: String, val damage: Int)

// ─────────────────────────────────────────────
// ABSTRACT BASE CLASS
// ─────────────────────────────────────────────

/**
 * Abstract base for all character types.
 * Every subclass must implement [describeAction].
 *
 * @param name    Unique name chosen by the player
 * @param maxHp   Maximum (and starting) hit points
 * @param weapon  The weapon this character carries
 */
abstract class Character(
    val name: String,
    val maxHp: Int,
    val weapon: Weapon
) : Attacker {

    // Encapsulation: HP is managed only through dedicated methods
    var currentHp: Int = maxHp
        private set

    val isAlive: Boolean get() = currentHp > 0

    /** Returns the simple type name for display (e.g. "Warrior") */
    abstract val typeName: String

    /** Reduce HP by [amount], clamped to 0 */
    fun takeDamage(amount: Int) {
        currentHp = (currentHp - amount).coerceAtLeast(0)
    }

    /** Restore HP by [amount], clamped to maxHp */
    fun restoreHp(amount: Int) {
        currentHp = (currentHp + amount).coerceAtMost(maxHp)
    }

    /** Polymorphic attack — uses this character's weapon */
    override fun attack(target: Character): String {
        val dmg = weapon.damage
        target.takeDamage(dmg)
        return "⚔️  $name hits ${target.name} with ${weapon.name} for $dmg damage! " +
                "(${target.name} HP: ${target.currentHp}/${target.maxHp})"
    }

    /** Short description of available actions, shown in the action menu */
    abstract fun describeAction(): String

    /** Status line shown in end-of-game summary */
    fun statusLine(): String {
        val status = if (isAlive) "💚 ALIVE  $currentHp/$maxHp HP" else "💀 DEAD"
        val paddedName = name.padEnd(10)
        val paddedType = typeName.padEnd(8)
        return "  $paddedName | $paddedType | $status"
    }
}

// ─────────────────────────────────────────────
// CONCRETE CHARACTER TYPES  (Inheritance)
// ─────────────────────────────────────────────

/** Balanced fighter — moderate HP and weapon */
class Warrior(name: String) : Character(
    name   = name,
    maxHp  = 120,
    weapon = Weapon("Sword", 25)
) {
    override val typeName = "Warrior"
    override fun describeAction() = "Attack an enemy"
}

/**
 * Spell-caster that can also heal allies.
 * Implements [Healer] in addition to the base [Attacker].
 */
class Magus(name: String) : Character(
    name   = name,
    maxHp  = 140,
    weapon = Weapon("Staff", 15)
), Healer {
    override val typeName = "Magus"
    override val healPower = 30

    override fun heal(target: Character): String {
        target.restoreHp(healPower)
        return "✨ $name heals ${target.name} for $healPower HP! " +
                "(${target.name} HP: ${target.currentHp}/${target.maxHp})"
    }

    override fun describeAction() = "Attack an enemy  |  2. Heal an ally"
}

/** Tank — very high HP, medium weapon */
class Colossus(name: String) : Character(
    name   = name,
    maxHp  = 200,
    weapon = Weapon("War Hammer", 20)
) {
    override val typeName = "Colossus"
    override fun describeAction() = "Attack an enemy"
}

/** Glass cannon — low HP but devastating weapon */
class Dwarf(name: String) : Character(
    name   = name,
    maxHp  = 80,
    weapon = Weapon("Battle Axe", 40)
) {
    override val typeName = "Dwarf"
    override fun describeAction() = "Attack an enemy"
}

// ─────────────────────────────────────────────
// GAME SETUP HELPERS
// ─────────────────────────────────────────────

/** All available type keys */
val availableTypes = listOf("Warrior", "Magus", "Colossus", "Dwarf")

/** Display the type menu with stats */
fun printTypeMenu() {
    println("\n  Available types:")
    println("  1. Warrior  — HP: 120 | Weapon: Sword (25 dmg)  | Can: Attack")
    println("  2. Magus    — HP: 140 | Weapon: Staff (15 dmg)  | Can: Attack, Heal")
    println("  3. Colossus — HP: 200 | Weapon: War Hammer (20) | Can: Attack")
    println("  4. Dwarf    — HP: 80  | Weapon: Battle Axe (40) | Can: Attack")
}

/** Prompt until a valid type index is entered, excluding already-picked types */
fun pickType(usedTypes: MutableList<String>): String {
    while (true) {
        printTypeMenu()
        println("\n  Already used in your team: ${if (usedTypes.isEmpty()) "none" else usedTypes.joinToString()}")
        print("  Enter type number: ")
        when (val input = readln().trim()) {
            "1", "2", "3", "4" -> {
                val chosen = availableTypes[input.toInt() - 1]
                if (chosen in usedTypes) {
                    println("  ⚠️  You already have a $chosen in your team. Pick a different type.")
                } else {
                    return chosen
                }
            }
            else -> println("  ⚠️  Please enter a number between 1 and 4.")
        }
    }
}

/** Prompt until a valid unique name is entered */
fun pickName(allNames: MutableList<String>): String {
    while (true) {
        print("  Enter a unique character name: ")
        val input = readln().trim()
        when {
            input.isBlank()   -> println("  ⚠️  Name cannot be empty.")
            input in allNames -> println("  ⚠️  '$input' is already taken. Choose another name.")
            else              -> { allNames.add(input); return input }
        }
    }
}

/** Build one character from type string + name */
fun createCharacter(type: String, name: String): Character = when (type) {
    "Warrior"  -> Warrior(name)
    "Magus"    -> Magus(name)
    "Colossus" -> Colossus(name)
    else       -> Dwarf(name)
}

/** Full team-creation flow for one player */
fun buildTeam(playerNumber: Int, allNames: MutableList<String>): Player {
    println("\n╔══════════════════════════════════════╗")
    println("║   PLAYER $playerNumber — Build your team        ║")
    println("╚══════════════════════════════════════╝")

    val usedTypes  = mutableListOf<String>()
    val characters = mutableListOf<Character>()

    repeat(3) { slot ->
        println("\n  ── Character ${slot + 1} ──")
        val type = pickType(usedTypes)
        usedTypes.add(type)
        val charName = pickName(allNames)
        characters.add(createCharacter(type, charName))
        println("  ✅ $charName the $type added to your team!")
    }
    return Player(playerNumber, characters)
}

// ─────────────────────────────────────────────
// PLAYER
// ─────────────────────────────────────────────

/**
 * Represents one of the two human players.
 * @param playerNumber  1 or 2, used for display
 * @param team          The three characters they created
 */
class Player(val playerNumber: Int, val team: List<Character>) {
    val name = "Player $playerNumber"
    val aliveCharacters get() = team.filter { it.isAlive }
    val isDefeated      get() = aliveCharacters.isEmpty()
}

// ─────────────────────────────────────────────
// COMBAT HELPERS
// ─────────────────────────────────────────────

/** Print a numbered list of characters with HP */
fun printCharacterList(chars: List<Character>, label: String) {
    println("\n  $label:")
    chars.forEachIndexed { i, c ->
        val filled = (c.currentHp * 10 / c.maxHp).coerceAtLeast(0)
        val hpBar  = "█".repeat(filled) + "░".repeat(10 - filled)
        println("  ${i + 1}. ${c.name} (${c.typeName}) — ${c.currentHp}/${c.maxHp} HP  [$hpBar]")
    }
}

/** Ask the active player to pick one of their alive characters */
fun pickActiveCharacter(player: Player): Character {
    val alive = player.aliveCharacters
    while (true) {
        printCharacterList(alive, "${player.name}'s alive characters")
        print("  Choose your character (1-${alive.size}): ")
        when (val i = readln().trim().toIntOrNull()) {
            null -> println("  ⚠️  Invalid choice.")
            else -> if (i in 1..alive.size) return alive[i - 1]
            else println("  ⚠️  Invalid choice.")
        }
    }
}

/** Ask the player which action to perform (attack or heal if Magus) */
fun pickAction(character: Character): String {
    println("\n  Actions for ${character.name}:")
    println("  1. ${character.describeAction()}")
    while (true) {
        print("  Choose action: ")
        when (val input = readln().trim()) {
            "1"                        -> return "attack"
            "2" -> if (character is Healer) return "heal"
            else println("  ⚠️  $input is not a valid action for ${character.name}.")
            else -> println("  ⚠️  Invalid action.")
        }
    }
}

/** Ask the player to pick a target from a list */
fun pickTarget(targets: List<Character>, label: String): Character {
    while (true) {
        printCharacterList(targets, label)
        print("  Choose target (1-${targets.size}): ")
        when (val i = readln().trim().toIntOrNull()) {
            null -> println("  ⚠️  Invalid choice.")
            else -> if (i in 1..targets.size) return targets[i - 1]
            else println("  ⚠️  Invalid choice.")
        }
    }
}

// ─────────────────────────────────────────────
// MAIN GAME LOOP
// ─────────────────────────────────────────────

fun main() {
    println("╔══════════════════════════════════════════╗")
    println("║         ⚔️   BATTLE ARENA  ⚔️             ║")
    println("╚══════════════════════════════════════════╝")

    val allNames = mutableListOf<String>()

    // ── Phase 1: Team creation ──
    val player1 = buildTeam(1, allNames)
    val player2 = buildTeam(2, allNames)

    println("\n🎮 Both teams are ready. Let the battle begin!\n")

    // ── Phase 2: Battle loop ──
    var turn    = 1
    val players = listOf(player1, player2)

    while (!player1.isDefeated && !player2.isDefeated) {
        val activePlayer = players[(turn - 1) % 2]
        val enemyPlayer  = players[turn % 2]

        println("\n┌─────────────────────────────────────────")
        println("│  TURN $turn — ${activePlayer.name}'s move")
        println("└─────────────────────────────────────────")

        // Step A: pick character
        val actor = pickActiveCharacter(activePlayer)

        // Step B: pick action
        val action = pickAction(actor)

        // Step C: execute action
        val message = when (action) {
            "attack" -> {
                val target = pickTarget(enemyPlayer.aliveCharacters, "Enemy targets")
                actor.attack(target)
            }
            "heal" -> {
                val target = pickTarget(activePlayer.aliveCharacters, "Allies to heal")
                (actor as Healer).heal(target)
            }
            else -> ""
        }

        println("\n  $message")

        // Announce any character that just died this turn
        enemyPlayer.team
            .filter { !it.isAlive }
            .forEach { println("  💀 ${it.name} has been defeated!") }

        turn++
    }

    // ── Phase 3: End of game ──
    val winner = if (!player1.isDefeated) player1 else player2

    println("\n╔══════════════════════════════════════════╗")
    println("║           🏆  GAME OVER  🏆               ║")
    println("╚══════════════════════════════════════════╝")
    println("\n  🎉 ${winner.name} wins after ${turn - 1} turns!\n")
    println("  ── Final status ──")
    println("  ${player1.name}:")
    player1.team.forEach { println(it.statusLine()) }
    println("  ${player2.name}:")
    player2.team.forEach { println(it.statusLine()) }
}

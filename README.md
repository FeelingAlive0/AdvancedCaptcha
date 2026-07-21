<h1 align="center">AdvancedCaptcha</h1>
<p align="center">🛡️ Modern, Stylish, and Secure Captcha System for Minecraft Servers</p>

<p align="center">
  <a href="https://www.spigotmc.org/resources/127093/">
    <img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/spigot_vector.svg" alt="Spigot" />
  </a>
</p>

---

## ✨ Overview

**AdvancedCaptcha** keeps your Minecraft server safe from bots and ensures a smooth experience for real players.

It uses visual and mathematical challenges to verify players on join, combined with an innovative **freeze system** to block movement and interaction until verified. Fully customizable, multilingual, and lightweight—this plugin is the modern solution to an old problem.

> ⚙️ Supports Minecraft 1.21.11

---

## 🧠 Why Choose AdvancedCaptcha?

- 🔘 **Dual Captcha Modes**  
  Colorful button captchas or simple math problems (addition, subtraction, multiplication).

- 🧊 **Freeze System**  
  Players can’t move, interact, or chat until verification is complete.

- 🌐 **Multilingual**  
  English, Russian, Ukrainian out of the box. Add your own in seconds.

- 🌈 **Vivid Effects**  
  Customizable messages, sounds, and particles for success/failure.

- 🛠️ **Highly Configurable**  
  Choose captcha types, difficulty, allowed commands, cooldowns, and more.

- ⚡ **Lightweight & Compatible**  
  Runs smoothly with no performance impact on any modern Spigot-based server.

- 🤖 **Built-in Anti-Bot Logic**  
  Macro detection, limited attempts, IP checks—bots don’t stand a chance.

---

## 🔧 Installation

1. Download the latest `.jar` from the [Releases](https://github.com/FeelingAlive0/AdvancedCaptcha/releases) or [SpigotMC page](https://www.spigotmc.org/resources/advancedcaptcha-modern-hub-captcha-system-1-16-5-1-21-5.127093/).
2. Drop it into your server's `/plugins` folder.
3. Restart the server.
4. Configuration and messages will be generated automatically.

---

## ⚙️ Configuration

All settings live in `/plugins/AdvancedCaptcha/config.yml`. Example:

```yaml
# AdvancedCaptcha Configuration
# This file controls the behavior of the AdvancedCaptcha plugin.
# All settings are organized into sections for clarity.

# General settings for the plugin
general:
  # Default language for messages (e.g., 'en', 'ru', 'ua')
  default-language: en
  # Supported languages for player locale detection
  supported-languages:
    - en
    - ru
    - ua
  # Logging level (INFO, DEBUG, WARN)
  log-level: INFO

# Captcha settings
captcha:
  # Type of captcha to use (BUTTON, MATH, SEQUENCE, RANDOM)
  type: RANDOM
  # Captcha types used when captcha.type is RANDOM.
  # SEQUENCE is ignored here if sequence-captcha.enabled is false.
  random-types:
    - BUTTON
    - MATH
    - SEQUENCE
  # Cooldown for re-verification in milliseconds (default: 6 hours = 21600000 ms)
  cooldown: 21600000
  # Minimum number of buttons in captcha (min: 2)
  button-count-min: 4
  # Maximum number of buttons in captcha (max: 10)
  button-count-max: 7
  # Maximum number of attempts to pass captcha
  max-attempts: 2
  # Timeout for captcha completion in seconds
  timeout: 300
  # Commands allowed during captcha verification
  allowed-commands:
    - /login
    - /register
  # Colors used for buttons (supported: DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE)
  button-colors:
    - BLUE
    - GREEN
    - RED
    - YELLOW
    - WHITE
    - GRAY
    - LIGHT_PURPLE
  # Sound and particle effects
  success-sound: ENTITY_PLAYER_LEVELUP
  fail-sound: ENTITY_VILLAGER_NO
  success-particle: HAPPY_VILLAGER
  fail-particle: SMOKE
  # Anti-macro threshold in milliseconds (minimum time to click to avoid macro detection)
  anti-macro-threshold: 200
  # Minimum delay in milliseconds between captcha button clicks.
  # Keep this low enough for SEQUENCE captcha, where several clicks are expected.
  click-spam-threshold: 150
  # Minimum delay in milliseconds between repeated blocked-action messages
  message-throttle: 1500
  # Freeze settings to restrict player actions during captcha
  freeze:
    # Enable or disable freezing player actions
    enabled: true
    # Slowness effect level (1-6, higher values make player slower)
    slowness-level: 6
    # Apply blindness while the player is solving captcha
    blindness: true

# Settings specific to the button captcha
button-captcha:
  # Decoy traps add visually similar wrong options, while still keeping one guaranteed correct option.
  decoy-traps:
    enabled: true
    # Chance that each wrong button becomes a trap instead of a fully random decoy (0-100)
    chance-percent: 70
    # Same text as the target, but a different color
    same-text-different-color: true
    # Same color as the target, but a different text
    same-color-different-text: true
    # Upper/lowercase variants of the target text when the language supports it
    case-variants: true

# Settings specific to the mathematical captcha
math-captcha:
  # Allowed mathematical operations (PLUS, MINUS, MULTIPLY)
  allowed-operations:
    - PLUS
    - MINUS
    - MULTIPLY
  # Minimum value for numbers in math expressions
  number-range-min: 1
  # Maximum value for numbers in math expressions
  number-range-max: 20

# Settings specific to the sequence captcha
sequence-captcha:
  # Enable or disable sequence captcha availability in RANDOM/adaptive pools
  enabled: true
  # Minimum number of correct clicks in sequence
  length-min: 2
  # Maximum number of correct clicks in sequence
  length-max: 3
  # Prefer unique colors in one sequence when enough colors are configured
  unique-colors: true
  # Reset sequence progress after a wrong click
  reset-on-wrong-click: true

# Adaptive difficulty raises challenge complexity for suspicious players.
# It is fully in-memory and automatically expires old profile data.
adaptive-difficulty:
  enabled: true
  # How long player/IP suspicion profiles stay in memory, in milliseconds
  profile-ttl: 1800000
  # Upper bound for accumulated suspicion score
  max-score: 20
  # Score required for medium difficulty
  suspicious-threshold: 3
  # Score required for high difficulty
  high-risk-threshold: 7
  # How much IP reputation contributes to a player's score (0 disables IP influence)
  ip-weight: 0.5
  points:
    # Points added after a wrong answer
    wrong-answer: 2
    # Points added after a macro/too-fast click signal
    fast-click: 4
    # Points added after captcha timeout
    timeout: 3
    # Points removed after a successful captcha
    success-decay: 1
  suspicious:
    # Captcha types preferred for suspicious players when captcha.type is RANDOM
    captcha-types:
      - BUTTON
      - SEQUENCE
    # Extra answer buttons added on this difficulty
    button-count-bonus: 1
    # Extra sequence clicks added on this difficulty
    sequence-length-bonus: 1
    # Extra upper range added to math captcha numbers
    math-range-bonus: 5
  high-risk:
    # Captcha types preferred for high-risk players when captcha.type is RANDOM
    captcha-types:
      - SEQUENCE
      - MATH
    # Extra answer buttons added on this difficulty
    button-count-bonus: 2
    # Extra sequence clicks added on this difficulty
    sequence-length-bonus: 2
    # Extra upper range added to math captcha numbers
    math-range-bonus: 15
```
This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

<h1 align="center">AdvancedCaptcha</h1>
<p align="center">🛡️ Modern, Stylish, and Secure Captcha System for Minecraft Servers</p>

<p align="center">
  <a href="https://www.spigotmc.org/resources/127093/">
    <img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/spigot_vector.svg" alt="Spigot" />
  </a>
  <a href="https://modrinth.com/plugin/advancedcaptcha">
    <img src="https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/compact/available/modrinth_vector.svg" alt="Modrinth" />
  </a>
</p>

---

## ✨ Overview

**AdvancedCaptcha** keeps your Minecraft server safe from bots and ensures a smooth experience for real players.

It uses visual and mathematical challenges to verify players on join, combined with an innovative **freeze system** to block movement and interaction until verified. Fully customizable, multilingual, and lightweight—this plugin is the modern solution to an old problem.

> ⚙️ Supports Minecraft 1.16.5 → 1.21.5

---

## 🧠 Why Choose AdvancedCaptcha?

- 🔘 **Dual Captcha Modes**  
  Colorful button captchas or simple math problems (addition, subtraction, multiplication).

- 🧊 **Freeze System (NEW in 1.2)**  
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
  # Type of captcha to use (BUTTON, MATH, RANDOM)
  type: RANDOM
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
  # Colors used for buttons (supported: BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW, WHITE)
  button-colors:
    - BLUE
    - GREEN
    - RED
    - YELLOW
    - WHITE
    - BLACK
    - GRAY
    - LIGHT_PURPLE
  # Sound and particle effects
  success-sound: ENTITY_PLAYER_LEVELUP
  fail-sound: ENTITY_VILLAGER_NO
  success-particle: VILLAGER_HAPPY
  fail-particle: SMOKE_NORMAL
  # Anti-macro threshold in milliseconds (minimum time to click to avoid macro detection)
  anti-macro-threshold: 200
  # Freeze settings to restrict player actions during captcha
  freeze:
    # Enable or disable freezing player actions
    enabled: true
    # Slowness effect level (1-6, higher values make player slower)
    slowness-level: 6

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

```
This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

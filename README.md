# Battle Goose Telegram Bot

This is a simple Telegram bot game where you level up your "battle goose" by completing tasks.

## Technologies Used

* **Language:** Java
* **Build Tool:** Gradle
* **Library:** [Telegram Bots API](https://github.com/rubenlagus/TelegramBots)

## How to Run the Bot

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   ```
2. **Create a `config.properties` file:**
   In the root of the project, create a file named `config.properties` with the following content:
   ```properties
   bot.username=YOUR_BOT_USERNAME
   bot.token=YOUR_BOT_TOKEN
   ```
   Replace `YOUR_BOT_USERNAME` with your bot's username and `YOUR_BOT_TOKEN` with your bot's token, which you can get from [BotFather](https.t.me/BotFather).

3. **Build the project:**
   ```bash
   .\gradlew.bat build (for Windows)
   ./gradlew build (for Linux/macOS)
   ```

4. **Run the bot:**
   ```bash
   .\gradlew.bat run (for Windows)
   ./gradlew run (for Linux/macOS)
   ```
   Or run the `main` method in the `org.example.Main` class from your IDE.

## How to Play

1. **Start the bot:**
   Open a chat with your bot in Telegram and send the `/start` command.

2. **Level up your goose:**
   You will be presented with a series of tasks. Completing a task will level up your goose and earn you coins.

3. **Reach the final level:**
   The goal is to reach the final level and purchase a "Javelin" for your goose.

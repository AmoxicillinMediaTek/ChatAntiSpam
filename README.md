# Chat Anti-Spam

A client-side Fabric mod for Minecraft 1.21.11 that combines consecutive
duplicate chat messages into one line and appends a count such as `[x5]`.
While the chat screen is open, hover a chat line and click the `C` button at
the right edge to copy that player's complete message to the clipboard.

## Build

Requirements:

- Java 21

Run:

```sh
./gradlew build
```

The finished mod is written to `build/libs/chat-anti-spam-1.0.0.jar`.

Install the jar in the client `mods` folder alongside Fabric Loader and Fabric
API. The mod only changes the local chat HUD; it does not alter server chat or
send messages back to the server.
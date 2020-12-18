# Main

Contains entry method of the whole program, creates InteractionService for ConsoleRenderer

# TextRenderService

Contains and generates shared text resources with ConsoleRenderer and TelnetRenderer. For ConsoleRenderer, returns menu with "start service entry" and for TelnetRenderer returns menu with one-screen or online types of game.

# StreamRenderer : Renderer

Implementation for text-mode game rendering. Can be used for local console rendering or any stream/terminal rendering. Calls TextRenderService to generate game frontend from game values.

# Renderer

Used by GameService, initially created by Main, but can be created by TelnetGameService for online game.

# InteractionService

Handles interaction between Renderer and GameService (and PlayFieldService sometimes). Can start a telnet game server (TelnetNetworknService) if asked by renderer.

# GameService

Handles game status, gives an oportunity to make a turn for current player, initializes and controls playfield, gives information about online game (if it is)

# GameRoom

Contains information about current online player status, their connection, related GameService

# PlayFieldService

Contains methods to control playfield: put things on it, check if there's a win-combination

# TelnetGameService

Contains methods to start the server for the telnet game, also creates GameRoom for each new player, distributes players to rooms.

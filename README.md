# MinecraftUhcPlugin V1.0
- Configurable settings are displayed as `<Setting>`.

## Features
- A lobby (world) is automatically created where players can join before the game starts.
  - Teams are created for players to join.

## Planned
- Players can either play solo or in teams.
  - Friendly fire is off `<FriendlyFire>`.
  - During the `<PveDuration>`, pvp is turned off.
  - You can see teammates through walls (Client mod) `<MarkTeammates>`.
  - Player collision is configured with `<PlayerCollision>`.
  - Player health is shown based on `<ShowPlayerHealth>`.
  - Name tags are shown baded on `<ShowNameTags>`
- Natural regeneration is turned off during the game, so you can only heal using golden apples, potions etc.
  - During the `<GracePeriod>`, your life cannot drop below `<GracePeriodLife>`.
  - Players have a maximum life of `<MaxLife>`, with the exception of absorption health.
  - The freezing effect is turned off with `<Freezing>`.
- When dying all other players are notified in chat and by a sound effect.
  - Your game mode changes to spectator and you can spectate other players (don't spoil anything!).
  - A gravestone marks your death location.
  - Teammates can revive each other by right clicking a gravestone (and sacrificing a golden apple) `<AllowRevival>`.
  - After revival you will have `<RevivedLife>` health.
- Advancements can be turned off with `<Advancements>` to hide information from other players.
- Players are spawned along a circle with radius `<SpawnRadius>`.
  - Teams are evenly spaced out along the circle.
  - Teammates spawn together.
  - If a spawn happens to be in a challenging biome (water, snow, etc) a new spawn point is searched for radially.
  - After spawning, players are invicible for `<InvincibilityPeriod>`.
- The world is zoned by a world border.
  - The world border has an initial radius of `<WorldBorderInitialRadius>`.
  - Between `<ShrinkStart>` and `<ShrinkEnd>`, the world border will shrink to a radius of `<WorldBorderFinalRadius>`.
  - The acceleration of the shrink can be changed with `<ShrinkFactor>` (1 = linear, 2 = quadratic).
  - The first death of a player contributes `<ShrinkDeathBoost>`% to the shrink speed.
- After `<EternalDayStart>` the day-night cycle stops and it will remain noon for the rest of the game.
- The ratio between overworld and nether portal coordinates is configured with `<PortalRatio>`.
- Weather can be controlled by the `<Weather>` setting.
- Your coordinates are displayed in the HUD (Client mod).
- Loot boxes will randomly spawn around the world containing resources, food and gear.
- After `<TrackingStart>` players can track other teams.
  - The location of enemy teams (average position of teammates) will be visible for other teams (Client mod).
  - You don't know who you are tracking, you only see the location.
  - The location is delayed by a time between `<TrackingMinDelay>` and `<TrackingMaxDelay>` based on the difference in total life between the teams.
- To incentivise players to stay above ground either or both of the following mechanics will be possible:
  - Flood: All caves will slowly be filled with water (or lava).
  - Depth Sickness: After staying below ground for too long, you will start to feel some effects which include reduced vision, hearing random sounds (creeper, food steps), seeing particles etc. (nausea is too cruel). If you endure these effects for too long you will also start taking damage. The start and severity of this effect depends on the depth and the duration of the game.

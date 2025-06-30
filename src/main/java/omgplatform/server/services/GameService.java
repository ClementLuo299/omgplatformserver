package omgplatform.server.services;

import omgplatform.server.dto.GameMessage;
import omgplatform.server.entities.User;
import omgplatform.server.utils.LoggingUtil;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Service for managing game state and player interactions.
 *
 * @authors Clement Luo,
 * @date June 29, 2025
 * @since 1.0
 */
@Service
public class GameService {
    
    // Active players in the game
    private final Map<String, GameMessage.PlayerInfo> activePlayers = new ConcurrentHashMap<>();
    
    // Game rooms
    private final Map<String, GameMessage.RoomInfo> gameRooms = new ConcurrentHashMap<>();
    
    // Default room
    private static final String DEFAULT_ROOM = "main";
    
    public GameService() {
        LoggingUtil.info("GameService initialized");
        initializeDefaultRoom();
    }
    
    /**
     * Initialize the default game room
     */
    private void initializeDefaultRoom() {
        GameMessage.RoomInfo defaultRoom = new GameMessage.RoomInfo(
            DEFAULT_ROOM, 
            "Main Lobby", 
            50, 
            0, 
            "OPEN"
        );
        gameRooms.put(DEFAULT_ROOM, defaultRoom);
        LoggingUtil.info("Default game room initialized: " + DEFAULT_ROOM);
    }
    
    /**
     * Add a player to the game
     */
    public GameMessage.PlayerInfo addPlayer(User user) {
        GameMessage.PlayerInfo playerInfo = new GameMessage.PlayerInfo(
            user.getUsername(),
            "ONLINE",
            0, // Starting X position
            0, // Starting Y position
            System.currentTimeMillis()
        );
        
        activePlayers.put(user.getUsername(), playerInfo);
        updateRoomPlayerCount(DEFAULT_ROOM, 1);
        
        LoggingUtil.info("Player added to game", Map.of(
            "username", user.getUsername(),
            "totalPlayers", activePlayers.size()
        ));
        
        return playerInfo;
    }
    
    /**
     * Remove a player from the game
     */
    public GameMessage.PlayerInfo removePlayer(String username) {
        GameMessage.PlayerInfo player = activePlayers.remove(username);
        if (player != null) {
            updateRoomPlayerCount(DEFAULT_ROOM, -1);
            LoggingUtil.info("Player removed from game", Map.of(
                "username", username,
                "totalPlayers", activePlayers.size()
            ));
        }
        return player;
    }
    
    /**
     * Update player position
     */
    public boolean updatePlayerPosition(String username, int x, int y, String direction) {
        GameMessage.PlayerInfo player = activePlayers.get(username);
        if (player != null) {
            player.setX(x);
            player.setY(y);
            player.setLastSeen(System.currentTimeMillis());
            
            LoggingUtil.debug("Player position updated", Map.of(
                "username", username,
                "x", x,
                "y", y,
                "direction", direction
            ));
            return true;
        }
        return false;
    }
    
    /**
     * Get current game state
     */
    public GameMessage.GameState getGameState() {
        List<GameMessage.PlayerInfo> players = new ArrayList<>(activePlayers.values());
        
        return new GameMessage.GameState(
            DEFAULT_ROOM,
            "PLAYING",
            players,
            Map.of(
                "roomInfo", gameRooms.get(DEFAULT_ROOM),
                "timestamp", System.currentTimeMillis()
            )
        );
    }
    
    /**
     * Get list of online players
     */
    public List<GameMessage.PlayerInfo> getOnlinePlayers() {
        return new ArrayList<>(activePlayers.values());
    }
    
    /**
     * Get player by username
     */
    public GameMessage.PlayerInfo getPlayer(String username) {
        return activePlayers.get(username);
    }
    
    /**
     * Check if player is online
     */
    public boolean isPlayerOnline(String username) {
        return activePlayers.containsKey(username);
    }
    
    /**
     * Get player count
     */
    public int getPlayerCount() {
        return activePlayers.size();
    }
    
    /**
     * Update room player count
     */
    private void updateRoomPlayerCount(String roomId, int delta) {
        GameMessage.RoomInfo room = gameRooms.get(roomId);
        if (room != null) {
            room.setCurrentPlayers(Math.max(0, room.getCurrentPlayers() + delta));
            
            // Update room status based on player count
            if (room.getCurrentPlayers() >= room.getMaxPlayers()) {
                room.setStatus("FULL");
            } else {
                room.setStatus("OPEN");
            }
            
            LoggingUtil.debug("Room player count updated", Map.of(
                "roomId", roomId,
                "currentPlayers", room.getCurrentPlayers(),
                "maxPlayers", room.getMaxPlayers(),
                "status", room.getStatus()
            ));
        }
    }
    
    /**
     * Get room information
     */
    public GameMessage.RoomInfo getRoomInfo(String roomId) {
        return gameRooms.get(roomId);
    }
    
    /**
     * Get all rooms
     */
    public List<GameMessage.RoomInfo> getAllRooms() {
        return new ArrayList<>(gameRooms.values());
    }
    
    /**
     * Create a new room
     */
    public GameMessage.RoomInfo createRoom(String roomId, String name, int maxPlayers) {
        GameMessage.RoomInfo room = new GameMessage.RoomInfo(
            roomId,
            name,
            maxPlayers,
            0,
            "OPEN"
        );
        
        gameRooms.put(roomId, room);
        LoggingUtil.info("New game room created", Map.of(
            "roomId", roomId,
            "name", name,
            "maxPlayers", maxPlayers
        ));
        
        return room;
    }
    
    /**
     * Remove inactive players (cleanup)
     */
    public void cleanupInactivePlayers(long timeoutMs) {
        long currentTime = System.currentTimeMillis();
        List<String> inactivePlayers = activePlayers.values().stream()
            .filter(player -> (currentTime - player.getLastSeen()) > timeoutMs)
            .map(GameMessage.PlayerInfo::getUsername)
            .collect(Collectors.toList());
        
        for (String username : inactivePlayers) {
            removePlayer(username);
            LoggingUtil.info("Removed inactive player: " + username);
        }
        
        if (!inactivePlayers.isEmpty()) {
            LoggingUtil.info("Cleanup completed", Map.of(
                "removedPlayers", inactivePlayers.size(),
                "remainingPlayers", activePlayers.size()
            ));
        }
    }
} 
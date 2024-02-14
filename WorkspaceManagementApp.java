import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


class FloorPlan {
    private Map<Integer, Map<String, Object>> floorPlanData = new HashMap<>();

    public void uploadFloorPlan(int userId, Map<String, Object> changes) {
        if (floorPlanData.containsKey(userId)) {
            // Handle conflict based on priority, timestamp, or user roles
            Map<String, Object> existingChanges = floorPlanData.get(userId);
            if ((int) existingChanges.get("timestamp") < (int) changes.get("timestamp")) {
                floorPlanData.put(userId, changes);
            }
        } else {
            floorPlanData.put(userId, changes);
        }
    }

    public Map<Integer, Map<String, Object>> getFloorPlan() {
        return floorPlanData;
    }
}

class AdminOfflineManager {
    private Map<Integer, Map<String, Object>> localChanges = new HashMap<>();

    public void makeChangesOffline(int userId, Map<String, Object> changes) {
        localChanges.put(userId, changes);
    }

    public void synchronizeChanges(FloorPlan floorPlanInstance) {
        // Synchronize local changes with the server when online
        for (Map.Entry<Integer, Map<String, Object>> entry : localChanges.entrySet()) {
            floorPlanInstance.uploadFloorPlan(entry.getKey(), entry.getValue());
        }
        localChanges.clear();
    }
}

class MeetingRoomSystem {
    private Map<String, Integer> meetingRoomCapacities = new HashMap<>();
    private Map<String, Double> meetingRoomWeightages = new HashMap<>();

    public void bookMeetingRoom(String roomName, int participants) {
        // Implement booking logic, considering participants and other requirements
        // For simplicity, just updating the weightage based on the last booking time
        meetingRoomWeightages.put(roomName, System.currentTimeMillis() / 1000.0);
    }

    public String suggestMeetingRoom(int participants) {
        // Implement a recommendation system to suggest meeting rooms based on capacity and proximity
        // For simplicity, just choosing the room with the highest weightage
        return meetingRoomWeightages.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }
}


public class WorkspaceManagementApp {
    public static void main(String[] args) {
        FloorPlan floorPlanManager = new FloorPlan();
        AdminOfflineManager offlineManager = new AdminOfflineManager();
        MeetingRoomSystem meetingRoomSystem = new MeetingRoomSystem();

        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter Admin ID:");
        int adminId = scanner.nextInt();

        // Simulate an admin uploading a floor plan
        System.out.println("Enter timestamp for floor plan changes:");
        int floorPlanTimestamp = scanner.nextInt();
        System.out.println("Enter floor plan data:");
        String floorPlanData = scanner.next();

        Map<String, Object> floorPlanChanges = Map.of("timestamp", floorPlanTimestamp, "data", floorPlanData);
        floorPlanManager.uploadFloorPlan(adminId, floorPlanChanges);

        // Simulate an admin making changes offline
        System.out.println("Enter timestamp for offline changes:");
        int offlineTimestamp = scanner.nextInt();
        System.out.println("Enter offline changes:");
        String offlineData = scanner.next();

        Map<String, Object> offlineChanges = Map.of("timestamp", offlineTimestamp, "data", offlineData);
        offlineManager.makeChangesOffline(adminId, offlineChanges);

        // Simulate synchronization when the admin is back online
        offlineManager.synchronizeChanges(floorPlanManager);
        System.out.println("Updated Floor Plan: " + floorPlanManager.getFloorPlan());

        // Simulate meeting room booking and suggestion
        System.out.println("Enter number of participants for meeting room booking:");
        int participants = scanner.nextInt();

        meetingRoomSystem.bookMeetingRoom("MeetingRoom1", participants);
        meetingRoomSystem.bookMeetingRoom("MeetingRoom2", participants);

        String suggestedRoom = meetingRoomSystem.suggestMeetingRoom(participants);
        System.out.println("Suggested Meeting Room: " + suggestedRoom);

        scanner.close();
    }
}

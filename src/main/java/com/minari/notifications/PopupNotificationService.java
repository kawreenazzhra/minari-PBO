/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.minari.notifications;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Aliyah Rahma
 */
public class PopupNotificationService {
    private List<String> notificationQueue = new ArrayList<>();
    
    public void showPopup(String title, String message, String type) {
        String popupMessage = String.format(
            "🔔 %s\n%s\nType: %s\nTime: %s",
            title, message, type, java.time.LocalDateTime.now()
        );
        
        notificationQueue.add(popupMessage);
        displayPopup(popupMessage);
    }
    
    private void displayPopup(String message) {
        // Simulasi popup notification
        System.out.println("🪟 === POPUP NOTIFICATION ===");
        System.out.println(message);
        System.out.println("=============================");
    }
    
    public void showSuccessPopup(String message) {
        showPopup("✅ Success", message, "SUCCESS");
    }
    
    public void showInfoPopup(String message) {
        showPopup("ℹ️ Information", message, "INFO");
    }
    
    public void showWarningPopup(String message) {
        showPopup("⚠️ Warning", message, "WARNING");
    }
    
    public void showErrorPopup(String message) {
        showPopup("❌ Error", message, "ERROR");
    }
    
    public List<String> getNotificationQueue() {
        return new ArrayList<>(notificationQueue);
    }
    
    public void clearNotifications() {
        notificationQueue.clear();
    }
    
    public int getUnreadCount() {
        return notificationQueue.size();
    }
}

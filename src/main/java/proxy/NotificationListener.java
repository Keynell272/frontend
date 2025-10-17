package proxy;

import org.json.JSONObject;

public interface NotificationListener {
    
    /**
     * Se llama cuando llega una notificación del servidor
     * 
     * @param action Tipo de notificación (USER_LOGIN, USER_LOGOUT, NEW_MESSAGE)
     * @param data Datos de la notificación en formato JSON
     */
    void onNotificacion(String action, JSONObject data);
}
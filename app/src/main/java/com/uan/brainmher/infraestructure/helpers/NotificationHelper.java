package com.uan.brainmher.infraestructure.helpers;

import com.google.gson.Gson;

import okhttp3.*;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationHelper {

    private static final String API_KEY = "os_v2_app_44tnxyen4vbybev2oy6fwtcbf76bnxho3rcedonsffxy3r4wfxuxcm3aj3ibhhfrwle36g2qlwzjid5iye5hqgbkelbo6rugodz473y"; // Reemplaza con tu API Key de OneSignal
    private static final String API_URL = "https://onesignal.com/api/v1/notifications";

    /**
     * Envía una notificación usando la API REST de OneSignal.
     *
     * @param playerIds Lista de Player IDs a los que se enviará la notificación.
     * @param title     Título de la notificación.
     * @param message   Contenido de la notificación.
     */
    public static void createNotification(List<String> playerIds, String title, String message) {
        OkHttpClient client = new OkHttpClient();

        // Cuerpo de la notificación
        Map<String, Object> body = new HashMap<>();
        body.put("app_id", "e726dbe0-8de5-4380-92ba-763c5b4c412f"); // Reemplaza con tu App ID
        body.put("include_player_ids", playerIds);

        Map<String, String> headings = new HashMap<>();
        headings.put("en", title);
        body.put("headings", headings);

        Map<String, String> contents = new HashMap<>();
        contents.put("en", message);
        body.put("contents", contents);

        String json = new Gson().toJson(body);

        // Crear la solicitud POST
        Request request = new Request.Builder()
                .url("https://onesignal.com/api/v1/notifications")
                .post(RequestBody.create(json, MediaType.get("application/json; charset=utf-8")))
                .addHeader("Authorization", "Basic os_v2_app_44tnxyen4vbybev2oy6fwtcbf76bnxho3rcedonsffxy3r4wfxuxcm3aj3ibhhfrwle36g2qlwzjid5iye5hqgbkelbo6rugodz473y") // Cambia esto con tu API Key REST
                .build();

        // Ejecutar la solicitud
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("Error al enviar la notificación: " + e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                System.out.println("Respuesta de la notificación: " + response.body().string());
            }
        });
    }


}
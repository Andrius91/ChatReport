package icu.yogurt.chatreport.task;

import icu.yogurt.common.interfaces.IChatReport;
import icu.yogurt.common.model.Message;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

@RequiredArgsConstructor
public class SaveTask extends TimerTask {

    private final IChatReport plugin;
    private final List<Message> messageBuffer;

    @Override
    public void run() {
        // Verificar si hay mensajes en el búfer temporal
        if (!messageBuffer.isEmpty()) {
            // Crear una copia de los mensajes en el búfer
            List<Message> messagesToSave = new ArrayList<>(messageBuffer);

            // Guardar los mensajes en Redis
            plugin.getStorage().saveMessages(messagesToSave);

            plugin.log(3, "Se han guardado: " + messagesToSave.size() + " mensajes");
            // Limpiar el búfer temporal
            messageBuffer.clear();
        }
    }
}

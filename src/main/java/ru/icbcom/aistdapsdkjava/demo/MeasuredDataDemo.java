package ru.icbcom.aistdapsdkjava.demo;

import ru.icbcom.aistdapsdkjava.api.client.Client;
import ru.icbcom.aistdapsdkjava.api.client.Clients;
import ru.icbcom.aistdapsdkjava.api.device.Device;
import ru.icbcom.aistdapsdkjava.api.measureddata.MeasuredData;

import java.time.LocalDateTime;

/**
 * Примеры работы с измерениями.
 */
public class MeasuredDataDemo {

    public static void main(String[] args) {
        // Создание клиента для работы с API Aist Dap.
        Client client = Clients.builder()
                .setBaseUrl("http://127.0.0.1:8080/")
                .setLogin("Admin")
                .setPassword("Admin")
                .build();

        // Создание нового устройства.
        Device device = client.devices().create(client.instantiate(Device.class)
                .setObjectTypeId(38L)
                .setName("Новая метеостанция IMETEOLABS")
                .setAttributeValue("Address", "0")
                .setAttributeValue("Latitude", "55.7558")
                .setAttributeValue("Longitude", "37.6173"));

        // Создание нового измерения и его сохранение.
        MeasuredData measuredData1 = client.instantiate(MeasuredData.class)
                .setDataSourceId(400L)
                .setDapObjectId(device.getId())
                .setDateTime(LocalDateTime.now())
                .setDoubleValue(27.5);
        client.measuredData().insert(measuredData1);

        // Создание измерения без явного указания времени. В этом случае в качестве времени измерения будет взято
        // текущее время сервера платформа Aist Dap.
        MeasuredData measuredData2 = client.instantiate(MeasuredData.class)
                .setDataSourceId(412L)
                .setDapObjectId(device.getId())
                .setLongValue(99);
        client.measuredData().insert(measuredData2);

        // Удаление устройства.
        device.delete();
    }

}

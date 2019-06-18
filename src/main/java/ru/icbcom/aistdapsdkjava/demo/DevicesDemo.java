package ru.icbcom.aistdapsdkjava.demo;

import ru.icbcom.aistdapsdkjava.api.client.Client;
import ru.icbcom.aistdapsdkjava.api.client.Clients;
import ru.icbcom.aistdapsdkjava.api.device.Device;
import ru.icbcom.aistdapsdkjava.api.device.DeviceList;
import ru.icbcom.aistdapsdkjava.api.objecttype.ObjectType;

/**
 * Примеры работы с устройствами.
 */
public class DevicesDemo {

    public static void main(String[] args) {
        // Создание клиента для работы с API Aist Dap.
        Client client = Clients.builder()
                .setBaseUrl("http://127.0.0.1:8080/")
                .setLogin("Admin")
                .setPassword("Admin")
                .build();

        // Получение списока всех устройств.
        System.out.println("Devices:");
        DeviceList devices = client.devices().getAll();
        for (Device device : devices) {
            System.out.println(device);
        }

        // Создание нового устройства.
        Device newDevice = client.instantiate(Device.class)
                .setObjectTypeId(38L)
                .setName("Новая метеостанция IMETEOLABS")
                .setAttributeValue("Address", "1")
                .setAttributeValue("Latitude", "55.7558")
                .setAttributeValue("Longitude", "37.6173");
        newDevice = client.devices().create(newDevice);
        System.out.println();
        System.out.println("New device:");
        System.out.println(newDevice);

        // Поиск устройства по его идентификатору.
        long id = newDevice.getId();
        Device foundDevice = client.devices().getById(id).orElseThrow();
        System.out.println();
        System.out.println("Found device:");
        System.out.println(foundDevice);

        // Изменение значений атрибутов устройства.
        foundDevice.setAttributeValue("Address", "100");
        foundDevice.save();
        System.out.println();
        System.out.println("Device updated");

        // Получение типа объекта устройства.
        ObjectType objectType = foundDevice.getObjectType();
        System.out.println();
        System.out.println("Object type:");
        System.out.println(objectType);

        // Удаление устройства.
        foundDevice.delete();
        System.out.println();
        System.out.println("Device deleted");
    }

}

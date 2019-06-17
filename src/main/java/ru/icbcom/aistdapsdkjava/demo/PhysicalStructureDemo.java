package ru.icbcom.aistdapsdkjava.demo;

import ru.icbcom.aistdapsdkjava.api.client.Client;
import ru.icbcom.aistdapsdkjava.api.client.Clients;
import ru.icbcom.aistdapsdkjava.api.device.Device;
import ru.icbcom.aistdapsdkjava.api.device.DeviceList;
import ru.icbcom.aistdapsdkjava.api.objecttype.ObjectType;
import ru.icbcom.aistdapsdkjava.api.physicalstructure.PhysicalStructureObject;
import ru.icbcom.aistdapsdkjava.api.physicalstructure.PhysicalStructureObjectList;

public class PhysicalStructureDemo {

    public static void main(String[] args) {
        Client client = Clients.builder()
                .setBaseUrl("http://127.0.0.1:8080/")
                .setLogin("Admin")
                .setPassword("Admin")
                .build();

        // Получение списока корневых объектов физической структуры.
        System.out.println("Physical structure root object:");
        PhysicalStructureObjectList allInRoot = client.physicalStructure().getAllInRoot();
        for (PhysicalStructureObject physicalStructureObject : allInRoot) {
            System.out.println(physicalStructureObject.toString());
        }

        // Создание объекта в корне физической структуры.
        PhysicalStructureObject newPhysicalStructureObject = client.instantiate(PhysicalStructureObject.class)
                .setObjectTypeId(3L)
                .setName("Новая УСПД Пума")
                .setAttributeValue("Identifier", "30001111")
                .setAttributeValue("Log", "60")
                .setAttributeValue("CtrlPoll", "15")
                .setAttributeValue("UseServerUtc", "True")
                .setAttributeValue("Utc", "0")
                .setAttributeValue("UseSecondaryCommandServer", "False")
                .setAttributeValue("SecondaryCommandServer", "192.168.1.1:2725")
                .setAttributeValue("LoadTransitServer", "False")
                .setAttributeValue("TransitServer", "192.168.1.1:2726");
        newPhysicalStructureObject = client.physicalStructure().createInRoot(newPhysicalStructureObject);

        System.out.println();
        System.out.println("New physical structure object:");
        System.out.println(newPhysicalStructureObject);

        // Поиск объекта физической структуры по его идентификатору.
        long id = newPhysicalStructureObject.getId();
        PhysicalStructureObject foundPhysicalStructureObject = client.physicalStructure().getById(id).orElseThrow();
        System.out.println();
        System.out.println("Found physical structure object:");
        System.out.println(foundPhysicalStructureObject);

        // Изменение параметров существуещего объекта физической структуры.
        foundPhysicalStructureObject.setAttributeValue("Log", "120");
        foundPhysicalStructureObject.setAttributeValue("Utc", "3");
        foundPhysicalStructureObject.save();
        System.out.println();
        System.out.println("Physical structure object updated");

        // Получение типа объекта.
        ObjectType objectType = foundPhysicalStructureObject.getObjectType();
        System.out.println();
        System.out.println("Object type:");
        System.out.println(objectType);

        // Создание дочернего объекта физической структуры.
        PhysicalStructureObject newRs485PhysicalStructureObject = client.instantiate(PhysicalStructureObject.class)
                .setObjectTypeId(5L)
                .setName("Новый объект интерфейса RS485")
                .setAttributeValue("Baud", "7")
                .setAttributeValue("DataSize", "2")
                .setAttributeValue("Parity", "1")
                .setAttributeValue("StopBits", "1")
                .setAttributeValue("Timeout", "10");
        newRs485PhysicalStructureObject = newPhysicalStructureObject.createDescendant(newRs485PhysicalStructureObject);
        System.out.println();
        System.out.println("Descendant physical structure object created: ");
        System.out.println(newRs485PhysicalStructureObject);

        // Создание нового устройства и его привязка к объекту физической структуры.
        Device newDevice = client.devices().create(client.instantiate(Device.class)
                .setObjectTypeId(38L)
                .setName("Новая метеостанция IMETEOLABS")
                .setAttributeValue("Address", "1")
                .setAttributeValue("Latitude", "55.7558")
                .setAttributeValue("Longitude", "37.6173"));
        newDevice.attach(newRs485PhysicalStructureObject.getId());

        // Получение списка привязанных устройств.
        System.out.println();
        System.out.println("Attached devices:");
        DeviceList attachedDevices = newRs485PhysicalStructureObject.getAttachedDevices();
        for (Device device : attachedDevices) {
            System.out.println(device);
        }

        // Удаление устройства.
        newDevice.delete();

        // Удаление корневого объекта физической структуры.
        newPhysicalStructureObject.delete();
        System.out.println();
        System.out.println("Physical structure object deleted");
    }

}

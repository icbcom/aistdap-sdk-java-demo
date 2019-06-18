package ru.icbcom.aistdapsdkjava.demo;

import ru.icbcom.aistdapsdkjava.api.client.Client;
import ru.icbcom.aistdapsdkjava.api.client.Clients;
import ru.icbcom.aistdapsdkjava.api.device.Device;
import ru.icbcom.aistdapsdkjava.api.physicalstructure.PhysicalStructureObject;
import ru.icbcom.aistdapsdkjava.api.physicalstructure.PhysicalStructureObjectList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * В данном примере показано как можно получить список устройств привязанных к объектам типа 'Устройство LoRa'.
 */
public class LoraWanDevicesDemo {

    private static final long LORA_NETWORK_SERVER_OBJECT_TYPE_ID = 44L;
    private static final int LORA_WAN_DEVICE_OBJECT_TYPE_ID = 45;

    public static void main(String[] args) {
        // Создание клиента для работы с API Aist Dap.
        Client client = Clients.builder()
                .setBaseUrl("http://127.0.0.1:8080/")
                .setLogin("Admin")
                .setPassword("Admin")
                .build();

        // Получение списка всех объектов физической структуры типа 'Устройство LoRa' которые имеют привязанные устройства.
        // Например, для дерева физической структуры:
        // * LoRa Network Server1
        // |--- * Устройство LoRa 1
        //      |--- * Контролле ДГУ 1 (device)
        // |--- * Устройство LoRa 2
        //      |--- * Контролле ДГУ 2 (device)
        // * LoRa Network Server2
        // |--- * Устройство LoRa 3
        //      |--- * Контролле ДГУ 3 (device)
        // |--- * Устройство LoRa 4
        // В результирующем списке будут 3 объекта типа 'Устройство LoRa': Устройство LoRa 1, Устройство LoRa 2 и Устройство LoRa 3.
        // Устройство LoRa 4 будет отсутствовать в результирующем списке, т.к. к этому объекту не привязано ни одного устройства.
        PhysicalStructureObjectList rootObjects = client.physicalStructure().getAllInRoot();
        List<PhysicalStructureObject> loraWanPhysicalStructureObjects =
                rootObjects.stream()
                        .filter(physicalStructureObject -> physicalStructureObject.getObjectTypeId() == LORA_NETWORK_SERVER_OBJECT_TYPE_ID)
                        .flatMap(physicalStructureObject -> physicalStructureObject.getDescendants().stream())
                        .filter(physicalStructureObject -> physicalStructureObject.getObjectTypeId() == LORA_WAN_DEVICE_OBJECT_TYPE_ID)
                        .filter(PhysicalStructureObject::hasAttachedDevices)
                        .collect(Collectors.toList());

        // Вывод объектов из списка.
        for (PhysicalStructureObject loraWanPhysicalStructureObject : loraWanPhysicalStructureObjects) {
            System.out.println(loraWanPhysicalStructureObject);
        }

        // Хэш-таблица содержащая devEUI в качестве ключа и объекта устройства в качестве значения.
        Map<String, Device> devEUIToDevices = new HashMap<>();

        // Заполнение хэш-таблицы соответствующими данными.
        for (PhysicalStructureObject loraWanPhysicalStructureObject : loraWanPhysicalStructureObjects) {
            String devEUI = loraWanPhysicalStructureObject.getAttributeValueByName("DevEUI").orElseThrow();
            Device device = loraWanPhysicalStructureObject.getAttachedDevices().iterator().next();
            devEUIToDevices.put(devEUI, device);
        }

        // Теперь для получения идентификатора устройства в платформе Aist Dap по devEUI можно использовать соответствующую хэш-таблицу.
        Device device = devEUIToDevices.get("00112233AABBCCDD");
        Long deviceId = device.getId();
        System.out.println("Device id: " + deviceId);
    }

}

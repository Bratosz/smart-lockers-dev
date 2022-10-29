package pl.bratosz.smartlockers.service.creators;

import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Department;
import pl.bratosz.smartlockers.model.MeasurementList;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.model.orders.ExchangeStrategy;
import pl.bratosz.smartlockers.model.orders.OrderType;
import pl.bratosz.smartlockers.service.ClientService;
import pl.bratosz.smartlockers.utils.string.MyString;

import java.util.HashMap;

public class ClientCreator {
    private ClientService service;

    public ClientCreator(ClientService clientService) {
        this.service = clientService;
    }

    public Client create(String name) {
        name = MyString.create(name).get();
        Client client = new Client(name);
        HashMap<OrderType, ExchangeStrategy> map = new HashMap<>();
        map.put(OrderType.CHANGE_ARTICLE, ExchangeStrategy.PIECE_FOR_PIECE);
        map.put(OrderType.CHANGE_SIZE, ExchangeStrategy.RELEASE_BEFORE_RETURN);
        map.put(OrderType.EXCHANGE_FOR_NEW_ONE, ExchangeStrategy.PIECE_FOR_PIECE);
        client.setExchangeStrategies(map);

        MeasurementList measurementList = service.getMeasurementListService().create();
        client.addMeasurementList(measurementList);
        return service.getClientRepository().save(client);
    }

    public Client createWithPlantAndDepartment(String name, int plantNumber) {
        Client client = create(name);
        Plant plant = service.getPlantService().create(plantNumber, client);
        client.getPlants().add(plant);
        Department mainDepartment = service.getDepartmentService()
                .createDefaultDepartmentsAndReturnMain(client, plantNumber);
        client.getDepartments().add(mainDepartment);
        return client;
    }
}

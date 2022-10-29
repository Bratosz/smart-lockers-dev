package pl.bratosz.smartlockers.service;

import org.springframework.stereotype.Service;
import pl.bratosz.smartlockers.model.Client;
import pl.bratosz.smartlockers.model.Location;
import pl.bratosz.smartlockers.model.Plant;
import pl.bratosz.smartlockers.repository.ClientRepository;
import pl.bratosz.smartlockers.repository.LocationRepository;
import pl.bratosz.smartlockers.repository.PlantsRepository;
import pl.bratosz.smartlockers.repository.UsersRepository;
import pl.bratosz.smartlockers.response.CreateResponse;
import pl.bratosz.smartlockers.utils.string.MyString;

import java.util.LinkedList;
import java.util.List;

@Service
public class LocationService {
    private ClientRepository clientRepository;
    private LocationRepository locationRepository;
    private UsersRepository usersRepository;
    private PlantsRepository plantsRepository;

    public LocationService(ClientRepository clientRepository, LocationRepository locationRepository,
                           UsersRepository usersRepository, PlantsRepository plantsRepository) {
        this.clientRepository = clientRepository;
        this.plantsRepository = plantsRepository;
        this.locationRepository = locationRepository;
        this.usersRepository = usersRepository;
    }

    public Location getByNameAndPlantNumber(String name, int plantNumber) {
        name = name.toUpperCase().trim();
        return locationRepository.getByNameAndPlantNumber(name, plantNumber);
    }

    public Location getByNameAndClientId(String name, long clientId) {
        name = MyString.create(name).get();
        Location l = locationRepository.getByNameAndClient(name, clientId);
        if(l == null) {
            return getSurrogateBy(clientId);
        } else {
            return l;
        }
    }

    public Location create(long clientId, String locationName) {
        return create(clientId, locationName, false);
    }

    public Location create(long clientId, String locationName, boolean surrogate) {
        Client client = clientRepository.getById(clientId);
        Location location = new Location(locationName, client, surrogate);
        return locationRepository.save(location);
    }

    public Location createAndAssignToPlant(Client client, String locationName, boolean surrogate, Plant plant) {
        return locationRepository.save(new Location(locationName, client, surrogate, plant));

    }

    public Location assignToPlant(long locationId, int plantNumber) {
        Plant plant = plantsRepository.getByPlantNumber(plantNumber);
        Location location = locationRepository.getOne(locationId);
        location.setPlant(plant);
        return locationRepository.save(location);
    }

    public List<Location> getAll(long userId) {
        long clientId = usersRepository.getById(userId).getActualClientId();
        return locationRepository.getAll(clientId);
    }

    public Location getById(long id) {
        if (id == 0) {
            return null;
        }
        return locationRepository.getById(id);
    }

    public Location getSurrogateBy(Client client) {
        return locationRepository.getBySurrogateAndClient(true, client);
    }

    public Location getSurrogateBy(long clientId) {
        Location l = locationRepository.getBySurrogateAndClientId(true, clientId);
        if(l == null) {
            return createSurrogate(clientId);
        } else {
            return l;
        }
    }

    private Location createSurrogate(long clientId) {
        String s = MyString.create("ZASTĘPCZY").get();
        return create(clientId, s, true);
    }

    public CreateResponse create(String locationName, long plantId) {
        Plant plant = plantsRepository.getById(plantId);
        locationName = MyString.create(locationName).get();
        Location location = new Location(locationName, plant);
        location = locationRepository.save(location);
        return new CreateResponse(location, "Utworzono lokalizację");
    }

    public Location create(Client client, int plantNumber, Plant plant) {
        createAndAssignToPlant(client, plantNumber + " ZASTĘPCZA", true, plant);
        return createAndAssignToPlant(client, plantNumber + " GŁÓWNA", false, plant);
    }

    public List<Location> create(List<String> locationsNames, Client client, Plant plant) {
        List<Location> locations = new LinkedList<>();
        locations.add(createAndAssignToPlant(
                client,"ZASTĘPCZA", true, plant));
        locationsNames.forEach(locationName -> locations.add(
                createAndAssignToPlant(client, locationName, false, plant))
        );
        return locations;
    }
}

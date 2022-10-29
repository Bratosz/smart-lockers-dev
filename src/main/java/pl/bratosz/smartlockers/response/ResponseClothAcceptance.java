package pl.bratosz.smartlockers.response;

import com.fasterxml.jackson.annotation.JsonView;
import pl.bratosz.smartlockers.model.Views;
import pl.bratosz.smartlockers.model.clothes.Cloth;
import pl.bratosz.smartlockers.model.clothes.ClothAcceptanceType;
import pl.bratosz.smartlockers.model.orders.ClothOrder;
import pl.bratosz.smartlockers.model.orders.OrderType;

public class ResponseClothAcceptance {
    @JsonView(Views.Public.class)
    private boolean found;
    @JsonView(Views.Public.class)
    private boolean accepted;
    @JsonView(Views.Public.class)
    boolean active;
    @JsonView(Views.Public.class)
    private String message;
    @JsonView(Views.InternalForClothes.class)
    private Cloth cloth;

    private ResponseClothAcceptance(String message){
        this.message = message;
    }

    public static ResponseClothAcceptance createClothAcceptedResponse(ClothOrder clothOrder) {
        ResponseClothAcceptance response =  new ResponseClothAcceptance("Zaakceptowano ubranie.\n" +
                clothOrder.toString());
        response.setAcceptedStatus();
        return response;
    }

    public static ResponseClothAcceptance createClothNotFound(long id) {
        ResponseClothAcceptance response = new ResponseClothAcceptance("Nie znaleziono ubrania o id: " + id);
        response.setNotFoundStatus();
        return response;
    }

    public static ResponseClothAcceptance createClothBelongsToOtherClient(Cloth cloth) {
        int plantNumber = cloth.getEmployee().getDepartment().getMainPlantNumber();
        ResponseClothAcceptance response = new ResponseClothAcceptance(
                "Ubranie należy do innego klienta. Jego nr zakładu to: " + plantNumber);
        response.setBelongToOtherClientStatus();
        return response;
    }

    public static ResponseClothAcceptance createNoActiveOrderPresentResponse(Cloth cloth) {

        ResponseClothAcceptance response = new ResponseClothAcceptance("Brak aktywnego zlecenia dla ubrania.\n" +
                "Ubranie: " + cloth.toString());
        response.setNotAcceptedStatus();
        return response;
    }

    public static ResponseClothAcceptance createNewOrderAddedAndClothAcceptedResponse(ClothOrder clothOrder) {
        ResponseClothAcceptance response = new ResponseClothAcceptance("Utworzono nowe zlecenie i zaakceptowano ubranie.\n" +
                clothOrder.toString());
        response.setAcceptedStatus();
        return response;
    }

    public static ResponseClothAcceptance createAnotherOrderIsActiveResponse(ClothOrder clothOrder) {
        ResponseClothAcceptance response = new ResponseClothAcceptance("Aktywne jest inne zlecenie. Aby je usunąć udaj" +
                " się do widoku pracownika.\n" +
                "Aktywne zamówienie:\n" + clothOrder.toString());
        return response;
    }

    public static ResponseClothAcceptance createWrongAcceptanceTypeResponse(ClothAcceptanceType acceptanceType) {
        ResponseClothAcceptance response = new ResponseClothAcceptance("Błędny typ akceptacji odzieży: "
                + acceptanceType.getName());
        return response;
    }

    public static ResponseClothAcceptance wrongOrderTypeResponse(OrderType orderType) {
        ResponseClothAcceptance response = new ResponseClothAcceptance("Niewłaściwy typ zamówienia \n"
        + orderType.getName());
        return response;
    }

    public static ResponseClothAcceptance createClothAlreadyReturned(Cloth cloth) {
        ResponseClothAcceptance response = new ResponseClothAcceptance(
                "Ubranie zostało już przyjęte do wymiany: " +
                "Rodzaj zamówienia: " + cloth.getExchangeOrder().getOrderType().getName() +
                "Status: " + cloth.getClothStatus().getStatus().getName() + " " + cloth.getClothStatus().getDateOfUpdate());
        return response;
    }

    public static ResponseClothAcceptance createClothIsActive(Cloth cloth) {
        ResponseClothAcceptance response = new ResponseClothAcceptance(
                "Ubranie jest aktywne.");
        response.clothIsActive();
        response.setCloth(cloth);
        return response;
    }

    private void clothIsActive() {
        found = true;
        accepted = false;
        active = true;
    }

    public static ResponseClothAcceptance createClothIsInactive(Cloth cloth) {
        ResponseClothAcceptance response = new ResponseClothAcceptance(
                "Ubranie jest nieaktywne.");
        response.clothIsNOTActive();
        response.setCloth(cloth);
        return response;
    }

    private void clothIsNOTActive() {
        found = true;
        accepted = false;
        active = false;

    }

    private void setNotAcceptedStatus() {
        found = true;
        accepted = false;
    }

    private void setBelongToOtherClientStatus() {
        found = true;
        accepted = false;
    }

    private void setNotFoundStatus() {
        found = false;
        accepted = false;
    }

    private void setAcceptedStatus() {
        found = true;
        accepted = true;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public void setAccepted(boolean accepted) {
        this.accepted = accepted;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Cloth getCloth() {
        return cloth;
    }

    public boolean isActive() {
        return active;
    }

    public void setCloth(Cloth cloth) {
        this.cloth = cloth;
    }
}

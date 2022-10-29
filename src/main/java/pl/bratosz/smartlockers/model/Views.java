package pl.bratosz.smartlockers.model;

public class Views {
    public static class Public {
    }

    public static class InternalForLockers extends Public {
    }

    public static class InternalForEmployees extends Public {
    }

    public static class DismissedEmployees extends Public {

    }

    public static class InternalForEmployeesWithClothes extends Public {}

    public static class InternalForBoxes extends Public {

    }

    public static class InternalForClothes extends Public {

    }

    public static class PlantBasicInfo extends Public {

    }

    public static class PlantExtendedInfo extends Public{}


    public static class DepartmentBasicInfo extends Public {
    }

    public static class PrivateView {

    }

    public static class InternalForClothOrders extends Public {}

    public static class InternalForEmployeesForOurStaff extends InternalForEmployees{
    }

    public static class EmployeeCompleteInfo extends InternalForEmployees {
    }

    public static class EmployeeBasicInfo extends InternalForEmployees {

    }

    public static class LockersWithoutBoxes extends Public {
    }

    public static class DepartmentAdvancedInfo extends DepartmentBasicInfo {
    }

    public static class ClientBasicInfo extends PlantBasicInfo {
    }

    public static class BasicLocationInfo extends Public {
    }

    public static class OrderBasicInfo extends Public{

    }

    public class PositionBasicInfo  extends Public{
    }
}
